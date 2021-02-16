package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.MissionType;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Map;

public class PlayerFishListener implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        final Player player = event.getPlayer();
        final Location location = event.getHook().getLocation();
        if (!IslandManager.isIslandWorld(location)) return;

        final User user = User.getUser(player);
        final Island userIsland = user.getIsland();
        if (userIsland == null) return;

        // Prevent entities from being caught on other islands
        if (!userIsland.isInIsland(location)) {
            Island island = IslandManager.getIslandViaLocation(location);
            if (!island.members.contains(user.player) && !island.isCoop(userIsland)) {
                if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        for (Mission mission : IridiumSkyblock.getInstance().getMissions().missions) {
            final Map<String, Integer> levels = userIsland.getMissionLevels();
            levels.putIfAbsent(mission.name, 1);

            final MissionData level = mission.levels.get(levels.get(mission.name));
            if (level.type == MissionType.FISH_CATCH)
                userIsland.addMission(mission.name, 1);
        }
    }
}
