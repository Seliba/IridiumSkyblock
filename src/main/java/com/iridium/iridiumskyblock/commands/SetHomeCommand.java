package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand extends Command {

    public SetHomeCommand() {
        super(Collections.singletonList("sethome"), "Set your island home", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (Utils.isSafe(p.getLocation(), user.getIsland()) && p.getLocation().getWorld().equals(IslandManager.getWorld())) {
                user.getIsland().home = p.getLocation();
                p.sendMessage(Utils.color(IridiumSkyblock.messages.setHome.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.messages.isNotSafe.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            if (Utils.isSafe(p.getLocation(), island) && p.getLocation().getWorld().equals(IslandManager.getWorld())) {
                island.home = p.getLocation();
                p.sendMessage(Utils.color(IridiumSkyblock.messages.setHome.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.messages.isNotSafe.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
