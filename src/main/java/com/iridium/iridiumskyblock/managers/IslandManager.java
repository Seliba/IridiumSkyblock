package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.SkyblockGenerator;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Schematics;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandManager {

    public static Map<Integer, Island> cache = new HashMap<>();

    public static transient Integer id = 0;

    public static int length;
    public static int current;

    public static Direction direction;
    public static Location nextLocation;

    public static int nextID;

    public static World getWorld() {
        return Bukkit.getWorld(IridiumSkyblock.configuration.worldName);
    }

    public static World getNetherWorld() {
        return Bukkit.getWorld(IridiumSkyblock.configuration.netherWorldName);
    }

    public static void createIsland(Player player) {
        User user = User.getUser(player);
        if (user.isOnCooldown()) {
            //The user cannot create an island
            player.sendMessage(Utils.color(user.getCooldownTimeMessage()));
            return;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, IridiumSkyblock.configuration.regenCooldown);
        user.lastCreate = c.getTime();

        Location pos1 = nextLocation.clone().subtract((IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(1).size / 2.00), 0, (IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(1).size / 2.00));
        Location pos2 = nextLocation.clone().add((IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(1).size / 2.00), 0, (IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(1).size / 2.00));
        Location center = nextLocation.clone().add(0, 100, 0);
        Location home = nextLocation.clone();
        Island island = new Island(player, pos1, pos2, center, home, nextID);
        Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.instance, () -> {
            try {
                Connection connection = IridiumSkyblock.sqlManager.getConnection();
                PreparedStatement insert = connection.prepareStatement("INSERT INTO islands (id,json) VALUES (?,?);");
                insert.setInt(1, nextID);
                insert.setString(2, IridiumSkyblock.persist.gson.toJson(island));
                insert.executeUpdate();
                insert.close();
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        cache.put(nextID, island);

        user.islandID = nextID;
        user.role = Role.Owner;

        if (IridiumSkyblock.schematics.schematics.size() == 1) {
            for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
                island.schematic = schematic.name;
                island.netherschematic = schematic.netherisland;
                island.home = island.home.add(schematic.x, schematic.y, schematic.z);
            }
            island.pasteSchematic(player, false);
        } else {
            player.openInventory(island.schematicSelectGUI.getInventory());
        }

        switch (direction) {
            case NORTH:
                nextLocation.add(IridiumSkyblock.configuration.distance, 0, 0);
                break;
            case EAST:
                nextLocation.add(0, 0, IridiumSkyblock.configuration.distance);
                break;
            case SOUTH:
                nextLocation.subtract(IridiumSkyblock.configuration.distance, 0, 0);
                break;
            case WEST:
                nextLocation.subtract(0, 0, IridiumSkyblock.configuration.distance);
                break;
        }

        current++;

        if (current == length) {
            current = 0;
            direction = direction.next();
            if (direction == Direction.SOUTH || direction == Direction.NORTH) {
                length++;
            }
        }

        nextID++;

        IridiumSkyblock.instance.saveData(true);
    }

    public static int purgeIslands(int days, CommandSender sender) {
        List<Integer> ids = getLoadedIslands().stream().filter(island -> oldIsland(days, island)).map(is -> is.id).collect(Collectors.toList());
        final ListIterator<Integer> islandIds = ids.listIterator();
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(IridiumSkyblock.instance, new Runnable() {
            int amount = 0;

            @Override
            public void run() {
                if (islandIds.hasNext()) {
                    int i = islandIds.next();
                    Island island = getIslandViaId(i);
                    island.delete();
                    amount++;
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.messages.purgingFinished.replace("%amount%", String.valueOf(amount)).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                    Bukkit.getScheduler().cancelTask(id);
                    id = 0;
                }
            }
        }, 0, 20 * 5);
        return ids.size();
    }

    private static boolean oldIsland(int days, Island island) {
        LocalDateTime now = LocalDateTime.now();
        for (OfflinePlayer player : island.members.stream().map(s -> Bukkit.getOfflinePlayer(UUID.fromString(s))).collect(Collectors.toList())) {
            if (player == null) continue;
            LocalDateTime lastLogin = LocalDateTime.ofInstant(Instant.ofEpochMilli(player.getLastPlayed()), TimeZone.getDefault().toZoneId());
            Duration duration = Duration.between(lastLogin, now);
            if (duration.toDays() < days) {
                return false;
            }
        }
        return true;
    }

    public static void makeWorlds() {
        makeWorld(Environment.NORMAL, IridiumSkyblock.configuration.worldName);
        if (IridiumSkyblock.configuration.netherIslands)
            makeWorld(Environment.NETHER, IridiumSkyblock.configuration.netherWorldName);
    }

    private static void makeWorld(Environment env, String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        wc.generator(new SkyblockGenerator());
        wc.environment(env);
        wc.createWorld();
    }

    public static List<Island> getLoadedIslands() {
        return cache.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Island getIslandViaLocation(Location location) {
        if (location == null) return null;
        if (!isIslandWorld(location)) return null;

        final Chunk chunk = location.getChunk();

        final double x = location.getX();
        final double z = location.getZ();
        final Set<Integer> islandIds = ClaimManager.getIslands(chunk.getX(), chunk.getZ());

        for (int id : islandIds) {
            final Island island = getIslandViaId(id);
            if (island == null) continue;
            if (island.isInIsland(x, z)) return island;
        }

        for (Island island : getLoadedIslands()) {
            if (!island.isInIsland(x, z)) continue;
            ClaimManager.addClaim(chunk.getX(), chunk.getZ(), island.id);
            return island;
        }

        return null;
    }

    public static Island getIslandViaId(int id) {

        if (cache.containsKey(id)) return cache.get(id);
        try {
            Connection connection = IridiumSkyblock.sqlManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE id =?;");
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                //There is a value
                Island island = IridiumSkyblock.persist.gson.fromJson(resultSet.getString("json"), Island.class);
                cache.put(id, island);
                connection.close();

                island.init();
                if (island.getName().length() > IridiumSkyblock.configuration.maxIslandName) {
                    island.name = island.getName().substring(0, IridiumSkyblock.configuration.maxIslandName);
                }
                if (island.getName().length() < IridiumSkyblock.configuration.minIslandName) {
                    OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(island.owner));
                    island.name = owner.getName();
                }

                return island;
            }
            connection.close();
            cache.put(id, null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static boolean isIslandWorld(Location location) {
        if (location == null) return false;
        return isIslandWorld(location.getWorld());
    }

    public static boolean isIslandWorld(World world) {
        if (world == null) return false;
        final String name = world.getName();
        return isIslandWorld(name);
    }

    public static boolean isIslandWorld(String name) {
        final Config config = IridiumSkyblock.configuration;
        return (name.equals(config.worldName) || name.equals(config.netherWorldName));
    }

    public static void save(Island island, boolean async) {
        if (async)
            Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.instance, () -> save(island, false));
        try {
            Connection connection = IridiumSkyblock.sqlManager.getConnection();
            PreparedStatement insert = connection.prepareStatement("UPDATE islands SET json = ? WHERE id = ?;");
            insert.setString(1, IridiumSkyblock.persist.gson.toJson(island));
            insert.setInt(2, island.id);
            insert.executeUpdate();
            insert.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void removeIsland(Island island) {
        final int id = island.id;
        cache.remove(id);
        Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.instance, () -> {
            try {
                Connection connection = IridiumSkyblock.sqlManager.getConnection();
                PreparedStatement insert = connection.prepareStatement("DELETE FROM islands WHERE id=?;");
                insert.setInt(1, id);
                insert.executeUpdate();
                insert.close();
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        ClaimManager.removeClaims(island.id);
    }
}
