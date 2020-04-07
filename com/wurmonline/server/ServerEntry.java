// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.zones.TilePoller;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.nio.ByteBuffer;
import java.io.IOException;
import com.wurmonline.server.webinterface.WcTicket;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.webinterface.WcPlayerStatus;
import com.wurmonline.server.webinterface.WcEpicStatusReport;
import com.wurmonline.server.webinterface.WebCommand;
import com.wurmonline.server.webinterface.WcKingdomInfo;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.webinterface.WcSpawnPoints;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.epic.EpicEntity;
import java.util.logging.Level;
import java.util.StringTokenizer;
import java.util.HashSet;
import com.wurmonline.server.players.Spawnpoint;
import java.util.Set;
import com.wurmonline.server.intra.IntraClient;
import java.util.logging.Logger;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.intra.IntraServerConnectionListener;

public final class ServerEntry implements MiscConstants, IntraServerConnectionListener, TimeConstants, Comparable<ServerEntry>, MonetaryConstants
{
    public int id;
    public int SPAWNPOINTJENNX;
    public int SPAWNPOINTJENNY;
    public int SPAWNPOINTMOLX;
    public int SPAWNPOINTMOLY;
    public int SPAWNPOINTLIBX;
    public int SPAWNPOINTLIBY;
    public boolean HOMESERVER;
    public boolean PVPSERVER;
    public boolean EPIC;
    public String INTRASERVERADDRESS;
    public String INTRASERVERPORT;
    public int RMI_PORT;
    public int REGISTRATION_PORT;
    public ServerEntry serverNorth;
    public ServerEntry serverEast;
    public ServerEntry serverSouth;
    public ServerEntry serverWest;
    public String INTRASERVERPASSWORD;
    public boolean testServer;
    public String name;
    public String mapname;
    public String EXTERNALIP;
    public boolean LOGINSERVER;
    public String EXTERNALPORT;
    public String STEAMQUERYPORT;
    private byte[] externalIpBytes;
    private byte[] internalIpBytes;
    public byte KINGDOM;
    public boolean challengeServer;
    public boolean entryServer;
    public boolean ISPAYMENT;
    protected boolean isAvailable;
    private static final Logger logger;
    private boolean done;
    private IntraClient client;
    private long timeOutAt;
    private long timeOutTime;
    private long startTime;
    private long lastPing;
    public long lastDecreasedChampionPoints;
    private final Set<Byte> existingKingdoms;
    private float skillGainRate;
    private float actionTimer;
    private int hotaDelay;
    private float combatRatingModifier;
    String consumerKeyToUse;
    String consumerSecretToUse;
    String applicationToken;
    String applicationSecret;
    String champConsumerKeyToUse;
    String champConsumerSecretToUse;
    String champApplicationToken;
    String champApplicationSecret;
    private static final String SET_CHAMP_TWITTER = "UPDATE SERVERS SET CHAMPTWITKEY=?,CHAMPTWITSECRET=?,CHAMPTWITAPP=?,CHAMPTWITAPPSECRET=? WHERE SERVER=?";
    private static final String SET_TWITTER = "UPDATE SERVERS SET TWITKEY=?,TWITSECRET=?,TWITAPP=?,TWITAPPSECRET=? WHERE SERVER=?";
    private static final int PLAYER_LIMIT_MARGIN = 10;
    private boolean canTwit;
    public boolean canTwitChamps;
    public boolean maintaining;
    public int pLimit;
    public boolean playerLimitOverridable;
    public int maxCreatures;
    public int maxTypedCreatures;
    public float percentAggCreatures;
    public int treeGrowth;
    public int currentPlayers;
    public int isShuttingDownIn;
    public boolean loggingIn;
    private static final String SET_CHAMPSTAMP = "UPDATE SERVERS SET LASTRESETCHAMPS=? WHERE SERVER=?";
    public boolean isLocal;
    public boolean reloading;
    public int meshSize;
    public boolean shouldResendKingdoms;
    private long movedArtifacts;
    private static final String SET_MOVEDARTIFACTS = "UPDATE SERVERS SET MOVEDARTIS=? WHERE SERVER=?";
    private static final String SET_SPAWNEDUNIQUE = "UPDATE SERVERS SET SPAWNEDUNIQUE=? WHERE SERVER=?";
    private static final String MOVEPLAYERS = "UPDATE PLAYERS SET CURRENTSERVER=? WHERE CURRENTSERVER=?";
    private static final String UPDATE_CAHELPGROUP = "UPDATE SERVERS SET CAHELPGROUP=? WHERE SERVER=?";
    private static final String UPDATE_CHALLENGETIMES = "UPDATE SERVERS SET CHALLENGESTARTED=?, CHALLENGEEND=? WHERE SERVER=?";
    public static final String SET_TIMERS = "UPDATE SERVERS SET SKILLDAYSWITCH=?,SKILLWEEKSWITCH=?,NEXTEPICPOLL=?,FATIGUESWITCH=?,NEXTHOTA=?,WORLDTIME=?,TILEREST=?,POLLTILE=?,POLLMOD=?,POLLROUND=? WHERE SERVER=?";
    private long skillDaySwitch;
    private long skillWeekSwitch;
    private long nextEpicPoll;
    private long fatigueSwitch;
    private long lastSpawnedUnique;
    private long nextHota;
    private Spawnpoint[] spawns;
    private byte caHelpGroup;
    private long challengeStarted;
    private long challengeEnds;
    public boolean isCreating;
    public boolean randomSpawns;
    public static final String UPDATE_SERVER_NEW = "UPDATE SERVERS SET SERVER=?,NAME=?,MAXCREATURES=?,MAXPLAYERS=?,PERCENT_AGG_CREATURES=?,TREEGROWTH=?,SKILLGAINRATE=?,ACTIONTIMER=?,HOTADELAY=?,PVP=?,          HOMESERVER=?,KINGDOM=?,INTRASERVERPASSWORD=?,EXTERNALIP=?,EXTERNALPORT=?,INTRASERVERADDRESS=?,INTRASERVERPORT=?,ISTEST=?,ISPAYMENT=?,LOGINSERVER=?,           RMIPORT=?,REGISTRATIONPORT=?,LOCAL=?,RANDOMSPAWNS=?,SKILLBASICSTART=?,SKILLMINDLOGICSTART=?,SKILLFIGHTINGSTART=?,SKILLOVERALLSTART=?,EPIC=?,CRMOD=?,            STEAMPW=?,UPKEEP=?,MAXDEED=?,FREEDEEDS=?,TRADERMAX=?,TRADERINIT=?,BREEDING=?,FIELDGROWTH=?,KINGSMONEY=?, MOTD=?,     TUNNELING=?,SKILLBODYCONTROLSTART=? WHERE SERVER=?";
    private float skillbasicval;
    private float skillmindval;
    private float skillfightval;
    private float skilloverallval;
    private float skillbcval;
    private String steamServerPassword;
    private boolean upkeep;
    private int maxDeedSize;
    private boolean freeDeeds;
    private int traderMaxIrons;
    private int initialTraderIrons;
    private int tunnelingHits;
    private long breedingTimer;
    private long fieldGrowthTime;
    private int kingsmoneyAtRestart;
    private String motd;
    public String adminPassword;
    int pingcounter;
    
    public ServerEntry() {
        this.EPIC = false;
        this.INTRASERVERADDRESS = "";
        this.INTRASERVERPORT = "";
        this.RMI_PORT = 7220;
        this.REGISTRATION_PORT = 7221;
        this.INTRASERVERPASSWORD = "";
        this.testServer = false;
        this.name = "Unknown";
        this.mapname = "";
        this.EXTERNALIP = "";
        this.LOGINSERVER = false;
        this.EXTERNALPORT = "";
        this.STEAMQUERYPORT = "";
        this.externalIpBytes = null;
        this.internalIpBytes = null;
        this.KINGDOM = 0;
        this.challengeServer = false;
        this.entryServer = false;
        this.ISPAYMENT = false;
        this.isAvailable = false;
        this.timeOutTime = 20000L;
        this.lastPing = 0L;
        this.lastDecreasedChampionPoints = 0L;
        this.existingKingdoms = new HashSet<Byte>();
        this.skillGainRate = 1.0f;
        this.actionTimer = 1.0f;
        this.hotaDelay = 2160;
        this.combatRatingModifier = 1.0f;
        this.consumerKeyToUse = "";
        this.consumerSecretToUse = "";
        this.applicationToken = "";
        this.applicationSecret = "";
        this.champConsumerKeyToUse = "";
        this.champConsumerSecretToUse = "";
        this.champApplicationToken = "";
        this.champApplicationSecret = "";
        this.canTwit = false;
        this.canTwitChamps = false;
        this.maintaining = false;
        this.pLimit = 200;
        this.playerLimitOverridable = true;
        this.maxCreatures = 1000;
        this.maxTypedCreatures = 250;
        this.percentAggCreatures = 10.0f;
        this.treeGrowth = 20;
        this.currentPlayers = 0;
        this.isShuttingDownIn = 0;
        this.loggingIn = false;
        this.isLocal = false;
        this.reloading = false;
        this.meshSize = Constants.meshSize;
        this.shouldResendKingdoms = false;
        this.movedArtifacts = System.currentTimeMillis();
        this.skillDaySwitch = 0L;
        this.skillWeekSwitch = 0L;
        this.nextEpicPoll = 0L;
        this.fatigueSwitch = 0L;
        this.lastSpawnedUnique = 0L;
        this.nextHota = 0L;
        this.caHelpGroup = -1;
        this.challengeStarted = 0L;
        this.challengeEnds = 0L;
        this.isCreating = false;
        this.randomSpawns = false;
        this.skillbasicval = 20.0f;
        this.skillmindval = 20.0f;
        this.skillfightval = 1.0f;
        this.skilloverallval = 1.0f;
        this.skillbcval = 20.0f;
        this.steamServerPassword = "";
        this.upkeep = true;
        this.maxDeedSize = 0;
        this.freeDeeds = false;
        this.traderMaxIrons = 500000;
        this.initialTraderIrons = 10000;
        this.tunnelingHits = 51;
        this.breedingTimer = 0L;
        this.fieldGrowthTime = 86400000L;
        this.kingsmoneyAtRestart = 0;
        this.motd = "";
        this.adminPassword = "";
        this.pingcounter = 0;
    }
    
    ServerEntry(final int aId, final String aName, final boolean aEntryServer, final boolean aHomeServer, final boolean aPvpServer, final boolean aLoginServer, final boolean aIsPayment, final byte aKingdom, final String aExternalIP, final String aExternalPort, final String aIntraServerAddress, final String aIntraServerPort, final String aIntraServerPassword, final int aSpawnPointJennX, final int aSpawnPointJennY, final int aSpawPpointMolX, final int aSpawPpointMolY, final int aSpawnPointLibX, final int aSpawnPointLibY, final String _consumerKeyToUse, final String _consumerSecretToUse, final String _applicationToken, final String _applicationSecret, final boolean isTest, final long lastDecreasedChamps, final long movedArtis, final long spawnedUniques, final boolean challenge) {
        this.EPIC = false;
        this.INTRASERVERADDRESS = "";
        this.INTRASERVERPORT = "";
        this.RMI_PORT = 7220;
        this.REGISTRATION_PORT = 7221;
        this.INTRASERVERPASSWORD = "";
        this.testServer = false;
        this.name = "Unknown";
        this.mapname = "";
        this.EXTERNALIP = "";
        this.LOGINSERVER = false;
        this.EXTERNALPORT = "";
        this.STEAMQUERYPORT = "";
        this.externalIpBytes = null;
        this.internalIpBytes = null;
        this.KINGDOM = 0;
        this.challengeServer = false;
        this.entryServer = false;
        this.ISPAYMENT = false;
        this.isAvailable = false;
        this.timeOutTime = 20000L;
        this.lastPing = 0L;
        this.lastDecreasedChampionPoints = 0L;
        this.existingKingdoms = new HashSet<Byte>();
        this.skillGainRate = 1.0f;
        this.actionTimer = 1.0f;
        this.hotaDelay = 2160;
        this.combatRatingModifier = 1.0f;
        this.consumerKeyToUse = "";
        this.consumerSecretToUse = "";
        this.applicationToken = "";
        this.applicationSecret = "";
        this.champConsumerKeyToUse = "";
        this.champConsumerSecretToUse = "";
        this.champApplicationToken = "";
        this.champApplicationSecret = "";
        this.canTwit = false;
        this.canTwitChamps = false;
        this.maintaining = false;
        this.pLimit = 200;
        this.playerLimitOverridable = true;
        this.maxCreatures = 1000;
        this.maxTypedCreatures = 250;
        this.percentAggCreatures = 10.0f;
        this.treeGrowth = 20;
        this.currentPlayers = 0;
        this.isShuttingDownIn = 0;
        this.loggingIn = false;
        this.isLocal = false;
        this.reloading = false;
        this.meshSize = Constants.meshSize;
        this.shouldResendKingdoms = false;
        this.movedArtifacts = System.currentTimeMillis();
        this.skillDaySwitch = 0L;
        this.skillWeekSwitch = 0L;
        this.nextEpicPoll = 0L;
        this.fatigueSwitch = 0L;
        this.lastSpawnedUnique = 0L;
        this.nextHota = 0L;
        this.caHelpGroup = -1;
        this.challengeStarted = 0L;
        this.challengeEnds = 0L;
        this.isCreating = false;
        this.randomSpawns = false;
        this.skillbasicval = 20.0f;
        this.skillmindval = 20.0f;
        this.skillfightval = 1.0f;
        this.skilloverallval = 1.0f;
        this.skillbcval = 20.0f;
        this.steamServerPassword = "";
        this.upkeep = true;
        this.maxDeedSize = 0;
        this.freeDeeds = false;
        this.traderMaxIrons = 500000;
        this.initialTraderIrons = 10000;
        this.tunnelingHits = 51;
        this.breedingTimer = 0L;
        this.fieldGrowthTime = 86400000L;
        this.kingsmoneyAtRestart = 0;
        this.motd = "";
        this.adminPassword = "";
        this.pingcounter = 0;
        this.id = aId;
        this.name = aName;
        this.entryServer = aEntryServer;
        this.HOMESERVER = aHomeServer;
        this.PVPSERVER = aPvpServer;
        this.LOGINSERVER = aLoginServer;
        this.ISPAYMENT = aIsPayment;
        this.KINGDOM = aKingdom;
        this.EXTERNALIP = aExternalIP;
        this.EXTERNALPORT = aExternalPort;
        this.INTRASERVERADDRESS = aIntraServerAddress;
        this.INTRASERVERPORT = aIntraServerPort;
        this.INTRASERVERPASSWORD = aIntraServerPassword;
        this.SPAWNPOINTJENNX = aSpawnPointJennX;
        this.SPAWNPOINTJENNY = aSpawnPointJennY;
        this.SPAWNPOINTMOLX = aSpawPpointMolX;
        this.SPAWNPOINTMOLY = aSpawPpointMolY;
        this.SPAWNPOINTLIBX = aSpawnPointLibX;
        this.SPAWNPOINTLIBY = aSpawnPointLibY;
        this.consumerKeyToUse = _consumerKeyToUse;
        this.consumerSecretToUse = _consumerSecretToUse;
        this.applicationToken = _applicationToken;
        this.applicationSecret = _applicationSecret;
        this.lastDecreasedChampionPoints = lastDecreasedChamps;
        this.lastSpawnedUnique = spawnedUniques;
        this.testServer = isTest;
        this.challengeServer = challenge;
        if (movedArtis > 0L) {
            this.setMovedArtifacts(movedArtis);
        }
        else {
            this.movedArtifacts();
        }
        this.canTwit();
    }
    
    public boolean canTwit() {
        if (this.consumerKeyToUse != null && this.consumerKeyToUse.length() > 5 && this.consumerSecretToUse != null && this.consumerSecretToUse.length() > 5 && this.applicationToken != null && this.applicationToken.length() > 5 && this.applicationSecret != null && this.applicationSecret.length() > 5) {
            this.canTwit = true;
        }
        else {
            this.canTwit = false;
        }
        if (this.champConsumerKeyToUse != null && this.champConsumerKeyToUse.length() > 5 && this.champConsumerSecretToUse != null && this.champConsumerSecretToUse.length() > 5 && this.champApplicationToken != null && this.champApplicationToken.length() > 5 && this.champApplicationSecret != null && this.champApplicationSecret.length() > 5) {
            this.canTwitChamps = true;
        }
        else {
            this.canTwitChamps = false;
        }
        return this.canTwit;
    }
    
    public final boolean isChaosServer() {
        return this.id == 3 || (this.testServer && this.PVPSERVER && Features.Feature.CHAOS.isEnabled());
    }
    
    public final boolean isChallengeServer() {
        return this.challengeServer;
    }
    
    public final boolean isChallengeOrEpicServer() {
        return this.challengeServer || this.EPIC;
    }
    
    public Twit createTwit(final String message) {
        if (this.canTwit) {
            return new Twit(this.name, message, this.consumerKeyToUse, this.consumerSecretToUse, this.applicationToken, this.applicationSecret, false);
        }
        return null;
    }
    
    public void createChampTwit(final String message) {
        if (this.canTwitChamps) {
            final Twit t = new Twit(this.name, message, this.champConsumerKeyToUse, this.champConsumerSecretToUse, this.champApplicationToken, this.champApplicationSecret, false);
            if (t != null) {
                Twit.twit(t);
            }
        }
    }
    
    public byte[] getExternalIpAsBytes() {
        if (this.externalIpBytes == null) {
            this.externalIpBytes = new byte[4];
            final StringTokenizer tokens = new StringTokenizer(this.EXTERNALIP);
            int x = 0;
            while (tokens.hasMoreTokens()) {
                final String next = tokens.nextToken();
                this.externalIpBytes[x] = (byte)(Object)Integer.valueOf(next);
                ++x;
            }
        }
        return this.externalIpBytes;
    }
    
    public void setAvailable(final boolean available, final boolean maintain, final int currentPlayerCount, final int plimit, final int secsToShutdown, final int mSize) {
        this.pLimit = plimit;
        this.currentPlayers = currentPlayerCount;
        this.isShuttingDownIn = secsToShutdown;
        this.meshSize = mSize;
        this.maintaining = maintain;
        if (available != this.isAvailable) {
            this.isAvailable = available;
            if (available) {
                ServerEntry.logger.log(Level.INFO, this.name + " is now available.");
                final int serverId = this.getId();
                new Thread("ServerEntry.setAvailable-Thread") {
                    @Override
                    public void run() {
                        final long now = System.nanoTime();
                        if (ServerEntry.logger.isLoggable(Level.FINE)) {
                            ServerEntry.logger.fine("Starting ServerEntry.setAvailable() thread");
                        }
                        ServerEntry.this.sendKingdomInfo();
                        ServerEntry.this.setupPlayerStates();
                        ServerEntry.this.sendSpawnpoints();
                        if (Servers.localServer.LOGINSERVER) {
                            for (final EpicEntity entity : Server.getEpicMap().getAllEntities()) {
                                if (entity.isDeity()) {
                                    entity.checkifServerFailed(serverId);
                                }
                            }
                        }
                        if (ServerEntry.logger.isLoggable(Level.FINE)) {
                            final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                            ServerEntry.logger.fine("Finished ServerEntry.setAvailable() thread. That took " + lElapsedTime + " millis.");
                        }
                    }
                }.start();
            }
            else {
                ServerEntry.logger.log(Level.INFO, this.name + " is no longer available.");
                PlayerInfoFactory.setPlayerStatesToOffline(this.id);
            }
        }
    }
    
    public final void sendSpawnpoints() {
        if (Servers.getLocalServerId() != this.id) {
            final Set<Spawnpoint> lSpawns = new HashSet<Spawnpoint>();
            for (final Kingdom kingdom : Kingdoms.getAllKingdoms()) {
                final Village[] villages = Villages.getPermanentVillages(kingdom.getId());
                if (villages.length > 0) {
                    for (final Village vill : villages) {
                        String toSend = vill.getMotto();
                        if (Servers.localServer.isChallengeServer()) {
                            if (vill.getId() == 1 || vill.getId() == 7 || vill.getId() == 9) {
                                toSend = "Forward Base, for experienced players";
                            }
                            else {
                                toSend = "Far Base, for new players";
                            }
                        }
                        lSpawns.add(new Spawnpoint(vill.getName(), (byte)1, toSend, (short)vill.getTokenX(), (short)vill.getTokenY(), true, vill.kingdom));
                    }
                }
            }
            if (lSpawns.size() > 0) {
                final WcSpawnPoints wcp = new WcSpawnPoints(WurmId.getNextWCCommandId());
                wcp.setSpawns(lSpawns.toArray(new Spawnpoint[lSpawns.size()]));
                wcp.sendToServer(this.id);
            }
        }
    }
    
    public boolean isAvailable(final int power, final boolean isPremium) {
        return (Servers.getLocalServerId() == this.id && (!this.maintaining || power > 0)) || (this.isAvailable && (!this.maintaining || power > 0) && (!this.isFull() || isPremium || power > 0));
    }
    
    public boolean isFull() {
        return this.currentPlayers >= this.pLimit - 10;
    }
    
    public boolean isConnected() {
        return this.isAvailable;
    }
    
    public byte[] getInternalIpAsBytes() {
        if (this.internalIpBytes == null) {
            this.internalIpBytes = new byte[4];
            final StringTokenizer tokens = new StringTokenizer(this.INTRASERVERADDRESS);
            int x = 0;
            while (tokens.hasMoreTokens()) {
                final String next = tokens.nextToken();
                this.internalIpBytes[x] = (byte)(Object)Integer.valueOf(next);
                ++x;
            }
        }
        return this.internalIpBytes;
    }
    
    private final void sendKingdomInfo() {
        final LoginServerWebConnection lsw = new LoginServerWebConnection(this.id);
        if (Servers.localServer.LOGINSERVER) {
            lsw.setWeather(Server.getWeather().getWindRotation(), Server.getWeather().getWindPower(), Server.getWeather().getWindDir());
            final WcKingdomInfo wc = new WcKingdomInfo(WurmId.getNextWCCommandId(), false, (byte)0);
            wc.encode();
            lsw.sendWebCommand((short)7, wc);
            final WcEpicStatusReport report = new WcEpicStatusReport(WurmId.getNextWCCommandId(), false, 0, (byte)(-1), -1);
            report.fillStatusReport(Server.getEpicMap());
            report.sendToServer(this.id);
        }
        final Kingdom[] allKingdoms;
        final Kingdom[] kingdoms = allKingdoms = Kingdoms.getAllKingdoms();
        for (final Kingdom k : allKingdoms) {
            if (k.existsHere()) {
                lsw.kingdomExists(Servers.getLocalServerId(), k.getId(), true);
            }
            else if (ServerEntry.logger.isLoggable(Level.FINER)) {
                ServerEntry.logger.log(Level.FINER, k.getName() + " doesn't exist here");
            }
        }
        this.shouldResendKingdoms = false;
    }
    
    private final void setupPlayerStates() {
        if (Servers.isThisLoginServer() && this.id != Servers.loginServer.id) {
            final WcPlayerStatus wps = new WcPlayerStatus();
            wps.sendToServer(this.id);
            final WcTicket wt = new WcTicket(Tickets.getLatestActionDate());
            wt.sendToServer(this.id);
        }
        else if (!Servers.isThisLoginServer() && this.id == Servers.loginServer.id) {
            Tickets.checkBatchNos();
            PlayerInfoFactory.grabPlayerStates();
            final WcTicket wt2 = new WcTicket(Tickets.getLatestActionDate());
            wt2.sendToServer(this.id);
        }
    }
    
    public boolean poll() {
        if (this.id == Servers.getLocalServerId()) {
            return this.isAvailable = true;
        }
        if (this.client != null) {
            if (this.client.hasFailedConnection) {
                this.setAvailable(false, false, 0, 0, 0, 10);
                if (this.client != null) {
                    this.client.disconnect("Failed.");
                }
                this.client = null;
                this.done = true;
                this.loggingIn = false;
            }
            else if (!this.client.isConnecting && !this.client.loggedIn && !this.loggingIn) {
                this.loggingIn = true;
                this.client.login(this.INTRASERVERPASSWORD, true);
            }
        }
        if (this.client == null && System.currentTimeMillis() > this.timeOutAt) {
            this.startTime = System.currentTimeMillis();
            this.timeOutAt = this.startTime + this.timeOutTime;
            this.done = false;
            this.client = new IntraClient();
            this.loggingIn = false;
            this.client.reconnectAsynch(this.INTRASERVERADDRESS, Integer.parseInt(this.INTRASERVERPORT), this);
        }
        if (this.client != null && !this.done && !this.client.isConnecting) {
            if (System.currentTimeMillis() > this.timeOutAt && !this.client.loggedIn) {
                this.done = true;
            }
            if (!this.done) {
                try {
                    if (this.client.loggedIn && System.currentTimeMillis() - this.lastPing > 10000L) {
                        if (this.shouldResendKingdoms) {
                            this.sendKingdomInfo();
                        }
                        try {
                            this.client.executePingCommand();
                            this.lastPing = System.currentTimeMillis();
                            this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
                        }
                        catch (Exception ex) {
                            this.done = true;
                            this.client.disconnect(ex.getMessage());
                        }
                    }
                    if (!this.done) {
                        this.client.update();
                    }
                }
                catch (IOException iox) {
                    ServerEntry.logger.log(Level.INFO, "IOException to " + this.name + ". Disc:" + iox.getMessage(), iox);
                    this.done = true;
                }
            }
        }
        if (this.done && this.client != null) {
            this.client.disconnect("done");
            this.client = null;
        }
        return this.done;
    }
    
    @Override
    public void commandExecuted(final IntraClient aClient) {
        this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        this.setAvailable(false, false, 0, 0, 0, 10);
        this.done = true;
        if (this.loggingIn) {
            this.loggingIn = false;
        }
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        ServerEntry.logger.log(Level.INFO, "Datareceived " + this.name);
    }
    
    @Override
    public void reschedule(final IntraClient aClient) {
        this.setAvailable(false, false, 0, 0, 0, 10);
    }
    
    @Override
    public void remove(final IntraClient aClient) {
        this.done = true;
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        this.maintaining = ((buffer.get() & 0x1) == 0x1);
        final int numsPlaying = buffer.getInt();
        final int maxLimit = buffer.getInt();
        final int secsToShutdown = buffer.getInt();
        final int mSize = buffer.getInt();
        this.setAvailable(true, this.maintaining, numsPlaying, maxLimit, secsToShutdown, mSize);
        this.timeOutAt = System.currentTimeMillis() + this.timeOutTime;
        ++this.pingcounter;
        if (this.pingcounter == 20) {
            this.pingcounter = 0;
        }
    }
    
    public String getConsumerKey() {
        return this.consumerKeyToUse;
    }
    
    public void setConsumerKeyToUse(final String aConsumerKey) {
        this.consumerKeyToUse = aConsumerKey;
    }
    
    public String getConsumerSecret() {
        return this.consumerSecretToUse;
    }
    
    public void setConsumerSecret(final String aConsumerSecret) {
        this.consumerSecretToUse = aConsumerSecret;
    }
    
    public String getApplicationToken() {
        return this.applicationToken;
    }
    
    public void setApplicationToken(final String aApplicationToken) {
        this.applicationToken = aApplicationToken;
    }
    
    public String getApplicationSecret() {
        return this.applicationSecret;
    }
    
    public void setApplicationSecret(final String aApplicationSecret) {
        this.applicationSecret = aApplicationSecret;
    }
    
    void addExistingKingdom(final byte kingdomId) {
        if (!this.kingdomExists(kingdomId)) {
            this.existingKingdoms.add(kingdomId);
        }
    }
    
    boolean kingdomExists(final byte kingdomId) {
        return this.existingKingdoms.contains(kingdomId);
    }
    
    boolean removeKingdom(final byte kingdomId) {
        return this.existingKingdoms.remove(kingdomId);
    }
    
    public Set<Byte> getExistingKingdoms() {
        return this.existingKingdoms;
    }
    
    public byte getKingdom() {
        return this.KINGDOM;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getId() {
        return this.id;
    }
    
    @Override
    public String toString() {
        return "ServerEntry [id: " + this.id + ", Name: " + this.name + ", IntraIP: " + this.INTRASERVERADDRESS + ':' + this.INTRASERVERPORT + ", ExternalIP: " + this.EXTERNALIP + ':' + this.EXTERNALPORT + ", canTwit: " + this.canTwit() + ']';
    }
    
    public final void setChampTwitter(final String newChampConsumerKeyToUse, final String newChampConsumerSecretToUse, final String newChampApplicationToken, final String newChampApplicationSecret) {
        this.champConsumerKeyToUse = newChampConsumerKeyToUse;
        this.champConsumerSecretToUse = newChampConsumerSecretToUse;
        this.champApplicationToken = newChampApplicationToken;
        this.champApplicationSecret = newChampApplicationSecret;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET CHAMPTWITKEY=?,CHAMPTWITSECRET=?,CHAMPTWITAPP=?,CHAMPTWITAPPSECRET=? WHERE SERVER=?");
            ps.setString(1, this.champConsumerKeyToUse);
            ps.setString(2, this.champConsumerSecretToUse);
            ps.setString(3, this.champApplicationToken);
            ps.setString(4, this.champApplicationSecret);
            ps.setInt(5, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to set champ stamp for localserver ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        this.canTwit();
    }
    
    public final void setChampStamp() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.lastDecreasedChampionPoints = System.currentTimeMillis();
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET LASTRESETCHAMPS=? WHERE SERVER=?");
            ps.setLong(1, this.lastDecreasedChampionPoints);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to set champ stamp for localserver ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final boolean saveTwitter() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET TWITKEY=?,TWITSECRET=?,TWITAPP=?,TWITAPPSECRET=? WHERE SERVER=?");
            ps.setString(1, this.consumerKeyToUse);
            ps.setString(2, this.consumerSecretToUse);
            ps.setString(3, this.applicationToken);
            ps.setString(4, this.applicationSecret);
            ps.setInt(5, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to save twitter for server " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        return this.canTwit();
    }
    
    public final void movedArtifacts() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.setMovedArtifacts(System.currentTimeMillis());
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET MOVEDARTIS=? WHERE SERVER=?");
            ps.setLong(1, this.getMovedArtifacts());
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to set moved artifacts stamp for localserver ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void setCAHelpGroup(final byte dbCAHelpGroup) {
        this.caHelpGroup = dbCAHelpGroup;
    }
    
    public final byte getCAHelpGroup() {
        return this.caHelpGroup;
    }
    
    public final void saveChallengeTimes() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET CHALLENGESTARTED=?, CHALLENGEEND=? WHERE SERVER=?");
            ps.setLong(1, this.challengeStarted);
            ps.setLong(2, this.challengeEnds);
            ps.setLong(3, this.getId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to update ChallengeTimes for localserver ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void updateCAHelpGroup(final byte newCAHelpGroup) {
        if (this.caHelpGroup != newCAHelpGroup) {
            this.caHelpGroup = newCAHelpGroup;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getLoginDbCon();
                ps = dbcon.prepareStatement("UPDATE SERVERS SET CAHELPGROUP=? WHERE SERVER=?");
                ps.setByte(1, newCAHelpGroup);
                ps.setInt(2, this.id);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                ServerEntry.logger.log(Level.WARNING, "Failed to update CAHelp Group for localserver ", sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public final void setLastSpawnedUnique(final long val) {
        this.lastSpawnedUnique = val;
    }
    
    public final long getLastSpawnedUnique() {
        return this.lastSpawnedUnique;
    }
    
    public final void spawnedUnique() {
        this.lastSpawnedUnique = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET SPAWNEDUNIQUE=? WHERE SERVER=?");
            ps.setLong(1, this.lastSpawnedUnique);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to set moved artifacts stamp for localserver ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void movePlayersFromId(final int oldId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET CURRENTSERVER=? WHERE CURRENTSERVER=?");
            ps.setInt(1, this.id);
            ps.setInt(2, oldId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to move players from server id " + oldId + " to localserver id " + this.id, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void saveNewGui(final int oldId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET SERVER=?,NAME=?,MAXCREATURES=?,MAXPLAYERS=?,PERCENT_AGG_CREATURES=?,TREEGROWTH=?,SKILLGAINRATE=?,ACTIONTIMER=?,HOTADELAY=?,PVP=?,          HOMESERVER=?,KINGDOM=?,INTRASERVERPASSWORD=?,EXTERNALIP=?,EXTERNALPORT=?,INTRASERVERADDRESS=?,INTRASERVERPORT=?,ISTEST=?,ISPAYMENT=?,LOGINSERVER=?,           RMIPORT=?,REGISTRATIONPORT=?,LOCAL=?,RANDOMSPAWNS=?,SKILLBASICSTART=?,SKILLMINDLOGICSTART=?,SKILLFIGHTINGSTART=?,SKILLOVERALLSTART=?,EPIC=?,CRMOD=?,            STEAMPW=?,UPKEEP=?,MAXDEED=?,FREEDEEDS=?,TRADERMAX=?,TRADERINIT=?,BREEDING=?,FIELDGROWTH=?,KINGSMONEY=?, MOTD=?,     TUNNELING=?,SKILLBODYCONTROLSTART=? WHERE SERVER=?");
            ps.setInt(1, this.id);
            ps.setString(2, this.name);
            ps.setInt(3, this.maxCreatures);
            ps.setInt(4, this.pLimit);
            ps.setFloat(5, this.percentAggCreatures);
            ps.setInt(6, this.treeGrowth);
            ps.setFloat(7, this.skillGainRate);
            ps.setFloat(8, this.actionTimer);
            ps.setInt(9, this.hotaDelay);
            ps.setBoolean(10, this.PVPSERVER);
            ps.setBoolean(11, this.HOMESERVER);
            ps.setByte(12, this.KINGDOM);
            ps.setString(13, this.INTRASERVERPASSWORD);
            ps.setString(14, this.EXTERNALIP);
            ps.setString(15, this.EXTERNALPORT);
            ps.setString(16, this.INTRASERVERADDRESS);
            ps.setString(17, this.INTRASERVERPORT);
            ps.setBoolean(18, this.testServer);
            ps.setBoolean(19, this.ISPAYMENT);
            ps.setBoolean(20, this.LOGINSERVER);
            ps.setString(21, String.valueOf(this.RMI_PORT));
            ps.setString(22, String.valueOf(this.REGISTRATION_PORT));
            ps.setBoolean(23, this.isLocal);
            ps.setBoolean(24, this.randomSpawns);
            ps.setFloat(25, this.getSkillbasicval());
            ps.setFloat(26, this.getSkillmindval());
            ps.setFloat(27, this.getSkillfightval());
            ps.setFloat(28, this.getSkilloverallval());
            ps.setBoolean(29, this.EPIC);
            ps.setFloat(30, this.getCombatRatingModifier());
            ps.setString(31, this.getSteamServerPassword());
            ps.setBoolean(32, this.isUpkeep());
            ps.setInt(33, this.getMaxDeedSize());
            ps.setBoolean(34, this.isFreeDeeds());
            ps.setInt(35, this.getTraderMaxIrons());
            ps.setInt(36, this.getInitialTraderIrons());
            ps.setLong(37, this.getBreedingTimer());
            ps.setLong(38, this.getFieldGrowthTime());
            ps.setInt(39, this.getKingsmoneyAtRestart());
            ps.setString(40, this.motd);
            ps.setInt(41, this.getTunnelingHits());
            ps.setFloat(42, this.getSkillbcval());
            ps.setInt(43, oldId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to save new stuff from gui or command line", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void saveTimers() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET SKILLDAYSWITCH=?,SKILLWEEKSWITCH=?,NEXTEPICPOLL=?,FATIGUESWITCH=?,NEXTHOTA=?,WORLDTIME=?,TILEREST=?,POLLTILE=?,POLLMOD=?,POLLROUND=? WHERE SERVER=?");
            ps.setLong(1, this.getSkillDaySwitch());
            ps.setLong(2, this.getSkillWeekSwitch());
            ps.setLong(3, this.getNextEpicPoll());
            ps.setLong(4, this.getFatigueSwitch());
            ps.setLong(5, this.getNextHota());
            ps.setLong(6, WurmCalendar.getCurrentTime());
            ps.setInt(7, TilePoller.rest);
            ps.setInt(8, TilePoller.currentPollTile);
            ps.setInt(9, TilePoller.pollModifier);
            ps.setInt(10, TilePoller.pollround);
            ps.setInt(11, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ServerEntry.logger.log(Level.WARNING, "Failed to set time stamps for localserver ", sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public int getCurrentPlayersForSort() {
        if (this.id == 3) {
            return 1000;
        }
        return this.currentPlayers;
    }
    
    @Override
    public int compareTo(final ServerEntry entry) {
        return this.getCurrentPlayersForSort() - entry.getCurrentPlayersForSort();
    }
    
    public String getAbbreviation() {
        if (this.testServer) {
            return this.name.substring(0, 2) + this.name.substring(this.name.length() - 1);
        }
        return this.name.substring(0, 3);
    }
    
    public long getMovedArtifacts() {
        return this.movedArtifacts;
    }
    
    public void setMovedArtifacts(final long aMovedArtifacts) {
        this.movedArtifacts = aMovedArtifacts;
    }
    
    public long getSkillDaySwitch() {
        return this.skillDaySwitch;
    }
    
    public void setSkillDaySwitch(final long aSkillDaySwitch) {
        this.skillDaySwitch = aSkillDaySwitch;
    }
    
    public long getSkillWeekSwitch() {
        return this.skillWeekSwitch;
    }
    
    public void setSkillWeekSwitch(final long aSkillWeekSwitch) {
        this.skillWeekSwitch = aSkillWeekSwitch;
    }
    
    public long getNextEpicPoll() {
        return this.nextEpicPoll;
    }
    
    public void setNextEpicPoll(final long aNextEpicPoll) {
        this.nextEpicPoll = aNextEpicPoll;
    }
    
    public long getFatigueSwitch() {
        return this.fatigueSwitch;
    }
    
    public void setFatigueSwitch(final long aFatigueSwitch) {
        this.fatigueSwitch = aFatigueSwitch;
    }
    
    public long getNextHota() {
        return this.nextHota;
    }
    
    public void setNextHota(final long aNextHota) {
        this.nextHota = aNextHota;
    }
    
    public Spawnpoint[] getSpawns() {
        return this.spawns;
    }
    
    public void setSpawns(final Spawnpoint[] aSpawns) {
        this.spawns = aSpawns;
    }
    
    public void updateSpawns() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET SPAWNPOINTJENNX=?,SPAWNPOINTJENNY=?,SPAWNPOINTLIBX=?,SPAWNPOINTLIBY=?,SPAWNPOINTMOLX=?,SPAWNPOINTMOLY=? WHERE SERVER=?");
            ps.setInt(1, this.SPAWNPOINTJENNX);
            ps.setInt(2, this.SPAWNPOINTJENNY);
            ps.setInt(3, this.SPAWNPOINTLIBX);
            ps.setInt(4, this.SPAWNPOINTLIBY);
            ps.setInt(5, this.SPAWNPOINTMOLX);
            ps.setInt(6, this.SPAWNPOINTMOLY);
            ps.setInt(7, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            ServerEntry.logger.log(Level.WARNING, "Failed to update spawnpoints." + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getChallengeStarted() {
        return this.challengeStarted;
    }
    
    public void setChallengeStarted(final long challengeStarted) {
        this.challengeStarted = challengeStarted;
    }
    
    public long getChallengeEnds() {
        return this.challengeEnds;
    }
    
    public void setChallengeEnds(final long challengeEnds) {
        this.challengeEnds = challengeEnds;
    }
    
    public float getSkillGainRate() {
        return this.skillGainRate;
    }
    
    public void setSkillGainRate(final float skillGainRate) {
        this.skillGainRate = skillGainRate;
    }
    
    public float getActionTimer() {
        return this.actionTimer;
    }
    
    public void setActionTimer(final float actionTimer) {
        this.actionTimer = actionTimer;
    }
    
    public int getHotaDelay() {
        return this.hotaDelay;
    }
    
    public void setHotaDelay(final int hotaDelay) {
        this.hotaDelay = hotaDelay;
    }
    
    public float getSkillfightval() {
        return this.skillfightval;
    }
    
    public void setSkillfightval(final float skillfightval) {
        this.skillfightval = skillfightval;
    }
    
    public float getSkillbasicval() {
        return this.skillbasicval;
    }
    
    public void setSkillbasicval(final float skillbasicval) {
        this.skillbasicval = skillbasicval;
    }
    
    public float getSkillmindval() {
        return this.skillmindval;
    }
    
    public void setSkillmindval(final float skillmindval) {
        this.skillmindval = skillmindval;
    }
    
    public float getSkilloverallval() {
        return this.skilloverallval;
    }
    
    public void setSkilloverallval(final float skilloverallval) {
        this.skilloverallval = skilloverallval;
    }
    
    public float getCombatRatingModifier() {
        return this.combatRatingModifier;
    }
    
    public void setCombatRatingModifier(final float combatRatingModifier) {
        this.combatRatingModifier = combatRatingModifier;
    }
    
    public String getSteamServerPassword() {
        return this.steamServerPassword;
    }
    
    public void setSteamServerPassword(final String steamServerPassword) {
        this.steamServerPassword = steamServerPassword;
    }
    
    public boolean isUpkeep() {
        return this.upkeep;
    }
    
    public void setUpkeep(final boolean upkeep) {
        this.upkeep = upkeep;
    }
    
    public boolean isFreeDeeds() {
        return this.freeDeeds;
    }
    
    public void setFreeDeeds(final boolean freeDeeds) {
        this.freeDeeds = freeDeeds;
    }
    
    public int getMaxDeedSize() {
        return this.maxDeedSize;
    }
    
    public void setMaxDeedSize(final int maxDeedSize) {
        this.maxDeedSize = maxDeedSize;
    }
    
    public int getTraderMaxIrons() {
        return this.traderMaxIrons;
    }
    
    public void setTraderMaxIrons(final int traderMaxIrons) {
        this.traderMaxIrons = traderMaxIrons;
    }
    
    public int getInitialTraderIrons() {
        return this.initialTraderIrons;
    }
    
    public void setInitialTraderIrons(final int initialTraderIrons) {
        this.initialTraderIrons = initialTraderIrons;
    }
    
    public int getTunnelingHits() {
        return this.tunnelingHits;
    }
    
    public void setTunnelingHits(final int tunnelingHits) {
        this.tunnelingHits = tunnelingHits;
    }
    
    public long getBreedingTimer() {
        return this.breedingTimer;
    }
    
    public void setBreedingTimer(final long breedingTimer) {
        this.breedingTimer = breedingTimer;
    }
    
    public long getFieldGrowthTime() {
        return this.fieldGrowthTime;
    }
    
    public void setFieldGrowthTime(final long fieldGrowthTime) {
        this.fieldGrowthTime = fieldGrowthTime;
    }
    
    public int getKingsmoneyAtRestart() {
        return this.kingsmoneyAtRestart;
    }
    
    public void setKingsmoneyAtRestart(final int kingsmoneyAtRestart) {
        this.kingsmoneyAtRestart = kingsmoneyAtRestart;
    }
    
    public String getMotd() {
        return this.motd;
    }
    
    public final boolean hasMotd() {
        return this.getMotd() != null && this.getMotd().length() > 0;
    }
    
    public void setMotd(final String nmotd) {
        this.motd = nmotd;
        if (this.hasMotd()) {
            Constants.motd = this.motd;
        }
    }
    
    public float getSkillbcval() {
        return this.skillbcval;
    }
    
    public void setSkillbcval(final float skillbcval) {
        this.skillbcval = skillbcval;
    }
    
    static {
        logger = Logger.getLogger(ServerEntry.class.getName());
    }
}
