package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class AddValueCommand extends Command {

    public AddValueCommand() {
        super(Collections.singletonList("addvalue"), "Give an island extra value", "iridiumskyblock.addvalue", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + " /is addvalue <player> <amount>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    try {
                        island.addExtraValue(Double.parseDouble(args[2]));
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().addedValue.replace("%value%", args[2]).replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notNumber.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%error%", (args[2]))));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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
