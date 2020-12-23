package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BiomeCommand extends Command {

    public BiomeCommand() {
        super(Collections.singletonList("biome"), "Opens the biome GUI", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        World.Environment environment = p.getWorld().getEnvironment();
        if (user.getIsland() != null) {
            switch (environment) {
                case NORMAL:
                    p.openInventory(user.getIsland().biomeGUI.pages.get(1).getInventory());
                    break;
                case NETHER:
                    p.openInventory(user.getIsland().netherBiomeGUI.pages.get(1).getInventory());
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            p.openInventory(island.biomeGUI.pages.get(1).getInventory());
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
