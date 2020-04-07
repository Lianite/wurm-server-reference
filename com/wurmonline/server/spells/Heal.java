// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.zones.Zones;
import java.util.logging.Level;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;

public final class Heal extends ReligiousSpell
{
    private static Logger logger;
    public static final int RANGE = 12;
    
    Heal() {
        super("Heal", 249, 30, 40, 30, 40, 10000L);
        this.targetCreature = true;
        this.healing = true;
        this.description = "heals an extreme amount of damage";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if (target.getBody() == null || target.getBody().getWounds() == null) {
            performer.getCommunicator().sendNormalServerMessage(target.getNameWithGenus() + " has no wounds to heal.", (byte)3);
            return false;
        }
        if (target.isReborn()) {
            return true;
        }
        if (target.equals(performer)) {
            return true;
        }
        if (!target.isPlayer() || performer.getDeity() == null) {
            return true;
        }
        if (target.isFriendlyKingdom(performer.getKingdomId())) {
            return true;
        }
        if (performer.faithful) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never accept that.", (byte)3);
            return false;
        }
        return true;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        boolean doeff = true;
        if (target.isReborn()) {
            doeff = false;
            performer.getCommunicator().sendNormalServerMessage("You slay " + target.getNameWithGenus() + ".", (byte)4);
            Server.getInstance().broadCastAction(performer.getName() + " slays " + target.getNameWithGenus() + "!", performer, 5);
            target.addAttacker(performer);
            target.die(false, "Heal cast on Reborn");
        }
        else if (!target.equals(performer) && target.isPlayer() && performer.getDeity() != null && !target.isFriendlyKingdom(performer.getKingdomId())) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " becomes very upset at the way you abuse " + performer.getDeity().getHisHerItsString() + " powers!", (byte)3);
            try {
                performer.setFaith(performer.getFaith() / 2.0f);
            }
            catch (Exception ex) {
                Heal.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        if (doeff) {
            final Wounds tWounds = target.getBody().getWounds();
            if (tWounds == null) {
                performer.getCommunicator().sendNormalServerMessage(target.getName() + " has no wounds to heal.", (byte)3);
                return;
            }
            final double resistance = SpellResist.getSpellResistance(target, this.getNumber());
            double healingPool = Math.max(20.0, power) / 100.0 * 65535.0 * 2.0;
            if (performer.getCultist() != null && performer.getCultist().healsFaster()) {
                healingPool *= 2.0;
            }
            healingPool *= resistance;
            for (final Wound w : tWounds.getWounds()) {
                if (w.getSeverity() <= healingPool) {
                    healingPool -= w.getSeverity();
                    SpellResist.addSpellResistance(target, this.getNumber(), w.getSeverity());
                    w.heal();
                }
            }
            if (tWounds.getWounds().length > 0 && healingPool > 0.0) {
                SpellResist.addSpellResistance(target, this.getNumber(), healingPool);
                tWounds.getWounds()[Server.rand.nextInt(tWounds.getWounds().length)].modifySeverity((int)(-healingPool));
            }
            if (tWounds.getWounds().length > 0) {
                performer.getCommunicator().sendNormalServerMessage("You heal some of " + target.getNameWithGenus() + "'s wounds.", (byte)4);
                target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " heals some of your wounds.", (byte)4);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You fully heal " + target.getNameWithGenus() + ".", (byte)4);
                target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " heals your wounds.", (byte)4);
            }
            final VolaTile t = Zones.getTileOrNull(target.getTileX(), target.getTileY(), target.isOnSurface());
            if (t != null) {
                t.sendAttachCreatureEffect(target, (byte)11, (byte)0, (byte)0, (byte)0, (byte)0);
            }
        }
    }
    
    static {
        Heal.logger = Logger.getLogger(Heal.class.getName());
    }
}
