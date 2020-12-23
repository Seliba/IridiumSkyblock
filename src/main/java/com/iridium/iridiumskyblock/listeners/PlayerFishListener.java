package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.MissionType;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.managers.IslandManager;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishListener implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        try {
            final Player player = event.getPlayer();
            final Location location = player.getLocation();
            if (!IslandManager.isIslandWorld(location)) return;

            if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

            final User user = User.getUser(player);
            final Island island = user.getIsland();
            if (island == null) return;

            for (Mission mission : IridiumSkyblock.missions.missions) {
                final Map<String, Integer> levels = island.getMissionLevels();
                levels.putIfAbsent(mission.name, 1);

                final MissionData level = mission.levels.get(levels.get(mission.name));
                if (level.type == MissionType.FISH_CATCH)
                    island.addMission(mission.name, 1);
            }
        } catch (Exception e) {
            IridiumSkyblock.instance.sendErrorMessage(e);
        }
    }
}
