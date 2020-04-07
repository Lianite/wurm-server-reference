// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class WisdomVynora extends ReligiousSpell
{
    public static final int RANGE = 12;
    
    public WisdomVynora() {
        super("Wisdom of Vynora", 445, 30, 30, 50, 30, 1800000L);
        this.targetCreature = true;
        this.description = "transfers fatigue to sleep bonus";
        this.type = 1;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if (target.isReborn()) {
            return true;
        }
        if (target.getFatigueLeft() < 100) {
            performer.getCommunicator().sendNormalServerMessage(target.getName() + " has almost no fatigue left.", (byte)3);
            return false;
        }
        if (target.equals(performer)) {
            return true;
        }
        if (performer.getDeity() == null) {
            return true;
        }
        if (target.getDeity() == null) {
            return true;
        }
        if (!target.getDeity().isHateGod()) {
            return true;
        }
        if (performer.isFaithful()) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never help the infidel " + target.getName() + "!", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        double toconvert = Math.max(20.0, power);
        toconvert = Math.min(99.0, toconvert + performer.getNumLinks() * 10);
        toconvert /= 100.0;
        int numsecondsToMove = Math.min((int)(target.getFatigueLeft() / 12.0f * toconvert), 3600);
        target.setFatigue(-numsecondsToMove);
        numsecondsToMove *= (int)0.2f;
        if (target.isPlayer()) {
            ((Player)target).getSaveFile().addToSleep(numsecondsToMove);
        }
    }
}
