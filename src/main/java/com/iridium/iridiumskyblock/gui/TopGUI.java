package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TopGUI extends GUI implements Listener {

    public Map<Integer, Integer> islands = new HashMap<>();

    public TopGUI() {
        super(IridiumSkyblock.inventories.topGUISize, IridiumSkyblock.inventories.topGUITitle, 40);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        ItemStack filler = Utils.makeItem(IridiumSkyblock.inventories.topfiller);
        for (int i : IridiumSkyblock.configuration.islandTopSlots.keySet()) {
            CompletableFuture<List<Integer>> completableFuture = IslandDataManager.getIslands(IslandDataManager.IslandSortType.VALUE, i - 1, i, false);
            completableFuture.thenRun(() -> {
                try {
                    List<Integer> islandid = completableFuture.get();
                    if (islandid.size() > 0) {
                        Island island = IslandManager.getIslandViaId(islandid.get(0));
                        if (island == null) return;
                        User owner = User.getUser(island.owner);
                        ArrayList<Utils.Placeholder> placeholders = new ArrayList<>(Arrays.asList(new Utils.Placeholder("player", owner.name), new Utils.Placeholder("votes", island.getVotes() + ""), new Utils.Placeholder("name", island.getName()), new Utils.Placeholder("rank", i + ""), new Utils.Placeholder("level", Utils.NumberFormatter.format(island.value / IridiumSkyblock.configuration.valuePerLevel)), new Utils.Placeholder("value", island.getFormattedValue()), new Utils.Placeholder("members", island.members.size() + "")));
                        for (XMaterial item : IridiumSkyblock.blockValues.blockvalue.keySet()) {
                            placeholders.add(new Utils.Placeholder(item.name() + "_amount", "" + island.valuableBlocks.getOrDefault(item.name(), 0)));
                        }
                        for (String item : IridiumSkyblock.blockValues.spawnervalue.keySet()) {
                            placeholders.add(new Utils.Placeholder(item + "_amount", "" + island.spawners.getOrDefault(item, 0)));
                        }
                        placeholders.add(new Utils.Placeholder("ISLANDBANK_value", IridiumSkyblock.configuration.islandMoneyPerValue != 0 ? Utils.NumberFormatter.format(island.money / IridiumSkyblock.configuration.islandMoneyPerValue) : "0"));
                        ItemStack head = Utils.makeItem(IridiumSkyblock.inventories.topisland, placeholders);
                        islands.put(IridiumSkyblock.configuration.islandTopSlots.get(i), island.id);
                        setItem(IridiumSkyblock.configuration.islandTopSlots.get(i), head);
                    } else {
                        setItem(IridiumSkyblock.configuration.islandTopSlots.get(i), filler);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
        if (IridiumSkyblock.inventories.backButtons)
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.inventories.back));
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.inventories.backButtons) {
                if (User.getUser((Player) e.getWhoClicked()).getIsland() != null) {
                    e.getWhoClicked().openInventory(User.getUser((Player) e.getWhoClicked()).getIsland().islandMenuGUI.getInventory());
                } else {
                    e.getWhoClicked().closeInventory();
                }
            }
            if (islands.containsKey(e.getSlot())) {
                e.getWhoClicked().closeInventory();
                Island island = IslandManager.getIslandViaId(islands.get(e.getSlot()));
                if (island.visit || User.getUser((OfflinePlayer) e.getWhoClicked()).bypassing) {
                    island.teleportHome((Player) e.getWhoClicked());
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
        }
    }
}