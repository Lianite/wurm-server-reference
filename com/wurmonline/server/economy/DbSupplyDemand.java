// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

final class DbSupplyDemand extends SupplyDemand
{
    private static final Logger logger;
    private static final String UPDATE_BOUGHT_ITEMS = "UPDATE SUPPLYDEMAND SET ITEMSBOUGHT=? WHERE ID=?";
    private static final String UPDATE_SOLD_ITEMS = "UPDATE SUPPLYDEMAND SET ITEMSSOLD=? WHERE ID=?";
    private static final String CHECK_SUPLLY_DEMAND = "SELECT ID FROM SUPPLYDEMAND WHERE ID=?";
    private static final String RESET_SUPPLY_DEMAND = "DELETE FROM SUPPLYDEMAND WHERE ID=?";
    private static final String CREATE_SUPPLY_DEMAND = "INSERT INTO SUPPLYDEMAND (ID, ITEMSBOUGHT,ITEMSSOLD, LASTPOLLED) VALUES(?,?,?,?)";
    
    DbSupplyDemand(final int aId, final int aItemsBought, final int aItemsSold) {
        super(aId, aItemsBought, aItemsSold);
    }
    
    DbSupplyDemand(final int aId, final int aItemsBought, final int aItemsSold, final long aLastPolled) {
        super(aId, aItemsBought, aItemsSold, aLastPolled);
    }
    
    @Override
    boolean supplyDemandExists() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT ID FROM SUPPLYDEMAND WHERE ID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            return rs.next();
        }
        catch (SQLException sqx) {
            DbSupplyDemand.logger.log(Level.WARNING, "Failed to check if supplyDemandExists for ID: " + this.id + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return false;
    }
    
    @Override
    void updateItemsBoughtByTraders(final int items) {
        if (this.supplyDemandExists()) {
            if (this.itemsBought != items) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    this.itemsBought = items;
                    dbcon = DbConnector.getEconomyDbCon();
                    ps = dbcon.prepareStatement("UPDATE SUPPLYDEMAND SET ITEMSBOUGHT=? WHERE ID=?");
                    ps.setInt(1, this.itemsBought);
                    ps.setInt(2, this.id);
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    DbSupplyDemand.logger.log(Level.WARNING, "Failed to update supplyDemand with ID: " + this.id + ", items: " + items + " due to " + sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
        else {
            this.createSupplyDemand(1000 + items, 1000);
        }
    }
    
    @Override
    void updateItemsSoldByTraders(final int items) {
        if (this.supplyDemandExists()) {
            if (this.itemsSold != items) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    this.itemsSold = items;
                    dbcon = DbConnector.getEconomyDbCon();
                    ps = dbcon.prepareStatement("UPDATE SUPPLYDEMAND SET ITEMSSOLD=? WHERE ID=?");
                    ps.setInt(1, this.itemsSold);
                    ps.setInt(2, this.id);
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    DbSupplyDemand.logger.log(Level.WARNING, "Failed to update supplyDemand with ID: " + this.id + ", items: " + items + " due to " + sqx.getMessage(), sqx);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
        else {
            this.createSupplyDemand(1000, 1000 + items);
        }
    }
    
    @Override
    void createSupplyDemand(final int aItemsBought, final int aItemsSold) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.lastPolled = System.currentTimeMillis();
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("INSERT INTO SUPPLYDEMAND (ID, ITEMSBOUGHT,ITEMSSOLD, LASTPOLLED) VALUES(?,?,?,?)");
            ps.setInt(1, this.id);
            ps.setInt(2, aItemsBought);
            ps.setInt(3, aItemsSold);
            ps.setLong(4, this.lastPolled);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbSupplyDemand.logger.log(Level.WARNING, "Failed to create supplyDemand with ID: " + this.id + ", itemsBought: " + aItemsBought + ", itemsSold: " + aItemsSold + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void reset(final long time) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            this.itemsBought = Math.max(1000, (int)(this.itemsBought * 0.99));
            this.itemsSold = Math.max(1000, (int)(this.itemsSold * 0.99));
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("DELETE FROM SUPPLYDEMAND WHERE ID=?");
            ps.setInt(1, this.id);
            ps.executeUpdate();
            this.lastPolled = time;
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("INSERT INTO SUPPLYDEMAND (ID, ITEMSBOUGHT,ITEMSSOLD, LASTPOLLED) VALUES(?,?,?,?)");
            ps.setInt(1, this.id);
            ps.setInt(2, this.itemsBought);
            ps.setInt(3, this.itemsSold);
            ps.setLong(4, this.lastPolled);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbSupplyDemand.logger.log(Level.WARNING, "Failed to reset supplyDemand with ID: " + this.id + ", time: " + time + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(DbSupplyDemand.class.getName());
    }
}
