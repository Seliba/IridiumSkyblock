package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class RemoveValueCommand extends Command {

    public RemoveValueCommand() {
        super(Collections.singletonList("removevalue"), "Take island value from an island", "iridiumskyblock.removevalue", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + " /is removevalue <player> <amount>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    try {
                        if (Double.parseDouble(args[2]) <= island.value) {
                            island.removeExtraValue(Double.parseDouble(args[2]));
                            sender.sendMessage(Utils.color(IridiumSkyblock.messages.removedValue.replace("%value%", args[2]).replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        } else {
                            sender.sendMessage(Utils.color(IridiumSkyblock.messages.notEnoughValue.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Utils.color(IridiumSkyblock.messages.notNumber.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%error%", args[2])));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerNoIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerOffline.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerOffline.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
