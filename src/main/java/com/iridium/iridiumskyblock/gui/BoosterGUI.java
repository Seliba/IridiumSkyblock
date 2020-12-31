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
        super(island, IridiumSkyblock.getInventories().boosterGUISize, IridiumSkyblock.getInventories().boosterGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            if (IridiumSkyblock.getBoosters().spawnerBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().spawnerBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().spawner, getIsland()));
            if (IridiumSkyblock.getBoosters().farmingBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().farmingBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().farming, getIsland()));
            if (IridiumSkyblock.getBoosters().experianceBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().experianceBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().exp, getIsland()));
            if (IridiumSkyblock.getBoosters().flightBooster.enabled)
                setItem(IridiumSkyblock.getBoosters().flightBooster.slot, Utils.makeItem(IridiumSkyblock.getInventories().flight, getIsland()));
            if (IridiumSkyblock.getInventories().backButtons)
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    public void sendMessage(Player p, String s) {
        for (String m : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().activatedBooster.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%", p.getName()).replace("%boostername%", s)));
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
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().spawnerBooster.slot && IridiumSkyblock.getBoosters().spawnerBooster.enabled) {
                if (getIsland().spawnerBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.getBoosters().spawnerBooster.vaultCost, IridiumSkyblock.getBoosters().spawnerBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Spawner");
                        int baseValue = IridiumSkyblock.getConfiguration().stackableBoosters ? getIsland().spawnerBooster : 0;
                        getIsland().spawnerBooster = baseValue + IridiumSkyblock.getBoosters().spawnerBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().spawnerBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().farmingBooster.slot && IridiumSkyblock.getBoosters().farmingBooster.enabled) {
                if (getIsland().farmingBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.getBoosters().farmingBooster.vaultCost, IridiumSkyblock.getBoosters().farmingBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                      sendMessage(p, "Farming");
                      int baseValue = IridiumSkyblock.getConfiguration().stackableBoosters ? getIsland().farmingBooster : 0;
                      getIsland().farmingBooster = baseValue + IridiumSkyblock.getBoosters().farmingBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().farmingBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().experianceBooster.slot && IridiumSkyblock.getBoosters().experianceBooster.enabled) {
                if (getIsland().expBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.getBoosters().experianceBooster.vaultCost, IridiumSkyblock.getBoosters().experianceBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                      sendMessage(p, "Experience");
                      int baseValue = IridiumSkyblock.getConfiguration().stackableBoosters ? getIsland().expBooster : 0;
                      getIsland().expBooster = baseValue + IridiumSkyblock.getBoosters().experianceBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().expBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getBoosters().flightBooster.slot && IridiumSkyblock.getBoosters().flightBooster.enabled) {
                if (getIsland().flightBooster == 0) {
                    Utils.BuyResponce responce = Utils.canBuy(p, IridiumSkyblock.getBoosters().flightBooster.vaultCost, IridiumSkyblock.getBoosters().flightBooster.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                      sendMessage(p, "Flight");
                      int baseValue = IridiumSkyblock.getConfiguration().stackableBoosters ? getIsland().flightBooster : 0;
                      getIsland().flightBooster = baseValue + IridiumSkyblock.getBoosters().flightBooster.time;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().flightBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
    }
}