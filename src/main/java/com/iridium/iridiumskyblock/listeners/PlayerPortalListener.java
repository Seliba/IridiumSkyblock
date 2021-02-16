package com.iridium.iridiumskyblock.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;

public class PlayerPortalListener implements Listener {

    public final boolean supports = XMaterial.supports(15);

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        final Location fromLocation = event.getFrom().clone();
        final Island island = IslandManager.getIslandViaLocation(fromLocation);
        if (island == null) return;

        if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) return;

        if (!IridiumSkyblock.getInstance().getConfiguration().netherIslands) {
            if (!IridiumSkyblock.getInstance().getConfiguration().publicNetherPortals) event.setCancelled(true);
            return;
        }

        final Player player = event.getPlayer();
        final User user = User.getUser(player);
        if (!island.getPermissions(user).useNetherPortal) {
            event.setCancelled(true);
            return;
        }

        if (!IridiumSkyblock.getInstance().getConfiguration().netherPortalCreation) {
            event.setCancelled(true);
            island.teleportNetherHome(player);
        } else {
            if (supports) {
                event.setCanCreatePortal(true);
            } else {
                try {
                    PlayerPortalEvent.class.getMethod("useTravelAgent", boolean.class).invoke(event, true);
                    Class.forName("org.bukkit.TravelAgent")
                            .getMethod("setCanCreatePortal", boolean.class)
                            .invoke(PlayerPortalEvent.class.getMethod("getPortalTravelAgent").invoke(event), true);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                }
            }
        }

        final World world = fromLocation.getWorld();
        if (world == null) return;

        final String worldName = world.getName();

        if (worldName.equals(IridiumSkyblock.getInstance().getConfiguration().worldName))
            event.setTo(island.getNetherHome());
        else if (worldName.equals(IridiumSkyblock.getInstance().getConfiguration().netherWorldName))
            event.setTo(island.home);
        Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> {
            Island is = IslandManager.getIslandViaLocation(player.getLocation());
            if (is != null) {
                is.sendBorder(player);
                is.sendHolograms(player);
            }
        });
    }
}
