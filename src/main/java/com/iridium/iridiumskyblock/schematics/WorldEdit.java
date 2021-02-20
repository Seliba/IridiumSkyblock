package com.iridium.iridiumskyblock.schematics;

import com.iridium.iridiumskyblock.Island;
import java.io.File;
import org.bukkit.Location;

public interface WorldEdit {
    int version();

    void paste(File file, Location location, Island island);
}
