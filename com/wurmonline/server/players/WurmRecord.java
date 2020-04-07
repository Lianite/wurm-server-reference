// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public class WurmRecord
{
    private static final String GET_CHAMPIONRECORDS = "SELECT * FROM CHAMPIONS";
    private int value;
    private String holder;
    private boolean current;
    private static final Logger logger;
    
    public WurmRecord(final int val, final String name, final boolean isCurrent) {
        this.value = 0;
        this.holder = "";
        this.current = true;
        this.value = val;
        this.holder = name;
        this.current = isCurrent;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public void setValue(final int aValue) {
        this.value = aValue;
    }
    
    public String getHolder() {
        return this.holder;
    }
    
    public void setHolder(final String aHolder) {
        this.holder = aHolder;
    }
    
    public boolean isCurrent() {
        return this.current;
    }
    
    public void setCurrent(final boolean aCurrent) {
        this.current = aCurrent;
    }
    
    public static final void loadAllChampRecords() {
        final long now = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM CHAMPIONS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final WurmRecord record = new WurmRecord(rs.getInt("VALUE"), rs.getString("NAME"), rs.getBoolean("CURRENT"));
                PlayerInfoFactory.addChampRecord(record);
            }
        }
        catch (SQLException sqex) {
            WurmRecord.logger.log(Level.WARNING, "Failed to load champ records.");
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.currentTimeMillis();
            WurmRecord.logger.info("Loaded " + PlayerInfoFactory.getChampionRecords().length + " champ records from the database took " + (end - now) + " ms");
        }
    }
    
    static {
        logger = Logger.getLogger(WurmRecord.class.getName());
    }
}
