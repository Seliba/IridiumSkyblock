package com.iridium.iridiumskyblock.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.utils.PlayerUtils;
import org.bukkit.entity.Player;

public class ExperienceBankItem implements BankItem<Integer> {

    private Inventories.Item item;
    private final boolean enabled = true;

    public ExperienceBankItem(){}

    public ExperienceBankItem(Inventories.Item item) {
        this.item = item;
    }

    @Override
    public Inventories.Item getItem() {
        return item;
    }

    @Override
    public Integer getValue(Island island) {
        return island.getExperience();
    }

    @Override
    public void withdraw(Player player, Island island, Integer amount) {
        int current = getValue(island);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setExperience(getValue(island) - amount);
        PlayerUtils.setTotalExperience(player, PlayerUtils.getTotalExperience(player) + amount);
    }

    @Override
    public void deposit(Player player, Island island, Integer amount) {
        int current = PlayerUtils.getTotalExperience(player);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setExperience(getValue(island) + amount);
        PlayerUtils.setTotalExperience(player, PlayerUtils.getTotalExperience(player) - amount);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    @JsonIgnore
    public Integer getDefaultWithdraw() {
        return 100;
    }
}
