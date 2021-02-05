package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.Placeholder;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitGUI extends GUI implements Listener {

    private final int page;
    public Map<Integer, Integer> islands = new HashMap<>();

    public VisitGUI(int page) {
        super(IridiumSkyblock.getInstance().getInventories().visitGUISize, IridiumSkyblock.getInstance().getInventories().visitGUITitle, 40);
        IridiumSkyblock.getInstance().registerListeners(this);
        this.page = page;
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        List<Integer> islandIDS = IridiumSkyblockAPI.getInstance().getIslands(IslandDataManager.IslandSortType.VOTES, 45 * (page - 1), 45 * page, true);
        for (int i = 0; i < islandIDS.size(); i++) {
            Island island = IslandManager.getIslandViaId(islandIDS.get(i));
            if (island == null) continue;
            int slot = i;
            Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.getInstance(), () -> {
                User owner = User.getUser(island.owner);
                ItemStack head = ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().visitisland, Arrays.asList(new Placeholder("player", owner.name), new Placeholder("name", island.getName()), new Placeholder("rank", Integer.toString(island.getRank())), new Placeholder("votes", NumberFormat.getInstance().format(island.getVotes())), new Placeholder("value", island.getFormattedValue())));
                islands.put(slot, island.id);
                setItem(slot, head);
            });
        }
        setItem(getInventory().getSize() - 3, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().nextPage));
        setItem(getInventory().getSize() - 7, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().previousPage));
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player player = (Player) e.getWhoClicked();
            if (islands.containsKey(e.getSlot())) {
                Island island = IslandManager.getIslandViaId(islands.get(e.getSlot()));
                User u = User.getUser(player);
                if (island.visit || u.bypassing || User.getUser(island.owner).hasCoopVisitPermissions(u)) {
                    if (e.getClick() == ClickType.RIGHT) {
                        if (island.hasVoted(u)) {
                            island.removeVote(u);
                        } else {
                            island.addVote(u);
                        }
                    } else {
                        player.closeInventory();
                        island.teleportHome(player);
                    }
                } else {
                    player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                }
            } else if (e.getSlot() == getInventory().getSize() - 7) {
                VisitGUI visitGUI = IridiumSkyblock.getInstance().getVisitGUI().getPage(page - 1);
                if (visitGUI != null) {
                    visitGUI.addContent();
                    player.openInventory(visitGUI.getInventory());
                }
            } else if (e.getSlot() == getInventory().getSize() - 3) {
                VisitGUI visitGUI = IridiumSkyblock.getInstance().getVisitGUI().getPage(page + 1);
                if (visitGUI != null) {
                    visitGUI.addContent();
                    player.openInventory(visitGUI.getInventory());
                }
            }
        }
    }
}