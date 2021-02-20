package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.*;
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
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is setname <Island Name>");
            return;
        }
        if (user.getIsland() != null) {
            if (user.role.equals(Role.Owner)) {
                if (args[1].length() > IridiumSkyblock.getConfiguration().maxIslandName) {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandNameTooLong
                            .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                            .replace("%name%", args[1])
                            .replace("%max_length%", IridiumSkyblock.getConfiguration().maxIslandName + "")));

                } else if (args[1].length() < IridiumSkyblock.getConfiguration().minIslandName) {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandNameTooShort
                            .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                            .replace("%name%", args[1])
                            .replace("%min_length%", IridiumSkyblock.getConfiguration().minIslandName + "")));
                } else {
                    user.getIsland().name = args[1];
                    for (String member : user.getIsland().members) {
                        Player player = Bukkit.getPlayer(User.getUser(member).name);
                        if (player != null) {
                            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().changesIslandName.replace("%player%", p.getName()).replace("%name%", args[1]).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    }
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is admin <island> setname <Island Name>");
            return;
        }
        if (island != null) {
            island.name = args[1];
            for (String member : island.members) {
                Player player = Bukkit.getPlayer(User.getUser(member).name);
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().changesIslandName.replace("%player%", p.getName()).replace("%name%", args[1]).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
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
