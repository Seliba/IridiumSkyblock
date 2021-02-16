package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.MiscUtils;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetHomeCommand extends Command {

    public SetHomeCommand() {
        super(Collections.singletonList("sethome"), "Set your island home", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (MiscUtils.isSafe(p.getLocation(), user.getIsland()) && p.getLocation().getWorld().equals(IslandManager.getWorld())) {
                user.getIsland().home = p.getLocation();
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().setHome.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().isNotSafe.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            if (MiscUtils.isSafe(p.getLocation(), island) && p.getLocation().getWorld().equals(IslandManager.getWorld())) {
                island.home = p.getLocation();
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().setHome.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().isNotSafe.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
