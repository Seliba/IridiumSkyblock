package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.commands.Command;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor, TabCompleter {

    public List<Command> commands = new ArrayList<>();

    public CommandManager(String command) {
        IridiumSkyblock.instance.getCommand(command).setExecutor(this);
        IridiumSkyblock.instance.getCommand(command).setTabCompleter(this);
    }

    public void registerCommands() {
        List<String> manuallyRegisteredCommands = Arrays.asList("shopCommand");
        Arrays.stream(IridiumSkyblock.commands.getClass().getFields())
            .filter(field -> field.getClass().getSuperclass() == Command.class)
            .filter(field -> !manuallyRegisteredCommands.contains(field.getName()))
            .map(field -> {
                try {
                    return (Command) field.get(IridiumSkyblock.commands);
                } catch (IllegalAccessException exception) {
                    exception.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(this::registerCommand);

        if (IridiumSkyblock.configuration.islandShop) {
            registerCommand(IridiumSkyblock.commands.shopCommand);
        }
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void unRegisterCommand(Command command) {
        commands.remove(command);
    }

    @Override
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        try {
            if (!IridiumSkyblock.configuration.mainCommandPerm.equalsIgnoreCase("") && !cs
                .hasPermission(IridiumSkyblock.configuration.mainCommandPerm)) {
                cs.sendMessage(Utils.color(IridiumSkyblock.messages.noPermission
                    .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                return false;
            }
            if (args.length != 0) {
                for (Command command : commands) {
                    if (command.aliases.contains(args[0]) && command.enabled) {
                        if (command.player && !(cs instanceof Player)) {
                            // Must be a player
                            cs.sendMessage(Utils.color(IridiumSkyblock.messages.mustBeAPlayer
                                .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                            return true;
                        }
                        if ((cs.hasPermission(command.permission) || command.permission
                            .equalsIgnoreCase("") || command.permission
                            .equalsIgnoreCase("iridiumskyblock.")) && command.enabled) {
                            command.execute(cs, args);
                        } else {
                            // No permission
                            cs.sendMessage(Utils.color(IridiumSkyblock.messages.noPermission
                                .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        }
                        return true;
                    }
                }
            } else {
                if (cs instanceof Player) {
                    Player p = (Player) cs;
                    User u = User.getUser(p);
                    if (u.getIsland() != null) {
                        if (u.getIsland().schematic == null) {
                            if (IridiumSkyblock.schematics.schematics.size() == 1) {
                                for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
                                    u.getIsland().schematic = schematic.name;
                                }
                            } else {
                                p.openInventory(u.getIsland().schematicSelectGUI.getInventory());
                                return true;
                            }
                        }
                        if (IridiumSkyblock.configuration.islandMenu) {
                            p.openInventory(u.getIsland().islandMenuGUI.getInventory());
                        } else {
                            u.getIsland().teleportHome(p);
                        }
                    } else {
                        IslandManager.createIsland(p);
                    }
                    return true;
                }
            }
            cs.sendMessage(Utils.color(IridiumSkyblock.messages.unknownCommand
                .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        } catch (Exception e) {
            IridiumSkyblock.instance.sendErrorMessage(e);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        try {
            if (args.length == 1) {
                ArrayList<String> result = new ArrayList<>();
                for (Command command : commands) {
                    for (String alias : command.aliases) {
                        if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && (
                            command.enabled && (cs.hasPermission(command.permission)
                                || command.permission.equalsIgnoreCase("") || command.permission
                                .equalsIgnoreCase("iridiumskyblock.")))) {
                            result.add(alias);
                        }
                    }
                }
                return result;
            }
            for (Command command : commands) {
                if (command.aliases.contains(args[0]) && (command.enabled && (
                    cs.hasPermission(command.permission) || command.permission.equalsIgnoreCase("")
                        || command.permission.equalsIgnoreCase("iridiumskyblock.")))) {
                    return command.TabComplete(cs, cmd, s, args);
                }
            }
        } catch (Exception e) {
            IridiumSkyblock.instance.sendErrorMessage(e);
        }
        return null;
    }

}
