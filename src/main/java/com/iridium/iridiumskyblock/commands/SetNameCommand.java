package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetNameCommand extends Command {

    public SetNameCommand() {
        super(Collections.singletonList("setname"), "Set your islands name", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is setname <Island Name>");
            return;
        }
        if (user.getIsland() != null) {
            if (user.role.equals(Role.Owner)) {
                if (args[1].length() > IridiumSkyblock.getInstance().getConfiguration().maxIslandName) {
                    sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().islandNameTooLong
                            .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                            .replace("%name%", args[1])
                            .replace("%max_length%", IridiumSkyblock.getInstance().getConfiguration().maxIslandName + "")));

                } else if (args[1].length() < IridiumSkyblock.getInstance().getConfiguration().minIslandName) {
                    sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().islandNameTooShort
                            .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                            .replace("%name%", args[1])
                            .replace("%min_length%", IridiumSkyblock.getInstance().getConfiguration().minIslandName + "")));
                } else {
                    user.getIsland().name = args[1];
                    for (String member : user.getIsland().members) {
                        Player player = Bukkit.getPlayer(User.getUser(member).name);
                        if (player != null) {
                            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().changesIslandName.replace("%player%", p.getName()).replace("%name%", args[1]).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                        }
                    }
                }
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is admin <island> setname <Island Name>");
            return;
        }
        if (island != null) {
            island.name = args[1];
            for (String member : island.members) {
                Player player = Bukkit.getPlayer(User.getUser(member).name);
                if (player != null) {
                    player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().changesIslandName.replace("%player%", p.getName()).replace("%name%", args[1]).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
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
