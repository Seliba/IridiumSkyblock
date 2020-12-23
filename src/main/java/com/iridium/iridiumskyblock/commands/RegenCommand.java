package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.gui.ConfirmationGUI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegenCommand extends Command {

    public RegenCommand() {
        super(Arrays.asList("regen", "reset"), "Regenerate your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (user.role.equals(Role.Owner)) {
                if (user.bypassing || user.getIsland().getPermissions(user.role).regen) {
                    long time = user.getIsland().canGenerate() / 1000;
                    if (time == 0 || user.bypassing) {
                        if (IridiumSkyblock.schematics.schematics.size() == 1) {
                            p.openInventory(new ConfirmationGUI(user.getIsland(), () -> {
                                for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
                                    user.getIsland().schematic = schematic.name;
                                    user.getIsland().home = user.getIsland().home.add(schematic.x, schematic.y, schematic.z);
                                }
                                user.getIsland().pasteSchematic(true);
                                if (IridiumSkyblock.configuration.restartUpgradesOnRegen) {
                                    user.getIsland().resetMissions();
                                    user.getIsland().setSizeLevel(1);
                                    user.getIsland().memberLevel = 1;
                                    user.getIsland().warpLevel = 1;
                                    user.getIsland().oreLevel = 1;
                                    user.getIsland().flightBooster = 0;
                                    user.getIsland().expBooster = 0;
                                    user.getIsland().farmingBooster = 0;
                                    user.getIsland().spawnerBooster = 0;
                                    user.getIsland().crystals = 0;
                                    user.getIsland().exp = 0;
                                    user.getIsland().money = 0;
                                }
                                user.getIsland().teleportPlayersHome();
                            }, IridiumSkyblock.messages.resetAction).getInventory());
                        } else {
                            p.openInventory(user.getIsland().schematicSelectGUI.getInventory());
                        }
                    } else {
                        int day = (int) TimeUnit.SECONDS.toDays(time);
                        int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
                        int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
                        int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);
                        p.sendMessage(Utils.color(IridiumSkyblock.messages.regenCooldown.replace("%days%", day + "").replace("%hours%", hours + "").replace("%minutes%", minute + "").replace("%seconds%", second + "").replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.messages.mustBeIslandOwner.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            if (IridiumSkyblock.schematics.schematics.size() == 1) {
                p.openInventory(new ConfirmationGUI(island, () -> {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
                        island.schematic = schematic.name;
                        island.home = island.home.add(schematic.x, schematic.y, schematic.z);
                    }
                    island.pasteSchematic(true);
                    if (IridiumSkyblock.configuration.restartUpgradesOnRegen) {
                        island.resetMissions();
                        island.setSizeLevel(1);
                        island.memberLevel = 1;
                        island.warpLevel = 1;
                        island.oreLevel = 1;
                        island.flightBooster = 0;
                        island.expBooster = 0;
                        island.farmingBooster = 0;
                        island.spawnerBooster = 0;
                        island.crystals = 0;
                        island.exp = 0;
                        island.money = 0;
                    }
                    island.teleportPlayersHome();
                }, IridiumSkyblock.messages.resetAction).getInventory());
            } else {
                p.openInventory(island.schematicSelectGUI.getInventory());
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.noIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
