package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import java.util.Collections;
import java.util.List;

public class Schematics {
    public List<FakeSchematic> schematics = Collections.singletonList(new FakeSchematic("island.schematic", "nether.schematic", -0.5, 96.00, -2.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"", XMaterial.GRASS_BLOCK, XBiome.PLAINS, "&b&lDefault Island", Collections.singletonList("&7The default island"), 0));

    public static class FakeSchematic {
        public String name;
        public double x;
        public double y;
        public double z;
        public double xOffset;
        public double yOffset;
        public double zOffset;
        public double xNetherOffset;
        public double yNetherOffset;
        public double zNetherOffset;
        public String netherisland;
        public String permission;
        public XMaterial item;
        public XBiome biome;
        public String displayname;
        public List<String> lore;
        public Integer slot;

        public FakeSchematic(String name, String netherisland, double x, double y, double z, double xOffset, double yOffset, double zOffset, double xNetherOffset, double yNetherOffset, double zNetherOffset, String permission, XMaterial item, XBiome biome, String displayname, List<String> lore, int slot) {
            this.name = name;
            this.netherisland = netherisland;
            this.permission = permission;
            this.item = item;
            this.displayname = displayname;
            this.lore = lore;
            this.biome = biome;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.xNetherOffset = xNetherOffset;
            this.yNetherOffset = yNetherOffset;
            this.zNetherOffset = zNetherOffset;
            this.slot = slot;
        }
    }
}
