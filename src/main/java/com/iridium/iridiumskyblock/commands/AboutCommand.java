package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;

public class AboutCommand extends Command {

    public AboutCommand() {
        super(Arrays.asList("about", "version"), "Displays plugin info", "", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(IridiumColorAPI.color("Plugin Name: IridiumSkyblock", new Color(0x05C195), new Color(0x0560C1)));
        sender.sendMessage(IridiumColorAPI.color("Plugin Version: " + IridiumSkyblock.instance.getDescription().getVersion(), new Color(0x05C195), new Color(0x0560C1)));
        sender.sendMessage(IridiumColorAPI.color("Coded by IridiumDevelopment", new Color(0x05C195), new Color(0x0560C1)));
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
