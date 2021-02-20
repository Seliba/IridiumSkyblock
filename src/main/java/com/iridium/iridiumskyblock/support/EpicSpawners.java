package com.iridium.iridiumskyblock.support;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import org.bukkit.block.CreatureSpawner;

public class EpicSpawners implements SpawnerSupport {

    public EpicSpawners() {
        IridiumSkyblock.getInstance().getLogger().info("EpicSpawners support loaded");
    }

    public int getSpawnerAmount(CreatureSpawner spawner) {
        return com.songoda.epicspawners.EpicSpawners.getInstance().getSpawnerManager().getSpawnerFromWorld(spawner.getLocation()).getSpawnCount();
    }
}
