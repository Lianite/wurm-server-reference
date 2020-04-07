// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import java.util.logging.Level;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.AttitudeConstants;

public class FireHeart extends DamageSpell implements AttitudeConstants
{
    private static Logger logger;
    public static final int RANGE = 50;
    public static final double BASE_DAMAGE = 9000.0;
    public static final double DAMAGE_PER_POWER = 80.0;
    
    public FireHeart() {
        super("Fireheart", 424, 7, 20, 20, 35, 30000L);
        this.targetCreature = true;
        this.offensive = true;
        this.description = "damages the targets heart with superheated fire";
        this.type = 2;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && performer.faithful && !performer.isDuelOrSpar(target)) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept your attack on " + target.getNameWithGenus() + ".", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        if ((target.isHuman() || target.isDominated()) && target.getAttitude(performer) != 2 && !performer.isDuelOrSpar(target)) {
            performer.modifyFaith(-5.0f);
        }
        final VolaTile t = target.getCurrentTile();
        if (t != null) {
            t.sendAddQuickTileEffect((byte)35, target.getFloorLevel());
            t.sendAttachCreatureEffect(target, (byte)5, (byte)0, (byte)0, (byte)0, (byte)0);
        }
        try {
            final byte pos = target.getBody().getCenterWoundPos();
            final double damage = this.calculateDamage(target, power, 9000.0, 80.0);
            target.addWoundOfType(performer, (byte)4, pos, false, 1.0f, false, damage, 0.0f, 0.0f, false, true);
        }
        catch (Exception exe) {
            FireHeart.logger.log(Level.WARNING, exe.getMessage(), exe);
        }
    }
    
    static {
        FireHeart.logger = Logger.getLogger(FireHeart.class.getName());
    }
}
