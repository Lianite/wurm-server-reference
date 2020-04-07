// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.players.Permissions;
import java.util.concurrent.ConcurrentHashMap;
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

public class MineDoorSettings implements MiscConstants
{
    private static final Logger logger;
    private static final String GET_ALL_SETTINGS = "SELECT * FROM MDPERMS";
    private static final String ADD_PLAYER = "INSERT INTO MDPERMS (SETTINGS,ID,PERMITTED) VALUES(?,?,?)";
    private static final String DELETE_SETTINGS = "DELETE FROM MDPERMS WHERE ID=?";
    private static final String REMOVE_PLAYER = "DELETE FROM MDPERMS WHERE ID=? AND PERMITTED=?";
    private static final String UPDATE_PLAYER = "UPDATE MDPERMS SET SETTINGS=? WHERE ID=? AND PERMITTED=?";
    private static int MAX_PLAYERS_PER_OBJECT;
    private static Map<Long, PermissionsPlayerList> objectSettings;
    
    public static void loadAll() throws IOException {
        MineDoorSettings.logger.log(Level.INFO, "Loading all minedoor settings.");
        final long start = System.nanoTime();
        long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MDPERMS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int minedoorId = rs.getInt("ID");
                final long playerId = rs.getLong("PERMITTED");
                int settings = rs.getInt("SETTINGS");
                if (settings == 0) {
                    settings = MinedoorPermissions.PASS.getValue();
                }
                add(minedoorId, playerId, settings);
                ++count;
            }
        }
        catch (SQLException ex) {
            MineDoorSettings.logger.log(Level.WARNING, "Failed to load settings for minedoors.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            MineDoorSettings.logger.log(Level.INFO, "Loaded " + count + " minedoor settings. That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    public static int getMaxAllowed() {
        return Servers.isThisATestServer() ? 10 : MineDoorSettings.MAX_PLAYERS_PER_OBJECT;
    }
    
    private static PermissionsByPlayer add(final int minedoorId, final long playerId, final int settings) {
        final Long id = (Long)minedoorId;
        if (MineDoorSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = MineDoorSettings.objectSettings.get(id);
            return ppl.add(playerId, settings);
        }
        final PermissionsPlayerList ppl = new PermissionsPlayerList();
        MineDoorSettings.objectSettings.put(id, ppl);
        return ppl.add(playerId, settings);
    }
    
    public static void addPlayer(final int minedoorId, final long playerId, final int settings) {
        final PermissionsByPlayer pbp = add(minedoorId, playerId, settings);
        if (pbp == null) {
            dbAddPlayer(minedoorId, playerId, settings, true);
        }
        else if (pbp.getSettings() != settings) {
            dbAddPlayer(minedoorId, playerId, settings, false);
        }
    }
    
    public static void removePlayer(final int minedoorId, final long playerId) {
        final Long id = (Long)minedoorId;
        if (MineDoorSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = MineDoorSettings.objectSettings.get(id);
            ppl.remove(playerId);
            dbRemovePlayer(minedoorId, playerId);
            if (ppl.isEmpty()) {
                MineDoorSettings.objectSettings.remove(id);
            }
        }
        else {
            MineDoorSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for minedoor " + minedoorId + ".");
        }
    }
    
    private static void dbAddPlayer(final int minedoorId, final long playerId, final int settings, final boolean add) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (add) {
                ps = dbcon.prepareStatement("INSERT INTO MDPERMS (SETTINGS,ID,PERMITTED) VALUES(?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE MDPERMS SET SETTINGS=? WHERE ID=? AND PERMITTED=?");
            }
            ps.setInt(1, settings);
            ps.setInt(2, minedoorId);
            ps.setLong(3, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            MineDoorSettings.logger.log(Level.WARNING, "Failed to " + (add ? "add" : "update") + " player (" + playerId + ") for minedoor with id " + minedoorId, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbRemovePlayer(final int minedoorId, final long playerId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MDPERMS WHERE ID=? AND PERMITTED=?");
            ps.setInt(1, minedoorId);
            ps.setLong(2, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            MineDoorSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for minedoor " + minedoorId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean exists(final long wurmId) {
        final Long id = wurmId;
        return MineDoorSettings.objectSettings.containsKey(id);
    }
    
    public static void remove(final int minedoorId) {
        final Long id = (Long)minedoorId;
        if (MineDoorSettings.objectSettings.containsKey(id)) {
            dbRemove(minedoorId);
            MineDoorSettings.objectSettings.remove(id);
        }
    }
    
    private static void dbRemove(final int minedoorId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MDPERMS WHERE ID=?");
            ps.setInt(1, minedoorId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            MineDoorSettings.logger.log(Level.WARNING, "Failed to delete settings for minedoor " + minedoorId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static PermissionsPlayerList getPermissionsPlayerList(final long wurmId) {
        final Long id = wurmId;
        final PermissionsPlayerList ppl = MineDoorSettings.objectSettings.get(id);
        if (ppl == null) {
            return new PermissionsPlayerList();
        }
        return ppl;
    }
    
    private static boolean hasPermission(final PermissionsPlayerList.ISettings is, final long objectId, final Creature creature, final int bit) {
        if (is.isOwner(creature)) {
            return bit != MinedoorPermissions.EXCLUDE.getBit();
        }
        final Long id = objectId;
        final PermissionsPlayerList ppl = MineDoorSettings.objectSettings.get(id);
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
        final PermissionsPlayerList ppl = MineDoorSettings.objectSettings.get(id);
        return ppl != null && ppl.exists(playerId);
    }
    
    public static boolean canManage(final PermissionsPlayerList.ISettings is, final long objectId, final Creature creature) {
        return hasPermission(is, objectId, creature, MinedoorPermissions.MANAGE.getBit());
    }
    
    public static boolean mayPass(final PermissionsPlayerList.ISettings is, final long objectId, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, objectId, creature, MinedoorPermissions.PASS.getBit());
    }
    
    public static boolean isExcluded(final PermissionsPlayerList.ISettings is, final long objectId, final Creature creature) {
        return creature.getPower() <= 1 && hasPermission(is, objectId, creature, MinedoorPermissions.EXCLUDE.getBit());
    }
    
    static {
        logger = Logger.getLogger(MineDoorSettings.class.getName());
        MineDoorSettings.MAX_PLAYERS_PER_OBJECT = 1000;
        MineDoorSettings.objectSettings = new ConcurrentHashMap<Long, PermissionsPlayerList>();
    }
    
    public enum MinedoorPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        PASS(1, "Pass Door", "Pass", "Mine Door", "Allows entry through this mine door."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.Allow[] types;
        
        private MinedoorPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return MinedoorPermissions.types;
        }
        
        static {
            types = Permissions.Allow.values();
        }
    }
}
