package com.iridium.iridiumskyblock;

public enum Role {
    Owner(4), CoOwner(3), Moderator(2), Member(1), Visitor(-1);

    public final int rank;

    Role(int rank) {
        this.rank = rank;
    }

    public static Role getViaRank(int i) {
        for (Role role : values()) {
            if (role.rank == i) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        if (IridiumSkyblock.getInstance().getMessages().roles.containsKey(this)) {
            return IridiumSkyblock.getInstance().getMessages().roles.get(this);
        }
        return this.name();
    }
}