// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Iterator;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.villages.WarDeclaration;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.villages.PvPAlliance;
import java.util.Arrays;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.TimeConstants;

public final class ManageAllianceQuestion extends Question implements TimeConstants
{
    private static final String NOCHANGE = "No change";
    private Village[] allies;
    
    public ManageAllianceQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 19, aTarget);
        this.allies = null;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseManageAllianceQuestion(this);
    }
    
    public final Village[] getAllies() {
        return this.allies;
    }
    
    @Override
    public void sendQuestion() {
        final Village village = this.getResponder().getCitizenVillage();
        if (village != null) {
            Arrays.sort(this.allies = village.getAllies());
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            final PvPAlliance pvpAll = PvPAlliance.getPvPAlliance(village.getAllianceNumber());
            if (pvpAll != null) {
                buf.append("text{text=\"You are in the " + pvpAll.getName() + ".\"}");
                if (FocusZone.getHotaZone() != null) {
                    buf.append("text{text=\"" + pvpAll.getName() + " has won the Hunt of the Ancients " + pvpAll.getNumberOfWins() + " times.\"}");
                }
                if (village.getId() == village.getAllianceNumber()) {
                    if (village.getMayor().getId() == this.getResponder().getWurmId()) {
                        buf.append("text{text=\"" + village.getName() + " is the capital in the alliance which means your diplomats are responsible for ousting other settlements. The mayor may change name, disband or set another village as the alliance capital:\"};");
                        buf.append("harray{label{text=\"Alliance name:\"};input{id=\"allName\"; text=\"" + pvpAll.getName() + "\";maxchars=\"20\"}}");
                        buf.append("harray{label{text='Alliance capital:'}dropdown{id=\"masterVill\";options=\"");
                        for (int x = 0; x < this.allies.length; ++x) {
                            buf.append(this.allies[x].getName() + ",");
                        }
                        buf.append("No change");
                        buf.append("\";default=\"" + this.allies.length + "\"}}");
                        buf.append("harray{checkbox{text=\"Check this if you wish to disband this alliance: \";id=\"disbandAll\"; selected=\"false\"}}");
                    }
                    for (final Village ally : this.allies) {
                        if (ally != village) {
                            buf.append("harray{label{text=\"Check to break alliance with " + ally.getName() + ":\"}checkbox{id=\"break" + ally.getId() + "\";text=' '}}");
                        }
                    }
                }
                else {
                    buf.append("harray{label{text=\"Check to break alliance with " + pvpAll.getName() + ":\"}checkbox{id=\"break" + pvpAll.getId() + "\";text=' '}}");
                }
                buf.append("text{type=\"bold\";text=\"Alliance message of the day:\"}");
                buf.append("input{maxchars=\"200\";id=\"motd\";text=\"" + pvpAll.getMotd() + "\"}");
            }
            if (this.allies.length == 0) {
                buf.append("text{text='You have no allies.'}");
            }
            buf.append("text{text=''}");
            buf.append("text{text=''}");
            if (village.warDeclarations != null) {
                buf.append("text{type='bold'; text='The current village war declarations:' }");
                for (final WarDeclaration declaration : village.warDeclarations.values()) {
                    if (declaration.declarer == village) {
                        if (Servers.isThisAChaosServer() && System.currentTimeMillis() - declaration.time > 86400000L) {
                            declaration.accept();
                            buf.append("harray{label{text=\"" + declaration.receiver.getName() + " has now automatically accepted your declaration.\"}}");
                        }
                        else {
                            buf.append("harray{label{text=\"Check to withdraw declaration to " + declaration.receiver.getName() + ":\"}checkbox{id'decl" + declaration.receiver.getId() + "';text=' '}}");
                        }
                    }
                    else if (Servers.isThisAChaosServer()) {
                        if (System.currentTimeMillis() - declaration.time < 86400000L) {
                            buf.append("harray{label{text=\"You have " + Server.getTimeFor(System.currentTimeMillis() - declaration.time) + " until you automatically accept the declaration of war.\"}}");
                            buf.append("harray{label{text=\"Check to accept declaration from " + declaration.declarer.getName() + ":\"}checkbox{id='recv" + declaration.declarer.getId() + "';text=' '}}");
                        }
                        else {
                            declaration.accept();
                            buf.append("harray{label{text=\"" + declaration.receiver.getName() + " has now automatically accepted the war declaration from " + declaration.declarer.getName() + ".\"}}");
                        }
                    }
                    else {
                        buf.append("harray{label{text=\"Check to accept declaration from " + declaration.declarer.getName() + ":\"}checkbox{id='recv" + declaration.declarer.getId() + "';text=' '}}");
                    }
                }
                buf.append("text{text=''}");
                buf.append("text{text=''}");
            }
            else if (Servers.localServer.PVPSERVER) {
                buf.append("text{text='You have no pending war declarations.'}");
            }
            final Village[] enemies = village.getEnemies();
            if (enemies.length > 0) {
                buf.append("harray{text{type='bold'; text='We are at war with: '}text{text=\" ");
                Arrays.sort(enemies);
                for (int x2 = 0; x2 < enemies.length; ++x2) {
                    if (x2 == enemies.length - 1) {
                        buf.append(enemies[x2].getName());
                    }
                    else if (x2 == enemies.length - 2) {
                        buf.append(enemies[x2].getName() + " and ");
                    }
                    else {
                        buf.append(enemies[x2].getName() + ", ");
                    }
                }
                buf.append(".\"}}");
            }
            else if (Servers.localServer.PVPSERVER) {
                buf.append("text{text='You are not at war with any particular settlement.'}");
            }
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
}
