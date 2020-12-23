package com.iridium.iridiumskyblock.serializer;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.serializer.typeadapter.DateTypeAdapter;
import com.iridium.iridiumskyblock.serializer.typeadapter.EnumTypeAdapter;
import com.iridium.iridiumskyblock.serializer.typeadapter.InventoryTypeAdapter;
import com.iridium.iridiumskyblock.serializer.typeadapter.LocationTypeAdapter;
import com.iridium.iridiumskyblock.serializer.typeadapter.XBiomeTypeAdapter;
import com.iridium.iridiumskyblock.serializer.typeadapter.XMaterialsTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class Persist {
    public final Gson gson = buildGson().create();

    public static String getName(Class<?> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    // ------------------------------------------------------------ //
    // GET NAME - What should we call this type of object?
    // ------------------------------------------------------------ //

    public static String getName(Object o) {
        return getName(o.getClass());
    }

    public static String getName(Type type) {
        return getName(type.getClass());
    }

    private GsonBuilder buildGson() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
                .enableComplexMapKeySerialization()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(Inventory.class, new InventoryTypeAdapter())
                .registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY)
                .registerTypeAdapter(XMaterial.class, new XMaterialsTypeAdapter())
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(XBiome.class, new XBiomeTypeAdapter());
    }

    // ------------------------------------------------------------ //
    // GET FILE - In which file would we like to store this object?
    // ------------------------------------------------------------ //

    public File getFile(String name) {
        return new File(IridiumSkyblock.instance.getDataFolder(), name + ".json");
    }

    public File getFile(Class<?> clazz) {
        return getFile(getName(clazz));
    }

    public File getFile(Object obj) {
        return getFile(getName(obj));
    }

    // SAVE

    public void save(Object instance) {
        save(instance, getFile(instance));
    }

    public void save(Object instance, File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                IridiumSkyblock.instance.sendErrorMessage(e);
            }
        }
        DiscUtil.writeCatch(file, gson.toJson(instance));
    }

    // LOAD BY CLASS

    public <T> T load(Class<T> clazz) {
        return load(clazz, getFile(clazz));
    }

    public <T> T load(Class<T> clazz, File file) {
        String content = DiscUtil.readCatch(file);
        if (content == null) {
            return null;
        }

        try {
            return gson.fromJson(content, clazz);
        } catch (Exception ex) {
            IridiumSkyblock.instance.getLogger().severe("Failed to parse " + file.toString() + ": " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(IridiumSkyblock.instance);
        }

        return null;
    }

    public <T> T load(Class<T> clazz, String content) {
        try {
            return gson.fromJson(content, clazz);
        } catch (Exception ex) {
            IridiumSkyblock.instance.getLogger().severe("Failed to parse json");
            Bukkit.getPluginManager().disablePlugin(IridiumSkyblock.instance);
        }

        return null;
    }
}