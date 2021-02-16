package com.iridium.iridiumskyblock.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.utils.InventoryUtils;
import com.iridium.iridiumskyblock.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrystalsBankItem implements BankItem<Integer> {

    private Inventories.Item item;
    private final boolean enabled = true;

    public CrystalsBankItem(){}

    public CrystalsBankItem(Inventories.Item item) {
        this.item = item;
    }

    @Override
    public Inventories.Item getItem() {
        return item;
    }

    @Override
    public Integer getValue(Island island) {
        return island.getCrystals();
    }

    @Override
    public void withdraw(Player player, Island island, Integer amount) {
        int current = getValue(island);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setCrystals(current - amount);
        addCrystals(player, amount);
    }

    @Override
    public void deposit(Player player, Island island, Integer amount) {
        int i = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            int crystals = MiscUtils.getCrystals(itemStack) * itemStack.getAmount();
            if (crystals != 0) {
                double current = getValue(island);
                island.setCrystals((int) (current + crystals));
                player.getInventory().clear(i);
                if (amount != 0) return;
            }
            i++;
        }
    }

    private void addCrystals(Player player, double amount) {
        ItemStack itemStack = MiscUtils.getCrystals((int) amount);
        if (InventoryUtils.hasOpenSlot(player.getInventory())) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    @JsonIgnore
    public Integer getDefaultWithdraw() {
        return 10;
    }
}
