package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldBorderCommand extends Command {

    public WorldBorderCommand() {
        super(Arrays.asList("border", "worldborder", "color", "colour"), "Opens the WorldBorder GUI", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        Island island = user.getIsland();
        if (island != null) {
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("red") && IridiumSkyblock.getBorder().RedEnabled) {
                    island.setBorderColor(Color.Red);
                } else if (args[1].equalsIgnoreCase("blue") && IridiumSkyblock.getBorder().BlueEnabled) {
                    island.setBorderColor(Color.Blue);
                } else if (args[1].equalsIgnoreCase("green") && IridiumSkyblock.getBorder().GreenEnabled) {
                    island.setBorderColor(Color.Green);
                } else if (args[1].equalsIgnoreCase("off") && IridiumSkyblock.getBorder().OffEnabled) {
                    island.setBorderColor(Color.Off);
                } else {
                    p.openInventory(island.borderColorGUI.getInventory());
                }
            }else {
                p.openInventory(island.borderColorGUI.getInventory());
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            p.openInventory(island.borderColorGUI.getInventory());
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length == 2){
            ArrayList<String> result = new ArrayList<>();
            if (IridiumSkyblock.getBorder().BlueEnabled) result.add("blue");
            if (IridiumSkyblock.getBorder().OffEnabled) result.add("off");
            if (IridiumSkyblock.getBorder().GreenEnabled) result.add("green");
            if (IridiumSkyblock.getBorder().RedEnabled) result.add("red");
            return result;

        }
        return null;
    }
}
