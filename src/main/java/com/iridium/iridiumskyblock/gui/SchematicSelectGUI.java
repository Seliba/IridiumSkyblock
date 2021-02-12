package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SchematicSelectGUI extends GUI implements Listener {

    public SchematicSelectGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().schematicselectGUISize, IridiumSkyblock.getInstance().getInventories().schematicselectGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            int i = 0;
            IridiumSkyblock.getInstance().getSchematics().schematicList.stream().map(fakeSchematic -> fakeSchematic.item).forEach(item -> {
                setItem(item.slot, ItemStackUtils.makeItem(item));
            });
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getInstance().getSchematics().schematicList) {
                if (e.getSlot() == fakeSchematic.item.slot && (fakeSchematic.permission.isEmpty() || e.getWhoClicked().hasPermission(fakeSchematic.permission))) {
                    e.getWhoClicked().closeInventory();
                    if (getIsland().schematic != null) {
                        for (String player : getIsland().members) {
                            User user = User.getUser(player);
                            Player p = Bukkit.getPlayer(user.name);
                            if (p != null) {
                                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().regenIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                            }
                        }
                    }
                    if (getIsland().schematic == null) {
                        getIsland().schematic = fakeSchematic.overworldData.schematic;
                        getIsland().netherschematic = fakeSchematic.netherData.schematic;
                        getIsland().pasteSchematic((Player) e.getWhoClicked(), false);
                    } else {
                        getIsland().schematic = fakeSchematic.overworldData.schematic;
                        getIsland().netherschematic = fakeSchematic.netherData.schematic;
                        getIsland().pasteSchematic(true);
                    }
                    getIsland().home = getIsland().home.add(fakeSchematic.x, fakeSchematic.y, fakeSchematic.z);
                    if (IridiumSkyblock.getInstance().getConfiguration().restartUpgradesOnRegen) {
                        getIsland().resetMissions();
                        getIsland().setSizeLevel(1);
                        getIsland().setMemberLevel(1);
                        getIsland().setWarpLevel(1);
                        getIsland().setOreLevel(1);
                    }
                    return;
                }
            }
        }
    }
}
