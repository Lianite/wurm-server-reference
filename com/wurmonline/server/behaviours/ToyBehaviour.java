// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;

final class ToyBehaviour extends ItemBehaviour
{
    ToyBehaviour() {
        super((short)26);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (object.getTemplateId() == 271) {
            toReturn.add(Actions.actionEntrys[190]);
        }
        toReturn.addAll(super.getBehavioursFor(performer, object));
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Item object) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (object.getTemplateId() == 271) {
            toReturn.add(Actions.actionEntrys[190]);
        }
        toReturn.addAll(super.getBehavioursFor(performer, source, object));
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item target, final short action, final float counter) {
        boolean toReturn = true;
        if (action == 190) {
            if (target.getTemplateId() == 271) {
                toReturn = MethodsItems.yoyo(performer, target, counter, act);
            }
        }
        else {
            toReturn = super.action(act, performer, target, action, counter);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Item target, final short action, final float counter) {
        boolean toReturn = false;
        if (target.getTemplateId() == 271 && action == 190) {
            toReturn = this.action(act, performer, target, action, counter);
        }
        else {
            toReturn = super.action(act, performer, source, target, action, counter);
        }
        return toReturn;
    }
}
