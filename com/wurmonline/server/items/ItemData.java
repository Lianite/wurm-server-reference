// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import java.sql.Connection;
import com.wurmonline.server.Items;
import java.util.logging.Logger;

public final class ItemData
{
    int data1;
    int data2;
    int extra1;
    int extra2;
    public final long wurmid;
    private static final Logger logger;
    
    public ItemData(final long wid, final int d1, final int d2, final int e1, final int e2) {
        this.wurmid = wid;
        this.data1 = d1;
        this.data2 = d2;
        this.extra1 = e1;
        this.extra2 = e2;
        Items.addData(this);
    }
    
    public void createDataEntry(final Connection dbcon) {
        PreparedStatement ps = null;
        try {
            ps = dbcon.prepareStatement(ItemDbStrings.getInstance().createData());
            ps.setInt(1, this.data1);
            ps.setInt(2, this.data2);
            ps.setInt(3, this.extra1);
            ps.setInt(4, this.extra2);
            ps.setLong(5, this.wurmid);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            ItemData.logger.log(Level.WARNING, "Failed to save item data " + this.wurmid, sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
        }
    }
    
    int getData1() {
        return this.data1;
    }
    
    void setData1(final int aData1) {
        this.data1 = aData1;
    }
    
    int getData2() {
        return this.data2;
    }
    
    void setData2(final int aData2) {
        this.data2 = aData2;
    }
    
    int getExtra1() {
        return this.extra1;
    }
    
    void setExtra1(final int aExtra1) {
        this.extra1 = aExtra1;
    }
    
    int getExtra2() {
        return this.extra2;
    }
    
    void setExtra2(final int aExtra2) {
        this.extra2 = aExtra2;
    }
    
    public long getWurmid() {
        return this.wurmid;
    }
    
    static {
        logger = Logger.getLogger(ItemData.class.getName());
    }
}
