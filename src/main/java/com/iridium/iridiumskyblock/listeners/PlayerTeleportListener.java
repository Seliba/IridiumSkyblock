package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        try {
            final Location toLocation = event.getTo();
            final Location fromLocation = event.getFrom();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(toLocation);
            if (island == null) return;

            final Player player = event.getPlayer();
            final User user = User.getUser(player);

            if (user.islandID == island.getId()) return;

            if (toLocation.getWorld() == fromLocation.getWorld() && toLocation.distance(fromLocation) < 1) return;

            if ((island.isVisit() && !island.isBanned(user)) || user.bypassing) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> island.sendBorder(player), 1);
                if (user.islandID != island.getId()) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    for (String pl : island.getMembers()) {
                        Player p = Bukkit.getPlayer(User.getUser(pl).name);
                        if (p != null) {
                            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    }
                }
            } else {
                event.setCancelled(true);
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
