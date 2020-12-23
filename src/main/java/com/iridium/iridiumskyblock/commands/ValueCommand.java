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
import org.bukkit.entity.Player;

public class ValueCommand extends Command {

    public ValueCommand() {
        super(Collections.singletonList("value"), "Shows your island value", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        Island island;
        if (args.length == 2) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            island = User.getUser(player).getIsland();
        } else {
            island = user.getIsland();
        }
        if (island != null) {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.islandValue.replace("%value%", island.getFormattedValue()).replace("%rank%", Utils.getIslandRank(island) + "").replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
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
