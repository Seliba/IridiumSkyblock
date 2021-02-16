package com.iridium.iridiumskyblock.utils;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.configs.Upgrades;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MiscUtils {

    public static XMaterialItemId xMaterialItemId;

    static {
        {
            InputStream inputStream = IridiumSkyblock.getInstance().getResource("itemdata.json");
            Scanner sc = new Scanner(inputStream);
            //Reading line by line from scanner to StringBuffer
            StringBuffer content = new StringBuffer();
            while (sc.hasNext()) {
                content.append(sc.nextLine());
            }
            xMaterialItemId = IridiumSkyblock.getInstance().getPersist().load(XMaterialItemId.class, content.toString());
        }
    }

    public static boolean isBlockValuable(Block b) {
        for (Upgrades.IslandUpgrade islandUpgrade : IridiumSkyblock.getInstance().getUpgrades().islandBlockLimitUpgrade.upgrades.values()) {
            if (((Upgrades.IslandBlockLimitUpgrade) islandUpgrade).limitedBlocks.containsKey(XMaterial.matchXMaterial(b.getType())))
                return true;
        }
        return IridiumSkyblock.getInstance().getBlockValues().blockvalue.containsKey(XMaterial.matchXMaterial(b.getType())) || b.getState() instanceof CreatureSpawner;
    }

    public static boolean isBlockValuable(XMaterial material) {
        for (Upgrades.IslandUpgrade islandUpgrade : IridiumSkyblock.getInstance().getUpgrades().islandBlockLimitUpgrade.upgrades.values()) {
            if (((Upgrades.IslandBlockLimitUpgrade) islandUpgrade).limitedBlocks.containsKey(material)) return true;
        }
        return IridiumSkyblock.getInstance().getBlockValues().blockvalue.containsKey(material);
    }

    public static boolean isSafe(Location loc, Island island) {
        if (loc == null) return false;
        if (loc.getY() < 1) return false;
        if (!island.isInIsland(loc)) return false;
        if (!loc.getBlock().getType().name().endsWith("AIR")) return false;
        if (loc.clone().add(0, -1, 0).getBlock().getType().name().endsWith("AIR"))
            return false;
        return !loc.clone().add(0, -1, 0).getBlock().isLiquid();
    }

    public static Location getNewHome(Island island, Location loc) {
        Block b;
        if (loc != null) {
            b = loc.getWorld().getHighestBlockAt(loc);
            while (!XMaterial.matchXMaterial(b.getType()).name().endsWith("AIR")) {
                b = b.getLocation().clone().add(0, 1, 0).getBlock();
            }
            if (isSafe(b.getLocation(), island)) {
                return b.getLocation().add(0.5, 0, 0.5);
            }
        }

        for (double X = island.pos1.getX(); X <= island.pos2.getX(); X++) {
            for (double Z = island.pos1.getZ(); Z <= island.pos2.getZ(); Z++) {
                b = loc.getWorld().getHighestBlockAt((int) X, (int) Z);
                while (!XMaterial.matchXMaterial(b.getType()).name().endsWith("AIR")) {
                    b = b.getLocation().clone().add(0, 1, 0).getBlock();
                }
                if (isSafe(b.getLocation(), island)) {
                    return b.getLocation().add(0.5, 0, 0.5);
                }
            }
        }
        return null;
    }

    public static void pay(Player p, double vault, int crystals) {
        User u = User.getUser(p);
        Island island = u.getIsland();
        if (island != null) {
            island.setCrystals(island.getCrystals() + crystals);
            if (IridiumSkyblock.getInstance().getEconomy() == null) {
                island.setMoney(island.getMoney() + vault);
            } else {
                IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, vault);
            }
        } else {
            if (IridiumSkyblock.getInstance().getEconomy() == null) {
                IridiumSkyblock.getInstance().getLogger().warning("Vault plugin not found");
                return;
            }
            IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, vault);
        }
    }

    public static BuyResponse canBuy(Player p, double vault, int crystals) {
        User u = User.getUser(p);
        Island island = u.getIsland();
        if (island != null) {
            if (island.getCrystals() < crystals) return BuyResponse.NOT_ENOUGH_CRYSTALS;
            if (IridiumSkyblock.getInstance().getEconomy() != null) {
                if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) >= vault) {
                    IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, vault);
                    island.setCrystals(island.getCrystals() - crystals);
                    return BuyResponse.SUCCESS;
                }
            }
            if (island.getMoney() >= vault) {
                island.setMoney(island.getMoney() - vault);
                island.setCrystals(island.getCrystals() - crystals);
                return BuyResponse.SUCCESS;
            }
            return BuyResponse.NOT_ENOUGH_VAULT;
        }
        if (IridiumSkyblock.getInstance().getEconomy() != null) {
            if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) >= vault && crystals == 0) {
                IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, vault);
                return BuyResponse.SUCCESS;
            }
        }
        return crystals == 0 ? BuyResponse.NOT_ENOUGH_VAULT : BuyResponse.NOT_ENOUGH_CRYSTALS;
    }

    public static ItemStack getCrystals(int amount) {
        ItemStack itemStack = ItemStackUtils.makeItemHidden(IridiumSkyblock.getInstance().getInventories().crystal, Collections.singletonList(new Placeholder("amount", amount + "")));
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("crystals", amount);
        return nbtItem.getItem();
    }

    public static int getCrystals(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasKey("crystals")) {
            return nbtItem.getInteger("crystals");
        }
        return 0;
    }


    public static String getCurrentTimeStamp(Date date, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        return sdfDate.format(date);
    }

    public static Date getLocalDateTime(String time, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        try {
            return sdfDate.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static XMaterial getXMaterialFromId(int id, byte data) {
        for (MaterialItemId materialItemId : xMaterialItemId.items) {
            if (materialItemId.type == id && materialItemId.meta == data) {
                return XMaterial.matchXMaterial(materialItemId.name).get();
            }
        }
        return null;
    }

    public enum BuyResponse {
        SUCCESS,
        NOT_ENOUGH_CRYSTALS,
        NOT_ENOUGH_VAULT
    }

    public static class XMaterialItemId {
        List<MaterialItemId> items;
    }

    public static class MaterialItemId {
        int type;
        byte meta;
        String name;
    }

}
