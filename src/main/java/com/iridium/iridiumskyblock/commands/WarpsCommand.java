package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpsCommand extends Command {

    public WarpsCommand() {
        super(Collections.singletonList("warps"), "opens the Warp GUI", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        Island island;
        if (args.length == 2) {
            island = User.getUser(Bukkit.getOfflinePlayer(args[1])).getIsland();
        } else {
            island = user.getIsland();
        }
        if (island != null) {
            if (island.getPermissions(user.islandID == island.id ? user.role : Role.Visitor).useWarps || user.bypassing) {
                p.openInventory(island.warpGUI.getInventory());
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            if (user.getIsland() != null) {
                p.openInventory(user.getIsland().warpGUI.getInventory());
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
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
