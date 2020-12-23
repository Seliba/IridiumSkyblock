package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatSpyCommand extends Command {

    public ChatSpyCommand() {
        super(Arrays.asList("chatspy", "spy"), "Spy on island chats", "iridiumskyblock.spy", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User u = User.getUser(p);
        u.spyingIslandsChat = !u.spyingIslandsChat;
        if (u.spyingIslandsChat) {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.chatSpyEnabled.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.chatSpyDisabled.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
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
