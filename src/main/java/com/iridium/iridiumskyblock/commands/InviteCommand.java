package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class InviteCommand extends Command {

    public InviteCommand() {
        super(Collections.singletonList("invite"), "Invite a player to your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is invite player");
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
                    sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                }
            } else {
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerAlreadyHaveIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length != 4) {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getConfiguration().prefix) + "/is admin <island> invite player");
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
                sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerAlreadyHaveIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    private void runCommand(Player player, OfflinePlayer invitedPlayer) {
        player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().playerInvited.replace("%player%", invitedPlayer.getName()).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        if (invitedPlayer.getPlayer() != null) {
            BaseComponent[] components = TextComponent.fromLegacyText(StringUtils.color(IridiumSkyblock.getInstance().getMessages().invitedByPlayer.replace("%player%", player
                    .getName()).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));

            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is join " + player
                    .getName());
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumSkyblock.getInstance().getMessages().inviteHoverMessage).create());
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
