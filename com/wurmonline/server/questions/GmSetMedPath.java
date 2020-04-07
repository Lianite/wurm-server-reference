// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.players.Cults;
import com.wurmonline.server.players.Cultist;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class GmSetMedPath extends Question
{
    private static final Logger logger;
    private final Creature creature;
    
    public GmSetMedPath(final Creature aResponder, final Creature aTarget) {
        super(aResponder, "Meditation Path", aTarget.getName() + " Meditation Path", 132, aTarget.getWurmId());
        this.creature = aTarget;
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            GmSetMedPath.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 132 && this.getResponder().getPower() >= 4) {
            try {
                String prop = aAnswer.getProperty("newcult");
                Label_0242: {
                    if (prop != null) {
                        this.getResponder().getLogger().log(Level.INFO, "Setting meditation path and level of " + this.creature.getName());
                        final byte cultId = Byte.parseByte(prop);
                        Cultist c = this.creature.getCultist();
                        if (c != null || cultId <= 0) {
                            if (c != null) {
                                if (cultId > 0) {
                                    c.setPath(cultId);
                                    break Label_0242;
                                }
                                try {
                                    c.deleteCultist();
                                    if (this.creature != this.getResponder()) {
                                        this.creature.getCommunicator().sendNormalServerMessage("You are no longer following any path.");
                                    }
                                    this.getResponder().getCommunicator().sendNormalServerMessage(this.creature.getName() + " is no longer following any path.");
                                    this.creature.refreshVisible();
                                    this.creature.getCommunicator().sendOwnTitles();
                                }
                                catch (IOException ex) {
                                    break Label_0242;
                                }
                            }
                            return;
                        }
                        c = new Cultist(this.creature.getWurmId(), cultId);
                    }
                }
                prop = aAnswer.getProperty("cultlevel");
                if (prop != null) {
                    final byte cultLvl = Byte.parseByte(prop);
                    final Cultist c = this.creature.getCultist();
                    if (c != null) {
                        c.setLevel(cultLvl);
                    }
                }
                if (this.creature != this.getResponder()) {
                    this.creature.getCommunicator().sendNormalServerMessage("You are now " + this.creature.getCultist().getCultistTitle());
                }
                this.getResponder().getCommunicator().sendNormalServerMessage(this.creature.getName() + " is now " + this.creature.getCultist().getCultistTitle());
                this.creature.refreshVisible();
                this.creature.getCommunicator().sendOwnTitles();
            }
            catch (NumberFormatException nsf) {
                this.getResponder().getCommunicator().sendNormalServerMessage("Unable to set meditation path and level with those answers.");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getPower() >= 4) {
            buf.append("text{type=\"bold\";text=\"Choose path to set the target to:\"}");
            final Cultist c = this.creature.getCultist();
            final int cId = (c == null) ? 0 : c.getPath();
            boolean isSelected = false;
            for (int i = 0; i < 6; ++i) {
                isSelected = (i == cId);
                buf.append("radio{ group='newcult'; id='" + i + "'; text='" + Cults.getPathNameFor((byte)i) + "'" + (isSelected ? ";selected='true'" : "") + "}");
            }
            final int cLvl = (c == null) ? 0 : c.getLevel();
            buf.append("harray{input{id='cultlevel'; maxchars='2'; text='" + cLvl + "'}label{text='Path Level'}}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(250, 250, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
    
    static {
        logger = Logger.getLogger(GmSetMedPath.class.getName());
    }
}
