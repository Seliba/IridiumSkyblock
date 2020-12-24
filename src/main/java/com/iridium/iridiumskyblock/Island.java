package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.api.IslandWorthCalculatedEvent;
import com.iridium.iridiumskyblock.api.MissionCompleteEvent;
import com.iridium.iridiumskyblock.configs.BlockValues;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.configs.Messages;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.gui.BankGUI;
import com.iridium.iridiumskyblock.gui.BiomeGUI;
import com.iridium.iridiumskyblock.gui.BoosterGUI;
import com.iridium.iridiumskyblock.gui.BorderColorGUI;
import com.iridium.iridiumskyblock.gui.CoopGUI;
import com.iridium.iridiumskyblock.gui.IslandAdminGUI;
import com.iridium.iridiumskyblock.gui.IslandMenuGUI;
import com.iridium.iridiumskyblock.gui.MembersGUI;
import com.iridium.iridiumskyblock.gui.MissionsGUI;
import com.iridium.iridiumskyblock.gui.PermissionsGUI;
import com.iridium.iridiumskyblock.gui.SchematicSelectGUI;
import com.iridium.iridiumskyblock.gui.UpgradeGUI;
import com.iridium.iridiumskyblock.gui.VisitorGUI;
import com.iridium.iridiumskyblock.gui.WarpGUI;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.support.SpawnerSupport;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Island {

    public static class Warp {
        Location location;
        String name;
        String password;

        public Warp(Location location, String name, String password) {
            this.location = location;
            this.name = name;
            this.password = password;
        }

        public Location getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }
    }

    public String owner;
    public Set<String> members;
    public Location pos1;
    public Location pos2;
    public Location center;
    public Location home;

    public transient UpgradeGUI upgradeGUI;
    public transient BoosterGUI boosterGUI;
    public transient MissionsGUI missionsGUI;
    public transient MembersGUI membersGUI;
    public transient WarpGUI warpGUI;
    public transient BorderColorGUI borderColorGUI;
    public transient SchematicSelectGUI schematicSelectGUI;
    public transient PermissionsGUI permissionsGUI;
    public transient IslandMenuGUI islandMenuGUI;
    public transient IslandAdminGUI islandAdminGUI;
    public transient CoopGUI coopGUI;
    public transient BankGUI bankGUI;
    public transient BiomeGUI biomeGUI;
    public transient BiomeGUI netherBiomeGUI;
    public transient VisitorGUI visitorGUI;

    public int id;

    public int spawnerBooster;
    public int farmingBooster;
    public int expBooster;
    public int flightBooster;

    private transient int boosterId;

    public int crystals;

    public int sizeLevel;
    public int memberLevel;
    public int warpLevel;
    public int oreLevel;

    public transient int generateID;

    public double value;

    private double lastMissionValue;

    public double extravalue;

    public transient ConcurrentHashMap<String, Integer> valuableBlocks;
    public transient ConcurrentHashMap<String, Integer> spawners;
    public ConcurrentHashMap<Location, Integer> stackedBlocks;

    public final List<Warp> warps;

    private double startvalue;

    private Map<String, Integer> missions = new HashMap<>();

    private Map<String, Integer> missionLevels = new HashMap<>();

    public boolean visit;

    public Color borderColor;

    private Map<Role, Permissions> permissions;

    public String schematic;
    public String netherschematic;

    private Set<String> bans;

    private Set<String> votes;

    private Set<Integer> coop;

    public transient Set<Integer> coopInvites;

    public String name;

    public double money;
    public int exp;

    public XBiome biome;

    public XBiome netherBiome;

    public transient Set<Location> failedGenerators;
    public transient int interestCrystal;
    public transient int interestExp;
    public transient double interestMoney;

    private Date lastRegen;

    private transient Set<Player> playersOnIsland;
    private long lastPlayerCaching;

    private static final transient boolean IS_FLAT = XMaterial.supports(13);
    private static transient Method getMaterial;
    private static transient Method getBlock;

    static {
        try {
            getMaterial = Material.class.getMethod("getMaterial", int.class);
            getBlock = ChunkSnapshot.class.getMethod("getBlockTypeId", int.class, int.class, int.class);
        } catch (NoSuchMethodException e) {
            getMaterial = null;
            getBlock = null;
        }
    }

    public Island(Player owner, Location pos1, Location pos2, Location center, Location home, int id) {
        User user = User.getUser(owner);
        user.role = Role.Owner;
        this.biome = IridiumSkyblock.configuration.defaultBiome;
        this.netherBiome = XBiome.NETHER_WASTES;
        valuableBlocks = new ConcurrentHashMap<>();
        spawners = new ConcurrentHashMap<>();
        this.owner = user.player;
        this.name = user.name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.center = center;
        this.home = home;
        this.members = new HashSet<>(Collections.singletonList(user.player));
        this.id = id;
        spawnerBooster = 0;
        farmingBooster = 0;
        expBooster = 0;
        flightBooster = 0;
        crystals = 0;
        sizeLevel = 1;
        memberLevel = 1;
        warpLevel = 1;
        oreLevel = 1;
        value = 0;
        lastMissionValue = 0;
        warps = new ArrayList<>();
        startvalue = -1;
        borderColor = IridiumSkyblock.border.startingColor;
        visit = IridiumSkyblock.configuration.defaultIslandPublic;
        permissions = new HashMap<>(IridiumSkyblock.configuration.defaultPermissions);
        this.coop = new HashSet<>();
        this.bans = new HashSet<>();
        this.votes = new HashSet<>();
        init();
        Bukkit.getPluginManager().callEvent(new IslandCreateEvent(owner, this));
    }

    public void initBlocks() {
        if (!center.getWorld().isChunkLoaded(center.getBlockX() >> 4, center.getBlockZ() >> 4)) return;
        final IridiumSkyblock plugin = IridiumSkyblock.instance;
        final boolean nether = IridiumSkyblock.configuration.netherIslands;

        int minX = pos1.getChunk().getX();
        int minZ = pos1.getChunk().getZ();
        int maxX = pos2.getChunk().getX();
        int maxZ = pos2.getChunk().getZ();

        valuableBlocks.clear();
        spawners.clear();

        for (Location location : stackedBlocks.keySet()) {
            XMaterial xMaterial = XMaterial.matchXMaterial(location.getBlock().getType());
            valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                if (original == null) return stackedBlocks.get(location) - 1;
                return original + (stackedBlocks.get(location) - 1);
            });
            if (xMaterial == XMaterial.AIR || xMaterial == XMaterial.CAVE_AIR || xMaterial == XMaterial.VOID_AIR) {
                stackedBlocks.remove(location);
            }
        }
        Bukkit.getScheduler().runTask(IridiumSkyblock.instance, (Runnable) this::sendHolograms);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                //Update the nether world values
                if (nether) {
                    Chunk netherChunk = IslandManager.getNetherWorld().getChunkAt(x, z);
                    computeValue(plugin, x, z, netherChunk);
                }

                //Update overworld values
                Chunk chunk = IslandManager.getWorld().getChunkAt(x, z);
                computeValue(plugin, x, z, chunk);
            }
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.instance, this::calculateIslandValue, 20);
    }

    private void computeValue(IridiumSkyblock plugin, int finalX, int finalZ, Chunk chunk) {
        ChunkSnapshot netherSnapshot = chunk.getChunkSnapshot(true, false, false);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (int x1 = 0; x1 < 16; x1++) {
                for (int z1 = 0; z1 < 16; z1++) {
                    if (!isInIsland(x1 + (16 * finalX), z1 + (16 * finalZ)))
                        continue;
                    final int maxy = netherSnapshot.getHighestBlockYAt(x1, z1);
                    for (int y = 0; y < maxy; y++) {
                        final Material material;
                        if (IS_FLAT) {
                            material = netherSnapshot.getBlockType(x1, y, z1);
                        } else {
                            try {
                                material = (Material) getMaterial.invoke(null, getBlock.invoke(
                                    netherSnapshot, x1, y, z1));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        final XMaterial xMaterial = XMaterial.matchXMaterial(material);
                        if (Utils.isBlockValuable(xMaterial)) {
                            valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                                if (original == null) return 1;
                                return original + 1;
                            });
                        }
                    }
                }
            }
        });
    }

    public void resetMissions() {
        if (missions == null) missions = new HashMap<>();
        if (missionLevels == null) missionLevels = new HashMap<>();
        missions.clear();
        missionLevels.clear();
        lastMissionValue = value;
    }

    public int getMission(String mission) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        return missions.get(mission);
    }

    public void addMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, missions.get(mission) + amount);
        checkMissionStatus(mission);
    }

    public void setMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, amount);
        checkMissionStatus(mission);
    }

    private void checkMissionStatus(String mission) {
        for (Mission m : IridiumSkyblock.missions.missions) {
            if (m.name.equalsIgnoreCase(mission)) {
                if (!getMissionLevels().containsKey(mission)) getMissionLevels().put(mission, 1);
                if (m.levels.get(getMissionLevels().get(mission)).amount <= missions.get(mission)) {
                    completeMission(mission);
                }
            }
        }
    }

    public Permissions getPermissions(User user) {
        if (user.bypassing) return new Permissions();

        Role role;
        if (user.islandID == id) role = user.getRole();
        else if (isCoop(user.getIsland())) role = Role.Member;
        else role = Role.Visitor;
        return getPermissions(role);
    }

    public Permissions getPermissions(Role role) {
        if (permissions == null)
            permissions = new HashMap<>(IridiumSkyblock.configuration.defaultPermissions);
        if (!permissions.containsKey(role)) {
            permissions.put(role, new Permissions());
        }
        return permissions.get(role);
    }

    public void setBorderColor(Color color) {
        borderColor = color;
        sendBorder();
    }

    public void sendBorder() {
        for (Player p : getPlayersOnIsland()) {
            sendBorder(p);
        }
    }

    public void sendBorder(Player p) {
        double size = IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size;
        if (size % 2 == 0) size++;
        String worldName = p.getLocation().getWorld().getName();
        if (worldName.equals(IslandManager.getWorld().getName())) {
            IridiumSkyblock.nms.sendWorldBorder(p, borderColor, size, center);
        } else if (IridiumSkyblock.configuration.netherIslands && worldName
            .equals(IslandManager.getNetherWorld().getName())) {
            Location loc = center.clone();
            loc.setWorld(IslandManager.getNetherWorld());
            IridiumSkyblock.nms.sendWorldBorder(p, borderColor, size, loc);
        }
    }

    public void hideBorder() {
        for (Player p : getPlayersOnIsland()) {
            hideBorder(p);
        }
    }

    public void hideBorder(Player p) {
        IridiumSkyblock.nms.sendWorldBorder(p, borderColor, Integer.MAX_VALUE, center.clone());
    }

    public void completeMission(String missionName) {
        missionLevels.putIfAbsent(missionName, 1);

        final Config config = IridiumSkyblock.configuration;
        missions.put(missionName, (config.missionRestart == MissionRestart.Instantly ? 0 : Integer.MIN_VALUE));

        final Mission mission = IridiumSkyblock
                .missions
                .missions
                .stream()
                .filter(m -> m.name.equalsIgnoreCase(missionName))
                .findAny()
                .orElse(null);
        if (mission == null) return;

        final Map<Integer, MissionData> levels = mission.levels;
        final int levelProgress = missionLevels.get(missionName);
        final MissionData level = levels.get(levelProgress);
        final int crystalReward = level.crystalReward;
        final int vaultReward = level.vaultReward;
        this.crystals += crystalReward;
        this.money += vaultReward;
        Bukkit.getPluginManager().callEvent(new MissionCompleteEvent(this, missionName, level.type, levelProgress));
        final Messages messages = IridiumSkyblock.messages;
        final String titleMessage = messages.missionComplete
                .replace("%mission%", missionName)
                .replace("%level%", levelProgress + "");
        final String subTitleMessage = messages.rewards
                .replace("%crystalsReward%", crystalReward + "")
                .replace("%vaultReward%", vaultReward + "");
        for (String member : members) {
            final User user = User.getUser(member);
            final Player p = Bukkit.getPlayer(user.name);
            if (p == null) continue;
            IridiumSkyblock.nms.sendTitle(p, titleMessage, 20, 40, 20);
            IridiumSkyblock.nms.sendSubTitle(p, subTitleMessage, 20, 40, 20);
        }

        //Reset current mission status
        if (mission.levels.containsKey(levelProgress + 1)) {
            //We have another mission, put us on the next level
            missions.remove(missionName);
            missionLevels.put(missionName, levelProgress + 1);
        } else if (config.missionRestart == MissionRestart.Instantly) {
            missions.remove(missionName);
            missionLevels.remove(missionName);
        }
    }

    public void calculateIslandValue() {
        if (!center.getWorld().isChunkLoaded(center.getBlockX() >> 4, center.getBlockZ() >> 4)) return;
        if (valuableBlocks == null) valuableBlocks = new ConcurrentHashMap<>();
        if (spawners == null) spawners = new ConcurrentHashMap<>();

        final BlockValues blockValues = IridiumSkyblock.blockValues;
        final Map<XMaterial, Double> blockValueMap = blockValues.blockvalue;

        BigDecimal value = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : valuableBlocks.entrySet()) {
            final String item = entry.getKey();
            final BigDecimal amount = BigDecimal.valueOf(entry.getValue());
            if (amount.intValue() < 1) continue;
            final Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(item);
            if (!xmaterial.isPresent()) continue;

            if (!blockValueMap.containsKey(xmaterial.get())) continue;
            final BigDecimal blockValue = BigDecimal.valueOf(blockValueMap.get(xmaterial.get()));

            value = value.add(blockValue.multiply(amount));
        }

        final Config config = IridiumSkyblock.configuration;

        final Set<World> worlds = new HashSet<>();
        final World islandWorld = IslandManager.getWorld();
        worlds.add(islandWorld);

        if (config.netherIslands) {
            final World netherIslandWorld = IslandManager.getNetherWorld();
            worlds.add(netherIslandWorld);
        }

        final Chunk pos1Chunk = pos1.getChunk();
        final int minChunkX = pos1Chunk.getX();
        final int minChunkZ = pos1Chunk.getZ();

        final Chunk pos2Chunk = pos2.getChunk();
        final int maxChunkX = pos2Chunk.getX();
        final int maxChunkZ = pos2Chunk.getZ();

        final double minX = pos1.getX();
        final double minZ = pos1.getZ();
        final double maxX = pos2.getX();
        final double maxZ = pos2.getZ();

        final Map<String, Double> spawnerValueMap = blockValues.spawnervalue;

        Function<CreatureSpawner, Integer> getSpawnerAmount;
        SpawnerSupport spawnerSupport = IridiumSkyblock.instance.spawnerSupport;
        if (spawnerSupport != null) {
            getSpawnerAmount = spawnerSupport::getSpawnerAmount;
        } else {
            getSpawnerAmount = null;
        }

        spawners.clear();

        for (World world : worlds) {
            for (int X = minChunkX; X <= maxChunkX; X++) {
                for (int Z = minChunkZ; Z <= maxChunkZ; Z++) {
                    final Chunk chunk = world.getChunkAt(X, Z);
                    for (BlockState state : chunk.getTileEntities()) {
                        if (!isInIsland(state.getLocation())) continue;
                        if (!(state instanceof CreatureSpawner)) continue;

                        final CreatureSpawner spawner = (CreatureSpawner) state;
                        final Location location = spawner.getLocation();
                        final double x = location.getX();
                        final double z = location.getZ();
                        if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                        final EntityType type = spawner.getSpawnedType();
                        final String typeName = type.name();
                        if (!spawnerValueMap.containsKey(typeName)) continue;
                        final BigDecimal spawnerValue = BigDecimal.valueOf(spawnerValueMap.get(typeName));

                        final BigDecimal amount = (getSpawnerAmount == null) ? BigDecimal.ONE : BigDecimal.valueOf(getSpawnerAmount.apply(spawner));
                        spawners.compute(typeName, (name, original) -> {
                            if (original == null) return amount.intValue();
                            return original + amount.intValue();
                        });

                        value = value.add(spawnerValue.multiply(amount));
                    }
                }
            }
        }

        this.value = value.doubleValue();
        if (startvalue == -1) startvalue = value.doubleValue();

        for (Mission mission : IridiumSkyblock.missions.missions) {
            missionLevels.putIfAbsent(mission.name, 1);
            if (mission.levels.get(missionLevels.get(mission.name)).type == MissionType.VALUE_INCREASE) {
                setMission(mission.name, (int) (value.doubleValue() - startvalue - lastMissionValue));
            }
        }
        this.value += this.extravalue;
        if (IridiumSkyblock.configuration.islandMoneyPerValue != 0)
            this.value += this.money / IridiumSkyblock.configuration.islandMoneyPerValue;

        IslandWorthCalculatedEvent islandWorthCalculatedEvent = new IslandWorthCalculatedEvent(this, this.value);
        Bukkit.getPluginManager().callEvent(islandWorthCalculatedEvent);
        this.value = islandWorthCalculatedEvent.islandWorth;
        IslandDataManager.save(this, true);
    }

    public void addWarp(Player player, Location location, String name, String password) {
        if (warps.size() < IridiumSkyblock.upgrades.warpUpgrade.upgrades.get(warpLevel).size) {
            warps.add(new Warp(location, name, password));
            player.sendMessage(Utils.color(IridiumSkyblock.messages.warpAdded.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        } else {
            player.sendMessage(Utils.color(IridiumSkyblock.messages.maxWarpsReached.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
    }

    public void addUser(User user) {
        if (members.size() < IridiumSkyblock.upgrades.memberUpgrade.upgrades.get(memberLevel).size) {

            for (String player : members) {
                User u = User.getUser(player);
                Player p = Bukkit.getPlayer(u.name);
                if (p != null) {
                    p.sendMessage(Utils.color(IridiumSkyblock.messages.playerJoinedYourIsland.replace("%player%", user.name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
            bans.remove(user.player);
            user.islandID = id;
            user.role = Role.Member;
            user.invites.clear();
            members.add(user.player);
            teleportHome(Bukkit.getPlayer(user.name));
        } else {
            Player player = Bukkit.getPlayer(user.name);
            if (player != null) {
                player.sendMessage(Utils.color(IridiumSkyblock.messages.maxMemberCount.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        membersGUI.getInventory().clear();
        membersGUI.addContent();
    }

    public void removeUser(User user) {
        user.islandID = 0;
        Player player = Bukkit.getPlayer(user.name);
        if (player != null) {
            spawnPlayer(player);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        members.remove(user.player);
        user.role = Role.Visitor;
        for (String member : members) {
            User u = User.getUser(member);
            Player p = Bukkit.getPlayer(u.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.messages.kickedMember.replace("%member%", user.name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        membersGUI.getInventory().clear();
        membersGUI.addContent();
    }

    public boolean isInIsland(Location location) {
        if (location == null) return false;
        return isInIsland(location.getX(), location.getZ());
    }

    public boolean isInIsland(double x, double z) {
        return x >= pos1.getX()
                && x <= pos2.getX()
                && z >= pos1.getZ()
                && z <= pos2.getZ();
    }

    public void init() {
        if (netherschematic == null) {
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.schematics.schematics) {
                if (fakeSchematic.name.equals(schematic)) {
                    netherschematic = fakeSchematic.netherisland;
                }
            }
        }
        if (biome == null) biome = IridiumSkyblock.configuration.defaultBiome;
        if (valuableBlocks == null) valuableBlocks = new ConcurrentHashMap<>();
        if (spawners == null) spawners = new ConcurrentHashMap<>();
        if (stackedBlocks == null) stackedBlocks = new ConcurrentHashMap<>();
        if (members == null) {
            members = new HashSet<>();
            members.add(owner);
        }
        this.playersOnIsland = new HashSet<>();
        this.lastPlayerCaching = 0L;
        upgradeGUI = new UpgradeGUI(this);
        boosterGUI = new BoosterGUI(this);
        missionsGUI = new MissionsGUI(this);
        membersGUI = new MembersGUI(this);
        warpGUI = new WarpGUI(this);
        borderColorGUI = new BorderColorGUI(this);
        schematicSelectGUI = new SchematicSelectGUI(this);
        permissionsGUI = new PermissionsGUI(this);
        islandMenuGUI = new IslandMenuGUI(this);
        islandAdminGUI = new IslandAdminGUI(this);
        coopGUI = new CoopGUI(this);
        bankGUI = new BankGUI(this);
        biomeGUI = new BiomeGUI(this, World.Environment.NORMAL);
        netherBiomeGUI = new BiomeGUI(this, World.Environment.NETHER);
        visitorGUI = new VisitorGUI(this);

        failedGenerators = new HashSet<>();
        coopInvites = new HashSet<>();
        boosterId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.instance, () -> {
            if (spawnerBooster > 0) spawnerBooster--;
            if (farmingBooster > 0) farmingBooster--;
            if (expBooster > 0) expBooster--;
            if (flightBooster == 1) {
                for (String player : members) {
                    Player p = Bukkit.getPlayer(player);
                    if (p != null) {
                        if ((!p.hasPermission("IridiumSkyblock.Fly") && !p.hasPermission("iridiumskyblock.fly")) && p.getGameMode().equals(GameMode.SURVIVAL)) {
                            p.setAllowFlight(false);
                            p.setFlying(false);
                            User.getUser(p).flying = false;
                        }
                    }
                }
            }
            if (flightBooster > 0) flightBooster--;
        }, 0, 20);
        if (permissions == null) {
            permissions = new HashMap<Role, Permissions>() {{
                for (Role role : Role.values()) {
                    put(role, new Permissions());
                }
            }};
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.instance, (Runnable) this::sendBorder, 20);
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.instance, (Runnable) this::sendHolograms, 20);
    }

    public long canGenerate() {
        if (lastRegen == null) return 0;
        if (new Date().after(lastRegen)) return 0;
        return lastRegen.getTime() - System.currentTimeMillis();
    }

    public void teleportPlayersHome() {
        for (Player p : getPlayersOnIsland()) {
            teleportHome(p);
        }
    }

    public void pasteSchematic(boolean deleteBlocks) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, IridiumSkyblock.configuration.regenCooldown);
        lastRegen = c.getTime();
        if (deleteBlocks) deleteBlocks();
        pasteSchematic();
        killEntities();
        //Reset island home
        for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
            if (!schematic.name.equals(this.schematic)) continue;
            home = new Location(IslandManager.getWorld(), center.getX() + schematic.x, schematic.y, center.getZ() + schematic.z);
            this.setBiome(schematic.biome);
        }
    }

    public void pasteSchematic(Player player, boolean deleteBlocks) {
        pasteSchematic(deleteBlocks);
        User.getUser(player).teleportingHome = false;
        teleportHome(player);
        sendBorder(player);
        IridiumSkyblock.nms.sendTitle(player, IridiumSkyblock.messages.islandCreated, 20, 40, 20);
        if (!IridiumSkyblock.messages.islandCreatedSubtitle.isEmpty())
            IridiumSkyblock.nms.sendSubTitle(player, IridiumSkyblock.messages.islandCreatedSubtitle, 20, 40, 20);
    }

    private void pasteSchematic() {
        stackedBlocks.clear();
        for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.schematics.schematics) {
            if (!fakeSchematic.name.equals(this.schematic)) continue;
            IridiumSkyblock.worldEdit.paste(new File(IridiumSkyblock.schematicFolder, schematic), center.clone().add(fakeSchematic.xOffset, fakeSchematic.yOffset, fakeSchematic.zOffset), this);
            Location center = this.center.clone();
            if (IridiumSkyblock.configuration.netherIslands) {
                center.setWorld(IslandManager.getNetherWorld());
                IridiumSkyblock.worldEdit.paste(new File(IridiumSkyblock.schematicFolder, netherschematic), center.clone().add(fakeSchematic.xNetherOffset, fakeSchematic.yNetherOffset, fakeSchematic.zNetherOffset), this);
            }
        }
    }

    public void clearInventories() {
        if (IridiumSkyblock.configuration.clearInventories) {
            for (String player : members) {
                User user = User.getUser(player);
                Player p = Bukkit.getPlayer(user.name);
                if (p != null) {
                    p.getInventory().clear();
                    if (IridiumSkyblock.configuration.clearEnderChests) p.getEnderChest().clear();
                }
            }
        }
    }

    public void sendHolograms(Player player) {
        World world = player.getWorld();
        User user = User.getUser(player);
        for (Object object : user.getHolograms()) {
            IridiumSkyblock.nms.removeHologram(player, object);
        }
        user.clearHolograms();
        for (Location location : stackedBlocks.keySet()) {
            if (location.getWorld() != world) continue;
            Block block = location.getBlock();
            int amount = stackedBlocks.get(location);
            IridiumSkyblock.nms.sendHologram(player, block.getLocation().add(0.5, -0.5, 0.5), Utils.processMultiplePlaceholders(IridiumSkyblock.messages.stackedBlocksHologram, Arrays.asList(new Utils.Placeholder("amount", amount + ""), new Utils.Placeholder("block", XMaterial.matchXMaterial(block.getType()).toString()))));
        }
    }

    public void sendHolograms() {
        for (Player player : getPlayersOnIsland()) {
            sendHolograms(player);
        }
    }

    public void teleportHome(Player p) {
        if (home == null) home = center;
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString()) && !p.hasPermission("iridiumskyblock.visitbypass")) {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.bannedFromIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            return;
        }
        if (schematic == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.schematics.schematics.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
                        this.schematic = schematic.name;
                        this.netherschematic = schematic.netherisland;
                    }
                } else {
                    p.openInventory(schematicSelectGUI.getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.teleportingHome.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        }
        if (Utils.isSafe(home, this)) {
            p.teleport(home);
            sendBorder(p);
        } else {
            Location loc = Utils.getNewHome(this, this.home);
            if (loc != null) {
                this.home = loc;
                p.teleport(this.home);
                sendBorder(p);
            } else {
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, true);
            }
        }
    }

    public void teleportNetherHome(Player p) {
        Location netherHome = center.clone();
        netherHome.setWorld(IslandManager.getNetherWorld());
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.bannedFromIsland.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            return;
        }
        if (schematic == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.schematics.schematics.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.schematics.schematics) {
                        this.schematic = schematic.name;
                    }
                } else {
                    p.openInventory(schematicSelectGUI.getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.teleportingHome.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.messages.visitingIsland.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            for (String pl : members) {
                Player player = Bukkit.getPlayer(User.getUser(pl).name);
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.messages.visitedYourIsland.replace("%player%", p.getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
                }
            }
        }
        if (Utils.isSafe(getNetherHome(), this)) {
            p.teleport(getNetherHome());
            sendBorder(p);
        } else {

            Location loc = Utils.getNewHome(this, netherHome);
            if (loc != null) {
                p.teleport(netherHome);
                sendBorder(p);
            } else {
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, false);
            }
        }
    }

    public void delete() {
        Bukkit.getPluginManager().callEvent(new IslandDeleteEvent(this));

        Bukkit.getScheduler().cancelTask(membersGUI.scheduler);
        Bukkit.getScheduler().cancelTask(boosterGUI.scheduler);
        Bukkit.getScheduler().cancelTask(missionsGUI.scheduler);
        Bukkit.getScheduler().cancelTask(upgradeGUI.scheduler);
        Bukkit.getScheduler().cancelTask(warpGUI.scheduler);
        Bukkit.getScheduler().cancelTask(permissionsGUI.scheduler);
        Bukkit.getScheduler().cancelTask(islandMenuGUI.scheduler);
        Bukkit.getScheduler().cancelTask(islandAdminGUI.scheduler);
        Bukkit.getScheduler().cancelTask(coopGUI.scheduler);
        Bukkit.getScheduler().cancelTask(bankGUI.scheduler);
        if (generateID != -1) Bukkit.getScheduler().cancelTask(generateID);
        permissions.clear();
        spawnPlayers();
        for (String player : members) {
            User.getUser(player).islandID = 0;
            Player p = Bukkit.getPlayer(User.getUser(player).name);
            if (p != null) {
                p.closeInventory();
                p.sendMessage(Utils.color(IridiumSkyblock.messages.islandDeleted.replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        killEntities();
        deleteBlocks();
        for (int id : coop) {
            IslandManager.getIslandViaId(id).coop.remove(id);
        }
        coop = null;
        hideBorder();
        this.owner = null;
        this.pos1 = null;
        this.pos2 = null;
        this.members = null;
        this.center = null;
        this.home = null;
        IslandManager.removeIsland(this);
        this.id = 0;
        IridiumSkyblock.instance.saveConfigs();
        Bukkit.getScheduler().cancelTask(boosterId);
        boosterId = -1;
    }

    public void removeBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.remove(user.player);
    }

    public void addBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.add(user.player);
    }

    public void removeVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.remove(user.player);
        IslandDataManager.save(this, true);
    }

    public void addVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.add(user.player);
        IslandDataManager.save(this, true);
    }

    public boolean hasVoted(User user) {
        if (votes == null) votes = new HashSet<>();
        return votes.contains(user.player);
    }

    public int getVotes() {
        if (votes == null) votes = new HashSet<>();
        return votes.size();
    }

    public boolean isBanned(User user) {
        if (bans == null) bans = new HashSet<>();
        return bans.contains(user.player) && !user.bypassing;
    }

    public void addCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        for (String member : island.members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.messages.coopGiven.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        for (String member : members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.messages.coopAdded.replace("%player%", User.getUser(island.owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        coop.add(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.add(id);
    }

    public void inviteCoop(Island island) {
        if (coopInvites == null) coopInvites = new HashSet<>();
        coopInvites.add(island.id);
        for (String member : members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                BaseComponent[] components = TextComponent.fromLegacyText(Utils.color(IridiumSkyblock.messages.coopInvite.replace("%player%", User.getUser(island.owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));

                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is coop " + User.getUser(island.owner).name);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumSkyblock.messages.coopHoverMessage).create());
                for (BaseComponent component : components) {
                    component.setClickEvent(clickEvent);
                    component.setHoverEvent(hoverEvent);
                }
                pl.getPlayer().spigot().sendMessage(components);
            }
        }
    }

    public void removeCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.remove(id);
        for (String member : island.members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.messages.coopTaken.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        for (String member : members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.messages.coopTaken.replace("%player%", User.getUser(island.owner).name).replace("%prefix%", IridiumSkyblock.configuration.prefix)));
            }
        }
        coopGUI.getInventory().clear();
        coopGUI.addContent();
        island.coopGUI.getInventory().clear();
        island.coopGUI.addContent();
    }

    public void removeCoop(int id) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(id);
    }

    public boolean isCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        if (island == null) return false;
        return coop.contains(island.id);
    }

    public Set<Integer> getCoop() {
        if (coop == null) coop = new HashSet<>();
        return coop;
    }

    public void spawnPlayers() {
        for (Player p : getPlayersOnIsland()) {
            spawnPlayer(p);
        }
    }

    public Set<Player> getPlayersOnIsland() {
        if (System.currentTimeMillis() >= lastPlayerCaching + (IridiumSkyblock.configuration.playersOnIslandRefreshTime * 1000L)) {
            reloadPlayersOnIsland();
        }
        return playersOnIsland;
    }

    public void reloadPlayersOnIsland() {
        lastPlayerCaching = System.currentTimeMillis();
        playersOnIsland.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isInIsland(p.getLocation())) {
                playersOnIsland.add(p);
            }
        }
    }

    public void spawnPlayer(Player player) {
        if (player == null) return;
        if (Bukkit.getPluginManager().isPluginEnabled("EssentialsSpawn")) {
            EssentialsSpawn essentialsSpawn = (EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn");
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            player.teleport(essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()));
        } else {
            World world = Bukkit.getWorld(IridiumSkyblock.configuration.worldSpawn);
            if (world == null) world = Bukkit.getWorlds().get(0);
            player.teleport(world.getSpawnLocation());
        }
        playersOnIsland.remove(player);
    }

    public void setBiome(XBiome biome) {
        this.biome = biome;
        final World world = IslandManager.getWorld();
        final List<Chunk> chunks = new ArrayList<Chunk>() {{
            for (int X = pos1.getChunk().getX(); X <= pos2.getChunk().getX(); X++) {
                for (int Z = pos1.getChunk().getZ(); Z <= pos2.getChunk().getZ(); Z++) {
                    add(world.getChunkAt(X, Z));
                }
            }
        }};
        biome.setBiome(pos1, pos2).thenRunAsync(() -> {
            for (Chunk c : chunks) {
                for (Player p : world.getPlayers()) {
                    if (p.getLocation().getWorld() == world) {
                        IridiumSkyblock.nms.sendChunk(p, c);
                    }
                }
            }
        });
    }

    public void setNetherBiome(XBiome biome) {
        if (!IridiumSkyblock.configuration.netherIslands) return;
        this.netherBiome = biome;
        final World world = IslandManager.getNetherWorld();
        Location pos1 = this.pos1;
        Location pos2 = this.pos2;
        pos1.setWorld(world);
        pos2.setWorld(world);
        biome.setBiome(pos1, pos2).thenRunAsync(() -> {
            for (int X = pos1.getChunk().getX(); X <= pos2.getChunk().getX(); X++) {
                for (int Z = pos1.getChunk().getZ(); Z <= pos2.getChunk().getZ(); Z++) {
                    for (Player p : world.getPlayers()) {
                        if (p.getLocation().getWorld() == world) {
                            IridiumSkyblock.nms.sendChunk(p, world.getChunkAt(X, Z));
                        }
                    }
                }
            }
        });
    }

    public void deleteBlocks() {
        clearInventories();
        valuableBlocks.clear();
        calculateIslandValue();
        final World world = IslandManager.getWorld();
        final World nether = IslandManager.getNetherWorld();
        setBlock(world);
        if (IridiumSkyblock.configuration.netherIslands) {
            setBlock(nether);
        }
    }

    private void setBlock(World world) {
        for (int X = pos1.getBlockX(); X <= pos2.getBlockX(); X++) {
            for (int Y = 0; Y <= 255; Y++) {
                for (int Z = pos1.getBlockZ(); Z <= pos2.getBlockZ(); Z++) {
                    IridiumSkyblock.nms.setBlockFast(world.getBlockAt(X, Y, Z), 0, (byte) 0);
                }
            }
        }
    }

    public void killEntities() {
        for (Entity entity : IslandManager.getWorld().getNearbyEntities(center, IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 255, IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00)) {
            if (!entity.getType().equals(EntityType.PLAYER)) {
                entity.remove();
            }
        }
        if (IridiumSkyblock.configuration.netherIslands) {
            Location netherCenter = center.clone();
            netherCenter.setWorld(IslandManager.getNetherWorld());

            for (Entity entity : IslandManager.getNetherWorld().getNearbyEntities(netherCenter, IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 255, IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00)) {
                if (!entity.getType().equals(EntityType.PLAYER)) {
                    entity.remove();
                }
            }
        }
    }

    public void addExtraValue(double amount) {
        this.extravalue += amount;
    }

    public void removeExtraValue(double amount) {
        this.extravalue -= amount;
    }


    public Location getNetherHome() {
        Location netherHome = center.clone();
        netherHome.setWorld(IslandManager.getNetherWorld());
        return netherHome;
    }

    public void setOwner(OfflinePlayer owner) {
        for (String player : members) {
            User user = User.getUser(player);
            Player p = Bukkit.getPlayer(user.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.messages.transferdOwnership.replace("%player%", owner.getName()).replace("%prefix%", IridiumSkyblock.configuration.prefix)));

            }
        }
        User.getUser(owner).role = Role.CoOwner;
        this.owner = owner.getUniqueId().toString();
        User.getUser(owner).role = Role.Owner;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;

        pos1 = center.clone().subtract(IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        pos2 = center.clone().add(IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.upgrades.sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        sendBorder();
        setBiome(biome);
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public String getName() {
        if (name == null) name = User.getUser(owner).name;
        return name;
    }

    public Map<String, Integer> getMissionLevels() {
        if (missionLevels == null) missionLevels = new HashMap<>();
        return missionLevels;
    }

    public String getFormattedValue() {
        return Utils.NumberFormatter.format(value);
    }

    public String getFormattedMoney() {
        return Utils.NumberFormatter.format(money);
    }

    public String getFormattedExp() {
        return Utils.NumberFormatter.format(exp);
    }

    public String getFormattedCrystals() {
        return Utils.NumberFormatter.format(crystals);
    }

    public void save(boolean async) {
        IslandManager.save(this, async);
    }
}
