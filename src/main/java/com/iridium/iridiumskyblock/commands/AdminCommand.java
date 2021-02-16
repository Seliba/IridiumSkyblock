package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class AdminCommand extends Command {

    public AdminCommand() {
        super(Collections.singletonList("admin"), "Control a players Island", "iridiumskyblock.admin", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        //Open island admin gui
        if (args.length == 2) {
            runCommand(args, p);
        } else if (args.length >= 2) {
            Island island;
            if (!org.apache.commons.lang.StringUtils.isNumeric(args[1])) {
                runCommand(args, p);
                return;
            }
            int id = Integer.parseInt(args[1]);
            island = IslandManager.getIslandViaId(id);
            if (island != null) {
                for (Command command : IridiumSkyblock.getInstance().getCommandManager().commands) {
                    if (command.aliases.contains(args[2]) && command.enabled) {
                        if ((sender.hasPermission(command.permission) || command.permission.equalsIgnoreCase("") || command.permission.equalsIgnoreCase("iridiumskyblock.")) && command.enabled) {
                            command.admin(sender, args, island);
                        } else {
                            // No permission
                            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                        }
                        return;
                    }
                }
            }
        } else {
            sender.sendMessage("/is admin <island>");
        }

    }

    private void runCommand(String[] args, Player p) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User u = User.getUser(player);
        Island island = u.getIsland();
        if (island != null) {
            p.openInventory(island.islandAdminGUI.getInventory());
        } else {
            p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
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
