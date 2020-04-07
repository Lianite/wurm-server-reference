// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.Hashtable;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public final class Constants
{
    public static String dbHost;
    public static String dbPort;
    private static final boolean DEFAULT_DB_AUTO_MIGRATE = true;
    public static boolean dbAutoMigrate;
    public static boolean enabledMounts;
    public static boolean loadNpcs;
    public static boolean loadEndGameItems;
    public static boolean enableSpyPrevention;
    public static boolean enableAutoNetworking;
    public static boolean analyseAllDbTables;
    public static boolean checkAllDbTables;
    public static boolean optimiseAllDbTables;
    public static boolean useSplitCreaturesTable;
    public static boolean createTemporaryDatabaseIndicesAtStartup;
    public static boolean dropTemporaryDatabaseIndicesAtStartup;
    public static boolean usePrepStmts;
    public static boolean gatherDbStats;
    public static boolean checkWurmLogs;
    public static boolean startChallenge;
    public static int challengeDays;
    private static final boolean DEFAULT_IS_GAME_SERVER = true;
    public static boolean isGameServer;
    public static boolean isEigcEnabled;
    public static String dbUser;
    public static String dbPass;
    public static String dbDriver;
    public static String webPath;
    public static final boolean useDb = true;
    public static boolean usePooledDb;
    public static boolean trackOpenDatabaseResources;
    public static boolean enablePnpPortForward;
    private static Logger logger;
    private static Properties props;
    public static int numberOfDirtyMeshRowsToSaveEachCall;
    public static boolean useDirectByteBuffersForMeshIO;
    public static int meshSize;
    public static boolean createSeeds;
    public static boolean devmode;
    public static String motd;
    public static String skillTemplatesDBPath;
    public static String zonesDBPath;
    public static String itemTemplatesDBPath;
    public static String creatureTemplatesDBPath;
    public static String creatureStatsDBPath;
    public static String playerStatsDBPath;
    public static String itemStatsDBPath;
    public static String tileStatsDBPath;
    public static String itemOldStatsDBPath;
    public static String creatureOldStatsDBPath;
    public static boolean RUNBATCH;
    public static boolean maintaining;
    public static boolean useQueueToSendDataToPlayers;
    public static boolean useMultiThreadedBankPolling;
    public static boolean useScheduledExecutorToCountEggs;
    public static boolean useScheduledExecutorToSaveConstants;
    public static boolean useScheduledExecutorToSaveDirtyMeshRows;
    public static boolean useScheduledExecutorToSendTimeSync;
    public static boolean useScheduledExecutorToSwitchFatigue;
    public static boolean useScheduledExecutorToTickCalendar;
    public static boolean useScheduledExecutorToWriteLogs;
    public static boolean useScheduledExecutorForServer;
    public static int scheduledExecutorServiceThreads;
    public static String playerStatLog;
    public static String logonStatLog;
    public static String ipStatLog;
    public static String tutorialLog;
    public static String newbieStatLog;
    public static String totIpStatLog;
    public static String payingLog;
    public static String subscriptionLog;
    public static String moneyLog;
    public static String economyLog;
    public static String expiryLog;
    public static String lagLog;
    public static String retentionStatLog;
    public static String retentionPercentStatLog;
    public static boolean useItemTransferLog;
    public static boolean useTileEventLog;
    public static boolean useDatabaseForServerStatisticsLog;
    public static boolean useScheduledExecutorToUpdateCreaturePositionInDatabase;
    public static int numberOfDbCreaturePositionsToUpdateEachTime;
    public static boolean useScheduledExecutorToUpdatePlayerPositionInDatabase;
    public static int numberOfDbPlayerPositionsToUpdateEachTime;
    public static boolean useScheduledExecutorToUpdateItemDamageInDatabase;
    public static int numberOfDbItemDamagesToUpdateEachTime;
    public static boolean useScheduledExecutorToUpdateItemOwnerInDatabase;
    public static boolean useScheduledExecutorToUpdateItemLastOwnerInDatabase;
    public static boolean useScheduledExecutorToUpdateItemParentInDatabase;
    public static int numberOfDbItemOwnersToUpdateEachTime;
    public static boolean useScheduledExecutorToConnectToTwitter;
    public static boolean useScheduledExecutorForTrello;
    public static long lagThreshold;
    static boolean crashed;
    public static boolean respawnUniques;
    public static boolean pruneDb;
    public static boolean reprospect;
    public static boolean caveImg;
    public static long minMillisBetweenPlayerConns;
    public static String trelloBoardid;
    public static String trelloApiKey;
    public static String trelloToken;
    public static String trelloMVBoardId;
    private static final boolean DEFAULT_USE_INCOMING_RMI = false;
    public static boolean useIncomingRMI;
    public static boolean isNewbieFriendly;
    
    public static final void load() {
        Constants.props = new Properties();
        File file = null;
        try {
            file = new File(ServerDirInfo.getConstantsFileName());
            Constants.logger.info("Loading configuration file at " + file.getAbsolutePath());
            final FileInputStream fis = new FileInputStream(file);
            Constants.props.load(fis);
            fis.close();
        }
        catch (FileNotFoundException ex2) {
            Constants.logger.log(Level.SEVERE, "Failed to locate wurm initializer file at " + file.getAbsolutePath());
            try {
                save();
            }
            catch (Exception fex) {
                Constants.logger.log(Level.SEVERE, "Failed to create wurm initializer file at " + file.getAbsolutePath(), fex);
            }
        }
        catch (IOException ex3) {
            Constants.logger.log(Level.SEVERE, "Failed to load properties at " + file.getAbsolutePath());
        }
        try {
            Constants.motd = Constants.props.getProperty("MOTD");
            final File dbdir = null;
            Constants.dbHost = Constants.props.getProperty("DB_HOST");
            Constants.dbUser = Constants.props.getProperty("DB_USER");
            Constants.dbPass = Constants.props.getProperty("DB_PASS");
            Constants.dbDriver = Constants.props.getProperty("DB_DRIVER");
            Constants.webPath = Constants.props.getProperty("WEB_PATH");
            Constants.usePooledDb = getBoolean("USE_POOLED_DB", false);
            Constants.trackOpenDatabaseResources = getBoolean("TRACK_OPEN_DATABASE_RESOURCES", false);
            Constants.createSeeds = getBoolean("CREATESEEDS", false);
            if (Constants.props.getProperty("DB_PORT") != null && Constants.props.getProperty("DB_PORT").length() > 0) {
                Constants.dbPort = ":" + Constants.props.getProperty("DB_PORT");
            }
            Constants.dbAutoMigrate = getBoolean("DB_AUTO_MIGRATE", true);
            try {
                Constants.numberOfDirtyMeshRowsToSaveEachCall = Integer.parseInt(Constants.props.getProperty("NUMBER_OF_DIRTY_MESH_ROWS_TO_SAVE_EACH_CALL"));
                Constants.useDirectByteBuffersForMeshIO = getBoolean("USE_DIRECT_BYTE_BUFFERS_FOR_MESHIO", false);
            }
            catch (Exception ex4) {
                Constants.numberOfDirtyMeshRowsToSaveEachCall = 10;
                Constants.useDirectByteBuffersForMeshIO = false;
            }
            final File worldMachineOutput = new File(ServerDirInfo.getFileDBPath() + "top_layer.map");
            final long baseFileSize = worldMachineOutput.length();
            final int mapDimension = (int)Math.sqrt(baseFileSize) / 2;
            Constants.meshSize = (int)(Math.log(mapDimension) / Math.log(2.0));
            System.out.println("Meshsize=" + Constants.meshSize);
            Constants.devmode = getBoolean("DEVMODE", false);
            Constants.crashed = getBoolean("CRASHED", false);
            Constants.RUNBATCH = getBoolean("RUNBATCH", false);
            Constants.maintaining = getBoolean("MAINTAINING", false);
            Constants.checkWurmLogs = getBoolean("CHECK_WURMLOGS", false);
            try {
                Constants.startChallenge = getBoolean("STARTCHALLENGE", false);
                Constants.challengeDays = Integer.parseInt(Constants.props.getProperty("CHALLENGEDAYS"));
            }
            catch (Exception ex6) {}
            Constants.isGameServer = getBoolean("IS_GAME_SERVER", true);
            Constants.lagThreshold = getLong("LAG_THRESHOLD", 1000L);
            Constants.useSplitCreaturesTable = getBoolean("USE_SPLIT_CREATURES_TABLE", false);
            Constants.analyseAllDbTables = getBoolean("ANALYSE_ALL_DB_TABLES", false);
            Constants.checkAllDbTables = getBoolean("CHECK_ALL_DB_TABLES", false);
            Constants.optimiseAllDbTables = getBoolean("OPTIMISE_ALL_DB_TABLES", false);
            Constants.usePrepStmts = getBoolean("PREPSTATEMENTS", false);
            Constants.gatherDbStats = getBoolean("DBSTATS", false);
            Constants.pruneDb = getBoolean("PRUNEDB", false);
            Constants.reprospect = getBoolean("PROSPECT", false);
            ((Hashtable<String, String>)Constants.props).put("PROSPECT", String.valueOf(false));
            Constants.caveImg = getBoolean("CAVEIMG", false);
            ((Hashtable<String, String>)Constants.props).put("CAVEIMG", String.valueOf(false));
            try {
                Constants.respawnUniques = getBoolean("RESPAWN", false);
                ((Hashtable<String, String>)Constants.props).put("RESPAWN", String.valueOf(false));
            }
            catch (Exception ex5) {
                Constants.logger.log(Level.WARNING, "Not respawning uniques");
            }
            Constants.loadNpcs = getBoolean("NPCS", true);
            Constants.minMillisBetweenPlayerConns = getLong("PLAYER_CONN_MILLIS", 1000L);
            Constants.useQueueToSendDataToPlayers = getBoolean("USE_QUEUE_TO_SEND_DATA_TO_PLAYERS", false);
            Constants.useMultiThreadedBankPolling = getBoolean("USE_MULTI_THREADED_BANK_POLLING", false);
            Constants.useScheduledExecutorToCountEggs = getBoolean("USE_SCHEDULED_EXECUTOR_TO_COUNT_EGGS", false);
            Constants.useScheduledExecutorToSaveDirtyMeshRows = getBoolean("USE_SCHEDULED_EXECUTOR_TO_SAVE_DIRTY_MESH_ROWS", false);
            Constants.useScheduledExecutorToSendTimeSync = getBoolean("USE_SCHEDULED_EXECUTOR_TO_SEND_TIME_SYNC", false);
            Constants.useScheduledExecutorToSwitchFatigue = getBoolean("USE_SCHEDULED_EXECUTOR_TO_SWITCH_FATIGUE", false);
            Constants.useScheduledExecutorToTickCalendar = getBoolean("USE_SCHEDULED_EXECUTOR_TO_TICK_CALENDAR", false);
            Constants.useScheduledExecutorToWriteLogs = getBoolean("USE_SCHEDULED_EXECUTOR", false);
            Constants.useScheduledExecutorForServer = getBoolean("USE_SCHEDULED_EXECUTOR_FOR_SERVER", false);
            Constants.useScheduledExecutorForTrello = getBoolean("USE_SCHEDULED_EXECUTOR_FOR_TRELLO", false);
            Constants.scheduledExecutorServiceThreads = getInt("SCHEDULED_EXECUTOR_SERVICE_NUMBER_OF_THREADS", Constants.scheduledExecutorServiceThreads);
            Constants.useItemTransferLog = getBoolean("USE_ITEM_TRANSFER_LOG", false);
            Constants.useTileEventLog = getBoolean("USE_TILE_LOG", false);
            Constants.useDatabaseForServerStatisticsLog = getBoolean("USE_DATABASE_FOR_SERVER_STATISTICS_LOG", false);
            Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase = getBoolean("USE_SCHEDULED_EXECUTOR_TO_UPDATE_CREATURE_POSITION_IN_DATABASE", false);
            Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase = getBoolean("USE_SCHEDULED_EXECUTOR_TO_UPDATE_PLAYER_POSITION_IN_DATABASE", false);
            Constants.useScheduledExecutorToUpdateItemDamageInDatabase = getBoolean("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_DAMAGE_IN_DATABASE", false);
            Constants.useScheduledExecutorToUpdateItemOwnerInDatabase = getBoolean("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_OWNER_IN_DATABASE", true);
            Constants.useScheduledExecutorToUpdateItemLastOwnerInDatabase = getBoolean("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_LASTOWNER_IN_DATABASE", true);
            Constants.useScheduledExecutorToUpdateItemParentInDatabase = getBoolean("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_PARENT_IN_DATABASE", true);
            Constants.numberOfDbCreaturePositionsToUpdateEachTime = getInt("NUMBER_OF_DB_CREATURE_POSITIONS_TO_UPDATE_EACH_TIME", Constants.numberOfDbCreaturePositionsToUpdateEachTime);
            Constants.numberOfDbPlayerPositionsToUpdateEachTime = getInt("NUMBER_OF_DB_PLAYER_POSITIONS_TO_UPDATE_EACH_TIME", Constants.numberOfDbPlayerPositionsToUpdateEachTime);
            Constants.numberOfDbItemDamagesToUpdateEachTime = getInt("NUMBER_OF_DB_ITEM_DAMAGES_TO_UPDATE_EACH_TIME", Constants.numberOfDbItemDamagesToUpdateEachTime);
            Constants.numberOfDbItemOwnersToUpdateEachTime = getInt("NUMBER_OF_DB_ITEM_OWNERS_TO_UPDATE_EACH_TIME", Constants.numberOfDbItemOwnersToUpdateEachTime);
            Constants.trelloBoardid = Constants.props.getProperty("TRELLO_BOARD_ID", "");
            Constants.trelloMVBoardId = Constants.props.getProperty("TRELLO_MUTE_VOTE_BOARD_ID", "");
            Constants.trelloApiKey = Constants.props.getProperty("TRELLO_APIKEY", "");
            Constants.trelloToken = Constants.props.getProperty("TRELLO_TOKEN");
            Constants.enableAutoNetworking = getBoolean("AUTO_NETWORKING", true);
            Constants.enablePnpPortForward = getBoolean("ENABLE_PNP_PORT_FORWARD", true);
            Constants.useIncomingRMI = getBoolean("USE_INCOMING_RMI", false);
            final String logfile = Constants.props.getProperty("PLAYERLOG");
            if (logfile != null && logfile.length() > 0) {
                if (logfile.endsWith(".log")) {
                    Constants.playerStatLog = logfile;
                }
                else {
                    Constants.logger.log(Level.WARNING, "PLAYERLOG file does not end with '.log'. Using default: " + Constants.playerStatLog);
                }
            }
            else {
                Constants.logger.log(Level.WARNING, "PLAYERLOG not specified. Using default: " + Constants.playerStatLog);
            }
        }
        catch (Exception ex) {
            Constants.logger.log(Level.WARNING, "Failed to load property.", ex);
        }
    }
    
    private static boolean getBoolean(final String key, final boolean defaultValue) {
        final String maybeBoolean = Constants.props.getProperty(key);
        return (maybeBoolean == null) ? defaultValue : Boolean.parseBoolean(maybeBoolean);
    }
    
    private static int getInt(final String key, final int defaultValue) {
        final String maybeInt = Constants.props.getProperty(key);
        if (maybeInt == null) {
            System.out.println(key + " - " + maybeInt);
            return defaultValue;
        }
        try {
            return Integer.parseInt(maybeInt);
        }
        catch (NumberFormatException e) {
            System.out.println(key + " - " + maybeInt);
            return defaultValue;
        }
    }
    
    private static long getLong(final String key, final long defaultValue) {
        final String maybeLong = Constants.props.getProperty(key);
        if (maybeLong == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(maybeLong);
        }
        catch (NumberFormatException e) {
            System.out.println(key + " - " + maybeLong);
            return defaultValue;
        }
    }
    
    public static int getMeshSize() {
        return Constants.meshSize;
    }
    
    public static void save() {
        final File file = new File(ServerDirInfo.getConstantsFileName());
        if (Constants.logger.isLoggable(Level.FINER)) {
            Constants.logger.finer("Saving wurm initializer file at " + file.getAbsolutePath());
        }
        try {
            if (!ServerDirInfo.getFileDBPath().endsWith(File.separator)) {
                ServerDirInfo.setFileDBPath(ServerDirInfo.getFileDBPath() + File.separator);
            }
            ((Hashtable<String, String>)Constants.props).put("DBPATH", ServerDirInfo.getFileDBPath());
            ((Hashtable<String, String>)Constants.props).put("MOTD", Constants.motd);
            ((Hashtable<String, String>)Constants.props).put("CHECK_WURMLOGS", String.valueOf(Constants.checkWurmLogs));
            ((Hashtable<String, String>)Constants.props).put("LAG_THRESHOLD", String.valueOf(Constants.lagThreshold));
            ((Hashtable<String, String>)Constants.props).put("DB_HOST", Constants.dbHost);
            ((Hashtable<String, String>)Constants.props).put("DB_USER", Constants.dbUser);
            ((Hashtable<String, String>)Constants.props).put("DB_PASS", Constants.dbPass);
            ((Hashtable<String, String>)Constants.props).put("DB_DRIVER", Constants.dbDriver);
            ((Hashtable<String, String>)Constants.props).put("USEDB", String.valueOf(true));
            ((Hashtable<String, String>)Constants.props).put("USE_POOLED_DB", String.valueOf(Constants.usePooledDb));
            ((Hashtable<String, String>)Constants.props).put("TRACK_OPEN_DATABASE_RESOURCES", String.valueOf(Constants.trackOpenDatabaseResources));
            ((Hashtable<String, String>)Constants.props).put("NUMBER_OF_DIRTY_MESH_ROWS_TO_SAVE_EACH_CALL", Integer.toString(Constants.numberOfDirtyMeshRowsToSaveEachCall));
            ((Hashtable<String, String>)Constants.props).put("USE_DIRECT_BYTE_BUFFERS_FOR_MESHIO", Boolean.toString(Constants.useDirectByteBuffersForMeshIO));
            ((Hashtable<String, String>)Constants.props).put("MAINTAINING", String.valueOf(false));
            ((Hashtable<String, String>)Constants.props).put("RUNBATCH", String.valueOf(false));
            ((Hashtable<String, String>)Constants.props).put("PROSPECT", String.valueOf(false));
            ((Hashtable<String, String>)Constants.props).put("CAVEIMG", String.valueOf(false));
            ((Hashtable<String, String>)Constants.props).put("USE_QUEUE_TO_SEND_DATA_TO_PLAYERS", Boolean.toString(Constants.useQueueToSendDataToPlayers));
            ((Hashtable<String, String>)Constants.props).put("USE_MULTI_THREADED_BANK_POLLING", Boolean.toString(Constants.useMultiThreadedBankPolling));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_COUNT_EGGS", Boolean.toString(Constants.useScheduledExecutorToCountEggs));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_SAVE_DIRTY_MESH_ROWS", Boolean.toString(Constants.useScheduledExecutorToSaveDirtyMeshRows));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_SEND_TIME_SYNC", Boolean.toString(Constants.useScheduledExecutorToSendTimeSync));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_SWITCH_FATIGUE", Boolean.toString(Constants.useScheduledExecutorToSwitchFatigue));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_TICK_CALENDAR", Boolean.toString(Constants.useScheduledExecutorToTickCalendar));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR", Boolean.toString(Constants.useScheduledExecutorToWriteLogs));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_FOR_SERVER", Boolean.toString(Constants.useScheduledExecutorForServer));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_FOR_TRELLO", Boolean.toString(Constants.useScheduledExecutorForTrello));
            ((Hashtable<String, String>)Constants.props).put("SCHEDULED_EXECUTOR_SERVICE_NUMBER_OF_THREADS", Integer.toString(Constants.scheduledExecutorServiceThreads));
            ((Hashtable<String, String>)Constants.props).put("PLAYERLOG", Constants.playerStatLog);
            ((Hashtable<String, String>)Constants.props).put("USE_ITEM_TRANSFER_LOG", Boolean.toString(Constants.useItemTransferLog));
            ((Hashtable<String, String>)Constants.props).put("USE_TILE_LOG", Boolean.toString(Constants.useTileEventLog));
            ((Hashtable<String, String>)Constants.props).put("USE_DATABASE_FOR_SERVER_STATISTICS_LOG", Boolean.toString(Constants.useDatabaseForServerStatisticsLog));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_CREATURE_POSITION_IN_DATABASE", Boolean.toString(Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_PLAYER_POSITION_IN_DATABASE", Boolean.toString(Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_DAMAGE_IN_DATABASE", Boolean.toString(Constants.useScheduledExecutorToUpdateItemDamageInDatabase));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_OWNER_IN_DATABASE", Boolean.toString(Constants.useScheduledExecutorToUpdateItemOwnerInDatabase));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_LASTOWNER_IN_DATABASE", Boolean.toString(Constants.useScheduledExecutorToUpdateItemLastOwnerInDatabase));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_ITEM_Parent_IN_DATABASE", Boolean.toString(Constants.useScheduledExecutorToUpdateItemParentInDatabase));
            ((Hashtable<String, String>)Constants.props).put("NUMBER_OF_DB_CREATURE_POSITIONS_TO_UPDATE_EACH_TIME", Integer.toString(Constants.numberOfDbCreaturePositionsToUpdateEachTime));
            ((Hashtable<String, String>)Constants.props).put("NUMBER_OF_DB_PLAYER_POSITIONS_TO_UPDATE_EACH_TIME", Integer.toString(Constants.numberOfDbPlayerPositionsToUpdateEachTime));
            ((Hashtable<String, String>)Constants.props).put("NUMBER_OF_DB_ITEM_DAMAGES_TO_UPDATE_EACH_TIME", Integer.toString(Constants.numberOfDbItemDamagesToUpdateEachTime));
            ((Hashtable<String, String>)Constants.props).put("NUMBER_OF_DB_ITEM_OWNERS_TO_UPDATE_EACH_TIME", Integer.toString(Constants.numberOfDbItemOwnersToUpdateEachTime));
            ((Hashtable<String, String>)Constants.props).put("USE_SCHEDULED_EXECUTOR_TO_UPDATE_TWITTER", Boolean.toString(Constants.useScheduledExecutorToConnectToTwitter));
            ((Hashtable<String, String>)Constants.props).put("WEB_PATH", Constants.webPath);
            ((Hashtable<String, String>)Constants.props).put("CREATESEEDS", "false");
            ((Hashtable<String, String>)Constants.props).put("DEVMODE", String.valueOf(Constants.devmode));
            ((Hashtable<String, String>)Constants.props).put("CRASHED", String.valueOf(Constants.crashed));
            ((Hashtable<String, String>)Constants.props).put("PRUNEDB", String.valueOf(Constants.pruneDb));
            ((Hashtable<String, String>)Constants.props).put("PLAYER_CONN_MILLIS", String.valueOf(Constants.minMillisBetweenPlayerConns));
            ((Hashtable<String, String>)Constants.props).put("USE_SPLIT_CREATURES_TABLE", Boolean.toString(Constants.useSplitCreaturesTable));
            ((Hashtable<String, String>)Constants.props).put("ANALYSE_ALL_DB_TABLES", Boolean.toString(Constants.analyseAllDbTables));
            ((Hashtable<String, String>)Constants.props).put("CHECK_ALL_DB_TABLES", Boolean.toString(Constants.checkAllDbTables));
            ((Hashtable<String, String>)Constants.props).put("OPTIMISE_ALL_DB_TABLES", Boolean.toString(Constants.optimiseAllDbTables));
            ((Hashtable<String, String>)Constants.props).put("CREATE_TEMPORARY_DATABASE_INDICES_AT_STARTUP", Boolean.toString(Constants.createTemporaryDatabaseIndicesAtStartup));
            ((Hashtable<String, String>)Constants.props).put("DROP_TEMPORARY_DATABASE_INDICES_AT_STARTUP", Boolean.toString(Constants.dropTemporaryDatabaseIndicesAtStartup));
            ((Hashtable<String, String>)Constants.props).put("PREPSTATEMENTS", String.valueOf(Constants.usePrepStmts));
            ((Hashtable<String, String>)Constants.props).put("DBSTATS", String.valueOf(Constants.gatherDbStats));
            ((Hashtable<String, String>)Constants.props).put("MOUNTS", String.valueOf(Constants.enabledMounts));
            ((Hashtable<String, String>)Constants.props).put("DB_PORT", String.valueOf(Constants.dbPort.replace(":", "")));
            ((Hashtable<String, String>)Constants.props).put("TRELLO_BOARD_ID", Constants.trelloBoardid);
            ((Hashtable<String, String>)Constants.props).put("TRELLO_MUTE_VOTE_BOARD_ID", Constants.trelloMVBoardId);
            ((Hashtable<String, String>)Constants.props).put("TRELLO_APIKEY", Constants.trelloApiKey);
            ((Hashtable<String, String>)Constants.props).put("NPCS", Boolean.toString(Constants.loadNpcs));
            if (Constants.trelloToken != null) {
                ((Hashtable<String, String>)Constants.props).put("TRELLO_TOKEN", Constants.trelloToken);
            }
        }
        catch (Exception fex) {
            Constants.logger.log(Level.SEVERE, "Failed to create wurm initializer file at " + file.getAbsolutePath(), fex);
        }
    }
    
    static void logConstantValues(final boolean aWithPasswords) {
        Constants.logger.info("motd: " + Constants.motd);
        Constants.logger.info("");
        Constants.logger.info("fileName: " + ServerDirInfo.getConstantsFileName());
        Constants.logger.info("");
        Constants.logger.info("Check WURMLOGS: " + Constants.checkWurmLogs);
        Constants.logger.info("isGameServer: " + Constants.isGameServer);
        Constants.logger.info("devmode: " + Constants.devmode);
        Constants.logger.info("maintaining: " + Constants.maintaining);
        Constants.logger.info("crashed: " + Constants.crashed);
        Constants.logger.info("RUNBATCH: " + Constants.RUNBATCH);
        Constants.logger.info("pruneDb: " + Constants.pruneDb);
        Constants.logger.info("reprospect: " + Constants.reprospect);
        Constants.logger.info("caveImg: " + Constants.caveImg);
        Constants.logger.info("createSeeds: " + Constants.createSeeds);
        Constants.logger.info("Min millis between player connections: " + Constants.minMillisBetweenPlayerConns);
        Constants.logger.info("");
        Constants.logger.info("fileDBPath: " + ServerDirInfo.getFileDBPath());
        Constants.logger.info("dbHost: " + Constants.dbHost);
        Constants.logger.info("useSplitCreaturesTable: " + Constants.useSplitCreaturesTable);
        Constants.logger.info("analyseAllDbTables: " + Constants.analyseAllDbTables);
        Constants.logger.info("checkAllDbTables: " + Constants.checkAllDbTables);
        Constants.logger.info("optimiseAllDbTables: " + Constants.optimiseAllDbTables);
        Constants.logger.info("createTemporaryDatabaseIndicesAtStartup: " + Constants.createTemporaryDatabaseIndicesAtStartup);
        Constants.logger.info("dropTemporaryDatabaseIndicesAtStartup: " + Constants.dropTemporaryDatabaseIndicesAtStartup);
        Constants.logger.info("usePrepStmts: " + Constants.usePrepStmts);
        Constants.logger.info("gatherDbStats: " + Constants.gatherDbStats);
        Constants.logger.info("");
        Constants.logger.info("");
        Constants.logger.info("");
        Constants.logger.info("dbUser: " + Constants.dbUser);
        if (aWithPasswords) {
            Constants.logger.info("dbPass: " + Constants.dbPass);
        }
        Constants.logger.info("dbDriver: " + Constants.dbDriver);
        Constants.logger.info("useDb: true");
        Constants.logger.info("usePooledDb: " + Constants.usePooledDb);
        Constants.logger.info("dbPort: " + Constants.dbPort);
        Constants.logger.info("trackOpenDatabaseResources: " + Constants.trackOpenDatabaseResources);
        Constants.logger.info("");
        Constants.logger.info("numberOfDirtyMeshRowsToSaveEachCall: " + Constants.numberOfDirtyMeshRowsToSaveEachCall);
        Constants.logger.info("useDirectByteBuffersForMeshIO: " + Constants.useDirectByteBuffersForMeshIO);
        Constants.logger.info("");
        Constants.logger.info("webPath: " + Constants.webPath);
        Constants.logger.info("skillTemplatesDBPath: " + Constants.skillTemplatesDBPath);
        Constants.logger.info("zonesDBPath: " + Constants.zonesDBPath);
        Constants.logger.info("itemTemplatesDBPath: " + Constants.itemTemplatesDBPath);
        Constants.logger.info("creatureStatsDBPath: " + Constants.creatureStatsDBPath);
        Constants.logger.info("playerStatsDBPath: " + Constants.playerStatsDBPath);
        Constants.logger.info("itemStatsDBPath: " + Constants.itemStatsDBPath);
        Constants.logger.info("tileStatsDBPath: " + Constants.tileStatsDBPath);
        Constants.logger.info("itemOldStatsDBPath: " + Constants.itemOldStatsDBPath);
        Constants.logger.info("creatureOldStatsDBPath: " + Constants.creatureOldStatsDBPath);
        Constants.logger.info("useQueueToSendDataToPlayers: " + Constants.useQueueToSendDataToPlayers);
        Constants.logger.info("useMultiThreadedBankPolling: " + Constants.useMultiThreadedBankPolling);
        Constants.logger.info("useScheduledExecutorToCountEggs: " + Constants.useScheduledExecutorToCountEggs);
        Constants.logger.info("useScheduledExecutorToSaveConstants: " + Constants.useScheduledExecutorToSaveConstants);
        Constants.logger.info("useScheduledExecutorToSaveDirtyMeshRows: " + Constants.useScheduledExecutorToSaveDirtyMeshRows);
        Constants.logger.info("useScheduledExecutorToSendTimeSync: " + Constants.useScheduledExecutorToSendTimeSync);
        Constants.logger.info("useScheduledExecutorToSwitchFatigue: " + Constants.useScheduledExecutorToSwitchFatigue);
        Constants.logger.info("useScheduledExecutorToTickCalendar: " + Constants.useScheduledExecutorToTickCalendar);
        Constants.logger.info("useScheduledExecutorToWriteLogs: " + Constants.useScheduledExecutorToWriteLogs);
        Constants.logger.info("useScheduledExecutorForServer: " + Constants.useScheduledExecutorForServer);
        Constants.logger.info("scheduledExecutorServiceThreads: " + Constants.scheduledExecutorServiceThreads);
        Constants.logger.info("useItemTransferLog: " + Constants.useItemTransferLog);
        Constants.logger.info("useTileEventLog: " + Constants.useTileEventLog);
        Constants.logger.info("useDatabaseForServerStatisticsLog: " + Constants.useDatabaseForServerStatisticsLog);
        Constants.logger.info("useScheduledExecutorToUpdateCreaturePositionInDatabase: " + Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase);
        Constants.logger.info("useScheduledExecutorToUpdatePlayerPositionInDatabase: " + Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase);
        Constants.logger.info("useScheduledExecutorToUpdateItemDamageInDatabase: " + Constants.useScheduledExecutorToUpdateItemDamageInDatabase);
        Constants.logger.info("useScheduledExecutorToUpdateItemOwnerInDatabase: " + Constants.useScheduledExecutorToUpdateItemOwnerInDatabase);
        Constants.logger.info("useScheduledExecutorToUpdateItemLastOwnerInDatabase: " + Constants.useScheduledExecutorToUpdateItemLastOwnerInDatabase);
        Constants.logger.info("useScheduledExecutorToUpdateItemParentInDatabase: " + Constants.useScheduledExecutorToUpdateItemParentInDatabase);
        Constants.logger.info("useScheduledExecutorToConnectToTwitter: " + Constants.useScheduledExecutorToConnectToTwitter);
        Constants.logger.info("numberOfDbCreaturePositionsToUpdateEachTime: " + Constants.numberOfDbCreaturePositionsToUpdateEachTime);
        Constants.logger.info("numberOfDbPlayerPositionsToUpdateEachTime: " + Constants.numberOfDbPlayerPositionsToUpdateEachTime);
        Constants.logger.info("numberOfDbItemDamagesToUpdateEachTime: " + Constants.numberOfDbItemDamagesToUpdateEachTime);
        Constants.logger.info("numberOfDbItemOwnersToUpdateEachTime: " + Constants.numberOfDbItemOwnersToUpdateEachTime);
        Constants.logger.info("playerStatLog: " + Constants.playerStatLog);
        Constants.logger.info("logonStatLog: " + Constants.logonStatLog);
        Constants.logger.info("ipStatLog: " + Constants.ipStatLog);
        Constants.logger.info("newbieStatLog: " + Constants.newbieStatLog);
        Constants.logger.info("totIpStatLog: " + Constants.totIpStatLog);
        Constants.logger.info("payingLog: " + Constants.payingLog);
        Constants.logger.info("moneyLog: " + Constants.moneyLog);
        Constants.logger.info("lagLog: " + Constants.lagLog);
        Constants.logger.info("lagThreshold: " + Constants.lagThreshold);
        Constants.logger.info("Eigc enabled " + Constants.isEigcEnabled);
        Constants.logger.info("useIncomingRMI: " + Constants.useIncomingRMI);
    }
    
    static {
        Constants.dbHost = "localhost";
        Constants.dbPort = ":3306";
        Constants.dbAutoMigrate = true;
        Constants.enabledMounts = true;
        Constants.loadNpcs = true;
        Constants.loadEndGameItems = true;
        Constants.enableSpyPrevention = true;
        Constants.enableAutoNetworking = true;
        Constants.analyseAllDbTables = false;
        Constants.checkAllDbTables = false;
        Constants.optimiseAllDbTables = false;
        Constants.useSplitCreaturesTable = false;
        Constants.createTemporaryDatabaseIndicesAtStartup = true;
        Constants.dropTemporaryDatabaseIndicesAtStartup = true;
        Constants.usePrepStmts = false;
        Constants.gatherDbStats = false;
        Constants.checkWurmLogs = false;
        Constants.startChallenge = false;
        Constants.challengeDays = 30;
        Constants.isGameServer = true;
        Constants.isEigcEnabled = false;
        Constants.dbUser = "";
        Constants.dbPass = "";
        Constants.dbDriver = "com.mysql.jdbc.Driver";
        Constants.webPath = ".";
        Constants.usePooledDb = true;
        Constants.trackOpenDatabaseResources = false;
        Constants.enablePnpPortForward = true;
        Constants.logger = Logger.getLogger(Constants.class.getName());
        Constants.props = null;
        Constants.numberOfDirtyMeshRowsToSaveEachCall = 1;
        Constants.useDirectByteBuffersForMeshIO = false;
        Constants.createSeeds = false;
        Constants.devmode = false;
        Constants.motd = "Wurm has been waiting for you.";
        Constants.skillTemplatesDBPath = "templates" + File.separator + "skills" + File.separator;
        Constants.zonesDBPath = "zones" + File.separator;
        Constants.itemTemplatesDBPath = "templates" + File.separator + "items" + File.separator;
        Constants.creatureTemplatesDBPath = "templates" + File.separator + "creatures" + File.separator;
        Constants.creatureStatsDBPath = "creatures" + File.separator;
        Constants.playerStatsDBPath = "players" + File.separator;
        Constants.itemStatsDBPath = "items" + File.separator;
        Constants.tileStatsDBPath = "tiles" + File.separator;
        Constants.itemOldStatsDBPath = "olditems" + File.separator;
        Constants.creatureOldStatsDBPath = "deadCreatures" + File.separator;
        Constants.RUNBATCH = false;
        Constants.maintaining = false;
        Constants.useQueueToSendDataToPlayers = false;
        Constants.useMultiThreadedBankPolling = false;
        Constants.useScheduledExecutorToCountEggs = false;
        Constants.useScheduledExecutorToSaveConstants = false;
        Constants.useScheduledExecutorToSaveDirtyMeshRows = false;
        Constants.useScheduledExecutorToSendTimeSync = false;
        Constants.useScheduledExecutorToSwitchFatigue = false;
        Constants.useScheduledExecutorToTickCalendar = false;
        Constants.useScheduledExecutorToWriteLogs = false;
        Constants.useScheduledExecutorForServer = false;
        Constants.scheduledExecutorServiceThreads = 1;
        Constants.playerStatLog = "numplayers.log";
        Constants.logonStatLog = "numlogons.log";
        Constants.ipStatLog = "numips.log";
        Constants.tutorialLog = "tutorial.log";
        Constants.newbieStatLog = "newbies.log";
        Constants.totIpStatLog = "totalips.log";
        Constants.payingLog = "paying.log";
        Constants.subscriptionLog = "subscriptions.log";
        Constants.moneyLog = "mrtgmoney.log";
        Constants.economyLog = "economy.log";
        Constants.expiryLog = "expiry.log";
        Constants.lagLog = "lag.log";
        Constants.retentionStatLog = "retention.log";
        Constants.retentionPercentStatLog = "retentionpercent.log";
        Constants.useItemTransferLog = false;
        Constants.useTileEventLog = false;
        Constants.useDatabaseForServerStatisticsLog = false;
        Constants.useScheduledExecutorToUpdateCreaturePositionInDatabase = false;
        Constants.numberOfDbCreaturePositionsToUpdateEachTime = 500;
        Constants.useScheduledExecutorToUpdatePlayerPositionInDatabase = false;
        Constants.numberOfDbPlayerPositionsToUpdateEachTime = 500;
        Constants.useScheduledExecutorToUpdateItemDamageInDatabase = false;
        Constants.numberOfDbItemDamagesToUpdateEachTime = 500;
        Constants.useScheduledExecutorToUpdateItemOwnerInDatabase = true;
        Constants.useScheduledExecutorToUpdateItemLastOwnerInDatabase = true;
        Constants.useScheduledExecutorToUpdateItemParentInDatabase = true;
        Constants.numberOfDbItemOwnersToUpdateEachTime = 500;
        Constants.useScheduledExecutorToConnectToTwitter = true;
        Constants.useScheduledExecutorForTrello = false;
        Constants.lagThreshold = 1000L;
        Constants.crashed = true;
        Constants.respawnUniques = false;
        Constants.pruneDb = true;
        Constants.reprospect = false;
        Constants.caveImg = false;
        Constants.minMillisBetweenPlayerConns = 1000L;
        Constants.trelloBoardid = "";
        Constants.trelloApiKey = "";
        Constants.trelloToken = null;
        Constants.trelloMVBoardId = "";
        Constants.useIncomingRMI = false;
        Constants.isNewbieFriendly = false;
        load();
    }
    
    static final class ConstantsSaver implements Runnable
    {
        @Override
        public void run() {
            if (Constants.logger.isLoggable(Level.FINER)) {
                Constants.logger.finer("Running newSingleThreadScheduledExecutor for saving Constants to wurm.ini");
            }
            try {
                final long now = System.nanoTime();
                Constants.save();
                final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                if (lElapsedTime > Constants.lagThreshold) {
                    Constants.logger.info("Finished saving Constants to wurm.ini, which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e) {
                Constants.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while calling Constants.save()", e);
                throw e;
            }
        }
    }
}
