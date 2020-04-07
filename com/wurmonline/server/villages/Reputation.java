// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.Players;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.NoSuchPlayerException;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Reputation implements MiscConstants, Comparable<Reputation>
{
    private static final String DELETEREPUTATION = "DELETE FROM REPUTATION WHERE WURMID=? AND VILLAGEID=?";
    private static final String UPDATEREPUTATION = "UPDATE REPUTATION SET REPUTATION=?,PERMANENT=? WHERE WURMID=? AND VILLAGEID=?";
    private static final String CREATEREPUTATION = "INSERT INTO REPUTATION (REPUTATION,PERMANENT, WURMID,VILLAGEID) VALUES(?,?,?,?)";
    private final long wurmid;
    private final int villageId;
    private boolean permanent;
    private byte value;
    private static final Logger logger;
    private final boolean guest;
    
    Reputation(final long wurmId, final int village, final boolean perma, int val, final boolean isGuest, final boolean loading) {
        this.permanent = false;
        this.value = 0;
        this.wurmid = wurmId;
        this.villageId = village;
        this.permanent = perma;
        if (val > 100) {
            val = 100;
        }
        else if (val < -100) {
            val = -100;
        }
        this.value = (byte)val;
        this.guest = isGuest;
        if (!loading) {
            this.create();
        }
    }
    
    @Override
    public int compareTo(final Reputation otherReputation) {
        try {
            return this.getNameFor().compareTo(otherReputation.getNameFor());
        }
        catch (NoSuchPlayerException e) {
            return 0;
        }
    }
    
    public int getValue() {
        return this.value;
    }
    
    public boolean isGuest() {
        return this.guest;
    }
    
    void setValue(int val, final boolean override) {
        if (val > 100) {
            val = 100;
        }
        else if (val < -100) {
            val = -100;
        }
        if (!this.permanent || override) {
            this.value = (byte)val;
        }
        if (this.value == 0) {
            this.delete();
        }
        else {
            this.update();
        }
    }
    
    public long getWurmId() {
        return this.wurmid;
    }
    
    public Creature getCreature() {
        Creature toReturn = null;
        try {
            toReturn = Server.getInstance().getCreature(this.wurmid);
        }
        catch (NoSuchPlayerException ex) {}
        catch (NoSuchCreatureException ex2) {}
        return toReturn;
    }
    
    public String getNameFor() throws NoSuchPlayerException {
        String name = "Unknown";
        if (this.guest) {
            name += " guest";
        }
        if (WurmId.getType(this.wurmid) == 0) {
            try {
                name = Players.getInstance().getNameFor(this.wurmid);
            }
            catch (IOException iox) {
                Reputation.logger.log(Level.WARNING, iox.getMessage(), iox);
                name = "";
            }
        }
        else {
            name += " creature";
        }
        return name;
    }
    
    public Village getVillage() {
        Village toReturn = null;
        try {
            toReturn = Villages.getVillage(this.villageId);
        }
        catch (NoSuchVillageException nsv) {
            Reputation.logger.log(Level.WARNING, "No village for reputation with wurmid " + this.wurmid + " and villageid " + this.villageId, nsv);
        }
        return toReturn;
    }
    
    public boolean isPermanent() {
        return this.permanent;
    }
    
    public void setPermanent(final boolean perma) {
        this.permanent = perma;
        this.update();
    }
    
    void modify(final int val) {
        if (val != 0 && !this.permanent) {
            this.value += (byte)val;
            if (this.value > 100) {
                this.value = 100;
            }
            else if (this.value < -100) {
                this.value = -100;
            }
            if (this.value == 0) {
                this.delete();
            }
            else {
                this.update();
            }
        }
    }
    
    void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM REPUTATION WHERE WURMID=? AND VILLAGEID=?");
            ps.setLong(1, this.wurmid);
            ps.setInt(2, this.villageId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Reputation.logger.log(Level.WARNING, "Failed to delete reputation for wurmid=" + this.wurmid + ", village with id=" + this.villageId, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void create() {
        if (!this.guest) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("INSERT INTO REPUTATION (REPUTATION,PERMANENT, WURMID,VILLAGEID) VALUES(?,?,?,?)");
                ps.setByte(1, this.value);
                ps.setBoolean(2, this.permanent);
                ps.setLong(3, this.wurmid);
                ps.setInt(4, this.villageId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Reputation.logger.log(Level.WARNING, "Failed to create reputation for wurmid=" + this.wurmid + ", village with id=" + this.villageId, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    private void update() {
        if (!this.guest) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE REPUTATION SET REPUTATION=?,PERMANENT=? WHERE WURMID=? AND VILLAGEID=?");
                ps.setByte(1, this.value);
                ps.setBoolean(2, this.permanent);
                ps.setLong(3, this.wurmid);
                ps.setInt(4, this.villageId);
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                Reputation.logger.log(Level.WARNING, "Failed to update reputation for wurmid=" + this.wurmid + ", village with id=" + this.villageId, sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(Reputation.class.getName());
    }
}
