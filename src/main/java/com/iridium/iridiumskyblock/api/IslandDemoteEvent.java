package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class IslandDemoteEvent extends IslandEvent implements Cancellable {
    @NotNull public final User target;
    @NotNull public final User demoter;
    @NotNull public final Role role;
    private boolean cancelled;

    public IslandDemoteEvent(@NotNull Island island, @NotNull User target, @NotNull User demoter, @NotNull Role role) {
        super(island);
        this.target = target;
        this.demoter = demoter;
        this.role = role;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

}
