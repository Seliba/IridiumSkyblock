package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.NumberFormatter;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        if (player.isOp()) {
            final String latest = plugin.getLatest();
            if (latest != null
                    && IridiumSkyblock.getInstance().getConfiguration().notifyAvailableUpdate
                    && !latest.equals(plugin.getDescription().getVersion())) {
                final String prefix = IridiumSkyblock.getInstance().getConfiguration().prefix;
                player.sendMessage(StringUtils.color(prefix + " &7This message is only seen by opped players."));
                player.sendMessage(StringUtils.color(prefix + " &7Newer version available: " + latest));
            }
        }
        final Location location = player.getLocation();
        final User user = User.getUser(player);
        user.name = player.getName();
        Island island = user.getIsland();
        if (island != null) {
            if (!user.tookInterestMessage) {
                if (island.interestMoney != 0 && island.interestExp != 0 && island.interestCrystal != 0) {
                    player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().islandInterest
                            .replace("%exp%", NumberFormatter.format(island.interestExp))
                            .replace("%crystals%", NumberFormatter.format(island.interestCrystal))
                            .replace("%money%", NumberFormatter.format(island.interestMoney))
                            .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                }
                user.tookInterestMessage = true;
            }
        }

        if (island == null && IridiumSkyblock.getInstance().getConfiguration().createIslandOnJoin) {
            if (!user.isOnCooldown() || IridiumSkyblock.getInstance().getConfiguration().ignoreCooldownOnJoinCreation) {
                IslandManager.createIsland(player);
            } else {
                player.sendMessage(StringUtils.color(user.getCooldownTimeMessage()));
            }
        }

        if (user.flying && (island == null || island.isInIsland(location) || island.getBoosterTime(IridiumSkyblock.getInstance().getBoosters().islandFlightBooster.name) == 0)) {
            player.setAllowFlight(false);
            player.setFlying(false);
            user.flying = false;
        }
        if (IridiumSkyblock.getInstance().getConfiguration().disableBypassOnJoin || !player.hasPermission(IridiumSkyblock.getInstance().getCommands().bypassCommand.permission))
            user.bypassing = false;

        final Island visitingIsland = IslandManager.getIslandViaLocation(location);
        if (visitingIsland == null) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> visitingIsland.sendBorder(player), 1);
        Bukkit.getScheduler().runTaskLater(plugin, () -> visitingIsland.sendHolograms(player), 1);
    }

}
