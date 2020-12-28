package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LeaveCommand extends Command {

    public LeaveCommand() {
        super(Collections.singletonList("leave"), "Leave an island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (user.role.equals(Role.Owner)) {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantLeaveIfOwner.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            } else {
                user.getIsland().removeUser(user);
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().leftIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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
