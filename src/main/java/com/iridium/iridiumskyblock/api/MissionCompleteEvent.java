package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.MissionType;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MissionCompleteEvent extends Event {
    @NotNull private static final HandlerList handlers = new HandlerList();
    @NotNull public HandlerList getHandlers() { return handlers; }
    @NotNull public static HandlerList getHandlerList() {
        return handlers;
    }
    @NotNull @Getter String missionName;
    @NotNull @Getter MissionType missionType;
    @Getter int missionLevel;

    public MissionCompleteEvent(@NotNull String missionName, @NotNull MissionType missionType, int missionLevel) {
        this.missionName = missionName;
        this.missionType = missionType;
        this.missionLevel = missionLevel;
    }
}