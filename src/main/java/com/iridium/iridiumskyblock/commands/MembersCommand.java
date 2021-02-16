package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MembersCommand extends Command {

    public MembersCommand() {
        super(Arrays.asList("members", "users", "list"), "View your island members", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        Island island;
        if (args.length == 2) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            island = User.getUser(player).getIsland();
        } else {
            island = user.getIsland();
        }
        if (island != null) {
            p.openInventory(island.membersGUI.getInventory());
        } else {
            if (args.length == 2) {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            p.openInventory(island.membersGUI.getInventory());
        } else {
            if (args.length == 2) {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
