package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.IslandManager;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class RecalculateCommand extends Command {

    private Integer id = 0;

    public RecalculateCommand() {
        super(Arrays.asList("recalc", "recalculate"), "Recalculate all island values", "recalculate", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (id != 0) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().calculationAlreadyInProcess.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        final IslandManager manager = IridiumSkyblock.getIslandManager();
        int interval = 5;
        int total = manager.islands.size();
        double totalSecconds = total * (interval / 20.00);
        int minutes = (int) Math.floor(totalSecconds / 60.00);
        double seconds = (int) (totalSecconds - (minutes * 60));
        sender.sendMessage(total + " " + totalSecconds + " " + minutes + " " + seconds);
        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().calculatingIslands.replace("%amount%", total + "").replace("%seconds%", seconds + "").replace("%minutes%", minutes + "").replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(IridiumSkyblock.getInstance(), new Runnable() {
            ListIterator<Integer> islands = new ArrayList<>(manager.islands.keySet()).listIterator();

            @Override
            public void run() {
                if (islands.hasNext()) {
                    int id = islands.next();
                    Island island = manager.getIslandViaId(id);
                    if (island != null) {
                        island.initBlocks();
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().calculatingFinished.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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
