// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.epic.EpicEntity;
import com.wurmonline.server.webinterface.WcCreateEpicMission;
import com.wurmonline.server.webinterface.WebCommand;
import java.util.HashMap;
import com.wurmonline.server.players.Ban;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.players.Player;
import java.util.Iterator;
import java.util.Map;
import com.wurmonline.server.creatures.Creature;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.rmi.Naming;
import java.rmi.RemoteException;
import com.wurmonline.server.webinterface.WebInterfaceImpl;
import java.util.logging.Logger;
import com.wurmonline.server.webinterface.WebInterface;

public final class LoginServerWebConnection
{
    private WebInterface wurm;
    private static Logger logger;
    private int serverId;
    private static final char EXCLAMATION_MARK = '!';
    private static final String FAILED_TO_CREATE_TRINKET = ", failed to create trinket! ";
    private static final String YOU_RECEIVED = "You received ";
    private static final String AN_ERROR_OCCURRED_WHEN_CONTACTING_THE_LOGIN_SERVER = "An error occurred when contacting the login server. Please try later.";
    private static final String FAILED_TO_CONTACT_THE_LOGIN_SERVER = "Failed to contact the login server ";
    private static final String FAILED_TO_CONTACT_THE_LOGIN_SERVER_PLEASE_TRY_LATER = "Failed to contact the login server. Please try later.";
    private static final String FAILED_TO_CONTACT_THE_BANK_PLEASE_TRY_LATER = "Failed to contact the bank. Please try later.";
    private static final String GAME_SERVER_IS_CURRENTLY_UNAVAILABLE = "The game server is currently unavailable.";
    private static final char COLON_CHAR = ':';
    private String intraServerPassword;
    static final int[] failedIntZero;
    
    public LoginServerWebConnection() {
        this.wurm = null;
        this.serverId = Servers.loginServer.id;
        this.intraServerPassword = Servers.localServer.INTRASERVERPASSWORD;
    }
    
    public LoginServerWebConnection(final int aServerId) {
        this.wurm = null;
        this.serverId = Servers.loginServer.id;
        this.intraServerPassword = Servers.localServer.INTRASERVERPASSWORD;
        this.serverId = aServerId;
    }
    
    private void connect() throws MalformedURLException, RemoteException, NotBoundException {
        if (this.wurm == null) {
            if (Servers.localServer.id == this.serverId) {
                this.wurm = new WebInterfaceImpl();
            }
            else {
                final long lStart = System.nanoTime();
                String name = null;
                try {
                    final ServerEntry server = Servers.getServerWithId(this.serverId);
                    if (server == null) {
                        throw new RemoteException("Server " + this.serverId + " not found");
                    }
                    if (!server.isAvailable(5, true)) {
                        throw new RemoteException("Server unavailable");
                    }
                    this.intraServerPassword = server.INTRASERVERPASSWORD;
                    name = "//" + server.INTRASERVERADDRESS + ':' + server.RMI_PORT + "/" + "wuinterface";
                    this.wurm = (WebInterface)Naming.lookup(name);
                }
                finally {
                    if (LoginServerWebConnection.logger.isLoggable(Level.FINE)) {
                        LoginServerWebConnection.logger.fine("Looking up WebInterface RMI: " + name + " took " + (System.nanoTime() - lStart) / 1000000.0f + "ms.");
                    }
                }
            }
        }
    }
    
    public int getServerId() {
        return this.serverId;
    }
    
    public byte[] createAndReturnPlayer(final String playerName, final String hashedIngamePassword, final String challengePhrase, final String challengeAnswer, final String emailAddress, final byte kingdom, final byte power, final long appearance, final byte gender, final boolean titleKeeper, final boolean addPremium, final boolean passwordIsHashed) throws Exception {
        if (this.wurm == null) {
            this.connect();
        }
        if (this.wurm != null) {
            return this.wurm.createAndReturnPlayer(this.intraServerPassword, playerName, hashedIngamePassword, challengePhrase, challengeAnswer, emailAddress, kingdom, power, appearance, gender, titleKeeper, addPremium, passwordIsHashed);
        }
        throw new RemoteException("Failed to create web connection.");
    }
    
    public long chargeMoney(final String playerName, final long moneyToCharge) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, playerName + " + Failed to contact the login server " + ex.getMessage());
                return -10L;
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.chargeMoney(this.intraServerPassword, playerName, moneyToCharge);
            }
            catch (RemoteException rx) {
                return -10L;
            }
        }
        return -10L;
    }
    
    public boolean addPlayingTime(final Creature player, final String name, final int months, final int days, final String detail) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the bank. Please try later.");
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                final Map<String, String> result = this.wurm.addPlayingTime(this.intraServerPassword, name, months, days, detail, Servers.localServer.testServer || player.getPower() > 0);
                for (final Map.Entry<String, String> e : result.entrySet()) {
                    if (e.getKey().equals("error")) {
                        player.getCommunicator().sendAlertServerMessage(e.getValue());
                        return false;
                    }
                    if (e.getKey().equals("ok")) {
                        return true;
                    }
                }
            }
            catch (RemoteException rx) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the bank. Please try later.");
                return false;
            }
        }
        return false;
    }
    
    public boolean addMoney(final Creature player, final String name, final long money, final String detail) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the bank. Please try later.");
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                final Map<String, String> result = this.wurm.addMoneyToBank(this.intraServerPassword, name, money, detail, false);
                for (final Map.Entry<String, String> e : result.entrySet()) {
                    if (e.getKey().equals("error")) {
                        player.getCommunicator().sendAlertServerMessage(e.getValue());
                        return false;
                    }
                    if (e.getKey().equals("ok")) {
                        return true;
                    }
                }
            }
            catch (RemoteException rx) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the bank. Please try later.");
                return false;
            }
        }
        return false;
    }
    
    public long getMoney(final Creature player) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the bank. Please try later.");
                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + " " + "Failed to contact the login server " + " " + this.serverId + " " + ex.getMessage());
                return 0L;
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getMoney(this.intraServerPassword, player.getWurmId(), player.getName());
            }
            catch (RemoteException rx) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the bank. Please try later.");
                return 0L;
            }
        }
        return 0L;
    }
    
    public boolean addMoney(final long wurmid, final String name, final long money, final String detail) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, wurmid + ": failed to receive " + money + ", " + detail + ", " + ex.getMessage());
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                final Map<String, String> result = this.wurm.addMoneyToBank(this.intraServerPassword, name, wurmid, money, detail, false);
                for (final Map.Entry<String, String> e : result.entrySet()) {
                    if (e.getKey().equals("error")) {
                        LoginServerWebConnection.logger.log(Level.WARNING, wurmid + ": failed to receive " + money + ", " + detail + ", " + e.getValue());
                        return false;
                    }
                    if (e.getKey().equals("ok")) {
                        return true;
                    }
                }
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, wurmid + ": failed to receive " + money + ", " + detail + ", " + rx, rx);
                return false;
            }
        }
        return false;
    }
    
    public void testAdding(final String playerName) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, playerName + ": " + ex.getMessage(), ex);
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return;
            }
        }
        try {
            final Map<String, String> result = this.wurm.addPlayingTime(this.intraServerPassword, playerName, 1, 4, "test" + System.currentTimeMillis());
            for (final Map.Entry<String, String> e : result.entrySet()) {
                LoginServerWebConnection.logger.log(Level.INFO, e.getKey() + ':' + e.getValue());
            }
            final Map<String, String> result2 = this.wurm.addMoneyToBank(this.intraServerPassword, playerName, 10000L, "test" + System.currentTimeMillis());
            for (final Map.Entry<String, String> e2 : result2.entrySet()) {
                LoginServerWebConnection.logger.log(Level.INFO, e2.getKey() + ':' + e2.getValue());
            }
        }
        catch (RemoteException rx) {
            LoginServerWebConnection.logger.log(Level.WARNING, rx.getMessage(), rx);
        }
    }
    
    public void setWeather(final float windRotation, final float windpower, final float windDir) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                return;
            }
        }
        if (this.wurm != null) {
            try {
                this.wurm.setWeather(this.intraServerPassword, windRotation, windpower, windDir);
            }
            catch (RemoteException rx) {}
        }
    }
    
    public Map<String, Byte> getReferrers(final Creature player, final long wurmid) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the login server. Please try later.");
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return null;
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getReferrers(this.intraServerPassword, wurmid);
            }
            catch (RemoteException rx) {
                player.getCommunicator().sendAlertServerMessage("An error occurred when contacting the login server. Please try later.");
            }
        }
        return null;
    }
    
    public void addReferrer(final Player player, final String receiver) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the login server. Please try later.");
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return;
            }
        }
        if (this.wurm != null) {
            try {
                final String mess = this.wurm.addReferrer(this.intraServerPassword, receiver, player.getWurmId());
                try {
                    final long referrer = Long.parseLong(mess);
                    player.getSaveFile().setReferedby(referrer);
                    player.getCommunicator().sendNormalServerMessage("Okay, you have set " + receiver + " as your referrer.");
                }
                catch (NumberFormatException nfe) {
                    player.getCommunicator().sendNormalServerMessage(mess);
                }
            }
            catch (RemoteException rx) {
                player.getCommunicator().sendAlertServerMessage("An error occurred when contacting the login server. Please try later.");
            }
        }
    }
    
    public void acceptReferrer(final Creature player, final String referrerName, final boolean money) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                player.getCommunicator().sendAlertServerMessage("Failed to contact the login server. Please try later.");
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return;
            }
        }
        if (this.wurm != null) {
            try {
                player.getCommunicator().sendNormalServerMessage(this.wurm.acceptReferrer(this.intraServerPassword, player.getWurmId(), referrerName, money));
            }
            catch (RemoteException rx) {
                player.getCommunicator().sendAlertServerMessage("An error occurred when contacting the login server. Please try later.");
            }
        }
    }
    
    public String getReimburseInfo(final Player player) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return "Failed to contact the login server. Please try later.";
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getReimbursementInfo(this.intraServerPassword, player.getSaveFile().emailAddress);
            }
            catch (RemoteException rx) {
                return "An error occurred when contacting the login server. Please try later.";
            }
        }
        return "Failed to contact the login server. Please try later.";
    }
    
    public long[] getCurrentServer(final String name, final long wurmid) throws Exception {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                throw new WurmServerException("Failed to contact the login server. Please try later.");
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getCurrentServerAndWurmid(this.intraServerPassword, name, wurmid);
            }
            catch (RemoteException rx) {
                throw new WurmServerException("An error occurred when contacting the login server. Please try later.", rx);
            }
        }
        throw new WurmServerException("Failed to contact the login server. Please try later.");
    }
    
    public Map<Long, byte[]> getPlayerStates(final long[] wurmids) throws WurmServerException {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                throw new WurmServerException("Failed to contact the login server. Please try later.");
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getPlayerStates(this.intraServerPassword, wurmids);
            }
            catch (RemoteException rx) {
                throw new WurmServerException("An error occurred when contacting the login server. Please try later.", rx);
            }
        }
        throw new WurmServerException("Failed to contact the login server. Please try later.");
    }
    
    public void manageFeature(final int aServerId, final int featureId, final boolean aOverridden, final boolean aEnabled, final boolean global) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
            }
        }
        if (this.wurm != null) {
            try {
                this.wurm.manageFeature(this.intraServerPassword, aServerId, featureId, aOverridden, aEnabled, global);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "An error occurred when contacting the login server. Please try later. " + this.serverId + " " + rx.getMessage());
            }
        }
    }
    
    public void startShutdown(final String instigator, final int seconds, final String reason) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
            }
        }
        if (this.wurm != null) {
            try {
                this.wurm.startShutdown(this.intraServerPassword, instigator, seconds, reason);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "An error occurred when contacting the login server. Please try later. " + this.serverId + " " + rx.getMessage());
            }
        }
    }
    
    public String withDraw(final Player player, final String name, final String _email, final int _months, final int _silvers, final boolean titlebok, final boolean mbok, final int _daysLeft) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return "Failed to contact the login server. Please try later.";
            }
        }
        if (this.wurm != null) {
            try {
                if (this.wurm.withDraw(this.intraServerPassword, player.getName(), name, _email, _months, _silvers, titlebok, _daysLeft)) {
                    if (titlebok) {
                        try {
                            final Item bok = ItemFactory.createItem(443, 99.0f, player.getName());
                            if (mbok) {
                                bok.setName("Master bag of keeping");
                                bok.setSizes(3, 10, 20);
                            }
                            player.getInventory().insertItem(bok, true);
                            player.getCommunicator().sendSafeServerMessage("You received " + bok.getNameWithGenus() + '!');
                        }
                        catch (FailedException fe) {
                            LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create bok! " + fe.getMessage(), fe);
                        }
                        catch (NoSuchTemplateException nsi) {
                            LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create bok! " + nsi.getMessage(), nsi);
                        }
                        player.addTitle(Titles.Title.Ageless);
                        if (mbok) {
                            player.addTitle(Titles.Title.KeeperTruth);
                        }
                    }
                    if (_months > 0) {
                        try {
                            final Item spyglass = ItemFactory.createItem(489, 80.0f + Server.rand.nextInt(20), player.getName());
                            player.getInventory().insertItem(spyglass, true);
                            player.getCommunicator().sendSafeServerMessage("You received " + spyglass.getNameWithGenus() + '!');
                        }
                        catch (FailedException fe) {
                            LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + fe.getMessage(), fe);
                        }
                        catch (NoSuchTemplateException nsi) {
                            LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + nsi.getMessage(), nsi);
                        }
                        Item trinket = null;
                        if (_months > 1) {
                            try {
                                trinket = ItemFactory.createItem(509, 80.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                            }
                            catch (FailedException fe2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + fe2.getMessage(), fe2);
                            }
                            catch (NoSuchTemplateException nsi2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + nsi2.getMessage(), nsi2);
                            }
                            try {
                                trinket = ItemFactory.createItem(93, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(79, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(20, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(313, 40.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(8, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(90, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                            }
                            catch (FailedException fe2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + fe2.getMessage(), fe2);
                            }
                            catch (NoSuchTemplateException nsi2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + nsi2.getMessage(), nsi2);
                            }
                        }
                        if (_months > 2) {
                            try {
                                trinket = ItemFactory.createItem(105, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(105, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(107, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(103, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(103, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(108, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(104, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(106, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(106, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(4, 30.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                            }
                            catch (FailedException fe2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + fe2.getMessage(), fe2);
                            }
                            catch (NoSuchTemplateException nsi2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + nsi2.getMessage(), nsi2);
                            }
                        }
                        if (_months > 3) {
                            try {
                                trinket = ItemFactory.createItem(135, 50.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                trinket = ItemFactory.createItem(480, 70.0f, player.getName());
                                player.getInventory().insertItem(trinket, true);
                                player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                            }
                            catch (FailedException fe2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + fe2.getMessage(), fe2);
                            }
                            catch (NoSuchTemplateException nsi2) {
                                LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + nsi2.getMessage(), nsi2);
                            }
                        }
                        if (_months > 4) {
                            for (int x = 0; x < 3; ++x) {
                                try {
                                    trinket = ItemFactory.createItem(509, 80.0f, player.getName());
                                    player.getInventory().insertItem(trinket, true);
                                    player.getCommunicator().sendSafeServerMessage("You received " + trinket.getNameWithGenus() + '!');
                                }
                                catch (FailedException fe3) {
                                    LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + fe3.getMessage(), fe3);
                                }
                                catch (NoSuchTemplateException nsi3) {
                                    LoginServerWebConnection.logger.log(Level.WARNING, player.getName() + ", failed to create trinket! " + nsi3.getMessage(), nsi3);
                                }
                            }
                        }
                    }
                    return "You have been reimbursed.";
                }
                return "There was an error with your request. The server may be unavailable. You may also want to verify the amounts entered.";
            }
            catch (RemoteException rx) {
                return "An error occurred when contacting the login server. Please try later.";
            }
        }
        return "Failed to contact the login server. Please try later.";
    }
    
    public boolean transferPlayer(final Player player, final String playerName, final int posx, final int posy, final boolean surfaced, final byte[] data) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + "," + ex.getMessage());
                if (player != null) {
                    player.getCommunicator().sendAlertServerMessage("Failed to contact the login server. Please try later.");
                }
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                if (!this.wurm.transferPlayer(this.intraServerPassword, playerName, posx, posy, surfaced, player.getPower(), data)) {
                    if (player != null) {
                        player.getCommunicator().sendAlertServerMessage("An error was reported from the login server. Please try later or report this using /support if the problem persists.");
                    }
                    return false;
                }
                return true;
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to transfer " + playerName + " to the login server " + rx.getMessage());
                if (player != null) {
                    player.getCommunicator().sendAlertServerMessage("An error occurred when contacting the login server. Please try later.");
                }
                return false;
            }
        }
        return false;
    }
    
    public boolean changePassword(final long wurmId, final String newPassword) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage() + " server=" + this.serverId);
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.changePassword(this.intraServerPassword, wurmId, newPassword);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to change password for  " + wurmId + "." + rx.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public int[] getPremTimeSilvers(final long wurmId) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                return LoginServerWebConnection.failedIntZero;
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getPremTimeSilvers(this.intraServerPassword, wurmId);
            }
            catch (RemoteException ex2) {}
        }
        return LoginServerWebConnection.failedIntZero;
    }
    
    public boolean setCurrentServer(final String name, final int currentServer) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                if (this.wurm.setCurrentServer(this.intraServerPassword, name, currentServer)) {
                    return true;
                }
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "failed to set current server of " + name + " to " + currentServer + ", " + rx.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public String renamePlayer(final String oldName, final String newName, final String newPass, final int power) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server " + ex.getMessage() + "" + this.serverId);
                return "Failed to contact server. Try later. This is an Error.";
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.rename(this.intraServerPassword, oldName, newName, newPass, power);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to change name of " + oldName + ", " + rx.getMessage());
                return "Failed to contact server. Try later. This is an Error.";
            }
        }
        return "";
    }
    
    public String changePassword(final String changerName, final String name, final String newPass, final int power) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.changePassword(this.intraServerPassword, changerName, name, newPass, power);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, changerName + " failed to change password of " + name + ", " + rx.getMessage());
                return rx.getMessage();
            }
        }
        return "";
    }
    
    public String ascend(final int newDeityId, final String deityName, final long wurmid, final byte existingDeity, final byte gender, final byte newPower, final float initialBStr, final float initialBSta, final float initialBCon, final float initialML, final float initialMS, final float initialSS, final float initialSD) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server " + ex.getMessage() + " " + this.serverId);
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.ascend(this.intraServerPassword, newDeityId, deityName, wurmid, existingDeity, gender, newPower, initialBStr, initialBSta, initialBCon, initialML, initialMS, initialSS, initialSD);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, wurmid + " failed to create deity " + deityName + ", " + rx.getMessage());
                return rx.getMessage();
            }
        }
        return "";
    }
    
    public String changeEmail(final String changerName, final String name, final String newEmail, final String password, final int power, final String pwQuestion, final String pwAnswer) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.changeEmail(this.intraServerPassword, changerName, name, newEmail, password, power, pwQuestion, pwAnswer);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, changerName + " failed to change email of " + name + ", " + rx.getMessage());
                return rx.getMessage();
            }
        }
        return "";
    }
    
    public String addReimb(final String changerName, final String name, final int numMonths, final int _silver, final int _daysLeft, final boolean setbok) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.addReimb(this.intraServerPassword, changerName, name, numMonths, _silver, _daysLeft, setbok);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, changerName + " failed to add reimb of " + name + ", " + rx.getMessage());
                return rx.getMessage();
            }
        }
        return "";
    }
    
    public String sendMail(final byte[] maildata, final byte[] items, final long sender, final long wurmid, final int targetServer) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.sendMail(this.intraServerPassword, maildata, items, sender, wurmid, targetServer);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send mail " + rx.getMessage());
                return rx.getMessage();
            }
        }
        return "";
    }
    
    public String ban(final String name, final String reason, final int days) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.ban(this.intraServerPassword, name, reason, days);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to ban " + name + ':' + rx.getMessage());
                return "Failed to ban " + name + ':' + rx.getMessage();
            }
        }
        return "Failed to contact the login server. Please try later.";
    }
    
    public String addBannedIp(final String ip, final String reason, final int days) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.addBannedIp(this.intraServerPassword, ip, reason, days);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to ban " + ip + ':' + rx.getMessage());
                return "Failed to ban " + ip + ':' + rx.getMessage();
            }
        }
        return "Failed to contact the login server. Please try later.";
    }
    
    public Ban[] getPlayersBanned() throws Exception {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                throw new WurmServerException("Failed to contact the login server:" + ex.getMessage());
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getPlayersBanned(this.intraServerPassword);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to retrieve banned players :" + rx.getMessage());
                throw new WurmServerException("Failed to retrieve banned players :" + rx.getMessage());
            }
        }
        return null;
    }
    
    public Ban[] getIpsBanned() throws Exception {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                throw new WurmServerException("Failed to contact the login server:" + ex.getMessage());
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.getIpsBanned(this.intraServerPassword);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to retrieve banned ips :" + rx.getMessage());
                throw new WurmServerException("Failed to retrieve banned ips :" + rx.getMessage());
            }
        }
        return null;
    }
    
    public String pardonban(final String name) throws RemoteException {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.pardonban(this.intraServerPassword, name);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to pardon " + name + ':' + rx.getMessage());
                return "Failed to pardon " + name + ':' + rx.getMessage();
            }
        }
        return "Failed to contact the login server. Please try later.";
    }
    
    public String removeBannedIp(final String ip) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.removeBannedIp(this.intraServerPassword, ip);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to ban " + ip + ':' + rx.getMessage());
                return "Failed to ban " + ip + ':' + rx.getMessage();
            }
        }
        return "Failed to contact the login server. Please try later.";
    }
    
    public Map<String, String> doesPlayerExist(final String playerName) {
        final Map<String, String> toReturn = new HashMap<String, String>();
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                toReturn.put("ResponseCode", "NOTOK");
                toReturn.put("ErrorMessage", "The game server is currently unavailable.");
                toReturn.put("display_text", "The game server is currently unavailable.");
                return toReturn;
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.doesPlayerExist(this.intraServerPassword, playerName);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact server.");
                toReturn.put("ResponseCode", "NOTOK");
                toReturn.put("ErrorMessage", "The game server is currently unavailable.");
                toReturn.put("display_text", "The game server is currently unavailable.");
                return toReturn;
            }
        }
        toReturn.put("ResponseCode", "NOTOK");
        toReturn.put("ErrorMessage", "The game server is currently unavailable.");
        toReturn.put("display_text", "The game server is currently unavailable.");
        return toReturn;
    }
    
    public String sendVehicle(final byte[] passengerdata, final byte[] itemdata, final long pilot, final long vehicleId, final int targetServer, final int tilex, final int tiley, final int layer, final float rotation) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return ex.getMessage();
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.sendVehicle(this.intraServerPassword, passengerdata, itemdata, pilot, vehicleId, targetServer, tilex, tiley, layer, rotation);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send vehicle " + rx.getMessage());
                return rx.getMessage();
            }
            catch (Exception ex) {
                return ex.getMessage();
            }
        }
        return "";
    }
    
    public void sendWebCommand(final short type, final WebCommand command) {
        new Thread() {
            @Override
            public void run() {
                boolean ok = false;
                if (LoginServerWebConnection.this.wurm == null) {
                    try {
                        LoginServerWebConnection.this.connect();
                    }
                    catch (Exception ex2) {}
                }
                if (LoginServerWebConnection.this.wurm != null) {
                    try {
                        LoginServerWebConnection.this.wurm.genericWebCommand(LoginServerWebConnection.this.intraServerPassword, type, command.getWurmId(), command.getData());
                        ok = true;
                    }
                    catch (RemoteException rx) {
                        LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send command " + rx.getMessage());
                    }
                }
                if (!ok && command.getType() == 11 && Servers.localServer.LOGINSERVER) {
                    try {
                        final EpicEntity entity = Server.getEpicMap().getEntity(((WcCreateEpicMission)command).entityNumber);
                        if (entity != null) {
                            entity.addFailedServer(LoginServerWebConnection.this.serverId);
                        }
                    }
                    catch (Exception ex) {
                        LoginServerWebConnection.logger.log(Level.WARNING, ex.getMessage(), ex);
                    }
                }
            }
        }.start();
    }
    
    public void setKingdomInfo(final byte kingdomId, final byte templateKingdom, final String _name, final String _password, final String _chatName, final String _suffix, final String mottoOne, final String mottoTwo, final boolean acceptsPortals) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {}
        }
        if (this.wurm != null) {
            try {
                this.wurm.setKingdomInfo(this.intraServerPassword, Servers.localServer.id, kingdomId, templateKingdom, _name, _password, _chatName, _suffix, mottoOne, mottoTwo, acceptsPortals);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send command " + rx.getMessage());
            }
        }
    }
    
    public boolean kingdomExists(final int thisServerId, final byte kingdomId, final boolean exists) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {}
        }
        if (this.wurm != null) {
            try {
                return this.wurm.kingdomExists(this.intraServerPassword, thisServerId, kingdomId, exists);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send command " + rx.getMessage());
            }
        }
        return true;
    }
    
    public void requestDemigod(final byte existingDeity, final String deityName) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {}
        }
        if (this.wurm != null) {
            try {
                this.wurm.requestDemigod(this.intraServerPassword, existingDeity, deityName);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send command " + rx.getMessage());
            }
        }
    }
    
    public boolean requestDeityMove(final int deityNum, final int desiredHex, final String guide) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {}
        }
        if (this.wurm != null) {
            try {
                return this.wurm.requestDeityMove(this.intraServerPassword, deityNum, desiredHex, guide);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to send command " + rx.getMessage());
            }
        }
        return false;
    }
    
    public boolean awardPlayer(final long wurmid, final String name, final int days, final int months) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
                return false;
            }
        }
        if (this.wurm != null) {
            try {
                this.wurm.awardPlayer(this.intraServerPassword, wurmid, name, days, months);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "failed to set award " + wurmid + " (" + name + ") " + months + " months, " + days + " days, " + rx.getMessage());
                return false;
            }
        }
        return false;
    }
    
    public boolean isFeatureEnabled(final int featureId) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.isFeatureEnabled(this.intraServerPassword, featureId);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "An error occurred when contacting the login server. Please try later. " + this.serverId + " " + rx.getMessage());
            }
        }
        return false;
    }
    
    public boolean setPlayerFlag(final long wurmid, final int flag, final boolean set) {
        if (this.wurm == null) {
            try {
                this.connect();
            }
            catch (Exception ex) {
                LoginServerWebConnection.logger.log(Level.WARNING, "Failed to contact the login server  " + this.serverId + " " + ex.getMessage());
            }
        }
        if (this.wurm != null) {
            try {
                return this.wurm.setPlayerFlag(Servers.localServer.INTRASERVERPASSWORD, wurmid, flag, set);
            }
            catch (RemoteException rx) {
                LoginServerWebConnection.logger.log(Level.WARNING, "An error occurred when contacting the login server. Please try later. " + this.serverId + " " + rx.getMessage());
            }
        }
        return false;
    }
    
    static {
        LoginServerWebConnection.logger = Logger.getLogger(LoginServerWebConnection.class.getName());
        failedIntZero = new int[] { -1, -1 };
    }
}
