// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

final class MenuRequestBehaviour extends Behaviour
{
    private static final Logger logger;
    
    MenuRequestBehaviour() {
        super((short)53);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int menuId) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (menuId == 0) {
            toReturn.addAll(ManageMenu.getBehavioursFor(performer));
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int menuId, final short action, final float counter) {
        final boolean done = true;
        return menuId != 0 || !ManageMenu.isManageAction(performer, action) || ManageMenu.action(act, performer, action, counter);
    }
    
    static {
        logger = Logger.getLogger(MenuRequestBehaviour.class.getName());
    }
}
