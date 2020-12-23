package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.gui.ConfirmationGUI;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferCommand extends Command {

    public TransferCommand() {
        super(Collections.singletonList("transfer"), "Transfer island ownership", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is transfer <player>");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            Island island = user.getIsland();
            if (island.owner.equals(p.getUniqueId().toString())) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (User.getUser(player).getIsland() == island) {
                    p.openInventory(new ConfirmationGUI(user.getIsland(), () -> island.setOwner(player), IridiumSkyblock.messages.transferAction.replace("%player%", player.getName())).getInventory());
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.notInYourIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is admin <island> transfer <player>");
            return;
        }
        Player p = (Player) sender;
        if (island != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (User.getUser(player).getIsland() == island) {
                p.openInventory(new ConfirmationGUI(island, () -> island.setOwner(player), IridiumSkyblock.messages.transferAction.replace("%player%", player.getName())).getInventory());
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.notInYourIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
