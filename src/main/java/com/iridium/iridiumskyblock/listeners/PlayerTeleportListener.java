package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        try {
            final Location toLocation = event.getTo();
            final Location fromLocation = event.getFrom();
            if (!IslandManager.isIslandWorld(toLocation)) return;
            final Island toIsland = IslandManager.getIslandViaLocation(toLocation);
            if (toIsland == null) return;

            final Player player = event.getPlayer();
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.instance, () -> toIsland.sendHolograms(player), 1);
            final User user = User.getUser(player);

            if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
                Island fromIsland = IslandManager.getIslandViaLocation(fromLocation);
                if (fromIsland == null || !fromIsland.isInIsland(toLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (user.islandID == toIsland.id) return;

            if ((toIsland.visit && !toIsland.isBanned(user)) || user.bypassing || player.hasPermission("iridiumskyblock.visitbypass")) {
                if (!toIsland.isInIsland(fromLocation)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.instance, () -> toIsland.sendBorder(player), 1);
                    if (user.islandID != toIsland.id) {
                        player.sendMessage(Utils.color(IridiumSkyblock.messages.visitingIsland.replace("%player%", User.getUser(toIsland.owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        if (player.hasPermission("iridiumskyblock.silentvisit")) return;
                        for (String pl : toIsland.members) {
                            Player p = Bukkit.getPlayer(UUID.fromString(pl));
                            if (p != null && p.canSee(player)) {
                                p.sendMessage(Utils.color(IridiumSkyblock.messages.visitedYourIsland.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                            }
                        }
                    }
                }
            } else {
                event.setCancelled(true);
                player.sendMessage(Utils.color(IridiumSkyblock.messages.playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } catch (Exception e) {
            IridiumSkyblock.instance.sendErrorMessage(e);
        }
    }
}
