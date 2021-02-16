package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BanCommand extends Command {

    public BanCommand() {
        super(Collections.singletonList("ban"), "Ban a player from visiting your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is ban <player>");
            sender.sendMessage("/is ban <player>");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!user.getIsland().equals(User.getUser(player).getIsland())) {
                user.getIsland().addBan(User.getUser(player));
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerBanned.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                if (player.getPlayer() != null) {
                    if (user.getIsland().isInIsland(player.getPlayer().getLocation())) {
                        player.getPlayer().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().bannedFromIsland.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                        user.getIsland().spawnPlayer(player.getPlayer());
                    }
                }
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is admin <island> ban <player>");
            sender.sendMessage("/is admin <island> ban <player>");
            return;
        }
        if (island != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[3]);
            if (!island.equals(User.getUser(player).getIsland())) {
                island.addBan(User.getUser(player));
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerBanned.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                if (player.getPlayer() != null) {
                    if (island.isInIsland(player.getPlayer().getLocation())) {
                        player.getPlayer().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().bannedFromIsland.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                        island.spawnPlayer(player.getPlayer());
                    }
                }
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
