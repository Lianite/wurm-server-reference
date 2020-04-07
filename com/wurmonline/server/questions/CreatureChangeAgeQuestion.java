// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.io.IOException;
import com.wurmonline.server.creatures.DbCreatureStatus;
import java.util.Properties;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.Creature;

public final class CreatureChangeAgeQuestion extends Question
{
    public CreatureChangeAgeQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 153, aTarget);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        final int width = 150;
        final int height = 150;
        try {
            final Creature target = Creatures.getInstance().getCreature(this.target);
            final int age = target.getStatus().age;
            buf.append("harray{input{id='newAge'; maxchars='3'; text='").append(age).append("'}label{text='Age'}}");
        }
        catch (NoSuchCreatureException ex) {
            ex.printStackTrace();
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(width, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        this.init(this);
    }
    
    private void init(final CreatureChangeAgeQuestion question) {
        final Creature responder = question.getResponder();
        int newAge = 0;
        final long target = question.getTarget();
        try {
            final Creature creature = Creatures.getInstance().getCreature(target);
            final String age = question.getAnswer().getProperty("newAge");
            newAge = Integer.parseInt(age);
            ((DbCreatureStatus)creature.getStatus()).updateAge(newAge);
            creature.getStatus().lastPolledAge = 0L;
            creature.pollAge();
            creature.refreshVisible();
        }
        catch (NoSuchCreatureException | IOException ex3) {
            final Exception ex2;
            final Exception ex = ex2;
            ex.printStackTrace();
        }
        responder.getCommunicator().sendNormalServerMessage("Age = " + newAge + ".");
    }
}
