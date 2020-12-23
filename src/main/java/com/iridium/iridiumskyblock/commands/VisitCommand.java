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

public class VisitCommand extends Command {

    public VisitCommand() {
        super(Collections.singletonList("visit"), "Visit another players island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2) {
            p.openInventory(IridiumSkyblock.visitGUI.get(1).getInventory());
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User user = User.getUser(player);
        if (user.getIsland() != null) {
            if (user.getIsland().visit || User.getUser(p).bypassing || p.hasPermission("iridiumskyblock.visitbypass")) {
                user.getIsland().teleportHome(p);
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
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
