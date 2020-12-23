package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BoosterGUI extends GUI implements Listener {
    public BoosterGUI(Island island) {
        super(island, IridiumSkyblock.inventories.boosterGUISize, IridiumSkyblock.inventories.boosterGUITitle);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            if (IridiumSkyblock.boosters.spawnerBooster.enabled)
                setItem(IridiumSkyblock.boosters.spawnerBooster.slot, Utils.makeItem(IridiumSkyblock.inventories.spawner, getIsland()));
            if (IridiumSkyblock.boosters.farmingBooster.enabled)
                setItem(IridiumSkyblock.boosters.farmingBooster.slot, Utils.makeItem(IridiumSkyblock.inventories.farming, getIsland()));
            if (IridiumSkyblock.boosters.experianceBooster.enabled)
                setItem(IridiumSkyblock.boosters.experianceBooster.slot, Utils.makeItem(IridiumSkyblock.inventories.exp, getIsland()));
            if (IridiumSkyblock.boosters.flightBooster.enabled)
                setItem(IridiumSkyblock.boosters.flightBooster.slot, Utils.makeItem(IridiumSkyblock.inventories.flight, getIsland()));
            if (IridiumSkyblock.inventories.backButtons)
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.inventories.back));
        }
    }

    public void sendMessage(Player p, String s) {
        for (String m : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.messages.activatedBooster.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%player%", p.getName()).replace("%boostername%", s)));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.inventories.backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            if (e.getSlot() == IridiumSkyblock.boosters.spawnerBooster.slot && IridiumSkyblock.boosters.spawnerBooster.enabled) {
                if (getIsland().spawnerBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.boosters.spawnerBooster.vaultCost, IridiumSkyblock.boosters.spawnerBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Spawner");
                        getIsland().spawnerBooster = IridiumSkyblock.boosters.spawnerBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.spawnerBoosterActive.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.boosters.farmingBooster.slot && IridiumSkyblock.boosters.farmingBooster.enabled) {
                if (getIsland().farmingBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.boosters.farmingBooster.vaultCost, IridiumSkyblock.boosters.farmingBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Farming");
                        getIsland().farmingBooster = IridiumSkyblock.boosters.farmingBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.farmingBoosterActive.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.boosters.experianceBooster.slot && IridiumSkyblock.boosters.experianceBooster.enabled) {
                if (getIsland().expBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.boosters.experianceBooster.vaultCost, IridiumSkyblock.boosters.experianceBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Experience");
                        getIsland().expBooster = IridiumSkyblock.boosters.experianceBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.expBoosterActive.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.boosters.flightBooster.slot && IridiumSkyblock.boosters.flightBooster.enabled) {
                if (getIsland().flightBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.boosters.flightBooster.vaultCost, IridiumSkyblock.boosters.flightBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Flight");
                        getIsland().flightBooster = IridiumSkyblock.boosters.flightBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.flightBoosterActive.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
        }
    }
}