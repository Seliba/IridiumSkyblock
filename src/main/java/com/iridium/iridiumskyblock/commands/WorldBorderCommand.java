package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.Color;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                if (args[1].equalsIgnoreCase("red") && IridiumSkyblock.border.RedEnabled) {
                    island.setBorderColor(Color.Red);
                } else if (args[1].equalsIgnoreCase("blue") && IridiumSkyblock.border.BlueEnabled) {
                    island.setBorderColor(Color.Blue);
                } else if (args[1].equalsIgnoreCase("green") && IridiumSkyblock.border.GreenEnabled) {
                    island.setBorderColor(Color.Green);
                } else if (args[1].equalsIgnoreCase("off") && IridiumSkyblock.border.OffEnabled) {
                    island.setBorderColor(Color.Off);
                } else {
                    p.openInventory(island.borderColorGUI.getInventory());
                }
            }else {
                p.openInventory(island.borderColorGUI.getInventory());
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            p.openInventory(island.borderColorGUI.getInventory());
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length == 2){
            ArrayList<String> result = new ArrayList<>();
            if (IridiumSkyblock.border.BlueEnabled) result.add("blue");
            if (IridiumSkyblock.border.OffEnabled) result.add("off");
            if (IridiumSkyblock.border.GreenEnabled) result.add("green");
            if (IridiumSkyblock.border.RedEnabled) result.add("red");
            return result;

        }
        return null;
    }
}
