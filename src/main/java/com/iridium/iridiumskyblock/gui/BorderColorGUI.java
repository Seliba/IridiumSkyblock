package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.Color;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BorderColorGUI extends GUI implements Listener {

    public BorderColorGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().borderColorGUISize, IridiumSkyblock.getInstance().getInventories().borderColorGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (IridiumSkyblock.getInstance().getBorder().RedEnabled)
            setItem(IridiumSkyblock.getInstance().getInventories().red.slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().red));
        if (IridiumSkyblock.getInstance().getBorder().BlueEnabled)
            setItem(IridiumSkyblock.getInstance().getInventories().blue.slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().blue));
        if (IridiumSkyblock.getInstance().getBorder().GreenEnabled)
            setItem(IridiumSkyblock.getInstance().getInventories().green.slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().green));
        if (IridiumSkyblock.getInstance().getBorder().OffEnabled)
            setItem(IridiumSkyblock.getInstance().getInventories().off.slot, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().off));
        if (IridiumSkyblock.getInstance().getInventories().backButtons)
            setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getCurrentItem() != null) {
                if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInstance().getInventories().backButtons) {
                    e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
                }
                if (IridiumSkyblock.getInstance().getBorder().BlueEnabled && e.getSlot() == IridiumSkyblock.getInstance().getInventories().blue.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Blue);
                if (IridiumSkyblock.getInstance().getBorder().RedEnabled && e.getSlot() == IridiumSkyblock.getInstance().getInventories().red.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Red);
                if (IridiumSkyblock.getInstance().getBorder().GreenEnabled && e.getSlot() == IridiumSkyblock.getInstance().getInventories().green.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Green);
                if (IridiumSkyblock.getInstance().getBorder().OffEnabled && e.getSlot() == IridiumSkyblock.getInstance().getInventories().off.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Off);
            }
        }
    }
}
