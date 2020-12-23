package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UpgradeGUI extends GUI implements Listener {

    public UpgradeGUI(Island island) {
        super(island, IridiumSkyblock.inventories.upgradeGUISize, IridiumSkyblock.inventories.upgradeGUITitle);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        Island island = getIsland();
        if (island != null) {
            if (IridiumSkyblock.upgrades.sizeUpgrade.enabled)
                setItem(IridiumSkyblock.upgrades.sizeUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.inventories.size, getIsland()));
            if (IridiumSkyblock.upgrades.memberUpgrade.enabled)
                setItem(IridiumSkyblock.upgrades.memberUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.inventories.member, getIsland()));
            if (IridiumSkyblock.upgrades.warpUpgrade.enabled)
                setItem(IridiumSkyblock.upgrades.warpUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.inventories.warp, getIsland()));
            if (IridiumSkyblock.upgrades.oresUpgrade.enabled)
                setItem(IridiumSkyblock.upgrades.oresUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.inventories.ores, getIsland()));
            if (IridiumSkyblock.inventories.backButtons)
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.inventories.back));
        }
    }

    private void sendMessage(Player p, String upgrade, int oldlvl, int newlvl) {
        for (String m : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.messages.islandUpgraded.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%player%", p.getName()).replace("%upgradename%", upgrade).replace("%oldlvl%", oldlvl + "").replace("%newlvl%", newlvl + "")));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.inventories.backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            if (e.getSlot() == IridiumSkyblock.upgrades.sizeUpgrade.slot && IridiumSkyblock.upgrades.sizeUpgrade.enabled) {
                if (IridiumSkyblock.upgrades.sizeUpgrade.upgrades.containsKey(getIsland().sizeLevel + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(getIsland().sizeLevel + 1);
                    Utils.BuyResponce responce = Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Size", getIsland().sizeLevel, getIsland().sizeLevel + 1);
                        getIsland().setSizeLevel(getIsland().sizeLevel + 1);
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.maxLevelReached.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.upgrades.memberUpgrade.slot && IridiumSkyblock.upgrades.memberUpgrade.enabled) {
                if (IridiumSkyblock.upgrades.memberUpgrade.upgrades.containsKey(getIsland().memberLevel + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.upgrades.memberUpgrade.upgrades.get(getIsland().memberLevel + 1);
                    Utils.BuyResponce responce = Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Member", getIsland().memberLevel, getIsland().memberLevel + 1);
                        getIsland().memberLevel++;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.maxLevelReached.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.upgrades.warpUpgrade.slot && IridiumSkyblock.upgrades.warpUpgrade.enabled) {
                if (IridiumSkyblock.upgrades.warpUpgrade.upgrades.containsKey(getIsland().warpLevel + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.upgrades.warpUpgrade.upgrades.get(getIsland().warpLevel + 1);
                    Utils.BuyResponce responce = Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Warp", getIsland().warpLevel, getIsland().warpLevel + 1);
                        getIsland().warpLevel = getIsland().warpLevel + 1;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.maxLevelReached.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.upgrades.oresUpgrade.slot && IridiumSkyblock.upgrades.oresUpgrade.enabled) {
                if (IridiumSkyblock.upgrades.oresUpgrade.upgrades.containsKey(getIsland().oreLevel + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.upgrades.oresUpgrade.upgrades.get(getIsland().oreLevel + 1);
                    Utils.BuyResponce responce = Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost);
                    if (responce == Utils.BuyResponce.SUCCESS) {
                        sendMessage(p, "Ore", getIsland().oreLevel, getIsland().oreLevel + 1);
                        getIsland().oreLevel++;
                    } else {
                        p.sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.messages.cantBuy : IridiumSkyblock.messages.notEnoughCrystals.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.maxLevelReached.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
        }
    }
}
