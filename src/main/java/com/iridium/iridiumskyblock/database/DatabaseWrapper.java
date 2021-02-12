package com.iridium.iridiumskyblock.database;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.SQL;
import com.iridium.iridiumskyblock.database.orm.Island;
import com.iridium.iridiumskyblock.database.orm.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper class for initialising database tables
 * and using CRUD operations on them
 *
 * @author BomBardyGamer
 * @since 3.0
 */
public final class DatabaseWrapper {

    private static final SQL SQL_CONFIG = IridiumSkyblock.getInstance().getSql();

    private final Dao<User, Integer> userDao;
    private final Dao<Island, Integer> islandDao;

    public DatabaseWrapper() throws SQLException {
        String databaseURL = getDatabaseURL();

        ConnectionSource connectionSource = new JdbcConnectionSource(
                databaseURL,
                SQL_CONFIG.username,
                SQL_CONFIG.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Island.class);

        this.userDao = DaoManager.createDao(connectionSource, User.class);
        this.islandDao = DaoManager.createDao(connectionSource, Island.class);
    }

    private @NotNull String getDatabaseURL() {
        switch (SQL_CONFIG.driver) {
            case MYSQL:
            case MARIADB:
            case POSTGRESQL:
                return "jdbc:" + SQL_CONFIG.driver + "://" + SQL_CONFIG.host + ":" + SQL_CONFIG.port + "/" + SQL_CONFIG.database;
            case SQLSERVER:
                return "jdbc:sqlserver://" + SQL_CONFIG.host + ":" + SQL_CONFIG.port + ";databaseName=" + SQL_CONFIG.database;
            case H2:
                return "jdbc:h2:file:" + SQL_CONFIG.database;
            case SQLITE:
                return "jdbc:sqlite:" + new File(IridiumSkyblock.getInstance().getDataFolder(), SQL_CONFIG.database + ".db");
        }

        throw new RuntimeException("How did we get here?");
    }

    public CompletableFuture<@Nullable User> getUserByUUID(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return userDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            return null;
        });
    }

    public CompletableFuture<@Nullable User> getUserByName(@NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                userDao.queryBuilder().where().eq("name", name).queryForFirst();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            return null;
        });
    }

    public CompletableFuture<@Nullable Island> getIslandById(int id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return islandDao.queryBuilder().where().eq("id", id).queryForFirst();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            return null;
        });
    }

    public CompletableFuture<@Nullable Island> getIslandByOwner(@NotNull User owner) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return islandDao.queryBuilder().where().eq("owner_id", owner.getId()).queryForFirst();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }

            return null;
        });
    }

    public void saveUser(@NotNull User user) {
        try {
            userDao.createOrUpdate(user);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void saveIsland(@NotNull Island island) {
        try {
            islandDao.createOrUpdate(island);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
