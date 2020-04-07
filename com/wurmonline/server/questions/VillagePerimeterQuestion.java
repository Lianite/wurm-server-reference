// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public final class VillagePerimeterQuestion extends Question
{
    private final int villageId;
    
    public VillagePerimeterQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final int villid) {
        super(aResponder, aTitle, aQuestion, 75, aTarget);
        this.villageId = villid;
    }
    
    @Override
    public void answer(final Properties props) {
    }
    
    @Override
    public void sendQuestion() {
        if (!Servers.localServer.HOMESERVER) {
            final StringBuilder buf = new StringBuilder(this.getBmlHeaderWithScroll());
            buf.append("text{type='bold';text='Perimeters are not active on wild servers.'}");
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        else {
            try {
                final Village village = Villages.getVillage(this.villageId);
                final String deedType = "settlement";
                final StringBuilder buf2 = new StringBuilder(this.getBmlHeaderWithScroll());
                buf2.append("text{type='bold';text='Set the permissions for the settlement perimeter.'}");
                buf2.append("text{text=''}");
                final int max = (village.getEndX() - village.getStartX()) / 2;
                buf2.append("text{text=\"Perimeter friends. These may do all these things in your perimeter. " + village.getName() + " may have up to " + max + " friends.\"}");
                buf2.append(this.createAnswerButton3());
                this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf2.toString(), 200, 200, 200, this.title);
            }
            catch (NoSuchVillageException nsv) {
                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to update perimeter settings. No such village could be located.");
            }
        }
    }
}
