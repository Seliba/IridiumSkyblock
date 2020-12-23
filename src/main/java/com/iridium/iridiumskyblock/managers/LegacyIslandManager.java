package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;

public class LegacyIslandManager {
    //Used solely for converting old .json to SQL

    private Map<Integer, Island> islands;
    private Map<List<Integer>, Set<Integer>> islandCache;
    private Map<String, User> users;

    int length;
    int current;

    public Direction direction;
    public Location nextLocation;

    public int nextID;


    public void moveToSQL() {
        IridiumSkyblock.sqlManager.deleteAll();
        IridiumSkyblock.instance.getLogger().info("Moving to SQL");
        Connection connection = IridiumSkyblock.sqlManager.getConnection();
        if (users != null) {
            for (String uuid : users.keySet()) {
                try {
                    User user = users.get(uuid);
                    IridiumSkyblock.instance.getLogger().info("Moving User " + uuid + " To SQL");
                    UserManager.cache.put(UUID.fromString(uuid), user);
                    PreparedStatement insert = connection.prepareStatement("INSERT INTO users (UUID,json) VALUES (?,?);");
                    insert.setString(1, uuid);
                    insert.setString(2, IridiumSkyblock.persist.gson.toJson(user));
                    insert.executeUpdate();
                    insert.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            users = null;
        }
        if (islandCache != null) {
            for (List<Integer> coords : islandCache.keySet()) {
                for (int id : islandCache.get(coords)) {
                    IridiumSkyblock.instance.getLogger().info("Moving claim to SQL");
                    ClaimManager.addClaim(coords.get(0), coords.get(1), id);
                }
            }
            islandCache = null;
        }
        if (islands != null) {
            for (Island island : islands.values()) {
                try {
                    IridiumSkyblock.instance.getLogger().info("Moving Island " + island.id + " To SQL");
                    PreparedStatement insert = connection.prepareStatement("INSERT INTO islands (id,json) VALUES (?,?);");
                    insert.setInt(1, island.id);
                    insert.setString(2, IridiumSkyblock.persist.gson.toJson(island));
                    insert.executeUpdate();
                    insert.close();
                    IslandDataManager.save(island, false);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                IslandDataManager.save(island, false);
            }
        }
        IslandManager.length = length;
        IslandManager.current = current;
        IslandManager.direction = direction;
        IslandManager.nextID = nextID;
        IslandManager.nextLocation = nextLocation;
        try {
            PreparedStatement insert = connection.prepareStatement("INSERT INTO islandmanager (nextID,length,current,direction,x,y) VALUES (?,?,?,?,?,?);");
            insert.setInt(1, IslandManager.nextID);
            insert.setInt(2, IslandManager.length);
            insert.setInt(3, IslandManager.current);
            insert.setString(4, IslandManager.direction.name());
            insert.setDouble(5, IslandManager.nextLocation.getX());
            insert.setDouble(6, IslandManager.nextLocation.getZ());
            insert.executeUpdate();
            insert.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
