package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class GiveBoosterCommand extends Command {

    public GiveBoosterCommand() {
        super(Collections.singletonList("givebooster"), "Give an Island a Booster", "iridiumskyblock.givebooster", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 4 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is givebooster <player> <booster> <amount>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    if (args.length == 3 || StringUtils.isNumeric(args[3])) {
                        int amount = args.length == 3 ? 3600 : Integer.parseInt(args[3]);
                        if (args[2].equalsIgnoreCase("exp")) {
                            island.expBooster = amount;
                        }
                        if (args[2].equalsIgnoreCase("farming")) {
                            island.farmingBooster = amount;
                        }
                        if (args[2].equalsIgnoreCase("flight")) {
                            island.flightBooster = amount;
                        }
                        if (args[2].equalsIgnoreCase("spawner")) {
                            island.spawnerBooster = amount;
                        }
                    } else {
                        sender.sendMessage(Utils.color(IridiumSkyblock.messages.notNumber.replace("%prefix%", IridiumSkyblock.configuration.prefix).replace("%error%", args[2])));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerNoIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerOffline.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length == 3) {
            return Arrays.asList("exp", "farming", "flight", "spawner");
        }
        return null;
    }
}
