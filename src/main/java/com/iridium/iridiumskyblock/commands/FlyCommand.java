package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends Command {

    public FlyCommand() {
        super(Arrays.asList("fly", "flight"), "Toggle your ability to fly", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (user.getIsland().isInIsland(p.getLocation())) {
                if (user.getIsland().flightBooster != 0 || p.hasPermission("iridiumskyblock.Fly")) {
                    if (p.getAllowFlight()) {
                        p.setAllowFlight(false);
                        p.setFlying(false);
                        p.sendMessage(Utils.color(IridiumSkyblock.messages.flightDisabled.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    } else {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                        p.sendMessage(Utils.color(IridiumSkyblock.messages.flightEnabled.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                    user.flying = p.isFlying();
                } else {
                    p.sendMessage(Utils.color(IridiumSkyblock.messages.flightBoosterNotActive.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.messages.mustBeInIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
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
