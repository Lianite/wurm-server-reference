// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.players.Permissions;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.creatures.MineDoorSettings;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PermissionsByPlayer;
import com.wurmonline.server.Servers;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.logging.Level;
import com.wurmonline.server.players.PermissionsPlayerList;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class DoorSettings implements MiscConstants
{
    private static final Logger logger;
    private static final String GET_ALL_SETTINGS = "SELECT * FROM DOORSETTINGS";
    private static final String ADD_PLAYER = "INSERT INTO DOORSETTINGS (SETTINGS,WURMID,PLAYERID) VALUES(?,?,?)";
    private static final String DELETE_SETTINGS = "DELETE FROM DOORSETTINGS WHERE WURMID=?";
    private static final String REMOVE_PLAYER = "DELETE FROM DOORSETTINGS WHERE WURMID=? AND PLAYERID=?";
    private static final String UPDATE_PLAYER = "UPDATE DOORSETTINGS SET SETTINGS=? WHERE WURMID=? AND PLAYERID=?";
    private static final int MAX_PLAYERS_PER_OBJECT = 1000;
    private static Map<Long, PermissionsPlayerList> objectSettings;
    
    public static void loadAll() throws IOException {
        DoorSettings.logger.log(Level.INFO, "Loading all door (and gate) settings.");
        final long start = System.nanoTime();
        long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM DOORSETTINGS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmId = rs.getLong("WURMID");
                final long playerId = rs.getLong("PLAYERID");
                final int settings = rs.getInt("SETTINGS");
                add(wurmId, playerId, settings);
                ++count;
            }
        }
        catch (SQLException ex) {
            DoorSettings.logger.log(Level.WARNING, "Failed to load settings for doors (and gates).", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            DoorSettings.logger.log(Level.INFO, "Loaded " + count + " door (and gate) settings. That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    public static int getMaxAllowed() {
        return Servers.isThisATestServer() ? 10 : 1000;
    }
    
    private static PermissionsByPlayer add(final long wurmId, final long playerId, final int settings) {
        final Long id = wurmId;
        if (DoorSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = DoorSettings.objectSettings.get(id);
            return ppl.add(playerId, settings);
        }
        final PermissionsPlayerList ppl = new PermissionsPlayerList();
        DoorSettings.objectSettings.put(id, ppl);
        return ppl.add(playerId, settings);
    }
    
    public static void addPlayer(final long wurmId, final long playerId, final int settings) {
        final PermissionsByPlayer pbp = add(wurmId, playerId, settings);
        if (pbp == null) {
            dbAddPlayer(wurmId, playerId, settings, true);
        }
        else if (pbp.getSettings() != settings) {
            dbAddPlayer(wurmId, playerId, settings, false);
        }
    }
    
    public static void removePlayer(final long wurmId, final long playerId) {
        final Long id = wurmId;
        if (DoorSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = DoorSettings.objectSettings.get(id);
            ppl.remove(playerId);
            dbRemovePlayer(wurmId, playerId);
            if (ppl.isEmpty()) {
                DoorSettings.objectSettings.remove(id);
            }
        }
    }
    
    private static void dbAddPlayer(final long wurmId, final long playerId, final int settings, final boolean add) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (add) {
                ps = dbcon.prepareStatement("INSERT INTO DOORSETTINGS (SETTINGS,WURMID,PLAYERID) VALUES(?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE DOORSETTINGS SET SETTINGS=? WHERE WURMID=? AND PLAYERID=?");
            }
            ps.setInt(1, settings);
            ps.setLong(2, wurmId);
            ps.setLong(3, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DoorSettings.logger.log(Level.WARNING, "Failed to " + (add ? "add" : "update") + " player (" + playerId + ") for door with id " + wurmId, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemovePlayer(final long wurmId, final long playerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM DOORSETTINGS WHERE WURMID=? AND PLAYERID=?");
            ps.setLong(1, wurmId);
            ps.setLong(2, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DoorSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for door " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean exists(final long wurmId) {
        final Long id = wurmId;
        return DoorSettings.objectSettings.containsKey(id);
    }
    
    public static void remove(final long wurmId) {
        final Long id = wurmId;
        if (DoorSettings.objectSettings.containsKey(id)) {
            dbRemove(wurmId);
            DoorSettings.objectSettings.remove(id);
        }
    }
    
    private static void dbRemove(final long wurmId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM DOORSETTINGS WHERE WURMID=?");
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            DoorSettings.logger.log(Level.WARNING, "Failed to delete settings for door " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static PermissionsPlayerList getPermissionsPlayerList(final long wurmId) {
        final Long id = wurmId;
        final PermissionsPlayerList ppl = DoorSettings.objectSettings.get(id);
        if (ppl == null) {
            return new PermissionsPlayerList();
        }
        return ppl;
    }
    
    private static boolean hasPermission(final PermissionsPlayerList.ISettings is, final Creature creature, final int bit) {
        if (is.isOwner(creature)) {
            return bit != MineDoorSettings.MinedoorPermissions.EXCLUDE.getBit();
        }
        final Long id = is.getWurmId();
        final PermissionsPlayerList ppl = DoorSettings.objectSettings.get(id);
        if (ppl == null) {
            return false;
        }
        if (ppl.exists(creature.getWurmId())) {
            return ppl.getPermissionsFor(creature.getWurmId()).hasPermission(bit);
        }
        if (is.isCitizen(creature) && ppl.exists(-30L)) {
            return ppl.getPermissionsFor(-30L).hasPermission(bit);
        }
        if (is.isAllied(creature) && ppl.exists(-20L)) {
            return ppl.getPermissionsFor(-20L).hasPermission(bit);
        }
        if (is.isSameKingdom(creature) && ppl.exists(-40L)) {
            return ppl.getPermissionsFor(-40L).hasPermission(bit);
        }
        return ppl.exists(-50L) && ppl.getPermissionsFor(-50L).hasPermission(bit);
    }
    
    public static boolean isGuest(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return isGuest(is, creature.getWurmId());
    }
    
    public static boolean isGuest(final PermissionsPlayerList.ISettings is, final long playerId) {
        if (is.isOwner(playerId)) {
            return true;
        }
        final Long id = is.getWurmId();
        final PermissionsPlayerList ppl = DoorSettings.objectSettings.get(id);
        return ppl != null && ppl.exists(playerId);
    }
    
    public static boolean canManage(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return hasPermission(is, creature, GatePermissions.MANAGE.getBit());
    }
    
    public static boolean mayPass(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, DoorPermissions.PASS.getBit());
    }
    
    public static boolean mayLock(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, GatePermissions.LOCK.getBit());
    }
    
    public static boolean isExcluded(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() <= 1 && hasPermission(is, creature, DoorPermissions.EXCLUDE.getBit());
    }
    
    static {
        logger = Logger.getLogger(DoorSettings.class.getName());
        DoorSettings.objectSettings = new ConcurrentHashMap<Long, PermissionsPlayerList>();
    }
    
    public enum DoorPermissions implements Permissions.IPermission
    {
        PASS(1, "Pass Door", "Pass", "Door", "Allows entry through this door even when its locked."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private DoorPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
            this.bit = (byte)aBit;
            this.description = aDescription;
            this.header1 = aHeader1;
            this.header2 = aHeader2;
            this.hover = aHover;
        }
        
        @Override
        public byte getBit() {
            return this.bit;
        }
        
        @Override
        public int getValue() {
            return 1 << this.bit;
        }
        
        @Override
        public String getDescription() {
            return this.description;
        }
        
        @Override
        public String getHeader1() {
            return this.header1;
        }
        
        @Override
        public String getHeader2() {
            return this.header2;
        }
        
        @Override
        public String getHover() {
            return this.hover;
        }
        
        public static Permissions.IPermission[] getPermissions() {
            return DoorPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum GatePermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        PASS(1, "Pass Gate", "Pass", "Gate", "Allows entry through this gate even when its locked."), 
        LOCK(2, "(Un)Lock Gate", "(Un)Lock", "Gate", "Allows locking (and unlocking) of this gate."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private GatePermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
            this.bit = (byte)aBit;
            this.description = aDescription;
            this.header1 = aHeader1;
            this.header2 = aHeader2;
            this.hover = aHover;
        }
        
        @Override
        public byte getBit() {
            return this.bit;
        }
        
        @Override
        public int getValue() {
            return 1 << this.bit;
        }
        
        @Override
        public String getDescription() {
            return this.description;
        }
        
        @Override
        public String getHeader1() {
            return this.header1;
        }
        
        @Override
        public String getHeader2() {
            return this.header2;
        }
        
        @Override
        public String getHover() {
            return this.hover;
        }
        
        public static Permissions.IPermission[] getPermissions() {
            return GatePermissions.types;
        }
        
        static {
            types = values();
        }
    }
}
