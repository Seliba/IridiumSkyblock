package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Schematics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SchematicSelectGUI extends GUI implements Listener {

    public SchematicSelectGUI(Island island) {
        super(island, IridiumSkyblock.inventories.schematicselectGUISize, IridiumSkyblock.inventories.schematicselectGUITitle);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            int i = 0;
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.schematics.schematics) {
                if (fakeSchematic.slot == null) fakeSchematic.slot = i;
                try {
                    setItem(fakeSchematic.slot, Utils.makeItem(fakeSchematic.item, 1, fakeSchematic.displayname, fakeSchematic.lore));
                } catch (Exception e) {
                    setItem(fakeSchematic.slot, Utils.makeItem(XMaterial.STONE, 1, fakeSchematic.displayname, fakeSchematic.lore));
                }
                i++;
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.schematics.schematics) {
                if (e.getSlot() == fakeSchematic.slot && (fakeSchematic.permission.isEmpty() || e.getWhoClicked().hasPermission(fakeSchematic.permission))) {
                    e.getWhoClicked().closeInventory();
                    if (getIsland().schematic != null) {
                        for (String player : getIsland().members) {
                            User user = User.getUser(player);
                            Player p = Bukkit.getPlayer(user.name);
                            if (p != null) {
                                p.sendMessage(Utils.color(IridiumSkyblock.messages.regenIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                            }
                        }
                    }
                    if (getIsland().schematic == null) {
                        getIsland().schematic = fakeSchematic.name;
                        getIsland().netherschematic = fakeSchematic.netherisland;
                        getIsland().pasteSchematic((Player) e.getWhoClicked(), false);
                    } else {
                        getIsland().schematic = fakeSchematic.name;
                        getIsland().netherschematic = fakeSchematic.netherisland;
                        getIsland().pasteSchematic(true);
                    }
                    getIsland().home = getIsland().home.add(fakeSchematic.x, fakeSchematic.y, fakeSchematic.z);
                    if (IridiumSkyblock.configuration.restartUpgradesOnRegen) {
                        getIsland().resetMissions();
                        getIsland().setSizeLevel(1);
                        getIsland().memberLevel = 1;
                        getIsland().warpLevel = 1;
                        getIsland().oreLevel = 1;
                        getIsland().flightBooster = 0;
                        getIsland().expBooster = 0;
                        getIsland().farmingBooster = 0;
                        getIsland().spawnerBooster = 0;
                    }
                    return;
                }
            }
        }
    }
}
