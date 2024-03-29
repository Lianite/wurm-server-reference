// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.utils.StringUtil;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.Skill;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.AttitudeConstants;

public final class Bless extends ReligiousSpell implements AttitudeConstants
{
    private static final Logger logger;
    public static final int RANGE = 4;
    
    Bless() {
        super("Bless", 245, 10, 10, 10, 8, 0L);
        this.targetCreature = true;
        this.targetItem = true;
        this.description = "adds a holy aura of purity";
        this.type = 0;
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        if (performer.getDeity() == null) {
            return false;
        }
        if (target.isPlayer()) {
            if (target.getAttitude(performer) != 2) {
                return true;
            }
            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " would never help the infidel " + target.getName() + ".", (byte)3);
            return false;
        }
        else {
            final boolean isLibila = performer.getDeity().isLibila();
            if (isLibila) {
                if (target.hasTrait(22)) {
                    performer.getCommunicator().sendNormalServerMessage(target.getNameWithGenus() + " is already corrupt.", (byte)3);
                    return false;
                }
                return true;
            }
            else {
                if (target.hasTrait(22)) {
                    return true;
                }
                performer.getCommunicator().sendNormalServerMessage(target.getNameWithGenus() + " is not corrupt.", (byte)3);
                return false;
            }
        }
    }
    
    @Override
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        if (performer.getDeity() != null) {
            if (target.getBless() == null) {
                if (target.isUnfinished()) {
                    performer.getCommunicator().sendNormalServerMessage("The spell will not work on unfinished items.", (byte)3);
                    return false;
                }
                return true;
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already blessed to " + target.getBless().getName() + ".", (byte)3);
            }
        }
        return false;
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
        target.getCommunicator().sendNormalServerMessage(performer.getName() + " blesses you.");
        performer.getCommunicator().sendNormalServerMessage("You bless " + target.getNameWithGenus() + ".");
        if (performer.getDeity() != null) {
            if (target.isPlayer()) {
                if (performer.getDeity().accepts(target.getAlignment())) {
                    try {
                        if (target.getFavor() < performer.getFavor()) {
                            if (target.getFavor() < target.getFaith()) {
                                if (performer.getDeity().isHateGod()) {
                                    performer.maybeModifyAlignment(-1.0f);
                                }
                                else {
                                    performer.maybeModifyAlignment(1.0f);
                                }
                            }
                            target.setFavor((float)(target.getFavor() + this.cost * 100 / (performer.getFaith() * 30.0f) * castSkill.getKnowledge(performer.zoneBonus) / 100.0));
                            target.getCommunicator().sendNormalServerMessage("The light of " + performer.getDeity().getName() + " shines upon you.");
                        }
                    }
                    catch (IOException iox) {
                        Bless.logger.log(Level.WARNING, performer.getName(), iox);
                    }
                }
                else {
                    target.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " does not seem pleased with " + target.getNameWithGenus() + ".");
                    performer.getCommunicator().sendNormalServerMessage(performer.getDeity().getName() + " does not seem pleased with " + target.getNameWithGenus() + ".");
                }
            }
            else {
                this.blessCreature(performer, target);
            }
        }
    }
    
    void blessCreature(final Creature performer, final Creature target) {
        final boolean isLibila = performer.getDeity().isLibila();
        final boolean isCorrupt = target.hasTrait(22);
        if (isLibila && !isCorrupt) {
            target.getStatus().setTraitBit(22, true);
            if (!target.hasTrait(63)) {
                performer.getCommunicator().sendNormalServerMessage("The dark energies of Libila flows through " + target.getNameWithGenus() + " corrupting " + target.getHimHerItString() + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("The dark energies of Libila flows through " + target.getNameWithGenus() + " corrupting " + target.getHimHerItString() + ".");
            }
        }
        else if (!isLibila && isCorrupt) {
            target.getStatus().setTraitBit(22, false);
            final String deityName = performer.getDeity().getName();
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("The cleansing power of %s courses through %s purifying %s.", deityName, target.getNameWithGenus(), target.getHimHerItString()));
        }
    }
    
    @Override
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
        target.bless(performer.getDeity().getNumber());
        if (target.isDomainItem()) {
            target.setName(target.getName() + " of " + performer.getDeity().getName());
            performer.getCommunicator().sendNormalServerMessage("You may now pray at the blessed altar.");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("You bless the " + target.getName() + " with the power of " + performer.getDeity().getName() + ".");
            if (target.getTemplateId() == 654) {
                performer.getCommunicator().sendUpdateInventoryItem(target);
            }
        }
    }
    
    static {
        logger = Logger.getLogger(Bless.class.getName());
    }
}
