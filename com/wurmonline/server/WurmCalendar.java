// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.logging.Level;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.time.Year;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.utils.StringUtil;
import java.util.logging.Logger;
import java.text.DateFormat;

public final class WurmCalendar implements TimeConstants
{
    private static final DateFormat gmtDateFormat;
    private static final String[] day_names;
    private static final Logger logger;
    static final int STARFALL_DIAMONDS = 0;
    static final int STARFALL_SAW = 1;
    static final int STARFALL_DIGGING = 2;
    static final int STARFALL_LEAF = 3;
    static final int STARFALL_BEAR = 4;
    static final int STARFALL_SNAKE = 5;
    static final int STARFALL_SHARK = 6;
    static final int STARFALL_FIRES = 7;
    static final int STARFALL_RAVEN = 8;
    static final int STARFALL_DANCERS = 9;
    static final int STARFALL_OMENS = 10;
    static final int STARFALL_SILENCE = 11;
    private static boolean isSpring;
    private static boolean isTestChristmas;
    public static boolean wasTestChristmas;
    private static boolean isTestEaster;
    private static boolean isTestWurm;
    private static boolean isTestHalloween;
    private static boolean personalGoalsActive;
    private static final boolean ENABLE_CHECK_SPRING = false;
    private static final int startYear = 980;
    public static long currentTime;
    public static long lastHarvestableCheck;
    private static final String[] starfall_names;
    
    public static String getTime() {
        final long year = 980L + WurmCalendar.currentTime / 29030400L;
        final int starfall = (int)(WurmCalendar.currentTime % 29030400L / 2419200L);
        final int day = (int)(WurmCalendar.currentTime % 2419200L / 86400L);
        final int dayOfWeek = day % 7;
        final long week = day / 7 + 1;
        final int hour = (int)(WurmCalendar.currentTime % 86400L / 3600L);
        final int minute = getMinute();
        final int second = getSecond();
        String toReturn = StringUtil.format("It is %02d:%02d:%02d", hour, minute, second);
        toReturn = toReturn + " on " + WurmCalendar.day_names[dayOfWeek] + " in week " + week + " of " + WurmCalendar.starfall_names[starfall] + " in the year of " + year + ".";
        return toReturn;
    }
    
    public static final boolean mayDestroyHugeAltars() {
        final int day = (int)(WurmCalendar.currentTime % 2419200L / 86400L);
        final long week = day / 7 + 1;
        return (getDay() == 3 || getDay() == 6) && (week == 1L || week == 3L);
    }
    
    public static final String getTimeFor(final long wurmtime) {
        final long year = 980L + wurmtime / 29030400L;
        final int starfall = (int)Math.max(0L, wurmtime % 29030400L / 2419200L);
        final int day = (int)(wurmtime % 2419200L / 86400L);
        final int dayOfWeek = Math.max(0, day % 7);
        final long week = day / 7 + 1;
        final int hour = (int)(wurmtime % 86400L / 3600L);
        final int minute = (int)(wurmtime % 3600L / 60L);
        final int second = (int)(wurmtime % 60L);
        String toReturn = StringUtil.format("%02d:%02d:%02d", hour, minute, second);
        toReturn = toReturn + " on " + WurmCalendar.day_names[dayOfWeek] + " in week " + week + " of " + WurmCalendar.starfall_names[starfall] + " in the year of " + year + ".";
        return toReturn;
    }
    
    public static final String getDateFor(final long wurmtime) {
        final long year = 980L + wurmtime / 29030400L;
        final int starfall = (int)(wurmtime % 29030400L / 2419200L);
        final int day = (int)(wurmtime % 2419200L / 86400L);
        final int dayOfWeek = day % 7;
        final long week = day / 7 + 1;
        String toReturn = "";
        toReturn = toReturn + WurmCalendar.day_names[dayOfWeek] + ", week " + week + " of " + WurmCalendar.starfall_names[starfall] + ", " + year + ".";
        return toReturn;
    }
    
    public static final String getDaysFrom(final long wurmtime) {
        final boolean inPast = WurmCalendar.currentTime > wurmtime;
        final long diff = Math.abs(WurmCalendar.currentTime - wurmtime);
        final long diffYear = diff / 29030400L;
        final int diffMonth = (int)(diff % 29030400L / 2419200L);
        final int diffWeek = (int)(diff % 2419200L / 604800L);
        final int diffDay = (int)(diff % 604800L / 86400L);
        final StringBuilder buf = new StringBuilder();
        if (diffYear > 0L) {
            if (diffYear == 1L) {
                buf.append(diffYear + " year, ");
            }
            else {
                buf.append(diffYear + " years, ");
            }
        }
        if (diffYear > 0L || diffMonth > 0) {
            if (diffMonth == 1) {
                buf.append(diffMonth + " month, ");
            }
            else {
                buf.append(diffMonth + " months, ");
            }
        }
        if (diffYear > 0L || diffMonth > 0 || diffWeek > 0) {
            if (diffWeek == 1) {
                buf.append(diffWeek + " week, ");
            }
            else {
                buf.append(diffWeek + " weeks, ");
            }
        }
        if (diffDay == 1) {
            buf.append(diffDay + " day");
        }
        else {
            buf.append(diffDay + " days");
        }
        if (inPast) {
            buf.append(" ago.");
        }
        else {
            buf.append(".");
        }
        return buf.toString();
    }
    
    public static void tickSecond() {
        ++WurmCalendar.currentTime;
        if (WurmCalendar.currentTime < WurmHarvestables.getLastHarvestableCheck() + 3600L) {
            return;
        }
        WurmHarvestables.checkHarvestables(WurmCalendar.currentTime);
        if (WurmCalendar.personalGoalsActive && !nowIsBefore(0, 1, 1, 1, 2019)) {
            WurmCalendar.personalGoalsActive = false;
            Server.getInstance().broadCastAlert("Alert: Personal Goals are now disabled", true);
            for (final Player p : Players.getInstance().getPlayers()) {
                p.getCommunicator().sendCloseWindow((short)27);
            }
        }
    }
    
    public static int getYear() {
        return 980 + getYearOffset();
    }
    
    public static int getYearOffset() {
        return (int)(WurmCalendar.currentTime / 29030400L);
    }
    
    public static int getStarfallWeek() {
        return (int)(WurmCalendar.currentTime % 29030400L / 604800L);
    }
    
    public static int getStarfall() {
        return (int)(WurmCalendar.currentTime % 29030400L / 2419200L);
    }
    
    public static int getDay() {
        final int day = (int)(WurmCalendar.currentTime % 29030400L / 86400L);
        final int dayOfWeek = day % 7;
        return dayOfWeek;
    }
    
    public static int getHour() {
        return (int)(WurmCalendar.currentTime % 86400L / 3600L);
    }
    
    public static int getMinute() {
        return (int)(WurmCalendar.currentTime % 3600L / 60L);
    }
    
    public static int getSecond() {
        return (int)(WurmCalendar.currentTime % 60L);
    }
    
    public static void incrementHour() {
        setTime(WurmCalendar.currentTime + 3600L);
    }
    
    protected static void setTime(final long time) {
        WurmCalendar.currentTime = time;
    }
    
    public static boolean isNight() {
        final int h = getHour();
        return h > 20 || h < 6;
    }
    
    public static boolean isMorning() {
        final int h = getHour();
        return h <= 8 && h >= 2;
    }
    
    public static boolean isNewYear1() {
        return nowIsBetween(0, 1, 1, 0, Year.now().getValue(), 0, 5, 1, 0, Year.now().getValue());
    }
    
    public static boolean isAfterNewYear1() {
        return nowIsAfter(0, 5, 1, 0, Year.now().getValue());
    }
    
    public static boolean toggleSpecial(final String special) {
        WurmCalendar.wasTestChristmas = WurmCalendar.isTestChristmas;
        WurmCalendar.isTestChristmas = false;
        WurmCalendar.isTestEaster = false;
        WurmCalendar.isTestWurm = false;
        WurmCalendar.isTestHalloween = false;
        switch (special) {
            case "xmas": {
                return WurmCalendar.isTestChristmas = true;
            }
            case "easter": {
                return WurmCalendar.isTestEaster = true;
            }
            case "wurm": {
                return WurmCalendar.isTestWurm = true;
            }
            case "halloween": {
                return WurmCalendar.isTestHalloween = true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isTestChristmas() {
        return WurmCalendar.isTestChristmas;
    }
    
    public static boolean isChristmas() {
        return WurmCalendar.isTestChristmas || nowIsBetween(15, 0, 23, 11, Year.now().getValue(), 12, 0, 31, 11, Year.now().getValue());
    }
    
    public static boolean isBeforeChristmas() {
        return nowIsBefore(17, 0, 23, 11, Year.now().getValue());
    }
    
    public static boolean isAfterChristmas() {
        return nowIsAfter(12, 0, 31, 11, Year.now().getValue());
    }
    
    public static boolean isAfterEaster() {
        final Calendar c = EasterCalculator.findHolyDay(Year.now().getValue());
        return nowIsAfter(10, 0, c.get(5) + 2, c.get(2), c.get(1));
    }
    
    public static boolean isEaster() {
        if (WurmCalendar.isTestEaster) {
            return true;
        }
        final Calendar c = EasterCalculator.findHolyDay(Year.now().getValue());
        return nowIsAfter(10, 0, c.get(5), c.get(2), c.get(1)) && nowIsBefore(10, 0, c.get(5) + 2, c.get(2), c.get(1));
    }
    
    public static boolean isHalloween() {
        return WurmCalendar.isTestHalloween || nowIsBetween(0, 1, 28, 9, Year.now().getValue(), 23, 59, 5, 10, Year.now().getValue());
    }
    
    public static boolean isAnniversary() {
        return WurmCalendar.isTestWurm || nowIsBetween(0, 1, 6, 5, Year.now().getValue(), 23, 59, 12, 5, Year.now().getValue());
    }
    
    public static String getSpecialMapping(final boolean predot) {
        if (isChristmas()) {
            return predot ? ".xmas" : "xmas.";
        }
        if (isEaster()) {
            return predot ? ".easter" : "easter.";
        }
        if (isHalloween()) {
            return predot ? ".halloween" : "halloween.";
        }
        if (isAnniversary()) {
            return predot ? ".wurm" : "wurm.";
        }
        return "";
    }
    
    public static void checkSpring() {
    }
    
    public static boolean isSpring() {
        final int starfall = getStarfall();
        return starfall > 2 && starfall < 6;
    }
    
    public static boolean isSummer() {
        final int starfall = getStarfall();
        return starfall > 5 && starfall < 9;
    }
    
    public static boolean isAutumn() {
        final int starfall = getStarfall();
        return starfall > 8 && starfall < 12;
    }
    
    public static boolean isWinter() {
        final int starfall = getStarfall();
        return starfall > 11 || starfall < 3;
    }
    
    public static boolean isAutumnWinter() {
        final int starfall = getStarfall();
        return starfall > 8 || starfall < 3;
    }
    
    public static boolean isSeasonSpring() {
        final int starfallWeek = getStarfallWeek();
        return starfallWeek >= 2 && starfallWeek < 12;
    }
    
    public static boolean isSeasonSummer() {
        final int starfallWeek = getStarfallWeek();
        return starfallWeek >= 12 && starfallWeek < 35;
    }
    
    public static boolean isSeasonAutumn() {
        final int starfallWeek = getStarfallWeek();
        return starfallWeek >= 35 && starfallWeek < 45;
    }
    
    public static boolean isSeasonWinter() {
        final int starfallWeek = getStarfallWeek();
        return starfallWeek >= 46 || starfallWeek < 2;
    }
    
    public static int getSeasonNumber() {
        int season = 0;
        if (isWinter()) {
            season = 4;
        }
        if (isSpring()) {
            season = 0;
        }
        if (isSummer()) {
            season = 2;
        }
        if (isAutumn()) {
            season = 3;
        }
        return season;
    }
    
    public static boolean nowIsBetween(final int shour, final int sminute, final int sday, final int smonth, final int syear, final int ehour, final int eminute, final int eday, final int emonth, final int eyear) {
        final Calendar start = Calendar.getInstance();
        start.set(syear, smonth, sday, shour, sminute);
        final long startTime = start.getTimeInMillis();
        final Calendar end = Calendar.getInstance();
        end.set(eyear, emonth, eday, ehour, eminute);
        final long endTime = end.getTimeInMillis();
        final long now = System.currentTimeMillis();
        return now >= startTime && now <= endTime;
    }
    
    public static boolean nowIsBefore(final int shour, final int sminute, final int sday, final int smonth, final int syear) {
        final Calendar start = Calendar.getInstance();
        start.set(syear, smonth, sday, shour, sminute);
        return System.currentTimeMillis() < start.getTimeInMillis();
    }
    
    public static boolean nowIsAfter(final int hour, final int minute, final int day, final int month, final int year) {
        final Calendar cnow = Calendar.getInstance();
        cnow.set(year, month, day, hour, minute);
        return System.currentTimeMillis() > cnow.getTimeInMillis();
    }
    
    public static String formatGmt(final long time) {
        return WurmCalendar.gmtDateFormat.format(new Date(time)) + " GMT";
    }
    
    public static long getCurrentTime() {
        return WurmCalendar.currentTime;
    }
    
    static {
        (gmtDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss")).setTimeZone(TimeZone.getTimeZone("GMT"));
        day_names = new String[] { "day of the Ant", "Luck day", "day of the Wurm", "Wrath day", "day of Tears", "day of Sleep", "day of Awakening" };
        logger = Logger.getLogger(WurmCalendar.class.getName());
        WurmCalendar.isSpring = false;
        WurmCalendar.isTestChristmas = false;
        WurmCalendar.wasTestChristmas = false;
        WurmCalendar.isTestEaster = false;
        WurmCalendar.isTestWurm = false;
        WurmCalendar.isTestHalloween = false;
        WurmCalendar.personalGoalsActive = nowIsBefore(0, 1, 1, 1, 2019);
        WurmCalendar.currentTime = 0L;
        WurmCalendar.lastHarvestableCheck = 0L;
        starfall_names = new String[] { "the starfall of Diamonds", "the starfall of the Saw", "the starfall of the Digging", "the starfall of the Leaf", "the Bear's starfall", "the Snake's starfall", "the White Shark starfall", "the starfall of Fires", "the Raven's starfall", "the starfall of Dancers", "the starfall of Omens", "the starfall of Silence" };
    }
    
    static final class Ticker implements Runnable
    {
        @Override
        public void run() {
            if (WurmCalendar.logger.isLoggable(Level.FINEST)) {
                WurmCalendar.logger.finest("Running newSingleThreadScheduledExecutor for calling WurmCalendar.tickSecond()");
            }
            try {
                final long now = System.nanoTime();
                try {
                    WurmCalendar.tickSecond();
                }
                catch (Exception e) {
                    WurmCalendar.logger.log(Level.WARNING, "Exception in WurmCalendar.tickSecond");
                    e.printStackTrace();
                }
                final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                if (lElapsedTime > Constants.lagThreshold) {
                    WurmCalendar.logger.info("Finished calling WurmCalendar.tickSecond(), which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e2) {
                WurmCalendar.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while calling WurmCalendar.tickSecond()", e2);
                throw e2;
            }
        }
    }
}
