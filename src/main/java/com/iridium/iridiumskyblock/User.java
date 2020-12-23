package com.iridium.iridiumskyblock;

import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.managers.UserManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class User {

    public String player;
    public String name;
    public int islandID;
    public Role role;
    public Set<Integer> invites;
    public Island.Warp warp;
    public boolean bypassing;
    public boolean islandChat;
    public boolean spyingIslandsChat;
    public boolean flying;
    public transient boolean teleportingHome;
    public transient boolean tookInterestMessage;
    public Date lastCreate;
    private transient List<Object> holograms;


    public User(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        invites = new HashSet<>();
        this.player = uuid.toString();
        this.name = player.getName();
        this.islandID = 0;
        bypassing = false;
        islandChat = false;
        spyingIslandsChat = false;
        flying = false;
    }

    public Island getIsland() {
        return IslandManager.getIslandViaId(islandID);
    }

    public Role getRole() {
        if (role == null) {
            if (getIsland() != null) {
                if (getIsland().owner.equals(player)) {
                    role = Role.Owner;
                } else {
                    role = Role.Member;
                }
            } else {
                role = Role.Visitor;
            }
        }
        return role;
    }

    public boolean isOnCooldown() {
        return IridiumSkyblock.configuration.createCooldown && this.lastCreate != null && !this.bypassing && new Date().before(this.lastCreate);
    }

    public String getCooldownTimeMessage() {
        long time = (this.lastCreate.getTime() - System.currentTimeMillis()) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(time);
        int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
        int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
        int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);
        return IridiumSkyblock.messages.createCooldown.replace("%days%", day + "").replace("%hours%", hours + "").replace("%minutes%", minute + "").replace("%seconds%", second + "").replace("%prefix%", IridiumSkyblock.configuration.prefix);
    }

    public static User getUser(UUID uuid) {
        return UserManager.getUser(uuid);
    }

    public static User getUser(String uuid) {
        return getUser(UUID.fromString(uuid));
    }

    public static User getUser(OfflinePlayer p) {
        if (p == null) return null;
        return UserManager.getUser(p.getUniqueId());
    }

    public List<Object> getHolograms() {
        if (holograms == null) holograms = new ArrayList<>();
        return holograms;
    }

    public void addHologram(Object object) {
        if (holograms == null) holograms = new ArrayList<>();
        holograms.add(object);
    }

    public void removeHologram(Object object) {
        if (holograms == null) holograms = new ArrayList<>();
        holograms.remove(object);
    }

    public void clearHolograms() {
        if (holograms == null) holograms = new ArrayList<>();
        holograms.clear();
    }

    public void save(boolean async) {
        UserManager.saveUser(this, async);
    }
}
