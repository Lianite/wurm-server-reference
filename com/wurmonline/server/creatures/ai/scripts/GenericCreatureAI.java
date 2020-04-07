// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai.scripts;

import com.wurmonline.server.creatures.ai.Path;
import com.wurmonline.server.Server;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.creatures.Creatures;
import java.util.ArrayList;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.creatures.ai.CreatureAI;

public abstract class GenericCreatureAI extends CreatureAI implements TimeConstants
{
    private static final int T_NEWMOVEMENT = 0;
    private static final long TD_NEWMOVEMENT = 30000L;
    
    @Override
    protected boolean pollMovement(final Creature c, final long delta) {
        final GenericCreatureAIData aiData = (GenericCreatureAIData)c.getCreatureAIData();
        if (aiData.isMovementFrozen()) {
            return false;
        }
        if (c.getStatus().getPath() == null) {
            if (c.getTarget() != null) {
                if (!c.getTarget().isWithinDistanceTo(c, 6.0f)) {
                    c.startPathingToTile(this.getMovementTarget(c, c.getTarget().getTileX(), c.getTarget().getTileY()));
                }
                else {
                    c.setOpponent(c.getTarget());
                }
            }
            else if (c.getLatestAttackers().length > 0) {
                final long[] attackers = c.getLatestAttackers();
                final ArrayList<Creature> attackerList = new ArrayList<Creature>();
                for (final long a : attackers) {
                    try {
                        attackerList.add(Creatures.getInstance().getCreature(a));
                    }
                    catch (NoSuchCreatureException ex) {}
                }
                Collections.sort(attackerList, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature creature1, final Creature creature2) {
                        float distance1 = creature1.getPos2f().distance(c.getPos2f());
                        float distance2 = creature2.getPos2f().distance(c.getPos2f());
                        if (aiData.doesPreferPlayers()) {
                            if (creature1.isPlayer() && !creature2.isPlayer()) {
                                distance1 *= aiData.getPrefersPlayersModifier();
                            }
                            if (!creature1.isPlayer() && creature2.isPlayer()) {
                                distance2 *= aiData.getPrefersPlayersModifier();
                            }
                        }
                        if (distance1 < distance2) {
                            return -1;
                        }
                        if (distance2 > distance1) {
                            return 1;
                        }
                        return 0;
                    }
                });
                for (boolean gotTarget = false; !gotTarget && !attackerList.isEmpty(); gotTarget = true) {
                    final Creature newTarget = attackerList.remove(0);
                    if (newTarget.isWithinDistanceTo(c, c.getMaxHuntDistance())) {
                        c.setTarget(newTarget.getWurmId(), true);
                        if (!c.getTarget().isWithinDistanceTo(c, 6.0f)) {
                            c.startPathingToTile(this.getMovementTarget(c, c.getTarget().getTileX(), c.getTarget().getTileY()));
                        }
                    }
                }
            }
            else if (aiData.hasTether() && !c.isWithinTileDistanceTo((int)aiData.getTetherPos().x, (int)aiData.getTetherPos().y, 0, aiData.getTetherDistance())) {
                c.startPathingToTile(this.getMovementTarget(c, (int)aiData.getTetherPos().x, (int)aiData.getTetherPos().y));
            }
            else if (!this.addPathToInteresting(c, delta)) {
                this.increaseTimer(c, delta, 0);
                if (this.isTimerReady(c, 0, 30000L) && Server.rand.nextFloat() < aiData.getRandomMovementChance()) {
                    this.simpleMovementTick(c);
                    this.resetTimer(c, 0);
                }
            }
        }
        else {
            this.pathedMovementTick(c);
            if (c.getStatus().getPath().isEmpty()) {
                c.getStatus().setPath(null);
                c.getStatus().setMoving(false);
            }
        }
        return false;
    }
    
    @Override
    public void creatureCreated(final Creature c) {
        final GenericCreatureAIData aiData = (GenericCreatureAIData)c.getCreatureAIData();
        if (aiData.hasTether()) {
            aiData.setTether(c.getTileX(), c.getTileY());
        }
    }
    
    protected abstract boolean addPathToInteresting(final Creature p0, final long p1);
}
