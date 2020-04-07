// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import com.wurmonline.server.Constants;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.items.Itempool;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.players.PermissionsByPlayer;
import java.util.LinkedList;
import com.wurmonline.server.items.ItemSettings;
import com.wurmonline.server.items.ItemRequirement;
import com.wurmonline.server.items.ItemData;
import com.wurmonline.server.effects.EffectMetaData;
import com.wurmonline.server.creatures.CreatureDataStream;
import com.wurmonline.server.villages.Citizen;
import java.util.Map;
import com.wurmonline.server.zones.Zone;
import java.util.BitSet;
import java.util.Iterator;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.players.PlayerMetaData;
import com.wurmonline.server.spells.SpellEffectMetaData;
import com.wurmonline.server.skills.Affinities;
import com.wurmonline.server.items.ItemMetaData;
import java.util.HashSet;
import java.util.HashMap;
import com.wurmonline.server.items.Puppet;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.questions.QuestionParser;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.bodys.WoundMetaData;
import com.wurmonline.server.Items;
import com.wurmonline.server.spells.Cooldowns;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import com.wurmonline.server.players.MapAnnotation;
import java.sql.Timestamp;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.players.AchievementTemplate;
import com.wurmonline.server.players.Achievement;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.players.EpicPlayerTransferMetaData;
import com.wurmonline.server.players.Awards;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.skills.SkillMetaData;
import com.wurmonline.server.players.Cultist;
import java.io.DataInputStream;
import com.wurmonline.server.items.DbStrings;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.items.FrozenItemDbStrings;
import com.wurmonline.server.items.ItemDbStrings;
import com.wurmonline.server.WurmId;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import com.wurmonline.server.skills.AffinitiesTimed;
import com.wurmonline.server.items.RecipesByPlayer;
import com.wurmonline.server.spells.SpellEffect;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.creatures.CreaturePos;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Message;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Server;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.Servers;
import java.io.UnsupportedEncodingException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Players;
import java.io.IOException;
import java.util.logging.Level;
import java.nio.ByteBuffer;
import java.util.Set;
import com.wurmonline.server.ServerMonitoring;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.communication.SimpleConnectionListener;

public final class IntraServerConnection implements SimpleConnectionListener, MiscConstants, TimeConstants
{
    private final SocketConnection conn;
    private static final Logger logger;
    private ByteArrayOutputStream dataStream;
    private final ServerMonitoring wurmserver;
    private static final String DELETE_FRIENDS = "DELETE FROM FRIENDS WHERE WURMID=?";
    private static final String DELETE_ENEMIES = "DELETE FROM ENEMIES WHERE WURMID=?";
    private static final String DELETE_IGNORED = "DELETE FROM IGNORED WHERE WURMID=?";
    private static final String DELETE_TITLES = "DELETE FROM TITLES WHERE WURMID=?";
    private static final String DELETE_HISTORY_IP = "DELETE FROM PLAYERHISTORYIPS WHERE PLAYERID=?";
    private static final String DELETE_HISTORY_EMAIL = "DELETE FROM PLAYEREHISTORYEMAIL WHERE PLAYERID=?";
    private static final int DISCONNECT_TICKS = 200;
    private static long draggedItem;
    private static final Set<String> moneyDetails;
    private static final Set<String> timeDetails;
    public static String lastItemName;
    public static long lastItemId;
    private static boolean saving;
    
    IntraServerConnection(final SocketConnection aConn, final ServerMonitoring aServer) {
        this.conn = aConn;
        this.wurmserver = aServer;
    }
    
    @Override
    public void reallyHandle(final int num, final ByteBuffer byteBuffer) {
        final long check = System.currentTimeMillis();
        final short cmd = byteBuffer.get();
        if (IntraServerConnection.logger.isLoggable(Level.FINER)) {
            IntraServerConnection.logger.finer("Received cmd " + cmd);
        }
        if (cmd == 1) {
            this.validate(byteBuffer);
        }
        else if (cmd == 13) {
            try {
                this.sendPingAnswer();
            }
            catch (IOException ex10) {}
        }
        else if (cmd == 9) {
            final long wurmid = byteBuffer.getLong();
            try {
                final String name = Players.getInstance().getNameFor(wurmid);
                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
                pinf.load();
                this.sendPlayerVersion(pinf.version);
            }
            catch (NoSuchPlayerException nsp) {
                try {
                    this.sendPlayerVersion(0L);
                }
                catch (IOException iox2) {
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException iox3) {
                        IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                    }
                }
            }
            catch (IOException iox4) {
                try {
                    this.sendCommandFailed();
                }
                catch (IOException iox5) {
                    IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                }
            }
        }
        else if (cmd == 11) {
            final long wid = byteBuffer.getLong();
            try {
                final String name = Players.getInstance().getNameFor(wid);
                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
                pinf.load();
                this.sendPlayerPaymentExpire(pinf.getPaymentExpire());
            }
            catch (NoSuchPlayerException nsp) {
                try {
                    this.sendPlayerPaymentExpire(0L);
                }
                catch (IOException iox2) {
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException iox3) {
                        IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                    }
                }
            }
            catch (IOException iox4) {
                try {
                    this.sendCommandFailed();
                }
                catch (IOException iox5) {
                    IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                }
            }
        }
        else if (cmd == 6) {
            this.validateTransferRequest(byteBuffer);
        }
        else if (cmd == 3) {
            final int posx = byteBuffer.getInt();
            final int posy = byteBuffer.getInt();
            final boolean surfaced = byteBuffer.get() != 0;
            if (this.unpackPlayerData(posx, posy, surfaced)) {
                try {
                    this.sendCommandDone();
                }
                catch (IOException ex) {
                    try {
                        IntraServerConnection.logger.log(Level.WARNING, "Failed to receive user: " + ex.getMessage(), ex);
                        this.sendCommandFailed();
                    }
                    catch (IOException ex6) {
                        IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                    }
                    this.conn.ticksToDisconnect = 200;
                }
            }
            else {
                try {
                    this.sendCommandFailed();
                    IntraServerConnection.logger.log(Level.WARNING, "Failed to unpack data.");
                    this.conn.ticksToDisconnect = 200;
                }
                catch (IOException ex7) {
                    IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                }
            }
        }
        else if (cmd == 7) {
            if (this.readNextDataBlock(byteBuffer)) {
                try {
                    this.sendDataReceived();
                }
                catch (IOException iox6) {
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException ex8) {
                        IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.");
                    }
                }
            }
        }
        else if (cmd == 16) {
            this.conn.ticksToDisconnect = 200;
            final long wurmid = byteBuffer.getLong();
            final long currentMoney = byteBuffer.getLong();
            final long moneyAdded = byteBuffer.getLong();
            final int length = byteBuffer.getInt();
            final byte[] det = new byte[length];
            byteBuffer.get(det);
            String detail = "unknown";
            try {
                detail = new String(det, "UTF-8");
                if (IntraServerConnection.moneyDetails.contains(detail)) {
                    try {
                        this.sendCommandDone();
                        return;
                    }
                    catch (IOException ex11) {}
                }
            }
            catch (UnsupportedEncodingException ex2) {
                IntraServerConnection.logger.log(Level.WARNING, ex2.getMessage(), ex2);
            }
            final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmid);
            Label_0769: {
                if (info != null) {
                    try {
                        info.load();
                        break Label_0769;
                    }
                    catch (IOException iox) {
                        try {
                            IntraServerConnection.logger.log(Level.WARNING, "Failed to load player info for " + wurmid + ": " + iox.getMessage(), iox);
                            this.sendCommandFailed();
                        }
                        catch (IOException ex12) {}
                        this.conn.ticksToDisconnect = 200;
                        return;
                    }
                }
                IntraServerConnection.logger.log(Level.WARNING, wurmid + ", failed to locate player info and set money to " + currentMoney + "!");
                try {
                    this.sendCommandFailed();
                }
                catch (IOException ex13) {}
            }
            if (info != null && info.wurmId > 0L) {
                if (info.currentServer != Servers.localServer.id) {
                    IntraServerConnection.logger.warning("Received a CMD_SET_PLAYER_MONEY for player " + info.getName() + " (id: " + wurmid + ") but their currentserver (id: " + info.getCurrentServer() + ") is not this server (id: " + Servers.localServer.id + ")");
                }
                try {
                    info.setMoney(currentMoney);
                    if (detail.contains("Premium")) {
                        final Shop kingsShop = Economy.getEconomy().getKingsShop();
                        if (kingsShop != null) {
                            kingsShop.setMoney(kingsShop.getMoney() - moneyAdded);
                        }
                    }
                    new MoneyTransfer(info.getName(), wurmid, currentMoney, moneyAdded, detail, (byte)0, "");
                    this.sendCommandDone();
                    boolean referred = false;
                    if (detail.startsWith("Referred by ")) {
                        referred = true;
                        info.addToSleep(3600);
                    }
                    IntraServerConnection.moneyDetails.add(detail);
                    try {
                        final Player p = Players.getInstance().getPlayer(wurmid);
                        final Change c = new Change(currentMoney);
                        p.getCommunicator().sendNormalServerMessage("Your available money in the bank is now " + c.getChangeString() + ".");
                        if (referred) {
                            final String sleepString = "You also received an hour of sleep bonus which will increase your skill gain speed.";
                            p.getCommunicator().sendSafeServerMessage("You also received an hour of sleep bonus which will increase your skill gain speed.");
                        }
                    }
                    catch (NoSuchPlayerException ex14) {}
                }
                catch (IOException iox) {
                    IntraServerConnection.logger.log(Level.WARNING, wurmid + ", failed to set money to " + currentMoney + ".", iox);
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException iox7) {
                        this.conn.disconnect();
                    }
                }
            }
            else {
                IntraServerConnection.logger.log(Level.WARNING, wurmid + ", failed to locate player info and set money to " + currentMoney + "!");
                try {
                    this.sendCommandFailed();
                }
                catch (IOException iox8) {
                    this.conn.disconnect();
                }
            }
            if (System.currentTimeMillis() - check > 1000L) {
                IntraServerConnection.logger.log(Level.INFO, "Lag detected at CMD_SET_PLAYER_MONEY: " + (int)((System.currentTimeMillis() - check) / 1000L));
            }
        }
        else if (cmd == 17) {
            this.conn.ticksToDisconnect = 200;
            final long wurmid = byteBuffer.getLong();
            final long currentExpire = byteBuffer.getLong();
            final int days = byteBuffer.getInt();
            final int months = byteBuffer.getInt();
            final boolean dealItems = byteBuffer.get() > 0;
            final int length2 = byteBuffer.getInt();
            final byte[] det2 = new byte[length2];
            byteBuffer.get(det2);
            String detail2 = "unknown";
            try {
                detail2 = new String(det2, "UTF-8");
                if (IntraServerConnection.timeDetails.contains(detail2)) {
                    try {
                        this.sendCommandDone();
                        return;
                    }
                    catch (IOException ex15) {}
                }
            }
            catch (UnsupportedEncodingException ex3) {
                IntraServerConnection.logger.log(Level.WARNING, ex3.getMessage(), ex3);
            }
            final PlayerInfo info2 = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmid);
            if (info2 != null) {
                try {
                    info2.load();
                }
                catch (IOException iox9) {
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException iox10) {
                        this.conn.disconnect();
                    }
                    this.conn.ticksToDisconnect = 200;
                    if (System.currentTimeMillis() - check > 1000L) {
                        IntraServerConnection.logger.log(Level.INFO, "Lag detected at CMD_SET_PLAYER_PAYMENTEXPIRE IOEXCEPTION: " + (int)((System.currentTimeMillis() - check) / 1000L));
                    }
                    return;
                }
                catch (NullPointerException np) {
                    IntraServerConnection.logger.log(Level.WARNING, "No player with id=" + wurmid + " on this server.");
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException iox10) {
                        this.conn.disconnect();
                    }
                    this.conn.ticksToDisconnect = 200;
                    if (System.currentTimeMillis() - check > 1000L) {
                        IntraServerConnection.logger.log(Level.INFO, "Lag detected at CMD_SET_PLAYER_PAYMENTEXPIRE IOEXCEPTION: " + (int)((System.currentTimeMillis() - check) / 1000L));
                    }
                    return;
                }
            }
            if (info2.wurmId > 0L) {
                if (info2.currentServer != Servers.localServer.id) {
                    IntraServerConnection.logger.warning("Received a CMD_SET_PLAYER_PAYMENTEXPIRE for player " + info2.getName() + " (id: " + wurmid + ") but their currentserver (id: " + info2.getCurrentServer() + ") is not this server (id: " + Servers.localServer.id + ")");
                }
                try {
                    if (currentExpire > System.currentTimeMillis()) {
                        if (info2.getPaymentExpire() <= 0L) {
                            Server.addNewPlayer(info2.getName());
                        }
                        else {
                            Server.incrementOldPremiums(info2.getName());
                        }
                    }
                    info2.setPaymentExpire(currentExpire);
                    boolean referred2 = false;
                    if (detail2.startsWith("Referred by ")) {
                        referred2 = true;
                        info2.addToSleep(3600);
                    }
                    new TimeTransfer(info2.getName(), wurmid, months, dealItems, days, detail2);
                    this.sendCommandDone();
                    IntraServerConnection.timeDetails.add(detail2);
                    try {
                        final Player p2 = Players.getInstance().getPlayer(wurmid);
                        final String expireString = "You now have premier playing time until " + WurmCalendar.formatGmt(currentExpire) + ".";
                        p2.getCommunicator().sendSafeServerMessage(expireString);
                        if (referred2) {
                            final String sleepString2 = "You also received an hour of sleep bonus which will increase your skill gain speed.";
                            p2.getCommunicator().sendSafeServerMessage("You also received an hour of sleep bonus which will increase your skill gain speed.");
                        }
                        if (dealItems) {
                            try {
                                final Item inventory = p2.getInventory();
                                for (int x = 0; x < months; ++x) {
                                    final Item i = ItemFactory.createItem(666, 99.0f, "");
                                    inventory.insertItem(i, true);
                                }
                                IntraServerConnection.logger.log(Level.INFO, "Inserted " + months + " sleep powder in " + p2.getName() + " inventory " + inventory.getWurmId());
                                final Message rmess = new Message(null, (byte)3, ":Event", "You have received " + months + " sleeping powders in your inventory.");
                                rmess.setReceiver(p2.getWurmId());
                                Server.getInstance().addMessage(rmess);
                            }
                            catch (Exception ex4) {
                                IntraServerConnection.logger.log(Level.INFO, ex4.getMessage(), ex4);
                            }
                        }
                    }
                    catch (NoSuchPlayerException exp) {
                        if (dealItems) {
                            try {
                                final long inventoryId = DbCreatureStatus.getInventoryIdFor(info2.wurmId);
                                for (int x = 0; x < months; ++x) {
                                    final Item i = ItemFactory.createItem(666, 99.0f, "");
                                    i.setParentId(inventoryId, true);
                                    i.setOwnerId(info2.wurmId);
                                }
                                IntraServerConnection.logger.log(Level.INFO, "Inserted " + months + " sleep powder in offline " + info2.getName() + " inventory " + inventoryId);
                            }
                            catch (Exception ex5) {
                                IntraServerConnection.logger.log(Level.INFO, ex5.getMessage(), ex5);
                            }
                        }
                    }
                }
                catch (IOException iox9) {
                    try {
                        this.sendCommandFailed();
                    }
                    catch (IOException iox10) {
                        this.conn.disconnect();
                    }
                }
            }
            else {
                IntraServerConnection.logger.log(Level.WARNING, wurmid + ", failed to locate player info and set expire time to " + currentExpire + "!");
                try {
                    this.sendCommandFailed();
                }
                catch (IOException iox7) {
                    this.conn.disconnect();
                }
            }
            if (System.currentTimeMillis() - check > 1000L) {
                IntraServerConnection.logger.log(Level.INFO, "Lag detected at CMD_SET_PLAYER_PAYMENTEXPIRE: " + (int)((System.currentTimeMillis() - check) / 1000L));
            }
        }
        else if (cmd == 10) {
            try {
                this.sendTimeSync();
            }
            catch (IOException ex9) {
                this.conn.ticksToDisconnect = 200;
            }
            if (System.currentTimeMillis() - check > 1000L) {
                IntraServerConnection.logger.log(Level.INFO, "Lag detected at CMD_GET_TIME: " + (int)((System.currentTimeMillis() - check) / 1000L));
            }
        }
        else if (cmd == 18) {
            if (this.changePassword(byteBuffer)) {
                try {
                    this.sendCommandDone();
                }
                catch (IOException iox6) {
                    this.conn.ticksToDisconnect = 200;
                }
            }
            else {
                try {
                    this.sendCommandFailed();
                }
                catch (IOException iox6) {
                    this.conn.ticksToDisconnect = 200;
                }
            }
            if (System.currentTimeMillis() - check > 1000L) {
                IntraServerConnection.logger.log(Level.INFO, "Lag detected at CMD_SET_PLAYER_PASSWORD: " + (int)((System.currentTimeMillis() - check) / 1000L));
            }
        }
        else if (cmd == 15) {
            IntraServerConnection.logger.log(Level.INFO, "Received disconnect.");
            this.conn.disconnect();
        }
    }
    
    private boolean changePassword(final ByteBuffer byteBuffer) {
        final long playerId = byteBuffer.getLong();
        final int length = byteBuffer.getInt();
        final byte[] pw = new byte[length];
        byteBuffer.get(pw);
        try {
            final String hashedPassword = new String(pw, "UTF-8");
            return setNewPassword(playerId, hashedPassword);
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean setNewPassword(final long playerId, final String newHashedPassword) {
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(playerId);
        if (info != null) {
            try {
                info.load();
            }
            catch (Exception eex) {
                IntraServerConnection.logger.log(Level.WARNING, "Failed to load info for wurmid " + playerId + ". Password unchanged." + eex.getMessage(), eex);
            }
            if (info.wurmId <= 0L) {
                IntraServerConnection.logger.log(Level.WARNING, "Failed to load info for wurmid " + playerId + ". No info available. Password unchanged.");
            }
            else {
                info.setPassword(newHashedPassword);
                try {
                    final Player p = Players.getInstance().getPlayer(playerId);
                    p.getCommunicator().sendAlertServerMessage("Your password has been updated. Use the new one to connect next time.");
                }
                catch (NoSuchPlayerException ex) {}
            }
        }
        return true;
    }
    
    private boolean readNextDataBlock(final ByteBuffer byteBuffer) {
        if (this.dataStream == null) {
            this.dataStream = new ByteArrayOutputStream();
        }
        final int length = byteBuffer.getInt();
        final byte[] toput = new byte[length];
        byteBuffer.get(toput);
        this.dataStream.write(toput, 0, length);
        return byteBuffer.get() == 1;
    }
    
    public static final void deletePlayer(final long id) throws IOException {
        CreaturePos.delete(id);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM PLAYERS WHERE WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Skills for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM SKILLS WHERE OWNER=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Wounds for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM WOUNDS WHERE OWNER=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Friends for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM FRIENDS WHERE WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Enemies for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM ENEMIES WHERE WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Ignored for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM IGNORED WHERE WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Titles for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM TITLES WHERE WURMID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting IP History for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM PLAYERHISTORYIPS WHERE PLAYERID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                IntraServerConnection.logger.finest("Deleting Email History for Player ID: " + id);
            }
            ps = dbcon.prepareStatement("DELETE FROM PLAYEREHISTORYEMAIL WHERE PLAYERID=?");
            ps.setLong(1, id);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            SpellEffect.deleteEffectsForPlayer(id);
            RecipesByPlayer.deleteRecipesForPlayer(id);
            AffinitiesTimed.deleteTimedAffinitiesForPlayer(id);
        }
        catch (SQLException sqex) {
            throw new IOException("Problem deleting playerid: " + id + " due to " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static final void deletePlayer(final String name, final long id) throws IOException {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            if (id > -10L) {
                ps = dbcon.prepareStatement("DELETE FROM PLAYERS WHERE NAME=?");
                ps.setString(1, name);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM SKILLS WHERE OWNER=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM WOUNDS WHERE OWNER=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM FRIENDS WHERE WURMID=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM ENEMIES WHERE WURMID=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM IGNORED WHERE WURMID=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM TITLES WHERE WURMID=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM PLAYERHISTORYIPS WHERE PLAYERID=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM PLAYEREHISTORYEMAIL WHERE PLAYERID=?");
                ps.setLong(1, id);
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                SpellEffect.deleteEffectsForPlayer(id);
            }
        }
        catch (SQLException sqex) {
            throw new IOException(name + " " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static final void deleteItem(final long id, final boolean frozen) throws IOException {
        if (WurmId.getType(id) != 19) {
            Connection dbcon = null;
            PreparedStatement ps = null;
            try {
                dbcon = DbConnector.getItemDbCon();
                DbStrings dbstrings = Item.getDbStringsByWurmId(id);
                if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                    IntraServerConnection.logger.finest("Deleting item: " + id);
                }
                ps = dbcon.prepareStatement(dbstrings.deleteTransferedItem());
                ps.setLong(1, id);
                int rows = ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                if (dbstrings == ItemDbStrings.getInstance()) {
                    dbstrings = FrozenItemDbStrings.getInstance();
                    ps = dbcon.prepareStatement(dbstrings.deleteTransferedItem());
                    ps.setLong(1, id);
                    if (rows == 0) {
                        rows = ps.executeUpdate();
                    }
                    else {
                        ps.executeUpdate();
                    }
                    DbUtilities.closeDatabaseObjects(ps, null);
                }
                if (rows > 0) {
                    if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                        IntraServerConnection.logger.finest("Deleting effects for item: " + id);
                    }
                    ps = dbcon.prepareStatement("DELETE FROM EFFECTS WHERE OWNER=?");
                    ps.setLong(1, id);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                    if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                        IntraServerConnection.logger.finest("Deleting itemdata for item: " + id);
                    }
                    ps = dbcon.prepareStatement("DELETE FROM ITEMDATA WHERE WURMID=?");
                    ps.setLong(1, id);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                    if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                        IntraServerConnection.logger.finest("Deleting inscription data for item: " + id);
                    }
                    ps = dbcon.prepareStatement("DELETE FROM INSCRIPTIONS WHERE WURMID=?");
                    ps.setLong(1, id);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                    if (IntraServerConnection.logger.isLoggable(Level.FINEST)) {
                        IntraServerConnection.logger.finest("Deleting locks for item: " + id);
                    }
                    ps = dbcon.prepareStatement("DELETE FROM LOCKS WHERE WURMID=?");
                    ps.setLong(1, id);
                    ps.executeUpdate();
                    DbUtilities.closeDatabaseObjects(ps, null);
                    final ItemSpellEffects spefs = ItemSpellEffects.getSpellEffects(id);
                    if (spefs != null) {
                        spefs.clear();
                    }
                    SpellEffect.deleteEffectsForItem(id);
                }
            }
            catch (SQLException sqex) {
                if (!Servers.localServer.LOGINSERVER) {
                    throw new IOException(id + " " + sqex.getMessage(), sqex);
                }
                IntraServerConnection.logger.log(Level.WARNING, "ITEMDELETE Failed to delete item " + id + " " + sqex.getMessage(), sqex);
            }
            finally {
                DbUtilities.closeDatabaseObjects(ps, null);
                DbConnector.returnConnection(dbcon);
            }
        }
    }
    
    private boolean unpackPlayerData(final int posx, final int posy, final boolean surfaced) {
        try {
            this.dataStream.flush();
            this.dataStream.close();
            final byte[] bytes = this.dataStream.toByteArray();
            return savePlayerToDisk(bytes, posx, posy, surfaced, false) > 0L;
        }
        catch (IOException ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
            return false;
        }
    }
    
    public static final void readNullCultist(final DataInputStream dis, final String name, final long wurmId) {
        try {
            final Cultist cultist = Cultist.getCultist(wurmId);
            if (cultist != null) {
                cultist.deleteCultist();
            }
        }
        catch (IOException iox) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to read cultist for " + name + " " + wurmId);
        }
    }
    
    public static final void readCultist(final DataInputStream dis, final String name, final long wurmId) {
        try {
            final byte clevel = dis.readByte();
            final byte cpath = dis.readByte();
            final long lastMeditated = dis.readLong();
            final long lastReceivedLevel = dis.readLong();
            final long lastAppointedLevel = dis.readLong();
            final long cd1 = dis.readLong();
            final long cd2 = dis.readLong();
            final long cd3 = dis.readLong();
            final long cd4 = dis.readLong();
            final long cd5 = dis.readLong();
            final long cd6 = dis.readLong();
            final long cd7 = dis.readLong();
            final byte skillgainCount = dis.readByte();
            final Cultist cultist = Cultist.getCultist(wurmId);
            if (cultist == null) {
                final Cultist c = new Cultist(wurmId, lastMeditated, lastReceivedLevel, lastAppointedLevel, clevel, cpath, cd1, cd2, cd3, cd4, cd5, cd6, cd7);
                try {
                    c.saveCultist(true);
                }
                catch (IOException iox) {
                    IntraServerConnection.logger.log(Level.WARNING, "Failed to save cultist " + name + " level=" + clevel + " path=" + cpath + " " + iox.getMessage(), iox);
                }
                c.setSkillgainCount(skillgainCount);
            }
            else {
                cultist.deleteCultist();
                final Cultist c = new Cultist(wurmId, lastMeditated, lastReceivedLevel, lastAppointedLevel, clevel, cpath, cd1, cd2, cd3, cd4, cd5, cd6, cd7);
                try {
                    c.saveCultist(true);
                }
                catch (IOException iox) {
                    IntraServerConnection.logger.log(Level.WARNING, "Failed to save cultist " + name + " level=" + clevel + " path=" + cpath + " " + iox.getMessage(), iox);
                }
                c.setSkillgainCount(skillgainCount);
            }
        }
        catch (IOException iox2) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to read cultist for " + name + " " + wurmId);
        }
    }
    
    public static final byte calculateBloodFromKingdom(final byte kingdom) {
        if (kingdom == 3) {
            return 1;
        }
        if (kingdom == 2) {
            return 8;
        }
        if (kingdom == 1) {
            return 4;
        }
        if (kingdom == 4) {
            return 2;
        }
        return 0;
    }
    
    public static long savePlayerEpicTransfer(final DataInputStream dis) {
        try {
            IntraServerConnection.logger.log(Level.INFO, "Epic transfer");
            final long wurmId = dis.readLong();
            final String name = dis.readUTF();
            String password = dis.readUTF();
            final String session = dis.readUTF();
            String emailAddress = dis.readUTF();
            final long sessionExpiration = dis.readLong();
            final byte power = dis.readByte();
            long money = dis.readLong();
            long paymentExpire = dis.readLong();
            final int numignored = dis.readInt();
            long[] ignored = new long[numignored];
            for (int ni = 0; ni < numignored; ++ni) {
                ignored[ni] = dis.readLong();
            }
            if (numignored == 0) {
                ignored = IntraServerConnection.EMPTY_LONG_PRIMITIVE_ARRAY;
            }
            final int numfriends = dis.readInt();
            long[] friends = new long[numfriends];
            byte[] friendCats = new byte[numfriends];
            for (int nf = 0; nf < numfriends; ++nf) {
                friends[nf] = dis.readLong();
                friendCats[nf] = dis.readByte();
            }
            if (numfriends == 0) {
                friends = IntraServerConnection.EMPTY_LONG_PRIMITIVE_ARRAY;
                friendCats = IntraServerConnection.EMPTY_BYTE_PRIMITIVE_ARRAY;
            }
            final long playingTime = dis.readLong();
            final long creationDate = dis.readLong();
            final long lastwarned = dis.readLong();
            byte kingdom = dis.readByte();
            final boolean banned = dis.readBoolean();
            final long banexpiry = dis.readLong();
            final String banreason = dis.readUTF();
            final boolean mute = dis.readBoolean();
            final short muteTimes = dis.readShort();
            final long muteexpiry = dis.readLong();
            final String mutereason = dis.readUTF();
            final boolean maymute = dis.readBoolean();
            boolean overRideShop = dis.readBoolean();
            final boolean reimbursed = dis.readBoolean();
            final int warnings = dis.readInt();
            final boolean mayHearDevtalk = dis.readBoolean();
            final String ipaddress = dis.readUTF();
            final long version = dis.readLong();
            final long referrer = dis.readLong();
            final String pwQuestion = dis.readUTF();
            final String pwAnswer = dis.readUTF();
            final boolean logging = dis.readBoolean();
            final boolean seesCAWin = dis.readBoolean();
            final boolean isCA = dis.readBoolean();
            final boolean mayAppointCA = dis.readBoolean();
            final long face = dis.readLong();
            byte blood = dis.readByte();
            final long flags = dis.readLong();
            final long flags2 = dis.readLong();
            byte chaosKingdom = dis.readByte();
            final byte undeadType = dis.readByte();
            final int undeadKills = dis.readInt();
            final int undeadPKills = dis.readInt();
            final int undeadPSecs = dis.readInt();
            final long lastResetEarningsCounter = dis.readLong();
            final long moneyEarnedBySellingLastHour = dis.readLong();
            final long moneyEarnedBySellingEver = dis.readLong();
            int daysPrem = 0;
            long lastTicked = 0L;
            int monthsPaidEver = 0;
            int monthsPaidInARow = 0;
            int monthsPaidSinceReset = 0;
            int silverPaidEver = 0;
            int currentLoyalty = 0;
            int totalLoyalty = 0;
            boolean awards = false;
            if (dis.readBoolean()) {
                awards = true;
                daysPrem = dis.readInt();
                lastTicked = dis.readLong();
                monthsPaidEver = dis.readInt();
                monthsPaidInARow = dis.readInt();
                monthsPaidSinceReset = dis.readInt();
                silverPaidEver = dis.readInt();
                currentLoyalty = dis.readInt();
                totalLoyalty = dis.readInt();
            }
            final byte sex = dis.readByte();
            final int epicServerId = dis.readInt();
            final byte epicServerKingdom = dis.readByte();
            for (int numskills = dis.readInt(), s = 0; s < numskills; ++s) {
                final long skillId = dis.readLong();
                final int skillNumber = dis.readInt();
                final double skillValue = dis.readDouble();
                final double skillMinimum = dis.readDouble();
                final long skillLastUsed = dis.readLong();
                if (Servers.isThisAnEpicServer()) {
                    final SkillMetaData sk = SkillMetaData.copyToEpicSkill(skillId, wurmId, skillNumber, skillValue, skillMinimum, skillLastUsed);
                    SkillMetaData.deleteSkill(wurmId, skillNumber);
                    sk.save();
                }
            }
            unpackAchievements(wurmId, dis);
            RecipesByPlayer.unPackRecipes(dis, wurmId);
            PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
            if (pinf == null) {
                try {
                    LoginHandler.createPlayer(name, password, pwQuestion, pwAnswer, emailAddress, kingdom, power, face, sex, false, false, wurmId);
                }
                catch (Exception ex) {
                    IntraServerConnection.logger.log(Level.WARNING, "Creation exception " + ex.getMessage(), ex);
                    return -1L;
                }
                pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
                if (!Servers.localServer.EPIC) {
                    pinf.lastChangedKindom = 0L;
                }
            }
            else if (Servers.isThisAChaosServer()) {
                chaosKingdom = pinf.getChaosKingdom();
                final Village cv = Villages.getVillageForCreature(wurmId);
                if (cv != null) {
                    kingdom = cv.kingdom;
                }
                if (blood == 0) {
                    blood = calculateBloodFromKingdom(chaosKingdom);
                }
            }
            if (pinf != null) {
                if (awards) {
                    pinf.awards = Awards.getAwards(pinf.wurmId);
                    if (pinf.awards != null) {
                        (pinf.awards = new Awards(wurmId, daysPrem, monthsPaidEver, monthsPaidInARow, monthsPaidSinceReset, silverPaidEver, lastTicked, currentLoyalty, totalLoyalty, false)).update();
                    }
                    else {
                        pinf.awards = new Awards(wurmId, daysPrem, monthsPaidEver, monthsPaidInARow, monthsPaidSinceReset, silverPaidEver, lastTicked, currentLoyalty, totalLoyalty, true);
                    }
                }
                unpackPMList(pinf, dis);
            }
            if (Servers.isThisLoginServer() && pinf != null) {
                paymentExpire = pinf.getPaymentExpire();
                overRideShop = pinf.overRideShop;
                if (pinf.emailAddress.length() > 0) {
                    emailAddress = pinf.emailAddress;
                }
                password = pinf.getPassword();
                if (money != pinf.money) {
                    IntraServerConnection.logger.log(Level.INFO, "Setting money for " + pinf.getName() + " to " + pinf.money + " instead of " + money);
                }
                money = pinf.money;
            }
            if (blood == 0 || (Servers.localServer.EPIC && blood == 2)) {
                blood = calculateBloodFromKingdom(kingdom);
            }
            final EpicPlayerTransferMetaData pmd = new EpicPlayerTransferMetaData(wurmId, name, password, session, sessionExpiration, power, lastwarned, playingTime, kingdom, banned, banexpiry, banreason, reimbursed, warnings, mayHearDevtalk, paymentExpire, ignored, friends, friendCats, ipaddress, mute, sex, version, money, face, seesCAWin, logging, isCA, mayAppointCA, referrer, pwQuestion, pwAnswer, overRideShop, muteTimes, muteexpiry, mutereason, maymute, emailAddress, creationDate, epicServerId, epicServerKingdom, chaosKingdom, blood, flags, flags2, undeadType, undeadKills, undeadPKills, undeadPSecs, moneyEarnedBySellingEver, daysPrem, lastTicked, currentLoyalty, totalLoyalty, monthsPaidEver, monthsPaidInARow, monthsPaidSinceReset, silverPaidEver, awards);
            pmd.save();
            if (pinf != null) {
                final boolean setPremFlag = pinf.isFlagSet(8);
                pinf.setMoneyEarnedBySellingLastHour(moneyEarnedBySellingLastHour);
                pinf.setLastResetEarningsCounter(lastResetEarningsCounter);
                if (!password.equals(pinf.getPassword())) {
                    IntraServerConnection.logger.log(Level.WARNING, name + " after transfer but before loading: password now is " + pinf.getPassword() + ". Sent " + password);
                }
                pinf.loaded = false;
                try {
                    pinf.load();
                    boolean updateFlags = false;
                    if (pinf.flags != flags) {
                        pinf.setFlagBits(pinf.flags = flags);
                        if (setPremFlag) {
                            pinf.setFlag(8, true);
                        }
                        updateFlags = true;
                    }
                    if (pinf.flags2 != flags2) {
                        pinf.setFlag2Bits(pinf.flags2 = flags2);
                        updateFlags = true;
                    }
                    if (updateFlags) {
                        pinf.forceFlagsUpdate();
                    }
                    if (!password.equals(pinf.getPassword())) {
                        IntraServerConnection.logger.log(Level.WARNING, name + " after transfer: password now is " + pinf.getPassword() + "  Sent " + password);
                    }
                }
                catch (IOException iox) {
                    IntraServerConnection.logger.log(Level.WARNING, iox.getMessage());
                }
            }
            pinf.loaded = false;
            pinf.load();
            pinf.lastUsedEpicPortal = System.currentTimeMillis();
            return wurmId;
        }
        catch (IOException ex2) {
            IntraServerConnection.logger.log(Level.WARNING, "Unpack exception " + ex2.getMessage(), ex2);
            return -1L;
        }
        finally {
            if (dis != null) {
                try {
                    dis.close();
                }
                catch (IOException ex3) {}
            }
        }
    }
    
    private static final void unpackAchievements(final long wurmId, final DataInputStream dis) throws IOException {
        for (int templateNums = dis.readInt(), x = 0; x < templateNums; ++x) {
            final int number = dis.readInt();
            final String tname = dis.readUTF();
            final String desc = dis.readUTF();
            final String creator = dis.readUTF();
            final AchievementTemplate t = Achievement.getTemplate(number);
            if (t == null) {
                new AchievementTemplate(number, tname, false, 1, desc, creator, false, false);
            }
        }
        final int nums = dis.readInt();
        Achievements.deleteAllAchievements(wurmId);
        for (int x2 = 0; x2 < nums; ++x2) {
            final int achievement = dis.readInt();
            final int counter = dis.readInt();
            final long date = dis.readLong();
            final Timestamp ts = new Timestamp(date);
            new Achievement(achievement, ts, wurmId, counter, -1).create(true);
        }
    }
    
    private static final void unpackPMList(final PlayerInfo pinf, final DataInputStream dis) throws IOException {
        for (int theCount = dis.readInt(), x = 0; x < theCount; ++x) {
            final String targetName = dis.readUTF();
            final long targetId = dis.readLong();
            pinf.addPMTarget(targetName, targetId);
        }
        final long sessionFlags = dis.readLong();
        pinf.setSessionFlags(sessionFlags);
    }
    
    private static final void unpackPrivateMapAnnotations(final long playerID, final DataInputStream dis) throws IOException {
        MapAnnotation.deletePrivateAnnotationsForOwner(playerID);
        final boolean containsAnnotations = dis.readBoolean();
        if (containsAnnotations) {
            for (int count = dis.readInt(), i = 0; i < count; ++i) {
                final long id = dis.readLong();
                final byte type = dis.readByte();
                final String name = dis.readUTF();
                final String server = dis.readUTF();
                final long position = dis.readLong();
                final long ownerId = dis.readLong();
                final byte icon = dis.readByte();
                MapAnnotation.createNew(id, name, type, position, ownerId, server, icon);
            }
        }
    }
    
    public static long savePlayerToDisk(final byte[] bytes, int posx, int posy, final boolean surfaced, final boolean newPlayer) {
        if (IntraServerConnection.saving) {
            return -10L;
        }
        IntraServerConnection.saving = true;
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(bytes));
            if (dis.readBoolean()) {
                return savePlayerEpicTransfer(dis);
            }
            final long wurmId = dis.readLong();
            try {
                final Player p = Players.getInstance().getPlayer(wurmId);
                Players.getInstance().logoutPlayer(p);
            }
            catch (NoSuchPlayerException ex3) {}
            catch (Exception ex) {
                IntraServerConnection.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
            if (info != null) {
                info.lastLogout = System.currentTimeMillis();
            }
            byte oldChaosKingdom = 0;
            boolean setPremFlag = false;
            if (info != null) {
                oldChaosKingdom = info.getChaosKingdom();
                if (info.isFlagSet(8)) {
                    setPremFlag = true;
                }
            }
            deletePlayer(wurmId);
            Cooldowns.deleteCooldownsFor(wurmId);
            final Set<Long> itemIds = Items.loadAllNonTransferredItemsIdsForCreature(wurmId, info);
            final Iterator<Long> it = itemIds.iterator();
            while (it.hasNext()) {
                deleteItem(it.next(), info != null && info.hasMovedInventory());
            }
            for (int numwounds = dis.readInt(), w = 0; w < numwounds; ++w) {
                final WoundMetaData wm = new WoundMetaData(dis.readLong(), dis.readByte(), dis.readByte(), dis.readFloat(), wurmId, dis.readFloat(), dis.readFloat(), dis.readBoolean(), dis.readLong(), dis.readByte());
                wm.save();
            }
            final String name = dis.readUTF();
            if (info == null) {
                info = PlayerInfoFactory.createPlayerInfo(name);
                info.loaded = false;
                try {
                    info.load();
                    IntraServerConnection.logger.log(Level.INFO, "Found old player info for the name " + name + ". Deleting old information with wurmid " + info.wurmId + ". New wurmid=" + wurmId);
                    if (info.wurmId > 0L) {
                        deletePlayer(name, info.wurmId);
                        info.wurmId = wurmId;
                        info.loaded = false;
                        info = null;
                        IntraServerConnection.logger.log(Level.INFO, "Player " + name + " deleted. PlayerInfo is null");
                    }
                    else {
                        IntraServerConnection.logger.log(Level.INFO, "Since the player information for " + name + " had wurmid " + info.wurmId + " it was not deleted.");
                    }
                }
                catch (IOException iox2) {
                    info = null;
                }
            }
            final String password = dis.readUTF();
            final String session = dis.readUTF();
            final String email = dis.readUTF();
            final long sessionExpiration = dis.readLong();
            final byte power = dis.readByte();
            byte deity = dis.readByte();
            float align = dis.readFloat();
            float faith = dis.readFloat();
            float favor = dis.readFloat();
            final byte god = dis.readByte();
            final byte realdeath = dis.readByte();
            final long lastChangedDeity = dis.readLong();
            final int fatiguesecsleft = dis.readInt();
            final int fatigueSecsToday = dis.readInt();
            final int fatigueSecsYesterday = dis.readInt();
            final long lastfatigue = dis.readLong();
            final long lastwarned = dis.readLong();
            final long lastcheated = dis.readLong();
            final long plantedSign = dis.readLong();
            final long playingTime = dis.readLong();
            final long creationDate = dis.readLong();
            byte kingdom = dis.readByte();
            final boolean votedKing = dis.readBoolean();
            final int rank = dis.readInt();
            final int maxRank = dis.readInt();
            final long lastModifiedRank = dis.readLong();
            final boolean banned = dis.readBoolean();
            final long banexpiry = dis.readLong();
            final String banreason = dis.readUTF();
            final short muteTimes = dis.readShort();
            final boolean reimbursed = dis.readBoolean();
            final int warnings = dis.readInt();
            final boolean mayHearDevtalk = dis.readBoolean();
            final long paymentExpire = dis.readLong();
            final int numignored = dis.readInt();
            long[] ignored = new long[numignored];
            for (int ni = 0; ni < numignored; ++ni) {
                ignored[ni] = dis.readLong();
            }
            if (numignored == 0) {
                ignored = IntraServerConnection.EMPTY_LONG_PRIMITIVE_ARRAY;
            }
            final int numfriends = dis.readInt();
            long[] friends = new long[numfriends];
            byte[] friendCats = new byte[numfriends];
            for (int nf = 0; nf < numfriends; ++nf) {
                friends[nf] = dis.readLong();
                friendCats[nf] = dis.readByte();
            }
            if (numfriends == 0) {
                friends = IntraServerConnection.EMPTY_LONG_PRIMITIVE_ARRAY;
                friendCats = IntraServerConnection.EMPTY_BYTE_PRIMITIVE_ARRAY;
            }
            final String ipaddress = dis.readUTF();
            final long version = dis.readLong();
            final boolean dead = dis.readBoolean();
            final boolean mute = dis.readBoolean();
            final long lastFaith = dis.readLong();
            final byte numFaith = dis.readByte();
            final long money = dis.readLong();
            final boolean climbing = dis.readBoolean();
            final byte changedKingdom = dis.readByte();
            final long face = dis.readLong();
            byte blood = dis.readByte();
            final long flags = dis.readLong();
            long flags2 = dis.readLong();
            final long abilities = dis.readLong();
            final int scenarioKarma = dis.readInt();
            final int abilityTitle = dis.readInt();
            byte chaosKingdom = dis.readByte();
            final byte undeadType = dis.readByte();
            final int undeadKills = dis.readInt();
            final int undeadPKills = dis.readInt();
            final int undeadPSecs = dis.readInt();
            final long lastResetEarningsCounter = dis.readLong();
            final long moneyEarnedBySellingLastHour = dis.readLong();
            final long moneyEarnedBySellingEver = dis.readLong();
            int daysPrem = 0;
            long lastTicked = 0L;
            int monthsPaidEver = 0;
            int monthsPaidInARow = 0;
            int monthsPaidSinceReset = 0;
            int silverPaidEver = 0;
            int currentLoyalty = 0;
            int totalLoyalty = 0;
            boolean awards = false;
            if (dis.readBoolean()) {
                awards = true;
                daysPrem = dis.readInt();
                lastTicked = dis.readLong();
                monthsPaidEver = dis.readInt();
                monthsPaidInARow = dis.readInt();
                monthsPaidSinceReset = dis.readInt();
                silverPaidEver = dis.readInt();
                currentLoyalty = dis.readInt();
                totalLoyalty = dis.readInt();
                if (info != null) {
                    info.awards = Awards.getAwards(info.wurmId);
                    if (info.awards != null) {
                        (info.awards = new Awards(wurmId, daysPrem, monthsPaidEver, monthsPaidInARow, monthsPaidSinceReset, silverPaidEver, lastTicked, currentLoyalty, totalLoyalty, false)).update();
                    }
                    else {
                        info.awards = new Awards(wurmId, daysPrem, monthsPaidEver, monthsPaidInARow, monthsPaidSinceReset, silverPaidEver, lastTicked, currentLoyalty, totalLoyalty, true);
                    }
                }
            }
            final short hotaWins = dis.readShort();
            final boolean hasFreeTransfer = dis.readBoolean();
            final int reputation = dis.readInt();
            final long lastPolledRep = dis.readLong();
            long pet = dis.readLong();
            if (pet != -10L) {
                if (!Creatures.getInstance().isCreatureOffline(pet)) {
                    try {
                        final Creature petcret = Creatures.getInstance().getCreature(pet);
                        if (petcret.dominator != wurmId) {
                            pet = -10L;
                        }
                    }
                    catch (NoSuchCreatureException nsc) {
                        pet = Creatures.getInstance().getPetId(wurmId);
                    }
                }
            }
            else {
                pet = Creatures.getInstance().getPetId(wurmId);
            }
            final long nicotime = dis.readLong();
            final long alcotime = dis.readLong();
            final float nicotine = dis.readFloat();
            final float alcohol = dis.readFloat();
            final boolean logging = dis.readBoolean();
            final int title = dis.readInt();
            final int secondTitle = dis.readInt();
            final int numTitles = dis.readInt();
            int[] titleArr = IntraServerConnection.EMPTY_INT_ARRAY;
            if (numTitles > 0) {
                titleArr = new int[numTitles];
                for (int x = 0; x < numTitles; ++x) {
                    titleArr[x] = dis.readInt();
                }
            }
            final long muteexpiry = dis.readLong();
            final String mutereason = dis.readUTF();
            final boolean maymute = dis.readBoolean();
            final boolean overRideShop = dis.readBoolean();
            final int currentServer = dis.readInt();
            final int lastServer = dis.readInt();
            final long referrer = dis.readLong();
            final String pwQuestion = dis.readUTF();
            final String pwAnswer = dis.readUTF();
            boolean isPriest = dis.readBoolean();
            byte priestType = 0;
            long lastChangedPriest = 0L;
            if (isPriest) {
                priestType = dis.readByte();
                lastChangedPriest = dis.readLong();
            }
            if (Servers.localServer.PVPSERVER) {
                if (oldChaosKingdom != 0) {
                    chaosKingdom = oldChaosKingdom;
                }
                if (chaosKingdom != 0) {
                    kingdom = chaosKingdom;
                }
                final Village cv = Villages.getVillageForCreature(wurmId);
                if (cv != null) {
                    kingdom = cv.kingdom;
                }
                if (blood == 0) {
                    blood = calculateBloodFromKingdom(chaosKingdom);
                }
                if (info != null && info.getDeity() != null && info.getDeity().getNumber() == 4) {
                    final BitSet flag2Bits = MiscConstants.createBitSetLong(flags2);
                    if (!flag2Bits.get(11)) {
                        if (deity == 0) {
                            deity = 4;
                        }
                        if (info.getFaith() > faith) {
                            faith = info.getFaith();
                        }
                        if (info.isPriest && !isPriest) {
                            isPriest = info.isPriest;
                        }
                        flag2Bits.set(11);
                        flags2 = MiscConstants.bitSetToLong(flag2Bits);
                    }
                }
                if (Deities.getDeity(deity) != null && !QuestionParser.doesKingdomTemplateAcceptDeity(Kingdoms.getKingdomTemplateFor(kingdom), Deities.getDeity(deity))) {
                    if (kingdom == 4) {
                        kingdom = 3;
                    }
                    else {
                        faith = 0.0f;
                        favor = 0.0f;
                        align = 0.0f;
                        deity = 0;
                        isPriest = false;
                    }
                }
            }
            else if (power <= 0 && !Servers.localServer.PVPSERVER && Servers.localServer.HOMESERVER) {
                kingdom = (byte)((Servers.localServer.getKingdom() != 0) ? Servers.localServer.getKingdom() : 4);
                if (deity == 4 && info != null && info.getDeity() != null && info.getDeity().getNumber() != 4) {
                    final BitSet flag2Bits2 = MiscConstants.createBitSetLong(flags2);
                    if (!flag2Bits2.get(11)) {
                        if (info.getFaith() > faith) {
                            faith = info.getFaith();
                        }
                        if (info.isPriest && !isPriest) {
                            isPriest = info.isPriest;
                        }
                        flag2Bits2.set(11);
                        flags2 = MiscConstants.bitSetToLong(flag2Bits2);
                    }
                }
            }
            if (blood == 0) {
                blood = calculateBloodFromKingdom(chaosKingdom);
            }
            final long bed = dis.readLong();
            final int sleep = dis.readInt();
            final boolean theftWarned = dis.readBoolean();
            final boolean noReimbursmentLeft = dis.readBoolean();
            final boolean deathProt = dis.readBoolean();
            final byte fightmode = dis.readByte();
            final long naffinity = dis.readLong();
            final int tutLevel = dis.readInt();
            final boolean autof = dis.readBoolean();
            final long appoints = dis.readLong();
            final boolean seesPAWin = dis.readBoolean();
            final boolean isPA = dis.readBoolean();
            final boolean mayAppointPA = dis.readBoolean();
            final long lastChangedKingdom = dis.readLong();
            float px = (posx << 2) + 2;
            float py = (posy << 2) + 2;
            float posz = 0.0f;
            int zoneId = 0;
            if (!Servers.localServer.LOGINSERVER) {
                try {
                    if (posx > Zones.worldTileSizeX || posx < 0) {
                        posx = Zones.worldTileSizeX / 2;
                    }
                    if (posy > Zones.worldTileSizeY || posy < 0) {
                        posy = Zones.worldTileSizeY / 2;
                    }
                    px = (posx << 2) + 2;
                    py = (posy << 2) + 2;
                    final Zone zone = Zones.getZone(posx, posy, surfaced);
                    zoneId = zone.getId();
                    posz = 0.0f;
                    int tile = Server.surfaceMesh.getTile(posx, posy);
                    if (!surfaced) {
                        tile = Server.caveMesh.getTile(posx, posy);
                        posz = Math.max(-1.45f, Tiles.decodeHeightAsFloat(tile));
                    }
                    else {
                        posz = Math.max(-1.45f, Tiles.decodeHeightAsFloat(tile));
                    }
                }
                catch (NoSuchZoneException nsz2) {
                    IntraServerConnection.logger.log(Level.WARNING, "No end zone for " + wurmId + " at " + posx + ", " + posy);
                    return -1L;
                }
            }
            final long lastLostChampion = dis.readLong();
            final short championPoints = dis.readShort();
            final float champChanneling = dis.readFloat();
            final byte epicKingdom = dis.readByte();
            final int epicServerId = dis.readInt();
            final int karma = dis.readInt();
            final int maxKarma = dis.readInt();
            final int totalKarma = dis.readInt();
            final String templateName = dis.readUTF();
            final short chigh = dis.readShort();
            final short clong = dis.readShort();
            final short cwide = dis.readShort();
            final float rotation = dis.readFloat();
            final long bodyId = dis.readLong();
            final long buildingId = dis.readLong();
            final int damage = dis.readInt();
            final int hunger = dis.readInt();
            final int stunned = dis.readInt();
            final int thirst = dis.readInt();
            final int stamina = dis.readInt();
            final float nutritionLevel = dis.readFloat();
            final byte sex = dis.readByte();
            final long inventoryId = dis.readLong();
            final boolean onSurface = dis.readBoolean();
            final boolean unconscious = dis.readBoolean();
            final int age = dis.readInt();
            final long lastPolledAge = dis.readLong();
            final byte fat = dis.readByte();
            final short detectionSecs = dis.readShort();
            final byte disease = dis.readByte();
            final float calories = dis.readFloat();
            final float carbs = dis.readFloat();
            final float fats = dis.readFloat();
            final float proteins = dis.readFloat();
            if (dis.readBoolean()) {
                readCultist(dis, name, wurmId);
            }
            else {
                readNullCultist(dis, name, wurmId);
            }
            final long lastChangedPath = dis.readLong();
            final long lastPuppeteered = dis.readLong();
            if (lastPuppeteered > 0L) {
                Puppet.addPuppetTime(wurmId, lastPuppeteered);
            }
            final int numcooldowns = dis.readInt();
            final Map<Integer, Long> cooldowns = new HashMap<Integer, Long>();
            if (numcooldowns > 0) {
                for (int x2 = 0; x2 < numcooldowns; ++x2) {
                    cooldowns.put(dis.readInt(), dis.readLong());
                }
            }
            final int numItems = dis.readInt();
            final Set<ItemMetaData> idset = new HashSet<ItemMetaData>();
            for (int x3 = 0; x3 < numItems; ++x3) {
                createItem(dis, px, py, posz, idset, info != null && info.hasMovedInventory());
            }
            Affinities.deleteAllPlayerAffinity(wurmId);
            for (int numskills = dis.readInt(), s = 0; s < numskills; ++s) {
                final SkillMetaData sk = new SkillMetaData(dis.readLong(), wurmId, dis.readInt(), dis.readDouble(), dis.readDouble(), dis.readLong());
                if (Servers.localServer.isChallengeServer()) {
                    sk.setChallenge();
                }
                sk.save();
            }
            for (int numAffinities = dis.readInt(), xa = 0; xa < numAffinities; ++xa) {
                final int skillNumber = dis.readInt();
                final int affinity = dis.readByte() & 0xFF;
                if (affinity > 0) {
                    Affinities.setAffinity(wurmId, skillNumber, affinity, false);
                }
            }
            for (int numspeffects = dis.readInt(), seff = 0; seff < numspeffects; ++seff) {
                new SpellEffectMetaData(dis.readLong(), wurmId, dis.readByte(), dis.readFloat(), dis.readInt(), false).save();
            }
            unpackAchievements(wurmId, dis);
            try {
                RecipesByPlayer.unPackRecipes(dis, wurmId);
            }
            catch (Exception e) {
                IntraServerConnection.logger.warning("Exception unpacking recipes: " + e.getMessage());
                e.printStackTrace();
                IntraServerConnection.logger.warning("Deleting recipes for player to prevent corruption.");
                RecipesByPlayer.deleteRecipesForPlayer(wurmId);
            }
            final PlayerMetaData pmd = new PlayerMetaData(wurmId, name, password, session, chigh, clong, cwide, sessionExpiration, power, deity, align, faith, favor, god, realdeath, lastChangedDeity, fatiguesecsleft, lastfatigue, lastwarned, lastcheated, plantedSign, playingTime, kingdom, rank, banned, banexpiry, banreason, reimbursed, warnings, mayHearDevtalk, paymentExpire, ignored, friends, friendCats, templateName, ipaddress, dead, mute, bodyId, buildingId, damage, hunger, stunned, thirst, stamina, sex, inventoryId, surfaced, unconscious, px, py, posz, rotation, zoneId, version, lastFaith, numFaith, money, climbing, changedKingdom, age, lastPolledAge, fat, face, reputation, lastPolledRep, title, secondTitle, titleArr);
            pmd.pet = pet;
            pmd.alcohol = alcohol;
            pmd.alcoholTime = alcotime;
            pmd.nicotine = nicotine;
            pmd.nicotineTime = nicotime;
            pmd.priestType = priestType;
            pmd.lastChangedPriestType = lastChangedPriest;
            pmd.logging = logging;
            pmd.mayMute = maymute;
            pmd.overrideshop = overRideShop;
            pmd.maxRank = maxRank;
            pmd.lastModifiedRank = lastModifiedRank;
            pmd.muteexpiry = muteexpiry;
            pmd.mutereason = mutereason;
            pmd.lastServer = lastServer;
            pmd.currentServer = currentServer;
            pmd.referrer = referrer;
            pmd.pwQuestion = pwQuestion;
            pmd.pwAnswer = pwAnswer;
            pmd.isPriest = isPriest;
            pmd.bed = bed;
            pmd.sleep = sleep;
            pmd.creationDate = creationDate;
            pmd.istheftwarned = theftWarned;
            pmd.noReimbLeft = noReimbursmentLeft;
            pmd.deathProt = deathProt;
            pmd.fatigueSecsToday = fatigueSecsToday;
            pmd.fatigueSecsYday = fatigueSecsYesterday;
            pmd.fightmode = fightmode;
            pmd.nextAffinity = naffinity;
            pmd.detectionSecs = detectionSecs;
            pmd.tutLevel = tutLevel;
            pmd.autofight = autof;
            pmd.appointments = appoints;
            pmd.seesPAWin = seesPAWin;
            pmd.isPA = isPA;
            pmd.mayAppointPA = mayAppointPA;
            pmd.nutrition = nutritionLevel;
            pmd.disease = disease;
            pmd.calories = calories;
            pmd.carbs = carbs;
            pmd.fats = fats;
            pmd.proteins = proteins;
            pmd.cooldowns = cooldowns;
            pmd.lastChangedKingdom = lastChangedKingdom;
            pmd.lastLostChampion = lastLostChampion;
            pmd.championPoints = championPoints;
            pmd.champChanneling = champChanneling;
            pmd.muteTimes = muteTimes;
            pmd.voteKing = votedKing;
            pmd.epicKingdom = epicKingdom;
            pmd.epicServerId = epicServerId;
            pmd.chaosKingdom = chaosKingdom;
            pmd.hotaWins = hotaWins;
            pmd.hasFreeTransfer = hasFreeTransfer;
            pmd.karma = karma;
            pmd.maxKarma = maxKarma;
            pmd.totalKarma = totalKarma;
            if (blood == 0) {
                blood = calculateBloodFromKingdom(kingdom);
            }
            pmd.blood = blood;
            pmd.flags = flags;
            pmd.flags2 = flags2;
            pmd.scenarioKarma = scenarioKarma;
            pmd.abilities = abilities;
            pmd.abilityTitle = abilityTitle;
            pmd.undeadType = undeadType;
            pmd.undeadKills = undeadKills;
            pmd.undeadPKills = undeadPKills;
            pmd.undeadPSecs = undeadPSecs;
            pmd.moneySalesEver = moneyEarnedBySellingEver;
            pmd.daysPrem = daysPrem;
            pmd.lastTicked = lastTicked;
            pmd.currentLoyaltyPoints = currentLoyalty;
            pmd.totalLoyaltyPoints = totalLoyalty;
            pmd.monthsPaidEver = monthsPaidEver;
            pmd.monthsPaidInARow = monthsPaidInARow;
            pmd.monthsPaidSinceReset = monthsPaidSinceReset;
            pmd.silverPaidEver = silverPaidEver;
            pmd.hasAwards = awards;
            if (Servers.isThisLoginServer()) {
                if (info != null) {
                    pmd.paymentExpire = info.getPaymentExpire();
                    if (info.emailAddress.length() == 0) {
                        pmd.emailAdress = email;
                    }
                    else {
                        pmd.emailAdress = info.emailAddress;
                    }
                    pmd.password = info.getPassword();
                    if (pmd.money != info.money) {
                        IntraServerConnection.logger.log(Level.INFO, "Setting money for " + info.getName() + " to " + info.money + " instead of " + pmd.money);
                    }
                    pmd.money = info.money;
                }
                else {
                    pmd.emailAdress = email;
                }
                pmd.save();
            }
            else {
                pmd.emailAdress = email;
                pmd.save();
            }
            IntraServerConnection.logger.log(Level.INFO, "has info:" + (info != null));
            if (info != null) {
                unpackPMList(info, dis);
                unpackPrivateMapAnnotations(info.getPlayerId(), dis);
                if (!password.equals(info.getPassword())) {
                    IntraServerConnection.logger.log(Level.WARNING, name + " after transfer but before loading: password now is " + info.getPassword() + ". Sent " + password);
                }
                info.loaded = false;
                try {
                    info.load();
                    boolean updateFlags = false;
                    if (info.flags != flags) {
                        info.setFlagBits(info.flags = flags);
                        if (setPremFlag) {
                            info.setFlag(8, true);
                        }
                        updateFlags = true;
                    }
                    if (info.flags2 != flags2) {
                        info.setFlag2Bits(info.flags2 = flags2);
                        updateFlags = true;
                    }
                    if (updateFlags) {
                        info.forceFlagsUpdate();
                    }
                    if (!password.equals(info.getPassword())) {
                        IntraServerConnection.logger.log(Level.WARNING, name + " after transfer: password now is " + info.getPassword() + "  Sent " + password);
                    }
                }
                catch (IOException iox) {
                    IntraServerConnection.logger.log(Level.WARNING, iox.getMessage());
                }
                info.setMoneyEarnedBySellingLastHour(moneyEarnedBySellingLastHour);
                info.setLastResetEarningsCounter(lastResetEarningsCounter);
                if (lastChangedPath > info.getLastChangedPath()) {
                    info.setLastChangedPath(lastChangedPath);
                }
            }
            if (IntraServerConnection.draggedItem >= 0L) {
                try {
                    final Item d = Items.getItem(IntraServerConnection.draggedItem);
                    try {
                        final Zone z = Zones.getZone((int)d.getPosX() >> 2, (int)d.getPosY() >> 2, true);
                        z.addItem(d);
                    }
                    catch (NoSuchZoneException nsz) {
                        IntraServerConnection.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                    }
                }
                catch (NoSuchItemException nsi) {
                    IntraServerConnection.logger.log(Level.WARNING, "Weird. No dragged item " + IntraServerConnection.draggedItem + " after it was saved.");
                }
                IntraServerConnection.draggedItem = -10L;
            }
            if (newPlayer) {
                IntraServerConnection.logger.log(Level.FINE, name + " created successfully.");
            }
            else {
                IntraServerConnection.logger.log(Level.FINE, name + " unpacked successfully.");
            }
            final Village v = Villages.getVillageForCreature(wurmId);
            if (v != null && v.kingdom != kingdom) {
                if (v.getMayor().getId() != wurmId) {
                    final Citizen c = v.getCitizen(wurmId);
                    v.removeCitizen(c);
                }
                else if (Servers.localServer.HOMESERVER) {
                    v.startDisbanding(null, name, wurmId);
                }
            }
            IntraServerConnection.saving = false;
            return wurmId;
        }
        catch (IOException ex2) {
            IntraServerConnection.saving = false;
            IntraServerConnection.logger.log(Level.WARNING, "Unpack exception " + ex2.getMessage(), ex2);
            return -1L;
        }
        finally {
            IntraServerConnection.saving = false;
            if (dis != null) {
                try {
                    dis.close();
                }
                catch (IOException ex4) {}
            }
        }
    }
    
    public static final void resetTransferVariables(final String playerName) {
        IntraServerConnection.logger.log(Level.INFO, playerName + " resetting transfer data");
        IntraServerConnection.lastItemName = "unknown";
        IntraServerConnection.lastItemId = -10L;
    }
    
    public static void createItem(final DataInputStream dis, final float posx, final float posy, final float posz, final Set<ItemMetaData> metadataset, final boolean frozen) throws IOException {
        try {
            final boolean isStoredAnimalItem = dis.readBoolean();
            if (isStoredAnimalItem) {
                CreatureDataStream.fromStream(dis);
            }
        }
        catch (IOException e) {
            IntraServerConnection.logger.log(Level.WARNING, "Exception", e);
        }
        final boolean locked = dis.readBoolean();
        final long lockid = dis.readLong();
        if (lockid != -10L) {
            final boolean ok = dis.readBoolean();
            if (ok) {
                createItem(dis, posx, posy, posz, metadataset, frozen);
            }
        }
        final long itemId = dis.readLong();
        deleteItem(itemId, frozen);
        final boolean dragged = dis.readBoolean();
        if (dragged) {
            IntraServerConnection.draggedItem = itemId;
        }
        for (int numEffects = dis.readInt(), e2 = 0; e2 < numEffects; ++e2) {
            new EffectMetaData(itemId, dis.readShort(), 0.0f, 0.0f, 0.0f, dis.readLong()).save();
        }
        for (int numspeffects = dis.readInt(), seff = 0; seff < numspeffects; ++seff) {
            new SpellEffectMetaData(dis.readLong(), itemId, dis.readByte(), dis.readFloat(), dis.readInt(), true).save();
        }
        final int numKeys = dis.readInt();
        long[] keyids = IntraServerConnection.EMPTY_LONG_PRIMITIVE_ARRAY;
        if (numKeys > 0) {
            keyids = new long[numKeys];
            for (int k = 0; k < numKeys; ++k) {
                keyids[k] = dis.readLong();
            }
        }
        final long lastowner = dis.readLong();
        final int data1 = dis.readInt();
        final int data2 = dis.readInt();
        final int extra1 = dis.readInt();
        final int extra2 = dis.readInt();
        if (data1 != -1 || data2 != -1 || extra1 != -1 || extra2 != -1) {
            final ItemData d = new ItemData(itemId, data1, data2, extra1, extra2);
            try {
                d.createDataEntry(DbConnector.getItemDbCon());
            }
            catch (SQLException sqx) {
                IntraServerConnection.logger.log(Level.WARNING, sqx.getMessage(), sqx);
            }
        }
        final String itname = dis.readUTF();
        if (Servers.isThisATestServer()) {
            IntraServerConnection.logger.log(Level.INFO, "Creating " + itname + ", " + itemId);
        }
        final String desc = dis.readUTF();
        final long ownerId = dis.readLong();
        final long parentId = dis.readLong();
        final long lastmaintained = dis.readLong();
        final float ql = dis.readFloat();
        final float itemdam = dis.readFloat();
        final float origQl = dis.readFloat();
        final int itemtemplateId = dis.readInt();
        final int weight = dis.readInt();
        final short place = dis.readShort();
        final int sizex = dis.readInt();
        final int sizey = dis.readInt();
        final int sizez = dis.readInt();
        final int bless = dis.readInt();
        final byte enchantment = dis.readByte();
        final byte material = dis.readByte();
        final int price = dis.readInt();
        final short temp = dis.readShort();
        final boolean banked = dis.readBoolean();
        final byte auxdata = dis.readByte();
        final long creationDate = dis.readLong();
        final byte creationState = dis.readByte();
        final int realTemplate = dis.readInt();
        final boolean hasMoreItems = dis.readBoolean();
        if (hasMoreItems) {
            ItemRequirement.deleteRequirements(itemId);
            for (int nums = dis.readInt(), xa = 0; xa < nums; ++xa) {
                final int templateId = dis.readInt();
                final int numsDone = dis.readInt();
                ItemRequirement.setRequirements(itemId, templateId, numsDone, true, true);
            }
        }
        final boolean wornAsArmour = dis.readBoolean();
        final boolean female = dis.readBoolean();
        final boolean mailed = dis.readBoolean();
        final byte mailTimes = dis.readByte();
        final byte rarity = dis.readByte();
        final long onBridge = dis.readLong();
        final int settings = dis.readInt();
        final int numPermissions = dis.readInt();
        ItemSettings.remove(itemId);
        final LinkedList<String> added = new LinkedList<String>();
        for (int p = 0; p < numPermissions; ++p) {
            final long pId = dis.readLong();
            final int pSettings = dis.readInt();
            ItemSettings.addPlayer(itemId, pId, pSettings);
            final String pName = PermissionsByPlayer.getPlayerOrGroupName(pId);
            final BitSet permissionBits = new BitSet(32);
            for (int x = 0; x < 32; ++x) {
                if ((pSettings >>> x & 0x1) == 0x1) {
                    permissionBits.set(x);
                }
            }
            final LinkedList<String> perms = new LinkedList<String>();
            if (permissionBits.get(ItemSettings.ItemPermissions.MANAGE.getBit())) {
                perms.add("+Manage");
            }
            if (permissionBits.get(ItemSettings.VehiclePermissions.COMMANDER.getBit())) {
                perms.add("+Commander");
            }
            if (permissionBits.get(ItemSettings.VehiclePermissions.PASSENGER.getBit())) {
                perms.add("+Passenger");
            }
            if (permissionBits.get(ItemSettings.VehiclePermissions.ACCESS_HOLD.getBit())) {
                perms.add("+Access Hold");
            }
            if (permissionBits.get(ItemSettings.BedPermissions.MAY_USE_BED.getBit())) {
                perms.add("+Sleep");
            }
            if (permissionBits.get(ItemSettings.BedPermissions.FREE_SLEEP.getBit())) {
                perms.add("+Free Sleep");
            }
            if (permissionBits.get(ItemSettings.MessageBoardPermissions.MAY_POST_NOTICES.getBit())) {
                perms.add("+Add Notices");
            }
            if (permissionBits.get(ItemSettings.MessageBoardPermissions.MAY_ADD_PMS.getBit())) {
                perms.add("+Add PMs");
            }
            if (permissionBits.get(ItemSettings.VehiclePermissions.DRAG.getBit())) {
                perms.add("+Drag");
            }
            if (permissionBits.get(ItemSettings.VehiclePermissions.EXCLUDE.getBit())) {
                perms.add("+Deny All");
            }
            added.add(pName + "(" + String.join(", ", perms) + ")");
        }
        if (!added.isEmpty()) {
            final String stuffAdded = "Imported " + String.join(", ", added);
            PermissionsHistories.addHistoryEntry(itemId, System.currentTimeMillis(), -10L, "Transfered", stuffAdded);
        }
        final boolean hasInscription = dis.readBoolean();
        if (hasInscription) {
            final InscriptionData insdata = new InscriptionData(itemId, dis.readUTF(), dis.readUTF(), 0);
            try {
                insdata.createInscriptionEntry(DbConnector.getItemDbCon());
            }
            catch (SQLException sqx2) {
                IntraServerConnection.logger.log(Level.WARNING, sqx2.getMessage(), sqx2);
            }
        }
        final int color = dis.readInt();
        final int color2 = dis.readInt();
        final String creator = dis.readUTF();
        Itempool.deleteItem(itemtemplateId, itemId);
        if (dis.readBoolean()) {
            final short calories = dis.readShort();
            final short carbs = dis.readShort();
            final short fats = dis.readShort();
            final short proteins = dis.readShort();
            final byte bonus = dis.readByte();
            final byte stages = dis.readByte();
            final byte ingredients = dis.readByte();
            final short recipeId = dis.readShort();
            ItemMealData.save(itemId, recipeId, calories, carbs, fats, proteins, bonus, stages, ingredients);
        }
        final ItemMetaData imd = new ItemMetaData(locked, lockid, itemId, keyids, lastowner, data1, data2, extra1, extra2, itname, desc, ownerId, parentId, lastmaintained, ql, itemdam, origQl, itemtemplateId, weight, sizex, sizey, sizez, bless, enchantment, material, price, temp, banked, auxdata, creationDate, creationState, realTemplate, wornAsArmour, color, color2, place, posx, posy, posz, creator, female, mailed, mailTimes, rarity, onBridge, hasInscription, settings, frozen);
        imd.save();
        metadataset.add(imd);
        IntraServerConnection.lastItemName = itname;
        IntraServerConnection.lastItemId = itemId;
    }
    
    private void validateTransferRequest(final ByteBuffer byteBuffer) {
        if (this.wurmserver.isLagging()) {
            try {
                this.sendTransferUserRequestAnswer(false, "The server is lagging. Try later.", 10);
            }
            catch (IOException ex) {}
            return;
        }
        if (Constants.maintaining) {
            try {
                this.sendTransferUserRequestAnswer(false, "The server is in maintenance mode.", 0);
            }
            catch (IOException ex2) {}
            return;
        }
        final boolean isDev = byteBuffer.get() != 0;
        if (!isDev && Players.getInstance().numberOfPlayers() > Servers.localServer.pLimit) {
            try {
                this.sendTransferUserRequestAnswer(false, "The server is full. Try later", 30);
            }
            catch (IOException ex3) {}
        }
    }
    
    private void validate(final ByteBuffer byteBuffer) {
        final int protocolVersion = byteBuffer.getInt();
        if (protocolVersion != 1) {
            try {
                this.sendLoginAnswer(false, "You are using an old protocol.\nPlease update the server.", 0);
            }
            catch (IOException ex) {}
            return;
        }
        final byte[] bytes = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(bytes);
        final boolean dev = byteBuffer.get() == 1;
        String password = "Unknown";
        try {
            password = new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException nse) {
            password = new String(bytes);
            IntraServerConnection.logger.log(Level.WARNING, "Unsupported encoding for password.", nse);
        }
        if (!password.equals(Servers.localServer.INTRASERVERPASSWORD)) {
            try {
                this.sendLoginAnswer(false, "Wrong password: " + password, 0);
            }
            catch (IOException ex2) {}
            return;
        }
        try {
            this.sendLoginAnswer(true, "ok" + Server.rand.nextInt(1000000), 0);
        }
        catch (IOException ex3) {}
    }
    
    private void sendLoginAnswer(final boolean ok, final String message, final int retrySeconds) throws IOException {
        try {
            final byte[] messageb = message.getBytes("UTF-8");
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)2);
            if (ok) {
                bb.put((byte)1);
            }
            else {
                bb.put((byte)0);
            }
            bb.putShort((short)messageb.length);
            bb.put(messageb);
            bb.putShort((short)retrySeconds);
            bb.putLong(System.currentTimeMillis());
            this.conn.flush();
            if (!ok) {
                this.conn.ticksToDisconnect = 200;
            }
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send login answer.", ex);
        }
    }
    
    private void sendTransferUserRequestAnswer(final boolean ok, final String sessionKey, final int retrySeconds) throws IOException {
        try {
            final byte[] messageb = sessionKey.getBytes("UTF-8");
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)6);
            if (ok) {
                bb.put((byte)1);
            }
            else {
                bb.put((byte)0);
            }
            bb.putShort((short)messageb.length);
            bb.put(messageb);
            bb.putShort((short)retrySeconds);
            bb.putLong(System.currentTimeMillis());
            this.conn.flush();
            if (!ok) {
                this.conn.ticksToDisconnect = 200;
            }
            IntraServerConnection.logger.log(Level.INFO, "Intraserver sent transferrequestanswer. " + ok);
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send TransferUserRequest answer.", ex);
        }
    }
    
    private void sendCommandDone() throws IOException {
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)4);
            this.conn.flush();
            this.conn.ticksToDisconnect = 200;
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send command done.", ex);
        }
    }
    
    private void sendCommandFailed() throws IOException {
        IntraServerConnection.logger.log(Level.WARNING, "Command failed : ", new Exception());
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)5);
            this.conn.flush();
            this.conn.ticksToDisconnect = 200;
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send command failed.", ex);
        }
    }
    
    private void sendDataReceived() throws IOException {
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)8);
            this.conn.flush();
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send DataReceived.", ex);
        }
    }
    
    private void sendTimeSync() throws IOException {
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)10);
            bb.putLong(WurmCalendar.currentTime);
            this.conn.flush();
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send timesync.", ex);
        }
        this.conn.ticksToDisconnect = 200;
    }
    
    private void sendPlayerVersion(final long playerversion) throws IOException {
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)9);
            bb.putLong(playerversion);
            this.conn.flush();
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send player version.", ex);
        }
        this.conn.ticksToDisconnect = 200;
    }
    
    private void sendPlayerPaymentExpire(final long paymentExpire) throws IOException {
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)11);
            bb.putLong(paymentExpire);
            this.conn.flush();
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send expiretime.", ex);
            this.sendCommandFailed();
        }
        this.conn.ticksToDisconnect = 200;
    }
    
    private void sendPingAnswer() throws IOException {
        try {
            final ByteBuffer bb = this.conn.getBuffer();
            if (Server.getMillisToShutDown() > -1000L && Server.getMillisToShutDown() < 120000L) {
                bb.put((byte)14);
            }
            else {
                bb.put((byte)13);
                if (Constants.maintaining) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.putInt(Players.getInstance().getNumberOfPlayers());
                bb.putInt(Servers.localServer.pLimit);
                if (Server.getMillisToShutDown() > 0L) {
                    bb.putInt(Math.max(1, (int)(Server.getMillisToShutDown() / 1000L)));
                }
                else {
                    bb.putInt(0);
                }
                bb.putInt(Constants.meshSize);
            }
            this.conn.flush();
        }
        catch (Exception ex) {
            IntraServerConnection.logger.log(Level.WARNING, "Failed to send ping answer.", ex);
            this.sendCommandFailed();
        }
    }
    
    static {
        logger = Logger.getLogger(IntraServerConnection.class.getName());
        IntraServerConnection.draggedItem = -10L;
        moneyDetails = new HashSet<String>();
        timeDetails = new HashSet<String>();
        IntraServerConnection.lastItemName = "unknown";
        IntraServerConnection.lastItemId = -10L;
        IntraServerConnection.saving = false;
    }
}
