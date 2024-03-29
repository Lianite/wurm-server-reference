// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.awt.Color;
import com.wurmonline.server.utils.BMLBuilder;
import com.wurmonline.server.deities.Deity;
import java.io.IOException;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.players.Player;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public class TransferQuestion extends Question
{
    public TransferQuestion(final Creature aResponder, final String aTitle, final String aQuestion) {
        super(aResponder, aTitle, aQuestion, 88, -10L);
    }
    
    @Override
    public void answer(final Properties answers) {
        if (this.getResponder().isPlayer()) {
            final Player rp = (Player)this.getResponder();
            if (rp.hasFlag(74)) {
                rp.getCommunicator().sendNormalServerMessage("You do not have a free faith transfer available.");
                return;
            }
            if (rp.getDeity() == null) {
                rp.getCommunicator().sendNormalServerMessage("You currently pray to no deity and cannot transfer to a new one.");
                return;
            }
            if (rp.isChampion()) {
                rp.getCommunicator().sendNormalServerMessage("Champions cannot convert faith with this command.");
                return;
            }
            final String key = "deityid";
            final String val = answers.getProperty("deityid");
            if (val != null) {
                try {
                    final int index = Integer.parseInt(val);
                    final int newDeity = Deities.getDeities()[index].getNumber();
                    if (newDeity == 0 || newDeity == rp.getDeity().getNumber()) {
                        rp.getCommunicator().sendNormalServerMessage("You decide not to change deity for now.");
                        return;
                    }
                    final Deity newd = Deities.getDeity(newDeity);
                    if (!QuestionParser.doesKingdomTemplateAcceptDeity(rp.getKingdomTemplateId(), newd)) {
                        rp.getCommunicator().sendNormalServerMessage("Your kingdom does not allow following that god.");
                        return;
                    }
                    try {
                        rp.getSaveFile().transferDeity(newd);
                        rp.getCommunicator().sendNormalServerMessage("You decide to use your transfer and change deity to " + newd.getName() + ".");
                        rp.setFlag(74, true);
                    }
                    catch (IOException iox) {
                        rp.getCommunicator().sendNormalServerMessage("An exception occurred when changing deity. Please try again later or use /support if this persists.");
                    }
                }
                catch (NumberFormatException nfe) {
                    rp.getCommunicator().sendNormalServerMessage("Failed to parse index " + val);
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to change deity for now.");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final Deity[] deities = Deities.getDeities();
        final String[] deityNames = new String[deities.length];
        int defaultId = 0;
        for (int i = 0; i < deities.length; ++i) {
            deityNames[i] = deities[i].getName();
            if (deities[i] == this.getResponder().getDeity()) {
                defaultId = i;
            }
        }
        final BMLBuilder bml = BMLBuilder.createNormalWindow(Integer.toString(this.getId()), "Transfer your faith to which deity?", BMLBuilder.createGenericBuilder().addText("You currently have a single use free faith transfer from your current deity of " + this.getResponder().getDeity().getName() + " to another of your choice from the list below.").addText("").addText("Once you select which deity to transfer to and click accept, you will not be able to transfer back. Choose wisely.", null, BMLBuilder.TextType.BOLD, null).addText("").addText("Warning: Converting to a deity on Freedom then travelling to a Chaos kingdom that does not align with your deity you will lose all faith and abilities granted, and you will stop following that deity. Libila does not align with WL kingdoms and Fo/Vynora/Magranon do not align with BL kingdoms.", null, BMLBuilder.TextType.BOLD, Color.RED).addText("").addLabel("Choose a deity to transfer your faith to:").addDropdown("deityid", Integer.toString(defaultId), deityNames).addText("").addButton("submit", "Accept", null, null, null, true));
        this.getResponder().getCommunicator().sendBml(350, 330, true, true, bml.toString(), 200, 200, 200, this.title);
    }
}
