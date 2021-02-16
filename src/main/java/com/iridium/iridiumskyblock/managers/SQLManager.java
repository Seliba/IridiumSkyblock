package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.SQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLManager {
    private HikariDataSource hikariDataSource;

    public SQLManager() {
        setupConnection();
    }

    private void setupConnection() {
        if (hikariDataSource != null) return;

        HikariConfig hikariConfig = new HikariConfig();

        final SQL sql = IridiumSkyblock.getInstance().getSql();
        hikariConfig.setMaximumPoolSize(sql.poolSize);
        hikariConfig.setConnectionTimeout(sql.connectionTimeout);
        hikariConfig.setLeakDetectionThreshold(sql.leakDetectionThreshold);
        hikariConfig.setConnectionTestQuery("SELECT 1;");
        if (sql.username.isEmpty()) {
            //SQL Lite
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            hikariConfig.setJdbcUrl("jdbc:sqlite:" + new File(IridiumSkyblock.getInstance().getDataFolder(), sql.database + ".db"));
        } else {
            //Use SQL
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            }

            hikariConfig.addDataSourceProperty("cachePrepStmts", true);
            hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
            hikariConfig.setUsername(sql.username);
            hikariConfig.setPassword(sql.password);
            hikariConfig.setJdbcUrl("jdbc:mysql://" + sql.host + ":" + sql.port + "/" + sql.database);
        }

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void deleteAll() {

        try (Connection connection = getConnection()) {
            connection.createStatement().executeUpdate("DELETE FROM users;");
            connection.createStatement().executeUpdate("DELETE FROM claims;");
            connection.createStatement().executeUpdate("DELETE FROM islands;");
            connection.createStatement().executeUpdate("DELETE FROM islandmanager;");
            connection.createStatement().executeUpdate("DELETE FROM islanddata;");
            connection.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTables() {
        try (Connection connection = getConnection()) {

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS users "
                    + "(UUID VARCHAR(255), json TEXT, PRIMARY KEY (UUID));");


            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS claims "
                    + "(x INTEGER, z INTEGER, island INTEGER);");

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS islands "
                    + "(id INTEGER, json TEXT, PRIMARY KEY (id));");

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS islandmanager "
                    + "(nextID INTEGER, length INTEGER, current INTEGER, x DOUBLE, y DOUBLE, direction VARCHAR(10));");

            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS islanddata "
                    + "(islandID INTEGER, value DOUBLE, votes INTEGER, private BOOLEAN);");

            connection.commit();

        } catch (SQLException ex) {
            IridiumSkyblock.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on Creating Tables", ex);
        }
    }

    public Connection getConnection() {
        try {
            Connection connection = hikariDataSource.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
