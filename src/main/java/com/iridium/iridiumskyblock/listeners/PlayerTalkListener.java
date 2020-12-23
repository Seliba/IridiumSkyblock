package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerTalkListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTalk(AsyncPlayerChatEvent event) {
        try {
            final Player player = event.getPlayer();
            final User user = User.getUser(player);

            if (user.warp != null) {
                if (user.warp.getPassword().equals(event.getMessage())) {
                    Bukkit.getScheduler().runTask(IridiumSkyblock.instance, () -> { player.teleport(user.warp.getLocation()); user.warp = null; });
                    player.sendMessage(Utils.color(IridiumSkyblock.messages.teleporting
                            .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                } else {
                    player.sendMessage(Utils.color(IridiumSkyblock.messages.wrongPassword
                            .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    user.warp = null;
                }
                event.setCancelled(true);
            }

            final Island island = user.getIsland();

            String format = event.getFormat();
            if (format.contains(IridiumSkyblock.configuration.chatRankPlaceholder)) {
                if (island == null) {
                    format = format.replace(IridiumSkyblock.configuration.chatRankPlaceholder, "");
                } else {
                    format = format.replace(IridiumSkyblock.configuration.chatRankPlaceholder, Utils.getIslandRank(island) + "");
                }
            }
            if (format.contains(IridiumSkyblock.configuration.chatNAMEPlaceholder)) {
                if (island == null) {
                    format = format.replace(IridiumSkyblock.configuration.chatNAMEPlaceholder, "");
                } else {
                    format = format.replace(IridiumSkyblock.configuration.chatNAMEPlaceholder, island.getName());
                }
            }
            if (format.contains(IridiumSkyblock.configuration.chatValuePlaceholder)) {
                if (island == null) {
                    format = format.replace(IridiumSkyblock.configuration.chatValuePlaceholder, "");
                } else {
                    format = format.replace(IridiumSkyblock.configuration.chatValuePlaceholder, island.getFormattedValue());
                }
            }
            if (format.contains(IridiumSkyblock.configuration.chatLevelPlaceholder)) {
                if (island == null) {
                    format = format.replace(IridiumSkyblock.configuration.chatLevelPlaceholder, "");
                } else {
                    format = format.replace(IridiumSkyblock.configuration.chatLevelPlaceholder, island.getFormattedValue());
                }
            }

            if (island != null && user.islandChat) {
                for (String member : island.members) {
                    final Player islandPlayer = Bukkit.getPlayer(User.getUser(member).name);
                    if (islandPlayer == null) continue;
                    islandPlayer.sendMessage(Utils.color(IridiumSkyblock.messages.chatFormat)
                            .replace(IridiumSkyblock.configuration.chatValuePlaceholder, island.getFormattedValue())
                            .replace(IridiumSkyblock.configuration.chatNAMEPlaceholder, island.getName())
                            .replace(IridiumSkyblock.configuration.chatLevelPlaceholder, island.getFormattedValue())
                            .replace(IridiumSkyblock.configuration.chatRankPlaceholder, Utils.getIslandRank(island) + "")
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));
                }
                Bukkit.getServer().getOnlinePlayers().stream()
                  .filter(onlinePlayer -> User.getUser(onlinePlayer).spyingIslandsChat)
                  .forEach((spyingPlayer) -> {
                        spyingPlayer.sendMessage(Utils.color(IridiumSkyblock.messages.spyChatFormat)
                            .replace(IridiumSkyblock.configuration.chatValuePlaceholder, island.getFormattedValue())
                            .replace(IridiumSkyblock.configuration.chatNAMEPlaceholder, island.getName())
                            .replace(IridiumSkyblock.configuration.chatLevelPlaceholder, island.getFormattedValue())
                            .replace(IridiumSkyblock.configuration.chatRankPlaceholder, Utils.getIslandRank(island) + "")
                            .replace("%player%", player.getName())
                            .replace("%message%", event.getMessage()));

                  });
                event.setCancelled(true);
            }

            event.setFormat(Utils.color(format));
        } catch (Exception e) {
            IridiumSkyblock.instance.sendErrorMessage(e);
        }
    }
}
