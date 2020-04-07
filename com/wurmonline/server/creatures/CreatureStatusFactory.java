// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.logging.Level;
import java.util.logging.Logger;

final class CreatureStatusFactory
{
    private static final Logger logger;
    
    static CreatureStatus createCreatureStatus(final Creature creature, final float posx, final float posy, final float rot, final int layer) throws Exception {
        CreatureStatus toReturn = null;
        toReturn = new DbCreatureStatus(creature, posx, posy, rot, layer);
        if (CreatureStatusFactory.logger.isLoggable(Level.FINEST)) {
            CreatureStatusFactory.logger.finest("Created new CreatureStatus: " + toReturn);
        }
        return toReturn;
    }
    
    static {
        logger = Logger.getLogger(CreatureStatusFactory.class.getName());
    }
}
