package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        super(Arrays.asList("purge"), "Purge old islands", "iridiumskyblock.purge", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        int days;
        if (args.length < 2) {
            sender.sendMessage("/is purge <days>");
            return;
        }
        try {
            days = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().notNumber.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            return;
        }
        if (IslandManager.id != 0) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().calculationAlreadyInProcess.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            return;
        }
        int total = IslandManager.purgeIslands(days, sender);
        double totalSeconds = total * 5;
        int minutes = (int) Math.floor(totalSeconds / 60.00);
        double seconds = (int) (totalSeconds - (minutes * 60));
        sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().purgingIslands.replace("%seconds%", String.valueOf(seconds)).replace("%minutes%", String.valueOf(minutes)).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
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
