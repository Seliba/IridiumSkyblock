package com.iridium.iridiumskyblock.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Upgrades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {

    public static String color(String string) {
        return IridiumColorAPI.process(string);
    }

    public static List<String> color(List<String> strings) {
        return strings.stream().map(StringUtils::color).collect(Collectors.toList());
    }


    public static List<Placeholder> getIslandPlaceholders(Island island) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(
                new Placeholder("experience", island.getFormattedExp()),
                new Placeholder("crystals", island.getFormattedCrystals()),
                new Placeholder("money", island.getFormattedMoney()),
                new Placeholder("value", island.getFormattedValue())
        ));
        for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
            int level = island.getUpgradeLevel(upgrade.name);
            placeholders.add(new Placeholder(upgrade.name + "vaultcost", upgrade.getIslandUpgrade(level + 1) != null ? Integer.toString(upgrade.getIslandUpgrade(level + 1).vaultCost) : IridiumSkyblock.getInstance().getMessages().maxlevelreached));
            placeholders.add(new Placeholder(upgrade.name + "crystalscost", upgrade.getIslandUpgrade(level + 1) != null ? Integer.toString(upgrade.getIslandUpgrade(level + 1).crystalsCost) : IridiumSkyblock.getInstance().getMessages().maxlevelreached));
            placeholders.add(new Placeholder(upgrade.name + "level", Integer.toString(level)));
            if (upgrade.getIslandUpgrade(level).size != null) {
                placeholders.add(new Placeholder(upgrade.name + "blocks", Integer.toString(upgrade.getIslandUpgrade(level).size)));
                placeholders.add(new Placeholder(upgrade.name + "count", Integer.toString(upgrade.getIslandUpgrade(level).size)));
            }
        }
        for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
            placeholders.add(new Placeholder(booster.name + "booster", Integer.toString(island.getBoosterTime(booster.name))));
            placeholders.add(new Placeholder(booster.name + "booster_seconds", Integer.toString(island.getBoosterTime(booster.name) % 60)));
            placeholders.add(new Placeholder(booster.name + "booster_minutes", Integer.toString((int) Math.floor(island.getBoosterTime(booster.name) / 60.00))));
            placeholders.add(new Placeholder(booster.name + "booster_crystalcost", Integer.toString(booster.crystalsCost)));
            placeholders.add(new Placeholder(booster.name + "booster_vaultcost", Integer.toString(booster.vaultCost)));
        }
        return placeholders;
    }

    public static String processIslandPlaceholders(String line, Island island) {
        return processMultiplePlaceholders(line, getIslandPlaceholders(island));
    }

    public static List<String> processIslandPlaceholders(List<String> lines, Island island) {
        return lines.stream().map(s -> processIslandPlaceholders(s, island)).collect(Collectors.toList());
    }

    public static List<String> processMultiplePlaceholders(List<String> lines, List<Placeholder> placeholders) {
        return lines.stream().map(s -> processMultiplePlaceholders(s, placeholders)).collect(Collectors.toList());
    }

    public static String processMultiplePlaceholders(String line, List<Placeholder> placeholders) {
        for (Placeholder placeholder : placeholders) {
            line = placeholder.process(line);
        }
        return color(line);
    }
}
