package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Missions;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.Placeholder;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class MissionsGUI extends GUI implements Listener {

    public MissionsGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().missionsGUISize, IridiumSkyblock.getInstance().getInventories().missionsGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            Island island = getIsland();
            for (Missions.Mission mission : IridiumSkyblock.getInstance().getMissions().missions) {
                List<Placeholder> placeholderList = StringUtils.getIslandPlaceholders(island);

                if (!island.getMissionLevels().containsKey(mission.name))
                    island.getMissionLevels().put(mission.name, 1);

                Missions.MissionData data = mission.levels.get(island.getMissionLevels().get(mission.name));

                placeholderList.add(new Placeholder("level", island.getMissionLevels().get(mission.name) + ""));
                placeholderList.add(new Placeholder("vaultReward", data.vaultReward + ""));
                placeholderList.add(new Placeholder("crystalsReward", data.crystalReward + ""));
                placeholderList.add(new Placeholder("amount", data.amount + ""));
                placeholderList.add(new Placeholder("status", island.getMission(mission.name) == Integer.MIN_VALUE ? IridiumSkyblock.getInstance().getMessages().completed : island.getMission(mission.name) + "/" + data.amount + ""));
                setItem(mission.item.slot, ItemStackUtils.makeItemHidden(mission.item, placeholderList));
            }
            if (IridiumSkyblock.getInstance().getInventories().backButtons)
                setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInstance().getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
        }
    }
}
