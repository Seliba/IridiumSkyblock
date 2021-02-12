package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerTalkListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTalk(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final User user = User.getUser(player);

        if (user.islandWarp != null) {
            if (user.islandWarp.getPassword().equals(event.getMessage())) {
                Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> {
                    player.teleport(user.islandWarp.getLocation());
                    user.islandWarp = null;
                });
                player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().teleporting
                        .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().wrongPassword
                        .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                user.islandWarp = null;
            }
            event.setCancelled(true);
        }

        final Island island = user.getIsland();

        String format = event.getFormat();
        if (format.contains(IridiumSkyblock.getInstance().getConfiguration().chatRankPlaceholder)) {
            if (island == null) {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatRankPlaceholder, "");
            } else {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatRankPlaceholder, Integer.toString(island.getRank()));
            }
        }
        if (format.contains(IridiumSkyblock.getInstance().getConfiguration().chatNAMEPlaceholder)) {
            if (island == null) {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatNAMEPlaceholder, "");
            } else {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatNAMEPlaceholder, island.getName());
            }
        }
        if (format.contains(IridiumSkyblock.getInstance().getConfiguration().chatValuePlaceholder)) {
            if (island == null) {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatValuePlaceholder, "");
            } else {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatValuePlaceholder, island.getFormattedValue());
            }
        }
        if (format.contains(IridiumSkyblock.getInstance().getConfiguration().chatLevelPlaceholder)) {
            if (island == null) {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatLevelPlaceholder, "");
            } else {
                format = format.replace(IridiumSkyblock.getInstance().getConfiguration().chatLevelPlaceholder, island.getFormattedLevel());
            }
        }

        if (island != null && user.islandChat) {
            for (String member : island.members) {
                final Player islandPlayer = Bukkit.getPlayer(User.getUser(member).name);
                if (islandPlayer == null) continue;
                islandPlayer.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().chatFormat)
                        .replace(IridiumSkyblock.getInstance().getConfiguration().chatValuePlaceholder, island.getFormattedValue())
                        .replace(IridiumSkyblock.getInstance().getConfiguration().chatNAMEPlaceholder, island.getName())
                        .replace(IridiumSkyblock.getInstance().getConfiguration().chatLevelPlaceholder, island.getFormattedLevel())
                        .replace(IridiumSkyblock.getInstance().getConfiguration().chatRankPlaceholder, Integer.toString(island.getRank()))
                        .replace("%player%", player.getName())
                        .replace("%message%", event.getMessage()));
            }
            Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(onlinePlayer -> User.getUser(onlinePlayer).spyingIslandsChat)
                    .forEach((spyingPlayer) -> spyingPlayer.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().spyChatFormat)
                            .replace(IridiumSkyblock.getInstance().getConfiguration().chatValuePlaceholder, island.getFormattedValue())
                            .replace(IridiumSkyblock.getInstance().getConfiguration().chatNAMEPlaceholder, island.getName())
                            .replace(IridiumSkyblock.getInstance().getConfiguration().chatLevelPlaceholder, island.getFormattedLevel())
                            .replace(IridiumSkyblock.getInstance().getConfiguration().chatRankPlaceholder, Integer.toString(island.getRank()))
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage())));
            event.setCancelled(true);
        }

        event.setFormat(StringUtils.color(format));
    }
}
