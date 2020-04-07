// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.behaviours.Vehicles;
import java.util.Arrays;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.webinterface.WebCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import com.wurmonline.server.zones.TilePoller;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.utils.SimpleArgumentParser;

public final class Servers implements MiscConstants
{
    public static SimpleArgumentParser arguments;
    private static Map<String, ServerEntry> neighbours;
    private static Map<Integer, ServerEntry> allServers;
    public static ServerEntry localServer;
    public static ServerEntry loginServer;
    private static final Logger logger;
    private static final String GET_ALL_SERVERS = "SELECT * FROM SERVERS";
    private static final String INSERT_SERVER = "INSERT INTO SERVERS(SERVER,NAME,HOMESERVER,SPAWNPOINTJENNX,SPAWNPOINTJENNY,SPAWNPOINTLIBX,SPAWNPOINTLIBY,SPAWNPOINTMOLX,SPAWNPOINTMOLY,INTRASERVERADDRESS,INTRASERVERPORT,INTRASERVERPASSWORD,EXTERNALIP, EXTERNALPORT,LOGINSERVER, KINGDOM,ISPAYMENT,TWITKEY,TWITSECRET,TWITAPP,TWITAPPSECRET, LOCAL,ISTEST,RANDOMSPAWNS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String GET_NEIGHBOURS = "SELECT * FROM SERVERNEIGHBOURS WHERE SERVER=?";
    private static final String ADD_NEIGHBOUR = "INSERT INTO SERVERNEIGHBOURS(SERVER,NEIGHBOUR,DIRECTION) VALUES(?,?,?)";
    private static final String DELETE_NEIGHBOUR = "DELETE FROM SERVERNEIGHBOURS WHERE SERVER=? AND NEIGHBOUR=?";
    private static final String DELETE_SERVER = "DELETE FROM SERVERS WHERE SERVER=?";
    private static final String DELETE_SERVER2 = "DELETE FROM SERVERNEIGHBOURS WHERE SERVER=? OR NEIGHBOUR=?";
    private static final String SET_TWITTER = "UPDATE SERVERS SET TWITKEY=?,TWITSECRET=?,TWITAPP=?,TWITAPPSECRET=? WHERE SERVER=?";
    
    public static ServerEntry getServer(final String direction) {
        return Servers.neighbours.get(direction);
    }
    
    public static boolean addServerNeighbour(final int serverid, final String direction) {
        boolean ok = false;
        if (loadNeighbour(serverid, direction)) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getLoginDbCon();
                ps = dbcon.prepareStatement("INSERT INTO SERVERNEIGHBOURS(SERVER,NEIGHBOUR,DIRECTION) VALUES(?,?,?)");
                ps.setInt(1, Servers.localServer.id);
                ps.setInt(2, serverid);
                ps.setString(3, direction);
                ps.executeUpdate();
                ok = true;
            }
            catch (SQLException sqex) {
                Servers.logger.log(Level.WARNING, "Failed to insert neighbour " + serverid + "," + direction + " into logindb!" + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
        return ok;
    }
    
    public static boolean isThisLoginServer() {
        return Servers.loginServer == null || Servers.localServer.id == Servers.loginServer.id;
    }
    
    public static boolean isRealLoginServer() {
        return Servers.localServer.id == Servers.loginServer.id;
    }
    
    public static boolean isThisAChaosServer() {
        return Servers.localServer != null && Servers.localServer.isChaosServer();
    }
    
    public static boolean isThisAnEpicServer() {
        return Servers.localServer != null && Servers.localServer.EPIC;
    }
    
    public static boolean isThisAnEpicOrChallengeServer() {
        return Servers.localServer != null && (Servers.localServer.EPIC || Servers.localServer.isChallengeServer());
    }
    
    public static boolean isThisAHomeServer() {
        return Servers.localServer != null && Servers.localServer.HOMESERVER;
    }
    
    public static boolean isThisAPvpServer() {
        return Servers.localServer != null && Servers.localServer.PVPSERVER;
    }
    
    public static boolean isThisATestServer() {
        return Servers.localServer != null && Servers.localServer.testServer;
    }
    
    public static byte getLocalKingdom() {
        byte localKingdom;
        if (Servers.localServer != null) {
            localKingdom = Servers.localServer.getKingdom();
        }
        else {
            localKingdom = -10;
        }
        return localKingdom;
    }
    
    public static int getLocalServerId() {
        int localServerId;
        if (Servers.localServer != null) {
            localServerId = Servers.localServer.getId();
        }
        else {
            localServerId = 0;
        }
        return localServerId;
    }
    
    public static ServerEntry getLoginServer() {
        return Servers.loginServer;
    }
    
    public static int getLoginServerId() {
        return Servers.loginServer.id;
    }
    
    public static String getLocalServerName() {
        String localServerName;
        if (Servers.localServer != null) {
            localServerName = Servers.localServer.getName();
        }
        else {
            localServerName = "Unknown";
        }
        return localServerName;
    }
    
    public static boolean deleteServerNeighbour(final String dir) {
        boolean ok = false;
        final ServerEntry entry = Servers.neighbours.get(dir);
        if (entry != null) {
            ok = deleteServerNeighbour(entry.id);
        }
        Servers.neighbours.remove(dir);
        if (dir.equals("NORTH")) {
            Servers.localServer.serverNorth = null;
        }
        else if (dir.equals("WEST")) {
            Servers.localServer.serverWest = null;
        }
        else if (dir.equals("SOUTH")) {
            Servers.localServer.serverSouth = null;
        }
        else if (dir.equals("EAST")) {
            Servers.localServer.serverEast = null;
        }
        return ok;
    }
    
    public static boolean deleteServerNeighbour(final int id) {
        boolean ok = false;
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("DELETE FROM SERVERNEIGHBOURS WHERE SERVER=? AND NEIGHBOUR=?");
            ps.setInt(1, Servers.localServer.id);
            ps.setInt(2, id);
            ps.executeUpdate();
            ok = true;
        }
        catch (SQLException sqex) {
            Servers.logger.log(Level.WARNING, "Failed to delete neighbour " + id + " from logindb!" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        return ok;
    }
    
    public static boolean deleteServerEntry(final int id) {
        boolean ok = false;
        Connection dbcon = null;
        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps2 = dbcon.prepareStatement("DELETE FROM SERVERNEIGHBOURS WHERE SERVER=? OR NEIGHBOUR=?");
            ps2.setInt(1, id);
            ps2.setInt(2, id);
            ps2.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps2, null);
            ps = dbcon.prepareStatement("DELETE FROM SERVERS WHERE SERVER=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ok = true;
        }
        catch (SQLException sqex) {
            Servers.logger.log(Level.WARNING, "Failed to delete neighbour " + id + " from logindb!" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbUtilities.closeDatabaseObjects(ps2, null);
            DbConnector.returnConnection(dbcon);
        }
        removeServer(id);
        return ok;
    }
    
    private static final void removeServer(final int id) {
        Servers.allServers.remove(id);
        Servers.neighbours.remove(id);
    }
    
    public static final void registerServer(final int id, final String name, final boolean homeServer, final int fox, final int foy, final int libx, final int liby, final int molx, final int moly, final String intraip, final String intraport, final String password, final String externalip, final String externalport, final boolean loginserver, final byte kingdom, final boolean isPayment, final String _consumerKeyToUse, final String _consumerSecretToUse, final String _applicationToken, final String _applicationSecret, final boolean isLocalServer, final boolean isTestServer, final boolean randomSpawns) {
        Connection dbcon = null;
        PreparedStatement ps2 = null;
        try {
            Servers.logger.log(Level.INFO, "Registering server id: " + id + ", external IP: " + externalip + ", name: " + name);
            dbcon = DbConnector.getLoginDbCon();
            ps2 = dbcon.prepareStatement("INSERT INTO SERVERS(SERVER,NAME,HOMESERVER,SPAWNPOINTJENNX,SPAWNPOINTJENNY,SPAWNPOINTLIBX,SPAWNPOINTLIBY,SPAWNPOINTMOLX,SPAWNPOINTMOLY,INTRASERVERADDRESS,INTRASERVERPORT,INTRASERVERPASSWORD,EXTERNALIP, EXTERNALPORT,LOGINSERVER, KINGDOM,ISPAYMENT,TWITKEY,TWITSECRET,TWITAPP,TWITAPPSECRET, LOCAL,ISTEST,RANDOMSPAWNS) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps2.setInt(1, id);
            ps2.setString(2, name);
            ps2.setBoolean(3, homeServer);
            ps2.setInt(4, fox);
            ps2.setInt(5, foy);
            ps2.setInt(6, libx);
            ps2.setInt(7, liby);
            ps2.setInt(8, molx);
            ps2.setInt(9, moly);
            ps2.setString(10, intraip);
            ps2.setString(11, intraport);
            ps2.setString(12, password);
            ps2.setString(13, externalip);
            ps2.setString(14, externalport);
            ps2.setBoolean(15, loginserver);
            ps2.setByte(16, kingdom);
            ps2.setBoolean(17, isPayment);
            ps2.setString(18, _consumerKeyToUse);
            ps2.setString(19, _consumerSecretToUse);
            ps2.setString(20, _applicationToken);
            ps2.setString(21, _applicationSecret);
            ps2.setBoolean(22, isLocalServer);
            ps2.setBoolean(23, isTestServer);
            ps2.setBoolean(24, randomSpawns);
            ps2.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps2, null);
            if (loginserver) {
                loadLoginServer();
            }
            loadAllServers(true);
        }
        catch (SQLException sqex) {
            Servers.logger.log(Level.WARNING, "Failed to load or insert server into logindb!" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps2, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void setTwitCredentials(final int serverId, final String _consumerKeyToUse, final String _consumerSecretToUse, final String _applicationToken, final String _applicationSecret) {
        final ServerEntry entry = getServerWithId(serverId);
        if (entry != null) {
            entry.consumerKeyToUse = _consumerKeyToUse;
            entry.consumerSecretToUse = _consumerSecretToUse;
            entry.applicationToken = _applicationToken;
            entry.applicationSecret = _applicationSecret;
            entry.canTwit();
        }
        if (Servers.localServer.id == serverId) {
            Servers.localServer.consumerKeyToUse = _consumerKeyToUse;
            Servers.localServer.consumerSecretToUse = _consumerSecretToUse;
            Servers.localServer.applicationToken = _applicationToken;
            Servers.localServer.applicationSecret = _applicationSecret;
            Servers.localServer.canTwit();
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE SERVERS SET TWITKEY=?,TWITSECRET=?,TWITAPP=?,TWITAPPSECRET=? WHERE SERVER=?");
            ps.setString(1, _consumerKeyToUse);
            ps.setString(2, _consumerSecretToUse);
            ps.setString(3, _applicationToken);
            ps.setString(4, _applicationSecret);
            ps.setInt(5, serverId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Servers.logger.log(Level.WARNING, "Failed to set twitter info for server with id " + serverId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void loadNeighbours() {
        Servers.neighbours = new HashMap<String, ServerEntry>();
        if (Servers.localServer != null) {
            Servers.localServer.serverNorth = null;
            Servers.localServer.serverEast = null;
            Servers.localServer.serverSouth = null;
            Servers.localServer.serverWest = null;
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getLoginDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM SERVERNEIGHBOURS WHERE SERVER=?");
                ps.setInt(1, Servers.localServer.id);
                rs = ps.executeQuery();
                while (rs.next()) {
                    final int serverid = rs.getInt("NEIGHBOUR");
                    final String direction = rs.getString("DIRECTION");
                    loadNeighbour(serverid, direction);
                }
            }
            catch (SQLException sqex) {
                Servers.logger.log(Level.WARNING, "Failed to load all neighbours!" + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    public static final boolean loadNeighbour(final int serverid, final String dir) {
        boolean ok = false;
        final ServerEntry entry = getServerWithId(serverid);
        if (entry != null) {
            Servers.logger.log(Level.INFO, "found neighbour " + entry.name + " " + dir);
            Servers.neighbours.put(dir, entry);
            if (dir.equals("NORTH")) {
                Servers.logger.log(Level.INFO, "NORTH neighbour " + entry.name + " " + dir);
                Servers.localServer.serverNorth = entry;
            }
            else if (dir.equals("WEST")) {
                Servers.logger.log(Level.INFO, "WEST neighbour " + entry.name + " " + dir);
                Servers.localServer.serverWest = entry;
            }
            else if (dir.equals("SOUTH")) {
                Servers.logger.log(Level.INFO, "SOUTH neighbour " + entry.name + " " + dir);
                Servers.localServer.serverSouth = entry;
            }
            else if (dir.equals("EAST")) {
                Servers.logger.log(Level.INFO, "EAST neighbour " + entry.name + " " + dir);
                Servers.localServer.serverEast = entry;
            }
            ok = true;
        }
        return ok;
    }
    
    public static void loadAllServers(final boolean reload) {
        System.out.println("Loading servers");
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (!reload) {
                Servers.allServers = new ConcurrentHashMap<Integer, ServerEntry>();
                Servers.localServer = null;
            }
            else {
                for (final ServerEntry server : Servers.allServers.values()) {
                    server.reloading = true;
                }
            }
            Servers.logger.log(Level.INFO, "Loading all servers.");
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SERVERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int loadedId = rs.getInt("SERVER");
                ServerEntry entry = null;
                if (reload) {
                    entry = getServerWithId(loadedId);
                }
                if (entry == null) {
                    entry = new ServerEntry();
                    entry.id = loadedId;
                }
                else {
                    entry.reloading = false;
                }
                entry.HOMESERVER = rs.getBoolean("HOMESERVER");
                entry.PVPSERVER = rs.getBoolean("PVP");
                entry.name = rs.getString("NAME");
                Servers.logger.log(Level.INFO, "Loading " + entry.name + " - " + entry.id);
                entry.isLocal = rs.getBoolean("LOCAL");
                if (entry.isLocal) {
                    Servers.localServer = entry;
                    entry.isAvailable = true;
                    entry.setNextEpicPoll(rs.getLong("NEXTEPICPOLL"));
                    entry.setSkillDaySwitch(rs.getLong("SKILLDAYSWITCH"));
                    entry.setSkillWeekSwitch(rs.getLong("SKILLWEEKSWITCH"));
                    entry.setNextHota(rs.getLong("NEXTHOTA"));
                    entry.setFatigueSwitch(rs.getLong("FATIGUESWITCH"));
                }
                entry.SPAWNPOINTJENNX = rs.getInt("SPAWNPOINTJENNX");
                entry.SPAWNPOINTJENNY = rs.getInt("SPAWNPOINTJENNY");
                entry.SPAWNPOINTLIBX = rs.getInt("SPAWNPOINTLIBX");
                entry.SPAWNPOINTLIBY = rs.getInt("SPAWNPOINTLIBY");
                entry.SPAWNPOINTMOLX = rs.getInt("SPAWNPOINTMOLX");
                entry.SPAWNPOINTMOLY = rs.getInt("SPAWNPOINTMOLY");
                entry.INTRASERVERADDRESS = rs.getString("INTRASERVERADDRESS");
                entry.INTRASERVERPORT = rs.getString("INTRASERVERPORT");
                entry.INTRASERVERPASSWORD = rs.getString("INTRASERVERPASSWORD");
                entry.EXTERNALIP = rs.getString("EXTERNALIP");
                entry.EXTERNALPORT = rs.getString("EXTERNALPORT");
                entry.LOGINSERVER = rs.getBoolean("LOGINSERVER");
                if (entry.LOGINSERVER) {
                    Servers.loginServer = entry;
                }
                entry.KINGDOM = rs.getByte("KINGDOM");
                entry.ISPAYMENT = rs.getBoolean("ISPAYMENT");
                entry.entryServer = rs.getBoolean("ENTRYSERVER");
                entry.testServer = rs.getBoolean("ISTEST");
                entry.challengeServer = rs.getBoolean("CHALLENGE");
                if (entry.challengeServer) {
                    entry.setChallengeStarted(rs.getLong("CHALLENGESTARTED"));
                    entry.setChallengeEnds(rs.getLong("CHALLENGEEND"));
                    if (entry.getChallengeStarted() == 0L && entry.getChallengeEnds() == 0L) {
                        entry.challengeServer = false;
                    }
                }
                entry.lastDecreasedChampionPoints = rs.getLong("LASTRESETCHAMPS");
                entry.consumerKeyToUse = rs.getString("TWITKEY");
                entry.consumerSecretToUse = rs.getString("TWITSECRET");
                entry.applicationToken = rs.getString("TWITAPP");
                entry.applicationSecret = rs.getString("TWITAPPSECRET");
                entry.champConsumerKeyToUse = rs.getString("CHAMPTWITKEY");
                entry.champConsumerSecretToUse = rs.getString("CHAMPTWITSECRET");
                entry.champApplicationToken = rs.getString("CHAMPTWITAPP");
                entry.champApplicationSecret = rs.getString("CHAMPTWITAPPSECRET");
                final long movedArtifacts = rs.getLong("MOVEDARTIS");
                if (movedArtifacts > 0L) {
                    entry.setMovedArtifacts(movedArtifacts);
                }
                else {
                    entry.movedArtifacts();
                }
                final long lastSpawnedUnique = rs.getLong("SPAWNEDUNIQUE");
                entry.setLastSpawnedUnique(lastSpawnedUnique);
                entry.canTwit();
                final String rmiPort = rs.getString("RMIPORT");
                if (rmiPort != null && rmiPort.length() > 0) {
                    try {
                        entry.RMI_PORT = Integer.parseInt(rmiPort);
                    }
                    catch (NullPointerException npe) {
                        Servers.logger.log(Level.WARNING, "rmiPort for server " + loadedId + " was not a number " + rmiPort, npe);
                    }
                }
                final String regPort = rs.getString("REGISTRATIONPORT");
                if (regPort != null && regPort.length() > 0) {
                    try {
                        entry.REGISTRATION_PORT = Integer.parseInt(regPort);
                    }
                    catch (NullPointerException npe2) {
                        Servers.logger.log(Level.WARNING, "regPort for server " + loadedId + " was not a number " + regPort, npe2);
                    }
                }
                try {
                    entry.pLimit = rs.getInt("MAXPLAYERS");
                    entry.maxCreatures = rs.getInt("MAXCREATURES");
                    entry.maxTypedCreatures = entry.maxCreatures / 8;
                    entry.percentAggCreatures = rs.getFloat("PERCENT_AGG_CREATURES");
                    entry.treeGrowth = rs.getInt("TREEGROWTH");
                    TilePoller.treeGrowth = entry.treeGrowth;
                    entry.setSkillGainRate(rs.getFloat("SKILLGAINRATE"));
                    entry.setActionTimer(rs.getFloat("ACTIONTIMER"));
                    entry.setHotaDelay(rs.getInt("HOTADELAY"));
                }
                catch (Exception ex) {
                    Servers.logger.log(Level.WARNING, "Please run USE WURMLOGIN;    ALTER TABLE SERVERS ADD COLUMN MAXPLAYERS INT NOT NULL DEFAULT 1000;    ALTER TABLE SERVERS ADD COLUMN MAXCREATURES INT NOT NULL DEFAULT 1000;    ALTER TABLE SERVERS ADD COLUMN PERCENT_AGG_CREATURES FLOAT NOT NULL DEFAULT 30;    ALTER TABLE SERVERS ADD COLUMN TREEGROWTH INT NOT NULL DEFAULT 20;    ALTER TABLE SERVERS ADD COLUMN SKILLGAINRATE FLOAT NOT NULL DEFAULT 100;    ALTER TABLE SERVERS ADD COLUMN ACTIONTIMER FLOAT NOT NULL DEFAULT 100;    ALTER TABLE SERVERS ADD COLUMN HOTADELAY INT NOT NULL DEFAULT 2160; ALTER TABLE SERVERS ADD COLUMN MESHSIZE INT NOT NULL DEFAULT 2048;");
                }
                try {
                    entry.mapname = rs.getString("MAPNAME");
                }
                catch (Exception ex2) {}
                try {
                    entry.randomSpawns = rs.getBoolean("RANDOMSPAWNS");
                    entry.setSkillbasicval(rs.getFloat("SKILLBASICSTART"));
                    entry.setSkillfightval(rs.getFloat("SKILLFIGHTINGSTART"));
                    entry.setSkillmindval(rs.getFloat("SKILLMINDLOGICSTART"));
                    entry.setSkilloverallval(rs.getFloat("SKILLOVERALLSTART"));
                    entry.EPIC = rs.getBoolean("EPIC");
                    entry.setCombatRatingModifier(rs.getFloat("CRMOD"));
                    entry.setSteamServerPassword(rs.getString("STEAMPW"));
                    entry.setUpkeep(rs.getBoolean("UPKEEP"));
                    entry.setMaxDeedSize(rs.getInt("MAXDEED"));
                    entry.setFreeDeeds(rs.getBoolean("FREEDEEDS"));
                    entry.setTraderMaxIrons(rs.getInt("TRADERMAX"));
                    entry.setInitialTraderIrons(rs.getInt("TRADERINIT"));
                    entry.setTunnelingHits(rs.getInt("TUNNELING"));
                    entry.setBreedingTimer(rs.getLong("BREEDING"));
                    entry.setFieldGrowthTime(rs.getLong("FIELDGROWTH"));
                    entry.setKingsmoneyAtRestart(rs.getInt("KINGSMONEY"));
                    entry.setMotd(rs.getString("MOTD"));
                    entry.setSkillbcval(rs.getFloat("SKILLBODYCONTROLSTART"));
                }
                catch (Exception ex3) {}
                final byte caHelpGroup = rs.getByte("CAHELPGROUP");
                entry.setCAHelpGroup(caHelpGroup);
                Servers.allServers.put(entry.id, entry);
                if (Servers.logger.isLoggable(Level.FINE)) {
                    Servers.logger.fine("Loaded server " + entry);
                }
                if (entry.isLocal) {
                    final long time = rs.getLong("WORLDTIME");
                    if (time <= 0L) {
                        continue;
                    }
                    Servers.logger.log(Level.INFO, "Using database entry for time " + time);
                    WurmCalendar.setTime(time);
                    TilePoller.currentPollTile = rs.getInt("POLLTILE");
                    TilePoller.rest = rs.getInt("TILEREST");
                    TilePoller.pollModifier = rs.getInt("POLLMOD");
                    TilePoller.pollround = rs.getInt("POLLROUND");
                    WurmCalendar.checkSpring();
                    TilePoller.calcRest();
                }
            }
        }
        catch (SQLException sqex) {
            Servers.logger.log(Level.WARNING, "Failed to load all servers!" + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            Servers.logger.info("Loaded " + Servers.allServers.size() + " servers from the database");
        }
        final Set<Integer> toDelete = new HashSet<Integer>();
        for (final ServerEntry server2 : Servers.allServers.values()) {
            if (server2.reloading) {
                toDelete.add(server2.id);
            }
            server2.reloading = false;
        }
        for (final Integer id : toDelete) {
            Servers.allServers.remove(id);
        }
        loadNeighbours();
        ServerProperties.loadProperties();
    }
    
    public static final void moveServerId(final ServerEntry entry, final int oldId) {
        Servers.allServers.remove(oldId);
        Servers.allServers.put(entry.getId(), entry);
        if (entry.isLocal) {
            Servers.localServer = entry;
        }
    }
    
    public static void loadLoginServer() {
        for (final ServerEntry server : Servers.allServers.values()) {
            if (server.LOGINSERVER) {
                Servers.loginServer = server;
                Servers.logger.log(Level.INFO, "Loaded loginserver " + Servers.loginServer.id);
            }
        }
    }
    
    public static String rename(final String oldName, final String newName, final String newPass, final int power) {
        String toReturn = "";
        if (!Servers.localServer.testServer) {
            for (final ServerEntry s : Servers.allServers.values()) {
                if (!s.isConnected()) {
                    return "Not all servers are connected (" + s.getName() + "). Try later. This is an Error.";
                }
            }
        }
        for (final ServerEntry s : Servers.allServers.values()) {
            if (s.id != Servers.localServer.id) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(s.id);
                toReturn += lsw.renamePlayer(oldName, newName, newPass, power);
            }
        }
        return toReturn;
    }
    
    public static String sendChangePass(final String changerName, final String name, final String newPass, final int power) {
        String toReturn = "";
        for (final ServerEntry s : Servers.allServers.values()) {
            if (s.id != Servers.localServer.id) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(s.id);
                toReturn += lsw.changePassword(changerName, name, newPass, power);
            }
        }
        return toReturn;
    }
    
    public static String requestDemigod(final byte existingDeity, final String deityName) {
        final String toReturn = "";
        int max = 0;
        for (final ServerEntry s : Servers.allServers.values()) {
            if (s.id != Servers.localServer.id && s.EPIC) {
                ++max;
            }
        }
        if (max > 0) {
            if (Servers.localServer.testServer) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(63500);
                lsw.requestDemigod(existingDeity, deityName);
            }
            else {
                for (final ServerEntry s : Servers.allServers.values()) {
                    if (s.id != Servers.localServer.id && s.EPIC && Server.rand.nextInt(max) == 0) {
                        final LoginServerWebConnection lsw2 = new LoginServerWebConnection(s.id);
                        lsw2.requestDemigod(existingDeity, deityName);
                    }
                }
            }
        }
        return "";
    }
    
    public static String ascend(final int nextDeityId, final String deityname, final long wurmid, final byte existingDeity, final byte gender, final byte newPower, final float initialBStr, final float initialBSta, final float initialBCon, final float initialML, final float initialMS, final float initialSS, final float initialSD) {
        final String toReturn = "";
        for (final ServerEntry s : Servers.allServers.values()) {
            if (s.id != Servers.localServer.id) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(s.id);
                lsw.ascend(nextDeityId, deityname, wurmid, existingDeity, gender, newPower, initialBStr, initialBSta, initialBCon, initialML, initialMS, initialSS, initialSD);
            }
        }
        return "";
    }
    
    public static String changeEmail(final String changerName, final String name, final String newEmail, final String password, final int power, final String pwQuestion, final String pwAnswer) {
        String toReturn = "";
        for (final ServerEntry s : Servers.allServers.values()) {
            if (s.id != Servers.localServer.id) {
                if (s.isAvailable(5, true)) {
                    final LoginServerWebConnection lsw = new LoginServerWebConnection(s.id);
                    toReturn += lsw.changeEmail(changerName, name, newEmail, password, power, pwQuestion, pwAnswer);
                }
                else {
                    toReturn = toReturn + s.name + " was unavailable. ";
                }
            }
        }
        return toReturn;
    }
    
    public static ServerEntry[] getAllServers() {
        return Servers.allServers.values().toArray(new ServerEntry[Servers.allServers.size()]);
    }
    
    public static ServerEntry[] getAllNeighbours() {
        return Servers.neighbours.values().toArray(new ServerEntry[Servers.neighbours.size()]);
    }
    
    public static ServerEntry getEntryServer() {
        final ServerEntry[] allServers;
        final ServerEntry[] alls = allServers = getAllServers();
        for (final ServerEntry lAll : allServers) {
            if (lAll.entryServer) {
                return lAll;
            }
        }
        return null;
    }
    
    public static List<ServerEntry> getServerList(final int desiredNum) {
        final boolean getEpicServers = desiredNum == 100001;
        final boolean getFreedomServers = desiredNum == 100000;
        final boolean getChallengeServers = desiredNum == 100002;
        final List<ServerEntry> lAskedServers = new ArrayList<ServerEntry>();
        for (final ServerEntry lServerEntry : Servers.allServers.values()) {
            if (desiredNum == lServerEntry.id || (getEpicServers && lServerEntry.EPIC != Servers.localServer.EPIC) || (getChallengeServers && lServerEntry.isChallengeServer()) || (getFreedomServers && (!lServerEntry.PVPSERVER || lServerEntry.id == 3))) {
                lAskedServers.add(lServerEntry);
            }
        }
        return lAskedServers;
    }
    
    public static ServerEntry getServerWithId(final int id) {
        if (Servers.loginServer != null && Servers.loginServer.id == id) {
            return Servers.loginServer;
        }
        if (Servers.localServer != null) {
            if (Servers.localServer.serverNorth != null && Servers.localServer.serverNorth.id == id) {
                return Servers.localServer.serverNorth;
            }
            if (Servers.localServer.serverSouth != null && Servers.localServer.serverSouth.id == id) {
                return Servers.localServer.serverSouth;
            }
            if (Servers.localServer.serverWest != null && Servers.localServer.serverWest.id == id) {
                return Servers.localServer.serverWest;
            }
            if (Servers.localServer.serverEast != null && Servers.localServer.serverEast.id == id) {
                return Servers.localServer.serverEast;
            }
        }
        final ServerEntry[] allServers;
        final ServerEntry[] alls = allServers = getAllServers();
        for (final ServerEntry entry : allServers) {
            if (entry.id == id) {
                return entry;
            }
        }
        return null;
    }
    
    public static ServerEntry getClosestSpawnServer(final byte aKingdom) {
        if (Servers.localServer.serverNorth != null && (Servers.localServer.serverNorth.kingdomExists(aKingdom) || Servers.localServer.serverNorth.KINGDOM == aKingdom)) {
            return Servers.localServer.serverNorth;
        }
        if (Servers.localServer.serverSouth != null && (Servers.localServer.serverSouth.kingdomExists(aKingdom) || Servers.localServer.serverSouth.KINGDOM == aKingdom)) {
            return Servers.localServer.serverSouth;
        }
        if (Servers.localServer.serverWest != null && (Servers.localServer.serverWest.kingdomExists(aKingdom) || Servers.localServer.serverWest.KINGDOM == aKingdom)) {
            return Servers.localServer.serverWest;
        }
        if (Servers.localServer.serverEast != null && (Servers.localServer.serverEast.kingdomExists(aKingdom) || Servers.localServer.serverEast.KINGDOM == aKingdom)) {
            return Servers.localServer.serverEast;
        }
        if (Servers.localServer.serverNorth != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverNorth;
        }
        if (Servers.localServer.serverSouth != null && !Servers.localServer.serverSouth.HOMESERVER) {
            return Servers.localServer.serverSouth;
        }
        if (Servers.localServer.serverWest != null && !Servers.localServer.serverWest.HOMESERVER) {
            return Servers.localServer.serverWest;
        }
        if (Servers.localServer.serverEast != null && !Servers.localServer.serverEast.HOMESERVER) {
            return Servers.localServer.serverEast;
        }
        final ServerEntry[] allNeighbours;
        final ServerEntry[] alls = allNeighbours = getAllNeighbours();
        for (final ServerEntry entry : allNeighbours) {
            if (entry.EPIC == Servers.localServer.EPIC && entry.isChallengeServer() == Servers.localServer.isChallengeServer() && (entry.kingdomExists(aKingdom) || entry.KINGDOM == aKingdom)) {
                return entry;
            }
        }
        return null;
    }
    
    public static ServerEntry getClosestJennHomeServer() {
        if (Servers.localServer.serverNorth != null && Servers.localServer.serverNorth.HOMESERVER && Servers.localServer.serverNorth.KINGDOM == 1) {
            return Servers.localServer.serverNorth;
        }
        if (Servers.localServer.serverSouth != null && Servers.localServer.serverSouth.HOMESERVER && Servers.localServer.serverSouth.KINGDOM == 1) {
            return Servers.localServer.serverSouth;
        }
        if (Servers.localServer.serverWest != null && Servers.localServer.serverWest.HOMESERVER && Servers.localServer.serverWest.KINGDOM == 1) {
            return Servers.localServer.serverWest;
        }
        if (Servers.localServer.serverEast != null && Servers.localServer.serverEast.HOMESERVER && Servers.localServer.serverEast.KINGDOM == 1) {
            return Servers.localServer.serverEast;
        }
        final ServerEntry[] allNeighbours;
        final ServerEntry[] alls = allNeighbours = getAllNeighbours();
        for (final ServerEntry entry : allNeighbours) {
            if (entry.HOMESERVER && entry.KINGDOM == 1) {
                return entry;
            }
        }
        if (Servers.localServer.serverNorth != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverNorth;
        }
        if (Servers.localServer.serverSouth != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverSouth;
        }
        if (Servers.localServer.serverWest != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverWest;
        }
        if (Servers.localServer.serverEast != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverEast;
        }
        for (final ServerEntry entry : alls) {
            if (!entry.HOMESERVER) {
                return entry;
            }
        }
        return null;
    }
    
    public static ServerEntry getClosestMolRehanHomeServer() {
        if (Servers.localServer.serverNorth != null && Servers.localServer.serverNorth.HOMESERVER && Servers.localServer.serverNorth.KINGDOM == 2) {
            return Servers.localServer.serverNorth;
        }
        if (Servers.localServer.serverSouth != null && Servers.localServer.serverSouth.HOMESERVER && Servers.localServer.serverSouth.KINGDOM == 2) {
            return Servers.localServer.serverSouth;
        }
        if (Servers.localServer.serverWest != null && Servers.localServer.serverWest.HOMESERVER && Servers.localServer.serverWest.KINGDOM == 2) {
            return Servers.localServer.serverWest;
        }
        if (Servers.localServer.serverEast != null && Servers.localServer.serverEast.HOMESERVER && Servers.localServer.serverEast.KINGDOM == 2) {
            return Servers.localServer.serverEast;
        }
        final ServerEntry[] allNeighbours;
        final ServerEntry[] alls = allNeighbours = getAllNeighbours();
        for (final ServerEntry entry : allNeighbours) {
            if (entry.HOMESERVER && entry.KINGDOM == 2) {
                return entry;
            }
        }
        if (Servers.localServer.serverNorth != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverNorth;
        }
        if (Servers.localServer.serverSouth != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverSouth;
        }
        if (Servers.localServer.serverWest != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverWest;
        }
        if (Servers.localServer.serverEast != null && !Servers.localServer.serverNorth.HOMESERVER) {
            return Servers.localServer.serverEast;
        }
        for (final ServerEntry entry : alls) {
            if (!entry.HOMESERVER) {
                return entry;
            }
        }
        return null;
    }
    
    public static void sendWeather(final float windRotation, final float windpower, final float windDir) {
        final ServerEntry[] allServers;
        final ServerEntry[] alls = allServers = getAllServers();
        for (final ServerEntry entry : allServers) {
            if (entry.id != Servers.localServer.id) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(entry.id);
                lsw.setWeather(windRotation, windpower, windDir);
            }
        }
    }
    
    public static final void pingServers() {
        if (Servers.localServer.serverNorth != null) {
            final boolean pollResult = Servers.localServer.serverNorth.poll();
            if (Servers.logger.isLoggable(Level.FINER)) {
                Servers.logger.finer("Polling north server result: " + pollResult + ", " + Servers.localServer.serverNorth);
            }
        }
        if (Servers.localServer.serverEast != null) {
            final boolean pollResult = Servers.localServer.serverEast.poll();
            if (Servers.logger.isLoggable(Level.FINER)) {
                Servers.logger.finer("Polling east server result: " + pollResult + ", " + Servers.localServer.serverEast);
            }
        }
        if (Servers.localServer.serverSouth != null) {
            final boolean pollResult = Servers.localServer.serverSouth.poll();
            if (Servers.logger.isLoggable(Level.FINER)) {
                Servers.logger.finer("Polling south server result: " + pollResult + ", " + Servers.localServer.serverSouth);
            }
        }
        if (Servers.localServer.serverWest != null) {
            final boolean pollResult = Servers.localServer.serverWest.poll();
            if (Servers.logger.isLoggable(Level.FINER)) {
                Servers.logger.finer("Polling west server result: " + pollResult + ", " + Servers.localServer.serverWest);
            }
        }
        if (!Servers.localServer.LOGINSERVER) {
            if (Servers.loginServer != null) {
                final boolean pollResult = Servers.loginServer.poll();
                if (Servers.logger.isLoggable(Level.FINER)) {
                    Servers.logger.finer("Polling login server result: " + pollResult + ", " + Servers.loginServer);
                }
            }
            for (final ServerEntry portal : Servers.neighbours.values()) {
                if (portal != Servers.localServer.serverEast && portal != Servers.localServer.serverWest && portal != Servers.localServer.serverNorth && portal != Servers.localServer.serverSouth) {
                    portal.poll();
                }
            }
        }
        else {
            for (final ServerEntry entry : Servers.allServers.values()) {
                if (!entry.LOGINSERVER) {
                    final boolean pollResult = entry.poll();
                    if (!Servers.logger.isLoggable(Level.FINER)) {
                        continue;
                    }
                    Servers.logger.finer("Polling server id " + entry.id + " result: " + pollResult + ", " + entry);
                }
            }
        }
    }
    
    public static final void sendKingdomExistsToAllServers(final int serverId, final byte kingdomId, final boolean exists) {
        for (final ServerEntry entry : Servers.allServers.values()) {
            if (entry.isAvailable(5, true) && entry.id != Servers.localServer.id && entry.id != serverId) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(entry.id);
                lsw.kingdomExists(serverId, kingdomId, exists);
            }
        }
    }
    
    public static final void sendWebCommandToAllServers(final short type, final WebCommand command, final boolean restrictEpic) {
        new Thread() {
            @Override
            public void run() {
                for (final ServerEntry entry : Servers.allServers.values()) {
                    if (entry.isAvailable(5, true) && entry.id != Servers.localServer.id && entry.id != WurmId.getOrigin(command.getWurmId()) && (entry.EPIC || !restrictEpic)) {
                        final LoginServerWebConnection lsw = new LoginServerWebConnection(entry.id);
                        lsw.sendWebCommand(type, command);
                    }
                }
            }
        }.start();
    }
    
    public static final boolean kingdomExists(final int serverId, final byte kingdomId, final boolean exists) {
        if (!exists) {
            if (Servers.localServer.serverEast != null && Servers.localServer.serverEast.id == serverId) {
                Servers.localServer.serverEast.removeKingdom(kingdomId);
            }
            if (Servers.localServer.serverWest != null && Servers.localServer.serverWest.id == serverId) {
                Servers.localServer.serverWest.removeKingdom(kingdomId);
            }
            if (Servers.localServer.serverNorth != null && Servers.localServer.serverNorth.id == serverId) {
                Servers.localServer.serverNorth.removeKingdom(kingdomId);
            }
            if (Servers.localServer.serverSouth != null && Servers.localServer.serverSouth.id == serverId) {
                Servers.localServer.serverSouth.removeKingdom(kingdomId);
            }
            for (final ServerEntry portal : Servers.neighbours.values()) {
                if (portal != Servers.localServer.serverEast && portal != Servers.localServer.serverWest && portal != Servers.localServer.serverNorth && portal != Servers.localServer.serverSouth && portal.id == serverId) {
                    portal.removeKingdom(kingdomId);
                }
            }
            final ServerEntry e = Servers.allServers.get(serverId);
            if (e != null) {
                e.removeKingdom(kingdomId);
            }
            if (Servers.localServer.id == serverId) {
                Servers.localServer.removeKingdom(kingdomId);
            }
            if (Servers.loginServer.id == serverId) {
                Servers.loginServer.removeKingdom(kingdomId);
            }
            for (final ServerEntry se : Servers.allServers.values()) {
                if (se.kingdomExists(kingdomId) && se.id != serverId) {
                    return true;
                }
            }
            return false;
        }
        if (Servers.localServer.serverEast != null && Servers.localServer.serverEast.id == serverId) {
            Servers.localServer.serverEast.addExistingKingdom(kingdomId);
        }
        if (Servers.localServer.serverWest != null && Servers.localServer.serverWest.id == serverId) {
            Servers.localServer.serverWest.addExistingKingdom(kingdomId);
        }
        if (Servers.localServer.serverNorth != null && Servers.localServer.serverNorth.id == serverId) {
            Servers.localServer.serverNorth.addExistingKingdom(kingdomId);
        }
        if (Servers.localServer.serverSouth != null && Servers.localServer.serverSouth.id == serverId) {
            Servers.localServer.serverSouth.addExistingKingdom(kingdomId);
        }
        if (Servers.neighbours != null) {
            for (final ServerEntry portal : Servers.neighbours.values()) {
                if (portal != Servers.localServer.serverEast && portal != Servers.localServer.serverWest && portal != Servers.localServer.serverNorth && portal != Servers.localServer.serverSouth && portal.id == serverId) {
                    portal.addExistingKingdom(kingdomId);
                }
            }
        }
        if (Servers.localServer.id == serverId) {
            Servers.localServer.addExistingKingdom(kingdomId);
        }
        if (Servers.loginServer.id == serverId) {
            Servers.loginServer.addExistingKingdom(kingdomId);
        }
        final ServerEntry e = Servers.allServers.get(serverId);
        if (e != null) {
            e.addExistingKingdom(kingdomId);
        }
        else {
            Servers.logger.log(Level.WARNING, "No such server - " + serverId + ", kingdom=" + kingdomId + ", exists: " + exists);
        }
        return true;
    }
    
    public static final void removeKingdomInfo(final byte kingdomId) {
        if (Servers.localServer.serverEast != null) {
            Servers.localServer.serverEast.removeKingdom(kingdomId);
        }
        if (Servers.localServer.serverWest != null) {
            Servers.localServer.serverWest.removeKingdom(kingdomId);
        }
        if (Servers.localServer.serverNorth != null) {
            Servers.localServer.serverNorth.removeKingdom(kingdomId);
        }
        if (Servers.localServer.serverSouth != null) {
            Servers.localServer.serverSouth.removeKingdom(kingdomId);
        }
        for (final ServerEntry portal : Servers.neighbours.values()) {
            if (portal != Servers.localServer.serverEast && portal != Servers.localServer.serverWest && portal != Servers.localServer.serverNorth && portal != Servers.localServer.serverSouth) {
                portal.removeKingdom(kingdomId);
            }
        }
        for (final ServerEntry entry : Servers.allServers.values()) {
            entry.removeKingdom(kingdomId);
        }
    }
    
    public static final int getNumberOfLoyalServers(final int deityId) {
        final int kingdomTemplate = Deities.getFavoredKingdom(deityId);
        int toReturn = 0;
        for (final ServerEntry entry : Servers.allServers.values()) {
            if (!entry.LOGINSERVER && entry.EPIC) {
                if (!entry.HOMESERVER) {
                    ++toReturn;
                }
                else {
                    if (entry.KINGDOM != kingdomTemplate) {
                        continue;
                    }
                    ++toReturn;
                }
            }
        }
        return toReturn;
    }
    
    public static void startShutdown(final String instigator, final int seconds, final String reason) {
        if (isThisLoginServer()) {
            for (final ServerEntry server : getAllServers()) {
                if (server.id != getLocalServerId()) {
                    final LoginServerWebConnection lsw = new LoginServerWebConnection(server.id);
                    lsw.startShutdown(instigator, seconds, reason);
                }
            }
        }
        else {
            final LoginServerWebConnection lsw2 = new LoginServerWebConnection();
            lsw2.startShutdown(instigator, seconds, reason);
        }
    }
    
    public static final boolean mayEnterServer(final Creature _player, final ServerEntry entry) {
        return true;
    }
    
    public static boolean isAvailableDestination(final Creature performer, final ServerEntry entry) {
        return entry.isAvailable(5, true);
    }
    
    public static ServerEntry[] getDestinations(final Creature performer) {
        final ServerEntry[] allServers = getAllServers();
        final List<ServerEntry> servers = new ArrayList<ServerEntry>();
        Arrays.sort(allServers);
        for (final ServerEntry entry : allServers) {
            if (isAvailableDestination(performer, entry)) {
                servers.add(entry);
            }
        }
        servers.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
        return servers.toArray(new ServerEntry[servers.size()]);
    }
    
    public static ServerEntry getDestinationFor(final Creature performer) {
        if (performer.isVehicleCommander() && performer.getVehicle() != -10L) {
            final Vehicle vehicle = Vehicles.getVehicleForId(performer.getVehicle());
            if (vehicle.hasDestinationSet() && mayEnterServer(performer, vehicle.getDestinationServer())) {
                return vehicle.getDestinationServer();
            }
        }
        else if (performer.getDestination() != null) {
            return performer.getDestination();
        }
        return Servers.localServer;
    }
    
    static {
        Servers.arguments = null;
        Servers.neighbours = new ConcurrentHashMap<String, ServerEntry>();
        Servers.allServers = new ConcurrentHashMap<Integer, ServerEntry>();
        logger = Logger.getLogger(Servers.class.getName());
    }
}
