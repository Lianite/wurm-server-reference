// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class CornucopiaBehaviour extends ItemBehaviour
{
    private static final Logger logger;
    
    CornucopiaBehaviour() {
        super((short)30);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, target);
        if (WurmPermissions.mayCreateItems(performer)) {
            toReturn.add(Actions.actionEntrys[148]);
        }
        else {
            CornucopiaBehaviour.logger.warning(performer.getName() + " tried to use a Cornucopia but their power was only " + performer.getPower());
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item target) {
        final List<ActionEntry> toReturn = super.getBehavioursFor(performer, source, target);
        if (WurmPermissions.mayCreateItems(performer)) {
            toReturn.add(Actions.actionEntrys[148]);
        }
        else {
            CornucopiaBehaviour.logger.warning(performer.getName() + " tried to use a Cornucopia but their power was only " + performer.getPower());
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 148) {
            done = true;
            if (WurmPermissions.mayCreateItems(performer)) {
                Methods.sendCreateQuestion(performer, source);
            }
            else {
                CornucopiaBehaviour.logger.warning(performer.getName() + " tried to use a Cornucopia but their power was only " + performer.getPower());
            }
        }
        else {
            done = super.action(act, performer, source, target, action, counter);
        }
        return done;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean done = true;
        if (action == 148) {
            done = true;
            if (WurmPermissions.mayCreateItems(performer)) {
                Methods.sendCreateQuestion(performer, target);
            }
            else {
                CornucopiaBehaviour.logger.warning(performer.getName() + " tried to use a Cornucopia but their power was only " + performer.getPower());
            }
        }
        else {
            done = super.action(act, performer, target, action, counter);
        }
        return done;
    }
    
    static {
        logger = Logger.getLogger(CornucopiaBehaviour.class.getName());
    }
}
