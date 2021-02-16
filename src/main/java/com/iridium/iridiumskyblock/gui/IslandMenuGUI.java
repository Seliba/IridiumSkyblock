package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class IslandMenuGUI extends GUI implements Listener {

    public IslandMenuGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().islandMenuGUISize, IridiumSkyblock.getInstance().getInventories().islandMenuGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            IridiumSkyblock.getInstance().getInventories().menu.stream().map(menuItem -> menuItem.item).forEach(item -> setItem(item.slot, ItemStackUtils.makeItemHidden(item, getIsland())));
        }
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            Optional<Inventories.MenuItem> item = IridiumSkyblock.getInstance().getInventories().menu.stream().filter(menuItem -> menuItem.item.slot==e.getSlot()).findFirst();
            if (item.isPresent()) {
                p.closeInventory();
                Bukkit.getServer().dispatchCommand(e.getWhoClicked(), item.get().command);
            }
        }
    }
}
