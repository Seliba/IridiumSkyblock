package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Schematics {
    public List<FakeSchematic> schematicList = Arrays.asList(
            new FakeSchematic(0, 0, 0,
                    new SchematicData("desert.schem", XBiome.DESERT, 0.0, 0.0, 0.0),
                    new SchematicData("nether.schematic", XBiome.NETHER_WASTES, 0.0, 0.0, 0.0), "",
                    new Inventories.Item(XMaterial.PLAYER_HEAD, 11, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGY0OTNkZDgwNjUzM2Q5ZDIwZTg0OTUzOTU0MzY1ZjRkMzY5NzA5Y2ViYzlkZGVmMDIyZDFmZDQwZDg2YTY4ZiJ9fX0=", 1, "&b&lDesert Island", Collections.singletonList("&7A starter desert island."))),
            new FakeSchematic(0, 0, 0,
                    new SchematicData("jungle.schem", XBiome.JUNGLE, 0.0, 0.0, 0.0),
                    new SchematicData("nether.schematic", XBiome.NETHER_WASTES, 0.0, 0.0, 0.0), "",
                    new Inventories.Item(XMaterial.PLAYER_HEAD, 13, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgzYWRmNDU2MGRlNDc0MTQwNDA5M2FjNjFjMzNmYjU1NmIzZDllZTUxNDBmNjIwMzYyNTg5ZmRkZWRlZmEyZCJ9fX0=", 1, "&b&lJungle Island", Collections.singletonList("&7A starter jungle island."))),
            new FakeSchematic(0, 0, 0,
                    new SchematicData("mushroom.schem", XBiome.MUSHROOM_FIELDS, 0.0, 0.0, 0.0),
                    new SchematicData("nether.schematic", XBiome.NETHER_WASTES, 0.0, 0.0, 0.0), "",
                    new Inventories.Item(XMaterial.PLAYER_HEAD, 15, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE0NWQxYjQxN2NiZGRjMjE3NjdiMDYwNDRlODk5YjI2NmJmNzhhNjZlMjE4NzZiZTNjMDUxNWFiNTVkNzEifX19", 1, "&b&lMushroom Island", Collections.singletonList("&7A starter mushroom island.")))
    );

    public static class FakeSchematic {
        public double x;
        public double y;
        public double z;
        public SchematicData overworldData;
        public SchematicData netherData;
        public String permission;
        public Inventories.Item item;

        public FakeSchematic() {
        }

        public FakeSchematic(double x, double y, double z, SchematicData overworldData, SchematicData netherData, String permission, Inventories.Item item) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.overworldData = overworldData;
            this.netherData = netherData;
            this.permission = permission;
            this.item = item;
        }
    }

    public static class SchematicData {
        public String schematic;
        public XBiome biome;
        public double xOffset;
        public double yOffset;
        public double zOffset;

        public SchematicData() {
        }

        public SchematicData(String schematic, XBiome biome, double xOffset, double yOffset, double zOffset) {
            this.schematic = schematic;
            this.biome = biome;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
        }
    }
}
