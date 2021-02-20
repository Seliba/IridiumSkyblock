package com.iridium.iridiumskyblock.schematics;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import org.bukkit.Location;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class WorldEdit6 implements WorldEdit {

    public Constructor<?> EditSession;
    public Method flush;

    {
        try {
            EditSession = Class.forName("com.sk89q.worldedit.EditSession").getConstructor(com.sk89q.worldedit.LocalWorld.class, int.class);
            flush = Class.forName("com.sk89q.worldedit.EditSession").getMethod("flushQueue");
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int version() {
        return 6;
    }

    @Override
    public void paste(File file, Location location, Island island) {
        try {
            EditSession editSession = (com.sk89q.worldedit.EditSession) EditSession.newInstance(new BukkitWorld(location.getWorld()), 999999999);
            editSession.enableQueue();

            SchematicFormat schematic = SchematicFormat.getFormat(file);
            CuboidClipboard clipboard = schematic.load(file);


            clipboard.paste(editSession, BukkitUtil.toVector(location.clone().add(clipboard.getWidth() / 2.00, clipboard.getHeight() / 2.00, clipboard.getLength() / 2.00)), true);
            flush.invoke(editSession);
        } catch (Exception e) {
            IridiumSkyblock.getInstance().getLogger().warning("Failed to paste schematic using worldedit");
            IridiumSkyblock.getSchematic().paste(file, location, island);
        }
    }
}
