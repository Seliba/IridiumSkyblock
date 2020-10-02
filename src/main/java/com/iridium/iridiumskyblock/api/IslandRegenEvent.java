package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class IslandRegenEvent extends IslandEvent implements Cancellable {

    public boolean cancelled;

    public IslandRegenEvent(@NotNull Island island)  {
        super(island);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
