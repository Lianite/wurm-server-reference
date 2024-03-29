// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Server;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class AskKingdomQuestion extends Question
{
    public AskKingdomQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 38, aTarget);
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseAskKingdomQuestion(this);
    }
    
    @Override
    public void sendQuestion() {
        try {
            final Creature asker = Server.getInstance().getCreature(this.target);
            final String kname = Kingdoms.getNameFor(asker.getKingdomId());
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            if (asker.getKingdomId() != this.getResponder().getKingdomId()) {
                buf.append("text{text='" + asker.getName() + " asks if you wish to join " + kname + ".'}");
                if (this.getResponder().getAppointments() != 0L) {
                    buf.append("text{type='bold';text='If you accept, you will loose all your royal orders, titles and appointments!'}");
                }
                if (!Servers.localServer.HOMESERVER && Kingdoms.getKingdom(asker.getKingdomId()).isCustomKingdom() && this.getResponder().getCitizenVillage() != null && this.getResponder().getCitizenVillage().getMayor().wurmId == this.getResponder().getWurmId()) {
                    buf.append("text{type='bold';text='If you accept, your whole village will convert!'}");
                }
                buf.append("text{text='Do you want to join " + kname + "?'}");
                buf.append("radio{ group='conv'; id='true';text='Yes'}");
                buf.append("radio{ group='conv'; id='false';text='No';selected='true'}");
            }
            else {
                buf.append("text{text='You are already in " + kname + ".'}");
            }
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            asker.getCommunicator().sendNormalServerMessage("You ask " + this.getResponder().getName() + " to join " + kname + ".");
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
    }
}
