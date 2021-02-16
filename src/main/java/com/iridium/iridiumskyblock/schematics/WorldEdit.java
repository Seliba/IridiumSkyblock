package com.iridium.iridiumskyblock.schematics;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.Location;

import java.io.File;

public interface WorldEdit {
    int version();

    void paste(File file, Location location, Island island);
}
