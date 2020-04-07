// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.webinterface.WcPlayerStatus;
import java.rmi.RemoteException;
import com.wurmonline.server.LoginServerWebConnection;
import java.util.Optional;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.intra.IntraCommand;
import com.wurmonline.server.intra.PlayerTransfer;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.webinterface.WebInterfaceImpl;
import com.wurmonline.server.Mailer;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.intra.IntraServerConnection;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Items;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Constants;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.intra.TimeTransfer;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.intra.MoneyTransfer;
import com.wurmonline.server.Players;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.HashSet;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Servers;
import com.wurmonline.server.LoginHandler;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

public final class PlayerInfoFactory implements TimeConstants, MiscConstants
{
    private static final ConcurrentHashMap<String, PlayerInfo> playerInfos;
    private static final ConcurrentHashMap<Long, PlayerInfo> playerInfosWurmId;
    private static final ConcurrentHashMap<Long, PlayerState> playerStatus;
    private static final Set<Long> failedIds;
    private static final ConcurrentLinkedDeque<PlayerState> statesToUpdate;
    private static final ConcurrentHashMap<Long, PlayerState> friendsToUpdate;
    private static final Logger logger;
    private static final Logger deletelogger;
    private static final String LOAD_AWARDS = "SELECT * FROM AWARDS";
    private static final String GET_ALL_PLAYERS = "SELECT * FROM PLAYERS";
    private static final long EXPIRATION_TIME = 7257600000L;
    protected static final long NOTICE_TIME = 604800000L;
    private static final Map<Long, Set<Referer>> referrers;
    private static final String LOAD_REFERERS = "SELECT * FROM REFERERS";
    private static final String SET_REFERER = "UPDATE REFERERS SET HANDLED=1, MONEY=? WHERE WURMID=? AND REFERER=?";
    private static final String ADD_REFERER = "INSERT INTO REFERERS (WURMID, REFERER,HANDLED, MONEY ) VALUES(?,?,0,0)";
    private static final String REVERT_REFERER = "UPDATE REFERERS SET HANDLED=0, MONEY=0 WHERE WURMID=? AND REFERER=?";
    private static final String RESET_SCENARIOKARMA = "UPDATE PLAYERS SET SCENARIOKARMA=0";
    private static int deletedPlayers;
    public static final String NOPERMISSION = "NO";
    public static final String RETRIEVAL = " Retrieval info updated.";
    private static long OFFLINETIME_UNTIL_FREEZE;
    private static final LinkedList<WurmRecord> championRecords;
    
    public static PlayerInfo createPlayerInfo(String name) {
        name = LoginHandler.raiseFirstLetter(name);
        if (PlayerInfoFactory.playerInfos.containsKey(name)) {
            return PlayerInfoFactory.playerInfos.get(name);
        }
        return new DbPlayerInfo(name);
    }
    
    public static void addPlayerInfo(final PlayerInfo info) {
        if (!doesPlayerInfoExist(info.getName())) {
            PlayerInfoFactory.playerInfos.put(info.name, info);
            PlayerInfoFactory.playerInfosWurmId.put(info.wurmId, info);
        }
    }
    
    private static boolean doesPlayerInfoExist(final String aName) {
        return PlayerInfoFactory.playerInfos.containsKey(aName);
    }
    
    public static long getPlayerMoney() {
        final PlayerInfo[] p = getPlayerInfos();
        long toRet = 0L;
        for (final PlayerInfo lElement : p) {
            if (lElement.currentServer == Servers.localServer.id || Servers.localServer.LOGINSERVER) {
                toRet += lElement.money;
            }
        }
        return toRet;
    }
    
    public static final void loadReferers() throws IOException {
        final long start = System.nanoTime();
        int loadedReferrers = 0;
        if (Servers.localServer.id == Servers.loginServer.id) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                dbcon = DbConnector.getPlayerDbCon();
                ps = dbcon.prepareStatement("SELECT * FROM REFERERS");
                rs = ps.executeQuery();
                while (rs.next()) {
                    final Referer r = new Referer(rs.getLong("WURMID"), rs.getLong("REFERER"), rs.getBoolean("MONEY"), rs.getBoolean("HANDLED"));
                    final Long wid = new Long(r.getWurmid());
                    Set<Referer> s = PlayerInfoFactory.referrers.get(wid);
                    if (s == null) {
                        s = new HashSet<Referer>();
                        PlayerInfoFactory.referrers.put(wid, s);
                        ++loadedReferrers;
                    }
                    s.add(r);
                }
            }
            catch (SQLException sqex) {
                PlayerInfoFactory.logger.log(Level.WARNING, sqex.getMessage(), sqex);
                throw new IOException(sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, rs);
                DbConnector.returnConnection(dbcon);
                final long end = System.nanoTime();
                PlayerInfoFactory.logger.info("Loaded " + loadedReferrers + " referrers from the database took " + (end - start) / 1000000.0f + " ms");
            }
        }
        else {
            PlayerInfoFactory.logger.info("Not Loading referrers from the database as this is not the login server");
        }
    }
    
    public static final boolean addReferrer(final long wurmid, final long referrer) throws IOException {
        Set<Referer> s = PlayerInfoFactory.referrers.get(new Long(wurmid));
        if (s != null) {
            for (final Referer r : s) {
                if (r.getReferer() == referrer) {
                    return false;
                }
            }
        }
        else {
            s = new HashSet<Referer>();
            PlayerInfoFactory.referrers.put(new Long(wurmid), s);
        }
        final Referer r2 = new Referer(wurmid, referrer);
        s.add(r2);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("INSERT INTO REFERERS (WURMID, REFERER,HANDLED, MONEY ) VALUES(?,?,0,0)");
            ps.setLong(1, wurmid);
            ps.setLong(2, referrer);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            PlayerInfoFactory.logger.log(Level.WARNING, "Failed to add referrer " + referrer + " for " + wurmid);
            throw new IOException(ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        final PlayerInfo pinf = getPlayerInfoWithWurmId(referrer);
        if (pinf != null) {
            pinf.setReferedby(wurmid);
        }
        return true;
    }
    
    public static final boolean acceptReferer(final long wurmid, final long referrer, final boolean money) throws IOException {
        final Set<Referer> s = PlayerInfoFactory.referrers.get(new Long(wurmid));
        if (s != null) {
            boolean found = false;
            for (final Referer r : s) {
                if (r.getReferer() == referrer) {
                    found = true;
                    r.setMoney(money);
                    r.setHandled(true);
                    break;
                }
            }
            if (found) {
                Connection dbcon = null;
                PreparedStatement ps = null;
                try {
                    dbcon = DbConnector.getPlayerDbCon();
                    ps = dbcon.prepareStatement("UPDATE REFERERS SET HANDLED=1, MONEY=? WHERE WURMID=? AND REFERER=?");
                    ps.setBoolean(1, money);
                    ps.setLong(2, wurmid);
                    ps.setLong(3, referrer);
                    ps.executeUpdate();
                    return true;
                }
                catch (SQLException ex) {
                    PlayerInfoFactory.logger.log(Level.WARNING, "Failed to set referrer " + referrer + " for " + wurmid + " and money=" + money);
                    throw new IOException(ex);
                }
                finally {
                    DbUtilities.closeDatabaseObjects(ps, null);
                    DbConnector.returnConnection(dbcon);
                }
            }
        }
        return false;
    }
    
    public static final void revertReferer(final long wurmid, final long referrer) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE REFERERS SET HANDLED=0, MONEY=0 WHERE WURMID=? AND REFERER=?");
            ps.setLong(1, wurmid);
            ps.setLong(2, referrer);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            PlayerInfoFactory.logger.log(Level.WARNING, "Failed to revert referrer " + referrer + " for " + wurmid);
            throw new IOException(ex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final Map<String, Byte> getReferrers(final long wurmid) {
        final Map<String, Byte> map = new HashMap<String, Byte>();
        final Set<Referer> s = PlayerInfoFactory.referrers.get(new Long(wurmid));
        if (s != null) {
            for (final Referer r : s) {
                byte type = 0;
                if (r.isHandled()) {
                    if (r.isMoney()) {
                        type = 1;
                    }
                    else {
                        type = 2;
                    }
                }
                String name = String.valueOf(r.getReferer());
                try {
                    name = Players.getInstance().getNameFor(r.getReferer());
                }
                catch (Exception e) {
                    PlayerInfoFactory.logger.log(Level.WARNING, "No name found for " + r.getReferer());
                }
                map.put(name, type);
            }
        }
        return map;
    }
    
    public static final boolean addMoneyToBank(final long wurmid, final long moneyToAdd, final String transactionDetail) throws Exception {
        try {
            final Player p = Players.getInstance().getPlayer(wurmid);
            if (moneyToAdd >= 1000000L) {
                PlayerInfoFactory.logger.log(Level.INFO, "Adding " + moneyToAdd + " to " + p.getName(), new Exception());
            }
            p.addMoney(moneyToAdd);
            final long money = p.getMoney();
            new MoneyTransfer(p.getName(), p.getWurmId(), money, moneyToAdd, transactionDetail, (byte)0, "");
            final Change change = new Change(moneyToAdd);
            final Change current = new Change(money);
            p.save();
            p.getCommunicator().sendSafeServerMessage("An amount of " + change.getChangeString() + " has been added to your bank account. Current balance is " + current.getChangeString() + ".");
            if (transactionDetail.startsWith("Referred by ")) {
                p.getSaveFile().addToSleep(3600);
                final String sleepString = "You received an hour of sleep bonus which will increase your skill gain speed.";
                p.getCommunicator().sendSafeServerMessage("You received an hour of sleep bonus which will increase your skill gain speed.");
            }
        }
        catch (NoSuchPlayerException nsp) {
            final PlayerInfo p2 = getPlayerInfoWithWurmId(wurmid);
            if (p2.wurmId <= 0L) {
                return false;
            }
            if (moneyToAdd >= 1000000L) {
                PlayerInfoFactory.logger.log(Level.INFO, "Adding " + moneyToAdd + " to " + p2.getName(), new Exception());
            }
            p2.setMoney(p2.money + moneyToAdd);
            p2.save();
            if (transactionDetail.startsWith("Referred by ")) {
                p2.addToSleep(3600);
            }
            if (Servers.localServer.id != p2.currentServer) {
                new MoneyTransfer(p2.getName(), p2.wurmId, p2.money, moneyToAdd, transactionDetail, (byte)5, "", false);
            }
            else {
                new MoneyTransfer(p2.getName(), p2.wurmId, p2.money, moneyToAdd, transactionDetail, (byte)5, "");
            }
        }
        return true;
    }
    
    public static final boolean addPlayingTime(final long wurmid, final int months, final int days, final String transactionDetail) throws Exception {
        if (wurmid < 0L || transactionDetail == null || transactionDetail.length() == 0) {
            throw new WurmServerException("Illegal arguments. Check if name or transaction detail is null or empty strings.");
        }
        if (months < 0 || days < 0) {
            throw new WurmServerException("Illegal arguments. Make sure that the values for days and months are not negative.");
        }
        long timeToAdd = 0L;
        if (days != 0) {
            timeToAdd = days * 86400000L;
        }
        if (months != 0) {
            timeToAdd += months * 86400000L * 30L;
        }
        try {
            final Player p = Players.getInstance().getPlayer(wurmid);
            long currTime = p.getPaymentExpire();
            currTime = Math.max(currTime, System.currentTimeMillis());
            currTime += timeToAdd;
            if (transactionDetail.startsWith("Referred by ")) {
                p.getSaveFile().addToSleep(3600);
                final String sleepString = "You received an hour of sleep bonus which will increase your skill gain speed.";
                p.getCommunicator().sendSafeServerMessage("You received an hour of sleep bonus which will increase your skill gain speed.");
            }
            p.setPaymentExpire(currTime);
            new TimeTransfer(p.getName(), p.getWurmId(), months, false, days, transactionDetail);
            p.save();
            p.getCommunicator().sendNormalServerMessage("You now have premier playing time until " + WurmCalendar.formatGmt(currTime) + ".");
        }
        catch (NoSuchPlayerException nsp) {
            final PlayerInfo p2 = getPlayerInfoWithWurmId(wurmid);
            if (p2.wurmId <= 0L) {
                return false;
            }
            long currTime2 = p2.getPaymentExpire();
            currTime2 = Math.max(currTime2, System.currentTimeMillis());
            currTime2 += timeToAdd;
            p2.setPaymentExpire(currTime2);
            if (transactionDetail.startsWith("Referred by ")) {
                p2.addToSleep(3600);
            }
            if (p2.currentServer != Servers.localServer.id) {
                new TimeTransfer(p2.getName(), p2.wurmId, months, false, days, transactionDetail, false);
            }
            else {
                new TimeTransfer(p2.getName(), p2.wurmId, months, false, days, transactionDetail);
            }
        }
        return true;
    }
    
    public static final void pruneRanks(final long now) {
        for (final PlayerInfo pinf : getPlayerInfos()) {
            if (pinf.getRank() > 1000 && now - pinf.lastModifiedRank > 864000000L) {
                try {
                    pinf.setRank((int)(pinf.getRank() * 0.975));
                    PlayerInfoFactory.logger.log(Level.INFO, "Set rank of " + pinf.getName() + " to " + pinf.getRank());
                }
                catch (IOException iox) {
                    PlayerInfoFactory.logger.log(Level.INFO, pinf.getName() + ": " + iox.getMessage());
                }
            }
        }
    }
    
    public static final void pollPremiumPlayers() {
        for (final PlayerInfo info : getPlayerInfos()) {
            if (info.timeToCheckPrem-- <= 0) {
                info.timeToCheckPrem = (int)((86400000L + System.currentTimeMillis()) / 1000L) + Server.rand.nextInt(200);
                if (info.getPower() <= 0 && info.paymentExpireDate > 0L && !info.isFlagSet(63) && info.awards != null) {
                    if (System.currentTimeMillis() - info.awards.getLastTickedDay() > 86400000L) {
                        final boolean wasPrem = info.awards.getLastTickedDay() < info.paymentExpireDate;
                        if (info.isQAAccount() || info.paymentExpireDate > System.currentTimeMillis() || wasPrem) {
                            info.awards.setDaysPrem(info.awards.getDaysPrem() + 1);
                            info.timeToCheckPrem = 86400 + Server.rand.nextInt(200);
                            if (info.awards.getDaysPrem() % 28 == 0) {
                                info.awards.setMonthsPaidSinceReset(info.awards.getMonthsPaidSinceReset() + 1);
                                info.awards.setMonthsPaidInARow(info.awards.getMonthsPaidInARow() + 1);
                                AwardLadder.award(info, true);
                            }
                        }
                        else if (info.awards.getMonthsPaidInARow() > 0) {
                            info.awards.setMonthsPaidInARow(0);
                        }
                        info.awards.setLastTickedDay(System.currentTimeMillis());
                        info.awards.update();
                    }
                    else {
                        info.timeToCheckPrem = (int)((info.awards.getLastTickedDay() + 86400000L - System.currentTimeMillis()) / 1000L) + 100;
                    }
                }
            }
        }
    }
    
    public static final void checkIfDeleteOnePlayer() {
        final long now = System.currentTimeMillis();
        final boolean loginServer = Servers.localServer.LOGINSERVER;
        if (Constants.pruneDb && Server.getSecondsUptime() > 30) {
            for (final PlayerInfo pinf : getPlayerInfos()) {
                Label_0873: {
                    if (pinf.creationDate < now - 604800000L && !pinf.isQAAccount()) {
                        if (pinf.power == 0 && pinf.playingTime < 86400000L && pinf.lastLogout < now - 7257600000L && (pinf.paymentExpireDate == 0L || pinf.isFlagSet(63)) && Servers.localServer.id != 20 && pinf.currentServer != 20 && pinf.lastServer != 20) {
                            try {
                                if (pinf.money < 50000L) {
                                    ++PlayerInfoFactory.deletedPlayers;
                                    final Village[] villages;
                                    final Village[] vills = villages = Villages.getVillages();
                                    for (final Village v : villages) {
                                        if (v.getMayor() != null && v.getMayor().getId() == pinf.wurmId) {
                                            v.disband(pinf.getName() + " deleted");
                                        }
                                    }
                                    final Set<Item> items = Items.loadAllItemsForCreatureWithId(pinf.wurmId, pinf.hasMovedInventory());
                                    for (final Item item : items) {
                                        if (!item.isIndestructible() && !item.isVillageDeed() && !item.isHomesteadDeed() && WurmId.getType(item.getWurmId()) != 19) {
                                            IntraServerConnection.deleteItem(item.getWurmId(), pinf.hasMovedInventory());
                                            Items.removeItem(item.getWurmId());
                                        }
                                    }
                                    IntraServerConnection.deletePlayer(pinf.wurmId);
                                    PlayerInfoFactory.deletelogger.log(Level.INFO, "Deleted " + pinf.name + ", email[" + pinf.emailAddress + "] " + pinf.wurmId);
                                    MissionPerformed.deleteMissionPerformer(pinf.wurmId);
                                    PlayerInfoFactory.playerStatus.remove(pinf.wurmId);
                                    PlayerInfoFactory.playerInfos.remove(pinf.getName());
                                    return;
                                }
                                if (loginServer) {
                                    sendDeletePreventLetter(pinf);
                                }
                                PlayerInfoFactory.deletelogger.log(Level.INFO, "Kept and charged 5 silver from " + pinf.name + ", " + pinf);
                                pinf.setMoney(pinf.money - 50000L);
                                pinf.lastLogout = now;
                                pinf.setFlag(8, false);
                                pinf.save();
                                return;
                            }
                            catch (IOException iox) {
                                PlayerInfoFactory.logger.log(Level.WARNING, iox.getMessage(), iox);
                                break Label_0873;
                            }
                        }
                        if (!pinf.isOnlineHere() && now - pinf.lastLogout > PlayerInfoFactory.OFFLINETIME_UNTIL_FREEZE) {
                            if (!pinf.hasMovedInventory() && !PlayerInfoFactory.failedIds.contains(pinf.wurmId)) {
                                if (Items.moveItemsToFreezerFor(pinf.wurmId)) {
                                    pinf.setMovedInventory(true);
                                    PlayerInfoFactory.deletelogger.log(Level.INFO, "Froze items for " + pinf.getName());
                                    return;
                                }
                                PlayerInfoFactory.failedIds.add(pinf.wurmId);
                            }
                        }
                        else if (pinf.power == 0 && pinf.playingTime < 86400000L && pinf.lastLogout < now - 7257600000L - 604800000L && (pinf.paymentExpireDate == 0L || pinf.isFlagSet(63))) {
                            if (loginServer && !pinf.isFlagSet(8)) {
                                sendDeleteLetter(pinf);
                            }
                        }
                        else if (pinf.power == 0 && pinf.paymentExpireDate > now && pinf.paymentExpireDate < now + 604800000L) {
                            if (loginServer && !pinf.isFlagSet(8)) {
                                sendPremiumWarningLetter(pinf);
                            }
                        }
                        else if (pinf.power == 0 && pinf.paymentExpireDate < now && !pinf.isFlagSet(9)) {
                            Server.addExpiry();
                            pinf.setFlag(9, true);
                        }
                    }
                }
            }
        }
    }
    
    public static final void sendDeletePreventLetter(final PlayerInfo pinf) {
        try {
            String email = Mailer.getAccountDelPreventionMail();
            email = email.replace("@pname", pinf.getName());
            Mailer.sendMail(WebInterfaceImpl.mailAccount, pinf.emailAddress, "Wurm Online deletion protection", email);
        }
        catch (Exception ex) {
            PlayerInfoFactory.logger.log(Level.INFO, ex.getMessage(), ex);
        }
    }
    
    public static final void sendDeleteLetter(final PlayerInfo pinf) {
        try {
            String email = Mailer.getAccountDelMail();
            email = email.replace("@pname", pinf.getName());
            Mailer.sendMail(WebInterfaceImpl.mailAccount, pinf.emailAddress, "Wurm Online character deletion", email);
        }
        catch (Exception ex) {
            PlayerInfoFactory.logger.log(Level.INFO, ex.getMessage(), ex);
        }
        pinf.setFlag(8, true);
    }
    
    public static final void sendPremiumWarningLetter(final PlayerInfo pinf) {
        if (pinf.awards != null) {
            String rewString = "We have no award specified at this level of total premium time since this program started";
            String reward = "unspecified";
            String nextRewardMonth = "lots more";
            final int ql = (int)AwardLadder.consecutiveItemQL(pinf.awards.getMonthsPaidInARow() + 1);
            final AwardLadder next = pinf.awards.getNextReward();
            if (next != null) {
                rewString = "Your next award is <i>@award</i> which will occur when you have @nextmonths months of premium time since this program started";
                reward = next.getName();
                nextRewardMonth = next.getMonthsRequiredReset() + "";
                rewString = rewString.replace("@award", reward);
                rewString = rewString.replace("@nextmonths", nextRewardMonth);
            }
            try {
                String email = Mailer.getPremExpiryMail();
                email = email.replace("@pname", pinf.getName());
                email = email.replace("@reward", rewString);
                email = email.replace("@qualityLevel", ql + "");
                email = email.replace("@currmonths", pinf.awards.getMonthsPaidSinceReset() + "");
                Mailer.sendMail(WebInterfaceImpl.mailAccount, pinf.emailAddress, "Wurm Online premium expiry warning", email);
            }
            catch (Exception ex) {
                PlayerInfoFactory.logger.log(Level.INFO, ex.getMessage(), ex);
            }
            pinf.setFlag(8, true);
        }
    }
    
    public static final Logger getDeleteLogger() {
        return PlayerInfoFactory.deletelogger;
    }
    
    public static final void loadAwards() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM AWARDS");
            rs = ps.executeQuery();
            while (rs.next()) {
                new Awards(rs.getLong("WURMID"), rs.getInt("DAYSPREM"), rs.getInt("MONTHSEVER"), rs.getInt("CONSECMONTHS"), rs.getInt("MONTHSPREM"), rs.getInt("SILVERSPURCHASED"), rs.getLong("LASTTICKEDPREM"), rs.getInt("CURRENTLOYALTY"), rs.getInt("TOTALLOYALTY"), false);
            }
        }
        catch (SQLException sqex) {
            PlayerInfoFactory.logger.log(Level.WARNING, sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void loadPlayerInfos() throws IOException {
        Players.loadAllArtists();
        loadAwards();
        final long now = System.currentTimeMillis();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (Constants.pruneDb) {
                PlayerInfoFactory.logger.log(Level.INFO, "Loading player infos. Going to prune DB.");
            }
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PLAYERS");
            rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("NAME");
                name = LoginHandler.raiseFirstLetter(name);
                final DbPlayerInfo pinf = new DbPlayerInfo(name);
                pinf.wurmId = rs.getLong("WURMID");
                pinf.password = rs.getString("PASSWORD");
                pinf.playingTime = rs.getLong("PLAYINGTIME");
                pinf.reimbursed = rs.getBoolean("REIMBURSED");
                pinf.plantedSign = rs.getLong("PLANTEDSIGN");
                pinf.ipaddress = rs.getString("IPADDRESS");
                pinf.banned = rs.getBoolean("BANNED");
                pinf.power = rs.getByte("POWER");
                pinf.rank = rs.getInt("RANK");
                pinf.maxRank = rs.getInt("MAXRANK");
                pinf.lastModifiedRank = rs.getLong("LASTMODIFIEDRANK");
                pinf.mayHearDevTalk = rs.getBoolean("DEVTALK");
                pinf.paymentExpireDate = rs.getLong("PAYMENTEXPIRE");
                pinf.lastWarned = rs.getLong("LASTWARNED");
                pinf.warnings = rs.getShort("WARNINGS");
                pinf.lastCheated = rs.getLong("CHEATED");
                pinf.lastFatigue = rs.getLong("LASTFATIGUE");
                pinf.fatigueSecsLeft = rs.getInt("FATIGUE");
                pinf.fatigueSecsToday = rs.getInt("FATIGUETODAY");
                pinf.fatigueSecsYesterday = rs.getInt("FATIGUEYDAY");
                pinf.dead = rs.getBoolean("DEAD");
                pinf.version = rs.getLong("VERSION");
                pinf.money = rs.getLong("MONEY");
                pinf.climbing = rs.getBoolean("CLIMBING");
                pinf.banexpiry = rs.getLong("BANEXPIRY");
                pinf.banreason = rs.getString("BANREASON");
                pinf.emailAddress = rs.getString("EMAIL");
                if (pinf.banreason == null) {
                    pinf.banreason = "";
                }
                pinf.logging = rs.getBoolean("LOGGING");
                pinf.referrer = rs.getLong("REFERRER");
                pinf.isPriest = rs.getBoolean("PRIEST");
                pinf.bed = rs.getLong("BED");
                pinf.sleep = rs.getInt("SLEEP");
                pinf.isTheftWarned = rs.getBoolean("THEFTWARNED");
                pinf.noReimbursementLeft = rs.getBoolean("NOREIMB");
                pinf.deathProtected = rs.getBoolean("DEATHPROT");
                pinf.tutorialLevel = rs.getInt("TUTORIALLEVEL");
                pinf.autoFighting = rs.getBoolean("AUTOFIGHT");
                pinf.appointments = rs.getLong("APPOINTMENTS");
                pinf.playerAssistant = rs.getBoolean("PA");
                pinf.mayAppointPlayerAssistant = rs.getBoolean("APPOINTPA");
                pinf.seesPlayerAssistantWindow = rs.getBoolean("PAWINDOW");
                pinf.hasFreeTransfer = rs.getBoolean("FREETRANSFER");
                pinf.votedKing = rs.getBoolean("VOTEDKING");
                final byte kingdom = rs.getByte("KINGDOM");
                Players.getInstance().registerNewKingdom(pinf.wurmId, kingdom);
                if (pinf.playingTime < 0L) {
                    pinf.playingTime = 0L;
                }
                pinf.alignment = rs.getFloat("ALIGNMENT");
                final byte deityNum = rs.getByte("DEITY");
                if (deityNum > 0) {
                    final Deity d = Deities.getDeity(deityNum);
                    pinf.deity = d;
                }
                else {
                    pinf.deity = null;
                }
                pinf.favor = rs.getFloat("FAVOR");
                pinf.faith = rs.getFloat("FAITH");
                final byte gid = rs.getByte("GOD");
                if (gid > 0) {
                    final Deity d2 = Deities.getDeity(gid);
                    pinf.god = d2;
                }
                pinf.lastChangedDeity = rs.getLong("LASTCHANGEDDEITY");
                pinf.changedKingdom = rs.getByte("NUMSCHANGEDKINGDOM");
                pinf.realdeath = rs.getByte("REALDEATH");
                pinf.muted = rs.getBoolean("MUTED");
                pinf.muteTimes = rs.getShort("MUTETIMES");
                pinf.lastFaith = rs.getLong("LASTFAITH");
                pinf.numFaith = rs.getByte("NUMFAITH");
                pinf.creationDate = rs.getLong("CREATIONDATE");
                long face = rs.getLong("FACE");
                if (face == 0L) {
                    face = Server.rand.nextLong();
                }
                pinf.face = face;
                pinf.reputation = rs.getInt("REPUTATION");
                pinf.lastPolledReputation = rs.getLong("LASTPOLLEDREP");
                if (pinf.lastPolledReputation == 0L) {
                    pinf.lastPolledReputation = System.currentTimeMillis();
                }
                final int titnum = rs.getInt("TITLE");
                if (titnum > 0) {
                    pinf.title = Titles.Title.getTitle(titnum);
                }
                try {
                    final int secTitleNum = rs.getInt("SECONDTITLE");
                    if (secTitleNum > 0) {
                        pinf.secondTitle = Titles.Title.getTitle(secTitleNum);
                    }
                }
                catch (SQLException ex) {
                    PlayerInfoFactory.logger.severe("You may need to run the script addSecondTitle.sql!");
                    PlayerInfoFactory.logger.severe(ex.getMessage());
                    pinf.secondTitle = null;
                }
                pinf.pet = rs.getLong("PET");
                pinf.lastLogout = rs.getLong("LASTLOGOUT");
                pinf.nicotine = rs.getFloat("NICOTINE");
                pinf.alcohol = rs.getFloat("ALCOHOL");
                pinf.nicotineAddiction = rs.getLong("NICOTINETIME");
                pinf.alcoholAddiction = rs.getLong("ALCOHOLTIME");
                pinf.mayMute = rs.getBoolean("MAYMUTE");
                pinf.overRideShop = rs.getBoolean("MAYUSESHOP");
                pinf.muteexpiry = rs.getLong("MUTEEXPIRY");
                pinf.mutereason = rs.getString("MUTEREASON");
                pinf.lastServer = rs.getInt("LASTSERVER");
                pinf.currentServer = rs.getInt("CURRENTSERVER");
                pinf.pwQuestion = rs.getString("PWQUESTION");
                pinf.pwAnswer = rs.getString("PWANSWER");
                pinf.lastChangedVillage = rs.getLong("CHANGEDVILLAGE");
                pinf.fightmode = rs.getByte("FIGHTMODE");
                pinf.nextAffinity = rs.getLong("NEXTAFFINITY");
                pinf.lastvehicle = rs.getLong("VEHICLE");
                pinf.lastTaggedKindom = rs.getByte("ENEMYTERR");
                pinf.lastMovedBetweenKingdom = rs.getLong("LASTMOVEDTERR");
                pinf.priestType = rs.getByte("PRIESTTYPE");
                pinf.lastChangedPriestType = rs.getLong("LASTCHANGEDPRIEST");
                pinf.hasMovedInventory = rs.getBoolean("MOVEDINV");
                pinf.hasSkillGain = rs.getBoolean("HASSKILLGAIN");
                pinf.lastTriggerEffect = rs.getInt("LASTTRIGGER");
                pinf.lastChangedKindom = rs.getLong("LASTCHANGEDKINGDOM");
                pinf.championTimeStamp = rs.getLong("LASTLOSTCHAMPION");
                pinf.championPoints = rs.getShort("CHAMPIONPOINTS");
                pinf.champChanneling = rs.getFloat("CHAMPCHANNELING");
                pinf.epicKingdom = rs.getByte("EPICKINGDOM");
                pinf.epicServerId = rs.getInt("EPICSERVER");
                pinf.chaosKingdom = rs.getByte("CHAOSKINGDOM");
                pinf.hotaWins = rs.getShort("HOTA_WINS");
                pinf.spamMode = rs.getBoolean("SPAMMODE");
                pinf.karma = rs.getInt("KARMA");
                pinf.maxKarma = rs.getInt("MAXKARMA");
                pinf.totalKarma = rs.getInt("TOTALKARMA");
                pinf.blood = rs.getByte("BLOOD");
                pinf.flags = rs.getLong("FLAGS");
                pinf.flags2 = rs.getLong("FLAGS2");
                pinf.abilities = rs.getLong("ABILITIES");
                pinf.abilityTitle = rs.getInt("ABILITYTITLE");
                pinf.undeadType = rs.getByte("UNDEADTYPE");
                pinf.undeadKills = rs.getInt("UNDEADKILLS");
                pinf.undeadPlayerKills = rs.getInt("UNDEADPKILLS");
                pinf.undeadPlayerSeconds = rs.getInt("UNDEADPSECS");
                pinf.moneyEarnedBySellingEver = rs.getLong("MONEYSALES");
                pinf.setFlagBits(pinf.flags);
                pinf.setFlag2Bits(pinf.flags2);
                pinf.setAbilityBits(pinf.abilities);
                pinf.scenarioKarma = rs.getInt("SCENARIOKARMA");
                pinf.loaded = true;
                if ((Servers.localServer.id == pinf.currentServer || Servers.localServer.LOGINSERVER) && pinf.paymentExpireDate > 0L) {
                    pinf.awards = Awards.getAwards(pinf.wurmId);
                }
                PlayerInfoFactory.playerInfos.put(name, pinf);
                PlayerInfoFactory.playerInfosWurmId.put(pinf.wurmId, pinf);
                if (Servers.isThisLoginServer()) {
                    PlayerInfoFactory.playerStatus.put(pinf.wurmId, new PlayerState(pinf.currentServer, pinf.wurmId, pinf.name, pinf.lastLogin, pinf.lastLogout, PlayerOnlineStatus.OFFLINE));
                }
            }
        }
        catch (SQLException sqex) {
            PlayerInfoFactory.logger.log(Level.WARNING, sqex.getMessage(), sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.currentTimeMillis();
            PlayerInfoFactory.logger.info("Loaded " + PlayerInfoFactory.playerInfos.size() + " PlayerInfos from the database took " + (end - now) + " ms");
        }
    }
    
    public static final void transferPlayersToWild() {
        PlayerInfoFactory.logger.log(Level.INFO, "Starting to migrate accounts");
        final ServerEntry localServer = Servers.localServer;
        final ServerEntry wildServer = Servers.getServerWithId(3);
        final String targetIp = wildServer.INTRASERVERADDRESS;
        final int targetPort = Integer.parseInt(wildServer.INTRASERVERPORT);
        final String serverpass = wildServer.INTRASERVERPASSWORD;
        int tilex = wildServer.SPAWNPOINTJENNX;
        int tiley = wildServer.SPAWNPOINTJENNY;
        for (final PlayerInfo p : PlayerInfoFactory.playerInfos.values()) {
            if ((p.getPaymentExpire() > 0L || p.money >= 50000L) && p.currentServer == localServer.id && !p.banned) {
                try {
                    final Player player = new Player(p);
                    Server.getInstance().addPlayer(player);
                    player.checkBodyInventoryConsistency();
                    player.loadSkills();
                    Items.loadAllItemsForCreature(player, player.getStatus().getInventoryId());
                    player.getBody().load();
                    PlayerTransfer.willItemsTransfer(player, true, 3);
                    tilex = wildServer.SPAWNPOINTJENNX;
                    tiley = wildServer.SPAWNPOINTJENNY;
                    if (player.getKingdomId() == 3) {
                        tilex = wildServer.SPAWNPOINTLIBX;
                        tiley = wildServer.SPAWNPOINTLIBY;
                    }
                    else if (player.getKingdomId() == 2) {
                        tilex = wildServer.SPAWNPOINTMOLX;
                        tiley = wildServer.SPAWNPOINTMOLY;
                    }
                    final PlayerTransfer pt = new PlayerTransfer(Server.getInstance(), player, targetIp, targetPort, serverpass, 3, tilex, tiley, true, false, player.getKingdomId());
                    pt.copiedToLoginServer = true;
                    Server.getInstance().addIntraCommand(pt);
                }
                catch (Exception ex) {
                    PlayerInfoFactory.logger.log(Level.INFO, ex.getMessage(), ex);
                }
            }
        }
        PlayerInfoFactory.logger.log(Level.INFO, "Created intra commands");
    }
    
    public static final PlayerInfo[] getPlayerInfos() {
        return PlayerInfoFactory.playerInfos.values().toArray(new PlayerInfo[PlayerInfoFactory.playerInfos.size()]);
    }
    
    public static final PlayerInfo[] getPlayerInfosWithEmail(final String email) {
        final Set<PlayerInfo> infos = new HashSet<PlayerInfo>();
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info == null) {
                PlayerInfoFactory.logger.log(Level.WARNING, "getPlayerInfosWithEmail() NULL in playerInfos.values()??");
            }
            else {
                try {
                    info.load();
                    if (!wildCardMatch(info.emailAddress.toLowerCase(), email.toLowerCase())) {
                        continue;
                    }
                    infos.add(info);
                }
                catch (IOException e) {
                    PlayerInfoFactory.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return infos.toArray(new PlayerInfo[infos.size()]);
    }
    
    public static final PlayerInfo[] getPlayerInfosWithIpAddress(final String ipaddress) {
        final Set<PlayerInfo> infos = new HashSet<PlayerInfo>();
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info == null) {
                PlayerInfoFactory.logger.log(Level.WARNING, "getPlayerInfosWithIpAddress() NULL in playerInfos.values()??");
            }
            else {
                try {
                    info.load();
                    if (info.ipaddress == null || !wildCardMatch(info.ipaddress, ipaddress)) {
                        continue;
                    }
                    infos.add(info);
                }
                catch (IOException e) {
                    PlayerInfoFactory.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return infos.toArray(new PlayerInfo[infos.size()]);
    }
    
    public static boolean wildCardMatch(final String text, final String pattern) {
        final String[] cards = pattern.split("\\*");
        int offset = 0;
        boolean first = true;
        for (final String card : cards) {
            if (card.length() > 0) {
                final int idx = text.indexOf(card, offset);
                if (idx == -1 || (first && idx != 0)) {
                    return false;
                }
                offset = idx + card.length();
            }
            first = false;
        }
        return offset >= text.length() || pattern.endsWith("*");
    }
    
    public static PlayerInfo getPlayerInfoWithWurmId(final long wurmId) {
        return PlayerInfoFactory.playerInfosWurmId.get(wurmId);
    }
    
    public static Optional<PlayerInfo> getPlayerInfoOptional(final long wurmId) {
        return Optional.ofNullable(PlayerInfoFactory.playerInfosWurmId.get(wurmId));
    }
    
    public static PlayerInfo getPlayerInfoWithName(final String name) {
        return PlayerInfoFactory.playerInfos.get(LoginHandler.raiseFirstLetter(name));
    }
    
    public static Optional<PlayerInfo> getPlayerInfoOptional(final String name) {
        return Optional.ofNullable(PlayerInfoFactory.playerInfos.get(LoginHandler.raiseFirstLetter(name)));
    }
    
    public static Map<Long, byte[]> getPlayerStates(final long[] wurmids) throws RemoteException, WurmServerException {
        if (Servers.localServer.id == Servers.loginServer.id) {
            final Map<Long, byte[]> toReturn = new HashMap<Long, byte[]>();
            if (wurmids.length > 0) {
                for (int x = 0; x < wurmids.length; ++x) {
                    toReturn.put(wurmids[x], new PlayerState(wurmids[x]).encode());
                }
            }
            else {
                for (final PlayerState pState : PlayerInfoFactory.playerStatus.values()) {
                    toReturn.put(pState.getPlayerId(), pState.encode());
                }
            }
            return toReturn;
        }
        final LoginServerWebConnection lsw = new LoginServerWebConnection();
        return lsw.getPlayerStates(wurmids);
    }
    
    public static PlayerState getPlayerState(final long playerWurmId) {
        return PlayerInfoFactory.playerStatus.get(playerWurmId);
    }
    
    private static boolean playerJustTransfered(final PlayerState playerState) {
        final PlayerInfo pinf = getPlayerInfoWithWurmId(playerState.getPlayerId());
        if (pinf != null) {
            try {
                pinf.load();
                if (pinf.currentServer != Servers.getLocalServerId()) {
                    return true;
                }
            }
            catch (IOException e) {
                PlayerInfoFactory.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        return false;
    }
    
    public static final void setPlantedSignFalse() {
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            info.plantedSign = 0L;
        }
    }
    
    public static boolean doesPlayerExist(String name) {
        name = LoginHandler.raiseFirstLetter(name);
        return doesPlayerInfoExist(name);
    }
    
    public static String[] getAccountsForEmail(final String email) {
        final Set<String> set = new HashSet<String>();
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info.emailAddress.toLowerCase().equals(email.toLowerCase())) {
                set.add(info.name);
            }
        }
        return set.toArray(new String[set.size()]);
    }
    
    public static PlayerInfo[] getPlayerInfosForEmail(final String email) {
        final Set<PlayerInfo> set = new HashSet<PlayerInfo>();
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info.emailAddress.toLowerCase().equals(email.toLowerCase())) {
                set.add(info);
            }
        }
        return set.toArray(new PlayerInfo[set.size()]);
    }
    
    public static PlayerInfo getPlayerSleepingInBed(final long bedid) {
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info.bed == bedid) {
                if (info.lastLogin <= 0L && info.lastLogout > System.currentTimeMillis() - 86400000L && info.currentServer == Servers.localServer.id) {
                    return info;
                }
                return null;
            }
        }
        return null;
    }
    
    public static final int getNumberOfPayingPlayers() {
        final long now = System.currentTimeMillis();
        int nums = 0;
        try {
            for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
                if (info.getPower() == 0 && info.paymentExpireDate > now && (info.getCurrentServer() == Servers.localServer.id || Servers.localServer.LOGINSERVER)) {
                    ++nums;
                }
            }
        }
        catch (Exception ex) {
            PlayerInfoFactory.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        return nums;
    }
    
    public static final int getNumberOfPayingPlayersEver() {
        int nums = 0;
        try {
            for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
                if (info.getPower() == 0 && info.paymentExpireDate > 0L && (info.getCurrentServer() == Servers.localServer.id || Servers.localServer.LOGINSERVER)) {
                    ++nums;
                }
            }
        }
        catch (Exception ex) {
            PlayerInfoFactory.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        return nums;
    }
    
    public static String rename(final String oldName, final String newName, final String newPass, final int power) throws IOException {
        PlayerInfoFactory.logger.log(Level.INFO, "Trying to rename " + oldName + " to " + newName);
        if (!PlayerInfoFactory.playerInfos.containsKey(oldName)) {
            return "";
        }
        final PlayerInfo pinf = PlayerInfoFactory.playerInfos.get(oldName);
        PlayerInfoFactory.logger.log(Level.INFO, "Trying to rename " + oldName + " to " + newName + " power=" + power + ", pinf power=" + pinf.power);
        if (pinf.power < power) {
            pinf.setName(newName);
            pinf.updatePassword(newPass);
            PlayerInfoFactory.playerInfos.remove(oldName);
            PlayerInfoFactory.playerInfos.put(newName, pinf);
            try {
                final Player p = Players.getInstance().getPlayer(oldName);
                p.refreshVisible();
                p.getCommunicator().sendSelfToLocal();
                p.getCommunicator().sendSafeServerMessage("Your password now is '" + newPass + "'. Please take a note of this.");
            }
            catch (NoSuchPlayerException ex) {}
            try {
                final Village[] villages2;
                final Village[] villages = villages2 = Villages.getVillages();
                for (final Village lVillage : villages2) {
                    if (lVillage.mayorName.equals(oldName)) {
                        lVillage.setMayor(newName);
                    }
                }
            }
            catch (IOException iox) {
                PlayerInfoFactory.logger.log(Level.WARNING, oldName + " failed to change the mayorname to " + newName, iox);
                return Servers.localServer.name + " failed to change the mayor name from " + oldName + " to " + newName;
            }
            return Servers.localServer.name + " - ok\n";
        }
        return Servers.localServer.name + " you do not have the power to do that.";
    }
    
    public static String changePassword(final String changerName, final String name, final String newPass, final int power) throws IOException {
        if (!PlayerInfoFactory.playerInfos.containsKey(name)) {
            return "";
        }
        final PlayerInfo pinf = PlayerInfoFactory.playerInfos.get(name);
        if (pinf.power < power || changerName.equals(name)) {
            pinf.updatePassword(newPass);
            if (!changerName.equals(name)) {
                try {
                    final Player p = Players.getInstance().getPlayer(name);
                    p.getCommunicator().sendSafeServerMessage("Your password has been changed by " + changerName + " to " + newPass);
                }
                catch (NoSuchPlayerException ex) {}
            }
            PlayerInfoFactory.logger.log(Level.INFO, changerName + " changed the password of " + name + ".");
            return Servers.localServer.name + " - ok\n";
        }
        return Servers.localServer.name + " you do not have the power to do that.";
    }
    
    public static boolean doesEmailExist(final String email) {
        final PlayerInfo[] accs = getPlayerInfosForEmail(email);
        return accs.length > 0;
    }
    
    public static boolean verifyPasswordForEmail(final String email, final String password, final int power) {
        final PlayerInfo[] accs = getPlayerInfosForEmail(email);
        boolean ok = true;
        if (accs.length > 0) {
            if (accs.length > 4) {
                return false;
            }
            ok = false;
            if (power > 0) {
                ok = true;
            }
            for (final PlayerInfo lAcc : accs) {
                if (power == 0 || lAcc.power > power) {
                    ok = false;
                    if (password != null) {
                        try {
                            if (lAcc.password.equals(LoginHandler.hashPassword(password, LoginHandler.encrypt(LoginHandler.raiseFirstLetter(lAcc.name))))) {
                                return true;
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
            }
        }
        return ok;
    }
    
    public static String changeEmail(final String changerName, final String name, final String newEmail, final String password, final int power, final String pwQuestion, final String pwAnswer) throws IOException {
        if (!PlayerInfoFactory.playerInfos.containsKey(name)) {
            return "";
        }
        final PlayerInfo pinf = PlayerInfoFactory.playerInfos.get(name);
        if (pinf.power >= power && !changerName.equals(name)) {
            return Servers.localServer.name + " you do not have the power to do that.";
        }
        boolean ok = false;
        String retrievalInfo = "";
        if (pwQuestion != null && pwAnswer != null && changerName.equals(name) && ((pwQuestion.length() > 3 && !pwQuestion.equals(pinf.pwQuestion)) || (pwAnswer.length() > 2 && !pwAnswer.equals(pinf.pwAnswer)))) {
            pinf.setPassRetrieval(pwQuestion, pwAnswer);
            retrievalInfo = " Retrieval info updated.";
        }
        if (doesEmailExist(newEmail)) {
            if (verifyPasswordForEmail(newEmail, password, power)) {
                ok = true;
            }
            PlayerInfoFactory.logger.log(Level.INFO, "Email exists for " + pinf.name + " " + pinf.password + " " + pinf.emailAddress + " new email:" + newEmail + " verified=" + ok);
        }
        else if (pinf.power >= power) {
            try {
                if (pinf.password.equals(LoginHandler.hashPassword(password, LoginHandler.encrypt(LoginHandler.raiseFirstLetter(pinf.name))))) {
                    ok = true;
                }
            }
            catch (Exception ex) {
                PlayerInfoFactory.logger.log(Level.INFO, "Skipped " + pinf.name + " " + pinf.password + " " + pinf.emailAddress);
            }
        }
        else {
            ok = true;
        }
        if (!ok) {
            return "NO" + retrievalInfo;
        }
        pinf.setEmailAddress(newEmail);
        PlayerInfoFactory.logger.log(Level.INFO, changerName + " changed the email of " + name + " to " + newEmail + "." + retrievalInfo);
        try {
            final Player p = Players.getInstance().getPlayer(name);
            p.getCommunicator().sendSafeServerMessage("Your email has been changed by " + changerName + " to " + newEmail + "." + retrievalInfo);
        }
        catch (NoSuchPlayerException ex2) {}
        return Servers.localServer.name + " - ok " + retrievalInfo + "\n";
    }
    
    public static final void switchFatigue() {
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            info.saveSwitchFatigue();
        }
    }
    
    public static final void resetScenarioKarma() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("UPDATE PLAYERS SET SCENARIOKARMA=0");
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            PlayerInfoFactory.logger.log(Level.WARNING, "Failed to reset scenario karma");
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            info.scenarioKarma = 0;
        }
        for (final Player p : Players.getInstance().getPlayers()) {
            p.sendScenarioKarma();
        }
    }
    
    public static void resetFaithGain() {
        for (final PlayerInfo p : PlayerInfoFactory.playerInfos.values()) {
            p.numFaith = 0;
            p.lastFaith = 0L;
        }
    }
    
    public static final int getNumberOfActivePlayersWithDeity(final int deityNumber) {
        int nums = 0;
        final long breakOff = System.currentTimeMillis() - 604800000L;
        for (final PlayerInfo p : PlayerInfoFactory.playerInfos.values()) {
            if (p.getDeity() != null && p.getDeity().number == deityNumber && p.getLastLogin() > breakOff) {
                ++nums;
            }
        }
        return nums;
    }
    
    public static final PlayerInfo[] getActivePriestsForDeity(final int deityNumber) {
        final Set<PlayerInfo> infos = new HashSet<PlayerInfo>();
        final long breakOff = System.currentTimeMillis() - 604800000L;
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info == null) {
                PlayerInfoFactory.logger.log(Level.WARNING, "getPlayerInfosWithEmail() NULL in playerInfos.values()??");
            }
            else {
                try {
                    info.load();
                    if (info.getDeity() == null || info.getDeity().number != deityNumber || !info.isPriest || info.getLastLogin() <= breakOff) {
                        continue;
                    }
                    infos.add(info);
                }
                catch (IOException e) {
                    PlayerInfoFactory.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return infos.toArray(new PlayerInfo[infos.size()]);
    }
    
    public static final PlayerInfo[] getActiveFollowersForDeity(final int deityNumber) {
        final Set<PlayerInfo> infos = new HashSet<PlayerInfo>();
        final long breakOff = System.currentTimeMillis() - 604800000L;
        for (final PlayerInfo info : PlayerInfoFactory.playerInfos.values()) {
            if (info == null) {
                PlayerInfoFactory.logger.log(Level.WARNING, "getPlayerInfosWithEmail() NULL in playerInfos.values()??");
            }
            else {
                try {
                    info.load();
                    if (info.getDeity() == null || info.getDeity().number != deityNumber || info.getLastLogin() <= breakOff) {
                        continue;
                    }
                    infos.add(info);
                }
                catch (IOException e) {
                    PlayerInfoFactory.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return infos.toArray(new PlayerInfo[infos.size()]);
    }
    
    public static final int getVotesForKingdom(final byte kingdom) {
        int nums = 0;
        final PlayerInfo[] playerInfos;
        final PlayerInfo[] pinfs = playerInfos = getPlayerInfos();
        for (final PlayerInfo lPinf : playerInfos) {
            if (lPinf.votedKing && Players.getInstance().getKingdomForPlayer(lPinf.wurmId) == kingdom) {
                ++nums;
            }
        }
        return nums;
    }
    
    public static final void resetVotesForKingdom(final byte kingdom) {
        final PlayerInfo[] playerInfos;
        final PlayerInfo[] pinfs = playerInfos = getPlayerInfos();
        for (final PlayerInfo lPinf : playerInfos) {
            if (lPinf.votedKing && Players.getInstance().getKingdomForPlayer(lPinf.wurmId) == kingdom) {
                lPinf.setVotedKing(false);
            }
        }
    }
    
    public static final int getNumberOfChamps(final int deityNum) {
        int nums = 0;
        final PlayerInfo[] playerInfos;
        final PlayerInfo[] pinfs = playerInfos = getPlayerInfos();
        for (final PlayerInfo lPinf : playerInfos) {
            if (lPinf.realdeath > 0 && lPinf.realdeath < 4 && System.currentTimeMillis() - lPinf.championTimeStamp < 14515200000L && lPinf.getDeity() != null && lPinf.getDeity().number == deityNum) {
                ++nums;
            }
        }
        return nums;
    }
    
    public static final void whosOnline() {
        grabPlayerStates();
        for (final PlayerState entry : PlayerInfoFactory.playerStatus.values()) {
            if (entry.getState() == PlayerOnlineStatus.ONLINE && entry.getServerId() == Servers.getLocalServerId()) {
                final WcPlayerStatus wps = new WcPlayerStatus(entry);
                wps.sendToLoginServer();
            }
        }
    }
    
    public static final void grabPlayerStates() {
        if (PlayerInfoFactory.playerStatus.size() < 100) {
            try {
                final Map<Long, byte[]> statusBytes = getPlayerStates(PlayerInfoFactory.EMPTY_LONG_PRIMITIVE_ARRAY);
                for (final byte[] entry : statusBytes.values()) {
                    final PlayerState pState = new PlayerState(entry);
                    PlayerInfoFactory.playerStatus.put(pState.getPlayerId(), pState);
                }
                PlayerInfoFactory.logger.log(Level.INFO, "Got " + PlayerInfoFactory.playerStatus.size() + " player status");
            }
            catch (RemoteException e) {
                PlayerInfoFactory.logger.log(Level.WARNING, e.getMessage(), e);
            }
            catch (WurmServerException e2) {
                PlayerInfoFactory.logger.log(Level.WARNING, e2.getMessage(), e2);
            }
        }
    }
    
    public static final void updatePlayerState(final Player player, final long whenStateChanged, final PlayerOnlineStatus aStatus) {
        final PlayerState pState = new PlayerState(player.getWurmId(), player.getName(), whenStateChanged, aStatus);
        if (playerJustTransfered(pState)) {
            return;
        }
        updatePlayerState(pState);
        if (aStatus == PlayerOnlineStatus.ONLINE) {
            for (final Friend f : player.getFriends()) {
                final PlayerState fState = PlayerInfoFactory.playerStatus.get(f.getFriendId());
                if (fState != null) {
                    player.getCommunicator().sendFriend(fState, f.getNote());
                }
            }
        }
        final WcPlayerStatus wps = new WcPlayerStatus(pState);
        if (Servers.isThisLoginServer()) {
            wps.sendFromLoginServer();
        }
        else {
            wps.sendToLoginServer();
        }
    }
    
    public static final void updatePlayerState(final PlayerState pState) {
        if (pState.getState() == PlayerOnlineStatus.UNKNOWN) {
            return;
        }
        PlayerInfoFactory.statesToUpdate.add(pState);
    }
    
    public static final void handlePlayerStateList() {
        for (PlayerState pState = PlayerInfoFactory.statesToUpdate.pollFirst(); pState != null; pState = PlayerInfoFactory.statesToUpdate.pollFirst()) {
            boolean tellAll = true;
            final PlayerState oldState = PlayerInfoFactory.playerStatus.get(pState.getPlayerId());
            if (pState.getState() == PlayerOnlineStatus.DELETE_ME) {
                PlayerInfoFactory.playerStatus.remove(pState.getPlayerId());
            }
            else if (oldState != null && oldState.getState() == PlayerOnlineStatus.OFFLINE && pState.getState() == PlayerOnlineStatus.LOST_LINK) {
                tellAll = false;
            }
            else {
                PlayerInfoFactory.playerStatus.put(pState.getPlayerId(), pState);
            }
            if (tellAll) {
                Players.tellFriends(pState);
                if (oldState == null) {
                    Tickets.playerStateChange(pState);
                }
                else if (pState.getState() != oldState.getState()) {
                    if (pState.getState() == PlayerOnlineStatus.ONLINE || oldState.getState() == PlayerOnlineStatus.ONLINE) {
                        Tickets.playerStateChange(pState);
                    }
                }
            }
        }
        for (final Map.Entry<Long, PlayerState> entry : PlayerInfoFactory.friendsToUpdate.entrySet()) {
            try {
                final long playerWurmId = entry.getKey();
                final Player player = Players.getInstance().getPlayer(playerWurmId);
                player.getCommunicator().sendFriend(entry.getValue());
            }
            catch (NoSuchPlayerException ex) {}
        }
        PlayerInfoFactory.friendsToUpdate.clear();
    }
    
    public static final boolean isPlayerOnline(final long playerWurmId) {
        final PlayerState pState = PlayerInfoFactory.playerStatus.get(playerWurmId);
        return pState != null && pState.getState() == PlayerOnlineStatus.ONLINE;
    }
    
    public static final String getPlayerName(final long playerWurmId) {
        final PlayerState pState = PlayerInfoFactory.playerStatus.get(playerWurmId);
        if (pState != null) {
            return pState.getPlayerName();
        }
        return "Unknown";
    }
    
    public static final long getWurmId(final String name) {
        for (final PlayerState pState : PlayerInfoFactory.playerStatus.values()) {
            if (pState.getPlayerName().equalsIgnoreCase(name)) {
                return pState.getPlayerId();
            }
        }
        return -10L;
    }
    
    public static final PlayerState getPlayerState(final String name) {
        for (final PlayerState pState : PlayerInfoFactory.playerStatus.values()) {
            if (pState.getPlayerName().equalsIgnoreCase(name)) {
                return pState;
            }
        }
        return null;
    }
    
    public static final void setPlayerStatesToOffline(final int serverId) {
        for (final PlayerState pState : PlayerInfoFactory.playerStatus.values()) {
            if (pState.getServerId() == serverId && pState.getState() != PlayerOnlineStatus.OFFLINE) {
                final PlayerState newState = new PlayerState(pState.getServerId(), pState.getPlayerId(), pState.getPlayerName(), System.currentTimeMillis(), PlayerOnlineStatus.OFFLINE);
                updatePlayerState(newState);
            }
        }
    }
    
    public static long[] getPlayersOnCurrentServer() {
        final Set<Long> pIds = new HashSet<Long>();
        for (final PlayerState pState : PlayerInfoFactory.playerStatus.values()) {
            if (pState.getServerId() == Servers.getLocalServerId()) {
                pIds.add(pState.getPlayerId());
            }
        }
        final long[] ans = new long[pIds.size()];
        int x = 0;
        for (final Long pId : pIds) {
            ans[x++] = pId;
        }
        return ans;
    }
    
    public static long breakFriendship(final String playerName, final long playerWurmId, final String friendName) {
        for (final PlayerState fState : PlayerInfoFactory.playerStatus.values()) {
            if (fState.getPlayerName().equalsIgnoreCase(friendName)) {
                final long friendWurmId = fState.getPlayerId();
                breakFriendship(playerName, playerWurmId, friendName, friendWurmId);
                return friendWurmId;
            }
        }
        return -10L;
    }
    
    public static void breakFriendship(final String playerName, final long playerWurmId, final String friendName, final long friendWurmId) {
        breakFriendship(playerWurmId, friendWurmId, friendName);
        breakFriendship(friendWurmId, playerWurmId, playerName);
    }
    
    private static void breakFriendship(final long playerWurmId, final long friendWurmId, final String friendName) {
        final PlayerInfo pInfo = getPlayerInfoWithWurmId(playerWurmId);
        if (pInfo != null) {
            pInfo.removeFriend(friendWurmId);
            final PlayerState pState = new PlayerState(friendWurmId, friendName, -1L, PlayerOnlineStatus.DELETE_ME);
            PlayerInfoFactory.friendsToUpdate.put(playerWurmId, pState);
        }
    }
    
    public static final WurmRecord getChampionRecord(final String name) {
        for (final WurmRecord record : PlayerInfoFactory.championRecords) {
            if (record.getHolder().toLowerCase().equals(name.toLowerCase()) && record.isCurrent()) {
                return record;
            }
        }
        return null;
    }
    
    public static final void addChampRecord(final WurmRecord record) {
        PlayerInfoFactory.championRecords.add(record);
    }
    
    public static final WurmRecord[] getChampionRecords() {
        return PlayerInfoFactory.championRecords.toArray(new WurmRecord[PlayerInfoFactory.championRecords.size()]);
    }
    
    public static void expelMember(final long playerId, final byte fromKingdomId, final byte toKingdomId, final int originServer) {
        boolean isOnline = true;
        final ServerEntry server = Servers.getServerWithId(originServer);
        if (server == null) {
            PlayerInfoFactory.logger.warning("ExpelMember request from invalid server ID " + originServer + " for playerID " + playerId);
            return;
        }
        Player p = Players.getInstance().getPlayerOrNull(playerId);
        if (p == null) {
            final PlayerInfo pInfo = PlayerInfoFactory.playerInfosWurmId.get(playerId);
            if (pInfo == null) {
                return;
            }
            isOnline = false;
            try {
                pInfo.load();
                p = new Player(pInfo);
            }
            catch (Exception ex) {
                PlayerInfoFactory.logger.log(Level.WARNING, "Unable to complete expel command for: " + playerId, ex);
                return;
            }
        }
        PlayerInfoFactory.logger.info("Expelling " + p.getName() + " from " + Kingdoms.getNameFor(fromKingdomId) + " on " + server.getName() + ", new kingdom: " + Kingdoms.getNameFor(toKingdomId));
        Label_0381: {
            if (server.EPIC && Servers.localServer.EPIC) {
                try {
                    if (!p.setKingdomId(toKingdomId, false, false, isOnline)) {
                        PlayerInfoFactory.logger.log(Level.WARNING, "Unable to complete expel command for: " + p.getName());
                        return;
                    }
                    break Label_0381;
                }
                catch (IOException iox) {
                    PlayerInfoFactory.logger.log(Level.WARNING, "Unable to complete expel command for: " + p.getName(), iox);
                    return;
                }
            }
            if (server.EPIC && !Server.getInstance().isPS()) {
                p.getSaveFile().setEpicLocation(toKingdomId, p.getSaveFile().epicServerId);
            }
            else if (server.PVPSERVER || server.isChaosServer()) {
                p.getSaveFile().setChaosKingdom(toKingdomId);
            }
        }
        if (isOnline) {
            p.getCommunicator().sendAlertServerMessage("You have been expelled from " + Kingdoms.getNameFor(fromKingdomId) + " on " + server.getName() + "!");
        }
    }
    
    static {
        playerInfos = new ConcurrentHashMap<String, PlayerInfo>();
        playerInfosWurmId = new ConcurrentHashMap<Long, PlayerInfo>();
        playerStatus = new ConcurrentHashMap<Long, PlayerState>();
        failedIds = new HashSet<Long>();
        statesToUpdate = new ConcurrentLinkedDeque<PlayerState>();
        friendsToUpdate = new ConcurrentHashMap<Long, PlayerState>();
        logger = Logger.getLogger(PlayerInfoFactory.class.getName());
        deletelogger = Logger.getLogger("deletions");
        referrers = new ConcurrentHashMap<Long, Set<Referer>>();
        PlayerInfoFactory.deletedPlayers = 0;
        PlayerInfoFactory.OFFLINETIME_UNTIL_FREEZE = 1296000000L;
        championRecords = new LinkedList<WurmRecord>();
    }
    
    public static final class FatigueSwitcher implements Runnable
    {
        @Override
        public void run() {
            if (PlayerInfoFactory.logger.isLoggable(Level.FINER)) {
                PlayerInfoFactory.logger.finer("Running newSingleThreadScheduledExecutor for calling PlayerInfoFactory.switchFatigue()");
            }
            try {
                final long now = System.nanoTime();
                PlayerInfoFactory.switchFatigue();
                final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
                if (lElapsedTime > Constants.lagThreshold) {
                    PlayerInfoFactory.logger.info("Finished calling PlayerInfoFactory.switchFatigue(), which took " + lElapsedTime + " millis.");
                }
            }
            catch (RuntimeException e) {
                PlayerInfoFactory.logger.log(Level.WARNING, "Caught exception in ScheduledExecutorService while calling PlayerInfoFactory.switchFatigue()", e);
                throw e;
            }
        }
    }
}
