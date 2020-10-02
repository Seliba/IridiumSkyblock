package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.Color;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BorderColorGUI extends GUI implements Listener {

    public BorderColorGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().borderColorGUISize, IridiumSkyblock.getInventories().borderColorGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (IridiumSkyblock.border.RedEnabled) setItem(IridiumSkyblock.getInventories().red.slot,Utils.makeItem(IridiumSkyblock.getInventories().red));
        if (IridiumSkyblock.border.BlueEnabled) setItem(IridiumSkyblock.getInventories().blue.slot,Utils.makeItem(IridiumSkyblock.getInventories().blue));
        if (IridiumSkyblock.border.GreenEnabled) setItem(IridiumSkyblock.getInventories().green.slot,Utils.makeItem(IridiumSkyblock.getInventories().green));
        if (IridiumSkyblock.border.OffEnabled) setItem(IridiumSkyblock.getInventories().off.slot,Utils.makeItem(IridiumSkyblock.getInventories().off));
        setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getCurrentItem() != null) {
                if (e.getSlot() == getInventory().getSize() - 5) {
                    e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
                }
                if (e.getSlot() == IridiumSkyblock.getInventories().blue.slot)
                    IridiumSkyblock.getIslandManager().getIslandViaId(islandID).setBorderColor(Color.Blue);
                if (e.getSlot() == IridiumSkyblock.getInventories().red.slot)
                    IridiumSkyblock.getIslandManager().getIslandViaId(islandID).setBorderColor(Color.Red);
                if (e.getSlot() == IridiumSkyblock.getInventories().green.slot)
                    IridiumSkyblock.getIslandManager().getIslandViaId(islandID).setBorderColor(Color.Green);
                if (e.getSlot() == IridiumSkyblock.getInventories().off.slot)
                    IridiumSkyblock.getIslandManager().getIslandViaId(islandID).setBorderColor(Color.Off);
                IridiumSkyblock.getIslandManager().getIslandViaId(islandID).sendBorder();
            }
        }
    }
}
