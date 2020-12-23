package com.iridium.iridiumskyblock.placeholders;


import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClipPlaceholderAPIManager extends PlaceholderExpansion {

    // Identifier for this expansion
    @Override
    public String getIdentifier() {
        return "iridiumskyblock";
    }

    @Override
    public String getAuthor() {
        return "Peaches_MLG";
    }

    // Since we are registering this expansion from the dependency, this can be null
    @Override
    public String getPlugin() {
        return null;
    }

    // Return the plugin version since this expansion is bundled with the dependency
    @Override
    public String getVersion() {
        return IridiumSkyblock.instance.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null || placeholder == null) {
            return "";
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(time);
        int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
        int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
        int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);

        User user = User.getUser(player);

        switch (placeholder) {
            case "island_value":
                return user.getIsland() != null ? user.getIsland().getFormattedValue() : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_level":
                return user.getIsland() != null ? Utils.NumberFormatter.format(Math.floor(user.getIsland().value / IridiumSkyblock.configuration.valuePerLevel)) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_rank":
                return user.getIsland() != null ? NumberFormat.getInstance().format(Utils.getIslandRank(user.getIsland())) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_owner":
                return user.getIsland() != null ? User.getUser(user.getIsland().owner).name : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_name":
                return user.getIsland() != null ? user.getIsland().getName() : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_crystals":
                return user.getIsland() != null ? user.getIsland().getFormattedCrystals() : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_members":
                return user.getIsland() != null ? String.valueOf(user.getIsland().members.size()) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_members_online":
                if (user.getIsland() == null) return IridiumSkyblock.configuration.placeholderDefaultValue;
                int online = 0;
                for (String member : user.getIsland().members) {
                    if (Bukkit.getPlayer(User.getUser(member).name) != null) {
                        online++;
                    }
                }
                return String.valueOf(online);
            case "island_upgrade_member_level":
                return user.getIsland() != null ? NumberFormat.getInstance().format(user.getIsland().memberLevel) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_upgrade_member_amount":
                return user.getIsland() != null ? String.valueOf(IridiumSkyblock.upgrades.memberUpgrade.upgrades.get(user.getIsland().memberLevel).size) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_upgrade_size_level":
                return user.getIsland() != null ? String.valueOf(user.getIsland().sizeLevel) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_upgrade_size_dimensions":
                return user.getIsland() != null ? String.valueOf(IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(user.getIsland().sizeLevel).size) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_upgrade_ore_level":
                return user.getIsland() != null ? String.valueOf(user.getIsland().oreLevel) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_upgrade_warp_level":
                return user.getIsland() != null ? String.valueOf(user.getIsland().warpLevel) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_booster_spawner":
                return user.getIsland() != null ? String.valueOf(user.getIsland().spawnerBooster) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_booster_exp":
                return user.getIsland() != null ? String.valueOf(user.getIsland().expBooster) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_booster_farming":
                return user.getIsland() != null ? String.valueOf(user.getIsland().farmingBooster) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_booster_flight":
                return user.getIsland() != null ? String.valueOf(user.getIsland().flightBooster) : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_bank_vault":
                return user.getIsland() != null ? user.getIsland().getFormattedMoney() : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_bank_experience":
                return user.getIsland() != null ? user.getIsland().getFormattedExp() : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "island_biome":
                return user.getIsland() != null ? user.getIsland().biome.name() : IridiumSkyblock.configuration.placeholderDefaultValue;
            case "midnight_seconds":
                return String.valueOf(second);
            case "midnight_minutes":
                return String.valueOf(minute);
            case "midnight_hours":
                return String.valueOf(hours);
        }
        if (placeholder.startsWith("island_top_name_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_name_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? phCheckIfStripped(Utils.getTopIslands().get(integer - 1).getName()) : IridiumSkyblock.configuration.placeholderDefaultValue;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (placeholder.startsWith("island_top_owner_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_owner_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? User.getUser(Utils.getTopIslands().get(integer - 1).owner).name : IridiumSkyblock.configuration.placeholderDefaultValue;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (placeholder.startsWith("island_top_value_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_value_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? Utils.NumberFormatter.format(Utils.getTopIslands().get(integer - 1).value) : IridiumSkyblock.configuration.placeholderDefaultValue;
            } catch (NumberFormatException ignored) {

            }
        }
        if (placeholder.startsWith("island_top_level_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_level_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? Utils.NumberFormatter.format(Math.floor(Utils.getTopIslands().get(integer - 1).value / IridiumSkyblock.configuration.valuePerLevel)) + "" : IridiumSkyblock.configuration.placeholderDefaultValue;
            } catch (NumberFormatException ignored) {

            }
        }
        return null;
    }

    public String phCheckIfStripped(String ph) {
        if (IridiumSkyblock.configuration.stripTopIslandPlaceholderColors) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ph)).replace("\"","\\\"");
        }
        return ph.replace("\"","\\\"");
    }

}