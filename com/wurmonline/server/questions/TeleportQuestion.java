// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Arrays;
import com.wurmonline.server.Players;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.players.Player;
import java.util.List;

public final class TeleportQuestion extends Question
{
    private final List<Player> playerlist;
    private final List<Village> villagelist;
    private String filter;
    private boolean filterPlayers;
    private boolean filterVillages;
    
    public TeleportQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 17, aTarget);
        this.playerlist = new LinkedList<Player>();
        this.villagelist = new LinkedList<Village>();
        this.filter = "";
        this.filterPlayers = false;
        this.filterVillages = false;
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text='Tile x'};input{id='data1'; text='-1'}}");
        buf.append("harray{label{text='Tile y'};input{id='data2'; text='-1'}}");
        buf.append("harray{label{text='Surfaced: '};dropdown{id='layer';options='true,false'}}");
        final Player[] players = Players.getInstance().getPlayers();
        Arrays.sort(players);
        for (int x = 0; x < players.length; ++x) {
            if (!this.filterPlayers || PlayerInfoFactory.wildCardMatch(players[x].getName().toLowerCase(), this.filter.toLowerCase())) {
                this.playerlist.add(players[x]);
            }
        }
        buf.append("text{text=''};");
        buf.append("harray{label{text=\"Filter by: \"};input{maxchars=\"20\";id=\"filtertext\";text=\"" + this.filter + "\"; onenter='filterboth'};label{text=' (Use * as a wildcard)'};}");
        buf.append("harray{label{text='Player:    '}; dropdown{id='wurmid';options='");
        for (int x = 0; x < this.playerlist.size(); ++x) {
            if (x > 0) {
                buf.append(",");
            }
            buf.append(this.playerlist.get(x).getName());
        }
        buf.append("'};button{text='Filter'; id='filterplayer'}}");
        buf.append("harray{label{text='Village:   '}; dropdown{id='villid';default='0';options=\"none,");
        final Village[] vills = Villages.getVillages();
        Arrays.sort(vills);
        int lastPerm = 0;
        for (int x2 = 0; x2 < vills.length; ++x2) {
            if (!this.filterVillages || PlayerInfoFactory.wildCardMatch(vills[x2].getName().toLowerCase(), this.filter.toLowerCase())) {
                if (vills[x2].isPermanent) {
                    this.villagelist.add(lastPerm, vills[x2]);
                    ++lastPerm;
                }
                else {
                    this.villagelist.add(vills[x2]);
                }
            }
        }
        for (int x2 = 0; x2 < this.villagelist.size(); ++x2) {
            if (x2 > 0) {
                buf.append(",");
            }
            if (this.villagelist.get(x2).isPermanent) {
                buf.append("#");
            }
            buf.append(this.villagelist.get(x2).getName());
        }
        buf.append("\"};button{text='Filter'; id='filtervillage'}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        boolean filterP = false;
        boolean filterV = false;
        String val = this.getAnswer().getProperty("filterplayer");
        if (val != null && val.equals("true")) {
            filterP = true;
        }
        val = this.getAnswer().getProperty("filtervillage");
        if (val != null && val.equals("true")) {
            filterV = true;
        }
        val = this.getAnswer().getProperty("filterboth");
        if (val != null && val.equals("true")) {
            filterP = (filterV = true);
        }
        if (filterP || filterV) {
            val = this.getAnswer().getProperty("filtertext");
            if (val == null || val.length() == 0) {
                val = "*";
            }
            final TeleportQuestion tq = new TeleportQuestion(this.getResponder(), this.title, this.question, this.target);
            tq.filter = val;
            tq.filterPlayers = filterP;
            tq.filterVillages = filterV;
            tq.sendQuestion();
        }
        else {
            QuestionParser.parseTeleportQuestion(this);
        }
    }
    
    Player getPlayer(final int aPosition) {
        return this.playerlist.get(aPosition);
    }
    
    Village getVillage(final int aPosition) {
        return this.villagelist.get(aPosition);
    }
}
