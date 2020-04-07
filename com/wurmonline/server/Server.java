// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.items.TradingWindow;
import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.intra.TimeTransfer;
import com.wurmonline.server.intra.MoneyTransfer;
import coffee.keenan.network.wrappers.upnp.UPNPService;
import javax.annotation.Nonnull;
import com.wurmonline.math.TilePos;
import com.wurmonline.server.zones.ZonesUtility;
import com.wurmonline.communication.SimpleConnectionListener;
import com.wurmonline.server.players.HackerIp;
import com.wurmonline.communication.SocketConnection;
import java.util.ListIterator;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.economy.LocalSupplyDemand;
import com.wurmonline.server.creatures.VisionArea;
import com.wurmonline.server.players.PlayerInfo;
import java.util.Iterator;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.zones.AreaSpellEffect;
import com.wurmonline.server.statistics.ChallengePointEnum;
import com.wurmonline.server.combat.Battles;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.questions.Questions;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.zones.ErrorChecks;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.players.AwardLadder;
import com.wurmonline.server.spells.SpellResist;
import com.wurmonline.server.epic.ValreiMapData;
import com.wurmonline.server.skills.SkillStat;
import com.wurmonline.server.intra.MountTransfer;
import java.util.HashSet;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.combat.Arrows;
import com.wurmonline.server.creatures.Offspring;
import com.wurmonline.server.combat.ServerProjectile;
import java.io.IOException;
import com.wurmonline.server.behaviours.TileBehaviour;
import com.wurmonline.server.behaviours.TileRockBehaviour;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.support.Trello;
import com.wurmonline.server.items.DbItem;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.intra.TimeSync;
import com.wurmonline.server.deities.DbRitual;
import com.wurmonline.server.zones.WaterType;
import com.wurmonline.server.statistics.ChallengeSummary;
import com.wurmonline.server.epic.EpicXmlWriter;
import com.wurmonline.server.tutorial.TriggerEffects;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.tutorial.Missions;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import com.wurmonline.server.villages.VillageMessages;
import com.wurmonline.server.skills.AffinitiesTimed;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.items.Recipes;
import com.wurmonline.server.players.PlayerVotes;
import com.wurmonline.server.support.VoteQuestions;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.epic.Effectuator;
import com.wurmonline.server.players.Cultist;
import com.wurmonline.server.items.WurmMail;
import com.wurmonline.server.statistics.Statistics;
import com.wurmonline.server.epic.Hota;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.webinterface.WcEpicKarmaCommand;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.players.AchievementGenerator;
import com.wurmonline.server.zones.Trap;
import com.wurmonline.server.intra.PasswordTransfer;
import com.wurmonline.server.players.PendingAccount;
import com.wurmonline.server.players.Reimbursement;
import com.wurmonline.server.zones.Dens;
import com.wurmonline.server.skills.Affinities;
import com.wurmonline.server.players.WurmRecord;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.villages.RecruitmentAds;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.CreaturePos;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.creatures.Delivery;
import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.zones.TilePoller;
import com.wurmonline.server.spells.Cooldowns;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.banks.Banks;
import com.wurmonline.server.combat.WeaponCreator;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.items.ItemRequirement;
import com.wurmonline.server.items.CoinDbStrings;
import com.wurmonline.server.items.ItemDbStrings;
import com.wurmonline.server.items.DbStrings;
import com.wurmonline.server.items.BodyDbStrings;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.creatures.MineDoorSettings;
import com.wurmonline.server.structures.StructureSettings;
import com.wurmonline.server.structures.DoorSettings;
import com.wurmonline.server.items.ItemSettings;
import com.wurmonline.server.creatures.AnimalSettings;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.batchjobs.PlayerBatchJob;
import com.wurmonline.server.zones.CropTilePoller;
import com.wurmonline.server.utils.DbIndexManager;
import com.wurmonline.server.loot.LootTableCreator;
import com.wurmonline.server.creatures.CreatureTemplateCreator;
import com.wurmonline.server.spells.SpellGenerator;
import com.wurmonline.server.items.ItemTemplateCreator;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.shared.exceptions.WurmServerException;
import java.util.concurrent.Executors;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.ArrayList;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.players.Player;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.steam.SteamHandler;
import com.wurmonline.server.highways.HighwayFinder;
import com.wurmonline.server.zones.Water;
import com.wurmonline.server.players.PlayerCommunicatorSender;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import com.wurmonline.server.intra.IntraCommand;
import com.wurmonline.server.intra.IntraServer;
import com.wurmonline.server.weather.Weather;
import com.wurmonline.server.players.PendingAward;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;
import com.wurmonline.server.behaviours.TerraformingTask;
import com.wurmonline.server.webinterface.WebCommand;
import java.util.Set;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.epic.HexMap;
import com.wurmonline.mesh.MeshIO;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.communication.SocketServer;
import com.wurmonline.server.epic.EpicMapListener;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.communication.ServerListener;
import java.util.TimerTask;

public final class Server extends TimerTask implements Runnable, ServerMonitoring, ServerListener, CounterTypes, MiscConstants, CreatureTemplateIds, TimeConstants, EpicMapListener
{
    private SocketServer socketServer;
    private boolean isPS;
    private static final Logger logger;
    private static Server instance;
    private static boolean EpicServer;
    private static boolean ChallengeServer;
    public static final Random rand;
    public static final Object SYNC_LOCK;
    public static final long SLEEP_TIME = 25L;
    private static final long LIGHTNING_INTERVAL = 5000L;
    private static final long DIRTY_MESH_ROW_SAVE_INTERVAL = 60000L;
    private static final long SKILL_POLL_INTERVAL = 21600000L;
    private static final long MACROING_RESET_INTERVAL = 14400000L;
    private static final long ARROW_POLL_INTERVAL = 100L;
    private static final long MAIL_POLL_INTERVAL = 364000L;
    private static final long RUBBLE_POLL_INTERVAL = 60000L;
    private static final long WATER_POLL_INTERVAL = 1000L;
    private static final float STORM_RAINY_THRESHOLD = 0.5f;
    private static final float STORM_CLOUDY_THRESHOLD = 0.5f;
    private static final long WEATHER_SET_INTERVAL = 70000L;
    private static short counter;
    private List<Long> playersAtLogin;
    private static final ReentrantReadWriteLock PLAYERS_AT_LOGIN_RW_LOCK;
    private static boolean locked;
    private static short molRehanX;
    private static short molRehanY;
    private static int newPremiums;
    private static int expiredPremiums;
    private static long lastResetNewPremiums;
    private static long lastPolledSupplyDepots;
    private static long savedChallengePage;
    private static int oldPremiums;
    private static long lastResetOldPremiums;
    public static MeshIO surfaceMesh;
    public static MeshIO caveMesh;
    public static MeshIO resourceMesh;
    public static MeshIO rockMesh;
    public static HexMap epicMap;
    private static MeshIO flagsMesh;
    private static final int bitBonatize = 128;
    private static final int bitForage = 64;
    private static final int bitGather = 32;
    private static final int bitInvestigate = 16;
    private static final int bitGrubs = 2048;
    private static final int bitHiveCheck = 1024;
    private static final int bitBeingTransformed = 512;
    private static final int bitTransformed = 256;
    private boolean needSeeds;
    private static List<Creature> creaturesToRemove;
    private static final ReentrantReadWriteLock CREATURES_TO_REMOVE_RW_LOCK;
    private static final Set<WebCommand> webcommands;
    private static final Set<TerraformingTask> terraformingTasks;
    public static final ReentrantReadWriteLock TERRAFORMINGTASKS_RW_LOCK;
    public static final ReentrantReadWriteLock WEBCOMMANDS_RW_LOCK;
    public static int lagticks;
    public static float lastLagticks;
    public static int lagMoveModifier;
    private static int lastSentWarning;
    private static long lastAwardedBattleCamps;
    private static long startTime;
    private static long lastSecond;
    private static long lastPolledRubble;
    private static long lastPolledShopCultist;
    private static Map<String, Boolean> ips;
    private static ConcurrentLinkedQueue<PendingAward> pendingAwards;
    private static int numips;
    private static int logons;
    private static int logonsPrem;
    private static int newbies;
    private static volatile long millisToShutDown;
    private static long lastPinged;
    private static long lastDeletedPlayer;
    private static long lastLoweredRanks;
    private static volatile String shutdownReason;
    private static List<Long> finalLogins;
    private static final ReentrantReadWriteLock FINAL_LOGINS_RW_LOCK;
    private static boolean pollCommunicators;
    public static final int VILLAGE_POLL_MOD = 4000;
    private long lastTicked;
    private static long lastWeather;
    private static long lastArrow;
    private static long lastMailCheck;
    private static long lastFaith;
    private static long lastRecruitmentPoll;
    private static long lastAwardedItems;
    private static int lostConnections;
    private long nextTerraformPoll;
    private static int totalTicks;
    private static int commPollCounter;
    private static int commPollCounterInit;
    private long lastLogged;
    private static long lastPolledBanks;
    private static long lastPolledWater;
    private static long lastPolledHighwayFinder;
    private byte[] externalIp;
    private byte[] internalIp;
    private static final Weather weather;
    private boolean thunderMode;
    private long lastFlash;
    private IntraServer intraServer;
    private final List<IntraCommand> intraCommands;
    private static final ReentrantReadWriteLock INTRA_COMMANDS_RW_LOCK;
    private long lastClearedFaithGain;
    private static int exceptions;
    private static int secondsLag;
    public static String alertMessage1;
    public static long lastAlertMess1;
    public static String alertMessage2;
    public static long lastAlertMess2;
    public static String alertMessage3;
    public static long lastAlertMess3;
    public static String alertMessage4;
    public static long lastAlertMess4;
    public static long timeBetweenAlertMess1;
    public static long timeBetweenAlertMess2;
    public static long timeBetweenAlertMess3;
    public static long timeBetweenAlertMess4;
    private static long lastPolledSkills;
    private static long lastPolledRifts;
    private static long lastResetAspirations;
    private static long lastPolledTileEffects;
    private static long lastResetTiles;
    private static int combatCounter;
    private static int secondsUptime;
    private ScheduledExecutorService scheduledExecutorService;
    public static boolean allowTradeCheat;
    private ExecutorService mainExecutorService;
    private static final int EXECUTOR_SERVICE_NUMBER_OF_THREADS = 20;
    private static PlayerCommunicatorSender playerCommunicatorSender;
    private static boolean appointedSixThousand;
    static final double FMOD = 1.3571428060531616;
    static final double RMOD = 0.1666666716337204;
    public static int playersThroughTutorial;
    public Water waterThread;
    public HighwayFinder highwayFinderThread;
    private static Map<Integer, Short> lowDirtHeight;
    private static Set<Integer> newYearEffects;
    public SteamHandler steamHandler;
    private static final ConcurrentHashMap<Long, Long> tempEffects;
    
    public static Server getInstance() {
        while (Server.locked) {
            try {
                Thread.sleep(1000L);
                Server.logger.log(Level.INFO, "Thread sleeping 1 second waiting for server to start.");
            }
            catch (InterruptedException ex2) {}
        }
        if (Server.instance == null) {
            try {
                Server.locked = true;
                Server.instance = new Server();
                Server.locked = false;
            }
            catch (Exception ex) {
                Server.logger.log(Level.SEVERE, "Failed to create server instance... shutting down.", ex);
                System.exit(0);
            }
        }
        return Server.instance;
    }
    
    public void addCreatureToRemove(final Creature creature) {
        Server.CREATURES_TO_REMOVE_RW_LOCK.writeLock().lock();
        try {
            Server.creaturesToRemove.add(creature);
        }
        finally {
            Server.CREATURES_TO_REMOVE_RW_LOCK.writeLock().unlock();
        }
    }
    
    public void startShutdown(final int seconds, final String reason) {
        Server.millisToShutDown = seconds * 1000L;
        Server.shutdownReason = "Reason: " + reason;
        final int mins = seconds / 60;
        final int secs = seconds - mins * 60;
        final StringBuffer buf = new StringBuffer();
        if (mins > 0) {
            buf.append(mins + " minute");
            if (mins > 1) {
                buf.append("s");
            }
        }
        if (secs > 0) {
            if (mins > 0) {
                buf.append(" and ");
            }
            buf.append(secs + " seconds");
        }
        this.broadCastAlert("The server is shutting down in " + buf.toString() + ". " + Server.shutdownReason, true, (byte)0);
    }
    
    private void removeCreatures() {
        Server.CREATURES_TO_REMOVE_RW_LOCK.writeLock().lock();
        try {
            final Creature[] array;
            final Creature[] crets = array = Server.creaturesToRemove.toArray(new Creature[Server.creaturesToRemove.size()]);
            for (final Creature lCret : array) {
                if (lCret instanceof Player) {
                    Players.getInstance().logoutPlayer((Player)lCret);
                }
                else {
                    Creatures.getInstance().removeCreature(lCret);
                }
                Server.creaturesToRemove.remove(lCret);
            }
            if (Server.creaturesToRemove.size() > 0) {
                Server.logger.log(Level.WARNING, "Okay something is weird here. Deleting list. Debug more.");
                Server.creaturesToRemove = new ArrayList<Creature>();
            }
        }
        finally {
            Server.CREATURES_TO_REMOVE_RW_LOCK.writeLock().unlock();
        }
    }
    
    private Server() throws Exception {
        this.isPS = false;
        this.needSeeds = false;
        this.lastTicked = 0L;
        this.nextTerraformPoll = System.currentTimeMillis();
        this.lastLogged = 0L;
        this.externalIp = new byte[4];
        this.internalIp = new byte[4];
        this.thunderMode = false;
        this.lastFlash = 0L;
        this.intraCommands = new LinkedList<IntraCommand>();
        this.lastClearedFaithGain = 0L;
        this.waterThread = null;
        this.highwayFinderThread = null;
        this.steamHandler = new SteamHandler();
    }
    
    @Override
    public boolean isLagging() {
        return Server.lagticks >= 2000;
    }
    
    public void setExternalIp() {
        final StringTokenizer tokens = new StringTokenizer(Servers.localServer.EXTERNALIP, ".");
        int x = 0;
        while (tokens.hasMoreTokens()) {
            final String next = tokens.nextToken();
            this.externalIp[x] = (byte)(Object)Integer.valueOf(next);
            ++x;
        }
    }
    
    private void setInternalIp() {
        final StringTokenizer tokens = new StringTokenizer(Servers.localServer.INTRASERVERADDRESS, ".");
        int x = 0;
        while (tokens.hasMoreTokens()) {
            final String next = tokens.nextToken();
            this.internalIp[x] = (byte)(Object)Integer.valueOf(next);
            ++x;
        }
    }
    
    private void initialiseExecutorService(final int aNumberOfThreads) {
        Server.logger.info("Initialising ExecutorService with NumberOfThreads: " + aNumberOfThreads);
        this.mainExecutorService = Executors.newFixedThreadPool(aNumberOfThreads);
    }
    
    public ExecutorService getMainExecutorService() {
        return this.mainExecutorService;
    }
    
    public void startRunning() throws Exception {
        Constants.logConstantValues(false);
        addShutdownHook();
        this.logCodeVersionInformation();
        DbConnector.initialize();
        if (Constants.dbAutoMigrate) {
            if (DbConnector.hasPendingMigrations() && DbConnector.performMigrations().isError()) {
                throw new WurmServerException("Could not perform migrations successfully, they must either be performed manually or disabled.");
            }
        }
        else {
            Server.logger.info("Database auto-migration is not enabled - skipping migrations checks");
        }
        if (Constants.checkAllDbTables) {
            DbUtilities.performAdminOnAllTables(DbConnector.getLoginDbCon(), DbUtilities.DbAdminAction.CHECK_MEDIUM);
        }
        else {
            Server.logger.info("checkAllDbTables is false so not checking database tables for errors.");
        }
        if (Constants.analyseAllDbTables) {
            DbUtilities.performAdminOnAllTables(DbConnector.getLoginDbCon(), DbUtilities.DbAdminAction.ANALYZE);
        }
        else {
            Server.logger.info("analyseAllDbTables is false so not analysing database tables to update indices.");
        }
        if (Constants.optimiseAllDbTables) {
            DbUtilities.performAdminOnAllTables(DbConnector.getLoginDbCon(), DbUtilities.DbAdminAction.OPTIMIZE);
        }
        else {
            Server.logger.info("OptimizeAllDbTables is false so not optimising database tables.");
        }
        Servers.loadAllServers(false);
        if (Constants.useDirectByteBuffersForMeshIO) {
            MeshIO.setAllocateDirectBuffers(true);
        }
        if (this.steamHandler.getIsOfflineServer()) {
            Servers.localServer.EXTERNALIP = "0.0.0.0";
            Servers.localServer.INTRASERVERADDRESS = "0.0.0.0";
        }
        this.loadWorldMesh();
        this.loadCaveMesh();
        this.loadResourceMesh();
        this.loadRockMesh();
        this.loadFlagsMesh();
        Server.logger.info("Max height: " + getMaxHeight());
        try {
            Features.Feature.SURFACEWATER.isEnabled();
        }
        catch (Exception ex) {
            throw ex;
        }
        if (Features.Feature.SURFACEWATER.isEnabled()) {
            Water.loadWaterMesh();
        }
        Server.surfaceMesh.calcDistantTerrain();
        Features.loadAllFeatures();
        MessageServer.initialise();
        Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().lock();
        try {
            this.playersAtLogin = new ArrayList<Long>();
        }
        finally {
            Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().unlock();
        }
        Groups.addGroup(new Group("wurm"));
        Server.EpicServer = Servers.localServer.EPIC;
        Server.ChallengeServer = Servers.localServer.isChallengeServer();
        Server.logger.log(Level.INFO, "Protocol: 250990585");
        ItemTemplateCreator.initialiseItemTemplates();
        SpellGenerator.createSpells();
        CreatureTemplateCreator.createCreatureTemplates();
        LootTableCreator.initializeLootTables();
        if (Constants.createTemporaryDatabaseIndicesAtStartup) {
            DbIndexManager.createIndexes();
        }
        else {
            Server.logger.warning("createTemporaryDatabaseIndicesAtStartup is false so not creating indices. This is only for development and should not happen in production");
        }
        if (Features.Feature.CROP_POLLER.isEnabled()) {
            CropTilePoller.initializeFields();
        }
        if (Constants.RUNBATCH) {}
        if (Constants.crashed) {
            PlayerBatchJob.reimburseFatigue();
        }
        else if (!Servers.localServer.LOGINSERVER) {
            Constants.crashed = true;
        }
        EffectFactory.getInstance().loadEffects();
        AnimalSettings.loadAll();
        ItemSettings.loadAll();
        DoorSettings.loadAll();
        StructureSettings.loadAll();
        MineDoorSettings.loadAll();
        PermissionsHistories.loadAll();
        Items.loadAllItemData();
        Items.loadAllItempInscriptionData();
        Items.loadAllStaticItems();
        Items.loadAllZoneItems(BodyDbStrings.getInstance());
        Items.loadAllZoneItems(ItemDbStrings.getInstance());
        Items.loadAllZoneItems(CoinDbStrings.getInstance());
        ItemRequirement.loadAllItemRequirements();
        ArmourTemplate.initialize();
        WeaponCreator.createWeapons();
        Banks.loadAllBanks();
        Wall.loadAllWalls();
        Floor.loadAllFloors();
        BridgePart.loadAllBridgeParts();
        Kingdom.loadAllKingdoms();
        King.loadAllEra();
        Cooldowns.loadAllCooldowns();
        TilePoller.mask = (1 << Constants.meshSize) * (1 << Constants.meshSize) - 1;
        Zones.getZone(0, 0, true);
        Villages.loadVillages();
        if (Features.Feature.HIGHWAYS.isEnabled()) {
            (this.highwayFinderThread = new HighwayFinder()).start();
            Routes.generateAllRoutes();
            if (Features.Feature.WAGONER.isEnabled()) {
                Delivery.dbLoadAllDeliveries();
                Wagoner.dbLoadAllWagoners();
            }
        }
        try {
            CreaturePos.loadAllPositions();
            Creatures.getInstance().loadAllCreatures();
        }
        catch (NoSuchCreatureException nsc) {
            Server.logger.log(Level.WARNING, nsc.getMessage(), nsc);
            System.exit(0);
            return;
        }
        Villages.loadDeadVillages();
        Villages.loadCitizens();
        Villages.loadGuards();
        fixHoles();
        Items.loadAllItemEffects();
        MineDoorPermission.loadAllMineDoors();
        Zones.loadTowers();
        PvPAlliance.loadPvPAlliances();
        Villages.loadWars();
        Villages.loadWarDeclarations();
        RecruitmentAds.loadRecruitmentAds();
        Zones.addWarDomains();
        final long start = System.nanoTime();
        Economy.getEconomy();
        if (Servers.localServer.getKingsmoneyAtRestart() > 0) {
            Economy.getEconomy().getKingsShop().setMoney(Servers.localServer.getKingsmoneyAtRestart());
        }
        Server.logger.log(Level.INFO, "Loading economy took " + (System.nanoTime() - start) / 1000000.0f + " ms.");
        EndGameItems.loadEndGameItems();
        if (Servers.localServer.HOMESERVER || Items.getWarTargets().length == 0) {}
        if ((!Servers.localServer.EPIC || !Servers.localServer.HOMESERVER) && Items.getSourceSprings().length == 0) {
            Zones.shouldSourceSprings = true;
        }
        if (!Features.Feature.NEWDOMAINS.isEnabled()) {
            Zones.checkAltars();
        }
        PlayerInfoFactory.loadPlayerInfos();
        WurmRecord.loadAllChampRecords();
        Affinities.loadAffinities();
        PlayerInfoFactory.loadReferers();
        Dens.loadDens();
        Reimbursement.loadAll();
        PendingAccount.loadAllPendingAccounts();
        PasswordTransfer.loadAllPasswordTransfers();
        Trap.loadAllTraps();
        this.setExternalIp();
        this.setInternalIp();
        AchievementGenerator.generateAchievements();
        Achievements.loadAllAchievements();
        if (Constants.isGameServer) {
            Zones.writeZones();
        }
        if (Constants.dropTemporaryDatabaseIndicesAtStartup) {
            DbIndexManager.removeIndexes();
        }
        else {
            Server.logger.warning("dropTemporaryDatabaseIndicesAtStartup is false so not dropping indices. This is only for development and should not happen in production");
        }
        TilePoller.entryServer = Servers.localServer.entryServer;
        WcEpicKarmaCommand.loadAllKarmaHelpers();
        FocusZone.loadAll();
        Hota.loadAllHotaItems();
        Hota.loadAllHelpers();
        if (Constants.createSeeds || this.needSeeds) {
            Zones.createSeeds();
        }
        if (Servers.localServer.testServer) {
            Zones.createInvestigatables();
        }
        this.intraServer = new IntraServer(this);
        Statistics.getInstance().startup(Server.logger);
        WurmHarvestables.setStartTimes();
        WurmMail.loadAllMails();
        HistoryManager.loadHistory();
        Cultist.loadAllCultists();
        Effectuator.loadEffects();
        EpicServerStatus.loadLocalEntries();
        Tickets.loadTickets();
        VoteQuestions.loadVoteQuestions();
        PlayerVotes.loadAllPlayerVotes();
        Recipes.loadAllRecipes();
        ItemMealData.loadAllMealData();
        AffinitiesTimed.loadAllPlayerTimedAffinities();
        VillageMessages.loadVillageMessages();
        if (Constants.RUNBATCH) {}
        Constants.RUNBATCH = false;
        if (Constants.useMultiThreadedBankPolling || Constants.useQueueToSendDataToPlayers) {
            this.initialiseExecutorService(20);
            this.initialisePlayerCommunicatorSender();
        }
        this.setupScheduledExecutors();
        Eigc.loadAllAccounts();
        this.socketServer = new SocketServer(this.externalIp, Integer.parseInt(Servers.localServer.EXTERNALPORT), Integer.parseInt(Servers.localServer.EXTERNALPORT) + 1, this);
        SocketServer.MIN_MILLIS_BETWEEN_CONNECTIONS = Constants.minMillisBetweenPlayerConns;
        Server.logger.log(Level.INFO, "The Wurm Server is listening on ip " + Servers.localServer.EXTERNALIP + " and port " + Servers.localServer.EXTERNALPORT + ". Min time between same ip connections=" + SocketServer.MIN_MILLIS_BETWEEN_CONNECTIONS);
        Server.commPollCounterInit = 1;
        if (!Servers.localServer.PVPSERVER && Zones.worldTileSizeX > 5000) {
            Server.commPollCounterInit = 6;
        }
        Server.logger.log(Level.INFO, "commPollCounterInit=" + Server.commPollCounterInit);
        if (Constants.useScheduledExecutorForServer) {
            final ScheduledExecutorService scheduledServerRunExecutor = Executors.newScheduledThreadPool(Constants.scheduledExecutorServiceThreads);
            for (int i = 0; i < Constants.scheduledExecutorServiceThreads; ++i) {
                scheduledServerRunExecutor.scheduleWithFixedDelay(this, i * 2, 25L, TimeUnit.MILLISECONDS);
            }
        }
        else {
            final Timer timer = new Timer();
            timer.scheduleAtFixedRate(this, 0L, 25L);
            Server.startTime = System.currentTimeMillis();
        }
        Missions.getAllMissions();
        MissionTriggers.getAllTriggers();
        TriggerEffects.getAllEffects();
        if (Servers.localServer.LOGINSERVER) {
            (Server.epicMap = EpicServerStatus.getValrei()).loadAllEntities();
            Server.epicMap.addListener(this);
            EpicXmlWriter.dumpEntities(Server.epicMap);
        }
        if (Features.Feature.SURFACEWATER.isEnabled()) {
            (this.waterThread = new Water()).loadSprings();
            this.waterThread.start();
        }
        if (Constants.startChallenge) {
            Servers.localServer.setChallengeStarted(System.currentTimeMillis());
            Servers.localServer.setChallengeEnds(System.currentTimeMillis() + Constants.challengeDays * 86400000L);
            Servers.localServer.saveChallengeTimes();
            Constants.startChallenge = false;
        }
        ChallengeSummary.loadLocalChallengeScores();
        Creatures.getInstance().startPollTask();
        WaterType.calcWaterTypes();
        this.steamHandler.initializeSteam();
        this.steamHandler.createServer("wurmunlimitedserver", "wurmunlimitedserver", "Wurm Unlimited Server", "1.0.0.0");
        DbRitual.loadRiteEvents();
        DbRitual.loadRiteClaims();
        Server.logger.info("End of game server initialisation");
    }
    
    private void setupScheduledExecutors() {
        if (Constants.useScheduledExecutorToWriteLogs || Constants.useScheduledExecutorToSaveConstants || Constants.useScheduledExecutorToTickCalendar || Constants.useScheduledExecutorToCountEggs || Constants.useScheduledExecutorToSaveDirtyMeshRows || Constants.useScheduledExecutorToSendTimeSync || Constants.useScheduledExecutorToSwitchFatigue || Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase || Constants.useScheduledExecutorToUpdateItemDamageInDatabase || Constants.useScheduledExecutorToUpdateItemOwnerInDatabase || Constants.useScheduledExecutorToUpdateItemLastOwnerInDatabase || Constants.useScheduledExecutorToUpdateItemParentInDatabase || Constants.useScheduledExecutorToConnectToTwitter || Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase || Constants.useItemTransferLog || Constants.useTileEventLog) {
            this.scheduledExecutorService = Executors.newScheduledThreadPool(15);
        }
        if (Constants.useScheduledExecutorToWriteLogs) {
            Server.logger.info("Going to use a ScheduledExecutorService to write logs");
            final long lInitialDelay = 60L;
            final long lDelay = 300L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (Server.logger.isLoggable(Level.FINER)) {
                        Server.logger.finer("Running newSingleThreadScheduledExecutor for stat log writing");
                    }
                }
            }, 60L, 300L, TimeUnit.SECONDS);
            final long lPingDelay = 300L;
            final long lInitialDelay2 = 5000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (Server.logger.isLoggable(Level.FINEST)) {
                            try {
                                Servers.pingServers();
                            }
                            catch (RuntimeException e) {
                                Server.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorServicePollServers while calling pingServers()", e);
                                throw e;
                            }
                            return;
                        }
                        continue;
                    }
                }
            }, 5000L, 300L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToCountEggs) {
            Server.logger.info("Going to use a ScheduledExecutorService to count eggs");
            final long lInitialDelay = 1000L;
            final long lDelay = 3600000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new Items.EggCounter(), 1000L, 3600000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToSaveConstants) {
            Server.logger.info("Going to use a ScheduledExecutorService to save Constants to wurm.ini");
            final long lInitialDelay = 1000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new Constants.ConstantsSaver(), 1000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToSaveDirtyMeshRows) {
            Server.logger.info("Going to use a ScheduledExecutorService to call MeshIO.saveNextDirtyRow()");
            long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            final long delayInterval = 250L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new MeshSaver(Server.surfaceMesh, "SurfaceMesh", Constants.numberOfDirtyMeshRowsToSaveEachCall), lInitialDelay, 1000L, TimeUnit.MILLISECONDS);
            lInitialDelay += 250L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new MeshSaver(Server.caveMesh, "CaveMesh", Constants.numberOfDirtyMeshRowsToSaveEachCall), lInitialDelay, 1000L, TimeUnit.MILLISECONDS);
            lInitialDelay += 250L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new MeshSaver(Server.rockMesh, "RockMesh", Constants.numberOfDirtyMeshRowsToSaveEachCall), lInitialDelay, 1000L, TimeUnit.MILLISECONDS);
            lInitialDelay += 250L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new MeshSaver(Server.resourceMesh, "ResourceMesh", Constants.numberOfDirtyMeshRowsToSaveEachCall), lInitialDelay, 1000L, TimeUnit.MILLISECONDS);
            lInitialDelay += 250L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new MeshSaver(Server.flagsMesh, "FlagsMesh", Constants.numberOfDirtyMeshRowsToSaveEachCall), lInitialDelay, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToSendTimeSync) {
            if (Servers.localServer.LOGINSERVER) {
                Server.logger.warning("This is the login server so it will not send TimeSync commands");
            }
            else {
                Server.logger.info("Going to use a ScheduledExecutorService to send TimeSync commands");
                final long lInitialDelay = 1000L;
                final long lDelay = 3600000L;
                this.scheduledExecutorService.scheduleWithFixedDelay(new TimeSync.TimeSyncSender(), 1000L, 3600000L, TimeUnit.MILLISECONDS);
            }
        }
        if (Constants.useScheduledExecutorToSwitchFatigue) {
            Server.logger.info("Going to use a ScheduledExecutorService to switch fatigue");
            final long lInitialDelay = 60000L;
            final long lDelay = 86400000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new PlayerInfoFactory.FatigueSwitcher(), 60000L, 86400000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToTickCalendar) {
            Server.logger.info("Going to use a ScheduledExecutorService to call WurmCalendar.tickSeconds()");
            final long lInitialDelay = 125L;
            final long lDelay = 125L;
            this.scheduledExecutorService.scheduleWithFixedDelay(new WurmCalendar.Ticker(), 125L, 125L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useItemTransferLog) {
            Server.logger.info("Going to use a ScheduledExecutorService to log Item Transfers");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(Item.getItemlogger(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useTileEventLog) {
            Server.logger.info("Going to use a ScheduledExecutorService to log tile events");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(TileEvent.getTilelogger(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase) {
            Server.logger.info("Going to use a ScheduledExecutorService to update creature positions in database");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(CreaturePos.getCreatureDbPosUpdater(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase) {
            Server.logger.info("Going to use a ScheduledExecutorService to update player positions in database");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(CreaturePos.getPlayerDbPosUpdater(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToUpdateItemDamageInDatabase) {
            Server.logger.info("Going to use a ScheduledExecutorService to update item damage in database");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(DbItem.getItemDamageDatabaseUpdater(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToUpdateItemOwnerInDatabase) {
            Server.logger.info("Going to use a ScheduledExecutorService to update item owner in database");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(DbItem.getItemOwnerDatabaseUpdater(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToUpdateItemLastOwnerInDatabase) {
            Server.logger.info("Going to use a ScheduledExecutorService to update item last owner in database");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(DbItem.getItemLastOwnerDatabaseUpdater(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToUpdateItemParentInDatabase) {
            Server.logger.info("Going to use a ScheduledExecutorService to update item parent in database");
            final long lInitialDelay = 60000L;
            final long lDelay = 1000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(DbItem.getItemParentDatabaseUpdater(), 60000L, 1000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorToConnectToTwitter) {
            Server.logger.info("Going to use a ScheduledExecutorService to connect to twitter");
            final long lInitialDelay = 60000L;
            final long lDelay = 5000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(Twit.getTwitterThread(), 60000L, 5000L, TimeUnit.MILLISECONDS);
        }
        if (Constants.useScheduledExecutorForTrello) {
            Server.logger.info("Going to use a ScheduledExecutorService for maintaining tickets in Trello");
            final long lInitialDelay = 5000L;
            final long lDelay = 60000L;
            this.scheduledExecutorService.scheduleWithFixedDelay(Trello.getTrelloThread(), 5000L, 60000L, TimeUnit.MILLISECONDS);
        }
    }
    
    void twitLocalServer(final String message) {
        final Twit t = Servers.localServer.createTwit(message);
        if (t != null) {
            Twit.twit(t);
        }
    }
    
    private void logCodeVersionInformation() {
        try {
            final Package p = Class.forName("com.wurmonline.server.Server").getPackage();
            if (p == null) {
                Server.logger.warning("Wurm Build Date: UNKNOWN (Package.getPackage() is null!)");
            }
            else {
                Server.logger.info("Wurm Impl Title: " + p.getImplementationTitle());
                Server.logger.info("Wurm Impl Vendor: " + p.getImplementationVendor());
                Server.logger.info("Wurm Impl Version: " + p.getImplementationVersion());
            }
        }
        catch (Exception ex) {
            Server.logger.severe("Wurm version: UNKNOWN (Error getting version number from MANIFEST.MF)");
        }
        try {
            final Package p = Class.forName("com.wurmonline.shared.constants.ProtoConstants").getPackage();
            if (p == null) {
                Server.logger.warning("Wurm Common: UNKNOWN (Package.getPackage() is null!)");
            }
            else {
                Server.logger.info("Wurm Common Impl Title: " + p.getImplementationTitle());
                Server.logger.info("Wurm Common Impl Vendor: " + p.getImplementationVendor());
                Server.logger.info("Wurm Common Impl Version: " + p.getImplementationVersion());
            }
        }
        catch (Exception ex) {
            Server.logger.severe("Wurm Common: UNKNOWN (Error getting version number from MANIFEST.MF)");
        }
        try {
            final Package p = Class.forName("com.mysql.jdbc.Driver").getPackage();
            if (p == null) {
                Server.logger.warning("MySQL JDBC: UNKNOWN (Package.getPackage() is null!)");
            }
            else {
                Server.logger.info("MySQL JDBC Spec Title: " + p.getSpecificationTitle());
                Server.logger.info("MySQL JDBC Spec Vendor: " + p.getSpecificationVendor());
                Server.logger.info("MySQL JDBC Spec Version: " + p.getSpecificationVersion());
                Server.logger.info("MySQL JDBC Impl Title: " + p.getImplementationTitle());
                Server.logger.info("MySQL JDBC Impl Vendor: " + p.getImplementationVendor());
                Server.logger.info("MySQL JDBC Impl Version: " + p.getImplementationVersion());
            }
        }
        catch (Exception ex) {
            Server.logger.severe("MySQL JDBC: UNKNOWN (Error getting version number from MANIFEST.MF)");
        }
        try {
            final Package p = Class.forName("javax.mail.Message").getPackage();
            if (p == null) {
                Server.logger.warning("Javax Mail: UNKNOWN (Package.getPackage() is null!)");
            }
            else {
                Server.logger.info("Javax Mail Spec Title: " + p.getSpecificationTitle());
                Server.logger.info("Javax Mail Spec Vendor: " + p.getSpecificationVendor());
                Server.logger.info("Javax Mail Spec Version: " + p.getSpecificationVersion());
                Server.logger.info("Javax Mail Impl Title: " + p.getImplementationTitle());
                Server.logger.info("Javax Mail Impl Vendor: " + p.getImplementationVendor());
                Server.logger.info("Javax Mail Impl Version: " + p.getImplementationVersion());
            }
        }
        catch (Exception ex) {
            Server.logger.severe("Javax Mail: UNKNOWN (Error getting version number from MANIFEST.MF)");
        }
        try {
            final Package p = Class.forName("javax.activation.DataSource").getPackage();
            if (p == null) {
                Server.logger.warning("Javax Activation: UNKNOWN (Package.getPackage() is null!)");
            }
            else {
                Server.logger.info("Javax Activation Spec Title: " + p.getSpecificationTitle());
                Server.logger.info("Javax Activation Spec Vendor: " + p.getSpecificationVendor());
                Server.logger.info("Javax Activation Spec Version: " + p.getSpecificationVersion());
                Server.logger.info("Javax Activation Impl Title: " + p.getImplementationTitle());
                Server.logger.info("Javax Activation Impl Vendor: " + p.getImplementationVendor());
                Server.logger.info("Javax Activation Impl Version: " + p.getImplementationVersion());
            }
        }
        catch (Exception ex) {
            Server.logger.severe("Javax Activation: UNKNOWN (Error getting version number from MANIFEST.MF)");
        }
    }
    
    public void initialisePlayerCommunicatorSender() {
        if (Constants.useQueueToSendDataToPlayers) {
            Server.playerCommunicatorSender = new PlayerCommunicatorSender();
            this.getMainExecutorService().execute(Server.playerCommunicatorSender);
        }
    }
    
    private static void fixHoles() {
        Server.logger.log(Level.INFO, "Fixing cave entrances.");
        final long start = System.nanoTime();
        int found = 0;
        int fixed = 0;
        int fixed2 = 0;
        int fixed3 = 0;
        int fixed4 = 0;
        int fixed5 = 0;
        int fixedWalls = 0;
        final int min = 0;
        final int ms = Constants.meshSize;
        for (int max = 1 << ms, x = 0; x < max; ++x) {
            for (int y = 0; y < max; ++y) {
                int tile = Server.surfaceMesh.getTile(x, y);
                if (Tiles.decodeType(tile) == Tiles.Tile.TILE_HOLE.id) {
                    ++found;
                    boolean fix = false;
                    final int t = Server.caveMesh.getTile(x, y);
                    if (Tiles.decodeType(t) != Tiles.Tile.TILE_CAVE_EXIT.id) {
                        ++fixed;
                        setSurfaceTile(x, y, Tiles.decodeHeight(tile), Tiles.Tile.TILE_ROCK.id, (byte)0);
                    }
                    else {
                        for (int xx = 0; xx <= 1; ++xx) {
                            for (int yy = 0; yy <= 1; ++yy) {
                                final int tt = Server.caveMesh.getTile(x + xx, y + yy);
                                if (Tiles.decodeHeight(tt) == -100 && Tiles.decodeData(tt) == 0) {
                                    fix = true;
                                    break;
                                }
                            }
                        }
                        if (fix) {
                            ++fixed2;
                            for (int xx = 0; xx <= 1; ++xx) {
                                for (int yy = 0; yy <= 1; ++yy) {
                                    Server.caveMesh.setTile(x + xx, y + yy, Tiles.encode((short)(-100), TileRockBehaviour.prospect(x + xx, y + yy, false), (byte)0));
                                }
                            }
                            setSurfaceTile(x, y, Tiles.decodeHeight(tile), Tiles.Tile.TILE_ROCK.id, (byte)0);
                        }
                    }
                    if (!fix) {
                        int lowestX = 100000;
                        int lowestY = 100000;
                        int nextLowestX = lowestX;
                        int nextLowestY = lowestY;
                        int nextLowestHeight;
                        int lowestHeight = nextLowestHeight = 100000;
                        for (int xa = 0; xa <= 1; ++xa) {
                            for (int ya = 0; ya <= 1; ++ya) {
                                if (x + xa < max && y + ya < max) {
                                    final int rockTile = Server.rockMesh.getTile(x + xa, y + ya);
                                    final int rockHeight = Tiles.decodeHeight(rockTile);
                                    if (rockHeight <= lowestHeight) {
                                        if (lowestHeight < nextLowestHeight && TileBehaviour.isAdjacent(lowestX, lowestY, x + xa, y + ya)) {
                                            nextLowestHeight = lowestHeight;
                                            nextLowestX = lowestX;
                                            nextLowestY = lowestY;
                                        }
                                        lowestHeight = rockHeight;
                                        lowestX = x + xa;
                                        lowestY = y + ya;
                                    }
                                    else if (rockHeight <= nextLowestHeight && nextLowestHeight > lowestHeight && TileBehaviour.isAdjacent(lowestX, lowestY, x + xa, y + ya)) {
                                        nextLowestHeight = rockHeight;
                                        nextLowestX = x + xa;
                                        nextLowestY = y + ya;
                                    }
                                }
                            }
                        }
                        if (lowestX != 100000 && lowestY != 100000 && nextLowestX != 100000 && nextLowestY != 100000) {
                            final int lowestRock = Server.rockMesh.getTile(lowestX, lowestY);
                            final int nextLowestRock = Server.rockMesh.getTile(nextLowestX, nextLowestY);
                            final int lowestCave = Server.caveMesh.getTile(lowestX, lowestY);
                            final int nextLowestCave = Server.caveMesh.getTile(nextLowestX, nextLowestY);
                            final int lowestSurf = Server.surfaceMesh.getTile(lowestX, lowestY);
                            final int nextLowestSurf = Server.surfaceMesh.getTile(nextLowestX, nextLowestY);
                            final short lrockHeight = Tiles.decodeHeight(lowestRock);
                            final short nlrockHeight = Tiles.decodeHeight(nextLowestRock);
                            final short lcaveHeight = Tiles.decodeHeight(lowestCave);
                            final short nlcaveHeight = Tiles.decodeHeight(nextLowestCave);
                            final short lsurfHeight = Tiles.decodeHeight(lowestSurf);
                            final short nlsurfHeight = Tiles.decodeHeight(nextLowestSurf);
                            if (lcaveHeight != lrockHeight || Tiles.decodeData(lowestCave) != 0) {
                                ++fixed4;
                                Server.caveMesh.setTile(lowestX, lowestY, Tiles.encode(lrockHeight, Tiles.decodeType(lowestCave), (byte)0));
                            }
                            if (nlcaveHeight != nlrockHeight || Tiles.decodeData(nextLowestCave) != 0) {
                                ++fixed4;
                                Server.caveMesh.setTile(nextLowestX, nextLowestY, Tiles.encode(nlrockHeight, Tiles.decodeType(nextLowestCave), (byte)0));
                            }
                            if (lsurfHeight != lrockHeight) {
                                ++fixed5;
                                setSurfaceTile(lowestX, lowestY, lrockHeight, Tiles.decodeType(lowestSurf), Tiles.decodeData(lowestSurf));
                            }
                            if (nlsurfHeight != nlrockHeight) {
                                ++fixed5;
                                setSurfaceTile(nextLowestX, nextLowestY, nlrockHeight, Tiles.decodeType(nextLowestSurf), Tiles.decodeData(nextLowestSurf));
                            }
                        }
                    }
                }
                else {
                    tile = Server.caveMesh.getTile(x, y);
                    if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE.id) {
                        int minheight = -100;
                        boolean fix2 = false;
                        for (int xx = 0; xx <= 1; ++xx) {
                            for (int yy = 0; yy <= 1; ++yy) {
                                final int tt = Server.caveMesh.getTile(x + xx, y + yy);
                                if (Tiles.decodeHeight(tt) == -100 && Tiles.decodeData(tt) == 0) {
                                    fix2 = true;
                                    if (Tiles.decodeHeight(tt) > minheight) {
                                        minheight = Tiles.decodeHeight(tt);
                                    }
                                }
                            }
                        }
                        if (fix2) {
                            ++fixed3;
                            for (int xx = 0; xx <= 1; ++xx) {
                                for (int yy = 0; yy <= 1; ++yy) {
                                    final int tt = Server.caveMesh.getTile(x + xx, y + yy);
                                    final int rocktile = Server.rockMesh.getTile(x + xx, y + yy);
                                    final int rockHeight2 = Tiles.decodeHeight(rocktile);
                                    final int maxHeight = rockHeight2 - minheight;
                                    if (Tiles.decodeHeight(tt) == -100 && Tiles.decodeData(tt) == 0) {
                                        Server.caveMesh.setTile(x + xx, y + yy, Tiles.encode((short)minheight, Tiles.decodeType(tt), (byte)Math.min(maxHeight, 5)));
                                    }
                                }
                            }
                        }
                    }
                    else if (Tiles.getTile(Tiles.decodeType(tile)) == null) {
                        Server.caveMesh.setTile(x, y, Tiles.encode((short)(-100), TileRockBehaviour.prospect(x & (1 << Constants.meshSize) - 1, y >> Constants.meshSize, false), (byte)0));
                        Server.logger.log(Level.INFO, "Mended a " + Tiles.decodeType(tile) + " cave tile at " + x + "," + y);
                    }
                    else {
                        final int cavet = Server.caveMesh.getTile(x, y);
                        if (Tiles.decodeData(cavet) != 0) {
                            final byte cceil = Tiles.decodeData(cavet);
                            final int caveh = Tiles.decodeHeight(cavet);
                            final int rockHeight3 = Tiles.decodeHeight(Server.rockMesh.getTile(x, y));
                            if (cceil + caveh > rockHeight3) {
                                ++fixedWalls;
                                final int maxHeight2 = rockHeight3 - caveh;
                                Server.caveMesh.setTile(x, y, Tiles.encode((short)caveh, Tiles.decodeType(cavet), (byte)Math.min(maxHeight2, cceil)));
                            }
                        }
                    }
                }
            }
        }
        try {
            Server.surfaceMesh.saveAll();
            Server.logger.log(Level.INFO, "Set " + fixed + " cave entrances to rock out of " + found);
        }
        catch (IOException iox) {
            Server.logger.log(Level.WARNING, "Failed to save surfaceMesh", iox);
        }
        Label_1499: {
            if (fixed2 <= 0 && fixed3 <= 0 && fixedWalls <= 0 && fixed4 <= 0) {
                if (fixed5 <= 0) {
                    break Label_1499;
                }
            }
            try {
                Server.caveMesh.saveAll();
                Server.logger.log(Level.INFO, "Fixed " + fixed2 + " crazy cave entrances and " + fixed3 + " weird caves as well. Also fixed " + fixedWalls + " walls sticking up. Also fixed " + fixed4 + " unleavable exit nodes. Fixed " + fixed5 + " misaligned surface tile nodes.");
            }
            catch (IOException iox) {
                Server.logger.log(Level.WARNING, "Failed to save surfaceMesh", iox);
            }
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        Server.logger.info("Fixing cave entrances took " + lElapsedTime + " ms");
    }
    
    private void checkShutDown() {
        final int secondsToShutDown = (int)(Server.millisToShutDown / 1000L);
        if (secondsToShutDown == 2400) {
            if (Server.lastSentWarning != 2400) {
                Server.lastSentWarning = 2400;
                this.broadCastAlert("40 minutes to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 1200) {
            if (Server.lastSentWarning != 1200) {
                Server.lastSentWarning = 1200;
                this.broadCastAlert("20 minutes to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 600) {
            if (Server.lastSentWarning != 600) {
                Server.lastSentWarning = 600;
                this.broadCastAlert("10 minutes to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 300) {
            if (Server.lastSentWarning != 300) {
                Server.lastSentWarning = 300;
                this.broadCastAlert("5 minutes to shutdown. ", true, (byte)1);
                this.broadCastAlert(Server.shutdownReason, true, (byte)0);
                Players.getInstance().setChallengeStep(2);
            }
        }
        else if (secondsToShutDown == 180) {
            if (Server.lastSentWarning != 180) {
                Server.lastSentWarning = 180;
                this.broadCastAlert("3 minutes to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
                Players.getInstance().setChallengeStep(3);
                Players.getInstance().setChallengeStep(4);
            }
        }
        else if (secondsToShutDown == 60) {
            if (Server.lastSentWarning != 60) {
                Server.lastSentWarning = 60;
                this.broadCastAlert("1 minute to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 30) {
            if (Server.lastSentWarning != 30) {
                Server.lastSentWarning = 30;
                this.broadCastAlert("30 seconds to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 20) {
            if (Server.lastSentWarning != 20) {
                Server.lastSentWarning = 20;
                this.broadCastAlert("20 seconds to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 10) {
            if (Server.lastSentWarning != 10) {
                Server.lastSentWarning = 10;
                final FocusZone hotaZone = FocusZone.getHotaZone();
                if (hotaZone != null) {
                    Hota.forcePillarsToWorld();
                }
                this.broadCastAlert("10 seconds to shutdown. ", false, (byte)1);
                this.broadCastAlert(Server.shutdownReason, false, (byte)0);
            }
        }
        else if (secondsToShutDown == 3 && Server.lastSentWarning != 1) {
            Server.lastSentWarning = 1;
            this.broadCastAlert("Server shutting down NOW!/%7?o#### NO CARRIER", false);
            Players.getInstance().sendLogoff("The server shut down: " + Server.shutdownReason);
            this.twitLocalServer("The server shut down: " + Server.shutdownReason);
        }
        if (secondsToShutDown < 120) {
            Constants.maintaining = true;
        }
    }
    
    @Override
    public void run() {
        long now = 0L;
        long check = 0L;
        try {
            now = (check = System.currentTimeMillis());
            if (Constants.isGameServer) {
                TilePoller.pollNext();
            }
            if (!Servers.localServer.testServer && System.currentTimeMillis() - check > Constants.lagThreshold) {
                Server.logger.log(Level.INFO, "Lag detected at tilepoller.pollnext (0.1): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
            }
            check = System.currentTimeMillis();
            Zones.pollNextZones(25L);
            if (Features.Feature.CROP_POLLER.isEnabled()) {
                CropTilePoller.pollCropTiles();
            }
            Players.getInstance().pollPlayers();
            Delivery.poll();
            if (!Servers.localServer.testServer && System.currentTimeMillis() - check > Constants.lagThreshold) {
                Server.logger.log(Level.INFO, "Lag detected at Zones.pollnextzones (0.5): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
            }
            if (Server.millisToShutDown > -1000L) {
                if (Server.millisToShutDown < 0L) {
                    this.shutDown();
                }
                else {
                    this.checkShutDown();
                    Server.millisToShutDown -= 25L;
                }
            }
            if (Server.counter == 2) {
                VoteQuestions.handleVoting();
                VoteQuestions.handleArchiveTickets();
                if (Features.Feature.HIGHWAYS.isEnabled()) {
                    Routes.handlePathsToSend();
                }
            }
            if (Server.counter == 3) {
                PlayerInfoFactory.handlePlayerStateList();
                Tickets.handleArchiveTickets();
                Tickets.handleTicketsToSend();
            }
            if (++Server.counter == 5) {
                if (Constants.useScheduledExecutorToTickCalendar) {
                    if (Server.logger.isLoggable(Level.FINEST)) {}
                }
                else {
                    WurmCalendar.tickSecond();
                }
                ServerProjectile.pollAll();
                if (now - this.lastLogged > 300000L) {
                    this.lastLogged = now;
                    if (Constants.useScheduledExecutorToWriteLogs && Server.logger.isLoggable(Level.FINER)) {
                        Server.logger.finer("Using a ScheduledExecutorService to write logs so do not call writePlayerLog() from main Server thread");
                    }
                    if (Constants.isGameServer && System.currentTimeMillis() - Servers.localServer.getFatigueSwitch() > 86400000L) {
                        if (Constants.useScheduledExecutorToSwitchFatigue) {
                            if (Server.logger.isLoggable(Level.FINER)) {
                                Server.logger.finer("Using a ScheduledExecutorService to switch fatigue so do not call PlayerInfoFactory.switchFatigue() from main Server thread");
                            }
                        }
                        else {
                            PlayerInfoFactory.switchFatigue();
                        }
                        Offspring.resetOffspringCounters();
                        Servers.localServer.setFatigueSwitch(System.currentTimeMillis());
                    }
                    King.pollKings();
                    Players.getInstance().checkElectors();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at 1: " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (Constants.isGameServer && now - Server.lastArrow > 100L) {
                    Arrows.pollAll(now - Server.lastArrow);
                    Server.lastArrow = now;
                }
                final boolean startHota = Servers.localServer.getNextHota() > 0L && System.currentTimeMillis() > Servers.localServer.getNextHota();
                if (startHota) {
                    Hota.poll();
                }
                if (now - Server.lastMailCheck > 364000L) {
                    WurmMail.poll();
                    Server.lastMailCheck = now;
                }
                if (now - Server.lastPolledRubble > 60000L) {
                    Server.lastPolledRubble = System.currentTimeMillis();
                    for (final Fence fence : Fence.getRubbleFences()) {
                        fence.poll(now);
                    }
                    for (final Wall wall : Wall.getRubbleWalls()) {
                        wall.poll(now, null, null);
                    }
                    if (Server.ChallengeServer && Servers.localServer.getChallengeEnds() > 0L && System.currentTimeMillis() > Servers.localServer.getChallengeEnds() && Server.millisToShutDown < 0L) {
                        for (final Village v : Villages.getVillages()) {
                            v.disband("System");
                        }
                        this.startShutdown(600, "The world is ending.");
                        Players.getInstance().setChallengeStep(1);
                    }
                    if (Server.tempEffects.size() > 0) {
                        final HashSet<Long> toRemove = new HashSet<Long>();
                        for (final Map.Entry<Long, Long> entry : Server.tempEffects.entrySet()) {
                            if (System.currentTimeMillis() > entry.getValue()) {
                                toRemove.add(entry.getKey());
                            }
                        }
                        for (final Long val : toRemove) {
                            Server.tempEffects.remove(val);
                            Players.getInstance().removeGlobalEffect(val);
                        }
                    }
                }
                if (now - Server.lastPolledWater > 1000L) {
                    this.pollSurfaceWater();
                    Server.lastPolledWater = System.currentTimeMillis();
                }
                if (now - Server.lastWeather > 70000L) {
                    check = System.currentTimeMillis();
                    Server.lastWeather = now;
                    boolean setw = true;
                    if (Server.weather.tick() && Servers.localServer.LOGINSERVER) {
                        startSendWeatherThread();
                        setw = false;
                    }
                    if (setw) {
                        Players.getInstance().setShouldSendWeather(true);
                    }
                    this.thunderMode = (Server.weather.getRain() > 0.5f && Server.weather.getCloudiness() > 0.5f);
                    if (WurmCalendar.isChristmas()) {
                        Zones.loadChristmas();
                    }
                    else if (WurmCalendar.wasTestChristmas) {
                        WurmCalendar.wasTestChristmas = false;
                        Zones.deleteChristmas();
                    }
                    else if (WurmCalendar.isAfterChristmas()) {
                        Zones.deleteChristmas();
                    }
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Weather (2): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                    if (!startHota) {
                        Hota.poll();
                    }
                }
                if (Constants.isGameServer && this.thunderMode && now - this.lastFlash > 5000L) {
                    this.lastFlash = now;
                    if (Server.weather.getRain() - 0.5f + (Server.weather.getCloudiness() - 0.5f) > Server.rand.nextFloat()) {
                        Zones.flash();
                    }
                }
                if (Constants.isGameServer && now - Server.lastSecond > 60000L) {
                    check = System.currentTimeMillis();
                    Server.lastSecond = now;
                    if (Constants.useScheduledExecutorToSaveDirtyMeshRows) {
                        if (Server.logger.isLoggable(Level.FINER)) {
                            Server.logger.finer("useScheduledExecutorToSaveDirtyMeshRows is true so do not save the meshes from Server.run()");
                        }
                    }
                    else {
                        Server.caveMesh.saveNextDirtyRow();
                        Server.surfaceMesh.saveNextDirtyRow();
                        Server.rockMesh.saveNextDirtyRow();
                        Server.resourceMesh.saveNextDirtyRow();
                        Server.flagsMesh.saveNextDirtyRow();
                    }
                    MountTransfer.pruneTransfers();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Meshes.saveNextDirtyRow (4): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (Constants.isGameServer && now - Server.lastPolledSkills > 21600000L) {
                    check = System.currentTimeMillis();
                    if (!Features.Feature.SKILLSTAT_DISABLE.isEnabled()) {
                        SkillStat.pollSkills();
                    }
                    Server.lastPolledSkills = System.currentTimeMillis();
                    EndGameItems.pollAll();
                    Trap.checkUpdate();
                    Items.pollUnstableRifts();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at pollskills (4.5): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                    if (System.currentTimeMillis() - Servers.localServer.getLastSpawnedUnique() > 1209600000L) {
                        Dens.checkDens(true);
                    }
                }
                if (Constants.isGameServer && now - Server.lastResetTiles > 14400000L) {
                    Zones.saveProtectedTiles();
                    Server.lastResetTiles = System.currentTimeMillis();
                }
                if (Servers.localServer.LOGINSERVER && System.currentTimeMillis() > Servers.localServer.getNextEpicPoll()) {
                    Server.epicMap.pollAllEntities(false);
                    Servers.localServer.setNextEpicPoll(System.currentTimeMillis() + 1200000L);
                }
                ValreiMapData.pollValreiData();
                SpellResist.onServerPoll();
                if (now - Server.lastRecruitmentPoll > 86400000L) {
                    Server.lastRecruitmentPoll = System.currentTimeMillis();
                    RecruitmentAds.poll();
                }
                if (now - Server.lastAwardedItems > 2000L) {
                    ValreiMapData.pollValreiData();
                    pollPendingAwards();
                    AwardLadder.clearItemAwards();
                    Server.lastAwardedItems = System.currentTimeMillis();
                }
                if (now - Server.lastFaith > 3600000L) {
                    check = System.currentTimeMillis();
                    Server.lastFaith = System.currentTimeMillis();
                    if (Constants.isGameServer) {
                        Deities.calculateFaiths();
                        if (now - this.lastClearedFaithGain > 86400000L) {
                            Players.resetFaithGain();
                            this.lastClearedFaithGain = now;
                        }
                        Creatures.getInstance().pollOfflineCreatures();
                    }
                    if (!Servers.isThisLoginServer()) {
                        if (Constants.useScheduledExecutorToSendTimeSync) {
                            if (Server.logger.isLoggable(Level.FINER)) {
                                Server.logger.finer("useScheduledExecutorToSendTimeSync is true so do not send TimeSync from Server.run()");
                            }
                        }
                        else {
                            final TimeSync synch = new TimeSync();
                            this.addIntraCommand(synch);
                        }
                    }
                    else {
                        ErrorChecks.checkItemWatchers();
                    }
                    if (Server.rand.nextInt(3) == 0) {
                        PendingAccount.poll();
                    }
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at 5: " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (Constants.isGameServer && now - Server.lastPolledBanks > 3601000L) {
                    check = System.currentTimeMillis();
                    if (Constants.useScheduledExecutorToCountEggs) {
                        if (Server.logger.isLoggable(Level.FINER)) {
                            Server.logger.finer("useScheduledExecutorToCountEggs is true so do not call Items.countEggs() from Server.run()");
                        }
                    }
                    else {
                        Items.countEggs();
                    }
                    Banks.poll(Server.lastPolledBanks = now);
                    Players.getInstance().checkAffinities();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Banks and Eggs (6): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (Constants.isGameServer && WurmCalendar.currentTime % 4000L == 0L) {
                    check = System.currentTimeMillis();
                    Players.getInstance().calcCRBonus();
                    Villages.poll();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Villages.poll (7): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                    check = System.currentTimeMillis();
                    Kingdoms.poll();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Kingdoms.poll (7.1): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                    check = System.currentTimeMillis();
                    Questions.trimQuestions();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Questions.trimQuestions (7.2): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (WurmCalendar.currentTime % 100L == 0L) {
                    check = System.currentTimeMillis();
                    Skills.switchSkills(check);
                    Battles.poll(false);
                    Servers.localServer.saveTimers();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at Battles and Constants (9): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                else if (WurmCalendar.currentTime % 1050L == 0L) {
                    Players.getInstance().pollChamps();
                    Effectuator.pollEpicEffects();
                }
                if (now - Server.lastDeletedPlayer > 3000L) {
                    PlayerInfoFactory.checkIfDeleteOnePlayer();
                    Server.lastDeletedPlayer = System.currentTimeMillis();
                }
                if (now - Server.lastLoweredRanks > 600000L) {
                    PlayerInfoFactory.pruneRanks(now);
                    EpicServerStatus.pollExpiredMissions();
                    Server.lastLoweredRanks = System.currentTimeMillis();
                }
                if (now > this.nextTerraformPoll) {
                    this.pollTerraformingTasks();
                    this.nextTerraformPoll = System.currentTimeMillis() + 1000L;
                }
                if (Servers.localServer.EPIC && !Servers.localServer.HOMESERVER && now > Server.lastPolledSupplyDepots + 60000L) {
                    for (final Item depot : Items.getSupplyDepots()) {
                        depot.checkItemSpawn();
                    }
                    Server.lastPolledSupplyDepots = now;
                }
                if (Servers.localServer.isChallengeServer()) {
                    if (now - Server.lastAwardedBattleCamps > 600000L) {
                        for (final Item i : Items.getWarTargets()) {
                            final Kingdom k = Kingdoms.getKingdom(i.getKingdom());
                            if (k != null) {
                                k.addWinpoints(1);
                            }
                            for (final PlayerInfo pinf : PlayerInfoFactory.getPlayerInfos()) {
                                if (System.currentTimeMillis() - pinf.lastLogin < 86400000L && Players.getInstance().getKingdomForPlayer(pinf.wurmId) == i.getKingdom()) {
                                    ChallengeSummary.addToScore(pinf, ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype(), 1.0f);
                                }
                            }
                        }
                        Server.lastAwardedBattleCamps = System.currentTimeMillis();
                    }
                    if (now > Server.lastPolledSupplyDepots + 60000L) {
                        for (final Item depot : Items.getSupplyDepots()) {
                            depot.checkItemSpawn();
                        }
                        Server.lastPolledSupplyDepots = now;
                    }
                    if (now - Server.savedChallengePage > 10000L) {
                        ChallengeSummary.saveCurrentGlobalHtmlPage();
                        Server.savedChallengePage = System.currentTimeMillis();
                    }
                }
                if (now - Server.lastPinged > 1000L) {
                    Trap.checkQuickUpdate();
                    Players.getInstance().checkSendWeather();
                    check = System.currentTimeMillis();
                    if (Server.lostConnections > 20 && Server.lostConnections > Players.getInstance().numberOfPlayers() / 2) {
                        Server.logger.log(Level.INFO, "Trying to forcibly log off linkless players: " + Server.lostConnections);
                        Players.getInstance().logOffLinklessPlayers();
                    }
                    Server.lostConnections = 0;
                    this.checkAlertMessages();
                    Server.lastPinged = now;
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at checkAlertMessages (10): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (Constants.isGameServer && now - Server.lastPolledShopCultist > 86400000L) {
                    Server.lastPolledShopCultist = System.currentTimeMillis();
                    Cultist.resetSkillGain();
                    Server.logger.log(Level.INFO, "Polling shop demands");
                    check = System.currentTimeMillis();
                    this.pollShopDemands();
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at pollShopDemands (11): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                if (System.currentTimeMillis() - Server.lastPolledTileEffects > 3000L) {
                    AreaSpellEffect.pollEffects();
                    Server.lastPolledTileEffects = System.currentTimeMillis();
                    Players.printStats();
                }
                if (System.currentTimeMillis() - Server.lastResetAspirations > 90000000L) {
                    Methods.resetAspirants();
                    Server.lastResetAspirations = System.currentTimeMillis();
                }
                if (this.playersAtLogin.size() > 0) {
                    check = System.currentTimeMillis();
                    final Iterator<Long> it = this.playersAtLogin.listIterator();
                    while (it.hasNext()) {
                        final long pid = it.next();
                        try {
                            final Creature player = Players.getInstance().getPlayer(pid);
                            if (player.getVisionArea() == null) {
                                Server.logger.log(Level.INFO, "VisionArea null for " + player.getName() + ", creating one.");
                                player.createVisionArea();
                            }
                            final VisionArea area = player.getVisionArea();
                            if (area != null && area.isInitialized()) {
                                it.remove();
                            }
                            else {
                                try {
                                    if (area != null && !player.isDead()) {
                                        area.sendNextStrip();
                                    }
                                    else {
                                        if (area != null || player.isDead() || player.isTeleporting()) {
                                            continue;
                                        }
                                        Server.logger.log(Level.WARNING, "VisionArea is null for player " + player.getName() + ". Removing from login.");
                                        it.remove();
                                    }
                                }
                                catch (Exception ex) {
                                    Server.logger.log(Level.INFO, ex.getMessage(), ex);
                                    it.remove();
                                }
                            }
                        }
                        catch (NoSuchPlayerException nsp) {
                            Server.logger.log(Level.INFO, nsp.getMessage(), nsp);
                            it.remove();
                        }
                    }
                    if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                        Server.logger.log(Level.INFO, "Lag detected at VisionArea (12): " + (System.currentTimeMillis() - check) / 1000.0f + " seconds");
                    }
                }
                check = System.currentTimeMillis();
                this.removeCreatures();
                if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                    Server.logger.log(Level.INFO, "Lag detected at removeCreatures (13.5): " + (System.currentTimeMillis() - check) / 1000.0f);
                }
                Server.counter = 0;
                this.pollWebCommands();
            }
            check = System.currentTimeMillis();
            MessageServer.sendMessages();
            if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                Server.logger.log(Level.INFO, "Lag detected at sendMessages (14): " + (System.currentTimeMillis() - check) / 1000.0f);
            }
            check = System.currentTimeMillis();
            this.sendFinals();
            if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                Server.logger.log(Level.INFO, "Lag detected at sendFinals (15): " + (System.currentTimeMillis() - check) / 1000.0f);
            }
            check = System.currentTimeMillis();
            this.socketServer.tick();
            final int realTicks = (int)(now - Server.startTime) / 25;
            Server.totalTicks = realTicks - Server.totalTicks;
            if (--Server.commPollCounter <= 0) {
                this.pollComms(now);
                Server.commPollCounter = Server.commPollCounterInit;
            }
            Server.totalTicks = realTicks;
            if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                Server.logger.log(Level.INFO, "Lag detected at socketserver.tick (15.5): " + (System.currentTimeMillis() - check) / 1000.0f);
                Server.logger.log(Level.INFO, "Numcommands=" + Communicator.getNumcommands() + ", last=" + Communicator.getLastcommand() + ", prev=" + Communicator.getPrevcommand() + " target=" + Communicator.getCommandAction() + ", Message=" + Communicator.getCommandMessage());
                Server.logger.log(Level.INFO, "Size of connections=" + this.socketServer.getNumberOfConnections() + " logins=" + LoginHandler.logins + ", redirs=" + LoginHandler.redirects + " exceptions=" + Server.exceptions);
            }
            LoginHandler.logins = 0;
            LoginHandler.redirects = 0;
            Server.exceptions = 0;
            check = System.currentTimeMillis();
            this.pollIntraCommands();
            if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                Server.logger.log(Level.INFO, "Lag detected at pollintracommands (15.8): " + (System.currentTimeMillis() - check) / 1000.0f);
            }
            try {
                check = System.currentTimeMillis();
                this.intraServer.socketServer.tick();
                if (System.currentTimeMillis() - check > Constants.lagThreshold) {
                    Server.logger.log(Level.INFO, "Lag detected at intraServer.tick (16): " + (System.currentTimeMillis() - check) / 1000.0f);
                }
            }
            catch (IOException iox1) {
                Server.logger.log(Level.INFO, "Failed to update intraserver.", iox1);
            }
            final long runLoopTime = System.currentTimeMillis() - now;
            if (runLoopTime > 1000L) {
                Server.secondsLag += (int)(runLoopTime / 1000L);
                Server.logger.info("Elapsed time (" + runLoopTime + "ms) for this loop was more than 1 second so adding it to the lag count, which is now: " + Server.secondsLag);
            }
        }
        catch (IOException e1) {
            Server.logger.log(Level.INFO, "Failed to update updserver", e1);
        }
        catch (Throwable t) {
            Server.logger.log(Level.SEVERE, t.getMessage(), t);
            if (t.getMessage() == null && t.getCause() == null) {
                Server.logger.log(Level.SEVERE, "Server is shutting down but there is no information in the Exception so creating a new one", new Exception());
            }
            this.shutDown();
        }
        finally {
            if (Server.logger.isLoggable(Level.FINEST)) {}
        }
        this.steamHandler.update();
    }
    
    private final void pollComms(final long now) {
        final long check = System.currentTimeMillis();
        final Map<String, Player> playerMap = Players.getInstance().getPlayerMap();
        for (final Map.Entry<String, Player> mapEntry : playerMap.entrySet()) {
            if (mapEntry.getValue().getCommunicator() != null) {
                for (int xm = 0; xm < 10 && mapEntry.getValue().getCommunicator().getMoves() > 0 && mapEntry.getValue().getCommunicator().getAvailableMoves() > 0; ++xm) {
                    if (mapEntry.getValue().getCommunicator().pollNextMove()) {
                        mapEntry.getValue().getCommunicator().setAvailableMoves(mapEntry.getValue().getCommunicator().getAvailableMoves() - 1);
                    }
                }
                if (!mapEntry.getValue().moveWarned && (mapEntry.getValue().getCommunicator().getMoves() > 240 || mapEntry.getValue().getCommunicator().getMoves() < -240)) {
                    if (mapEntry.getValue().getPower() >= 5) {
                        mapEntry.getValue().getCommunicator().sendAlertServerMessage("Moves at " + mapEntry.getValue().getCommunicator().getMoves());
                    }
                    else {
                        mapEntry.getValue().getCommunicator().sendAlertServerMessage("Your position on the server is not updated. Please move slower.");
                    }
                    mapEntry.getValue().moveWarned = true;
                    mapEntry.getValue().moveWarnedTime = System.currentTimeMillis();
                }
                else if (mapEntry.getValue().moveWarned && mapEntry.getValue().getCommunicator().getMoves() > -24 && mapEntry.getValue().getCommunicator().getMoves() < 24) {
                    mapEntry.getValue().getCommunicator().sendSafeServerMessage("Your position on the server is now updated.");
                    final long seconds = (System.currentTimeMillis() - mapEntry.getValue().moveWarnedTime) / 1000L;
                    Server.logger.log(Level.INFO, mapEntry.getValue().getName() + " moves down to " + mapEntry.getValue().getCommunicator().getMoves() + ". Was lagging " + seconds + " seconds with a peak of " + mapEntry.getValue().peakMoves + " moves.");
                    mapEntry.getValue().moveWarned = false;
                    mapEntry.getValue().peakMoves = 0L;
                    mapEntry.getValue().moveWarnedTime = 0L;
                }
                else if (mapEntry.getValue().moveWarned && (mapEntry.getValue().getCommunicator().getMoves() > 1440 || mapEntry.getValue().getCommunicator().getMoves() < -1440)) {
                    mapEntry.getValue().getCommunicator().sendAlertServerMessage("You are out of synch with the server. Please stand still.");
                }
                if (mapEntry.getValue().getCommunicator().getMoves() > 240) {
                    if (mapEntry.getValue().peakMoves >= mapEntry.getValue().getCommunicator().getMoves()) {
                        continue;
                    }
                    mapEntry.getValue().peakMoves = mapEntry.getValue().getCommunicator().getMoves();
                }
                else {
                    if (mapEntry.getValue().getCommunicator().getMoves() >= -240 || mapEntry.getValue().peakMoves <= mapEntry.getValue().getCommunicator().getMoves()) {
                        continue;
                    }
                    mapEntry.getValue().peakMoves = mapEntry.getValue().getCommunicator().getMoves();
                }
            }
        }
        final long time = System.currentTimeMillis() - this.lastTicked;
        if (time <= 3L) {
            ++Server.lagticks;
        }
        this.lastTicked = System.currentTimeMillis();
        if (System.currentTimeMillis() - check > Constants.lagThreshold) {
            Server.logger.log(Level.INFO, "Lag detected at Player Moves (13): " + (System.currentTimeMillis() - check) / 1000.0f);
        }
    }
    
    private final void pollSurfaceWater() {
        if (this.waterThread != null) {
            this.waterThread.propagateChanges();
        }
    }
    
    public void pollShopDemands() {
        final Shop[] shops2;
        final Shop[] shops = shops2 = Economy.getEconomy().getShops();
        for (final Shop lShop : shops2) {
            lShop.getLocalSupplyDemand().lowerDemands();
        }
        LocalSupplyDemand.increaseAllDemands();
        Economy.getEconomy().pollTraderEarnings();
    }
    
    public static void addNewPlayer(final String name) {
        if (System.currentTimeMillis() - Server.lastResetNewPremiums > 10800000L) {
            Server.newPremiums = 0;
            Server.lastResetNewPremiums = System.currentTimeMillis();
        }
        ++Server.newPremiums;
    }
    
    public static final void addNewbie() {
        ++Server.newbies;
    }
    
    public static final void addExpiry() {
        ++Server.expiredPremiums;
    }
    
    private void sendFinals() {
        if (Server.FINAL_LOGINS_RW_LOCK.writeLock().tryLock()) {
            try {
                final ListIterator<Long> it = Server.finalLogins.listIterator();
                while (it.hasNext()) {
                    try {
                        final long pid = it.next();
                        final Player player = Players.getInstance().getPlayer(pid);
                        final int step = player.getLoginStep();
                        if (player.isNew()) {
                            if (player.hasLink()) {
                                int result = LoginHandler.createPlayer(player, step);
                                if (result == Integer.MAX_VALUE) {
                                    it.remove();
                                    if (!this.isPlayerReceivingTiles(player)) {
                                        this.playersAtLogin.add(new Long(player.getWurmId()));
                                    }
                                    player.setLoginHandler(null);
                                }
                                else if (result >= 0) {
                                    player.setLoginStep(++result);
                                }
                                else {
                                    player.setLoginHandler(null);
                                    it.remove();
                                }
                            }
                            else {
                                player.setLoginHandler(null);
                                it.remove();
                            }
                        }
                        else if (player.hasLink()) {
                            final LoginHandler handler = player.getLoginhandler();
                            if (handler != null) {
                                int result2 = handler.loadPlayer(player, step);
                                if (result2 == Integer.MAX_VALUE) {
                                    it.remove();
                                    if (!this.isPlayerReceivingTiles(player)) {
                                        this.playersAtLogin.add(new Long(player.getWurmId()));
                                    }
                                    player.setLoginHandler(null);
                                }
                                else if (result2 >= 0) {
                                    player.setLoginStep(++result2);
                                }
                                else {
                                    player.setLoginHandler(null);
                                    it.remove();
                                }
                            }
                            else {
                                it.remove();
                            }
                        }
                        else {
                            player.setLoginHandler(null);
                            it.remove();
                        }
                        player.getStatus().setMoving(false);
                        if (player.hasLink()) {
                            continue;
                        }
                        Players.getInstance().logoutPlayer(player);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Server.logger.log(Level.INFO, nsp.getMessage(), nsp);
                        it.remove();
                    }
                }
            }
            finally {
                Server.FINAL_LOGINS_RW_LOCK.writeLock().unlock();
            }
        }
    }
    
    public void addCreatureToPort(final Creature creature) {
        if (creature.isPlayer()) {
            Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().lock();
            try {
                if (!this.playersAtLogin.contains(new Long(creature.getWurmId()))) {
                    this.playersAtLogin.add(new Long(creature.getWurmId()));
                }
            }
            finally {
                Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().unlock();
            }
        }
    }
    
    @Override
    public void clientConnected(final SocketConnection serverConnection) {
        final HackerIp ip = LoginHandler.failedIps.get(serverConnection.getIp());
        if (ip != null) {
            if (System.currentTimeMillis() <= ip.mayTryAgain) {
                Server.logger.log(Level.INFO, ip.name + " Because of the repeated failures the conn may try again in " + getTimeFor(ip.mayTryAgain - System.currentTimeMillis()) + '.');
                serverConnection.disconnect();
                return;
            }
        }
        try {
            final LoginHandler login = new LoginHandler(serverConnection);
            serverConnection.setConnectionListener(login);
        }
        catch (Exception ex) {
            Server.logger.log(Level.SEVERE, "Failed to create login handler for serverConnection: " + serverConnection + '.', ex);
        }
    }
    
    public void addToPlayersAtLogin(final Player player) {
        if (WurmId.getType(player.getWurmId()) != 0) {
            Server.logger.log(Level.WARNING, "Adding " + player.getName() + " to playersAtLogin.", new Exception());
        }
        if (!this.isPlayerReceivingTiles(player)) {
            Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().lock();
            try {
                this.playersAtLogin.add(new Long(player.getWurmId()));
            }
            finally {
                Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().unlock();
            }
        }
    }
    
    public void addPlayer(final Player player) {
        Players.getInstance().addPlayer(player);
        if (player.isPaying()) {
            ++Server.logonsPrem;
        }
        ++Server.logons;
    }
    
    void addIp(final String ip) {
        if (!Server.ips.keySet().contains(ip)) {
            Server.ips.put(ip, Boolean.FALSE);
            ++Server.numips;
        }
        else {
            final Boolean newb = Server.ips.get(ip);
            if (!newb) {
                Server.ips.put(ip, Boolean.FALSE);
            }
        }
    }
    
    private void checkAlertMessages() {
        if (Server.timeBetweenAlertMess1 < Long.MAX_VALUE && Server.alertMessage1.length() > 0 && Server.lastAlertMess1 + Server.timeBetweenAlertMess1 < System.currentTimeMillis()) {
            this.broadCastAlert(Server.alertMessage1);
            Server.lastAlertMess1 = System.currentTimeMillis();
        }
        if (Server.timeBetweenAlertMess2 < Long.MAX_VALUE && Server.alertMessage2.length() > 0 && Server.lastAlertMess2 + Server.timeBetweenAlertMess2 < System.currentTimeMillis()) {
            this.broadCastAlert(Server.alertMessage2);
            Server.lastAlertMess2 = System.currentTimeMillis();
        }
        if (Server.timeBetweenAlertMess3 < Long.MAX_VALUE && Server.alertMessage3.length() > 0 && Server.lastAlertMess3 + Server.timeBetweenAlertMess3 < System.currentTimeMillis()) {
            this.broadCastAlert(Server.alertMessage3);
            Server.lastAlertMess3 = System.currentTimeMillis();
        }
        if (Server.timeBetweenAlertMess4 < Long.MAX_VALUE && Server.alertMessage4.length() > 0 && Server.lastAlertMess4 + Server.timeBetweenAlertMess4 < System.currentTimeMillis()) {
            this.broadCastAlert(Server.alertMessage4);
            Server.lastAlertMess4 = System.currentTimeMillis();
        }
    }
    
    public void startSendingFinals(final Player player) {
        Server.FINAL_LOGINS_RW_LOCK.writeLock().lock();
        try {
            Server.finalLogins.add(new Long(player.getWurmId()));
        }
        finally {
            Server.FINAL_LOGINS_RW_LOCK.writeLock().unlock();
        }
    }
    
    private boolean isPlayerReceivingTiles(final Player player) {
        Server.PLAYERS_AT_LOGIN_RW_LOCK.readLock().lock();
        try {
            return this.playersAtLogin.contains(new Long(player.getWurmId()));
        }
        finally {
            Server.PLAYERS_AT_LOGIN_RW_LOCK.readLock().unlock();
        }
    }
    
    @Override
    public void clientException(final SocketConnection conn, final Exception ex) {
        ++Server.exceptions;
        try {
            final Player player = Players.getInstance().getPlayer(conn);
            ++Server.lostConnections;
            if (this.playersAtLogin != null) {
                Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().lock();
                try {
                    this.playersAtLogin.remove(new Long(player.getWurmId()));
                }
                finally {
                    Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().unlock();
                }
            }
            if (Server.finalLogins != null) {
                Server.FINAL_LOGINS_RW_LOCK.writeLock().lock();
                try {
                    Server.finalLogins.remove(new Long(player.getWurmId()));
                }
                finally {
                    Server.FINAL_LOGINS_RW_LOCK.writeLock().unlock();
                }
            }
            player.setLink(false);
        }
        catch (Exception ex2) {
            final Player player2 = Players.getInstance().logout(conn);
            if (player2 != null) {
                if (this.playersAtLogin != null) {
                    Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().lock();
                    try {
                        this.playersAtLogin.remove(new Long(player2.getWurmId()));
                    }
                    finally {
                        Server.PLAYERS_AT_LOGIN_RW_LOCK.writeLock().unlock();
                    }
                }
                if (Server.finalLogins != null) {
                    Server.FINAL_LOGINS_RW_LOCK.writeLock().lock();
                    try {
                        Server.finalLogins.remove(new Long(player2.getWurmId()));
                    }
                    finally {
                        Server.FINAL_LOGINS_RW_LOCK.writeLock().unlock();
                    }
                }
                Server.logger.log(Level.INFO, player2.getName() + " lost link at exception 2");
            }
        }
    }
    
    public Creature getCreature(final long creatureId) throws NoSuchPlayerException, NoSuchCreatureException {
        Creature toReturn = null;
        if (WurmId.getType(creatureId) == 1) {
            toReturn = Creatures.getInstance().getCreature(creatureId);
        }
        else {
            toReturn = Players.getInstance().getPlayer(creatureId);
        }
        return toReturn;
    }
    
    public Creature getCreatureOrNull(final long creatureId) {
        if (WurmId.getType(creatureId) == 1) {
            return Creatures.getInstance().getCreatureOrNull(creatureId);
        }
        return Players.getInstance().getPlayerOrNull(creatureId);
    }
    
    public void addMessage(final Message message) {
        MessageServer.addMessage(message);
    }
    
    public void broadCastNormal(final String message) {
        this.broadCastNormal(message, true);
    }
    
    public void broadCastNormal(final String message, final boolean twit) {
        MessageServer.broadCastNormal(message);
        if (twit) {
            this.twitLocalServer(message);
        }
    }
    
    public void broadCastSafe(final String message) {
        this.broadCastSafe(message, true);
    }
    
    public void broadCastSafe(final String message, final boolean twit) {
        this.broadCastSafe(message, twit, (byte)0);
    }
    
    public void broadCastSafe(final String message, final boolean twit, final byte messageType) {
        MessageServer.broadCastSafe(message, messageType);
        if (twit) {
            this.twitLocalServer(message);
        }
    }
    
    public void broadCastAlert(final String message) {
        this.broadCastAlert(message, true);
    }
    
    public void broadCastAlert(final String message, final boolean twit) {
        this.broadCastAlert(message, twit, (byte)0);
    }
    
    public void broadCastAlert(final String message, final boolean twit, final byte messageType) {
        MessageServer.broadCastAlert(message, messageType);
        if (twit) {
            this.twitLocalServer(message);
        }
    }
    
    public void broadCastAction(final String message, final Creature performer, final int tileDist, final boolean combat) {
        MessageServer.broadCastAction(message, performer, null, tileDist, combat);
    }
    
    public void broadCastAction(final String message, final Creature performer, final int tileDist) {
        MessageServer.broadCastAction(message, performer, tileDist);
    }
    
    public void broadCastAction(final String message, final Creature performer, final Creature receiver, final int tileDist) {
        MessageServer.broadCastAction(message, performer, receiver, tileDist);
    }
    
    public void broadCastAction(final String message, final Creature performer, final Creature receiver, final int tileDist, final boolean combat) {
        MessageServer.broadCastAction(message, performer, receiver, tileDist, combat);
    }
    
    public void broadCastMessage(final String message, final int tilex, final int tiley, final boolean surfaced, final int tiledistance) {
        MessageServer.broadCastMessage(message, tilex, tiley, surfaced, tiledistance);
    }
    
    private void loadCaveMesh() {
        final long start = System.nanoTime();
        try {
            Server.caveMesh = MeshIO.open(ServerDirInfo.getFileDBPath() + "map_cave.map");
        }
        catch (IOException iex) {
            Server.logger.log(Level.SEVERE, "Cavemap doesn't exist... initializing... size will be " + (1 << Constants.meshSize) + "!");
            try {
                Constants.caveImg = true;
                final int msize = (1 << Constants.meshSize) * (1 << Constants.meshSize);
                final int[] caveArr = new int[msize];
                for (int x = 0; x < msize; ++x) {
                    if (x % 100000 == 0) {
                        Server.logger.log(Level.INFO, "Created " + x + " tiles out of " + msize);
                    }
                    caveArr[x] = Tiles.encode((short)(-100), TileRockBehaviour.prospect(x & (1 << Constants.meshSize) - 1, x >> Constants.meshSize, false), (byte)0);
                }
                Server.caveMesh = MeshIO.createMap(ServerDirInfo.getFileDBPath() + "map_cave.map", Constants.meshSize, caveArr);
            }
            catch (IOException iox) {
                Server.logger.log(Level.INFO, "Failed to initialize caves. Exiting. " + iox.getMessage(), iox);
                System.exit(0);
            }
            catch (ArrayIndexOutOfBoundsException ex2) {
                Server.logger.log(Level.WARNING, "Failed to initialize caves. Exiting. " + ex2.getMessage(), ex2);
                System.exit(0);
            }
            catch (Exception ex3) {
                Server.logger.log(Level.WARNING, "Failed to initialize caves. Exiting. " + ex3.getMessage(), ex3);
                System.exit(0);
            }
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Server.logger.info("Loading cave mesh, size: " + Server.caveMesh.getSize() + " took " + lElapsedTime + " ms");
        }
        if (Constants.reprospect) {
            TileRockBehaviour.reProspect();
        }
        if (Constants.caveImg) {
            ZonesUtility.saveAsImg(Server.caveMesh);
            Server.logger.log(Level.INFO, "Saved cave mesh as img");
        }
    }
    
    private void loadWorldMesh() {
        final long start = System.nanoTime();
        try {
            Server.surfaceMesh = MeshIO.open(ServerDirInfo.getFileDBPath() + "top_layer.map");
        }
        catch (IOException iex) {
            Server.logger.log(Level.SEVERE, "Worldmap " + ServerDirInfo.getFileDBPath() + "top_layer.map doesn't exist.. Shutting down..", iex);
            System.exit(0);
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Server.logger.info("Loading world mesh, size: " + Server.surfaceMesh.getSize() + " took " + lElapsedTime + " ms");
        }
    }
    
    private void loadRockMesh() {
        final long start = System.nanoTime();
        try {
            Server.rockMesh = MeshIO.open(ServerDirInfo.getFileDBPath() + "rock_layer.map");
        }
        catch (IOException iex) {
            Server.logger.log(Level.SEVERE, "Worldmap " + ServerDirInfo.getFileDBPath() + "rock_layer.map doesn't exist.. Shutting down..", iex);
            System.exit(0);
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Server.logger.info("Loading rock mesh, size: " + Server.rockMesh.getSize() + " took " + lElapsedTime + " ms");
        }
    }
    
    public static int getCaveResource(final int tilex, final int tiley) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        final int toReturn = value >> 16 & 0xFFFF;
        return toReturn;
    }
    
    public static void setCaveResource(final int tilex, final int tiley, final int newValue) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        if ((value >> 16 & 0xFFFF) != newValue) {
            Server.resourceMesh.setTile(tilex, tiley, ((newValue & 0xFFFF) << 16) + (value & 0xFFFF));
        }
    }
    
    public static int getWorldResource(final int tilex, final int tiley) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        final int toReturn = value & 0xFFFF;
        return toReturn;
    }
    
    public static void setWorldResource(final int tilex, final int tiley, final int newValue) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        if ((value & 0xFFFF) != newValue) {
            Server.resourceMesh.setTile(tilex, tiley, (value & 0xFFFF0000) + (newValue & 0xFFFF));
        }
    }
    
    public static int getDigCount(final int tilex, final int tiley) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        final int digCount = value & 0xFF;
        return digCount;
    }
    
    public static void setDigCount(final int tilex, final int tiley, final int newValue) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        if ((value & 0xFF) != newValue) {
            Server.resourceMesh.setTile(tilex, tiley, (value & 0xFFFFFF00) + (newValue & 0xFF));
        }
    }
    
    public static int getPotionQLCount(final int tilex, final int tiley) {
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        final int pQLCount = (value & 0xFF00) >> 8;
        if (pQLCount == 255) {
            return 0;
        }
        return pQLCount;
    }
    
    public static void setPotionQLCount(final int tilex, final int tiley, final int newValue) {
        final int pQLCount = newValue << 8;
        final int value = Server.resourceMesh.getTile(tilex, tiley);
        if ((value & 0xFF00) != pQLCount) {
            Server.resourceMesh.setTile(tilex, tiley, (value & 0xFFFF00FF) + (pQLCount & 0xFF00));
        }
    }
    
    public static boolean isBotanizable(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x80) == 0x80;
    }
    
    public static void setBotanizable(final int tilex, final int tiley, final boolean isBotanizable) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isBotanizable ? 128 : 0;
        if ((value & 0x80) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFF7F) | newValue);
        }
    }
    
    public static boolean isForagable(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x40) == 0x40;
    }
    
    public static void setForagable(final int tilex, final int tiley, final boolean isForagable) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isForagable ? 64 : 0;
        if ((value & 0x40) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFFBF) | newValue);
        }
    }
    
    public static boolean isGatherable(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x20) == 0x20;
    }
    
    public static void setGatherable(final int tilex, final int tiley, final boolean isGather) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isGather ? 32 : 0;
        if ((value & 0x20) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFFDF) | newValue);
        }
    }
    
    public static boolean isInvestigatable(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x10) == 0x10;
    }
    
    public static void setInvestigatable(final int tilex, final int tiley, final boolean isInvestigate) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isInvestigate ? 16 : 0;
        if ((value & 0x10) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFFEF) | newValue);
        }
    }
    
    public static boolean isCheckHive(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x400) == 0x400;
    }
    
    public static void setCheckHive(final int tilex, final int tiley, final boolean isChecked) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isChecked ? 1024 : 0;
        if ((value & 0x400) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFBFF) | newValue);
        }
    }
    
    public static boolean wasTransformed(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x100) == 0x100;
    }
    
    public static void setTransformed(final int tilex, final int tiley, final boolean isTransformed) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isTransformed ? 256 : 0;
        if ((value & 0x100) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFEFF) | newValue);
        }
    }
    
    public static boolean isBeingTransformed(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x200) == 0x200;
    }
    
    public static void setBeingTransformed(final int tilex, final int tiley, final boolean isTransformed) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = isTransformed ? 512 : 0;
        if ((value & 0x200) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFDFF) | newValue);
        }
    }
    
    public static boolean hasGrubs(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (value & 0x800) == 0x800;
    }
    
    public static void setGrubs(final int tilex, final int tiley, final boolean grubs) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        final int newValue = grubs ? 2048 : 0;
        if ((value & 0x800) != newValue) {
            Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFF7FF) | newValue);
        }
    }
    
    public static byte getClientSurfaceFlags(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (byte)(value & 0xFF);
    }
    
    public static byte getServerSurfaceFlags(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (byte)(value >>> 8 & 0xFF);
    }
    
    public static byte getServerCaveFlags(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (byte)(value >>> 24 & 0xFF);
    }
    
    public static byte getClientCaveFlags(final int tilex, final int tiley) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        return (byte)(value >>> 16 & 0xFF);
    }
    
    public static void setServerCaveFlags(final int tilex, final int tiley, final byte newByte) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        Server.flagsMesh.setTile(tilex, tiley, (value & 0xFFFFFF) | (newByte & 0xFF) << 24);
    }
    
    public static void setClientCaveFlags(final int tilex, final int tiley, final byte newByte) {
        final int value = Server.flagsMesh.getTile(tilex, tiley);
        Server.flagsMesh.setTile(tilex, tiley, (value & 0xFF00FFFF) | (newByte & 0xFF) << 16);
    }
    
    public static void setSurfaceTile(@Nonnull final TilePos tilePos, final short newHeight, final byte newTileType, final byte newTileData) {
        setSurfaceTile(tilePos.x, tilePos.y, newHeight, newTileType, newTileData);
    }
    
    public static void setSurfaceTile(final int tilex, final int tiley, final short newHeight, final byte newTileType, final byte newTileData) {
        final int oldTile = Server.surfaceMesh.getTile(tilex, tiley);
        final byte oldType = Tiles.decodeType(oldTile);
        if (oldType != newTileType) {
            modifyFlagsByTileType(tilex, tiley, newTileType);
        }
        Server.surfaceMesh.setTile(tilex, tiley, Tiles.encode(newHeight, newTileType, newTileData));
    }
    
    public static void modifyFlagsByTileType(final int tilex, final int tiley, final byte newTileType) {
        final Tiles.Tile theNewTile = Tiles.getTile(newTileType);
        if (!theNewTile.canBotanize()) {
            setBotanizable(tilex, tiley, false);
        }
        if (!theNewTile.canForage()) {
            setForagable(tilex, tiley, false);
        }
        setGatherable(tilex, tiley, false);
        setBeingTransformed(tilex, tiley, false);
        setTransformed(tilex, tiley, false);
    }
    
    public static boolean canBotanize(final byte type) {
        return type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_MARSH.id || type == Tiles.Tile.TILE_MOSS.id || type == Tiles.Tile.TILE_PEAT.id || Tiles.isNormalBush(type) || Tiles.isNormalTree(type);
    }
    
    public static boolean canForage(final byte type) {
        return type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_TUNDRA.id || type == Tiles.Tile.TILE_MARSH.id || Tiles.isNormalBush(type) || Tiles.isNormalTree(type);
    }
    
    public static boolean canBearFruit(final byte type) {
        return Tiles.isTree(type) || Tiles.isBush(type);
    }
    
    public void shutDown(final String aReason, final Throwable aCause) {
        try {
            Server.logger.log(Level.INFO, "Shutting down the server - reason: " + aReason);
            Server.logger.log(Level.INFO, "Shutting down the server - cause: ", aCause);
        }
        finally {
            this.shutDown();
        }
    }
    
    public void shutDown() {
        if (ServerProperties.getBoolean("ENABLE_PNP_PORT_FORWARD", Constants.enablePnpPortForward)) {
            UPNPService.shutdown();
        }
        Creatures.getInstance().shutDownPolltask();
        Creature.shutDownPathFinders();
        Server.logger.log(Level.INFO, "Shutting down at: ", new Exception());
        if (this.highwayFinderThread != null) {
            Server.logger.info("Shutting down - Stopping HighwayFinder");
            this.highwayFinderThread.shouldStop();
        }
        ServerProjectile.clear();
        Server.logger.info("Shutting down - Polling Battles");
        if (Constants.isGameServer) {
            Battles.poll(true);
        }
        Zones.saveProtectedTiles();
        Server.logger.info("Shutting down - Saving Players");
        Players.getInstance().savePlayersAtShutdown();
        Server.logger.info("Shutting down - Clearing Item Database Batches");
        DbItem.clearBatches();
        Server.logger.info("Shutting down - Saving Creatures");
        Server.logger.info("Shutting down - Clearing Creature Database Batches");
        for (final Creature c : Creatures.getInstance().getCreatures()) {
            if (c.getStatus().getPosition() != null && c.getStatus().getPosition().isChanged()) {
                try {
                    c.getStatus().savePosition(c.getWurmId(), false, c.getStatus().getZoneId(), true);
                }
                catch (IOException iox) {
                    Server.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
            }
        }
        if (Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase) {
            CreaturePos.getCreatureDbPosUpdater().saveImmediately();
        }
        CreaturePos.clearBatches();
        Server.logger.info("Shutting down - Saving all creatures");
        Creatures.getInstance().saveCreatures();
        Server.logger.info("Shutting down - Saving All Zones");
        Zones.saveAllZones();
        if (this.scheduledExecutorService != null && !this.scheduledExecutorService.isShutdown()) {
            this.scheduledExecutorService.shutdown();
        }
        Server.logger.info("Shutting down - Saving Surface Mesh");
        try {
            Server.surfaceMesh.saveAll();
            Server.surfaceMesh.close();
        }
        catch (IOException iox2) {
            Server.logger.log(Level.WARNING, "Failed to save surfacemesh!", iox2);
        }
        Server.logger.info("Shutting down - Saving Rock Mesh");
        try {
            Server.rockMesh.saveAll();
            Server.rockMesh.close();
        }
        catch (IOException iox2) {
            Server.logger.log(Level.WARNING, "Failed to save rockmesh!", iox2);
        }
        Server.logger.info("Shutting down - Saving Cave Mesh");
        try {
            Server.caveMesh.saveAll();
            Server.caveMesh.close();
        }
        catch (IOException iox2) {
            Server.logger.log(Level.WARNING, "Failed to save cavemesh!", iox2);
        }
        Server.logger.info("Shutting down - Saving Resource Mesh");
        try {
            Server.resourceMesh.saveAll();
            Server.resourceMesh.close();
        }
        catch (IOException iox2) {
            Server.logger.log(Level.WARNING, "Failed to save resourcemesh!", iox2);
        }
        Server.logger.info("Shutting down - Saving Flags Mesh");
        try {
            Server.flagsMesh.saveAll();
            Server.flagsMesh.close();
        }
        catch (IOException iox2) {
            Server.logger.log(Level.WARNING, "Failed to save flagsmesh!", iox2);
        }
        if (this.waterThread != null) {
            Server.logger.info("Shutting down - Saving Water Mesh");
            this.waterThread.shouldStop = true;
        }
        Server.logger.info("Shutting down - Saving Constants");
        Constants.crashed = false;
        Constants.save();
        Server.logger.info("Shutting down - Saving WurmID Numbers");
        WurmId.updateNumbers();
        this.steamHandler.closeServer();
        Server.logger.info("Shutting down - Closing Database Connections");
        DbConnector.closeAll();
        Server.logger.log(Level.INFO, "The server shut down nicely. Wurmcalendar time is " + WurmCalendar.currentTime);
        System.exit(0);
    }
    
    private void loadResourceMesh() {
        final long start = System.nanoTime();
        try {
            Server.resourceMesh = MeshIO.open(ServerDirInfo.getFileDBPath() + "resources.map");
        }
        catch (IOException iex) {
            Server.logger.log(Level.INFO, "resources doesn't exist.. creating..");
            final int[] resourceArr = new int[(1 << Constants.meshSize) * (1 << Constants.meshSize)];
            for (int x = 0; x < (1 << Constants.meshSize) * (1 << Constants.meshSize); ++x) {
                resourceArr[x] = -1;
            }
            try {
                Server.resourceMesh = MeshIO.createMap(ServerDirInfo.getFileDBPath() + "resources.map", Constants.meshSize, resourceArr);
            }
            catch (IOException iox) {
                Server.logger.log(Level.SEVERE, "Failed to create resources. Exiting.", iox);
                System.exit(0);
            }
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Server.logger.info("Loading resource mesh, size: " + Server.resourceMesh.getSize() + " took " + lElapsedTime + " ms");
        }
    }
    
    private void loadFlagsMesh() {
        final long start = System.nanoTime();
        try {
            Server.flagsMesh = MeshIO.open(ServerDirInfo.getFileDBPath() + "flags.map");
            final int first = Server.flagsMesh.getTile(0, 0);
            if ((first & 0xFFFFFF00) == 0xFFFFFF00) {
                Server.logger.log(Level.INFO, "converting flags.");
                for (int x = 0; x < 1 << Constants.meshSize; ++x) {
                    for (int y = 0; y < 1 << Constants.meshSize; ++y) {
                        int value = Server.flagsMesh.getTile(x, y) & 0xFF;
                        final int serverSurfaceFlag = value & 0xF;
                        value |= serverSurfaceFlag << 8;
                        value &= 0xFFF0;
                        Server.flagsMesh.setTile(x, y, value);
                    }
                }
            }
        }
        catch (IOException iex) {
            Server.logger.log(Level.INFO, "flags doesn't exist.. creating..");
            final int[] resourceArr = new int[(1 << Constants.meshSize) * (1 << Constants.meshSize)];
            for (int x2 = 0; x2 < (1 << Constants.meshSize) * (1 << Constants.meshSize); ++x2) {
                resourceArr[x2] = 0;
            }
            try {
                Server.flagsMesh = MeshIO.createMap(ServerDirInfo.getFileDBPath() + "flags.map", Constants.meshSize, resourceArr);
                this.needSeeds = true;
            }
            catch (IOException iox) {
                Server.logger.log(Level.SEVERE, "Failed to create flags. Exiting.", iox);
                System.exit(0);
            }
        }
        finally {
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            Server.logger.info("Loading flags mesh, size: " + Server.flagsMesh.getSize() + " took " + lElapsedTime + " ms");
        }
    }
    
    public static final void addPendingAward(final PendingAward award) {
        Server.pendingAwards.add(award);
    }
    
    private static final void pollPendingAwards() {
        for (final PendingAward award : Server.pendingAwards) {
            award.award();
        }
        Server.pendingAwards.clear();
    }
    
    public static final String getTimeFor(final long aTime) {
        String times = "";
        if (aTime < 60000L) {
            final long secs = aTime / 1000L;
            times = times + secs + ((secs == 1L) ? " second" : " seconds");
        }
        else {
            final long daysleft = aTime / 86400000L;
            final long hoursleft = (aTime - daysleft * 86400000L) / 3600000L;
            final long minutesleft = (aTime - daysleft * 86400000L - hoursleft * 3600000L) / 60000L;
            if (daysleft > 0L) {
                times = times + daysleft + ((daysleft == 1L) ? " day" : " days");
            }
            if (hoursleft > 0L) {
                String aft = "";
                if (daysleft > 0L && minutesleft > 0L) {
                    times += ", ";
                    aft += " and ";
                }
                else if (daysleft > 0L) {
                    times += " and ";
                }
                else if (minutesleft > 0L) {
                    aft += " and ";
                }
                times = times + hoursleft + ((hoursleft == 1L) ? " hour" : " hours") + aft;
            }
            if (minutesleft > 0L) {
                String aft = "";
                if (daysleft > 0L && hoursleft == 0L) {
                    aft = " and ";
                }
                times = times + aft + minutesleft + ((minutesleft == 1L) ? " minute" : " minutes");
            }
        }
        if (times.length() == 0) {
            times = "nothing";
        }
        return times;
    }
    
    public void transaction(final long itemId, final long oldownerid, final long newownerid, final String reason, final long value) {
        Economy.getEconomy().transaction(itemId, oldownerid, newownerid, reason, value);
    }
    
    private void pollIntraCommands() {
        try {
            if (Server.INTRA_COMMANDS_RW_LOCK.writeLock().tryLock()) {
                try {
                    final IntraCommand[] comms = this.intraCommands.toArray(new IntraCommand[this.intraCommands.size()]);
                    for (int x = 0; x < comms.length; ++x) {
                        if (x < 40 && comms[x].poll()) {
                            this.intraCommands.remove(comms[x]);
                        }
                    }
                }
                finally {
                    Server.INTRA_COMMANDS_RW_LOCK.writeLock().unlock();
                }
            }
            final MoneyTransfer[] transfers = MoneyTransfer.transfers.toArray(new MoneyTransfer[MoneyTransfer.transfers.size()]);
            for (int x = 0; x < transfers.length; ++x) {
                if (transfers[x].poll() && (transfers[x].deleted || transfers[x].pollTimes > 500)) {
                    Server.logger.log(Level.INFO, "Polling MoneyTransfer " + x + " deleted: " + transfers[x]);
                    MoneyTransfer.transfers.remove(transfers[x]);
                }
            }
            final TimeTransfer[] array;
            final TimeTransfer[] ttransfers = array = TimeTransfer.transfers.toArray(new TimeTransfer[TimeTransfer.transfers.size()]);
            for (final TimeTransfer lTtransfer : array) {
                if (lTtransfer.poll() && lTtransfer.deleted) {
                    Server.logger.log(Level.INFO, "Polling tt deleted");
                    TimeTransfer.transfers.remove(lTtransfer);
                }
            }
            final PasswordTransfer[] array2;
            final PasswordTransfer[] ptransfers = array2 = PasswordTransfer.transfers.toArray(new PasswordTransfer[PasswordTransfer.transfers.size()]);
            for (final PasswordTransfer lPtransfer : array2) {
                if (lPtransfer.poll() && lPtransfer.deleted) {
                    PasswordTransfer.transfers.remove(lPtransfer);
                }
            }
        }
        catch (Exception ex) {
            Server.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    public void addIntraCommand(final IntraCommand command) {
        Server.INTRA_COMMANDS_RW_LOCK.writeLock().lock();
        try {
            this.intraCommands.add(command);
        }
        finally {
            Server.INTRA_COMMANDS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public void addWebCommand(final WebCommand command) {
        try {
            Server.WEBCOMMANDS_RW_LOCK.writeLock().lock();
            Server.webcommands.add(command);
        }
        finally {
            Server.WEBCOMMANDS_RW_LOCK.writeLock().unlock();
        }
    }
    
    private void pollWebCommands() {
        try {
            Server.WEBCOMMANDS_RW_LOCK.writeLock().lock();
            for (final WebCommand wc : Server.webcommands) {
                wc.execute();
            }
            Server.webcommands.clear();
        }
        finally {
            Server.WEBCOMMANDS_RW_LOCK.writeLock().unlock();
        }
    }
    
    public void addTerraformingTask(final TerraformingTask task) {
        try {
            Server.TERRAFORMINGTASKS_RW_LOCK.writeLock().lock();
            Server.terraformingTasks.add(task);
        }
        finally {
            Server.TERRAFORMINGTASKS_RW_LOCK.writeLock().unlock();
        }
    }
    
    private void pollTerraformingTasks() {
        try {
            Server.TERRAFORMINGTASKS_RW_LOCK.writeLock().lock();
            final TerraformingTask[] array;
            final TerraformingTask[] tasks = array = Server.terraformingTasks.toArray(new TerraformingTask[Server.terraformingTasks.size()]);
            for (final TerraformingTask task : array) {
                if (task.poll()) {
                    Server.terraformingTasks.remove(task);
                }
            }
        }
        finally {
            Server.TERRAFORMINGTASKS_RW_LOCK.writeLock().unlock();
        }
    }
    
    @Override
    public byte[] getExternalIp() {
        return this.externalIp;
    }
    
    @Override
    public byte[] getInternalIp() {
        return this.internalIp;
    }
    
    @Override
    public int getIntraServerPort() {
        return Integer.parseInt(Servers.localServer.INTRASERVERPORT);
    }
    
    public static short getMolRehanX() {
        return Server.molRehanX;
    }
    
    public static void setMolRehanX(final short aMolRehanX) {
        Server.molRehanX = aMolRehanX;
    }
    
    public static short getMolRehanY() {
        return Server.molRehanY;
    }
    
    public static void setMolRehanY(final short aMolRehanY) {
        Server.molRehanY = aMolRehanY;
    }
    
    public static void incrementOldPremiums(final String name) {
        if (System.currentTimeMillis() - Server.lastResetOldPremiums > 10800000L) {
            Server.oldPremiums = 0;
            Server.lastResetOldPremiums = System.currentTimeMillis();
        }
        ++Server.oldPremiums;
        if (!Server.appointedSixThousand && (PlayerInfoFactory.getNumberOfPayingPlayers() + 1) % 1000 == 0) {
            Server.logger.log(Level.INFO, name + " IS THE NUMBER " + (PlayerInfoFactory.getNumberOfPayingPlayers() + 1) + " PAYING PLAYER");
            Server.appointedSixThousand = true;
        }
    }
    
    public static long getStartTime() {
        return Server.startTime;
    }
    
    public static long getMillisToShutDown() {
        return Server.millisToShutDown;
    }
    
    public static String getShutdownReason() {
        return Server.shutdownReason;
    }
    
    public static Weather getWeather() {
        return Server.weather;
    }
    
    public static int getCombatCounter() {
        return Server.combatCounter;
    }
    
    public static void incrementCombatCounter() {
        ++Server.combatCounter;
    }
    
    public static int getSecondsUptime() {
        return Server.secondsUptime;
    }
    
    public static void incrementSecondsUptime() {
        ++Server.secondsUptime;
        Players.getInstance().tickSecond();
        Server.lastLagticks = Server.lagticks;
        if (Server.lastLagticks > 0.0f) {
            Server.lagMoveModifier = (int)Math.max(10.0f, Server.lastLagticks / 30.0f * 24.0f);
        }
        else {
            Server.lagMoveModifier = 0;
        }
        Server.lagticks = 0;
        if (WurmCalendar.isNewYear1()) {
            Server.logger.log(Level.INFO, "IT's NEW YEAR");
            if (Server.secondsUptime % 20 == 0) {
                if (Server.rand.nextBoolean()) {
                    final Effect globalEffect = EffectFactory.getInstance().createSpawnEff(WurmId.getNextTempItemId(), Server.rand.nextFloat() * Zones.worldMeterSizeX, Server.rand.nextFloat() * Zones.worldMeterSizeY, 0.0f, true);
                    Server.newYearEffects.add(globalEffect.getId());
                    try {
                        ItemFactory.createItem(52, Server.rand.nextFloat() * 90.0f + 1.0f, globalEffect.getPosX(), globalEffect.getPosY(), globalEffect.getPosZ(), true, (byte)8, getRandomRarityNotCommon(), -10L, "", (byte)0);
                    }
                    catch (Exception ex) {}
                }
                else {
                    final Effect globalEffect = EffectFactory.getInstance().createChristmasEff(WurmId.getNextTempItemId(), Server.rand.nextFloat() * Zones.worldMeterSizeX, Server.rand.nextFloat() * Zones.worldMeterSizeY, 0.0f, true);
                    Server.newYearEffects.add(globalEffect.getId());
                    try {
                        ItemFactory.createItem(52, Server.rand.nextFloat() * 90.0f + 1.0f, globalEffect.getPosX(), globalEffect.getPosY(), globalEffect.getPosZ(), true, (byte)8, getRandomRarityNotCommon(), -10L, "", (byte)0);
                    }
                    catch (Exception ex2) {}
                }
            }
            if (Server.secondsUptime % 11 == 0) {
                Zones.sendNewYear();
            }
        }
        else if (WurmCalendar.isAfterNewYear1()) {
            if (Server.newYearEffects != null && !Server.newYearEffects.isEmpty()) {
                for (final Integer l : Server.newYearEffects) {
                    EffectFactory.getInstance().deleteEffect(l);
                }
            }
            if (Server.newYearEffects != null) {
                Server.newYearEffects.clear();
            }
        }
    }
    
    public static final byte getRandomRarityNotCommon() {
        if (Server.rand.nextFloat() * 10000.0f <= 1.0f) {
            return 3;
        }
        if (Server.rand.nextInt(100) <= 0) {
            return 2;
        }
        return 1;
    }
    
    private static void startSendWeatherThread() {
        new Thread() {
            @Override
            public void run() {
                Servers.sendWeather(Server.weather.getWindRotation(), Server.weather.getWindPower(), Server.weather.getWindDir());
            }
        }.start();
        Players.getInstance().sendWeather();
    }
    
    private static void addShutdownHook() {
        Server.logger.info("Adding Shutdown Hook");
        Runtime.getRuntime().addShutdownHook(new Thread("WurmServerShutdownHook-Thread") {
            @Override
            public void run() {
                Server.logger.info("\nWurm Server Shutdown hook is running\n");
                DbConnector.closeAll();
                ServerLauncher.stopLoggers();
                TradingWindow.stopLoggers();
                Players.stopLoggers();
            }
        });
    }
    
    public static final double getModifiedFloatEffect(final double eff) {
        if (Server.EpicServer) {
            double modEff = 0.0;
            if (eff >= 1.0) {
                if (eff <= 70.0) {
                    modEff = 1.3571428060531616 * eff;
                }
                else {
                    modEff = 0.949999988079071 + (eff - 70.0) * 0.1666666716337204;
                }
            }
            else {
                modEff = 1.0 - (1.0 - eff) * (1.0 - eff);
            }
            return modEff;
        }
        return eff;
    }
    
    public static final double getModifiedPercentageEffect(final double eff) {
        if (Server.EpicServer || Server.ChallengeServer) {
            double modEff = 0.0;
            if (eff >= 100.0) {
                if (eff <= 7000.0) {
                    modEff = 1.3571428060531616 * eff;
                }
                else {
                    modEff = 95.0 + (eff - 7000.0) * 0.1666666716337204;
                }
            }
            else {
                modEff = (10000.0 - (100.0 - eff) * (100.0 - eff)) / 100.0;
            }
            return modEff;
        }
        return eff;
    }
    
    public static final double getBuffedQualityEffect(final double eff) {
        if (eff < 1.0) {
            return Math.max(0.05, 1.0 - (1.0 - eff) * (1.0 - eff));
        }
        final double base = 2.0;
        final double pow1 = 1.3;
        final double pow2 = 3.0;
        final double newPower = 1.0 + base * (1.0 - Math.pow(2.0, -Math.pow(eff - 1.0, pow1) / pow2));
        return newPower;
    }
    
    public static final HexMap getEpicMap() {
        return Server.epicMap;
    }
    
    @Override
    public void broadCastEpicEvent(final String event) {
        Servers.localServer.createChampTwit(event);
    }
    
    @Override
    public void broadCastEpicWinCondition(final String scenarioname, final String scenarioQuest) {
        Servers.localServer.createChampTwit(scenarioname + " has begun. " + scenarioQuest);
    }
    
    public final boolean hasThunderMode() {
        return this.thunderMode;
    }
    
    public final short getLowDirtHeight(final int x, final int y) {
        final Integer xy = x | y << Constants.meshSize;
        if (Server.lowDirtHeight.containsKey(xy)) {
            return Server.lowDirtHeight.get(xy);
        }
        return Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y));
    }
    
    public static final boolean isDirtHeightLower(final int x, final int y, final short ht) {
        final Integer xy = x | y << Constants.meshSize;
        short cHt;
        if (Server.lowDirtHeight.containsKey(xy)) {
            cHt = Server.lowDirtHeight.get(xy);
            if (ht < cHt) {
                Server.lowDirtHeight.put(xy, ht);
            }
        }
        else {
            cHt = Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y));
            Server.lowDirtHeight.put(xy, (short)Math.min(cHt, ht));
        }
        return ht < cHt;
    }
    
    public boolean isPS() {
        return this.isPS;
    }
    
    public void setIsPS(final boolean ps) {
        this.isPS = ps;
    }
    
    public final void addGlobalTempEffect(final long id, final long expiretime) {
        Server.tempEffects.put(id, expiretime);
    }
    
    public static short getMaxHeight() {
        return Server.surfaceMesh.getMaxHeight();
    }
    
    public HighwayFinder getHighwayFinderThread() {
        return this.highwayFinderThread;
    }
    
    static {
        logger = Logger.getLogger(Server.class.getName());
        Server.instance = null;
        rand = new Random();
        SYNC_LOCK = new Object();
        Server.counter = 0;
        PLAYERS_AT_LOGIN_RW_LOCK = new ReentrantReadWriteLock();
        Server.locked = false;
        Server.molRehanX = 438;
        Server.molRehanY = 2142;
        Server.newPremiums = 0;
        Server.expiredPremiums = 0;
        Server.lastResetNewPremiums = 0L;
        Server.lastPolledSupplyDepots = 0L;
        Server.savedChallengePage = System.currentTimeMillis() + 120000L;
        Server.oldPremiums = 0;
        Server.lastResetOldPremiums = 0L;
        Server.creaturesToRemove = new ArrayList<Creature>();
        CREATURES_TO_REMOVE_RW_LOCK = new ReentrantReadWriteLock();
        webcommands = new HashSet<WebCommand>();
        terraformingTasks = new HashSet<TerraformingTask>();
        TERRAFORMINGTASKS_RW_LOCK = new ReentrantReadWriteLock();
        WEBCOMMANDS_RW_LOCK = new ReentrantReadWriteLock();
        Server.lagticks = 0;
        Server.lastLagticks = 0.0f;
        Server.lagMoveModifier = 0;
        Server.lastSentWarning = 0;
        Server.lastAwardedBattleCamps = System.currentTimeMillis();
        Server.startTime = System.currentTimeMillis();
        Server.lastSecond = System.currentTimeMillis();
        Server.lastPolledRubble = 0L;
        Server.lastPolledShopCultist = System.currentTimeMillis();
        Server.ips = new ConcurrentHashMap<String, Boolean>();
        Server.pendingAwards = new ConcurrentLinkedQueue<PendingAward>();
        Server.numips = 0;
        Server.logons = 0;
        Server.logonsPrem = 0;
        Server.newbies = 0;
        Server.millisToShutDown = Long.MIN_VALUE;
        Server.lastPinged = 0L;
        Server.lastDeletedPlayer = 0L;
        Server.lastLoweredRanks = System.currentTimeMillis() + 600000L;
        Server.shutdownReason = "Reason: unknown";
        Server.finalLogins = new ArrayList<Long>();
        FINAL_LOGINS_RW_LOCK = new ReentrantReadWriteLock();
        Server.pollCommunicators = false;
        Server.lastWeather = 0L;
        Server.lastArrow = 0L;
        Server.lastMailCheck = System.currentTimeMillis();
        Server.lastFaith = 0L;
        Server.lastRecruitmentPoll = 0L;
        Server.lastAwardedItems = System.currentTimeMillis();
        Server.lostConnections = 0;
        Server.totalTicks = 0;
        Server.commPollCounter = 0;
        Server.commPollCounterInit = 1;
        Server.lastPolledBanks = 0L;
        Server.lastPolledWater = 0L;
        Server.lastPolledHighwayFinder = 0L;
        weather = new Weather();
        INTRA_COMMANDS_RW_LOCK = new ReentrantReadWriteLock();
        Server.exceptions = 0;
        Server.secondsLag = 0;
        Server.alertMessage1 = "";
        Server.lastAlertMess1 = Long.MAX_VALUE;
        Server.alertMessage2 = "";
        Server.lastAlertMess2 = Long.MAX_VALUE;
        Server.alertMessage3 = "";
        Server.lastAlertMess3 = Long.MAX_VALUE;
        Server.alertMessage4 = "";
        Server.lastAlertMess4 = Long.MAX_VALUE;
        Server.timeBetweenAlertMess1 = Long.MAX_VALUE;
        Server.timeBetweenAlertMess2 = Long.MAX_VALUE;
        Server.timeBetweenAlertMess3 = Long.MAX_VALUE;
        Server.timeBetweenAlertMess4 = Long.MAX_VALUE;
        Server.lastPolledSkills = 0L;
        Server.lastPolledRifts = 0L;
        Server.lastResetAspirations = System.currentTimeMillis();
        Server.lastPolledTileEffects = System.currentTimeMillis();
        Server.lastResetTiles = System.currentTimeMillis();
        Server.combatCounter = 0;
        Server.secondsUptime = 0;
        Server.allowTradeCheat = true;
        Server.appointedSixThousand = false;
        Server.playersThroughTutorial = 0;
        Server.lowDirtHeight = new ConcurrentHashMap<Integer, Short>();
        Server.newYearEffects = new HashSet<Integer>();
        tempEffects = new ConcurrentHashMap<Long, Long>();
    }
}
