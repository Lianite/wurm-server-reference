// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.webinterface.WcTabLists;
import com.wurmonline.server.webinterface.WcDemotion;
import com.wurmonline.server.support.VoteQuestion;
import javax.annotation.Nullable;
import com.wurmonline.server.support.TicketAction;
import com.wurmonline.server.webinterface.WCGmMessage;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.players.MapAnnotation;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import com.wurmonline.website.StatsXMLWriter;
import com.wurmonline.server.players.WurmRecord;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.kingdom.Kingdoms;
import java.util.HashSet;
import com.wurmonline.server.steam.SteamId;
import com.wurmonline.server.players.SteamIdBan;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.players.IPBan;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.players.DbSearcher;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.Trap;
import javax.annotation.Nonnull;
import com.wurmonline.math.TilePos;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.endgames.EndGameItem;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.support.Ticket;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.players.KingdomIp;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.tutorial.MissionPerformer;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.villages.Reputation;
import com.wurmonline.server.questions.KosWarningInfo;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.creatures.NoSuchCreatureTemplateException;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import com.wurmonline.communication.SocketConnection;
import java.util.Optional;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.Iterator;
import com.wurmonline.server.players.PlayerInfo;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.creatures.Creature;
import java.util.HashMap;
import com.wurmonline.server.villages.KosWarning;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.wurmonline.server.players.PlayerKills;
import com.wurmonline.server.players.Ban;
import java.util.Set;
import com.wurmonline.server.players.Artist;
import java.util.logging.Logger;
import com.wurmonline.server.players.TabData;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.players.Player;
import java.util.Map;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.shared.constants.EffectConstants;
import com.wurmonline.server.creatures.CreatureTemplateIds;

public final class Players implements MiscConstants, CreatureTemplateIds, EffectConstants, MonetaryConstants, TimeConstants
{
    private static Map<String, Player> players;
    private static Map<Long, Player> playersById;
    private final Map<Long, Byte> pkingdoms;
    private static final ConcurrentHashMap<String, TabData> tabListGM;
    private static final ConcurrentHashMap<String, TabData> tabListMGMT;
    private static Players instance;
    private static Logger logger;
    private static final String DOES_PLAYER_NAME_EXIST = "SELECT WURMID FROM PLAYERS WHERE NAME=?";
    private static final Map<Long, Artist> artists;
    private static final String GET_ARTISTS = "SELECT * FROM ARTISTS";
    private static final String SET_ARTISTS = "INSERT INTO ARTISTS (WURMID,SOUND,GRAPHICS) VALUES(?,?,?)";
    private static final String DELETE_ARTIST = "DELETE FROM ARTISTS WHERE WURMID=?";
    private static final String GET_PLAYERS_BANNED = "SELECT NAME,BANREASON,BANEXPIRY FROM PLAYERS WHERE BANNED=1";
    private static final String SET_NOSTRUCTURE = "update PLAYERS set BUILDINGID=-10 WHERE BUILDINGID=?";
    private static final String GET_LASTLOGOUT = "SELECT LASTLOGOUT FROM PLAYERS WHERE WURMID=?";
    private static final String GET_KINGDOM = "SELECT KINGDOM FROM PLAYERS WHERE WURMID=?";
    private static final String GET_KINGDOM_PLAYERS = "SELECT NAME,WURMID FROM PLAYERS WHERE KINGDOM=? AND CURRENTSERVER=? AND POWER=0";
    private static final String GET_PREMIUM_KINGDOM_PLAYERS = "SELECT NAME,WURMID FROM PLAYERS WHERE KINGDOM=? AND PAYMENTEXPIRE>? AND POWER=0";
    private static final String GET_CHAMPION_KINGDOM_PLAYERS = "SELECT NAME,WURMID,REALDEATH,LASTLOSTCHAMPION FROM PLAYERS WHERE KINGDOM=? AND REALDEATH>0 AND REALDEATH<4 AND POWER=0";
    private static final String RESET_FAITHGAIN = "UPDATE PLAYERS SET LASTFAITH=0,NUMFAITH=0";
    private static final String GM_SALARY = "UPDATE PLAYERS SET MONEY=MONEY+250000 WHERE POWER>1";
    private static final String RESET_PLAYER_SKILLS = "UPDATE SKILLS SET VALUE=20, MINVALUE=20 WHERE VALUE>20 AND OWNER=?";
    private static final String RESET_PLAYER_FAITH = "UPDATE PLAYERS SET FAITH=20 WHERE FAITH>20 AND WURMID=?";
    private static final String ADD_GM_MESSAGE = "INSERT INTO GMMESSAGES(TIME,SENDER,MESSAGE) VALUES(?,?,?)";
    private static final String ADD_MGMT_MESSAGE = "INSERT INTO MGMTMESSAGES(TIME,SENDER,MESSAGE) VALUES(?,?,?)";
    private static final String GET_GM_MESSAGES = "SELECT TIME,SENDER,MESSAGE FROM GMMESSAGES ORDER BY TIME";
    private static final String GET_MGMT_MESSAGES = "SELECT TIME,SENDER,MESSAGE FROM MGMTMESSAGES ORDER BY TIME";
    private static final String PRUNE_GM_MESSAGES = "DELETE FROM GMMESSAGES WHERE TIME<?";
    private static final String PRUNE_MGMT_MESSAGES = "DELETE FROM MGMTMESSAGES WHERE TIME<?";
    private static final String GET_BATTLE_RANKS = "select RANK, NAME from PLAYERS ORDER BY RANK DESC LIMIT ?";
    private static final String GET_MAXBATTLE_RANKS = "select MAXRANK,RANK, NAME from PLAYERS ORDER BY MAXRANK DESC LIMIT ?";
    private static final String GET_FRIENDS = "select p.NAME,p.WURMID from PLAYERS p INNER JOIN FRIENDS f ON f.FRIEND=p.WURMID WHERE f.WURMID=? ORDER BY NAME";
    private static final String GET_PLAYERID_BY_NAME = "SELECT WURMID FROM PLAYERS WHERE NAME=?";
    private static final String GET_PLAYERS_MUTED = "SELECT NAME,MUTEREASON,MUTEEXPIRY FROM PLAYERS WHERE MUTED=1";
    private static final String GET_MUTERS = "SELECT NAME FROM PLAYERS WHERE MAYMUTE=1";
    private static final String GET_DEVTALKERS = "SELECT NAME FROM PLAYERS WHERE DEVTALK=1";
    private static final String GET_CAS = "SELECT NAME FROM PLAYERS WHERE PA=1";
    private static final String GET_HEROS = "SELECT NAME FROM PLAYERS WHERE POWER=? AND CURRENTSERVER=?";
    private static final String GET_PRIVATE_MAP_POI = "SELECT * FROM MAP_ANNOTATIONS WHERE POITYPE=0 AND OWNERID=?";
    private static final String CHANGE_KINGDOM = "UPDATE PLAYERS SET KINGDOM=? WHERE KINGDOM=?";
    private static final String CHANGE_KINGDOM_FOR_PLAYER = "UPDATE PLAYERS SET KINGDOM=? WHERE WURMID=?";
    public static final String CACHAN = "CA HELP";
    public static final String GVCHAN = "GV HELP";
    public static final String JKCHAN = "JK HELP";
    public static final String MRCHAN = "MR HELP";
    public static final String HOTSCHAN = "HOTS HELP";
    private static final String CAPREFIX = " CA ";
    private static final Map<String, Logger> loggers;
    private static Set<Ban> bans;
    private static final Map<Long, PlayerKills> playerKills;
    private final Map<Byte, Float> crBonuses;
    private boolean shouldSendWeather;
    private final long timeBetweenChampDecreases = 604800000L;
    private static ConcurrentLinkedQueue<KosWarning> kosList;
    private static String header;
    private long lastPoll;
    private static final float minDelta = 0.095f;
    private static boolean pollCheckClients;
    private long lastCheckClients;
    private static int challengeStep;
    private static HashMap<Long, Short> deathCount;
    private static final Logger caHelpLogger;
    private static String header2;
    private static final String footer2 = "\n</BODY>\n</HTML>";
    private static String headerStats;
    private static String headerStats2;
    private static final String footerStats = "\n</BODY>\n</HTML>";
    
    public static Players getInstance() {
        if (Players.instance == null) {
            Players.instance = new Players();
        }
        return Players.instance;
    }
    
    static synchronized Players getInstanceForUnitTestingWithoutDatabase() {
        if (Players.instance == null) {
            Players.instance = new Players(true);
        }
        return Players.instance;
    }
    
    public void setCreatureDead(final Creature dead) {
        final long deadid = dead.getWurmId();
        final Player[] players;
        final Player[] plays = players = getInstance().getPlayers();
        for (final Player player : players) {
            if (player.opponent == dead) {
                player.setOpponent(null);
            }
            if (player.target == deadid) {
                player.setTarget(-10L, true);
            }
            player.removeTarget(deadid);
        }
        Vehicles.removeDragger(dead);
    }
    
    private Players() {
        this.pkingdoms = new ConcurrentHashMap<Long, Byte>();
        this.crBonuses = new HashMap<Byte, Float>();
        this.shouldSendWeather = false;
        this.lastPoll = System.currentTimeMillis();
        this.lastCheckClients = System.currentTimeMillis();
        this.loadBannedIps();
        this.loadBannedSteamIds();
        Players.header = getBattleRanksHtmlHeader();
    }
    
    private Players(final boolean forUnitTestingWithoutDatabase) {
        this.pkingdoms = new ConcurrentHashMap<Long, Byte>();
        this.crBonuses = new HashMap<Byte, Float>();
        this.shouldSendWeather = false;
        this.lastPoll = System.currentTimeMillis();
        this.lastCheckClients = System.currentTimeMillis();
        if (forUnitTestingWithoutDatabase) {
            Players.logger.warning("Instantiating Players for Unit Test without a database");
        }
        else {
            this.loadBannedIps();
            this.loadBannedSteamIds();
        }
    }
    
    static String getBattleRanksHtmlHeader() {
        return "<HTML> <HEAD><TITLE>Wurm battle ranks on " + Servers.getLocalServerName() + "</TITLE></HEAD><BODY><BR><BR>";
    }
    
    public int numberOfPlayers() {
        return Players.players.size();
    }
    
    public int numberOfPremiumPlayers() {
        int x = 0;
        for (final Player lPlayer : getInstance().getPlayers()) {
            if (lPlayer.isPaying() && lPlayer.getPower() == 0) {
                ++x;
            }
        }
        return x;
    }
    
    public void weatherFlash(final int tilex, final int tiley, final float height) {
        for (final Player p : getInstance().getPlayers()) {
            if (p != null) {
                final Communicator lPlayerCommunicator = p.getCommunicator();
                if (lPlayerCommunicator != null) {
                    lPlayerCommunicator.sendAddEffect(9223372036854775707L, (short)1, tilex << 2, tiley << 2, height, (byte)0);
                }
            }
        }
    }
    
    public void sendGlobalNonPersistantComplexEffect(final long target, final short effect, final int tilex, final int tiley, final float height, final float radiusMeters, final float lengthMeters, final int direction, final byte kingdomTemplateId, final byte epicEntityId) {
        final long effectId = Long.MAX_VALUE - Server.rand.nextInt(1000);
        for (final Player p : getInstance().getPlayers()) {
            if (p != null) {
                final Communicator lPlayerCommunicator = p.getCommunicator();
                if (lPlayerCommunicator != null) {
                    lPlayerCommunicator.sendAddComplexEffect(effectId, target, effect, tilex << 2, tiley << 2, height, (byte)0, radiusMeters, lengthMeters, direction, kingdomTemplateId, epicEntityId);
                }
            }
        }
    }
    
    public void sendGlobalNonPersistantEffect(final long id, final short effect, final int tilex, final int tiley, final float height) {
        final long effectId = Long.MAX_VALUE - Server.rand.nextInt(1000);
        for (final Player p : getInstance().getPlayers()) {
            if (p != null) {
                final Communicator lPlayerCommunicator = p.getCommunicator();
                if (lPlayerCommunicator != null) {
                    lPlayerCommunicator.sendAddEffect((id <= 0L) ? effectId : id, effect, tilex << 2, tiley << 2, height, (byte)0);
                }
            }
        }
    }
    
    public void sendGlobalNonPersistantTimedEffect(final long id, final short effect, final int tilex, final int tiley, final float height, final long expireTime) {
        final long effectId = (id <= 0L) ? (Long.MAX_VALUE - Server.rand.nextInt(10000)) : id;
        Server.getInstance().addGlobalTempEffect(effectId, expireTime);
        for (final Player p : getInstance().getPlayers()) {
            if (p != null) {
                final Communicator lPlayerCommunicator = p.getCommunicator();
                if (lPlayerCommunicator != null) {
                    lPlayerCommunicator.sendAddEffect(effectId, effect, tilex << 2, tiley << 2, height, (byte)0);
                }
            }
        }
    }
    
    public final int getChallengeStep() {
        return Players.challengeStep;
    }
    
    public final void setChallengeStep(final int step) {
        if (Servers.localServer.isChallengeServer() || Servers.localServer.testServer) {
            Players.challengeStep = step;
            byte toSend = 0;
            switch (Players.challengeStep) {
                case 1: {
                    toSend = 20;
                    break;
                }
                case 2: {
                    toSend = 21;
                    break;
                }
                case 3: {
                    toSend = 22;
                    break;
                }
                case 4: {
                    toSend = 23;
                    break;
                }
                default: {
                    toSend = 0;
                    break;
                }
            }
            if (toSend > 0) {
                this.sendGlobalNonPersistantEffect(Long.MAX_VALUE - Server.rand.nextInt(100000), toSend, 0, 0, 0.0f);
            }
        }
    }
    
    public void sendPlayerStatus(final Player player) {
        for (final Player p : getInstance().getPlayers()) {
            player.getCommunicator().sendNormalServerMessage(p.getName() + ", secstolog=" + p.getSecondsToLogout() + ", logged off=" + p.loggedout);
        }
    }
    
    public int getOnlinePlayersFromKingdom(final byte kingdomId) {
        int nums = 0;
        for (final Player lPlayer : getInstance().getPlayers()) {
            if (lPlayer.getKingdomId() == kingdomId) {
                ++nums;
            }
        }
        return nums;
    }
    
    public Player[] getPlayersByIp() {
        final Player[] playerArr = this.getPlayers();
        Arrays.sort(playerArr, new Comparator<Player>() {
            @Override
            public int compare(final Player o1, final Player o2) {
                return o1.getSaveFile().getIpaddress().compareTo(o2.getSaveFile().getIpaddress());
            }
        });
        return playerArr;
    }
    
    public void sendIpsToPlayer(final Player player) {
        final Player[] playersByIp;
        final Player[] playerArr = playersByIp = this.getPlayersByIp();
        for (final Player lPlayer : playersByIp) {
            if (lPlayer.getPower() <= player.getPower() && player.getPower() > 1) {
                player.getCommunicator().sendNormalServerMessage(lPlayer.getName() + " IP: " + lPlayer.getSaveFile().getIpaddress());
            }
        }
        player.getCommunicator().sendNormalServerMessage(playerArr.length + " players logged on.");
    }
    
    public void sendIpsToPlayer(final Player player, final String playername) {
        PlayerInfo pinfo = null;
        try {
            pinfo = this.getPlayer(playername).getSaveFile();
        }
        catch (NoSuchPlayerException nsp) {
            pinfo = PlayerInfoFactory.createPlayerInfo(playername);
            try {
                pinfo.load();
            }
            catch (IOException iox) {
                Players.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
        }
        if (pinfo != null) {
            if (pinfo.getPower() <= player.getPower() && player.getPower() > 1) {
                final Player[] playerArr = this.getPlayersByIp();
                final Map<String, String> ps = new HashMap<String, String>();
                final boolean error = false;
                ps.put(playername, pinfo.getIpaddress());
                for (final Player lPlayer : playerArr) {
                    if (lPlayer.getSaveFile().getIpaddress().equals(pinfo.getIpaddress())) {
                        ps.put(lPlayer.getName(), lPlayer.getSaveFile().getIpaddress());
                    }
                }
                for (final String name : ps.keySet()) {
                    final String ip = ps.get(name);
                    player.getCommunicator().sendNormalServerMessage(name + ", " + ip);
                }
            }
            else {
                player.getCommunicator().sendNormalServerMessage("You may not check that player's ip.");
            }
        }
        else {
            player.getCommunicator().sendNormalServerMessage(playername + " - not found!");
        }
    }
    
    public static void stopLoggers() {
        for (final Logger logger : Players.loggers.values()) {
            if (logger != null) {
                for (final Handler h : logger.getHandlers()) {
                    h.close();
                }
            }
        }
    }
    
    public static Logger getLogger(final Player player) {
        if (player.getPower() > 0 || player.isLogged() || isArtist(player.getWurmId(), false, false)) {
            final String name = player.getName();
            Logger personalLogger = Players.loggers.get(name);
            if (personalLogger == null) {
                personalLogger = Logger.getLogger(name);
                personalLogger.setUseParentHandlers(false);
                final Handler[] h = Players.logger.getHandlers();
                for (int i = 0; i != h.length; ++i) {
                    personalLogger.removeHandler(h[i]);
                }
                try {
                    final FileHandler fh = new FileHandler(name + ".log", 0, 1, true);
                    fh.setFormatter(new SimpleFormatter());
                    personalLogger.addHandler(fh);
                }
                catch (IOException ie) {
                    Logger.getLogger(name).log(Level.WARNING, name + ":no redirection possible!");
                }
                Players.loggers.put(name, personalLogger);
            }
            return personalLogger;
        }
        return null;
    }
    
    public Player getPlayer(final String name) throws NoSuchPlayerException {
        final Player p = this.getPlayerByName(LoginHandler.raiseFirstLetter(name));
        if (p == null) {
            throw new NoSuchPlayerException(name);
        }
        return p;
    }
    
    private Player getPlayerByName(final String aName) {
        return Players.players.get(aName);
    }
    
    public Player getPlayerOrNull(final String aName) {
        return this.getPlayerByName(aName);
    }
    
    public Optional<Player> getPlayerOptional(final String aName) {
        return Optional.ofNullable(this.getPlayerByName(aName));
    }
    
    public Player getPlayer(final long id) throws NoSuchPlayerException {
        final Player p = this.getPlayerById(id);
        if (p != null) {
            return p;
        }
        throw new NoSuchPlayerException("Player with id " + id + " could not be found.");
    }
    
    public Player getPlayerOrNull(final long id) {
        return this.getPlayerById(id);
    }
    
    public Optional<Player> getPlayerOptional(final long id) {
        return Optional.ofNullable(this.getPlayerById(id));
    }
    
    public Player getPlayer(final Long id) throws NoSuchPlayerException {
        final Player p = this.getPlayerById(id);
        if (p != null) {
            return p;
        }
        throw new NoSuchPlayerException("Player with id " + id + " could not be found.");
    }
    
    private Player getPlayerById(final long aWurmID) {
        return this.getPlayerById(new Long(aWurmID));
    }
    
    private Player getPlayerById(final Long aWurmID) {
        return Players.playersById.get(aWurmID);
    }
    
    public Player getPlayer(final SocketConnection serverConnection) throws NoSuchPlayerException {
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            try {
                if (serverConnection == lPlayer.getCommunicator().getConnection()) {
                    return lPlayer;
                }
            }
            catch (NullPointerException ex) {
                if (lPlayer == null) {
                    Players.logger.log(Level.WARNING, "A player in the Players list is null. this shouldn't happen.");
                }
                else if (lPlayer.getCommunicator() == null) {
                    Players.logger.log(Level.WARNING, lPlayer + "'s communicator is null.");
                }
                else {
                    Players.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        throw new NoSuchPlayerException("Player could not be found.");
    }
    
    void sendReconnect(final Player player) {
        if (!player.isUndead()) {
            player.getCommunicator().sendClearFriendsList();
            this.sendConnectInfo(player, " reconnected.", player.getLastLogin(), PlayerOnlineStatus.ONLINE);
        }
    }
    
    void sendAddPlayer(final Player player) {
        if (!player.isUndead()) {
            this.sendConnectInfo(player, " joined.", player.getLastLogin(), PlayerOnlineStatus.ONLINE);
        }
    }
    
    public void sendConnectAlert(final String message) {
        final Player[] players;
        final Player[] playerArr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.getPower() > 0) {
                lPlayer.getCommunicator().sendAlertServerMessage(message);
            }
        }
    }
    
    public void sendConnectInfo(final Player player, final String message, final long whenStateChanged, final PlayerOnlineStatus loginstatus) {
        this.sendConnectInfo(player, message, whenStateChanged, loginstatus, false);
    }
    
    public void sendConnectInfo(final Player player, final String message, final long whenStateChanged, final PlayerOnlineStatus loginstatus, final boolean loggedin) {
        PlayerInfoFactory.updatePlayerState(player, whenStateChanged, loginstatus);
        if (!player.isUndead()) {
            final Player[] playerArr = this.getPlayers();
            final int tilex = player.getTileX();
            final int tiley = player.getTileY();
            final int tilez = (int)(player.getPositionZ() + player.getAltOffZ()) >> 2;
            int vision = 80;
            try {
                vision = CreatureTemplateFactory.getInstance().getTemplate(1).getVision();
            }
            catch (NoSuchCreatureTemplateException nst) {
                Players.logger.log(Level.WARNING, "Failed to find HUMAN_CID. Vision set to " + vision);
            }
            final Village village = player.getCitizenVillage();
            for (final Player lPlayer : playerArr) {
                if (player != lPlayer) {
                    if (lPlayer.getPower() > 1) {
                        if (player.getCommunicator() != null && player.getCommunicator().getConnection() != null && lPlayer.getPower() > player.getPower()) {
                            try {
                                lPlayer.getCommunicator().sendSystemMessage(player.getName() + "[" + player.getCommunicator().getConnection().getIp() + "] " + message);
                            }
                            catch (Exception ex) {
                                lPlayer.getCommunicator().sendSystemMessage(player.getName() + message);
                            }
                        }
                    }
                    else if (player.isVisibleTo(lPlayer) && (!loggedin || player.getPower() <= 1)) {
                        if (!lPlayer.isFriend(player.getWurmId())) {
                            if (village != null && lPlayer.getCitizenVillage() == village) {
                                lPlayer.getCommunicator().sendSafeServerMessage(player.getName() + message);
                            }
                            else if (lPlayer.isOnSurface() == player.isOnSurface() && lPlayer.isWithinTileDistanceTo(tilex, tiley, tilez, vision)) {
                                lPlayer.getCommunicator().sendSafeServerMessage(player.getName() + message);
                            }
                        }
                    }
                    if (lPlayer.seesPlayerAssistantWindow() && player.seesPlayerAssistantWindow()) {
                        if (player.isVisibleTo(lPlayer)) {
                            if (player.isPlayerAssistant()) {
                                lPlayer.getCommunicator().sendAddPa(" CA " + player.getName(), player.getWurmId());
                            }
                            else if (this.shouldReceivePlayerList(lPlayer)) {
                                lPlayer.getCommunicator().sendAddPa(player.getName(), player.getWurmId());
                            }
                        }
                        if (lPlayer.isVisibleTo(player)) {
                            if (lPlayer.isPlayerAssistant()) {
                                player.getCommunicator().sendAddPa(" CA " + lPlayer.getName(), lPlayer.getWurmId());
                            }
                            else if (this.shouldReceivePlayerList(player)) {
                                player.getCommunicator().sendAddPa(lPlayer.getName(), lPlayer.getWurmId());
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void combatRound() {
        for (final Player lPlayer : getInstance().getPlayers()) {
            lPlayer.getCombatHandler().clearRound();
        }
    }
    
    public void pollKosWarnings() {
        for (final KosWarning kos : Players.kosList) {
            try {
                final Player p = this.getPlayer(kos.playerId);
                if (p.isFullyLoaded() && p.getVisionArea() != null && p.getVisionArea().isInitialized() && p.hasLink()) {
                    if (kos.getTick() < 10) {
                        if (kos.tick() == 10) {
                            if (p.acceptsKosPopups(kos.village.getId())) {
                                final KosWarningInfo kwi = new KosWarningInfo(p, kos.playerId, kos.village);
                                kwi.sendQuestion();
                            }
                            else {
                                p.getCommunicator().sendAlertServerMessage("You are being put on the KOS list of " + kos.village.getName() + " again.", (byte)4);
                            }
                        }
                    }
                    else if (kos.getTick() % 30 == 0 && p.acceptsKosPopups(kos.village.getId())) {
                        if (p.getCurrentVillage() == kos.village) {
                            p.getCommunicator().sendAlertServerMessage("You must leave the settlement of " + kos.village.getName() + " immediately or you will be attacked by the guards!", (byte)4);
                        }
                        else {
                            p.getCommunicator().sendAlertServerMessage("Make sure to stay out of " + kos.village.getName() + " since you soon will be killed on sight there!", (byte)4);
                        }
                    }
                    if (kos.getTick() >= 130 && p.acceptsKosPopups(kos.village.getId())) {
                        p.getCommunicator().sendAlertServerMessage("You will now be killed on sight in " + kos.village.getName() + "!", (byte)4);
                    }
                }
            }
            catch (NoSuchPlayerException ex) {}
            final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(kos.playerId);
            if (pinf != null) {
                if (kos.getTick() < 10) {
                    continue;
                }
                kos.tick();
                if (kos.getTick() < 130) {
                    continue;
                }
                final Reputation r = kos.village.setReputation(kos.playerId, kos.newReputation, false, true);
                r.setPermanent(kos.permanent);
                kos.village.addHistory(pinf.getName(), "will now be killed on sight.");
                Players.kosList.remove(kos);
            }
            else {
                Players.kosList.remove(kos);
            }
        }
    }
    
    public final boolean addKosWarning(final KosWarning newkos) {
        for (final KosWarning kosw : Players.kosList) {
            if (kosw.playerId == newkos.playerId) {
                return false;
            }
        }
        Players.kosList.add(newkos);
        return true;
    }
    
    public final boolean removeKosFor(final long wurmId) {
        for (final KosWarning kos : Players.kosList) {
            if (kos.playerId == wurmId) {
                Players.kosList.remove(kos);
                return true;
            }
        }
        return false;
    }
    
    public void pollDeadPlayers() {
        final Player[] players;
        final Player[] playerarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer != null && lPlayer.getSaveFile() != null && lPlayer.pollDead()) {
                Players.logger.log(Level.INFO, "Removing from players " + lPlayer.getName() + ".");
                Players.players.remove(lPlayer.getName());
            }
        }
    }
    
    public void broadCastMissionInfo(final String missionInfo, final int missionRelated) {
        final Player[] playarr = this.getPlayers();
        for (int x = 0; x < playarr.length; ++x) {
            final MissionPerformer mp = MissionPerformed.getMissionPerformer(playarr[x].getWurmId());
            if (mp != null) {
                final MissionPerformed m = mp.getMission(missionRelated);
                if (m != null) {
                    playarr[x].getCommunicator().sendSafeServerMessage(missionInfo);
                }
            }
        }
    }
    
    public final void broadCastConquerInfo(final Creature conquerer, final String info) {
        final Player[] playarr = this.getPlayers();
        for (int x = 0; x < playarr.length; ++x) {
            int r = 200;
            int g = 200;
            final int b = 25;
            if (conquerer.isFriendlyKingdom(playarr[x].getKingdomId())) {
                r = 25;
            }
            else {
                g = 25;
            }
            playarr[x].getCommunicator().sendDeathServerMessage(info, (byte)r, (byte)g, (byte)25);
        }
    }
    
    public final void broadCastDestroyInfo(final Creature performer, final String info) {
        final Player[] playarr = getInstance().getPlayers();
        for (int x = 0; x < playarr.length; ++x) {
            int r = 200;
            int g = 200;
            final int b = 25;
            if (performer.getKingdomId() == playarr[x].getKingdomId()) {
                r = 25;
            }
            else {
                g = 25;
            }
            playarr[x].getCommunicator().sendDeathServerMessage(info, (byte)r, (byte)g, (byte)25);
        }
    }
    
    public final void broadCastBashInfo(final Item target, final String info) {
        final Player[] playarr = getInstance().getPlayers();
        for (int x = 0; x < playarr.length; ++x) {
            if (target.getKingdom() == playarr[x].getKingdomId()) {
                final int r = 200;
                final int g = 25;
                final int b = 25;
                playarr[x].getCommunicator().sendDeathServerMessage(info, (byte)r, (byte)g, (byte)b);
            }
        }
    }
    
    public final void broadCastDeathInfo(final Player player, final String slayers) {
        if (Servers.isThisAPvpServer()) {
            final String toSend = player.getName() + " slain by " + slayers;
            final Player[] playarr = this.getPlayers();
            for (int x = 0; x < playarr.length; ++x) {
                int r = 200;
                int g = 200;
                final int b = 25;
                if (player.isFriendlyKingdom(playarr[x].getKingdomId())) {
                    r = 25;
                }
                else {
                    g = 25;
                }
                playarr[x].getCommunicator().sendDeathServerMessage(toSend, (byte)r, (byte)g, (byte)25);
            }
        }
        else if (Features.Feature.PVE_DEATHTABS.isEnabled()) {
            final String toSend = player.getName() + " slain by " + slayers;
            for (final Player p : getInstance().getPlayers()) {
                if (!p.hasFlag(60)) {
                    p.getCommunicator().sendDeathServerMessage(toSend, (byte)25, (byte)(-56), (byte)25);
                }
            }
        }
    }
    
    public final void sendAddToAlliance(final Creature player, final Village village) {
        if (village != null) {
            final Player[] players;
            final Player[] playerArr = players = this.getPlayers();
            for (final Player lPlayer : players) {
                if (player != lPlayer && player.isVisibleTo(lPlayer) && lPlayer.getCitizenVillage() != null && village.getAllianceNumber() > 0 && village.getAllianceNumber() == lPlayer.getCitizenVillage().getAllianceNumber()) {
                    lPlayer.getCommunicator().sendAddAlly(player.getName(), player.getWurmId());
                    player.getCommunicator().sendAddAlly(lPlayer.getName(), lPlayer.getWurmId());
                }
            }
        }
    }
    
    public final void sendRemoveFromAlliance(final Creature player, final Village village) {
        if (village != null) {
            final Player[] players;
            final Player[] playerArr = players = this.getPlayers();
            for (final Player lPlayer : players) {
                if (player != lPlayer && lPlayer.getCitizenVillage() != null && village.getAllianceNumber() > 0 && village.getAllianceNumber() == lPlayer.getCitizenVillage().getAllianceNumber()) {
                    lPlayer.getCommunicator().sendRemoveAlly(player.getName());
                    player.getCommunicator().sendRemoveAlly(lPlayer.getName());
                }
            }
        }
    }
    
    public void addToGroups(final Player player) {
        if (!player.isUndead()) {
            try {
                Groups.getGroup("wurm").addMember(player.getName(), player);
            }
            catch (NoSuchGroupException ex) {
                Players.logger.log(Level.WARNING, "Could not get group for Group 'wurm', Player: " + player + " due to " + ex.getMessage(), ex);
            }
            final Village citvil = Villages.getVillageForCreature(player);
            player.setCitizenVillage(citvil);
            player.sendSkills();
            if (citvil != null) {
                try {
                    citvil.setLogin();
                    Groups.getGroup(citvil.getName()).addMember(player.getName(), player);
                    if (citvil.getAllianceNumber() > 0) {
                        final PvPAlliance pvpAll = PvPAlliance.getPvPAlliance(citvil.getAllianceNumber());
                        if (pvpAll != null && !pvpAll.getMotd().isEmpty()) {
                            final Message mess = pvpAll.getMotdMessage();
                            player.getCommunicator().sendMessage(mess);
                        }
                        else {
                            final Message mess = new Message(player, (byte)15, "Alliance", "");
                            player.getCommunicator().sendMessage(mess);
                        }
                    }
                }
                catch (NoSuchGroupException ex2) {
                    Players.logger.log(Level.WARNING, "Could not get group for Village: " + citvil + ", Player: " + player + " due to " + ex2.getMessage(), ex2);
                }
            }
            final Player[] playerArr = this.getPlayers();
            final Village village = player.getCitizenVillage();
            if (village != null) {
                for (final Player lPlayer : playerArr) {
                    if (player != lPlayer && player.isVisibleTo(lPlayer, true)) {
                        if (lPlayer.getCitizenVillage() == village) {
                            lPlayer.getCommunicator().sendAddVillager(player.getName(), player.getWurmId());
                        }
                        if (lPlayer.getCitizenVillage() != null && village.getAllianceNumber() > 0 && village.getAllianceNumber() == lPlayer.getCitizenVillage().getAllianceNumber()) {
                            lPlayer.getCommunicator().sendAddAlly(player.getName(), player.getWurmId());
                            player.getCommunicator().sendAddAlly(lPlayer.getName(), lPlayer.getWurmId());
                        }
                    }
                }
            }
        }
        else {
            player.sendSkills();
        }
    }
    
    private void removeFromGroups(final Player player) {
        try {
            Groups.getGroup("wurm").dropMember(player.getName());
            if (player.getCitizenVillage() != null) {
                Groups.getGroup(player.getCitizenVillage().getName()).dropMember(player.getName());
            }
        }
        catch (NoSuchGroupException nsg) {
            Players.logger.log(Level.WARNING, "Could not get group for Village: " + player.getCitizenVillage() + ", Player: " + player + " due to " + nsg.getMessage(), nsg);
        }
        if (player.mayHearDevTalk() || player.mayHearMgmtTalk()) {
            this.removeFromTabs(player.getWurmId(), player.getName());
            this.sendRemoveFromTabs(player.getWurmId(), player.getName());
        }
        final Village village = player.getCitizenVillage();
        final Player[] players;
        final Player[] playerArr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (player != lPlayer) {
                if (village != null) {
                    if (lPlayer.getCitizenVillage() == village) {
                        lPlayer.getCommunicator().sendRemoveVillager(player.getName());
                    }
                    if (lPlayer.getCitizenVillage() != null && village.getAllianceNumber() > 0 && village.getAllianceNumber() == lPlayer.getCitizenVillage().getAllianceNumber()) {
                        lPlayer.getCommunicator().sendRemoveAlly(player.getName());
                    }
                }
                if (player.seesPlayerAssistantWindow() && lPlayer.seesPlayerAssistantWindow()) {
                    if (player.isPlayerAssistant()) {
                        lPlayer.getCommunicator().sendRemovePa(" CA " + player.getName());
                    }
                    else if (this.shouldReceivePlayerList(lPlayer)) {
                        lPlayer.getCommunicator().sendRemovePa(player.getName());
                    }
                }
            }
        }
    }
    
    public void setShouldSendWeather(final boolean shouldSend) {
        this.shouldSendWeather = shouldSend;
    }
    
    private boolean shouldSendWeather() {
        return this.shouldSendWeather;
    }
    
    public void checkSendWeather() {
        if (this.shouldSendWeather()) {
            this.sendWeather();
            this.setShouldSendWeather(false);
        }
    }
    
    public void sendWeather() {
        final Player[] players;
        final Player[] playerArr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer != null && lPlayer.getCommunicator() != null) {
                lPlayer.getCommunicator().sendWeather();
            }
        }
    }
    
    public Player logout(final SocketConnection serverConnection) {
        Player player = null;
        String ip = "";
        if (serverConnection != null) {
            try {
                ip = serverConnection.getIp();
            }
            catch (Exception ex2) {}
            try {
                serverConnection.disconnect();
            }
            catch (NullPointerException ex3) {}
        }
        try {
            player = this.getPlayer(serverConnection);
            this.logoutPlayer(player);
        }
        catch (NoSuchPlayerException ex) {
            try {
                player = this.getPlayer(serverConnection);
                if (player != null) {
                    if (ip.equals("")) {
                        ip = player.getSaveFile().getIpaddress();
                    }
                    this.removeFromGroups(player);
                    Players.players.remove(player.getName());
                    Players.playersById.remove(player.getWurmId());
                    Players.logger.log(Level.INFO, "Logout - " + ex.getMessage() + " please verify that player " + player.getName() + " is logged out.", ex);
                }
                else {
                    Players.logger.log(Level.INFO, "Logout - " + ex.getMessage(), ex);
                }
            }
            catch (NoSuchPlayerException ex4) {}
        }
        if (Servers.localServer.PVPSERVER && !Servers.isThisATestServer() && !ip.isEmpty()) {
            final KingdomIp kip = KingdomIp.getKIP(ip, (byte)0);
            if (kip != null) {
                kip.logoff();
            }
        }
        return player;
    }
    
    public void sendPAWindow(final Player player) {
        final String chan = getKingdomHelpChannelName(player.getKingdomId());
        if (chan.length() == 0) {
            return;
        }
        final Message mess = new Message(player, (byte)12, chan, "<System> This is the Community Assistance window. Just type your questions here. To stop receiving these messages, manage your profile.");
        player.getCommunicator().sendMessage(mess);
        this.joinPAChannel(player);
    }
    
    public static String getKingdomHelpChannelName(final byte kingdomId) {
        String chan = "";
        if (kingdomId == 4) {
            chan = "CA HELP";
        }
        else if (kingdomId == 1) {
            chan = "JK HELP";
        }
        else if (kingdomId == 2) {
            chan = "MR HELP";
        }
        else if (kingdomId == 3) {
            chan = "HOTS HELP";
        }
        return chan;
    }
    
    public void sendGVHelpWindow(final Player player) {
        final Message mess = new Message(player, (byte)12, "GV HELP", "<System> This is the GV Help window. just reply to questions here. To stop receiving these messages, manage your profile.");
        player.getCommunicator().sendMessage(mess);
    }
    
    void sendGmsToPlayer(final Player player) {
        final Message mess = new Message(player, (byte)9, "MGMT", "");
        player.getCommunicator().sendMessage(mess);
        if (player.mayHearMgmtTalk() || player.mayHearDevTalk()) {
            this.sendToTabs(player, player.getPower() < 2, player.getPower() >= 2);
        }
        if (player.mayHearMgmtTalk()) {
            for (final TabData tabData : Players.tabListMGMT.values()) {
                if (tabData.isVisible() || tabData.getPower() < 2) {
                    player.getCommunicator().sendAddMgmt(tabData.getName(), tabData.getWurmId());
                }
            }
        }
        if (player.mayMute()) {
            final Message mess2 = new Message(player, (byte)11, "GM", "");
            player.getCommunicator().sendMessage(mess2);
            if (player.mayHearDevTalk()) {
                for (final TabData tabData2 : Players.tabListGM.values()) {
                    if (tabData2.isVisible() || tabData2.getPower() <= player.getPower()) {
                        player.getCommunicator().sendAddGm(tabData2.getName(), tabData2.getWurmId());
                    }
                }
            }
        }
    }
    
    void sendTicketsToPlayer(final Player player) {
        final Ticket[] tickets2;
        final Ticket[] tickets = tickets2 = Tickets.getTickets(player);
        for (final Ticket t : tickets2) {
            player.getCommunicator().sendTicket(t);
        }
    }
    
    public final void removeGlobalEffect(final long id) {
        for (final Player player : this.getPlayers()) {
            player.getCommunicator().sendRemoveEffect(id);
        }
    }
    
    void sendAltarsToPlayer(final Player player) {
        for (final EndGameItem eg : EndGameItems.altars.values()) {
            if (eg.isHoly()) {
                player.getCommunicator().sendAddEffect(eg.getWurmid(), (short)2, eg.getItem().getPosX(), eg.getItem().getPosY(), eg.getItem().getPosZ(), (byte)0);
                if (!WurmCalendar.isChristmas()) {
                    continue;
                }
                if (Zones.santaMolRehan != null) {
                    player.getCommunicator().sendAddEffect(Zones.santaMolRehan.getWurmId(), (short)4, Zones.santaMolRehan.getPosX(), Zones.santaMolRehan.getPosY(), Zones.santaMolRehan.getPositionZ(), (byte)0);
                }
                if (Zones.santa != null) {
                    player.getCommunicator().sendAddEffect(Zones.santa.getWurmId(), (short)4, Zones.santa.getPosX(), Zones.santa.getPosY(), Zones.santa.getPositionZ(), (byte)0);
                }
                if (Zones.santas == null || Zones.santas.isEmpty()) {
                    continue;
                }
                for (final Creature santa : Zones.santas.values()) {
                    player.getCommunicator().sendAddEffect(santa.getWurmId(), (short)4, santa.getPosX(), santa.getPosY(), santa.getPositionZ(), (byte)0);
                }
            }
            else {
                player.getCommunicator().sendAddEffect(eg.getWurmid(), (short)3, eg.getItem().getPosX(), eg.getItem().getPosY(), eg.getItem().getPosZ(), (byte)0);
                if (!WurmCalendar.isChristmas() || Zones.evilsanta == null) {
                    continue;
                }
                player.getCommunicator().sendAddEffect(Zones.evilsanta.getWurmId(), (short)4, Zones.evilsanta.getPosX(), Zones.evilsanta.getPosY(), Zones.evilsanta.getPositionZ(), (byte)0);
            }
        }
        if ((EndGameItems.altars == null || EndGameItems.altars.isEmpty()) && WurmCalendar.isChristmas()) {
            if (Zones.santa != null) {
                player.getCommunicator().sendAddEffect(Zones.santa.getWurmId(), (short)4, Zones.santa.getPosX(), Zones.santa.getPosY(), Zones.santa.getPositionZ(), (byte)0);
            }
            if (Zones.santaMolRehan != null) {
                player.getCommunicator().sendAddEffect(Zones.santaMolRehan.getWurmId(), (short)4, Zones.santaMolRehan.getPosX(), Zones.santaMolRehan.getPosY(), Zones.santaMolRehan.getPositionZ(), (byte)0);
            }
            if (Zones.evilsanta != null) {
                player.getCommunicator().sendAddEffect(Zones.evilsanta.getWurmId(), (short)4, Zones.evilsanta.getPosX(), Zones.evilsanta.getPosY(), Zones.evilsanta.getPositionZ(), (byte)0);
            }
            if (Zones.santas != null && !Zones.santas.isEmpty()) {
                for (final Creature santa2 : Zones.santas.values()) {
                    player.getCommunicator().sendAddEffect(santa2.getWurmId(), (short)4, santa2.getPosX(), santa2.getPosY(), santa2.getPositionZ(), (byte)0);
                }
            }
        }
        if (Servers.localServer.isChallengeServer() && Players.challengeStep > 0) {
            player.getCommunicator().sendAddEffect(Long.MAX_VALUE - Server.rand.nextInt(100000), (short)20, 0.0f, 0.0f, 0.0f, (byte)0);
            if (Players.challengeStep > 1) {
                player.getCommunicator().sendAddEffect(Long.MAX_VALUE - Server.rand.nextInt(100000), (short)21, 0.0f, 0.0f, 0.0f, (byte)0);
            }
            if (Players.challengeStep > 2) {
                player.getCommunicator().sendAddEffect(Long.MAX_VALUE - Server.rand.nextInt(100000), (short)22, 0.0f, 0.0f, 0.0f, (byte)0);
            }
            if (Players.challengeStep > 3) {
                player.getCommunicator().sendAddEffect(Long.MAX_VALUE - Server.rand.nextInt(100000), (short)23, 0.0f, 0.0f, 0.0f, (byte)0);
            }
        }
        final Effect[] allEffects;
        final Effect[] effs = allEffects = EffectFactory.getInstance().getAllEffects();
        for (final Effect effect : allEffects) {
            if (effect.isGlobal()) {
                if (Players.logger.isLoggable(Level.FINER)) {
                    Players.logger.finer(player.getName() + " Sending effect type " + effect.getType() + " at position (x,y,z) " + effect.getPosX() + ',' + effect.getPosY() + ',' + effect.getPosZ());
                }
                player.getCommunicator().sendAddEffect(effect.getOwner(), effect.getType(), effect.getPosX(), effect.getPosY(), effect.getPosZ(), (byte)0);
            }
        }
    }
    
    public void logoutPlayer(final Player player) {
        if (player.hasLink()) {
            try {
                player.getCommunicator().sendShutDown("You were logged out by the server.", false);
            }
            catch (Exception e) {
                if (Players.logger.isLoggable(Level.FINEST)) {
                    Players.logger.log(Level.FINEST, "Could not send shutdown to " + player + " due to " + e.getMessage(), e);
                }
            }
            try {
                player.getCommunicator().disconnect();
            }
            catch (Exception e) {
                if (Players.logger.isLoggable(Level.FINEST)) {
                    Players.logger.log(Level.FINEST, "Could not send disconnect to " + player + " due to " + e.getMessage(), e);
                }
            }
        }
        else {
            player.getCommunicator().disconnect();
        }
        this.sendConnectInfo(player, " left the world.", System.currentTimeMillis(), PlayerOnlineStatus.OFFLINE);
        this.removeFromGroups(player);
        this.setCreatureDead(player);
        Creatures.getInstance().setCreatureDead(player);
        player.logout();
        Players.players.remove(player.getName());
        Players.playersById.remove(player.getWurmId());
        Server.getInstance().steamHandler.EndAuthSession(player.getSteamId().toString());
        if (Servers.localServer.PVPSERVER && !Servers.isThisATestServer() && player.getPower() < 1) {
            final KingdomIp kip = KingdomIp.getKIP(player.getSaveFile().getIpaddress(), (byte)0);
            if (kip != null) {
                kip.logoff();
            }
        }
    }
    
    final void removePlayer(final Player player) {
        Players.players.remove(player.getName());
        Players.playersById.remove(player.getWurmId());
    }
    
    final void addPlayer(final Player player) {
        Players.players.put(player.getName(), player);
        Players.playersById.put(player.getWurmId(), player);
    }
    
    public Player[] getPlayers() {
        return Players.players.values().toArray(new Player[Players.players.size()]);
    }
    
    public Map<String, Player> getPlayerMap() {
        return Players.players;
    }
    
    String[] getPlayerNames() {
        final String[] lReturn = Players.players.keySet().toArray(new String[Players.players.size()]);
        return lReturn;
    }
    
    public void sendEffect(final short effType, final float posx, final float posy, final float posz, final boolean surfaced, final float maxDistMeters) {
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            try {
                if (lPlayer.getVisionArea() != null && lPlayer.isOnSurface() == surfaced && lPlayer.isWithinDistanceTo(posx, posy, posz, maxDistMeters)) {
                    lPlayer.getCommunicator().sendAddEffect(WurmId.getNextTempItemId(), effType, posx, posy, posz, (byte)(surfaced ? 0 : -1));
                }
            }
            catch (NullPointerException npe) {
                Players.logger.log(Level.WARNING, "Null visionArea or communicator for player " + lPlayer.getName() + ", disconnecting.");
                lPlayer.setLink(false);
            }
        }
    }
    
    public void sendChangedTile(@Nonnull final TilePos tilePos, final boolean surfaced, final boolean destroyTrap) {
        this.sendChangedTile(tilePos.x, tilePos.y, surfaced, destroyTrap);
    }
    
    public void sendChangedTile(final int tilex, final int tiley, final boolean surfaced, final boolean destroyTrap) {
        final Player[] playarr = this.getPlayers();
        if (destroyTrap) {
            final Trap t = Trap.getTrap(tilex, tiley, surfaced ? 0 : -1);
            if (t != null) {
                final byte tiletype = Tiles.decodeType(Zones.getMesh(surfaced).getTile(tilex, tiley));
                if (!t.mayTrapRemainOnTile(tiletype)) {
                    try {
                        t.delete();
                    }
                    catch (IOException ex) {}
                }
            }
        }
        final boolean nearRoad = this.isNearRoad(surfaced, tilex, tiley);
        if (surfaced) {
            for (final Player lPlayer : playarr) {
                try {
                    if (lPlayer.getVisionArea() != null && lPlayer.getVisionArea().contains(tilex, tiley) && lPlayer.getCommunicator() != null) {
                        try {
                            lPlayer.getMovementScheme().touchFreeMoveCounter();
                            if (nearRoad) {
                                lPlayer.getCommunicator().sendTileStrip((short)(tilex - 1), (short)(tiley - 1), 3, 3);
                            }
                            else {
                                lPlayer.getCommunicator().sendTileStrip((short)tilex, (short)tiley, 1, 1);
                            }
                        }
                        catch (IOException ex2) {}
                    }
                }
                catch (NullPointerException npe) {
                    if (lPlayer == null) {
                        Players.logger.log(Level.INFO, "Null player detected. Ignoring for now.");
                    }
                    else {
                        Players.logger.log(Level.WARNING, "Null visionArea or communicator for player " + lPlayer.getName() + ", disconnecting.");
                        lPlayer.setLink(false);
                    }
                }
            }
        }
        else {
            for (final Player lPlayer : playarr) {
                try {
                    if (lPlayer.getVisionArea() != null && lPlayer.getVisionArea().containsCave(tilex, tiley)) {
                        lPlayer.getMovementScheme().touchFreeMoveCounter();
                        lPlayer.getCommunicator().sendCaveStrip((short)(tilex - 1), (short)(tiley - 1), 3, 3);
                    }
                }
                catch (NullPointerException npe) {
                    Players.logger.log(Level.WARNING, "Null visionArea or communicator for player " + lPlayer.getName() + ", disconnecting.");
                    lPlayer.setLink(false);
                }
            }
        }
    }
    
    public void sendChangedTiles(final int startX, final int startY, final int sizeX, final int sizeY, final boolean surfaced, final boolean destroyTrap) {
        if (destroyTrap) {
            for (int x = 0; x < sizeX; ++x) {
                for (int y = 0; y < sizeY; ++y) {
                    final int tempTileX = startX + x;
                    final int tempTileY = startY + y;
                    if (GeneralUtilities.isValidTileLocation(tempTileX, tempTileY)) {
                        final Trap t = Trap.getTrap(tempTileX, tempTileY, surfaced ? 0 : -1);
                        if (t != null) {
                            final byte tiletype = Tiles.decodeType(Zones.getMesh(surfaced).getTile(tempTileX, tempTileY));
                            if (!t.mayTrapRemainOnTile(tiletype)) {
                                try {
                                    t.delete();
                                }
                                catch (IOException ex) {}
                            }
                        }
                    }
                }
            }
        }
        final boolean nearRoad = sizeX == 1 && sizeY == 1 && this.isNearRoad(surfaced, startX, startY);
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            try {
                Label_0461: {
                    if (surfaced) {
                        if (nearRoad) {
                            if (lPlayer.getVisionArea() != null) {
                                if (!lPlayer.getVisionArea().contains(startX, startY) && !lPlayer.getVisionArea().contains(startX, startY + sizeY) && !lPlayer.getVisionArea().contains(startX + sizeX, startY + sizeY)) {
                                    if (!lPlayer.getVisionArea().contains(startX + sizeX, startY)) {
                                        break Label_0461;
                                    }
                                }
                                try {
                                    lPlayer.getCommunicator().sendTileStrip((short)(startX - 1), (short)(startY - 1), 3, 3);
                                }
                                catch (IOException ex2) {}
                            }
                        }
                        else if (lPlayer.getVisionArea() != null) {
                            if (!lPlayer.getVisionArea().contains(startX, startY) && !lPlayer.getVisionArea().contains(startX, startY + sizeY) && !lPlayer.getVisionArea().contains(startX + sizeX, startY + sizeY)) {
                                if (!lPlayer.getVisionArea().contains(startX + sizeX, startY)) {
                                    break Label_0461;
                                }
                            }
                            try {
                                lPlayer.getCommunicator().sendTileStrip((short)startX, (short)startY, sizeX, sizeY);
                            }
                            catch (IOException ex3) {}
                        }
                    }
                    else if (lPlayer.isNearCave()) {
                        for (int xx = startX; xx < startX + sizeX; ++xx) {
                            for (int yy = startY; yy < startY + sizeY; ++yy) {
                                if (lPlayer.getVisionArea() != null && lPlayer.getVisionArea().containsCave(xx, yy)) {
                                    lPlayer.getCommunicator().sendCaveStrip((short)xx, (short)yy, 1, 1);
                                }
                            }
                        }
                    }
                }
            }
            catch (NullPointerException npe) {
                Players.logger.log(Level.WARNING, "Null visionArea or communicator for player " + lPlayer.getName() + ", disconnecting.");
                lPlayer.setLink(false);
            }
        }
    }
    
    private boolean isNearRoad(final boolean surfaced, final int tilex, final int tiley) {
        try {
            if (surfaced) {
                for (int x = -1; x <= 1; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        if (GeneralUtilities.isValidTileLocation(tilex + x, tiley + y) && Tiles.isRoadType(Server.surfaceMesh.getTile(tilex + x, tiley + y))) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            Players.logger.log(Level.WARNING, "****** Oops invalid x,y " + tilex + "," + tiley + ".");
        }
        return false;
    }
    
    void savePlayersAtShutdown() {
        Players.logger.info("Saving Players");
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.getDraggedItem() != null) {
                Items.stopDragging(lPlayer.getDraggedItem());
            }
            try {
                lPlayer.sleep();
            }
            catch (Exception ex) {
                Players.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        Players.logger.info("Finished saving Players");
    }
    
    public String getNameFor(final long playerId) throws NoSuchPlayerException, IOException {
        final Long pid = playerId;
        final Player p = this.getPlayerById(pid);
        if (p != null) {
            return p.getName();
        }
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(playerId);
        if (info != null) {
            return info.getName();
        }
        final PlayerState pState = PlayerInfoFactory.getPlayerState(playerId);
        if (pState != null) {
            return pState.getPlayerName();
        }
        return DbSearcher.getNameForPlayer(playerId);
    }
    
    public long getWurmIdFor(final String name) throws NoSuchPlayerException, IOException {
        final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(name);
        if (info.loaded) {
            return info.wurmId;
        }
        return DbSearcher.getWurmIdForPlayer(name);
    }
    
    private void loadBannedIps() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement(IPBan.getSelectSql());
            rs = ps.executeQuery();
            while (rs.next()) {
                final String ip = rs.getString("IPADDRESS");
                final String reason = rs.getString("BANREASON");
                final long expiry = rs.getLong("BANEXPIRY");
                final Ban bip = new IPBan(ip, reason, expiry);
                if (!bip.isExpired()) {
                    Players.bans.add(bip);
                }
                else {
                    this.removeBan(bip);
                }
            }
            Players.logger.info("Loaded " + Players.bans.size() + " banned IPs");
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to load banned ips.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void loadBannedSteamIds() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement(SteamIdBan.getSelectSql());
            rs = ps.executeQuery();
            while (rs.next()) {
                final String identifier = rs.getString("STEAM_ID");
                final String reason = rs.getString("BANREASON");
                final long expiry = rs.getLong("BANEXPIRY");
                final Ban bip = new SteamIdBan(SteamId.fromSteamID64(Long.valueOf(identifier)), reason, expiry);
                if (!bip.isExpired()) {
                    Players.bans.add(bip);
                }
                else {
                    this.removeBan(bip);
                }
            }
            Players.logger.info("Loaded " + Players.bans.size() + " more bans from steamids");
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to load banned steamids.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public int getNumberOfPlayers() {
        return Players.players.size();
    }
    
    public void addBannedIp(final String ip, final String reason, final long expiry) {
        final Ban ban = new IPBan(ip, reason, expiry);
        this.addBan(ban);
    }
    
    public void addBan(final Ban ban) {
        if (ban == null || ban.getIdentifier() == null || ban.getIdentifier().isEmpty()) {
            Players.logger.warning("Cannot add a null ban");
            return;
        }
        final Ban bip = this.getBannedIp(ban.getIdentifier());
        if (bip == null) {
            Players.bans.add(ban);
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement(ban.getInsertSql());
                ps.setString(1, ban.getIdentifier());
                ps.setString(2, ban.getReason());
                ps.setLong(3, ban.getExpiry());
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                Players.logger.log(Level.WARNING, "Failed to add ban " + ban.getIdentifier(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        else {
            bip.setReason(ban.getReason());
            bip.setExpiry(ban.getExpiry());
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement(bip.getUpdateSql());
                ps.setString(1, bip.getReason());
                ps.setLong(2, bip.getExpiry());
                ps.setString(3, bip.getIdentifier());
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                Players.logger.log(Level.WARNING, "Failed to update ban " + bip.getIdentifier(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public boolean removeBan(final String identifier) {
        Ban existing = null;
        for (final Ban lBip : Players.bans) {
            if (lBip.getIdentifier().equals(identifier)) {
                existing = lBip;
            }
            else {
                if (!identifier.contains("*") || !lBip.getIdentifier().startsWith(identifier)) {
                    continue;
                }
                existing = lBip;
            }
        }
        if (existing == null) {
            existing = Ban.fromString(identifier);
        }
        return this.removeBan(existing);
    }
    
    public boolean removeBan(final Ban ban) {
        Players.bans.remove(ban);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement(ban.getDeleteSql());
            ps.setString(1, ban.getIdentifier());
            ps.executeUpdate();
            return true;
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to remove ban " + ban.getIdentifier(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        return false;
    }
    
    public final void tickSecond() {
        for (final Player p : Players.players.values()) {
            if (p.getSaveFile() != null && p.getSaveFile().sleep > 0 && !p.getSaveFile().frozenSleep) {
                final float chance = p.getStatus().getFats() / 3.0f;
                if (Server.rand.nextFloat() < chance) {
                    continue;
                }
                final PlayerInfo saveFile = p.getSaveFile();
                --saveFile.sleep;
            }
        }
    }
    
    public Ban[] getPlayersBanned() {
        final Set<Ban> banned = new HashSet<Ban>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME,BANREASON,BANEXPIRY FROM PLAYERS WHERE BANNED=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String ip = rs.getString("NAME");
                final String reason = rs.getString("BANREASON");
                final long expiry = rs.getLong("BANEXPIRY");
                if (expiry > System.currentTimeMillis()) {
                    banned.add(new IPBan(ip, reason, expiry));
                }
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to get players banned.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return banned.toArray(new Ban[banned.size()]);
    }
    
    public void sendGmMessage(final Creature sender, final String playerName, final String message, final boolean emote, final int red, final int green, final int blue) {
        Message mess = null;
        if (emote) {
            mess = new Message(sender, (byte)6, "GM", message);
        }
        else {
            mess = new Message(sender, (byte)11, "GM", "<" + playerName + "> " + message, red, green, blue);
        }
        addGmMessage(playerName, message);
        final Player[] players;
        final Player[] playerArr = players = getInstance().getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.mayHearDevTalk()) {
                if (sender == null) {
                    mess.setSender(lPlayer);
                }
                lPlayer.getCommunicator().sendMessage(mess);
            }
        }
    }
    
    public void sendGmMessage(final Creature sender, final String playerName, final String message, final boolean emote) {
        this.sendGmMessage(sender, playerName, message, emote, -1, -1, -1);
    }
    
    public void sendGlobalKingdomMessage(final Creature sender, final long senderId, final String playerName, final String message, final boolean emote, final byte kingdom, final int r, final int g, final int b) {
        Message mess = null;
        mess = new Message(sender, (byte)16, "GL-" + Kingdoms.getChatNameFor(kingdom), "<" + playerName + "> " + message);
        mess.setSenderKingdom(kingdom);
        mess.setSenderId(senderId);
        mess.setColorR(r);
        mess.setColorG(g);
        mess.setColorB(b);
        Server.getInstance().addMessage(mess);
    }
    
    public void sendGlobalTradeMessage(final Creature sender, final long senderId, final String playerName, final String message, final byte kingdom, final int r, final int g, final int b) {
        Message mess = null;
        mess = new Message(sender, (byte)18, "Trade", "<" + playerName + "> " + message);
        mess.setSenderKingdom(kingdom);
        mess.setSenderId(senderId);
        mess.setColorR(r);
        mess.setColorG(g);
        mess.setColorB(b);
        Server.getInstance().addMessage(mess);
    }
    
    public void partPAChannel(final Player player) {
        if (!player.seesPlayerAssistantWindow()) {
            final Player[] players;
            final Player[] playerArr = players = getInstance().getPlayers();
            for (final Player lPlayer : players) {
                if (lPlayer.getSaveFile() != null && lPlayer.seesPlayerAssistantWindow()) {
                    lPlayer.getCommunicator().sendRemovePa(player.getName());
                }
            }
        }
    }
    
    public void joinPAChannel(final Player player) {
        final Player[] players;
        final Player[] playerArr = players = getInstance().getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.getSaveFile() != null && lPlayer.seesPlayerAssistantWindow() && player.isVisibleTo(lPlayer)) {
                if (player.isPlayerAssistant()) {
                    lPlayer.getCommunicator().sendAddPa(" CA " + player.getName(), player.getWurmId());
                }
                else if (this.shouldReceivePlayerList(lPlayer) && player.getPower() < 2) {
                    lPlayer.getCommunicator().sendAddPa(player.getName(), player.getWurmId());
                }
            }
        }
    }
    
    public void partChannels(final Player player) {
        final boolean mayDev = player.mayHearDevTalk();
        final boolean mayMgmt = player.mayHearMgmtTalk();
        final boolean mayHelp = player.seesPlayerAssistantWindow();
        if (!mayDev && !mayMgmt && !mayHelp) {
            return;
        }
        final String playerName = player.getName();
        if (mayDev || mayMgmt) {
            this.removeFromTabs(player.getWurmId(), playerName);
            this.sendRemoveFromTabs(player.getWurmId(), playerName);
        }
        for (final Player otherPlayer : getInstance().getPlayers()) {
            if (!player.isVisibleTo(otherPlayer)) {
                if (mayHelp && otherPlayer.seesPlayerAssistantWindow()) {
                    if (player.isPlayerAssistant()) {
                        otherPlayer.getCommunicator().sendRemovePa(" CA " + playerName);
                    }
                    else if (this.shouldReceivePlayerList(otherPlayer)) {
                        otherPlayer.getCommunicator().sendRemovePa(playerName);
                    }
                }
            }
        }
    }
    
    public void joinChannels(final Player player) {
        final boolean mayDev = player.mayHearDevTalk();
        final boolean mayMgmt = player.mayHearMgmtTalk();
        final boolean mayHelp = player.seesPlayerAssistantWindow();
        if (!mayDev && !mayMgmt && !mayHelp) {
            return;
        }
        final long playerId = player.getWurmId();
        final String playerName = player.getName();
        if (mayDev || mayMgmt) {
            this.sendToTabs(player, player.getPower() < 2, player.getPower() >= 2);
        }
        for (final Player otherPlayer : getInstance().getPlayers()) {
            if (player.isVisibleTo(otherPlayer)) {
                if (player != otherPlayer) {
                    if (mayHelp && otherPlayer.seesPlayerAssistantWindow()) {
                        if (player.isPlayerAssistant()) {
                            otherPlayer.getCommunicator().sendAddPa(" CA " + playerName, playerId);
                        }
                        else if (this.shouldReceivePlayerList(otherPlayer) && player.getPower() < 2) {
                            otherPlayer.getCommunicator().sendAddPa(playerName, playerId);
                        }
                    }
                }
            }
        }
    }
    
    public void sendPaMessage(final Message mes) {
        Players.caHelpLogger.info(mes.getMessage());
        final Player[] players;
        final Player[] playerArr = players = getInstance().getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.seesPlayerAssistantWindow()) {
                lPlayer.getCommunicator().sendMessage(mes);
            }
        }
    }
    
    public void sendGVMessage(final Message mes) {
        Players.caHelpLogger.info(mes.getMessage());
        final Player[] players;
        final Player[] playerArr = players = getInstance().getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.seesGVHelpWindow()) {
                lPlayer.getCommunicator().sendMessage(mes);
            }
        }
    }
    
    public void sendCaMessage(final byte kingdom, final Message mes) {
        Players.caHelpLogger.info(mes.getMessage());
        final Player[] players;
        final Player[] playerArr = players = getInstance().getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.seesPlayerAssistantWindow() && (lPlayer.getKingdomId() == kingdom || lPlayer.getPower() >= 2)) {
                lPlayer.getCommunicator().sendMessage(mes);
            }
        }
    }
    
    public String[] getMuters() {
        final Set<String> muted = new HashSet<String>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME FROM PLAYERS WHERE MAYMUTE=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String name = rs.getString("NAME");
                muted.add(name);
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to get muters.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return muted.toArray(new String[muted.size()]);
    }
    
    public String[] getDevTalkers() {
        final Set<String> devTalkers = new HashSet<String>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME FROM PLAYERS WHERE DEVTALK=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String name = rs.getString("NAME");
                devTalkers.add(name);
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to get dev talkers.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return devTalkers.toArray(new String[devTalkers.size()]);
    }
    
    public String[] getCAs() {
        final Set<String> pas = new HashSet<String>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME FROM PLAYERS WHERE PA=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String name = rs.getString("NAME");
                pas.add(name);
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to get pas.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return pas.toArray(new String[pas.size()]);
    }
    
    public String[] getHeros(final byte checkPower) {
        final Set<String> heros = new HashSet<String>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME FROM PLAYERS WHERE POWER=? AND CURRENTSERVER=?");
            ps.setByte(1, checkPower);
            ps.setInt(2, Servers.localServer.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                final String name = rs.getString("NAME");
                heros.add(name);
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to get heros.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return heros.toArray(new String[heros.size()]);
    }
    
    public Ban[] getPlayersMuted() {
        final Set<Ban> muted = new HashSet<Ban>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME,MUTEREASON,MUTEEXPIRY FROM PLAYERS WHERE MUTED=1");
            rs = ps.executeQuery();
            while (rs.next()) {
                final String ip = rs.getString("NAME");
                final String reason = rs.getString("MUTEREASON");
                final long expiry = rs.getLong("MUTEEXPIRY");
                muted.add(new IPBan(ip, reason, expiry));
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to get players muted.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return muted.toArray(new Ban[muted.size()]);
    }
    
    public Ban getAnyBan(final String ip, final Player player, final String steamId) {
        Ban ban = player.getBan();
        if (ban == null) {
            ban = this.getBannedIp(ip);
        }
        if (ban == null) {
            ban = this.getBannedSteamId(steamId);
        }
        return ban;
    }
    
    public Ban getBannedSteamId(final String steamId) {
        if (steamId.isEmpty()) {
            return null;
        }
        final Ban[] array;
        final Ban[] banArr = array = Players.bans.toArray(new Ban[0]);
        for (final Ban ban : array) {
            if (ban != null) {
                if (ban.getIdentifier().equals(steamId)) {
                    if (!ban.isExpired()) {
                        return ban;
                    }
                    this.removeBan(ban);
                }
            }
        }
        return null;
    }
    
    public Ban getBannedIp(final String ip) {
        if (ip.isEmpty()) {
            return null;
        }
        final Ban[] bips = Players.bans.toArray(new Ban[0]);
        int dots = 0;
        for (final Ban lBip : bips) {
            if (lBip == null) {
                Players.logger.warning("BannedIPs includes a null");
                return null;
            }
            dots = lBip.getIdentifier().indexOf("*");
            if (dots > 0) {
                if (lBip.isExpired()) {
                    this.removeBan(lBip.getIdentifier());
                }
                else if (lBip.getIdentifier().substring(0, dots).equals(ip.substring(0, dots))) {
                    return lBip;
                }
            }
            if (lBip.getIdentifier().equals(ip)) {
                if (!lBip.isExpired()) {
                    return lBip;
                }
                this.removeBan(lBip.getIdentifier());
            }
        }
        return null;
    }
    
    public Ban[] getBans() {
        final Ban[] bips = Players.bans.toArray(new Ban[Players.bans.size()]);
        Arrays.sort(bips, new Comparator<Ban>() {
            @Override
            public int compare(final Ban o1, final Ban o2) {
                return o1.getIdentifier().compareTo(o2.getIdentifier());
            }
        });
        return bips;
    }
    
    public void convertFromKingdomToKingdom(final byte oldKingdom, final byte newKingdom) {
        final Player[] players;
        final Player[] playerArr = players = this.getPlayers();
        for (final Player play : players) {
            if (play.getKingdomId() == oldKingdom) {
                try {
                    play.setKingdomId(newKingdom, true);
                }
                catch (IOException iox) {
                    Players.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
            }
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET KINGDOM=? WHERE KINGDOM=?");
            ps.setByte(1, newKingdom);
            ps.setByte(2, oldKingdom);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to change kingdom to " + newKingdom, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void convertPlayerToKingdom(final long wurmId, final byte newKingdom) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET KINGDOM=? WHERE WURMID=?");
            ps.setByte(1, newKingdom);
            ps.setLong(2, wurmId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to change kingdom to " + newKingdom + " for " + wurmId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getLastLogoutForPlayer(final long wurmid) {
        long toReturn = 0L;
        if (this.getPlayerById(wurmid) != null) {
            toReturn = System.currentTimeMillis();
        }
        else {
            final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmid);
            if (pinf != null) {
                return pinf.lastLogout;
            }
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT LASTLOGOUT FROM PLAYERS WHERE WURMID=?");
            ps.setLong(1, wurmid);
            rs = ps.executeQuery();
            if (rs.next()) {
                toReturn = rs.getLong("LASTLOGOUT");
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve lastlogout for " + wurmid, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    public boolean doesPlayerNameExist(final String name) {
        if (this.getPlayerByName(name) != null) {
            return true;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT WURMID FROM PLAYERS WHERE NAME=?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to check if " + name + " exists:" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return false;
    }
    
    public long getWurmIdByPlayerName(final String name) {
        final String lName = LoginHandler.raiseFirstLetter(name);
        if (this.getPlayerByName(lName) != null) {
            return this.getPlayerByName(lName).getWurmId();
        }
        final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(lName);
        if (pinf.wurmId > 0L) {
            return pinf.wurmId;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT WURMID FROM PLAYERS WHERE NAME=?");
            ps.setString(1, lName);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("WURMID");
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve wurmid for " + name + " exists:" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return -1L;
    }
    
    public void registerNewKingdom(final Creature registered) {
        this.registerNewKingdom(registered.getWurmId(), registered.getKingdomId());
    }
    
    public void pollChamps() {
        if (System.currentTimeMillis() - Servers.localServer.lastDecreasedChampionPoints > 604800000L) {
            Servers.localServer.setChampStamp();
            final PlayerInfo[] playinfos = PlayerInfoFactory.getPlayerInfos();
            for (int p = 0; p < playinfos.length; ++p) {
                if (playinfos[p].realdeath > 0 && playinfos[p].realdeath < 5) {
                    try {
                        final Player play = getInstance().getPlayer(playinfos[p].wurmId);
                        play.sendAddChampionPoints();
                    }
                    catch (NoSuchPlayerException ex) {}
                }
            }
        }
        printChampStats();
    }
    
    public void registerNewKingdom(final long aWurmId, final byte aKingdom) {
        this.pkingdoms.put(aWurmId, aKingdom);
    }
    
    public byte getKingdomForPlayer(final long wurmid) {
        final Byte b = this.pkingdoms.get(wurmid);
        if (b != null) {
            return b;
        }
        final Player lPlayerById = this.getPlayerById(wurmid);
        if (lPlayerById != null) {
            this.registerNewKingdom(wurmid, lPlayerById.getKingdomId());
            return lPlayerById.getKingdomId();
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT KINGDOM FROM PLAYERS WHERE WURMID=?");
            ps.setLong(1, wurmid);
            rs = ps.executeQuery();
            if (rs.next()) {
                final byte toret = rs.getByte("KINGDOM");
                this.pkingdoms.put(wurmid, toret);
                return toret;
            }
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve kingdom for " + wurmid, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return 0;
    }
    
    public int getPlayersFromKingdom(final byte kingdomId) {
        final int nums = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME,WURMID FROM PLAYERS WHERE KINGDOM=? AND CURRENTSERVER=? AND POWER=0");
            ps.setByte(1, kingdomId);
            ps.setInt(2, Servers.localServer.id);
            rs = ps.executeQuery();
            rs.last();
            return rs.getRow();
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve nums kingdom for " + kingdomId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return 0;
    }
    
    public static int getChampionsFromKingdom(final byte kingdomId) {
        int nums = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME,WURMID,REALDEATH,LASTLOSTCHAMPION FROM PLAYERS WHERE KINGDOM=? AND REALDEATH>0 AND REALDEATH<4 AND POWER=0");
            ps.setByte(1, kingdomId);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("WURMID");
                final String name = rs.getString("NAME");
                final long lastChamped = rs.getLong("LASTLOSTCHAMPION");
                final int realDeath = rs.getInt("REALDEATH");
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wid);
                if (pinf.getCurrentServer() == Servers.localServer.id && System.currentTimeMillis() - pinf.championTimeStamp < 14515200000L) {
                    ++nums;
                }
            }
            return nums;
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve nums kingdom for " + kingdomId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return nums;
    }
    
    public static int getChampionsFromKingdom(final byte kingdomId, final int deity) {
        int nums = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME,WURMID,REALDEATH,LASTLOSTCHAMPION FROM PLAYERS WHERE KINGDOM=? AND REALDEATH>0 AND REALDEATH<4 AND POWER=0");
            ps.setByte(1, kingdomId);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("WURMID");
                final String name = rs.getString("NAME");
                final long lastChamped = rs.getLong("LASTLOSTCHAMPION");
                final int realDeath = rs.getInt("REALDEATH");
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wid);
                if (pinf.getCurrentServer() == Servers.localServer.id && System.currentTimeMillis() - pinf.championTimeStamp < 14515200000L && pinf.getDeity() != null && pinf.getDeity().getNumber() == deity) {
                    ++nums;
                }
            }
            Players.logger.log(Level.INFO, "Found " + nums + " champs for kingdom =" + kingdomId);
            return nums;
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve nums kingdom for " + kingdomId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return nums;
    }
    
    public static int getPremiumPlayersFromKingdom(final byte kingdomId) {
        int nums = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT NAME,WURMID FROM PLAYERS WHERE KINGDOM=? AND PAYMENTEXPIRE>? AND POWER=0");
            ps.setByte(1, kingdomId);
            ps.setLong(2, System.currentTimeMillis());
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("WURMID");
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wid);
                if (pinf.getCurrentServer() == Servers.localServer.id || System.currentTimeMillis() - pinf.getLastLogout() < 259200000L) {
                    ++nums;
                }
            }
            return nums;
        }
        catch (SQLException sqex) {
            Players.logger.log(Level.WARNING, "Failed to retrieve nums kingdom for " + kingdomId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return nums;
    }
    
    public void setStructureFinished(final long structureid) {
        final Player[] playarr = this.getPlayers();
        boolean found = false;
        for (final Player lPlayer : playarr) {
            try {
                if (lPlayer.getStructure().getWurmId() == structureid) {
                    try {
                        lPlayer.setStructure(null);
                        lPlayer.save();
                        found = true;
                        break;
                    }
                    catch (Exception ex) {
                        Players.logger.log(Level.WARNING, "Failed to set structure finished for " + lPlayer, ex);
                    }
                }
            }
            catch (NoSuchStructureException ex2) {}
        }
        if (!found) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("update PLAYERS set BUILDINGID=-10 WHERE BUILDINGID=?");
                ps.setLong(1, structureid);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                Players.logger.log(Level.WARNING, "Failed to set buidlingid to -10 for " + structureid, sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static void resetFaithGain() {
        PlayerInfoFactory.resetFaithGain();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET LASTFAITH=0,NUMFAITH=0");
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem resetting faith gain - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void payGms() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET MONEY=MONEY+250000 WHERE POWER>1");
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem processing GM Salary - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.getPower() > 0) {
                lPlayer.getCommunicator().sendSafeServerMessage("You have now received salary.");
            }
        }
    }
    
    public void resetPlayer(final long wurmid) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE SKILLS SET VALUE=20, MINVALUE=20 WHERE VALUE>20 AND OWNER=?");
            ps.setLong(1, wurmid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem resetting player skills - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET FAITH=20 WHERE FAITH>20 AND WURMID=?");
            ps.setLong(1, wurmid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem resetting player faith - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            final Player p = this.getPlayer(wurmid);
            try {
                if (p.isChampion()) {
                    p.revertChamp();
                    if (p.getFaith() > 20.0f) {
                        p.setFaith(20.0f);
                    }
                }
            }
            catch (IOException ex) {}
            final Skills sk = p.getSkills();
            final Skill[] skills = sk.getSkills();
            for (int x = 0; x < skills.length; ++x) {
                if (skills[x].getKnowledge() > 20.0) {
                    skills[x].minimum = 20.0;
                    skills[x].setKnowledge(20.0, true);
                }
            }
        }
        catch (NoSuchPlayerException nsp) {
            final PlayerInfo p2 = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmid);
            if (p2 != null) {
                try {
                    p2.setRealDeath((byte)0);
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    public static void sendGmMessages(final Player player) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT TIME,SENDER,MESSAGE FROM GMMESSAGES ORDER BY TIME");
            rs = ps.executeQuery();
            while (rs.next()) {
                player.getCommunicator().sendGmMessage(rs.getLong("TIME"), rs.getString("SENDER"), rs.getString("MESSAGE"));
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem getting GM messages - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        pruneMessages();
    }
    
    public static void sendMgmtMessages(final Player player) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT TIME,SENDER,MESSAGE FROM MGMTMESSAGES ORDER BY TIME");
            rs = ps.executeQuery();
            while (rs.next()) {
                player.getCommunicator().sendMgmtMessage(rs.getLong("TIME"), rs.getString("SENDER"), rs.getString("MESSAGE"));
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem getting management messages - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        pruneMessages();
    }
    
    public void sendStartKingdomChat(final Player player) {
        if (player.showKingdomStartMessage()) {
            final Message mess = new Message(player, (byte)10, Kingdoms.getChatNameFor(player.getKingdomId()), "<System> This is the Kingdom Chat for your current server. ", 250, 150, 250);
            player.getCommunicator().sendMessage(mess);
            final Message mess2 = new Message(player, (byte)10, Kingdoms.getChatNameFor(player.getKingdomId()), "<System> You can disable receiving these messages, by a setting in your profile.", 250, 150, 250);
            player.getCommunicator().sendMessage(mess2);
        }
    }
    
    public void sendStartGlobalKingdomChat(final Player player) {
        if (player.showGlobalKingdomStartMessage()) {
            final Message mess = new Message(player, (byte)16, "GL-" + Kingdoms.getChatNameFor(player.getKingdomId()), "<System> This is your Global Kingdom Chat. ", 250, 150, 250);
            player.getCommunicator().sendMessage(mess);
            final Message mess2 = new Message(player, (byte)16, "GL-" + Kingdoms.getChatNameFor(player.getKingdomId()), "<System> You can disable receiving these messages, by a setting in your profile.", 250, 150, 250);
            player.getCommunicator().sendMessage(mess2);
        }
    }
    
    public void sendStartGlobalTradeChannel(final Player player) {
        if (player.showTradeStartMessage()) {
            final Message mess = new Message(player, (byte)18, "Trade", "<System> This is the Trade channel. ", 250, 150, 250);
            player.getCommunicator().sendMessage(mess);
            final Message mess2 = new Message(player, (byte)18, "Trade", "<System> Only messages starting with WTB, WTS, WTT, PC or @ are allowed. ", 250, 150, 250);
            player.getCommunicator().sendMessage(mess2);
            final Message mess3 = new Message(player, (byte)18, "Trade", "<System> Please PM the person if you are interested in the Item.", 250, 150, 250);
            player.getCommunicator().sendMessage(mess3);
            final Message mess4 = new Message(player, (byte)18, "Trade", "<System> You can also use @<name> to send a reply in this channel to <name>.", 250, 150, 250);
            player.getCommunicator().sendMessage(mess4);
            final Message mess5 = new Message(player, (byte)18, "Trade", "<System> You can disable receiving these messages, by a setting in your profile.", 250, 150, 250);
            player.getCommunicator().sendMessage(mess5);
        }
    }
    
    public static Map<String, Integer> getBattleRanks(final int num) {
        final Map<String, Integer> toReturn = new HashMap<String, Integer>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("select RANK, NAME from PLAYERS ORDER BY RANK DESC LIMIT ?");
            ps.setInt(1, num);
            rs = ps.executeQuery();
            while (rs.next()) {
                toReturn.put(rs.getString("NAME"), rs.getInt("RANK"));
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem getting battle ranks - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    public static Map<String, Integer> getMaxBattleRanks(final int num) {
        final Map<String, Integer> toReturn = new HashMap<String, Integer>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("select MAXRANK,RANK, NAME from PLAYERS ORDER BY MAXRANK DESC LIMIT ?");
            ps.setInt(1, num);
            rs = ps.executeQuery();
            while (rs.next()) {
                toReturn.put(rs.getString("NAME"), rs.getInt("MAXRANK"));
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem getting Max battle ranks - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    public static void printMaxRanks() {
        Writer output = null;
        try {
            String dir = Constants.webPath;
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
            final File aFile = new File(dir + "maxranks.html");
            output = new BufferedWriter(new FileWriter(aFile));
            final String start = "<TABLE class=\"gameDataTable\"><TR><TH><Name</TH><TH>Rank</TH></TR>";
            try {
                output.write("<TABLE class=\"gameDataTable\"><TR><TH><Name</TH><TH>Rank</TH></TR>");
            }
            catch (IOException iox) {
                Players.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            int nums = 0;
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("select MAXRANK,RANK, NAME from PLAYERS ORDER BY MAXRANK DESC LIMIT ?");
                ps.setInt(1, 30);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (nums < 10) {
                        output.write("<TR class=\"gameDataTopTenTR\"><TD class=\"gameDataTopTenTDName\">" + rs.getString("NAME") + "</TD><TD class=\"gameDataTopTenTDValue\">" + rs.getInt("MAXRANK") + "</TD></TR>");
                    }
                    else {
                        output.write("<TR class=\"gameDataTR\"><TD class=\"gameDataTDName\">" + rs.getString("NAME") + "</TD><TD class=\"gameDataTDValue\">" + rs.getInt("MAXRANK") + "</TD></TR>");
                    }
                    ++nums;
                }
            }
            catch (SQLException sqx) {
                Players.logger.log(Level.WARNING, "Problem writing maxranks" + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
            output.write("</TABLE>");
        }
        catch (IOException iox2) {
            Players.logger.log(Level.WARNING, "Failed to save maxranks.html", iox2);
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (IOException ex) {}
        }
    }
    
    public static void printRanks() {
        printMaxRanks();
        Writer output = null;
        try {
            String dir = Constants.webPath;
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
            final File aFile = new File(dir + "ranks.html");
            output = new BufferedWriter(new FileWriter(aFile));
            output.write(Players.header2);
            final String start = "<TABLE id=\"gameDataTable\">\n\t\t<TR>\n\t\t\t<TH>Name</TH>\n\t\t\t<TH>Rank</TH>\n\t\t</TR>\n\t\t";
            try {
                output.write("<TABLE id=\"gameDataTable\">\n\t\t<TR>\n\t\t\t<TH>Name</TH>\n\t\t\t<TH>Rank</TH>\n\t\t</TR>\n\t\t");
            }
            catch (IOException iox) {
                Players.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            int nums = 0;
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("select RANK, NAME from PLAYERS ORDER BY RANK DESC LIMIT ?");
                ps.setInt(1, 30);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (nums < 10) {
                        output.write("<TR class=\"gameDataTopTenTR\">\n\t\t\t<TD class=\"gameDataTopTenTDName\">" + rs.getString("NAME") + "</TD>\n\t\t\t<TD class=\"gameDataTopTenTDValue\">" + rs.getInt("RANK") + "</TD>\n\t\t</TR>\n\t\t");
                    }
                    else {
                        output.write("<TR class=\"gameDataTR\">\n\t\t\t<TD class=\"gameDataTDName\">" + rs.getString("NAME") + "</TD>\n\t\t\t<TD class=\"gameDataTDValue\">" + rs.getInt("RANK") + "</TD>\n\t\t</TR>\n\n\t");
                    }
                    ++nums;
                }
            }
            catch (SQLException sqx) {
                Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
            output.write("</TABLE>\n");
            output.write("\n</BODY>\n</HTML>");
        }
        catch (IOException iox2) {
            Players.logger.log(Level.WARNING, "Failed to close ranks.html", iox2);
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (IOException ex) {}
        }
    }
    
    public static void printChampStats() {
        final WurmRecord[] alls = PlayerInfoFactory.getChampionRecords();
        if (alls.length > 0) {
            Writer output = null;
            try {
                String dir = Constants.webPath;
                if (!dir.endsWith(File.separator)) {
                    dir += File.separator;
                }
                final File aFile = new File(dir + "champs.html");
                output = new BufferedWriter(new FileWriter(aFile));
                try {
                    output.write(Players.headerStats2);
                    final String start = "<TABLE id=\"statsDataTable\">\n\t\t<TR>\n\t\t\t<TH></TH>\n\t\t\t<TH></TH>\n\t\t</TR>\n\t\t";
                    output.write("<TABLE id=\"statsDataTable\">\n\t\t<TR>\n\t\t\t<TH></TH>\n\t\t\t<TH></TH>\n\t\t</TR>\n\t\t");
                    int total = 0;
                    int totalLimit = 0;
                    for (final WurmRecord entry : alls) {
                        output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">" + entry.getHolder() + " players</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + entry.getValue() + " current=" + entry.isCurrent() + "</TD>\n\t\t</TR>\n\t\t");
                        total += entry.getValue();
                        ++totalLimit;
                    }
                    output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Average points</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + total + "/" + totalLimit + "=" + total / totalLimit + "</TD>\n\t\t</TR>\n\t\t");
                    output.write("</TABLE>\n");
                    output.write("\n</BODY>\n</HTML>");
                }
                catch (IOException iox) {
                    Players.logger.log(Level.WARNING, "Problem writing server stats = " + iox.getMessage(), iox);
                }
            }
            catch (IOException iox2) {
                Players.logger.log(Level.WARNING, "Failed to open stats.html", iox2);
            }
            finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static void printStats() {
        try {
            String dir = Constants.webPath;
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
            final File aFile = new File(dir + "stats.xml");
            StatsXMLWriter.createXML(aFile);
        }
        catch (Exception ex) {
            Players.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        Writer output = null;
        try {
            String dir2 = Constants.webPath;
            if (!dir2.endsWith(File.separator)) {
                dir2 += File.separator;
            }
            final File aFile2 = new File(dir2 + "stats.html");
            output = new BufferedWriter(new FileWriter(aFile2));
            try {
                output.write(Players.headerStats);
                final String start = "<TABLE id=\"statsDataTable\">\n\t\t<TR>\n\t\t\t<TH></TH>\n\t\t\t<TH></TH>\n\t\t</TR>\n\t\t";
                output.write("<TABLE id=\"statsDataTable\">\n\t\t<TR>\n\t\t\t<TH></TH>\n\t\t\t<TH></TH>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Server name</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + Servers.localServer.getName() + "</TD>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Last updated</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + DateFormat.getDateInstance(2).format(new Timestamp(System.currentTimeMillis())) + "</TD>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Status</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + (Servers.localServer.maintaining ? "Maintenance" : ((Server.getMillisToShutDown() > 0L) ? ("Shutting down in " + Server.getMillisToShutDown() / 1000L + " seconds") : "Up and running")) + "</TD>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Uptime</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + Server.getTimeFor(Server.getSecondsUptime() * 1000) + "</TD>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Wurm Time</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + WurmCalendar.getTime() + "</TD>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Weather</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + Server.getWeather().getWeatherString(false) + "</TD>\n\t\t</TR>\n\t\t");
                int total = 0;
                int totalLimit = 0;
                int epic = 0;
                int epicMax = 0;
                final ServerEntry[] allServers;
                final ServerEntry[] alls = allServers = Servers.getAllServers();
                for (final ServerEntry entry : allServers) {
                    if (!entry.EPIC) {
                        if (!entry.isLocal) {
                            output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">" + entry.getName() + " players</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + entry.currentPlayers + "/" + entry.pLimit + "</TD>\n\t\t</TR>\n\t\t");
                            total += entry.currentPlayers;
                            totalLimit += entry.pLimit;
                        }
                        else {
                            output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">" + entry.getName() + " players</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + getInstance().getNumberOfPlayers() + "/" + entry.pLimit + "</TD>\n\t\t</TR>\n\t\t");
                            total += getInstance().getNumberOfPlayers();
                            totalLimit += entry.pLimit;
                        }
                    }
                    else {
                        epic += entry.currentPlayers;
                        epicMax += entry.pLimit;
                        totalLimit += entry.pLimit;
                        total += entry.currentPlayers;
                    }
                }
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Epic cluster players</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + epic + "/" + epicMax + "</TD>\n\t\t</TR>\n\t\t");
                output.write("<TR class=\"statsTR\">\n\t\t\t<TD class=\"statsDataTDName\">Total players</TD>\n\t\t\t<TD class=\"statsDataTDValue\">" + total + "/" + totalLimit + "</TD>\n\t\t</TR>\n\t\t");
                output.write("</TABLE>\n");
                output.write("\n</BODY>\n</HTML>");
            }
            catch (IOException iox) {
                Players.logger.log(Level.WARNING, "Problem writing server stats = " + iox.getMessage(), iox);
            }
        }
        catch (IOException iox2) {
            Players.logger.log(Level.WARNING, "Failed to open stats.html", iox2);
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    public static void printRanks2() {
        printMaxRanks();
        Writer output = null;
        try {
            String dir = Constants.webPath;
            if (!dir.endsWith(File.separator)) {
                dir += File.separator;
            }
            final File aFile = new File(dir + "ranks.html");
            output = new BufferedWriter(new FileWriter(aFile));
            final String start = "<TABLE class=\"gameDataTable\"><TR><TH><Name</TH><TH>Rank</TH></TR>";
            try {
                output.write("<TABLE class=\"gameDataTable\"><TR><TH><Name</TH><TH>Rank</TH></TR>");
            }
            catch (IOException iox) {
                Players.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            int nums = 0;
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("select RANK, NAME from PLAYERS ORDER BY RANK DESC LIMIT ?");
                ps.setInt(1, 30);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (nums < 10) {
                        output.write("<TR class=\"gameDataTopTenTR\"><TD class=\"gameDataTopTenTDName\">" + rs.getString("NAME") + "</TD><TD class=\"gameDataTopTenTDValue\">" + rs.getInt("RANK") + "</TD></TR>");
                    }
                    else {
                        output.write("<TR class=\"gameDataTR\"><TD class=\"gameDataTDName\">" + rs.getString("NAME") + "</TD><TD class=\"gameDataTDValue\">" + rs.getInt("RANK") + "</TD></TR>");
                    }
                    ++nums;
                }
            }
            catch (SQLException sqx) {
                Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
            }
            output.write("</TABLE>");
        }
        catch (IOException iox2) {
            Players.logger.log(Level.WARNING, "Failed to close ranks.html", iox2);
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (IOException ex) {}
        }
    }
    
    public static Map<String, Long> getFriends(final long wurmid) {
        final Map<String, Long> toReturn = new HashMap<String, Long>();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("select p.NAME,p.WURMID from PLAYERS p INNER JOIN FRIENDS f ON f.FRIEND=p.WURMID WHERE f.WURMID=? ORDER BY NAME");
            ps.setLong(1, wurmid);
            rs = ps.executeQuery();
            while (rs.next()) {
                toReturn.put(rs.getString("NAME"), rs.getLong("WURMID"));
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return toReturn;
    }
    
    public static void pruneMessages() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM GMMESSAGES WHERE TIME<? AND MESSAGE NOT LIKE '<Roads> %' AND MESSAGE NOT LIKE '<System> Debug:'");
            ps.setLong(1, System.currentTimeMillis() - 172800000L);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM GMMESSAGES WHERE TIME<?");
            ps.setLong(1, System.currentTimeMillis() - 604800000L);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MGMTMESSAGES WHERE TIME<?");
            ps.setLong(1, System.currentTimeMillis() - 86400000L);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void addMgmtMessage(final String sender, final String message) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MGMTMESSAGES(TIME,SENDER,MESSAGE) VALUES(?,?,?)");
            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, sender);
            ps.setString(3, message.substring(0, Math.min(message.length(), 200)));
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void addGmMessage(final String sender, final String message) {
        if (!message.contains(" movement too ")) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("INSERT INTO GMMESSAGES(TIME,SENDER,MESSAGE) VALUES(?,?,?)");
                ps.setLong(1, System.currentTimeMillis());
                ps.setString(2, sender);
                ps.setString(3, message.substring(0, Math.min(message.length(), 200)));
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Players.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static void loadAllPrivatePOIForPlayer(final Player player) {
        if (!player.getPrivateMapAnnotations().isEmpty()) {
            return;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MAP_ANNOTATIONS WHERE POITYPE=0 AND OWNERID=?");
            ps.setLong(1, player.getWurmId());
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("ID");
                final String name = rs.getString("NAME");
                final long position = rs.getLong("POSITION");
                final byte type = rs.getByte("POITYPE");
                final long ownerId = rs.getLong("OWNERID");
                final String server = rs.getString("SERVER");
                final byte icon = rs.getByte("ICON");
                player.addMapPOI(new MapAnnotation(wid, name, type, position, ownerId, server, icon), false);
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem loading all private POI's - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void loadAllArtists() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ARTISTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wid = rs.getLong("WURMID");
                Players.artists.put(wid, new Artist(wid, rs.getBoolean("SOUND"), rs.getBoolean("GRAPHICS")));
            }
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem loading all artists - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean isArtist(final long wurmid, final boolean soundRequired, final boolean graphicsRequired) {
        if (!Players.artists.containsKey(wurmid)) {
            return false;
        }
        final Artist artist = Players.artists.get(wurmid);
        if (soundRequired) {
            if (!artist.isSound()) {
                return false;
            }
            if (graphicsRequired) {
                return artist.isGraphics();
            }
            return artist.isSound();
        }
        else {
            if (!graphicsRequired) {
                return true;
            }
            if (!artist.isGraphics()) {
                return false;
            }
            if (soundRequired) {
                return artist.isSound();
            }
            return artist.isGraphics();
        }
    }
    
    public static void addArtist(final long wurmid, final boolean sound, final boolean graphics) {
        if (!Players.artists.containsKey(wurmid)) {
            final Artist artist = new Artist(wurmid, sound, graphics);
            Players.artists.put(wurmid, artist);
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("INSERT INTO ARTISTS (WURMID,SOUND,GRAPHICS) VALUES(?,?,?)");
                ps.setLong(1, wurmid);
                ps.setBoolean(2, sound);
                ps.setBoolean(3, graphics);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Players.logger.log(Level.WARNING, "Problem adding artist with id: " + wurmid + " - " + sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        else {
            final Artist artist = Players.artists.get(wurmid);
            if (artist.isSound() != sound || artist.isGraphics() != graphics) {
                deleteArtist(wurmid);
                addArtist(wurmid, sound, graphics);
            }
        }
    }
    
    public static void deleteArtist(final long wurmid) {
        Players.artists.remove(wurmid);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ARTISTS WHERE WURMID=?");
            ps.setLong(1, wurmid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Players.logger.log(Level.WARNING, "Problem deleting artist with id: " + wurmid + " - " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getNumberOfKills() {
        long totalPlayerKills = 0L;
        for (final PlayerKills pk : Players.playerKills.values()) {
            if (pk != null && pk.getNumberOfKills() > 0) {
                totalPlayerKills += pk.getNumberOfKills();
            }
        }
        return totalPlayerKills;
    }
    
    public PlayerKills getPlayerKillsFor(final long wurmId) {
        PlayerKills pk = Players.playerKills.get(wurmId);
        if (pk == null) {
            pk = new PlayerKills(wurmId);
            Players.playerKills.put(wurmId, pk);
        }
        return pk;
    }
    
    public boolean isOverKilling(final long killerid, final long victimid) {
        final PlayerKills pk = this.getPlayerKillsFor(killerid);
        return pk.isOverKilling(victimid) || (Players.deathCount.containsKey(victimid) && Players.deathCount.get(victimid) > 3);
    }
    
    public void addKill(final long killerid, final long victimid, final String victimName) {
        final PlayerKills pk = this.getPlayerKillsFor(killerid);
        pk.addKill(victimid, victimName);
    }
    
    public void addPvPDeath(final long victimId) {
        short currentCount = 0;
        if (Players.deathCount.containsKey(victimId)) {
            currentCount = Players.deathCount.get(victimId);
        }
        Players.deathCount.put(victimId, (short)(currentCount + 1));
    }
    
    public void removePvPDeath(final long victimId) {
        if (!Players.deathCount.containsKey(victimId)) {
            return;
        }
        final short currentCount = Players.deathCount.get(victimId);
        if (currentCount > 1) {
            Players.deathCount.put(victimId, (short)(currentCount - 1));
        }
        else {
            Players.deathCount.remove(victimId);
        }
    }
    
    public boolean hasPvpDeaths(final long victimId) {
        return Players.deathCount.containsKey(victimId);
    }
    
    public void sendLogoff(final String reason) {
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            lPlayer.getCommunicator().sendShutDown(reason, false);
        }
    }
    
    public void logOffLinklessPlayers() {
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (!lPlayer.hasLink()) {
                this.logoutPlayer(lPlayer);
            }
        }
    }
    
    public void checkAffinities() {
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            lPlayer.checkAffinity();
        }
    }
    
    public void checkElectors() {
        final Player[] players;
        final Player[] playarr = players = this.getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.isAspiringKing()) {
                return;
            }
        }
        Methods.resetJennElector();
        Methods.resetHotsElector();
        Methods.resetMolrStone();
    }
    
    public float getCRBonus(final byte kingdomId) {
        final Float f = this.crBonuses.get(kingdomId);
        if (f != null) {
            return f;
        }
        return 0.0f;
    }
    
    public void sendUpdateEpicMission(final EpicMission mission) {
        for (final Player p : this.getPlayers()) {
            if (!Servers.localServer.PVPSERVER) {
                MissionPerformer.sendEpicMission(mission, p.getCommunicator());
            }
            else {
                MissionPerformer.sendEpicMissionPvPServer(mission, p, p.getCommunicator());
            }
        }
    }
    
    public void calcCRBonus() {
        if (!Servers.isThisAHomeServer()) {
            final Map<Byte, Float> numPs = new HashMap<Byte, Float>();
            float total = 0.0f;
            for (final Player lPlayer : this.getPlayers()) {
                if (lPlayer.isPaying()) {
                    final byte kingdomId = lPlayer.getKingdomId();
                    Float f = numPs.get(kingdomId);
                    if (f == null) {
                        f = 1.0f;
                    }
                    else {
                        ++f;
                    }
                    numPs.put(kingdomId, f);
                    ++total;
                }
            }
            final Map<Byte, Float> alliedPs = new HashMap<Byte, Float>();
            for (final Byte b : numPs.keySet()) {
                Float f2 = numPs.get(b);
                alliedPs.put(b, f2);
                final Kingdom k = Kingdoms.getKingdom(b);
                if (k != null) {
                    final Map<Byte, Byte> allies = k.getAllianceMap();
                    for (final Map.Entry<Byte, Byte> entry : allies.entrySet()) {
                        if (entry.getValue() == 1) {
                            final Float other = numPs.get(entry.getKey());
                            if (other == null) {
                                continue;
                            }
                            f2 += other;
                        }
                    }
                }
            }
            this.crBonuses.clear();
            if (total > 20.0f) {
                for (final Map.Entry<Byte, Float> totals : alliedPs.entrySet()) {
                    final float numbers = totals.getValue();
                    if (numbers / total < 0.05f) {
                        this.crBonuses.put(totals.getKey(), 2.0f);
                    }
                    else {
                        if (numbers / total >= 0.1f) {
                            continue;
                        }
                        this.crBonuses.put(totals.getKey(), 1.0f);
                    }
                }
            }
        }
    }
    
    public final void updateEigcInfo(final EigcClient client) {
        if (!client.getPlayerName().isEmpty()) {
            try {
                final Player p = this.getPlayer(client.getPlayerName());
                p.getCommunicator().updateEigcInfo(client);
            }
            catch (NoSuchPlayerException ex) {}
        }
    }
    
    public static boolean existsPlayerWithIp(final String ipAddress) {
        for (final Player p : getInstance().getPlayers()) {
            if (p.getSaveFile().getIpaddress().contains(ipAddress)) {
                return true;
            }
        }
        return false;
    }
    
    public final void sendGlobalGMMessage(final Creature sender, final String message) {
        final Message mess = new Message(sender, (byte)11, "GM", "<" + sender.getName() + "> " + message);
        Server.getInstance().addMessage(mess);
        addGmMessage(sender.getName(), message);
        if (message.trim().length() > 1) {
            final WCGmMessage wc = new WCGmMessage(WurmId.getNextWCCommandId(), sender.getName(), "(" + Servers.localServer.getAbbreviation() + ") " + message, false);
            if (Servers.localServer.LOGINSERVER) {
                wc.sendFromLoginServer();
            }
            else {
                wc.sendToLoginServer();
            }
        }
    }
    
    public final void pollPlayers() {
        final long delta = System.currentTimeMillis() - this.lastPoll;
        if (delta < 0.095f) {
            return;
        }
        this.lastPoll = System.currentTimeMillis();
        for (final Player lPlayer : this.getPlayers()) {
            if (lPlayer != null) {
                lPlayer.pollActions();
            }
        }
    }
    
    public final void sendKingdomToPlayers(final Kingdom kingdom) {
        for (final Player lPlayer : this.getPlayers()) {
            if (lPlayer.hasLink()) {
                lPlayer.getCommunicator().sendNewKingdom(kingdom.getId(), kingdom.getName(), kingdom.getSuffix());
            }
        }
    }
    
    public static void tellFriends(final PlayerState pState) {
        for (final Player p : getInstance().getPlayers()) {
            if (p.isFriend(pState.getPlayerId())) {
                p.getCommunicator().sendFriend(pState);
            }
        }
    }
    
    public final void sendTicket(final Ticket ticket) {
        for (final Player p : getInstance().getPlayers()) {
            if (p.hasLink() && ticket.isTicketShownTo(p)) {
                p.getCommunicator().sendTicket(ticket);
            }
        }
    }
    
    public final void sendTicket(final Ticket ticket, @Nullable final TicketAction ticketAction) {
        for (final Player p : getInstance().getPlayers()) {
            if (p.hasLink() && ticket.isTicketShownTo(p) && (ticketAction == null || ticketAction.isActionShownTo(p))) {
                p.getCommunicator().sendTicket(ticket, ticketAction);
            }
        }
    }
    
    public final void removeTicket(final Ticket ticket) {
        for (final Player p : getInstance().getPlayers()) {
            if (p.hasLink() && ticket.isTicketShownTo(p)) {
                p.getCommunicator().removeTicket(ticket);
            }
        }
    }
    
    public static final void sendVotingOpen(final VoteQuestion vq) {
        for (final Player p : getInstance().getPlayers()) {
            sendVotingOpen(p, vq);
        }
    }
    
    public static void sendVotingOpen(final Player p, final VoteQuestion vq) {
        if (p.hasLink() && vq.canVote(p)) {
            p.getCommunicator().sendServerMessage("Poll for " + vq.getQuestionTitle() + " is open, use /poll to participate.", 250, 150, 250);
        }
    }
    
    public void sendMgmtMessage(final Creature sender, final String playerName, final String message, final boolean emote, final boolean logit, final int red, final int green, final int blue) {
        Message mess = null;
        if (emote) {
            mess = new Message(sender, (byte)6, "MGMT", message);
        }
        else {
            mess = new Message(sender, (byte)9, "MGMT", "<" + playerName + "> " + message, red, green, blue);
        }
        if (logit) {
            addMgmtMessage(playerName, message);
        }
        final Player[] players;
        final Player[] playerArr = players = getInstance().getPlayers();
        for (final Player lPlayer : players) {
            if (lPlayer.mayHearMgmtTalk()) {
                if (sender == null) {
                    mess.setSender(lPlayer);
                }
                lPlayer.getCommunicator().sendMessage(mess);
            }
        }
    }
    
    private boolean shouldReceivePlayerList(final Player player) {
        return player.getKingdomId() == 4 && (player.isPlayerAssistant() || player.mayMute() || player.mayHearDevTalk() || player.getPower() > 0);
    }
    
    public static boolean getPollCheckClients() {
        return Players.pollCheckClients;
    }
    
    public static void setPollCheckClients(final boolean doit) {
        Players.pollCheckClients = doit;
    }
    
    public static void appointCA(final Creature performer, final String targetName) {
        Player playerPerformer = null;
        if (performer instanceof Player) {
            playerPerformer = (Player)performer;
            if (playerPerformer.mayAppointPlayerAssistant()) {
                final String pname = LoginHandler.raiseFirstLetter(targetName);
                Player p = null;
                try {
                    p = getInstance().getPlayer(pname);
                }
                catch (NoSuchPlayerException nsp) {
                    playerPerformer.getCommunicator().sendNormalServerMessage("No player online with the name " + pname);
                }
                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
                try {
                    pinf.load();
                }
                catch (IOException e) {
                    performer.getCommunicator().sendAlertServerMessage("This player does not exist.");
                    return;
                }
                if (pinf.wurmId > 0L) {
                    if (pinf.isPlayerAssistant()) {
                        pinf.setIsPlayerAssistant(false);
                        if (p != null) {
                            p.getCommunicator().sendAlertServerMessage("You no longer have the duties of a community assistant.", (byte)1);
                        }
                        playerPerformer.getCommunicator().sendSafeServerMessage(pname + " no longer has the duties of being a community assistant.", (byte)1);
                        final WcDemotion wc = new WcDemotion(WurmId.getNextWCCommandId(), playerPerformer.getWurmId(), pinf.wurmId, (short)1);
                        wc.sendToLoginServer();
                    }
                    else {
                        if (p != null) {
                            p.setPlayerAssistant(true);
                            p.togglePlayerAssistantWindow(true);
                            p.getCommunicator().sendSafeServerMessage("You are now a Community Assistant and receives a CA window.");
                            p.getCommunicator().sendSafeServerMessage("New players will also receive that and may ask you questions.");
                            p.getCommunicator().sendSafeServerMessage("The suggested way to approach new players is not to approach them directly");
                            p.getCommunicator().sendSafeServerMessage("but instead let them ask questions. Otherwise many of them may become deterred");
                            p.getCommunicator().sendSafeServerMessage("since this may be an early online experience or they have poor english knowledge.");
                        }
                        else {
                            pinf.setIsPlayerAssistant(true);
                            pinf.togglePlayerAssistantWindow(true);
                            playerPerformer.getCommunicator().sendAlertServerMessage(pname + " needs to be online in order to receive the title.", (byte)2);
                        }
                        playerPerformer.getCommunicator().sendSafeServerMessage(pname + " is now appointed Community Assistant.", (byte)1);
                        if (playerPerformer.getLogger() != null) {
                            playerPerformer.getLogger().log(Level.INFO, playerPerformer.getName() + " appoints " + pname + " community assistant.");
                        }
                        Players.logger.log(Level.INFO, playerPerformer.getName() + " appoints " + pname + " as community assistant.");
                    }
                }
                else {
                    playerPerformer.getCommunicator().sendNormalServerMessage("No player found with the name " + pname);
                }
            }
        }
    }
    
    public static void appointCM(final Creature performer, final String targetName) {
        if (performer.getPower() >= 1) {
            final String pname = LoginHandler.raiseFirstLetter(targetName);
            Player p = null;
            try {
                p = getInstance().getPlayer(pname);
            }
            catch (NoSuchPlayerException nsp) {
                performer.getCommunicator().sendNormalServerMessage("No player online with the name " + pname);
            }
            final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
            try {
                pinf.load();
            }
            catch (IOException e) {
                performer.getCommunicator().sendAlertServerMessage("This player does not exist.");
                return;
            }
            if (pinf.wurmId > 0L) {
                if (pinf.getPower() == 0) {
                    if (pinf.mayMute) {
                        pinf.setMayMute(false);
                        if (p != null) {
                            p.getCommunicator().sendAlertServerMessage("You may no longer mute other players.", (byte)1);
                        }
                        performer.getCommunicator().sendSafeServerMessage(pname + " may no longer mute other players.");
                        final WcDemotion wc = new WcDemotion(WurmId.getNextWCCommandId(), performer.getWurmId(), pinf.wurmId, (short)2);
                        wc.sendToLoginServer();
                    }
                    else {
                        pinf.setMayMute(true);
                        if (p != null) {
                            p.getCommunicator().sendSafeServerMessage("You may now mute other players. Use this with extreme care and wise judgement.");
                            p.getCommunicator().sendSafeServerMessage("The syntax is #mute <playername> <number of hours> <reason>");
                            p.getCommunicator().sendSafeServerMessage("For example: #mute unforgiven 6 foul language");
                            p.getCommunicator().sendSafeServerMessage("To unmute a player, use #unmute <playername>");
                            p.getCommunicator().sendSafeServerMessage("You may see who are muted with the command #showmuted");
                        }
                        performer.getCommunicator().sendSafeServerMessage(pname + " may now mute other players.", (byte)1);
                        if (performer.getLogger() != null) {
                            performer.getLogger().log(Level.INFO, performer.getName() + " allows " + pname + " to mute other players.");
                        }
                        Players.logger.log(Level.INFO, performer.getName() + " allows " + pname + " to mute other players.");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage(pinf.getName() + " may already mute, because he is a Hero or higher.");
                }
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("No player found with the name " + pname);
            }
        }
    }
    
    public static void displayLCMInfo(final Creature performer, final String targetName) {
        if (performer == null || !performer.hasLink()) {
            return;
        }
        if (performer.getPower() >= 1) {
            try {
                final PlayerInfo targetInfo = PlayerInfoFactory.createPlayerInfo(targetName);
                targetInfo.load();
                final PlayerState targetState = PlayerInfoFactory.getPlayerState(targetInfo.wurmId);
                final Logger logger = performer.getLogger();
                if (logger != null) {
                    logger.log(Level.INFO, performer.getName() + " tried to view the info of " + targetInfo.getName());
                }
                if (performer.getPower() < targetInfo.getPower()) {
                    performer.getCommunicator().sendSafeServerMessage("You can't just look at the information of higher ranking staff members!");
                    return;
                }
                final String email = targetInfo.emailAddress;
                final String ip = targetInfo.getIpaddress();
                final String lastLogout = new Date(targetState.getLastLogout()).toString();
                final String timePlayed = Server.getTimeFor(targetInfo.playingTime);
                final String CAInfo = targetInfo.getName() + " is " + (targetInfo.isPlayerAssistant() ? "a CA." : "not a CA.");
                final String CMInfo = targetInfo.getName() + " is " + (targetInfo.mayMute ? "a CM." : "not a CM.");
                performer.getCommunicator().sendNormalServerMessage("Information about " + targetInfo.getName());
                performer.getCommunicator().sendNormalServerMessage("-----");
                performer.getCommunicator().sendNormalServerMessage("Email address: " + email);
                performer.getCommunicator().sendNormalServerMessage("IP address: " + ip);
                performer.getCommunicator().sendNormalServerMessage("Last logout: " + lastLogout);
                performer.getCommunicator().sendNormalServerMessage("Time played: " + timePlayed);
                performer.getCommunicator().sendNormalServerMessage(CAInfo);
                performer.getCommunicator().sendNormalServerMessage(CMInfo);
                performer.getCommunicator().sendNormalServerMessage("-----");
            }
            catch (Exception e) {
                performer.getCommunicator().sendAlertServerMessage("This player does not exist.");
            }
        }
    }
    
    public void updateTabs(final byte tab, final TabData tabData) {
        if (tab == 2) {
            this.removeFromTabs(tabData.getWurmId(), tabData.getName());
        }
        else if (tab == 0) {
            Players.tabListGM.put(tabData.getName(), tabData);
            for (final Player player : getInstance().getPlayers()) {
                if (player.mayHearDevTalk()) {
                    if (tabData.isVisible() || tabData.getPower() <= player.getPower()) {
                        player.getCommunicator().sendAddGm(tabData.getName(), tabData.getWurmId());
                    }
                    else {
                        player.getCommunicator().sendRemoveGm(tabData.getName());
                    }
                }
            }
        }
        else if (tab == 1) {
            Players.tabListMGMT.put(tabData.getName(), tabData);
            for (final Player player : getInstance().getPlayers()) {
                if (player.mayHearMgmtTalk()) {
                    if (tabData.isVisible()) {
                        player.getCommunicator().sendAddMgmt(tabData.getName(), tabData.getWurmId());
                    }
                    else {
                        player.getCommunicator().sendRemoveMgmt(tabData.getName());
                    }
                }
            }
        }
    }
    
    public void sendToTabs(final Player player, final boolean showMe, final boolean justGM) {
        if (player.getPower() >= 2 || player.mayHearDevTalk()) {
            boolean sendGM = false;
            TabData tabData = Players.tabListGM.get(player.getName());
            if (tabData == null) {
                tabData = new TabData(player.getWurmId(), player.getName(), (byte)player.getPower(), showMe || player.getPower() < 2);
                sendGM = true;
            }
            else if (tabData.isVisible() != showMe && player.getPower() >= 2) {
                tabData = new TabData(player.getWurmId(), player.getName(), (byte)player.getPower(), showMe);
                sendGM = true;
            }
            if (sendGM) {
                this.updateTabs((byte)0, tabData);
                final WcTabLists wtl = new WcTabLists((byte)0, tabData);
                if (Servers.isThisLoginServer()) {
                    wtl.sendFromLoginServer();
                }
                else {
                    wtl.sendToLoginServer();
                }
            }
        }
        if (!justGM) {
            boolean sendMGMT = false;
            TabData tabData = Players.tabListMGMT.get(player.getName());
            if (tabData == null) {
                tabData = new TabData(player.getWurmId(), player.getName(), (byte)player.getPower(), showMe || player.getPower() < 2);
                sendMGMT = true;
            }
            else if (tabData.isVisible() != showMe && player.getPower() >= 2) {
                tabData = new TabData(player.getWurmId(), player.getName(), (byte)player.getPower(), showMe);
                sendMGMT = true;
            }
            if (sendMGMT) {
                this.updateTabs((byte)1, tabData);
                final WcTabLists wtl = new WcTabLists((byte)1, tabData);
                if (Servers.isThisLoginServer()) {
                    wtl.sendFromLoginServer();
                }
                else {
                    wtl.sendToLoginServer();
                }
            }
        }
    }
    
    public void removeFromTabs(final long wurmId, final String name) {
        final TabData oldGMTabData = Players.tabListGM.remove(name);
        final TabData oldMGMTTabData = Players.tabListMGMT.remove(name);
        if (oldGMTabData != null || oldMGMTTabData != null) {
            for (final Player player : getInstance().getPlayers()) {
                if (oldGMTabData != null && player.mayHearDevTalk()) {
                    player.getCommunicator().sendRemoveGm(name);
                }
                if (oldMGMTTabData != null && player.mayHearMgmtTalk()) {
                    player.getCommunicator().sendRemoveMgmt(name);
                }
            }
        }
    }
    
    public void sendRemoveFromTabs(final long wurmId, final String name) {
        final TabData tabData = new TabData(wurmId, name, (byte)0, false);
        if (tabData != null) {
            final WcTabLists wtl = new WcTabLists((byte)2, tabData);
            if (Servers.isThisLoginServer()) {
                wtl.sendFromLoginServer();
            }
            else {
                wtl.sendToLoginServer();
            }
        }
    }
    
    static {
        Players.players = new ConcurrentHashMap<String, Player>();
        Players.playersById = new ConcurrentHashMap<Long, Player>();
        tabListGM = new ConcurrentHashMap<String, TabData>();
        tabListMGMT = new ConcurrentHashMap<String, TabData>();
        Players.instance = null;
        Players.logger = Logger.getLogger(Players.class.getName());
        artists = new HashMap<Long, Artist>();
        loggers = new HashMap<String, Logger>();
        Players.bans = new HashSet<Ban>();
        playerKills = new ConcurrentHashMap<Long, PlayerKills>();
        Players.kosList = new ConcurrentLinkedQueue<KosWarning>();
        Players.header = "<HTML> <HEAD><TITLE>Wurm battle ranks</TITLE></HEAD><BODY><BR><BR>";
        Players.pollCheckClients = false;
        Players.challengeStep = 0;
        Players.deathCount = new HashMap<Long, Short>();
        caHelpLogger = Logger.getLogger("ca-help");
        Players.header2 = "<HTML>\n\t<HEAD>\n\t<TITLE>Wurm Online battle ranks</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t";
        Players.headerStats = "<!DOCTYPE html> <HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> <TITLE>Wurm Online Server Stats</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t";
        Players.headerStats2 = "<!DOCTYPE html> <HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> <TITLE>Wurm Online Champion Eternal Records</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t";
    }
}
