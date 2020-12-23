package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VisitorGUI extends GUI implements Listener {
    public Map<Integer, Player> visitors = new HashMap<>();

    public VisitorGUI(Island island) {
        super(island, IridiumSkyblock.inventories.visitorGUISize, IridiumSkyblock.inventories.visitorGUITitle);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        Island island = getIsland();
        if (island == null) return;
        visitors.clear();
        int i = 0;
        for (Player p : island.getPlayersOnIsland()) {
            User visitorUser = User.getUser(p);
            if (!island.members.contains(p.getUniqueId().toString()) && !island.isCoop(visitorUser.getIsland()) && !(visitorUser.bypassing || p.hasPermission("iridiumskyblock.silentvisit"))) {
                if (i >= getInventory().getSize()) return;
                ItemStack head = Utils.makeItem(IridiumSkyblock.inventories.islandVisitors, Collections.singletonList(new Utils.Placeholder("player", p.getName())));
                setItem(i, head);
                visitors.put(i, p);
                i++;
            }
        }
        if (IridiumSkyblock.inventories.backButtons) setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.inventories.back));
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            Player p = (Player) e.getWhoClicked();
            Island island = getIsland();
            e.setCancelled(true);
            int i = e.getSlot();
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.inventories.backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (visitors.containsKey(i)) {
                Player visitor = visitors.get(i);
                if (island.isInIsland(visitor.getLocation())) {
                    if (User.getUser(visitor).bypassing || visitor.hasPermission("iridiumskyblock.visitbypass")) {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.cantExpelPlayer.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%player%", visitor.getName() + "")));
                    } else {
                        island.spawnPlayer(visitor);
                        visitors.clear();
                        p.sendMessage(Utils.color(IridiumSkyblock.messages.expelledVisitor.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%player%", visitor.getName() + "")));
                        visitor.sendMessage(Utils.color(IridiumSkyblock.messages.youHaveBeenExpelled.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%kicker%", p.getName() + "")));
                        getInventory().clear();
                        addContent();
                    }
                }
            }
        }
    }
}