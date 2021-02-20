package com.iridium.iridiumskyblock.support;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import org.bukkit.block.CreatureSpawner;

public class Wildstacker implements SpawnerSupport {

    public Wildstacker() {
        IridiumSkyblock.getInstance().getLogger().info("Wildstacker support loaded");
    }

    public int getSpawnerAmount(CreatureSpawner spawner) {
        return WildStackerAPI.getSpawnersAmount(spawner);
    }
}
