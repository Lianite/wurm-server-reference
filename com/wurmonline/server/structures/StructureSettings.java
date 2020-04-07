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

public final class StructureSettings implements MiscConstants
{
    private static final Logger logger;
    private static final String GET_ALL_SETTINGS = "SELECT * FROM STRUCTUREGUESTS";
    private static final String ADD_PLAYER = "INSERT INTO STRUCTUREGUESTS (SETTINGS,STRUCTUREID,GUESTID) VALUES(?,?,?)";
    private static final String DELETE_SETTINGS = "DELETE FROM STRUCTUREGUESTS WHERE STRUCTUREID=?";
    private static final String REMOVE_PLAYER = "DELETE FROM STRUCTUREGUESTS WHERE STRUCTUREID=? AND GUESTID=?";
    private static final String UPDATE_PLAYER = "UPDATE STRUCTUREGUESTS SET SETTINGS=? WHERE STRUCTUREID=? AND GUESTID=?";
    private static int MAX_PLAYERS_PER_OBJECT;
    private static Map<Long, PermissionsPlayerList> objectSettings;
    
    public static void loadAll() throws IOException {
        StructureSettings.logger.log(Level.INFO, "Loading all structure settings.");
        final long start = System.nanoTime();
        long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM STRUCTUREGUESTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmId = rs.getLong("STRUCTUREID");
                final long guestId = rs.getLong("GUESTID");
                int settings = rs.getInt("SETTINGS");
                if (settings == 0) {
                    settings = StructurePermissions.PASS.getValue() + StructurePermissions.LOAD.getValue() + StructurePermissions.MODIFY.getValue() + StructurePermissions.PICKUP.getValue() + StructurePermissions.PLACE_MERCHANTS.getValue();
                }
                add(wurmId, guestId, settings);
                ++count;
            }
        }
        catch (SQLException ex) {
            StructureSettings.logger.log(Level.WARNING, "Failed to load settings for structures.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            StructureSettings.logger.log(Level.INFO, "Loaded " + count + " structure settings (guests). That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    public static int getMaxAllowed() {
        return Servers.isThisATestServer() ? 10 : StructureSettings.MAX_PLAYERS_PER_OBJECT;
    }
    
    private static PermissionsByPlayer add(final long wurmId, final long playerId, final int settings) {
        final Long id = wurmId;
        if (StructureSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = StructureSettings.objectSettings.get(id);
            return ppl.add(playerId, settings);
        }
        final PermissionsPlayerList ppl = new PermissionsPlayerList();
        StructureSettings.objectSettings.put(id, ppl);
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
        if (StructureSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = StructureSettings.objectSettings.get(id);
            ppl.remove(playerId);
            dbRemovePlayer(wurmId, playerId);
            if (ppl.isEmpty()) {
                StructureSettings.objectSettings.remove(id);
            }
        }
        else {
            StructureSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for structure " + wurmId + ".");
        }
    }
    
    private static void dbAddPlayer(final long wurmId, final long playerId, final int settings, final boolean add) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (add) {
                ps = dbcon.prepareStatement("INSERT INTO STRUCTUREGUESTS (SETTINGS,STRUCTUREID,GUESTID) VALUES(?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE STRUCTUREGUESTS SET SETTINGS=? WHERE STRUCTUREID=? AND GUESTID=?");
            }
            ps.setInt(1, settings);
            ps.setLong(2, wurmId);
            ps.setLong(3, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            StructureSettings.logger.log(Level.WARNING, "Failed to " + (add ? "add" : "update") + " player (" + playerId + ") for structure with id " + wurmId, ex);
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
            ps = dbcon.prepareStatement("DELETE FROM STRUCTUREGUESTS WHERE STRUCTUREID=? AND GUESTID=?");
            ps.setLong(1, wurmId);
            ps.setLong(2, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            StructureSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for structure " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean exists(final long wurmId) {
        final Long id = wurmId;
        return StructureSettings.objectSettings.containsKey(id);
    }
    
    public static void remove(final long wurmId) {
        final Long id = wurmId;
        if (StructureSettings.objectSettings.containsKey(id)) {
            dbRemove(wurmId);
            StructureSettings.objectSettings.remove(id);
        }
    }
    
    private static void dbRemove(final long wurmId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM STRUCTUREGUESTS WHERE STRUCTUREID=?");
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            StructureSettings.logger.log(Level.WARNING, "Failed to delete settings for structure " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static PermissionsPlayerList getPermissionsPlayerList(final long wurmId) {
        final Long id = wurmId;
        final PermissionsPlayerList ppl = StructureSettings.objectSettings.get(id);
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
        final PermissionsPlayerList ppl = StructureSettings.objectSettings.get(id);
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
        final PermissionsPlayerList ppl = StructureSettings.objectSettings.get(id);
        return ppl != null && ppl.exists(playerId) && !ppl.getPermissionsFor(playerId).hasPermission(StructurePermissions.EXCLUDE.getBit());
    }
    
    public static boolean canManage(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return hasPermission(is, creature, StructurePermissions.MANAGE.getBit());
    }
    
    public static boolean mayModify(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, StructurePermissions.MODIFY.getBit());
    }
    
    public static boolean mayPass(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, StructurePermissions.PASS.getBit());
    }
    
    public static boolean mayPickup(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, StructurePermissions.PICKUP.getBit());
    }
    
    public static boolean mayPickupPlanted(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, StructurePermissions.PICKUP_PLANTED.getBit());
    }
    
    public static boolean mayPlaceMerchants(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, StructurePermissions.PLACE_MERCHANTS.getBit());
    }
    
    public static boolean mayLoad(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, StructurePermissions.LOAD.getBit());
    }
    
    public static boolean isExcluded(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() <= 1 && hasPermission(is, creature, StructurePermissions.EXCLUDE.getBit());
    }
    
    static {
        logger = Logger.getLogger(StructureSettings.class.getName());
        StructureSettings.MAX_PLAYERS_PER_OBJECT = 1000;
        StructureSettings.objectSettings = new ConcurrentHashMap<Long, PermissionsPlayerList>();
    }
    
    public enum StructurePermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        PASS(1, "May Enter", "May", "Enter", "Allows entry even through locked doors (if door is Controlled By Building) and the abililty to improve and repair the building and items inside."), 
        MODIFY(2, "Modify Building", "Modify", "Building", "Allows destroying, building and rotating of floors, roofs and walls - needs deed permissions to add or remove tiles."), 
        PICKUP(3, "Pickup Items", "Pickup", "Items", "Allows picking up of items and Pull/Push/Turn (overrides deed setting), also allows Hauling Up and Down of items."), 
        PICKUP_PLANTED(4, "Pickup Planted", "Pickup", "Planted", "Allows picking up of planted items (overrides deed setting). Requires 'Pickup Items' as well."), 
        PLACE_MERCHANTS(5, "Place Merchants", "Place", "Merchants", "Allows planting of merchants and traders (overrides deed setting)."), 
        LOAD(6, "May Load", "May", "(Un)Load", "Allows (Un)loading of items (overrides deed setting). Requires 'Pickup Items' to load items they dont own, will also requires 'Pickup Planted' if item is planted."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        private final byte bit;
        private final String description;
        private final String header1;
        private final String header2;
        private final String hover;
        private static final Permissions.IPermission[] types;
        
        private StructurePermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return StructurePermissions.types;
        }
        
        static {
            types = values();
        }
    }
}
