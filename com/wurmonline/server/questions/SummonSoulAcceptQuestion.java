// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public class SummonSoulAcceptQuestion extends Question
{
    private final String summonerName;
    private final int summonX;
    private final int summonY;
    private final int summonLayer;
    private final int summonFloor;
    
    public SummonSoulAcceptQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final Creature summoner) {
        super(aResponder, aTitle, aQuestion, 27, aTarget);
        this.summonerName = summoner.getName();
        this.summonX = summoner.getTileX() * 4;
        this.summonY = summoner.getTileY() * 4;
        this.summonLayer = summoner.getLayer();
        this.summonFloor = summoner.getFloorLevel();
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        final boolean accept = this.getAnswer().getProperty("summ").equals("true");
        if (accept) {
            if (this.getResponder().getEnemyPresense() > 0) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You cannot be summoned right now, enemies are nearby.");
                return;
            }
            this.getResponder().setTeleportPoints(this.summonX, this.summonY, this.summonLayer, this.summonFloor);
            if (this.getResponder().startTeleporting()) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You are summoned to the location of " + this.summonerName + ".");
                this.getResponder().getCommunicator().sendTeleport(false);
            }
        }
        else {
            this.getResponder().getCommunicator().sendNormalServerMessage("You decline the summon from " + this.summonerName + ".");
        }
    }
    
    @Override
    public void sendQuestion() {
        try {
            final Creature asker2 = Server.getInstance().getCreature(this.target);
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            buf.append("text{text='" + asker2.getName() + " would like to summon you to their location.'}text{text=''}text{text=''}text{text='Would you like to accept?'}");
            buf.append("text{text=''}");
            buf.append("radio{ group='summ'; id='true';text='Yes'}");
            buf.append("radio{ group='summ'; id='false';text='No';selected='true'}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            asker2.getCommunicator().sendNormalServerMessage("You request " + this.getResponder().getName() + " to be summoned to your location.");
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
    }
}
