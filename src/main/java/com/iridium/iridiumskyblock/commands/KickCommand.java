package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.api.IslandKickEvent;
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
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is kick <player>");
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
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantKickOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                } else {
                    if (user.bypassing || user.getIsland().getPermissions(u.getRole()).kickMembers) {
                        IslandKickEvent islandKickEvent = new IslandKickEvent(user.getIsland(), user, u);
                        Bukkit.getPluginManager().callEvent(islandKickEvent);
                        if (!islandKickEvent.isCancelled()) {
                            user.getIsland().removeUser(u);
                            if (player.getPlayer() != null) {
                                player.getPlayer().sendMessage(Utils.color(IridiumSkyblock.getMessages().youHaveBeenKicked.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        }
                    } else {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is admin <island> kick <player>");
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User u = User.getUser(player); // Player we want to kick
        if (island != null) {
            if (island.equals(u.getIsland())) {
                if (u.role.equals(Role.Owner)) {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantKickOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                } else {
                    IslandKickEvent islandKickEvent = new IslandKickEvent(island, null, u);
                    Bukkit.getPluginManager().callEvent(islandKickEvent);
                    if (!islandKickEvent.isCancelled()) {
                        island.removeUser(u);
                        if (player.getPlayer() != null) {
                            player.getPlayer().sendMessage(Utils.color(IridiumSkyblock.getMessages().youHaveBeenKicked.replace(
                                    "%prefix%",
                                    IridiumSkyblock.getConfiguration().prefix
                            )));
                        }
                    }
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
