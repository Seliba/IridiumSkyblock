package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PermissionsGUI extends GUI implements Listener {

    private Role role;

    private final Map<Role, PermissionsGUI> permissions = new HashMap<>();

    public PermissionsGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().permissionsGUISize, IridiumSkyblock.getInventories().permissionsGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    public PermissionsGUI(Island island, Role role) {
        super(island, 27, IridiumSkyblock.getInventories().permissionsGUITitle);
        this.role = role;
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            if (role != null) {
                int i = 0;
                try {
                    for (Field field : Permissions.class.getDeclaredFields()) {
                        Object object = field.get(getIsland().getPermissions(role));
                        if (object instanceof Boolean) {
                            if ((Boolean) object) {
                                setItem(i, Utils.makeItem(IridiumSkyblock.getInventories().islandPermissionAllow, Collections.singletonList(new Utils.Placeholder("permission", IridiumSkyblock.getMessages().permissions.getOrDefault(field.getName(), field.getName())))));
                            } else {
                                setItem(i, Utils.makeItem(IridiumSkyblock.getInventories().islandPermissionDeny, Collections.singletonList(new Utils.Placeholder("permission", IridiumSkyblock.getMessages().permissions.getOrDefault(field.getName(), field.getName())))));
                            }
                        }
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int i = 11;
                for (Role role : Role.values()) {
                    permissions.put(role, new PermissionsGUI(getIsland(), role));
                    setItem(i, Utils.makeItem(IridiumSkyblock.getInventories().islandRoles, Collections.singletonList(new Utils.Placeholder("role", role.toString()))));
                    i++;
                }
            }
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }


    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        User u = User.getUser(p);
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5) {
                e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
            }
            int i = 11;
            for (Role role : Role.values()) {
                if (e.getSlot() == i) {
                    e.getWhoClicked().openInventory(permissions.get(role).getInventory());
                }
                i++;
            }
        } else {
            for (Role role : permissions.keySet()) {
                PermissionsGUI gui = permissions.get(role);
                if (e.getInventory().equals(gui.getInventory())) {
                    e.setCancelled(true);
                    if (e.getSlot() == getInventory().getSize() - 5) {
                        e.getWhoClicked().openInventory(getIsland().getPermissionsGUI().getInventory());
                        return;
                    }
                    if (role.getRank() < u.role.getRank()) {
                        int i = 0;
                        try {
                            for (Field field : Permissions.class.getDeclaredFields()) {
                                Object object = field.get(getIsland().getPermissions(role));
                                if (i == e.getSlot()) {
                                    field.setAccessible(true);
                                    field.setBoolean(getIsland().getPermissions(role), !(Boolean) object);
                                    addContent();
                                }
                                i++;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}