// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.kingdom.King;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class NewKingQuestion extends Question
{
    public NewKingQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 69, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{type='bold';text='Welcome as the " + King.getRulerTitle(this.getResponder().isNotFemale(), this.getResponder().getKingdomId()) + " of " + Kingdoms.getNameFor(this.getResponder().getKingdomId()) + "!'}");
        buf.append("text{type='';text='There are some things you should be aware of:'}");
        buf.append("text{type='';text='As the " + King.getRulerTitle(this.getResponder().isNotFemale(), this.getResponder().getKingdomId()) + ", you have one general goal, apart from your personal ones.'}");
        buf.append("text{type='';text='This is to gain land.'}");
        buf.append("text{type='';text='The more land you gain, the better your public title will become.'}");
        buf.append("text{type='';text='The more land you control, the more and finer titles and orders you may bestow upon your subjects.'}");
        buf.append("text{type='';text='Therefor a good idea is to reward those who gain land for you.'}");
        buf.append("text{type='';text='Land also has the benefit of yielding more coins to traders from the pool.'}");
        buf.append("text{text=''}");
        buf.append("text{type='';text='Good luck in your rulership!'}");
        buf.append("text{text=''}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(400, 250, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
