// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.endgames;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MiscConstants;

public final class EndGameItem implements MiscConstants, ItemTypes
{
    private static final String DELETE = "DELETE FROM ENDGAMEITEMS WHERE WURMID=?";
    private static final String CREATE = "INSERT INTO ENDGAMEITEMS (WURMID,TYPE,HOLY) VALUES(?,?,?)";
    private static final String SETLASTMOVED = "UPDATE ENDGAMEITEMS SET LASTMOVED=? WHERE WURMID=?";
    private static final Logger logger;
    private final long wurmid;
    private final Item item;
    private final boolean holy;
    private final short type;
    long lastMoved;
    
    public EndGameItem(final Item aItem, final boolean aHoly, final short aType, final boolean aCreate) {
        this.lastMoved = System.currentTimeMillis();
        this.item = aItem;
        this.holy = aHoly;
        this.type = aType;
        this.wurmid = aItem.getWurmId();
        if (aCreate) {
            this.create();
        }
    }
    
    public boolean isInWorld() {
        return this.item.getZoneId() != -10 || this.item.getOwnerId() != -10L;
    }
    
    private void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO ENDGAMEITEMS (WURMID,TYPE,HOLY) VALUES(?,?,?)");
            ps.setLong(1, this.wurmid);
            ps.setShort(2, this.type);
            ps.setBoolean(3, this.holy);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EndGameItem.logger.log(Level.WARNING, "Failed to create endgameitem " + this.wurmid + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void destroy() {
        this.delete();
    }
    
    public long getLastMoved() {
        return this.lastMoved;
    }
    
    void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ENDGAMEITEMS WHERE WURMID=?");
            ps.setLong(1, this.wurmid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EndGameItem.logger.log(Level.WARNING, "Failed to delete endgameitem " + this.wurmid + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static void delete(final long wurmId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM ENDGAMEITEMS WHERE WURMID=?");
            ps.setLong(1, wurmId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EndGameItem.logger.log(Level.WARNING, "Failed to delete endgameitem " + wurmId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void setLastMoved(final long lastm) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.lastMoved = lastm;
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("UPDATE ENDGAMEITEMS SET LASTMOVED=? WHERE WURMID=?");
            ps.setLong(1, this.lastMoved);
            ps.setLong(2, this.wurmid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            EndGameItem.logger.log(Level.WARNING, "Failed to set last moved " + this.wurmid + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public long getWurmid() {
        return this.wurmid;
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public boolean isHoly() {
        return this.holy;
    }
    
    public short getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return "EndGameItem [ID: " + this.wurmid + ", holy: " + this.holy + ", type: " + this.type + ", inWorld: " + this.isInWorld() + ", item: " + this.item + ']';
    }
    
    static {
        logger = Logger.getLogger(EndGameItem.class.getName());
    }
}
