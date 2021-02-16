package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Shop;
import com.iridium.iridiumskyblock.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShopGUI extends GUI implements Listener {
    private final int page;
    private final Shop.ShopObject shop;
    private final Map<Integer, Shop.ShopItem> items = new HashMap<>();

    public ShopGUI(Shop.ShopObject shop, int page) {
        super(IridiumSkyblock.getInstance().getInventories().shopGUISize, IridiumSkyblock.getInstance().getInventories().shopGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
        this.shop = shop;
        this.page = page;
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (!IridiumSkyblock.getInstance().getConfiguration().islandShop) return;
        if (shop != null) {
            for (Shop.ShopItem item : shop.items) {
                if (item.page == page) {
                    items.put(item.slot, item);
                    setItem(item.slot, ItemStackUtils.makeItem(item.material, item.amount, item.displayName,
                            StringUtils.color(StringUtils.processMultiplePlaceholders(IridiumSkyblock.getInstance().getShop().lore, Arrays.asList(
                                    new Placeholder("buyvaultprice", NumberFormatter.format(item.buyVault)),
                                    new Placeholder("sellvaultprice", NumberFormatter.format(item.sellVault)),
                                    new Placeholder("buycrystalprice", NumberFormatter.format(item.buyCrystals)),
                                    new Placeholder("sellcrystalprice", NumberFormatter.format(item.sellCrystals)))))));
                }
            }
            setItem(getInventory().getSize() - 3, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().nextPage));
            setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
            setItem(getInventory().getSize() - 7, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().previousPage));
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (items.containsKey(e.getSlot())) {
                Shop.ShopItem item = items.get(e.getSlot());
                if (e.getClick().equals(ClickType.RIGHT)) {
                    sellItem((Player) e.getWhoClicked(), item, item.amount);
                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                    sellItem((Player) e.getWhoClicked(), item, item.material.parseMaterial().getMaxStackSize());
                } else if (e.getClick().equals(ClickType.LEFT)) {
                    buyItem((Player) e.getWhoClicked(), item, item.amount);
                } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                    buyItem((Player) e.getWhoClicked(), item, item.material.parseMaterial().getMaxStackSize());
                }
            }
            if (e.getSlot() == getInventory().getSize() - 3) {
                ShopGUI shopGUI = IridiumSkyblock.getInstance().getShopGUI().pages.getPage(shop.slot).getPage(page + 1);
                if (shopGUI != null) {
                    e.getWhoClicked().openInventory(shopGUI.getInventory());
                }
            }
            if (e.getSlot() == getInventory().getSize() - 5) {
                e.getWhoClicked().openInventory(IridiumSkyblock.getInstance().getShopGUI().getInventory());
            }
            if (e.getSlot() == getInventory().getSize() - 7) {
                ShopGUI shopGUI = IridiumSkyblock.getInstance().getShopGUI().pages.getPage(shop.slot).getPage(page - 1);
                if (shopGUI != null) {
                    e.getWhoClicked().openInventory(shopGUI.getInventory());
                }
            }
        }
    }

    private void sellItem(Player player, Shop.ShopItem item, int amount) {
        int itemsInInventory = InventoryUtils.getAmount(player.getInventory(), item.material);
        IridiumSkyblock.getInstance().getLogger().info(amount + " " + itemsInInventory);
        if (itemsInInventory < amount) amount = itemsInInventory;
        if ((item.sellVault > 0 || item.sellCrystals > 0) && itemsInInventory > 0 && item.commands == null) {
            double sellVault = item.sellVault / item.amount * amount;
            int sellCrystals = (int) Math.floor((item.sellCrystals / (double) item.amount) * amount);

            InventoryUtils.removeAmount(player.getInventory(), item.material, amount);

            MiscUtils.pay(player, sellVault, sellCrystals);
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().shopSoldMessage
                    .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                    .replace("%item%", item.material + "")
                    .replace("%amount%", amount + "")
                    .replace("%crystals%", NumberFormatter.format(sellCrystals))
                    .replace("%money%", NumberFormatter.format(sellVault))));
        } else {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cannotSellItem.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

    private void buyItem(Player player, Shop.ShopItem item, int amount) {
        if (item.commands != null) amount = 1;
        if (item.buyVault > 0 || item.buyCrystals > 0) {
            double buyVault = item.buyVault / item.amount * amount;
            int buyCrystals = (int) (item.buyCrystals / ((double) item.amount * amount));
            MiscUtils.BuyResponse response = MiscUtils.canBuy(player, buyVault, buyCrystals);
            if (response == MiscUtils.BuyResponse.SUCCESS) {
                if (item.commands == null) {
                    ItemStack itemStack = item.material.parseItem();
                    itemStack.setAmount(amount);
                    if (InventoryUtils.hasOpenSlot(player.getInventory())) {
                        player.getInventory().addItem(itemStack);
                    } else {
                        player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
                    }
                } else {
                    for (String command : item.commands) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }
                }
                player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().shopBoughtMessage
                        .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)
                        .replace("%item%", item.material + "")
                        .replace("%amount%", amount + "")
                        .replace("%crystals%", NumberFormatter.format(buyCrystals))
                        .replace("%money%", NumberFormatter.format(buyVault))));
            } else {
                player.sendMessage(StringUtils.color((response == MiscUtils.BuyResponse.NOT_ENOUGH_VAULT ? IridiumSkyblock.getInstance().getMessages().cantBuy : IridiumSkyblock.getInstance().getMessages().notEnoughCrystals).replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
            }
        } else {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().cannotBuyItem.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }
}
