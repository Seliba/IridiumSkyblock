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
        super(island, IridiumSkyblock.getInventories().upgradeGUISize, IridiumSkyblock.getInventories().upgradeGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        Island island = getIsland();
        if (island != null) {
            if (IridiumSkyblock.getUpgrades().sizeUpgrade.enabled)
                setItem(IridiumSkyblock.getUpgrades().sizeUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().size, getIsland()));
            if (IridiumSkyblock.getUpgrades().memberUpgrade.enabled)
                setItem(IridiumSkyblock.getUpgrades().memberUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().member, getIsland()));
            if (IridiumSkyblock.getUpgrades().warpUpgrade.enabled)
                setItem(IridiumSkyblock.getUpgrades().warpUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().warp, getIsland()));
            if (IridiumSkyblock.getUpgrades().oresUpgrade.enabled)
                setItem(IridiumSkyblock.getUpgrades().oresUpgrade.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().ores, getIsland()));
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    private void sendMessage(Player p, String upgrade, int oldlvl, int newlvl){
        for (String m : getIsland().getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandUpgraded.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%", p.getName()).replace("%upgradename%", upgrade).replace("%oldlvl%",oldlvl+"").replace("%newlvl%",newlvl+"")));
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
            if (e.getSlot() == getInventory().getSize() - 5) {
                e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
            }
            if (e.getSlot() == IridiumSkyblock.getUpgrades().sizeUpgrade.slot && IridiumSkyblock.getUpgrades().sizeUpgrade.enabled) {
                if (IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(getIsland().getSizeLevel() + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(getIsland().getSizeLevel() + 1);
                    if (Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost)) {
                        sendMessage(p,"Size", getIsland().getSizeLevel(),getIsland().getSizeLevel()+1);
                        getIsland().setSizeLevel(getIsland().getSizeLevel() + 1);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().maxLevelReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getUpgrades().memberUpgrade.slot && IridiumSkyblock.getUpgrades().memberUpgrade.enabled) {
                if (IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(getIsland().getMemberLevel() + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(getIsland().getMemberLevel() + 1);
                    if (Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost)) {
                        sendMessage(p,"Member", getIsland().getMemberLevel(),getIsland().getMemberLevel()+1);
                        getIsland().setMemberLevel(getIsland().getMemberLevel() + 1);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().maxLevelReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getUpgrades().warpUpgrade.slot && IridiumSkyblock.getUpgrades().warpUpgrade.enabled) {
                if (IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(getIsland().getWarpLevel() + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(getIsland().getWarpLevel() + 1);
                    if (Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost)) {
                        sendMessage(p,"Warp", getIsland().getWarpLevel(),getIsland().getWarpLevel()+1);
                        getIsland().setWarpLevel(getIsland().getWarpLevel() + 1);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().maxLevelReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            if (e.getSlot() == IridiumSkyblock.getUpgrades().oresUpgrade.slot && IridiumSkyblock.getUpgrades().oresUpgrade.enabled) {
                if (IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.containsKey(getIsland().getOreLevel() + 1)) {
                    Upgrades.IslandUpgrade upgrade = IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.get(getIsland().getOreLevel() + 1);
                    if (Utils.canBuy(p, upgrade.vaultCost, upgrade.crystalsCost)) {
                        sendMessage(p,"Ore", getIsland().getOreLevel(),getIsland().getOreLevel()+1);
                        getIsland().setOreLevel(getIsland().getOreLevel() + 1);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().maxLevelReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
    }
}
