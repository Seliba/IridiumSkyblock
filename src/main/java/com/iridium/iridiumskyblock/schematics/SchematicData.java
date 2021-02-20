package com.iridium.iridiumskyblock.schematics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.StringTag;
import org.jnbt.Tag;

public class SchematicData {

    public enum SchematicVersion {
        v1_13, v_1_8
    }

    public final short width;
    public final short length;
    public final short height;
    public final File file;
    public final SchematicVersion schematicVersion;
    public List<Tag> tileEntities;
    public byte[] blocks;
    public byte[] data;
    public byte[] blockdata;
    public Map<String, Tag> palette;
    public Integer version;

    public SchematicData(File file, short width, short length, short height, List<Tag> tileEntities, byte[] blocks, byte[] data, List<Tag> entities) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.length = length;
        this.height = height;
        this.tileEntities = tileEntities;
        this.schematicVersion = SchematicVersion.v_1_8;
        this.file = file;
    }

    public SchematicData(File file, short width, short length, short height, byte[] blockdata, Map<String, Tag> palette, int version) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.palette = palette;
        this.blockdata = blockdata;
        this.schematicVersion = SchematicVersion.v1_13;
        this.version = version;
        this.file = file;
    }

    public SchematicData(File file, short width, short length, short height, List<Tag> tileEntities, byte[] blockdata, Map<String, Tag> palette, int version) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.palette = palette;
        this.blockdata = blockdata;
        this.schematicVersion = SchematicVersion.v1_13;
        this.tileEntities = tileEntities;
        this.version = version;
        this.file = file;
    }

    public static SchematicData loadSchematic(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        NBTInputStream nbtStream = new NBTInputStream(stream);

        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
        stream.close();
        nbtStream.close();
        Map<String, Tag> schematic = schematicTag.getValue();

        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
        if (!schematic.containsKey("Blocks")) {
            // 1.13 Schematic
            int version = getChildTag(schematic, "Version", IntTag.class).getValue();
            Map<String, Tag> palette = getChildTag(schematic, "Palette", CompoundTag.class).getValue();
            byte[] blockdata = getChildTag(schematic, "BlockData", ByteArrayTag.class).getValue();
            if (version == 1) {
                List<Tag> TileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
                return new SchematicData(file, width, length, height, TileEntities, blockdata, palette, version);
            } else if (version == 2) {
                List<Tag> BlockEntities = getChildTag(schematic, "BlockEntities", ListTag.class).getValue();
                return new SchematicData(file, width, length, height, BlockEntities, blockdata, palette, version);
            } else {
                return new SchematicData(file, width, length, height, blockdata, palette, version);
            }

        } else {
            List<Tag> TileEntities = getChildTag(schematic, "TileEntities", ListTag.class).getValue();
            String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
            if (!materials.equals("Alpha")) {
                throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
            }

            byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
            byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
            List<Tag> entities = getChildTag(schematic, "Entities", ListTag.class).getValue();
            return new SchematicData(file, width, length, height, TileEntities, blocks, blockData, entities);
        }
    }

    public static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws
            IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}
