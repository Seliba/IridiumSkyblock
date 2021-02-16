package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ChatCommand extends Command {

    public ChatCommand() {
        super(Collections.singletonList("chat"), "Talk to your island members", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            user.islandChat = !user.islandChat;
            if (user.islandChat) {
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().chatEnabled.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            } else {
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().chatDisabled.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
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
        return null;
    }
}
