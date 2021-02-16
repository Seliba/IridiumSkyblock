package com.iridium.iridiumskyblock.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Upgrades;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MVDWPlaceholderAPIManager {

    public MVDWPlaceholderAPIManager() {
    }

    public void register() {
        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_value", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedValue() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });
        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_role", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getRole().toString() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_level", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedLevel() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_rank", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? NumberFormat.getInstance().format(Integer.toString(user.getIsland().getRank())) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_owner", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? User.getUser(user.getIsland().owner).name : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_name", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getName() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_crystals", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedCrystals() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_members", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? String.valueOf(user.getIsland().members.size()) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_members_online", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            if (user.getIsland() == null)
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            int online = 0;
            for (String member : user.getIsland().members) {
                if (Bukkit.getPlayer(User.getUser(member).name) != null) {
                    online++;
                }
            }
            return String.valueOf(online);
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_bank_vault", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedMoney() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_bank_experience", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedExp() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_biome", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().biome.name() : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_midnight_seconds", e -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
            int day = (int) TimeUnit.SECONDS.toDays(time);
            int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400L));
            int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);
            return String.valueOf(second);
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_midnight_minutes", e -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
            int day = (int) TimeUnit.SECONDS.toDays(time);
            int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400L));
            int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
            return String.valueOf(minute);
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_midnight_hours", e -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
            int day = (int) TimeUnit.SECONDS.toDays(time);
            int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400L));
            return String.valueOf(hours);
        });

        for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_level", e -> {
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? NumberFormat.getInstance().format(level) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_dimensions", e -> {
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? Integer.toString(upgrade.getIslandUpgrade(level).size) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_amount", e -> {
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? Integer.toString(upgrade.getIslandUpgrade(level).size) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_count", e -> {
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? Integer.toString(upgrade.getIslandUpgrade(level).size) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
        }

        for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_booster_" + booster.name, e -> {
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                return user.getIsland() != null ? Integer.toString(user.getIsland().getBoosterTime(booster.name)) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
        }

        for (int i = 0; i < 10; i++) { //TODO there is probably a more efficient way to do this?
            int finalI = i;
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_owner_" + (i + 1), e -> {
                Island island = getIsland(finalI);
                return island != null ? phCheckIfStripped(User.getUser(island.owner).name) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_name_" + (i + 1), e -> {
                Island island = getIsland(finalI);
                return island != null ? phCheckIfStripped(island.getName()) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_value_" + (i + 1), e -> {
                Island island = getIsland(finalI);
                return island != null ? phCheckIfStripped(island.getFormattedValue()) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_level_" + (i + 1), e -> {
                Island island = getIsland(finalI);
                return island != null ? phCheckIfStripped(island.getFormattedLevel()) : IridiumSkyblock.getInstance().getConfiguration().placeholderDefaultValue;
            });
        }
    }

    private Island getIsland(int rank) {
        List<Integer> islandID = IridiumSkyblockAPI.getInstance().getIslands(IslandDataManager.IslandSortType.VALUE, rank - 1, rank, false);
        if (islandID.isEmpty()) return null;
        return IslandManager.getIslandViaId(islandID.get(0));
    }

    public String phCheckIfStripped(String ph) {
        if (IridiumSkyblock.getInstance().getConfiguration().stripTopIslandPlaceholderColors) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ph)).replace("\"", "\\\"");
        }
        return ph.replace("\"", "\\\"");
    }
}