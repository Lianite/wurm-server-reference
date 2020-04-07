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
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class DbGuard extends Guard
{
    private static final Logger logger;
    private static final String SET_EXPIREDATE = "UPDATE GUARDS SET EXPIREDATE=? WHERE WURMID=?";
    private static final String CREATE_GUARD = "INSERT INTO GUARDS (WURMID, VILLAGEID, EXPIREDATE ) VALUES (?,?,?)";
    static final String DELETE_GUARD = "DELETE FROM GUARDS WHERE WURMID=?";
    
    DbGuard(final int aVillageId, final Creature aCreature, final long aExpireDate) {
        super(aVillageId, aCreature, aExpireDate);
    }
    
    @Override
    void save() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO GUARDS (WURMID, VILLAGEID, EXPIREDATE ) VALUES (?,?,?)");
            ps.setLong(1, this.creature.getWurmId());
            ps.setInt(2, this.villageId);
            ps.setLong(3, this.expireDate);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbGuard.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void setExpireDate(final long newDate) {
        if (this.expireDate != newDate) {
            this.expireDate = newDate;
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getZonesDbCon();
                ps = dbcon.prepareStatement("UPDATE GUARDS SET EXPIREDATE=? WHERE WURMID=?");
                ps.setLong(1, this.expireDate);
                ps.setLong(2, this.creature.getWurmId());
                ps.executeUpdate();
            }
            catch (SQLException sqx) {
                DbGuard.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    @Override
    void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM GUARDS WHERE WURMID=?");
            ps.setLong(1, this.creature.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbGuard.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(DbGuard.class.getName());
    }
}
