package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iridium.iridiumskyblock.commands.CommandManager;
import com.iridium.iridiumskyblock.configs.BlockValues;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Border;
import com.iridium.iridiumskyblock.configs.Commands;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.configs.Messages;
import com.iridium.iridiumskyblock.configs.Missions;
import com.iridium.iridiumskyblock.configs.SQL;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.configs.Shop;
import com.iridium.iridiumskyblock.configs.Stackable;
import com.iridium.iridiumskyblock.configs.Upgrades;
import com.iridium.iridiumskyblock.gui.ConfirmationGUI;
import com.iridium.iridiumskyblock.gui.LanguagesGUI;
import com.iridium.iridiumskyblock.gui.ShopGUI;
import com.iridium.iridiumskyblock.gui.TopGUI;
import com.iridium.iridiumskyblock.gui.VisitGUI;
import com.iridium.iridiumskyblock.listeners.BlockBreakListener;
import com.iridium.iridiumskyblock.listeners.BlockFromToListener;
import com.iridium.iridiumskyblock.listeners.BlockGrowListener;
import com.iridium.iridiumskyblock.listeners.BlockPistonListener;
import com.iridium.iridiumskyblock.listeners.BlockPlaceListener;
import com.iridium.iridiumskyblock.listeners.CreatureSpawnListener;
import com.iridium.iridiumskyblock.listeners.EntityDamageByEntityListener;
import com.iridium.iridiumskyblock.listeners.EntityDeathListener;
import com.iridium.iridiumskyblock.listeners.EntityExplodeListener;
import com.iridium.iridiumskyblock.listeners.EntityPickupItemListener;
import com.iridium.iridiumskyblock.listeners.EntitySpawnListener;
import com.iridium.iridiumskyblock.listeners.EntityTargetLivingEntityListener;
import com.iridium.iridiumskyblock.listeners.ExpansionUnregisterListener;
import com.iridium.iridiumskyblock.listeners.ItemCraftListener;
import com.iridium.iridiumskyblock.listeners.PlayerBucketEmptyListener;
import com.iridium.iridiumskyblock.listeners.PlayerExpChangeListener;
import com.iridium.iridiumskyblock.listeners.PlayerFishListener;
import com.iridium.iridiumskyblock.listeners.PlayerInteractListener;
import com.iridium.iridiumskyblock.listeners.PlayerJoinLeaveListener;
import com.iridium.iridiumskyblock.listeners.PlayerMoveListener;
import com.iridium.iridiumskyblock.listeners.PlayerPortalListener;
import com.iridium.iridiumskyblock.listeners.PlayerTalkListener;
import com.iridium.iridiumskyblock.listeners.PlayerTeleportListener;
import com.iridium.iridiumskyblock.listeners.SpawnerSpawnListener;
import com.iridium.iridiumskyblock.listeners.StructureGrowListener;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.managers.LegacyIslandManager;
import com.iridium.iridiumskyblock.managers.SQLManager;
import com.iridium.iridiumskyblock.managers.UserManager;
import com.iridium.iridiumskyblock.nms.NMS;
import com.iridium.iridiumskyblock.placeholders.ClipPlaceholderAPIManager;
import com.iridium.iridiumskyblock.placeholders.MVDWPlaceholderAPIManager;
import com.iridium.iridiumskyblock.schematics.Schematic;
import com.iridium.iridiumskyblock.schematics.WorldEdit;
import com.iridium.iridiumskyblock.schematics.WorldEdit6;
import com.iridium.iridiumskyblock.schematics.WorldEdit7;
import com.iridium.iridiumskyblock.serializer.Persist;
import com.iridium.iridiumskyblock.support.AdvancedSpawners;
import com.iridium.iridiumskyblock.support.EpicSpawners;
import com.iridium.iridiumskyblock.support.MergedSpawners;
import com.iridium.iridiumskyblock.support.RoseStacker;
import com.iridium.iridiumskyblock.support.SpawnerSupport;
import com.iridium.iridiumskyblock.support.UltimateStacker;
import com.iridium.iridiumskyblock.support.Wildstacker;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class IridiumSkyblock extends JavaPlugin {

    public static SQL sql;
    public static Config configuration;
    public static Messages messages;
    public static Missions missions;
    public static Upgrades upgrades;
    public static Boosters boosters;
    public static Inventories inventories;
    public static Schematics schematics;
    public static Commands commands;
    public static BlockValues blockValues;
    public static Stackable stackable;
    public static Shop shop;
    public static TopGUI topGUI;
    public static ShopGUI shopGUI;
    public static Border border;
    public static Map<Integer, VisitGUI> visitGUI;
    public static Map<Integer, List<String>> oreUpgradeCache = new HashMap<>();
    public static Map<Integer, List<String>> netherOreUpgradeCache = new HashMap<>();
    public static SkyblockGenerator generator;
    public static WorldEdit worldEdit;
    public static Schematic schematic;
    public static IridiumSkyblock instance;
    public static Persist persist;

    public SpawnerSupport spawnerSupport;

    public Economy economy;

    public static SQLManager sqlManager;
    public static CommandManager commandManager;
    public List<String> languages = new ArrayList<>();
    public LanguagesGUI languagesGUI;
    public String latest;

    public Map<UUID, Island> entities = new HashMap<>();

    public static NMS nms;

    public static int blocksPerTick;

    public static Upgrades getUpgrades() {
        if (upgrades == null) {
            upgrades = new Upgrades();
            IridiumSkyblock.persist.getFile(upgrades).delete();
            IridiumSkyblock.instance.saveConfigs();
        }
        return upgrades;
    }

    private final HashMap<String, BlockData> legacy = new HashMap<>();

    public static File schematicFolder;

    @Override
    public void onEnable() {
        blocksPerTick = -1;
        try {
            nms = (NMS) Class.forName("com.iridium.iridiumskyblock.nms." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]).newInstance();
        } catch (ClassNotFoundException e) {
            //Unsupported Version
            getLogger().info("Unsupported Version Detected: " + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
            getLogger().info("Try updating from spigot");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        } catch (ClassNotFoundException e) {
            getLogger().info("CraftBukkit is not Supported");
            getLogger().info("Please use Spigot instead");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        try {
            generator = new SkyblockGenerator();
            instance = this;

            super.onEnable();
            Bukkit.getUpdateFolderFile().mkdir();
            getDataFolder().mkdir();

            persist = new Persist();

            new Metrics(this, 5825);

            if (!loadConfigs()) return;
            saveConfigs();

            startCounting();
            getLanguages();
            moveToSQL();
            Bukkit.getScheduler().runTask(this, () -> { // Call this a tick later to ensure all worlds are loaded
                IslandManager.makeWorlds();
                IslandManager.nextLocation = new Location(IslandManager.getWorld(), 0, 0, 0);
                loadManagers();

                if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) registerMultiverse();

                // Call it as a delayed task to wait for the server to properly load first
                Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.instance, IridiumSkyblock.instance::islandValueManager);

                topGUI = new TopGUI();
                shopGUI = new ShopGUI();
                visitGUI = new HashMap<>();

                registerListeners(new StructureGrowListener(), new EntitySpawnListener(), new BlockPistonListener(), new EntityPickupItemListener(), new PlayerTalkListener(), new ItemCraftListener(), new PlayerTeleportListener(), new PlayerPortalListener(), new BlockBreakListener(), new BlockPlaceListener(), new PlayerInteractListener(), new BlockFromToListener(), new SpawnerSpawnListener(), new EntityDeathListener(), new PlayerJoinLeaveListener(), new BlockGrowListener(), new PlayerTalkListener(), new PlayerMoveListener(), new EntityDamageByEntityListener(), new PlayerExpChangeListener(), new PlayerFishListener(), new EntityExplodeListener(), new PlayerBucketEmptyListener(), new EntityTargetLivingEntityListener(), new CreatureSpawnListener());

                Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.instance, this::addPages, 0, 20 * 60);
                Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.instance, () -> saveData(false), 0, 20 * 60);

                setupPlaceholderAPI();

                schematic = new Schematic();

                Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
                Plugin asyncWorldEdit = Bukkit.getPluginManager().getPlugin("AsyncWorldEdit");
                /*
                If AsyncWorldEdit is loaded, then the schematic wont get pasted instantly.
                This will cause the plugin to try to teleport to the island, however as the schematic hasn't been pasted yet
                it will keep retrying to paste the schematic and get caught into a constant loop of pasting the island until the server crashes
                 */
                if (worldEdit != null && asyncWorldEdit == null) {
                    String worldEditVersion = worldEdit.getDescription().getVersion();
                    // See https://regex101.com/r/j4CEMo/1.
                    // This regex may be updated to support future releases of WorldEdit (version 10+).
                    if (XMaterial.supports(13) && !worldEditVersion.matches("(7\\.[2-9]+.*|[^0-7]\\.[2-9]+.*)")) {
                        getLogger().warning("Your current WorldEdit version has problems with the island schematics!");
                        getLogger().warning("Please update to the newest version immediately!");
                        getLogger().warning("A fallback system is now used");
                        IridiumSkyblock.worldEdit = schematic;
                    } else if (worldEditVersion.startsWith("6")) {
                        IridiumSkyblock.worldEdit = new WorldEdit6();
                    } else if (worldEditVersion.startsWith("7")) {
                        IridiumSkyblock.worldEdit = new WorldEdit7();
                    } else {
                        IridiumSkyblock.worldEdit = schematic;
                    }
                } else {
                    IridiumSkyblock.worldEdit = schematic;
                }

                try {
                    loadSchematics();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) spawnerSupport = new Wildstacker();
                if (Bukkit.getPluginManager().isPluginEnabled("MergedSpawner")) spawnerSupport = new MergedSpawners();
                if (Bukkit.getPluginManager().isPluginEnabled("UltimateStacker"))
                    spawnerSupport = new UltimateStacker();
                if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners")) spawnerSupport = new EpicSpawners();
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedSpawners"))
                    spawnerSupport = new AdvancedSpawners();
                if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")) spawnerSupport = new RoseStacker();
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    registerListeners(new ExpansionUnregisterListener());

                //Register Vault
                RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
                if (rsp != null) {
                    economy = rsp.getProvider();
                }

                getLogger().info("----------------------------------------");
                getLogger().info("");
                getLogger().info(getDescription().getName() + " Enabled!");
                getLogger().info("Version: " + getDescription().getVersion());
                getLogger().info("Patreon: www.patreon.com/Peaches_MLG");
                getLogger().info("");
                getLogger().info("----------------------------------------");

                update();
            });
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    private void update() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            latest = getVersion();
            int latestNumber = Integer.parseInt(latest.replace(".", ""));
            if (latest != null && !latest.equals(getDescription().getVersion()) && latestNumber > Integer.parseInt(getDescription().getVersion().replace(".", ""))) {
                getLogger().info("Newer version available: " + latest);
                if (configuration.automaticUpdate) {
                    getLogger().info("Attempting to download version: " + latest);
                    try {
                        URL url = new URL("http://www.iridiumllc.com/IridiumSkyblock-" + latest + ".jar");
                        URLConnection conn = url.openConnection();
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(15000);
                        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                        conn.setAllowUserInteraction(false);
                        conn.setDoOutput(true);
                        InputStream in = conn.getInputStream();

                        File file = new File(Bukkit.getUpdateFolderFile() + "/IridiumSkyblock-" + latest + ".jar");
                        file.createNewFile();
                        writeToFile(file, in);
                        getFile().renameTo(new File(getFile().getParentFile(), "/IridiumSkyblock-" + latest + ".jar"));
                    } catch (Exception e) {
                        getLogger().info("Failed to connect to update server");
                    }
                }
            }
        });
    }

    private String getVersion() {
        try {
            URL url = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=62480");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            JsonObject object = (JsonObject) new JsonParser().parse(responseBody);
            return object.get("current_version").getAsString();
        } catch (Exception e) {
            getLogger().warning("Failed to connect to api.spigotmc.org");
        }
        return getDescription().getVersion();
    }

    public void getLanguages() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            languages.clear();
            try {
                URLConnection connection = new URL("https://raw.githubusercontent.com/IridiumLLC/IridiumSkyblockLanguages/main/Languages").openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                connection.setAllowUserInteraction(false);
                connection.setDoOutput(true);
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNext()) {
                    String language = scanner.next();
                    languages.add(language);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            languagesGUI = new LanguagesGUI();
        });
    }

    public void setLanguage(String language, Player player) {
        ConfirmationGUI gui = new ConfirmationGUI(() -> {
            //Reset the configs back to default
            persist.getFile(commands).delete();
            persist.getFile(inventories).delete();
            persist.getFile(messages).delete();
            persist.getFile(missions).delete();
            if (!language.equalsIgnoreCase("English")) {
                downloadConfig(language, persist.getFile(commands));
                downloadConfig(language, persist.getFile(inventories));
                downloadConfig(language, persist.getFile(messages));
                downloadConfig(language, persist.getFile(missions));
            }
            loadConfigs();
            saveConfigs();
            player.sendMessage(Utils.color(IridiumSkyblock.messages.reloaded.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }, "Change Language");
        player.openInventory(gui.getInventory());
    }

    public void downloadConfig(String language, File file) {
        try {
            URLConnection connection = new URL("https://raw.githubusercontent.com/IridiumLLC/IridiumSkyblockLanguages/main/" + language + "/" + file.getName()).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setAllowUserInteraction(false);
            connection.setDoOutput(true);
            InputStream in = connection.getInputStream();

            if (!file.exists()) file.createNewFile();
            writeToFile(file, in);
        } catch (IOException e) {
            IridiumSkyblock.instance.getLogger().info("Failed to connect to Translation servers");
        }
    }

    private void writeToFile(File file, InputStream in) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[1024];

        int numRead;
        while ((numRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, numRead);
        }
        in.close();
        out.close();
    }

    private void registerMultiverse() {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + IslandManager.getWorld().getName() + " normal -g " + getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + IslandManager.getWorld().getName());

        if (IridiumSkyblock.configuration.netherIslands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + IslandManager.getNetherWorld().getName() + " nether -g " + getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + IslandManager.getNetherWorld().getName());
        }
    }

    @Override
    public void onDisable() {
        try {
            super.onDisable();

            saveData(false);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.closeInventory();
                User user = User.getUser(p);
                for (Object object : user.getHolograms()) {
                    IridiumSkyblock.nms.removeHologram(p, object);
                }
            }

            getLogger().info("-------------------------------");
            getLogger().info("");
            getLogger().info(getDescription().getName() + " Disabled!");
            getLogger().info("");
            getLogger().info("-------------------------------");
        } catch (Exception e) {
            sendErrorMessage(e);
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (worldName.equals(configuration.worldName) || worldName.equals(configuration.netherWorldName))
            return generator;
        return super.getDefaultWorldGenerator(worldName, id);
    }

    //So we dont break old api's
    @Deprecated
    public IslandManager getIslandManager() {
        return new IslandManager();
    }

    private void addPages() {
        int size = (int) (Math.floor(Utils.getIslands().size() / 45.00) + 1);
        for (int i = 1; i <= size; i++) {
            if (!visitGUI.containsKey(i)) {
                visitGUI.put(i, new VisitGUI(i));
            }
        }
    }

    public void startCounting() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        new Timer().schedule(new TimerTask() {
            public void run() {
                LocalDateTime ldt = LocalDateTime.now();
                if (ldt.getDayOfWeek().equals(DayOfWeek.MONDAY) && configuration.missionRestart.equals(MissionRestart.Weekly) || configuration.missionRestart.equals(MissionRestart.Daily)) {
                    for (Island island : IslandManager.getLoadedIslands()) {
                        island.resetMissions();
                    }
                }
                for (User user : UserManager.cache.values()) {
                    user.tookInterestMessage = false;
                }
                for (Island island : IslandManager.getLoadedIslands()) {
                    double cm = island.money;
                    int cc = island.crystals;
                    int ce = island.exp;
                    island.money = Math.floor(island.money * (1 + (configuration.dailyMoneyInterest / 100.00)));
                    island.crystals = (int) Math.floor(island.crystals * (1 + (configuration.dailyCrystalsInterest / 100.00)));
                    island.exp = (int) Math.floor(island.exp * (1 + (configuration.dailyExpInterest / 100.00)));
                    island.interestCrystal = island.crystals - cc;
                    island.interestMoney = island.money - cm;
                    island.interestExp = island.exp - ce;
                    for (String member : island.members) {
                        Player p = Bukkit.getPlayer(User.getUser(member).name);
                        if (p != null) {
                            if (cm != island.money && cc != island.crystals && ce != island.exp)
                                p.sendMessage(Utils.color(IridiumSkyblock.messages.islandInterest
                                        .replace("%exp%", Utils.NumberFormatter.format(island.interestExp))
                                        .replace("%crystals%", Utils.NumberFormatter.format(island.interestCrystal))
                                        .replace("%money%", Utils.NumberFormatter.format(island.interestMoney))
                                        .replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                        }
                    }
                }
                Bukkit.getScheduler().runTask(IridiumSkyblock.instance, () -> startCounting());
            }

        }, c.getTime());
    }

    public void islandValueManager() {
        //Loop through all online islands and make sure Island#valuableBlocks is accurate
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            ListIterator<Integer> islands = IslandManager.getLoadedIslands().stream().map(is -> is.id).collect(Collectors.toList()).listIterator();

            @Override
            public void run() {
                if (!islands.hasNext()) {
                    islands = IslandManager.getLoadedIslands().stream().map(is -> is.id).collect(Collectors.toList()).listIterator();
                }
                if (islands.hasNext()) {
                    int id = islands.next();
                    Island island = IslandManager.getIslandViaId(id);
                    if (island != null) {
                        island.initBlocks();
                    }
                }
            }
        }, 0, configuration.valueUpdateInterval);
    }

    public void sendErrorMessage(Exception e) {
        e.printStackTrace();
    }

    public void registerListeners(Listener... listener) {
        for (Listener l : listener) {
            Bukkit.getPluginManager().registerEvents(l, this);
        }
    }

    private void setupPlaceholderAPI() {
        Plugin mvdw = getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
        if (mvdw != null && mvdw.isEnabled()) {
            new MVDWPlaceholderAPIManager().register();
            getLogger().info("Successfully registered placeholders with MVDWPlaceholderAPI.");
        }
        setupClipsPlaceholderAPI();
    }

    public void setupClipsPlaceholderAPI() {
        Plugin clip = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (clip != null && clip.isEnabled()) {
            if (new ClipPlaceholderAPIManager().register()) {
                getLogger().info("Successfully registered placeholders with PlaceholderAPI.");
            }
        }
    }

    public void loadSchematics() throws IOException {
        schematicFolder = new File(getDataFolder(), "schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }
        if (!new File(schematicFolder, "island.schematic").exists()) {
            if (getResource("schematics/island.schematic") != null) {
                saveResource("schematics/island.schematic", false);
            }
        }
        if (!new File(schematicFolder, "nether.schematic").exists()) {
            if (getResource("schematics/nether.schematic") != null) {
                saveResource("schematics/nether.schematic", false);
            }
        }

        for (Schematics.FakeSchematic fakeSchematic : schematics.schematics) {
            if (fakeSchematic.netherisland == null) {
                fakeSchematic.netherisland = fakeSchematic.name;
            }
            File overworld = new File(schematicFolder, fakeSchematic.name);
            File nether = new File(schematicFolder, fakeSchematic.netherisland);
            try {
                if (overworld.exists()) {
                    schematic.getSchematicData(overworld);
                } else {
                    IridiumSkyblock.instance.getLogger().warning("Failed to load schematic: " + fakeSchematic.name);
                }
                if (nether.exists()) {
                    schematic.getSchematicData(nether);
                } else {
                    IridiumSkyblock.instance.getLogger().warning("Failed to load schematic: " + fakeSchematic.netherisland);
                }
            } catch (Exception e) {
                e.printStackTrace();
                IridiumSkyblock.instance.getLogger().warning("Failed to load schematic: " + fakeSchematic.name);
            }
        }
    }

    public void moveToSQL() {
        sqlManager = new SQLManager();
        sqlManager.createTables();
        if (persist.getFile("islandmanager").exists()) {
            LegacyIslandManager legacyIslandManager = persist.load(LegacyIslandManager.class, persist.getFile("islandmanager"));
            legacyIslandManager.moveToSQL();
            persist.getFile("islandmanager").renameTo(persist.getFile("islandmanager_old"));
        }
    }

    public void loadManagers() {
        try {
            Connection connection = sqlManager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM islandmanager;");

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                IslandManager.nextID = resultSet.getInt("nextID");
                IslandManager.length = resultSet.getInt("length");
                IslandManager.current = resultSet.getInt("current");
                IslandManager.direction = Direction.valueOf(resultSet.getString("direction"));
                IslandManager.nextLocation = new Location(IslandManager.getWorld(), resultSet.getDouble("x"), 0, resultSet.getDouble("y"));
            } else {
                IslandManager.nextID = 1;
                IslandManager.length = 1;
                IslandManager.current = 0;
                IslandManager.direction = Direction.NORTH;
                IslandManager.nextLocation = new Location(IslandManager.getWorld(), 0, 0, 0);
                PreparedStatement insert = connection.prepareStatement("INSERT INTO islandmanager (nextID,length,current,direction,x,y)VALUES (?,?,?,?,?,?);");
                insert.setInt(1, IslandManager.nextID);
                insert.setInt(2, IslandManager.length);
                insert.setInt(3, IslandManager.current);
                insert.setString(4, IslandManager.direction.name());
                insert.setDouble(5, IslandManager.nextLocation.getX());
                insert.setDouble(6, IslandManager.nextLocation.getZ());
                insert.executeUpdate();
                insert.close();
            }
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        IslandManager.getWorld().getWorldBorder().setSize(Double.MAX_VALUE);
        if (configuration.netherIslands) IslandManager.getNetherWorld().getWorldBorder().setSize(Double.MAX_VALUE);
    }

    public boolean loadConfigs() {
        configuration = persist.getFile(Config.class).exists() ? persist.load(Config.class) : new Config();
        sql = persist.getFile(SQL.class).exists() ? persist.load(SQL.class) : new SQL();
        missions = persist.getFile(Missions.class).exists() ? persist.load(Missions.class) : new Missions();
        messages = persist.getFile(Messages.class).exists() ? persist.load(Messages.class) : new Messages();
        upgrades = persist.getFile(Upgrades.class).exists() ? persist.load(Upgrades.class) : new Upgrades();
        boosters = persist.getFile(Boosters.class).exists() ? persist.load(Boosters.class) : new Boosters();
        inventories = persist.getFile(Inventories.class).exists() ? persist.load(Inventories.class) : new Inventories();
        schematics = persist.getFile(Schematics.class).exists() ? persist.load(Schematics.class) : new Schematics();
        commands = persist.getFile(Commands.class).exists() ? persist.load(Commands.class) : new Commands();
        blockValues = persist.getFile(BlockValues.class).exists() ? persist.load(BlockValues.class) : new BlockValues();
        shop = persist.getFile(Shop.class).exists() ? persist.load(Shop.class) : new Shop();
        border = persist.getFile(Border.class).exists() ? persist.load(Border.class) : new Border();
        stackable = persist.getFile(Stackable.class).exists() ? persist.load(Stackable.class) : new Stackable();
        if (stackable.blockList.isEmpty()) {
            stackable.blockList = Arrays.asList(XMaterial.NETHERITE_BLOCK, XMaterial.DIAMOND_BLOCK, XMaterial.EMERALD_BLOCK, XMaterial.GOLD_BLOCK, XMaterial.IRON_BLOCK);
        }

        if (inventories.red.slot == null) inventories.red.slot = 10;
        if (inventories.green.slot == null) inventories.green.slot = 12;
        if (inventories.blue.slot == null) inventories.blue.slot = 14;
        if (inventories.off.slot == null) inventories.off.slot = 16;
        for (Schematics.FakeSchematic schematic : schematics.schematics) {
            if (schematic.biome == null) schematic.biome = XBiome.PLAINS;
        }

        missions.missions.remove(null);


        commandManager = new CommandManager("island");
        commandManager.registerCommands();

        if (configuration == null || missions == null || messages == null || upgrades == null || boosters == null || inventories == null || schematics == null || commands == null || blockValues == null || shop == null || stackable == null) {
            return false;
        }

        if (shop.shop == null) shop = new Shop();

        if (commandManager != null) {
            if (commandManager.commands.contains(IridiumSkyblock.commands.shopCommand)) {
                if (!configuration.islandShop)
                    commandManager.unRegisterCommand(IridiumSkyblock.commands.shopCommand);
            } else {
                if (configuration.islandShop)
                    commandManager.registerCommand(IridiumSkyblock.commands.shopCommand);
            }
        }

        blockValues.blockvalue.remove(XMaterial.AIR);

        if (configuration.biomes != null) {
            configuration.islandBiomes.clear();
            for (XBiome biome : configuration.biomes) {
                configuration.islandBiomes.put(biome, new Config.BiomeConfig());
            }
            configuration.biomes = null;
        }

        oreUpgradeCache.clear();
        for (int i : upgrades.oresUpgrade.upgrades.keySet()) {
            ArrayList<String> items = new ArrayList<>();
            for (String item : upgrades.oresUpgrade.upgrades.get(i).ores) {
                if (item != null) {
                    int i1 = Integer.parseInt(item.split(":")[1]);
                    for (int a = 0; a <= i1; a++) {
                        items.add(item.split(":")[0]);
                    }
                } else {
                    upgrades.oresUpgrade.upgrades.get(i).ores.remove(null);
                }
            }
            oreUpgradeCache.put(i, items);
        }

        netherOreUpgradeCache.clear();
        for (int i : upgrades.oresUpgrade.upgrades.keySet()) {
            ArrayList<String> items = new ArrayList<>();
            for (String item : upgrades.oresUpgrade.upgrades.get(i).netherores) {
                if (item != null) {
                    int i1 = Integer.parseInt(item.split(":")[1]);
                    for (int a = 0; a <= i1; a++) {
                        items.add(item.split(":")[0]);
                    }
                } else {
                    upgrades.oresUpgrade.upgrades.get(i).netherores.remove(null);
                }
            }
            netherOreUpgradeCache.put(i, items);
        }

        if (boosters.flightBooster.time == 0) boosters.flightBooster.time = 3600;
        if (boosters.experianceBooster.time == 0) boosters.experianceBooster.time = 3600;
        if (boosters.farmingBooster.time == 0) boosters.farmingBooster.time = 3600;
        if (boosters.spawnerBooster.time == 0) boosters.spawnerBooster.time = 3600;

        if (boosters.spawnerBooster.crystalsCost == 0 && boosters.spawnerBooster.vaultCost == 0)
            boosters.spawnerBooster.crystalsCost = 15;
        if (boosters.farmingBooster.crystalsCost == 0 && boosters.farmingBooster.vaultCost == 0)
            boosters.farmingBooster.crystalsCost = 15;
        if (boosters.experianceBooster.crystalsCost == 0 && boosters.experianceBooster.vaultCost == 0)
            boosters.experianceBooster.crystalsCost = 15;
        if (boosters.flightBooster.crystalsCost == 0 && boosters.flightBooster.vaultCost == 0)
            boosters.flightBooster.crystalsCost = 15;

        if (configuration.blockvalue != null) {
            blockValues.blockvalue = new HashMap<>(configuration.blockvalue);
            configuration.blockvalue = null;
        }
        if (configuration.spawnervalue != null) {
            blockValues.spawnervalue = new HashMap<>(configuration.spawnervalue);
            configuration.spawnervalue = null;
        }
        int max = 0;
        for (Upgrades.IslandUpgrade size : upgrades.sizeUpgrade.upgrades.values()) {
            if (max < size.size) {
                max = size.size;
            }
        }
        if (configuration.distance <= max) {
            configuration.distance = max + 1;
        }
        for (Island island : IslandManager.getLoadedIslands()) {
            if (island.islandMenuGUI != null) island.islandMenuGUI.getInventory().clear();
            if (island.schematicSelectGUI != null) island.schematicSelectGUI.getInventory().clear();
            if (island.bankGUI != null) island.bankGUI.getInventory().clear();
            if (island.boosterGUI != null) island.boosterGUI.getInventory().clear();
            if (island.coopGUI != null) island.coopGUI.getInventory().clear();
            if (island.membersGUI != null) island.membersGUI.getInventory().clear();
            if (island.missionsGUI != null) island.missionsGUI.getInventory().clear();
            if (island.permissionsGUI != null) island.permissionsGUI.getInventory().clear();
            if (island.upgradeGUI != null) island.upgradeGUI.getInventory().clear();
            if (island.warpGUI != null) island.warpGUI.getInventory().clear();
            if (island.borderColorGUI != null) island.borderColorGUI.getInventory().clear();
            if (configuration.missionRestart == MissionRestart.Instantly) {
                island.resetMissions();
            }
        }
        try {
            for (Field field : Permissions.class.getDeclaredFields()) {
                if (!messages.permissions.containsKey(field.getName())) {
                    messages.permissions.put(field.getName(), field.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public BlockData fromLegacy(Material material, byte data) {
        if (!legacy.containsKey(material.name() + data))
            legacy.put(material.name() + data, Bukkit.getUnsafe().fromLegacy(material, data));
        return legacy.get(material.name() + data);
    }

    public void saveData(boolean async) {
        if (async) Bukkit.getScheduler().runTaskAsynchronously(this, () -> saveData(false));
        for (User user : UserManager.cache.values()) {
            user.save(false);
        }

        for (Island island : IslandManager.getLoadedIslands()) {
            island.save(false);
            IslandDataManager.save(island, false);
        }
        try {
            Connection connection = sqlManager.getConnection();
            PreparedStatement insert = connection.prepareStatement("UPDATE islandmanager SET nextID = ?, length=?, current=?, direction=?, x=?,y=?;");
            insert.setInt(1, IslandManager.nextID);
            insert.setInt(2, IslandManager.length);
            insert.setInt(3, IslandManager.current);
            insert.setString(4, IslandManager.direction.name());
            insert.setDouble(5, IslandManager.nextLocation.getX());
            insert.setDouble(6, IslandManager.nextLocation.getZ());
            insert.executeUpdate();
            insert.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveConfigs() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (configuration != null) persist.save(configuration);
            if (sql != null) persist.save(sql);
            if (missions != null) persist.save(missions);
            if (messages != null) persist.save(messages);
            if (upgrades != null) persist.save(upgrades);
            if (boosters != null) persist.save(boosters);
            if (inventories != null) persist.save(inventories);
            if (schematics != null) persist.save(schematics);
            if (commands != null) persist.save(commands);
            if (blockValues != null) persist.save(blockValues);
            if (shop != null) persist.save(shop);
            if (border != null) persist.save(border);
            if (stackable != null) persist.save(stackable);
        });
    }
}