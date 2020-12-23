package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.api.IslandDemoteEvent;
import com.iridium.iridiumskyblock.api.IslandPromoteEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MembersGUI extends GUI implements Listener {

    public Map<Integer, User> users = new HashMap<>();

    public MembersGUI(Island island) {
        super(island, IridiumSkyblock.inventories.membersGUISize, IridiumSkyblock.inventories.membersGUITitle);
        IridiumSkyblock.instance.registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        users.clear();
        Island island = getIsland();
        if (island != null) {
            int i = 0;
            for (String member : island.members) {
                User u = User.getUser(member);
                users.put(i, u);
                ItemStack head = Utils.makeItem(IridiumSkyblock.inventories.islandmember, Arrays.asList(new Utils.Placeholder("demote", u.getRole().equals(Role.Visitor) ? IridiumSkyblock.messages.Kick : IridiumSkyblock.messages.Demote), new Utils.Placeholder("player", User.getUser(member).name), new Utils.Placeholder("role", u.getRole().toString())));
                setItem(i, head);
                i++;
            }
            if (IridiumSkyblock.inventories.backButtons) setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.inventories.back));
        }
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.inventories.backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
                return;
            }
            User user = User.getUser((Player) e.getWhoClicked());
            Island island = user.getIsland();
            if (user.bypassing || getIsland().equals(island)) {
                if (users.containsKey(e.getSlot())) {
                    User u = users.get(e.getSlot());
                    if (e.getClick().equals(ClickType.LEFT)) {
                        if (island.getPermissions(user.role).demote) {
                            if (u.getRole().rank < user.role.rank) {
                                if (u.getRole().equals(Role.Member)) {
                                    if (island.getPermissions(user.role).kickMembers) {
                                        island.removeUser(u);
                                        getInventory().clear();
                                        Player player = Bukkit.getPlayer(u.name);
                                        if (player != null)
                                            player.sendMessage(Utils.color(IridiumSkyblock.messages.youHaveBeenKicked.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                                    } else {
                                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                                    }
                                } else {
                                    IslandDemoteEvent event = new IslandDemoteEvent(island, u, user, Role.getViaRank(u.getRole().rank + 1));
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled()) {
                                        u.role = Role.getViaRank(u.getRole().rank - 1);
                                        for (String member : island.members) {
                                            Player p = Bukkit.getPlayer(User.getUser(member).name);
                                            if (p != null) {
                                                p.sendMessage(Utils.color(IridiumSkyblock.messages.playerDemoted.replace("%rank%", u.getRole().toString()).replace("%player%", u.name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                                            }
                                        }
                                    }
                                }
                            } else {
                                e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.cantDemoteUser.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                            }
                        } else {
                            e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        }
                    } else {
                        if (island.getPermissions(user.role).promote) {
                            if (!(u.getRole().equals(Role.Owner))) {
                                if (u.getRole().rank < user.role.rank) {
                                    if (u.getRole().rank >= Role.CoOwner.rank) {
                                        e.getWhoClicked().openInventory(new ConfirmationGUI(island, () -> {
                                            OfflinePlayer p = Bukkit.getOfflinePlayer(u.name);
                                            if (p != null) {
                                                island.setOwner(p);
                                            }
                                        }, IridiumSkyblock.messages.transferAction.replace("%player%", u.name)).getInventory());
                                    } else {
                                        IslandPromoteEvent event = new IslandPromoteEvent(island, u, user, Role.getViaRank(u.getRole().rank + 1));
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            u.role = Role.getViaRank(u.getRole().rank + 1);
                                            for (String member : island.members) {
                                                Player p = Bukkit.getPlayer(User.getUser(member).name);
                                                if (p != null) {
                                                    p.sendMessage(Utils.color(IridiumSkyblock.messages.playerPromoted.replace("%rank%", u.getRole().toString()).replace("%player%", u.name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.cantPromoteUser.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                                }
                            } else {
                                e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.noPermission.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                            }
                        } else {
                            e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.messages.cantDemoteOwner.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        }
                    }
                }
            }
        }
    }
}
