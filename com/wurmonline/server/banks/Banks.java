// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.banks;

import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.TimeConstants;

public final class Banks implements TimeConstants
{
    private static final Map<Long, Bank> banks;
    private static final String LOADBANKS = "SELECT * FROM BANKS";
    private static final String ISBANKED = "SELECT EXISTS(SELECT 1 FROM BANKS_ITEMS WHERE ITEMID=?) AS ISBANKED";
    private static final String BANKID = "SELECT BANKID FROM BANKS_ITEMS WHERE ITEMID=?";
    private static final String OWNEROFBANK = "SELECT OWNER FROM BANKS WHERE WURMID=?";
    private static final Logger logger;
    
    private static final void addBank(final Bank bank) {
        Banks.banks.put(new Long(bank.owner), bank);
    }
    
    public static final Bank getBank(final long owner) {
        final Bank bank = Banks.banks.get(new Long(owner));
        return bank;
    }
    
    public static final int getNumberOfBanks() {
        return Banks.banks.size();
    }
    
    public static final void poll(final long now) {
        if (Banks.banks != null && !Banks.banks.isEmpty()) {
            final boolean MULTI_THREADED_BANK_POLL = false;
            final int NUMBER_OF_BANK_POLL_TASKS = 10;
            for (final Bank bank : Banks.banks.values()) {
                bank.poll(now);
            }
        }
        else {
            Banks.logger.log(Level.FINE, "No banks to poll");
        }
    }
    
    public static boolean startBank(final long owner, final int size, final int currentVillage) {
        if (Banks.banks.containsKey(new Long(owner))) {
            return false;
        }
        final Bank bank = new Bank(owner, size, currentVillage);
        addBank(bank);
        return true;
    }
    
    public static void loadAllBanks() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int loadedBanks = 0;
        final long start = System.nanoTime();
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM BANKS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmid = rs.getLong("WURMID");
                final long owner = rs.getLong("OWNER");
                final long lastpolled = rs.getLong("LASTPOLLED");
                final long startedMove = rs.getLong("STARTEDMOVE");
                final int size = rs.getInt("SIZE");
                final int currentVillage = rs.getInt("CURRENTVILLAGE");
                final int targetVillage = rs.getInt("TARGETVILLAGE");
                addBank(new Bank(wurmid, owner, size, lastpolled, startedMove, currentVillage, targetVillage));
                ++loadedBanks;
            }
        }
        catch (SQLException sqx) {
            Banks.logger.log(Level.WARNING, "Failed to load banks, SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
            final Exception lNext = sqx.getNextException();
            if (lNext != null) {
                Banks.logger.log(Level.WARNING, "Failed to load banks, Next Exception", lNext);
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Banks.logger.info("Loaded " + loadedBanks + " banks from database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static long itemInBank(final long itemID) {
        long inBank = 0L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT BANKID FROM BANKS_ITEMS WHERE ITEMID=?");
            ps.setLong(1, itemID);
            rs = ps.executeQuery();
            while (rs.next()) {
                inBank = rs.getLong("BANKID");
            }
        }
        catch (SQLException sqx) {
            Banks.logger.log(Level.WARNING, "Failed execute ISBANKED, SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
            final Exception lNext = sqx.getNextException();
            if (lNext != null) {
                Banks.logger.log(Level.WARNING, "Failed to execute ISBANKED, Next Exception", lNext);
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return inBank;
    }
    
    public static final boolean isItemBanked(final long itemID) {
        return itemInBank(itemID) != 0L;
    }
    
    public static final long ownerOfBank(final long bankID) {
        long ownerid = -10L;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("SELECT OWNER FROM BANKS WHERE WURMID=?");
            ps.setLong(1, bankID);
            rs = ps.executeQuery();
            while (rs.next()) {
                ownerid = rs.getLong("OWNER");
            }
        }
        catch (SQLException sqx) {
            Banks.logger.log(Level.WARNING, "Failed execute ISBANKED, SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
            final Exception lNext = sqx.getNextException();
            if (lNext != null) {
                Banks.logger.log(Level.WARNING, "Failed to execute ISBANKED, Next Exception", lNext);
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
        return ownerid;
    }
    
    static {
        banks = new HashMap<Long, Bank>();
        logger = Logger.getLogger(Banks.class.getName());
    }
}
