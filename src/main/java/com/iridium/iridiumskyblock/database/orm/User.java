package com.iridium.iridiumskyblock.database.orm;

import com.iridium.iridiumskyblock.Role;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ORM class representing a user in the database
 *
 * @author BomBardyGamer
 * @since 3.0
 */
@Getter
@Setter
@NoArgsConstructor
@DatabaseTable(tableName = "users")
public final class User {

    @DatabaseField(columnName = "id", generatedId = true)
    @Nullable
    private Integer id;

    @DatabaseField(columnName = "uuid", canBeNull = false)
    @NotNull
    private UUID uuid;

    @DatabaseField(columnName = "name", canBeNull = false)
    @NotNull
    private String name;

    @DatabaseField(columnName = "island_id", foreign = true)
    @NotNull
    private Island island;

    @DatabaseField(columnName = "role")
    @Nullable
    private Role role;

    @DatabaseField(columnName = "last_creation_time", canBeNull = false)
    @Nullable
    private LocalDateTime lastCreationTime; // May not fully work yet, still waiting on java.time support for ormlite

    public User(final @NotNull UUID uuid, final @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
