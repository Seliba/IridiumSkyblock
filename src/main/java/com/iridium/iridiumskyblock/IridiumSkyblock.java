package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.api.IridiumSkyblockReloadEvent;
import com.iridium.iridiumskyblock.bank.BankItem;
import com.iridium.iridiumskyblock.commands.CommandManager;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.listeners.*;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.managers.SQLManager;
import com.iridium.iridiumskyblock.managers.UserManager;
import com.iridium.iridiumskyblock.nms.NMS;
import com.iridium.iridiumskyblock.placeholders.ClipPlaceholderAPIManager;
import com.iridium.iridiumskyblock.placeholders.MVDWPlaceholderAPIManager;
import com.iridium.iridiumskyblock.schematics.Schematic;
import com.iridium.iridiumskyblock.schematics.WorldEdit;
import com.iridium.iridiumskyblock.schematics.WorldEdit6;
import com.iridium.iridiumskyblock.schematics.WorldEdit7;
import com.iridium.iridiumskyblock.support.*;
import com.iridium.iridiumskyblock.utils.StringUtils;
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

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class IridiumSkyblock extends JavaPlugin {

    private static IridiumSkyblock instance;
    private final Map<Integer, List<XMaterial>> oreUpgradeCache = new HashMap<>();
    private final Map<Integer, List<XMaterial>> netherOreUpgradeCache = new HashMap<>();
    private final HashMap<String, BlockData> legacy = new HashMap<>();
    private final List<Upgrades.Upgrade> islandUpgrades = new ArrayList<>();
    private final List<Boosters.Booster> islandBoosters = new ArrayList<>();
    private final List<BankItem> bankItems = new ArrayList<>();
    public Map<UUID, Island> entities = new HashMap<>();
    private SQL sql;
    private Config configuration;
    private Messages messages;
    private Missions missions;
    private Upgrades upgrades;
    private Boosters boosters;
    private Inventories inventories;
    private Schematics schematics;
    private Commands commands;
    private BlockValues blockValues;
    private Stackable stackable;
    private Shop shop;
    private TopGUI topGUI;
    private ShopMenuGUI shopMenuGUI;
    private Border border;
    private Bank bank;
    private SkyblockGenerator generator;
    private WorldEdit worldEdit;
    private Schematic schematic;
    private Persist persist;
    private SpawnerSupport spawnerSupport;
    private Economy economy;
    private SQLManager sqlManager;
    private CommandManager commandManager;
    private MultiplePagesGUI<LanguagesGUI> languagesGUI;
    private String latest;
    private NMS nms;
    private File schematicFolder;
    private MultiplePagesGUI<VisitGUI> visitGUI;

    public static IridiumSkyblock getInstance() {
        return instance;
    }

//    @Getter
//    private DatabaseWrapper database;

    @Override
    public void onEnable() {
        instance = this;

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
        generator = new SkyblockGenerator();

        super.onEnable();
        Bukkit.getUpdateFolderFile().mkdir();
        getDataFolder().mkdir();

        persist = new Persist(Persist.PersistType.YAML);

        new Metrics(this, 5825);

        loadConfigs();
        saveConfigs();

        IslandManager.startCounting();
        setLanguages();
        Bukkit.getScheduler().runTask(this, () -> { // Call this a tick later to ensure all worlds are loaded
            IslandManager.makeWorlds();
            IslandManager.nextLocation = new Location(IslandManager.getWorld(), 0, 0, 0);
//            try {
//                database = new DatabaseWrapper();
//            } catch (SQLException exception) {
//                exception.printStackTrace();
//            }
            loadManagers();

            if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) registerMultiverse();

            // Call it as a delayed task to wait for the server to properly load first
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), IslandManager::islandValueManager);

            topGUI = new TopGUI();
            shopMenuGUI = new ShopMenuGUI();
            visitGUI = new MultiplePagesGUI<>(() -> {
                int size = (int) (Math.floor(IslandManager.getLoadedIslands().size() / 45.00) + 1);
                for (int i = 1; i <= size; i++) {
                    VisitGUI visitGUI = getVisitGUI().getPage(i);
                    if (visitGUI == null) {
                        getVisitGUI().addPage(i, new VisitGUI(i));
                    }
                }
            }, true);

            registerListeners(new PlayerRespawnListener(), new StructureGrowListener(), new EntitySpawnListener(), new BlockPistonListener(), new EntityPickupItemListener(), new PlayerTalkListener(), new ItemCraftListener(), new PlayerTeleportListener(), new PlayerPortalListener(), new BlockBreakListener(), new BlockPlaceListener(), new PlayerInteractListener(), new BlockFromToListener(), new SpawnerSpawnListener(), new EntityDeathListener(), new PlayerJoinLeaveListener(), new BlockGrowListener(), new PlayerTalkListener(), new PlayerMoveListener(), new EntityDamageByEntityListener(), new PlayerExpChangeListener(), new PlayerFishListener(), new EntityExplodeListener(), new PlayerBucketEmptyListener(), new EntityTargetLivingEntityListener(), new CreatureSpawnListener());

            Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::saveData, 0, 20 * 30);

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
                    IridiumSkyblock.getInstance().worldEdit = schematic;
                } else if (worldEditVersion.startsWith("6")) {
                    IridiumSkyblock.getInstance().worldEdit = new WorldEdit6();
                } else if (worldEditVersion.startsWith("7")) {
                    IridiumSkyblock.getInstance().worldEdit = new WorldEdit7();
                } else {
                    IridiumSkyblock.getInstance().worldEdit = schematic;
                }
            } else {
                IridiumSkyblock.getInstance().worldEdit = schematic;
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

            new IridiumSkyblockAPI(this);

            getLogger().info("----------------------------------------");
            getLogger().info("");
            getLogger().info(getDescription().getName() + " Enabled!");
            getLogger().info("Version: " + getDescription().getVersion());
            getLogger().info("Patreon: www.patreon.com/Peaches_MLG");
            getLogger().info("");
            getLogger().info("----------------------------------------");

            update();
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();

        saveData();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
            User user = User.getUser(p);
            for (Object object : user.getHolograms()) {
                getNms().removeHologram(p, object);
            }
        }

        getLogger().info("-------------------------------");
        getLogger().info("");
        getLogger().info(getDescription().getName() + " Disabled!");
        getLogger().info("");
        getLogger().info("-------------------------------");
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

    public void setLanguages() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            List<String> languages = new ArrayList<>();
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
            java.util.Collections.sort(languages);
            languagesGUI = new MultiplePagesGUI<>(() -> {
                int size = (int) (Math.floor(languages.size() / 45.00) + 1);
                for (int i = 1; i <= size; i++) {
                    languagesGUI.addPage(i, new LanguagesGUI(i, languages));
                }
            }, false);
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
            player.sendMessage(StringUtils.color(getMessages().reloaded.replace("%prefix%", getConfiguration().prefix)));
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
            getLogger().info("Failed to connect to Translation servers");
        }
    }

    private void writeToFile(File file, InputStream in) throws IOException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] buffer = new byte[1024];

            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }
        }

        in.close();
    }

    private void registerMultiverse() {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + IslandManager.getWorld().getName() + " normal -g " + getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + IslandManager.getWorld().getName());

        if (getConfiguration().netherIslands) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + IslandManager.getNetherWorld().getName() + " nether -g " + getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + getName() + " " + IslandManager.getNetherWorld().getName());
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (worldName.equals(configuration.worldName) || worldName.equals(configuration.netherWorldName))
            return generator;
        return super.getDefaultWorldGenerator(worldName, id);
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

    public void saveSchematics(String name) {
        if (!new File(schematicFolder, name).exists()) {
            if (getResource("schematics/" + name) != null) {
                saveResource("schematics/" + name, false);
            }
        }
    }

    public void loadSchematics() throws IOException {
        schematicFolder = new File(getDataFolder(), "schematics");
        if (!schematicFolder.exists()) schematicFolder.mkdir();
        saveSchematics("desert.schem");
        saveSchematics("jungle.schem");
        saveSchematics("mushroom.schem");
        saveSchematics("nether.schematic");

        for (Schematics.FakeSchematic fakeSchematic : schematics.schematicList) {
            File overworld = new File(schematicFolder, fakeSchematic.overworldData.schematic);
            File nether = new File(schematicFolder, fakeSchematic.netherData.schematic);
            try {
                if (overworld.exists()) {
                    schematic.getSchematicData(overworld);
                } else {
                    getLogger().warning("Failed to load schematic: " + fakeSchematic.overworldData.schematic);
                }
                if (nether.exists()) {
                    schematic.getSchematicData(nether);
                } else {
                    getLogger().warning("Failed to load schematic: " + fakeSchematic.netherData.schematic);
                }
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().warning("Failed to load schematic: " + fakeSchematic.netherData.schematic);
            }
        }
    }

    public void loadManagers() {

        sqlManager = new SQLManager();
        sqlManager.createTables();
        try (Connection connection = sqlManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM islandmanager;")) {
                try (ResultSet resultSet = statement.executeQuery()) {
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
                        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO islandmanager (nextID,length,current,direction,x,y)VALUES (?,?,?,?,?,?);")) {
                            insert.setInt(1, IslandManager.nextID);
                            insert.setInt(2, IslandManager.length);
                            insert.setInt(3, IslandManager.current);
                            insert.setString(4, IslandManager.direction.name());
                            insert.setDouble(5, IslandManager.nextLocation.getX());
                            insert.setDouble(6, IslandManager.nextLocation.getZ());
                            insert.executeUpdate();
                        }
                    }
                    IslandDataManager.update(connection);
                }
                connection.commit();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        IslandManager.getWorld().getWorldBorder().setSize(Double.MAX_VALUE);
        if (configuration.netherIslands) IslandManager.getNetherWorld().getWorldBorder().setSize(Double.MAX_VALUE);
    }

    public void loadConfigs() {
        configuration = persist.load(Config.class);
        sql = persist.load(SQL.class);
        missions = persist.load(Missions.class);
        messages = persist.load(Messages.class);
        upgrades = persist.load(Upgrades.class);
        boosters = persist.load(Boosters.class);
        inventories = persist.load(Inventories.class);
        schematics = persist.load(Schematics.class);
        commands = persist.load(Commands.class);
        blockValues = persist.load(BlockValues.class);
        shop = persist.load(Shop.class);
        border = persist.load(Border.class);
        stackable = persist.load(Stackable.class);
        bank = persist.load(Bank.class);

        islandUpgrades.clear();
        islandBoosters.clear();
        bankItems.clear();

        registerUpgrade(getUpgrades().islandSizeUpgrade);
        registerUpgrade(getUpgrades().islandMemberUpgrade);
        registerUpgrade(getUpgrades().islandOresUpgrade);
        registerUpgrade(getUpgrades().islandWarpUpgrade);
        registerUpgrade(getUpgrades().islandBlockLimitUpgrade);

        registerBooster(getBoosters().islandFlightBooster);
        registerBooster(getBoosters().islandSpawnerBooster);
        registerBooster(getBoosters().islandFarmingBooster);
        registerBooster(getBoosters().islandExperienceBooster);

        registerBankItem(bank.crystalsBankItem);
        registerBankItem(bank.experienceBankItem);
        registerBankItem(bank.vaultBankItem);

        commandManager = new CommandManager("island");
        commandManager.registerCommands();

        if (commandManager != null) {
            if (commandManager.commands.contains(getCommands().shopCommand)) {
                if (!configuration.islandShop)
                    commandManager.unRegisterCommand(getCommands().shopCommand);
            } else {
                if (configuration.islandShop)
                    commandManager.registerCommand(getCommands().shopCommand);
            }
        }

        blockValues.blockvalue.remove(XMaterial.AIR);

        oreUpgradeCache.clear();
        for (int i : upgrades.islandOresUpgrade.upgrades.keySet()) {
            ArrayList<XMaterial> items = new ArrayList<>();
            for (String item : ((Upgrades.IslandOreUpgrade) upgrades.islandOresUpgrade.getIslandUpgrade(i)).ores) {
                if (item != null) {
                    String[] itemData = item.split(":");
                    int amount = Integer.parseInt(itemData[1]);
                    for (int a = 0; a <= amount; a++) {
                        items.add(XMaterial.valueOf(itemData[0]));
                    }
                } else {
                    ((Upgrades.IslandOreUpgrade) upgrades.islandOresUpgrade.getIslandUpgrade(i)).ores.remove(null);
                }
            }
            oreUpgradeCache.put(i, items);
        }

        netherOreUpgradeCache.clear();
        for (int i : upgrades.islandOresUpgrade.upgrades.keySet()) {
            ArrayList<XMaterial> items = new ArrayList<>();
            for (String item : ((Upgrades.IslandOreUpgrade) upgrades.islandOresUpgrade.getIslandUpgrade(i)).netherores) {
                if (item != null) {
                    String[] itemData = item.split(":");
                    int amount = Integer.parseInt(itemData[1]);
                    for (int a = 0; a <= amount; a++) {
                        items.add(XMaterial.valueOf(itemData[0]));
                    }
                } else {
                    ((Upgrades.IslandOreUpgrade) upgrades.islandOresUpgrade.getIslandUpgrade(i)).netherores.remove(null);
                }
            }
            netherOreUpgradeCache.put(i, items);
        }

        int max = upgrades.islandSizeUpgrade.upgrades.values().stream().map(islandUpgrade -> islandUpgrade.size).sorted((a, b) -> b - a).collect(Collectors.toList()).get(0);
        if (configuration.distance <= max) {
            configuration.distance = max + 1;
        }

        if (configuration.missionRestart == MissionRestart.Instantly) {
            for (Island island : IslandManager.getLoadedIslands()) {
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
        Bukkit.getPluginManager().callEvent(new IridiumSkyblockReloadEvent());
    }

    public BlockData fromLegacy(Material material, byte data) {
        if (!legacy.containsKey(material.name() + data))
            legacy.put(material.name() + data, Bukkit.getUnsafe().fromLegacy(material, data));
        return legacy.get(material.name() + data);
    }

    public void saveData() {
        try (Connection connection = sqlManager.getConnection()) {
            for (User user : ImmutableList.copyOf(UserManager.cache.values())) {
                user.save(connection);
            }

            for (Island island : IslandManager.getLoadedIslands()) {
                island.save(connection);
                IslandDataManager.save(island, connection);
            }

            try (PreparedStatement insert = connection.prepareStatement("UPDATE islandmanager SET nextID = ?, length=?, current=?, direction=?, x=?,y=?;")) {
                insert.setInt(1, IslandManager.nextID);
                insert.setInt(2, IslandManager.length);
                insert.setInt(3, IslandManager.current);
                insert.setString(4, IslandManager.direction.name());
                insert.setDouble(5, IslandManager.nextLocation.getX());
                insert.setDouble(6, IslandManager.nextLocation.getZ());
                insert.executeUpdate();
            }

            connection.commit();
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
            if (bank != null) persist.save(bank);
        });
    }

    public List<XMaterial> getOreCache(int i) {
        return oreUpgradeCache.getOrDefault(i, Collections.singletonList(XMaterial.COBBLESTONE));
    }

    public List<XMaterial> getNetherOreCache(int i) {
        return netherOreUpgradeCache.getOrDefault(i, Collections.singletonList(XMaterial.COBBLESTONE));
    }

    public MultiplePagesGUI<VisitGUI> getVisitGUI() {
        return visitGUI;
    }

    public void registerBankItem(BankItem bankItem) {
        bankItems.add(bankItem);
    }

    public List<BankItem> getBankItems() {
        return bankItems;
    }

    public void registerBooster(Boosters.Booster booster) {
        islandBoosters.add(booster);
    }

    public List<Boosters.Booster> getIslandBoosters() {
        return islandBoosters;
    }

    public void registerUpgrade(Upgrades.Upgrade upgrade) {
        islandUpgrades.add(upgrade);
    }

    public List<Upgrades.Upgrade> getIslandUpgrades() {
        return islandUpgrades;
    }

    public Persist getPersist() {
        return persist;
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public WorldEdit getWorldEdit() {
        return worldEdit;
    }

    public Border getBorder() {
        return border;
    }

    public ShopMenuGUI getShopGUI() {
        return shopMenuGUI;
    }

    public TopGUI getTopGUI() {
        return topGUI;
    }

    public Shop getShop() {
        return shop;
    }

    public Stackable getStackable() {
        return stackable;
    }

    public BlockValues getBlockValues() {
        return blockValues;
    }

    public Commands getCommands() {
        return commands;
    }

    public Schematics getSchematics() {
        return schematics;
    }

    public Inventories getInventories() {
        return inventories;
    }

    public Boosters getBoosters() {
        return boosters;
    }

    public Upgrades getUpgrades() {
        return upgrades;
    }

    public Missions getMissions() {
        return missions;
    }

    public Messages getMessages() {
        return messages;
    }

    public Config getConfiguration() {
        return configuration;
    }

    public SQL getSql() {
        return sql;
    }

    public SpawnerSupport getSpawnerSupport() {
        return spawnerSupport;
    }

    public Economy getEconomy() {
        return economy;
    }

    public MultiplePagesGUI<LanguagesGUI> getLanguagesGUI() {
        return languagesGUI;
    }

    public String getLatest() {
        return latest;
    }

    public NMS getNms() {
        return nms;
    }

    public File getSchematicFolder() {
        return schematicFolder;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}