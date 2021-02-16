package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PrivateCommand extends Command {

    public PrivateCommand() {
        super(Collections.singletonList("private"), "Stop players visiting your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (user.bypassing || user.getIsland().getPermissions(user.role).islandprivate) {
                user.getIsland().visit = false;
                user.getIsland().getPlayersOnIsland().stream().filter(player -> user.getIsland().id != User.getUser(player).getIsland().id && !User.getUser(player).bypassing && !user.getIsland().isCoop(User.getUser(player).getIsland()) && !player.hasPermission("iridiumskyblock.visitbypass")).forEach(player -> {
                    user.getIsland().spawnPlayer(player);
                    player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().expelledIslandLocked
                            .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                            .replace("%player%", p.getName())));
                });
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().islandNowPrivate
                        .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (island != null) {
            island.visit = false;
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().islandNowPrivate.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
