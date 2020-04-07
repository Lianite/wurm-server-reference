// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.creatures.Traits;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public class GmSetTraits extends Question
{
    private static final Logger logger;
    private final Creature creature;
    
    public GmSetTraits(final Creature aResponder, final Creature aTarget) {
        super(aResponder, "Creature Traits", aTarget.getName() + " Traits", 103, aTarget.getWurmId());
        this.creature = aTarget;
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            GmSetTraits.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 103 && this.getResponder().getPower() >= 4) {
            boolean somethingChanged = false;
            for (int x = 0; x < 64; ++x) {
                final boolean was = this.creature.getStatus().isTraitBitSet(x);
                final boolean is = Boolean.parseBoolean(aAnswer.getProperty("sel" + x));
                if (is != was) {
                    this.creature.getStatus().setTraitBit(x, is);
                    GmSetTraits.logger.log(Level.INFO, this.getResponder().getName() + " setting trait " + x + " " + this.creature.getName() + ", " + this.creature.getWurmId());
                    this.getResponder().getLogger().log(Level.INFO, " setting trait " + x + " " + this.creature.getName() + ", " + this.creature.getWurmId());
                    somethingChanged = true;
                }
            }
            if (somethingChanged) {
                this.creature.refreshVisible();
                final GmSetTraits gt = new GmSetTraits(this.getResponder(), this.creature);
                gt.sendQuestion();
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.getResponder().getPower() >= 4) {
            for (int x = 0; x < 64; ++x) {
                String traitName = Traits.getTraitString(x);
                if (x == 15) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 16) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 17) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 18) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 24) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 25) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 23) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 30) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 31) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 32) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 33) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                else if (x == 34) {
                    traitName = StringUtilities.raiseFirstLetterOnly(this.creature.getColourName(x));
                }
                if (traitName.length() > 0) {
                    String colour = "";
                    if (Traits.isTraitNegative(x)) {
                        colour = "color=\"255,66,66\";";
                    }
                    else if (!Traits.isTraitNeutral(x)) {
                        colour = "color=\"66,255,66\";";
                    }
                    buf.append("harray{checkbox{id=\"sel" + x + "\";selected=\"" + this.creature.getStatus().isTraitBitSet(x) + "\"};label{" + colour + "text=\"" + traitName + "\"};}");
                }
            }
            buf.append("label{text=\"\"};");
            buf.append("text{type=\"bold\";text=\"--------------- Help -------------------\"}");
            buf.append("text{text=\"Can add or remove traits.\"}");
            buf.append("text{text=\"If anything is changed, then once the change is applied it will show this screen again.\"}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(250, 500, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
    
    static {
        logger = Logger.getLogger(GmSetTraits.class.getName());
    }
}
