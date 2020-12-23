package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Shop;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand extends Command {

    public ShopCommand() {
        super(Collections.singletonList("shop"), "Access the Skyblock Shop", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length > 1) {
            String shopName = args[1];
            for (Shop.ShopObject shopObject : IridiumSkyblock.shop.shop) {
                if (shopObject.name.equalsIgnoreCase(shopName)) {
                    p.openInventory(IridiumSkyblock.shopGUI.shops.get(shopObject.slot).shops.get(1).getInventory());
                    return;
                }
            }
        }
        p.openInventory(IridiumSkyblock.shopGUI.getInventory());
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return IridiumSkyblock.shop.shop.stream().map(shopObject -> shopObject.name).collect(Collectors.toList());
    }
}
