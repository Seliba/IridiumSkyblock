package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.MiscUtils;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BoosterGUI extends GUI implements Listener {
    public BoosterGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().boosterGUISize, IridiumSkyblock.getInstance().getInventories().boosterGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
                if (booster.enabled) {
                    setItem(booster.item.slot, ItemStackUtils.makeItem(booster.item, getIsland()));
                }
            }
            if (IridiumSkyblock.getInstance().getInventories().backButtons)
                setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
        }
    }

    public void sendMessage(Player p, String s) {
        for (String m : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().activatedBooster.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix).replace("%player%", p.getName()).replace("%boostername%", s)));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInstance().getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
                if (booster.enabled && e.getSlot() == booster.item.slot) {
                    int time = getIsland().getBoosterTime(booster.name);
                    if (time == 0 || IridiumSkyblock.getInstance().getConfiguration().stackableBoosters) {
                        MiscUtils.BuyResponse response = MiscUtils.canBuy(p, booster.vaultCost, booster.crystalsCost);
                        if (response == MiscUtils.BuyResponse.SUCCESS) {
                            sendMessage(p, booster.name);
                            getIsland().addBoosterTime(booster.name, booster.time);
                        } else {
                            p.sendMessage(StringUtils.color((response == MiscUtils.BuyResponse.NOT_ENOUGH_VAULT ? IridiumSkyblock.getInstance().getMessages().cantBuy : IridiumSkyblock.getInstance().getMessages().notEnoughCrystals)
                                    .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                        }
                    } else {
                        p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().boosterAlreadyActive
                                .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                                .replace("%booster%", booster.name)));
                    }
                }
            }
        }
    }
}