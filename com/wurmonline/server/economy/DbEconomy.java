// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.economy;

import com.wurmonline.server.Servers;
import com.wurmonline.server.items.Item;
import java.util.List;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.io.IOException;
import java.util.logging.Logger;

public final class DbEconomy extends Economy
{
    private static final Logger logger;
    private static final String createEconomy = "insert into ECONOMY(ID, GOLDCOINS, SILVERCOINS, COPPERCOINS, IRONCOINS)values(?,?,?,?,?)";
    private static final String getEconomy = "SELECT * FROM ECONOMY WHERE ID=?";
    private static final String updateLastPolledTraders = "UPDATE ECONOMY SET LASTPOLLED=? WHERE ID=?";
    private static final String updateCreatedGold = "UPDATE ECONOMY SET GOLDCOINS=? WHERE ID=?";
    private static final String updateCreatedSilver = "UPDATE ECONOMY SET SILVERCOINS=? WHERE ID=?";
    private static final String updateCreatedCopper = "UPDATE ECONOMY SET COPPERCOINS=? WHERE ID=?";
    private static final String updateCreatedIron = "UPDATE ECONOMY SET IRONCOINS=? WHERE ID=?";
    private static final String logSoldItem = "INSERT INTO ITEMSSOLD (ITEMNAME,ITEMVALUE,TRADERNAME,PLAYERNAME, TEMPLATEID) VALUES(?,?,?,?,?)";
    private static final String getCoins = "SELECT * FROM COINS WHERE TEMPLATEID=? AND OWNERID=-10 AND PARENTID=-10 AND ZONEID=-10 AND BANKED=1 AND MAILED=0";
    private static final String getSupplyDemand = "SELECT * FROM SUPPLYDEMAND";
    private static final String getTraderMoney = "SELECT * FROM TRADER";
    private static final String createTransaction = "INSERT INTO TRANSACTS (ITEMID, OLDOWNERID,NEWOWNERID,REASON, VALUE) VALUES (?,?,?,?,?)";
    
    DbEconomy(final int serverNumber) throws IOException {
        super(serverNumber);
    }
    
    @Override
    void initialize() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            if (this.exists(dbcon)) {
                this.load();
            }
            else {
                ps = dbcon.prepareStatement("insert into ECONOMY(ID, GOLDCOINS, SILVERCOINS, COPPERCOINS, IRONCOINS)values(?,?,?,?,?)");
                ps.setInt(1, this.id);
                ps.setLong(2, DbEconomy.goldCoins);
                ps.setLong(3, DbEconomy.silverCoins);
                ps.setLong(4, DbEconomy.copperCoins);
                ps.setLong(5, DbEconomy.ironCoins);
                ps.executeUpdate();
            }
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        this.loadSupplyDemand();
        this.loadShopMoney();
    }
    
    private void load() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM ECONOMY WHERE ID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            if (rs.next()) {
                DbEconomy.goldCoins = rs.getLong("GOLDCOINS");
                DbEconomy.silverCoins = rs.getLong("SILVERCOINS");
                DbEconomy.copperCoins = rs.getLong("COPPERCOINS");
                DbEconomy.ironCoins = rs.getLong("IRONCOINS");
                DbEconomy.lastPolledTraders = rs.getLong("LASTPOLLED");
            }
            DbUtilities.closeDatabaseObjects(ps, rs);
            final Change change = new Change(DbEconomy.ironCoins + DbEconomy.copperCoins * 100L + DbEconomy.silverCoins * 10000L + DbEconomy.goldCoins * 1000000L);
            if (DbEconomy.lastPolledTraders <= 0L) {
                DbEconomy.lastPolledTraders = System.currentTimeMillis() - 2419200000L;
            }
            this.updateCreatedIron(change.getIronCoins());
            DbEconomy.logger.log(Level.INFO, "Iron=" + DbEconomy.ironCoins);
            this.updateCreatedCopper(change.getCopperCoins());
            DbEconomy.logger.log(Level.INFO, "Copper=" + DbEconomy.copperCoins);
            this.updateCreatedSilver(change.getSilverCoins());
            DbEconomy.logger.log(Level.INFO, "Silver=" + DbEconomy.silverCoins);
            this.updateCreatedGold(change.getGoldCoins());
            DbEconomy.logger.log(Level.INFO, "Gold=" + DbEconomy.goldCoins);
            this.loadCoins(50);
            this.loadCoins(54);
            this.loadCoins(58);
            this.loadCoins(53);
            this.loadCoins(57);
            this.loadCoins(61);
            this.loadCoins(51);
            this.loadCoins(55);
            this.loadCoins(59);
            this.loadCoins(52);
            this.loadCoins(56);
            this.loadCoins(60);
        }
        catch (SQLException sqx) {
            throw new IOException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private boolean exists(final Connection dbcon) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = dbcon.prepareStatement("SELECT * FROM ECONOMY WHERE ID=?");
            ps.setInt(1, this.id);
            rs = ps.executeQuery();
            return rs.next();
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
    }
    
    @Override
    public void transaction(final long itemId, final long oldownerid, final long newownerid, final String newReason, final long value) {
        if (DbConnector.isUseSqlite()) {
            return;
        }
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            final String reason = newReason.substring(0, Math.min(19, newReason.length()));
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("INSERT INTO TRANSACTS (ITEMID, OLDOWNERID,NEWOWNERID,REASON, VALUE) VALUES (?,?,?,?,?)");
            ps.setLong(1, itemId);
            ps.setLong(2, oldownerid);
            ps.setLong(3, newownerid);
            ps.setString(4, reason);
            ps.setLong(5, value);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to create transaction for itemId: " + itemId + " due to " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void updateCreatedGold(final long number) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            DbEconomy.goldCoins = number;
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("UPDATE ECONOMY SET GOLDCOINS=? WHERE ID=?");
            ps.setLong(1, DbEconomy.goldCoins);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to update num gold: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void updateLastPolled() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            DbEconomy.lastPolledTraders = System.currentTimeMillis();
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("UPDATE ECONOMY SET LASTPOLLED=? WHERE ID=?");
            ps.setLong(1, DbEconomy.lastPolledTraders);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to update last polled traders: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void updateCreatedSilver(final long number) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            DbEconomy.silverCoins = number;
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("UPDATE ECONOMY SET SILVERCOINS=? WHERE ID=?");
            ps.setLong(1, DbEconomy.silverCoins);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to update num silver: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void updateCreatedCopper(final long number) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            DbEconomy.copperCoins = number;
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("UPDATE ECONOMY SET COPPERCOINS=? WHERE ID=?");
            ps.setLong(1, DbEconomy.copperCoins);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to update num copper: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void updateCreatedIron(final long number) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            DbEconomy.ironCoins = number;
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("UPDATE ECONOMY SET IRONCOINS=? WHERE ID=?");
            ps.setLong(1, DbEconomy.ironCoins);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to update num iron: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void loadCoins(final int type) {
        final List<Item> current = this.getListForCointype(type);
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM COINS WHERE TEMPLATEID=? AND OWNERID=-10 AND PARENTID=-10 AND ZONEID=-10 AND BANKED=1 AND MAILED=0");
            ps.setInt(1, type);
            rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    final Item toAdd = Items.getItem(rs.getLong("WURMID"));
                    current.add(toAdd);
                }
                catch (NoSuchItemException nsi) {
                    DbEconomy.logger.log(Level.WARNING, "Failed to load coin: " + rs.getLong("WURMID"), nsi);
                }
            }
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to load coins: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public Shop createShop(final long wurmid) {
        return new DbShop(wurmid, Servers.localServer.getInitialTraderIrons());
    }
    
    @Override
    public Shop createShop(final long wurmid, final long ownerid) {
        int coins = 0;
        if (ownerid == -10L) {
            coins = Servers.localServer.getInitialTraderIrons();
        }
        return new DbShop(wurmid, coins, ownerid);
    }
    
    @Override
    SupplyDemand createSupplyDemand(final int aId) {
        return new DbSupplyDemand(aId, 1000, 1000);
    }
    
    @Override
    void loadSupplyDemand() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM SUPPLYDEMAND");
            rs = ps.executeQuery();
            while (rs.next()) {
                new DbSupplyDemand(rs.getInt("ID"), rs.getInt("ITEMSBOUGHT"), rs.getInt("ITEMSSOLD"), rs.getLong("LASTPOLLED"));
            }
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to load supplyDemand: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    void loadShopMoney() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM TRADER");
            rs = ps.executeQuery();
            while (rs.next()) {
                new DbShop(rs.getLong("WURMID"), rs.getLong("MONEY"), rs.getLong("OWNER"), rs.getFloat("PRICEMODIFIER"), rs.getBoolean("FOLLOWGLOBALPRICE"), rs.getBoolean("USELOCALPRICE"), rs.getLong("LASTPOLLED"), rs.getFloat("TAX"), rs.getLong("SPENT"), rs.getLong("SPENTLIFE"), rs.getLong("EARNED"), rs.getLong("EARNEDLIFE"), rs.getLong("SPENTLASTMONTH"), rs.getLong("TAXPAID"), rs.getInt("NUMBEROFITEMS"), rs.getLong("WHENEMPTY"), true);
            }
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to load traderMoney: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public void addItemSoldByTraders(final String name, final long money, final String traderName, final String playerName, final int templateId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("INSERT INTO ITEMSSOLD (ITEMNAME,ITEMVALUE,TRADERNAME,PLAYERNAME, TEMPLATEID) VALUES(?,?,?,?,?)");
            ps.setString(1, name.substring(0, Math.min(29, name.length())));
            ps.setLong(2, money);
            ps.setString(3, traderName.substring(0, Math.min(29, traderName.length())));
            ps.setString(4, playerName.substring(0, Math.min(29, playerName.length())));
            ps.setInt(5, templateId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            DbEconomy.logger.log(Level.WARNING, "Failed to update num iron: " + sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(DbEconomy.class.getName());
    }
}
