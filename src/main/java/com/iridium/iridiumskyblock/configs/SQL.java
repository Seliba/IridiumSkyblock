package com.iridium.iridiumskyblock.configs;

// TODO: Add proper connection pooling settings (max pool size, min idle, max lifetime, connection timeout)
public class SQL {

    public Driver driver = Driver.SQLITE;
    public String host = "localhost";
    public String database = "IridiumSkyblock";
    public String username = "";
    public String password = "";
    public int port = 3306;
    public int poolSize = 25;

    public enum Driver {

        MYSQL,
        MARIADB,
        SQLSERVER,
        POSTGRESQL,
        H2,
        SQLITE
    }

    public long connectionTimeout = 30000;
    public long leakDetectionThreshold = 60000;
}
