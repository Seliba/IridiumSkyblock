package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Crops;

import java.util.List;
import java.util.Map;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        try {
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) {
                User user = User.getUser(event.getPlayer());
                if (islandManager.isIslandWorld(event.getBlock().getWorld())) {
                    if (!user.bypassing) {
                        event.setCancelled(true);
                    }
                }
                return;
            }

            final Player player = event.getPlayer();
            final User user = User.getUser(player);

            final Material material = block.getType();
            final XMaterial xmaterial = XMaterial.matchXMaterial(material);
            final Config config = IridiumSkyblock.getConfiguration();
            final Integer max = config.limitedBlocks.get(xmaterial);
            if (max != null) {
                if (island.valuableBlocks.getOrDefault(xmaterial.name(), 0) >= max) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().blockLimitReached
                            .replace("%prefix%", config.prefix)));
                    event.setCancelled(true);
                    return;
                }
            }

            if (user.islandID == island.getId()) {
                for (Mission mission : IridiumSkyblock.getMissions().missions) {
                    final Map<String, Integer> levels = island.getMissionLevels();
                    levels.putIfAbsent(mission.name, 1);

                    final MissionData level = mission.levels.get(levels.get(mission.name));
                    if (level == null) continue;
                    if (level.type != MissionType.BLOCK_PLACE) continue;

                    final List<String> conditions = level.conditions;

                    if (
                            conditions.isEmpty()
                                    ||
                                    conditions.contains(xmaterial.name())
                                    ||
                                    conditions.contains(((Crops) block.getState().getData()).getState().toString())
                    )
                        island.addMission(mission.name, 1);
                }
            }

            if (!island.getPermissions(user).placeBlocks)
                event.setCancelled(true);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitorBlockPlace(BlockPlaceEvent event) {
        try {
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            if (!Utils.isBlockValuable(block)) return;

            final Material material = block.getType();
            final XMaterial xmaterial = XMaterial.matchXMaterial(material);
            island.valuableBlocks.compute(xmaterial.name(), (name, original) -> {
                if (original == null) return 1;
                return original + 1;
            });

            Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), island::calculateIslandValue);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
