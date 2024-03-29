// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.propertysheet;

import javafx.collections.ListChangeListener;
import javafx.beans.value.ObservableValue;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import java.util.Objects;
import coffee.keenan.network.helpers.address.AddressHelper;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import org.controlsfx.property.editor.PropertyEditor;
import javafx.util.Callback;
import javafx.beans.property.SimpleObjectProperty;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import com.wurmonline.server.steam.SteamHandler;
import java.net.InetAddress;
import javafx.collections.FXCollections;
import java.util.HashSet;
import java.util.Random;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import com.wurmonline.server.ServerProperties;
import com.wurmonline.server.Constants;
import java.util.Set;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import com.wurmonline.server.ServerEntry;
import java.util.logging.Logger;
import javafx.scene.layout.VBox;

public final class ServerPropertySheet extends VBox
{
    private static final Logger logger;
    private ServerEntry current;
    private PropertySheet propertySheet;
    private final ObservableList<PropertySheet.Item> list;
    private final String categoryServerSettings = "1: Server Settings";
    private final String categoryAdvanceSettings = "2: Advance Server Settings";
    private final String categoryTweaks = "3: Gameplay Tweaks";
    private final String categoryTwitter = "4: Twitter Settings";
    private final String categoryMaintenance = "5: Maintenance";
    private final String categoryOtherServerSettings = "1: Server Settings";
    private Set<PropertyType> changedProperties;
    boolean saveNewGui;
    boolean saveSpawns;
    boolean saveTwitter;
    boolean changedId;
    int oldId;
    private static final String PASSWORD_CHARS = "abcdefgijkmnopqrstwxyzABCDEFGHJKLMNPQRSTWXYZ23456789";
    
    public final String save() {
        String toReturn = "";
        boolean saveAtAll = false;
        for (final CustomPropertyItem item : (CustomPropertyItem[])this.list.toArray((Object[])new CustomPropertyItem[this.list.size()])) {
            if (this.changedProperties.contains(item.getPropertyType()) || this.current.isCreating) {
                saveAtAll = true;
                try {
                    switch (item.getPropertyType()) {
                        case SERVERID: {
                            if (this.current.isLocal) {
                                this.changedId = true;
                                this.oldId = this.current.id;
                            }
                            this.current.id = (int)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case ENABLE_PNP_PORT_FORWARD: {
                            final boolean upnpSetting = (boolean)item.getValue();
                            if (upnpSetting != ServerProperties.getBoolean("ENABLE_PNP_PORT_FORWARD", Constants.enablePnpPortForward)) {
                                ServerProperties.setValue("ENABLE_PNP_PORT_FORWARD", Boolean.toString(upnpSetting));
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case EXTSERVERIP: {
                            this.current.EXTERNALIP = item.getValue().toString();
                            this.saveNewGui = true;
                            break;
                        }
                        case EXTSERVERPORT: {
                            this.current.EXTERNALPORT = item.getValue().toString();
                            this.saveNewGui = true;
                            break;
                        }
                        case INTIP: {
                            this.current.INTRASERVERADDRESS = item.getValue().toString();
                            this.saveNewGui = true;
                            break;
                        }
                        case INTPORT: {
                            this.current.INTRASERVERPORT = item.getValue().toString();
                            this.saveNewGui = true;
                            break;
                        }
                        case MAXPLAYERS: {
                            this.current.pLimit = (int)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case SKILLGAINRATE: {
                            this.current.setSkillGainRate((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case ACTIONTIMER: {
                            this.current.setActionTimer((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case RMIPORT: {
                            this.current.RMI_PORT = (int)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case RMI_REG_PORT: {
                            this.current.REGISTRATION_PORT = (int)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case PVPSERVER: {
                            this.current.PVPSERVER = (boolean)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case EPIC: {
                            this.current.EPIC = (boolean)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case HOMESERVER: {
                            this.current.HOMESERVER = (boolean)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case SPAWNPOINTJENNX: {
                            this.current.SPAWNPOINTJENNX = (int)item.getValue();
                            this.saveSpawns = true;
                            break;
                        }
                        case SPAWNPOINTJENNY: {
                            this.current.SPAWNPOINTJENNY = (int)item.getValue();
                            this.saveSpawns = true;
                            break;
                        }
                        case SPAWNPOINTMOLX: {
                            this.current.SPAWNPOINTMOLX = (int)item.getValue();
                            this.saveSpawns = true;
                            break;
                        }
                        case SPAWNPOINTMOLY: {
                            this.current.SPAWNPOINTMOLY = (int)item.getValue();
                            this.saveSpawns = true;
                            break;
                        }
                        case SPAWNPOINTLIBX: {
                            this.current.SPAWNPOINTLIBX = (int)item.getValue();
                            this.saveSpawns = true;
                            break;
                        }
                        case SPAWNPOINTLIBY: {
                            this.current.SPAWNPOINTLIBY = (int)item.getValue();
                            this.saveSpawns = true;
                            break;
                        }
                        case KINGDOM: {
                            this.current.KINGDOM = (byte)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case INTRASERVERPASSWORD: {
                            this.current.INTRASERVERPASSWORD = item.getValue().toString();
                            this.saveNewGui = true;
                            break;
                        }
                        case TESTSERVER: {
                            this.current.testServer = (boolean)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case NPCS: {
                            final boolean newSetting = (boolean)item.getValue();
                            if (newSetting != ServerProperties.getBoolean("NPCS", Constants.loadNpcs)) {
                                ServerProperties.setValue("NPCS", Boolean.toString(newSetting));
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case ENDGAMEITEMS: {
                            final boolean loadEGI = (boolean)item.getValue();
                            if (loadEGI != ServerProperties.getBoolean("ENDGAMEITEMS", Constants.loadEndGameItems)) {
                                ServerProperties.setValue("ENDGAMEITEMS", Boolean.toString(loadEGI));
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case SPY_PREVENTION: {
                            final boolean spy = (boolean)item.getValue();
                            if (spy != ServerProperties.getBoolean("SPYPREVENTION", Constants.enableSpyPrevention)) {
                                ServerProperties.setValue("SPYPREVENTION", Boolean.toString(spy));
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case NEWBIE_FRIENDLY: {
                            final boolean newbie = (boolean)item.getValue();
                            if (newbie != ServerProperties.getBoolean("NEWBIEFRIENDLY", Constants.isNewbieFriendly)) {
                                ServerProperties.setValue("NEWBIEFRIENDLY", Boolean.toString(newbie));
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case STEAMQUERYPORT: {
                            final short sqp = (short)item.getValue();
                            if (sqp != ServerProperties.getShort("STEAMQUERYPORT", sqp)) {
                                ServerProperties.setValue("STEAMQUERYPORT", Short.toString(sqp));
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case ADMIN_PWD: {
                            final String pwd = (String)item.getValue();
                            if (!pwd.equals(ServerProperties.getString("ADMINPASSWORD", pwd))) {
                                ServerProperties.setValue("ADMINPASSWORD", pwd);
                                ServerProperties.checkProperties();
                                break;
                            }
                            break;
                        }
                        case NAME: {
                            this.current.name = item.getValue().toString();
                            this.saveNewGui = true;
                            break;
                        }
                        case LOGINSERVER: {
                            this.current.LOGINSERVER = (boolean)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case ISPAYMENT: {
                            this.current.ISPAYMENT = false;
                            this.saveNewGui = true;
                            break;
                        }
                        case TWITTERCONSUMERKEY: {
                            this.current.setConsumerKeyToUse(item.getValue().toString());
                            this.saveTwitter = true;
                            break;
                        }
                        case TWITTERCONSUMERSECRET: {
                            this.current.setConsumerSecret(item.getValue().toString());
                            this.saveTwitter = true;
                            break;
                        }
                        case TWITTERAPPTOKEN: {
                            this.current.setApplicationToken(item.getValue().toString());
                            this.saveTwitter = true;
                            break;
                        }
                        case TWITTERAPPSECRET: {
                            this.current.setApplicationSecret(item.getValue().toString());
                            this.saveTwitter = true;
                            break;
                        }
                        case MAINTAINING: {
                            this.current.maintaining = (boolean)item.getValue();
                            break;
                        }
                        case MAXCREATURES: {
                            this.current.maxCreatures = (int)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case PERCENTAGG: {
                            this.current.percentAggCreatures = (float)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case HOTADELAY: {
                            this.current.setHotaDelay((int)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case RANDOMSPAWNS: {
                            this.current.randomSpawns = (boolean)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case SKBASIC: {
                            this.current.setSkillbasicval((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case SKFIGHT: {
                            this.current.setSkillfightval((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case SKMIND: {
                            this.current.setSkillmindval((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case SKOVERALL: {
                            this.current.setSkilloverallval((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case SKBC: {
                            this.current.setSkillbcval((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case CRMOD: {
                            this.current.setCombatRatingModifier((float)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case STEAMPW: {
                            this.current.setSteamServerPassword(item.getValue().toString());
                            this.saveNewGui = true;
                            break;
                        }
                        case UPKEEP: {
                            this.current.setUpkeep((boolean)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case MAXDEED: {
                            this.current.setMaxDeedSize((int)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case FREEDEEDS: {
                            this.current.setFreeDeeds((boolean)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case TRADERMAX: {
                            this.current.setTraderMaxIrons((int)item.getValue() * 10000);
                            this.saveNewGui = true;
                            break;
                        }
                        case TRADERINIT: {
                            this.current.setInitialTraderIrons((int)item.getValue() * 10000);
                            this.saveNewGui = true;
                            break;
                        }
                        case TUNNELING: {
                            this.current.setTunnelingHits((int)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case BREEDING: {
                            this.current.setBreedingTimer((long)item.getValue());
                            this.saveNewGui = true;
                            break;
                        }
                        case FIELDGROWTH: {
                            this.current.setFieldGrowthTime((long)((float)item.getValue() * 3600.0f * 1000.0f));
                            this.saveNewGui = true;
                            break;
                        }
                        case TREEGROWTH: {
                            this.current.treeGrowth = (int)item.getValue();
                            this.saveNewGui = true;
                            break;
                        }
                        case KINGSMONEY: {
                            this.current.setKingsmoneyAtRestart((int)item.getValue() * 10000);
                            this.saveNewGui = true;
                            break;
                        }
                        case MOTD: {
                            this.current.setMotd(item.getValue().toString());
                            this.saveNewGui = true;
                            break;
                        }
                    }
                }
                catch (Exception ex) {
                    saveAtAll = false;
                    toReturn = toReturn + "Invalid value " + item.getCategory() + ": " + item.getValue() + ". ";
                    ServerPropertySheet.logger.log(Level.INFO, "Error " + ex.getMessage(), ex);
                }
            }
        }
        if (toReturn.length() == 0 && saveAtAll) {
            if (this.current.isCreating) {
                toReturn = "New server saved";
                Servers.registerServer(this.current.id, this.current.getName(), this.current.HOMESERVER, this.current.SPAWNPOINTJENNX, this.current.SPAWNPOINTJENNY, this.current.SPAWNPOINTLIBX, this.current.SPAWNPOINTLIBY, this.current.SPAWNPOINTMOLX, this.current.SPAWNPOINTMOLY, this.current.INTRASERVERADDRESS, this.current.INTRASERVERPORT, this.current.INTRASERVERPASSWORD, this.current.EXTERNALIP, this.current.EXTERNALPORT, this.current.LOGINSERVER, this.current.KINGDOM, this.current.ISPAYMENT, this.current.getConsumerKey(), this.current.getConsumerSecret(), this.current.getApplicationToken(), this.current.getApplicationSecret(), false, this.current.testServer, this.current.randomSpawns);
            }
            else {
                toReturn = "Properties saved";
            }
            if (this.saveNewGui) {
                ServerPropertySheet.logger.log(Level.INFO, "Saved using new method.");
                this.saveNewGui = false;
                if (this.changedId) {
                    this.current.saveNewGui(this.oldId);
                    this.current.movePlayersFromId(this.oldId);
                    this.changedId = false;
                    Servers.moveServerId(this.current, this.oldId);
                    this.oldId = 0;
                }
                else {
                    this.current.saveNewGui(this.current.id);
                }
            }
            if (this.saveTwitter && !this.current.isCreating) {
                if (this.current.saveTwitter()) {
                    ServerPropertySheet.logger.log(Level.INFO, "Saved twitter settings. The server will attempt to tweet.");
                }
                else {
                    ServerPropertySheet.logger.log(Level.INFO, "Saved twitter settings. The server will not tweet.");
                }
                this.saveTwitter = false;
            }
            if (this.saveSpawns) {
                this.current.updateSpawns();
                ServerPropertySheet.logger.log(Level.INFO, "Saved new spawn points.");
                this.saveSpawns = false;
            }
            saveAtAll = false;
            this.changedProperties.clear();
            this.current.isCreating = false;
        }
        return toReturn;
    }
    
    public static final String generateRandomPassword() {
        final Random rand = new Random();
        final int length = rand.nextInt(3) + 6;
        final char[] password = new char[length];
        for (int x = 0; x < length; ++x) {
            final int randDecimalAsciiVal = rand.nextInt("abcdefgijkmnopqrstwxyzABCDEFGHJKLMNPQRSTWXYZ23456789".length());
            password[x] = "abcdefgijkmnopqrstwxyzABCDEFGHJKLMNPQRSTWXYZ23456789".charAt(randDecimalAsciiVal);
        }
        return String.valueOf(password);
    }
    
    public static final short getNewServerId() {
        final Random random = new Random();
        short newRand = 0;
        final HashSet<Integer> usedNumbers = new HashSet<Integer>();
        for (final ServerEntry entry : Servers.getAllServers()) {
            usedNumbers.add(entry.id);
        }
        int tries = 0;
        final int max = 30000;
        usedNumbers.add(0);
        while (usedNumbers.contains((int)newRand) && tries < max) {
            newRand = (short)random.nextInt(32767);
            if (!usedNumbers.contains((int)newRand)) {
                break;
            }
            ++tries;
        }
        return newRand;
    }
    
    public ServerPropertySheet(final ServerEntry entry) {
        this.changedProperties = new HashSet<PropertyType>();
        this.saveNewGui = false;
        this.saveSpawns = false;
        this.saveTwitter = false;
        this.changedId = false;
        this.oldId = 0;
        this.current = entry;
        this.list = (ObservableList<PropertySheet.Item>)FXCollections.observableArrayList();
        if (entry == null) {
            return;
        }
        if (entry.isLocal) {
            this.initializeLocalServer(entry);
        }
        else {
            this.initializeNonLocalServer(entry);
        }
    }
    
    private void initializeLocalServer(final ServerEntry entry) {
        this.saveNewGui = false;
        this.saveSpawns = false;
        this.saveTwitter = false;
        this.list.add((Object)new CustomPropertyItem(PropertyType.NAME, "1: Server Settings", "Server Name", "Name", true, entry.name));
        if (entry.id == 0) {
            this.changedId = true;
            this.oldId = 0;
            entry.id = getNewServerId();
            this.list.add((Object)new CustomPropertyItem(PropertyType.SERVERID, "2: Advance Server Settings", "Server ID", "The unique ID in the cluster", true, entry.id));
            this.changedProperties.add(PropertyType.SERVERID);
            this.saveNewGui = true;
        }
        else {
            this.list.add((Object)new CustomPropertyItem(PropertyType.SERVERID, "2: Advance Server Settings", "Server ID", "The unique ID in the cluster", true, entry.id));
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.ENABLE_PNP_PORT_FORWARD, "1: Server Settings", "Auto port-forward", "Uses PNP to set up port-forwarding on your router", true, ServerProperties.getBoolean("ENABLE_PNP_PORT_FORWARD", Constants.enablePnpPortForward)));
        if ((entry.EXTERNALIP == null || entry.EXTERNALIP.equals("")) && entry.isLocal) {
            try {
                entry.EXTERNALIP = InetAddress.getLocalHost().getHostAddress();
                this.changedProperties.add(PropertyType.EXTSERVERIP);
                this.saveNewGui = true;
            }
            catch (Exception ex) {
                ServerPropertySheet.logger.log(Level.INFO, ex.getMessage());
            }
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.EXTSERVERIP, "1: Server Settings", "Server External IP Address", "IP Address", true, entry.EXTERNALIP));
        this.list.add((Object)new CustomPropertyItem(PropertyType.EXTSERVERPORT, "1: Server Settings", "Server External IP Port", "IP Port number", true, entry.EXTERNALPORT));
        if ((entry.INTRASERVERADDRESS == null || entry.INTRASERVERADDRESS.equals("")) && entry.isLocal) {
            try {
                entry.INTRASERVERADDRESS = InetAddress.getLoopbackAddress().getHostAddress();
                this.changedProperties.add(PropertyType.INTIP);
                this.saveNewGui = true;
            }
            catch (Exception ex) {
                ServerPropertySheet.logger.log(Level.INFO, ex.getMessage());
            }
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.INTIP, "2: Advance Server Settings", "Server Internal IP Address", "IP Address", true, entry.INTRASERVERADDRESS));
        this.list.add((Object)new CustomPropertyItem(PropertyType.INTPORT, "2: Advance Server Settings", "Server Internal IP Port", "IP Port number", true, entry.INTRASERVERPORT));
        this.list.add((Object)new CustomPropertyItem(PropertyType.RMI_REG_PORT, "2: Advance Server Settings", "RMI Registration Port", "IP Port number", true, entry.REGISTRATION_PORT));
        this.list.add((Object)new CustomPropertyItem(PropertyType.RMIPORT, "2: Advance Server Settings", "RMI Port", "IP Port number", true, entry.RMI_PORT));
        this.list.add((Object)new CustomPropertyItem(PropertyType.STEAMQUERYPORT, "2: Advance Server Settings", "Steam Query Port", "A port Steam uses for queries about connections. Standard is 27016", true, ServerProperties.getShort("STEAMQUERYPORT", SteamHandler.steamQueryPort)));
        if ((entry.INTRASERVERPASSWORD == null || entry.INTRASERVERPASSWORD.equals("")) && entry.isLocal) {
            entry.INTRASERVERPASSWORD = generateRandomPassword();
            this.changedProperties.add(PropertyType.INTRASERVERPASSWORD);
            this.saveNewGui = true;
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.INTRASERVERPASSWORD, "2: Advance Server Settings", "Intra server password", "Server Cross-Communication password. Used for connecting servers to eachother.", true, entry.INTRASERVERPASSWORD));
        this.list.add((Object)new CustomPropertyItem(PropertyType.STEAMPW, "1: Server Settings", "Server password", "Server Password. If set, players need to provide this in order to connect to your server", true, entry.getSteamServerPassword()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.MAXPLAYERS, "1: Server Settings", "Maximum number of players", "Maximum number of players on this server", true, entry.pLimit));
        this.list.add((Object)new CustomPropertyItem(PropertyType.PVPSERVER, "1: Server Settings", "Allow PvP", "Allowing Player Versus Player combat and theft", true, entry.PVPSERVER));
        this.list.add((Object)new CustomPropertyItem(PropertyType.EPIC, "1: Server Settings", "Epic settings", "Faster skillgain, missions affect Valrei", true, entry.EPIC));
        this.list.add((Object)new CustomPropertyItem(PropertyType.HOMESERVER, "1: Server Settings", "Home Server", "A Home server can only have settlements from one kingdom", true, entry.HOMESERVER));
        this.list.add((Object)new CustomPropertyItem(PropertyType.KINGDOM, "1: Server Settings", "Home Server Kingdom", "Which kingdom the Home server should have, 0 = No kingdom, 1 = Jenn-Kellon, 2 = Mol Rehan, 3 = Horde of the Summoned, 4 = Freedom Isles", true, entry.KINGDOM));
        this.list.add((Object)new CustomPropertyItem(PropertyType.LOGINSERVER, "2: Advance Server Settings", "Login Server", "The login server is the central cluster node responsible for bank accounts and cross communication", true, entry.LOGINSERVER));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TESTSERVER, "2: Advance Server Settings", "Test Server", "Some special settings and debug options", true, entry.testServer));
        this.list.add((Object)new CustomPropertyItem(PropertyType.NPCS, "2: Advance Server Settings", "Npcs", "Whether npcs should be loaded", true, ServerProperties.getBoolean("NPCS", Constants.loadNpcs)));
        this.list.add((Object)new CustomPropertyItem(PropertyType.ENDGAMEITEMS, "2: Advance Server Settings", "End Game Items", "Whether artifacts and huge altars should be loaded", true, ServerProperties.getBoolean("ENDGAMEITEMS", Constants.loadEndGameItems)));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPY_PREVENTION, "2: Advance Server Settings", "PVP Spy Prevention", "Prevents multiple IPs from different kingdoms from logging in at the same time.", true, ServerProperties.getBoolean("SPYPREVENTION", Constants.enableSpyPrevention)));
        this.list.add((Object)new CustomPropertyItem(PropertyType.NEWBIE_FRIENDLY, "2: Advance Server Settings", "Newbie Friendly", "Prevents harder creatures from spawning.", true, ServerProperties.getBoolean("NEWBIEFRIENDLY", Constants.isNewbieFriendly)));
        this.list.add((Object)new CustomPropertyItem(PropertyType.RANDOMSPAWNS, "2: Advance Server Settings", "Random spawn points", "Enable random spawn points for new players", true, entry.randomSpawns));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPAWNPOINTJENNX, "2: Advance Server Settings", "Spawnpoint x", "Where players generally spawn, tile x", true, entry.SPAWNPOINTJENNX));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPAWNPOINTJENNY, "2: Advance Server Settings", "Spawnpoint y", "Where players generally spawn, tile y", true, entry.SPAWNPOINTJENNY));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPAWNPOINTMOLX, "2: Advance Server Settings", "Kingdom 2 Spawnpoint x", "Where kingdom 2 players spawn, tile x", true, entry.SPAWNPOINTMOLX));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPAWNPOINTMOLY, "2: Advance Server Settings", "Kingdom 2 Spawnpoint y", "Where kingdom 2 players spawn, tile y", true, entry.SPAWNPOINTMOLY));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPAWNPOINTLIBX, "2: Advance Server Settings", "Kingdom 3 Spawnpoint x", "Where kingdom 3 players spawn, tile x", true, entry.SPAWNPOINTLIBX));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SPAWNPOINTLIBY, "2: Advance Server Settings", "Kingdom 3 Spawnpoint y", "Where kingdom 3 players spawn, tile y", true, entry.SPAWNPOINTLIBY));
        this.list.add((Object)new CustomPropertyItem(PropertyType.MOTD, "1: Server Settings", "Message of the day", "A message to display upon login", true, entry.getMotd()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TWITTERCONSUMERKEY, "4: Twitter Settings", "Consumer key", "Consumer key", true, entry.getConsumerKey()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TWITTERCONSUMERSECRET, "4: Twitter Settings", "Consumer secret", "Consumer secret", true, entry.getConsumerSecret()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TWITTERAPPTOKEN, "4: Twitter Settings", "Application token", "Application token", true, entry.getApplicationToken()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TWITTERAPPSECRET, "4: Twitter Settings", "Application secret", "Application secret", true, entry.getApplicationSecret()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SKILLGAINRATE, "3: Gameplay Tweaks", "Skill gain rate multiplier", "Multiplies the server skill gain rate. Higher means faster skill gain. It's not exact depending on a number of factors.", true, this.current.getSkillGainRate(), 0.01f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SKBASIC, "3: Gameplay Tweaks", "Characteristics start value", "Start value of Characteristics such as body strength, stamina and soul depth", true, this.current.getSkillbasicval(), 1.0f, 100.0f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SKMIND, "3: Gameplay Tweaks", "Mind Logic skill start value", "Start value of Mind Logic Characteristic (used for controlling vehicles)", true, this.current.getSkillmindval(), 1.0f, 100.0f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SKBC, "3: Gameplay Tweaks", "Body Control skill start value", "Start value of Mind Logic Characteristic (used for controlling mounts)", true, this.current.getSkillbcval(), 1.0f, 100.0f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SKFIGHT, "3: Gameplay Tweaks", "Fight skill start value", "Affects start value of the overall fighting skill", true, this.current.getSkillfightval(), 1.0f, 100.0f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.SKOVERALL, "3: Gameplay Tweaks", "Overall skill start value", "Start value of all other skills", true, this.current.getSkilloverallval(), 1.0f, 100.0f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.CRMOD, "3: Gameplay Tweaks", "Player combat rating modifier", "Modifies player combat power versus creatures", true, this.current.getCombatRatingModifier()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.ACTIONTIMER, "3: Gameplay Tweaks", "Action speed multiplier", "Divides the max standard time an action takes. Higher makes actions faster.", true, this.current.getActionTimer(), 0.01f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.HOTADELAY, "3: Gameplay Tweaks", "Hota Delay", "The time in minutes between Hunt Of The Ancients rounds", true, this.current.getHotaDelay()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.MAXCREATURES, "3: Gameplay Tweaks", "Max Creatures", "Max creatures", true, this.current.maxCreatures));
        this.list.add((Object)new CustomPropertyItem(PropertyType.PERCENTAGG, "3: Gameplay Tweaks", "Aggressive Creatures, %", "Approximate max number of aggressive creatures in per cent of all creatures", true, this.current.percentAggCreatures, 0.0f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.UPKEEP, "3: Gameplay Tweaks", "Settlement upkeep enabled", "If settlements require upkeep money", true, this.current.isUpkeep()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.FREEDEEDS, "3: Gameplay Tweaks", "No deeding costs", "If deeding is free and costs no money", true, this.current.isFreeDeeds()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TRADERMAX, "3: Gameplay Tweaks", "Trader max money in silver", "The max amount of money a trader will receive from the pool", true, this.current.getTraderMaxIrons() / 10000));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TRADERINIT, "3: Gameplay Tweaks", "Trader initial money in silver", "The initial amount of money a trader will receive from the pool", true, this.current.getInitialTraderIrons() / 10000));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TUNNELING, "3: Gameplay Tweaks", "Minimum mining hits required", "The minimum number of times you need to mine before a wall disappears", true, this.current.getTunnelingHits()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.BREEDING, "3: Gameplay Tweaks", "Breeding time modifier", "A modifier which makes breeding faster the higher it is.", true, this.current.getBreedingTimer()));
        this.list.add((Object)new CustomPropertyItem(PropertyType.FIELDGROWTH, "3: Gameplay Tweaks", "Field growth timer, hour", "The number hours between field growth checks", true, this.current.getFieldGrowthTime() / 1000.0f / 3600.0f, 0.01f));
        this.list.add((Object)new CustomPropertyItem(PropertyType.TREEGROWTH, "3: Gameplay Tweaks", "Tree spread odds", "Odds of new trees or mushrooms appearing, as determined by a 1 in <Tree Growth> chance. When set to 0, tree growth is prevented.", true, this.current.treeGrowth, 0));
        this.list.add((Object)new CustomPropertyItem(PropertyType.KINGSMONEY, "3: Gameplay Tweaks", "Money pool in silver", "This is the amount of money that will be in the money pool after server restart.", true, this.current.getKingsmoneyAtRestart() / 10000));
        this.list.add((Object)new CustomPropertyItem(PropertyType.MAINTAINING, "5: Maintenance", "Maintenance", "Start in maintenance mode", true, false));
        this.list.add((Object)new CustomPropertyItem(PropertyType.ADMIN_PWD, "1: Server Settings", "Admin Password", "Password used to unlock the ability to change game tweaks from within the game.", true, ServerProperties.getString("ADMINPASSWORD", "")));
        if (this.saveNewGui) {
            this.saveNewGui = false;
            if (entry.isCreating) {
                this.save();
            }
            else if (this.changedId) {
                this.current.saveNewGui(this.oldId);
                this.current.movePlayersFromId(this.oldId);
                this.changedId = false;
                Servers.moveServerId(this.current, this.oldId);
                this.oldId = 0;
            }
            else {
                entry.saveNewGui(entry.id);
            }
            this.changedProperties.clear();
            System.out.println("Saved new server");
        }
        final SimpleObjectProperty<Callback<PropertySheet.Item, PropertyEditor<?>>> propertyEditorFactory = (SimpleObjectProperty<Callback<PropertySheet.Item, PropertyEditor<?>>>)new SimpleObjectProperty((Object)this, "propertyEditor", (Object)new DefaultPropertyEditorFactory());
        (this.propertySheet = new PropertySheet(this.list)).setPropertyEditorFactory((Callback<PropertySheet.Item, PropertyEditor<?>>)new Callback<PropertySheet.Item, PropertyEditor<?>>() {
            public PropertyEditor<?> call(final PropertySheet.Item param) {
                if (param instanceof CustomPropertyItem) {
                    final CustomPropertyItem pi = (CustomPropertyItem)param;
                    if (pi.type == PropertyType.ACTIONTIMER || pi.getValue().getClass() == Float.class) {
                        return new FormattedFloatEditor<Object>(param);
                    }
                }
                if (propertyEditorFactory.get() == null) {
                    throw new NullPointerException("Null!");
                }
                return (PropertyEditor<?>)((Callback)propertyEditorFactory.get()).call((Object)param);
            }
        });
        this.propertySheet.setMode(PropertySheet.Mode.CATEGORY);
        VBox.setVgrow((Node)this.propertySheet, Priority.ALWAYS);
        this.getChildren().add((Object)this.propertySheet);
    }
    
    private void initializeNonLocalServer(final ServerEntry entry) {
        this.saveNewGui = false;
        this.saveSpawns = false;
        this.saveTwitter = false;
        this.list.add((Object)new CustomPropertyItem(PropertyType.NAME, "1: Server Settings", "Server Name", "Name", true, entry.name));
        if (entry.id == 0) {
            this.changedId = true;
            this.oldId = 0;
            entry.id = getNewServerId();
            this.list.add((Object)new CustomPropertyItem(PropertyType.SERVERID, "1: Server Settings", "Server ID", "The unique ID in the cluster", true, entry.id));
            this.changedProperties.add(PropertyType.SERVERID);
            this.saveNewGui = true;
        }
        else {
            this.list.add((Object)new CustomPropertyItem(PropertyType.SERVERID, "1: Server Settings", "Server ID", "The unique ID in the cluster", true, entry.id));
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.ENABLE_PNP_PORT_FORWARD, "1: Server Settings", "Auto port-forward", "Uses PNP to set up port-forwarding on your router", true, ServerProperties.getBoolean("ENABLE_PNP_PORT_FORWARD", Constants.enablePnpPortForward)));
        if (entry.EXTERNALIP == null || entry.EXTERNALIP.equals("")) {
            if (entry.isLocal) {
                try {
                    entry.EXTERNALIP = InetAddress.getLocalHost().getHostAddress();
                    this.changedProperties.add(PropertyType.EXTSERVERIP);
                    this.saveNewGui = true;
                }
                catch (Exception ex) {
                    ServerPropertySheet.logger.log(Level.INFO, ex.getMessage());
                }
            }
            else {
                entry.EXTERNALIP = Objects.requireNonNull(AddressHelper.getFirstValidAddress()).getHostAddress();
                this.changedProperties.add(PropertyType.EXTSERVERIP);
                this.saveNewGui = true;
            }
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.EXTSERVERIP, "1: Server Settings", "Server External IP Address", "IP Address", true, entry.EXTERNALIP));
        this.list.add((Object)new CustomPropertyItem(PropertyType.EXTSERVERPORT, "1: Server Settings", "Server External IP Port", "IP Port number", true, entry.EXTERNALPORT));
        if ((entry.INTRASERVERADDRESS == null || entry.INTRASERVERADDRESS.equals("")) && entry.isLocal) {
            try {
                entry.INTRASERVERADDRESS = InetAddress.getLocalHost().getHostAddress();
                this.changedProperties.add(PropertyType.INTIP);
                this.saveNewGui = true;
            }
            catch (Exception ex) {
                ServerPropertySheet.logger.log(Level.INFO, ex.getMessage());
            }
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.INTIP, "1: Server Settings", "Server Internal IP Address", "IP Address", true, entry.INTRASERVERADDRESS));
        this.list.add((Object)new CustomPropertyItem(PropertyType.INTPORT, "1: Server Settings", "Server Internal IP Port", "IP Port number", true, entry.INTRASERVERPORT));
        this.list.add((Object)new CustomPropertyItem(PropertyType.RMI_REG_PORT, "1: Server Settings", "RMI Registration Port", "IP Port number", true, entry.REGISTRATION_PORT));
        this.list.add((Object)new CustomPropertyItem(PropertyType.RMIPORT, "1: Server Settings", "RMI Port", "IP Port number", true, entry.RMI_PORT));
        if ((entry.INTRASERVERPASSWORD == null || entry.INTRASERVERPASSWORD.equals("")) && entry.isLocal) {
            entry.INTRASERVERPASSWORD = generateRandomPassword();
            this.changedProperties.add(PropertyType.INTRASERVERPASSWORD);
            this.saveNewGui = true;
        }
        this.list.add((Object)new CustomPropertyItem(PropertyType.INTRASERVERPASSWORD, "1: Server Settings", "Intra server password", "Server Cross-Communication password. Used for connecting servers to eachother.", true, entry.INTRASERVERPASSWORD));
        this.list.add((Object)new CustomPropertyItem(PropertyType.LOGINSERVER, "1: Server Settings", "Login Server", "The login server is the central cluster node responsible for bank accounts and cross communication", true, entry.LOGINSERVER));
        this.list.add((Object)new CustomPropertyItem(PropertyType.PVPSERVER, "1: Server Settings", "Allows PvP", "Allowing Player Versus Player combat and theft", true, entry.PVPSERVER));
        this.list.add((Object)new CustomPropertyItem(PropertyType.HOMESERVER, "1: Server Settings", "Home Server", "A Home server can only have settlements from one kingdom", true, entry.HOMESERVER));
        this.list.add((Object)new CustomPropertyItem(PropertyType.KINGDOM, "1: Server Settings", "Home Server Kingdom", "Which kingdom the Home server should have, 0 = No kingdom, 1 = Jenn-Kellon, 2 = Mol Rehan, 3 = Horde of the Summoned, 4 = Freedom Isles", true, entry.KINGDOM));
        if (this.saveNewGui) {
            this.saveNewGui = false;
            if (entry.isCreating) {
                this.save();
            }
            else if (this.changedId) {
                this.current.saveNewGui(this.oldId);
                this.current.movePlayersFromId(this.oldId);
                this.changedId = false;
                Servers.moveServerId(this.current, this.oldId);
                this.oldId = 0;
            }
            else {
                entry.saveNewGui(entry.id);
            }
            this.changedProperties.clear();
            System.out.println("Saved new server");
        }
        (this.propertySheet = new PropertySheet(this.list)).setMode(PropertySheet.Mode.CATEGORY);
        VBox.setVgrow((Node)this.propertySheet, Priority.ALWAYS);
        this.getChildren().add((Object)this.propertySheet);
    }
    
    public void setReadOnly() {
        if (this.propertySheet != null) {
            this.propertySheet.setMode(PropertySheet.Mode.NAME);
            this.propertySheet.setDisable(true);
        }
    }
    
    public boolean haveChanges() {
        return !this.changedProperties.isEmpty();
    }
    
    public void AskIfSave() {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved changes!");
        alert.setHeaderText("There are unsaved changes in the local server tab");
        alert.setContentText("Do you want to save the changes?");
        final Optional<ButtonType> result = (Optional<ButtonType>)alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            this.save();
        }
    }
    
    public final ServerEntry getCurrentServerEntry() {
        return this.current;
    }
    
    static {
        logger = Logger.getLogger(ServerPropertySheet.class.getName());
    }
    
    private enum PropertyType
    {
        SERVERID, 
        EXTSERVERIP, 
        EXTSERVERPORT, 
        INTIP, 
        INTPORT, 
        MAXPLAYERS, 
        SKILLGAINRATE, 
        ACTIONTIMER, 
        RMIPORT, 
        RMI_REG_PORT, 
        PVPSERVER, 
        HOMESERVER, 
        SPAWNPOINTJENNX, 
        SPAWNPOINTJENNY, 
        SPAWNPOINTMOLX, 
        SPAWNPOINTMOLY, 
        SPAWNPOINTLIBX, 
        SPAWNPOINTLIBY, 
        KINGDOM, 
        INTRASERVERPASSWORD, 
        TESTSERVER, 
        NAME, 
        LOGINSERVER, 
        ISPAYMENT, 
        TWITTERCONSUMERKEY, 
        TWITTERCONSUMERSECRET, 
        TWITTERAPPTOKEN, 
        TWITTERAPPSECRET, 
        MAINTAINING, 
        HOTADELAY, 
        MAXCREATURES, 
        PERCENTAGG, 
        RANDOMSPAWNS, 
        SKBASIC, 
        SKFIGHT, 
        SKMIND, 
        SKOVERALL, 
        SKBC, 
        EPIC, 
        CRMOD, 
        STEAMPW, 
        UPKEEP, 
        MAXDEED, 
        FREEDEEDS, 
        TRADERMAX, 
        TRADERINIT, 
        TUNNELING, 
        BREEDING, 
        FIELDGROWTH, 
        KINGSMONEY, 
        MOTD, 
        NPCS, 
        STEAMQUERYPORT, 
        TREEGROWTH, 
        ADMIN_PWD, 
        ENDGAMEITEMS, 
        SPY_PREVENTION, 
        NEWBIE_FRIENDLY, 
        ENABLE_PNP_PORT_FORWARD;
    }
    
    class CustomPropertyItem implements PropertySheet.Item
    {
        private PropertyType type;
        private String category;
        private String name;
        private String description;
        private boolean editable;
        private Object value;
        private Object minValue;
        private Object maxValue;
        
        CustomPropertyItem(final PropertyType aType, final String aCategory, final String aName, final String aDescription, final boolean aEditable, final Object aValue) {
            this.editable = true;
            this.type = aType;
            this.category = aCategory;
            this.name = aName;
            this.description = aDescription;
            this.editable = aEditable;
            this.value = aValue;
        }
        
        CustomPropertyItem(final ServerPropertySheet this$0, final PropertyType aType, final String aCategory, final String aName, final String aDescription, final boolean aEditable, final Object aValue, final Object aMinValue) {
            this(this$0, aType, aCategory, aName, aDescription, aEditable, aValue);
            this.minValue = aMinValue;
        }
        
        CustomPropertyItem(final ServerPropertySheet this$0, final PropertyType aType, final String aCategory, final String aName, final String aDescription, final boolean aEditable, final Object aValue, final Object aMinValue, final Object aMaxValue) {
            this(this$0, aType, aCategory, aName, aDescription, aEditable, aValue, aMinValue);
            this.maxValue = aMaxValue;
        }
        
        public PropertyType getPropertyType() {
            return this.type;
        }
        
        @Override
        public Class<?> getType() {
            return this.value.getClass();
        }
        
        @Override
        public String getCategory() {
            return this.category;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public String getDescription() {
            return this.description;
        }
        
        @Override
        public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
            return super.getPropertyEditorClass();
        }
        
        @Override
        public boolean isEditable() {
            return this.editable;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        public Object getMinValue() {
            return this.minValue;
        }
        
        public Object getMaxValue() {
            return this.maxValue;
        }
        
        @Override
        public void setValue(final Object aValue) {
            if (!this.value.equals(aValue)) {
                ServerPropertySheet.this.changedProperties.add(this.type);
            }
            this.value = aValue;
        }
        
        @Override
        public Optional<ObservableValue<?>> getObservableValue() {
            return Optional.of((ObservableValue<?>)new SimpleObjectProperty(this.value));
        }
    }
    
    class ServerPropertySheetChangeListener<CustomPropertyItem> implements ListChangeListener
    {
        public void onChanged(final ListChangeListener.Change c) {
            System.out.println("Change occurred: " + c);
        }
    }
}
