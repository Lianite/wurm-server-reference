// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

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

public class ItemSettings implements MiscConstants
{
    private static final Logger logger;
    private static final String GET_ALL_SETTINGS = "SELECT * FROM ITEMSETTINGS";
    private static final String ADD_PLAYER = "INSERT INTO ITEMSETTINGS (SETTINGS,WURMID,PLAYERID) VALUES(?,?,?)";
    private static final String DELETE_SETTINGS = "DELETE FROM ITEMSETTINGS WHERE WURMID=?";
    private static final String REMOVE_PLAYER = "DELETE FROM ITEMSETTINGS WHERE WURMID=? AND PLAYERID=?";
    private static final String UPDATE_PLAYER = "UPDATE ITEMSETTINGS SET SETTINGS=? WHERE WURMID=? AND PLAYERID=?";
    private static int MAX_PLAYERS_PER_OBJECT;
    private static Map<Long, PermissionsPlayerList> objectSettings;
    
    public static void loadAll() throws IOException {
        ItemSettings.logger.log(Level.INFO, "Loading all item settings.");
        final long start = System.nanoTime();
        long count = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ITEMSETTINGS");
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
            ItemSettings.logger.log(Level.WARNING, "Failed to load settings for items.", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            ItemSettings.logger.log(Level.INFO, "Loaded " + count + " item settings. That took " + (end - start) / 1000000.0f + " ms.");
        }
    }
    
    public static int getMaxAllowed() {
        return Servers.isThisATestServer() ? 10 : ItemSettings.MAX_PLAYERS_PER_OBJECT;
    }
    
    private static PermissionsByPlayer add(final long wurmId, final long playerId, final int settings) {
        final Long id = wurmId;
        if (ItemSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = ItemSettings.objectSettings.get(id);
            return ppl.add(playerId, settings);
        }
        final PermissionsPlayerList ppl = new PermissionsPlayerList();
        ItemSettings.objectSettings.put(id, ppl);
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
        if (ItemSettings.objectSettings.containsKey(id)) {
            final PermissionsPlayerList ppl = ItemSettings.objectSettings.get(id);
            ppl.remove(playerId);
            dbRemovePlayer(wurmId, playerId);
            if (ppl.isEmpty()) {
                ItemSettings.objectSettings.remove(id);
            }
        }
        else {
            ItemSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for item " + wurmId + ".");
        }
    }
    
    private static void dbAddPlayer(final long wurmId, final long playerId, final int settings, final boolean add) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            if (add) {
                ps = dbcon.prepareStatement("INSERT INTO ITEMSETTINGS (SETTINGS,WURMID,PLAYERID) VALUES(?,?,?)");
            }
            else {
                ps = dbcon.prepareStatement("UPDATE ITEMSETTINGS SET SETTINGS=? WHERE WURMID=? AND PLAYERID=?");
            }
            ps.setInt(1, settings);
            ps.setLong(2, wurmId);
            ps.setLong(3, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemSettings.logger.log(Level.WARNING, "Failed to " + (add ? "add" : "update") + " player (" + playerId + ") for item with id " + wurmId, ex);
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
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ITEMSETTINGS WHERE WURMID=? AND PLAYERID=?");
            ps.setLong(1, wurmId);
            ps.setLong(2, playerId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemSettings.logger.log(Level.WARNING, "Failed to remove player " + playerId + " from settings for item " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static boolean exists(final long wurmId) {
        final Long id = wurmId;
        return ItemSettings.objectSettings.containsKey(id);
    }
    
    public static void remove(final long wurmId) {
        final Long id = wurmId;
        if (ItemSettings.objectSettings.containsKey(id)) {
            dbRemove(wurmId);
            ItemSettings.objectSettings.remove(id);
        }
    }
    
    private static void dbRemove(final long wurmId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        final ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ITEMSETTINGS WHERE WURMID=?");
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            ItemSettings.logger.log(Level.WARNING, "Failed to delete settings for item " + wurmId + ".", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static PermissionsPlayerList getPermissionsPlayerList(final long wurmId) {
        final Long id = wurmId;
        final PermissionsPlayerList ppl = ItemSettings.objectSettings.get(id);
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
        final PermissionsPlayerList ppl = ItemSettings.objectSettings.get(id);
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
        final PermissionsPlayerList ppl = ItemSettings.objectSettings.get(id);
        return ppl != null && ppl.exists(playerId);
    }
    
    public static boolean canManage(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return hasPermission(is, creature, VehiclePermissions.MANAGE.getBit());
    }
    
    public static boolean mayCommand(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, VehiclePermissions.COMMANDER.getBit());
    }
    
    public static boolean mayPassenger(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, VehiclePermissions.PASSENGER.getBit());
    }
    
    public static boolean mayAccessHold(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, VehiclePermissions.ACCESS_HOLD.getBit());
    }
    
    public static boolean mayUseBed(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, BedPermissions.MAY_USE_BED.getBit());
    }
    
    public static boolean mayFreeSleep(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, BedPermissions.FREE_SLEEP.getBit());
    }
    
    public static boolean mayDrag(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, VehiclePermissions.DRAG.getBit());
    }
    
    public static boolean mayPostNotices(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, MessageBoardPermissions.MAY_POST_NOTICES.getBit());
    }
    
    public static boolean mayAddPMs(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() > 1 || hasPermission(is, creature, MessageBoardPermissions.MAY_ADD_PMS.getBit());
    }
    
    public static boolean isExcluded(final PermissionsPlayerList.ISettings is, final Creature creature) {
        return creature.getPower() <= 1 && hasPermission(is, creature, VehiclePermissions.EXCLUDE.getBit());
    }
    
    static {
        logger = Logger.getLogger(ItemSettings.class.getName());
        ItemSettings.MAX_PLAYERS_PER_OBJECT = 1000;
        ItemSettings.objectSettings = new ConcurrentHashMap<Long, PermissionsPlayerList>();
    }
    
    public enum GMItemPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Command", "Allows commanding of this vehicle."), 
        PASSENGER(2, "Passenger", "Can be", "Passenger", "Allows being a passenger of this vehicle."), 
        ACCESS_HOLD(3, "Access Hold", "Access", "Hold", "Allows acces to the hold."), 
        MAY_USE_BED(4, "Can Sleep", "Can", "Sleep", ""), 
        FREE_SLEEP(5, "Free Use", "Free", "Use", ""), 
        DRAG(6, "Drag", "May", "Drag", "Allows the Vehicle to be dragged."), 
        MAY_POST_NOTICES(7, "Notices", "May Post", "Notices", "Allows notices to be posted."), 
        MAY_ADD_PMS(8, "PMs", "May Add", "PMs", "Allows PMs to be added."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private GMItemPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return GMItemPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum VehiclePermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Command", "Allows commanding of this vehicle."), 
        PASSENGER(2, "Passenger", "Can be", "Passenger", "Allows being a passenger of this vehicle."), 
        ACCESS_HOLD(3, "Access Hold", "Access", "Hold", "Allows acces to the hold."), 
        DRAG(6, "Drag", "May", "Drag", "Allows the Vehicle to be dragged."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private VehiclePermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return VehiclePermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum SmallCartPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        ACCESS_HOLD(3, "Access Hold", "Access", "Hold", "Allows acces to the hold."), 
        DRAG(6, "Drag", "May", "Drag", "Allows the Vehicle to be dragged."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private SmallCartPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return SmallCartPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum WagonPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Command", "Allows commanding of this vehicle."), 
        PASSENGER(2, "Passenger", "Can be", "Passenger", "Allows being a passenger of this vehicle."), 
        ACCESS_HOLD(3, "Access Hold", "Access", "Hold", "Allows acces to the hold."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private WagonPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return WagonPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum ShipTransporterPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Command", "Allows commanding of this vehicle."), 
        ACCESS_HOLD(3, "Access Hold", "Access", "Hold", "Allows acces to the hold."), 
        DRAG(6, "Drag", "May", "Drag", "Allows the Vehicle to be dragged."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private ShipTransporterPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return ShipTransporterPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum CreatureTransporterPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        COMMANDER(1, "Commander", "Can", "Command", "Allows commanding of this vehicle."), 
        PASSENGER(2, "Passenger", "Can be", "Passenger", "Allows being a passenger of this vehicle."), 
        ACCESS_HOLD(3, "Access Hold", "Access", "Hold", "Allows acces to the hold."), 
        DRAG(6, "Drag", "May", "Drag", "Allows the Vehicle to be dragged."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private CreatureTransporterPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return CreatureTransporterPermissions.types;
        }
        
        static {
            types = ShipTransporterPermissions.values();
        }
    }
    
    public enum ItemPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        ACCESS_HOLD(3, "Access Item", "May", "Open", "Allows acces to this container."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private ItemPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return ItemPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum BedPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        MAY_USE_BED(4, "May Use Bed", "May Use", "Bed", "Allows acess to use this bed."), 
        FREE_SLEEP(5, "Free Sleep", "Free", "Sleep", "Allows this bed to be used for free (requires 'May Use Bed' as well)."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private BedPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return BedPermissions.types;
        }
        
        static {
            types = values();
        }
    }
    
    public enum MessageBoardPermissions implements Permissions.IPermission
    {
        MANAGE(0, "Manage Item", "Manage", "Item", "Allows managing of these permissions."), 
        MANAGE_NOTICES(1, "Manage Notices", "Manage", "Notices", "Allows managing of any notices."), 
        ACCESS_HOLD(3, "Access Item", "May View", "Messages", "Allows viewing of mesages on this board."), 
        MAY_POST_NOTICES(7, "Notices", "May Post", "Notices", "Allows notices to be posted."), 
        MAY_ADD_PMS(8, "PMs", "May Add", "PMs", "Allows PMs to be added."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private MessageBoardPermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return MessageBoardPermissions.types;
        }
        
        static {
            types = ItemPermissions.values();
        }
    }
    
    public enum CorpsePermissions implements Permissions.IPermission
    {
        COMMANDER(1, "Commander", "Can", "Access", "Allows looting of this corpse."), 
        EXCLUDE(15, "Deny All", "Deny", "All", "Deny all access.");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Permissions.IPermission[] types;
        
        private CorpsePermissions(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
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
            return CorpsePermissions.types;
        }
        
        static {
            types = ItemPermissions.values();
        }
    }
}
