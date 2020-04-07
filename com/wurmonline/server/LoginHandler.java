// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.HashMap;
import sun.misc.BASE64Encoder;
import java.security.MessageDigest;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import com.wurmonline.server.creatures.CreatureTemplateFactory;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.bodys.BodyTemplate;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.Set;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.intra.PlayerTransfer;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.players.Abilities;
import com.wurmonline.server.creatures.MountAction;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.VehicleBehaviour;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.villages.Village;
import java.util.Iterator;
import com.wurmonline.server.intra.MountTransfer;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.players.Spawnpoint;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.structures.Door;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.players.Ban;
import com.wurmonline.server.questions.SelectSpawnQuestion;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.KingdomIp;
import com.wurmonline.server.webinterface.WCGmMessage;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.tutorial.MissionPerformer;
import com.wurmonline.server.creatures.SpellEffectsEnum;
import com.wurmonline.server.skills.AffinitiesTimed;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.epic.ValreiMapData;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.PlayerCommunicator;
import com.wurmonline.server.steam.SteamId;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import com.wurmonline.server.utils.ProtocolUtilities;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import com.wurmonline.server.players.HackerIp;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.communication.SimpleConnectionListener;

public final class LoginHandler implements SimpleConnectionListener, TimeConstants, MiscConstants, CreatureTemplateIds, ProtoConstants, CounterTypes
{
    private final SocketConnection conn;
    private static Logger logger;
    public static final String legalChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int DISCONNECT_TICKS = 400;
    private int loadedItems;
    private boolean redirected;
    private static final byte clientDevLevel = 2;
    static int redirects;
    static int logins;
    private static final String BROKEN_PLAYER_MODEL = "model.player.broken";
    private static final String MESSAGE_FORMAT_UTF_8 = "UTF-8";
    private static final String PROBLEM_SENDING_LOGIN_DENIED_MESSAGE = ", problem sending login denied message: ";
    public static final Map<String, HackerIp> failedIps;
    private static final int MAX_REAL_DEATHS = 4;
    private static final int MAX_NAME_LENGTH = 40;
    private static final int MIN_NAME_LENGTH = 3;
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 192;
    
    public LoginHandler(final SocketConnection aConn) {
        this.loadedItems = 0;
        this.redirected = false;
        this.conn = aConn;
        if (LoginHandler.logger.isLoggable(Level.FINER)) {
            LoginHandler.logger.finer("Creating LoginHandler for SocketConnection " + aConn);
        }
    }
    
    public static final boolean containsIllegalCharacters(final String name) {
        final char[] charArray;
        final char[] chars = charArray = name.toCharArray();
        for (final char lC : charArray) {
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(lC) < 0) {
                return true;
            }
        }
        return false;
    }
    
    public static final String raiseFirstLetter(final String oldString) {
        if (oldString.length() == 0) {
            return oldString;
        }
        final String lOldString = oldString.toLowerCase();
        final String firstLetter = lOldString.substring(0, 1).toUpperCase();
        final String newString = firstLetter + lOldString.substring(1, lOldString.length());
        return newString;
    }
    
    @Override
    public void reallyHandle(final int num, final ByteBuffer byteBuffer) {
        final short cmd = byteBuffer.get();
        if (LoginHandler.logger.isLoggable(Level.FINEST)) {
            LoginHandler.logger.finest("Handling block with Command: " + cmd + ", " + ProtocolUtilities.getDescriptionForCommand((byte)cmd));
        }
        if (cmd == -15 || cmd == 23) {
            final int protocolVersion = byteBuffer.getInt();
            if (protocolVersion != 250990585) {
                final String message = "Incompatible communication protocol.\nPlease update the client at http://www.wurmonline.com or wait for the server to be updated.";
                LoginHandler.logger.log(Level.INFO, "Rejected protocol " + protocolVersion + ". Mine=" + 250990585 + ", (" + "0xEF5CFF9s" + ") " + this.conn);
                try {
                    this.sendLoginAnswer(false, "Incompatible communication protocol.\nPlease update the client at http://www.wurmonline.com or wait for the server to be updated.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                }
                catch (IOException ioe) {
                    if (LoginHandler.logger.isLoggable(Level.FINE)) {
                        LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + "Incompatible communication protocol.\nPlease update the client at http://www.wurmonline.com or wait for the server to be updated.", ioe);
                    }
                }
                return;
            }
            String name = this.getNextString(byteBuffer, "name", true);
            name = raiseFirstLetter(name);
            final String password = this.getNextString(byteBuffer, "password for " + name, false);
            final String serverPassword = this.getNextString(byteBuffer, "server password for " + name, false);
            final String steamIDAsString = this.getNextString(byteBuffer, "steamid for " + name, false);
            boolean sendExtraBytes = false;
            if (byteBuffer.hasRemaining()) {
                sendExtraBytes = (byteBuffer.get() != 0);
            }
            if (cmd == 23) {
                this.reconnect(name, password, false, serverPassword, steamIDAsString);
            }
            else {
                this.login(name, password, sendExtraBytes, false, serverPassword, steamIDAsString);
            }
        }
        else if (cmd == -52) {
            try {
                final String steamIDAsString2 = this.getNextString(byteBuffer, "steamid", false);
                final long authTicket = byteBuffer.getLong();
                final int arrayLenght = byteBuffer.getInt();
                final byte[] ticketArray = new byte[arrayLenght];
                for (int i = 0; i < ticketArray.length; ++i) {
                    ticketArray[i] = byteBuffer.get();
                }
                final long tokenLen = byteBuffer.getLong();
                if (Server.getInstance().steamHandler.getIsOfflineServer()) {
                    Server.getInstance().steamHandler.setIsPlayerAuthenticated(steamIDAsString2);
                    this.sendAuthenticationAnswer(true, "");
                    return;
                }
                final boolean wasAddedBeforeWithSameIp = Server.getInstance().steamHandler.addLoginHandler(steamIDAsString2, this);
                final int authenticationResult = Server.getInstance().steamHandler.BeginAuthSession(steamIDAsString2, ticketArray, tokenLen);
                if (authenticationResult != 0) {
                    if (authenticationResult == 2) {
                        if (!wasAddedBeforeWithSameIp) {
                            LoginHandler.logger.log(Level.INFO, "Duplicate authentication");
                            this.sendAuthenticationAnswer(false, "Duplicate authentication");
                        }
                        else {
                            Server.getInstance().steamHandler.setIsPlayerAuthenticated(steamIDAsString2);
                            this.sendAuthenticationAnswer(true, "");
                        }
                    }
                    else {
                        LoginHandler.logger.log(Level.INFO, "Steam could not authenticate the user");
                        this.sendAuthenticationAnswer(false, "Steam could not authenticate the user");
                    }
                }
            }
            catch (Throwable t) {
                LoginHandler.logger.log(Level.SEVERE, "Error while authenticating the user with steam.");
                this.sendAuthenticationAnswer(false, "Error while authenticating the user with steam.");
            }
        }
    }
    
    private String getNextString(final ByteBuffer byteBuffer, final String name, final boolean logValue) {
        final byte[] bytes = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(bytes);
        String decoded;
        try {
            decoded = new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException nse) {
            decoded = new String(bytes);
            final String logMessage = "Unsupported encoding for " + (logValue ? (name + ": " + decoded) : name);
            LoginHandler.logger.log(Level.WARNING, logMessage, nse);
        }
        return decoded;
    }
    
    private void login(final String name, String password, final boolean sendExtraBytes, final boolean isUndead, final String serverPassword, final String steamIDAsString) {
        try {
            password = hashPassword(password, encrypt(raiseFirstLetter(name)));
        }
        catch (Exception ex) {
            LoginHandler.logger.log(Level.SEVERE, name + " Failed to encrypt password", ex);
            final String message = "We failed to encrypt your password. Please try another.";
            try {
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, "We failed to encrypt your password. Please try another.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
            }
            catch (IOException ioe) {
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + "We failed to encrypt your password. Please try another.", ioe);
                }
            }
            return;
        }
        try {
            if (!Servers.localServer.LOGINSERVER && !Constants.maintaining && !isUndead && !Servers.localServer.testServer) {
                LoginHandler.logger.log(Level.WARNING, name + " logging in directly! Rejected.");
                final String message2 = "You need to connect to the login server.";
                try {
                    Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                    this.sendLoginAnswer(false, "You need to connect to the login server.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                }
                catch (IOException ioe2) {
                    if (LoginHandler.logger.isLoggable(Level.FINE)) {
                        LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + "You need to connect to the login server.", ioe2);
                    }
                }
                return;
            }
            this.handleLogin(name, password, sendExtraBytes, false, false, isUndead, serverPassword, steamIDAsString);
        }
        catch (Exception ex) {
            LoginHandler.logger.log(Level.SEVERE, "Failed to log " + name + " due to an Exception: " + ex.getMessage(), ex);
            final String message = "We failed to log you in. " + ex.getMessage();
            try {
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, message, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
            }
            catch (IOException ioe) {
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + message, ioe);
                }
            }
        }
    }
    
    private void reconnect(final String name, final String sessionkey, final boolean isUndead, final String serverPassword, final String steamIDAsString) {
        this.redirected = true;
        try {
            this.handleLogin(name, sessionkey, false, false, true, isUndead, serverPassword, steamIDAsString);
        }
        catch (Exception ex) {
            LoginHandler.logger.log(Level.SEVERE, name + " " + ex.getMessage(), ex);
            final String message = "We failed to log you in. " + ex.getMessage();
            try {
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, message, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
            }
            catch (IOException ioe) {
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + message, ioe);
                }
            }
        }
    }
    
    private boolean preValidateLogin(final String name, final String steamIDAsString) {
        if (Server.getInstance().isLagging()) {
            LoginHandler.logger.log(Level.INFO, "Refusing connection due to lagging server for " + name);
            final String message = "The server is lagging. Retrying in 20 seconds.";
            try {
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, "The server is lagging. Retrying in 20 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 20);
            }
            catch (IOException ioe) {
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + "The server is lagging. Retrying in 20 seconds.", ioe);
                }
            }
            return false;
        }
        return true;
    }
    
    private void handleLogin(String name, final String password, final boolean sendExtraBytes, final boolean usingWeb, final boolean reconnecting, final boolean isUndead, final String serverPassword, final String steamIDAsString) {
        if (!this.preValidateLogin(name, steamIDAsString)) {
            return;
        }
        String hashedSteamId;
        try {
            hashedSteamId = hashPassword(steamIDAsString, encrypt(raiseFirstLetter(name)));
        }
        catch (Exception ex) {
            LoginHandler.logger.log(Level.SEVERE, name + " Failed to encrypt password", ex);
            final String message = "We failed to encrypt your password. Please try another.";
            try {
                Server.getInstance().steamHandler.removeIsPlayerAuthenticated(steamIDAsString);
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, "We failed to encrypt your password. Please try another.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
            }
            catch (IOException ioe) {
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + "We failed to encrypt your password. Please try another.", ioe);
                }
            }
            return;
        }
        if (!Server.getInstance().steamHandler.isPlayerAuthenticated(steamIDAsString)) {
            Server.getInstance().steamHandler.removeIsPlayerAuthenticated(steamIDAsString);
            try {
                final String message2 = "You need to be authenticated";
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, message2, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -2);
            }
            catch (IOException ex6) {}
            if (!password.equals(hashedSteamId)) {
                LoginHandler.logger.log(Level.INFO, "Unauthenticated user trying to login with incorrect credentials, with ip: " + this.conn.getIp());
            }
            else {
                LoginHandler.logger.log(Level.INFO, "Unauthenticated user trying to login, with ip: " + this.conn.getIp());
            }
            return;
        }
        Server.getInstance().steamHandler.removeIsPlayerAuthenticated(steamIDAsString);
        if (!password.equals(hashedSteamId)) {
            try {
                final String message2 = "You need to be authenticated";
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, message2, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -2);
            }
            catch (IOException ex7) {}
            LoginHandler.logger.log(Level.INFO, "Authenticated user trying to login with incorrect credentials, with ip: " + this.conn.getIp());
            return;
        }
        final String steamServerPassword = Servers.localServer.getSteamServerPassword();
        if (!steamServerPassword.equals(serverPassword)) {
            try {
                final String message = "Incorrect server password!";
                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                this.sendLoginAnswer(false, message, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -2);
            }
            catch (IOException ex8) {}
            LoginHandler.logger.log(Level.INFO, "Incorrect server password: " + this.conn.getIp());
            return;
        }
        if (this.checkName(name)) {
            if (isUndead && !Servers.localServer.LOGINSERVER) {
                name = "Undead " + name;
            }
            boolean wasinvuln = false;
            try {
                final Player p = Players.getInstance().getPlayer(name);
                p.setSteamID(SteamId.fromSteamID64(Long.valueOf(steamIDAsString)));
                String dbpassw = "";
                dbpassw = p.getSaveFile().getPassword();
                if (!reconnecting) {
                    p.setSendExtraBytes(sendExtraBytes);
                }
                if (!dbpassw.equals(password) && !sendExtraBytes) {
                    String message3 = "Password incorrect. Please try again or create a new player with a different name than " + name + ".";
                    final HackerIp ip = LoginHandler.failedIps.get(this.conn.getIp());
                    if (ip != null) {
                        ip.name = name;
                        final HackerIp hackerIp = ip;
                        ++hackerIp.timesFailed;
                        long atime = 0L;
                        if (ip.timesFailed == 10) {
                            atime = 180000L;
                        }
                        if (ip.timesFailed == 20) {
                            atime = 600000L;
                        }
                        else if (ip.timesFailed % 20 == 0) {
                            atime = 10800000L;
                        }
                        if (ip.timesFailed == 100) {
                            Players.addGmMessage("System", "The ip " + this.conn.getIp() + " has failed the password for " + name + " 100 times. It is now banned one hour every failed attempt.");
                        }
                        if (ip.timesFailed > 100) {
                            atime = 3600000L;
                        }
                        ip.mayTryAgain = System.currentTimeMillis() + atime;
                        if (atime > 0L) {
                            message3 = message3 + " Because of the repeated failures you may try again in " + Server.getTimeFor(atime) + ".";
                        }
                    }
                    else {
                        LoginHandler.failedIps.put(this.conn.getIp(), new HackerIp(1, System.currentTimeMillis(), name));
                    }
                    try {
                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                        this.sendLoginAnswer(false, message3, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -2);
                    }
                    catch (IOException ioe2) {
                        if (LoginHandler.logger.isLoggable(Level.FINE)) {
                            LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + message3, ioe2);
                        }
                    }
                    return;
                }
                final Ban ban = Players.getInstance().getAnyBan(this.conn.getIp(), p, steamIDAsString);
                if (ban != null) {
                    final String time = Server.getTimeFor(ban.getExpiry() - System.currentTimeMillis());
                    String message4 = ban.getIdentifier() + " is banned for " + time + " more. Reason: " + ban.getReason();
                    if (ban.getExpiry() - System.currentTimeMillis() > 29030400000L) {
                        message4 = ban.getIdentifier() + " is permanently banned. Reason: " + ban.getReason();
                    }
                    try {
                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                        this.sendLoginAnswer(false, message4, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                    }
                    catch (IOException ex9) {}
                    LoginHandler.logger.log(Level.INFO, name + " is banned, trying to log on from " + this.conn.getIp());
                    return;
                }
                if (p.isFullyLoaded()) {
                    if (!p.isLoggedOut()) {
                        if (p.getCommunicator().getCurrentmove() != null && p.getCommunicator().getCurrentmove().getNext() != null) {
                            LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + " was still moving at reconnect - " + p.getCommunicator().getMoves());
                            final String message5 = "You are still moving on the server. Retry in 10 seconds.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "You are still moving on the server. Retry in 10 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 10);
                            }
                            catch (IOException ioe2) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "You are still moving on the server. Retry in 10 seconds.", ioe2);
                                }
                            }
                            return;
                        }
                        if (p.getSaveFile().realdeath > 4) {
                            final String message5 = "Your account has suffered real death. You can not log on.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "Your account has suffered real death. You can not log on.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                            }
                            catch (IOException ioe2) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "Your account has suffered real death. You can not log on.", ioe2);
                                }
                            }
                            return;
                        }
                        LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + " successfully reconnected.");
                        wasinvuln = p.getCommunicator().isInvulnerable();
                        p.getCommunicator().sendShutDown("Reconnected", true);
                        p.stopTeleporting();
                        p.getMovementScheme().clearIntraports();
                        p.getCommunicator().player = null;
                        p.setCommunicator(new PlayerCommunicator(p, this.conn));
                        this.conn.setLogin(true);
                        if (p.getSaveFile().currentServer != Servers.localServer.id) {
                            p.getSaveFile().setLogin();
                            p.getSaveFile().logout();
                            String message5 = "Failed to redirect to another server.";
                            LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + " redirected from " + Servers.localServer.id + " to " + p.getSaveFile().currentServer);
                            try {
                                final ServerEntry entry = Servers.getServerWithId(p.getSaveFile().currentServer);
                                if (entry != null) {
                                    if (entry.isAvailable(p.getPower(), p.isReallyPaying())) {
                                        p.getCommunicator().sendReconnect(entry.EXTERNALIP, Integer.parseInt(entry.EXTERNALPORT), password);
                                    }
                                    else {
                                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                        message5 = "The server is currently not available. Please try later.";
                                        this.sendLoginAnswer(false, message5, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 300);
                                    }
                                }
                                else {
                                    Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                    this.sendLoginAnswer(false, message5, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                                }
                            }
                            catch (IOException ex10) {}
                            Players.getInstance().logoutPlayer(p);
                            ++LoginHandler.redirects;
                            this.conn.ticksToDisconnect = 400;
                            return;
                        }
                        ++LoginHandler.logins;
                        p.setLoginHandler(this);
                        this.conn.setConnectionListener(p.getCommunicator());
                        Server.getInstance().addIp(this.conn.getIp());
                        p.setIpaddress(this.conn.getIp());
                        if (p.isTransferring()) {
                            final String message5 = "You are being transferred to another server.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "You are being transferred to another server.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                            }
                            catch (IOException ioe2) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "You are being transferred to another server.", ioe2);
                                }
                            }
                            return;
                        }
                        if (p.getPower() < 1 && !p.isPaying() && Players.getInstance().numberOfPlayers() > Servers.localServer.pLimit) {
                            final String message5 = "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                            }
                            catch (IOException ioe2) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.", ioe2);
                                }
                            }
                            return;
                        }
                        if (Constants.maintaining && p.getPower() <= 1) {
                            final String message5 = "The server is in maintenance mode. Retrying in 60 seconds.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "The server is in maintenance mode. Retrying in 60 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                            }
                            catch (IOException ioe2) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "The server is in maintenance mode. Retrying in 60 seconds.", ioe2);
                                }
                            }
                            return;
                        }
                        p.setLink(true);
                        if (!p.isDead()) {
                            p.setNewTile(null, 0.0f, true);
                        }
                        if (p.isTeleporting()) {
                            p.cancelTeleport();
                        }
                        putOutsideWall(p);
                        if (p.isOnSurface()) {
                            putOutsideFence(p);
                            if (!p.isDead() && creatureIsInsideWrongHouse(p, false)) {
                                putOutsideHouse(p, false);
                            }
                        }
                        p.sendAddChampionPoints();
                        p.getCommunicator().sendWeather();
                        p.getCommunicator().checkSendWeather();
                        p.getCommunicator().sendSleepInfo();
                        if (!p.isDead()) {
                            putInBoatAndAssignSeat(p, true);
                            try {
                                final Zone zone = Zones.getZone(p.getTileX(), p.getTileY(), p.isOnSurface());
                                zone.addCreature(p.getWurmId());
                            }
                            catch (NoSuchZoneException nsz) {
                                LoginHandler.logger.log(Level.WARNING, nsz.getMessage(), nsz);
                                p.logoutIn(2, "You were out of bounds.");
                                return;
                            }
                            catch (NoSuchCreatureException | NoSuchPlayerException ex11) {
                                final WurmServerException ex5;
                                final WurmServerException e = ex5;
                                LoginHandler.logger.log(Level.WARNING, e.getMessage(), e);
                                p.logoutIn(2, "A server error occurred.");
                                return;
                            }
                        }
                        try {
                            final String message5 = "Reconnecting " + name + "! " + (Servers.localServer.hasMotd() ? Servers.localServer.getMotd() : Constants.motd);
                            float posx = p.getStatus().getPositionX();
                            float posy = p.getStatus().getPositionY();
                            byte commandType = 0;
                            if (p.isVehicleCommander()) {
                                final Vehicle vehic = Vehicles.getVehicleForId(p.getVehicle());
                                if (vehic != null) {
                                    final Seat s = vehic.getPilotSeat();
                                    if (s != null && s.occupant == p.getWurmId()) {
                                        float posz = p.getStatus().getPositionZ();
                                        try {
                                            final VolaTile tile = Zones.getOrCreateTile((int)(p.getPosX() / 4.0f), (int)(p.getPosY() / 4.0f), p.getLayer() >= 0);
                                            boolean skipSetZ = false;
                                            if (tile != null) {
                                                final Structure structure = tile.getStructure();
                                                if (structure != null) {
                                                    skipSetZ = (structure.isTypeHouse() || structure.getWurmId() == p.getBridgeId());
                                                }
                                            }
                                            if (!skipSetZ) {
                                                posz = Zones.calculateHeight(p.getStatus().getPositionX(), p.getStatus().getPositionY(), p.isOnSurface());
                                            }
                                            if (posz < 0.0f) {
                                                posz = Math.max(-1.45f, posz);
                                            }
                                            p.getStatus().setPositionZ(posz);
                                        }
                                        catch (NoSuchZoneException ex12) {}
                                        commandType = vehic.commandType;
                                        posz = Math.max(posz + s.offz, s.offz);
                                        posy += s.offy;
                                        posx += s.offx;
                                        p.getStatus().setPositionZ(posz);
                                    }
                                }
                            }
                            p.getMovementScheme().setPosition(posx, posy, p.getStatus().getPositionZ(), p.getStatus().getRotation(), p.isOnSurface() ? 0 : -1);
                            final VolaTile targetTile = Zones.getTileOrNull((int)(posx / 4.0f), (int)(posy / 4.0f), p.isOnSurface());
                            if (targetTile != null) {
                                float height = (p.getFloorLevel() > 0) ? (p.getFloorLevel() * 3) : 0.0f;
                                if (p.getBridgeId() > 0L) {
                                    height = 0.0f;
                                }
                                p.getMovementScheme().setGroundOffset((int)(height * 10.0f), true);
                                p.calculateFloorLevel(targetTile, true);
                            }
                            p.getMovementScheme().haltSpeedModifier();
                            p.setTeleporting(true);
                            p.setTeleportCounter(p.getTeleportCounter() + 1);
                            final byte power = (byte)(Players.isArtist(p.getWurmId(), false, false) ? 2 : ((byte)p.getPower()));
                            this.sendLoginAnswer(true, message5, p.getStatus().getPositionX(), p.getStatus().getPositionY(), p.getStatus().getPositionZ(), p.getStatus().getRotation(), p.isOnSurface() ? 0 : -1, p.getModelName(), power, 0, commandType, p.getKingdomTemplateId(), p.getFace(), p.getTeleportCounter(), p.getBlood(), p.getBridgeId(), p.getMovementScheme().getGroundOffset());
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, "Sent " + p.getStatus().getPositionX() + "," + p.getStatus().getPositionY() + "," + p.getStatus().getPositionZ() + "," + p.getStatus().getRotation());
                            }
                        }
                        catch (IOException ioe3) {
                            LoginHandler.logger.log(Level.INFO, "Player " + name + " dropped during login.", ioe3);
                            p.logoutIn(2, "You seemed to have lost your connection to Wurm.");
                            return;
                        }
                        Server.getInstance().addToPlayersAtLogin(p);
                        try {
                            p.loadSkills();
                            p.sendSkills();
                            sendAllItemModelNames(p);
                            sendAllEquippedArmor(p);
                            if (p.getStatus().getBody().getBodyItem() != null) {
                                p.getStatus().getBody().getBodyItem().addWatcher(-1L, p);
                            }
                            p.getInventory().addWatcher(-1L, p);
                            p.getBody().sendWounds();
                            Players.loadAllPrivatePOIForPlayer(p);
                            p.sendAllMapAnnotations();
                            p.resetLastSentToolbelt();
                            ValreiMapData.sendAllMapData(p);
                        }
                        catch (Exception ex2) {
                            LoginHandler.logger.log(Level.SEVERE, "Failed to load status for player " + name + ".", ex2);
                            p.getCommunicator().sendAlertServerMessage("Failed to load your status! Please contact server administrators.");
                            p.logoutIn(2, "The game failed to load your status. Please contact server administrators.");
                            return;
                        }
                        p.sendReligion();
                        p.sendKarma();
                        p.sendScenarioKarma();
                        if (p.getTeam() != null) {
                            p.getTeam().creatureReconnectedTeam(p);
                        }
                        p.lastSentHasCompass = false;
                        Players.getInstance().sendReconnect(p);
                        try {
                            p.sendActionControl(p.getCurrentAction().getActionString(), true, p.getCurrentAction().getTimeLeft());
                        }
                        catch (NoSuchActionException ex13) {}
                        if (p.getSpellEffects() != null) {
                            p.getSpellEffects().sendAllSpellEffects();
                        }
                        ItemBonus.sendAllItemBonusToPlayer(p);
                        p.sendSpellResistances();
                        p.getCommunicator().sendClimb(p.isClimbing());
                        p.getCommunicator().sendToggle(0, p.isClimbing());
                        p.getCommunicator().sendToggle(2, p.isLegal());
                        p.getCommunicator().sendToggle(1, p.faithful);
                        p.getCommunicator().sendToggle(3, p.isStealth());
                        p.getCommunicator().sendToggle(4, p.isAutofight());
                        p.getCommunicator().sendToggle(100, p.isArcheryMode());
                        if (p.getShield() != null) {
                            p.getCommunicator().sendToggleShield(true);
                        }
                        final Item dragged = p.getMovementScheme().getDraggedItem();
                        if (dragged != null) {
                            Items.stopDragging(dragged);
                            Items.startDragging(p, dragged);
                        }
                        Players.getInstance().addToGroups(p);
                        p.destroyVisionArea();
                        try {
                            p.createVisionArea();
                        }
                        catch (Exception ex3) {
                            LoginHandler.logger.log(Level.WARNING, "Failed to create visionarea for player " + p.getName(), ex3);
                            p.logoutIn(2, "The game failed to create your vision area. Please contact the administrators.");
                            return;
                        }
                        if (!p.hasLink()) {
                            p.destroyVisionArea();
                            return;
                        }
                        if (!p.isDead()) {
                            VolaTile tile2 = p.getCurrentTile();
                            if (tile2 == null) {
                                LoginHandler.logger.log(Level.WARNING, p.getName() + " isn't in the world. Adding and retrying.");
                                p.sendToWorld();
                            }
                            p.getCommunicator().sendSelfToLocal();
                            Achievements.sendAchievementList(p);
                            p.getCommunicator().sendAllKingdoms();
                            tile2 = p.getCurrentTile();
                            final Door[] doors = tile2.getDoors();
                            if (doors != null) {
                                for (final Door lDoor : doors) {
                                    if (lDoor.canBeOpenedBy(p, false)) {
                                        if (lDoor instanceof FenceGate) {
                                            p.getCommunicator().sendOpenFence(((FenceGate)lDoor).getFence(), true, true);
                                        }
                                        else {
                                            p.getCommunicator().sendOpenDoor(lDoor);
                                        }
                                    }
                                }
                            }
                            if (!wasinvuln) {
                                p.getCommunicator().sendAlertServerMessage("You are not invulnerable now.");
                                p.getCommunicator().setInvulnerable(false);
                            }
                        }
                        else {
                            p.getCommunicator().sendDead();
                            p.sendSpawnQuestion();
                        }
                        p.setFullyLoaded();
                        p.getCommunicator().setReady(true);
                        sendLoggedInPeople(p);
                        sendStatus(p);
                        p.getCombatHandler().sendRodEffect();
                        p.sendHasFingerEffect();
                        p.getStatus().sendStateString();
                        p.getCommunicator().sendMapInfo();
                        Server.getInstance().addToPlayersAtLogin(p);
                        if (p.getVisionArea() != null && p.getVisionArea().getSurface() != null) {
                            p.getVisionArea().getSurface().sendCreatureItems(p);
                        }
                        p.setBestLightsource(null, true);
                        boolean isEducated = false;
                        for (final Titles.Title t : p.getTitles()) {
                            if (t == Titles.Title.Educated) {
                                isEducated = true;
                            }
                        }
                        if (!isEducated) {
                            PlayerTutorial.getTutorialForPlayer(p.getWurmId(), true).sendCurrentStageBML();
                        }
                        p.isLit = false;
                        p.recalcLimitingFactor(null);
                        if (p.getCultist() != null) {
                            p.getCultist().sendBuffs();
                        }
                        AffinitiesTimed.sendTimedAffinitiesFor(p);
                        if (p.isDeathProtected()) {
                            p.getCommunicator().sendAddStatusEffect(SpellEffectsEnum.DEATH_PROTECTION, Integer.MAX_VALUE);
                        }
                        MissionPerformer.sendEpicMissionsPerformed(p, p.getCommunicator());
                        final MissionPerformer mp = MissionPerformed.getMissionPerformer(p.getWurmId());
                        if (mp != null) {
                            mp.sendAllMissionPerformed(p.getCommunicator());
                        }
                        checkPutOnBoat(p);
                        return;
                    }
                }
                try {
                    final Zone zone = Zones.getZone(p.getTileX(), p.getTileY(), p.isOnSurface());
                    zone.deleteCreature(p, true);
                }
                catch (NoSuchZoneException ex14) {}
                catch (NoSuchCreatureException ex15) {}
                catch (NoSuchPlayerException ex16) {}
                Players.getInstance().logoutPlayer(p);
                LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + " logged on too early after reconnecting.");
            }
            catch (NoSuchPlayerException nsp) {
                final PlayerInfo file = PlayerInfoFactory.createPlayerInfo(name);
                Player player = null;
                try {
                    if (isUndead) {
                        file.undeadType = (byte)(1 + Server.rand.nextInt(3));
                        if (Servers.localServer.LOGINSERVER) {
                            try {
                                if (file.currentServer <= 1) {
                                    for (final ServerEntry entry2 : Servers.getAllServers()) {
                                        if (entry2.EPIC && entry2.isAvailable(file.getPower(), true)) {
                                            file.currentServer = entry2.getId();
                                        }
                                    }
                                }
                                player = new Player(file, this.conn);
                                player.setSteamID(SteamId.fromSteamID64(Long.valueOf(steamIDAsString)));
                                final Ban ban2 = Players.getInstance().getAnyBan(this.conn.getIp(), player, steamIDAsString);
                                if (ban2 != null) {
                                    try {
                                        final String time2 = Server.getTimeFor(ban2.getExpiry() - System.currentTimeMillis());
                                        String message6 = ban2.getIdentifier() + " is banned for " + time2 + " more. Reason: " + ban2.getReason();
                                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                        if (ban2.getExpiry() - System.currentTimeMillis() > 29030400000L) {
                                            message6 = ban2.getIdentifier() + " is permanently banned. Reason: " + ban2.getReason();
                                        }
                                        this.sendLoginAnswer(false, message6, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                                    }
                                    catch (IOException ioe2) {
                                        if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                            LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending banned IP login denied message", ioe2);
                                        }
                                    }
                                    LoginHandler.logger.log(Level.INFO, name + " is banned, trying to log on from " + this.conn.getIp());
                                    return;
                                }
                                String message4 = "The server is currently not available. Please try later.";
                                final ServerEntry entry3 = Servers.getServerWithId(file.currentServer);
                                if (entry3 != null) {
                                    if (entry3.isAvailable(file.getPower(), file.isPaying())) {
                                        player.getCommunicator().sendReconnect(entry3.EXTERNALIP, Integer.parseInt(entry3.EXTERNALPORT), password);
                                        LoginHandler.logger.log(Level.INFO, this.conn.getIp() + ", " + name + " redirected from " + Servers.localServer.id + " to server ID: " + file.currentServer);
                                    }
                                    else {
                                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                        message4 = "The server is currently not available. Please try later.";
                                        this.sendLoginAnswer(false, message4, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 300);
                                        LoginHandler.logger.log(Level.INFO, this.conn.getIp() + ", " + name + " could not be redirected from " + Servers.localServer.id + " to server ID: " + file.currentServer + " not avail.");
                                    }
                                }
                                else {
                                    LoginHandler.logger.warning(this.conn.getIp() + ", " + name + " could not be redirected from " + Servers.localServer.id + " to non-existant server ID: " + file.currentServer + ", the database entry is wrong");
                                    Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                    this.sendLoginAnswer(false, message4, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -1);
                                }
                                return;
                            }
                            catch (IOException ioe3) {
                                return;
                            }
                        }
                    }
                    file.load();
                    if (!password.equals(file.getPassword())) {
                        LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + ", tried to log in with wrong password.");
                        if (file.getPower() > 0) {
                            Players.getInstance().sendConnectAlert(this.conn.getIp() + "," + name + ", tried to log in with wrong password.");
                            Players.addGmMessage(name, this.conn.getIp() + "," + name + ", tried to log in with wrong password.");
                            final WCGmMessage wc = new WCGmMessage(WurmId.getNextWCCommandId(), name, "(" + Servers.localServer.id + ") " + this.conn.getIp() + "," + name + ", tried to log in with wrong password.", false);
                            wc.sendToLoginServer();
                        }
                        String message5 = "Password incorrect. Please try again or create a new player with a different name than " + name + ".";
                        final HackerIp ip2 = LoginHandler.failedIps.get(this.conn.getIp());
                        if (ip2 != null) {
                            ip2.name = name;
                            final HackerIp hackerIp2 = ip2;
                            ++hackerIp2.timesFailed;
                            long atime2 = 0L;
                            if (ip2.timesFailed == 10) {
                                atime2 = 180000L;
                            }
                            if (ip2.timesFailed == 20) {
                                atime2 = 600000L;
                            }
                            else if (ip2.timesFailed % 20 == 0) {
                                atime2 = 10800000L;
                            }
                            if (ip2.timesFailed == 100) {
                                Players.addGmMessage("System", "The ip " + this.conn.getIp() + " has failed the password for " + name + " 100 times. It is now banned one hour every failed attempt.");
                            }
                            if (ip2.timesFailed > 100) {
                                atime2 = 3600000L;
                            }
                            ip2.mayTryAgain = System.currentTimeMillis() + atime2;
                            if (atime2 > 0L) {
                                message5 = message5 + " Because of the repeated failures you may try again in " + Server.getTimeFor(atime2) + ".";
                            }
                        }
                        else {
                            LoginHandler.failedIps.put(this.conn.getIp(), new HackerIp(1, System.currentTimeMillis(), name));
                        }
                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                        this.sendLoginAnswer(false, message5, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                        return;
                    }
                    player = new Player(file, this.conn);
                    player.setSteamID(SteamId.fromSteamID64(Long.valueOf(steamIDAsString)));
                    final Ban ban2 = Players.getInstance().getAnyBan(this.conn.getIp(), player, steamIDAsString);
                    if (ban2 != null) {
                        try {
                            final String time2 = Server.getTimeFor(ban2.getExpiry() - System.currentTimeMillis());
                            String message6 = ban2.getIdentifier() + " is banned for " + time2 + " more. Reason: " + ban2.getReason();
                            Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                            if (ban2.getExpiry() - System.currentTimeMillis() > 29030400000L) {
                                message6 = ban2.getIdentifier() + " is permanently banned. Reason: " + ban2.getReason();
                            }
                            this.sendLoginAnswer(false, message6, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                        }
                        catch (IOException ioe2) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending banned IP login denied message", ioe2);
                            }
                        }
                        LoginHandler.logger.log(Level.INFO, name + " is banned, trying to log on from " + this.conn.getIp());
                        return;
                    }
                    if (file.currentServer != Servers.localServer.id) {
                        String message4 = "The server is currently not available. Please try later.";
                        try {
                            final ServerEntry entry3 = Servers.getServerWithId(file.currentServer);
                            if (entry3 != null) {
                                if (entry3.isAvailable(file.getPower(), file.isPaying())) {
                                    player.getCommunicator().sendReconnect(entry3.EXTERNALIP, Integer.parseInt(entry3.EXTERNALPORT), password);
                                    LoginHandler.logger.log(Level.INFO, this.conn.getIp() + ", " + name + " redirected from " + Servers.localServer.id + " to server ID: " + file.currentServer);
                                }
                                else {
                                    Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                    message4 = "The server is currently not available. Please try later.";
                                    this.sendLoginAnswer(false, message4, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 300);
                                    LoginHandler.logger.log(Level.INFO, this.conn.getIp() + ", " + name + " could not be redirected from " + Servers.localServer.id + " to server ID: " + file.currentServer + " not avail.");
                                }
                            }
                            else {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                LoginHandler.logger.warning(this.conn.getIp() + ", " + name + " could not be redirected from " + Servers.localServer.id + " to non-existant server ID: " + file.currentServer + ", the database entry is wrong");
                                this.sendLoginAnswer(false, message4, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -1);
                            }
                        }
                        catch (IOException ioe4) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem redirecting from " + Servers.localServer.id + " to server ID: " + file.currentServer, ioe4);
                            }
                        }
                        file.lastLogin = System.currentTimeMillis() - 10000L;
                        file.logout();
                        file.save();
                        ++LoginHandler.redirects;
                        this.conn.ticksToDisconnect = 400;
                        return;
                    }
                    if (!Constants.isGameServer && file.currentServer == Servers.localServer.id) {
                        LoginHandler.logger.log(Level.WARNING, name + " tried to logon locally.");
                        final String message4 = "You can not log on to this type of server. Contact a GM or Dev";
                        try {
                            Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                            this.sendLoginAnswer(false, "You can not log on to this type of server. Contact a GM or Dev", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -1);
                        }
                        catch (IOException ioe4) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "You can not log on to this type of server. Contact a GM or Dev", ioe4);
                            }
                        }
                        file.lastLogin = System.currentTimeMillis() - 10000L;
                        file.logout();
                        file.save();
                        this.conn.ticksToDisconnect = 400;
                        return;
                    }
                    if (player.getSaveFile().realdeath > 4) {
                        final String message4 = "Your account has suffered real death. You can not log on.";
                        try {
                            Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                            this.sendLoginAnswer(false, "Your account has suffered real death. You can not log on.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -1);
                        }
                        catch (IOException ioe4) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "Your account has suffered real death. You can not log on.", ioe4);
                            }
                        }
                        return;
                    }
                    player.setLoginHandler(this);
                    this.conn.setConnectionListener(player.getCommunicator());
                    ++LoginHandler.logins;
                    if (player.getPower() < 1 && !player.isPaying() && Players.getInstance().numberOfPlayers() > Servers.localServer.pLimit) {
                        final String message4 = "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.";
                        try {
                            Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                            this.sendLoginAnswer(false, "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                        }
                        catch (IOException ioe4) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.", ioe4);
                            }
                        }
                        return;
                    }
                    if (player.getPower() < 1 && !player.isPaying() && Servers.localServer.ISPAYMENT) {
                        final String message4 = "This server is a premium only server. You can not log on until you have purchased premium time in the webshop.";
                        try {
                            Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                            this.sendLoginAnswer(false, "This server is a premium only server. You can not log on until you have purchased premium time in the webshop.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                        }
                        catch (IOException ioe4) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "This server is a premium only server. You can not log on until you have purchased premium time in the webshop.", ioe4);
                            }
                        }
                        return;
                    }
                    if (Constants.maintaining && player.getPower() <= 1) {
                        try {
                            Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                            this.sendLoginAnswer(false, "The server is in maintenance mode. Retrying in 60 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                        }
                        catch (IOException ioe2) {
                            if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending maintenance mode login denied", ioe2);
                            }
                        }
                        return;
                    }
                    if (Constants.enableSpyPrevention && Servers.localServer.PVPSERVER && !Servers.localServer.testServer && player.getPower() < 1) {
                        final byte kingdom = Players.getInstance().getKingdomForPlayer(player.getWurmId());
                        final KingdomIp kip = KingdomIp.getKIP(this.conn.getIp(), kingdom);
                        if (kip != null) {
                            final long answer = kip.mayLogonKingdom(kingdom);
                            if (answer < 0L) {
                                try {
                                    final Kingdom k = Kingdoms.getKingdom(kip.getKingdom());
                                    Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                    if (k != null) {
                                        this.sendLoginAnswer(false, "Spy prevention: Someone is playing on kingdom " + k.getName() + " from this ip address.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                                    }
                                    else {
                                        this.sendLoginAnswer(false, "Spy prevention: Someone is playing on another kingdom from this ip address.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                                    }
                                }
                                catch (IOException ioe5) {
                                    if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                        LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending spy prevention login denied", ioe5);
                                    }
                                }
                                return;
                            }
                            if (answer > 1L) {
                                final String timeLeft = Server.getTimeFor(answer);
                                if (answer < 0L) {
                                    try {
                                        Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                        final Kingdom i = Kingdoms.getKingdom(kingdom);
                                        if (i != null) {
                                            this.sendLoginAnswer(false, "Spy prevention: You have to wait " + timeLeft + " because someone was recently playing " + i.getName() + " from this ip address.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                                        }
                                        else {
                                            this.sendLoginAnswer(false, "Spy prevention: You have to wait " + timeLeft + " because someone was recently playing in another kingdom from this ip address.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                                        }
                                    }
                                    catch (IOException ioe6) {
                                        if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                            LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending spy prevention login denied", ioe6);
                                        }
                                    }
                                    return;
                                }
                            }
                            kip.logon(kingdom);
                        }
                    }
                    LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + " successfully logged on, id: " + player.getWurmId() + '.');
                    Server.getInstance().addPlayer(player);
                    player.initialisePlayer(file);
                    if (!reconnecting) {
                        player.setSendExtraBytes(sendExtraBytes);
                    }
                    player.checkBodyInventoryConsistency();
                    player.getBody().createBodyParts();
                    Server.getInstance().startSendingFinals(player);
                    final long start = System.nanoTime();
                    player.loadSkills();
                    Items.loadAllItemsForCreature(player, player.getStatus().getInventoryId());
                    player.getCommunicator().sendMapInfo();
                    Players.loadAllPrivatePOIForPlayer(player);
                    player.resetLastSentToolbelt();
                    player.sendAllMapAnnotations();
                    ValreiMapData.sendAllMapData(player);
                    player.getCommunicator().sendClearTickets();
                    player.getCommunicator().sendClearFriendsList();
                    if (player.getCultist() != null) {
                        player.getCultist().sendBuffs();
                    }
                    AffinitiesTimed.sendTimedAffinitiesFor(player);
                    Players.getInstance().sendConnectInfo(player, " has logged in.", player.getLastLogin(), PlayerOnlineStatus.ONLINE, true);
                    Players.getInstance().addToGroups(player);
                    if (player.getBridgeId() != -10L) {
                        final BridgePart[] bridgeParts = player.getCurrentTile().getBridgeParts();
                        boolean foundBridge = false;
                        for (final BridgePart bp : bridgeParts) {
                            foundBridge = true;
                            if (!bp.isFinished()) {
                                foundBridge = false;
                                break;
                            }
                        }
                        if (foundBridge) {
                            for (final BridgePart bp : bridgeParts) {
                                if (bp.isFinished() && bp.hasAnExit() && bp.getStructureId() != player.getBridgeId()) {
                                    LoginHandler.logger.info(String.format("Player %s logged in at [%s, %s] where bridge ID %s used to be built, but has since been replaced by the bridge ID %s.", player.getName(), player.getTileX(), player.getTileY(), player.getBridgeId(), bp.getStructureId()));
                                    player.setBridgeId(bp.getStructureId());
                                    break;
                                }
                            }
                        }
                        else {
                            LoginHandler.logger.info(String.format("Player %s logged in at [%s, %s] where a bridge used to be, but no longer exists.", player.getName(), player.getTileX(), player.getTileY()));
                            player.setBridgeId(-10L);
                        }
                    }
                    if (LoginHandler.logger.isLoggable(Level.FINE)) {
                        LoginHandler.logger.info("Loading all skills and items took " + (System.nanoTime() - start) / 1000000.0f + " millis for " + name);
                    }
                }
                catch (Exception ex2) {
                    if (!isUndead && !Servers.localServer.testServer && LoginHandler.logger.isLoggable(Level.INFO) && !Server.getInstance().isPS()) {
                        LoginHandler.logger.log(Level.INFO, "Caught Exception while trying to log player in:" + ex2.getMessage() + " for " + name, ex2);
                    }
                    try {
                        if (!sendExtraBytes && !Servers.localServer.testServer && !isUndead && !Server.getInstance().isPS()) {
                            final String message4 = "You need to register an account on www.wurmonline.com.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "You need to register an account on www.wurmonline.com.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                            }
                            catch (IOException ioe4) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "You need to register an account on www.wurmonline.com.", ioe4);
                                }
                            }
                            return;
                        }
                        if (Constants.maintaining) {
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "The server is in maintenance mode. Retrying in 60 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                            }
                            catch (IOException ioe2) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending maintenance mode login denied", ioe2);
                                }
                            }
                            return;
                        }
                        if (Players.getInstance().numberOfPlayers() > Servers.localServer.pLimit) {
                            final String message4 = "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 60);
                            }
                            catch (IOException ioe4) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "The server is full. If you pay for a premium account you will be able to enter anyway. Retrying in 60 seconds.", ioe4);
                                }
                            }
                            return;
                        }
                        if (Servers.localServer.id != Servers.loginServer.id && !isUndead && !Servers.localServer.testServer) {
                            final String message4 = "There are multiple login servers in the cluster, please remove so it is only one";
                            try {
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                this.sendLoginAnswer(false, "There are multiple login servers in the cluster, please remove so it is only one", 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, -1);
                            }
                            catch (IOException ioe4) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + ", problem sending login denied message: " + "There are multiple login servers in the cluster, please remove so it is only one", ioe4);
                                }
                            }
                            return;
                        }
                        LoginHandler.logger.log(Level.INFO, this.conn.getIp() + "," + name + " was created successfully.");
                        player = Player.doNewPlayer(1, this.conn);
                        player.setName(name);
                        float posX = Servers.localServer.SPAWNPOINTJENNX * 4 + Server.rand.nextInt(10);
                        float posY = Servers.localServer.SPAWNPOINTJENNY * 4 + Server.rand.nextInt(10);
                        final int r = Server.rand.nextInt(3);
                        final float rot = Server.rand.nextInt(360);
                        byte kingdom2 = 1;
                        if (isUndead) {
                            kingdom2 = 0;
                            final float[] txty = Player.findRandomSpawnX(false, false);
                            posX = txty[0];
                            posY = txty[1];
                        }
                        else {
                            if (Servers.localServer.KINGDOM != 0) {
                                kingdom2 = Servers.localServer.KINGDOM;
                            }
                            else if (r == 1) {
                                kingdom2 = 2;
                                posX = Servers.localServer.SPAWNPOINTMOLX * 4 + Server.rand.nextInt(10);
                                posY = Servers.localServer.SPAWNPOINTMOLY * 4 + Server.rand.nextInt(10);
                            }
                            else if (r == 2) {
                                kingdom2 = 3;
                                posX = Servers.localServer.SPAWNPOINTLIBX * 4 + Server.rand.nextInt(10);
                                posY = Servers.localServer.SPAWNPOINTLIBY * 4 + Server.rand.nextInt(10);
                            }
                            if (Servers.localServer.randomSpawns) {
                                final float[] txty = Player.findRandomSpawnX(true, true);
                                posX = txty[0];
                                posY = txty[1];
                            }
                        }
                        final Spawnpoint sp = getInitialSpawnPoint(kingdom2);
                        if (sp != null) {
                            posX = sp.tilex * 4 + Server.rand.nextInt(10);
                            posY = sp.tiley * 4 + Server.rand.nextInt(10);
                        }
                        final long wurmId = WurmId.getNextPlayerId();
                        player = (Player)player.setWurmId(wurmId, posX, posY, rot, 0);
                        final Ban ban3 = Players.getInstance().getAnyBan(this.conn.getIp(), player, steamIDAsString);
                        if (ban3 != null) {
                            try {
                                final String time3 = Server.getTimeFor(ban3.getExpiry() - System.currentTimeMillis());
                                String message7 = ban3.getIdentifier() + " is banned for " + time3 + " more. Reason: " + ban3.getReason();
                                Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);
                                if (ban3.getExpiry() - System.currentTimeMillis() > 29030400000L) {
                                    message7 = ban3.getIdentifier() + " is permanently banned. Reason: " + ban3.getReason();
                                }
                                this.sendLoginAnswer(false, message7, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
                            }
                            catch (IOException ioe7) {
                                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", " + name + " problem sending IP banned login denied message", ioe7);
                                }
                            }
                            LoginHandler.logger.log(Level.INFO, name + " is banned, trying to log on from " + this.conn.getIp());
                            return;
                        }
                        putOutsideWall(player);
                        if (player.isOnSurface()) {
                            putOutsideHouse(player, false);
                            putOutsideFence(player);
                        }
                        final String message8 = "Welcome to Wurm, " + name + "! " + (Servers.localServer.hasMotd() ? Servers.localServer.getMotd() : Constants.motd);
                        player.getMovementScheme().setPosition(player.getStatus().getPositionX(), player.getStatus().getPositionY(), player.getStatus().getPositionZ(), player.getStatus().getRotation(), player.isOnSurface() ? 0 : -1);
                        final VolaTile targetTile2 = Zones.getTileOrNull((int)(player.getStatus().getPositionX() / 4.0f), (int)(player.getStatus().getPositionY() / 4.0f), player.isOnSurface());
                        if (targetTile2 != null) {
                            final float height2 = (player.getFloorLevel() > 0) ? (player.getFloorLevel() * 3) : 0.0f;
                            player.getMovementScheme().setGroundOffset((int)(height2 * 10.0f), true);
                            player.calculateFloorLevel(targetTile2, true);
                        }
                        player.getStatus().checkStaminaEffects(65535);
                        player.getMovementScheme().haltSpeedModifier();
                        player.setTeleporting(true);
                        player.setTeleportCounter(player.getTeleportCounter() + 1);
                        player.setNewPlayer(true);
                        file.initialize(name, player.getWurmId(), password, "What is your mother's maiden name?", "Sawyer", Server.rand.nextLong(), sendExtraBytes);
                        file.setEmailAddress(name + "@test.com");
                        player.setSaveFile(file);
                        player.setSteamID(SteamId.fromSteamID64(Long.valueOf(steamIDAsString)));
                        if (player.isUndead()) {
                            file.setUndeadData();
                        }
                        ++LoginHandler.logins;
                        player.setLoginHandler(this);
                        this.conn.setConnectionListener(player.getCommunicator());
                        Server.getInstance().addIp(this.conn.getIp());
                        player.setIpaddress(this.conn.getIp());
                        player.setSteamID(SteamId.fromSteamID64(Long.valueOf(steamIDAsString)));
                        player.setFlag(3, true);
                        player.setFlag(53, true);
                        Server.getInstance().addPlayer(player);
                        player.getBody().createBodyParts();
                        player.getLoginhandler().sendLoginAnswer(true, message8, player.getStatus().getPositionX(), player.getStatus().getPositionY(), player.getStatus().getPositionZ(), player.getStatus().getRotation(), player.isOnSurface() ? 0 : -1, player.getModelName(), (byte)0, 0, (byte)0, kingdom2, 0L, player.getTeleportCounter(), player.getBlood(), player.getBridgeId(), player.getMovementScheme().getGroundOffset());
                        final SelectSpawnQuestion question = new SelectSpawnQuestion(player, "Define your character", "Please select gender:", player.getWurmId(), message8, isUndead);
                        question.sendQuestion();
                        if (player.getStatus().getBody().getBodyItem() != null) {
                            player.getStatus().getBody().getBodyItem().addWatcher(-1L, player);
                        }
                        player.setFlag(76, true);
                    }
                    catch (Exception ex4) {
                        LoginHandler.logger.log(Level.WARNING, "Failed to create player with name " + name, ex4);
                    }
                }
            }
        }
    }
    
    private void setHasLoadedItems(final int step) {
        if (LoginHandler.logger.isLoggable(Level.FINER)) {
            LoginHandler.logger.finer("Setting loadedItems to " + step);
        }
        this.loadedItems = step;
    }
    
    int loadPlayer(final Player player, final int step) {
        if (LoginHandler.logger.isLoggable(Level.FINEST)) {
            LoginHandler.logger.finest("Loading " + player + ", step: " + step);
        }
        if (step == 0) {
            player.sendAddChampionPoints();
            player.getSaveFile().frozenSleep = true;
            return step;
        }
        if (step == 1) {
            if (this.loadedItems == 0) {
                final Thread t = new Thread("PlayerLoader-Thread-" + player.getWurmId()) {
                    @Override
                    public void run() {
                        try {
                            player.getBody().load();
                            player.getSaveFile().loadIgnored(player.getWurmId());
                            player.getSaveFile().loadFriends(player.getWurmId());
                            player.getSaveFile().loadTitles(player.getWurmId());
                            player.getSaveFile().loadHistoryIPs(player.getWurmId());
                            player.getSaveFile().loadHistorySteamIds(player.getWurmId());
                            player.getSaveFile().loadHistoryEmails(player.getWurmId());
                            LoginHandler.this.setHasLoadedItems(1);
                        }
                        catch (Exception sex2) {
                            try {
                                LoginHandler.logger.log(Level.WARNING, player.getName() + " has no body. Creating!", sex2);
                                player.getStatus().createNewBody();
                                LoginHandler.this.setHasLoadedItems(1);
                            }
                            catch (Exception sex3) {
                                LoginHandler.logger.log(Level.WARNING, player.getName() + " has no body.", sex3);
                                LoginHandler.this.setHasLoadedItems(-1);
                            }
                        }
                    }
                };
                this.loadedItems = Integer.MAX_VALUE;
                t.setPriority(4);
                t.start();
            }
            if (LoginHandler.logger.isLoggable(Level.FINER)) {
                LoginHandler.logger.finer("Body step=" + step + ", loadedItems=" + this.loadedItems);
            }
            if (this.loadedItems == 1 || this.loadedItems == -1) {
                return this.loadedItems;
            }
            return step - 1;
        }
        else {
            if (step == 2) {
                if (!player.isReallyPaying() && player.hasFlag(8)) {
                    if (player.getPaymentExpire() == 0L) {
                        LoginHandler.logger.log(Level.INFO, player.getName() + " logged on to prevent expiry.");
                    }
                    player.setFlag(8, false);
                }
                return this.loadedItems = 2;
            }
            if (step == 3) {
                putOutsideWall(player);
                if (player.isOnSurface()) {
                    putOutsideHouse(player, true);
                    putOutsideFence(player);
                }
                player.getCommunicator().sendWeather();
                player.getCommunicator().checkSendWeather();
                if (!player.isDead()) {
                    putInBoatAndAssignSeat(player, false);
                }
                return step;
            }
            if (step == 4) {
                player.getMovementScheme().setPosition(player.getStatus().getPositionX(), player.getStatus().getPositionY(), player.getStatus().getPositionZ(), player.getStatus().getRotation(), player.isOnSurface() ? 0 : -1);
                final VolaTile targetTile = Zones.getTileOrNull((int)(player.getStatus().getPositionX() / 4.0f), (int)(player.getStatus().getPositionY() / 4.0f), player.isOnSurface());
                if (targetTile != null) {
                    float height = (player.getFloorLevel() > 0) ? (player.getFloorLevel() * 3) : 0.0f;
                    if (player.getBridgeId() > 0L) {
                        height = 0.0f;
                    }
                    player.getMovementScheme().setGroundOffset((int)(height * 10.0f), true);
                    player.calculateFloorLevel(targetTile, true);
                }
                player.getMovementScheme().haltSpeedModifier();
                try {
                    final String message = "Welcome back, " + player.getName() + "! " + (Servers.localServer.hasMotd() ? Servers.localServer.getMotd() : Constants.motd);
                    player.setTeleporting(true);
                    player.setTeleportCounter(player.getTeleportCounter() + 1);
                    final byte power = (byte)(Players.isArtist(player.getWurmId(), false, false) ? 2 : ((byte)player.getPower()));
                    this.sendLoginAnswer(true, message, player.getStatus().getPositionX(), player.getStatus().getPositionY(), player.getStatus().getPositionZ(), player.getStatus().getRotation(), player.isOnSurface() ? 0 : -1, player.getModelName(), power, 0, (byte)0, player.getKingdomId(), player.getFace(), player.getTeleportCounter(), player.getBlood(), player.getBridgeId(), player.getMovementScheme().getGroundOffset());
                    if (LoginHandler.logger.isLoggable(Level.FINE)) {
                        LoginHandler.logger.log(Level.FINE, player.getName() + ": sent Position X,Y,Z,Rotation: " + player.getStatus().getPositionX() + "," + player.getStatus().getPositionY() + "," + player.getStatus().getPositionZ() + "," + player.getStatus().getRotation());
                    }
                }
                catch (IOException ioe) {
                    LoginHandler.logger.log(Level.FINE, "Player " + player.getName() + " dropped during login.", ioe);
                    return -1;
                }
                sendAllItemModelNames(player);
                sendAllEquippedArmor(player);
                return step;
            }
            if (step == 5) {
                if (!player.isDead() && !willGoOnBoat(player)) {
                    try {
                        player.createVisionArea();
                    }
                    catch (Exception ve) {
                        LoginHandler.logger.log(Level.WARNING, "Failed to create visionarea for player " + player.getName(), ve);
                        return -1;
                    }
                }
                if (!player.hasLink()) {
                    player.destroyVisionArea();
                    return -1;
                }
                return step;
            }
            else {
                if (step == 6) {
                    player.getCommunicator().sendToggle(0, player.isClimbing());
                    player.getCommunicator().sendToggle(2, player.isLegal());
                    player.getCommunicator().sendToggle(1, player.faithful);
                    player.getCommunicator().sendToggle(3, player.isStealth());
                    player.getCommunicator().sendToggle(4, player.isAutofight());
                    if (player.isStealth()) {
                        player.getMovementScheme().setStealthMod(true);
                    }
                    if (player.getShield() != null) {
                        player.getCommunicator().sendToggleShield(true);
                    }
                    if (player.getPower() > 0) {
                        player.getStatus().visible = false;
                        player.getCommunicator().sendNormalServerMessage("You should not be visible now.");
                    }
                    player.sendActionControl("", false, 0);
                    if (!player.isDead()) {
                        player.getCommunicator().sendClimb(player.isClimbing());
                        player.sendToWorld();
                        player.getCommunicator().sendSelfToLocal();
                        player.getCommunicator().sendAllKingdoms();
                        Achievements.sendAchievementList(player);
                        if (player.getVisionArea() != null) {
                            if (this.redirected) {
                                player.getCommunicator().sendAlertServerMessage("You may not move right now.");
                                player.transferCounter = 10;
                            }
                            else {
                                player.getCommunicator().setReady(true);
                            }
                        }
                        final VolaTile tile = player.getCurrentTile();
                        if (tile != null) {
                            final Door[] doors = tile.getDoors();
                            if (doors != null) {
                                for (final Door lDoor : doors) {
                                    if (lDoor.covers(player.getPosX(), player.getPosY(), player.getPositionZ(), player.getFloorLevel(), player.followsGround()) && lDoor.canBeOpenedBy(player, false)) {
                                        if (lDoor instanceof FenceGate) {
                                            player.getCommunicator().sendOpenFence(((FenceGate)lDoor).getFence(), true, true);
                                        }
                                        else {
                                            player.getCommunicator().sendOpenDoor(lDoor);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            LoginHandler.logger.log(Level.WARNING, player.getName() + "- tile is null!", new Exception());
                        }
                    }
                    else {
                        player.getCommunicator().sendDead();
                    }
                    return step;
                }
                if (step == 7) {
                    player.getBody().getBodyItem().addWatcher(-1L, player);
                    return step;
                }
                if (step == 8) {
                    player.getInventory().addWatcher(-1L, player);
                    return step;
                }
                if (step == 9) {
                    setStamina(player);
                    if (player.isDead()) {
                        player.sendSpawnQuestion();
                    }
                    else {
                        player.checkChallengeWarnQuestion();
                    }
                    player.recalcLimitingFactor(null);
                    return step;
                }
                if (step == 10) {
                    player.getBody().loadWounds();
                    return step;
                }
                if (step == 11) {
                    if (player.mayHearDevTalk()) {
                        Players.sendGmMessages(player);
                    }
                    if (player.mayHearMgmtTalk()) {
                        Players.sendMgmtMessages(player);
                    }
                    return step;
                }
                if (step == 12) {
                    player.createSpellEffects();
                    player.addNewbieBuffs();
                    player.getSaveFile().setLogin();
                    player.sendSpellResistances();
                    return step;
                }
                if (step == 13) {
                    sendStatus(player);
                    final Team t2 = Groups.getTeamForOfflineMember(player.getWurmId());
                    if (t2 != null) {
                        player.setTeam(t2, false);
                    }
                    return step;
                }
                if (step == 14) {
                    player.getStatus().sendStateString();
                    player.setBestLightsource(null, true);
                    return step;
                }
                if (step == 15) {
                    if (player.hasLink()) {
                        player.setIpaddress(this.conn.getIp());
                        Server.getInstance().addIp(this.conn.getIp());
                        MissionPerformer.sendEpicMissionsPerformed(player, player.getCommunicator());
                        final MissionPerformer mp = MissionPerformed.getMissionPerformer(player.getWurmId());
                        if (mp != null) {
                            mp.sendAllMissionPerformed(player.getCommunicator());
                        }
                        return step;
                    }
                    return Integer.MAX_VALUE;
                }
                else {
                    if (step == 16) {
                        sendLoggedInPeople(player);
                        player.sendReligion();
                        player.sendKarma();
                        player.sendScenarioKarma();
                        player.setFullyLoaded();
                        if (player.getCultist() != null) {
                            player.getCultist().sendBuffs();
                        }
                        AffinitiesTimed.sendTimedAffinitiesFor(player);
                        if (player.isDeathProtected()) {
                            player.getCommunicator().sendAddStatusEffect(SpellEffectsEnum.DEATH_PROTECTION, Integer.MAX_VALUE);
                        }
                        player.recalcLimitingFactor(null);
                        player.getCommunicator().sendSafeServerMessage("Type /help for available commands.");
                        if (player.isOnHostileHomeServer()) {
                            player.getCommunicator().sendAlertServerMessage("These enemy lands drain you of your confidence. You fight less effectively.");
                        }
                        boolean isEducated = false;
                        for (final Titles.Title t3 : player.getTitles()) {
                            if (t3 == Titles.Title.Educated) {
                                isEducated = true;
                            }
                        }
                        if (!isEducated) {
                            PlayerTutorial.getTutorialForPlayer(player.getWurmId(), true).sendCurrentStageBML();
                        }
                        return step;
                    }
                    if (step == 17) {
                        checkReimbursement(player);
                        if (player.getSaveFile().pet != -10L) {
                            try {
                                final Creature c = Creatures.getInstance().getCreature(player.getSaveFile().pet);
                                if (c.dominator != player.getWurmId()) {
                                    player.getCommunicator().sendNormalServerMessage(c.getNameWithGenus() + " is no longer your pet.");
                                    player.setPet(-10L);
                                }
                            }
                            catch (NoSuchCreatureException nsc) {
                                try {
                                    final Creature c2 = Creatures.getInstance().loadOfflineCreature(player.getSaveFile().pet);
                                    if (c2.dominator != player.getWurmId()) {
                                        if (LoginHandler.logger.isLoggable(Level.FINER)) {
                                            LoginHandler.logger.finer(c2.getName() + "," + c2.getWurmId() + " back from offline - no longer dominated by " + player.getWurmId());
                                        }
                                        player.getCommunicator().sendNormalServerMessage(c2.getNameWithGenus() + " is no longer your pet.");
                                        player.setPet(-10L);
                                    }
                                }
                                catch (NoSuchCreatureException nsc2) {
                                    if (LoginHandler.logger.isLoggable(Level.FINER)) {
                                        LoginHandler.logger.finer("Failed to load from offline to " + player.getSaveFile().pet);
                                    }
                                    player.getCommunicator().sendNormalServerMessage("Your pet is nowhere to be found. It may have died of old age.");
                                    player.setPet(-10L);
                                }
                            }
                        }
                        Creatures.getInstance().returnCreaturesForPlayer(player.getWurmId());
                        if (player.getVisionArea() != null && player.getVisionArea().getSurface() != null) {
                            player.getVisionArea().getSurface().sendCreatureItems(player);
                        }
                        checkPutOnBoat(player);
                        if (!player.checkTileInvulnerability()) {
                            player.getCommunicator().sendAlertServerMessage("You are not invulnerable here.");
                            player.getCommunicator().setInvulnerable(false);
                        }
                        if (player.isStealth()) {
                            player.setStealth(false);
                        }
                        return step;
                    }
                    return Integer.MAX_VALUE;
                }
            }
        }
    }
    
    static int createPlayer(final Player player, final int step) {
        if (LoginHandler.logger.isLoggable(Level.FINEST)) {
            LoginHandler.logger.finest("Creating player " + player + ", step: " + step);
        }
        if (step == 0) {
            try {
                player.loadSkills();
                player.sendSkills();
            }
            catch (Exception ex) {
                LoginHandler.logger.log(Level.INFO, "Failed to create skills: " + ex.getMessage(), ex);
                return -1;
            }
            return step;
        }
        if (step == 1) {
            try {
                player.getBody().createBodyParts();
            }
            catch (Exception ex) {
                LoginHandler.logger.log(Level.INFO, "Failed to create bodyparts: " + ex.getMessage(), ex);
                return -1;
            }
            return step;
        }
        if (step == 2) {
            try {
                player.createPossessions();
            }
            catch (Exception ex) {
                LoginHandler.logger.log(Level.INFO, "Failed to create possessions: " + ex.getMessage(), ex);
                return -1;
            }
            return step;
        }
        if (step == 3) {
            return step;
        }
        if (step == 4) {
            try {
                player.createVisionArea();
            }
            catch (Exception ve) {
                LoginHandler.logger.log(Level.WARNING, "Failed to create visionarea for player " + player.getName(), ve);
                return -1;
            }
            if (!player.hasLink()) {
                player.destroyVisionArea();
                return -1;
            }
            player.getCommunicator().setReady(true);
            return step;
        }
        else {
            if (step == 5) {
                player.createSomeItems(1.0f, false);
                return step;
            }
            if (step == 6) {
                player.setFullyLoaded();
                player.getCommunicator().sendToggle(0, player.isClimbing());
                player.getCommunicator().sendToggle(2, player.isLegal());
                player.getCommunicator().sendToggle(1, player.faithful);
                player.getCommunicator().sendToggle(3, player.isStealth());
                player.getCommunicator().sendToggle(4, player.isAutofight());
                return step;
            }
            if (step == 7) {
                player.sendReligion();
                player.sendKarma();
                player.sendScenarioKarma();
                player.sendToWorld();
                player.getCommunicator().sendWeather();
                player.getCommunicator().checkSendWeather();
                player.getCommunicator().sendSelfToLocal();
                return step;
            }
            if (step == 8) {
                if (!player.isGuest()) {
                    player.getStatus().setStatusExists(true);
                    try {
                        player.save();
                    }
                    catch (Exception ex) {
                        LoginHandler.logger.log(Level.INFO, "Failed to save player: " + ex.getMessage(), ex);
                        return -1;
                    }
                }
                return step;
            }
            if (step == 9) {
                return step;
            }
            if (step == 10) {
                player.createSpellEffects();
                player.getSaveFile().setLogin();
                return step;
            }
            if (step == 11) {
                sendStatus(player);
                return step;
            }
            if (step == 12) {
                player.getStatus().sendStateString();
                return step;
            }
            if (step == 13) {
                sendLoggedInPeople(player);
                MissionPerformer.sendEpicMissionsPerformed(player, player.getCommunicator());
                final MissionPerformer mp = MissionPerformed.getMissionPerformer(player.getWurmId());
                if (mp != null) {
                    mp.sendAllMissionPerformed(player.getCommunicator());
                }
                return step;
            }
            if (step == 14) {
                checkReimbursement(player);
                if (player.isNew()) {
                    final Item mirroritem = player.getCarriedItem(781);
                    if (mirroritem != null) {
                        player.getCommunicator().sendCustomizeFace(player.getFace(), mirroritem.getWurmId());
                    }
                }
                player.setNewPlayer(false);
                if (player.getVisionArea() != null && player.getVisionArea().getSurface() != null) {
                    player.getVisionArea().getSurface().sendCreatureItems(player);
                }
                sendAllEquippedArmor(player);
                sendAllItemModelNames(player);
                boolean isEducated = false;
                for (final Titles.Title t : player.getTitles()) {
                    if (t == Titles.Title.Educated) {
                        isEducated = true;
                    }
                }
                if (!isEducated) {
                    PlayerTutorial.getTutorialForPlayer(player.getWurmId(), true).sendCurrentStageBML();
                }
                return Integer.MAX_VALUE;
            }
            return Integer.MAX_VALUE;
        }
    }
    
    private static void checkReimbursement(final Player player) {
        player.reimburse();
    }
    
    private static void setStamina(final Player player) {
        if (System.currentTimeMillis() - player.getSaveFile().getLastLogin() < 21600000L) {
            player.getStatus().modifyStamina2((System.currentTimeMillis() - player.getSaveFile().lastLogout) / 2.16E7f);
        }
        else {
            player.getStatus().modifyStamina2(1.0f);
            player.getStatus().modifyHunger(-10000, 0.5f);
            player.getStatus().modifyThirst(-10000.0f);
        }
    }
    
    private static boolean creatureIsInsideWrongHouse(final Player player, final boolean load) {
        if (player.getPower() > 1) {
            return false;
        }
        VolaTile startTile = null;
        final int tilex = player.getTileX();
        final int tiley = player.getTileY();
        startTile = Zones.getTileOrNull(tilex, tiley, player.isOnSurface());
        if (startTile != null) {
            final Structure struct = startTile.getStructure();
            if (struct != null && struct.isFinished()) {
                return !struct.mayPass(player);
            }
        }
        return false;
    }
    
    public static boolean putOutsideHouse(final Player player, final boolean load) {
        if (player.getPower() > 1) {
            return false;
        }
        VolaTile startTile = null;
        int tilex = player.getTileX();
        int tiley = player.getTileY();
        startTile = Zones.getTileOrNull(tilex, tiley, player.isOnSurface());
        if (startTile != null) {
            final Structure struct = startTile.getStructure();
            if (struct != null && struct.isFinished() && struct.isTypeHouse()) {
                final Item[] keys2;
                final Item[] keys = keys2 = player.getKeys();
                for (final Item lKey : keys2) {
                    if (lKey.getWurmId() == struct.getWritId()) {
                        return false;
                    }
                }
                if (struct.mayPass(player)) {
                    return false;
                }
                final Door[] allDoors;
                final Door[] doors = allDoors = struct.getAllDoors();
                for (final Door door : allDoors) {
                    if (!door.isLocked() && door.getOuterTile().getStructure() != struct) {
                        return false;
                    }
                }
                for (final Door door : doors) {
                    if (door.getOuterTile().getStructure() != struct) {
                        startTile = door.getOuterTile();
                        break;
                    }
                }
                if (startTile == null) {
                    startTile = Zones.getOrCreateTile(Server.rand.nextBoolean() ? (struct.getMaxX() + 1) : (struct.getMinX() - 1), Server.rand.nextBoolean() ? (struct.getMaxY() + 1) : (struct.getMinY() - 1), true);
                }
                float posX = (startTile.getTileX() << 2) + 2;
                float posY = (startTile.getTileY() << 2) + 2;
                if (Servers.localServer.entryServer) {
                    posX = (startTile.getTileX() << 2) + 0.5f + Server.rand.nextFloat() * 3.0f;
                    posY = (startTile.getTileY() << 2) + 0.5f + Server.rand.nextFloat() * 3.0f;
                }
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.fine("Setting " + player.getName() + " outside structure " + struct.getName() + " on " + startTile.getTileX() + ", " + startTile.getTileY() + ". New or reconnect=" + !load);
                }
                final MountTransfer mt = MountTransfer.getTransferFor(player.getWurmId());
                if (mt != null) {
                    mt.remove(player.getWurmId());
                }
                player.setPositionX(posX);
                player.setPositionY(posY);
                if (player.getVehicle() != -10L) {
                    player.disembark(false);
                }
                try {
                    player.setPositionZ(Zones.calculateHeight(posX, posY, true));
                }
                catch (NoSuchZoneException nsz) {
                    LoginHandler.logger.log(Level.WARNING, player.getName() + " ending up outside map: " + player.getStatus().getPositionX() + ", " + player.getStatus().getPositionY());
                    player.calculateSpawnPoints();
                    if (player.spawnpoints != null) {
                        final Iterator<Spawnpoint> iterator = player.spawnpoints.iterator();
                        if (iterator.hasNext()) {
                            final Spawnpoint p = iterator.next();
                            tilex = p.tilex;
                            tiley = p.tiley;
                            posX = tilex * 4;
                            posY = tiley * 4;
                            player.setPositionX(posX + 2.0f);
                            player.setPositionY(posY + 2.0f);
                            try {
                                player.setPositionZ(Zones.calculateHeight(posX, posY, true));
                            }
                            catch (NoSuchZoneException nsz2) {
                                LoginHandler.logger.log(Level.WARNING, player.getName() + " Respawn failed at spawnpoint " + tilex + "," + tiley);
                            }
                            player.getCommunicator().sendNormalServerMessage("You have been respawned since your position was out of bounds.");
                            return true;
                        }
                    }
                }
                putOutsideEnemyDeed(player, load);
                return true;
            }
        }
        return putOutsideEnemyDeed(player, load);
    }
    
    private static final VolaTile getStartTileForDeed(final Player player) {
        final int tilex = player.getTileX();
        final int tiley = player.getTileY();
        final Village v = Zones.getVillage(tilex, tiley, player.isOnSurface());
        if (v != null && v.isEnemy(player, true)) {
            player.getCommunicator().sendSafeServerMessage("You find yourself outside the " + v.getName() + " settlement.");
            int ntx = v.getEndX() + Server.rand.nextInt(10);
            if (Server.rand.nextBoolean()) {
                ntx = v.getStartX() - Server.rand.nextInt(10);
            }
            int nty = v.getEndY() + Server.rand.nextInt(10);
            if (Server.rand.nextBoolean()) {
                nty = v.getStartY() - Server.rand.nextInt(10);
            }
            VolaTile startTile = Zones.getTileOrNull(ntx, nty, player.isOnSurface());
            if (startTile == null) {
                return Zones.getOrCreateTile(ntx, nty, true);
            }
            Structure struct = startTile.getStructure();
            if (struct == null || !struct.isFinished()) {
                return startTile;
            }
            ntx = v.getEndX() + Server.rand.nextInt(10);
            if (Server.rand.nextBoolean()) {
                ntx = v.getStartX() - Server.rand.nextInt(10);
            }
            nty = v.getStartY() - 10 + Server.rand.nextInt(v.getEndY() + 20 - v.getStartY());
            startTile = Zones.getTileOrNull(ntx, nty, player.isOnSurface());
            if (startTile == null) {
                return Zones.getOrCreateTile(ntx, nty, true);
            }
            struct = startTile.getStructure();
            if (struct == null || !struct.isFinished()) {
                return startTile;
            }
            for (int x = 0; x < 20; ++x) {
                nty = v.getEndY() + Server.rand.nextInt(10);
                if (Server.rand.nextBoolean()) {
                    nty = v.getStartY() - Server.rand.nextInt(10);
                }
                ntx = v.getStartX() - 10 + Server.rand.nextInt(v.getEndX() + 20 - v.getStartX());
                startTile = Zones.getTileOrNull(ntx, nty, player.isOnSurface());
                if (startTile == null) {
                    return Zones.getOrCreateTile(ntx, nty, true);
                }
                final Structure struct2 = startTile.getStructure();
                if (struct2 == null || !struct2.isFinished()) {
                    return startTile;
                }
            }
        }
        return null;
    }
    
    public static boolean putOutsideEnemyDeed(final Player player, final boolean load) {
        if (load && player.getPower() == 0) {
            final VolaTile startTile = getStartTileForDeed(player);
            if (startTile != null) {
                float posX = (startTile.getTileX() << 2) + 2;
                float posY = (startTile.getTileY() << 2) + 2;
                final MountTransfer mt = MountTransfer.getTransferFor(player.getWurmId());
                if (mt != null) {
                    mt.remove(player.getWurmId());
                }
                player.setPositionX(posX);
                player.setPositionY(posY);
                if (player.getVehicle() != -10L) {
                    player.disembark(false);
                }
                try {
                    player.setPositionZ(Zones.calculateHeight(posX, posY, true));
                }
                catch (NoSuchZoneException nsz) {
                    LoginHandler.logger.log(Level.WARNING, player.getName() + " ending up outside map: " + player.getStatus().getPositionX() + ", " + player.getStatus().getPositionY());
                    player.calculateSpawnPoints();
                    if (player.spawnpoints != null) {
                        final Iterator<Spawnpoint> iterator = player.spawnpoints.iterator();
                        if (iterator.hasNext()) {
                            final Spawnpoint p = iterator.next();
                            final int tilex = p.tilex;
                            final int tiley = p.tiley;
                            posX = tilex * 4;
                            posY = tiley * 4;
                            player.setPositionX(posX + 2.0f);
                            player.setPositionY(posY + 2.0f);
                            try {
                                player.setPositionZ(Zones.calculateHeight(posX, posY, true));
                            }
                            catch (NoSuchZoneException nsz2) {
                                LoginHandler.logger.log(Level.WARNING, player.getName() + " Respawn failed at spawnpoint " + tilex + "," + tiley);
                            }
                            player.getCommunicator().sendNormalServerMessage("You have been respawned since your position was out of bounds.");
                            return true;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public static void putOutsideWall(final Player player) {
        if (player.getStatus().getLayer() < 0) {
            int tilex = player.getTileX();
            int tiley = player.getTileY();
            int tile = Server.caveMesh.getTile(tilex, tiley);
            if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                boolean saved = false;
                for (int x = -1; x <= 1; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        tile = Server.caveMesh.getTile(tilex + x, tiley + y);
                        if (!Tiles.isSolidCave(Tiles.decodeType(tile))) {
                            final float posX = (tilex + x) * 4;
                            final float posY = (tiley + y) * 4;
                            player.setPositionX(posX + 2.0f);
                            player.setPositionY(posY + 2.0f);
                            saved = true;
                            break;
                        }
                    }
                }
                if (!saved) {
                    player.setLayer(0, false);
                }
                try {
                    player.setPositionZ(Zones.calculateHeight(player.getStatus().getPositionX(), player.getStatus().getPositionY(), player.isOnSurface()));
                }
                catch (NoSuchZoneException nsz) {
                    LoginHandler.logger.log(Level.WARNING, player.getName() + " ending up outside map: " + player.getStatus().getPositionX() + ", " + player.getStatus().getPositionY() + ". Respawning.");
                    player.calculateSpawnPoints();
                    if (player.spawnpoints != null) {
                        final Iterator<Spawnpoint> iterator = player.spawnpoints.iterator();
                        if (iterator.hasNext()) {
                            final Spawnpoint p = iterator.next();
                            tilex = p.tilex;
                            tiley = p.tiley;
                            final float posX2 = tilex * 4;
                            final float posY2 = tiley * 4;
                            player.setPositionX(posX2 + 2.0f);
                            player.setPositionY(posY2 + 2.0f);
                            try {
                                player.setPositionZ(Zones.calculateHeight(posX2, posY2, true));
                            }
                            catch (NoSuchZoneException nsz2) {
                                LoginHandler.logger.log(Level.WARNING, player.getName() + " Respawn failed at spawnpoint " + tilex + "," + tiley);
                            }
                            player.getCommunicator().sendNormalServerMessage("You have been respawned since your position was out of bounds.");
                        }
                    }
                }
            }
        }
    }
    
    public static boolean putOutsideFence(final Player player) {
        final boolean moved = true;
        int tilex = player.getTileX();
        int tiley = player.getTileY();
        float posX = tilex * 4;
        float posY = tiley * 4;
        if (player.getBridgeId() <= 0L) {
            posX = posX + 0.5f + Server.rand.nextFloat() * 3.0f;
            posY = posY + 0.5f + Server.rand.nextFloat() * 3.0f;
        }
        else {
            posX += 2.0f;
            posY += 2.0f;
        }
        player.setPositionX(posX);
        player.setPositionY(posY);
        if (player.getFloorLevel() <= 0) {
            try {
                player.setPositionZ(Zones.calculateHeight(posX, posY, true));
            }
            catch (NoSuchZoneException nsz) {
                LoginHandler.logger.log(Level.WARNING, player.getName() + " ending up outside map: " + player.getStatus().getPositionX() + ", " + player.getStatus().getPositionY() + ". Respawning.");
                player.calculateSpawnPoints();
                if (player.spawnpoints != null) {
                    final Iterator<Spawnpoint> iterator = player.spawnpoints.iterator();
                    if (iterator.hasNext()) {
                        final Spawnpoint p = iterator.next();
                        tilex = p.tilex;
                        tiley = p.tiley;
                        posX = tilex * 4;
                        posY = tiley * 4;
                        player.setPositionX(posX + 2.0f);
                        player.setPositionY(posY + 2.0f);
                        try {
                            player.setPositionZ(Zones.calculateHeight(posX, posY, true));
                        }
                        catch (NoSuchZoneException nsz2) {
                            LoginHandler.logger.log(Level.WARNING, player.getName() + " Respawn failed at spawnpoint " + tilex + "," + tiley);
                        }
                        player.getCommunicator().sendNormalServerMessage("You have been respawned since your position was out of bounds.");
                        return true;
                    }
                }
            }
        }
        return true;
    }
    
    public static final boolean willGoOnBoat(final Player player) {
        final MountTransfer mt = MountTransfer.getTransferFor(player.getWurmId());
        if (mt != null) {
            final long vehicleId = mt.getVehicleId();
            final Vehicle vehic = Vehicles.getVehicleForId(vehicleId);
            if (vehic != null) {
                if (!vehic.creature) {
                    try {
                        final Item i = Items.getItem(vehicleId);
                        if (i.isBoat()) {
                            return true;
                        }
                    }
                    catch (Exception ex) {
                        LoginHandler.logger.log(Level.WARNING, "Failed to locate boat with id " + vehicleId + " for player " + player.getName(), ex);
                    }
                }
                else {
                    try {
                        Creatures.getInstance().getCreature(vehicleId);
                        return true;
                    }
                    catch (Exception ex) {
                        LoginHandler.logger.log(Level.WARNING, "Failed to locate creature with id " + vehicleId + " for player " + player.getName(), ex);
                    }
                }
            }
        }
        return false;
    }
    
    public static final boolean putInBoatAndAssignSeat(final Player player, final boolean reconnect) {
        MountTransfer mt = MountTransfer.getTransferFor(player.getWurmId());
        if (mt == null && (player.getVehicle() == -10L || reconnect)) {
            long vehicleId = player.getSaveFile().lastvehicle;
            if (reconnect) {
                vehicleId = player.getVehicle();
            }
            final Vehicle vehic = Vehicles.getVehicleForId(vehicleId);
            if (vehic != null) {
                try {
                    Item i = null;
                    Creature creature = null;
                    int freeseatnum = -1;
                    float offz = 0.0f;
                    float offx = 0.0f;
                    float offy = 0.0f;
                    int start = 9999;
                    float posx = 50.0f;
                    float posy = 50.0f;
                    int layer = 0;
                    if (WurmId.getType(vehicleId) == 2) {
                        i = Items.getItem(vehicleId);
                        if (!reconnect && !i.isBoat()) {
                            return false;
                        }
                        posx = i.getPosX();
                        posy = i.getPosY();
                        layer = (i.isOnSurface() ? 0 : -1);
                        if ((VehicleBehaviour.hasKeyForVehicle(player, i) || VehicleBehaviour.mayDriveVehicle(player, i, null)) && VehicleBehaviour.canBeDriverOfVehicle(player, vehic)) {
                            start = 1;
                        }
                        else if (VehicleBehaviour.hasKeyForVehicle(player, i) || VehicleBehaviour.mayEmbarkVehicle(player, i)) {
                            start = 1;
                        }
                        else {
                            LoginHandler.logger.log(Level.INFO, player.getName() + " may no longer embark the vehicle " + i.getName());
                        }
                    }
                    else if (WurmId.getType(vehicleId) == 1) {
                        creature = Creatures.getInstance().getCreature(vehicleId);
                        posx = creature.getPosX();
                        posy = creature.getPosY();
                        layer = creature.getLayer();
                        if (VehicleBehaviour.mayDriveVehicle(player, creature) && VehicleBehaviour.canBeDriverOfVehicle(player, vehic)) {
                            start = 1;
                        }
                        else if (VehicleBehaviour.mayEmbarkVehicle(player, creature)) {
                            start = 1;
                        }
                        else {
                            LoginHandler.logger.log(Level.INFO, player.getName() + " may no longer mount the " + creature.getName());
                        }
                    }
                    for (int x = 0; x < vehic.seats.length; ++x) {
                        if (vehic.seats[x].occupant == player.getWurmId()) {
                            freeseatnum = x;
                            offz = vehic.seats[x].offz;
                            offy += vehic.seats[x].offy;
                            offx += vehic.seats[x].offx;
                        }
                    }
                    if (freeseatnum < 0) {
                        for (int x = start; x < vehic.seats.length; ++x) {
                            if (vehic.seats[x].occupant == -10L && freeseatnum < 0) {
                                freeseatnum = x;
                                offz = vehic.seats[x].offz;
                                offy += vehic.seats[x].offy;
                                offx += vehic.seats[x].offx;
                            }
                        }
                    }
                    player.setPositionX(posx + offx);
                    player.setPositionY(posy + offy);
                    final VolaTile tile = Zones.getOrCreateTile((int)(player.getPosX() / 4.0f), (int)(player.getPosY() / 4.0f), player.getLayer() >= 0);
                    boolean skipSetZ = false;
                    if (tile != null) {
                        final Structure structure = tile.getStructure();
                        if (structure != null) {
                            skipSetZ = (structure.isTypeHouse() || structure.getWurmId() == player.getBridgeId());
                        }
                    }
                    if (!skipSetZ) {
                        player.setPositionZ(Math.max(Zones.calculateHeight(posx + offx, posy + offy, layer >= 0) + offz, (freeseatnum >= 0) ? offz : -1.45f));
                    }
                    if (freeseatnum >= 0) {
                        mt = new MountTransfer(vehicleId, (freeseatnum == 0) ? player.getWurmId() : -10L);
                        mt.addToSeat(player.getWurmId(), freeseatnum);
                    }
                    return true;
                }
                catch (NoSuchItemException nsi) {
                    LoginHandler.logger.log(Level.WARNING, "No item to board for " + player.getName() + ":" + vehicleId, nsi);
                }
                catch (Exception ex) {
                    LoginHandler.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return false;
    }
    
    public static final boolean checkPutOnBoat(final Player player) {
        final MountTransfer mt = MountTransfer.getTransferFor(player.getWurmId());
        if (mt != null) {
            final long vehicleId = mt.getVehicleId();
            final Vehicle vehic = Vehicles.getVehicleForId(vehicleId);
            if (vehic != null) {
                if (vehic.isChair()) {
                    return false;
                }
                try {
                    Item i = null;
                    Creature creature = null;
                    if (WurmId.getType(vehicleId) == 2) {
                        i = Items.getItem(vehicleId);
                    }
                    else if (WurmId.getType(vehicleId) == 1) {
                        creature = Creatures.getInstance().getCreature(vehicleId);
                    }
                    final int seatnum = mt.getSeatFor(player.getWurmId());
                    if (seatnum >= 0) {
                        vehic.seats[seatnum].occupant = player.getWurmId();
                        if (mt.getPilotId() == player.getWurmId()) {
                            vehic.pilotId = player.getWurmId();
                            player.setVehicleCommander(true);
                        }
                        final MountAction m = new MountAction(creature, i, vehic, seatnum, mt.getPilotId() == player.getWurmId(), vehic.seats[seatnum].offz);
                        player.setMountAction(m);
                        player.setVehicle(vehicleId, false, vehic.seats[seatnum].getType());
                        return true;
                    }
                }
                catch (NoSuchItemException nsi) {
                    LoginHandler.logger.log(Level.WARNING, "No item to board for " + player.getName() + ":" + vehicleId, nsi);
                }
                catch (NoSuchCreatureException nsc) {
                    LoginHandler.logger.log(Level.WARNING, "No creature to mount for " + player.getName() + ":" + vehicleId, nsc);
                }
                catch (Exception ex) {
                    LoginHandler.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return false;
    }
    
    public static void sendWho(final Player player, final boolean loggingin) {
        if (player.isUndead()) {
            return;
        }
        final String[] names = Players.getInstance().getPlayerNames();
        String playerList = "none!";
        final Communicator comm = player.getCommunicator();
        int otherServers = 0;
        String localServerName = Servers.localServer.name;
        if (localServerName.length() > 1) {
            localServerName = localServerName.toLowerCase();
            localServerName = Character.toUpperCase(localServerName.charAt(0)) + localServerName.substring(1);
        }
        int epic = 0;
        for (final ServerEntry entry : Servers.getAllServers()) {
            if (!entry.EPIC) {
                if (!entry.isLocal) {
                    otherServers += entry.currentPlayers;
                }
            }
            else {
                epic += entry.currentPlayers;
            }
        }
        if (player.getPower() > 0) {
            comm.sendSafeServerMessage("These other players are online on " + localServerName + ":");
            int nums = names.length;
            if (names.length > 1) {
                playerList = "";
                for (int x = 0; x < names.length; ++x) {
                    if (!names[x].equals(player.getName())) {
                        final PlayerInfo p = PlayerInfoFactory.createPlayerInfo(names[x]);
                        if (player.getPower() >= p.getPower()) {
                            playerList = playerList + names[x] + " ";
                        }
                        else {
                            --nums;
                        }
                    }
                    if (x != 0 && x % 10 == 0) {
                        comm.sendSafeServerMessage(playerList);
                        playerList = "";
                    }
                }
                if (playerList.length() > 0) {
                    comm.sendSafeServerMessage(playerList);
                }
                String ss = "";
                if (names.length > 1) {
                    ss = "s";
                }
                comm.sendSafeServerMessage(nums + " player" + ss + " on this server. (" + (nums + otherServers + epic) + " totally in Wurm)");
            }
            else {
                comm.sendSafeServerMessage("none! (" + (nums + otherServers + epic) + " totally in Wurm)");
            }
        }
        else if (names.length > 1) {
            comm.sendSafeServerMessage(names.length - 1 + " other players are online. You are on " + localServerName + " (" + (names.length + otherServers + epic) + " totally in Wurm).");
        }
        else {
            comm.sendSafeServerMessage("No other players are online on " + localServerName + " (" + (1 + otherServers) + " totally in Wurm).");
        }
    }
    
    private static void sendLoggedInPeople(final Player player) {
        if (player.isUndead()) {
            return;
        }
        if (player.isSignedIn()) {
            player.getCommunicator().signIn("Just transferred.");
        }
        else if (player.canSignIn() && player.getPower() <= 1) {
            player.getCommunicator().remindToSignIn();
        }
        sendWho(player, true);
        final Village vill = player.getCitizenVillage();
        if (vill != null) {
            vill.sendCitizensToPlayer(player);
        }
        if (player.mayHearMgmtTalk() || player.mayHearDevTalk()) {
            Players.getInstance().sendGmsToPlayer(player);
        }
        if (player.seesPlayerAssistantWindow()) {
            Players.getInstance().sendPAWindow(player);
        }
        if (player.seesGVHelpWindow() && !Servers.isThisLoginServer()) {
            Players.getInstance().sendGVHelpWindow(player);
        }
        Players.getInstance().sendAltarsToPlayer(player);
        Players.getInstance().sendTicketsToPlayer(player);
        player.checkKingdom();
        if (player.isGlobalChat()) {
            Players.getInstance().sendStartGlobalKingdomChat(player);
        }
        if (player.isKingdomChat()) {
            Players.getInstance().sendStartKingdomChat(player);
        }
        if (player.isTradeChannel()) {
            Players.getInstance().sendStartGlobalTradeChannel(player);
        }
    }
    
    private static void sendStatus(final Player player) {
        player.getStatus().sendHunger();
        player.getStatus().sendThirst();
        player.getStatus().lastSentStamina = -200;
        player.getStatus().sendStamina();
        player.sendDeityEffectBonuses();
        player.getCommunicator().sendOwnTitles();
        player.getCommunicator().sendSleepInfo();
        player.sendAllPoisonEffect();
        Abilities.sendEffectsToCreature(player);
    }
    
    private void sendLoginAnswer(final boolean ok, final String message, final float x, final float y, final float z, final float rot, final int layer, final String bodyName, final byte power, final int retrySeconds) throws IOException {
        this.sendLoginAnswer(ok, message, x, y, z, rot, layer, bodyName, power, retrySeconds, (byte)0, (byte)0, 0L, 0, (byte)0, -10L, 0.0f);
    }
    
    public void sendLoginAnswer(final boolean ok, final String message, final float x, final float y, final float z, final float rot, final int layer, final String bodyName, final byte power, final int retrySeconds, final byte commandType, final byte templateKingdomId, final long face, final int teleportCounter, final byte blood, final long bridgeId, final float groundOffset) throws IOException {
        try {
            if (Constants.useQueueToSendDataToPlayers) {}
            final byte[] messageb = message.getBytes("UTF-8");
            final ByteBuffer bb = this.conn.getBuffer();
            bb.put((byte)(-15));
            if (ok) {
                bb.put((byte)1);
            }
            else {
                bb.put((byte)0);
            }
            bb.putShort((short)messageb.length);
            bb.put(messageb);
            bb.put((byte)layer);
            bb.putLong(WurmCalendar.currentTime);
            bb.putLong(System.currentTimeMillis());
            bb.putFloat(rot);
            bb.putFloat(x);
            bb.putFloat(y);
            bb.putFloat(z);
            final byte[] bodyb = bodyName.getBytes("UTF-8");
            bb.putShort((short)bodyb.length);
            bb.put(bodyb);
            if (power == 0) {
                bb.put((byte)0);
            }
            else if (power == 1) {
                bb.put((byte)2);
            }
            else {
                bb.put((byte)1);
            }
            bb.put(commandType);
            bb.putShort((short)retrySeconds);
            bb.putLong(face);
            bb.put(templateKingdomId);
            bb.putInt(teleportCounter);
            bb.put(blood);
            bb.putLong(bridgeId);
            bb.putFloat(groundOffset);
            bb.putInt(Zones.worldTileSizeX);
            this.conn.flush();
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception ex) {
            LoginHandler.logger.log(Level.WARNING, "Failed to send login answer.", ex);
        }
    }
    
    public void sendAuthenticationAnswer(final boolean wasSucces, final String failedMessage) {
        final ByteBuffer bb = this.conn.getBuffer();
        bb.put((byte)(-52));
        if (wasSucces) {
            bb.put((byte)1);
        }
        else {
            bb.put((byte)0);
        }
        try {
            final byte[] failedMessageb = failedMessage.getBytes("UTF-8");
            bb.putShort((short)failedMessageb.length);
            bb.put(failedMessageb);
        }
        catch (Exception e) {
            bb.putShort((short)0);
        }
        try {
            this.conn.flush();
        }
        catch (Exception ex) {
            LoginHandler.logger.log(Level.WARNING, "Failed to send Auth answer.", ex);
        }
    }
    
    public static byte[] createAndReturnPlayer(final String name, final String password, final String pwQuestion, final String pwAnswer, final String email, final byte kingdom, final byte power, final long appearance, final byte gender, final boolean titleKeeper, final boolean addPremium, final boolean passwordIsHashed) throws Exception {
        return createAndReturnPlayer(name, password, pwQuestion, pwAnswer, email, kingdom, power, appearance, gender, titleKeeper, addPremium, passwordIsHashed, -10L);
    }
    
    public static byte[] createAndReturnPlayer(String name, String password, final String pwQuestion, final String pwAnswer, final String email, final byte kingdom, final byte power, final long appearance, final byte gender, final boolean titleKeeper, final boolean addPremium, final boolean passwordIsHashed, final long wurmId) throws Exception {
        if (Servers.localServer.HOMESERVER && Servers.localServer.KINGDOM != kingdom) {
            throw new WurmServerException("Illegal kingdom");
        }
        name = raiseFirstLetter(name);
        if (!passwordIsHashed) {
            try {
                password = hashPassword(password, encrypt(name));
            }
            catch (Exception ex) {
                throw new WurmServerException("We failed to encrypt your password. Please try another.");
            }
        }
        if (wurmId < 0L) {
            final String result = checkName2(name);
            if (result.length() > 0) {
                throw new WurmServerException(result);
            }
            if (Players.getInstance().getWurmIdByPlayerName(name) != -1L) {
                throw new WurmServerException("That name is taken.");
            }
        }
        final Player player = Player.doNewPlayer(1);
        player.setName(name);
        int startx = Servers.localServer.SPAWNPOINTJENNX;
        int starty = Servers.localServer.SPAWNPOINTJENNY;
        final Spawnpoint spawn = getInitialSpawnPoint(kingdom);
        if (spawn != null) {
            startx = spawn.tilex;
            starty = spawn.tiley;
        }
        else if (kingdom == 3) {
            if (Servers.localServer.SPAWNPOINTLIBX > 0) {
                startx = Servers.localServer.SPAWNPOINTLIBX;
                starty = Servers.localServer.SPAWNPOINTLIBY;
            }
        }
        else if (kingdom == 2 && Servers.localServer.SPAWNPOINTMOLX > 0) {
            startx = Servers.localServer.SPAWNPOINTMOLX;
            starty = Servers.localServer.SPAWNPOINTMOLY;
        }
        if (Servers.localServer.id == 5) {
            startx = 2884;
            starty = 3004;
        }
        final float posX = startx * 4 + Server.rand.nextFloat() * 2.0f * 4.0f - 4.0f;
        final float posY = starty * 4 + Server.rand.nextFloat() * 2.0f * 4.0f - 4.0f;
        final float rot = Server.rand.nextInt(45) - 22.5f;
        if (wurmId < 0L) {
            player.setWurmId(WurmId.getNextPlayerId(), posX, posY, rot, 0);
        }
        else {
            player.setWurmId(wurmId, posX, posY, rot, 0);
        }
        putOutsideWall(player);
        if (player.isOnSurface()) {
            putOutsideHouse(player, false);
            putOutsideFence(player);
        }
        player.setNewPlayer(true);
        final PlayerInfo file = PlayerInfoFactory.createPlayerInfo(name);
        file.initialize(name, player.getWurmId(), password, pwQuestion, pwAnswer, appearance, false);
        player.getStatus().setStatusExists(true);
        file.setEmailAddress(email);
        file.loaded = true;
        player.setSaveFile(file);
        file.togglePlayerAssistantWindow(true);
        player.loadSkills();
        player.getBody().createBodyParts();
        player.createPossessions();
        player.createSomeItems(1.0f, false);
        player.setPower(power);
        checkReimbursement(player);
        player.setSex(gender, true);
        player.getStatus().setKingdom(kingdom);
        player.setFlag(53, true);
        player.setFlag(76, true);
        Players.loadAllPrivatePOIForPlayer(player);
        player.sendAllMapAnnotations();
        ValreiMapData.sendAllMapData(player);
        player.setFullyLoaded();
        if (power > 0) {
            file.setReimbursed(false);
        }
        if (titleKeeper) {
            if (kingdom == 3) {
                player.addTitle(Titles.Title.Destroyer_Faith);
            }
            else {
                player.addTitle(Titles.Title.Keeper_Faith);
            }
        }
        if (addPremium) {
            file.setPaymentExpire(System.currentTimeMillis());
        }
        player.sleep();
        PlayerInfoFactory.addPlayerInfo(file);
        Server.addNewbie();
        return PlayerTransfer.createPlayerData(Wounds.emptyWounds, player.getSaveFile(), player.getStatus(), player.getAllItems(), player.getSkills().getSkillsNoTemp(), null, Servers.localServer.id, 0L, kingdom);
    }
    
    public static final Spawnpoint getInitialSpawnPoint(final byte kingdom) {
        if (!Servers.localServer.entryServer || Server.getInstance().isPS()) {
            final Village[] villages = Villages.getPermanentVillages(kingdom);
            if (villages.length > 0) {
                final Village chosen = villages[Server.rand.nextInt(villages.length)];
                return new Spawnpoint(chosen.getName(), (byte)1, chosen.getMotto(), (short)chosen.getTokenX(), (short)chosen.getTokenY(), true, chosen.kingdom);
            }
        }
        return null;
    }
    
    public static long createPlayer(final String name, String password, final String pwQuestion, final String pwAnswer, final String email, final byte kingdom, final byte power, final long appearance, final byte gender) throws Exception {
        try {
            password = hashPassword(password, encrypt(raiseFirstLetter(name)));
        }
        catch (Exception ex) {
            throw new WurmServerException("We failed to encrypt your password. Please try another.");
        }
        return createPlayer(name, password, pwQuestion, pwAnswer, email, kingdom, power, appearance, gender, false, false, -10L);
    }
    
    public static long createPlayer(String name, final String hashedPassword, final String pwQuestion, final String pwAnswer, final String email, byte kingdom, final byte power, final long appearance, final byte gender, final boolean titleKeeper, final boolean addPremium, final long wurmId) throws Exception {
        if (Servers.localServer.HOMESERVER && Servers.localServer.KINGDOM != kingdom) {
            kingdom = Servers.localServer.KINGDOM;
        }
        name = raiseFirstLetter(name);
        if (wurmId < 0L) {
            final String result = checkName2(name);
            if (result.length() > 0) {
                throw new WurmServerException(result);
            }
            if (Players.getInstance().getWurmIdByPlayerName(name) != -1L) {
                throw new WurmServerException("That name is taken.");
            }
        }
        final Player player = Player.doNewPlayer(1);
        player.setName(name);
        int startx = Servers.localServer.SPAWNPOINTJENNX;
        int starty = Servers.localServer.SPAWNPOINTJENNY;
        final Spawnpoint spawn = getInitialSpawnPoint(kingdom);
        if (spawn != null) {
            startx = spawn.tilex;
            starty = spawn.tiley;
        }
        else if (kingdom == 3) {
            if (Servers.localServer.SPAWNPOINTLIBX > 0) {
                startx = Servers.localServer.SPAWNPOINTLIBX;
                starty = Servers.localServer.SPAWNPOINTLIBY;
            }
        }
        else if (kingdom == 2 && Servers.localServer.SPAWNPOINTMOLX > 0) {
            startx = Servers.localServer.SPAWNPOINTMOLX;
            starty = Servers.localServer.SPAWNPOINTMOLY;
        }
        final float posX = startx * 4 + Server.rand.nextFloat() * 2.0f * 4.0f - 4.0f;
        final float posY = starty * 4 + Server.rand.nextFloat() * 2.0f * 4.0f - 4.0f;
        final float rot = 4.0f;
        if (wurmId < 0L) {
            player.setWurmId(WurmId.getNextPlayerId(), posX, posY, 4.0f, 0);
        }
        else {
            player.setWurmId(wurmId, posX, posY, 4.0f, 0);
        }
        Players.getInstance().addPlayer(player);
        player.setNewPlayer(true);
        final PlayerInfo file = PlayerInfoFactory.createPlayerInfo(name);
        file.initialize(name, player.getWurmId(), hashedPassword, pwQuestion, pwAnswer, appearance, false);
        player.getStatus().setStatusExists(true);
        file.loaded = true;
        player.setSaveFile(file);
        file.togglePlayerAssistantWindow(true);
        file.setEmailAddress(email);
        putOutsideWall(player);
        if (player.isOnSurface()) {
            putOutsideHouse(player, false);
            putOutsideFence(player);
        }
        player.loadSkills();
        player.createPossessions();
        player.getBody().createBodyParts();
        player.createSomeItems(1.0f, false);
        player.setPower(power);
        checkReimbursement(player);
        player.setSex(gender, true);
        player.getStatus().setKingdom(kingdom);
        player.setFlag(53, true);
        player.setFlag(76, true);
        Players.loadAllPrivatePOIForPlayer(player);
        player.sendAllMapAnnotations();
        ValreiMapData.sendAllMapData(player);
        final Kingdom k = Kingdoms.getKingdom(kingdom);
        if (k != null) {
            if (k.isCustomKingdom()) {
                player.calculateSpawnPoints();
                final Set<Spawnpoint> spawns = player.spawnpoints;
                if (spawns != null) {
                    for (final Spawnpoint sp : spawns) {
                        if (sp.tilex > 20 && sp.tilex < Zones.worldTileSizeX - 20 && sp.tiley > 20 && sp.tiley < Zones.worldTileSizeY - 20) {
                            final float nposX = sp.tilex * 4 + 1 + Server.rand.nextFloat() * 2.0f;
                            final float nposY = sp.tiley * 4 + 1 + Server.rand.nextFloat() * 2.0f;
                            player.getStatus().setPositionXYZ(nposX, nposY, Zones.calculateHeight(nposX, nposY, true));
                            break;
                        }
                    }
                }
            }
            else {
                if (kingdom == 3) {
                    player.setDeity(Deities.getDeity(4));
                    player.setFaith(1.0f);
                }
                if (Servers.localServer.entryServer && Players.getInstance().getNumberOfPlayers() > 100) {
                    player.calculateSpawnPoints();
                    final Set<Spawnpoint> spawns = player.spawnpoints;
                    if (spawns != null && spawns.size() > 0) {
                        final int rand = Server.rand.nextInt(spawns.size());
                        int current = 0;
                        for (final Spawnpoint sp2 : spawns) {
                            if (rand == current) {
                                startx = sp2.tilex;
                                starty = sp2.tiley;
                                final float posNX = startx * 4 + Server.rand.nextFloat() * 2.0f * 4.0f - 4.0f;
                                final float posNY = starty * 4 + Server.rand.nextFloat() * 2.0f * 4.0f - 4.0f;
                                player.getStatus().getPosition().setPosX(posNX);
                                player.getStatus().getPosition().setPosY(posNY);
                                player.updateEffects();
                                break;
                            }
                            ++current;
                        }
                    }
                }
            }
        }
        player.setFullyLoaded();
        if (power > 0) {
            file.setReimbursed(false);
        }
        if (titleKeeper) {
            if (kingdom == 3) {
                player.addTitle(Titles.Title.Destroyer_Faith);
            }
            else {
                player.addTitle(Titles.Title.Keeper_Faith);
            }
        }
        if (addPremium) {
            file.setPaymentExpire(System.currentTimeMillis());
        }
        player.sleep();
        PlayerInfoFactory.addPlayerInfo(file);
        Server.addNewbie();
        Players.getInstance().removePlayer(player);
        return player.getWurmId();
    }
    
    private boolean checkName(final String name) {
        final String result = checkName2(name);
        if (result.length() > 0) {
            try {
                this.sendLoginAnswer(false, result, 0.0f, 0.0f, 0.0f, 0.0f, 0, "model.player.broken", (byte)0, 0);
            }
            catch (IOException ioe) {
                if (LoginHandler.logger.isLoggable(Level.FINE)) {
                    LoginHandler.logger.log(Level.FINE, this.conn.getIp() + ", problem sending login denied message: " + result, ioe);
                }
            }
            return false;
        }
        return true;
    }
    
    private static void sendAllItemModelNames(final Player player) {
        for (final ItemTemplate item : ItemTemplateFactory.getInstance().getTemplates()) {
            if (!item.isNoTake()) {
                player.getCommunicator().sendItemTemplateList(item.getTemplateId(), item.getModelName());
            }
        }
    }
    
    private static void sendAllEquippedArmor(final Player player) {
        for (final Item item : player.getBody().getContainersAndWornItems()) {
            if (item != null) {
                try {
                    final byte armorSlot = item.isArmour() ? BodyTemplate.convertToArmorEquipementSlot((byte)item.getParent().getPlace()) : BodyTemplate.convertToItemEquipementSlot((byte)item.getParent().getPlace());
                    player.getCommunicator().sendWearItem(-1L, item.getTemplateId(), armorSlot, WurmColor.getColorRed(item.getColor()), WurmColor.getColorGreen(item.getColor()), WurmColor.getColorBlue(item.getColor()), WurmColor.getColorRed(item.getColor2()), WurmColor.getColorGreen(item.getColor2()), WurmColor.getColorBlue(item.getColor2()), item.getMaterial(), item.getRarity());
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public static final String checkName2(final String name) {
        final boolean notok = containsIllegalCharacters(name);
        if (notok) {
            return "Please use only letters from a to z in your name.";
        }
        if (name.length() < 3) {
            return "Please use a name at least 3 letters long.";
        }
        if (name.length() > 40) {
            return "Please use a name no longer than 40 letters.";
        }
        if (!Deities.isNameOkay(name) || !CreatureTemplateFactory.isNameOkay(name)) {
            return "Illegal name.";
        }
        return "";
    }
    
    public static String hashPassword(final String password, final String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final char[] passwordChars = password.toCharArray();
        final byte[] saltBytes = salt.getBytes();
        final PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, 1000, 192);
        final SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final byte[] hashedPassword = key.generateSecret(spec).getEncoded();
        return String.format("%x", new BigInteger(hashedPassword));
    }
    
    public static String encrypt(final String plaintext) throws Exception {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        }
        catch (NoSuchAlgorithmException e) {
            throw new WurmServerException("No such algorithm 'SHA'", e);
        }
        try {
            md.update(plaintext.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e2) {
            throw new WurmServerException("No such encoding: UTF-8", e2);
        }
        final byte[] raw = md.digest();
        final String hash = new BASE64Encoder().encode(raw);
        return hash;
    }
    
    public String getConnectionIp() {
        if (this.conn != null) {
            return this.conn.getIp();
        }
        return "";
    }
    
    static {
        LoginHandler.logger = Logger.getLogger(LoginHandler.class.getName());
        LoginHandler.redirects = 0;
        LoginHandler.logins = 0;
        failedIps = new HashMap<String, HackerIp>();
    }
}
