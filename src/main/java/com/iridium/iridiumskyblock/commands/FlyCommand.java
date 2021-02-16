package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

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
                if (user.getIsland().getBoosterTime(IridiumSkyblock.getInstance().getBoosters().islandFlightBooster.name) != 0 || p.hasPermission("iridiumskyblock.Fly")) {
                    if (args.length == 2) {
                        switch (args[1].toLowerCase()) {
                            case "on":
                                p.setAllowFlight(true);
                                p.setFlying(true);
                                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().flightEnabled.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                                return;
                            case "off":
                                p.setAllowFlight(false);
                                p.setFlying(false);
                                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().flightDisabled.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                                return;
                        }
                    }
                    if (p.getAllowFlight()) {
                        p.setAllowFlight(false);
                        p.setFlying(false);
                        p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().flightDisabled.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    } else {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                        p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().flightEnabled.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    }
                    user.flying = p.isFlying();
                } else {
                    p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().flightBoosterNotActive.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                }
            } else {
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().mustBeInIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return Arrays.asList("on", "off");
    }
}
