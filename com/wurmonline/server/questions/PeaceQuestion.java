// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import java.util.Properties;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;

public final class PeaceQuestion extends Question
{
    private final Creature invited;
    
    public PeaceQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) throws NoSuchCreatureException, NoSuchPlayerException {
        super(aResponder, aTitle, aQuestion, 30, aTarget);
        this.invited = Server.getInstance().getCreature(aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseVillagePeaceQuestion(this);
    }
    
    public Creature getInvited() {
        return this.invited;
    }
    
    @Override
    public void sendQuestion() {
        final Village village = this.getResponder().getCitizenVillage();
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("header{text=\"Peace offer by " + village.getName() + ":\"}");
        buf.append("text{text=\"You have been offered peace by " + this.getResponder().getName() + " and the village of " + village.getName() + ". \"}");
        buf.append("text{text='Do you accept?'}");
        buf.append("radio{ group='peace'; id='true';text='Yes'}");
        buf.append("radio{ group='peace'; id='false';text='No';selected='true'}");
        buf.append(this.createAnswerButton2());
        this.getInvited().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        this.getResponder().getCommunicator().sendNormalServerMessage("You send a peace offer to " + this.getInvited().getName() + ".");
    }
}
