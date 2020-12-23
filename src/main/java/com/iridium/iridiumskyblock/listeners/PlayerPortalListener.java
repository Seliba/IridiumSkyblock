package com.iridium.iridiumskyblock.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerPortalListener implements Listener {

    public final boolean supports = XMaterial.supports(15);

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        try {
            final Location fromLocation = event.getFrom().clone();
            final Island island = IslandManager.getIslandViaLocation(fromLocation);
            if (island == null) return;

            if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) return;

            if (!IridiumSkyblock.configuration.netherIslands) {
                if (!IridiumSkyblock.configuration.publicNetherPortals) event.setCancelled(true);
                return;
            }

            final Player player = event.getPlayer();
            final User user = User.getUser(player);
            if (!island.getPermissions(user).useNetherPortal) {
                event.setCancelled(true);
                return;
            }

            if (supports) {
                event.setCanCreatePortal(true);

                // This setting forces portal search radius to 16, avoiding conflicts with bordering portals
                // (May have unintended consequences...?)
                if (IridiumSkyblock.configuration.forceShortPortalRadius)
                    event.setSearchRadius(16);
            } else {
                try {
                    PlayerPortalEvent.class.getMethod("useTravelAgent", boolean.class).invoke(event, true);
                    Class.forName("org.bukkit.TravelAgent")
                            .getMethod("setCanCreatePortal", boolean.class)
                            .invoke(PlayerPortalEvent.class.getMethod("getPortalTravelAgent").invoke(event), true);

                    // This setting forces portal search radius to 16, avoiding conflicts with bordering portals
                    // (May have unintended consequences...?)
                    if (IridiumSkyblock.configuration.forceShortPortalRadius) {
                        Class.forName("org.bukkit.TravelAgent")
                                .getMethod("setSearchRadius", int.class)
                                .invoke(PlayerPortalEvent.class.getMethod("getPortalTravelAgent").invoke(event), 16);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            final World world = fromLocation.getWorld();
            if (world == null) return;

            final String worldName = world.getName();

            if (worldName.equals(IridiumSkyblock.configuration.worldName))
                event.setTo(island.getNetherHome());
            else if (worldName.equals(IridiumSkyblock.configuration.netherWorldName))
                event.setTo(island.home);
            Bukkit.getScheduler().runTask(IridiumSkyblock.instance, () -> {
                Island is = IslandManager.getIslandViaLocation(player.getLocation());
                if (is != null) {
                    is.sendBorder(player);
                    is.sendHolograms(player);
                }
            });
        } catch (Exception e) {
            IridiumSkyblock.instance.sendErrorMessage(e);
        }
    }
}
