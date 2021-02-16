package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class GUI {

    public int islandID;
    public int scheduler;
    private Inventory inventory;

    public GUI() {

    }

    public GUI(Island island, int size, String name) {
        islandID = island.id;
        this.inventory = Bukkit.createInventory(null, size, StringUtils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, 2);
    }

    public GUI(int size, String name) {
        this.inventory = Bukkit.createInventory(null, size, StringUtils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, 2);
    }

    public GUI(Island island, int size, String name, int refresh) {
        islandID = island.id;
        this.inventory = Bukkit.createInventory(null, size, StringUtils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, refresh);
    }

    public GUI(int size, String name, int refresh) {
        this.inventory = Bukkit.createInventory(null, size, StringUtils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, refresh);
    }

    public void addContent() {
        if (inventory.getViewers().isEmpty()) return;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                setItem(i, ItemStackUtils.makeItemHidden(IridiumSkyblock.getInstance().getInventories().background));
            }
        }
    }

    public void setItem(int i, ItemStack itemStack) {
        if (getInventory().getItem(i) == null || !getInventory().getItem(i).isSimilar(itemStack)) {
            getInventory().setItem(i, itemStack);
        }
    }

    public abstract void onInventoryClick(InventoryClickEvent e);

    public Inventory getInventory() {
        return inventory;
    }

    public Island getIsland() {
        return IslandManager.getIslandViaId(islandID);
    }
}
