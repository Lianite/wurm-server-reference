// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.logging.Level;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.Server;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class AllianceQuestion extends Question
{
    private static final Logger logger;
    
    public AllianceQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 18, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parsePvPAllianceQuestion(this);
    }
    
    public final String getAllianceName() {
        try {
            final Creature sender = Server.getInstance().getCreature(this.target);
            if (sender.getCitizenVillage() != null) {
                final PvPAlliance all = PvPAlliance.getPvPAlliance(sender.getCitizenVillage().getAllianceNumber());
                if (all != null) {
                    return all.getName();
                }
                if (this.getResponder().getCitizenVillage() != null) {
                    return sender.getCitizenVillage().getName().substring(0, Math.min(8, sender.getCitizenVillage().getName().length())) + "and" + this.getResponder().getCitizenVillage().getName().substring(0, Math.min(8, this.getResponder().getCitizenVillage().getName().length()));
                }
                return sender.getCitizenVillage().getName();
            }
        }
        catch (Exception nsc) {
            AllianceQuestion.logger.log(Level.WARNING, nsc.getMessage(), nsc);
        }
        return "unknown";
    }
    
    @Override
    public void sendQuestion() {
        try {
            final Creature sender = Server.getInstance().getCreature(this.target);
            final Village senderVillage = sender.getCitizenVillage();
            final Village responderVillage = this.getResponder().getCitizenVillage();
            if (senderVillage.equals(responderVillage)) {
                sender.getCommunicator().sendNormalServerMessage("You cannot form an alliance within a settlement.");
            }
            else {
                final PvPAlliance respAlliance = PvPAlliance.getPvPAlliance(responderVillage.getAllianceNumber());
                if (respAlliance != null) {
                    sender.getCommunicator().sendNormalServerMessage(this.getResponder().getName() + " is already in the " + respAlliance.getName() + " alliance.");
                    return;
                }
                final StringBuilder buf = new StringBuilder();
                final VillageRole role = sender.getCitizenVillage().getCitizen(sender.getWurmId()).getRole();
                buf.append(this.getBmlHeader());
                buf.append("header{text=\"Citizens of " + responderVillage.getName() + "!\"}");
                buf.append("text{text=\"We, the citizens of " + senderVillage.getName() + ", under the wise leadership of " + sender.getName() + ", wish to declare our sincere intentions to invite you to the " + this.getAllianceName() + " alliance.\"}");
                buf.append("text{text='This union would stand forever, strengthening both our positions in this unfriendly world against common foes.'}");
                buf.append("text{text=''}");
                buf.append("text{text='We hope you see the possibilities in this, and return with a positive answer'}");
                buf.append("text{text=''}");
                buf.append("text{type='italic';text=\"" + sender.getName() + ", " + role.getName() + ", " + senderVillage.getName() + "\"}");
                buf.append("radio{ group='join'; id='accept';text='Accept'}");
                buf.append("radio{ group='join'; id='decline';text='Decline';selected='true'}");
                buf.append(this.createAnswerButton2());
                this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            }
        }
        catch (NoSuchCreatureException nsc) {
            AllianceQuestion.logger.log(Level.WARNING, nsc.getMessage(), nsc);
        }
        catch (NoSuchPlayerException nsp) {
            AllianceQuestion.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
    }
    
    static {
        logger = Logger.getLogger(AllianceQuestion.class.getName());
    }
}
