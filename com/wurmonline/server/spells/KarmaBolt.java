// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.WurmId;
import java.util.logging.Level;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public class KarmaBolt extends KarmaSpell
{
    private static final Logger logger;
    public static final int RANGE = 24;
    
    public KarmaBolt() {
        super("Karma Bolt", 550, 10, 200, 10, 1, 180000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "sends a thick bolt of negative Karma towards the target";
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && !performer.getDeity().isHateGod() && performer.faithful && !performer.isDuelOrSpar(target)) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept your attack on " + target.getName() + ".", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if (!target.isUnique() || power > 99.0) {
            if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && !performer.getDeity().isHateGod() && !performer.isDuelOrSpar(target)) {
                performer.modifyFaith(-(100.0f - performer.getFaith()) / 50.0f);
            }
            try {
                this.sendBolt(performer, target, 0.0f, 0.0f, 0.0f, power);
            }
            catch (Exception exe) {
                KarmaBolt.logger.log(Level.WARNING, exe.getMessage(), exe);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You try to bolt " + target.getName() + " but fail.", (byte)3);
            target.getCommunicator().sendNormalServerMessage(performer.getName() + " tries to bolt you but fails.", (byte)4);
        }
    }
    
    private final void sendBolt(final Creature performer, final Creature target, final float offx, final float offy, final float offz, final double power) throws Exception {
        VolaTile t = performer.getCurrentTile();
        final long shardId = WurmId.getNextTempItemId();
        if (t != null) {
            t.sendProjectile(shardId, (byte)4, "model.spell.ShardOfIce", "Karma Bolt", (byte)0, performer.getPosX() + offx, performer.getPosY() + offy, performer.getPositionZ() + performer.getAltOffZ() + offz, performer.getStatus().getRotation(), (byte)performer.getLayer(), (int)target.getPosX(), (int)target.getPosY(), target.getPositionZ() + target.getAltOffZ(), performer.getWurmId(), target.getWurmId(), 0.0f, 0.0f);
        }
        t = target.getCurrentTile();
        if (t != null) {
            t.sendProjectile(shardId, (byte)4, "model.spell.ShardOfIce", "Karma Bolt", (byte)0, performer.getPosX() + offx, performer.getPosY() + offy, performer.getPositionZ() + performer.getAltOffZ() + offz, performer.getStatus().getRotation(), (byte)performer.getLayer(), (int)target.getPosX(), (int)target.getPosY(), target.getPositionZ() + target.getAltOffZ(), performer.getWurmId(), target.getWurmId(), 0.0f, 0.0f);
        }
        final double damage = 5000.0 + 5000.0 * (power / 100.0);
        target.addWoundOfType(performer, (byte)6, 1, true, 1.0f, true, damage, 20.0f, 0.0f, false, true);
    }
    
    @Override
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        performer.getCommunicator().sendNormalServerMessage("You try to send negative karma to " + target.getName() + " but fail.", (byte)3);
        target.getCommunicator().sendNormalServerMessage(performer.getName() + " tries to give you negative karma but fails.", (byte)4);
    }
    
    static {
        logger = Logger.getLogger(KarmaMissile.class.getName());
    }
}
