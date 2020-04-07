// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class VillageJoinQuestion extends Question
{
    private static final Logger logger;
    private final Creature invited;
    
    public VillageJoinQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) throws NoSuchCreatureException, NoSuchPlayerException {
        super(aResponder, aTitle, aQuestion, 11, aTarget);
        this.invited = Server.getInstance().getCreature(aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseVillageJoinQuestion(this);
    }
    
    public Creature getInvited() {
        return this.invited;
    }
    
    @Override
    public void sendQuestion() {
        final Village village = this.getResponder().getCitizenVillage();
        if (village != null) {
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            buf.append("text{type=\"bold\";text=\"Joining settlement " + village.getName() + ":\"}");
            buf.append("text{text=\"You have been invited by " + this.getResponder().getName() + " to join " + village.getName() + ". \"}");
            if (this.getInvited().isPlayer() && this.getInvited().mayChangeVillageInMillis() > 0L) {
                buf.append("text{text=\"You may not change settlement in " + Server.getTimeFor(this.getInvited().mayChangeVillageInMillis()) + ". \"}");
            }
            else {
                final Village currvill = this.getInvited().getCitizenVillage();
                if (currvill != null) {
                    buf.append("text{text=\"Your " + currvill.getName() + " citizenship will be revoked. \"}");
                }
                else {
                    buf.append("text{text=\"You are currently not citizen in any settlement. \"}");
                }
                if (village.isDemocracy()) {
                    buf.append("text{text=\"" + village.getName() + " is a democracy. This means your citizenship cannot be revoked by any city officials such as the mayor. \"}");
                }
                else {
                    buf.append("text{text=\"" + village.getName() + " is a non-democracy. This means your citizenship can be revoked by any city officials such as the mayor. \"}");
                }
                buf.append("text{text=\"Do you want to join " + village.getName() + "?\"}");
                buf.append("radio{ group=\"join\"; id=\"true\";text=\"Yes\"}");
                buf.append("radio{ group=\"join\"; id=\"false\";text=\"No\";selected=\"true\"}");
            }
            buf.append(this.createAnswerButton2());
            this.getInvited().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        else {
            VillageJoinQuestion.logger.log(Level.WARNING, this.getResponder().getName() + " tried to invite to null settlement!");
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the settlement for that invitation. Please contact administration.");
        }
    }
    
    static {
        logger = Logger.getLogger(VillageJoinQuestion.class.getName());
    }
}
