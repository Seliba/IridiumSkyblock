package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class GiveUpgradeCommand extends Command {

    public GiveUpgradeCommand() {
        super(Collections.singletonList("giveupgrade"), "Give an Island an Upgrade", "iridiumskyblock.giveupgrade", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 4 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is giveupgrade <player> <upgrade> <level>");
            return;
        }

        if (Bukkit.getPlayer(args[1]) != null) {
            OfflinePlayer player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                Island island = User.getUser(player).getIsland();
                if (island != null) {
                    try {
                        if (args[2].equalsIgnoreCase("size")) {
                            int amount = args.length == 3 ? island.sizeLevel + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.upgrades.sizeUpgrade.upgrades.containsKey(amount)) {
                                island.setSizeLevel(amount);
                            }
                        }
                        if (args[2].equalsIgnoreCase("member")) {
                            int amount = args.length == 3 ? island.memberLevel + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.upgrades.memberUpgrade.upgrades.containsKey(amount)) {
                                island.memberLevel = amount;
                            }
                        }
                        if (args[2].equalsIgnoreCase("warp")) {
                            int amount = args.length == 3 ? island.warpLevel + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.upgrades.warpUpgrade.upgrades.containsKey(amount)) {
                                island.warpLevel = amount;
                            }
                        }
                        if (args[2].equalsIgnoreCase("ores")) {
                            int amount = args.length == 3 ? island.oreLevel + 1 : Integer.parseInt(args[3]);
                            if (IridiumSkyblock.upgrades.oresUpgrade.upgrades.containsKey(amount)) {
                                island.oreLevel = amount;
                            }
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(args[2] + " is not a number");
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
            return Arrays.asList("size", "member", "warp", "ores");
        }
        return null;
    }
}