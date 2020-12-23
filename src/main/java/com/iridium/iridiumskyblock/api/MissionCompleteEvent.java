package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.MissionType;
import org.jetbrains.annotations.NotNull;

public class MissionCompleteEvent extends IslandEvent {
    @NotNull public final String missionName;
    @NotNull public final MissionType missionType;
    public final int missionLevel;

    public MissionCompleteEvent(@NotNull Island island, @NotNull String missionName, @NotNull MissionType missionType, int missionLevel) {
        super(island);
        this.missionName = missionName;
        this.missionType = missionType;
        this.missionLevel = missionLevel;
    }
}
