// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.banks;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.MiscConstants;

public final class BankSlot implements MiscConstants
{
    public static final String VERSION = "$Revision: 1.2 $";
    public Item item;
    public boolean stasis;
    public long inserted;
    private final long bank;
    private static final String CREATE = "INSERT INTO BANKS_ITEMS (ITEMID,BANKID,INSERTED,STASIS) VALUES(?,?,?,?)";
    private static final String DELETESLOT = "DELETE FROM BANKS_ITEMS WHERE ITEMID=?";
    private static final Logger logger;
    
    BankSlot(final Item aItem, final long aBank, final boolean aStas, final long aInserted, final boolean aCreate) {
        this.inserted = -10L;
        this.item = aItem;
        this.bank = aBank;
        this.stasis = aStas;
        this.inserted = aInserted;
        if (aCreate) {
            this.create();
        }
    }
    
    private void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("INSERT INTO BANKS_ITEMS (ITEMID,BANKID,INSERTED,STASIS) VALUES(?,?,?,?)");
            ps.setLong(1, this.item.getWurmId());
            ps.setLong(2, this.bank);
            ps.setLong(3, System.currentTimeMillis());
            ps.setBoolean(4, this.stasis);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            BankSlot.logger.log(Level.WARNING, "Failed to create bank slot for item " + this.item.getWurmId() + ", SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
            final Exception lNext = sqx.getNextException();
            if (lNext != null) {
                BankSlot.logger.log(Level.WARNING, "Failed to create bank slot for item " + this.item.getWurmId() + ", next exception", lNext);
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void delete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getEconomyDbCon();
            ps = dbcon.prepareStatement("DELETE FROM BANKS_ITEMS WHERE ITEMID=?");
            ps.setLong(1, this.item.getWurmId());
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            BankSlot.logger.log(Level.WARNING, "Failed to delete bankslot for bank " + this.bank + ", SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
            final Exception lNext = sqx.getNextException();
            if (lNext != null) {
                BankSlot.logger.log(Level.WARNING, "Failed to delete bankslot for bank " + this.bank + ", next exception", lNext);
            }
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public Item getItem() {
        return this.item;
    }
    
    public void setItem(final Item aItem) {
        this.item = aItem;
    }
    
    public long getInserted() {
        return this.inserted;
    }
    
    public void setInserted(final long aInserted) {
        this.inserted = aInserted;
    }
    
    public boolean isStasis() {
        return this.stasis;
    }
    
    public long getBank() {
        return this.bank;
    }
    
    static {
        logger = Logger.getLogger(BankSlot.class.getName());
    }
}
