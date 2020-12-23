package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super(Collections.singletonList("reload"), "Reload your configurations", "iridiumskyblock.reload", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        IridiumSkyblock.instance.loadConfigs();
        sender.sendMessage(Utils.color(IridiumSkyblock.messages.reloaded.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
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
