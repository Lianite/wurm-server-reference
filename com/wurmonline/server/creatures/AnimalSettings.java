// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.players.Permissions;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import com.wurmonline.server.villages.Village;
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

public class AnimalSettings implements MiscConstants
{
    private static final Logger logger;
    private static final String GET_ALL_SETTINGS = "SELECT * FROM ANIMALSETTINGS";
    private static final String ADD_PLAYER = "INSERT INTO ANIMALSETTINGS (SETTINGS,WURMID,PLAYERID) VALUES(?,?,?)";
    private static final String DELETE_SETTINGS = "DELETE FROM ANIMALSETTINGS WHERE WURMID=?";
    private static final String REMOVE_PLAYER = "DELETE FROM ANIMALSETTINGS WHERE WURMID=? AND PLAYERID=?";
    private static final String UPDATE_PLAYER = "UPDATE ANIMALSETTINGS SET SETTINGS=? WHERE WURMID=? AND PLAYERID=?";
    private static int MAX_PLAYERS_PER_OBJECT;
    private static Map<Long, PermissionsPlayerList> objectSettings;
    
    public static void loadAll() throws IOException {
        AnimalSettings.logger.log(Level.INFO, "Loading all animal settings.");
        final long start = System.nanoTime();
        long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ANIMALSETTINGS");
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
            AnimalSettings.logger.log(Level.WARNING, "Failed to load settings for animals.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            AnimalSettings.logger.log(Level.INFO, "Loaded " + count + " animal settings. That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    public static int getMaxAllowed() {
        return Servers.isThisATestServer() ? 10 : AnimalSettings.MAX_PLAYERS_PER_OBJECT;
    }
    
    private static PermissionsByPlayer add(final long wurmId, final long playerId, final int settings) {
        final Long id = wurmId;
        if (AnimalSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = AnimalSettings.objectSettings.get(id);
            return ppl.add(playerId, settings);
        }
        final PermissionsPlayerList ppl = new PermissionsPlayerList();
        AnimalSettings.objectSettings.put(id, ppl);
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
        if (AnimalSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = AnimalSettings.objectSettings.get(id);
            ppl.remove(playerId);
            dbRemovePlayer(wurmId, playerId);
            if (ppl.isEmpty()) {
                AnimalSettings.objectSettings.remove(id);
            }
        }
        else {
            AnimalSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for animal " + wurmId + ".");
        }
    }
    
    private static void dbAddPlayer(final long wurmId, final long playerId, final int settings, final boolean add) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            if (add) {
                ps = dbcon.prepareStatement("INSERT INTO ANIMALSETTINGS (SETTINGS,WURMID,PLAYERID) VALUES(?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE ANIMALSETTINGS SET SETTINGS=? WHERE WURMID=? AND PLAYERID=?");
            }
            ps.setInt(1, settings);
            ps.setLong(2, wurmId);
            ps.setLong(3, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            AnimalSettings.logger.log(Level.WARNING, "Failed to " + (add ? "add" : "update") + " player (" + playerId + ") for animal with id " + wurmId, ex);
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
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ANIMALSETTINGS WHERE WURMID=? AND PLAYERID=?");
            ps.setLong(1, wurmId);
            ps.setLong(2, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            AnimalSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for animal " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean exists(final long wurmId) {
        final Long id = wurmId;
        return AnimalSettings.objectSettings.containsKey(id);
    }
    
    public static void remove(final long wurmId) {
        final Long id = wurmId;
        if (AnimalSettings.objectSettings.containsKey(id)) {
            dbRemove(wurmId);
            AnimalSettings.objectSettings.remove(id);
        }
    }
    
    private static void dbRemove(final long wurmId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ANIMALSETTINGS WHERE WURMID=?");
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            AnimalSettings.logger.log(Level.WARNING, "Failed to delete settings for animal " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static PermissionsPlayerList getPermissionsPlayerList(final long wurmId) {
        final Long id = wurmId;
        final PermissionsPlayerList ppl = AnimalSettings.objectSettings.get(id);
        if (ppl == null) {
            return new PermissionsPlayerList();
        }
        return ppl;
    }
    
    private static boolean hasPermission(final PermissionsPlayerList.ISettings is, final Creature creature, @Nullable final Village brandVillage, final int bit) {
        if (is.isOwner(creature)) {
            return bit != MineDoorSettings.MinedoorPermissions.EXCLUDE.getBit();
        }
        final Long id = is.getWurmId();
        final PermissionsPlayerList ppl = AnimalSettings.objectSettings.get(id);
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
        if (brandVillage != null && brandVillage.isActionAllowed((short)484, creature) && ppl.exists(-60L)) {
            return ppl.getPermissionsFor(-60L).hasPermission(bit);
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
        final PermissionsPlayerList ppl = AnimalSettings.objectSettings.get(id);
        return ppl != null && ppl.exists(playerId);
    }
    
    public static boolean canManage(final PermissionsPlayerList.ISettings is, final Creature creature, @Nullable final Village brandVillage) {
        return hasPermission(is, creature, brandVillage, Animal2Permissions.MANAGE.getBit());
    }
    
    public static boolean mayCommand(final PermissionsPlayerList.ISettings is, final Creature creature, @Nullable final Village brandVillage) {
        return creature.getPower() > 1 || hasPermission(is, creature, brandVillage, Animal2Permissions.COMMANDER.getBit());
    }
    
    public static boolean mayPassenger(final PermissionsPlayerList.ISettings is, final Creature creature, @Nullable final Village brandVillage) {
        return creature.getPower() > 1 || hasPermission(is, creature, brandVillage, Animal2Permissions.PASSENGER.getBit());
    }
    
    public static boolean mayAccessHold(final PermissionsPlayerList.ISettings is, final Creature creature, @Nullable final Village brandVillage) {
        return creature.getPower() > 1 || hasPermission(is, creature, brandVillage, Animal2Permissions.ACCESS_HOLD.getBit());
    }
    
    public static boolean mayUse(final PermissionsPlayerList.ISettings is, final Creature creature, @Nullable final Village brandVillage) {
        return creature.getPower() > 1 || hasPermission(is, creature, brandVillage, WagonerPermissions.CANUSE.getBit());
    }
    
    public static boolean publicMayUse(final PermissionsPlayerList.ISettings is) {
        final Long id = is.getWurmId();
        final PermissionsPlayerList ppl = AnimalSettings.objectSettings.get(id);
        return ppl != null && ppl.exists(-50L) && ppl.getPermissionsFor(-50L).hasPermission(WagonerPermissions.CANUSE.getBit());
    }
    
    public static boolean isExcluded(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() <= 1 && hasPermission(is, creature, null, Animal2Permissions.EXCLUDE.getBit());
    }
    
    static {
        logger = Logger.getLogger(AnimalSettings.class.getName());
        AnimalSettings.MAX_PLAYERS_PER_OBJECT = 1000;
        AnimalSettings.objectSettings = new ConcurrentHashMap<Long, PermissionsPlayerList>();
    }
    
    public enum Animal0Permissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Lead", "Allows leading of this animal."), 
        ACCESS_HOLD(3, "Manage Equipment", "Manage", "Equipment", "Allows adding or removing equipement from the animal without taming it."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private Animal0Permissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return Animal0Permissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum Animal1Permissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Ride", "Allows leading and riding of this animal."), 
        ACCESS_HOLD(3, "Manage Equipment", "Manage", "Equipment", "Allows adding or removing equipement from the animal without taming it."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private Animal1Permissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return Animal1Permissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum Animal2Permissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Ride", "Allows leading and riding of this animal."), 
        PASSENGER(2, "Passenger", "Can be", "Passenger", "Allows being a passenger on this animal."), 
        ACCESS_HOLD(3, "Manage Equipment", "Manage", "Equipment", "Allows adding or removing equipement from the animal without taming it."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private Animal2Permissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return Animal2Permissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum WagonerPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        CANUSE(6, "Can Use", "Can", "Use", "Allows sending of bulk items using this NPC."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private WagonerPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return WagonerPermissions.types;
        }
        
        static {
            types = values();
        }
    }
}
