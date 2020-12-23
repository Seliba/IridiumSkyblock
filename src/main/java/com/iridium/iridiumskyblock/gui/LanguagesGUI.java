package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Utils;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LanguagesGUI extends GUI implements Listener {

    int page;

    public LanguagesGUI root;

    public Map<Integer, LanguagesGUI> pages;

    public Map<Integer, String> languages;

    public LanguagesGUI() {
        pages = new HashMap<>();
        if (IridiumSkyblock.instance.languages.isEmpty()) {
            pages.put(1, new LanguagesGUI(1, this));
            return;
        }
        for (int i = 1; i <= Math.ceil(IridiumSkyblock.instance.languages.size() / 45.00); i++) {
            pages.put(i, new LanguagesGUI(i, this));
        }
    }

    public LanguagesGUI(int page, LanguagesGUI root) {
        super(54, "&7Languages");
        this.page = page;
        this.root = root;
        languages = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, IridiumSkyblock.instance);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        languages.clear();
        int slot = 0;
        int i = 0;
        java.util.Collections.sort(IridiumSkyblock.instance.languages);
        for (String language : IridiumSkyblock.instance.languages) {
            if (i >= (page - 1) * 45 && i < page * 54) {
                if (slot < 45) {
                    languages.put(slot, language);
                    setItem(slot, Utils.makeItem(XMaterial.PAPER, 1, "&b&l" + language));
                    slot++;
                }
            }
            i++;
        }
        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.inventories.nextPage));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.inventories.previousPage));
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (languages.containsKey(e.getSlot())) {
                IridiumSkyblock.instance.setLanguage(languages.get(e.getSlot()), (Player) e.getWhoClicked());
            } else if (e.getSlot() == getInventory().getSize() - 7) {
                if (root.pages.containsKey(page - 1)) {
                    e.getWhoClicked().openInventory(root.pages.get(page - 1).getInventory());
                }
            } else if (e.getSlot() == getInventory().getSize() - 3) {
                if (root.pages.containsKey(page + 1)) {
                    e.getWhoClicked().openInventory(root.pages.get(page + 1).getInventory());
                }
            }
        }
    }
}
