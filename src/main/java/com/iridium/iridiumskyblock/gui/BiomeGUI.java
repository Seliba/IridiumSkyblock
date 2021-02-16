package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XBiome;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.utils.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BiomeGUI extends GUI implements Listener {

    public int page;
    public World.Environment environment;
    public Map<Integer, XBiome> biomes = new HashMap<>();

    public BiomeGUI(Island island, int page, World.Environment environment) {
        super(island, IridiumSkyblock.getInstance().getInventories().biomeGUISize, IridiumSkyblock.getInstance().getInventories().biomeGUITitle);
        this.page = page;
        this.environment = environment;
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        if (getInventory().getViewers().isEmpty()) return;
        super.addContent();
        int i = 0;
        int slot = 0;
        for (XBiome xBiome : IridiumSkyblock.getInstance().getConfiguration().islandBiomes.keySet()) {
            if (xBiome.getEnvironment() != environment || xBiome.getBiome() == null) continue;
            if (i >= 45 * (page - 1) && slot < 45) {
                Config.BiomeConfig biomeConfig = IridiumSkyblock.getInstance().getConfiguration().islandBiomes.get(xBiome);
                ItemStack itemStack = ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().biome, Arrays.asList(
                        new Placeholder("price", NumberFormatter.format(biomeConfig.price)),
                        new Placeholder("crystals", NumberFormatter.format(biomeConfig.crystals)),
                        new Placeholder("biome", WordUtils.capitalize(xBiome.name().toLowerCase().replace("_", " ")))));
                Material icon = biomeConfig.icon.parseMaterial();
                if (icon != null) itemStack.setType(icon);
                setItem(slot, itemStack);
                biomes.put(slot, xBiome);
                slot++;
            }
            i++;
        }

        setItem(getInventory().getSize() - 3, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().nextPage));
        if (IridiumSkyblock.getInstance().getInventories().backButtons)
            setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
        setItem(getInventory().getSize() - 7, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().previousPage));
    }

    public void sendBiomeChangeMessage(String biome, Player p) {
        for (String member : getIsland().members) {
            Player pl = Bukkit.getPlayer(UUID.fromString(member));
            if (pl != null) {
                pl.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().biomeChanged
                        .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                        .replace("%biome%", biome).replace("%player%", p.getName())));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (getInventory() != null) {
            if (e.getInventory().equals(getInventory())) {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
                if (e.getSlot() == getInventory().getSize() - 3) {
                    BiomeGUI biomeGUI = environment == World.Environment.NORMAL ? getIsland().biomeGUI.getPage(page + 1) : getIsland().netherBiomeGUI.getPage(page + 1);
                    if (biomeGUI != null) {
                        p.openInventory(biomeGUI.getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 7) {
                    BiomeGUI biomeGUI = environment == World.Environment.NORMAL ? getIsland().biomeGUI.getPage(page - 1) : getIsland().netherBiomeGUI.getPage(page - 1);
                    if (biomeGUI != null) {
                        p.openInventory(biomeGUI.getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInstance().getInventories().backButtons) {
                    p.openInventory(getIsland().islandMenuGUI.getInventory());
                }
                if (biomes.containsKey(e.getSlot())) {
                    Config.BiomeConfig biomeConfig = IridiumSkyblock.getInstance().getConfiguration().islandBiomes.get(biomes.get(e.getSlot()));
                    MiscUtils.BuyResponse response = MiscUtils.canBuy(p, IridiumSkyblock.getInstance().getConfiguration().islandBiomes.getOrDefault(biomes.get(e.getSlot()), new Config.BiomeConfig()).price, biomeConfig.crystals);
                    if (response == MiscUtils.BuyResponse.SUCCESS) {
                        switch (environment) {
                            case NORMAL:
                                getIsland().setBiome(biomes.get(e.getSlot()));
                                break;
                            case NETHER:
                                getIsland().setNetherBiome(biomes.get(e.getSlot()));
                        }
                        p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().biomePurchased
                                .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                                .replace("%biome%", WordUtils.capitalize(biomes.get(e.getSlot()).name().toLowerCase().replace("_", " ")))
                                .replace("%crystals%", biomeConfig.crystals + "")
                                .replace("%money", NumberFormatter.format(biomeConfig.price))));
                        sendBiomeChangeMessage(WordUtils.capitalize(biomes.get(e.getSlot()).name().toLowerCase().replace("_", " ")), p);
                    } else {
                        p.sendMessage(StringUtils.color((response == MiscUtils.BuyResponse.NOT_ENOUGH_VAULT ? IridiumSkyblock.getInstance().getMessages().cantBuy : IridiumSkyblock.getInstance().getMessages().notEnoughCrystals).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}