// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import java.util.List;
import com.wurmonline.server.TimeConstants;

public final class Battles implements TimeConstants
{
    private static final List<Battle> battles;
    private static final Logger logger;
    
    public static Battle getBattleFor(final Creature creature) {
        for (final Battle battle : Battles.battles) {
            if (battle.containsCreature(creature)) {
                return battle;
            }
        }
        return null;
    }
    
    public static Battle getBattleFor(final Creature attacker, final Creature defender) {
        final Battle bone = getBattleFor(attacker);
        final Battle btwo = getBattleFor(defender);
        Battle toReturn = null;
        if (bone == null && btwo == null) {
            toReturn = new Battle(attacker, defender);
            Battles.battles.add(toReturn);
        }
        else if (bone == null && btwo != null) {
            btwo.addCreature(attacker);
            toReturn = btwo;
        }
        else if (btwo == null && bone != null) {
            bone.addCreature(defender);
            toReturn = bone;
        }
        else {
            toReturn = mergeBattles(bone, btwo);
        }
        return toReturn;
    }
    
    public static Battle mergeBattles(final Battle battleOne, final Battle battleTwo) {
        if (battleTwo != null && battleOne != null) {
            final Creature[] creatures;
            final Creature[] bonec = creatures = battleTwo.getCreatures();
            for (final Creature lElement : creatures) {
                battleOne.addCreature(lElement);
            }
            Battles.battles.remove(battleTwo);
        }
        else {
            Battles.logger.warning("Cannot merge null battles: battleOne: " + battleOne + ", battleTwo: " + battleTwo);
        }
        return battleOne;
    }
    
    public static void poll(final boolean shutdown) {
        final long now = System.currentTimeMillis();
        final ListIterator<Battle> it = Battles.battles.listIterator();
        while (it.hasNext()) {
            final Battle battle = it.next();
            if (battle.getCreatures().length <= 1 || now - battle.getEndTime() > 300000L || shutdown) {
                battle.save();
                battle.clearCreatures();
                it.remove();
            }
        }
    }
    
    static {
        battles = new LinkedList<Battle>();
        logger = Logger.getLogger(Battles.class.getName());
    }
}
