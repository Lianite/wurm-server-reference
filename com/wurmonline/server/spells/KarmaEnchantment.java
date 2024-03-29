// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.SpellEffects;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;

public class KarmaEnchantment extends KarmaSpell
{
    float durationModifier;
    
    public KarmaEnchantment(final String aName, final int aNum, final int aCastingTime, final int aCost, final int aDifficulty, final int aLevel, final long aCooldown) {
        super(aName, aNum, aCastingTime, aCost, aDifficulty, aLevel, aCooldown);
        this.durationModifier = 20.0f;
        this.targetCreature = true;
        this.targetTile = true;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if (target == null) {
            return false;
        }
        if (target.isReborn()) {
            performer.getCommunicator().sendNormalServerMessage(target.getName() + " doesn't seem affected.", (byte)3);
            return true;
        }
        if (target.equals(performer)) {
            return true;
        }
        if (!this.offensive && target.getKingdomId() != 0 && !performer.isFriendlyKingdom(target.getKingdomId())) {
            performer.getCommunicator().sendNormalServerMessage("Nothing happens as you try to cast this on an enemy.", (byte)3);
            return false;
        }
        if (performer.getDeity() == null) {
            return true;
        }
        if (target.getDeity() == null) {
            return true;
        }
        if (!this.offensive && !performer.getDeity().accepts(target.getDeity().getAlignment())) {
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never help the infidel " + target.getName() + ".", (byte)3);
            return false;
        }
        return true;
    }
    
    public static final void doImmediateEffect(final int number, final int seconds, final double power, final Creature target) {
        final Spell sp = Spells.getSpell(number);
        SpellEffects effs = target.getSpellEffects();
        if (effs == null) {
            effs = target.createSpellEffects();
        }
        SpellEffect eff = effs.getSpellEffect(sp.getEnchantment());
        if (eff == null) {
            eff = new SpellEffect(target.getWurmId(), sp.getEnchantment(), (sp.getEnchantment() == 68) ? 100.0f : ((float)power), Math.max(1, seconds), (byte)9, (byte)(sp.isOffensive() ? 1 : 0), true);
            effs.addSpellEffect(eff);
        }
        else if (eff.getPower() < power) {
            eff.setPower((sp.getEnchantment() == 68) ? 100.0f : ((float)power));
            eff.setTimeleft(Math.max(eff.timeleft, Math.max(1, seconds)));
            target.sendUpdateSpellEffect(eff);
        }
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        SpellEffects effs = target.getSpellEffects();
        if (effs == null) {
            effs = target.createSpellEffects();
        }
        SpellEffect eff = effs.getSpellEffect(this.enchantment);
        if (eff == null) {
            if (target != performer) {
                performer.getCommunicator().sendNormalServerMessage(target.getName() + " now has " + this.effectdesc, (byte)2);
            }
            target.getCommunicator().sendNormalServerMessage("You now have " + this.effectdesc, (byte)2);
            eff = new SpellEffect(target.getWurmId(), this.enchantment, (this.enchantment == 68) ? 100.0f : ((float)power), (int)(Math.max(1.0, power) * (this.durationModifier + performer.getNumLinks())), (byte)9, (byte)(this.isOffensive() ? 1 : 0), true);
            effs.addSpellEffect(eff);
            Server.getInstance().broadCastAction(performer.getName() + " looks pleased.", performer, 5);
        }
        else if (eff.getPower() > power) {
            performer.getCommunicator().sendNormalServerMessage("You frown as you fail to improve the power.", (byte)3);
            Server.getInstance().broadCastAction(performer.getName() + " frowns.", performer, 5);
        }
        else {
            if (target != performer) {
                performer.getCommunicator().sendNormalServerMessage("You succeed in improving the power of the " + this.name + ".", (byte)2);
            }
            target.getCommunicator().sendNormalServerMessage("You will now receive improved " + this.effectdesc, (byte)2);
            eff.setPower((float)power);
            eff.setTimeleft(Math.max(eff.timeleft, (int)(Math.max(1.0, (this.enchantment == 68) ? 100.0 : power) * (this.durationModifier + performer.getNumLinks()))));
            target.sendUpdateSpellEffect(eff);
            Server.getInstance().broadCastAction(performer.getName() + " looks pleased.", performer, 5);
        }
    }
}
