package com.iridium.iridiumskyblock.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.stream.Collectors;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        final Entity entity = event.getEntity();
        final Location location = entity.getLocation();
        Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;


        if (!IridiumSkyblock.getInstance().getConfiguration().allowExplosions) {
            event.setCancelled(true);
            return;
        }
        Island fromIsland = IridiumSkyblock.getInstance().entities.getOrDefault(entity.getUniqueId(), island);
        if (fromIsland == null || !fromIsland.isInIsland(location)) {
            event.setCancelled(true);
            IridiumSkyblock.getInstance().entities.remove(entity.getUniqueId());
            return;
        }

        IridiumSkyblock.getInstance().getLogger().info(fromIsland.stackedBlocks + "");

        event.blockList().stream()
                .filter(block -> block != null && (!fromIsland.isInIsland(block.getLocation()) || fromIsland.stackedBlocks.containsKey(block.getLocation())))
                .collect(Collectors.toList())
                .forEach(block -> event.blockList().remove(block));

        for (Block block : event.blockList()) {
            if (!MiscUtils.isBlockValuable(block)) continue;

            if (!(block.getState() instanceof CreatureSpawner)) {
                final Material material = block.getType();
                final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                island.valuableBlocks.computeIfPresent(xmaterial.name(), (name, original) -> original - 1);
            }
        }
        island.calculateIslandValue();
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        final Island island = IslandManager.getIslandViaLocation(event.getBlock().getLocation());
        if (island == null) return;
        event.blockList().stream()
                .filter(block -> block != null && (!island.isInIsland(block.getLocation()) || island.stackedBlocks.containsKey(block.getLocation())))
                .collect(Collectors.toList())
                .forEach(block -> event.blockList().remove(block));
    }
}
