// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.statistics;

import java.util.Iterator;
import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Date;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.PlayerInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class ChallengeSummary implements MiscConstants
{
    private static final String loadAllScores = "SELECT * FROM CHALLENGE";
    private static final String insertScore = "INSERT INTO CHALLENGE(LASTUPDATED,WURMID,ROUND,TYPE,POINTS,LASTPOINTS) VALUES (?,?,?,?,?,?)";
    private static final String updateScore = "UPDATE CHALLENGE SET LASTUPDATED=?,POINTS=?,LASTPOINTS=? WHERE WURMID=? AND ROUND=? AND TYPE=?";
    private static final Logger logger;
    private static final Map<Long, ChallengeSummary> allScores;
    private static boolean isDirty;
    private final long wid;
    private final String name;
    private static final ChallengeScore[] topScores;
    private static final String[] topScorers;
    private final ConcurrentHashMap<Integer, ChallengeRound> privateRounds;
    static final String start = "<TABLE id=\"gameDataTable\">\n\t\t<TR class=\"gameDataTopTenTR\">\n\t\t\t<TH>Name</TH>\n\t\t\t<TH>Points</TH>\n\t\t\t<TH>Last points</TH>\n\t\t\t<TH>Date</TH>\n\t\t</TR>\n\t\t";
    private static final String header = "<!DOCTYPE html><HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><TITLE>Wurm Online Challenge Standings</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t";
    private static final String rootdir = "/var/www/challenge/";
    private static String headerFilename;
    private static final String mainHeader = "<!DOCTYPE html><HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><TITLE>Wurm Online Challenge Standings</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t";
    static final String headerStart = "<TABLE id=\"gameDataTable\">\n\t\t<TR>\n\t\t\t<TH>Challenge</TH>\n\t\t\t<TH>Leader</TH>\n\t\t\t<TH>Points</TH>\n\t\t\t<TH>Last Points</TH>\n\t\t\t<TH>Date</TH>\n\t\t</TR>\n\t\t";
    private static final String tablefooter = "</TABLE>\n\n";
    private static final String pagefooter = "</BODY>\n</HTML>";
    private boolean fileExists;
    private String filename;
    static boolean writing;
    static final String hscpStart = "<TABLE id=\"gameDataTable\">\n\t\t<TR>\n\t\t\t<TH>Rank</TH>\n\t\t\t<TH>Name</TH>\n\t\t\t<TH>Points</TH>\n\t\t\t<TH>Last Points</TH>\n\t\t\t<TH>Date</TH>\n\t\t</TR>\n\t\t";
    
    public ChallengeSummary(final long wurmId, final String playerName) {
        this.privateRounds = new ConcurrentHashMap<Integer, ChallengeRound>();
        this.fileExists = false;
        this.filename = "";
        this.wid = wurmId;
        this.name = playerName;
    }
    
    public static final void addToScore(final PlayerInfo pid, final int scoreType, final float added) {
        if (pid.getPower() > 0) {
            return;
        }
        if (added != 0.0f) {
            boolean newScore = false;
            ChallengeSummary summary = getSummary(pid.wurmId);
            if (summary == null) {
                summary = new ChallengeSummary(pid.wurmId, pid.getName());
                addChallengeSummary(summary);
            }
            ChallengeRound round = summary.getPrivateChallengeRound(ChallengePointEnum.ChallengeScenario.current.getNum());
            if (round == null) {
                round = new ChallengeRound(ChallengePointEnum.ChallengeScenario.current.getNum());
                summary.addPrivateChallengeRound(round);
            }
            ChallengeScore scoreObj = round.getCurrentScoreForType(scoreType);
            if (scoreObj == null) {
                scoreObj = new ChallengeScore(scoreType, added, System.currentTimeMillis(), added);
                newScore = true;
            }
            else {
                scoreObj.setPoints(scoreObj.getPoints() + added);
                scoreObj.setLastPoints(added);
            }
            round.setScore(scoreObj);
            ChallengePointEnum.ChallengePoint.fromInt(scoreType).setDirty(true);
            if (newScore) {
                createScore(pid.wurmId, ChallengePointEnum.ChallengeScenario.current.getNum(), scoreObj);
            }
            else {
                updateScore(pid.wurmId, ChallengePointEnum.ChallengeScenario.current.getNum(), scoreObj);
            }
            if (checkIfTopScore(scoreObj, pid)) {
                try {
                    final Player player = Players.getInstance().getPlayer(pid.wurmId);
                    player.getCommunicator().sendSafeServerMessage("New High Score: " + ChallengePointEnum.ChallengePoint.fromInt(scoreType).getName() + " " + scoreObj.getPoints() + "!");
                }
                catch (NoSuchPlayerException ex) {}
            }
            if (scoreType == ChallengePointEnum.ChallengePoint.OVERALL.getEnumtype()) {
                summary.saveCurrentPersonalHtmlPage();
            }
        }
    }
    
    private static final boolean checkIfTopScore(final ChallengeScore score, final PlayerInfo pinf) {
        if (score.getType() != 0) {
            if (ChallengeSummary.topScores[score.getType()] == null && score.getPoints() > 0.0f) {
                ChallengeSummary.topScores[score.getType()] = score;
                ChallengeSummary.topScorers[score.getType()] = pinf.getName();
                return true;
            }
            if (score.getPoints() > 0.0f && score.getPoints() > ChallengeSummary.topScores[score.getType()].getPoints()) {
                ChallengeSummary.topScores[score.getType()] = score;
                ChallengeSummary.topScorers[score.getType()] = pinf.getName();
                return true;
            }
        }
        return false;
    }
    
    public static final void addScoreFromLoad(final PlayerInfo pid, final int roundNumber, final ChallengeScore score) {
        ChallengeSummary summary = getSummary(pid.wurmId);
        if (summary == null) {
            summary = new ChallengeSummary(pid.wurmId, pid.getName());
            addChallengeSummary(summary);
        }
        ChallengeRound round = summary.getPrivateChallengeRound(roundNumber);
        if (round == null) {
            round = new ChallengeRound(roundNumber);
            summary.addPrivateChallengeRound(round);
        }
        round.setScore(score);
        checkIfTopScore(score, pid);
    }
    
    public static final void addChallengeSummary(final ChallengeSummary summary) {
        ChallengeSummary.allScores.put(summary.getPlayerId(), summary);
    }
    
    public static final ChallengeSummary getSummary(final long playerId) {
        return ChallengeSummary.allScores.get(playerId);
    }
    
    public static final ChallengeRound getRoundSummary(final long playerId, final int round) {
        final ChallengeSummary summary = ChallengeSummary.allScores.get(playerId);
        if (summary != null) {
            return summary.getPrivateChallengeRound(round);
        }
        return null;
    }
    
    public final ChallengeRound getPrivateChallengeRound(final int round) {
        return this.privateRounds.get(round);
    }
    
    public final void addPrivateChallengeRound(final ChallengeRound round) {
        this.privateRounds.put(round.getRound(), round);
    }
    
    public final long getPlayerId() {
        return this.wid;
    }
    
    public final String getPlayerName() {
        return this.name;
    }
    
    public static final void loadLocalChallengeScores() {
        if (Servers.localServer.isChallengeServer()) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            int loadedScores = 0;
            final long lStart = System.nanoTime();
            try {
                dbcon = DbConnector.getLoginDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM CHALLENGE");
                rs = ps.executeQuery();
                while (rs.next()) {
                    final long wurmid = rs.getLong("WURMID");
                    final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmid);
                    final int round = rs.getInt("ROUND");
                    final float points = rs.getFloat("POINTS");
                    final int scoreType = rs.getInt("TYPE");
                    final long lastUpdated = rs.getLong("LASTUPDATED");
                    final long lastAdded = rs.getLong("LASTPOINTS");
                    if (pinf != null) {
                        addScoreFromLoad(pinf, round, new ChallengeScore(scoreType, points, lastUpdated, lastAdded));
                    }
                    ++loadedScores;
                }
            }
            catch (SQLException sqx) {
                ChallengeSummary.logger.log(Level.WARNING, "Failed to load scores, SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
                final Exception lNext = sqx.getNextException();
                if (lNext != null) {
                    ChallengeSummary.logger.log(Level.WARNING, "Failed to load scores, Next Exception", lNext);
                }
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
                final long end = System.nanoTime();
                ChallengeSummary.logger.info("Loaded " + loadedScores + " challenge scores from database took " + (end - lStart) / 1000000.0f + " ms");
            }
        }
    }
    
    public static final void createScore(final long pid, final int round, final ChallengeScore score) {
        try {
            if (Servers.localServer.isChallengeServer()) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                final ResultSet rs = null;
                try {
                    dbcon = DbConnector.getLoginDbCon();
                    ps = dbcon.prepareStatement("INSERT INTO CHALLENGE(LASTUPDATED,WURMID,ROUND,TYPE,POINTS,LASTPOINTS) VALUES (?,?,?,?,?,?)");
                    ps.setLong(1, score.getLastUpdated());
                    ps.setLong(2, pid);
                    ps.setInt(3, round);
                    ps.setInt(4, score.getType());
                    ps.setFloat(5, score.getPoints());
                    ps.setFloat(6, score.getLastPoints());
                    ps.execute();
                }
                catch (SQLException sqx) {
                    ChallengeSummary.logger.log(Level.WARNING, "Failed to save score " + pid + "," + round + "," + score.getPoints() + ", SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
                    final Exception lNext = sqx.getNextException();
                    if (lNext != null) {
                        ChallengeSummary.logger.log(Level.WARNING, "Failed to save scores, Next Exception", lNext);
                    }
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, rs);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
        catch (Exception ex) {
            ChallengeSummary.logger.log(Level.WARNING, "Exception saving challenge score " + ex.getMessage(), ex);
        }
    }
    
    public static final void updateScore(final long pid, final int round, final ChallengeScore score) {
        try {
            if (Servers.localServer.isChallengeServer()) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                final ResultSet rs = null;
                try {
                    dbcon = DbConnector.getLoginDbCon();
                    ps = dbcon.prepareStatement("UPDATE CHALLENGE SET LASTUPDATED=?,POINTS=?,LASTPOINTS=? WHERE WURMID=? AND ROUND=? AND TYPE=?");
                    ps.setLong(1, score.getLastUpdated());
                    ps.setFloat(2, score.getPoints());
                    ps.setFloat(3, score.getLastPoints());
                    ps.setLong(4, pid);
                    ps.setInt(5, round);
                    ps.setInt(6, score.getType());
                    ps.executeUpdate();
                }
                catch (SQLException sqx) {
                    ChallengeSummary.logger.log(Level.WARNING, "Failed to save score " + pid + "," + round + "," + score.getPoints() + ", SqlState: " + sqx.getSQLState() + ", ErrorCode: " + sqx.getErrorCode(), sqx);
                    final Exception lNext = sqx.getNextException();
                    if (lNext != null) {
                        ChallengeSummary.logger.log(Level.WARNING, "Failed to load scores, Next Exception", lNext);
                    }
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, rs);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
        catch (Exception ex) {
            ChallengeSummary.logger.log(Level.WARNING, "Exception " + ex.getMessage(), ex);
        }
    }
    
    private final File createFile() {
        if (!this.fileExists) {
            final String dir = "/var/www/challenge/" + this.name.substring(0, 1) + File.separator;
            final File dirFile = new File(dir.toLowerCase());
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            this.fileExists = true;
            this.filename = dir.toLowerCase() + this.name.toLowerCase() + ".html";
        }
        return new File(this.filename);
    }
    
    public final void saveCurrentPersonalHtmlPage() {
        ChallengeSummary.isDirty = true;
        new Thread() {
            @Override
            public void run() {
                Writer output = null;
                try {
                    final File aFile = ChallengeSummary.this.createFile();
                    output = new BufferedWriter(new FileWriter(aFile));
                    output.write("<!DOCTYPE html><HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><TITLE>Wurm Online Challenge Standings</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t");
                    output.write("<H1>Summary for " + ChallengeSummary.this.name + "</H1>\n\t<br>");
                    for (final ChallengePointEnum.ChallengeScenario scenario : ChallengePointEnum.ChallengeScenario.getScenarios()) {
                        if (scenario.getNum() > 0) {
                            final ChallengeRound summary = ChallengeSummary.this.getPrivateChallengeRound(scenario.getNum());
                            if (summary != null) {
                                output.write("<img src=\"" + summary.getRoundIcon() + "\" alt=\"round icon\"/><p><a href=\"../main" + summary.getRound() + ".html\">" + summary.getRoundName() + "</a></p>\n\t");
                                try {
                                    output.write("<TABLE id=\"gameDataTable\">\n\t\t<TR class=\"gameDataTopTenTR\">\n\t\t\t<TH>Name</TH>\n\t\t\t<TH>Points</TH>\n\t\t\t<TH>Last points</TH>\n\t\t\t<TH>Date</TH>\n\t\t</TR>\n\t\t");
                                }
                                catch (IOException iox) {
                                    ChallengeSummary.logger.log(Level.WARNING, iox.getMessage(), iox);
                                }
                                for (final ChallengeScore score : summary.getScores()) {
                                    if (score.getType() != 0) {
                                        output.write("<TR class=\"gameDataTopTenTR\">\n\t\t\t<TD class=\"gameDataTopTenTDName\">" + ChallengePointEnum.ChallengePoint.fromInt(score.getType()).getName() + "</TD>\n\t\t\t<TD class=\"gameDataTopTenTDValue\">" + score.getPoints() + "</TD>\n\t\t\t<TD>" + score.getLastPoints() + "</TD>\n\t\t\t<TD>" + new Date(score.getLastUpdated()) + "</TD>\n\t\t</TR>\n\t\t");
                                    }
                                }
                                output.write("</TABLE>\n\n");
                            }
                        }
                    }
                    output.write("</BODY>\n</HTML>");
                }
                catch (IOException iox2) {
                    ChallengeSummary.logger.log(Level.WARNING, "Failed to close html file for " + ChallengeSummary.this.name, iox2);
                }
                finally {
                    try {
                        if (output != null) {
                            output.close();
                        }
                    }
                    catch (IOException ex) {}
                }
            }
        }.start();
    }
    
    private static final File createHeaderFile() {
        return new File(ChallengeSummary.headerFilename);
    }
    
    private static final String getHighScoreUrl(final int pointType) {
        return ChallengePointEnum.ChallengePoint.fromInt(pointType).getName().replace(" ", "").trim().toLowerCase() + ChallengePointEnum.ChallengeScenario.current.getNum() + ".html";
    }
    
    private static final String getPlayerHomePageUrl(final String playerName) {
        return playerName.substring(0, 1).toLowerCase() + "/" + playerName.toLowerCase() + ".html";
    }
    
    public static final void saveCurrentGlobalHtmlPage() {
        if (ChallengeSummary.isDirty) {
            ChallengeSummary.isDirty = false;
            if (!ChallengeSummary.writing) {
                ChallengeSummary.writing = true;
                new Thread() {
                    @Override
                    public void run() {
                        Writer output = null;
                        try {
                            final File aFile = createHeaderFile();
                            output = new BufferedWriter(new FileWriter(aFile));
                            output.write("<!DOCTYPE html><HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><TITLE>Wurm Online Challenge Standings</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t");
                            output.write("<H1>Summary for " + ChallengePointEnum.ChallengeScenario.current.getName() + "</H1>\n\t<br>");
                            output.write("<img src=\"" + ChallengePointEnum.ChallengeScenario.current.getUrl() + "\" alt=\"round icon\"/><p>" + ChallengePointEnum.ChallengeScenario.current.getDesc() + "</p>\n\t");
                            try {
                                output.write("<TABLE id=\"gameDataTable\">\n\t\t<TR>\n\t\t\t<TH>Challenge</TH>\n\t\t\t<TH>Leader</TH>\n\t\t\t<TH>Points</TH>\n\t\t\t<TH>Last Points</TH>\n\t\t\t<TH>Date</TH>\n\t\t</TR>\n\t\t");
                            }
                            catch (IOException iox) {
                                ChallengeSummary.logger.log(Level.WARNING, iox.getMessage(), iox);
                            }
                            for (int x = 0; x < ChallengeSummary.topScores.length; ++x) {
                                if (ChallengeSummary.topScores[x] != null) {
                                    final String scorerUrl = getPlayerHomePageUrl(ChallengeSummary.topScorers[x]);
                                    output.write("<TR class=\"gameDataTopTenTR\">\n\t\t\t<TD class=\"gameDataTopTenTDName\"><a href=\"" + getHighScoreUrl(ChallengeSummary.topScores[x].getType()) + "\">" + ChallengePointEnum.ChallengePoint.fromInt(ChallengeSummary.topScores[x].getType()).getName() + "</a></TD>\n\t\t\t<TD class=\"gameDataTopTenTDName\"><a href=\"" + scorerUrl.toLowerCase() + "\">" + ChallengeSummary.topScorers[x] + "</a></TD>\n\t\t\t<TD class=\"gameDataTopTenTDValue\">" + ChallengeSummary.topScores[x].getPoints() + "</TD>\n\t\t\n\t\t<TR>\n\t\t\t<TH>" + ChallengeSummary.topScores[x].getLastPoints() + "</TH>\n\t\t\t<TH>" + new Date(ChallengeSummary.topScores[x].getLastUpdated()) + "</TH>\n\t\t</TR>\n\t\t");
                                }
                            }
                            output.write("</TABLE>\n\n");
                            output.write("</BODY>\n</HTML>");
                            for (final ChallengePointEnum.ChallengePoint point : ChallengePointEnum.ChallengePoint.getTypes()) {
                                if (point.getEnumtype() > 0 && point.isDirty()) {
                                    ChallengeSummary.createHighScorePage(point.getEnumtype());
                                }
                            }
                        }
                        catch (IOException iox2) {
                            ChallengeSummary.logger.log(Level.WARNING, "Failed to close html file for main page", iox2);
                        }
                        finally {
                            try {
                                if (output != null) {
                                    output.close();
                                }
                            }
                            catch (IOException ex) {}
                        }
                        ChallengeSummary.writing = false;
                    }
                }.start();
            }
        }
    }
    
    public static final void createHighScorePage(final int scoreType) {
        final String fileName = "/var/www/challenge/" + getHighScoreUrl(scoreType);
        final File aFile = new File(fileName);
        Writer output = null;
        try {
            final ChallengePointEnum.ChallengePoint point = ChallengePointEnum.ChallengePoint.fromInt(scoreType);
            output = new BufferedWriter(new FileWriter(aFile));
            output.write("<!DOCTYPE html><HTML>\n\t<HEAD>\n\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><TITLE>Wurm Online Challenge Standings</TITLE>\n\t<link rel=\"stylesheet\" type=\"text/css\" href=\"http://www.wurmonline.com/css/gameData.css\" />\n\t</HEAD>\n\n<BODY id=\"body\" class=\"gameDataBody\">\n\t");
            output.write("<H1>Summary for " + point.getName() + "</H1>\n\t<br>");
            output.write("<img src=\"" + ChallengePointEnum.ChallengeScenario.current.getUrl() + "\" alt=\"round icon\"/><p><a href=\"main" + ChallengePointEnum.ChallengeScenario.current.getNum() + ".html\">" + ChallengePointEnum.ChallengeScenario.current.getName() + "</a></p>\n\t");
            try {
                output.write("<TABLE id=\"gameDataTable\">\n\t\t<TR>\n\t\t\t<TH>Rank</TH>\n\t\t\t<TH>Name</TH>\n\t\t\t<TH>Points</TH>\n\t\t\t<TH>Last Points</TH>\n\t\t\t<TH>Date</TH>\n\t\t</TR>\n\t\t");
            }
            catch (IOException iox) {
                ChallengeSummary.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            final ConcurrentSkipListSet<ScoreNamePair> scores = new ConcurrentSkipListSet<ScoreNamePair>();
            for (final ChallengeSummary summary : ChallengeSummary.allScores.values()) {
                final ChallengeRound round = summary.getPrivateChallengeRound(ChallengePointEnum.ChallengeScenario.current.getNum());
                if (round != null) {
                    final ChallengeScore[] scores2;
                    final ChallengeScore[] scoreArr = scores2 = round.getScores();
                    for (final ChallengeScore score : scores2) {
                        if (score.getType() == scoreType && score.getPoints() > 0.0f) {
                            scores.add(new ScoreNamePair(summary.getPlayerName(), score));
                            break;
                        }
                    }
                }
            }
            final ScoreNamePair[] topScoreArr = scores.toArray(new ScoreNamePair[scores.size()]);
            Arrays.sort(topScoreArr);
            for (int x = 0; x < topScoreArr.length; ++x) {
                if (topScoreArr[x] != null) {
                    final String scorerUrl = getPlayerHomePageUrl(topScoreArr[x].name);
                    output.write("<TR class=\"gameDataTopTenTR\">\n\t\t\t<TD class=\"gameDataTopTenTDValue\">" + (x + 1) + "</TD>\n\t\t\t<TD class=\"gameDataTopTenTDName\"><a href=\"" + scorerUrl + "\">" + topScoreArr[x].name + "</a></TD>\n\t\t\t<TD class=\"gameDataTopTenTDValue\">" + topScoreArr[x].score.getPoints() + "</TD>\n\t\t\n\t\t<TR>\n\t\t\t<TH>" + topScoreArr[x].score.getLastPoints() + "</TH>\n\t\t\t<TH>" + new Date(topScoreArr[x].score.getLastUpdated()) + "</TH>\n\t\t</TR>\n\t\t");
                }
            }
            output.write("</TABLE>\n\n");
            output.write("</BODY>\n</HTML>");
        }
        catch (IOException iox2) {
            ChallengeSummary.logger.log(Level.WARNING, "Failed to close html file for main page", iox2);
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (IOException ex) {}
        }
        ChallengePointEnum.ChallengePoint.fromInt(scoreType).setDirty(false);
    }
    
    static {
        logger = Logger.getLogger(ChallengeSummary.class.getName());
        allScores = new ConcurrentHashMap<Long, ChallengeSummary>();
        ChallengeSummary.isDirty = false;
        topScores = new ChallengeScore[ChallengePointEnum.ChallengePoint.getTypes().length];
        topScorers = new String[ChallengePointEnum.ChallengePoint.getTypes().length];
        ChallengeSummary.headerFilename = "/var/www/challenge/main" + ChallengePointEnum.ChallengeScenario.current.getNum() + ".html";
        ChallengeSummary.writing = false;
    }
}
