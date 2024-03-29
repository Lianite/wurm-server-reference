// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Actions;
import java.util.logging.Level;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public class KarmaMissile extends KarmaSpell
{
    private static final Logger logger;
    public static final int RANGE = 24;
    
    public KarmaMissile() {
        super("Karma Missile", 551, 15, 300, 15, 1, 60000L);
        this.targetCreature = true;
        this.description = "sends a flurry of negative energy missiles towards the target";
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
                this.sendMissile(performer, target, 0.0f, 0.0f, 0.0f, power);
                double attPower = this.rollAttack(performer, castSkill, target);
                if (attPower > 0.0) {
                    this.sendMissile(performer, target, 0.0f, 0.0f, -0.5f, attPower);
                }
                attPower = this.rollAttack(performer, castSkill, target);
                if (attPower > 0.0) {
                    this.sendMissile(performer, target, 0.0f, 0.0f, 0.5f, attPower);
                }
                attPower = this.rollAttack(performer, castSkill, target);
                if (attPower > 0.0) {
                    this.sendMissile(performer, target, 0.5f, 0.5f, 0.5f, attPower);
                }
                attPower = this.rollAttack(performer, castSkill, target);
                if (attPower > 0.0) {
                    this.sendMissile(performer, target, -0.5f, -0.5f, 0.5f, attPower);
                }
            }
            catch (Exception exe) {
                KarmaMissile.logger.log(Level.WARNING, exe.getMessage(), exe);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You try to missile " + target.getName() + " but fail.", (byte)3);
            target.getCommunicator().sendNormalServerMessage(performer.getName() + " tries to missile you but fails.", (byte)4);
        }
    }
    
    private final double rollAttack(final Creature performer, final Skill castSkill, final Creature target) {
        double distDiff = 0.0;
        final double dist = Creature.getRange(performer, target.getPosX(), target.getPosY());
        try {
            distDiff = dist - Actions.actionEntrys[this.number].getRange() / 2.0f;
            if (distDiff > 0.0) {
                distDiff *= 2.0;
            }
        }
        catch (Exception ex) {
            KarmaMissile.logger.log(Level.WARNING, this.getName() + " error: " + ex.getMessage());
        }
        return Spell.trimPower(performer, Math.max(Server.rand.nextFloat() * 10.0f, castSkill.skillCheck(distDiff + this.difficulty, performer.zoneBonus, true, 1.0f)));
    }
    
    private final void sendMissile(final Creature performer, final Creature target, final float offx, final float offy, final float offz, final double power) throws Exception {
        VolaTile t = performer.getCurrentTile();
        final long shardId = WurmId.getNextTempItemId();
        if (t != null) {
            t.sendProjectile(shardId, (byte)4, "model.spell.ShardOfIce", "Karma Missile", (byte)0, performer.getPosX() + offx, performer.getPosY() + offy, performer.getPositionZ() + performer.getAltOffZ() + offz, performer.getStatus().getRotation(), (byte)performer.getLayer(), (int)target.getPosX(), (int)target.getPosY(), target.getPositionZ() + target.getAltOffZ(), performer.getWurmId(), target.getWurmId(), 0.0f, 0.0f);
        }
        t = target.getCurrentTile();
        if (t != null) {
            t.sendProjectile(shardId, (byte)4, "model.spell.ShardOfIce", "Karma Missile", (byte)0, performer.getPosX() + offx, performer.getPosY() + offy, performer.getPositionZ() + performer.getAltOffZ() + offz, performer.getStatus().getRotation(), (byte)performer.getLayer(), (int)target.getPosX(), (int)target.getPosY(), target.getPositionZ() + target.getAltOffZ(), performer.getWurmId(), target.getWurmId(), 0.0f, 0.0f);
        }
        final byte pos = target.getBody().getRandomWoundPos();
        final double damage = 2500.0 + 3500.0 * (power / 100.0);
        if (performer.getPower() > 1 && Servers.isThisATestServer()) {
            performer.getCommunicator().sendNormalServerMessage("Damage: " + damage);
        }
        target.addWoundOfType(performer, (byte)10, pos, false, 1.0f, true, damage, 0.0f, 0.0f, false, true);
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
