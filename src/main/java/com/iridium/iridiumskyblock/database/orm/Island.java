package com.iridium.iridiumskyblock.database.orm;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

// TODO: Finish this table, persist permissions properly
// also make sure this actually works as intended

/**
 * ORM class representing an island in the database
 *
 * @author BomBardyGamer
 * @since 3.0
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "islands")
public final class Island {

    @DatabaseField(columnName = "id", generatedId = true)
    @Nullable
    private Integer id;

    @DatabaseField(columnName = "name", canBeNull = false)
    @NotNull
    private String name;

    @DatabaseField(columnName = "owner_id", foreign = true, unique = true)
    @NotNull
    private User owner;

    @ForeignCollectionField
    @Setter(AccessLevel.PRIVATE)
    private ForeignCollection<User> members;

    @DatabaseField(columnName = "biome", canBeNull = false)
    @NotNull
    private Biome biome;

    @DatabaseField(columnName = "nether_biome", canBeNull = false)
    @NotNull
    private Biome netherBiome;

    @DatabaseField(columnName = "schematic", canBeNull = false)
    @NotNull
    private String schematic;

    @DatabaseField(columnName = "nether_schematic", canBeNull = false)
    @NotNull
    private String netherSchematic;

    @DatabaseField(columnName = "last_regen")
    @Nullable
    private LocalDateTime lastRegenTime;
}
