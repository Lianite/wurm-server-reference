// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Arrays;
import com.wurmonline.server.Server;
import com.wurmonline.server.Message;
import com.wurmonline.server.players.Ban;
import com.wurmonline.server.Players;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.Servers;
import java.io.IOException;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.Player;
import java.util.LinkedList;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class GmInterface extends Question implements TimeConstants
{
    private static final Logger logger;
    private final LinkedList<Player> playlist;
    private final LinkedList<String> iplist;
    private static final String muteReasonOne = "Profane language";
    private static final String muteReasonTwo = "Racial or sexist remarks";
    private static final String muteReasonThree = "Staff bashing";
    private static final String muteReasonFour = "Harassment";
    private static final String muteReasonFive = "Spam";
    private static final String muteReasonSix = "Insubordination";
    private static final String muteReasonSeven = "Repeated warnings";
    private PlayerInfo playerInfo;
    private Player targetPlayer;
    private boolean doneSomething;
    
    public GmInterface(final Creature aResponder, final long aTarget) {
        super(aResponder, "Player Management", "How may we help you?", 83, aTarget);
        this.playlist = new LinkedList<Player>();
        this.iplist = new LinkedList<String>();
        this.doneSomething = false;
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            GmInterface.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 83 && (this.getResponder().getPower() >= 2 || this.getResponder().mayMute())) {
            this.doneSomething = false;
            String pname = aAnswer.getProperty("pname");
            if (pname == null || pname.length() == 0) {
                final String pid = aAnswer.getProperty("ddname");
                final int num = Integer.parseInt(pid);
                if (num > 0) {
                    this.targetPlayer = this.playlist.get(num - 1);
                    pname = this.targetPlayer.getName();
                    if (!this.targetPlayer.hasLink()) {
                        this.targetPlayer = null;
                    }
                }
            }
            final boolean nameSpecified = pname != null && pname.length() > 0;
            if (nameSpecified) {
                this.playerInfo = PlayerInfoFactory.createPlayerInfo(pname);
                try {
                    this.playerInfo.load();
                }
                catch (IOException iox) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Failed to load data for the player with name " + pname + ".");
                    return;
                }
                if (this.playerInfo == null || this.playerInfo.wurmId <= 0L) {
                    long[] info = { Servers.localServer.id, -1L };
                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                    try {
                        info = lsw.getCurrentServer(pname, -1L);
                    }
                    catch (Exception e) {
                        info = new long[] { -1L, -1L };
                    }
                    if (info[0] == -1L) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Player with name " + pname + " not found anywhere!.");
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Player with name " + pname + " has never been on this server, but is currently on server " + info[0] + ", their WurmId is " + info[1] + ".");
                    }
                    return;
                }
                if (this.getResponder().mayMute() || this.getResponder().getPower() >= 2) {
                    this.checkMute(pname);
                }
                if (this.getResponder().getPower() >= 2) {
                    this.checkBans(pname);
                    String key = "summon";
                    String val = aAnswer.getProperty(key);
                    if (val != null) {
                        final boolean summon = val.equals("true");
                        if (summon) {
                            if (pname.equalsIgnoreCase(this.getResponder().getName())) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("You cannot summon yourself.");
                            }
                            else {
                                this.doneSomething = true;
                                int tilex = this.getResponder().getTileX();
                                int tiley = this.getResponder().getTileY();
                                byte layer = (byte)this.getResponder().getLayer();
                                key = "tilex";
                                val = aAnswer.getProperty(key);
                                if (val != null && val.length() > 0) {
                                    tilex = Integer.parseInt(val);
                                }
                                key = "tiley";
                                val = aAnswer.getProperty(key);
                                if (val != null && val.length() > 0) {
                                    tiley = Integer.parseInt(val);
                                }
                                key = "surfaced";
                                val = aAnswer.getProperty(key);
                                if (val != null && val.length() > 0) {
                                    layer = (byte)(val.equals("true") ? 0 : -1);
                                }
                                QuestionParser.summon(pname, this.getResponder(), tilex, tiley, layer);
                            }
                        }
                    }
                    key = "locate";
                    val = aAnswer.getProperty(key);
                    if (val != null) {
                        final boolean locate = val.equals("true");
                        if (locate) {
                            GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " locating " + pname);
                            this.doneSomething = true;
                            if (!LocatePlayerQuestion.locateCorpse(pname, this.getResponder(), 100.0, true)) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("No such soul found.");
                            }
                        }
                    }
                }
            }
            if (!this.doneSomething && this.getResponder().getPower() >= 2) {
                final String key = "gmtool";
                final String val = aAnswer.getProperty("gmtool");
                if (val != null) {
                    final boolean gmtool = val.equals("true");
                    if (gmtool) {
                        final String strWurmId = aAnswer.getProperty("wurmid");
                        final String searchemail = aAnswer.getProperty("searchemail");
                        final String searchip = aAnswer.getProperty("searchip");
                        final boolean wurmIdSpecified = strWurmId != null && strWurmId.length() > 0;
                        final boolean searchemailSpecified = searchemail != null && searchemail.length() > 0;
                        final boolean searchipSpecified = searchip != null && searchip.length() > 0;
                        int optCount = 0;
                        if (nameSpecified) {
                            ++optCount;
                        }
                        if (wurmIdSpecified) {
                            ++optCount;
                        }
                        if (searchemailSpecified) {
                            ++optCount;
                        }
                        if (searchipSpecified) {
                            ++optCount;
                        }
                        if (optCount != 1) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Name or Number or Email search or IP search must be specified");
                        }
                        else {
                            long toolWurmId = -1L;
                            byte toolType = 1;
                            byte toolSubType = 1;
                            String toolSearch = "";
                            if (this.getResponder().getLogger() != null && this.playerInfo != null) {
                                this.getResponder().getLogger().log(Level.INFO, "GM Tool for " + this.playerInfo.getName() + ", " + this.playerInfo.wurmId);
                            }
                            Label_1110: {
                                if (nameSpecified) {
                                    toolWurmId = this.playerInfo.wurmId;
                                }
                                else {
                                    if (wurmIdSpecified) {
                                        try {
                                            toolWurmId = Long.parseLong(strWurmId);
                                            break Label_1110;
                                        }
                                        catch (Exception e2) {
                                            this.getResponder().getCommunicator().sendNormalServerMessage("Wurm ID is not a number!");
                                            return;
                                        }
                                    }
                                    if (searchemailSpecified) {
                                        toolType = 2;
                                        toolSubType = 1;
                                        toolSearch = searchemail;
                                    }
                                    else if (searchipSpecified) {
                                        toolType = 2;
                                        toolSubType = 2;
                                        toolSearch = searchip;
                                    }
                                }
                            }
                            this.doneSomething = true;
                            final GmTool gt = new GmTool(this.getResponder(), toolType, toolSubType, toolWurmId, toolSearch, "", 50, (byte)0);
                            gt.sendQuestion();
                        }
                    }
                }
                if (!this.doneSomething) {
                    if (!nameSpecified) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("No player name provided. Doing nothing!");
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Nothing selected. Doing nothing!");
                    }
                }
            }
        }
    }
    
    private void checkMute(final String pname) {
        String muteReason = this.getAnswer().getProperty("mutereason");
        final String mutereasontb = this.getAnswer().getProperty("mutereasontb");
        if (mutereasontb != null && mutereasontb.length() > 2) {
            muteReason = mutereasontb;
        }
        else if (muteReason != null && muteReason.length() > 0) {
            try {
                final int reason = Integer.parseInt(muteReason);
                switch (reason) {
                    case 0: {
                        muteReason = "Profane language";
                        break;
                    }
                    case 1: {
                        muteReason = "Racial or sexist remarks";
                        break;
                    }
                    case 2: {
                        muteReason = "Staff bashing";
                        break;
                    }
                    case 3: {
                        muteReason = "Harassment";
                        break;
                    }
                    case 4: {
                        muteReason = "Spam";
                        break;
                    }
                    case 5: {
                        muteReason = "Insubordination";
                        break;
                    }
                    case 6: {
                        muteReason = "Repeated warnings";
                        break;
                    }
                    default: {
                        GmInterface.logger.warning("Unexpected parsed value: " + reason + " from mute reason: " + muteReason + ". Responder: " + this.getResponder());
                        break;
                    }
                }
            }
            catch (NumberFormatException nfe) {
                GmInterface.logger.log(Level.WARNING, "Problem parsing the mute reason: " + muteReason + ". Responder: " + this.getResponder() + " due to " + nfe, nfe);
            }
        }
        String key = "mutewarn";
        String val = this.getAnswer().getProperty(key);
        if (val != null && val.equals("true") && this.targetPlayer != null) {
            if (this.targetPlayer.getPower() <= this.getResponder().getPower()) {
                this.logMgmt("mutewarns " + pname + " (" + muteReason + ")");
                this.targetPlayer.getCommunicator().sendAlertServerMessage(this.getResponder().getName() + " issues a warning that you may be muted. Be silent for a while and try to understand why or change the subject of your conversation please.");
                if (muteReason.length() > 0) {
                    this.targetPlayer.getCommunicator().sendAlertServerMessage("The reason for this is '" + muteReason + "'");
                }
                this.getResponder().getCommunicator().sendSafeServerMessage("You warn " + this.targetPlayer.getName() + " that " + this.targetPlayer.getHeSheItString() + " may be muted.");
                this.getResponder().getCommunicator().sendSafeServerMessage("The reason you gave was '" + muteReason + "'.");
                this.doneSomething = true;
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You threaten " + pname + " with muting!");
                this.targetPlayer.getCommunicator().sendNormalServerMessage(this.getResponder().getName() + " tried to threaten you with muting!");
                if (muteReason.length() > 0) {
                    this.targetPlayer.getCommunicator().sendNormalServerMessage("The formal reason for this is '" + muteReason + "'");
                }
                this.doneSomething = true;
            }
        }
        key = "unmute";
        val = this.getAnswer().getProperty(key);
        if (val != null && val.equals("true")) {
            if (this.playerInfo != null) {
                this.playerInfo.setMuted(false, "", 0L);
                this.logMgmt("unmutes " + pname);
                this.doneSomething = true;
                this.getResponder().getCommunicator().sendNormalServerMessage("You have given " + pname + " the voice back.");
            }
            if (this.targetPlayer != null) {
                this.targetPlayer.getCommunicator().sendAlertServerMessage("You have been given your voice back and can shout again.");
            }
        }
        int hours = 1;
        key = "mute";
        val = this.getAnswer().getProperty(key);
        long expiry = 0L;
        if (val != null && val.equals("true")) {
            GmInterface.logger.log(Level.INFO, "Muting");
            this.doneSomething = true;
            final String muteTime = this.getAnswer().getProperty("mutetime");
            if (muteTime != null && muteTime.length() > 0) {
                try {
                    final int index = Integer.parseInt(muteTime);
                    switch (index) {
                        case 0: {
                            hours = 1;
                            break;
                        }
                        case 1: {
                            hours = 2;
                            break;
                        }
                        case 2: {
                            hours = 5;
                            break;
                        }
                        case 3: {
                            hours = 8;
                            break;
                        }
                        case 4: {
                            hours = 24;
                            break;
                        }
                        case 5: {
                            hours = 48;
                            break;
                        }
                        default: {
                            GmInterface.logger.warning("Unexpected muteTime value: " + muteTime + ". Responder: " + this.getResponder());
                            break;
                        }
                    }
                    expiry = System.currentTimeMillis() + hours * 3600000L;
                }
                catch (NumberFormatException nfe2) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("An error occurred with the number of hours for the mute: " + muteTime + ".");
                    return;
                }
            }
            if (expiry > System.currentTimeMillis()) {
                GmInterface.logger.log(Level.INFO, "Muting");
                if (this.playerInfo != null) {
                    if (this.playerInfo.getPower() < this.getResponder().getPower() || this.playerInfo.getPower() == 0) {
                        this.playerInfo.setMuted(true, muteReason, expiry);
                        this.getResponder().getCommunicator().sendNormalServerMessage("You have muted " + this.playerInfo.getName() + " for " + hours + " hours.");
                        this.logMgmt("muted " + pname + " for " + hours + " hours. Reason: " + muteReason);
                        if (this.targetPlayer != null) {
                            this.targetPlayer.getCommunicator().sendAlertServerMessage("You have been muted by " + this.getResponder().getName() + " for " + hours + " hours and cannot shout anymore. Reason: " + muteReason);
                        }
                        else {
                            this.getResponder().getCommunicator().sendNormalServerMessage(this.playerInfo.getName() + " is offline.");
                        }
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You are too weak to mute " + pname + '!');
                    }
                }
            }
        }
    }
    
    private void checkBans(final String pname) {
        final String banTime = this.getAnswer().getProperty("bantime");
        long expiry = 0L;
        int days = 0;
        String banReason = this.getAnswer().getProperty("banreason");
        if (banReason == null || banReason.length() < 2) {
            banReason = "Banned";
        }
        String key = "pardon";
        String val = this.getAnswer().getProperty(key);
        if (val != null && val.equals("true")) {
            this.doneSomething = true;
            if (this.playerInfo != null && this.playerInfo.isBanned()) {
                try {
                    this.playerInfo.setBanned(false, "", 0L);
                    final String bip = this.playerInfo.getIpaddress();
                    Players.getInstance().removeBan(bip);
                    this.getResponder().getCommunicator().sendSafeServerMessage("You have gratiously pardoned " + pname + " and the ipaddress " + bip);
                    this.log("pardons player " + pname + " and ipaddress " + bip + '.');
                }
                catch (IOException ex2) {}
            }
        }
        if (banTime != null && banTime.length() > 0) {
            try {
                final int index = Integer.parseInt(banTime);
                switch (index) {
                    case 0: {
                        days = 1;
                        break;
                    }
                    case 1: {
                        days = 3;
                        break;
                    }
                    case 2: {
                        days = 7;
                        break;
                    }
                    case 3: {
                        days = 30;
                        break;
                    }
                    case 4: {
                        days = 90;
                        break;
                    }
                    case 5: {
                        days = 365;
                        break;
                    }
                    case 6: {
                        days = 9999;
                        break;
                    }
                    default: {
                        GmInterface.logger.warning("Unexpected banTime value: " + banTime + ". Responder: " + this.getResponder());
                        break;
                    }
                }
                expiry = System.currentTimeMillis() + days * 86400000L;
            }
            catch (NumberFormatException nfe2) {
                this.getResponder().getCommunicator().sendNormalServerMessage("An error occurred with the number of days for the ban: " + banTime + ".");
                return;
            }
        }
        boolean bannedip = false;
        key = "ban";
        val = this.getAnswer().getProperty(key);
        if (val != null && val.equals("true") && expiry > System.currentTimeMillis()) {
            this.doneSomething = true;
            try {
                if (this.targetPlayer != null && this.targetPlayer.hasLink()) {
                    this.targetPlayer.getCommunicator().sendAlertServerMessage("You have been banned for " + days + " days and thrown out from the game. The reason is " + banReason, (byte)1);
                    this.targetPlayer.setFrozen(true);
                    this.targetPlayer.logoutIn(10, "banned");
                    this.getResponder().getCommunicator().sendSafeServerMessage(String.format("Player %s was successfully found and will be removed from the world in 10 seconds.", this.targetPlayer.getName()));
                }
                else {
                    this.getResponder().getCommunicator().sendSafeServerMessage(String.format("Something went wrong and %s was not removed from the world. You may need to kick them.", this.playerInfo.getName()));
                }
                this.playerInfo.setBanned(true, banReason, expiry);
                key = "banip";
                val = this.getAnswer().getProperty(key);
                if (val != null && val.equals("true")) {
                    bannedip = true;
                    final String bip2 = this.playerInfo.getIpaddress();
                    this.getResponder().getCommunicator().sendSafeServerMessage("You ban and kick " + pname + ". The server won't accept connections from " + bip2 + " anymore.", (byte)1);
                    this.log("bans player " + pname + " for " + days + " days and ipaddress " + bip2 + " for " + Math.min(days, 7) + " days.");
                    if (Servers.localServer.LOGINSERVER) {
                        Players.getInstance().addBannedIp(bip2, "[" + pname + "] " + banReason, Math.min(expiry, System.currentTimeMillis() + 604800000L));
                    }
                    else {
                        try {
                            final LoginServerWebConnection c = new LoginServerWebConnection();
                            this.getResponder().getCommunicator().sendSafeServerMessage(c.addBannedIp(bip2, "[" + pname + "] " + banReason, Math.min(days, 7)));
                        }
                        catch (Exception e) {
                            this.getResponder().getCommunicator().sendAlertServerMessage("Failed to ban on login server:" + e.getMessage());
                            GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " banning ip on login server failed: " + e.getMessage(), e);
                        }
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendSafeServerMessage("You ban and kick " + pname + ". No IP ban was issued.");
                    this.log("bans player " + pname + " for " + days + " days. No IP ban was issued.");
                }
                if (!Servers.localServer.LOGINSERVER) {
                    try {
                        final LoginServerWebConnection c2 = new LoginServerWebConnection();
                        this.getResponder().getCommunicator().sendSafeServerMessage(c2.ban(pname, banReason, days));
                    }
                    catch (Exception e2) {
                        this.getResponder().getCommunicator().sendAlertServerMessage("Failed to ban on login server:" + e2.getMessage());
                        GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " banning " + pname + " on login server failed: " + e2.getMessage(), e2);
                    }
                }
            }
            catch (IOException e3) {
                this.getResponder().getCommunicator().sendAlertServerMessage("Failed to ban on local server:" + e3.getMessage());
                GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " banning " + pname + " on local server failed: " + e3.getMessage(), e3);
            }
        }
        key = "banip";
        val = this.getAnswer().getProperty(key);
        if (!bannedip && val != null && val.equals("true") && expiry > System.currentTimeMillis()) {
            this.doneSomething = true;
            boolean ok = true;
            String ipToBan = this.getAnswer().getProperty("iptoban");
            if (ipToBan == null || ipToBan.length() < 5) {
                ok = false;
            }
            if (ok) {
                try {
                    if (ipToBan.charAt(0) != '/') {
                        ipToBan = '/' + ipToBan;
                    }
                }
                catch (Exception ex) {
                    ok = false;
                }
            }
            if (ok) {
                final int dots = ipToBan.indexOf(42);
                if (dots > 0 && dots < 5) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("Failed to ban the ip. The ip address must be at least 5 characters long.");
                    return;
                }
                final Player[] players = Players.getInstance().getPlayers();
                for (int x = 0; x < players.length; ++x) {
                    if (players[x].hasLink()) {
                        boolean ban = players[x].getCommunicator().getConnection().getIp().equals(ipToBan);
                        if (!ban && dots > 0) {
                            ban = players[x].getCommunicator().getConnection().getIp().startsWith(ipToBan.substring(0, dots));
                        }
                        if (ban) {
                            if (players[x].getPower() < this.getResponder().getPower()) {
                                Players.getInstance().logoutPlayer(players[x]);
                            }
                            else {
                                ok = false;
                                this.getResponder().getCommunicator().sendNormalServerMessage("You cannot kick " + players[x].getName() + '!');
                                players[x].getCommunicator().sendAlertServerMessage(this.getResponder().getName() + " tried to kick you from the game and ban your ip.");
                            }
                        }
                    }
                }
                if (Servers.localServer.LOGINSERVER) {
                    Players.getInstance().addBannedIp(ipToBan, banReason, expiry);
                }
                else {
                    try {
                        final LoginServerWebConnection c3 = new LoginServerWebConnection();
                        this.getResponder().getCommunicator().sendSafeServerMessage(c3.addBannedIp(ipToBan, banReason, days));
                    }
                    catch (Exception e4) {
                        this.getResponder().getCommunicator().sendAlertServerMessage("Failed to ban on login server:" + e4.getMessage());
                        GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " banning ip on login server failed: " + e4.getMessage(), e4);
                    }
                }
                this.getResponder().getCommunicator().sendSafeServerMessage("You ban " + ipToBan + " for " + days + " days. The server won't accept connections from " + ipToBan + " anymore.");
                this.log("bans ipaddress " + ipToBan + " for " + days + " days. Reason " + banReason);
            }
        }
        key = "warn";
        val = this.getAnswer().getProperty(key);
        if (val != null && val.equals("true")) {
            this.doneSomething = true;
            final long lastWarned = this.playerInfo.getLastWarned();
            try {
                this.playerInfo.warn();
            }
            catch (IOException iox) {
                GmInterface.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            final String wst = this.playerInfo.getWarningStats(lastWarned);
            this.getResponder().getCommunicator().sendSafeServerMessage("You have officially warned " + pname + ". " + wst, (byte)1);
            if (this.targetPlayer != null) {
                this.targetPlayer.getCommunicator().sendAlertServerMessage("You have just received an official warning. Too many of these will get you banned from the game.", (byte)1);
            }
            this.log("issues an official warning to " + pname + '.');
        }
        key = "resetWarn";
        val = this.getAnswer().getProperty(key);
        if (val != null && val.equals("true")) {
            this.doneSomething = true;
            try {
                this.playerInfo.resetWarnings();
                this.getResponder().getCommunicator().sendSafeServerMessage("You have officially removed the warnings for " + pname + '.');
                if (this.targetPlayer != null) {
                    this.targetPlayer.getCommunicator().sendSafeServerMessage("Your warnings have just been officially removed.", (byte)1);
                }
                this.log("removes warnings for " + pname + '.');
            }
            catch (IOException iox2) {
                GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " fails to reset warnings for " + pname + '.', iox2);
            }
        }
        key = "pardonip";
        val = this.getAnswer().getProperty(key);
        if (val != null && val.length() > 0) {
            try {
                final int num = Integer.parseInt(val);
                if (num > 0) {
                    this.doneSomething = true;
                    final String ip = this.iplist.get(num - 1);
                    final Ban bip3 = Players.getInstance().getBannedIp(ip);
                    if (bip3 != null) {
                        if (Players.getInstance().removeBan(ip)) {
                            this.getResponder().getCommunicator().sendSafeServerMessage("You have gratiously pardoned the ipaddress " + ip, (byte)1);
                            this.log("pardons ipaddress " + ip + '.');
                            try {
                                final LoginServerWebConnection c4 = new LoginServerWebConnection();
                                this.getResponder().getCommunicator().sendSafeServerMessage(c4.removeBannedIp(ip));
                            }
                            catch (Exception e5) {
                                this.getResponder().getCommunicator().sendAlertServerMessage("Failed to remove ip ban on login server:" + e5.getMessage());
                                GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " removing ip ban " + bip3 + " on login server failed: " + e5.getMessage(), e5);
                            }
                        }
                        else {
                            this.getResponder().getCommunicator().sendAlertServerMessage("Failed to unban ip " + ip + '.');
                        }
                    }
                }
            }
            catch (NumberFormatException nfe) {
                GmInterface.logger.log(Level.WARNING, "Problem parsing the pardonip value: " + val + ". Responder: " + this.getResponder() + " due to " + nfe, nfe);
            }
        }
    }
    
    private final void logMgmt(final String logString) {
        if (this.getResponder().getLogger() != null) {
            this.getResponder().getLogger().log(Level.INFO, this.getResponder().getName() + " " + logString);
        }
        GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " " + logString);
        Players.addMgmtMessage(this.getResponder().getName(), logString);
        final Message mess = new Message(this.getResponder(), (byte)9, "MGMT", "<" + this.getResponder().getName() + "> " + logString);
        Server.getInstance().addMessage(mess);
    }
    
    private final void log(final String logString) {
        if (this.getResponder().getLogger() != null) {
            this.getResponder().getLogger().log(Level.INFO, this.getResponder().getName() + " " + logString);
        }
        GmInterface.logger.log(Level.INFO, this.getResponder().getName() + " " + logString);
        Players.addGmMessage(this.getResponder().getName(), logString);
        final Message mess = new Message(this.getResponder(), (byte)11, "GM", "<" + this.getResponder().getName() + "> " + logString);
        Server.getInstance().addMessage(mess);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getPower() >= 2 || this.getResponder().mayMute()) {
            final Player[] players = Players.getInstance().getPlayers();
            Arrays.sort(players);
            buf.append("text{type=\"bold\";text=\"---------------- Select Player ------------------\"}");
            String displayWarnings = "";
            if (this.getResponder().getPower() >= 2) {
                displayWarnings = " - warnings:";
            }
            buf.append("harray{label{text=\"Player name - (m)uted" + displayWarnings + "\"}");
            buf.append("dropdown{id='ddname';options=\"");
            buf.append("Use textbox");
            final StringBuilder chattingBuild = new StringBuilder();
            for (int x = 0; x < players.length; ++x) {
                if (players[x].getWurmId() != this.getResponder().getWurmId() && players[x].getPower() <= this.getResponder().getWurmId() && (this.getResponder().getPower() >= 2 || (this.getResponder().getKingdomId() == players[x].getKingdomId() && players[x].isActiveInChat()))) {
                    buf.append(",");
                    this.playlist.add(players[x]);
                    buf.append(players[x].getName());
                    if (players[x].isMute()) {
                        buf.append(" (m)");
                    }
                    if (this.getResponder().getPower() >= 2 && players[x].getWarnings() > 0) {
                        buf.append(" - ");
                        buf.append(players[x].getWarnings());
                    }
                    if (this.getResponder().getPower() >= 2 && players[x].isActiveInChat()) {
                        chattingBuild.append(players[x].getName() + " ");
                    }
                }
            }
            buf.append("\"}label{text=\"  \"};input{id='pname';maxchars='32'}}");
            buf.append("text{text=\"\"};");
            buf.append("text{type=\"bold\";text=\"-------------- Chat control --------------------\"}");
            if (this.getResponder().getPower() > 0) {
                buf.append("text{text=\"Recently active in kchat:\"}");
                buf.append("text{text=\"" + chattingBuild.toString() + "\"}");
            }
            buf.append("harray{checkbox{id=\"mute\";text=\"Mute \"};checkbox{id=\"unmute\";text=\"Unmute \"};checkbox{id=\"mutewarn\";text=\"Mutewarn \"};dropdown{id='mutereason';options=\"");
            buf.append("Profane language");
            buf.append(", ");
            buf.append("Racial or sexist remarks");
            buf.append(", ");
            buf.append("Staff bashing");
            buf.append(", ");
            buf.append("Harassment");
            buf.append(", ");
            buf.append("Spam");
            buf.append(", ");
            buf.append("Insubordination");
            buf.append(", ");
            buf.append("Repeated warnings");
            buf.append("\"}");
            buf.append("label{text=\"Hours:\"};dropdown{id='mutetime';default='0';options=\"");
            buf.append(1);
            buf.append(", ");
            buf.append(2);
            buf.append(", ");
            buf.append(5);
            buf.append(", ");
            buf.append(8);
            buf.append(", ");
            buf.append(24);
            buf.append(", ");
            buf.append(48);
            buf.append("\"}}");
            buf.append("harray{label{text=\"Or enter reason:\"};input{maxchars=\"40\";id=\"mutereasontb\"};}");
            buf.append("text{text=\"\"};");
            if (this.getResponder().getPower() >= 2) {
                buf.append("text{type=\"bold\";text=\"---------------- Summon ------------------\"}");
                String sel = ";selected=\"true\"";
                if (!this.getResponder().isOnSurface()) {
                    sel = ";selected=\"false\"";
                }
                buf.append("harray{checkbox{id='summon';selected='false';text=\"Teleport/Set to \"};label{text=\"TX:\"};input{id='tilex';maxchars='5';text=\"" + this.getResponder().getTileX() + "\"};label{text=\"TY:\"};input{id='tiley';maxchars='5';text=\"" + this.getResponder().getTileY() + "\"};checkbox{id='surfaced'" + sel + ";text=\"Surfaced \"}}");
                buf.append("text{text=\"\"};");
                buf.append("text{type=\"bold\";text=\"--------------- IPBan control -------------------\"}");
                buf.append("harray{checkbox{id=\"pardon\";text=\"Pardon ban \"};checkbox{id=\"ban\";text=\"IPBan \"};checkbox{id=\"banip\";text=\"IPBan IP \"};checkbox{id=\"warn\";text=\"Warn \"};checkbox{id=\"resetWarn\";text=\"Reset warnings \"}};");
                buf.append("harray{label{text=\"Ip to ban:\"};input{id='iptoban';maxchars='16'}};");
                buf.append("harray{label{text=\"IPBan reason (max 250 chars):\"};input{id='banreason';text=\"Griefing\"}};");
                buf.append("harray{label{text=\"Days:\"};dropdown{id='bantime';default=\"1\";options=\"");
                buf.append(1);
                buf.append(", ");
                buf.append(3);
                buf.append(", ");
                buf.append(7);
                buf.append(", ");
                buf.append(30);
                buf.append(", ");
                buf.append(90);
                buf.append(", ");
                buf.append(365);
                buf.append(", ");
                buf.append(9999);
                buf.append("\"}}");
                buf.append("text{text=\"\"};");
                buf.append("text{type=\"bold\";text=\"--------------- Bans -------------------\"}");
                buf.append("harray{label{text=\"Pardon:\"};dropdown{id='pardonip';options=\"");
                buf.append("None");
                final Ban[] bans = Players.getInstance().getBans();
                for (int x2 = 0; x2 < bans.length; ++x2) {
                    buf.append(", ");
                    buf.append(bans[x2].getIdentifier());
                    this.iplist.add(bans[x2].getIdentifier());
                }
                buf.append("\"}};");
                buf.append("text{text=\"\"};");
                buf.append("text{type=\"bold\";text=\"---------------- Locate Corpse ------------------\"}");
                buf.append("text{type=\"italic\";text=\"Uses name at top of form now.\"}");
                buf.append("harray{checkbox{id=\"locate\";text=\"Locate Corpse? \"}}");
                buf.append("text{text=\"\"};");
                buf.append("text{type=\"bold\";text=\"---------------- GM Tool (In-Game GM Interface) ------------------\"}");
                buf.append("text{type=\"italic\";text=\"This should only be used on the server pertaining to the item or player.\"}");
                buf.append("checkbox{id=\"gmtool\";text=\"Start GM Tool? \"}");
                buf.append("label{text=\"Either select player name at top\"}");
                buf.append("harray{label{text=\"or specify a WurmId: \"};input{id=\"wurmid\";maxchars=\"20\";text=\"\"}}");
                buf.append("harray{label{text=\"or specify an Email Address to search for: \"};input{id=\"searchemail\";maxchars=\"60\";text=\"\"}}");
                buf.append("harray{label{text=\"or specify an IP Address to search for: \"};input{id=\"searchip\";maxchars=\"30\";text=\"\"}}");
                buf.append("text{text=\"\"};");
            }
            buf.append("text{type=\"bold\";text=\"--------------- Help -------------------\"}");
            buf.append("text{text=\"Either type a name in the textbox or select a name from the list.\"}");
            buf.append("text{text=\"You may check as many boxes you wish and all options will apply to the player you select.\"}");
            if (this.getResponder().getPower() >= 2) {
                buf.append("text{text=\"If you cross the ban ip checkbox when a player is selected, his ip will be banned for 7 days max.\"}");
                buf.append("text{text=\"If you want to extend this ban you will have to type it in the ip address box.\"}");
            }
            buf.append(this.createAnswerButton2());
            final int len = (this.getResponder().getPower() >= 2) ? 500 : 300;
            this.getResponder().getCommunicator().sendBml(500, len, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
    
    static {
        logger = Logger.getLogger(GmInterface.class.getName());
    }
}
