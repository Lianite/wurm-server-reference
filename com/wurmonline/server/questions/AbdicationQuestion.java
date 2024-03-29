// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.kingdom.Kingdom;
import java.util.logging.Level;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.kingdom.King;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class AbdicationQuestion extends Question
{
    public AbdicationQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 68, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        final String key = "abd";
        final String val = answers.getProperty("abd");
        if (val != null && val.equals("true")) {
            final byte kdom = this.getResponder().getKingdomId();
            final King k = King.getKing(kdom);
            if (k != null && k.kingid == this.getResponder().getWurmId()) {
                k.abdicate(this.getResponder().isOnSurface(), false);
                this.getResponder().getCommunicator().sendNormalServerMessage("You are no longer the " + King.getRulerTitle(this.getResponder().isNotFemale(), kdom) + " of " + Kingdoms.getNameFor(kdom) + ".");
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You are not the " + King.getRulerTitle(this.getResponder().isNotFemale(), kdom) + " of " + Kingdoms.getNameFor(kdom) + ".");
            }
        }
        else {
            this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to abdicate.");
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{type='italic';text='This is nothing to take lightly. You may never get the chance to become " + King.getRulerTitle(this.getResponder().isNotFemale(), this.getResponder().getKingdomId()) + " again.'}");
        buf.append("text{type='italic';text='You should not be forced into doing this. You are the " + King.getRulerTitle(this.getResponder().isNotFemale(), this.getResponder().getKingdomId()) + "!'}");
        buf.append("text{type='italic';text='Valid reasons for abdication include lack of time, that you perform poorly and have no hopes of succeeding, or that you are being rewarded for stepping down.'}");
        buf.append("text{type='italic';text='Just do not give up too easily. All you may have to do is to take control and be more active.'}");
        try {
            final Kingdom kingd = Kingdoms.getKingdom(this.getResponder().getKingdomId());
            if (kingd != null) {
                if (kingd.isCustomKingdom()) {
                    buf.append("text{type='bold';text='Please note that the royal items will drop on the ground upon abdicating for anyone to pick up.'}");
                }
                else {
                    buf.append("text{type='bold';text='Please note that the royal items will be destroyed and anyone else in the kingdom may attempt to become new ruler.'}");
                }
            }
        }
        catch (Exception ex) {
            AbdicationQuestion.logger.log(Level.WARNING, this.getResponder().getName() + " " + ex.getMessage(), ex);
        }
        buf.append("text{type='italic';text='With those words, hopefully you may take the right decision.'}");
        buf.append("text{text=''}");
        buf.append("text{text='Do you want to Abdicate?'}");
        buf.append("text{text=''}");
        buf.append("text{type='bold';text='If you answer yes you will no longer be " + King.getRulerTitle(this.getResponder().isNotFemale(), this.getResponder().getKingdomId()) + " of " + Kingdoms.getNameFor(this.getResponder().getKingdomId()) + ".'}");
        buf.append("text{text=''}");
        buf.append("radio{ group='abd'; id='true';text='Yes'}");
        buf.append("radio{ group='abd'; id='false';text='No';selected='true'}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
