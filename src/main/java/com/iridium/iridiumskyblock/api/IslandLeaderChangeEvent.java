package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class IslandLeaderChangeEvent extends IslandEvent implements Cancellable {
    @NotNull
    private final User fromUser;
    @NotNull
    private final User newOwner;
    private boolean cancelled;

    public IslandLeaderChangeEvent(@NotNull Island island, @NotNull User fromUser, @NotNull User target) {
        super(island);
        this.fromUser = fromUser;
        newOwner = target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public User getFromUser() {
        return newOwner;
    }

    public User getNewOwner() {
        return fromUser;
    }
}
