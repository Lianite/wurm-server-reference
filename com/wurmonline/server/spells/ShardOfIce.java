// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import java.util.logging.Level;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.AttitudeConstants;

public class ShardOfIce extends DamageSpell implements AttitudeConstants
{
    private static Logger logger;
    public static final int RANGE = 50;
    public static final double BASE_DAMAGE = 5000.0;
    public static final double DAMAGE_PER_POWER = 120.0;
    
    public ShardOfIce() {
        super("Shard of Ice", 485, 7, 20, 30, 35, 30000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "damages the targets body with a spear of ice causing frost damage";
        this.type = 2;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && performer.faithful && !performer.isDuelOrSpar(target)) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept your attack on " + target.getName() + ".", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && !performer.isDuelOrSpar(target)) {
            performer.modifyFaith(-(100.0f - performer.getFaith()) / 50.0f);
        }
        try {
            VolaTile t = performer.getCurrentTile();
            final long shardId = WurmId.getNextTempItemId();
            if (t != null) {
                t.sendProjectile(shardId, (byte)4, "model.spell.ShardOfIce", "Shard Of Ice", (byte)0, performer.getPosX(), performer.getPosY(), performer.getPositionZ() + performer.getAltOffZ(), performer.getStatus().getRotation(), (byte)performer.getLayer(), (int)target.getPosX(), (int)target.getPosY(), target.getPositionZ() + target.getAltOffZ(), performer.getWurmId(), target.getWurmId(), 0.0f, 0.0f);
            }
            t = target.getCurrentTile();
            if (t != null) {
                t.sendProjectile(shardId, (byte)4, "model.spell.ShardOfIce", "Shard Of Ice", (byte)0, performer.getPosX(), performer.getPosY(), performer.getPositionZ() + performer.getAltOffZ(), performer.getStatus().getRotation(), (byte)performer.getLayer(), (int)target.getPosX(), (int)target.getPosY(), target.getPositionZ() + target.getAltOffZ(), performer.getWurmId(), target.getWurmId(), 0.0f, 0.0f);
            }
            final byte pos = target.getBody().getCenterWoundPos();
            final double damage = this.calculateDamage(target, power, 5000.0, 120.0);
            target.addWoundOfType(performer, (byte)8, pos, false, 1.0f, false, damage, 0.0f, 0.0f, false, true);
        }
        catch (Exception exe) {
            ShardOfIce.logger.log(Level.WARNING, exe.getMessage(), exe);
        }
    }
    
    static {
        ShardOfIce.logger = Logger.getLogger(FireHeart.class.getName());
    }
}
