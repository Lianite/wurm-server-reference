// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.Servers;
import com.wurmonline.server.DbConnector;
import java.util.logging.Logger;

public class EpicPlayerTransferMetaData
{
    private static final Logger logger;
    private static final String UPDATE_PLAYER = "UPDATE PLAYERS SET NAME=?, PASSWORD=?, MAYUSESHOP=?, PLAYINGTIME=?, SEX=?, IPADDRESS=?, REIMBURSED=?, BANNED=?, PAYMENTEXPIRE=?, POWER=?,    DEVTALK=?, WARNINGS=?, LASTWARNED=?, KINGDOM=?, SESSIONKEY=?, SESSIONEXPIRE=?, VERSION=?, MUTED=?, MONEY=?, BANEXPIRY=?,        BANREASON=?, FACE=?, LOGGING=?, MAYMUTE=?, MUTEEXPIRY=?, MUTEREASON=?, REFERRER=?, EMAIL=?, PWQUESTION=?, PWANSWER=?,        CREATIONDATE=?, PA=?, APPOINTPA=?, PAWINDOW=?, MUTETIMES=?, EPICKINGDOM=?, EPICSERVER=?, CURRENTSERVER=?, CHAOSKINGDOM=?, BLOOD=?,       FLAGS=?, FLAGS2=?, MONEYSALES=?     WHERE WURMID=?";
    private static final String INSERT_FRIEND;
    private static final String DELETE_FRIENDS = "DELETE FROM FRIENDS WHERE WURMID=?";
    private static final String INSERT_IGNORED;
    private static final String DELETE_IGNORED = "DELETE FROM IGNORED WHERE WURMID=?";
    private final long wurmid;
    private final String password;
    private final String session;
    private final String email;
    private final boolean overRideShop;
    private final long sessionExpiration;
    private final byte power;
    private final long money;
    private final long paymentExpire;
    private final long[] ignored;
    private final long[] friends;
    private final byte[] friendcats;
    private final long playingTime;
    private final long creationDate;
    private final long lastwarned;
    private final byte kingdom;
    private final boolean banned;
    private final long banexpiry;
    private final String banreason;
    private final boolean mute;
    private final short muteTimes;
    private final long muteexpiry;
    private final String mutereason;
    private final boolean maymute;
    private final boolean reimbursed;
    private final int warnings;
    private final boolean mayHearDevtalk;
    private final long referrer;
    private final String pwQuestion;
    private final String pwAnswer;
    private final boolean logging;
    private final boolean seesPAWin;
    private final boolean isCA;
    private final boolean mayAppointPA;
    private final long face;
    private final byte blood;
    private final byte sex;
    private final long ver;
    private final String lastip;
    private final int epicServerId;
    private final byte epicServerKingdom;
    private final String name;
    private final byte chaosKingdom;
    private final long flags;
    private final long flags2;
    private byte undeadType;
    private int undeadKills;
    private int undeadPKills;
    private int undeadPSecs;
    private long moneySales;
    private int daysPrem;
    private long lastTicked;
    private int currentLoyaltyPoints;
    private int totalLoyaltyPoints;
    private int monthsPaidEver;
    private int monthsPaidInARow;
    private int monthsPaidSinceReset;
    private int silverPaidEver;
    private boolean hasAwards;
    
    public EpicPlayerTransferMetaData(final long aWurmid, final String aName, final String aPassword, final String aSession, final long aSessionExpiration, final byte aPower, final long aLastwarned, final long aPlayingTime, final byte aKingdom, final boolean aBanned, final long aBanexpiry, final String aBanreason, final boolean aReimbursed, final int aWarnings, final boolean aMayHearDevtalk, final long aPaymentExpire, final long[] aIgnored, final long[] aFriends, final byte[] aCats, final String aLastip, final boolean aMute, final byte aSex, final long version, final long aMoney, final long _face, final boolean seesPa, final boolean islogged, final boolean _isCA, final boolean mayAppointCA, final long _referrer, final String _pwQuestion, final String _pwAnswer, final boolean _overRideShop, final short _muteTimes, final long muteExpiry, final String muteReason, final boolean mayMute, final String emailAddress, final long _creationDate, final int _epicServerId, final byte _epicServerKingdom, final byte _chaosKingdom, final byte _blood, final long _flags, final long _flags2, final byte udt, final int udk, final int udpk, final int udps, final long salesEver, final int aDaysPrem, final long aLastTicked, final int currentLoyaltyPoints, final int totalLoyaltyPoints, final int aMonthsPaidEver, final int aMonthsPaidInARow, final int aMonthsPaidSinceReset, final int aSilverPaidEver, final boolean awards) {
        this.wurmid = aWurmid;
        this.name = aName;
        this.email = emailAddress;
        this.creationDate = _creationDate;
        this.password = aPassword;
        this.session = aSession;
        this.sessionExpiration = aSessionExpiration;
        this.lastwarned = aLastwarned;
        this.playingTime = aPlayingTime;
        this.kingdom = aKingdom;
        this.banned = aBanned;
        this.banexpiry = aBanexpiry;
        this.banreason = aBanreason;
        this.reimbursed = aReimbursed;
        this.warnings = aWarnings;
        this.mayHearDevtalk = aMayHearDevtalk;
        this.paymentExpire = aPaymentExpire;
        this.ignored = aIgnored;
        this.friends = aFriends;
        this.friendcats = aCats;
        this.lastip = aLastip;
        this.mute = aMute;
        this.muteTimes = _muteTimes;
        this.muteexpiry = muteExpiry;
        this.mutereason = muteReason;
        this.maymute = mayMute;
        this.sex = aSex;
        this.money = aMoney;
        this.logging = islogged;
        this.seesPAWin = seesPa;
        this.isCA = _isCA;
        this.mayAppointPA = mayAppointCA;
        this.ver = version;
        this.face = _face;
        this.referrer = _referrer;
        this.pwQuestion = _pwQuestion;
        this.pwAnswer = _pwAnswer;
        this.power = aPower;
        this.overRideShop = _overRideShop;
        this.epicServerId = _epicServerId;
        this.epicServerKingdom = _epicServerKingdom;
        this.chaosKingdom = _chaosKingdom;
        this.blood = _blood;
        this.flags = _flags;
        this.flags2 = _flags2;
        this.undeadType = udt;
        this.undeadPKills = udpk;
        this.undeadKills = udk;
        this.undeadPSecs = udps;
        this.moneySales = salesEver;
        this.daysPrem = aDaysPrem;
        this.lastTicked = aLastTicked;
        this.monthsPaidEver = aMonthsPaidEver;
        this.monthsPaidInARow = aMonthsPaidInARow;
        this.monthsPaidSinceReset = aMonthsPaidSinceReset;
        this.silverPaidEver = aSilverPaidEver;
        this.hasAwards = awards;
    }
    
    public void save() throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET NAME=?, PASSWORD=?, MAYUSESHOP=?, PLAYINGTIME=?, SEX=?, IPADDRESS=?, REIMBURSED=?, BANNED=?, PAYMENTEXPIRE=?, POWER=?,    DEVTALK=?, WARNINGS=?, LASTWARNED=?, KINGDOM=?, SESSIONKEY=?, SESSIONEXPIRE=?, VERSION=?, MUTED=?, MONEY=?, BANEXPIRY=?,        BANREASON=?, FACE=?, LOGGING=?, MAYMUTE=?, MUTEEXPIRY=?, MUTEREASON=?, REFERRER=?, EMAIL=?, PWQUESTION=?, PWANSWER=?,        CREATIONDATE=?, PA=?, APPOINTPA=?, PAWINDOW=?, MUTETIMES=?, EPICKINGDOM=?, EPICSERVER=?, CURRENTSERVER=?, CHAOSKINGDOM=?, BLOOD=?,       FLAGS=?, FLAGS2=?, MONEYSALES=?     WHERE WURMID=?");
            ps.setString(1, this.name);
            ps.setString(2, this.password);
            ps.setBoolean(3, this.overRideShop);
            ps.setLong(4, this.playingTime);
            ps.setByte(5, this.sex);
            ps.setString(6, this.lastip);
            ps.setBoolean(7, this.reimbursed);
            ps.setBoolean(8, this.banned);
            ps.setLong(9, this.paymentExpire);
            ps.setByte(10, this.power);
            ps.setBoolean(11, this.mayHearDevtalk);
            ps.setShort(12, (short)this.warnings);
            ps.setLong(13, this.lastwarned);
            ps.setByte(14, this.kingdom);
            ps.setString(15, this.session);
            ps.setLong(16, this.sessionExpiration);
            ps.setLong(17, this.ver);
            ps.setBoolean(18, this.mute);
            ps.setLong(19, this.money);
            ps.setLong(20, this.banexpiry);
            ps.setString(21, this.banreason);
            ps.setLong(22, this.face);
            ps.setBoolean(23, this.logging);
            ps.setBoolean(24, this.maymute);
            ps.setLong(25, this.muteexpiry);
            ps.setString(26, this.mutereason);
            ps.setLong(27, this.referrer);
            ps.setString(28, this.email);
            ps.setString(29, this.pwQuestion);
            ps.setString(30, this.pwAnswer);
            ps.setLong(31, this.creationDate);
            ps.setBoolean(32, this.isCA);
            ps.setBoolean(33, this.mayAppointPA);
            ps.setBoolean(34, this.seesPAWin);
            ps.setShort(35, this.muteTimes);
            ps.setByte(36, this.epicServerKingdom);
            ps.setInt(37, this.epicServerId);
            ps.setInt(38, Servers.localServer.id);
            ps.setByte(39, this.chaosKingdom);
            ps.setByte(40, this.blood);
            ps.setLong(41, this.flags);
            ps.setLong(42, this.flags2);
            ps.setLong(43, this.moneySales);
            ps.setLong(44, this.wurmid);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM FRIENDS WHERE WURMID=?");
            ps.setLong(1, this.wurmid);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (this.friends != null) {
                for (int x = 0; x < this.friends.length; ++x) {
                    ps = dbcon.prepareStatement(EpicPlayerTransferMetaData.INSERT_FRIEND);
                    ps.setLong(1, this.wurmid);
                    ps.setLong(2, this.friends[x]);
                    ps.setByte(3, this.friendcats[x]);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                }
            }
            ps = dbcon.prepareStatement("DELETE FROM IGNORED WHERE WURMID=?");
            ps.setLong(1, this.wurmid);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (this.ignored != null) {
                for (int x = 0; x < this.ignored.length; ++x) {
                    ps = dbcon.prepareStatement(EpicPlayerTransferMetaData.INSERT_IGNORED);
                    ps.setLong(1, this.wurmid);
                    ps.setLong(2, this.ignored[x]);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                }
            }
        }
        catch (SQLException sqex) {
            EpicPlayerTransferMetaData.logger.log(Level.WARNING, this.name + " " + sqex.getMessage(), sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        if (this.hasAwards) {
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("DELETE FROM AWARDS WHERE WURMID=?");
                ps.setLong(1, this.wurmid);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                EpicPlayerTransferMetaData.logger.log(Level.WARNING, this.name + " " + sqex.getMessage(), sqex);
                throw new IOException(sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("INSERT INTO AWARDS(WURMID, DAYSPREM, MONTHSPREM, MONTHSEVER, CONSECMONTHS, SILVERSPURCHASED, LASTTICKEDPREM, CURRENTLOYALTY, TOTALLOYALTY) VALUES(?,?,?,?,?,?,?,?,?)");
                ps.setLong(1, this.wurmid);
                ps.setInt(2, this.daysPrem);
                ps.setInt(3, this.monthsPaidSinceReset);
                ps.setInt(4, this.monthsPaidEver);
                ps.setInt(5, this.monthsPaidInARow);
                ps.setInt(6, this.silverPaidEver);
                ps.setLong(7, this.lastTicked);
                ps.setInt(8, this.currentLoyaltyPoints);
                ps.setInt(9, this.totalLoyaltyPoints);
                ps.executeUpdate();
            }
            catch (SQLException sqex) {
                EpicPlayerTransferMetaData.logger.log(Level.WARNING, this.name + " " + sqex.getMessage(), sqex);
                throw new IOException(sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(EpicPlayerTransferMetaData.class.getName());
        INSERT_FRIEND = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO FRIENDS(WURMID,FRIEND,CATEGORY) VALUES(?,?,?)" : "INSERT IGNORE INTO FRIENDS(WURMID,FRIEND,CATEGORY) VALUES(?,?,?)");
        INSERT_IGNORED = (DbConnector.isUseSqlite() ? "INSERT OR IGNORE INTO IGNORED(WURMID,IGNOREE) VALUES(?,?)" : "INSERT IGNORE INTO IGNORED(WURMID,IGNOREE) VALUES(?,?)");
    }
}
