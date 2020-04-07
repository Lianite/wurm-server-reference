// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public final class DrainHealth extends DamageSpell
{
    public static final int RANGE = 50;
    public static final double BASE_DAMAGE = 2000.0;
    public static final double DAMAGE_PER_POWER = 20.0;
    
    DrainHealth() {
        super("Drain Health", 255, 3, 15, 20, 19, 30000L);
        this.targetCreature = true;
        this.offensive = true;
        this.healing = true;
        this.description = "damages a creature internally and heals you";
        this.type = 2;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        double castDamage = power * 20.0;
        castDamage += 2000.0;
        double toHeal = castDamage * 2.0;
        final double healingResistance = SpellResist.getSpellResistance(performer, this.getNumber());
        toHeal *= healingResistance;
        if (toHeal > 1.0) {
            if (performer.getBody().getWounds() != null) {
                final Wound[] wounds = performer.getBody().getWounds().getWounds();
                if (wounds.length > 0) {
                    if (wounds[0].getSeverity() < toHeal) {
                        SpellResist.addSpellResistance(performer, this.getNumber(), wounds[0].getSeverity());
                        wounds[0].heal();
                    }
                    else {
                        SpellResist.addSpellResistance(performer, this.getNumber(), toHeal);
                        wounds[0].modifySeverity((int)(-toHeal));
                    }
                }
                else {
                    performer.getStatus().modifyWounds(-(int)(0.5 * toHeal));
                }
            }
            else {
                performer.getStatus().modifyWounds(-(int)(0.5 * toHeal));
            }
            byte pos = 1;
            try {
                pos = target.getBody().getRandomWoundPos();
            }
            catch (Exception ex) {
                pos = 1;
            }
            final double toDamage = this.calculateDamage(target, power, 2000.0, 20.0);
            target.addWoundOfType(performer, (byte)9, pos, false, 1.0f, false, toDamage, 0.0f, 0.0f, false, true);
            performer.getCommunicator().sendNormalServerMessage("You gain some health from " + target.getNameWithGenus() + ".", (byte)4);
            target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " drains you on health.", (byte)4);
            final VolaTile targetVolaTile = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
            if (targetVolaTile != null) {
                targetVolaTile.sendAttachCreatureEffect(target, (byte)8, (byte)0, (byte)0, (byte)0, (byte)0);
            }
            final VolaTile performerVolaTile = Zones.getTileOrNull(performer.getTileX(), performer.getTileY(), performer.isOnSurface());
            if (performerVolaTile != null) {
                performerVolaTile.sendAttachCreatureEffect(performer, (byte)9, (byte)0, (byte)0, (byte)0, (byte)0);
            }
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You try to drain some health from " + target.getNameWithGenus() + " but fail.", (byte)4);
            target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " tries to drain you on health but fails.", (byte)4);
        }
    }
    
    @Override
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        performer.getCommunicator().sendNormalServerMessage("You try to drain some health from " + target.getNameWithGenus() + " but fail.", (byte)4);
        target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " tries to drain you on health but fails.", (byte)4);
    }
}
