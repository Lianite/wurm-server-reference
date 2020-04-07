// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

import com.wurmonline.server.items.ItemTemplate;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.logging.Level;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;

public final class LocalSupplyDemand
{
    private final Map<Integer, Float> demandList;
    private final long traderId;
    private static final Logger logger;
    private static final float MAX_DEMAND = -200.0f;
    private static final float INITIAL_DEMAND = -100.0f;
    private static final float MIN_DEMAND = -0.001f;
    private static final String GET_ALL_ITEM_DEMANDS = "SELECT * FROM LOCALSUPPLYDEMAND WHERE TRADERID=?";
    private static final String UPDATE_DEMAND = "UPDATE LOCALSUPPLYDEMAND SET DEMAND=? WHERE ITEMID=? AND TRADERID=?";
    private static final String INCREASE_ALL_DEMANDS;
    private static final String CREATE_DEMAND = "INSERT INTO LOCALSUPPLYDEMAND (DEMAND,ITEMID,TRADERID) VALUES(?,?,?)";
    
    LocalSupplyDemand(final long aTraderId) {
        this.traderId = aTraderId;
        this.demandList = new HashMap<Integer, Float>();
        this.loadAllItemDemands();
        this.createUnexistingDemands();
    }
    
    double getPrice(final int itemTemplateId, final double basePrice, final int nums, final boolean selling) {
        final Float dem = this.demandList.get(itemTemplateId);
        float demand = -100.0f;
        if (dem != null) {
            demand = dem;
        }
        double price = 1.0;
        float halfSize = 100.0f;
        try {
            halfSize = ItemTemplateFactory.getInstance().getTemplate(itemTemplateId).priceHalfSize;
        }
        catch (NoSuchTemplateException nst) {
            LocalSupplyDemand.logger.log(Level.WARNING, nst.getMessage(), nst);
        }
        for (int x = 0; x < nums; ++x) {
            if (selling) {
                price = basePrice * Math.max(0.20000000298023224, Math.pow(demand / halfSize, 2.0));
                demand = Math.max(-200.0f, demand - 1.0f);
            }
            else {
                demand = Math.min(-0.001f, demand + 1.0f);
                price = basePrice * Math.pow(demand / halfSize, 2.0);
            }
        }
        return Math.max(0.0, price);
    }
    
    public void addItemSold(final int itemTemplateId, final float times) {
        final Float dem = this.demandList.get(itemTemplateId);
        float demand = -100.0f;
        if (dem != null) {
            demand = dem;
        }
        demand -= times;
        demand = Math.max(-200.0f, demand);
        this.demandList.put(itemTemplateId, new Float(demand));
        if (dem == null) {
            this.createDemand(itemTemplateId, demand);
        }
        else {
            this.updateDemand(itemTemplateId, demand);
        }
    }
    
    public void addItemPurchased(final int itemTemplateId, final float times) {
        final Float dem = this.demandList.get(itemTemplateId);
        float demand = -100.0f;
        if (dem != null) {
            demand = dem;
        }
        demand = Math.min(-0.001f, demand + times);
        this.demandList.put(itemTemplateId, new Float(demand));
        if (dem == null) {
            this.createDemand(itemTemplateId, demand);
        }
        else {
            this.updateDemand(itemTemplateId, demand);
        }
    }
    
    public void lowerDemands() {
        final ItemDemand[] itemDemands;
        final ItemDemand[] dems = itemDemands = this.getItemDemands();
        for (final ItemDemand lDem : itemDemands) {
            lDem.setDemand(Math.max(-200.0f, lDem.getDemand() * 1.1f));
            this.demandList.put(lDem.getTemplateId(), new Float(lDem.getDemand()));
        }
    }
    
    public ItemDemand[] getItemDemands() {
        ItemDemand[] dems = new ItemDemand[0];
        if (this.demandList.size() > 0) {
            dems = new ItemDemand[this.demandList.size()];
            int x = 0;
            for (final Map.Entry<Integer, Float> entry : this.demandList.entrySet()) {
                final int item = entry.getKey();
                final float demand = entry.getValue();
                dems[x] = new ItemDemand(item, demand);
                ++x;
            }
        }
        return dems;
    }
    
    private void loadAllItemDemands() {
        final long start = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM LOCALSUPPLYDEMAND WHERE TRADERID=?");
            ps.setLong(1, this.traderId);
            rs = ps.executeQuery();
            while (rs.next()) {
                this.demandList.put(rs.getInt("ITEMID"), new Float(Math.min(-0.001f, rs.getFloat("DEMAND"))));
            }
        }
        catch (SQLException sqx) {
            LocalSupplyDemand.logger.log(Level.WARNING, "Failed to load supplyDemand for trader " + this.traderId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            if (LocalSupplyDemand.logger.isLoggable(Level.FINER)) {
                final long end = System.currentTimeMillis();
                LocalSupplyDemand.logger.finer("Loading LocalSupplyDemand for Trader: " + this.traderId + " took " + (end - start) + " ms");
            }
        }
    }
    
    private void createUnexistingDemands() {
        final ItemTemplate[] templates2;
        final ItemTemplate[] templates = templates2 = ItemTemplateFactory.getInstance().getTemplates();
        for (final ItemTemplate lTemplate : templates2) {
            if (lTemplate.isPurchased()) {
                final Float dem = this.demandList.get(lTemplate.getTemplateId());
                if (dem == null) {
                    this.createDemand(lTemplate.getTemplateId(), -100.0f);
                    this.demandList.put(lTemplate.getTemplateId(), -100.0f);
                }
            }
        }
    }
    
    private void updateDemand(final int itemId, final float demand) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("UPDATE LOCALSUPPLYDEMAND SET DEMAND=? WHERE ITEMID=? AND TRADERID=?");
            ps.setFloat(1, Math.min(-0.001f, demand));
            ps.setInt(2, itemId);
            ps.setLong(3, this.traderId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            LocalSupplyDemand.logger.log(Level.WARNING, "Failed to update trader " + this.traderId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static void increaseAllDemands() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement(LocalSupplyDemand.INCREASE_ALL_DEMANDS);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            LocalSupplyDemand.logger.log(Level.WARNING, "Failed to increase all demands due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void createDemand(final int itemId, final float demand) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("INSERT INTO LOCALSUPPLYDEMAND (DEMAND,ITEMID,TRADERID) VALUES(?,?,?)");
            ps.setFloat(1, Math.min(-0.001f, demand));
            ps.setInt(2, itemId);
            ps.setLong(3, this.traderId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            LocalSupplyDemand.logger.log(Level.WARNING, "Failed to update trader " + this.traderId + ": " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(LocalSupplyDemand.class.getName());
        INCREASE_ALL_DEMANDS = (DbConnector.isUseSqlite() ? "UPDATE LOCALSUPPLYDEMAND SET DEMAND=MAX(-200.0,DEMAND*1.1)" : "UPDATE LOCALSUPPLYDEMAND SET DEMAND=GREATEST(-200.0,DEMAND*1.1)");
    }
}
