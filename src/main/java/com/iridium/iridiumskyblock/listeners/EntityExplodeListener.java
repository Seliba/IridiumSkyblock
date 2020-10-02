package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Map;
import java.util.UUID;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        try {
            final Entity entity = event.getEntity();
            final Location location = entity.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            if (!IridiumSkyblock.getConfiguration().allowExplosions)
                event.setCancelled(true);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitorEntityExplode(EntityExplodeEvent event) {
        try {
            final Entity entity = event.getEntity();
            final Location location = entity.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;

            final UUID uuid = entity.getUniqueId();
            final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
            final Map<UUID, Island> entities = plugin.entities;
            Island island = entities.get(uuid);
            if (island != null && island.isInIsland(location)) {
                event.setCancelled(true);
                entity.remove();
                entities.remove(uuid);
                return;
            }

            island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            for (Block block : event.blockList()) {
                if (!island.isInIsland(block.getLocation())) {
                    final BlockState state = block.getState();
                    IridiumSkyblock.nms.setBlockFast(block, 0, (byte) 0);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> state.update(true, true));
                }

                if (!Utils.isBlockValuable(block)) continue;

                if (!(block.getState() instanceof CreatureSpawner)) {
                    final Material material = block.getType();
                    final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                    island.valuableBlocks.computeIfPresent(xmaterial.name(), (name, original) -> original - 1);
                }

            }
            island.calculateIslandValue();
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }
}
