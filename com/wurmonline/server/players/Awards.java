// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Server;
import java.util.Map;
import java.util.logging.Logger;

public class Awards
{
    public static final String INSERT_AWARDS = "INSERT INTO AWARDS(WURMID, DAYSPREM, MONTHSPREM, MONTHSEVER, CONSECMONTHS, SILVERSPURCHASED, LASTTICKEDPREM, CURRENTLOYALTY, TOTALLOYALTY) VALUES(?,?,?,?,?,?,?,?,?)";
    public static final String UPDATE_AWARDS = "UPDATE AWARDS SET DAYSPREM=?, MONTHSPREM=?, MONTHSEVER=?, CONSECMONTHS=?, SILVERSPURCHASED=?, LASTTICKEDPREM=?, TOTALLOYALTY=?, CURRENTLOYALTY=? WHERE WURMID=?";
    public static final String DELETE_AWARDS = "DELETE FROM AWARDS WHERE WURMID=?";
    private long wurmId;
    private static final Logger logger;
    private int daysPrem;
    private int monthsPaidEver;
    private int monthsPaidSinceReset;
    private int monthsPaidInARow;
    private int silversPaidEver;
    private long lastTickedDay;
    private int currentLoyaltyPoints;
    private int totalLoyaltyPoints;
    public static final Map<Long, Awards> allAwards;
    
    public Awards(final long playerId, final int daysPremium, final int _monthsPaidEver, final int monthsPaidInSuccession, final int _monthsPaidSinceReset, final int silversPurchased, final long _lastTickedDay, final int _currentLoyalty, final int _totalLoyalty, final boolean createInDb) {
        this.wurmId = playerId;
        this.daysPrem = daysPremium;
        this.monthsPaidEver = _monthsPaidEver;
        this.monthsPaidInARow = monthsPaidInSuccession;
        this.silversPaidEver = silversPurchased;
        this.lastTickedDay = _lastTickedDay;
        this.monthsPaidSinceReset = _monthsPaidSinceReset;
        this.currentLoyaltyPoints = _currentLoyalty;
        this.totalLoyaltyPoints = _totalLoyalty;
        Awards.allAwards.put(this.wurmId, this);
        if (createInDb) {
            this.save();
        }
    }
    
    @Override
    public final String toString() {
        return "Awards for " + this.wurmId + ", days=" + this.daysPrem + ", mo's ever=" + this.monthsPaidEver + ", mo's row=" + this.monthsPaidInARow + ", mo's reset=" + this.monthsPaidSinceReset + ", silvers=" + this.silversPaidEver + ", loyalty=" + this.currentLoyaltyPoints + ", totalLoyalty=" + this.totalLoyaltyPoints + ", tick=" + Server.getTimeFor(System.currentTimeMillis() - this.lastTickedDay) + " ago";
    }
    
    public long getWurmId() {
        return this.wurmId;
    }
    
    public static final Awards getAwards(final long wurmid) {
        return Awards.allAwards.get(wurmid);
    }
    
    public void setWurmId(final long aWurmId) {
        this.wurmId = aWurmId;
    }
    
    public int getMonthsPaidEver() {
        return this.monthsPaidEver;
    }
    
    public void setMonthsPaidEver(final int aMonthsPaidEver) {
        this.monthsPaidEver = aMonthsPaidEver;
    }
    
    public AwardLadder getNextReward() {
        return AwardLadder.getNextTotalAward(this.getMonthsPaidSinceReset());
    }
    
    public int getMonthsPaidInARow() {
        return this.monthsPaidInARow;
    }
    
    public void setMonthsPaidInARow(final int aMonthsPaidInARow) {
        this.monthsPaidInARow = aMonthsPaidInARow;
    }
    
    public int getSilversPaidEver() {
        return this.silversPaidEver;
    }
    
    public void setSilversPaidEver(final int aSilversPaidEver) {
        this.silversPaidEver = aSilversPaidEver;
    }
    
    public long getLastTickedDay() {
        return this.lastTickedDay;
    }
    
    public void setLastTickedDay(final long aLastTickedDay) {
        this.lastTickedDay = aLastTickedDay;
    }
    
    public int getDaysPrem() {
        return this.daysPrem;
    }
    
    public void setDaysPrem(final int aDaysPrem) {
        this.daysPrem = aDaysPrem;
    }
    
    public int getMonthsPaidSinceReset() {
        return this.monthsPaidSinceReset;
    }
    
    public void setMonthsPaidSinceReset(final int aMonthsPaidSinceReset) {
        this.monthsPaidSinceReset = aMonthsPaidSinceReset;
    }
    
    public int getCurrentLoyalty() {
        return this.currentLoyaltyPoints;
    }
    
    public void setCurrentLoyalty(final int newLoyalty) {
        this.currentLoyaltyPoints = newLoyalty;
    }
    
    public int getTotalLoyalty() {
        return this.totalLoyaltyPoints;
    }
    
    public void setTotalLoyaltyPoints(final int newTotal) {
        this.totalLoyaltyPoints = newTotal;
    }
    
    public final void save() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO AWARDS(WURMID, DAYSPREM, MONTHSPREM, MONTHSEVER, CONSECMONTHS, SILVERSPURCHASED, LASTTICKEDPREM, CURRENTLOYALTY, TOTALLOYALTY) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, this.wurmId);
            ps.setInt(2, this.daysPrem);
            ps.setInt(3, this.monthsPaidSinceReset);
            ps.setInt(4, this.monthsPaidEver);
            ps.setInt(5, this.monthsPaidInARow);
            ps.setInt(6, this.silversPaidEver);
            ps.setLong(7, this.lastTickedDay);
            ps.setInt(8, this.currentLoyaltyPoints);
            ps.setInt(9, this.totalLoyaltyPoints);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Awards.logger.log(Level.WARNING, this.wurmId + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void update() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE AWARDS SET DAYSPREM=?, MONTHSPREM=?, MONTHSEVER=?, CONSECMONTHS=?, SILVERSPURCHASED=?, LASTTICKEDPREM=?, TOTALLOYALTY=?, CURRENTLOYALTY=? WHERE WURMID=?");
            ps.setInt(1, this.daysPrem);
            ps.setInt(2, this.monthsPaidSinceReset);
            ps.setInt(3, this.monthsPaidEver);
            ps.setInt(4, this.monthsPaidInARow);
            ps.setInt(5, this.silversPaidEver);
            ps.setLong(6, this.lastTickedDay);
            ps.setInt(7, this.currentLoyaltyPoints);
            ps.setInt(8, this.totalLoyaltyPoints);
            ps.setLong(9, this.wurmId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Awards.logger.log(Level.WARNING, this.wurmId + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    static {
        logger = Logger.getLogger(Awards.class.getName());
        allAwards = new ConcurrentHashMap<Long, Awards>();
    }
}
