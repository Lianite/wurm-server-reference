// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

public final class AskConvertQuestion extends Question
{
    private final Item holyItem;
    
    public AskConvertQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final Item aHolyItem) {
        super(aResponder, aTitle, aQuestion, 27, aTarget);
        this.holyItem = aHolyItem;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseAskConvertQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        try {
            final Creature asker = Server.getInstance().getCreature(this.target);
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            buf.append("text{text='" + asker.getName() + " wants to teach you about " + asker.getDeity().name + ".'}text{text=''}text{text=''}text{text='Do you want to listen?'}");
            buf.append("text{text='After you listen, you will get the option to join the followers of " + asker.getDeity().name + ".'}");
            buf.append("text{text=''}");
            buf.append("radio{ group='conv'; id='true';text='Yes'}");
            buf.append("radio{ group='conv'; id='false';text='No';selected='true'}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            asker.getCommunicator().sendNormalServerMessage("You ask " + this.getResponder().getName() + " to listen to you.");
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
    }
    
    Item getHolyItem() {
        return this.holyItem;
    }
}
