package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    public static HashMap<UUID, User> cache = new HashMap<>();

    public static User getUser(String uuid) {
        return getUser(UUID.fromString(uuid));
    }


    //Gets a user from UUID
    public static User getUser(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        //TODO: Never make a DB query on the main thread...
        try (Connection connection = IridiumSkyblock.getInstance().getSqlManager().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE UUID =?;")) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    //There is a value
                    User user = IridiumSkyblock.getInstance().getPersist().load(User.class, resultSet.getString("json"));
                    cache.put(uuid, user);

                    return user;
                } else {
                    User user = new User(uuid);
                    cache.put(uuid, user);
                    saveUser(user, connection);
                    connection.commit();
                    return user;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void saveUser(User user, Connection connection) {

        String json = IridiumSkyblock.getInstance().getPersist().toString(user);
        try (PreparedStatement insert = connection.prepareStatement("REPLACE INTO users (UUID,json) VALUES (?,?);")) {
            insert.setString(1, user.player);
            insert.setString(2, json);
            insert.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}