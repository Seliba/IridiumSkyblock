package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PublicCommand extends Command {

    public PublicCommand() {
        super(Collections.singletonList("public"), "allow players to visit your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (user.bypassing || user.getIsland().getPermissions(user.role).islandprivate) {
                user.getIsland().visit = true;
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.islandNowPublic.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (island != null) {
            island.visit = true;
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.islandNowPublic.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
