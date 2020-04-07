// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.deities.Deities;
import java.util.Arrays;
import com.wurmonline.server.Players;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import com.wurmonline.server.players.Player;
import java.util.List;

public final class SetDeityQuestion extends Question
{
    private final List<Player> playlist;
    private final Map<Integer, Integer> deityMap;
    
    public SetDeityQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 26, aTarget);
        this.playlist = new LinkedList<Player>();
        this.deityMap = new ConcurrentHashMap<Integer, Integer>();
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseSetDeityQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("harray{label{text='Player: '};dropdown{id='wurmid';options='");
        final Player[] players = Players.getInstance().getPlayers();
        Arrays.sort(players);
        for (int x = 0; x < players.length; ++x) {
            if (x > 0) {
                buf.append(",");
            }
            buf.append(players[x].getName());
            this.playlist.add(players[x]);
        }
        buf.append("'}}");
        final Deity[] deitys = Deities.getDeities();
        int counter = 0;
        buf.append("harray{label{text=\"Deity\"};dropdown{id=\"deityid\";options='None");
        for (final Deity d : deitys) {
            ++counter;
            this.deityMap.put(counter, d.getNumber());
            buf.append(",");
            buf.append(d.getName());
        }
        buf.append("'}}");
        buf.append("harray{label{text=\"Faith\"};input{maxchars=\"3\";id=\"faith\";text=\"1\"}label{text=\".\"}input{maxchars=\"6\"; id=\"faithdec\"; text=\"000000\"}}");
        buf.append("harray{label{text=\"Favor\"};input{maxchars='3';id=\"favor\";text=\"1\"}}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public final int getDeityNumberFromArrayPos(final int arrayPos) {
        if (arrayPos == 0) {
            return 0;
        }
        return this.deityMap.get(arrayPos);
    }
    
    Player getPlayer(final int aPosition) {
        return this.playlist.get(aPosition);
    }
}
