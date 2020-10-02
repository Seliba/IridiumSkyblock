package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.List;
import java.util.Random;

public class BlockFromToListener implements Listener {

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        try {
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            final Material material = block.getType();
            final Block toBlock = event.getToBlock();
            final Location toLocation = toBlock.getLocation();

            if (material.equals(Material.WATER) || material.equals(Material.LAVA)) {
                final Island toIsland = islandManager.getIslandViaLocation(toLocation);
                if (island != toIsland)
                    event.setCancelled(true);
            }

            if (!IridiumSkyblock.getUpgrades().oresUpgrade.enabled) return;

            if (event.getFace() == BlockFace.DOWN) return;

            if (!isSurroundedByWater(toLocation))
                return;

            final int oreLevel = island.getOreLevel();
            final World world = location.getWorld();
            if (world == null) return;

            final String worldName = world.getName();
            final Config config = IridiumSkyblock.getConfiguration();
            List<String> islandOreUpgrades;
            if (worldName.equals(config.worldName)) islandOreUpgrades = IridiumSkyblock.oreUpgradeCache.get(oreLevel);
            else if (worldName.equals(config.netherWorldName)) islandOreUpgrades = IridiumSkyblock.netherOreUpgradeCache.get(oreLevel);
            else return;

            Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> {
                final Material toMaterial = toBlock.getType();
                if (!(toMaterial.equals(Material.COBBLESTONE) || toMaterial.equals(Material.STONE)))
                    return;

                final Random random = new Random();
                final String oreUpgrade = islandOreUpgrades.get(random.nextInt(islandOreUpgrades.size()));

                final XMaterial oreUpgradeXmaterial = XMaterial.valueOf(oreUpgrade);
                final Material oreUpgradeMaterial = oreUpgradeXmaterial.parseMaterial(true);
                if (oreUpgradeMaterial == null) return;

                toBlock.setType(oreUpgradeMaterial);

                final BlockState blockState = toBlock.getState();
                blockState.update(true);

                if (Utils.isBlockValuable(toBlock)) {
                    final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                    island.valuableBlocks.compute(xmaterial.name(), (name, original) -> {
                        if (original == null) return 1;
                        return original + 1;
                    });
                    island.calculateIslandValue();
                }
            });
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler
    public void onBlockFrom(BlockFormEvent event) {
        try {
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            if (!event.getNewState().getType().equals(Material.OBSIDIAN)) return;

            island.failedGenerators.add(location);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    public boolean isSurroundedByWater(Location location) {
        final World world = location.getWorld();
        if (world == null) return false;
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final int[][] coords = {
                // At the same elevation
                {x + 1, y, z},
                {x - 1, y, z},
                {x, y, z + 1},
                {x, y, z - 1},
                // Above
                {x + 1, y + 1, z},
                {x - 1, y + 1, z},
                {x, y + 1, z + 1},
                {x, y + 1, z - 1},
                // Below
                {x + 1, y - 1, z},
                {x - 1, y - 1, z},
                {x, y - 1, z + 1},
                {x, y - 1, z - 1}
        };
        for (int[] coord : coords) {
            final Block block = world.getBlockAt(coord[0], coord[1], coord[2]);
            final Material material = block.getType();
            final String name = material.name();
            if (name.contains("WATER")) return true;
        }
        return false;
    }
}
