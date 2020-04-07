// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.OutputStream;
import com.wurmonline.shared.util.StreamUtilities;
import java.io.IOException;
import java.util.logging.Level;
import java.io.FileOutputStream;
import java.io.File;
import java.util.logging.Logger;

final class ServerStatistics
{
    private static final Logger logger;
    private static final String INSERT_SERVER_STATISTIC_INT = "insert into SERVER_STATS_LOG ( SERVER_ID, SERVER_NAME, STATISTIC_ID, VALUE_INT ) VALUES(?,?,?,?)";
    private static final int SERVER_ID;
    private static final String SERVER_NAME;
    private static final String STATISTIC_TYPE_NUMBER_OF_PREMIUM_PLAYERS = "numberOfPremiumPlayers";
    private static final String STATISTIC_TYPE_NUMBER_OF_PLAYERS = "numberOfPlayers";
    private static final String STATISTIC_TYPE_NUMBER_OF_CLIENT_IPS = "numberOfClientIps";
    private static final String STATISTIC_TYPE_NUMBER_OF_PLAYERS_THROUGH_TUTORIAL = "playersThroughTutorial";
    private static final String STATISTIC_TYPE_NUMBER_OF_PREMIUM_PLAYER_LOGONS = "logonsPrem";
    private static final String STATISTIC_TYPE_NUMBER_OF_PLAYER_LOGONS = "logons";
    private static final String STATISTIC_TYPE_NUMBER_OF_SECONDS_LAG = "secondsLag";
    private static final String STATISTIC_TYPE_NUMBER_OF_NEW_PLAYERS = "newbies";
    private static final String STATISTIC_TYPE_NUMBER_OF_PREMIUM_PLAYER_REGISTERED = "premiumPlayersRegistered";
    
    private static void writeIntegerStatisticsToMrtgFile(final String aStatisticType, final String aFileName, final int aFirstIntegerStatistic, final int aSecondIntegerStatistic) {
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(aFileName);
            fos = new FileOutputStream(file);
            fos.write((aFirstIntegerStatistic + "\n").getBytes());
            fos.write((aSecondIntegerStatistic + "\n\nwww.wurmonline.com").getBytes());
            fos.flush();
        }
        catch (IOException e) {
            ServerStatistics.logger.log(Level.WARNING, "Problem writing " + aStatisticType + " to file - " + e.getMessage(), e);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(fos);
        }
    }
    
    private static void storeOneIntegerStatisticInDatabase(final String aStatisticType, final int aIntegerStatisticValue) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("Going to log " + aStatisticType + " to database");
        }
        Connection logsConnection = null;
        PreparedStatement logsStatement = null;
        try {
            logsConnection = DbConnector.getLogsDbCon();
            logsStatement = logsConnection.prepareStatement("insert into SERVER_STATS_LOG ( SERVER_ID, SERVER_NAME, STATISTIC_ID, VALUE_INT ) VALUES(?,?,?,?)");
            logsStatement.setInt(1, ServerStatistics.SERVER_ID);
            logsStatement.setString(2, ServerStatistics.SERVER_NAME);
            logsStatement.setString(3, aStatisticType);
            logsStatement.setInt(4, aIntegerStatisticValue);
            logsStatement.execute();
        }
        catch (SQLException sqex) {
            ServerStatistics.logger.log(Level.WARNING, "Failed to store " + aStatisticType + " - " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(logsStatement, null);
            DbConnector.returnConnection(logsConnection);
        }
    }
    
    static void storeNumberOfPlayers(final int numberOfPremiumPlayers, final int numberOfPlayers) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfPlayers - numberOfPremiumPlayers: " + numberOfPremiumPlayers + ", numberOfPlayers: " + numberOfPlayers);
        }
        writeIntegerStatisticsToMrtgFile("numberOfPlayers", Constants.playerStatLog, numberOfPremiumPlayers, numberOfPlayers);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("numberOfPremiumPlayers", numberOfPremiumPlayers);
            storeOneIntegerStatisticInDatabase("numberOfPlayers", numberOfPlayers);
        }
    }
    
    static void storeNumberOfClientIps(final int numberOfClientIps) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfClientIps - numberOfClientIps: " + numberOfClientIps);
        }
        writeIntegerStatisticsToMrtgFile("numberOfClientIps", Constants.ipStatLog, numberOfClientIps, numberOfClientIps);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("numberOfClientIps", numberOfClientIps);
        }
    }
    
    static void storeNumberOfPlayersThroughTutorial(final int playersThroughTutorial) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfPlayersThroughTutorial - playersThroughTutorial: " + playersThroughTutorial);
        }
        writeIntegerStatisticsToMrtgFile("playersThroughTutorial", Constants.tutorialLog, playersThroughTutorial, playersThroughTutorial);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("playersThroughTutorial", playersThroughTutorial);
        }
    }
    
    static void storeNumberOfPlayerLogons(final int logonsPrem, final int logons) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfPlayerLogons - logonsPrem: " + logonsPrem + ", logons: " + logons);
        }
        writeIntegerStatisticsToMrtgFile("logons", Constants.logonStatLog, logonsPrem, logons);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("logonsPrem", logonsPrem);
            storeOneIntegerStatisticInDatabase("logons", logons);
        }
    }
    
    static void storeNumberOfSecondsLag(final int secondsLag) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfSecondsLag - secondsLag: " + secondsLag);
        }
        writeIntegerStatisticsToMrtgFile("secondsLag", Constants.lagLog, secondsLag, secondsLag);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("secondsLag", secondsLag);
        }
    }
    
    static void storeNumberOfNewPlayers(final int newbies) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfNewPlayers - newbies: " + newbies);
        }
        writeIntegerStatisticsToMrtgFile("newbies", Constants.newbieStatLog, newbies, newbies);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("newbies", newbies);
        }
    }
    
    static void storeNumberOfPayingPlayers(final int paying, final int payingMWithoutnewPremiums) {
        if (ServerStatistics.logger.isLoggable(Level.FINER)) {
            ServerStatistics.logger.finer("storeNumberOfPayingPlayers - paying: " + paying + ", payingMWithoutnewPremiums: " + payingMWithoutnewPremiums);
        }
        writeIntegerStatisticsToMrtgFile("premiumPlayersRegistered", Constants.payingLog, paying, payingMWithoutnewPremiums);
        if (Constants.useDatabaseForServerStatisticsLog) {
            storeOneIntegerStatisticInDatabase("premiumPlayersRegistered", paying);
        }
    }
    
    static {
        logger = Logger.getLogger(ServerStatistics.class.getName());
        SERVER_ID = Servers.getLocalServerId();
        SERVER_NAME = Servers.getLocalServerName();
    }
}
