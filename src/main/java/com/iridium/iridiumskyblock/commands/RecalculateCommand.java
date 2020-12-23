package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class RecalculateCommand extends Command {

    private Integer id = 0;

    public RecalculateCommand() {
        super(Arrays.asList("recalc", "recalculate"), "Recalculate all island values", "recalculate", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (id != 0) {
            sender.sendMessage(Utils.color(IridiumSkyblock.messages.calculationAlreadyInProcess.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            return;
        }
        int interval = 5;
        int total = IslandManager.getLoadedIslands().size();
        double totalSeconds = total * (interval / 20.00);
        int minutes = (int) Math.floor(totalSeconds / 60.00);
        double seconds = (int) (totalSeconds - (minutes * 60));
        sender.sendMessage(total + " " + totalSeconds + " " + minutes + " " + seconds);
        sender.sendMessage(Utils.color(IridiumSkyblock.messages.calculatingIslands.replace("%amount%", total + "").replace("%seconds%", seconds + "").replace("%minutes%", minutes + "").replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(IridiumSkyblock.instance, new Runnable() {
            ListIterator<Integer> islands = IslandManager.getLoadedIslands().stream().map(is -> is.id).collect(Collectors.toList()).listIterator();

            @Override
            public void run() {
                if (islands.hasNext()) {
                    int id = islands.next();
                    Island island = IslandManager.getIslandViaId(id);
                    if (island != null) {
                        island.initBlocks();
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.calculatingFinished.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    Bukkit.getScheduler().cancelTask(id);
                    id = 0;
                }
            }
        }, 0, interval);
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
