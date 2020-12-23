package com.iridium.iridiumskyblock.support;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import gcspawners.ASAPI;
import org.bukkit.block.CreatureSpawner;

public class AdvancedSpawners implements SpawnerSupport {

    public AdvancedSpawners() {
        IridiumSkyblock.instance.getLogger().info("AdvancedSpawners support loaded");
    }

    public int getSpawnerAmount(CreatureSpawner spawner) {
        return ASAPI.getSpawnerAmount(spawner.getBlock());
    }
}
