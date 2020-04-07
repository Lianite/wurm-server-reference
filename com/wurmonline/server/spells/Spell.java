// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.util.Random;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.Items;
import com.wurmonline.server.Features;
import com.wurmonline.server.creatures.SpellEffects;
import javax.annotation.Nullable;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.Constants;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.combat.BattleEvent;
import com.wurmonline.server.combat.Battles;
import com.wurmonline.server.zones.Zones;
import java.util.List;
import com.wurmonline.server.MessageServer;
import com.wurmonline.server.utils.CreatureLineSegment;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.ArrayList;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.items.RuneUtilities;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.Enchants;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.server.MiscConstants;

public abstract class Spell implements SpellTypes, MiscConstants, SoundNames, Enchants, TimeConstants, Comparable<Spell>
{
    protected static final Logger logger;
    public static final byte TYPE_CHAPLAIN = 1;
    public static final byte TYPE_MISSIONARY = 2;
    public static final byte TYPE_BOTH = 0;
    boolean[][] area;
    int[][] offsets;
    public final int number;
    public final String name;
    private final int castingTime;
    public boolean religious;
    public boolean offensive;
    public boolean healing;
    public boolean singleItemEnchant;
    static final int enchantDifficulty = 60;
    final int cost;
    protected boolean hasDynamicCost;
    protected final int difficulty;
    public final int level;
    protected boolean targetCreature;
    protected boolean targetItem;
    protected boolean targetWeapon;
    protected boolean targetArmour;
    protected boolean targetJewelry;
    protected boolean targetPendulum;
    protected boolean targetWound;
    protected boolean targetTile;
    protected boolean targetTileBorder;
    protected boolean karmaSpell;
    private long cooldown;
    boolean dominate;
    public boolean isRitual;
    byte enchantment;
    public String effectdesc;
    String description;
    protected byte type;
    public static final int TIME_ENCHANT_CAST = 30;
    public static final long TIME_ENCHANT = 300000L;
    public static final long TIME_CONTINUUM = 240000L;
    public static final long TIME_CREATUREBUFF = 180000L;
    public static final long TIME_AOE = 120000L;
    public static final long TIME_UTILITY = 1800000L;
    public static final long TIME_UTILITY_HALF = 900000L;
    public static final long TIME_UTILITY_DOUBLE = 3600000L;
    public static final long TIME_COMBAT = 0L;
    public static final long TIME_COMBAT_SMALLDELAY = 10000L;
    public static final long TIME_COMBAT_NORMALDELAY = 30000L;
    public static final long TIME_COMBAT_LONGDELAY = 60000L;
    public static final int Spirit_Fire = 1;
    public static final int Spirit_Water = 2;
    public static final int Spirit_Earth = 3;
    public static final int Spirit_Air = 4;
    public static final double minOffensivePower = 50.0;
    
    Spell(final String _name, final int num, final int _castingTime, final int _cost, final int _difficulty, final int _level, final long _cooldown) {
        this.religious = false;
        this.offensive = false;
        this.healing = false;
        this.singleItemEnchant = false;
        this.hasDynamicCost = false;
        this.targetCreature = false;
        this.targetItem = false;
        this.targetWeapon = false;
        this.targetArmour = false;
        this.targetJewelry = false;
        this.targetPendulum = false;
        this.targetWound = false;
        this.targetTile = false;
        this.targetTileBorder = false;
        this.karmaSpell = false;
        this.cooldown = 60000L;
        this.dominate = false;
        this.isRitual = false;
        this.enchantment = 0;
        this.effectdesc = "";
        this.description = "N/A";
        this.type = 0;
        this.name = _name;
        this.number = num;
        this.castingTime = _castingTime;
        this.cost = _cost;
        this.difficulty = _difficulty;
        this.level = _level;
        this.cooldown = _cooldown;
    }
    
    Spell(final String _name, final int num, final int _castingTime, final int _cost, final int _difficulty, final int _level, final String aEffectDescription, final byte aEnchantment, final boolean aDominate, final boolean aReligious, final boolean aOffensive, final boolean aTargetCreature, final boolean aTargetItem, final boolean aTargetWound, final boolean aTargetTile) {
        this.religious = false;
        this.offensive = false;
        this.healing = false;
        this.singleItemEnchant = false;
        this.hasDynamicCost = false;
        this.targetCreature = false;
        this.targetItem = false;
        this.targetWeapon = false;
        this.targetArmour = false;
        this.targetJewelry = false;
        this.targetPendulum = false;
        this.targetWound = false;
        this.targetTile = false;
        this.targetTileBorder = false;
        this.karmaSpell = false;
        this.cooldown = 60000L;
        this.dominate = false;
        this.isRitual = false;
        this.enchantment = 0;
        this.effectdesc = "";
        this.description = "N/A";
        this.type = 0;
        this.name = _name;
        this.number = num;
        this.castingTime = _castingTime;
        this.cost = _cost;
        this.difficulty = _difficulty;
        this.level = _level;
        this.effectdesc = aEffectDescription;
        this.enchantment = aEnchantment;
        this.dominate = aDominate;
        this.religious = aReligious;
        this.offensive = aOffensive;
        this.targetCreature = aTargetCreature;
        this.targetItem = aTargetItem;
        this.targetTile = aTargetTile;
        this.targetWound = aTargetWound;
    }
    
    public Spell(final String aName, final int aNum, final int aCastingTime, final int aCost, final int aDifficulty, final int aLevel, final long aCooldown, final boolean aReligious) {
        this.religious = false;
        this.offensive = false;
        this.healing = false;
        this.singleItemEnchant = false;
        this.hasDynamicCost = false;
        this.targetCreature = false;
        this.targetItem = false;
        this.targetWeapon = false;
        this.targetArmour = false;
        this.targetJewelry = false;
        this.targetPendulum = false;
        this.targetWound = false;
        this.targetTile = false;
        this.targetTileBorder = false;
        this.karmaSpell = false;
        this.cooldown = 60000L;
        this.dominate = false;
        this.isRitual = false;
        this.enchantment = 0;
        this.effectdesc = "";
        this.description = "N/A";
        this.type = 0;
        this.name = aName;
        this.number = aNum;
        this.castingTime = aCastingTime;
        this.cost = aCost;
        this.difficulty = aDifficulty;
        this.level = aLevel;
        this.cooldown = aCooldown;
        this.religious = aReligious;
    }
    
    @Override
    public int compareTo(final Spell otherSpell) {
        return this.getName().compareTo(otherSpell.getName());
    }
    
    public static final boolean mayBeEnchanted(final Item target) {
        return target.isOverrideNonEnchantable() || (!target.isBodyPart() && !target.isNewbieItem() && !target.isNoTake() && target.getTemplateId() != 179 && target.getTemplateId() != 386 && !target.isTemporary() && target.getTemplateId() != 272 && (!target.isLockable() || target.getLockId() == -10L) && !target.isIndestructible() && !target.isHugeAltar() && !target.isDomainItem() && !target.isKingdomMarker() && !target.isTraded() && !target.isBanked() && !target.isArtifact() && !target.isEgg() && !target.isChallengeNewbieItem() && !target.isRiftLoot() && !target.isRiftAltar() && target.getTemplateId() != 1307);
    }
    
    public final long getCooldown() {
        return this.cooldown;
    }
    
    public final void setType(final byte newType) {
        this.type = newType;
    }
    
    protected static double trimPower(final Creature performer, double power) {
        if (Servers.localServer.HOMESERVER && performer.isChampion()) {
            power = Math.min(power, 50.0);
        }
        if (performer.hasFlag(82)) {
            power += 5.0;
        }
        return power;
    }
    
    public final boolean stillCooldown(final Creature performer) {
        if (this.cooldown > 0L) {
            final Cooldowns cd = Cooldowns.getCooldownsFor(performer.getWurmId(), false);
            if (cd != null) {
                final long avail = cd.isAvaibleAt(this.number);
                if (avail > System.currentTimeMillis() && performer.getPower() < 3) {
                    performer.getCommunicator().sendNormalServerMessage("You need to wait " + Server.getTimeFor(avail - System.currentTimeMillis()) + " until you can cast " + this.name + " again.");
                    return true;
                }
            }
            for (final Creature c : performer.getLinks()) {
                if (this.stillCooldown(c)) {
                    final Cooldowns cd2 = Cooldowns.getCooldownsFor(performer.getWurmId(), false);
                    if (cd2 != null) {
                        final long avail2 = cd2.isAvaibleAt(this.number);
                        if (avail2 > System.currentTimeMillis() && c.getPower() < 3) {
                            performer.getCommunicator().sendNormalServerMessage(c.getName() + " needs to wait " + Server.getTimeFor(avail2 - System.currentTimeMillis()) + " until " + c.getHeSheItString() + " can cast " + this.name + " again.");
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public final void touchCooldown(final Creature performer) {
        if (this.cooldown > 0L) {
            final Cooldowns cd = Cooldowns.getCooldownsFor(performer.getWurmId(), true);
            cd.addCooldown(this.number, System.currentTimeMillis() + this.cooldown, false);
        }
    }
    
    public static double modifyDamage(final Creature target, double damage) {
        if (!target.isPlayer()) {
            final double armourMult = target.getArmourMod() * 2.0f;
            final double bodyStrengthWeight = target.getStrengthSkill() * 0.25;
            final double soulStrengthWeight = target.getSoulStrengthVal() * 0.75;
            final double strengthMult = (100.0 - (bodyStrengthWeight + soulStrengthWeight)) * 0.02;
            final double damageMult = armourMult * strengthMult;
            final double clampedMult = damageMult / (1.0 + damageMult / 3.0);
            damage *= clampedMult;
        }
        return damage;
    }
    
    protected boolean checkFavorRequirements(final Creature performer, final float baseCost) {
        if (this.isReligious()) {
            if (baseCost < 15.0f && performer.getFavor() < baseCost) {
                return true;
            }
            if (baseCost < 200.0f && performer.getFavor() < baseCost * 0.33f && performer.getFavor() < 15.0f) {
                return true;
            }
        }
        return false;
    }
    
    protected Skill getCastingSkill(final Creature performer) {
        if (this.religious) {
            return performer.getChannelingSkill();
        }
        return performer.getMindLogical();
    }
    
    protected boolean canCastSpell(final Creature performer) {
        if (this.isReligious()) {
            return performer.isPriest() || performer.getPower() > 0;
        }
        return performer.knowsKarmaSpell(this.getNumber());
    }
    
    private boolean isCastValid(final Creature performer, final boolean validTarget, final String targetName, final int tileX, final int tileY, final boolean onSurface) {
        if (!this.canCastSpell(performer)) {
            performer.getCommunicator().sendNormalServerMessage("You cannot cast that spell.");
            return false;
        }
        if (this.isReligiousSpell() && !validTarget) {
            performer.getCommunicator().sendNormalServerMessage("You cannot cast " + this.getName() + " on " + targetName + ".");
            return false;
        }
        if (this.stillCooldown(performer)) {
            return false;
        }
        if (performer.attackingIntoIllegalDuellingRing(tileX, tileY, onSurface)) {
            performer.getCommunicator().sendNormalServerMessage("The duelling ring is holy ground and casting is restricted across the border.");
            return true;
        }
        return true;
    }
    
    public final boolean isValidItemType(final Creature performer, final Item target) {
        if (this.isTargetItem()) {
            return true;
        }
        if (this.isTargetArmour() && target.isArmour()) {
            return true;
        }
        if (this.isTargetWeapon() && (target.isWeapon() || target.isWeaponBow() || target.isBowUnstringed() || target.isArrow())) {
            return true;
        }
        if (this.isTargetJewelry() && target.isEnchantableJewelry()) {
            return true;
        }
        if (this.isTargetPendulum() && target.getTemplateId() == 233) {
            return true;
        }
        EnchantUtil.sendInvalidTargetMessage(performer, this);
        return false;
    }
    
    public final boolean run(final Creature performer, final Item target, final float counter) {
        boolean done = false;
        if (!this.isCastValid(performer, target.isBodyPart() ? this.isTargetCreature() : this.isTargetAnyItem(), target.getNameWithGenus(), target.getTileX(), target.getTileY(), target.isOnSurface())) {
            return true;
        }
        if (!this.isValidItemType(performer, target)) {
            return true;
        }
        if (target.getTemplateId() == 669) {
            performer.getCommunicator().sendNormalServerMessage("You cannot cast " + this.getName() + " on the bulk item.");
            return true;
        }
        if (target.isMagicContainer()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot cast " + this.getName() + " on the " + target.getName() + ".");
            return true;
        }
        final Skill castSkill = this.getCastingSkill(performer);
        if (!this.precondition(castSkill, performer, target)) {
            return true;
        }
        float baseCost = this.getCost(target);
        if (performer.getPower() >= 5 && Servers.isThisATestServer()) {
            baseCost = 1.0f;
        }
        float needed = baseCost;
        if (this.religious) {
            if (performer.isRoyalPriest()) {
                needed *= 0.5f;
            }
            if (performer.getFavorLinked() < needed) {
                performer.getCommunicator().sendNormalServerMessage("You need more favor with your god to cast that spell.");
                return true;
            }
        }
        else if (performer.getKarma() < needed) {
            performer.getCommunicator().sendNormalServerMessage("You need more karma to use that ability.");
            return true;
        }
        if (counter == 1.0f && this.checkFavorRequirements(performer, baseCost)) {
            performer.getCommunicator().sendNormalServerMessage("You need more favor from your god to cast that spell.");
            return true;
        }
        double power = 0.0;
        if (counter == 1.0f && this.getCastingTime(performer) > 1) {
            if (this.isItemEnchantment() && performer.isChampion()) {
                if (((Player)performer).getChampionPoints() <= 0) {
                    performer.getCommunicator().sendNormalServerMessage("You will need to spend one Champion point in order to enchant items.");
                    return true;
                }
                performer.getCommunicator().sendAlertServerMessage("You will spend one champion point if you successfully enchant the item!");
            }
            performer.setStealth(false);
            performer.getCommunicator().sendNormalServerMessage("You start to cast '" + this.name + "' on " + target.getNameWithGenus() + ".");
            Server.getInstance().broadCastAction(performer.getNameWithGenus() + " starts to cast '" + this.name + "' on " + target.getNameWithGenus() + ".", performer, 5, this.shouldMessageCombat());
            performer.sendActionControl(Actions.actionEntrys[122].getVerbString(), true, this.getCastingTime(performer) * 10);
        }
        int speedMod = 0;
        if (!this.religious) {
            final Skill sp = performer.getMindSpeed();
            if (sp != null) {
                speedMod = (int)(sp.getKnowledge(0.0) / 25.0);
            }
        }
        if (counter >= this.getCastingTime(performer) - speedMod || (counter > 2.0f && performer.getPower() == 5)) {
            done = true;
            boolean limitFail = false;
            if (this.isOffensive() && performer.getArmourLimitingFactor() < 0.0f && Server.rand.nextFloat() < Math.abs(performer.getArmourLimitingFactor())) {
                limitFail = true;
            }
            float bonus = 0.0f;
            if (!this.religious) {
                final Skill sp2 = performer.getMindSpeed();
                if (sp2 != null) {
                    sp2.skillCheck(this.getDifficulty(true), performer.zoneBonus, false, counter);
                }
            }
            else {
                bonus = Math.abs(performer.getAlignment()) - 49.0f;
            }
            int rdDiff = 0;
            if (performer.mustChangeTerritory()) {
                bonus -= 50.0f;
                rdDiff = 20;
            }
            else if (target.isCrystal() && !target.isGem()) {
                bonus += 100.0f;
            }
            if (performer.getCitizenVillage() != null) {
                bonus += performer.getCitizenVillage().getFaithCreateBonus();
            }
            final boolean dryRun = false;
            float modifier = 1.0f;
            if (target.getSpellEffects() != null) {
                modifier = target.getSpellEffects().getRuneEffect(RuneUtilities.ModifierEffect.ENCH_ENCHANTABILITY);
            }
            if (bonus > 0.0f) {
                bonus *= 1.0f + performer.getArmourLimitingFactor();
            }
            power = trimPower(performer, castSkill.skillCheck(this.getDifficulty(true) + rdDiff + performer.getNumLinks() * 3, (performer.zoneBonus + bonus) * modifier, false, counter));
            if (limitFail) {
                power = -30.0f + Server.rand.nextFloat() * 29.0f;
            }
            if (power >= 0.0) {
                this.touchCooldown(performer);
                if (power >= 95.0) {
                    performer.achievement(629);
                }
                performer.getCommunicator().sendNormalServerMessage("You cast '" + this.name + "' on " + target.getNameWithGenus() + ".");
                Server.getInstance().broadCastAction(performer.getNameWithGenus() + " casts '" + this.name + "' on " + target.getNameWithGenus() + ".", performer, 5, this.shouldMessageCombat());
                Label_1063: {
                    if (this.religious) {
                        if (!this.postcondition(castSkill, performer, target, power)) {
                            try {
                                performer.depleteFavor(baseCost / 20.0f, this.isOffensive());
                                break Label_1063;
                            }
                            catch (IOException iox) {
                                Spell.logger.log(Level.WARNING, performer.getName(), iox);
                                performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                                return true;
                            }
                        }
                        try {
                            performer.depleteFavor(needed, this.isOffensive());
                            break Label_1063;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.modifyKarma((int)(-needed));
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Success Cost:" + needed + ", Power:" + power + ", SpeedMod:" + speedMod + ", Bonus:" + bonus);
                }
                this.doEffect(castSkill, power, performer, target);
                if (this.isItemEnchantment()) {
                    performer.achievement(606);
                    if (performer.isChampion()) {
                        performer.modifyChampionPoints(-1);
                    }
                }
            }
            else {
                Label_1465: {
                    if (this.religious) {
                        if (performer.mustChangeTerritory() && performer.isPlayer() && Server.rand.nextInt(3) == 0) {
                            performer.getCommunicator().sendAlertServerMessage("You sense a lack of energy. Rumours have it that " + performer.getDeity().getName() + " wants " + performer.getDeity().getHisHerItsString() + " champions to move between kingdoms and seek out the enemy.");
                        }
                        performer.getCommunicator().sendNormalServerMessage("You fail to channel the '" + this.name + "'.");
                        Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails to channel the '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
                        try {
                            performer.depleteFavor(baseCost / 5.0f, this.isOffensive());
                            break Label_1465;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("The '" + this.name + "' fizzles!");
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fizzles " + performer.getHisHerItsString() + " '" + this.name + "'!", performer, 5, this.shouldMessageCombat());
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Fail Cost:" + needed + ", Power:" + power);
                }
                this.doNegativeEffect(castSkill, power, performer, target);
            }
        }
        return done;
    }
    
    public final boolean run(final Creature performer, final Creature target, final float counter) {
        boolean done = false;
        if (target.isDead()) {
            return true;
        }
        if (!this.isCastValid(performer, this.targetCreature, target.getNameWithGenus(), target.getTileX(), target.getTileY(), target.isOnSurface())) {
            return true;
        }
        final Skill castSkill = this.getCastingSkill(performer);
        if (!this.precondition(castSkill, performer, target)) {
            return true;
        }
        float baseCost = this.getCost(target);
        if (performer.getPower() >= 5 && Servers.isThisATestServer()) {
            baseCost = 1.0f;
        }
        float needed = baseCost;
        if (this.religious) {
            if (performer.isRoyalPriest()) {
                needed *= 0.5f;
            }
            if (performer.getFavorLinked() < needed) {
                performer.getCommunicator().sendNormalServerMessage("You need more favor with your god to cast that spell.");
                return true;
            }
        }
        else if (performer.getKarma() < needed) {
            performer.getCommunicator().sendNormalServerMessage("You need more karma to use that ability.");
            return true;
        }
        if (counter == 1.0f) {
            if (this.checkFavorRequirements(performer, baseCost)) {
                performer.getCommunicator().sendNormalServerMessage("You need more favor from your god to cast that spell.");
                return true;
            }
            if (this.offensive) {
                if ((performer.opponent != null || target.isAggHuman()) && performer.opponent == null) {
                    performer.setOpponent(target);
                }
                if (target.opponent == null) {
                    target.setOpponent(performer);
                    target.setTarget(performer.getWurmId(), false);
                    target.getCommunicator().sendNormalServerMessage(performer.getNameWithGenus() + " is attacking you with a spell!");
                }
                target.addAttacker(performer);
            }
        }
        double power = 0.0;
        int speedMod = 0;
        if (counter == 1.0f && this.getCastingTime(performer) > 1) {
            performer.setStealth(false);
            if (performer == target) {
                performer.getCommunicator().sendNormalServerMessage("You start to cast '" + this.name + "' on yourself.");
                Server.getInstance().broadCastAction(performer.getNameWithGenus() + " starts to cast '" + this.name + "' on " + target.getHimHerItString() + "self.", performer, 5, this.shouldMessageCombat());
            }
            else {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(performer));
                segments.add(new MulticolorLineSegment(" starts to cast " + this.name + " on ", (byte)0));
                segments.add(new CreatureLineSegment(target));
                segments.add(new MulticolorLineSegment(".", (byte)0));
                MessageServer.broadcastColoredAction(segments, performer, 5, this.shouldMessageCombat());
                if (this.offensive || this.number == 450) {
                    target.getCommunicator().sendColoredMessageCombat(segments, (byte)2);
                }
                segments.get(1).setText(" start to cast " + this.name + " on ");
                if (this.shouldMessageCombat()) {
                    performer.getCommunicator().sendColoredMessageCombat(segments, (byte)2);
                }
                else {
                    performer.getCommunicator().sendColoredMessageEvent(segments);
                }
            }
            performer.sendActionControl(Actions.actionEntrys[122].getVerbString(), true, this.getCastingTime(performer) * 10);
        }
        if (!this.isReligious()) {
            final Skill sp = performer.getMindSpeed();
            if (sp != null) {
                speedMod = (int)(sp.getKnowledge(0.0) / 25.0);
            }
        }
        if (counter >= this.getCastingTime(performer) - speedMod || (counter > 2.0f && performer.getPower() == 5)) {
            done = true;
            double resist = 0.0;
            double attbonus = 0.0;
            if (!Zones.interruptedRange(performer, target)) {
                boolean limitFail = false;
                if (this.isOffensive() && performer.getArmourLimitingFactor() < 0.0f && Server.rand.nextFloat() < Math.abs(performer.getArmourLimitingFactor())) {
                    limitFail = true;
                }
                target.setStealth(false);
                if (!this.isReligious()) {
                    final Skill sp2 = performer.getMindSpeed();
                    if (sp2 != null) {
                        sp2.skillCheck(this.difficulty, performer.zoneBonus, false, counter);
                    }
                }
                if (this.isOffensive()) {
                    target.addAttacker(performer);
                    if (performer.isPlayer() && target.isPlayer()) {
                        final Battle battle = Battles.getBattleFor(performer, target);
                        if (battle != null) {
                            battle.addEvent(new BattleEvent((short)114, performer.getName(), target.getName(), performer.getName() + " casts " + this.getName() + " at " + target.getName() + "."));
                        }
                    }
                    int defSkill = 105;
                    int attSkill = 105;
                    if (this.dominate) {
                        try {
                            float extraDiff = 0.0f;
                            if (target.isUnique()) {
                                extraDiff = target.getBaseCombatRating();
                            }
                            attbonus = performer.getSkills().getSkill(attSkill).skillCheck(this.difficulty, performer.zoneBonus, false, counter);
                            if (attbonus > 0.0) {
                                attbonus *= 1.0f + performer.getArmourLimitingFactor();
                            }
                            power = trimPower(performer, castSkill.skillCheck((1.0f + ItemBonus.getSpellResistBonus(target)) * (target.getSkills().getSkill(defSkill).getKnowledge(0.0) + target.getStatus().getBattleRatingTypeModifier() + performer.getNumLinks() * 3 + extraDiff), performer.zoneBonus + attbonus, false, counter));
                        }
                        catch (NoSuchSkillException nss) {
                            performer.getCommunicator().sendNormalServerMessage(target.getNameWithGenus() + " seems impossible to dominate.");
                            Spell.logger.log(Level.WARNING, nss.getMessage(), nss);
                        }
                    }
                    else {
                        float abon = 0.0f;
                        float defbon = 0.0f;
                        if (performer.getEnemyPresense() > 1200 && target.isPlayer()) {
                            abon = 20.0f;
                        }
                        if (target.getEnemyPresense() > 1200 && performer.isPlayer()) {
                            defbon = 20.0f;
                        }
                        if (!this.religious) {
                            attSkill = 101;
                            defSkill = 101;
                        }
                        try {
                            resist = target.getSkills().getSkill(defSkill).skillCheck(this.difficulty, defbon, false, counter);
                        }
                        catch (NoSuchSkillException nss2) {
                            Spell.logger.log(Level.WARNING, target.getName() + " learning defskill " + defSkill, nss2);
                            if (target.isPlayer()) {
                                target.getSkills().learn(defSkill, 20.0f);
                            }
                            else {
                                target.getSkills().learn(defSkill, 99.99f);
                            }
                        }
                        try {
                            if (resist > 0.0) {
                                attbonus = performer.getSkills().getSkill(attSkill).skillCheck(resist, abon, false, counter);
                            }
                            else {
                                attbonus = 10.0f + abon;
                            }
                        }
                        catch (NoSuchSkillException nss2) {
                            Spell.logger.log(Level.WARNING, performer.getName() + " learning attskill " + attSkill, nss2);
                            performer.getSkills().learn(attSkill, 1.0f);
                        }
                    }
                    if (!target.isPlayer()) {
                        if (!performer.isInvulnerable()) {
                            target.setTarget(performer.getWurmId(), false);
                        }
                        target.setFleeCounter(20);
                    }
                }
                float bonus = 0.0f;
                if (this.religious) {
                    bonus = Math.abs(performer.getAlignment()) - 49.0f;
                }
                if (bonus > 0.0f) {
                    bonus *= 1.0f + performer.getArmourLimitingFactor();
                }
                double distDiff = 0.0;
                if (this.isOffensive() || this.getNumber() == 450) {
                    final double dist = Creature.getRange(performer, target.getPosX(), target.getPosY());
                    try {
                        distDiff = dist - Actions.actionEntrys[this.number].getRange() / 2.0f;
                        if (distDiff > 0.0) {
                            distDiff *= 2.0;
                        }
                    }
                    catch (Exception ex) {
                        Spell.logger.log(Level.WARNING, this.getName() + " error: " + ex.getMessage());
                    }
                }
                if (!this.dominate) {
                    power = trimPower(performer, Math.max(Server.rand.nextFloat() * 10.0f, castSkill.skillCheck((1.0f + ItemBonus.getSpellResistBonus(target)) * (distDiff + this.difficulty + performer.getNumLinks() * 3), performer.zoneBonus + attbonus + bonus, false, counter)));
                }
                if (limitFail) {
                    power = -30.0f + Server.rand.nextFloat() * 29.0f;
                }
            }
            if (power > 0.0) {
                this.touchCooldown(performer);
                Methods.sendSound(performer, "sound.religion.channel");
                if (power >= 95.0) {
                    performer.achievement(629);
                }
                if (performer == target) {
                    performer.getCommunicator().sendNormalServerMessage("You cast '" + this.name + "' on yourself.");
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " casts '" + this.name + "' on " + target.getHimHerItString() + "self.", performer, 5, this.shouldMessageCombat());
                }
                else {
                    final ArrayList<MulticolorLineSegment> segments2 = new ArrayList<MulticolorLineSegment>();
                    segments2.add(new CreatureLineSegment(performer));
                    segments2.add(new MulticolorLineSegment(" casts " + this.name + " on ", (byte)0));
                    segments2.add(new CreatureLineSegment(target));
                    segments2.add(new MulticolorLineSegment(".", (byte)0));
                    MessageServer.broadcastColoredAction(segments2, performer, 5, this.shouldMessageCombat());
                    if (this.offensive || this.number == 450) {
                        target.getCommunicator().sendColoredMessageCombat(segments2, (byte)2);
                    }
                    segments2.get(1).setText(" cast " + this.name + " on ");
                    if (this.shouldMessageCombat()) {
                        performer.getCommunicator().sendColoredMessageCombat(segments2, (byte)2);
                    }
                    else {
                        performer.getCommunicator().sendColoredMessageEvent(segments2);
                    }
                }
                if (Constants.devmode) {
                    performer.getCommunicator().sendNormalServerMessage("Power=" + power);
                }
                Label_2082: {
                    if (this.religious) {
                        try {
                            performer.depleteFavor(needed, this.isOffensive());
                            break Label_2082;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.modifyKarma((int)(-needed));
                    if (!performer.isPlayer()) {
                        try {
                            performer.depleteFavor(100.0f, this.isOffensive());
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                        }
                    }
                }
                boolean eff = true;
                if (this.isOffensive() && target.getCultist() != null && target.getCultist().ignoresSpells()) {
                    eff = false;
                    final ArrayList<MulticolorLineSegment> segments3 = new ArrayList<MulticolorLineSegment>();
                    segments3.add(new CreatureLineSegment(target));
                    segments3.add(new MulticolorLineSegment(" ignores the effects!", (byte)0));
                    MessageServer.broadcastColoredAction(segments3, performer, target, 5, true);
                    if (this.shouldMessageCombat()) {
                        performer.getCommunicator().sendColoredMessageCombat(segments3);
                    }
                    else {
                        performer.getCommunicator().sendColoredMessageEvent(segments3);
                    }
                    segments3.get(1).setText(" ignore the effects!");
                    if (this.shouldMessageCombat()) {
                        target.getCommunicator().sendColoredMessageCombat(segments3);
                    }
                    else {
                        target.getCommunicator().sendColoredMessageEvent(segments3);
                    }
                }
                if (eff) {
                    if (Servers.isThisATestServer()) {
                        performer.getCommunicator().sendNormalServerMessage("Success Cost:" + needed + ", Power:" + power + ", SpeedMod:" + speedMod + ", Bonus:" + attbonus);
                    }
                    this.doEffect(castSkill, power, performer, target);
                }
            }
            else {
                Label_2538: {
                    if (this.isReligious()) {
                        performer.getCommunicator().sendNormalServerMessage("You fail to channel the '" + this.name + "'.");
                        Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails to channel the '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
                        try {
                            performer.depleteFavor(baseCost / 20.0f, this.isOffensive());
                            break Label_2538;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("The '" + this.name + "' fails!");
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails " + performer.getHisHerItsString() + " '" + this.name + "'!", performer, 5, this.shouldMessageCombat());
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Fail Cost:" + needed + ", Power:" + power);
                }
                this.doNegativeEffect(castSkill, power, performer, target);
            }
        }
        return done;
    }
    
    public final boolean run(final Creature performer, final Wound target, final float counter) {
        boolean done = false;
        if (!this.isCastValid(performer, this.targetWound, target.getName(), target.getCreature().getTileX(), target.getCreature().getTileY(), target.getCreature().isOnSurface())) {
            return true;
        }
        final Skill castSkill = this.getCastingSkill(performer);
        if (!this.precondition(castSkill, performer, target)) {
            return true;
        }
        float baseCost = this.getCost(target);
        if (performer.getPower() >= 5 && Servers.isThisATestServer()) {
            baseCost = 1.0f;
        }
        float needed = baseCost;
        if (this.religious) {
            if (performer.isRoyalPriest()) {
                needed *= 0.5f;
            }
            if (performer.getFavorLinked() < needed) {
                performer.getCommunicator().sendNormalServerMessage("You need more favor with your god to cast that spell.");
                return true;
            }
        }
        else if (performer.getKarma() < needed) {
            performer.getCommunicator().sendNormalServerMessage("You need more karma to use that ability.");
            return true;
        }
        if (target.getCreature() == null) {
            performer.getCommunicator().sendNormalServerMessage("You fail to get a clear line of sight.");
            return true;
        }
        if (counter == 1.0f && this.checkFavorRequirements(performer, baseCost)) {
            performer.getCommunicator().sendNormalServerMessage("You need more favor from your god to cast that spell.");
            return true;
        }
        double power = 0.0;
        if (counter == 1.0f && this.getCastingTime(performer) > 1) {
            performer.setStealth(false);
            performer.getCommunicator().sendNormalServerMessage("You start to cast '" + this.name + "' on the wound.");
            if (target.getCreature() != null) {
                Server.getInstance().broadCastAction(performer.getNameWithGenus() + " starts to cast '" + this.name + "' on " + target.getCreature().getName() + ".", performer, 5, this.shouldMessageCombat());
            }
            performer.sendActionControl(Actions.actionEntrys[122].getVerbString(), true, this.getCastingTime(performer) * 10);
        }
        int speedMod = 0;
        if (!this.isReligious()) {
            final Skill sp = performer.getMindSpeed();
            if (sp != null) {
                speedMod = (int)(sp.getKnowledge(0.0) / 25.0);
            }
        }
        if (counter >= this.getCastingTime(performer) - speedMod || (counter > 2.0f && performer.getPower() == 5)) {
            done = true;
            boolean limitFail = false;
            if (this.isOffensive() && performer.getArmourLimitingFactor() < 0.0f && Server.rand.nextFloat() < Math.abs(performer.getArmourLimitingFactor())) {
                limitFail = true;
            }
            float bonus = 0.0f;
            if (!this.religious) {
                final Skill sp2 = performer.getMindSpeed();
                if (sp2 != null) {
                    sp2.skillCheck(this.difficulty, performer.zoneBonus, false, counter);
                }
            }
            else {
                bonus = Math.abs(performer.getAlignment()) - 49.0f;
            }
            if (bonus > 0.0f) {
                bonus *= 1.0f + performer.getArmourLimitingFactor();
            }
            power = trimPower(performer, Math.max(Server.rand.nextFloat() * 10.0f, castSkill.skillCheck(this.difficulty + performer.getNumLinks() * 3, performer.zoneBonus + bonus, false, counter)));
            if (limitFail) {
                power = -30.0f + Server.rand.nextFloat() * 29.0f;
            }
            if (power >= 0.0) {
                this.touchCooldown(performer);
                if (power >= 95.0) {
                    performer.achievement(629);
                }
                if (target.getCreature() != null) {
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " casts '" + this.name + "' on " + target.getCreature().getName() + ".", performer, 5, this.shouldMessageCombat());
                }
                final Battle battle = performer.getBattle();
                if (battle != null) {
                    battle.addEvent(new BattleEvent((short)114, performer.getName(), target.getName(), performer.getName() + " casts '" + this.name + "' on " + target.getCreature().getName() + "."));
                }
                Label_0826: {
                    if (this.religious) {
                        try {
                            performer.depleteFavor(needed, this.isOffensive());
                            break Label_0826;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.modifyKarma((int)(-needed));
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Success Cost:" + needed + ", Power:" + power + ", SpeedMod:" + speedMod + ", Bonus:" + bonus);
                }
                this.doEffect(castSkill, power, performer, target);
            }
            else {
                Label_1127: {
                    if (this.isReligious()) {
                        performer.getCommunicator().sendNormalServerMessage("You fail to channel the '" + this.name + "'.");
                        Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails to channel the '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
                        try {
                            performer.depleteFavor(baseCost / 20.0f, this.isOffensive());
                            break Label_1127;
                        }
                        catch (IOException iox2) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox2);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("The '" + this.name + "' fails!");
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails " + performer.getHisHerItsString() + " '" + this.name + "'!", performer, 5, this.shouldMessageCombat());
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Fail Cost:" + needed + ", Power:" + power);
                }
            }
        }
        return done;
    }
    
    public final boolean run(final Creature performer, final int tilexborder, final int tileyborder, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir, final float counter) {
        boolean done = false;
        if (!this.isCastValid(performer, this.targetTileBorder, "that border", tilexborder, tileyborder, layer >= 0)) {
            return true;
        }
        final Skill castSkill = this.getCastingSkill(performer);
        if (!this.precondition(castSkill, performer, tilexborder, tileyborder, layer, heightOffset, dir)) {
            return true;
        }
        float baseCost = this.getCost(tilexborder, tileyborder, layer, heightOffset, dir);
        if (performer.getPower() >= 5 && Servers.isThisATestServer()) {
            baseCost = 1.0f;
        }
        float needed = baseCost;
        if (this.isReligious()) {
            if (performer.isRoyalPriest()) {
                needed *= 0.5f;
            }
            if (performer.getFavorLinked() < needed) {
                performer.getCommunicator().sendNormalServerMessage("You need more favor with your god to cast that spell.");
                return true;
            }
        }
        else if (performer.getPower() <= 1 && performer.getKarma() < needed) {
            performer.getCommunicator().sendNormalServerMessage("You need more karma to use that ability.");
            return true;
        }
        if (counter == 1.0f && this.checkFavorRequirements(performer, baseCost)) {
            performer.getCommunicator().sendNormalServerMessage("You need more favor from your god to cast that spell.");
            return true;
        }
        double power = 0.0;
        if (counter == 1.0f && this.getCastingTime(performer) > 1) {
            performer.setStealth(false);
            performer.getCommunicator().sendNormalServerMessage("You start to cast '" + this.name + "'.");
            Server.getInstance().broadCastAction(performer.getNameWithGenus() + " starts to cast '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
            performer.sendActionControl(Actions.actionEntrys[122].getVerbString(), true, this.getCastingTime(performer) * 10);
        }
        int speedMod = 0;
        if (!this.isReligious()) {
            final Skill sp = performer.getMindSpeed();
            if (sp != null) {
                speedMod = (int)(sp.getKnowledge(0.0) / 25.0);
            }
        }
        if (counter >= this.getCastingTime(performer) - speedMod || (counter > 2.0f && performer.getPower() == 5)) {
            done = true;
            boolean limitFail = false;
            if (this.isOffensive() && performer.getArmourLimitingFactor() < 0.0f && Server.rand.nextFloat() < Math.abs(performer.getArmourLimitingFactor())) {
                limitFail = true;
            }
            float bonus = 0.0f;
            if (!this.isReligious()) {
                final Skill sp2 = performer.getMindSpeed();
                if (sp2 != null) {
                    sp2.skillCheck(this.difficulty, performer.zoneBonus, false, counter);
                }
            }
            else {
                bonus = Math.abs(performer.getAlignment()) - 49.0f;
            }
            if (bonus > 0.0f) {
                bonus *= 1.0f + performer.getArmourLimitingFactor();
            }
            double distDiff = 0.0;
            if (this.isOffensive() || this.getNumber() == 450) {
                final double dist = 4.0 * Creature.getTileRange(performer, tilexborder, tileyborder);
                try {
                    distDiff = dist - Actions.actionEntrys[this.number].getRange() / 2.0f;
                    if (distDiff > 0.0) {
                        distDiff *= 2.0;
                    }
                }
                catch (Exception ex) {
                    Spell.logger.log(Level.WARNING, this.getName() + " error: " + ex.getMessage());
                }
            }
            power = trimPower(performer, Math.max(Server.rand.nextFloat() * 10.0f, castSkill.skillCheck(distDiff + this.difficulty + performer.getNumLinks() * 3, performer.zoneBonus + bonus, false, counter)));
            if (limitFail) {
                power = -30.0f + Server.rand.nextFloat() * 29.0f;
            }
            if (power >= 0.0) {
                if (performer.getPower() <= 1) {
                    this.touchCooldown(performer);
                }
                if (power >= 95.0) {
                    performer.achievement(629);
                }
                Server.getInstance().broadCastAction(performer.getNameWithGenus() + " casts '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
                performer.getCommunicator().sendNormalServerMessage("You succeed.");
                Label_0840: {
                    if (this.isReligious()) {
                        try {
                            performer.depleteFavor(needed, this.isOffensive());
                            break Label_0840;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    if (performer.getPower() <= 1) {
                        performer.modifyKarma((int)(-needed));
                    }
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Success Cost:" + needed + ", Power:" + power + ", SpeedMod:" + speedMod + ", Bonus:" + bonus);
                }
                this.doEffect(castSkill, power, performer, tilexborder, tileyborder, layer, heightOffset, dir);
            }
            else {
                Label_1148: {
                    if (this.religious) {
                        performer.getCommunicator().sendNormalServerMessage("You fail to channel the '" + this.name + "'.");
                        Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails to channel the '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
                        try {
                            performer.depleteFavor(baseCost / 20.0f, this.isOffensive());
                            break Label_1148;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("The '" + this.name + "' fails!");
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails " + performer.getHisHerItsString() + " '" + this.name + "'!", performer, 5, this.shouldMessageCombat());
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Fail Cost:" + needed + ", Power:" + power);
                }
            }
        }
        return done;
    }
    
    public final boolean run(final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset, final float counter) {
        boolean done = false;
        if (!this.isCastValid(performer, this.targetTile, "that tile", tilex, tiley, layer >= 0)) {
            return true;
        }
        final Skill castSkill = this.getCastingSkill(performer);
        if (!this.precondition(castSkill, performer, tilex, tiley, layer)) {
            return true;
        }
        float baseCost = this.getCost(tilex, tiley, layer, heightOffset);
        if (performer.getPower() >= 5 && Servers.isThisATestServer()) {
            baseCost = 1.0f;
        }
        float needed = baseCost;
        if (this.isReligious()) {
            if (performer.isRoyalPriest()) {
                needed *= 0.5f;
            }
            if (performer.getFavorLinked() < needed) {
                performer.getCommunicator().sendNormalServerMessage("You need more favor with your god to cast that spell.");
                return true;
            }
        }
        else if (performer.getPower() <= 1 && performer.getKarma() < needed) {
            performer.getCommunicator().sendNormalServerMessage("You need more karma to use that ability.");
            return true;
        }
        if (counter == 1.0f && this.checkFavorRequirements(performer, baseCost)) {
            performer.getCommunicator().sendNormalServerMessage("You need more favor from your god to cast that spell.");
            return true;
        }
        double power = 0.0;
        if (counter == 1.0f && this.getCastingTime(performer) > 1) {
            performer.setStealth(false);
            final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
            segments.add(new CreatureLineSegment(performer));
            segments.add(new MulticolorLineSegment(" starts to cast " + this.getName() + ".", (byte)0));
            MessageServer.broadcastColoredAction(segments, performer, null, 5, this.shouldMessageCombat(), (byte)2);
            segments.get(1).setText(" start to cast " + this.getName() + ".");
            if (this.shouldMessageCombat()) {
                performer.getCommunicator().sendColoredMessageCombat(segments, (byte)2);
            }
            else {
                performer.getCommunicator().sendColoredMessageEvent(segments);
            }
            performer.sendActionControl(Actions.actionEntrys[122].getVerbString(), true, this.getCastingTime(performer) * 10);
        }
        int speedMod = 0;
        if (!this.isReligious()) {
            final Skill sp = performer.getMindSpeed();
            if (sp != null) {
                speedMod = (int)(sp.getKnowledge(0.0) / 25.0);
            }
        }
        if (counter >= this.getCastingTime(performer) - speedMod || (counter > 2.0f && performer.getPower() == 5)) {
            done = true;
            boolean limitFail = false;
            if (this.isOffensive() && performer.getArmourLimitingFactor() < 0.0f && Server.rand.nextFloat() < Math.abs(performer.getArmourLimitingFactor())) {
                limitFail = true;
            }
            float bonus = 0.0f;
            if (!this.isReligious()) {
                final Skill sp2 = performer.getMindSpeed();
                if (sp2 != null) {
                    sp2.skillCheck(this.difficulty, performer.zoneBonus, false, counter);
                }
            }
            else {
                bonus = Math.abs(performer.getAlignment()) - 49.0f;
            }
            if (bonus > 0.0f) {
                bonus *= 1.0f + performer.getArmourLimitingFactor();
            }
            double distDiff = 0.0;
            if (this.isOffensive() || this.getNumber() == 450) {
                final double dist = 4.0 * Creature.getTileRange(performer, tilex, tiley);
                try {
                    distDiff = dist - Actions.actionEntrys[this.number].getRange() / 2.0f;
                    if (distDiff > 0.0) {
                        distDiff *= 2.0;
                    }
                }
                catch (Exception ex) {
                    Spell.logger.log(Level.WARNING, this.getName() + " error: " + ex.getMessage());
                }
            }
            power = trimPower(performer, Math.max(Server.rand.nextFloat() * 10.0f, castSkill.skillCheck(distDiff + this.difficulty + performer.getNumLinks() * 3, performer.zoneBonus + bonus, false, counter)));
            if (limitFail) {
                power = -30.0f + Server.rand.nextFloat() * 29.0f;
            }
            if (power >= 0.0) {
                if (performer.getPower() <= 1) {
                    this.touchCooldown(performer);
                }
                if (power >= 95.0) {
                    performer.achievement(629);
                }
                final ArrayList<MulticolorLineSegment> segments2 = new ArrayList<MulticolorLineSegment>();
                segments2.add(new CreatureLineSegment(performer));
                segments2.add(new MulticolorLineSegment(" casts " + this.getName() + ".", (byte)0));
                MessageServer.broadcastColoredAction(segments2, performer, null, 5, this.shouldMessageCombat(), (byte)2);
                segments2.get(1).setText(" cast " + this.getName() + ".");
                if (this.shouldMessageCombat()) {
                    performer.getCommunicator().sendColoredMessageCombat(segments2, (byte)2);
                }
                else {
                    performer.getCommunicator().sendColoredMessageEvent(segments2);
                }
                Label_0992: {
                    if (this.isReligious()) {
                        try {
                            performer.depleteFavor(needed, this.isOffensive());
                            break Label_0992;
                        }
                        catch (IOException iox) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    if (performer.getPower() <= 1) {
                        performer.modifyKarma((int)(-needed));
                    }
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Success Cost:" + needed + ", Power:" + power + ", SpeedMod:" + speedMod + ", Bonus:" + bonus);
                }
                this.doEffect(castSkill, power, performer, tilex, tiley, layer, heightOffset);
            }
            else {
                Label_1298: {
                    if (this.isReligious()) {
                        performer.getCommunicator().sendNormalServerMessage("You fail to channel the '" + this.name + "'.");
                        Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails to channel the '" + this.name + "'.", performer, 5, this.shouldMessageCombat());
                        try {
                            performer.depleteFavor(baseCost / 20.0f, this.isOffensive());
                            break Label_1298;
                        }
                        catch (IOException iox2) {
                            Spell.logger.log(Level.WARNING, performer.getName(), iox2);
                            performer.getCommunicator().sendNormalServerMessage("The spell fizzles!");
                            return true;
                        }
                    }
                    performer.getCommunicator().sendNormalServerMessage("The '" + this.name + "' fails!");
                    Server.getInstance().broadCastAction(performer.getNameWithGenus() + " fails " + performer.getHisHerItsString() + " '" + this.name + "'!", performer, 5, this.shouldMessageCombat());
                }
                if (Servers.isThisATestServer()) {
                    performer.getCommunicator().sendNormalServerMessage("Fail Cost:" + needed + ", Power:" + power);
                }
            }
        }
        return done;
    }
    
    private boolean shouldMessageCombat() {
        return this.offensive || this.karmaSpell || this.healing;
    }
    
    public void enchantItem(final Creature performer, final Item target, final byte enchantment, final float power) {
        ItemSpellEffects effs = target.getSpellEffects();
        if (effs == null) {
            effs = new ItemSpellEffects(target.getWurmId());
        }
        SpellEffect eff = effs.getSpellEffect(enchantment);
        if (eff == null) {
            eff = new SpellEffect(target.getWurmId(), enchantment, power, 20000000);
            effs.addSpellEffect(eff);
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " " + this.getEffectdesc(), (byte)2);
            Server.getInstance().broadCastAction(performer.getNameWithGenus() + " looks pleased.", performer, 5);
        }
        else if (eff.getPower() > power) {
            performer.getCommunicator().sendNormalServerMessage("You frown as you fail to improve the power.", (byte)3);
            Server.getInstance().broadCastAction(performer.getNameWithGenus() + " frowns.", performer, 5);
        }
        else {
            eff.improvePower(performer, power);
            performer.getCommunicator().sendNormalServerMessage("You succeed in improving the power of the " + this.getName() + ".", (byte)2);
            Server.getInstance().broadCastAction(performer.getNameWithGenus() + " looks pleased.", performer, 5);
        }
    }
    
    public static final boolean mayArmourBeEnchanted(final Item target, @Nullable final Creature performer, final byte enchantment) {
        if (!mayBeEnchanted(target)) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.");
            }
            return false;
        }
        if (enchantment != 17 && target.getSpellPainShare() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 46 && target.getSpellSlowdown() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        return true;
    }
    
    public static final boolean mayReceiveSkillgainBuff(final Item target, @Nullable final Creature performer, final byte enchantment) {
        if (!mayBeEnchanted(target)) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.");
            }
            return false;
        }
        if (enchantment != 47) {
            if (target.getBonusForSpellEffect((byte)47) > 0.0f) {
                if (performer != null) {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
                }
                return false;
            }
        }
        else if (target.getBonusForSpellEffect((byte)13) > 0.0f || target.getBonusForSpellEffect((byte)16) > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        return true;
    }
    
    public static final boolean mayWeaponBeEnchanted(final Item target, @Nullable final Creature performer, final byte enchantment) {
        if (!mayBeEnchanted(target)) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.");
            }
            return false;
        }
        if (enchantment != 18 && target.getSpellRotModifier() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 26 && target.getSpellLifeTransferModifier() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 27 && target.getSpellVenomBonus() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 33 && target.getSpellFrostDamageBonus() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 45 && target.getSpellExtraDamageBonus() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 14 && target.getSpellDamageBonus() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        if (enchantment != 63 && target.getSpellEssenceDrainModifier() > 0.0f) {
            if (performer != null) {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted with something that would negate the effect.");
            }
            return false;
        }
        return true;
    }
    
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
    }
    
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Creature target) {
    }
    
    public void castSpell(final double power, final Creature performer, final Item target) {
        if (this.precondition(performer.getMindLogical(), performer, target)) {
            this.doEffect(performer.getMindLogical(), power, performer, target);
        }
    }
    
    public void castSpell(final double power, final Creature performer, final Creature target) {
        if (this.precondition(performer.getMindLogical(), performer, target)) {
            this.doEffect(performer.getMindLogical(), power, performer, target);
        }
    }
    
    public void castSpell(final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
        if (this.precondition(performer.getMindLogical(), performer, tilex, tiley, layer)) {
            this.doEffect(performer.getMindLogical(), power, performer, tilex, tiley, layer, heightOffset);
        }
    }
    
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
    }
    
    void doNegativeEffect(final Skill castSkill, final double power, final Creature performer, final Item target) {
    }
    
    void doEffect(final Skill castSkill, final double power, final Creature performer, final Wound target) {
    }
    
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir) {
    }
    
    void doEffect(final Skill castSkill, final double power, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset) {
    }
    
    boolean precondition(final Skill castSkill, final Creature performer, final Creature target) {
        return true;
    }
    
    boolean precondition(final Skill castSkill, final Creature performer, final Item target) {
        return true;
    }
    
    boolean postcondition(final Skill castSkill, final Creature performer, final Item target, final double effect) {
        return true;
    }
    
    boolean precondition(final Skill castSkill, final Creature performer, final Wound target) {
        return true;
    }
    
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer) {
        return true;
    }
    
    boolean precondition(final Skill castSkill, final Creature performer, final int tilex, final int tiley, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir) {
        return true;
    }
    
    final int getNumber() {
        return this.number;
    }
    
    public final String getName() {
        return this.name;
    }
    
    final int getCastingTime(final Creature performer) {
        final SpellEffects effs = performer.getSpellEffects();
        if (effs != null) {
            final SpellEffect eff = effs.getSpellEffect((byte)93);
            if (eff != null) {
                return (int)(this.castingTime * (1.0f + Math.max(30.0f, eff.getPower()) / 100.0f));
            }
        }
        return this.castingTime;
    }
    
    final boolean isReligious() {
        return this.religious;
    }
    
    final boolean isKarmaSpell() {
        return this.karmaSpell;
    }
    
    final boolean isOffensive() {
        return this.offensive;
    }
    
    public boolean isCreatureItemEnchantment() {
        return this.isTargetCreature() && this.isTargetAnyItem() && this.getEnchantment() != 0;
    }
    
    public boolean isItemEnchantment() {
        return this.isTargetAnyItem() && this.getEnchantment() != 0;
    }
    
    public final int getCost() {
        return this.cost;
    }
    
    public int getCost(final Creature creature) {
        return this.cost;
    }
    
    public int getCost(final Item item) {
        return this.cost;
    }
    
    public int getCost(final Wound wound) {
        return this.getCost();
    }
    
    public int getCost(final int tilexborder, final int tileyborder, final int layer, final int heightOffset, final Tiles.TileBorderDirection dir) {
        return this.getCost();
    }
    
    public int getCost(final int tilex, final int tiley, final int layer, final int heightOffset) {
        return this.getCost();
    }
    
    public boolean isDynamicCost() {
        return this.hasDynamicCost;
    }
    
    public final int getDifficulty(final boolean forItem) {
        if (forItem && this.isCreatureItemEnchantment()) {
            return this.difficulty * 2;
        }
        return this.difficulty;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    final int getLevel() {
        return this.level;
    }
    
    static final Logger getLogger() {
        return Spell.logger;
    }
    
    public final boolean isTargetCreature() {
        return this.targetCreature;
    }
    
    public final boolean isTargetItem() {
        return this.targetItem;
    }
    
    public final boolean isTargetAnyItem() {
        return this.targetItem || this.targetWeapon || this.targetArmour || this.targetJewelry || this.targetPendulum;
    }
    
    public final boolean isTargetWound() {
        return this.targetWound;
    }
    
    public final boolean isTargetTile() {
        return this.targetTile;
    }
    
    public final boolean isTargetTileBorder() {
        return this.targetTileBorder;
    }
    
    public final boolean isTargetWeapon() {
        return this.targetWeapon;
    }
    
    public final boolean isTargetArmour() {
        return this.targetArmour;
    }
    
    public final boolean isTargetJewelry() {
        return this.targetJewelry;
    }
    
    public final boolean isTargetPendulum() {
        return this.targetPendulum;
    }
    
    final boolean isDominate() {
        return this.dominate;
    }
    
    public final byte getEnchantment() {
        return this.enchantment;
    }
    
    final String getEffectdesc() {
        return this.effectdesc;
    }
    
    public final boolean isChaplain() {
        return this.type == 1 || this.type == 0;
    }
    
    public final boolean isReligiousSpell() {
        return this.religious;
    }
    
    public final boolean isSorcerySpell() {
        return this.karmaSpell;
    }
    
    private float getMaterialShatterMod(final byte material) {
        if (Features.Feature.METALLIC_ITEMS.isEnabled()) {
            switch (material) {
                case 56: {
                    return 0.15f;
                }
                case 57: {
                    return 0.25f;
                }
                case 7: {
                    return 0.2f;
                }
                case 67: {
                    return 1.0f;
                }
                case 8: {
                    return 0.1f;
                }
                case 96: {
                    return 0.15f;
                }
            }
        }
        else if (material == 67) {
            return 1.0f;
        }
        return 0.0f;
    }
    
    void checkDestroyItem(final double power, final Creature performer, final Item target) {
        if (Server.rand.nextFloat() < this.getMaterialShatterMod(target.getMaterial())) {
            return;
        }
        final ItemSpellEffects spellEffects = target.getSpellEffects();
        float chanceModifier = 1.0f;
        if (spellEffects != null) {
            chanceModifier = spellEffects.getRuneEffect(RuneUtilities.ModifierEffect.ENCH_SHATTERRES);
        }
        if (power < -(target.getQualityLevel() * chanceModifier) || (power < 0.0 && Server.rand.nextFloat() <= 0.01 / chanceModifier)) {
            if (spellEffects != null) {
                final SpellEffect eff = spellEffects.getSpellEffect((byte)98);
                if (eff != null) {
                    spellEffects.removeSpellEffect((byte)98);
                    performer.getCommunicator().sendAlertServerMessage("The " + target.getName() + " emits a strong deep sound of resonance and starts to shatter, but the Metallic Liquid protects the " + target.getName() + "! The Metallic Liquid has dissipated.", (byte)3);
                    Server.getInstance().broadCastAction("The " + target.getName() + " starts to shatter, but gets protected by a mystic substance!", performer, 5);
                    return;
                }
            }
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " emits a strong deep sound of resonance, then shatters!", (byte)3);
            Server.getInstance().broadCastAction("The " + target.getName() + " shatters!", performer, 5);
            Items.destroyItem(target.getWurmId());
            performer.achievement(627);
        }
        else if (power < -(target.getQualityLevel() * chanceModifier) / 3.0f) {
            if (spellEffects != null) {
                final SpellEffect eff = spellEffects.getSpellEffect((byte)98);
                if (eff != null) {
                    eff.setPower(eff.getPower() - 20.0f);
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " emits a deep worrying sound of resonance, and a small crack wants to start forming, but the Metallic Liquid steps in and takes the damage instead!");
                    if (eff.getPower() <= 0.0f) {
                        performer.getCommunicator().sendAlertServerMessage("The Metallic Liquid's strength has been depleted, and its protection has been removed from the " + target.getName());
                        spellEffects.removeSpellEffect((byte)98);
                    }
                    Server.getInstance().broadCastAction("The " + target.getName() + " starts to form cracks, but a mystic liquid protects it!", performer, 5);
                    return;
                }
            }
            target.setDamage(target.getDamage() + (float)Math.abs(power / 20.0));
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " emits a deep worrying sound of resonance, and a small crack starts to form on the surface.");
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " emits a deep worrying sound of resonance, but stays intact.");
        }
    }
    
    final List<Long> findBridgesInTheArea(final int sx, final int sy, final int ex, final int ey, final int layer, final int heightOffset, final int groundHeight) {
        final int actualHeight = groundHeight + heightOffset;
        final List<Long> arr = new ArrayList<Long>();
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                if (x == 266 && y == 303) {
                    final boolean f = false;
                }
                final VolaTile tile = Zones.getOrCreateTile(x, y, layer >= 0);
                if (tile != null) {
                    final Structure structure = tile.getStructure();
                    if (structure != null && structure.isTypeBridge()) {
                        final float[] hts = Zones.getNodeHeights(x, y, layer, structure.getWurmId());
                        final float h = hts[0] * 0.5f * 0.5f + hts[1] * 0.5f * 0.5f + hts[2] * 0.5f * 0.5f + hts[3] * 0.5f * 0.5f;
                        int closestHeight = -1000;
                        int smallestDiff = 110;
                        for (int i = 0; i < hts.length; ++i) {
                            final int dec = (int)(hts[i] * 10.0f);
                            final int diff = Math.abs(actualHeight - dec);
                            if (diff < smallestDiff) {
                                smallestDiff = diff;
                                closestHeight = dec;
                            }
                        }
                        if (closestHeight > -1000 && smallestDiff <= 5) {
                            final Long id = structure.getWurmId();
                            if (!arr.contains(id)) {
                                arr.add(id);
                            }
                        }
                    }
                }
            }
        }
        return arr;
    }
    
    final void calculateAOE(final int sx, final int sy, final int ex, final int ey, final int tilex, final int tiley, final int layer, final Structure playerStructure, final Structure targetStructure, final int heightOffset) {
        this.area = new boolean[1 + ex - sx][1 + ey - sy];
        this.offsets = new int[1 + ex - sx][1 + ey - sy];
        int groundHeight = 0;
        if (targetStructure == null || targetStructure.isTypeHouse()) {
            final float[] hts = Zones.getNodeHeights(tilex, tiley, layer, -10L);
            final float h = hts[0] * 0.5f * 0.5f + hts[1] * 0.5f * 0.5f + hts[2] * 0.5f * 0.5f + hts[3] * 0.5f * 0.5f;
            groundHeight = (int)(h * 10.0f);
        }
        final List<Long> bridges = this.findBridgesInTheArea(sx, sy, ex, ey, layer, heightOffset, groundHeight);
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final Item ring = Zones.isWithinDuelRing(x, y, layer >= 0);
                if (ring == null) {
                    final VolaTile tile = Zones.getOrCreateTile(x, y, layer >= 0);
                    if (tile != null) {
                        final Structure tileStructure = tile.getStructure();
                        final int currAreaX = x - sx;
                        final int currAreaY = y - sy;
                        if (layer < 0) {
                            final int ttile = Server.caveMesh.getTile(x, y);
                            final byte ttype = Tiles.decodeType(ttile);
                            if (Tiles.decodeHeight(ttile) < 0) {
                                this.area[currAreaX][currAreaY] = true;
                            }
                            else if (Tiles.isSolidCave(ttype)) {
                                this.area[currAreaX][currAreaY] = true;
                            }
                            else {
                                if (x > tilex + 1) {
                                    if (this.area[currAreaX - 1][currAreaY]) {
                                        this.area[currAreaX][currAreaY] = true;
                                    }
                                }
                                else if (x < tilex - 1 && this.area[currAreaX + 1][currAreaY]) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                                if (y < tiley - 1) {
                                    if (this.area[currAreaX][currAreaY + 1]) {
                                        this.area[currAreaX][currAreaY] = true;
                                    }
                                }
                                else if (y > tiley + 1 && this.area[currAreaX][currAreaY - 1]) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                            }
                        }
                        else if (targetStructure != null && targetStructure.isTypeHouse()) {
                            if (tileStructure != null && tileStructure.getWurmId() == targetStructure.getWurmId()) {
                                boolean foundFloor = false;
                                for (final Floor floor : tile.getFloors()) {
                                    if (floor.getHeightOffset() == heightOffset) {
                                        foundFloor = true;
                                        break;
                                    }
                                }
                                if (!foundFloor) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                                else {
                                    this.offsets[currAreaX][currAreaY] = heightOffset + groundHeight;
                                }
                            }
                            else if (tileStructure != null && tileStructure.isTypeBridge()) {
                                final Long bridgeId = tileStructure.getWurmId();
                                if (!bridges.contains(bridgeId)) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                                else {
                                    for (final BridgePart bp : tileStructure.getBridgeParts()) {
                                        if (bp.getTileX() == x && bp.getTileY() == y) {
                                            this.offsets[currAreaX][currAreaY] = bp.getHeightOffset();
                                            break;
                                        }
                                    }
                                }
                            }
                            else {
                                this.area[currAreaX][currAreaY] = true;
                            }
                        }
                        else if (targetStructure != null && targetStructure.isTypeBridge()) {
                            if (tileStructure != null && tileStructure.isTypeHouse()) {
                                boolean foundConnection = false;
                                for (int xx = x - 1; xx <= x + 1; ++xx) {
                                    for (int yy = y - 1; yy <= y + 1; ++yy) {
                                        if (yy != y || xx != x) {
                                            final VolaTile t = Zones.getOrCreateTile(xx, yy, layer >= 0);
                                            if (t != null) {
                                                final Structure s = t.getStructure();
                                                if (s != null && s.getWurmId() == targetStructure.getWurmId()) {
                                                    foundConnection = true;
                                                    int bridgeH = 0;
                                                    for (final BridgePart part : targetStructure.getBridgeParts()) {
                                                        if (part.getTileX() == xx && part.getTileY() == yy) {
                                                            bridgeH = part.getHeightOffset();
                                                            break;
                                                        }
                                                    }
                                                    final float[] hts2 = Zones.getNodeHeights(x, y, layer, -10L);
                                                    final float h2 = hts2[0] * 0.5f * 0.5f + hts2[1] * 0.5f * 0.5f + hts2[2] * 0.5f * 0.5f + hts2[3] * 0.5f * 0.5f;
                                                    final int gh = (int)(h2 * 10.0f);
                                                    int closestHeight = -1000;
                                                    int smallestDiff = 110;
                                                    for (final Floor floor2 : tile.getFloors()) {
                                                        final int fh = gh + floor2.getFloorLevel() * 30;
                                                        if (Math.abs(fh - bridgeH) < smallestDiff) {
                                                            smallestDiff = Math.abs(fh - bridgeH);
                                                            closestHeight = fh;
                                                        }
                                                    }
                                                    this.offsets[currAreaX][currAreaY] = closestHeight;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (foundConnection) {
                                        break;
                                    }
                                }
                                if (!foundConnection) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                            }
                            else if (tileStructure != null && tileStructure.isTypeBridge()) {
                                if (tileStructure.getWurmId() != targetStructure.getWurmId()) {
                                    final Long id = tileStructure.getWurmId();
                                    if (!bridges.contains(id)) {
                                        BridgePart part2 = null;
                                        for (final BridgePart bp2 : tileStructure.getBridgeParts()) {
                                            if (bp2.getTileX() == x && bp2.getTileY() == y) {
                                                part2 = bp2;
                                                break;
                                            }
                                        }
                                        this.area[currAreaX][currAreaY] = true;
                                        final float[] hts3 = Zones.getNodeHeights(x, y, layer, -10L);
                                        final float h3 = hts3[0] * 0.5f * 0.5f + hts3[1] * 0.5f * 0.5f + hts3[2] * 0.5f * 0.5f + hts3[3] * 0.5f * 0.5f;
                                        groundHeight = (int)(h3 * 10.0f);
                                        if (Math.abs(groundHeight - heightOffset) < 25) {
                                            this.offsets[currAreaX][currAreaY] = heightOffset;
                                            this.area[currAreaX][currAreaY] = false;
                                        }
                                    }
                                }
                                else {
                                    BridgePart part3 = null;
                                    for (final BridgePart bp : tileStructure.getBridgeParts()) {
                                        if (bp.getTileX() == x && bp.getTileY() == y) {
                                            part3 = bp;
                                            break;
                                        }
                                    }
                                    if (part3 != null) {
                                        this.offsets[currAreaX][currAreaY] = part3.getHeightOffset();
                                    }
                                }
                            }
                        }
                        else if (tileStructure != null) {
                            if (tileStructure.isTypeBridge()) {
                                BridgePart part3 = null;
                                for (final BridgePart p : tileStructure.getBridgeParts()) {
                                    if (p.getTileX() == x && p.getTileY() == y) {
                                        part3 = p;
                                        break;
                                    }
                                }
                                if (part3 != null && Math.abs(part3.getHeightOffset() - groundHeight) > 25) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                            }
                            else {
                                this.area[currAreaX][currAreaY] = true;
                            }
                        }
                    }
                }
            }
        }
    }
    
    final void calculateArea(final int sx, final int sy, final int ex, final int ey, final int tilex, final int tiley, final int layer, final Structure currstr) {
        this.area = new boolean[1 + ex - sx][1 + ey - sy];
        for (int x = sx; x <= ex; ++x) {
            for (int y = sy; y <= ey; ++y) {
                final Item ring = Zones.isWithinDuelRing(x, y, layer > 0);
                if (ring == null) {
                    final VolaTile t = Zones.getTileOrNull(x, y, layer >= 0);
                    Structure toCheck;
                    if (t != null) {
                        toCheck = t.getStructure();
                        if (toCheck != null && (!toCheck.isFinalFinished() || !toCheck.isFinished())) {
                            toCheck = null;
                        }
                    }
                    else {
                        toCheck = null;
                    }
                    final int currAreaX = x - sx;
                    final int currAreaY = y - sy;
                    if (currstr == toCheck) {
                        if (layer < 0) {
                            final int ttile = Server.caveMesh.getTile(x, y);
                            final byte ttype = Tiles.decodeType(ttile);
                            if (Tiles.decodeHeight(ttile) < 0) {
                                this.area[currAreaX][currAreaY] = true;
                            }
                            else if (Tiles.isSolidCave(ttype)) {
                                this.area[currAreaX][currAreaY] = true;
                            }
                            else {
                                if (x > tilex + 1) {
                                    if (this.area[currAreaX - 1][currAreaY]) {
                                        this.area[currAreaX][currAreaY] = true;
                                    }
                                }
                                else if (x < tilex - 1 && this.area[currAreaX + 1][currAreaY]) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                                if (y < tiley - 1) {
                                    if (this.area[currAreaX][currAreaY + 1]) {
                                        this.area[currAreaX][currAreaY] = true;
                                    }
                                }
                                else if (y > tiley + 1 && this.area[currAreaX][currAreaY - 1]) {
                                    this.area[currAreaX][currAreaY] = true;
                                }
                            }
                        }
                    }
                    else {
                        this.area[currAreaX][currAreaY] = true;
                    }
                }
            }
        }
        if (layer < 0) {
            for (int x = sx; x <= ex; ++x) {
                for (int y = sy; y <= ey; ++y) {
                    final int currAreaX2 = x - sx;
                    final int currAreaY2 = y - sy;
                    final int ttile2 = Server.caveMesh.getTile(x, y);
                    final byte ttype2 = Tiles.decodeType(ttile2);
                    if (Tiles.decodeHeight(ttile2) < 0) {
                        this.area[currAreaX2][currAreaY2] = true;
                    }
                    else if (Tiles.isSolidCave(ttype2)) {
                        this.area[currAreaX2][currAreaY2] = true;
                    }
                    else {
                        if (x > tilex + 1) {
                            if (this.area[currAreaX2 - 1][currAreaY2]) {
                                this.area[currAreaX2][currAreaY2] = true;
                            }
                        }
                        else if (x < tilex - 1 && this.area[currAreaX2 + 1][currAreaY2]) {
                            this.area[currAreaX2][currAreaY2] = true;
                        }
                        if (y < tiley - 1) {
                            if (this.area[currAreaX2][currAreaY2 + 1]) {
                                this.area[currAreaX2][currAreaY2] = true;
                            }
                        }
                        else if (y > tiley + 1 && this.area[currAreaX2][currAreaY2 - 1]) {
                            this.area[currAreaX2][currAreaY2] = true;
                        }
                    }
                }
            }
        }
    }
    
    final boolean isSpellBlocked(final int deityId, final int blockingSpellNum) {
        final Random rand2 = new Random(deityId + this.number * 1071);
        if (rand2.nextInt(3) == 0) {
            final Random rand3 = new Random(deityId + blockingSpellNum * 1071);
            if (rand3.nextInt(3) == 0) {
                return true;
            }
        }
        return false;
    }
    
    final boolean deityCanHaveSpell(final int deityId) {
        final Random rand = new Random(deityId + this.number * 1071);
        return rand.nextInt(3) == 0;
    }
    
    final boolean hateEnchantPrecondition(final Item target, final Creature performer) {
        if (!mayBeEnchanted(target)) {
            performer.getCommunicator().sendNormalServerMessage("The spell will not work on that.", (byte)3);
            return false;
        }
        if (target.enchantment != 0) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is already enchanted.", (byte)3);
            return false;
        }
        if (target.getCurrentQualityLevel() < 70.0f) {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is of too low quality for this enchantment.", (byte)3);
            return false;
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(Spell.class.getName());
    }
}
