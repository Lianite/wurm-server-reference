// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.villages.VillageStatus;

public final class VillageExpansionQuestion extends Question implements VillageStatus, ItemTypes
{
    private static final Logger logger;
    private final Item token;
    
    public VillageExpansionQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final Item aToken) {
        super(aResponder, aTitle, aQuestion, 12, aTarget);
        this.token = aToken;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseVillageExpansionQuestion(this);
    }
    
    public Item getToken() {
        return this.token;
    }
    
    @Override
    public void sendQuestion() {
        final int villid = this.token.getData2();
        try {
            final Item deed = Items.getItem(this.target);
            final int oldVill = deed.getData2();
            if (oldVill != -1) {
                try {
                    this.getResponder().getCommunicator().sendSafeServerMessage("This is the deed for " + Villages.getVillage(oldVill).getName() + "! You cannot use it to expand a settlement!");
                }
                catch (NoSuchVillageException nsv) {
                    this.getResponder().getCommunicator().sendSafeServerMessage("This deed already is already used! You cannot use it to expand this settlement!");
                }
                return;
            }
            final Village village = Villages.getVillage(villid);
            final StringBuilder buf = new StringBuilder(this.getBmlHeader());
            if (village != null) {
                final int size = Villages.getSizeForDeed(deed.getTemplateId());
                buf.append("text{text='The expansion will set the size of the settlement to " + size + " tiles out in all directions from the " + this.token.getName() + ".'}");
                buf.append("text{text='You will require all the house deeds for any houses in the new area.'}");
                buf.append("text{text='Also note that in the case that the allowed number of citizens is decreased any surplus will be kicked from the settlement automatically and in no particular order so you may want to do that manually instead.'}");
                buf.append(this.createAnswerButton2());
                this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            }
            else {
                this.getResponder().getCommunicator().sendSafeServerMessage("This token has no settlement associated with it. It cannot be expanded.");
            }
        }
        catch (NoSuchItemException nsi) {
            VillageExpansionQuestion.logger.log(Level.WARNING, "Failed to locate settlement with id " + this.target, nsi);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nss) {
            VillageExpansionQuestion.logger.log(Level.WARNING, "Failed to locate settlement with id " + villid, nss);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the settlement for that request. Please contact administration.");
        }
    }
    
    static {
        logger = Logger.getLogger(VillageExpansionQuestion.class.getName());
    }
}
