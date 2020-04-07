// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.TraderRentalQuestion;
import com.wurmonline.server.Features;
import com.wurmonline.server.questions.TraderManagementQuestion;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

final class TraderBookBehaviour extends ItemBehaviour
{
    TraderBookBehaviour() {
        super((short)29);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        if (target.getTemplateId() != 299 || performer.isOnSurface()) {
            toReturn.add(Actions.actionEntrys[85]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        if (target.getTemplateId() != 299 || performer.isOnSurface()) {
            toReturn.add(Actions.actionEntrys[85]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 85) {
            if (target.getTemplateId() == 300) {
                if (target.getData() == -1L && !Methods.isActionAllowed(performer, action)) {
                    return true;
                }
                final TraderManagementQuestion tq = new TraderManagementQuestion(performer, "Managing merchant.", "Set the options you prefer:", target.getWurmId());
                tq.sendQuestion();
            }
            else if (target.getTemplateId() == 299 && performer.isOnSurface()) {
                if (!Features.Feature.BLOCKED_TRADERS.isEnabled()) {
                    if (target.getData() == -1L && !Methods.isActionAllowed(performer, action)) {
                        return true;
                    }
                    final TraderRentalQuestion tq2 = new TraderRentalQuestion(performer, "Managing trader.", "Set the options you prefer:", target.getWurmId());
                    tq2.sendQuestion();
                }
                else {
                    performer.getCommunicator().sendSafeServerMessage("Trader contracts are disabled on this server");
                }
            }
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 85) {
            if (target.getTemplateId() == 300) {
                final TraderManagementQuestion tq = new TraderManagementQuestion(performer, "Managing merchant", "Personal merchant management", target.getWurmId());
                tq.sendQuestion();
            }
            else if (target.getTemplateId() == 299 && performer.isOnSurface()) {
                if (!Features.Feature.BLOCKED_TRADERS.isEnabled()) {
                    final TraderRentalQuestion tq2 = new TraderRentalQuestion(performer, "Managing trader", "Normal trader management", target.getWurmId());
                    tq2.sendQuestion();
                }
                else {
                    performer.getCommunicator().sendSafeServerMessage("Trader contracts are disabled on this server");
                }
            }
        }
        else {
            done = super.action(act, performer, source, target, action, counter);
        }
        return done;
    }
}
