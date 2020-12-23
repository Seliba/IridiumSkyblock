package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InviteCommand extends Command {

    public InviteCommand() {
        super(Collections.singletonList("invite"), "Invite a player to your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is invite player");
            return;
        }
        Player p = (Player) sender;
        User user = User.getUser(p);
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User u = User.getUser(player);
        if (user.getIsland() != null) {
            if (u.getIsland() == null) {
                if (user.bypassing || user.getIsland().getPermissions(user.role).inviteMembers) {
                    u.invites.add(user.getIsland().id);
                    runCommand(p, player);
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerAlreadyHaveIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(Utils.color(IridiumSkyblock.configuration.prefix) + "/is admin <island> invite player");
            return;
        }
        Player p = (Player) sender;
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User u = User.getUser(player);
        if (island != null) {
            if (u.getIsland() == null) {
                u.invites.add(island.id);
                runCommand(p, player);
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.playerAlreadyHaveIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    private void runCommand(Player player, OfflinePlayer invitedPlayer) {
        player.sendMessage(Utils
                .color(IridiumSkyblock.messages.playerInvited.replace("%player%", invitedPlayer.getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        if (invitedPlayer.getPlayer() != null) {
            BaseComponent[] components = TextComponent.fromLegacyText(Utils.color(IridiumSkyblock.messages.invitedByPlayer.replace("%player%", player
                    .getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));

            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is join " + player
                    .getName());
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumSkyblock.messages.inviteHoverMessage).create());
            for (BaseComponent component : components) {
                component.setClickEvent(clickEvent);
                component.setHoverEvent(hoverEvent);
            }
            invitedPlayer.getPlayer().spigot().sendMessage(components);
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
