// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import java.util.logging.Level;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public final class CureMedium extends ReligiousSpell
{
    private static final Logger logger;
    public static final int RANGE = 12;
    
    CureMedium() {
        super("Cure Medium", 247, 12, 13, 10, 32, 0L);
        this.targetWound = true;
        this.targetCreature = true;
        this.healing = true;
        this.description = "heals a large amount of damage on a single wound";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Wound target) {
        if (target.getCreature() == null) {
            performer.getCommunicator().sendNormalServerMessage("You cannot heal that wound.", (byte)3);
            return false;
        }
        final Creature tCret = target.getCreature();
        if (tCret.isReborn()) {
            return true;
        }
        if (!tCret.isPlayer() || target.getCreature() == performer) {
            return true;
        }
        if (tCret.isFriendlyKingdom(performer.getKingdomId())) {
            return true;
        }
        if (performer.faithful) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept that.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        final Wounds tWounds = target.getBody().getWounds();
        if (tWounds != null && tWounds.getWounds().length > 0) {
            return this.precondition(castSkill, performer, tWounds.getWounds()[0]);
        }
        performer.getCommunicator().sendNormalServerMessage(target.getName() + " has no wounds to heal.");
        return false;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        final Wounds tWounds = target.getBody().getWounds();
        if (tWounds != null && tWounds.getWounds().length > 0) {
            Wound highestWound = tWounds.getWounds()[0];
            float highestSeverity = highestWound.getSeverity();
            for (final Wound w : tWounds.getWounds()) {
                if (w.getSeverity() > highestSeverity) {
                    highestWound = w;
                    highestSeverity = w.getSeverity();
                }
            }
            this.doEffect(castSkill, power, performer, highestWound);
        }
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Wound target) {
        boolean doeff = true;
        final Creature tCret = target.getCreature();
        if (tCret.isReborn()) {
            doeff = false;
            performer.getCommunicator().sendNormalServerMessage("The wound grows.", (byte)3);
            target.modifySeverity(2000);
        }
        else if (tCret.isPlayer() && tCret != performer && performer.getDeity() != null && !tCret.isFriendlyKingdom(performer.getKingdomId())) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " becomes very upset at the way you abuse " + performer.getDeity().getHisHerItsString() + " powers!", (byte)3);
            try {
                performer.setFaith(performer.getFaith() / 2.0f);
            }
            catch (Exception ex) {
                CureMedium.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        if (doeff) {
            final double resistance = SpellResist.getSpellResistance(tCret, this.getNumber());
            double toHeal = 19650.0;
            toHeal += 19650.0 * (power / 300.0);
            if (performer.getCultist() != null && performer.getCultist().healsFaster()) {
                toHeal *= 2.0;
            }
            toHeal *= resistance;
            final VolaTile t = Zones.getTileOrNull(target.getCreature().getTileX(), target.getCreature().getTileY(), target.getCreature().isOnSurface());
            if (t != null) {
                t.sendAttachCreatureEffect(target.getCreature(), (byte)11, (byte)0, (byte)0, (byte)0, (byte)0);
            }
            if (target.getSeverity() <= toHeal) {
                SpellResist.addSpellResistance(tCret, this.getNumber(), target.getSeverity());
                target.heal();
                performer.getCommunicator().sendNormalServerMessage("You manage to heal the wound.", (byte)2);
                if (performer != tCret) {
                    tCret.getCommunicator().sendNormalServerMessage(performer.getName() + " completely heals your wound.", (byte)2);
                }
            }
            else {
                SpellResist.addSpellResistance(tCret, this.getNumber(), toHeal);
                target.modifySeverity((int)(-toHeal));
                performer.getCommunicator().sendNormalServerMessage("You cure the wound a bit.", (byte)2);
                if (performer != tCret) {
                    tCret.getCommunicator().sendNormalServerMessage(performer.getName() + " partially heals your wound.", (byte)2);
                }
            }
        }
    }
    
    static {
        logger = Logger.getLogger(CureMedium.class.getName());
    }
}
