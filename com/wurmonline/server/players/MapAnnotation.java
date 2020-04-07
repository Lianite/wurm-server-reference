// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.math.BigInteger;
import com.wurmonline.server.WurmId;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public final class MapAnnotation
{
    private static final Logger logger;
    public static final byte PRIVATE_POI = 0;
    public static final byte VILLAGE_POI = 1;
    public static final byte ALLIANCE_POI = 2;
    public static final short MAX_PRIVATE_ANNOTATIONS = 500;
    public static final short MAX_VILLAGE_ANNOTATIONS = 500;
    public static final short MAX_ALLIANCE_ANNOTATIONS = 500;
    private long id;
    private String name;
    private byte type;
    private long position;
    private long ownerId;
    private String server;
    private byte icon;
    private static final String CREATE_NEW_POI = "INSERT INTO MAP_ANNOTATIONS (ID, NAME, POSITION, POITYPE, OWNERID, SERVER, ICON) VALUES ( ?, ?, ?, ?, ?, ?, ? );";
    private static final String DELETE_POI = "DELETE FROM MAP_ANNOTATIONS WHERE ID=?;";
    private static final String DELETE_ALL_PRIVATE_ANNOTATIONS_BY_OWNER = "DELETE FROM MAP_ANNOTATIONS WHERE OWNERID=? AND POITYPE=0;";
    
    public static final MapAnnotation createNew(final long id, final String _name, final byte _type, final long _position, final long _ownerId, final String _server, final byte _icon) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MAP_ANNOTATIONS (ID, NAME, POSITION, POITYPE, OWNERID, SERVER, ICON) VALUES ( ?, ?, ?, ?, ?, ?, ? );");
            ps.setLong(1, id);
            ps.setString(2, _name);
            ps.setLong(3, _position);
            ps.setByte(4, _type);
            ps.setLong(5, _ownerId);
            ps.setString(6, _server);
            ps.setByte(7, _icon);
            ps.executeUpdate();
            final MapAnnotation poi = new MapAnnotation(id, _name, _type, _position, _ownerId, _server, _icon);
            return poi;
        }
        catch (SQLException sqx) {
            MapAnnotation.logger.log(Level.WARNING, "Failed to create POI: " + _name + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final MapAnnotation createNew(final String _name, final byte _type, final long _position, final long _ownerId, final String _server, final byte _icon) throws IOException {
        final long id = WurmId.getNextPoiId();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MAP_ANNOTATIONS (ID, NAME, POSITION, POITYPE, OWNERID, SERVER, ICON) VALUES ( ?, ?, ?, ?, ?, ?, ? );");
            ps.setLong(1, id);
            ps.setString(2, _name);
            ps.setLong(3, _position);
            ps.setByte(4, _type);
            ps.setLong(5, _ownerId);
            ps.setString(6, _server);
            ps.setByte(7, _icon);
            ps.executeUpdate();
            final MapAnnotation poi = new MapAnnotation(id, _name, _type, _position, _ownerId, _server, _icon);
            return poi;
        }
        catch (SQLException sqx) {
            MapAnnotation.logger.log(Level.WARNING, "Failed to create POI: " + _name + ": " + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void deleteAnnotation(final long id) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MAP_ANNOTATIONS WHERE ID=?;");
            ps.setLong(1, id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MapAnnotation.logger.log(Level.WARNING, "Failed to delete POI: " + id + " :" + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void deletePrivateAnnotationsForOwner(final long ownerId) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MAP_ANNOTATIONS WHERE OWNERID=? AND POITYPE=0;");
            ps.setLong(1, ownerId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            MapAnnotation.logger.log(Level.WARNING, "Failed to delete POI's for owner: " + ownerId + " :" + sqx.getMessage(), sqx);
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public MapAnnotation(final long _id) {
        this.id = _id;
    }
    
    public MapAnnotation(final long _id, final String _name, final byte _type, final long _position, final long _ownerId, final String _server, final byte _icon) {
        this.id = _id;
        this.name = _name;
        this.type = _type;
        this.position = _position;
        this.ownerId = _ownerId;
        this.server = _server;
        this.icon = _icon;
    }
    
    public final byte getIcon() {
        return this.icon;
    }
    
    public final long getId() {
        return this.id;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final long getOwnerId() {
        return this.ownerId;
    }
    
    public final long getPosition() {
        return this.position;
    }
    
    public final String getServer() {
        return this.server;
    }
    
    public final byte getType() {
        return this.type;
    }
    
    public final int getXPos() {
        return BigInteger.valueOf(this.position).shiftRight(32).intValue();
    }
    
    public final int getYPos() {
        return BigInteger.valueOf(this.position).intValue();
    }
    
    public void setIcon(final byte _icon) {
        this.icon = _icon;
    }
    
    public void setName(final String _name) {
        this.name = _name;
    }
    
    public void setOwnerId(final long _ownerId) {
        this.ownerId = _ownerId;
    }
    
    public void setPosition(final int x, final int y) {
        final long pos = x;
        this.position = BigInteger.valueOf(pos).shiftLeft(32).longValue() + y;
    }
    
    public void setPosition(final long _position) {
        this.position = _position;
    }
    
    public void setServer(final String _server) {
        this.server = _server;
    }
    
    public void setType(final byte _type) {
        this.type = _type;
    }
    
    static {
        logger = Logger.getLogger(MapAnnotation.class.getName());
    }
}
