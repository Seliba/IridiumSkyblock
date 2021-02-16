package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.bank.BankItem;
import com.iridium.iridiumskyblock.commands.Command;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Upgrades;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class IridiumSkyblockAPI {

    private static IridiumSkyblockAPI instance;
    private final IridiumSkyblock iridiumSkyblock;

    public IridiumSkyblockAPI(IridiumSkyblock iridiumSkyblock) {
        instance = this;
        this.iridiumSkyblock = iridiumSkyblock;
    }

    public static IridiumSkyblockAPI getInstance() {
        return instance;
    }

    /**
     * Gets an island from location
     *
     * @param location The location for the island
     * @since 3.0.0
     */
    public @Nullable Island getIslandViaLocation(@NotNull Location location) {
        return IslandManager.getIslandViaLocation(location);
    }

    /**
     * Gets an island from an islandID
     *
     * @param id The id of the island
     * @since 3.0.0
     */
    public @Nullable Island getIslandViaID(int id) {
        return IslandManager.getIslandViaId(id);
    }

    /**
     * Gets a sorted list  of islands
     *
     * @param sortType      The enum value of how the list will be sorted
     * @param fromIndex     The inclusive value of where the list will start e.g. 0
     * @param toIndex       The exclusive value of where the list will end
     * @param ignorePrivate if private  islands should be ignored from the list
     * @since 3.0.0
     */
    public @NotNull List<Integer> getIslands(@NotNull IslandDataManager.IslandSortType sortType, int fromIndex, int toIndex, boolean ignorePrivate) {
        return IslandDataManager.getIslands(sortType, fromIndex, toIndex, ignorePrivate);
    }

    /**
     * Gets a User from a UUID
     *
     * @param uuid The uuid of the player
     * @since 3.0.0
     */
    @NotNull
    public User getUser(@NotNull UUID uuid) {
        return User.getUser(uuid);
    }

    /**
     * Register a Booster
     *
     * @param booster The booster we want to register
     * @since 3.0.0
     */
    public void registerBooster(@NotNull Boosters.Booster booster) {
        iridiumSkyblock.registerBooster(booster);
    }

    /**
     * Register an Upgrade
     *
     * @param upgrade The upgrade we want to register
     * @since 3.0.0
     */
    public void registerUpgrade(@NotNull Upgrades.Upgrade upgrade) {
        iridiumSkyblock.registerUpgrade(upgrade);
    }

    /**
     * Register a BankItem
     *
     * @param bankItem The bankItem we want to register
     * @since 3.0.0
     */
    public void registerBankItem(@NotNull BankItem bankItem) {
        iridiumSkyblock.registerBankItem(bankItem);
    }

    /**
     * Registers a Command
     *
     * @param command The command we want to register
     * @since 3.0.0
     */
    public void registerCommand(@NotNull Command command) {
        iridiumSkyblock.getCommandManager().registerCommand(command);
    }
}
