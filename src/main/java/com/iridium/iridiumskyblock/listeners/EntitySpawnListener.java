package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        final Entity entity = event.getEntity();
        final Location location = entity.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;

        if (!IridiumSkyblock.configuration.blockedEntities.contains(event.getEntityType())) return;

        IridiumSkyblock.instance.entities.put(entity.getUniqueId(), island);
        monitorEntity(entity);
    }

    @EventHandler
    public void onVehicleSpawn(VehicleCreateEvent event) {
        final Vehicle vehicle = event.getVehicle();
        final Location location = vehicle.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;

        if (!IridiumSkyblock.configuration.blockedEntities.contains(vehicle.getType())) return;

        IridiumSkyblock.instance.entities.put(vehicle.getUniqueId(), island);
        monitorEntity(vehicle);
    }

    public void monitorEntity(Entity entity) {
        if (entity == null) return;
        if (entity.isDead()) return;

        final UUID uuid = entity.getUniqueId();
        final Island startingIsland = IridiumSkyblock.instance.entities.get(uuid);
        if (startingIsland.isInIsland(entity.getLocation())) {
            //The entity is still in the island, so make a scheduler to check again
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.instance, () -> monitorEntity(entity), 20);
        } else {
            //The entity is not in the island, so remove it
            entity.remove();
            IridiumSkyblock.instance.entities.remove(uuid);
        }
    }
}
