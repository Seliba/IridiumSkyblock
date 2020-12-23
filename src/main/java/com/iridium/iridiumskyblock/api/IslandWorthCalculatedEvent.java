package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import org.jetbrains.annotations.NotNull;

public class IslandWorthCalculatedEvent extends IslandEvent {

    public double islandWorth;

    public IslandWorthCalculatedEvent(@NotNull Island island, double islandWorth) {
        super(island);
        this.islandWorth = islandWorth;
    }
}
