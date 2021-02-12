package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.api.IslandKickEvent;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class KickCommand extends Command {

    public KickCommand() {
        super(Collections.singletonList("kick"), "Kick a player from your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is kick <player>");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p); // User kicking the player
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User u = User.getUser(player); // Player we want to kick
        Island island = user.getIsland();
        if (island != null) {
            if (island.id == u.islandID) {
                if (u.role.equals(Role.Owner)) {
                    sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cantKickOwner.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                } else {
                    if (user.bypassing || user.getIsland().getPermissions(u.getRole()).kickMembers) {
                        IslandKickEvent islandKickEvent = new IslandKickEvent(user.getIsland(), user, u);
                        Bukkit.getPluginManager().callEvent(islandKickEvent);
                        if (!islandKickEvent.isCancelled()) {
                            user.getIsland().removeUser(u);
                            if (player.getPlayer() != null) {
                                player.getPlayer().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().youHaveBeenKicked.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                            }
                        }
                    } else {
                        sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    }
                }
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is admin <island> kick <player>");
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User u = User.getUser(player); // Player we want to kick
        if (island != null) {
            if (island.equals(u.getIsland())) {
                if (u.role.equals(Role.Owner)) {
                    sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cantKickOwner.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                } else {
                    IslandKickEvent islandKickEvent = new IslandKickEvent(island, null, u);
                    Bukkit.getPluginManager().callEvent(islandKickEvent);
                    if (!islandKickEvent.isCancelled()) {
                        island.removeUser(u);
                        if (player.getPlayer() != null) {
                            player.getPlayer().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().youHaveBeenKicked.replace(
                                    "%prefix%",
                                    IridiumSkyblock.getInstance().getConfiguration().prefix
                            )));
                        }
                    }
                }
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
