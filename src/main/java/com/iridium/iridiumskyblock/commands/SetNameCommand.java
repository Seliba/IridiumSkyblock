package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNameCommand extends Command {

    public SetNameCommand() {
        super(Collections.singletonList("setname"), "Set your islands name", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is setname <Island Name>");
            return;
        }
        if (user.getIsland() != null) {
            if (user.role.equals(Role.Owner)) {
                if (args[1].length() > IridiumSkyblock.configuration.maxIslandName) {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.islandNameTooLong
                            .replace("%prefix%", IridiumSkyblock.configuration.prefix)
                            .replace("%name%", args[1])
                            .replace("%max_length%", IridiumSkyblock.configuration.maxIslandName + "")));

                } else if (args[1].length() < IridiumSkyblock.configuration.minIslandName) {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.islandNameTooShort
                            .replace("%prefix%", IridiumSkyblock.configuration.prefix)
                            .replace("%name%", args[1])
                            .replace("%min_length%", IridiumSkyblock.configuration.minIslandName + "")));
                } else {
                    user.getIsland().name = args[1];
                    for (String member : user.getIsland().members) {
                        Player player = Bukkit.getPlayer(User.getUser(member).name);
                        if (player != null) {
                            player.sendMessage(Utils.color(IridiumSkyblock.messages.changesIslandName.replace("%player%", p.getName()).replace("%name%", args[1]).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        }
                    }
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is admin <island> setname <Island Name>");
            return;
        }
        if (island != null) {
            island.name = args[1];
            for (String member : island.members) {
                Player player = Bukkit.getPlayer(User.getUser(member).name);
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.messages.changesIslandName.replace("%player%", p.getName()).replace("%name%", args[1]).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
