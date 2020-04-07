// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public class LCMManagementQuestion extends Question
{
    private short actionType;
    
    public LCMManagementQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final short actionType) {
        super(aResponder, aTitle, aQuestion, 128, aTarget);
        this.actionType = actionType;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseLCMManagementQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{text='Who do you want to " + this.getActionVerb() + "?'};");
        buf.append("label{text'Name:'};input{id='name';maxchars='40';text=\"\"};");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String getActionVerb() {
        if (this.actionType == 698) {
            return "add or remove their CA status from";
        }
        if (this.actionType == 699) {
            return "add or remove their CM status from";
        }
        if (this.actionType == 700) {
            return "see their info of";
        }
        return "";
    }
    
    public short getActionType() {
        return this.actionType;
    }
}
