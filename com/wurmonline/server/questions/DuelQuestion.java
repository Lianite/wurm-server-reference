// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class DuelQuestion extends Question
{
    public DuelQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final int aType, final long aTarget) {
        super(aResponder, aTitle, aQuestion, aType, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        try {
            final Player tplay = Players.getInstance().getPlayer(this.target);
            final String key = "duel";
            final String val = answers.getProperty("duel");
            if (val != null && val.equals("true")) {
                if (this.type == 59) {
                    ((Player)this.getResponder()).addDuellist(tplay);
                    tplay.addDuellist(this.getResponder());
                }
                else if (this.type == 60) {
                    ((Player)this.getResponder()).addSparrer(tplay);
                    tplay.addSparrer(this.getResponder());
                }
                this.getResponder().getCommunicator().sendNormalServerMessage("You may now attack " + tplay.getName() + " without penalty.");
                tplay.getCommunicator().sendNormalServerMessage("You may now attack " + this.getResponder().getName() + " without penalty.");
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to fight " + tplay.getName() + " now.");
            }
        }
        catch (NoSuchPlayerException nsp) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Your opponent seem to have left.");
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        String name = "An unknown player";
        try {
            final Player tplay = Players.getInstance().getPlayer(this.target);
            name = tplay.getName();
        }
        catch (NoSuchPlayerException ex) {}
        if (this.type == 59) {
            buf.append("text{text='" + name + " wants to duel to the death with you.'}text{text=''}");
            buf.append("text{text='You will receive no penalties for attacking or killing eachother.'}");
            buf.append("text{text='Make sure you are in a safe environment if you do not trust your opponent in order to avoid some kind of trap.'}");
            buf.append("text{text='The winner may loot the loser!'}");
        }
        else if (this.type == 60) {
            buf.append("text{text='" + name + " wants to duel friendly with you.'}text{text=''}");
            buf.append("text{text='You will receive no penalties for attacking eachother.'}");
            buf.append("text{text='Before the final blow, the duel will normally end and a winner be declared.'}");
            buf.append("text{text='On rare occasions the winning player unfortunately fails to hold his final blow back, and the opponent is slain!'}");
            buf.append("text{text='Make sure you are in a safe environment if you do not trust your opponent in order to avoid some kind of trap.'}");
            buf.append("text{text='The winner may not loot the loser'}");
        }
        buf.append("radio{ group='duel'; id='true';text='Yes'}");
        buf.append("radio{ group='duel'; id='false';text='No';selected='true'}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
