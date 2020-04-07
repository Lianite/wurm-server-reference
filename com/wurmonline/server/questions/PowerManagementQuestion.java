// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.players.Player;
import java.util.Arrays;
import com.wurmonline.server.Players;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import java.util.List;

public final class PowerManagementQuestion extends Question
{
    private final List<Long> playerIds;
    
    public PowerManagementQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 20, aTarget);
        this.playerIds = new LinkedList<Long>();
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("harray{label{text='Player'};dropdown{id='wurmid';options='");
        final Player[] players = Players.getInstance().getPlayers();
        Arrays.sort(players);
        this.playerIds.add(new Long(-10L));
        buf.append("none");
        for (int x = 0; x < players.length; ++x) {
            buf.append(",");
            buf.append(players[x].getName());
            this.playerIds.add(new Long(players[x].getWurmId()));
        }
        buf.append("'}}");
        buf.append("harray{label{text='Power'};dropdown{id='power';options='");
        buf.append("none,");
        buf.append("hero,");
        buf.append("demigod,");
        buf.append("high god,");
        buf.append("arch angel,");
        buf.append("implementor");
        buf.append("'}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parsePowerManagementQuestion(this);
    }
    
    Long getPlayerId(final int aPlayerID) {
        return this.playerIds.get(aPlayerID);
    }
}
