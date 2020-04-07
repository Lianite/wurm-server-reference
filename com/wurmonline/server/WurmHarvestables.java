// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.Tiles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.logging.Logger;

public class WurmHarvestables implements TimeConstants, MiscConstants
{
    private static final Logger logger;
    public static final int NONE_ID = 0;
    public static final int OLIVE_ID = 1;
    public static final int GRAPE_ID = 2;
    public static final int CHERRY_ID = 3;
    public static final int APPLE_ID = 4;
    public static final int LEMON_ID = 5;
    public static final int OLEANDER_ID = 6;
    public static final int CAMELLIA_ID = 7;
    public static final int LAVENDER_ID = 8;
    public static final int MAPLE_ID = 9;
    public static final int ROSE_ID = 10;
    public static final int CHESTNUT_ID = 11;
    public static final int WALNUT_ID = 12;
    public static final int PINE_ID = 13;
    public static final int HAZEL_ID = 14;
    public static final int HOPS_ID = 15;
    public static final int OAK_ID = 16;
    public static final int ORANGE_ID = 17;
    public static final int RASPBERRY_ID = 18;
    public static final int BLUEBERRY_ID = 19;
    public static final int LINGONBERRY_ID = 20;
    public static final int MAX_HARVEST_ID = 20;
    private static final String GET_CALENDAR_HARVEST_EVENTS = "SELECT * FROM CALENDAR WHERE type = 1";
    private static final String INSERT_CALENDAR_HARVEST_EVENT = "INSERT INTO CALENDAR (eventid, starttime, type) VALUES (?,?,1)";
    private static final String UPDATE_CALENDAR_HARVEST_EVENT = "UPDATE CALENDAR SET starttime = ? where eventid = ? and type = 1";
    private static final Harvestable[] harvestables;
    public static long lastHarvestableCheck;
    public static final Random endRand;
    
    @Nullable
    public static Harvestable getHarvestable(final int id) {
        if (id == 0) {
            return null;
        }
        if (id < 1 || id > 20) {
            WurmHarvestables.logger.severe("Invalid Harvest Id " + id);
            return null;
        }
        return WurmHarvestables.harvestables[id];
    }
    
    public static int getMaxHarvestId() {
        return 20;
    }
    
    static long getLastHarvestableCheck() {
        return WurmHarvestables.lastHarvestableCheck;
    }
    
    public static void setHarvestStart(final int eventId, final long newDate) {
        final Harvestable harvestable = getHarvestable(eventId);
        if (harvestable != null) {
            harvestable.setSeasonStart(newDate, true);
            dbUpdateHarvestEvent(eventId, newDate);
        }
    }
    
    private static void dbLoadHarvestStartTimes() {
        Connection dbcon = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            stmt = dbcon.createStatement();
            rs = stmt.executeQuery("SELECT * FROM CALENDAR WHERE type = 1");
            while (rs.next()) {
                final int lEventId = rs.getInt("eventid");
                final long tStartTime = rs.getLong("starttime");
                final boolean recalc = tStartTime > (WurmCalendar.getYearOffset() + 1) * 29030400L + (WurmCalendar.getStarfall() + 1) * 2419200L;
                final long lStartTime = Math.max(WurmCalendar.getYearOffset() * 29030400L, tStartTime);
                if (WurmHarvestables.logger.isLoggable(Level.FINEST)) {
                    WurmHarvestables.logger.finest("Loading harvest calendar event - Id: " + lEventId + ", start: " + lStartTime);
                }
                final Harvestable harvestable = getHarvestable(lEventId);
                if (harvestable != null) {
                    if (recalc) {
                        harvestable.calcHarvestStart(WurmCalendar.getYearOffset());
                    }
                    else {
                        harvestable.setSeasonStart(lStartTime, false);
                    }
                }
                else {
                    if (lEventId == 0) {
                        continue;
                    }
                    WurmHarvestables.logger.warning("Unknown harvest event in the Calendar: " + lEventId + ", start: " + lStartTime);
                }
            }
        }
        catch (SQLException ex) {
            WurmHarvestables.logger.log(Level.WARNING, "Failed to load harvest events from the calendar", ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateHarvestEvent(final int aEventId, final long aStartTime) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("UPDATE CALENDAR SET starttime = ? where eventid = ? and type = 1");
            ps.setLong(1, Math.max(0L, aStartTime));
            ps.setLong(2, aEventId);
            if (ps.executeUpdate() == 0) {
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("INSERT INTO CALENDAR (eventid, starttime, type) VALUES (?,?,1)");
                ps.setLong(1, aEventId);
                ps.setLong(2, aStartTime);
                ps.executeUpdate();
            }
        }
        catch (SQLException ex) {
            WurmHarvestables.logger.log(Level.WARNING, "Failed to update harvest event to calendar with event id " + aEventId + ", startTime: " + aStartTime, ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void setStartTimes() {
        final long start = System.nanoTime();
        final boolean forceEnumLoad = Harvestable.APPLE.isHarvestable();
        dbLoadHarvestStartTimes();
        for (final Harvestable harvestable : Harvestable.values()) {
            if (harvestable != Harvestable.NONE && harvestable.getSeasonStart() > WurmCalendar.currentTime + 29030400L + 2419200L) {
                harvestable.calcHarvestStart(WurmCalendar.getYearOffset());
            }
        }
        logGrowthStartDates();
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        WurmHarvestables.logger.log(Level.INFO, "Set harvest start dates. It took " + lElapsedTime + " millis.");
    }
    
    private static void logGrowthStartDates() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Current wurm time: ").append(WurmCalendar.currentTime).append(" - ").append(WurmCalendar.getTime());
        buf.append("\n" + Harvestable.APPLE.getHarvestEvent());
        buf.append("\n" + Harvestable.BLUEBERRY.getHarvestEvent());
        buf.append("\n" + Harvestable.CAMELLIA.getHarvestEvent());
        buf.append("\n" + Harvestable.CHERRY.getHarvestEvent());
        buf.append("\n" + Harvestable.CHESTNUT.getHarvestEvent());
        buf.append("\n" + Harvestable.GRAPE.getHarvestEvent());
        buf.append("\n" + Harvestable.HAZEL.getHarvestEvent());
        buf.append("\n" + Harvestable.HOPS.getHarvestEvent());
        buf.append("\n" + Harvestable.LAVENDER.getHarvestEvent());
        buf.append("\n" + Harvestable.LEMON.getHarvestEvent());
        buf.append("\n" + Harvestable.LINGONBERRY.getHarvestEvent());
        buf.append("\n" + Harvestable.MAPLE.getHarvestEvent());
        buf.append("\n" + Harvestable.OAK.getHarvestEvent());
        buf.append("\n" + Harvestable.OLEANDER.getHarvestEvent());
        buf.append("\n" + Harvestable.OLIVE.getHarvestEvent());
        buf.append("\n" + Harvestable.ORANGE.getHarvestEvent());
        buf.append("\n" + Harvestable.PINE.getHarvestEvent());
        buf.append("\n" + Harvestable.RASPBERRY.getHarvestEvent());
        buf.append("\n" + Harvestable.ROSE.getHarvestEvent());
        buf.append("\n" + Harvestable.WALNUT.getHarvestEvent());
        WurmHarvestables.logger.log(Level.INFO, buf.toString());
    }
    
    public static void checkHarvestables(final long currentTime) {
        boolean haveDatesChanged = false;
        if (currentTime < WurmHarvestables.lastHarvestableCheck + 3600L) {
            return;
        }
        WurmHarvestables.lastHarvestableCheck = WurmCalendar.currentTime;
        for (final Harvestable harvestable : WurmHarvestables.harvestables) {
            haveDatesChanged = (haveDatesChanged || harvestable.isHarvestOver());
            haveDatesChanged = (haveDatesChanged || harvestable.hasSeasonStarted());
        }
        if (haveDatesChanged) {
            logGrowthStartDates();
        }
    }
    
    private static void setHarvestable(final byte normalType, final byte myceliumType, final boolean harvestable) {
        final int min = 1;
        final int ms = Constants.meshSize;
        for (int max = (1 << ms) - 1, x = 1; x < max; ++x) {
            for (int y = 1; y < max; ++y) {
                final int encodedTile = Server.surfaceMesh.getTile(x, y);
                final byte tileType = Tiles.decodeType(encodedTile);
                if (tileType == normalType || tileType == myceliumType) {
                    final short newHeight = Tiles.decodeHeight(encodedTile);
                    byte tileData = Tiles.decodeData(encodedTile);
                    if (harvestable) {
                        tileData |= 0x8;
                    }
                    else {
                        tileData &= (byte)247;
                    }
                    Server.setSurfaceTile(x, y, newHeight, tileType, tileData);
                    Players.getInstance().sendChangedTile(x, y, true, false);
                }
            }
        }
    }
    
    private static void setHarvestable(final int itemType, final boolean harvestable) {
        for (final Item item : Items.getHarvestableItems()) {
            if (item.getTemplateId() == itemType) {
                item.setHarvestable(harvestable);
            }
        }
    }
    
    public static Harvestable[] getHarvestables() {
        return WurmHarvestables.harvestables;
    }
    
    public static int getHarvestableIdFromTile(final byte tileType) {
        for (final Harvestable harvestable : WurmHarvestables.harvestables) {
            if (harvestable.tileNormal == tileType || harvestable.tileMycelium == tileType) {
                return harvestable.harvestableId;
            }
        }
        return -1;
    }
    
    public static int getHarvestableIdFromTrellis(final int trellis) {
        for (final Harvestable harvestable : WurmHarvestables.harvestables) {
            if (harvestable.trellis == trellis) {
                return harvestable.harvestableId;
            }
        }
        return -1;
    }
    
    static {
        logger = Logger.getLogger(WurmHarvestables.class.getName());
        harvestables = new Harvestable[21];
        WurmHarvestables.lastHarvestableCheck = 0L;
        endRand = new Random();
    }
    
    public enum Harvestable
    {
        NONE(0, "none", 0, 0, (byte)(-1), (byte)(-1), -1, "", ""), 
        OLIVE(1, "olive", 7, 0, (byte)108, (byte)122, -1, "olive", "an olive"), 
        GRAPE(2, "grape", 8, 0, (byte)(-111), (byte)(-105), 920, "grape", "a grape"), 
        CHERRY(3, "cherry", 6, 0, (byte)109, (byte)123, -1, "cherry", "a cherry"), 
        APPLE(4, "apple", 8, 2, (byte)106, (byte)120, -1, "apple", "an apple"), 
        LEMON(5, "lemon", 8, 1, (byte)107, (byte)121, -1, "lemon", "a lemon"), 
        OLEANDER(6, "oleander", 3, 1, (byte)(-109), (byte)(-103), -1, "oleander leaf", "an oleander leaf"), 
        CAMELLIA(7, "camellia", 3, 3, (byte)(-110), (byte)(-104), -1, "camellia leaf", "a camellia leaf"), 
        LAVENDER(8, "lavender", 4, 1, (byte)(-114), (byte)(-108), -1, "lavender flower", "a lavender flower"), 
        MAPLE(9, "maple", 4, 3, (byte)105, (byte)119, -1, "maple sap", "some maple sap"), 
        ROSE(10, "rose", 4, 2, (byte)(-113), (byte)(-107), 1018, "rose flower", "a rose flower"), 
        CHESTNUT(11, "chestnut", 8, 3, (byte)110, (byte)124, -1, "chestnut", "a chestnut"), 
        WALNUT(12, "walnut", 9, 1, (byte)111, (byte)125, -1, "walnut", "a walnut"), 
        PINE(13, "pine", 0, 0, (byte)101, (byte)115, -1, "pinenut", "a pinenut"), 
        HAZEL(14, "hazel", 9, 2, (byte)(-96), (byte)(-95), -1, "hazelnut", "a hazelnut"), 
        HOPS(15, "hops", 7, 2, (byte)(-1), (byte)(-1), 1274, "hops", "some hops"), 
        OAK(16, "oak", 5, 1, (byte)102, (byte)116, -1, "acorn", "an acorn"), 
        ORANGE(17, "orange", 7, 3, (byte)(-93), (byte)(-92), -1, "orange", "an orange"), 
        RASPBERRY(18, "raspberry", 9, 0, (byte)(-90), (byte)(-89), -1, "raspberry", "a raspberry"), 
        BLUEBERRY(19, "blueberry", 7, 1, (byte)(-87), (byte)(-86), -1, "blueberry", "a blueberry"), 
        LINGONBERRY(20, "lingonberry", 9, 3, (byte)(-84), (byte)(-84), -1, "lingonberry", "a lingonberry");
        
        private final int harvestableId;
        private final String name;
        private final int month;
        private final int week;
        private final byte tileNormal;
        private final byte tileMycelium;
        private final int trellis;
        private final int reportDifficulty;
        private final String fruit;
        private final String fruitWithGenus;
        private long seasonStart;
        private long seasonEnd;
        private boolean isHarvestable;
        
        private Harvestable(final int id, final String name, final int month, final int week, final byte tileNormal, final byte tileMycelium, final int trellis, final String fruit, final String fruitWithGenus) {
            this.seasonStart = Long.MAX_VALUE;
            this.seasonEnd = Long.MAX_VALUE;
            this.isHarvestable = false;
            this.harvestableId = id;
            this.name = name;
            this.month = month;
            this.week = week;
            this.tileNormal = tileNormal;
            this.tileMycelium = tileMycelium;
            this.trellis = trellis;
            this.fruit = fruit;
            this.fruitWithGenus = fruitWithGenus;
            if (tileNormal > 0) {
                final Tiles.Tile tile = Tiles.getTile(tileNormal);
                if (tile != null) {
                    this.reportDifficulty = tile.getWoodDificulity();
                }
                else {
                    this.reportDifficulty = 2;
                }
            }
            else {
                this.reportDifficulty = 2;
            }
            if (id < 0 || id > 20) {
                WurmHarvestables.logger.severe("Invalid Harvest Id " + id);
            }
            else {
                WurmHarvestables.harvestables[id] = this;
            }
        }
        
        public int getHarvestableId() {
            return this.harvestableId;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getHarvestableWithDates() {
            if (this.isHarvestable) {
                return this.name + ", season ends in " + WurmCalendar.getDaysFrom(this.getSeasonEnd());
            }
            return "";
        }
        
        public String getState() {
            if (this.isHarvestable()) {
                return "Harvestable";
            }
            if (this.isAlmostRipe()) {
                return "Almost Ripe";
            }
            return "";
        }
        
        public String getFruit() {
            return this.fruit;
        }
        
        public String getFruitWithGenus() {
            return this.fruitWithGenus;
        }
        
        public long getSeasonStart() {
            return this.seasonStart;
        }
        
        public long getSeasonEnd() {
            return this.seasonEnd;
        }
        
        public void setSeasonStart(final long newGrowth, final boolean updateHarvestables) {
            this.seasonStart = newGrowth;
            final boolean harvestable = WurmCalendar.getCurrentTime() > this.seasonStart;
            if (harvestable != this.isHarvestable) {
                this.isHarvestable = harvestable;
                if (updateHarvestables) {
                    this.updateHarvestables(harvestable);
                }
            }
            WurmHarvestables.endRand.setSeed(this.seasonStart);
            final int diff = Math.min(7, this.reportDifficulty);
            final int adjust = WurmHarvestables.endRand.nextInt((int)(diff * 86400L));
            this.seasonEnd = this.getSeasonStart() + 2419200L - adjust;
        }
        
        public boolean isHarvestable() {
            return this.isHarvestable;
        }
        
        private void calcHarvestStart(final int yearOffset) {
            final long rDay = 259200L + Server.rand.nextInt(604800) + Server.rand.nextInt(604800) - Server.rand.nextInt(604800) - Server.rand.nextInt(604800);
            final long startWeek = yearOffset * 29030400L + this.month * 2419200L + this.week * 604800L;
            this.setSeasonStart(startWeek + rDay, true);
            dbUpdateHarvestEvent(this.harvestableId, this.seasonStart);
        }
        
        public long getDefaultSeasonStart() {
            final int yearOffset = (int)(this.seasonStart / 29030400L);
            return yearOffset * 29030400L + this.month * 2419200L + this.week * 604800L;
        }
        
        public long getDefaultSeasonEnd() {
            return this.getDefaultSeasonStart() + 2419200L;
        }
        
        private boolean isHarvestOver() {
            if (this.harvestableId == 0) {
                return false;
            }
            if (WurmHarvestables.lastHarvestableCheck > this.getSeasonEnd()) {
                this.calcHarvestStart(WurmCalendar.getYearOffset() + 1);
                this.updateHarvestables(false);
                return true;
            }
            return false;
        }
        
        private boolean hasSeasonStarted() {
            if (this.harvestableId == 0) {
                return false;
            }
            if (!this.isHarvestable && WurmHarvestables.lastHarvestableCheck > this.seasonStart) {
                this.updateHarvestables(true);
                return true;
            }
            return false;
        }
        
        private void updateHarvestables(final boolean harvestable) {
            this.isHarvestable = harvestable;
            if (this.tileNormal != -1) {
                setHarvestable(this.tileNormal, this.tileMycelium, this.isHarvestable);
            }
            if (this.trellis > 0) {
                setHarvestable(this.trellis, this.isHarvestable);
            }
        }
        
        public String getHarvestEvent() {
            return "start " + this.name + " season: " + (this.isHarvestable ? "(harvestable) " : "") + this.seasonStart + " - " + WurmCalendar.getTimeFor(this.seasonStart);
        }
        
        public boolean isAlmostRipe() {
            return WurmCalendar.currentTime >= this.getSeasonStart() - 2419200L && WurmCalendar.currentTime < this.getSeasonStart();
        }
        
        public int getReportDifficulty() {
            return this.reportDifficulty;
        }
        
        public boolean isSap() {
            return this.harvestableId == 9;
        }
        
        public boolean isFlower() {
            return this.harvestableId == 8 || this.harvestableId == 10;
        }
        
        public boolean isLeaf() {
            return this.harvestableId == 7 || this.harvestableId == 6;
        }
        
        public boolean isBerry() {
            return this.harvestableId == 18 || this.harvestableId == 19 || this.harvestableId == 20;
        }
        
        public boolean isNut() {
            return this.harvestableId == 11 || this.harvestableId == 12 || this.harvestableId == 13 || this.harvestableId == 14;
        }
        
        public boolean isFruit() {
            return this.harvestableId == 1 || this.harvestableId == 2 || this.harvestableId == 3 || this.harvestableId == 4 || this.harvestableId == 5 || this.harvestableId == 17;
        }
        
        public boolean isHops() {
            return this.harvestableId == 15;
        }
        
        public boolean isAcorn() {
            return this.harvestableId == 16;
        }
    }
}
