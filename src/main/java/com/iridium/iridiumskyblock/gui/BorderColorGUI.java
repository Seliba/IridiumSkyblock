package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.Color;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BorderColorGUI extends GUI implements Listener {

    public BorderColorGUI(Island island) {
        super(island, IridiumSkyblock.inventories.borderColorGUISize, IridiumSkyblock.inventories.borderColorGUITitle);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (IridiumSkyblock.border.RedEnabled)
            setItem(IridiumSkyblock.inventories.red.slot, Utils.makeItem(IridiumSkyblock.inventories.red));
        if (IridiumSkyblock.border.BlueEnabled)
            setItem(IridiumSkyblock.inventories.blue.slot, Utils.makeItem(IridiumSkyblock.inventories.blue));
        if (IridiumSkyblock.border.GreenEnabled)
            setItem(IridiumSkyblock.inventories.green.slot, Utils.makeItem(IridiumSkyblock.inventories.green));
        if (IridiumSkyblock.border.OffEnabled)
            setItem(IridiumSkyblock.inventories.off.slot, Utils.makeItem(IridiumSkyblock.inventories.off));
        if (IridiumSkyblock.inventories.backButtons)
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.inventories.back));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getCurrentItem() != null) {
                if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.inventories.backButtons) {
                    e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
                }
                if (IridiumSkyblock.border.BlueEnabled && e.getSlot() == IridiumSkyblock.inventories.blue.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Blue);
                if (IridiumSkyblock.border.RedEnabled && e.getSlot() == IridiumSkyblock.inventories.red.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Red);
                if (IridiumSkyblock.border.GreenEnabled && e.getSlot() == IridiumSkyblock.inventories.green.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Green);
                if (IridiumSkyblock.border.OffEnabled && e.getSlot() == IridiumSkyblock.inventories.off.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Off);
            }
        }
    }
}
