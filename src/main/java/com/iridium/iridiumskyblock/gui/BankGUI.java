package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.bank.BankItem;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.Placeholder;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;

public class BankGUI extends GUI implements Listener {
    public BankGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().bankGUISize, IridiumSkyblock.getInstance().getInventories().bankGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            for (BankItem bankItem : IridiumSkyblock.getInstance().getBankItems()) {
                if (!bankItem.isEnabled()) continue;
                setItem(bankItem.getItem().slot, ItemStackUtils.makeItem(bankItem.getItem(), Collections.singletonList(new Placeholder("amount", bankItem.getValue(getIsland()).toString()))));
            }

            if (IridiumSkyblock.getInstance().getInventories().backButtons) {
                setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            Island island = getIsland();
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInstance().getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
                return;
            }
            if (!IridiumSkyblock.getInstance().getConfiguration().bankWithdrawing) {
                p.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().withdrawDisabled
                        .replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                return;
            }
            for (BankItem bankItem : IridiumSkyblock.getInstance().getBankItems()) {
                if (bankItem.getItem().slot != e.getSlot() || !bankItem.isEnabled()) continue;
                switch (e.getClick()) {
                    case SHIFT_LEFT:
                        bankItem.withdraw(p, island, Integer.MAX_VALUE);
                        break;
                    case LEFT:
                        bankItem.withdraw(p, island, bankItem.getDefaultWithdraw());
                        break;
                    case SHIFT_RIGHT:
                        bankItem.deposit(p, island, Integer.MAX_VALUE);
                        break;
                    case RIGHT:
                        bankItem.deposit(p, island, bankItem.getDefaultWithdraw());
                        break;
                }
            }
        }
    }
}
