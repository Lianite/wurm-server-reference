// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.kingdom;

import java.util.HashSet;
import java.util.HashMap;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.WurmCalendar;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Items;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.zones.Zones;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class King implements MiscConstants, TimeConstants
{
    private static final String CREATE_KING_ERA = "insert into KING_ERA ( ERA,KINGDOM,KINGDOMNAME, KINGID,KINGSNAME,GENDER,STARTTIME,STARTWURMTIME,STARTLANDPERCENT, CURRENTLANDPERCENT,      NEXTCHALLENGE,CURRENT) VALUES (?,?,?,?,?,?,?,?,?,?,  ?,1)";
    private static final String UPDATE_KING_ERA = "UPDATE KING_ERA SET KINGSNAME=?,GENDER=?,ENDTIME=?,ENDWURMTIME=?, CURRENTLANDPERCENT=?, CAPITAL=?, CURRENT=?,KINGDOM=? WHERE ERA=?";
    private static final String UPDATE_LEVELSKILLED = "UPDATE KING_ERA SET LEVELSKILLED=? WHERE ERA=?";
    private static final String UPDATE_LEVELSLOST = "UPDATE KING_ERA SET LEVELSLOST=? WHERE ERA=?";
    private static final String UPDATE_APPOINTMENTS = "UPDATE KING_ERA SET APPOINTMENTS=? WHERE ERA=?";
    private static final String GET_ALL_KING_ERA = "select * FROM KING_ERA";
    private static final String UPDATE_CHALLENGES = "UPDATE KING_ERA SET NEXTCHALLENGE=?,DECLINEDCHALLENGES=?,ACCEPTDATE=?,CHALLENGEDATE=? WHERE ERA=?";
    public String kingdomName;
    private static Logger logger;
    public static int currentEra;
    public int era;
    public String kingName;
    public long kingid;
    private long startTime;
    private long endTime;
    public long startWurmTime;
    public long endWurmTime;
    public float startLand;
    public float currentLand;
    public int appointed;
    public int levelskilled;
    public int levelslost;
    public boolean current;
    public byte kingdom;
    private long nextChallenge;
    private int declinedChallenges;
    private long challengeDate;
    private long acceptDate;
    public byte gender;
    public String capital;
    private String rulerMaleTitle;
    private String rulerFemaleTitle;
    private static King kingJenn;
    private static King kingMolRehan;
    private static King kingHots;
    private Appointments appointments;
    public static final Map<Integer, King> eras;
    public static final Map<Long, Integer> challenges;
    private static final int challengesRequired;
    private static final int votesRequired;
    private static final Set<King> kings;
    private static final long challengeFactor;
    public static final float landPercentRequiredForBonus = 2.0f;
    long lastCapital;
    
    private King() {
        this.kingdomName = "unknown kingdom";
        this.era = 0;
        this.kingName = "";
        this.kingid = -10L;
        this.startTime = 0L;
        this.endTime = 0L;
        this.startWurmTime = 0L;
        this.endWurmTime = 0L;
        this.startLand = 0.0f;
        this.currentLand = 0.0f;
        this.appointed = 0;
        this.levelskilled = 0;
        this.levelslost = 0;
        this.current = false;
        this.kingdom = 0;
        this.nextChallenge = 0L;
        this.declinedChallenges = 0;
        this.challengeDate = 0L;
        this.acceptDate = 0L;
        this.gender = 0;
        this.capital = "";
        this.rulerMaleTitle = "Grand Prince";
        this.rulerFemaleTitle = "Grand Princess";
        this.appointments = null;
        this.lastCapital = System.currentTimeMillis();
        if (King.logger.isLoggable(Level.FINER)) {
            King.logger.finer("Creating new King");
        }
    }
    
    private static void addKing(final King king) {
        King.eras.put(king.era, king);
        King.logger.log(Level.INFO, "Loading kings, adding " + king.kingName);
        if (king.current) {
            if (king.kingdom == 1) {
                King.logger.log(Level.INFO, "Setting current jenn king: " + king.kingName);
                King.kingJenn = king;
            }
            else if (king.kingdom == 2) {
                King.logger.log(Level.INFO, "Setting current mol rehan king: " + king.kingName);
                King.kingMolRehan = king;
            }
            else if (king.kingdom == 3) {
                King.logger.log(Level.INFO, "Setting current hots king: " + king.kingName);
                King.kingHots = king;
            }
            King.kings.add(king);
        }
    }
    
    public static King getKing(final byte _kingdom) {
        if (_kingdom == 1) {
            return King.kingJenn;
        }
        if (_kingdom == 2) {
            return King.kingMolRehan;
        }
        if (_kingdom == 3) {
            return King.kingHots;
        }
        for (final King k : King.kings) {
            if (k.kingdom == _kingdom && k.current) {
                return k;
            }
        }
        return null;
    }
    
    public static boolean isKing(final long wurmid, final byte kingdom) {
        final King k = getKing(kingdom);
        return k != null && k.kingid == wurmid;
    }
    
    public static void purgeKing(final byte _kingdom) {
        Zones.calculateZones(true);
        if (_kingdom == 1) {
            if (King.kingJenn != null) {
                King.kingJenn.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                switchCurrent(King.kingJenn);
            }
            King.kingJenn = null;
            new Appointments(-1, (byte)1, true);
        }
        else if (_kingdom == 2) {
            if (King.kingMolRehan != null) {
                King.kingMolRehan.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                switchCurrent(King.kingMolRehan);
            }
            King.kingMolRehan = null;
            new Appointments(-2, (byte)2, true);
        }
        else if (_kingdom == 3) {
            if (King.kingHots != null) {
                King.kingHots.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                switchCurrent(King.kingHots);
            }
            King.kingHots = null;
            new Appointments(-3, (byte)3, true);
        }
        else {
            final King[] kings;
            final King[] kingarr = kings = getKings();
            for (final King k : kings) {
                if (k.kingdom == _kingdom) {
                    k.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                    switchCurrent(k);
                }
            }
        }
    }
    
    public static void pollKings() {
        final King[] kings;
        final King[] kingarr = kings = getKings();
        for (final King k : kings) {
            k.poll();
        }
    }
    
    public static final King[] getKings() {
        return King.kings.toArray(new King[King.kings.size()]);
    }
    
    private void poll() {
        if (System.currentTimeMillis() - this.appointments.lastChecked > 604800000L) {
            this.appointments.resetAppointments(this.kingdom);
            final Kingdom k = Kingdoms.getKingdom(this.kingdom);
            if (k.isCustomKingdom()) {
                final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(this.kingid);
                if (pinf != null && System.currentTimeMillis() - pinf.lastLogout > 2419200000L && System.currentTimeMillis() - pinf.lastLogin > 2419200000L) {
                    Items.deleteRoyalItemForKingdom(this.kingdom, true, false);
                    King.logger.log(Level.INFO, this.kingName + " has not logged in for a month. A new king for " + this.kingdomName + " will be found.");
                    purgeKing(this.kingdom);
                }
            }
        }
        else {
            final PlayerInfo pinf2 = PlayerInfoFactory.getPlayerInfoWithWurmId(this.kingid);
            if (pinf2 != null && pinf2.currentServer == Servers.localServer.id && !pinf2.isPaying()) {
                final Kingdom i = Kingdoms.getKingdom(this.kingdom);
                if (!i.isCustomKingdom()) {
                    Items.deleteRoyalItemForKingdom(this.kingdom, true, true);
                    King.logger.log(Level.INFO, this.kingName + " is no longer premium. Deleted the regalia.");
                    purgeKing(this.kingdom);
                    return;
                }
            }
            Zones.calculateZones(false);
            final float oldland = this.currentLand;
            this.currentLand = Zones.getPercentLandForKingdom(this.kingdom);
            if (oldland != this.currentLand) {
                King.logger.log(Level.INFO, "Saving " + this.kingName + " because new land is " + this.currentLand + " compared to " + oldland);
                this.save();
            }
            if (this.hasFailedToRespondToChallenge()) {
                HistoryManager.addHistory(this.kingName, "decided not to respond to a challenge.");
                Server.getInstance().broadCastAlert(this.kingName + " has decided not to respond to a challenge.");
                King.logger.log(Level.INFO, this.kingName + " did not respond to a challenge.");
                this.setChallengeDeclined();
                if (this.hasFailedAllChallenges()) {
                    HistoryManager.addHistory(this.kingName, "may now be voted away from the throne within one week at the duelling stone.");
                    Server.getInstance().broadCastAlert(this.getFullTitle() + " may now be voted away from the throne within one week at the duelling stone.");
                    King.logger.log(Level.INFO, this.kingName + " may now be voted away.");
                }
            }
            if (this.hasFailedAllChallenges()) {
                if (this.getVotesNeeded() == 0) {
                    this.removeByVote();
                }
                else if (this.getNextChallenge() < System.currentTimeMillis()) {
                    PlayerInfoFactory.resetVotesForKingdom(this.kingdom);
                    this.declinedChallenges = 0;
                    this.updateChallenges();
                    HistoryManager.addHistory(this.kingName, "was not voted away from the throne this time. The " + this.getRulerTitle() + " remains on the throne of " + this.kingdomName + ".");
                    Server.getInstance().broadCastNormal(this.kingName + " was not voted away from the throne this time. The " + this.getRulerTitle() + " remains on the throne of " + this.kingdomName + ".");
                    King.logger.log(Level.INFO, this.kingName + " may no longer be voted away.");
                }
            }
            if (this.acceptDate > 0L && System.currentTimeMillis() > this.acceptDate) {
                try {
                    final Player p = Players.getInstance().getPlayer(this.kingid);
                    if (p.isInOwnDuelRing()) {
                        if (Servers.isThisATestServer()) {
                            if (System.currentTimeMillis() - this.getChallengeAcceptedDate() > 300000L) {
                                this.passedChallenge();
                            }
                        }
                        else if (System.currentTimeMillis() - this.acceptDate > 1800000L) {
                            this.passedChallenge();
                        }
                        p.getCommunicator().sendAlertServerMessage("Unseen eyes watch you.");
                    }
                    else {
                        this.setFailedChallenge();
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.setFailedChallenge();
                }
            }
        }
    }
    
    public final void removeByVote() {
        HistoryManager.addHistory(this.kingName, "has been voted away from the throne by the people of " + this.kingdomName + "!");
        Server.getInstance().broadCastAlert(this.getFullTitle() + " has been voted away from the throne by the people of " + this.kingdomName + "!");
        Items.deleteRoyalItemForKingdom(this.kingdom, true, true);
        purgeKing(this.kingdom);
        King.logger.log(Level.INFO, this.kingName + " has been voted away.");
    }
    
    public final void removeByFailChallenge() {
        HistoryManager.addHistory(this.kingName, "has failed the challenge by the people of " + this.kingdomName + "!");
        Server.getInstance().broadCastNormal(this.getFullTitle() + " has failed the challenge by the people of " + this.kingdomName + "!");
        Items.deleteRoyalItemForKingdom(this.kingdom, true, true);
        purgeKing(this.kingdom);
        King.logger.log(Level.INFO, this.kingName + " has failed the challenge.");
    }
    
    private static void setRulerName(final King king) {
        king.rulerMaleTitle = getRulerTitle(true, king.kingdom);
        king.rulerFemaleTitle = getRulerTitle(false, king.kingdom);
    }
    
    public String getRulerTitle() {
        if (this.gender == 1) {
            return this.rulerFemaleTitle;
        }
        return this.rulerMaleTitle;
    }
    
    public static String getRulerTitle(final boolean male, final byte kingdom) {
        if (kingdom == 1) {
            if (male) {
                return "Grand Prince";
            }
            return "Grand Princess";
        }
        else if (kingdom == 2) {
            if (male) {
                return "Chancellor";
            }
            return "Chancellor";
        }
        else if (kingdom == 3) {
            if (male) {
                return "Emperor";
            }
            return "Empress";
        }
        else {
            if (male) {
                return "Chief";
            }
            return "Chieftain";
        }
    }
    
    public static void loadAllEra() {
        King.logger.log(Level.INFO, "Loading all kingdom eras.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("select * FROM KING_ERA");
            rs = ps.executeQuery();
            while (rs.next()) {
                final King k = new King();
                k.era = rs.getInt("ERA");
                k.kingdom = rs.getByte("KINGDOM");
                k.current = rs.getBoolean("CURRENT");
                if (k.era > King.currentEra) {
                    King.currentEra = k.era;
                }
                k.kingName = rs.getString("KINGSNAME");
                k.gender = rs.getByte("GENDER");
                k.startLand = rs.getFloat("STARTLANDPERCENT");
                k.startTime = rs.getLong("STARTTIME");
                k.endTime = rs.getLong("ENDTIME");
                k.startWurmTime = rs.getLong("STARTWURMTIME");
                k.endWurmTime = rs.getLong("ENDWURMTIME");
                k.currentLand = rs.getFloat("CURRENTLANDPERCENT");
                k.appointed = rs.getInt("APPOINTMENTS");
                k.levelskilled = rs.getInt("LEVELSKILLED");
                k.levelslost = rs.getInt("LEVELSLOST");
                k.capital = rs.getString("CAPITAL");
                k.kingid = rs.getLong("KINGID");
                k.appointed = rs.getInt("APPOINTMENTS");
                k.nextChallenge = rs.getLong("NEXTCHALLENGE");
                k.declinedChallenges = rs.getInt("DECLINEDCHALLENGES");
                k.acceptDate = rs.getLong("ACCEPTDATE");
                k.challengeDate = rs.getLong("CHALLENGEDATE");
                k.kingdomName = rs.getString("KINGDOMNAME");
                byte template = k.kingdom;
                final Kingdom kingd = Kingdoms.getKingdom(k.kingdom);
                if (kingd != null) {
                    template = kingd.getTemplate();
                    King.logger.log(Level.INFO, "Template for " + k.kingdom + "=" + template + " (" + kingd.getId() + ")");
                }
                k.appointments = new Appointments(k.era, template, k.current);
                setRulerName(k);
                addKing(k);
            }
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to load kingdom eras: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            King.logger.info("Loaded kingdom eras from database took " + (end - start) / 1000000.0f + " ms");
        }
        if (Appointments.jenn == null) {
            new Appointments(-1, (byte)1, true);
        }
        if (Appointments.hots == null) {
            new Appointments(-3, (byte)3, true);
        }
        if (Appointments.molr == null) {
            new Appointments(-2, (byte)2, true);
        }
        if (Appointments.none == null) {
            new Appointments(-5, (byte)0, true);
        }
    }
    
    public static void setToNoKingdom(final byte oldKingdom) {
        for (final King k : King.eras.values()) {
            if (k.kingdom == oldKingdom) {
                k.kingdom = 0;
                k.save();
            }
        }
        for (final King k : King.kings) {
            if (k.kingdom == oldKingdom) {
                k.kingdom = 0;
                k.save();
            }
        }
    }
    
    public static Appointments getCurrentAppointments(final byte kingdom) {
        final King k = getKing(kingdom);
        if (k != null && k.current) {
            return Appointments.getAppointments(k.era);
        }
        final Kingdom kingd = Kingdoms.getKingdom(kingdom);
        if (kingd != null) {
            return Appointments.getCurrentAppointments(kingd.getTemplate());
        }
        return null;
    }
    
    public void abdicate(final boolean isOnSurface, final boolean destroyItems) {
        Items.deleteRoyalItemForKingdom(this.kingdom, isOnSurface, destroyItems);
        purgeKing(this.kingdom);
    }
    
    public static King createKing(final byte _kingdom, final String kingname, final long kingwurmid, final byte kinggender) {
        final King k = new King();
        ++King.currentEra;
        k.era = King.currentEra;
        k.kingdom = _kingdom;
        k.kingid = kingwurmid;
        k.kingName = kingname;
        k.gender = kinggender;
        k.startTime = System.currentTimeMillis();
        k.startWurmTime = WurmCalendar.currentTime;
        k.nextChallenge = System.currentTimeMillis() + King.challengeFactor;
        k.kingdomName = Kingdoms.getNameFor(_kingdom);
        Zones.calculateZones(true);
        k.startLand = Zones.getPercentLandForKingdom(_kingdom);
        boolean foundCapital = false;
        try {
            final Player p = Players.getInstance().getPlayer(kingwurmid);
            p.achievement(321);
            if (p.getCitizenVillage() != null) {
                foundCapital = true;
                k.setCapital(p.getCitizenVillage().getName(), true);
            }
        }
        catch (NoSuchPlayerException ex) {}
        if (_kingdom == 1) {
            if (King.kingJenn != null) {
                King.kingJenn.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                switchCurrent(King.kingJenn);
            }
            King.kingJenn = k;
        }
        else if (_kingdom == 2) {
            if (King.kingMolRehan != null) {
                King.kingMolRehan.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                switchCurrent(King.kingMolRehan);
            }
            King.kingMolRehan = k;
        }
        else if (_kingdom == 3) {
            if (King.kingHots != null) {
                King.kingHots.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                switchCurrent(King.kingHots);
            }
            King.kingHots = k;
        }
        else {
            final King oldKing = getKing(_kingdom);
            if (oldKing != null) {
                oldKing.currentLand = Zones.getPercentLandForKingdom(_kingdom);
                King.logger.log(Level.INFO, "Found old king " + oldKing.kingName + " when creating new.");
                switchCurrent(oldKing);
                if (!foundCapital) {
                    k.setCapital(oldKing.capital, true);
                }
            }
        }
        k.currentLand = k.startLand;
        k.current = true;
        k.create();
        byte template = k.kingdom;
        final Kingdom kingd = Kingdoms.getKingdomOrNull(k.kingdom);
        if (kingd != null) {
            template = kingd.getTemplate();
            King.logger.log(Level.INFO, "Using " + Kingdoms.getNameFor(template) + " for " + kingd.getName());
        }
        k.appointments = new Appointments(k.era, template, k.current);
        setRulerName(k);
        addKing(k);
        HistoryManager.addHistory(k.kingName, "is appointed new " + k.getRulerTitle() + " of " + k.kingdomName);
        Items.transferRegaliaForKingdom(_kingdom, kingwurmid);
        pollKings();
        return k;
    }
    
    private static void switchCurrent(final King oldking) {
        oldking.endTime = System.currentTimeMillis();
        oldking.endWurmTime = WurmCalendar.currentTime;
        oldking.current = false;
        HistoryManager.addHistory(oldking.kingName, "no longer is the " + oldking.getRulerTitle() + " of " + oldking.kingdomName);
        Server.getInstance().broadCastNormal(oldking.kingName + " no longer is the " + oldking.getRulerTitle() + " of " + oldking.kingdomName);
        oldking.save();
        King.kings.remove(oldking);
        PlayerInfoFactory.resetVotesForKingdom(oldking.kingdom);
    }
    
    private void create() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("insert into KING_ERA ( ERA,KINGDOM,KINGDOMNAME, KINGID,KINGSNAME,GENDER,STARTTIME,STARTWURMTIME,STARTLANDPERCENT, CURRENTLANDPERCENT,      NEXTCHALLENGE,CURRENT) VALUES (?,?,?,?,?,?,?,?,?,?,  ?,1)");
            ps.setInt(1, this.era);
            ps.setByte(2, this.kingdom);
            ps.setString(3, this.kingdomName);
            ps.setLong(4, this.kingid);
            ps.setString(5, this.kingName);
            ps.setByte(6, this.gender);
            ps.setLong(7, this.startTime);
            ps.setLong(8, this.startWurmTime);
            ps.setFloat(9, this.startLand);
            ps.setFloat(10, this.currentLand);
            ps.setLong(11, this.nextChallenge);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to create kingdom for era " + this.era + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private final void save() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KING_ERA SET KINGSNAME=?,GENDER=?,ENDTIME=?,ENDWURMTIME=?, CURRENTLANDPERCENT=?, CAPITAL=?, CURRENT=?,KINGDOM=? WHERE ERA=?");
            ps.setString(1, this.kingName);
            ps.setByte(2, this.gender);
            ps.setLong(3, this.endTime);
            ps.setLong(4, this.endWurmTime);
            ps.setFloat(5, this.currentLand);
            ps.setString(6, this.capital);
            ps.setBoolean(7, this.current);
            ps.setByte(8, this.kingdom);
            ps.setInt(9, this.era);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to save kingdom for era " + this.era + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final boolean setCapital(final String newcapital, final boolean forced) {
        if (System.currentTimeMillis() - this.lastCapital > 21600000L || forced || Servers.isThisATestServer()) {
            this.capital = newcapital;
            this.lastCapital = System.currentTimeMillis();
            this.save();
            return true;
        }
        return false;
    }
    
    public final void setGender(final byte newgender) {
        this.gender = newgender;
        this.save();
    }
    
    public final void addAppointment(final Appointment app) {
        this.appointed += app.getLevel();
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KING_ERA SET APPOINTMENTS=? WHERE ERA=?");
            ps.setInt(1, this.appointed);
            ps.setInt(2, this.era);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to update appointed: " + this.appointed + " for era " + this.era + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void resetNextChallenge(final long nextTime) {
        this.nextChallenge = nextTime;
        King.challenges.clear();
        this.updateChallenges();
    }
    
    public final long getNextChallenge() {
        return this.nextChallenge;
    }
    
    public final void setChallengeDate() {
        this.challengeDate = System.currentTimeMillis();
        this.updateChallenges();
    }
    
    public final long getChallengeDate() {
        return this.challengeDate;
    }
    
    public final void setChallengeAccepted(final long date) {
        this.acceptDate = date;
        this.challengeDate = 0L;
        this.resetNextChallenge(this.acceptDate + King.challengeFactor * (3 - this.declinedChallenges));
        this.updateChallenges();
    }
    
    public final void setChallengeDeclined() {
        this.resetNextChallenge(System.currentTimeMillis() + King.challengeFactor);
        this.challengeDate = 0L;
        ++this.declinedChallenges;
        this.updateChallenges();
    }
    
    public final long getChallengeAcceptedDate() {
        return this.acceptDate;
    }
    
    public final int getDeclinedChallengesNumber() {
        return this.declinedChallenges;
    }
    
    public final void passedChallenge() {
        HistoryManager.addHistory(this.kingName, "passed the challenge put forth by the people of " + this.kingdomName + "!");
        Server.getInstance().broadCastNormal(this.getFullTitle() + " passed the challenge put forth by the people of " + this.kingdomName + "!");
        this.acceptDate = 0L;
        this.challengeDate = 0L;
        this.updateChallenges();
    }
    
    public final void setFailedChallenge() {
        if (!this.hasFailedAllChallenges()) {
            HistoryManager.addHistory(this.kingName, "failed the challenge put forth by the people of " + this.kingdomName + " and may now be voted away from the throne.");
            final Message mess = new Message(null, (byte)10, Kingdoms.getChatNameFor(this.kingdom), "<" + this.kingName + "> has failed the challenge and may now be voted away from the throne.");
            final Player[] playarr = Players.getInstance().getPlayers();
            final byte windowKingdom = this.kingdom;
            for (final Player lElement : playarr) {
                if (windowKingdom == lElement.getKingdomId() || lElement.getPower() > 0) {
                    lElement.getCommunicator().sendMessage(mess);
                }
            }
            this.resetNextChallenge(System.currentTimeMillis() + King.challengeFactor);
            this.acceptDate = 0L;
            this.challengeDate = 0L;
            this.declinedChallenges = 3;
            this.updateChallenges();
        }
    }
    
    public final boolean mayBeChallenged() {
        return System.currentTimeMillis() - this.challengeDate > King.challengeFactor && System.currentTimeMillis() > this.getNextChallenge();
    }
    
    public final boolean hasFailedToRespondToChallenge() {
        return this.challengeDate != 0L && System.currentTimeMillis() - this.challengeDate > King.challengeFactor;
    }
    
    public final boolean hasFailedAllChallenges() {
        return this.declinedChallenges >= 3;
    }
    
    public final int getVotes() {
        return PlayerInfoFactory.getVotesForKingdom(this.kingdom);
    }
    
    public final int getVotesNeeded() {
        return Math.max(0, King.votesRequired - this.getVotes());
    }
    
    public final boolean hasBeenChallenged() {
        int challengesCast = 0;
        for (final Integer i : King.challenges.values()) {
            if (i == this.era) {
                ++challengesCast;
            }
        }
        return challengesCast >= King.challengesRequired;
    }
    
    public final boolean addChallenge(final Creature challenger) {
        if (challenger.getKingdomId() != this.kingdom) {
            return false;
        }
        if (Servers.isThisATestServer()) {
            final boolean wasChallenged = this.hasBeenChallenged();
            King.challenges.put(Server.rand.nextLong(), this.era);
            if (this.hasBeenChallenged() != wasChallenged) {
                this.setChallengeDate();
            }
            return true;
        }
        if (King.challenges.containsKey(challenger.getWurmId())) {
            return false;
        }
        final boolean wasChallenged = this.hasBeenChallenged();
        King.challenges.put(challenger.getWurmId(), this.era);
        if (this.hasBeenChallenged() != wasChallenged) {
            this.setChallengeDate();
        }
        return true;
    }
    
    public final int getChallengeSize() {
        return King.challenges.size();
    }
    
    public void updateChallenges() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KING_ERA SET NEXTCHALLENGE=?,DECLINEDCHALLENGES=?,ACCEPTDATE=?,CHALLENGEDATE=? WHERE ERA=?");
            ps.setLong(1, this.nextChallenge);
            ps.setLong(2, this.declinedChallenges);
            ps.setLong(3, this.acceptDate);
            ps.setLong(4, this.challengeDate);
            ps.setInt(5, this.era);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to update challenges: for era " + this.era + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void addLevelsLost(final int lost) {
        this.levelslost += lost;
        King.logger.log(Level.INFO, this.kingName + " adding " + lost + " levels lost to " + this.levelslost + " for kingdom " + Kingdoms.getChatNameFor(this.kingdom) + " era " + this.era);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KING_ERA SET LEVELSLOST=? WHERE ERA=?");
            ps.setInt(1, this.levelslost);
            ps.setInt(2, this.era);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to update for era " + this.era + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void addLevelsKilled(final int killed, final String name, final int worth) {
        this.levelskilled += killed;
        King.logger.log(Level.INFO, this.kingName + " killed " + name + " worth " + worth + " adding " + killed + " levels killed to " + this.levelskilled + " for kingdom " + Kingdoms.getChatNameFor(this.kingdom) + " era " + this.era);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KING_ERA SET LEVELSKILLED=? WHERE ERA=?");
            ps.setInt(1, this.levelskilled);
            ps.setInt(2, this.era);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            King.logger.log(Level.WARNING, "Failed to update for era " + this.era + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public float getLandSuccessPercent() {
        if (this.startLand == 0.0f) {
            this.startLand = this.currentLand;
        }
        if (this.startLand == 0.0f) {
            return 100.0f;
        }
        return this.currentLand / this.startLand * 100.0f;
    }
    
    public float getAppointedSuccessPercent() {
        if (this.levelskilled == 0 && this.levelslost == 0) {
            return 100.0f;
        }
        if (this.levelslost < 20 && this.levelskilled < 20) {
            return 100.0f;
        }
        if (this.levelslost == 0 && this.levelskilled != 0) {
            return 100 + this.levelskilled;
        }
        if (this.levelslost != 0 && this.levelskilled == 0) {
            return 100 - this.levelslost;
        }
        return this.levelskilled / this.levelslost * 100.0f;
    }
    
    private String getSuccessTitle() {
        final float successPercentSinceStart = this.getLandSuccessPercent();
        if (successPercentSinceStart < 100.0f) {
            if (successPercentSinceStart < 10.0f) {
                return "the Traitor";
            }
            if (successPercentSinceStart < 20.0f) {
                return "the Tragic";
            }
            if (successPercentSinceStart < 30.0f) {
                return "the Joke";
            }
            if (successPercentSinceStart < 50.0f) {
                return "the Imbecile";
            }
            if (successPercentSinceStart < 70.0f) {
                return "the Failed";
            }
            if (successPercentSinceStart < 90.0f) {
                return "the Stupid";
            }
            return "the Acceptable";
        }
        else {
            if (successPercentSinceStart < 110.0f) {
                return "the Acceptable";
            }
            if (successPercentSinceStart < 120.0f) {
                return "the Lucky";
            }
            if (successPercentSinceStart < 130.0f) {
                return "the Conquering";
            }
            if (successPercentSinceStart < 140.0f) {
                return "the Strong";
            }
            if (successPercentSinceStart < 150.0f) {
                return "the Impressive";
            }
            if (successPercentSinceStart < 180.0f) {
                return "the Great";
            }
            if (successPercentSinceStart < 200.0f) {
                return "the Fantastic";
            }
            if (successPercentSinceStart < 400.0f) {
                return "the Magnificent";
            }
            return "the Divine";
        }
    }
    
    private String getAppointmentSuccess() {
        final float successPercentSinceStart = this.getAppointedSuccessPercent();
        if (successPercentSinceStart < 110.0f) {
            return "";
        }
        if (successPercentSinceStart < 120.0f) {
            return "";
        }
        if (successPercentSinceStart < 150.0f) {
            return " Warrior";
        }
        if (successPercentSinceStart < 180.0f) {
            return " Defender";
        }
        if (successPercentSinceStart < 200.0f) {
            return " Statesman";
        }
        if (successPercentSinceStart < 400.0f) {
            return " Saviour";
        }
        return " Holiness";
    }
    
    public String getFullTitle() {
        return this.getRulerTitle() + " " + this.kingName + " " + this.getSuccessTitle() + this.getAppointmentSuccess();
    }
    
    public static boolean isOfficial(final int officeId, final long wurmid, final byte kingdom) {
        final King tempKing = getKing(kingdom);
        return tempKing != null && tempKing.appointments != null && tempKing.appointments.officials[officeId - 1500] == wurmid;
    }
    
    public static Creature getOfficial(final byte _kingdom, final int officeId) {
        final King tempKing = getKing(_kingdom);
        if (tempKing != null && tempKing.appointments != null) {
            final long wurmid = tempKing.appointments.officials[officeId - 1500];
            try {
                final Player p = Players.getInstance().getPlayer(wurmid);
                return p;
            }
            catch (NoSuchPlayerException ex) {}
        }
        return null;
    }
    
    static {
        King.logger = Logger.getLogger(King.class.getName());
        King.currentEra = 0;
        King.kingJenn = null;
        King.kingMolRehan = null;
        King.kingHots = null;
        eras = new HashMap<Integer, King>();
        challenges = new HashMap<Long, Integer>();
        challengesRequired = (Servers.isThisATestServer() ? 3 : 10);
        votesRequired = (Servers.isThisATestServer() ? 1 : 10);
        kings = new HashSet<King>();
        challengeFactor = (Servers.isThisATestServer() ? 60000L : 604800000L);
    }
}
