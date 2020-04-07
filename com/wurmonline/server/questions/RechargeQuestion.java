// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.economy.MonetaryConstants;

public final class RechargeQuestion extends Question implements MonetaryConstants
{
    public RechargeQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 73, aTarget);
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        try {
            final Item wand = Items.getItem(this.target);
            if (wand.getOwnerId() == this.getResponder().getWurmId()) {
                final String key = "recharge";
                final String val = aAnswers.getProperty("recharge");
                if (val != null && val.equals("true")) {
                    if (wand.getQualityLevel() < 90.0f) {
                        try {
                            if (QuestionParser.charge(this.getResponder(), 50000L, "Recharging", 0.3f)) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("Something rummages through your pockets. The " + wand.getName() + " hums softly.");
                                wand.setQualityLevel(wand.getQualityLevel() + 10.0f);
                            }
                            else {
                                this.getResponder().getCommunicator().sendNormalServerMessage("The spirits demand that you carry 5 silver in your inventory in coinage.");
                            }
                        }
                        catch (Exception ex) {
                            this.getResponder().getCommunicator().sendNormalServerMessage(ex.getMessage());
                        }
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You may not recharge the " + wand.getName() + " while it has a quality level above 89.");
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to recharge the " + wand.getName() + " for now.");
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("You are not in possession of the " + wand.getName() + " any longer and may not recharge it.");
            }
        }
        catch (NoSuchItemException nsi) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You can not recharge the item now.");
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        try {
            final Item item = Items.getItem(this.target);
            buf.append("label{text=\"Recharge the " + item.getName() + "?\"};");
            buf.append("text{text='Recharging will cost 5 silver coins and increase the quality level by 10 to a maximum of 99.'}text{text=''}");
            buf.append("text{text='You need to have the coins in your inventory.'}text{text=''}");
            buf.append("text{text=\"The current quality level is " + item.getQualityLevel() + ".\"}text{text=''}");
            buf.append("text{text=\"\"}");
            if (item.getQualityLevel() > 89.0f) {
                buf.append("text{text=\"You may not recharge the item yet.\"}");
            }
            else {
                buf.append("text{type='italic';text=\"Do you want to recharge the " + item.getName() + "?\"}");
                buf.append("radio{ group='recharge'; id='true';text='Yes'}");
                buf.append("radio{ group='recharge'; id='false';text='No';selected='true'}");
            }
        }
        catch (NoSuchItemException nsi) {
            buf.append("text{text='The item can not be found.'}text{text=''}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
