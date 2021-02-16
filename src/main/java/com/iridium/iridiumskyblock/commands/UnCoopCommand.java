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

public class UnCoopCommand extends Command {

    public UnCoopCommand() {
        super(Collections.singletonList("uncoop"), "Revokes an Islands coop", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is uncoop <player>");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!user.getIsland().equals(User.getUser(player).getIsland()) && User.getUser(player).getIsland() != null) {
                if (user.bypassing || user.getIsland().getPermissions(user.getRole()).coop) {
                    user.getIsland().removeCoop(User.getUser(player).getIsland());
                } else {
                    sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is admin <island> uncoop <player>");
            return;
        }
        if (island != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (!island.equals(User.getUser(player).getIsland()) && User.getUser(player).getIsland() != null) {
                island.removeCoop(User.getUser(player).getIsland());
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
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
