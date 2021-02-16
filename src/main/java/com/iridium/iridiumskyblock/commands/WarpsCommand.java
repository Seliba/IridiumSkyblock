package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

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
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            if (user.getIsland() != null) {
                p.openInventory(user.getIsland().warpGUI.getInventory());
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
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
