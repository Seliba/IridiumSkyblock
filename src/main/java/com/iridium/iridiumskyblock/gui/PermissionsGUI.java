package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.utils.ItemStackUtils;
import com.iridium.iridiumskyblock.utils.Placeholder;
import com.iridium.iridiumskyblock.utils.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PermissionsGUI extends GUI implements Listener {

    private final Map<Role, PermissionsGUI> permissions = new HashMap<>();
    private Role role;

    public PermissionsGUI(Island island) {
        super(island, IridiumSkyblock.getInstance().getInventories().permissionsGUISize, IridiumSkyblock.getInstance().getInventories().permissionsGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    public PermissionsGUI(Island island, Role role) {
        super(island, 27, IridiumSkyblock.getInstance().getInventories().permissionsGUITitle);
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
                                setItem(i, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().islandPermissionAllow, Collections.singletonList(new Placeholder("permission", IridiumSkyblock.getInstance().getMessages().permissions.getOrDefault(field.getName(), field.getName())))));
                            } else {
                                setItem(i, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().islandPermissionDeny, Collections.singletonList(new Placeholder("permission", IridiumSkyblock.getInstance().getMessages().permissions.getOrDefault(field.getName(), field.getName())))));
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
                    setItem(i, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().islandRoles, Collections.singletonList(new Placeholder("role", role.toString()))));
                    i++;
                }
            }
            if (IridiumSkyblock.getInstance().getInventories().backButtons)
                setItem(getInventory().getSize() - 5, ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().back));
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
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInstance().getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
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
                        e.getWhoClicked().openInventory(getIsland().permissionsGUI.getInventory());
                        return;
                    }
                    if (role.rank < u.role.rank) {
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
                        e.getWhoClicked().sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}