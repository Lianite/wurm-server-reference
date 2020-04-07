// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.kingdom.Kingdom;
import java.util.Iterator;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Servers;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import com.wurmonline.server.kingdom.King;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class KingdomHistory extends Question
{
    public KingdomHistory(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 66, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
    }
    
    @Override
    public void sendQuestion() {
        final String lHtml = this.getBmlHeaderWithScroll();
        final StringBuilder buf = new StringBuilder(lHtml);
        final Map<Integer, King> kings = King.eras;
        final Map<String, LinkedList<King>> counters = new HashMap<String, LinkedList<King>>();
        for (final King k : kings.values()) {
            LinkedList<King> kinglist = counters.get(k.kingdomName);
            if (kinglist == null) {
                kinglist = new LinkedList<King>();
            }
            kinglist.add(k);
            counters.put(k.kingdomName, kinglist);
        }
        for (final Map.Entry<String, LinkedList<King>> entry : counters.entrySet()) {
            this.addKing(entry.getValue(), entry.getKey(), buf);
        }
        if (Servers.localServer.isChallengeServer()) {
            for (final Kingdom kingdom : Kingdoms.getAllKingdoms()) {
                if (kingdom.existsHere()) {
                    buf.append("label{text=\"" + kingdom.getName() + " points:\"};");
                    buf.append("label{text=\"" + kingdom.getWinpoints() + "\"};text{text=''};");
                }
            }
        }
        buf.append(this.createAnswerButton3());
        this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public void addKing(final Collection<King> kings, final String kingdomName, final StringBuilder buf) {
        buf.append("text{type=\"bold\";text=\"History of " + kingdomName + ":\"}text{text=''}");
        buf.append("table{rows='" + (kings.size() + 1) + "'; cols='10';label{text='Ruler'};label{text='Capital'};label{text='Start Land'};label{text='End Land'};label{text='Land Difference'};label{text='Levels Killed'};label{text='Levels Lost'};label{text='Levels Appointed'};label{text='Start Date'};label{text='End Date'};");
        for (final King k : kings) {
            buf.append("label{text=\"" + k.getFullTitle() + "\"};");
            buf.append("label{text=\"" + k.capital + "\"};");
            buf.append("label{text=\"" + String.format("%.2f%%", k.startLand) + "\"};");
            buf.append("label{text=\"" + String.format("%.2f%%", k.currentLand) + "\"};");
            buf.append("label{text=\"" + String.format("%.2f%%", k.currentLand - k.startLand) + "\"};");
            buf.append("label{text=\"" + k.levelskilled + "\"};");
            buf.append("label{text=\"" + k.levelslost + "\"};");
            buf.append("label{text=\"" + k.appointed + "\"};");
            buf.append("label{text=\"" + WurmCalendar.getDateFor(k.startWurmTime) + "\"};");
            if (k.endWurmTime > 0L) {
                buf.append("label{text=\"" + WurmCalendar.getDateFor(k.endWurmTime) + "\"};");
            }
            else {
                buf.append("label{text=\"N/A\"};");
            }
        }
        buf.append("}");
        buf.append("text{text=\"\"}");
    }
}
