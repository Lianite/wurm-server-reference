// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import com.wurmonline.server.kingdom.Kingdom;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.Spawnpoint;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.io.IOException;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.Titles;
import java.util.logging.Level;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.economy.MonetaryConstants;

public final class PortalQuestion extends Question implements MonetaryConstants, TimeConstants
{
    private final Item portal;
    private static final Logger logger;
    private static final int maxItems = 200;
    private static final int standardBodyInventoryItems = 12;
    public static final int PORTAL_FREEDOM_ID = 100000;
    public static final int PORTAL_EPIC_ID = 100001;
    public static final int PORTAL_CHALLENGE_ID = 100002;
    public static final boolean allowPortalToLatestServer = true;
    private int step;
    private int selectedServer;
    private byte selectedKingdom;
    private int selectedSpawn;
    public static boolean epicPortalsEnabled;
    private String cyan;
    private String green;
    private String orange;
    private String purple;
    private String red;
    
    public PortalQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final Item _portal) {
        super(aResponder, aTitle, aQuestion, 76, _portal.getWurmId());
        this.step = 0;
        this.selectedServer = 100000;
        this.selectedKingdom = 0;
        this.selectedSpawn = -1;
        this.cyan = "66,200,200";
        this.green = "66,225,66";
        this.orange = "255,156,66";
        this.purple = "166,166,66";
        this.red = "255,66,66";
        this.portal = _portal;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        final String val = aAnswers.getProperty("portalling");
        this.getResponder().sendToLoggers(" at A: " + val + " selectedServer=" + this.selectedServer);
        if (val != null && val.equals("true")) {
            if (this.portal != null) {
                byte targetKingdom = 0;
                int data1 = this.portal.getData1();
                if (this.step == 1) {
                    data1 = this.selectedServer;
                }
                this.getResponder().sendToLoggers(" at A: " + val + " selectedServer=" + data1);
                ServerEntry entry = Servers.getServerWithId(data1);
                if (entry != null) {
                    this.getResponder().sendToLoggers(" at 1: " + data1);
                    if (entry.id == Servers.loginServer.id) {
                        entry = Servers.loginServer;
                    }
                    boolean changingCluster = false;
                    final boolean newTutorial = this.portal.getTemplateId() == 855;
                    if (Servers.localServer.EPIC != entry.EPIC && !newTutorial) {
                        changingCluster = true;
                        if (!this.portal.isEpicPortal()) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens. This is not an epic portal.");
                            return;
                        }
                        if (!PortalQuestion.epicPortalsEnabled && this.getResponder().getPower() == 0) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The portal won't let you just yet.");
                            return;
                        }
                    }
                    else if (Servers.localServer.EPIC && this.getResponder().isChampion() && !this.portal.isEpicPortal()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens. You could not use this portal since you are a champion.");
                        return;
                    }
                    if (this.getResponder().getEnemyPresense() > 0) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens. You sense a disturbance.");
                        return;
                    }
                    if (this.getResponder().hasBeenAttackedWithin(300)) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens. You sense a disturbance - maybe your are not calm enough yet.");
                        return;
                    }
                    if (Servers.localServer.isChallengeServer()) {
                        changingCluster = true;
                    }
                    if (Servers.localServer.entryServer) {
                        changingCluster = false;
                    }
                    if (changingCluster && this.getResponder().isChampion() && !Servers.localServer.EPIC && !this.portal.isEpicPortal()) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens. You could not use this portal since you are a champion.");
                        return;
                    }
                    if (this.getResponder().getPower() == 0 && entry.entryServer && !Servers.localServer.testServer) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens.");
                        return;
                    }
                    if (this.portal.isEpicPortal()) {
                        if (!changingCluster && !Servers.localServer.entryServer) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Nothing happens. Actually this shouldn't be possible.");
                            return;
                        }
                        final long time = System.currentTimeMillis() - ((Player)this.getResponder()).getSaveFile().lastUsedEpicPortal;
                        if (this.getResponder().getEpicServerKingdom() == 0) {
                            final String kingdomid = "kingdid";
                            final String kval = aAnswers.getProperty("kingdid");
                            if (kval != null) {
                                try {
                                    targetKingdom = Byte.parseByte(kval);
                                }
                                catch (NumberFormatException nfe2) {
                                    PortalQuestion.logger.log(Level.WARNING, "Failed to parse " + kval + " to a valid byte.");
                                    this.getResponder().getCommunicator().sendAlertServerMessage("An error occured with the target kingdom. You can't select that kingdom.");
                                    return;
                                }
                            }
                        }
                        else {
                            targetKingdom = this.getResponder().getEpicServerKingdom();
                            if (Servers.isThisAChaosServer()) {
                                PortalQuestion.logger.log(Level.INFO, this.getResponder().getName() + " joining " + targetKingdom);
                            }
                        }
                        ((Player)this.getResponder()).getSaveFile().setEpicLocation(targetKingdom, entry.id);
                        this.getResponder().setRotation(270.0f);
                        int targetTileX = entry.SPAWNPOINTJENNX;
                        int targetTileY = entry.SPAWNPOINTJENNY;
                        if (targetKingdom == 2) {
                            this.getResponder().setRotation(90.0f);
                            targetTileX = entry.SPAWNPOINTMOLX;
                            targetTileY = entry.SPAWNPOINTMOLY;
                        }
                        else if (targetKingdom == 3) {
                            this.getResponder().setRotation(1.0f);
                            targetTileX = entry.SPAWNPOINTLIBX;
                            targetTileY = entry.SPAWNPOINTLIBY;
                        }
                        if (Servers.localServer.entryServer && this.getResponder().isPlayer()) {
                            ((Player)this.getResponder()).addTitle(Titles.Title.Educated);
                        }
                        if (this.getResponder().isPlayer()) {
                            ((Player)this.getResponder()).getSaveFile().setBed(this.portal.getWurmId());
                        }
                        this.getResponder().sendTransfer(Server.getInstance(), entry.INTRASERVERADDRESS, Integer.parseInt(entry.INTRASERVERPORT), entry.INTRASERVERPASSWORD, entry.id, targetTileX, targetTileY, true, this.getResponder().getPower() <= 0, targetKingdom);
                    }
                    else {
                        if (entry.HOMESERVER) {
                            if (entry.KINGDOM != 0) {
                                targetKingdom = entry.KINGDOM;
                            }
                            else {
                                targetKingdom = ((this.selectedKingdom == 0) ? this.getResponder().getKingdomId() : this.selectedKingdom);
                            }
                        }
                        else {
                            final String kingdomid2 = "kingdid";
                            final String kval2 = aAnswers.getProperty("kingdid");
                            if (kval2 != null) {
                                try {
                                    targetKingdom = Byte.parseByte(kval2);
                                    this.getResponder().sendToLoggers(" at kingdid: " + entry.getName() + " selected kingdom " + targetKingdom);
                                }
                                catch (NumberFormatException nfe3) {
                                    targetKingdom = this.getResponder().getKingdomId();
                                }
                            }
                            else {
                                targetKingdom = ((this.selectedKingdom == 0) ? this.getResponder().getKingdomId() : this.selectedKingdom);
                            }
                        }
                        this.getResponder().sendToLoggers(" at 1: " + entry.getName() + " target kingdom " + targetKingdom);
                        if (entry.isAvailable(this.getResponder().getPower(), this.getResponder().isReallyPaying())) {
                            if (!entry.ISPAYMENT || this.getResponder().isReallyPaying()) {
                                int numitems = 0;
                                int stayBehind = 0;
                                final Item[] inventoryItems = this.getResponder().getInventory().getAllItems(true);
                                for (int x = 0; x < inventoryItems.length; ++x) {
                                    if (!inventoryItems[x].willLeaveServer(true, changingCluster, this.getResponder().getPower() > 0)) {
                                        ++stayBehind;
                                        this.getResponder().getCommunicator().sendNormalServerMessage("The " + inventoryItems[x].getName() + " stays behind.");
                                    }
                                }
                                final Item[] bodyItems = this.getResponder().getBody().getAllItems();
                                for (int x2 = 0; x2 < bodyItems.length; ++x2) {
                                    if (!bodyItems[x2].willLeaveServer(true, changingCluster, this.getResponder().getPower() > 0)) {
                                        ++stayBehind;
                                        this.getResponder().getCommunicator().sendNormalServerMessage("The " + bodyItems[x2].getName() + " stays behind.");
                                    }
                                }
                                if (this.getResponder().getPower() == 0) {
                                    numitems = inventoryItems.length + bodyItems.length - stayBehind - 12;
                                }
                                if (numitems < 200) {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("You step through the portal. Will you ever return?");
                                    if (this.getResponder().getPower() == 0 && changingCluster) {
                                        try {
                                            this.getResponder().setLastKingdom();
                                            this.getResponder().getStatus().setKingdom(targetKingdom);
                                        }
                                        catch (IOException iox) {
                                            this.getResponder().getCommunicator().sendNormalServerMessage("A sudden strong wind blows through the portal, throwing you back!");
                                            PortalQuestion.logger.log(Level.WARNING, iox.getMessage(), iox);
                                            return;
                                        }
                                    }
                                    if (changingCluster) {
                                        if (this.getResponder().getPower() <= 0) {
                                            try {
                                                final Skill fs = this.getResponder().getSkills().getSkill(1023);
                                                if (fs.getKnowledge() > 50.0) {
                                                    double x3 = 100.0 - fs.getKnowledge();
                                                    x3 -= x3 * 0.95;
                                                    final double newskill = fs.getKnowledge() - x3;
                                                    fs.setKnowledge(newskill, false);
                                                    this.getResponder().getCommunicator().sendAlertServerMessage("Your group fighting skill has been set to " + fs.getKnowledge(0.0) + "!");
                                                }
                                            }
                                            catch (NoSuchSkillException ex) {}
                                            try {
                                                final Skill as = this.getResponder().getSkills().getSkill(1030);
                                                if (as.getKnowledge() > 50.0) {
                                                    double x3 = 100.0 - as.getKnowledge();
                                                    x3 -= x3 * 0.95;
                                                    final double newskill = as.getKnowledge() - x3;
                                                    as.setKnowledge(newskill, false);
                                                    this.getResponder().getCommunicator().sendAlertServerMessage("Your archery skill has been set to " + as.getKnowledge(0.0) + "!");
                                                }
                                            }
                                            catch (NoSuchSkillException ex2) {}
                                        }
                                        this.getResponder().setLastChangedCluster();
                                    }
                                    int targetTileX2 = entry.SPAWNPOINTJENNX;
                                    int targetTileY2 = entry.SPAWNPOINTJENNY;
                                    if (targetKingdom == 2) {
                                        targetTileX2 = entry.SPAWNPOINTMOLX;
                                        targetTileY2 = entry.SPAWNPOINTMOLY;
                                    }
                                    else if (targetKingdom == 3) {
                                        targetTileX2 = entry.SPAWNPOINTLIBX;
                                        targetTileY2 = entry.SPAWNPOINTLIBY;
                                    }
                                    this.getResponder().sendToLoggers("Before spawnpoints: " + this.selectedSpawn + ", server=" + this.selectedServer + ",kingdom=" + this.selectedKingdom + " entry name=" + entry.getName());
                                    final Spawnpoint[] spawns = entry.getSpawns();
                                    if (spawns != null) {
                                        String kval3 = aAnswers.getProperty("spawnpoint");
                                        this.getResponder().sendToLoggers("Inside spawns. Length is " + spawns.length + " kval=" + kval3);
                                        int spnum = -1;
                                        if (kval3 != null) {
                                            kval3 = kval3.replace("spawn", "");
                                            try {
                                                spnum = Integer.parseInt(kval3);
                                            }
                                            catch (NumberFormatException nfe4) {
                                                spnum = this.selectedSpawn;
                                            }
                                        }
                                        else {
                                            spnum = this.selectedSpawn;
                                        }
                                        this.getResponder().sendToLoggers("Before loop. " + spnum);
                                        for (final Spawnpoint sp : spawns) {
                                            if (!entry.HOMESERVER && spnum < 0 && sp.kingdom == targetKingdom) {
                                                this.selectedSpawn = sp.number;
                                                this.getResponder().sendToLoggers("Inside spawnpoints. Just selected " + this.selectedSpawn + " AT RANDOM, server=" + this.selectedServer + ",kingdom=" + this.selectedKingdom);
                                                targetTileX2 = sp.tilex - 2 + Server.rand.nextInt(5);
                                                targetTileY2 = sp.tiley - 2 + Server.rand.nextInt(5);
                                                break;
                                            }
                                            if (sp.number == this.selectedSpawn) {
                                                this.getResponder().sendToLoggers("Using selected spawn " + this.selectedSpawn);
                                                targetTileX2 = sp.tilex - 2 + Server.rand.nextInt(5);
                                                targetTileY2 = sp.tiley - 2 + Server.rand.nextInt(5);
                                                break;
                                            }
                                            if (spnum == sp.number) {
                                                this.selectedSpawn = sp.number;
                                                this.getResponder().sendToLoggers("Inside spawnpoints. Just selected " + this.selectedSpawn + ", server=" + this.selectedServer + ",kingdom=" + this.selectedKingdom);
                                                if (this.getResponder().getPower() <= 0 && targetKingdom == 0) {
                                                    targetKingdom = sp.kingdom;
                                                }
                                                targetTileX2 = sp.tilex - 2 + Server.rand.nextInt(5);
                                                targetTileY2 = sp.tiley - 2 + Server.rand.nextInt(5);
                                                break;
                                            }
                                        }
                                    }
                                    this.getResponder().sendToLoggers(" at 4: " + entry.getName() + " target kingdom " + targetKingdom + "tx=" + targetTileX2 + ", ty=" + targetTileY2);
                                    if (Servers.localServer.entryServer) {
                                        this.getResponder().setRotation(270.0f);
                                        if (this.getResponder().isPlayer()) {
                                            ((Player)this.getResponder()).addTitle(Titles.Title.Educated);
                                        }
                                    }
                                    if (newTutorial) {
                                        this.getResponder().setFlag(76, false);
                                    }
                                    this.getResponder().sendTransfer(Server.getInstance(), entry.INTRASERVERADDRESS, Integer.parseInt(entry.INTRASERVERPORT), entry.INTRASERVERPASSWORD, entry.id, targetTileX2, targetTileY2, true, entry.isChallengeServer(), targetKingdom);
                                }
                                else {
                                    this.getResponder().getCommunicator().sendNormalServerMessage("The portal does not work. You are probably carrying too much. Try 200 items on body and in inventory.");
                                }
                            }
                            else {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Alas! A trifle stops you from entering the portal. You need to purchase some nice premium time in order to enter the portal.");
                            }
                        }
                        else if (entry.maintaining) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The portal is shut but a flicker indicates that it may open soon. You may try later.");
                        }
                        else if (entry.isFull()) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The portal is shut. " + entry.currentPlayers + " people are on the other side of the portal but only " + entry.pLimit + " are allowed. Please note that we are adding new servers as soon as possible when all available servers are full.");
                        }
                        else {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The portal is shut. The lands beyond are not available at the moment.");
                        }
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The portal is shut. No matter what you try nothing happens.");
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to step through the portal.");
            }
        }
        else if (this.step == 1) {
            final String val2 = aAnswers.getProperty("sid");
            if (val2 == null) {
                if (val != null) {
                    return;
                }
            }
            try {
                int spnum2 = this.selectedSpawn;
                this.getResponder().sendToLoggers("At 1: " + this.selectedSpawn + ", server=" + this.selectedServer + ", val2=" + val2 + " kingdom=" + this.selectedKingdom);
                if (val2 != null) {
                    this.selectedServer = Integer.parseInt(val2);
                    this.getResponder().sendToLoggers("At 2: val 2 is not null server=" + this.selectedServer + ", val2=" + val2);
                }
                final ServerEntry entry = Servers.getServerWithId(this.selectedServer);
                if (entry != null) {
                    final Spawnpoint[] spawns2 = entry.getSpawns();
                    if (spawns2 != null) {
                        this.getResponder().sendToLoggers("At 2.5: server=" + this.selectedServer + " spawn " + spnum2);
                        String kval4 = aAnswers.getProperty("spawnpoint");
                        if (kval4 != null) {
                            this.getResponder().sendToLoggers("At 2.6: server=" + this.selectedServer + " spawn kval " + kval4);
                            kval4 = kval4.replace("spawn", "");
                            try {
                                spnum2 = Integer.parseInt(kval4);
                                this.getResponder().sendToLoggers("At 2.7: server=" + this.selectedServer + " spawn spnum " + spnum2);
                                for (final Spawnpoint sp2 : spawns2) {
                                    if (sp2.number == spnum2) {
                                        this.getResponder().sendToLoggers("At 2.8: spawn " + sp2.name);
                                        this.selectedKingdom = sp2.kingdom;
                                        break;
                                    }
                                }
                            }
                            catch (NumberFormatException ex3) {}
                        }
                    }
                }
                final String kingdomid3 = "kingdid";
                String kval4 = aAnswers.getProperty("kingdid");
                if (kval4 != null) {
                    try {
                        this.selectedKingdom = Byte.parseByte(kval4);
                        this.getResponder().sendToLoggers("At 3: " + spnum2 + ", server=" + this.selectedServer + ", val2=" + val2 + " selected kingdom=" + this.selectedKingdom);
                    }
                    catch (NumberFormatException nfe5) {
                        this.selectedKingdom = this.getResponder().getKingdomId();
                    }
                }
                final PortalQuestion pq = new PortalQuestion(this.getResponder(), "Entering portal", "Go ahead!", this.portal);
                pq.step = 1;
                pq.selectedServer = this.selectedServer;
                pq.selectedSpawn = spnum2;
                pq.selectedKingdom = this.selectedKingdom;
                pq.sendQuestion();
            }
            catch (NumberFormatException nfe) {
                PortalQuestion.logger.log(Level.WARNING, nfe.getMessage() + ": " + val2);
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.portal != null) {
            byte targetKingdom = this.selectedKingdom;
            int data1 = this.portal.getData1();
            final int epicServerId = this.getResponder().getEpicServerId();
            if (this.step == 1) {
                data1 = this.selectedServer;
            }
            else if (this.portal.isEpicPortal()) {
                if (epicServerId > 0 && epicServerId != Servers.localServer.id) {
                    data1 = epicServerId;
                    final ServerEntry entry = Servers.getServerWithId(data1);
                    if (entry != null && entry.EPIC == Servers.localServer.EPIC) {
                        data1 = 100001;
                    }
                }
                else {
                    data1 = 100001;
                }
            }
            ServerEntry entry = Servers.getServerWithId(data1);
            if (entry != null) {
                if (entry.id == Servers.loginServer.id) {
                    entry = Servers.loginServer;
                }
                if (this.getResponder().getPower() == 0 && !Servers.isThisATestServer() && (entry.entryServer || Servers.localServer.isChallengeServer() || (entry.isChallengeServer() && !Servers.localServer.entryServer))) {
                    buf.append("text{type='bold';text=\"The portal looks dormant.\"};");
                }
                else if (this.portal.isEpicPortal()) {
                    if (epicServerId == entry.id) {
                        if (entry.isAvailable(this.getResponder().getPower(), this.getResponder().isReallyPaying())) {
                            this.step = 1;
                            this.selectedServer = entry.id;
                            if (entry.EPIC) {
                                buf.append("text{text=\"This portal leads to the Epic server " + entry.name + " where you last left it.\"}");
                            }
                            else if (entry.isChallengeServer()) {
                                buf.append("text{text=\"This portal leads to the Challenge server '" + entry.name + "'.\"}");
                            }
                            else if (entry.PVPSERVER) {
                                buf.append("text{text=\"This portal leads back to the Wild server " + entry.name + " where you last left it.\"}");
                            }
                            else {
                                buf.append("text{text=\"This portal leads to back the Freedom server " + entry.name + " where you last left it.\"}");
                            }
                        }
                        else {
                            buf.append("text{text=\"The " + entry.name + " server is currently unavailable to you.\"}");
                        }
                    }
                    else if (entry.isAvailable(this.getResponder().getPower(), this.getResponder().isReallyPaying())) {
                        if (entry.EPIC) {
                            buf.append("text{text=\"This portal leads to the Epic server " + entry.name + ". Please select a kingdom to join:\"}");
                            addKingdoms(entry, buf);
                        }
                        else if (entry.PVPSERVER) {
                            buf.append("text{text=\"This portal leads to the Wild server " + entry.name + ". Please select a kingdom to join:\"}");
                            addKingdoms(entry, buf);
                        }
                        else {
                            buf.append("text{text=\"This portal leads to the Freedom server " + entry.name + ". You will join:\"}");
                            addKingdoms(entry, buf);
                        }
                    }
                    else {
                        buf.append("text{text=\"The " + entry.name + " server is currently unavailable to you.\"}");
                    }
                    if (!entry.ISPAYMENT || this.getResponder().isReallyPaying()) {
                        if (Servers.localServer.entryServer && this.getResponder().getPower() == 0) {
                            buf.append("text{text=\"Do you wish to enter this portal never to return?\"};");
                        }
                        else {
                            buf.append("text{text=\"Do you wish to enter this portal?\"};");
                        }
                        buf.append("radio{ group='portalling'; id='true';text='Yes'}");
                        buf.append("radio{ group='portalling'; id='false';text='No';selected='true'}");
                    }
                    else {
                        buf.append("text{text=\"Alas! A trifle stops you from entering the portal. You need to purchase some nice premium time in order to enter the portal.\"}");
                    }
                }
                else {
                    if (!entry.PVPSERVER) {
                        buf.append("text{text='This portal leads to the safe lands of " + Kingdoms.getNameFor(entry.KINGDOM) + ".'}");
                        if (!entry.PVPSERVER && this.getResponder().getDeity() != null && this.getResponder().getDeity().number == 4) {
                            buf.append("text{text=\"You will lose connection with " + this.getResponder().getDeity().name + " if you enter the portal.\"}");
                        }
                        if (entry.KINGDOM != 0) {
                            targetKingdom = entry.KINGDOM;
                        }
                        else {
                            targetKingdom = this.getResponder().getKingdomId();
                        }
                    }
                    else if (entry.KINGDOM != 0 && this.getResponder().getPower() == 0 && Servers.localServer.entryServer && targetKingdom == 0) {
                        targetKingdom = entry.KINGDOM;
                    }
                    else if (targetKingdom == 0) {
                        this.getResponder().sendToLoggers("Not setting kingdom at 12");
                        targetKingdom = this.getResponder().getKingdomId();
                    }
                    else {
                        this.getResponder().sendToLoggers("Keeping kingdom at 12:" + targetKingdom);
                    }
                    if (entry.isAvailable(this.getResponder().getPower(), this.getResponder().isReallyPaying())) {
                        boolean changingCluster = false;
                        boolean changingEpicCluster = false;
                        if (Servers.localServer.PVPSERVER != entry.PVPSERVER) {
                            changingCluster = true;
                        }
                        else if (Servers.localServer.EPIC != entry.EPIC) {
                            changingCluster = true;
                            changingEpicCluster = true;
                            buf.append("text{text=\"You will not be able to use this portal. You must use an Epic Portal which you can build yourself using stones and logs.\"};");
                        }
                        else if (targetKingdom == 3) {
                            buf.append("text{text=\"The portal comes to life! You may pass to " + Kingdoms.getNameFor((byte)3) + "!\"}");
                        }
                        if (Servers.localServer.entryServer) {
                            changingCluster = false;
                        }
                        if (changingCluster && !changingEpicCluster) {
                            if (this.getResponder().isChampion() && !Servers.localServer.EPIC) {
                                buf.append("text{text=\"You will not be able to use this portal since you are a champion.\"};");
                            }
                            if (this.getResponder().getLastChangedCluster() + 3600000L > System.currentTimeMillis()) {
                                buf.append("text{text=\"You will not be able to use this portal since you may only change cluster once per hour.\"};");
                            }
                            if (this.getResponder().getPower() <= 0) {
                                try {
                                    final Skill fs = this.getResponder().getSkills().getSkill(1023);
                                    if (fs.getKnowledge(0.0) > 50.0) {
                                        buf.append("text{text=\"Your new group fighting skill will become " + fs.getKnowledge(0.0) * 0.949999988079071 + "!\"};");
                                    }
                                }
                                catch (NoSuchSkillException ex) {}
                                try {
                                    final Skill as = this.getResponder().getSkills().getSkill(1030);
                                    if (as.getKnowledge(0.0) > 50.0) {
                                        buf.append("text{text=\"Your new group archery skill will become " + as.getKnowledge(0.0) * 0.949999988079071 + "!\"};");
                                    }
                                }
                                catch (NoSuchSkillException ex2) {}
                            }
                        }
                        int numitems = 0;
                        if (!changingEpicCluster) {
                            int stayBehind = 0;
                            final Item[] inventoryItems = this.getResponder().getInventory().getAllItems(true);
                            for (int x = 0; x < inventoryItems.length; ++x) {
                                if (!inventoryItems[x].willLeaveServer(false, changingCluster, this.getResponder().getPower() > 0)) {
                                    ++stayBehind;
                                    buf.append("text{text=\"The " + inventoryItems[x].getName() + " will stay behind.\"};");
                                    if (Servers.localServer.entryServer && inventoryItems[x].getTemplateId() == 166) {
                                        buf.append("text{text=\"The structure will be destroyed.\"};");
                                    }
                                }
                            }
                            final Item[] bodyItems = this.getResponder().getBody().getAllItems();
                            for (int x2 = 0; x2 < bodyItems.length; ++x2) {
                                if (!bodyItems[x2].willLeaveServer(false, changingCluster, this.getResponder().getPower() > 0)) {
                                    ++stayBehind;
                                    buf.append("text{text=\"The " + bodyItems[x2].getName() + " will stay behind.\"};");
                                    if (Servers.localServer.entryServer && bodyItems[x2].getTemplateId() == 166) {
                                        buf.append("text{text=\"The structure will be destroyed.\"};");
                                    }
                                }
                            }
                            if (stayBehind > 0) {
                                buf.append("text{text=\"Items that stay behind will normally be available again when you return here.\"};");
                            }
                            if (this.getResponder().getPower() == 0) {
                                numitems = inventoryItems.length + bodyItems.length - stayBehind - 12;
                            }
                        }
                        if (numitems > 200) {
                            buf.append("text{text=\"The portal seems to become unresponsive as you approach. You are carrying too much. Try removing " + (numitems - 200) + " items from body and inventory.\"};");
                        }
                        else if (!entry.ISPAYMENT || this.getResponder().isReallyPaying()) {
                            if (Servers.localServer.entryServer && this.getResponder().getPower() == 0) {
                                buf.append("text{text=\"Do you wish to enter this portal never to return?\"};");
                            }
                            else {
                                buf.append("text{text=\"Do you wish to enter this portal?\"};");
                            }
                            if (this.getResponder().getPower() == 0 && Servers.localServer.entryServer) {
                                buf.append("text{type='bold';text=\"Note that you will automatically convert to a " + Kingdoms.getNameFor(targetKingdom) + "!\"};");
                            }
                            buf.append("radio{ group='portalling'; id='true';text='Yes'}");
                            buf.append("radio{ group='portalling'; id='false';text='No';selected='true'}");
                        }
                        else {
                            buf.append("text{text=\"Alas! A trifle stops you from entering the portal. You need to purchase some nice premium time in order to enter the portal.\"}");
                        }
                    }
                    else if (entry.maintaining) {
                        buf.append("text{text=\"The portal is shut but a flicker indicates that it may open soon. You may try later.\"}");
                    }
                    else if (entry.isFull()) {
                        buf.append("text{text=\"The portal is shut. " + entry.currentPlayers + " people are on the other side of the portal but only " + entry.pLimit + " are allowed.\"}");
                    }
                    else {
                        buf.append("text{text=\"The portal is shut. The lands beyond are not available at the moment.\"}");
                    }
                }
            }
            else {
                if (data1 == 100000 || data1 == 100001 || data1 == 100002) {
                    buf.setLength(0);
                    this.sendQuestion2(data1);
                    return;
                }
                buf.append("text{text=\"The portal is shut. No matter what you try nothing happens.\"}");
            }
        }
        else {
            buf.append("text{text=\"The portal fades from view and becomes immaterial. No matter what you try nothing happens.\"}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(700, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public final void sendQuestion2(final int portalNumber) {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        this.step = 1;
        boolean selected = true;
        if (portalNumber != 100000 && portalNumber != 100001 && portalNumber != 100002) {
            this.selectedServer = portalNumber;
        }
        final List<ServerEntry> entries = Servers.getServerList(portalNumber);
        if (this.portal.isEpicPortal() && !PortalQuestion.epicPortalsEnabled && this.getResponder().getPower() == 0) {
            entries.clear();
        }
        if (entries.size() == 0) {
            buf.append("text{text=\"The portal is shut. No matter what you try nothing happens.\"}");
        }
        else {
            final ServerEntry[] entryArr = entries.toArray(new ServerEntry[entries.size()]);
            Arrays.sort(entryArr);
            for (final ServerEntry sentry : entryArr) {
                if (this.getResponder().getPower() > 0 || !sentry.entryServer || Servers.localServer.testServer) {
                    String desc = "";
                    String colour = "";
                    switch (sentry.id) {
                        case 1: {
                            desc = " - This is the tutorial server.";
                            colour = this.purple;
                            break;
                        }
                        case 3: {
                            desc = " - This is an old and large PvP server in the Freedom cluster. Custom kingdoms can be formed here.";
                            colour = this.orange;
                            break;
                        }
                        case 5: {
                            desc = " - This is the oldest large PvE server in the Freedom cluster.";
                            colour = this.green;
                            break;
                        }
                        case 6:
                        case 7:
                        case 8: {
                            desc = " - This is a standard sized, well developed PvE server in the Freedom cluster.";
                            colour = this.green;
                            break;
                        }
                        case 9: {
                            desc = " - This is the Jenn-Kellon Home PvP server in the Epic cluster. Home servers have large bonuses against attackers.";
                            colour = this.orange;
                            break;
                        }
                        case 10: {
                            desc = " - This is the Mol Rehan Home PvP server in the Epic cluster. Home servers have large bonuses against attackers.";
                            colour = this.orange;
                            break;
                        }
                        case 11: {
                            desc = " - This is the Horde of The Summoned Home PvP server in the Epic cluster. Home servers have large bonuses against attackers.";
                            colour = this.orange;
                            break;
                        }
                        case 12: {
                            desc = " - This is the central PvP server in the Epic cluster. This is where the kingdoms clash, and custom kingdoms are formed.";
                            colour = this.red;
                            break;
                        }
                        case 13:
                        case 14: {
                            desc = " - This is a standard sized, fairly well developed PvE server in the Freedom cluster.";
                            colour = this.green;
                            break;
                        }
                        case 15: {
                            desc = " - The most recent Land Rush server. It is bigger than all the other servers together.";
                            colour = this.green;
                            break;
                        }
                        case 20: {
                            desc = " - This is the Challenge server. Very quick skillgain, small and compact providing lots of action. Full loot PvP with highscore lists and prizes. Resets after a while.";
                            colour = this.cyan;
                            break;
                        }
                        default: {
                            final String kingdomname = Kingdoms.getNameFor(sentry.KINGDOM);
                            String pvp = " Pvp Kingdoms ";
                            String kingdoms = " (" + kingdomname + "): ";
                            if (!sentry.PVPSERVER) {
                                pvp = " Non-Pvp";
                            }
                            else if (sentry.HOMESERVER) {
                                pvp = " Pvp Home";
                            }
                            else {
                                kingdoms = ": ";
                            }
                            desc = " - Test Server. " + pvp + kingdoms;
                            colour = this.cyan;
                            break;
                        }
                    }
                    if (sentry.id != Servers.localServer.id) {
                        final boolean full = sentry.isFull();
                        if (sentry.isAvailable(this.getResponder().getPower(), this.getResponder().isReallyPaying())) {
                            if (entryArr.length == 1) {
                                buf.append("harray{radio{group='sid';id='" + sentry.id + "';selected='true'}label{color='" + colour + "';text='" + sentry.name + desc + (full ? " (Full)" : "") + "'}}");
                                buf.append("text{text=''}");
                                buf.append("text{text='You will join the following kingdom:'}");
                                addKingdoms(sentry, buf);
                            }
                            else {
                                buf.append("harray{radio{group='sid';id='" + sentry.id + "';selected='" + selected + "'}label{color='" + colour + "';text='" + sentry.name + desc + (full ? " (Full)" : "") + "'}}");
                            }
                            selected = false;
                        }
                        else {
                            String reason = "unavailable";
                            if (full && sentry.isConnected()) {
                                reason = "full";
                            }
                            if (sentry.maintaining) {
                                reason = "maintenance";
                            }
                            buf.append("label{color=\"" + colour + "\";text=\"    " + sentry.name + desc + " Unavailable: " + reason + ".\"}");
                        }
                    }
                }
            }
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(700, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private static final void addVillages(final ServerEntry entry, final StringBuilder buf, final byte selectedKingdom) {
        final Spawnpoint[] spawns = entry.getSpawns();
        if (spawns != null && spawns.length > 0) {
            buf.append("text{text=\"Also, please select a start village:\"}");
            final int numSelected = Server.rand.nextInt(spawns.length);
            int curr = 0;
            for (final Spawnpoint spawn : spawns) {
                if (selectedKingdom != 0 && spawn.kingdom == selectedKingdom) {
                    buf.append("radio{group=\"spawnpoint\";id=\"spawn" + spawn.number + "\"; text=\"" + spawn.name + " (" + spawn.description + ")\";selected=\"" + (numSelected == curr++) + "\"}");
                }
            }
        }
    }
    
    private static final void addKingdoms(final ServerEntry entry, final StringBuilder buf) {
        final Set<Byte> kingdoms = entry.getExistingKingdoms();
        if (entry.HOMESERVER) {
            final Kingdom kingd = Kingdoms.getKingdom(entry.KINGDOM);
            if (kingd != null) {
                buf.append("radio{group=\"kingdid\";id=\"" + entry.KINGDOM + "\"; text=\"" + kingd.getName() + "\";selected=\"" + true + "\"}");
            }
            buf.append("text{text=\"\"}");
            addVillages(entry, buf, entry.KINGDOM);
        }
        else if (entry.isChallengeServer()) {
            final Spawnpoint[] spawns = entry.getSpawns();
            if (spawns != null && spawns.length > 0) {
                final int numSelected = Server.rand.nextInt(spawns.length);
                int curr = 0;
                for (final Spawnpoint spawn : spawns) {
                    final Kingdom kingd2 = Kingdoms.getKingdom(spawn.kingdom);
                    if (kingd2 != null && kingd2.acceptsTransfers()) {
                        buf.append("radio{group=\"spawnpoint\";id=\"spawn" + spawn.number + "\"; text=\"" + spawn.name + " in " + kingd2.getName() + " (" + spawn.description + ")\";selected=\"" + (numSelected == curr) + "\"}");
                        ++curr;
                    }
                }
            }
            buf.append("text{text=\"\"}");
        }
        else {
            boolean selected = true;
            for (final Byte k : kingdoms) {
                final Kingdom kingd3 = Kingdoms.getKingdom(k);
                if (kingd3 != null && kingd3.acceptsTransfers()) {
                    buf.append("radio{group=\"kingdid\";id=\"" + (int)k + "\"; text=\"" + kingd3.getName() + " '" + kingd3.getFirstMotto() + " " + kingd3.getSecondMotto() + "'\";selected=\"" + selected + "\"}");
                    selected = false;
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger(PortalQuestion.class.getName());
        PortalQuestion.epicPortalsEnabled = true;
    }
}
