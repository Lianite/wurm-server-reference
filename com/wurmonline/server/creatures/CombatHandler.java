// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.ListIterator;
import com.wurmonline.server.players.Titles;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.zones.VirtualZone;
import java.util.Random;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.MessageServer;
import com.wurmonline.server.spells.SpellResist;
import com.wurmonline.server.spells.EnchantUtil;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import java.util.HashSet;
import com.wurmonline.server.Features;
import com.wurmonline.server.combat.CombatMove;
import java.util.Iterator;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.utils.CreatureLineSegment;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.ArrayList;
import com.wurmonline.server.combat.Weapon;
import com.wurmonline.server.behaviours.Actions;
import javax.annotation.Nullable;
import java.util.logging.Level;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.util.Collection;
import java.util.LinkedList;
import com.wurmonline.server.Players;
import com.wurmonline.server.modifiers.DoubleValueModifier;
import java.util.Set;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.combat.SpecialMove;
import com.wurmonline.server.behaviours.ActionEntry;
import java.util.List;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.server.combat.CombatConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class CombatHandler implements MiscConstants, TimeConstants, CombatConstants, SoundNames, CreatureTemplateIds
{
    private static final Logger logger;
    private final Creature creature;
    private boolean addToSkills;
    public static final byte[] NO_COMBAT_OPTIONS;
    private List<ActionEntry> moveStack;
    private boolean turned;
    private byte currentStance;
    private static SpecialMove[] specialmoves;
    private byte currentStrength;
    private static final List<ActionEntry> standardDefences;
    private static boolean hit;
    private static boolean miss;
    private static boolean crit;
    private int usedShieldThisRound;
    private boolean receivedShieldSkill;
    private static boolean dead;
    private static boolean aiming;
    private static float chanceToHit;
    private static double attCheck;
    private static double attBonus;
    private static double defCheck;
    private static double defBonus;
    private static double damage;
    private static byte pos;
    private static byte type;
    private static Item defShield;
    private static Item defParryWeapon;
    private static Item defLeftWeapon;
    private static Skill defPrimWeaponSkill;
    private static Skills defenderSkills;
    private static String attString;
    private static String othersString;
    private static final List<ActionEntry> selectStanceList;
    private static final String prones = "stancerebound";
    private static final String opens = "stanceopen";
    private static final String dodge = "dodge";
    private static final String fight = "fight";
    private static final String strike = "_strike";
    public static final float minShieldDam = 0.01f;
    private static boolean justOpen;
    private static double manouvreMod;
    private byte opportunityAttacks;
    public static final float enemyTerritoryMod = 0.7f;
    private static float parryBonus;
    private byte battleratingPenalty;
    private Set<DoubleValueModifier> parryModifiers;
    private Set<DoubleValueModifier> dodgeModifiers;
    private boolean sentAttacks;
    private static final float DODGE_MODIFIER = 3.0f;
    private boolean receivedFStyleSkill;
    private boolean receivedWeaponSkill;
    private boolean receivedSecWeaponSkill;
    private Set<Item> secattacks;
    private boolean hasSpiritFervor;
    private int lastShieldBashed;
    private boolean hasRodEffect;
    private static final float poleArmDamageBonus = 1.7f;
    private float lastTimeStamp;
    private float lastAttackPollDelta;
    private float waitTime;
    
    public CombatHandler(final Creature _creature) {
        this.addToSkills = false;
        this.moveStack = null;
        this.turned = false;
        this.currentStance = 15;
        this.currentStrength = 1;
        this.usedShieldThisRound = 0;
        this.receivedShieldSkill = false;
        this.opportunityAttacks = 0;
        this.battleratingPenalty = 0;
        this.sentAttacks = false;
        this.receivedFStyleSkill = false;
        this.receivedWeaponSkill = false;
        this.receivedSecWeaponSkill = false;
        this.secattacks = null;
        this.hasSpiritFervor = false;
        this.lastShieldBashed = 0;
        this.hasRodEffect = false;
        this.lastTimeStamp = 1.0f;
        this.lastAttackPollDelta = 0.0f;
        this.waitTime = 0.0f;
        this.creature = _creature;
    }
    
    public static void resolveRound() {
        Players.getInstance().combatRound();
        Creatures.getInstance().combatRound();
    }
    
    public void shieldBash() {
        this.lastShieldBashed = 2;
        this.creature.getCommunicator().sendToggleShield(false);
    }
    
    public boolean mayShieldBash() {
        return this.lastShieldBashed <= 0;
    }
    
    public void calcAttacks(final boolean newround) {
        if (!this.creature.isDead() && (this.moveStack == null || newround || !this.sentAttacks)) {
            if (this.moveStack == null) {
                this.moveStack = new LinkedList<ActionEntry>();
            }
            else {
                this.moveStack.clear();
            }
            CombatHandler.manouvreMod = this.creature.getMovementScheme().armourMod.getModifier();
            if (this.creature.opponent != null && this.creature.getPrimWeapon() != null) {
                float knowl = this.getCombatKnowledgeSkill();
                if (!this.creature.isPlayer()) {
                    knowl += 20.0f;
                }
                if (knowl > 50.0f) {
                    this.moveStack.addAll(CombatHandler.standardDefences);
                }
                final float mycr = this.creature.getCombatHandler().getCombatRating(this.creature.opponent, this.creature.getPrimWeapon(), false);
                final float oppcr = this.creature.opponent.getCombatHandler().getCombatRating(this.creature, this.creature.opponent.getPrimWeapon(), false);
                this.moveStack.addAll(this.getHighAttacks(this.creature.getPrimWeapon(), this.creature.isAutofight(), this.creature.opponent, mycr, oppcr, knowl));
                this.moveStack.addAll(this.getMidAttacks(this.creature.getPrimWeapon(), this.creature.isAutofight(), this.creature.opponent, mycr, oppcr, knowl));
                this.moveStack.addAll(this.getLowAttacks(this.creature.getPrimWeapon(), this.creature.isAutofight(), this.creature.opponent, mycr, oppcr, knowl));
            }
            if (!this.sentAttacks || newround) {
                this.sentAttacks = true;
                if (!this.creature.isAutofight()) {
                    this.creature.getCommunicator().sendCombatOptions(getOptions(this.moveStack, this.currentStance), (short)0);
                }
                this.sendSpecialMoves();
                if (this.creature.getShield() != null) {
                    if (this.mayShieldBash()) {
                        this.creature.getCommunicator().sendToggleShield(true);
                    }
                    else {
                        this.creature.getCommunicator().sendToggleShield(false);
                    }
                }
                else {
                    this.creature.getCommunicator().sendToggleShield(false);
                }
            }
        }
    }
    
    public float getCombatKnowledgeSkill() {
        float knowl = 0.0f;
        int primarySkill = 10052;
        try {
            if (!this.creature.getPrimWeapon().isBodyPartAttached()) {
                primarySkill = this.creature.getPrimWeapon().getPrimarySkill();
            }
            final Skill fightingSkill = this.creature.getSkills().getSkill(primarySkill);
            knowl = (float)fightingSkill.getKnowledge(this.creature.getPrimWeapon(), 0.0);
        }
        catch (NoSuchSkillException ex) {}
        if (knowl == 0.0f && !this.creature.isPlayer()) {
            final Skill unarmed = this.creature.getFightingSkill();
            knowl = (float)unarmed.getKnowledge(0.0);
        }
        if (this.creature.getPrimWeapon().isBodyPartAttached()) {
            knowl += this.creature.getBonusForSpellEffect((byte)24) / 5.0f;
        }
        final Seat s = this.creature.getSeat();
        if (s != null) {
            knowl *= s.manouvre;
        }
        if (this.creature.isOnHostileHomeServer()) {
            knowl *= 0.525f;
        }
        return knowl;
    }
    
    private void sendSpecialMoves() {
        if (this.creature.combatRound > 3 && !this.creature.getPrimWeapon().isBodyPart()) {
            double fightskill = 0.0;
            try {
                fightskill = this.creature.getSkills().getSkill(this.creature.getPrimWeapon().getPrimarySkill()).getKnowledge(0.0);
                if (fightskill > 19.0) {
                    CombatHandler.specialmoves = SpecialMove.getMovesForWeaponSkillAndStance(this.creature, this.creature.getPrimWeapon(), (int)fightskill);
                    if (CombatHandler.specialmoves.length > 0) {
                        this.creature.getCommunicator().sendSpecialMove((short)(-1), "");
                        if (!this.creature.isAutofight()) {
                            for (int sx = 0; sx < CombatHandler.specialmoves.length; ++sx) {
                                this.creature.getCommunicator().sendSpecialMove((short)(197 + sx), CombatHandler.specialmoves[sx].getName());
                            }
                        }
                        this.selectSpecialMove();
                    }
                    else {
                        this.creature.getCommunicator().sendSpecialMove((short)(-1), "N/A");
                    }
                }
                else {
                    this.creature.getCommunicator().sendSpecialMove((short)(-1), "N/A");
                }
            }
            catch (NoSuchSkillException nss) {
                this.creature.getCommunicator().sendSpecialMove((short)(-1), "N/A");
            }
        }
        else {
            this.creature.getCommunicator().sendSpecialMove((short)(-1), "N/A");
        }
    }
    
    private void selectSpecialMove() {
        if (this.creature.isAutofight() && Server.rand.nextInt(3) == 0) {
            final int sm = Server.rand.nextInt(CombatHandler.specialmoves.length);
            try {
                final float chance = this.getChanceToHit(this.creature.opponent, this.creature.getPrimWeapon());
                if (chance > 50.0f && this.creature.getStatus().getStamina() > CombatHandler.specialmoves[sm].getStaminaCost()) {
                    this.creature.setAction(new Action(this.creature, -1L, this.creature.getWurmId(), (short)(197 + sm), this.creature.getPosX(), this.creature.getPosY(), this.creature.getPositionZ() + this.creature.getAltOffZ(), this.creature.getStatus().getRotation()));
                }
            }
            catch (Exception fe) {
                CombatHandler.logger.log(Level.WARNING, this.creature.getName() + " failed:" + fe.getMessage(), fe);
            }
        }
    }
    
    public void addBattleRatingPenalty(byte penalty) {
        if (this.battleratingPenalty == 0) {
            penalty = (byte)Math.max(penalty, 2);
        }
        this.battleratingPenalty = (byte)Math.min(5, this.battleratingPenalty + penalty);
    }
    
    byte getBattleratingPenalty() {
        return this.battleratingPenalty;
    }
    
    public void setCurrentStance(final int actNum, final byte aStance) {
        this.currentStance = aStance;
        if (actNum > 0) {
            this.creature.sendStance(this.currentStance);
        }
        else if (aStance == 15) {
            this.creature.sendStance(this.currentStance);
        }
        else if (aStance == 8) {
            this.creature.playAnimation("stancerebound", true);
        }
        else if (aStance == 9) {
            this.creature.getStatus().setStunned(3.0f, false);
            this.creature.playAnimation("stanceopen", false);
        }
        else if (aStance == 0) {
            this.creature.sendStance(this.currentStance);
        }
    }
    
    public void setCurrentStance(final byte aCurrentStance) {
        this.currentStance = aCurrentStance;
    }
    
    public byte getCurrentStance() {
        return this.currentStance;
    }
    
    public void sendStanceAnimation(final byte aStance, final boolean attack) {
        if (aStance == 8) {
            this.creature.sendToLoggers(this.creature.getName() + ": " + "stancerebound", (byte)2);
            this.creature.playAnimation("stancerebound", false);
        }
        else if (aStance == 9) {
            this.creature.getStatus().setStunned(3.0f, false);
            this.creature.playAnimation("stanceopen", false);
            this.creature.sendToLoggers(this.creature.getName() + ": " + "stanceopen", (byte)2);
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append("fight");
            if (attack) {
                if (CombatHandler.attString.equals("hit")) {
                    sb.append("_strike");
                }
                else {
                    sb.append("_" + CombatHandler.attString);
                }
            }
            if (!this.creature.isUnique() || this.creature.getHugeMoveCounter() == 2) {
                this.creature.playAnimation(sb.toString(), !attack);
            }
            this.creature.sendToLoggers(this.creature.getName() + ": " + sb.toString(), (byte)2);
        }
    }
    
    public static final String getStanceDescription(final byte currentStance) {
        final StringBuilder sb = new StringBuilder();
        if (isHigh(currentStance)) {
            sb.append("higher ");
        }
        else if (isLow(currentStance)) {
            sb.append("lower ");
        }
        else {
            sb.append("mid ");
        }
        if (isLeft(currentStance)) {
            sb.append("left ");
        }
        else if (isRight(currentStance)) {
            sb.append("right ");
        }
        else {
            sb.append("center ");
        }
        return sb.toString();
    }
    
    private void addToList(final List<ActionEntry> list, @Nullable final Item weapon, final short number, final Creature opponent, final float mycr, final float oppcr, final float primweaponskill) {
        float movechance;
        if (this.creature.isPlayer()) {
            movechance = getMoveChance(this.creature, weapon, this.currentStance, Actions.actionEntrys[number], mycr, oppcr, primweaponskill);
        }
        else {
            movechance = getMoveChance(this.creature, weapon, this.currentStance, Actions.actionEntrys[number], mycr, oppcr, primweaponskill);
        }
        if (movechance > 0.0f) {
            list.add(new ActionEntry(number, (int)movechance + "%, " + Actions.actionEntrys[number].getActionString(), "attack"));
        }
    }
    
    public static final int getAttackSkillCap(final short action) {
        switch (action) {
            case 303: {
                return 0;
            }
            case 291: {
                return 3;
            }
            case 309: {
                return 2;
            }
            case 300: {
                return 15;
            }
            case 288: {
                return 13;
            }
            case 306: {
                return 12;
            }
            case 297: {
                return 9;
            }
            case 294: {
                return 7;
            }
            case 312: {
                return 5;
            }
            default: {
                return 0;
            }
        }
    }
    
    private List<ActionEntry> getHighAttacks(@Nullable final Item weapon, final boolean auto, final Creature opponent, final float mycr, final float oppcr, final float primweaponskill) {
        final LinkedList<ActionEntry> tempList = new LinkedList<ActionEntry>();
        if (primweaponskill > getAttackSkillCap((short)300)) {
            this.addToList(tempList, weapon, (short)300, opponent, mycr, oppcr, primweaponskill);
        }
        if (primweaponskill > getAttackSkillCap((short)288)) {
            this.addToList(tempList, weapon, (short)288, opponent, mycr, oppcr, primweaponskill);
        }
        if (primweaponskill > getAttackSkillCap((short)306)) {
            this.addToList(tempList, weapon, (short)306, opponent, mycr, oppcr, primweaponskill);
        }
        if (!auto && tempList.size() > 0) {
            tempList.addFirst(new ActionEntry((short)(-tempList.size()), "High", "high"));
        }
        return tempList;
    }
    
    private List<ActionEntry> getMidAttacks(@Nullable final Item weapon, final boolean auto, final Creature opponent, final float mycr, final float oppcr, final float primweaponskill) {
        final LinkedList<ActionEntry> tempList = new LinkedList<ActionEntry>();
        this.addToList(tempList, weapon, (short)303, opponent, mycr, oppcr, primweaponskill);
        if (primweaponskill > getAttackSkillCap((short)291)) {
            this.addToList(tempList, weapon, (short)291, opponent, mycr, oppcr, primweaponskill);
        }
        if (primweaponskill > getAttackSkillCap((short)309)) {
            this.addToList(tempList, weapon, (short)309, opponent, mycr, oppcr, primweaponskill);
        }
        if (!auto && tempList.size() > 0) {
            tempList.addFirst(new ActionEntry((short)(-tempList.size()), "Mid", "Mid"));
        }
        return tempList;
    }
    
    private List<ActionEntry> getLowAttacks(@Nullable final Item weapon, final boolean auto, final Creature opponent, final float mycr, final float oppcr, final float primweaponskill) {
        final LinkedList<ActionEntry> tempList = new LinkedList<ActionEntry>();
        if (primweaponskill > getAttackSkillCap((short)297)) {
            this.addToList(tempList, weapon, (short)297, opponent, mycr, oppcr, primweaponskill);
        }
        if (primweaponskill > getAttackSkillCap((short)294)) {
            this.addToList(tempList, weapon, (short)294, opponent, mycr, oppcr, primweaponskill);
        }
        if (primweaponskill > getAttackSkillCap((short)312)) {
            this.addToList(tempList, weapon, (short)312, opponent, mycr, oppcr, primweaponskill);
        }
        if (!auto && tempList.size() > 0) {
            tempList.addFirst(new ActionEntry((short)(-tempList.size()), "Low", "Low"));
        }
        return tempList;
    }
    
    public static final float getMoveChance(final Creature performer, @Nullable final Item weapon, final int stance, final ActionEntry entry, final float mycr, final float oppcr, final float primweaponskill) {
        final float basechance = 100.0f - oppcr * 2.0f + mycr + primweaponskill;
        float cost = 0.0f;
        if (isHigh(stance)) {
            if (entry.isAttackHigh()) {
                cost += 5.0f;
            }
            else if (entry.isAttackLow()) {
                cost += 10.0f;
            }
            else {
                cost += 3.0f;
            }
        }
        else if (isLow(stance)) {
            if (entry.isAttackHigh()) {
                cost += 10.0f;
            }
            else if (entry.isAttackLow()) {
                cost += 5.0f;
            }
            else {
                cost += 3.0f;
            }
        }
        else if (entry.isAttackHigh()) {
            cost += 5.0f;
        }
        else if (entry.isAttackLow()) {
            cost += 5.0f;
        }
        if (isRight(stance)) {
            if (entry.isAttackRight()) {
                cost += 3.0f;
            }
            else if (entry.isAttackLeft()) {
                cost += 10.0f;
            }
            else {
                cost += 3.0f;
            }
        }
        else if (isLeft(stance)) {
            if (entry.isAttackRight()) {
                cost += 10.0f;
            }
            else if (entry.isAttackLeft()) {
                cost += 3.0f;
            }
            else {
                cost += 3.0f;
            }
        }
        else if (entry.isAttackLeft()) {
            cost += 5.0f;
        }
        else if (entry.isAttackRight()) {
            cost += 5.0f;
        }
        else {
            cost += 10.0f;
        }
        if (entry.isAttackHigh() && !entry.isAttackLeft() && !entry.isAttackRight()) {
            cost += 3.0f;
        }
        else if (entry.isAttackLow() && !entry.isAttackLeft() && !entry.isAttackRight()) {
            cost += 3.0f;
        }
        cost *= (float)(1.0 - CombatHandler.manouvreMod);
        if (weapon != null) {
            cost += Weapon.getBaseSpeedForWeapon(weapon);
        }
        if (performer.fightlevel >= 2) {
            cost -= 10.0f;
        }
        return Math.min(100.0f, Math.max(0.0f, basechance - cost));
    }
    
    public static final boolean isHigh(final int stance) {
        return stance == 6 || stance == 1 || stance == 7;
    }
    
    public static final boolean isLow(final int stance) {
        return stance == 4 || stance == 3 || stance == 10 || stance == 8;
    }
    
    public static final boolean isLeft(final int stance) {
        return stance == 4 || stance == 5 || stance == 6;
    }
    
    public static final boolean isRight(final int stance) {
        return stance == 3 || stance == 2 || stance == 1 || stance == 11;
    }
    
    public static final boolean isCenter(final int stance) {
        return stance == 0 || stance == 9 || stance == 13 || stance == 14 || stance == 12;
    }
    
    public static final boolean isDefend(final int stance) {
        return stance == 13 || stance == 14 || stance == 12 || stance == 11;
    }
    
    public static boolean prerequisitesFail(final Creature creature, final Creature opponent, final boolean opportunity, final Item weapon) {
        return prerequisitesFail(creature, opponent, opportunity, weapon, false);
    }
    
    public static boolean prerequisitesFail(final Creature creature, final Creature opponent, final boolean opportunity, final Item weapon, final boolean ignoreWeapon) {
        if (opponent.isDead()) {
            creature.setTarget(-10L, true);
            return true;
        }
        if (opponent.equals(creature)) {
            if (!opportunity) {
                creature.getCommunicator().sendCombatAlertMessage("You cannot attack yourself.");
                creature.setOpponent(null);
            }
            return true;
        }
        if (!creature.isPlayer() && opponent.isPlayer() && creature.getHitched() != null && creature.getHitched().wurmid == opponent.getVehicle()) {
            creature.setOpponent(null);
            creature.setTarget(-10L, true);
            return true;
        }
        if (!opponent.isPlayer() && creature.isPlayer() && opponent.getHitched() != null && opponent.getHitched().wurmid == creature.getVehicle()) {
            opponent.setOpponent(null);
            opponent.setTarget(-10L, true);
            return true;
        }
        if (!ignoreWeapon && weapon == null) {
            if (!opportunity) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new MulticolorLineSegment("You have no weapon to attack ", (byte)0));
                segments.add(new CreatureLineSegment(opponent));
                segments.add(new MulticolorLineSegment(" with.", (byte)0));
                creature.getCommunicator().sendColoredMessageCombat(segments);
                creature.setOpponent(null);
            }
            return true;
        }
        if (opponent.isBridgeBlockingAttack(creature, false)) {
            return true;
        }
        if (!GeneralUtilities.mayAttackSameLevel(creature, opponent)) {
            if (creature.isOnSurface()) {
                final VolaTile t = Zones.getTileOrNull(creature.getTileX(), creature.getTileY(), creature.isOnSurface());
                if (t != null) {
                    creature.sendToLoggers("Fighting " + opponent.getName() + " my z=" + creature.getPositionZ() + " opponent z=" + opponent.getPositionZ() + " structure=" + t.getStructure() + " diff=" + Math.abs(creature.getStatus().getPositionZ() - opponent.getStatus().getPositionZ()) * 10.0f);
                    if (t.getStructure() != null) {
                        return true;
                    }
                }
            }
            if (opponent.isOnSurface()) {
                final VolaTile t = Zones.getTileOrNull(opponent.getTileX(), opponent.getTileY(), opponent.isOnSurface());
                if (t != null && t.getStructure() != null) {
                    return true;
                }
            }
        }
        final BlockingResult result = Blocking.getBlockerBetween(creature, opponent, 4);
        if (result != null) {
            boolean blocked = false;
            for (final Blocker b : result.getBlockerArray()) {
                if (!b.isDoor()) {
                    blocked = true;
                }
                if (!b.canBeOpenedBy(creature, false)) {
                    blocked = true;
                }
                if (!b.canBeOpenedBy(opponent, false)) {
                    blocked = true;
                }
                if (blocked) {
                    break;
                }
            }
            if (blocked) {
                creature.breakout();
                if (!opportunity) {
                    creature.getCommunicator().sendNormalServerMessage("The " + result.getFirstBlocker().getName() + " blocks your attempt.");
                    if (result.getFirstBlocker().isTile()) {
                        if (opponent.opponent == creature || opponent.getTarget() == creature) {
                            opponent.setTarget(-10L, true);
                        }
                        if (creature.getTarget() == opponent) {
                            creature.setTarget(-10L, true);
                        }
                    }
                }
                creature.setOpponent(null);
                creature.sendToLoggers("Blocker result when attacking " + opponent.getName() + " " + result.getFirstBlocker().getName(), (byte)2);
                return true;
            }
        }
        if (creature.isOnSurface() != opponent.isOnSurface()) {
            boolean fail = false;
            boolean transition = false;
            if (opponent.getCurrentTile().isTransition) {
                transition = true;
                if (Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(opponent.getTileX(), opponent.getTileY())))) {
                    fail = true;
                }
            }
            if (!fail && creature.getCurrentTile().isTransition) {
                transition = true;
                if (Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(creature.getTileX(), creature.getTileY())))) {
                    fail = true;
                }
            }
            if (!transition) {
                fail = true;
            }
            return fail;
        }
        return false;
    }
    
    private final AttackAction getAttackAction(final boolean isSecondary) {
        AttackAction attack = null;
        final List<AttackAction> list = isSecondary ? this.creature.getTemplate().getSecondaryAttacks() : this.creature.getTemplate().getPrimaryAttacks();
        final List<AttackAction> valid = new ArrayList<AttackAction>();
        for (final AttackAction act : list) {
            final UsedAttackData data = this.creature.getUsedAttackData(act);
            if (data == null) {
                valid.add(act);
            }
            else {
                if (data.getTime() > 0.0f || data.getRounds() > 0) {
                    continue;
                }
                valid.add(act);
            }
        }
        if (valid.size() > 0) {
            final int index = Server.rand.nextInt(valid.size());
            attack = valid.get(index);
        }
        return attack;
    }
    
    private boolean attack2(final Creature opponent, final int combatCounter, final boolean opportunity, final float actionCounter, final Action act) {
        final float delta = Math.max(0.0f, actionCounter - this.lastTimeStamp);
        final float updateTime = Math.abs(delta - this.lastAttackPollDelta);
        this.lastAttackPollDelta = updateTime;
        this.creature.updateAttacksUsed(updateTime);
        if (delta <= this.waitTime) {
            return false;
        }
        if (opportunity) {
            this.creature.opportunityAttackCounter = 2;
        }
        this.lastAttackPollDelta = 0.0f;
        this.waitTime = 0.5f;
        final AttackAction primaryAttack = this.getAttackAction(false);
        final AttackAction secondaryAttack = this.getAttackAction(true);
        final int[] tcmoves = this.creature.getCombatMoves();
        final boolean canDoSpecials = tcmoves != null && tcmoves.length > 0;
        this.creature.setSecondsToLogout(300);
        if (!opponent.isPlayer()) {
            this.creature.setSecondsToLogout(180);
        }
        boolean shouldDoSecondary = false;
        if (primaryAttack != null && secondaryAttack != null && this.creature.combatRound > 1) {
            shouldDoSecondary = (Server.rand.nextInt(5) == 0);
        }
        else if (primaryAttack == null && secondaryAttack != null) {
            shouldDoSecondary = true;
        }
        boolean doSpecialAttack = false;
        if (!this.creature.isPlayer()) {
            boolean changedStance = false;
            if (Server.rand.nextInt(10) == 0) {
                changedStance = this.checkStanceChange(this.creature, opponent);
            }
            if (canDoSpecials && !changedStance) {
                doSpecialAttack = ((primaryAttack == null && secondaryAttack == null) || Server.rand.nextInt(80) < 20);
            }
        }
        Item weapon = null;
        if ((shouldDoSecondary && secondaryAttack != null && secondaryAttack.isUsingWeapon()) || (!shouldDoSecondary && primaryAttack != null && primaryAttack.isUsingWeapon())) {
            weapon = this.creature.getPrimWeapon();
        }
        if (opportunity) {
            this.creature.opportunityAttackCounter = 2;
        }
        this.lastTimeStamp = actionCounter;
        if (prerequisitesFail(this.creature, opponent, opportunity, weapon, weapon == null)) {
            return true;
        }
        if (act != null && act.justTickedSecond()) {
            this.creature.getCommunicator().sendCombatStatus(getDistdiff(this.creature, opponent, shouldDoSecondary ? secondaryAttack : primaryAttack), this.getFootingModifier(weapon, opponent), this.currentStance);
        }
        if (this.isProne() || this.isOpen()) {
            return false;
        }
        boolean lDead = false;
        this.creature.opponentCounter = 30;
        if (actionCounter == 1.0f && !opportunity && this.creature.isMoving() && !opponent.isMoving() && opponent.target == this.creature.getWurmId()) {
            opponent.attackTarget();
            if (opponent.opponent == this.creature) {
                this.creature.sendToLoggers("Opponent strikes first", (byte)2);
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(opponent));
                segments.add(new MulticolorLineSegment(" strike ", (byte)0));
                segments.add(new CreatureLineSegment(this.creature));
                segments.add(new MulticolorLineSegment(" as " + this.creature.getHeSheItString() + " approaches!", (byte)0));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
                segments.get(1).setText(" strikes ");
                segments.get(1).setText(" as you approach. ");
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
                lDead = opponent.getCombatHandler().attack(this.creature, combatCounter, true, 2.0f, null);
            }
        }
        else if (opportunity && primaryAttack != null) {
            ++this.opportunityAttacks;
            this.creature.sendToLoggers("YOU OPPORTUNITY", (byte)2);
            opponent.sendToLoggers(this.creature.getName() + " OPPORTUNITY", (byte)2);
            if (opponent.spamMode()) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new MulticolorLineSegment("You open yourself to an attack from ", (byte)7));
                segments.add(new CreatureLineSegment(this.creature));
                segments.add(new MulticolorLineSegment(".", (byte)7));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
            }
            if (this.creature.spamMode()) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(opponent));
                segments.add(new MulticolorLineSegment(" opens " + opponent.getHimHerItString() + "self up to an easy attack.", (byte)3));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
            }
            lDead = this.attack(opponent, primaryAttack);
        }
        else if (!lDead && primaryAttack != null && !shouldDoSecondary && !doSpecialAttack) {
            final float time = this.getSpeed(primaryAttack, primaryAttack.isUsingWeapon() ? this.creature.getPrimWeapon() : null);
            this.creature.addToAttackUsed(primaryAttack, time, primaryAttack.getAttackValues().getRounds());
            lDead = this.attack(opponent, primaryAttack);
            this.waitTime = primaryAttack.getAttackValues().getWaitTime();
            if (this.creature.isPlayer() && act != null && act.justTickedSecond()) {
                this.checkStanceChange(this.creature, opponent);
            }
        }
        else if (!lDead && secondaryAttack != null && shouldDoSecondary && !doSpecialAttack) {
            final float time = this.getSpeed(secondaryAttack, secondaryAttack.isUsingWeapon() ? this.creature.getPrimWeapon(false) : null);
            this.creature.addToAttackUsed(secondaryAttack, time, secondaryAttack.getAttackValues().getRounds());
            lDead = this.attack(opponent, secondaryAttack);
            this.waitTime = secondaryAttack.getAttackValues().getWaitTime();
            if (this.creature.isPlayer() && act != null && act.justTickedSecond()) {
                this.checkStanceChange(this.creature, opponent);
            }
        }
        else if (!lDead && !this.creature.isPlayer() && this.creature.getTarget() != null && doSpecialAttack) {
            final int[] cmoves = this.creature.getCombatMoves();
            if (cmoves.length > 0) {
                for (final int lCmove : cmoves) {
                    final CombatMove c = CombatMove.getCombatMove(lCmove);
                    if (Server.rand.nextFloat() < c.getRarity() && this.creature.getHugeMoveCounter() == 0) {
                        this.creature.sendToLoggers("YOU COMBAT MOVE", (byte)2);
                        opponent.sendToLoggers(this.creature.getName() + " COMBAT MOVE", (byte)2);
                        this.creature.setHugeMoveCounter(2 + Server.rand.nextInt(4));
                        c.perform(this.creature);
                        this.waitTime = 2.0f;
                        break;
                    }
                }
            }
        }
        return lDead;
    }
    
    public void resetSecAttacks() {
        if (this.secattacks != null) {
            this.secattacks.clear();
        }
    }
    
    public boolean attack(final Creature opponent, final int combatCounter, final boolean opportunity, final float actionCounter, final Action act) {
        opponent.addAttacker(this.creature);
        if (actionCounter == 1.0f) {
            this.lastTimeStamp = actionCounter;
        }
        if (Features.Feature.CREATURE_COMBAT_CHANGES.isEnabled() && this.creature.getTemplate().isUsingNewAttacks()) {
            return this.attack2(opponent, combatCounter, opportunity, actionCounter, act);
        }
        final float delta = Math.max(0.0f, actionCounter - this.lastTimeStamp);
        if (delta < 0.1) {
            return false;
        }
        if (opportunity) {
            this.creature.opportunityAttackCounter = 2;
        }
        this.lastTimeStamp = actionCounter;
        Item weapon = this.creature.getPrimWeapon();
        this.creature.setSecondsToLogout(300);
        if (!opponent.isPlayer()) {
            this.creature.setSecondsToLogout(180);
        }
        if (prerequisitesFail(this.creature, opponent, opportunity, weapon)) {
            return true;
        }
        if (act != null && act.justTickedSecond()) {
            this.creature.getCommunicator().sendCombatStatus(getDistdiff(weapon, this.creature, opponent), this.getFootingModifier(weapon, opponent), this.currentStance);
        }
        boolean lDead = false;
        if (this.isProne() || this.isOpen()) {
            return false;
        }
        this.creature.opponentCounter = 30;
        if (actionCounter == 1.0f && !opportunity && this.creature.isMoving() && !opponent.isMoving() && opponent.target == this.creature.getWurmId()) {
            opponent.attackTarget();
            if (opponent.opponent == this.creature) {
                this.creature.sendToLoggers("Opponent strikes first", (byte)2);
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(opponent));
                segments.add(new MulticolorLineSegment(" strike ", (byte)0));
                segments.add(new CreatureLineSegment(this.creature));
                segments.add(new MulticolorLineSegment(" as " + this.creature.getHeSheItString() + " approaches!", (byte)0));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
                segments.get(1).setText(" strikes ");
                segments.get(1).setText(" as you approach. ");
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
                lDead = opponent.getCombatHandler().attack(this.creature, combatCounter, true, 2.0f, null);
            }
        }
        else if (opportunity) {
            ++this.opportunityAttacks;
            this.creature.sendToLoggers("YOU OPPORTUNITY", (byte)2);
            opponent.sendToLoggers(this.creature.getName() + " OPPORTUNITY", (byte)2);
            if (opponent.spamMode()) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new MulticolorLineSegment("You open yourself to an attack from ", (byte)7));
                segments.add(new CreatureLineSegment(this.creature));
                segments.add(new MulticolorLineSegment(".", (byte)7));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
            }
            if (this.creature.spamMode()) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(opponent));
                segments.add(new MulticolorLineSegment(" opens " + opponent.getHimHerItString() + "self up to an easy attack.", (byte)3));
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
            }
            if (Server.rand.nextInt(3) == 0) {
                final Item[] secweapons = this.creature.getSecondaryWeapons();
                if (secweapons.length > 0) {
                    weapon = secweapons[Server.rand.nextInt(secweapons.length)];
                }
            }
            lDead = this.attack(opponent, weapon, false);
        }
        else {
            boolean performedAttack = false;
            if (!lDead && this.creature.combatRound > 1) {
                final Item[] secondaryWeapons;
                final Item[] secweapons2 = secondaryWeapons = this.creature.getSecondaryWeapons();
                for (final Item lSecweapon : secondaryWeapons) {
                    if (this.creature.opponent != null) {
                        if (this.secattacks == null) {
                            this.secattacks = new HashSet<Item>();
                        }
                        if (!this.secattacks.contains(lSecweapon) && ((lSecweapon.getTemplateId() != 12 && lSecweapon.getTemplateId() != 17) || (this.creature.getHugeMoveCounter() == 0 && Server.rand.nextBoolean()))) {
                            final float time = this.getSpeed(lSecweapon);
                            final float timer = this.creature.addToWeaponUsed(lSecweapon, delta);
                            final boolean shouldAttack = timer > time;
                            if (!lDead && this.creature.combatRound % 2 == 1 && shouldAttack) {
                                this.creature.deductFromWeaponUsed(lSecweapon, time);
                                this.creature.sendToLoggers("YOU SECONDARY " + lSecweapon.getName(), (byte)2);
                                opponent.sendToLoggers(this.creature.getName() + " SECONDARY " + lSecweapon.getName() + "(" + lSecweapon.getWurmId() + ")", (byte)2);
                                this.creature.setHugeMoveCounter(2 + Server.rand.nextInt(4));
                                lDead = this.attack(opponent, lSecweapon, true);
                                performedAttack = true;
                                this.secattacks.add(lSecweapon);
                            }
                        }
                    }
                }
            }
            final float time2 = this.getSpeed(weapon);
            final float timer2 = this.creature.addToWeaponUsed(weapon, delta);
            final boolean shouldAttack2 = timer2 > time2;
            if (!lDead && shouldAttack2) {
                this.creature.deductFromWeaponUsed(weapon, time2);
                this.creature.sendToLoggers("YOU PRIMARY " + weapon.getName(), (byte)2);
                opponent.sendToLoggers(this.creature.getName() + " PRIMARY " + weapon.getName(), (byte)2);
                lDead = this.attack(opponent, weapon, false);
                performedAttack = true;
                if (this.creature.isPlayer() && act != null && act.justTickedSecond()) {
                    this.checkStanceChange(this.creature, opponent);
                }
            }
            else if (!performedAttack && !lDead && !this.creature.isPlayer() && this.creature.getTarget() != null && this.creature.getLayer() == opponent.getLayer() && !this.checkStanceChange(this.creature, opponent)) {
                final int[] cmoves = this.creature.getCombatMoves();
                if (cmoves.length > 0) {
                    for (final int lCmove : cmoves) {
                        final CombatMove c = CombatMove.getCombatMove(lCmove);
                        if (Server.rand.nextFloat() < c.getRarity() && this.creature.getHugeMoveCounter() == 0) {
                            this.creature.sendToLoggers("YOU COMBAT MOVE", (byte)2);
                            opponent.sendToLoggers(this.creature.getName() + " COMBAT MOVE", (byte)2);
                            this.creature.setHugeMoveCounter(2 + Server.rand.nextInt(4));
                            c.perform(this.creature);
                            break;
                        }
                    }
                }
            }
        }
        return lDead;
    }
    
    public void clearRound() {
        this.opportunityAttacks = 0;
        this.receivedWeaponSkill = false;
        this.receivedSecWeaponSkill = false;
        this.receivedFStyleSkill = false;
        this.receivedShieldSkill = false;
        this.usedShieldThisRound = 0;
        if (this.lastShieldBashed > 0) {
            --this.lastShieldBashed;
        }
        if (this.secattacks != null) {
            this.secattacks.clear();
        }
        this.turned = false;
        if (this.battleratingPenalty > 0) {
            this.battleratingPenalty = (byte)Math.max(0, this.battleratingPenalty - 2);
            if (this.battleratingPenalty == 0 && this.creature.isPlayer()) {
                this.creature.getCommunicator().sendCombatNormalMessage("You concentrate better again.");
            }
        }
        if (this.creature.isFighting()) {
            final Creature creature = this.creature;
            ++creature.combatRound;
            if (!this.creature.opponent.isDead()) {
                this.calcAttacks(true);
            }
            else {
                this.moveStack = null;
            }
            this.creature.setStealth(false);
        }
    }
    
    public static final boolean isStanceParrying(final byte defenderStance, final byte attackerStance) {
        if (attackerStance == 8 || attackerStance == 9) {
            return true;
        }
        if (defenderStance == 8 || defenderStance == 9) {
            return false;
        }
        if (defenderStance == 11) {
            return attackerStance == 3 || attackerStance == 4 || attackerStance == 10;
        }
        if (defenderStance == 12) {
            return attackerStance == 1 || attackerStance == 6 || attackerStance == 7;
        }
        if (defenderStance == 14) {
            return attackerStance == 5 || attackerStance == 6 || attackerStance == 4;
        }
        return defenderStance == 13 && (attackerStance == 2 || attackerStance == 1 || attackerStance == 3);
    }
    
    public static final boolean isStanceOpposing(final byte defenderStance, final byte attackerStance) {
        if (attackerStance == 8 || attackerStance == 9) {
            return true;
        }
        if (defenderStance == 8 || defenderStance == 9) {
            return false;
        }
        if (defenderStance == 1) {
            return attackerStance == 6;
        }
        if (defenderStance == 6) {
            return attackerStance == 1;
        }
        if (defenderStance == 4) {
            return attackerStance == 3;
        }
        if (defenderStance == 3) {
            return attackerStance == 4;
        }
        if (defenderStance == 5) {
            return attackerStance == 2;
        }
        if (defenderStance == 2) {
            return attackerStance == 5;
        }
        if (defenderStance == 7) {
            return attackerStance == 7;
        }
        if (defenderStance == 0) {
            return attackerStance == 0;
        }
        return defenderStance == 10 && attackerStance == 10;
    }
    
    private byte getWoundPos(final byte aStance, final Creature aCreature) throws Exception {
        return aCreature.getBody().getRandomWoundPos(aStance);
    }
    
    private static void resetFlags(final Creature opponent) {
        CombatHandler.hit = false;
        CombatHandler.miss = false;
        CombatHandler.crit = false;
        CombatHandler.aiming = false;
        CombatHandler.dead = false;
        CombatHandler.chanceToHit = 0.0f;
        CombatHandler.pos = 0;
        CombatHandler.attCheck = 0.0;
        CombatHandler.attBonus = 0.0;
        CombatHandler.defCheck = 0.0;
        CombatHandler.defBonus = 0.0;
        CombatHandler.defShield = null;
        CombatHandler.defenderSkills = opponent.getSkills();
        CombatHandler.defParryWeapon = null;
        CombatHandler.defLeftWeapon = null;
        CombatHandler.defPrimWeaponSkill = null;
        CombatHandler.type = 0;
        CombatHandler.attString = "";
        CombatHandler.damage = 0.0;
        CombatHandler.justOpen = false;
    }
    
    public float getSpeed(final AttackAction act, final Item weapon) {
        float timeMod = 0.5f;
        if (this.currentStrength == 0) {
            timeMod = 1.5f;
        }
        if (act.isUsingWeapon() && weapon != null) {
            float calcspeed = this.getWeaponSpeed(act, weapon);
            calcspeed += timeMod;
            if (weapon.getSpellSpeedBonus() != 0.0f) {
                calcspeed -= (float)(0.5 * (weapon.getSpellSpeedBonus() / 100.0f));
            }
            else if (!weapon.isArtifact() && this.creature.getBonusForSpellEffect((byte)39) > 0.0f) {
                calcspeed -= 0.5f;
            }
            if (weapon.isTwoHanded() && this.currentStrength == 3) {
                calcspeed *= 0.9f;
            }
            if (!Features.Feature.METALLIC_ITEMS.isEnabled() && weapon.getMaterial() == 57) {
                calcspeed *= 0.9f;
            }
            if (this.creature.getStatus().getStamina() < 2000) {
                ++calcspeed;
            }
            calcspeed *= (float)(this.creature.getMovementScheme().getWebArmourMod() * -4.0);
            if (this.creature.hasSpellEffect((byte)66)) {
                calcspeed *= 2.0f;
            }
            return Math.max(3.0f, calcspeed);
        }
        float calcspeed = this.getWeaponSpeed(act, null);
        calcspeed += timeMod;
        if (this.creature.getStatus().getStamina() < 2000) {
            ++calcspeed;
        }
        calcspeed *= (float)(this.creature.getMovementScheme().getWebArmourMod() * -4.0);
        if (this.creature.hasSpellEffect((byte)66)) {
            calcspeed *= 2.0f;
        }
        return Math.max(3.0f, calcspeed);
    }
    
    public float getSpeed(final Item weapon) {
        float timeMod = 0.5f;
        if (this.currentStrength == 0) {
            timeMod = 1.5f;
        }
        float calcspeed = this.getWeaponSpeed(weapon);
        calcspeed += timeMod;
        if (weapon.getSpellSpeedBonus() != 0.0f) {
            calcspeed -= (float)(0.5 * (weapon.getSpellSpeedBonus() / 100.0f));
        }
        else if (!weapon.isArtifact() && this.creature.getBonusForSpellEffect((byte)39) > 0.0f) {
            final float maxBonus = calcspeed * 0.1f;
            final float percentBonus = this.creature.getBonusForSpellEffect((byte)39) / 100.0f;
            calcspeed -= maxBonus * percentBonus;
        }
        if (weapon.isTwoHanded() && this.currentStrength == 3) {
            calcspeed *= 0.9f;
        }
        if (!Features.Feature.METALLIC_ITEMS.isEnabled() && weapon.getMaterial() == 57) {
            calcspeed *= 0.9f;
        }
        if (this.creature.getStatus().getStamina() < 2000) {
            ++calcspeed;
        }
        final float waMult = (float)(this.creature.getMovementScheme().getWebArmourMod() * -2.0);
        calcspeed *= 1.0f + waMult;
        if (this.creature.hasSpellEffect((byte)66)) {
            calcspeed *= 2.0f;
        }
        return Math.max(3.0f, calcspeed);
    }
    
    public boolean isOpen() {
        return this.currentStance == 9;
    }
    
    public boolean isProne() {
        return this.currentStance == 8;
    }
    
    private boolean attack(final Creature opponent, final AttackAction attackAction) {
        resetFlags(opponent);
        if (!(opponent instanceof Player) || !opponent.hasLink()) {
            if (!this.turned) {
                if (opponent.getTarget() == null || opponent.getTarget() == this.creature) {
                    opponent.turnTowardsCreature(this.creature);
                }
                this.turned = true;
            }
            boolean switchOpp = false;
            if (!opponent.isFighting() && (this.creature.isPlayer() || this.creature.isDominated())) {
                switchOpp = true;
            }
            opponent.setTarget(this.creature.getWurmId(), switchOpp);
        }
        this.creature.getStatus().modifyStamina((int)(-80.0f * (1.0f + this.currentStrength * 0.5f)));
        this.addToSkills = true;
        Item weapon = null;
        weapon = this.creature.getPrimWeapon(!attackAction.isUsingWeapon());
        CombatHandler.chanceToHit = this.getChanceToHit(opponent, weapon);
        CombatHandler.type = attackAction.getAttackValues().getDamageType();
        final float percent = this.checkShield(opponent, weapon);
        if (percent > 50.0f) {
            CombatHandler.chanceToHit = 0.0f;
        }
        else if (percent > 0.0f) {
            CombatHandler.chanceToHit *= 1.0f - percent / 100.0f;
        }
        float parrPercent = -1.0f;
        if ((opponent.getFightStyle() != 1 || Server.rand.nextInt(3) == 0) && CombatHandler.chanceToHit > 0.0f) {
            parrPercent = this.checkDefenderParry(opponent, weapon);
            if (parrPercent > 60.0f) {
                CombatHandler.chanceToHit = 0.0f;
            }
            else if (parrPercent > 0.0f) {
                CombatHandler.chanceToHit *= 1.0f - parrPercent / 200.0f;
            }
        }
        CombatHandler.pos = 2;
        try {
            CombatHandler.pos = this.getWoundPos(this.currentStance, opponent);
        }
        catch (Exception ex) {
            CombatHandler.logger.log(Level.WARNING, this.creature.getName() + " " + ex.getMessage(), ex);
        }
        CombatHandler.attCheck = Server.rand.nextFloat() * 100.0f * (1.0 + this.creature.getVisionMod());
        final String combatDetails = " CHANCE:" + CombatHandler.chanceToHit + ", roll=" + CombatHandler.attCheck;
        if (this.creature.spamMode() && Servers.isThisATestServer()) {
            this.creature.getCommunicator().sendCombatSafeMessage(combatDetails);
        }
        this.creature.sendToLoggers("YOU" + combatDetails, (byte)2);
        opponent.sendToLoggers(this.creature.getName() + combatDetails, (byte)2);
        if (CombatHandler.attCheck < CombatHandler.chanceToHit) {
            if (opponent.isPlayer() && !weapon.isArtifact()) {
                float critChance = attackAction.getAttackValues().getCriticalChance();
                if (isAtSoftSpot(opponent.getCombatHandler().getCurrentStance(), this.getCurrentStance())) {
                    critChance += 0.05f;
                }
                if (Server.rand.nextFloat() < critChance) {
                    CombatHandler.crit = true;
                }
            }
        }
        else {
            CombatHandler.miss = true;
        }
        if (!CombatHandler.miss && !CombatHandler.crit) {
            boolean keepGoing = true;
            CombatHandler.defCheck = Server.rand.nextFloat() * 100.0f * opponent.getCombatHandler().getDodgeMod();
            CombatHandler.defCheck *= opponent.getStatus().getDodgeTypeModifier();
            if (opponent.getMovePenalty() != 0) {
                CombatHandler.defCheck *= 1.0f + opponent.getMovePenalty() / 10.0f;
            }
            CombatHandler.defCheck *= 1.0 - opponent.getMovementScheme().armourMod.getModifier();
            if (CombatHandler.defCheck < opponent.getBodyControl() * ItemBonus.getDodgeBonus(opponent) / 3.0) {
                if (opponent.getStatus().getDodgeTypeModifier() * 100.0f < opponent.getBodyControl() / 3.0) {
                    CombatHandler.logger.log(Level.WARNING, opponent.getName() + " is impossible to hit except for crits: " + opponent.getCombatHandler().getDodgeMod() * 100.0 + " is always less than " + opponent.getBodyControl());
                }
                this.sendDodgeMessage(opponent);
                keepGoing = false;
                final String dodgeDetails = "Dodge=" + CombatHandler.defCheck + "<" + opponent.getBodyControl() / 3.0 + " dodgemod=" + opponent.getCombatHandler().getDodgeMod() + " dodgeType=" + opponent.getStatus().getDodgeTypeModifier() + " dodgeMovePenalty=" + opponent.getMovePenalty() + " armour=" + opponent.getMovementScheme().armourMod.getModifier();
                if (this.creature.spamMode() && Servers.isThisATestServer()) {
                    this.creature.getCommunicator().sendCombatSafeMessage(dodgeDetails);
                }
                this.creature.sendToLoggers(dodgeDetails, (byte)4);
                checkIfHitVehicle(this.creature, opponent);
            }
            if (keepGoing) {
                CombatHandler.hit = true;
            }
        }
        if (CombatHandler.hit || CombatHandler.crit) {
            this.creature.sendToLoggers("YOU DAMAGE " + weapon.getName(), (byte)2);
            opponent.sendToLoggers(this.creature.getName() + " DAMAGE " + weapon.getName(), (byte)2);
            CombatHandler.dead = this.setDamage(opponent, weapon, CombatHandler.damage, CombatHandler.pos, CombatHandler.type);
        }
        if (CombatHandler.dead) {
            this.setKillEffects(this.creature, opponent);
        }
        if (CombatHandler.miss) {
            if (this.creature.spamMode() && (CombatHandler.chanceToHit > 0.0f || (percent > 0.0f && parrPercent > 0.0f))) {
                this.creature.getCommunicator().sendCombatNormalMessage("You miss with the " + weapon.getName() + ".");
                this.creature.sendToLoggers("YOU MISS " + weapon.getName(), (byte)2);
                opponent.sendToLoggers(this.creature.getName() + " MISS " + weapon.getName(), (byte)2);
            }
            if (!this.creature.isUnique() && CombatHandler.attCheck - CombatHandler.chanceToHit > 50.0 && Server.rand.nextInt(10) == 0) {
                CombatHandler.justOpen = true;
                this.setCurrentStance(-1, (byte)9);
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(this.creature));
                segments.add(new MulticolorLineSegment(" makes a bad move and is an easy target!.", (byte)0));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
                segments.get(1).setText(" make a bad move, making you an easy target.");
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
                this.creature.getCurrentTile().checkOpportunityAttacks(this.creature);
                opponent.getCurrentTile().checkOpportunityAttacks(this.creature);
            }
            else if (Server.rand.nextInt(10) == 0) {
                checkIfHitVehicle(this.creature, opponent);
            }
        }
        this.addToSkills = false;
        this.getDamage(this.creature, attackAction, opponent);
        CombatHandler.attString = attackAction.getAttackIdentifier().getAnimationString();
        this.sendStanceAnimation(this.currentStance, true);
        return CombatHandler.dead;
    }
    
    private boolean attack(final Creature opponent, final Item weapon, final boolean secondaryWeapon) {
        if (weapon.isWeaponBow()) {
            return false;
        }
        resetFlags(opponent);
        if (!(opponent instanceof Player) || !opponent.hasLink()) {
            if (!this.turned) {
                if (opponent.getTarget() == null || opponent.getTarget() == this.creature) {
                    opponent.turnTowardsCreature(this.creature);
                }
                this.turned = true;
            }
            boolean switchOpp = false;
            if (!opponent.isFighting() && (this.creature.isPlayer() || this.creature.isDominated())) {
                switchOpp = true;
            }
            opponent.setTarget(this.creature.getWurmId(), switchOpp);
        }
        this.creature.getStatus().modifyStamina((int)(-weapon.getWeightGrams() / 10.0f * (1.0f + this.currentStrength * 0.5f)));
        this.addToSkills = true;
        CombatHandler.chanceToHit = this.getChanceToHit(opponent, weapon);
        this.getType(weapon, false);
        this.getDamage(this.creature, weapon, opponent);
        setAttString(this.creature, weapon, CombatHandler.type);
        this.sendStanceAnimation(this.currentStance, true);
        final float percent = this.checkShield(opponent, weapon);
        if (percent > 50.0f) {
            CombatHandler.chanceToHit = 0.0f;
        }
        else if (percent > 0.0f) {
            CombatHandler.chanceToHit *= 1.0f - percent / 100.0f;
        }
        float parrPercent = -1.0f;
        if ((opponent.getFightStyle() != 1 || Server.rand.nextInt(3) == 0) && CombatHandler.chanceToHit > 0.0f) {
            parrPercent = this.checkDefenderParry(opponent, weapon);
            if (parrPercent > 60.0f) {
                CombatHandler.chanceToHit = 0.0f;
            }
            else if (parrPercent > 0.0f) {
                CombatHandler.chanceToHit *= 1.0f - parrPercent / 200.0f;
            }
        }
        CombatHandler.pos = 2;
        try {
            CombatHandler.pos = this.getWoundPos(this.currentStance, opponent);
        }
        catch (Exception ex) {
            CombatHandler.logger.log(Level.WARNING, this.creature.getName() + " " + ex.getMessage(), ex);
        }
        CombatHandler.attCheck = Server.rand.nextFloat() * 100.0f * (1.0 + this.creature.getVisionMod());
        final String combatDetails = " CHANCE:" + CombatHandler.chanceToHit + ", roll=" + CombatHandler.attCheck;
        if (this.creature.spamMode() && Servers.isThisATestServer()) {
            this.creature.getCommunicator().sendCombatSafeMessage(combatDetails);
        }
        this.creature.sendToLoggers("YOU" + combatDetails, (byte)2);
        opponent.sendToLoggers(this.creature.getName() + combatDetails, (byte)2);
        if (CombatHandler.attCheck < CombatHandler.chanceToHit) {
            if (opponent.isPlayer()) {
                float critChance = Weapon.getCritChanceForWeapon(weapon);
                if (isAtSoftSpot(opponent.getCombatHandler().getCurrentStance(), this.getCurrentStance())) {
                    critChance += 0.05f;
                }
                if (!weapon.isArtifact() && Server.rand.nextFloat() < critChance) {
                    CombatHandler.crit = true;
                }
            }
        }
        else {
            CombatHandler.miss = true;
        }
        if (!CombatHandler.miss && !CombatHandler.crit) {
            boolean keepGoing = true;
            CombatHandler.defCheck = Server.rand.nextFloat() * 100.0f * opponent.getCombatHandler().getDodgeMod();
            CombatHandler.defCheck *= opponent.getStatus().getDodgeTypeModifier();
            if (opponent.getMovePenalty() != 0) {
                CombatHandler.defCheck *= 1.0f + opponent.getMovePenalty() / 10.0f;
            }
            CombatHandler.defCheck *= 1.0 - opponent.getMovementScheme().armourMod.getModifier();
            if (CombatHandler.defCheck < opponent.getBodyControl() / 3.0) {
                if (opponent.getStatus().getDodgeTypeModifier() * 100.0f < opponent.getBodyControl() / 3.0) {
                    CombatHandler.logger.log(Level.WARNING, opponent.getName() + " is impossible to hit except for crits: " + opponent.getCombatHandler().getDodgeMod() * 100.0 + " is always less than " + opponent.getBodyControl());
                }
                this.sendDodgeMessage(opponent);
                keepGoing = false;
                final String dodgeDetails = "Dodge=" + CombatHandler.defCheck + "<" + opponent.getBodyControl() / 3.0 + " dodgemod=" + opponent.getCombatHandler().getDodgeMod() + " dodgeType=" + opponent.getStatus().getDodgeTypeModifier() + " dodgeMovePenalty=" + opponent.getMovePenalty() + " armour=" + opponent.getMovementScheme().armourMod.getModifier();
                if (this.creature.spamMode() && Servers.isThisATestServer()) {
                    this.creature.getCommunicator().sendCombatSafeMessage(dodgeDetails);
                }
                this.creature.sendToLoggers(dodgeDetails, (byte)4);
                checkIfHitVehicle(this.creature, opponent);
            }
            if (keepGoing) {
                CombatHandler.hit = true;
            }
        }
        if (CombatHandler.hit || CombatHandler.crit) {
            this.creature.sendToLoggers("YOU DAMAGE " + weapon.getName(), (byte)2);
            opponent.sendToLoggers(this.creature.getName() + " DAMAGE " + weapon.getName(), (byte)2);
            CombatHandler.dead = this.setDamage(opponent, weapon, CombatHandler.damage, CombatHandler.pos, CombatHandler.type);
        }
        if (CombatHandler.dead) {
            this.setKillEffects(this.creature, opponent);
        }
        if (CombatHandler.miss) {
            if (this.creature.spamMode() && (CombatHandler.chanceToHit > 0.0f || (percent > 0.0f && parrPercent > 0.0f))) {
                this.creature.getCommunicator().sendCombatNormalMessage("You miss with the " + weapon.getName() + ".");
                this.creature.sendToLoggers("YOU MISS " + weapon.getName(), (byte)2);
                opponent.sendToLoggers(this.creature.getName() + " MISS " + weapon.getName(), (byte)2);
            }
            if (!this.creature.isUnique() && CombatHandler.attCheck - CombatHandler.chanceToHit > 50.0 && Server.rand.nextInt(10) == 0) {
                CombatHandler.justOpen = true;
                this.setCurrentStance(-1, (byte)9);
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(this.creature));
                segments.add(new MulticolorLineSegment(" makes a bad move and is an easy target!.", (byte)0));
                opponent.getCommunicator().sendColoredMessageCombat(segments);
                segments.get(1).setText(" make a bad move, making you an easy target.");
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
                this.creature.getCurrentTile().checkOpportunityAttacks(this.creature);
                opponent.getCurrentTile().checkOpportunityAttacks(this.creature);
            }
            else if (Server.rand.nextInt(10) == 0) {
                checkIfHitVehicle(this.creature, opponent);
            }
        }
        this.addToSkills = false;
        return CombatHandler.dead;
    }
    
    private static final void checkIfHitVehicle(final Creature creature, final Creature opponent) {
        if (creature.isBreakFence() && opponent.getVehicle() > -10L) {
            final Vehicle vehic = Vehicles.getVehicleForId(opponent.getVehicle());
            if (vehic != null && !vehic.creature) {
                try {
                    final Item i = Items.getItem(opponent.getVehicle());
                    Server.getInstance().broadCastAction(creature.getNameWithGenus() + " hits the " + i.getName() + " with huge force!", creature, 10, true);
                    i.setDamage(i.getDamage() + (float)(CombatHandler.damage / 300000.0));
                }
                catch (NoSuchItemException ex) {}
            }
        }
    }
    
    public static void setAttString(final Creature _creature, final Item _weapon, final byte _type) {
        CombatHandler.attString = CombatEngine.getAttackString(_creature, _weapon, _type);
    }
    
    public static void setAttString(final String string) {
        CombatHandler.attString = string;
    }
    
    public boolean setDamage(final Creature defender, final Item attWeapon, final double ddamage, final byte position, byte _type) {
        float armourMod = defender.getArmourMod();
        final float poisdam = 0.0f;
        if (attWeapon.getSpellVenomBonus() > 0.0f) {
            _type = 5;
        }
        if (attWeapon.enchantment == 90) {
            _type = 10;
        }
        else if (attWeapon.enchantment == 92) {
            _type = 8;
        }
        else if (attWeapon.enchantment == 91) {
            _type = 4;
        }
        float infection = 0.0f;
        if (attWeapon.getSpellExtraDamageBonus() > 0.0f) {
            final float bloodthirstPower = attWeapon.getSpellExtraDamageBonus();
            if (Server.rand.nextFloat() * 100000.0f <= bloodthirstPower) {
                _type = 6;
                infection = bloodthirstPower / 1000.0f;
            }
        }
        boolean metalArmour = false;
        Item armour = null;
        float bounceWoundPower = 0.0f;
        float evasionChance = ArmourTemplate.calculateGlanceRate(defender.getArmourType(), armour, _type, armourMod);
        Label_0657: {
            if (armourMod != 1.0f && !defender.isVehicle()) {
                if (!defender.isKingdomGuard()) {
                    break Label_0657;
                }
            }
            try {
                final byte bodyPosition = ArmourTemplate.getArmourPosition(position);
                armour = defender.getArmour(bodyPosition);
                if (!defender.isKingdomGuard()) {
                    armourMod = ArmourTemplate.calculateDR(armour, _type);
                }
                else {
                    armourMod *= ArmourTemplate.calculateDR(armour, _type);
                }
                defender.sendToLoggers("YOU ARMORMOD " + armourMod, (byte)2);
                this.creature.sendToLoggers(defender.getName() + " ARMORMOD " + armourMod, (byte)2);
                if (defender.isPlayer() || defender.isHorse()) {
                    armour.setDamage(armour.getDamage() + Math.max(0.01f, Math.min(1.0f, (float)(ddamage * Weapon.getMaterialArmourDamageBonus(attWeapon.getMaterial()) * ArmourTemplate.getArmourDamageModFor(armour, _type) / 1200000.0) * armour.getDamageModifier())));
                }
                CombatEngine.checkEnchantDestruction(attWeapon, armour, defender);
                if (armour.isMetal()) {
                    metalArmour = true;
                }
                if (!defender.isPlayer()) {
                    evasionChance = ArmourTemplate.calculateCreatureGlanceRate(_type, armour);
                }
                else {
                    evasionChance = ArmourTemplate.calculateGlanceRate(null, armour, _type, armourMod);
                }
                evasionChance *= 1.0f + ItemBonus.getGlanceBonusFor(armour.getArmourType(), _type, attWeapon, defender);
            }
            catch (NoArmourException ex) {}
            catch (NoSpaceException nsp) {
                CombatHandler.logger.log(Level.WARNING, defender.getName() + " no armour space on loc " + position);
            }
            if ((armour == null || (armour.getArmourType() != null && armour.getArmourType().getLimitFactor() >= 0.0f)) && defender.getBonusForSpellEffect((byte)22) > 0.0f) {
                if (!CombatEngine.isEye(position) || defender.isUnique()) {
                    float omod = 100.0f;
                    float minmod = 0.6f;
                    if (!defender.isPlayer()) {
                        omod = 300.0f;
                        minmod = 0.7f;
                    }
                    else if (defender.getBonusForSpellEffect((byte)22) > 70.0f) {
                        bounceWoundPower = defender.getBonusForSpellEffect((byte)22);
                    }
                    if (armourMod >= 1.0f) {
                        armourMod = 0.3f + (float)(1.0 - Server.getBuffedQualityEffect(defender.getBonusForSpellEffect((byte)22) / omod)) * minmod;
                        evasionChance = (float)Server.getBuffedQualityEffect(defender.getBonusForSpellEffect((byte)22) / 100.0f) / 3.0f;
                    }
                    else {
                        armourMod = Math.min(armourMod, 0.3f + (float)(1.0 - Server.getBuffedQualityEffect(defender.getBonusForSpellEffect((byte)22) / omod)) * minmod);
                    }
                }
            }
            else if (defender.isReborn()) {
                armourMod = (float)(1.0 - Server.getBuffedQualityEffect(defender.getStrengthSkill() / 100.0));
            }
        }
        if (defender.isUnique()) {
            evasionChance = 0.5f;
        }
        if (!attWeapon.isBodyPartAttached() && this.creature.isPlayer()) {
            final boolean rust = defender.hasSpellEffect((byte)70);
            if (rust) {
                this.creature.getCommunicator().sendAlertServerMessage("Your " + attWeapon.getName() + " takes excessive damage from " + defender.getNameWithGenus() + ".");
            }
            final float mod = rust ? 5.0f : 1.0f;
            attWeapon.setDamage(attWeapon.getDamage() + Math.min(1.0f, (float)(ddamage * armourMod / 1000000.0)) * attWeapon.getDamageModifier() * mod);
        }
        double defdamage = ddamage * ItemBonus.getDamReductionBonusFor((armour != null) ? armour.getArmourType() : defender.getArmourType(), _type, attWeapon, defender);
        if (attWeapon.getSpellVenomBonus() > 0.0f) {
            defdamage *= 0.8f + 0.2f * (attWeapon.getSpellVenomBonus() / 100.0f);
        }
        if (defender.isPlayer()) {
            if (((Player)defender).getAlcohol() > 50.0f) {
                defdamage *= 0.5;
            }
            if (defender.fightlevel >= 5) {
                defdamage *= 0.5;
            }
        }
        if (defender.hasTrait(2)) {
            defdamage *= 0.8999999761581421;
        }
        final float demiseBonus = EnchantUtil.getDemiseBonus(attWeapon, defender);
        if (demiseBonus > 0.0f) {
            defdamage *= 1.0f + demiseBonus;
        }
        if (this.creature.hasSpellEffect((byte)67) && !attWeapon.isArtifact()) {
            CombatHandler.crit = true;
        }
        if (CombatHandler.crit && !defender.isUnique()) {
            armourMod *= 1.5f;
        }
        if (defender.getTemplate().isTowerBasher() && (this.creature.isSpiritGuard() || this.creature.isKingdomGuard())) {
            final float mod2 = 1.0f / defender.getArmourMod();
            defdamage = Math.max((500 + Server.rand.nextInt(1000)) * mod2, defdamage);
        }
        if (Server.rand.nextFloat() < evasionChance) {
            if (this.creature.spamMode()) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new MulticolorLineSegment("Your attack glances off ", (byte)0));
                segments.add(new CreatureLineSegment(defender));
                segments.add(new MulticolorLineSegment("'s armour.", (byte)0));
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
            }
            if (defender.spamMode()) {
                defender.getCommunicator().sendCombatNormalMessage("The attack to the " + defender.getBody().getWoundLocationString(CombatHandler.pos) + " glances off your armour.");
            }
            this.creature.sendToLoggers(defender.getName() + " GLANCE", (byte)2);
            defender.sendToLoggers("YOU GLANCE", (byte)2);
        }
        else if (defdamage * armourMod >= 500.0) {
            if (this.creature.hasSpellEffect((byte)67) && !attWeapon.isArtifact()) {
                this.creature.removeTrueStrike();
            }
            if (attWeapon != null && !attWeapon.isBodyPartRemoved() && !attWeapon.isWeaponBow()) {
                try {
                    int primweaponskill = 10052;
                    if (!attWeapon.isBodyPartAttached()) {
                        primweaponskill = attWeapon.getPrimarySkill();
                    }
                    try {
                        final Skill pwsk = this.creature.getSkills().getSkill(primweaponskill);
                        pwsk.skillCheck(pwsk.getKnowledge(), attWeapon, 0.0, defender.isNoSkillFor(this.creature), (float)defdamage * armourMod / 1000.0f);
                    }
                    catch (NoSuchSkillException nss1) {
                        this.creature.getSkills().learn(primweaponskill, 1.0f);
                    }
                }
                catch (NoSuchSkillException ex2) {}
            }
            if (Servers.isThisATestServer()) {
                final String message = String.format("Base Damage: %.1f, Armour DR: %.2f%%, Final Damage: %.1f. Critical: %s", defdamage, (1.0f - armourMod) * 100.0f, defdamage * armourMod, CombatHandler.crit);
                if (this.creature.spamMode()) {
                    this.creature.getCommunicator().sendCombatSafeMessage(message);
                }
                if (defender.spamMode()) {
                    defender.getCommunicator().sendCombatAlertMessage(message);
                }
            }
            this.creature.sendToLoggers(defender.getName() + " DAMAGED " + defdamage * armourMod + " crit=" + CombatHandler.crit, (byte)2);
            defender.sendToLoggers("YOU DAMAGED " + defdamage * armourMod + " crit=" + CombatHandler.crit, (byte)2);
            final Battle battle = defender.getBattle();
            CombatHandler.dead = false;
            final float champMod = defender.isChampion() ? 0.4f : 1.0f;
            if (armour != null && armour.getSpellPainShare() > 0.0f) {
                bounceWoundPower = armour.getSpellPainShare();
                final int rarityModifier = Math.max(1, armour.getRarity() * 5);
                final SpellEffect speff = armour.getSpellEffect((byte)17);
                if (speff != null && Server.rand.nextInt(Math.max(2, (int)(rarityModifier * speff.power * 80.0f))) == 0) {
                    speff.setPower(speff.getPower() - 1.0f);
                    if (speff.getPower() <= 0.0f) {
                        final ItemSpellEffects speffs = armour.getSpellEffects();
                        if (speffs != null) {
                            speffs.removeSpellEffect(speff.type);
                        }
                    }
                }
            }
            if (defender.isUnique() && this.creature.isUnique() && defender.getStatus().damage > 10000) {
                defender.setTarget(-10L, true);
                this.creature.setTarget(-10L, true);
                defender.setOpponent(null);
                this.creature.setOpponent(null);
                try {
                    defender.checkMove();
                }
                catch (Exception ex3) {}
                try {
                    this.creature.checkMove();
                }
                catch (Exception ex4) {}
            }
            if (defender.isSparring(this.creature)) {
                if (defender.getStatus().damage + defdamage * armourMod * 2.0 > 65535.0) {
                    defender.setTarget(-10L, true);
                    this.creature.setTarget(-10L, true);
                    defender.setOpponent(null);
                    this.creature.setOpponent(null);
                    this.creature.getCommunicator().sendCombatSafeMessage("You win against " + defender.getName() + "! Congratulations!");
                    defender.getCommunicator().sendCombatNormalMessage("You lose against " + this.creature.getName() + " who stops just before finishing you off!");
                    Server.getInstance().broadCastAction(this.creature.getName() + " defeats " + defender.getName() + " while sparring!", this.creature, defender, 10);
                    this.creature.getCommunicator().sendCombatOptions(CombatHandler.NO_COMBAT_OPTIONS, (short)0);
                    this.creature.getCommunicator().sendSpecialMove((short)(-1), "N/A");
                    this.creature.achievement(39);
                    if (!Servers.localServer.PVPSERVER) {
                        this.creature.achievement(8);
                    }
                    final Item weapon = this.creature.getPrimWeapon();
                    if (weapon != null) {
                        if (weapon.isWeaponBow()) {
                            this.creature.achievement(11);
                        }
                        else if (weapon.isWeaponSword()) {
                            this.creature.achievement(14);
                        }
                        else if (weapon.isWeaponCrush()) {
                            this.creature.achievement(17);
                        }
                        else if (weapon.isWeaponAxe()) {
                            this.creature.achievement(20);
                        }
                        else if (weapon.isWeaponKnife()) {
                            this.creature.achievement(25);
                        }
                        if (weapon.getTemplateId() == 314) {
                            this.creature.achievement(27);
                        }
                        else if (weapon.getTemplateId() == 567) {
                            this.creature.achievement(29);
                        }
                        else if (weapon.getTemplateId() == 20) {
                            this.creature.achievement(30);
                        }
                    }
                    return true;
                }
                if (bounceWoundPower > 0.0f && defdamage * bounceWoundPower * champMod / 300.0 > 500.0 && this.creature.getStatus().damage + defdamage * bounceWoundPower * champMod / 300.0 > 65535.0) {
                    defender.setTarget(-10L, true);
                    this.creature.setTarget(-10L, true);
                    defender.setOpponent(null);
                    this.creature.setOpponent(null);
                    defender.getCommunicator().sendCombatSafeMessage("You win against " + this.creature.getName() + "! Congratulations!");
                    this.creature.getCommunicator().sendCombatNormalMessage("You lose against " + defender.getName() + " whose armour enchantment almost finished you off!");
                    Server.getInstance().broadCastAction(defender.getName() + " defeats " + this.creature.getName() + " while sparring!", defender, this.creature, 10);
                    this.creature.getCommunicator().sendCombatOptions(CombatHandler.NO_COMBAT_OPTIONS, (short)0);
                    this.creature.getCommunicator().sendSpecialMove((short)(-1), "N/A");
                    this.creature.achievement(39);
                    if (!Servers.localServer.PVPSERVER) {
                        this.creature.achievement(8);
                    }
                    final Item weapon = this.creature.getPrimWeapon();
                    if (weapon != null) {
                        if (weapon.isWeaponBow()) {
                            this.creature.achievement(11);
                        }
                        else if (weapon.isWeaponSword()) {
                            this.creature.achievement(14);
                        }
                        else if (weapon.isWeaponCrush()) {
                            this.creature.achievement(17);
                        }
                        else if (weapon.isWeaponAxe()) {
                            this.creature.achievement(20);
                        }
                        else if (weapon.isWeaponKnife()) {
                            this.creature.achievement(25);
                        }
                        if (weapon.getTemplateId() == 314) {
                            this.creature.achievement(27);
                        }
                        else if (weapon.getTemplateId() == 567) {
                            this.creature.achievement(29);
                        }
                        else if (weapon.getTemplateId() == 20) {
                            this.creature.achievement(30);
                        }
                    }
                    return true;
                }
            }
            if (defender.getStaminaSkill().getKnowledge() < 2.0) {
                defender.die(false, "Combat Stam Check Fail");
                this.creature.achievement(223);
                CombatHandler.dead = true;
            }
            else if (attWeapon.getWeaponSpellDamageBonus() > 0.0f) {
                defdamage += defdamage * attWeapon.getWeaponSpellDamageBonus() / 500.0;
                CombatHandler.dead = CombatEngine.addWound(this.creature, defender, _type, position, defdamage, armourMod, CombatHandler.attString, battle, Server.rand.nextInt((int)Math.max(1.0f, attWeapon.getWeaponSpellDamageBonus())), poisdam, false, false, false, false);
                if (attWeapon.isWeaponCrush() && attWeapon.getWeightGrams() > 4000 && armour != null && armour.getTemplateId() == 286) {
                    defender.achievement(49);
                }
            }
            else {
                final int dmgBefore = defender.getStatus().damage;
                CombatHandler.dead = CombatEngine.addWound(this.creature, defender, _type, position, defdamage, armourMod, CombatHandler.attString, battle, infection, poisdam, false, false, false, false);
                final float lifeTransferPower = Math.max(attWeapon.getSpellLifeTransferModifier(), attWeapon.getSpellEssenceDrainModifier() / 3.0f);
                if (lifeTransferPower > 0.0f && dmgBefore != defender.getStatus().damage && this.creature.getBody() != null && this.creature.getBody().getWounds() != null) {
                    final Wound[] w = this.creature.getBody().getWounds().getWounds();
                    if (w.length > 0) {
                        float mod3 = 500.0f;
                        if (this.creature.isChampion()) {
                            mod3 = 1000.0f;
                        }
                        else if (this.creature.getCultist() != null && this.creature.getCultist().healsFaster()) {
                            mod3 = 250.0f;
                        }
                        double toHeal = defdamage * lifeTransferPower / mod3;
                        final double resistance = SpellResist.getSpellResistance(this.creature, 409);
                        toHeal *= resistance;
                        Wound targetWound = w[0];
                        for (final Wound wound : w) {
                            if (wound.getSeverity() > targetWound.getSeverity()) {
                                targetWound = wound;
                            }
                        }
                        SpellResist.addSpellResistance(this.creature, 409, Math.min(targetWound.getSeverity(), toHeal));
                        targetWound.modifySeverity(-(int)toHeal);
                    }
                }
            }
            if (this.creature.isPlayer() != defender.isPlayer() && defdamage > 10000.0 && defender.fightlevel > 0) {
                --defender.fightlevel;
                defender.getCommunicator().sendCombatNormalMessage("You lose some focus.");
                if (defender.isPlayer()) {
                    defender.getCommunicator().sendFocusLevel(defender.getWurmId());
                }
            }
            if (!CombatHandler.dead && attWeapon.getSpellDamageBonus() > 0.0f && (attWeapon.getSpellDamageBonus() / 300.0f * defdamage > 500.0 || CombatHandler.crit)) {
                CombatHandler.dead = defender.addWoundOfType(this.creature, (byte)4, position, false, armourMod, false, attWeapon.getSpellDamageBonus() / 300.0f * defdamage, 0.0f, 0.0f, true, true);
            }
            if (!CombatHandler.dead && attWeapon.getSpellFrostDamageBonus() > 0.0f && (attWeapon.getSpellFrostDamageBonus() / 300.0f * defdamage > 500.0 || CombatHandler.crit)) {
                CombatHandler.dead = defender.addWoundOfType(this.creature, (byte)8, position, false, armourMod, false, attWeapon.getSpellFrostDamageBonus() / 300.0f * defdamage, 0.0f, 0.0f, true, true);
            }
            if (!CombatHandler.dead && attWeapon.getSpellEssenceDrainModifier() > 0.0f && (attWeapon.getSpellEssenceDrainModifier() / 1000.0f * defdamage > 500.0 || CombatHandler.crit)) {
                CombatHandler.dead = defender.addWoundOfType(this.creature, (byte)9, position, false, armourMod, false, attWeapon.getSpellEssenceDrainModifier() / 1000.0f * defdamage, 0.0f, 0.0f, true, true);
            }
            if (!CombatHandler.dead && Weapon.getMaterialExtraWoundMod(attWeapon.getMaterial()) > 0.0f) {
                final float extraDmg = Weapon.getMaterialExtraWoundMod(attWeapon.getMaterial());
                if (extraDmg * defdamage > 500.0 || CombatHandler.crit) {
                    CombatHandler.dead = defender.addWoundOfType(this.creature, Weapon.getMaterialExtraWoundType(attWeapon.getMaterial()), position, false, armourMod, false, extraDmg * defdamage, 0.0f, 0.0f, false, true);
                }
            }
            if (armour != null || bounceWoundPower > 0.0f) {
                if (bounceWoundPower > 0.0f) {
                    if (this.creature.isUnique()) {
                        if (armour != null) {
                            defender.getCommunicator().sendCombatNormalMessage(this.creature.getNameWithGenus() + " ignores the effects of the " + armour.getName() + ".");
                        }
                    }
                    else if (defdamage * bounceWoundPower * champMod / 300.0 > 500.0) {
                        CombatEngine.addBounceWound(defender, this.creature, _type, position, defdamage * bounceWoundPower * champMod / 300.0, armourMod, 0.0f, 0.0f, false, true);
                    }
                }
                else if (armour != null && armour.getSpellSlowdown() > 0.0f) {
                    if (this.creature.getMovementScheme().setWebArmourMod(true, armour.getSpellSlowdown())) {
                        this.creature.setWebArmourModTime(armour.getSpellSlowdown() / 10.0f);
                        this.creature.getCommunicator().sendCombatAlertMessage("Dark stripes spread along your " + attWeapon.getName() + " from " + defender.getNamePossessive() + " armour. You feel drained.");
                    }
                    final int rm = Math.max(1, armour.getRarity() * 5);
                    final SpellEffect speff = armour.getSpellEffect((byte)46);
                    if (speff != null && Server.rand.nextInt(Math.max(2, (int)(rm * speff.power * 80.0f))) == 0) {
                        speff.setPower(speff.getPower() - 1.0f);
                        if (speff.getPower() <= 0.0f) {
                            final ItemSpellEffects speffs = armour.getSpellEffects();
                            if (speffs != null) {
                                speffs.removeSpellEffect(speff.type);
                            }
                        }
                    }
                }
            }
            if (!Players.getInstance().isOverKilling(this.creature.getWurmId(), defender.getWurmId()) && attWeapon.getSpellExtraDamageBonus() > 0.0f) {
                if (defender.isPlayer() && !defender.isNewbie()) {
                    final SpellEffect speff2 = attWeapon.getSpellEffect((byte)45);
                    float mod4 = 1.0f;
                    if (defdamage * armourMod * champMod < 5000.0) {
                        mod4 = (float)(defdamage * armourMod * champMod / 5000.0);
                    }
                    if (speff2 != null) {
                        speff2.setPower(Math.min(10000.0f, speff2.power + (CombatHandler.dead ? 20.0f : (2.0f * mod4))));
                    }
                }
                else if (!defender.isPlayer() && !defender.isGuard() && CombatHandler.dead) {
                    final SpellEffect speff2 = attWeapon.getSpellEffect((byte)45);
                    float mod4 = 1.0f;
                    if (speff2.getPower() > 5000.0f && !Servers.isThisAnEpicOrChallengeServer()) {
                        mod4 = Math.max(0.5f, 1.0f - (speff2.getPower() - 5000.0f) / 5000.0f);
                    }
                    if (speff2 != null) {
                        speff2.setPower(Math.min(10000.0f, speff2.power + defender.getBaseCombatRating() * mod4));
                    }
                }
            }
            if (CombatHandler.dead) {
                if (battle != null) {
                    battle.addCasualty(this.creature, defender);
                }
                if (defender.isSparring(this.creature) && defender.getStatus().damage + defdamage * armourMod * 2.0 > 65535.0) {
                    this.creature.achievement(39);
                    if (!Servers.localServer.PVPSERVER) {
                        this.creature.achievement(8);
                    }
                    final Item weapon = this.creature.getPrimWeapon();
                    if (weapon != null) {
                        if (weapon.isWeaponBow()) {
                            this.creature.achievement(11);
                        }
                        else if (weapon.isWeaponSword()) {
                            this.creature.achievement(14);
                        }
                        else if (weapon.isWeaponCrush()) {
                            this.creature.achievement(17);
                        }
                        else if (weapon.isWeaponAxe()) {
                            this.creature.achievement(20);
                        }
                        else if (weapon.isWeaponKnife()) {
                            this.creature.achievement(25);
                        }
                        if (weapon.getTemplateId() == 314) {
                            this.creature.achievement(27);
                        }
                        else if (weapon.getTemplateId() == 567) {
                            this.creature.achievement(29);
                        }
                        else if (weapon.getTemplateId() == 20) {
                            this.creature.achievement(30);
                        }
                    }
                    this.creature.getCommunicator().sendCombatSafeMessage("You accidentally slay " + defender.getName() + "! Congratulations!");
                    defender.getCommunicator().sendCombatNormalMessage("You lose against " + this.creature.getName() + " who unfortunately fails to stop just before finishing you off!");
                    Server.getInstance().broadCastAction(this.creature.getName() + " defeats and accidentally slays " + defender.getName() + " while sparring!", this.creature, defender, 10);
                }
                if (this.creature.isDuelling(defender)) {
                    this.creature.achievement(37);
                }
            }
            else if (defdamage > 30000.0 && Server.rand.nextInt(100000) < defdamage) {
                Skill defBodyControl = null;
                try {
                    defBodyControl = defender.getSkills().getSkill(104);
                }
                catch (NoSuchSkillException nss2) {
                    defBodyControl = defender.getSkills().learn(104, 1.0f);
                }
                if (defBodyControl.skillCheck(defdamage / 10000.0, defender.getCombatHandler().getFootingModifier(attWeapon, this.creature) * 10.0f, false, 10.0f, defender, this.creature) < 0.0) {
                    defender.getCombatHandler().setCurrentStance(-1, (byte)8);
                    defender.getStatus().setStunned((byte)Math.max(3.0, defdamage / 10000.0), false);
                    final ArrayList<MulticolorLineSegment> segments2 = new ArrayList<MulticolorLineSegment>();
                    segments2.add(new CreatureLineSegment(defender));
                    segments2.add(new MulticolorLineSegment(" is knocked senseless from the hit.", (byte)0));
                    this.creature.getCommunicator().sendColoredMessageCombat(segments2);
                    defender.getCommunicator().sendCombatNormalMessage("You are knocked senseless from the hit.");
                    segments2.clear();
                    segments2.add(new CreatureLineSegment(this.creature));
                    segments2.add(new MulticolorLineSegment(" knocks ", (byte)0));
                    segments2.add(new CreatureLineSegment(defender));
                    segments2.add(new MulticolorLineSegment(" senseless with " + this.creature.getHisHerItsString() + " hit!", (byte)0));
                    MessageServer.broadcastColoredAction(segments2, this.creature, defender, 5, true);
                }
            }
            final int numsound = Server.rand.nextInt(3);
            if (defdamage > 10000.0) {
                if (numsound == 0) {
                    SoundPlayer.playSound("sound.combat.fleshbone1", defender, 1.6f);
                }
                else if (numsound == 1) {
                    SoundPlayer.playSound("sound.combat.fleshbone2", defender, 1.6f);
                }
                else if (numsound == 2) {
                    SoundPlayer.playSound("sound.combat.fleshbone3", defender, 1.6f);
                }
            }
            else if (metalArmour) {
                if (numsound == 0) {
                    SoundPlayer.playSound("sound.combat.fleshmetal1", defender, 1.6f);
                }
                else if (numsound == 1) {
                    SoundPlayer.playSound("sound.combat.fleshmetal2", defender, 1.6f);
                }
                else if (numsound == 2) {
                    SoundPlayer.playSound("sound.combat.fleshmetal3", defender, 1.6f);
                }
            }
            else if (numsound == 0) {
                SoundPlayer.playSound("sound.combat.fleshhit1", defender, 1.6f);
            }
            else if (numsound == 1) {
                SoundPlayer.playSound("sound.combat.fleshhit2", defender, 1.6f);
            }
            else if (numsound == 2) {
                SoundPlayer.playSound("sound.combat.fleshhit3", defender, 1.6f);
            }
            SoundPlayer.playSound(defender.getHitSound(), defender, 1.6f);
        }
        else {
            if (CombatHandler.aiming || this.creature.spamMode()) {
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new CreatureLineSegment(defender));
                segments.add(new MulticolorLineSegment(" takes no real damage from the hit to the " + defender.getBody().getWoundLocationString(position) + ".", (byte)0));
                this.creature.getCommunicator().sendColoredMessageCombat(segments);
            }
            if (defender.spamMode()) {
                defender.getCommunicator().sendCombatNormalMessage("You take no real damage from the blow to the " + defender.getBody().getWoundLocationString(position) + ".");
            }
            this.creature.sendToLoggers(defender.getName() + " NO DAMAGE", (byte)2);
            defender.sendToLoggers("YOU TAKE NO DAMAGE", (byte)2);
        }
        return CombatHandler.dead;
    }
    
    private static void setDefenderWeaponSkill(final Item defPrimWeapon) {
        int skillnum = -10;
        if (defPrimWeapon != null) {
            if (defPrimWeapon.isBodyPart()) {
                try {
                    skillnum = 10052;
                    CombatHandler.defPrimWeaponSkill = CombatHandler.defenderSkills.getSkill(skillnum);
                }
                catch (NoSuchSkillException nss) {
                    if (skillnum != -10) {
                        CombatHandler.defPrimWeaponSkill = CombatHandler.defenderSkills.learn(skillnum, 1.0f);
                    }
                }
            }
            else {
                try {
                    skillnum = defPrimWeapon.getPrimarySkill();
                    CombatHandler.defPrimWeaponSkill = CombatHandler.defenderSkills.getSkill(skillnum);
                }
                catch (NoSuchSkillException nss) {
                    if (skillnum != -10) {
                        CombatHandler.defPrimWeaponSkill = CombatHandler.defenderSkills.learn(skillnum, 1.0f);
                    }
                }
            }
        }
    }
    
    private static float getWeaponParryBonus(final Item weapon) {
        if (weapon.isWeaponSword()) {
            return 2.0f;
        }
        return 1.0f;
    }
    
    private float checkDefenderParry(final Creature defender, final Item attWeapon) {
        CombatHandler.defCheck = 0.0;
        boolean parried = false;
        int parryTime = 200;
        if (defender.getFightStyle() == 2) {
            parryTime = 120;
        }
        else if (defender.getFightStyle() == 1) {
            parryTime = 360;
        }
        CombatHandler.parryBonus = getParryBonus(defender.getCombatHandler().currentStance, this.currentStance);
        if (defender.fightlevel > 0) {
            CombatHandler.parryBonus -= defender.fightlevel * 4 / 100.0f;
        }
        if (defender.getPrimWeapon() != null) {
            CombatHandler.parryBonus *= Weapon.getMaterialParryBonus(defender.getPrimWeapon().getMaterial());
        }
        parryTime *= (int)CombatHandler.parryBonus;
        if (WurmCalendar.currentTime > defender.lastParry + Server.rand.nextInt(parryTime)) {
            CombatHandler.defParryWeapon = defender.getPrimWeapon();
            if (Weapon.getWeaponParryPercent(CombatHandler.defParryWeapon) > 0.0f) {
                if (CombatHandler.defParryWeapon.isTwoHanded() && CombatHandler.defShield != null) {
                    CombatHandler.defParryWeapon = null;
                    parried = false;
                }
                else {
                    parried = true;
                }
            }
            else {
                CombatHandler.defParryWeapon = null;
            }
            if ((!parried || Server.rand.nextInt(3) == 0) && CombatHandler.defShield == null) {
                CombatHandler.defLeftWeapon = defender.getLefthandWeapon();
                if (CombatHandler.defLeftWeapon != CombatHandler.defParryWeapon) {
                    if (CombatHandler.defLeftWeapon != null && (CombatHandler.defLeftWeapon.getSizeZ() > defender.getSize() * 10 || Weapon.getWeaponParryPercent(CombatHandler.defLeftWeapon) <= 0.0f)) {
                        CombatHandler.defLeftWeapon = null;
                    }
                    if (CombatHandler.defLeftWeapon != null) {
                        if (CombatHandler.defParryWeapon != null && parried) {
                            if (CombatHandler.defLeftWeapon.getSizeZ() > CombatHandler.defParryWeapon.getSizeZ()) {
                                CombatHandler.defParryWeapon = CombatHandler.defLeftWeapon;
                            }
                        }
                        else {
                            CombatHandler.defParryWeapon = CombatHandler.defLeftWeapon;
                        }
                    }
                }
            }
            if (CombatHandler.defParryWeapon != null && Weapon.getWeaponParryPercent(CombatHandler.defParryWeapon) > Server.rand.nextFloat()) {
                CombatHandler.defCheck = -1.0;
                if (defender.getStatus().getStamina() >= 300) {
                    setDefenderWeaponSkill(CombatHandler.defParryWeapon);
                    if (CombatHandler.defPrimWeaponSkill != null && (!defender.isMoving() || CombatHandler.defPrimWeaponSkill.getRealKnowledge() > 40.0)) {
                        double pdiff = Math.max(1.0, (CombatHandler.attCheck - CombatHandler.defBonus + CombatHandler.defParryWeapon.getWeightGrams() / 100.0f) / getWeaponParryBonus(CombatHandler.defParryWeapon) * (1.0 - this.getParryMod()));
                        if (!defender.isPlayer()) {
                            pdiff *= defender.getStatus().getParryTypeModifier();
                        }
                        CombatHandler.defCheck = CombatHandler.defPrimWeaponSkill.skillCheck(pdiff * ItemBonus.getParryBonus(defender, CombatHandler.defParryWeapon), CombatHandler.defParryWeapon, 0.0, this.creature.isNoSkillFor(defender) || CombatHandler.defParryWeapon.isWeaponBow(), 1.0f, defender, this.creature);
                        defender.lastParry = WurmCalendar.currentTime;
                        defender.getStatus().modifyStamina(-300.0f);
                    }
                    if (CombatHandler.defCheck < 0.0 && Server.rand.nextInt(20) == 0 && CombatHandler.defLeftWeapon != null && !CombatHandler.defLeftWeapon.equals(CombatHandler.defParryWeapon)) {
                        setDefenderWeaponSkill(CombatHandler.defLeftWeapon);
                        if (!defender.isMoving() || CombatHandler.defPrimWeaponSkill.getRealKnowledge() > 40.0) {
                            double pdiff = Math.max(1.0, (CombatHandler.attCheck - CombatHandler.defBonus + CombatHandler.defLeftWeapon.getWeightGrams() / 100.0f) / getWeaponParryBonus(CombatHandler.defLeftWeapon) * this.getParryMod());
                            pdiff *= defender.getStatus().getParryTypeModifier();
                            CombatHandler.defCheck = CombatHandler.defPrimWeaponSkill.skillCheck(pdiff * ItemBonus.getParryBonus(defender, CombatHandler.defParryWeapon), CombatHandler.defLeftWeapon, 0.0, this.creature.isNoSkillFor(defender) || CombatHandler.defParryWeapon.isWeaponBow(), 1.0f, defender, this.creature);
                            defender.lastParry = WurmCalendar.currentTime;
                            defender.getStatus().modifyStamina(-300.0f);
                        }
                    }
                    if (CombatHandler.defCheck > 0.0) {
                        this.setParryEffects(defender, attWeapon, CombatHandler.defCheck);
                    }
                }
            }
        }
        return (float)CombatHandler.defCheck;
    }
    
    private void setParryEffects(final Creature defender, final Item attWeapon, final double parryEff) {
        defender.lastParry = WurmCalendar.currentTime;
        if (CombatHandler.aiming || this.creature.spamMode()) {
            final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
            segments.add(new CreatureLineSegment(defender));
            segments.add(new MulticolorLineSegment(" " + CombatEngine.getParryString(parryEff) + " parries with " + CombatHandler.defParryWeapon.getNameWithGenus() + ".", (byte)0));
            this.creature.getCommunicator().sendColoredMessageCombat(segments);
        }
        if (defender.spamMode()) {
            defender.getCommunicator().sendCombatNormalMessage("You " + CombatEngine.getParryString(parryEff) + " parry with your " + CombatHandler.defParryWeapon.getName() + ".");
        }
        if (!CombatHandler.defParryWeapon.isBodyPart() || CombatHandler.defParryWeapon.getAuxData() == 100) {
            float vulnerabilityModifier = 1.0f;
            if (defender.isPlayer()) {
                if (attWeapon.isMetal() && Weapon.isWeaponDamByMetal(CombatHandler.defParryWeapon)) {
                    vulnerabilityModifier = 4.0f;
                }
                if (CombatHandler.defParryWeapon.isWeaponSword()) {
                    CombatHandler.defParryWeapon.setDamage(CombatHandler.defParryWeapon.getDamage() + 1.0E-7f * (float)CombatHandler.damage * CombatHandler.defParryWeapon.getDamageModifier() * vulnerabilityModifier);
                }
                else {
                    CombatHandler.defParryWeapon.setDamage(CombatHandler.defParryWeapon.getDamage() + 2.0E-7f * (float)CombatHandler.damage * CombatHandler.defParryWeapon.getDamageModifier() * vulnerabilityModifier);
                }
            }
            if (this.creature.isPlayer()) {
                vulnerabilityModifier = 1.0f;
                if (CombatHandler.defParryWeapon.isMetal() && Weapon.isWeaponDamByMetal(attWeapon)) {
                    vulnerabilityModifier = 4.0f;
                }
                if (attWeapon.isBodyPartAttached()) {
                    attWeapon.setDamage(attWeapon.getDamage() + 1.0E-7f * (float)CombatHandler.damage * attWeapon.getDamageModifier() * vulnerabilityModifier);
                }
            }
        }
        this.creature.sendToLoggers(defender.getName() + " PARRY " + parryEff, (byte)2);
        defender.sendToLoggers("YOU PARRY " + parryEff, (byte)2);
        final String lSstring = getParrySound(Server.rand);
        SoundPlayer.playSound(lSstring, defender, 1.6f);
        CombatEngine.checkEnchantDestruction(attWeapon, CombatHandler.defParryWeapon, defender);
        defender.playAnimation("parry.weapon", false);
    }
    
    static String getParrySound(final Random aRandom) {
        final int x = aRandom.nextInt(3);
        String lSstring;
        if (x == 0) {
            lSstring = "sound.combat.parry2";
        }
        else if (x == 1) {
            lSstring = "sound.combat.parry3";
        }
        else {
            lSstring = "sound.combat.parry1";
        }
        return lSstring;
    }
    
    public void increaseUseShieldCounter() {
        ++this.usedShieldThisRound;
    }
    
    private float checkShield(final Creature defender, final Item weapon) {
        if (defender.getCombatHandler().usedShieldThisRound > 1) {
            return 0.0f;
        }
        CombatHandler.defShield = defender.getShield();
        CombatHandler.defCheck = 0.0;
        float blockPercent = 0.0f;
        if (CombatHandler.defShield != null) {
            final Item defweapon = defender.getPrimWeapon();
            if (defweapon != null && defweapon.isTwoHanded()) {
                return 0.0f;
            }
            final Item defSecondWeapon = defender.getLefthandWeapon();
            if (defSecondWeapon != null && defSecondWeapon.isTwoHanded()) {
                return 0.0f;
            }
            if (!CombatHandler.defShield.isArtifact()) {
                final CombatHandler combatHandler = defender.getCombatHandler();
                ++combatHandler.usedShieldThisRound;
            }
            if (VirtualZone.isCreatureShieldedVersusTarget(this.creature, defender)) {
                int skillnum = -10;
                Skill defShieldSkill = null;
                try {
                    skillnum = CombatHandler.defShield.getPrimarySkill();
                    defShieldSkill = CombatHandler.defenderSkills.getSkill(skillnum);
                }
                catch (NoSuchSkillException nss) {
                    if (skillnum != -10) {
                        defShieldSkill = CombatHandler.defenderSkills.learn(skillnum, 1.0f);
                    }
                }
                if (defShieldSkill != null) {
                    if (CombatHandler.pos == 9) {
                        blockPercent = 100.0f;
                        if (defender.spamMode() && Servers.isThisATestServer()) {
                            defender.getCommunicator().sendCombatNormalMessage("Blocking left underarm.");
                        }
                    }
                    else if ((defender.getStatus().getStamina() >= 300 || Server.rand.nextInt(10) == 0) && (!defender.isMoving() || defShieldSkill.getRealKnowledge() > 40.0)) {
                        final double shieldModifier = (CombatHandler.defShield.getSizeY() + CombatHandler.defShield.getSizeZ()) / 2.0f * (CombatHandler.defShield.getCurrentQualityLevel() / 100.0f);
                        final double diff = Math.max(1.0, CombatHandler.chanceToHit - shieldModifier) - CombatHandler.defBonus;
                        blockPercent = (float)defShieldSkill.skillCheck(diff, CombatHandler.defShield, CombatHandler.defShield.isArtifact() ? 50.0 : 0.0, this.creature.isNoSkillFor(defender) || defender.getCombatHandler().receivedShieldSkill, (float)(CombatHandler.damage / 1000.0), defender, this.creature);
                        defender.getCombatHandler().receivedShieldSkill = true;
                        if (defender.spamMode() && Servers.isThisATestServer()) {
                            defender.getCommunicator().sendCombatNormalMessage("Shield parrying difficulty=" + diff + " including defensive bonus " + CombatHandler.defBonus + " vs " + defShieldSkill.getKnowledge(CombatHandler.defShield, 0.0) + " " + defender.zoneBonus + ":" + defender.getMovePenalty() + " gave " + blockPercent + ">0");
                        }
                        defender.getStatus().modifyStamina((int)(-300.0f - CombatHandler.defShield.getWeightGrams() / 20.0f));
                    }
                    if (blockPercent > 0.0f) {
                        float damageMod;
                        if (!weapon.isBodyPart() && weapon.isWeaponCrush()) {
                            damageMod = 1.5E-5f;
                        }
                        else if (CombatHandler.type == 0) {
                            damageMod = 1.0E-6f;
                        }
                        else {
                            damageMod = 5.0E-6f;
                        }
                        if (defender.isPlayer()) {
                            CombatHandler.defShield.setDamage(CombatHandler.defShield.getDamage() + Math.max(0.01f, damageMod * (float)CombatHandler.damage * CombatHandler.defShield.getDamageModifier()));
                        }
                        this.sendShieldMessage(defender, weapon, blockPercent);
                    }
                }
            }
        }
        return blockPercent;
    }
    
    private void sendShieldMessage(final Creature defender, final Item weapon, final float blockPercent) {
        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
        segments.add(new CreatureLineSegment(defender));
        segments.add(new MulticolorLineSegment(" raises " + defender.getHisHerItsString() + " shield and parries your " + CombatHandler.attString + ".", (byte)0));
        if (CombatHandler.aiming || this.creature.spamMode()) {
            this.creature.getCommunicator().sendColoredMessageCombat(segments);
        }
        if (defender.spamMode()) {
            segments.get(1).setText(" raise your shield and parry against ");
            segments.add(new CreatureLineSegment(this.creature));
            segments.add(new MulticolorLineSegment("'s " + CombatHandler.attString + ".", (byte)0));
            defender.getCommunicator().sendColoredMessageCombat(segments);
        }
        if (CombatHandler.defShield.isWood()) {
            Methods.sendSound(defender, "sound.combat.shield.wood");
        }
        else {
            Methods.sendSound(defender, "sound.combat.shield.metal");
        }
        CombatEngine.checkEnchantDestruction(weapon, CombatHandler.defShield, defender);
        this.creature.sendToLoggers(defender.getName() + " SHIELD " + blockPercent, (byte)2);
        defender.sendToLoggers("You SHIELD " + blockPercent, (byte)2);
        defender.playAnimation("parry.shield", false);
    }
    
    private void sendDodgeMessage(final Creature defender) {
        final double power = (float)(defender.getBodyControl() / 3.0 - CombatHandler.defCheck);
        String sstring;
        if (power > 20.0) {
            sstring = "sound.combat.miss.heavy";
        }
        else if (power > 10.0) {
            sstring = "sound.combat.miss.med";
        }
        else {
            sstring = "sound.combat.miss.light";
        }
        SoundPlayer.playSound(sstring, this.creature, 1.6f);
        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
        segments.add(new CreatureLineSegment(defender));
        segments.add(new MulticolorLineSegment(" " + CombatEngine.getParryString(power) + " evades the blow to the " + defender.getBody().getWoundLocationString(CombatHandler.pos) + ".", (byte)0));
        if (CombatHandler.aiming || this.creature.spamMode()) {
            this.creature.getCommunicator().sendColoredMessageCombat(segments);
        }
        if (defender.spamMode()) {
            segments.get(1).setText(" " + CombatEngine.getParryString(power) + " evade the blow to the " + defender.getBody().getWoundLocationString(CombatHandler.pos) + ".");
            defender.getCommunicator().sendColoredMessageCombat(segments);
        }
        this.creature.sendToLoggers(defender.getName() + " EVADE", (byte)2);
        defender.sendToLoggers("You EVADE", (byte)2);
        defender.playAnimation("dodge", false);
    }
    
    private final double getDamage(final Creature _creature, final AttackAction attk, final Creature opponent) {
        Skill attStrengthSkill = null;
        try {
            attStrengthSkill = _creature.getSkills().getSkill(102);
        }
        catch (NoSuchSkillException nss) {
            attStrengthSkill = _creature.getSkills().learn(102, 1.0f);
            CombatHandler.logger.log(Level.WARNING, _creature.getName() + " had no strength. Weird.");
        }
        final Item weapon = _creature.getPrimWeapon(!attk.isUsingWeapon());
        if (!attk.isUsingWeapon()) {
            CombatHandler.damage = attk.getAttackValues().getBaseDamage() * 1000.0f * _creature.getStatus().getDamageTypeModifier();
            if (_creature.isPlayer()) {
                final Skill weaponLess = _creature.getWeaponLessFightingSkill();
                final double modifier = 1.0 + 2.0 * weaponLess.getKnowledge() / 100.0;
                CombatHandler.damage *= modifier;
            }
            if (CombatHandler.damage < 10000.0 && _creature.getBonusForSpellEffect((byte)24) > 0.0f) {
                if (_creature.isPlayer()) {
                    CombatHandler.damage += Server.getBuffedQualityEffect(_creature.getBonusForSpellEffect((byte)24) / 100.0f) * 15000.0;
                }
                else {
                    CombatHandler.damage += Server.getBuffedQualityEffect(_creature.getBonusForSpellEffect((byte)24) / 100.0f) * 5000.0;
                }
            }
            final float randomizer = (50.0f + Server.rand.nextFloat() * 50.0f) / 100.0f;
            CombatHandler.damage *= randomizer;
        }
        else {
            CombatHandler.damage = Weapon.getModifiedDamageForWeapon(weapon, attStrengthSkill, opponent.getTemplate().getTemplateId() == 116) * 1000.0;
            CombatHandler.damage += Server.getBuffedQualityEffect(weapon.getCurrentQualityLevel() / 100.0f) * Weapon.getBaseDamageForWeapon(weapon) * 2400.0;
            CombatHandler.damage *= Weapon.getMaterialDamageBonus(weapon.getMaterial());
            if (!opponent.isPlayer() && opponent.isHunter()) {
                CombatHandler.damage *= Weapon.getMaterialHunterDamageBonus(weapon.getMaterial());
            }
            CombatHandler.damage *= ItemBonus.getWeaponDamageIncreaseBonus(_creature, weapon);
            CombatHandler.damage *= 1.0f + weapon.getCurrentQualityLevel() / 100.0f * (weapon.getSpellExtraDamageBonus() / 30000.0f);
        }
        if (_creature.getEnemyPresense() > 1200 && opponent.isPlayer() && !weapon.isArtifact()) {
            CombatHandler.damage *= 1.149999976158142;
        }
        if (!weapon.isArtifact() && this.hasRodEffect && opponent.isPlayer()) {
            CombatHandler.damage *= 1.2000000476837158;
        }
        final Vehicle vehicle = Vehicles.getVehicleForId(opponent.getVehicle());
        boolean mildStack = false;
        if (weapon.isWeaponPolearm() && ((vehicle != null && vehicle.isCreature()) || (opponent.isRidden() && weapon.isWeaponPierce()))) {
            CombatHandler.damage *= 1.7000000476837158;
        }
        else if (weapon.isArtifact()) {
            mildStack = true;
        }
        else if (_creature.getCultist() != null && _creature.getCultist().doubleWarDamage()) {
            CombatHandler.damage *= 1.5;
            mildStack = true;
        }
        else if (_creature.getDeity() != null && _creature.getDeity().isWarrior() && _creature.getFaith() >= 40.0f && _creature.getFavor() >= 20.0f) {
            CombatHandler.damage *= 1.149999976158142;
            mildStack = true;
        }
        if (_creature.isPlayer()) {
            if ((_creature.getFightStyle() != 2 || attStrengthSkill.getRealKnowledge() < 20.0) && attStrengthSkill.getRealKnowledge() != 20.0) {
                CombatHandler.damage *= 1.0 + (attStrengthSkill.getRealKnowledge() - 20.0) / 200.0;
            }
            if (this.currentStrength == 0) {
                Skill fstyle = null;
                try {
                    fstyle = _creature.getSkills().getSkill(10054);
                }
                catch (NoSuchSkillException nss2) {
                    fstyle = _creature.getSkills().learn(10054, 1.0f);
                }
                if (fstyle.skillCheck(opponent.getBaseCombatRating() * 3.0f, 0.0, this.receivedFStyleSkill || opponent.isNoSkillFor(_creature), 10.0f, _creature, opponent) > 0.0) {
                    this.receivedFStyleSkill = true;
                    CombatHandler.damage *= 0.800000011920929;
                }
                else {
                    CombatHandler.damage *= 0.5;
                }
            }
            if (_creature.getStatus().getStamina() > 2000 && this.currentStrength >= 1 && !this.receivedFStyleSkill) {
                int num = 10053;
                if (this.currentStrength == 1) {
                    num = 10055;
                }
                Skill fstyle2 = null;
                try {
                    fstyle2 = _creature.getSkills().getSkill(num);
                }
                catch (NoSuchSkillException nss3) {
                    fstyle2 = _creature.getSkills().learn(num, 1.0f);
                }
                if (fstyle2.skillCheck(opponent.getBaseCombatRating() * 3.0f, 0.0, this.receivedFStyleSkill || opponent.isNoSkillFor(_creature), 10.0f, _creature, opponent) > 0.0) {
                    this.receivedFStyleSkill = true;
                    if (this.currentStrength > 1) {
                        CombatHandler.damage *= 1.0 + Server.getModifiedFloatEffect(fstyle2.getRealKnowledge() / 100.0) / (mildStack ? 8.0f : 4.0f);
                    }
                }
            }
            float knowl = 1.0f;
            try {
                final Skill wSkill = _creature.getSkills().getSkill(weapon.getPrimarySkill());
                knowl = (float)wSkill.getRealKnowledge();
            }
            catch (NoSuchSkillException ex) {}
            if (knowl < 50.0f) {
                CombatHandler.damage = 0.800000011920929 * CombatHandler.damage + 0.2 * (knowl / 50.0f * CombatHandler.damage);
            }
        }
        else {
            CombatHandler.damage *= 0.85f + this.currentStrength * 0.15f;
        }
        if (_creature.isStealth() && _creature.opponent != null && !_creature.isVisibleTo(opponent)) {
            final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
            segments.add(new CreatureLineSegment(_creature));
            segments.add(new MulticolorLineSegment(" backstab ", (byte)0));
            segments.add(new CreatureLineSegment(opponent));
            _creature.getCommunicator().sendColoredMessageCombat(segments);
            CombatHandler.damage = Math.min(50000.0, CombatHandler.damage * 4.0);
        }
        if (_creature.getCitizenVillage() != null && _creature.getCitizenVillage().getFaithWarBonus() > 0.0f) {
            CombatHandler.damage *= 1.0f + _creature.getCitizenVillage().getFaithWarBonus() / 100.0f;
        }
        if (_creature.fightlevel >= 4) {
            CombatHandler.damage *= 1.100000023841858;
        }
        return CombatHandler.damage;
    }
    
    private final double getDamage(final Creature _creature, final Item weapon, final Creature opponent) {
        Skill attStrengthSkill = null;
        try {
            attStrengthSkill = _creature.getSkills().getSkill(102);
        }
        catch (NoSuchSkillException nss) {
            attStrengthSkill = _creature.getSkills().learn(102, 1.0f);
            CombatHandler.logger.log(Level.WARNING, _creature.getName() + " had no strength. Weird.");
        }
        if (weapon.isBodyPartAttached()) {
            CombatHandler.damage = _creature.getCombatDamage(weapon) * 1000.0f * _creature.getStatus().getDamageTypeModifier();
            if (_creature.isPlayer()) {
                final Skill weaponLess = _creature.getWeaponLessFightingSkill();
                final double modifier = 1.0 + 2.0 * weaponLess.getKnowledge() / 100.0;
                CombatHandler.damage *= modifier;
            }
            if (CombatHandler.damage < 10000.0 && _creature.getBonusForSpellEffect((byte)24) > 0.0f) {
                if (_creature.isPlayer()) {
                    CombatHandler.damage += Server.getBuffedQualityEffect(_creature.getBonusForSpellEffect((byte)24) / 100.0f) * 15000.0;
                }
                else {
                    CombatHandler.damage += Server.getBuffedQualityEffect(_creature.getBonusForSpellEffect((byte)24) / 100.0f) * 5000.0;
                }
            }
            final float randomizer = (50.0f + Server.rand.nextFloat() * 50.0f) / 100.0f;
            CombatHandler.damage *= randomizer;
        }
        else {
            CombatHandler.damage = Weapon.getModifiedDamageForWeapon(weapon, attStrengthSkill, opponent.getTemplate().getTemplateId() == 116) * 1000.0;
            if (!Servers.isThisAnEpicOrChallengeServer()) {
                CombatHandler.damage += weapon.getCurrentQualityLevel() / 100.0f * weapon.getSpellExtraDamageBonus();
            }
            CombatHandler.damage += Server.getBuffedQualityEffect(weapon.getCurrentQualityLevel() / 100.0f) * Weapon.getBaseDamageForWeapon(weapon) * 2400.0;
            CombatHandler.damage *= Weapon.getMaterialDamageBonus(weapon.getMaterial());
            if (!opponent.isPlayer() && opponent.isHunter()) {
                CombatHandler.damage *= Weapon.getMaterialHunterDamageBonus(weapon.getMaterial());
            }
            CombatHandler.damage *= ItemBonus.getWeaponDamageIncreaseBonus(_creature, weapon);
            if (Servers.isThisAnEpicOrChallengeServer()) {
                CombatHandler.damage *= 1.0f + weapon.getCurrentQualityLevel() / 100.0f * weapon.getSpellExtraDamageBonus() / 30000.0f;
            }
        }
        if (_creature.getEnemyPresense() > 1200 && opponent.isPlayer() && !weapon.isArtifact()) {
            CombatHandler.damage *= 1.149999976158142;
        }
        if (!weapon.isArtifact() && this.hasRodEffect && opponent.isPlayer()) {
            CombatHandler.damage *= 1.2000000476837158;
        }
        final Vehicle vehicle = Vehicles.getVehicleForId(opponent.getVehicle());
        boolean mildStack = false;
        if (weapon.isWeaponPolearm() && ((vehicle != null && vehicle.isCreature()) || (opponent.isRidden() && weapon.isWeaponPierce()))) {
            CombatHandler.damage *= 1.7000000476837158;
        }
        else if (weapon.isArtifact()) {
            mildStack = true;
        }
        else if (_creature.getCultist() != null && _creature.getCultist().doubleWarDamage()) {
            CombatHandler.damage *= 1.5;
            mildStack = true;
        }
        else if (_creature.getDeity() != null && _creature.getDeity().isWarrior() && _creature.getFaith() >= 40.0f && _creature.getFavor() >= 20.0f) {
            CombatHandler.damage *= 1.149999976158142;
            mildStack = true;
        }
        if (_creature.isPlayer()) {
            if ((_creature.getFightStyle() != 2 || attStrengthSkill.getRealKnowledge() < 20.0) && attStrengthSkill.getRealKnowledge() != 20.0) {
                CombatHandler.damage *= 1.0 + (attStrengthSkill.getRealKnowledge() - 20.0) / 200.0;
            }
            if (this.currentStrength == 0) {
                Skill fstyle = null;
                try {
                    fstyle = _creature.getSkills().getSkill(10054);
                }
                catch (NoSuchSkillException nss2) {
                    fstyle = _creature.getSkills().learn(10054, 1.0f);
                }
                if (fstyle.skillCheck(opponent.getBaseCombatRating() * 3.0f, 0.0, this.receivedFStyleSkill || opponent.isNoSkillFor(_creature), 10.0f, _creature, opponent) > 0.0) {
                    this.receivedFStyleSkill = true;
                    CombatHandler.damage *= 0.800000011920929;
                }
                else {
                    CombatHandler.damage *= 0.5;
                }
            }
            if (_creature.getStatus().getStamina() > 2000 && this.currentStrength >= 1 && !this.receivedFStyleSkill) {
                int num = 10053;
                if (this.currentStrength == 1) {
                    num = 10055;
                }
                Skill fstyle2 = null;
                try {
                    fstyle2 = _creature.getSkills().getSkill(num);
                }
                catch (NoSuchSkillException nss3) {
                    fstyle2 = _creature.getSkills().learn(num, 1.0f);
                }
                if (fstyle2.skillCheck(opponent.getBaseCombatRating() * 3.0f, 0.0, this.receivedFStyleSkill || opponent.isNoSkillFor(_creature), 10.0f, _creature, opponent) > 0.0) {
                    this.receivedFStyleSkill = true;
                    if (this.currentStrength > 1) {
                        CombatHandler.damage *= 1.0 + Server.getModifiedFloatEffect(fstyle2.getRealKnowledge() / 100.0) / (mildStack ? 8.0f : 4.0f);
                    }
                }
            }
            float knowl = 1.0f;
            try {
                final Skill wSkill = _creature.getSkills().getSkill(weapon.getPrimarySkill());
                knowl = (float)wSkill.getRealKnowledge();
            }
            catch (NoSuchSkillException ex) {}
            if (knowl < 50.0f) {
                CombatHandler.damage = 0.800000011920929 * CombatHandler.damage + 0.2 * (knowl / 50.0f * CombatHandler.damage);
            }
        }
        else {
            CombatHandler.damage *= 0.85f + this.currentStrength * 0.15f;
        }
        if (_creature.isStealth() && _creature.opponent != null && !_creature.isVisibleTo(opponent)) {
            final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
            segments.add(new CreatureLineSegment(_creature));
            segments.add(new MulticolorLineSegment(" backstab ", (byte)0));
            segments.add(new CreatureLineSegment(opponent));
            _creature.getCommunicator().sendColoredMessageCombat(segments);
            CombatHandler.damage = Math.min(50000.0, CombatHandler.damage * 4.0);
        }
        if (_creature.getCitizenVillage() != null && _creature.getCitizenVillage().getFaithWarBonus() > 0.0f) {
            CombatHandler.damage *= 1.0f + _creature.getCitizenVillage().getFaithWarBonus() / 100.0f;
        }
        if (_creature.fightlevel >= 4) {
            CombatHandler.damage *= 1.100000023841858;
        }
        return CombatHandler.damage;
    }
    
    public byte getType(final Item weapon, final boolean rawType) {
        byte woundType = this.creature.getCombatDamageType();
        if (weapon.isWeaponSword() || weapon.getTemplateId() == 706) {
            if (rawType || Server.rand.nextInt(2) == 0) {
                woundType = 1;
            }
            else {
                woundType = 2;
            }
        }
        else if (weapon.getTemplateId() == 1115) {
            if (rawType || Server.rand.nextInt(3) == 0) {
                woundType = 2;
            }
            else {
                woundType = 0;
            }
        }
        else if (weapon.isWeaponSlash()) {
            woundType = 1;
        }
        else if (weapon.isWeaponPierce()) {
            woundType = 2;
        }
        else if (weapon.isWeaponCrush()) {
            woundType = 0;
        }
        else if (weapon.isBodyPart()) {
            if (weapon.getTemplateId() == 17) {
                woundType = 3;
            }
            else if (weapon.getTemplateId() == 12) {
                woundType = 0;
            }
        }
        return CombatHandler.type = woundType;
    }
    
    private float getWeaponSpeed(final Item _weapon) {
        float flspeed = 20.0f;
        float knowl = 0.0f;
        int spskillnum = 10052;
        if (_weapon.isBodyPartAttached()) {
            flspeed = this.creature.getBodyWeaponSpeed(_weapon);
        }
        else {
            flspeed = Weapon.getBaseSpeedForWeapon(_weapon);
            try {
                spskillnum = _weapon.getPrimarySkill();
            }
            catch (NoSuchSkillException ex) {}
        }
        try {
            final Skill wSkill = this.creature.getSkills().getSkill(spskillnum);
            knowl = (float)wSkill.getRealKnowledge();
        }
        catch (NoSuchSkillException ex2) {}
        if (!this.creature.isGhost()) {
            flspeed -= flspeed * 0.1f * knowl / 100.0f;
        }
        return flspeed;
    }
    
    private float getWeaponSpeed(final AttackAction act, final Item _weapon) {
        float flspeed = 20.0f;
        float knowl = 0.0f;
        int spskillnum = 10052;
        if (!act.isUsingWeapon()) {
            flspeed = act.getAttackValues().getBaseSpeed();
        }
        else {
            flspeed = act.getAttackValues().getBaseSpeed();
            try {
                spskillnum = _weapon.getPrimarySkill();
            }
            catch (NoSuchSkillException ex) {}
        }
        try {
            final Skill wSkill = this.creature.getSkills().getSkill(spskillnum);
            knowl = (float)wSkill.getRealKnowledge();
        }
        catch (NoSuchSkillException ex2) {}
        if (!this.creature.isGhost()) {
            flspeed -= flspeed * 0.1f * knowl / 100.0f;
        }
        return flspeed;
    }
    
    public final void setHasSpiritFervor(final boolean hasFervor) {
        this.hasSpiritFervor = hasFervor;
    }
    
    public float getCombatRating(final Creature opponent, final Item weapon, final boolean attacking) {
        float combatRating = this.creature.getBaseCombatRating();
        if (this.hasSpiritFervor) {
            ++combatRating;
        }
        if (this.creature.isKing() && this.creature.isEligibleForKingdomBonus()) {
            combatRating += 3.0f;
        }
        if (this.creature.hasTrait(0)) {
            ++combatRating;
        }
        if (attacking) {
            combatRating += 1.0f + this.creature.getBonusForSpellEffect((byte)30) / 30.0f;
        }
        else {
            combatRating += 1.0f + this.creature.getBonusForSpellEffect((byte)28) / 30.0f;
        }
        if (this.creature.getDeity() != null && this.creature.getFaith() > 70.0f) {
            MeshIO mesh = Server.surfaceMesh;
            if (this.creature.getLayer() < 0) {
                mesh = Server.caveMesh;
            }
            final int tile = mesh.getTile(this.creature.getCurrentTile().getTileX(), this.creature.getCurrentTile().getTileY());
            final byte type = Tiles.decodeType(tile);
            if (this.creature.getDeity().isFo()) {
                final Tiles.Tile theTile = Tiles.getTile(type);
                if (theTile.isNormalTree() || theTile.isMyceliumTree() || type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_TUNDRA.id) {
                    ++combatRating;
                }
            }
            if ((this.creature.getDeity().isMagranon() || this.creature.getDeity().isLibila()) && attacking) {
                combatRating += 2.0f;
            }
            if (this.creature.getDeity().isVynora() && !attacking) {
                final short height = Tiles.decodeHeight(tile);
                if (height < 0) {
                    combatRating += 2.0f;
                }
                else if (Terraforming.isRoad(type)) {
                    combatRating += 2.0f;
                }
            }
        }
        if (this.creature.getCultist() != null && this.creature.getCultist().hasFearEffect()) {
            combatRating += 2.0f;
        }
        if (this.creature.isPlayer()) {
            final int antiGankBonus = Math.max(0, this.creature.getLastAttackers() - 1);
            combatRating += antiGankBonus;
            this.creature.sendToLoggers("Adding " + antiGankBonus + " to combat rating due to attackers.");
        }
        if (this.creature.isHorse() && this.creature.getLeader() != null && this.creature.getLeader().isPlayer()) {
            combatRating -= 5.0f;
        }
        if (this.creature.hasSpellEffect((byte)97)) {
            combatRating -= 4.0f;
        }
        if (this.creature.isSpiritGuard()) {
            if (Servers.localServer.isChallengeServer()) {
                if (opponent.isPlayer() && opponent.getKingdomId() != this.creature.getKingdomId()) {
                    combatRating = 10.0f;
                }
            }
            else if (this.creature.getCitizenVillage() != null && this.creature.getCitizenVillage().plan.isUnderSiege()) {
                combatRating += this.creature.getCitizenVillage().plan.getSiegeCount() / 3;
            }
        }
        final float bon = weapon.getSpellNimbleness();
        if (bon > 0.0f) {
            combatRating += bon / 30.0f;
        }
        if (this.creature.isPlayer() && opponent.isPlayer()) {
            if (this.creature.isRoyalExecutioner() && this.creature.isEligibleForKingdomBonus()) {
                combatRating += 2.0f;
            }
            else if (this.creature.hasCrownInfluence()) {
                ++combatRating;
            }
            combatRating += Players.getInstance().getCRBonus(this.creature.getKingdomId());
            if (this.creature.isInOwnDuelRing()) {
                if (opponent.getKingdomId() != this.creature.getKingdomId()) {
                    combatRating += 4.0f;
                }
            }
            else if (opponent.isInOwnDuelRing() && opponent.getKingdomId() != this.creature.getKingdomId()) {
                combatRating -= 4.0f;
            }
            if (Servers.localServer.PVPSERVER && this.creature.getNumberOfFollowers() > 1) {
                combatRating -= 10.0f;
            }
        }
        if (this.creature.isPlayer() && this.creature.hasBattleCampBonus()) {
            combatRating += 3.0f;
        }
        combatRating += ItemBonus.getCRBonus(this.creature);
        float crmod = 1.0f;
        if (attacking) {
            if (this.creature.isPlayer() && this.currentStrength >= 1 && this.creature.getStatus().getStamina() > 2000) {
                int num = 10053;
                if (this.currentStrength == 1) {
                    num = 10055;
                }
                Skill def = null;
                try {
                    def = this.creature.getSkills().getSkill(num);
                }
                catch (NoSuchSkillException nss) {
                    def = this.creature.getSkills().learn(num, 1.0f);
                }
                if (def.skillCheck(this.creature.getBaseCombatRating() * 2.0f, 0.0, true, 10.0f, this.creature, opponent) > 0.0) {
                    combatRating += (float)(this.currentStrength / 2.0f * Server.getModifiedFloatEffect(def.getRealKnowledge() / 100.0));
                }
            }
        }
        else if (this.creature.isPlayer() && this.currentStrength > 1) {
            Skill def2 = null;
            try {
                def2 = this.creature.getSkills().getSkill(10053);
            }
            catch (NoSuchSkillException nss2) {
                def2 = this.creature.getSkills().learn(10053, 1.0f);
            }
            if (def2.skillCheck(Server.getModifiedFloatEffect(70.0), 0.0, true, 10.0f, this.creature, opponent) < 0.0) {
                combatRating -= (float)(this.currentStrength * Server.getModifiedFloatEffect((100.0 - def2.getRealKnowledge()) / 100.0));
            }
        }
        if (this.creature.isPlayer()) {
            combatRating -= (float)Weapon.getSkillPenaltyForWeapon(weapon);
            combatRating += this.creature.getCRCounterBonus();
        }
        if (this.creature.isPlayer()) {
            if (opponent.isPlayer()) {
                combatRating += (float)(this.creature.getFightingSkill().getKnowledge(0.0) / 5.0);
            }
            else {
                combatRating += (float)(this.creature.getFightingSkill().getRealKnowledge() / 10.0);
            }
        }
        if (this.battleratingPenalty > 0) {
            combatRating -= this.battleratingPenalty;
        }
        crmod *= this.getFlankingModifier(opponent);
        crmod *= this.getHeightModifier(opponent);
        crmod *= this.getAlcMod();
        if (this.creature.getCitizenVillage() != null) {
            crmod *= 1.0f + this.creature.getCitizenVillage().getFaithWarBonus() / 100.0f;
        }
        combatRating *= crmod;
        if (this.creature.fightlevel >= 3) {
            combatRating += this.creature.fightlevel * 2;
        }
        if (this.creature.isPlayer()) {
            combatRating *= Servers.localServer.getCombatRatingModifier();
        }
        combatRating *= this.getFootingModifier(weapon, opponent);
        if (this.creature.isOnHostileHomeServer()) {
            combatRating *= 0.7f;
        }
        if (this.isOpen()) {
            combatRating *= 0.7f;
        }
        else if (this.isProne()) {
            combatRating *= 0.5f;
        }
        else {
            try {
                final Action act = this.creature.getCurrentAction();
                if (act.isVulnerable()) {
                    combatRating *= 0.5f;
                }
                else if (this.creature.isLinked()) {
                    final Creature linkedTo = this.creature.getCreatureLinkedTo();
                    if (linkedTo != null) {
                        try {
                            linkedTo.getCurrentAction().isSpell();
                            combatRating *= 0.7f;
                        }
                        catch (NoSuchActionException ex) {}
                    }
                }
            }
            catch (NoSuchActionException ex2) {}
        }
        if (this.creature.hasAttackedUnmotivated()) {
            combatRating = Math.min(4.0f, combatRating);
        }
        return this.normcr(combatRating);
    }
    
    private float getAlcMod() {
        if (!this.creature.isPlayer()) {
            return 1.0f;
        }
        float alc = 0.0f;
        alc = ((Player)this.creature).getAlcohol();
        if (alc < 20.0f) {
            return (100.0f + alc) / 100.0f;
        }
        return Math.max(40.0f, 100.0f - alc) / 80.0f;
    }
    
    private float normcr(final float combatRating) {
        return Math.min(100.0f, Math.max(1.0f, combatRating));
    }
    
    static float getParryBonus(final byte defenderStance, final byte attackerStance) {
        if (isStanceParrying(defenderStance, attackerStance)) {
            return 0.8f;
        }
        if (isStanceOpposing(defenderStance, attackerStance)) {
            return 0.9f;
        }
        return 1.0f;
    }
    
    public float getChanceToHit(final Creature opponent, final Item weapon) {
        this.setBonuses(weapon, opponent);
        float myCR = this.getCombatRating(opponent, weapon, true);
        final float oppCR = opponent.getCombatHandler().getCombatRating(this.creature, opponent.getPrimWeapon(), false);
        if (this.creature.isPlayer()) {
            final float distdiff = Math.abs(getDistdiff(weapon, this.creature, opponent));
            if (distdiff > 10.0f) {
                --myCR;
            }
            if (distdiff > 20.0f) {
                --myCR;
            }
        }
        CombatHandler.parryBonus = getParryBonus(opponent.getCombatHandler().currentStance, this.currentStance);
        if (opponent.fightlevel > 0) {
            CombatHandler.parryBonus -= opponent.fightlevel * 1 / 100.0f;
        }
        double m = 1.0;
        if (CombatHandler.attBonus != 0.0) {
            m = 1.0 + CombatHandler.attBonus / 100.0;
        }
        final Seat s = opponent.getSeat();
        if (s != null) {
            m *= s.cover;
        }
        final float chance = (float)(this.normcr(myCR) / (this.normcr(oppCR) + this.normcr(myCR)) * m * CombatHandler.parryBonus);
        final float rest = Math.max(0.01f, 1.0f - chance);
        return 100.0f * Math.max(0.01f, (float)Server.getBuffedQualityEffect(1.0f - rest));
    }
    
    public void setBonuses(final Item weapon, final Creature defender) {
        CombatHandler.attBonus = this.creature.zoneBonus - this.creature.getMovePenalty() * 0.5;
        if (this.currentStrength == 0) {
            CombatHandler.attBonus -= 20.0;
        }
        CombatHandler.defBonus = defender.zoneBonus - defender.getMovePenalty();
        if (this.addToSkills && defender.isPlayer() && defender.getCombatHandler().currentStrength == 0) {
            Skill def = null;
            try {
                def = defender.getSkills().getSkill(10054);
            }
            catch (NoSuchSkillException nss) {
                def = defender.getSkills().learn(10054, 1.0f);
            }
            if (defender.getStatus().getStamina() > 2000 && def.skillCheck(this.creature.getBaseCombatRating() * 2.0f, 0.0, this.creature.isNoSkillFor(defender) || defender.getCombatHandler().receivedFStyleSkill, 10.0f, defender, this.creature) > 0.0) {
                defender.getCombatHandler().receivedFStyleSkill = true;
                CombatHandler.defBonus += def.getKnowledge(0.0) / 4.0;
            }
        }
        if (defender.getCombatHandler().currentStrength > 0 && defender instanceof Player) {
            if (defender.isMoving()) {
                CombatHandler.defBonus -= defender.getCombatHandler().currentStrength * 15;
            }
            else if (defender.getCombatHandler().currentStrength > 1) {
                CombatHandler.defBonus -= defender.getCombatHandler().currentStrength * 7;
            }
        }
        if (defender.isOnHostileHomeServer()) {
            CombatHandler.defBonus -= 20.0;
        }
        else if (this.creature.isMoving() && this.creature instanceof Player) {
            CombatHandler.attBonus -= 15.0;
        }
    }
    
    private static final float getDistdiff(final Creature creature, final Creature opponent, final AttackAction atk) {
        if (atk != null && !atk.isUsingWeapon()) {
            final float idealDist = 10 + atk.getAttackValues().getAttackReach() * 3;
            final float dist = Creature.rangeToInDec(creature, opponent);
            return idealDist - dist;
        }
        final Item wpn = creature.getPrimWeapon();
        return getDistdiff(wpn, creature, opponent);
    }
    
    private static final float getDistdiff(final Item weapon, final Creature creature, final Creature opponent) {
        final float idealDist = 10 + Weapon.getReachForWeapon(weapon) * 3;
        final float dist = Creature.rangeToInDec(creature, opponent);
        return idealDist - dist;
    }
    
    private float getFootingModifier(final Item weapon, final Creature opponent) {
        final short[] steepness = Creature.getTileSteepness(this.creature.getCurrentTile().tilex, this.creature.getCurrentTile().tiley, this.creature.isOnSurface());
        float footingMod = 0.0f;
        float heightDiff = 0.0f;
        heightDiff = Math.max(-1.45f, this.creature.getStatus().getPositionZ() + this.creature.getAltOffZ()) - Math.max(-1.45f, opponent.getStatus().getPositionZ() + opponent.getAltOffZ());
        if (heightDiff > 0.5) {
            footingMod += 0.1;
        }
        else if (heightDiff < -0.5) {
            footingMod -= 0.1f;
        }
        if (this.creature.isSubmerged()) {
            return 1.0f;
        }
        if (this.creature.getVehicle() == -10L) {
            if (weapon != null && opponent.getVehicle() != -10L && weapon.isTwoHanded() && !weapon.isWeaponBow()) {
                footingMod += 0.3f;
            }
            if (this.creature.getStatus().getPositionZ() <= -1.45f) {
                return 0.2f + footingMod;
            }
            if (this.creature.isPlayer() && (steepness[1] > 20 || steepness[1] < -20)) {
                Skill bcskill = null;
                try {
                    bcskill = this.creature.getSkills().getSkill(104);
                }
                catch (NoSuchSkillException nss) {
                    bcskill = this.creature.getSkills().learn(104, 1.0f);
                }
                if (bcskill != null && bcskill.skillCheck(Math.abs(Math.max(Math.min(steepness[1], 99), -99)), this.creature.fightlevel * 10, true, 1.0f) > 0.0) {
                    return 1.0f + footingMod;
                }
                if (steepness[1] <= 40 && steepness[1] >= -40) {
                    return 0.9f + footingMod;
                }
                if (steepness[1] <= 60 && steepness[1] >= -60) {
                    return 0.8f + footingMod;
                }
                if (steepness[1] <= 80 && steepness[1] >= -80) {
                    return 0.6f + footingMod;
                }
                if (steepness[1] > 100 || steepness[1] < -100) {
                    return 0.2f + footingMod;
                }
                return 0.4f + footingMod;
            }
        }
        else if (opponent.isSubmerged()) {
            footingMod = 0.0f;
        }
        return 1.0f + footingMod;
    }
    
    private float getDirectionTo(final Creature opponent) {
        final float defAngle = Creature.normalizeAngle(opponent.getStatus().getRotation());
        final double newrot = Math.atan2(this.creature.getStatus().getPositionY() - opponent.getStatus().getPositionY(), this.creature.getStatus().getPositionX() - opponent.getStatus().getPositionX());
        final float attAngle = (float)(newrot * 57.29577951308232) + 90.0f;
        return Creature.normalizeAngle(attAngle - defAngle);
    }
    
    private float getFlankingModifier(final Creature opponent) {
        if (opponent == null) {
            return 1.0f;
        }
        float attAngle = this.getDirectionTo(opponent);
        if (opponent.getVehicle() > -10L) {
            final Vehicle vehic = Vehicles.getVehicleForId(opponent.getVehicle());
            if (vehic != null && vehic.isCreature()) {
                try {
                    final Creature ridden = Server.getInstance().getCreature(opponent.getVehicle());
                    attAngle = this.getDirectionTo(ridden);
                }
                catch (Exception ex) {
                    CombatHandler.logger.log(Level.INFO, "No creature for id " + opponent.getVehicle());
                }
            }
        }
        if (attAngle <= 140.0f || attAngle >= 220.0f) {
            return 1.0f;
        }
        if (attAngle > 160.0f && attAngle < 200.0f) {
            return 1.25f;
        }
        return 1.1f;
    }
    
    private float getHeightModifier(final Creature opponent) {
        if (opponent == null) {
            return 1.0f;
        }
        final float diff = this.creature.getPositionZ() + this.creature.getAltOffZ() - (opponent.getPositionZ() + opponent.getAltOffZ());
        if (diff > 1.0f) {
            if (diff > 2.0f) {
                return 1.1f;
            }
            return 1.05f;
        }
        else {
            if (diff >= -1.0f) {
                return 1.0f;
            }
            if (diff < -2.0f) {
                return 0.9f;
            }
            return 0.95f;
        }
    }
    
    public static final byte getStanceForAction(final ActionEntry entry) {
        if (entry.isAttackHigh()) {
            if (entry.isAttackLeft()) {
                return 6;
            }
            if (entry.isAttackRight()) {
                return 1;
            }
            return 7;
        }
        else if (entry.isAttackLow()) {
            if (entry.isAttackLeft()) {
                return 4;
            }
            if (entry.isAttackRight()) {
                return 3;
            }
            return 10;
        }
        else {
            if (entry.isAttackLeft()) {
                return 5;
            }
            if (entry.isAttackRight()) {
                return 2;
            }
            if (!entry.isDefend()) {
                return 0;
            }
            switch (entry.getNumber()) {
                case 314: {
                    return 12;
                }
                case 315: {
                    return 14;
                }
                case 316: {
                    return 11;
                }
                case 317: {
                    return 13;
                }
                default: {
                    return 0;
                }
            }
        }
    }
    
    public void setKillEffects(final Creature performer, final Creature defender) {
        defender.setOpponent(null);
        defender.setTarget(-10L, true);
        if (defender.getWurmId() == performer.target) {
            performer.setTarget(-10L, true);
        }
        defender.getCombatHandler().setCurrentStance(-1, (byte)15);
        performer.getCombatHandler().setCurrentStance(-1, (byte)15);
        if (performer.isUndead()) {
            performer.healRandomWound(100);
            final float nut = (50 + Server.rand.nextInt(49)) / 100.0f;
            performer.getStatus().refresh(nut, true);
        }
        if (performer.getCitizenVillage() != null) {
            performer.getCitizenVillage().removeTarget(defender);
        }
        if (defender.isPlayer() && performer.isPlayer()) {
            if (!defender.isOkToKillBy(performer)) {
                if (performer.hasAttackedUnmotivated() && performer.getReputation() < 0) {
                    performer.setReputation(performer.getReputation() - 10);
                }
                else {
                    performer.setReputation(performer.getReputation() - 20);
                }
            }
            if (!defender.isFriendlyKingdom(performer.getKingdomId()) && !Players.getInstance().isOverKilling(performer.getWurmId(), defender.getWurmId())) {
                if (performer.getKingdomTemplateId() == 3 || (performer.getDeity() != null && performer.getDeity().isHateGod())) {
                    performer.maybeModifyAlignment(-5.0f);
                }
                else {
                    performer.maybeModifyAlignment(5.0f);
                }
                if (performer.getCombatHandler().currentStrength == 0) {
                    performer.achievement(43);
                }
            }
        }
        else if (!defender.isPlayer() && !performer.isPlayer() && defender.isPrey() && performer.isCarnivore()) {
            performer.getStatus().modifyHunger(-65000, 99.0f);
        }
        if (!defender.isPlayer() && !defender.isReborn() && performer.isPlayer()) {
            if (defender.isKingdomGuard() && defender.getKingdomId() == performer.getKingdomId()) {
                performer.achievement(44);
            }
            try {
                final int tid = defender.getTemplate().getTemplateId();
                if (CreatureTemplate.isDragon(tid)) {
                    ((Player)performer).addTitle(Titles.Title.DragonSlayer);
                }
                else if (tid == 11 || tid == 27) {
                    ((Player)performer).addTitle(Titles.Title.TrollSlayer);
                }
                else if (tid == 20 || tid == 22) {
                    ((Player)performer).addTitle(Titles.Title.GiantSlayer);
                }
            }
            catch (Exception ex) {
                CombatHandler.logger.log(Level.WARNING, defender.getName() + " and " + performer.getName() + ":" + ex.getMessage(), ex);
            }
            if (performer.getDeity() != null && performer.getDeity().number == 2) {
                performer.maybeModifyAlignment(0.5f);
            }
            if (performer.getDeity() != null && performer.getDeity().number == 4) {
                performer.maybeModifyAlignment(-0.5f);
            }
        }
        if (performer.getPrimWeapon() != null) {
            final float ms = performer.getPrimWeapon().getSpellMindStealModifier();
            if (ms > 0.0f && !defender.isPlayer() && defender.getKingdomId() != performer.getKingdomId()) {
                final Skills s = defender.getSkills();
                final int r = Server.rand.nextInt(s.getSkills().length);
                final Skill toSteal = s.getSkills()[r];
                final float skillStolen = ms / 100.0f * 0.1f;
                try {
                    final Skill owned = this.creature.getSkills().getSkill(toSteal.getNumber());
                    if (owned.getKnowledge() < toSteal.getKnowledge()) {
                        final double smod = (toSteal.getKnowledge() - owned.getKnowledge()) / 100.0;
                        owned.setKnowledge(owned.getKnowledge() + skillStolen * smod, false);
                        this.creature.getCommunicator().sendSafeServerMessage("The " + performer.getPrimWeapon().getName() + " steals some " + toSteal.getName() + ".");
                    }
                }
                catch (NoSuchSkillException ex2) {}
            }
        }
    }
    
    private boolean checkStanceChange(final Creature defender, final Creature opponent) {
        if (defender.isFighting()) {
            if (defender.isPlayer()) {
                if (defender.isAutofight() && Server.rand.nextInt(10) == 0) {
                    this.selectStance(defender, opponent);
                    return true;
                }
            }
            else if (Server.rand.nextInt(5) == 0) {
                this.selectStance(defender, opponent);
                return true;
            }
        }
        return false;
    }
    
    private void selectStance(final Creature defender, final Creature player) {
        boolean selectNewStance = false;
        try {
            if (!defender.getCurrentAction().isDefend() && !defender.getCurrentAction().isStanceChange()) {
                selectNewStance = true;
            }
        }
        catch (NoSuchActionException nsa) {
            selectNewStance = true;
        }
        if (!defender.isPlayer() && selectNewStance && Server.rand.nextInt((int)(11.0f - Math.min(10.0f, defender.getAggressivity() * defender.getStatus().getAggTypeModifier() / 10.0f))) != 0) {
            selectNewStance = false;
        }
        if (selectNewStance) {
            CombatHandler.selectStanceList.clear();
            float mycr = -1.0f;
            float oppcr = -1.0f;
            float knowl = -1.0f;
            if (defender.isFighting()) {
                if (defender.mayRaiseFightLevel() && defender.getMindLogical().getKnowledge(0.0) > 7.0) {
                    if (defender.isPlayer() || Server.rand.nextInt(100) < 30) {
                        selectNewStance = false;
                        CombatHandler.selectStanceList.add(Actions.actionEntrys[340]);
                    }
                    else {
                        CombatHandler.selectStanceList.add(Actions.actionEntrys[340]);
                    }
                }
                if (defender.isPlayer() && this.getSpeed(defender.getPrimWeapon()) > Server.rand.nextInt(10)) {
                    selectNewStance = false;
                }
                if (selectNewStance) {
                    mycr = defender.getCombatHandler().getCombatRating(player, defender.getPrimWeapon(), false);
                    oppcr = player.getCombatHandler().getCombatRating(defender, player.getPrimWeapon(), false);
                    knowl = this.getCombatKnowledgeSkill();
                    if (knowl > 50.0f) {
                        CombatHandler.selectStanceList.addAll(CombatHandler.standardDefences);
                    }
                    if (!defender.isPlayer()) {
                        knowl += 20.0f;
                    }
                    CombatHandler.selectStanceList.addAll(defender.getCombatHandler().getHighAttacks(null, true, player, mycr, oppcr, knowl));
                    CombatHandler.selectStanceList.addAll(defender.getCombatHandler().getMidAttacks(null, true, player, mycr, oppcr, knowl));
                    CombatHandler.selectStanceList.addAll(defender.getCombatHandler().getLowAttacks(null, true, player, mycr, oppcr, knowl));
                }
            }
            if (CombatHandler.selectStanceList.size() > 0) {
                this.selectStanceFromList(defender, player, mycr, oppcr, knowl);
            }
            if (!defender.isPlayer() && Server.rand.nextInt(10) == 0) {
                final int randInt = Server.rand.nextInt(100);
                if (randInt <= Math.max(10.0f, (defender.getAggressivity() - 20) * defender.getStatus().getAggTypeModifier())) {
                    if (defender.getFightStyle() != 1) {
                        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                        segments.add(new CreatureLineSegment(defender));
                        segments.add(new MulticolorLineSegment(" suddenly goes into a frenzy.", (byte)0));
                        player.getCommunicator().sendColoredMessageCombat(segments);
                        defender.setFightingStyle((byte)1);
                    }
                }
                else if (randInt > Math.min(90.0f, (defender.getAggressivity() * defender.getStatus().getAggTypeModifier() + 20.0f) * defender.getStatus().getAggTypeModifier())) {
                    if (defender.getFightStyle() != 2) {
                        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                        segments.add(new CreatureLineSegment(defender));
                        segments.add(new MulticolorLineSegment(" cowers.", (byte)0));
                        player.getCommunicator().sendColoredMessageCombat(segments);
                        defender.setFightingStyle((byte)2);
                    }
                }
                else {
                    if (defender.getFightStyle() == 1) {
                        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                        segments.add(new CreatureLineSegment(defender));
                        segments.add(new MulticolorLineSegment(" calms down a bit.", (byte)0));
                        player.getCommunicator().sendColoredMessageCombat(segments);
                    }
                    else if (defender.getFightStyle() == 2) {
                        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                        segments.add(new CreatureLineSegment(defender));
                        segments.add(new MulticolorLineSegment(" seems a little more brave now.", (byte)0));
                        player.getCommunicator().sendColoredMessageCombat(segments);
                    }
                    if (defender.getFightStyle() != 0) {
                        defender.setFightingStyle((byte)0);
                    }
                }
            }
        }
    }
    
    private static final ActionEntry getDefensiveActionEntry(final byte opponentStance) {
        final Iterator<ActionEntry> it = CombatHandler.selectStanceList.listIterator();
        while (it.hasNext()) {
            ActionEntry e = it.next();
            e = Actions.actionEntrys[e.getNumber()];
            if (isStanceParrying(getStanceForAction(e), opponentStance) && !isAtSoftSpot(getStanceForAction(e), opponentStance)) {
                return e;
            }
        }
        return null;
    }
    
    private static final ActionEntry getOpposingActionEntry(final byte opponentStance) {
        final Iterator<ActionEntry> it = CombatHandler.selectStanceList.listIterator();
        while (it.hasNext()) {
            ActionEntry e = it.next();
            e = Actions.actionEntrys[e.getNumber()];
            if (isStanceOpposing(getStanceForAction(e), opponentStance) && !isAtSoftSpot(getStanceForAction(e), opponentStance)) {
                return e;
            }
        }
        return null;
    }
    
    private static final ActionEntry getNonDefensiveActionEntry(final byte opponentStance) {
        for (int x = 0; x < CombatHandler.selectStanceList.size(); ++x) {
            final int num = Server.rand.nextInt(CombatHandler.selectStanceList.size());
            ActionEntry e = CombatHandler.selectStanceList.get(num);
            e = Actions.actionEntrys[e.getNumber()];
            if (!isStanceParrying(getStanceForAction(e), opponentStance) && !isStanceOpposing(getStanceForAction(e), opponentStance) && !isAtSoftSpot(getStanceForAction(e), opponentStance)) {
                return e;
            }
        }
        return null;
    }
    
    private static final boolean isNextGoodStance(final byte currentStance, final byte nextStance, final byte opponentStance) {
        if (isAtSoftSpot(nextStance, opponentStance)) {
            return false;
        }
        if (isAtSoftSpot(opponentStance, currentStance)) {
            return false;
        }
        if (isAtSoftSpot(opponentStance, nextStance)) {
            return true;
        }
        if (currentStance == 0) {
            return nextStance == 5 || nextStance == 2;
        }
        if (currentStance == 5) {
            return nextStance == 6 || nextStance == 4;
        }
        if (currentStance == 2) {
            return nextStance == 1 || nextStance == 3;
        }
        if (currentStance == 1 || currentStance == 6) {
            return nextStance == 7;
        }
        return (currentStance == 3 || currentStance == 4) && nextStance == 10;
    }
    
    static final byte[] getSoftSpots(final byte currentStance) {
        if (currentStance == 0) {
            return CombatHandler.standardSoftSpots;
        }
        if (currentStance == 5) {
            return CombatHandler.midLeftSoftSpots;
        }
        if (currentStance == 2) {
            return CombatHandler.midRightSoftSpots;
        }
        if (currentStance == 1) {
            return CombatHandler.upperRightSoftSpots;
        }
        if (currentStance == 6) {
            return CombatHandler.upperLeftSoftSpots;
        }
        if (currentStance == 3) {
            return CombatHandler.lowerRightSoftSpots;
        }
        if (currentStance == 4) {
            return CombatHandler.lowerLeftSoftSpots;
        }
        return CombatHandler.emptyByteArray;
    }
    
    private static final boolean isAtSoftSpot(final byte stanceChecked, final byte stanceUnderAttack) {
        final byte[] softSpots;
        final byte[] opponentSoftSpots = softSpots = getSoftSpots(stanceChecked);
        for (final byte spot : softSpots) {
            if (spot == stanceUnderAttack) {
                return true;
            }
        }
        return false;
    }
    
    private static final boolean existsBetterOffensiveStance(final byte _currentStance, final byte opponentStance) {
        if (isAtSoftSpot(opponentStance, _currentStance)) {
            return false;
        }
        final boolean isOpponentAtSoftSpot = isAtSoftSpot(_currentStance, opponentStance);
        if (isOpponentAtSoftSpot || (!isStanceParrying(_currentStance, opponentStance) && !isStanceOpposing(_currentStance, opponentStance))) {
            for (int x = 0; x < CombatHandler.selectStanceList.size(); ++x) {
                final int num = Server.rand.nextInt(CombatHandler.selectStanceList.size());
                ActionEntry e = CombatHandler.selectStanceList.get(num);
                e = Actions.actionEntrys[e.getNumber()];
                final byte nextStance = getStanceForAction(e);
                if (isNextGoodStance(_currentStance, nextStance, opponentStance)) {
                    return true;
                }
            }
            return false;
        }
        for (int x = 0; x < CombatHandler.selectStanceList.size(); ++x) {
            final int num = Server.rand.nextInt(CombatHandler.selectStanceList.size());
            ActionEntry e = CombatHandler.selectStanceList.get(num);
            e = Actions.actionEntrys[e.getNumber()];
            final byte nextStance = getStanceForAction(e);
            if (!isStanceParrying(_currentStance, nextStance) && !isStanceOpposing(_currentStance, nextStance)) {
                return true;
            }
        }
        return false;
    }
    
    private static final ActionEntry changeToBestOffensiveStance(final byte _currentStance, final byte opponentStance) {
        for (int x = 0; x < CombatHandler.selectStanceList.size(); ++x) {
            final int num = Server.rand.nextInt(CombatHandler.selectStanceList.size());
            ActionEntry e = CombatHandler.selectStanceList.get(num);
            e = Actions.actionEntrys[e.getNumber()];
            final byte nextStance = getStanceForAction(e);
            if (isNextGoodStance(_currentStance, nextStance, opponentStance)) {
                return e;
            }
        }
        return null;
    }
    
    private final void selectStanceFromList(final Creature defender, final Creature opponent, final float mycr, final float oppcr, final float knowl) {
        ActionEntry e = null;
        if (defender.isPlayer() || defender.getMindLogical().getKnowledge(0.0) > 17.0) {
            if (oppcr - mycr > 3.0f) {
                if (Server.rand.nextInt(2) == 0) {
                    if (defender.mayRaiseFightLevel()) {
                        e = Actions.actionEntrys[340];
                    }
                }
                else if (defender.opponent == opponent) {
                    e = getDefensiveActionEntry(opponent.getCombatHandler().currentStance);
                    if (e == null) {
                        e = getOpposingActionEntry(opponent.getCombatHandler().currentStance);
                    }
                }
            }
            if (e == null) {
                if (defender.combatRound > 2 && Server.rand.nextInt(2) == 0) {
                    if (defender.mayRaiseFightLevel()) {
                        e = Actions.actionEntrys[340];
                    }
                }
                else if (mycr - oppcr > 2.0f || defender.getCombatHandler().getSpeed(defender.getPrimWeapon()) < 3.0f) {
                    if (existsBetterOffensiveStance(defender.getCombatHandler().currentStance, opponent.getCombatHandler().currentStance)) {
                        e = changeToBestOffensiveStance(defender.getCombatHandler().currentStance, opponent.getCombatHandler().currentStance);
                        if (e == null) {
                            e = getNonDefensiveActionEntry(opponent.getCombatHandler().currentStance);
                        }
                    }
                }
                else if (mycr >= oppcr) {
                    if (defender.getStatus().damage < opponent.getStatus().damage) {
                        if (existsBetterOffensiveStance(defender.getCombatHandler().currentStance, opponent.getCombatHandler().currentStance)) {
                            e = changeToBestOffensiveStance(defender.getCombatHandler().currentStance, opponent.getCombatHandler().currentStance);
                            if (e == null) {
                                e = getNonDefensiveActionEntry(opponent.getCombatHandler().currentStance);
                            }
                        }
                    }
                    else {
                        e = getDefensiveActionEntry(opponent.getCombatHandler().currentStance);
                        if (e == null) {
                            e = getOpposingActionEntry(opponent.getCombatHandler().currentStance);
                        }
                    }
                }
            }
        }
        else if (e == null) {
            if (!Server.rand.nextBoolean() || defender.getShield() == null) {
                final int num = Server.rand.nextInt(CombatHandler.selectStanceList.size());
                e = CombatHandler.selectStanceList.get(num);
                e = Actions.actionEntrys[e.getNumber()];
            }
            else {
                e = Actions.actionEntrys[105];
            }
        }
        if (e != null && e.getNumber() > 0) {
            try {
                if (Creature.rangeTo(defender, opponent) <= e.getRange()) {
                    if (e.getNumber() == 105) {
                        defender.setAction(new Action(defender, -1L, opponent.getWurmId(), e.getNumber(), defender.getPosX(), defender.getPosY(), defender.getPositionZ() + defender.getAltOffZ(), defender.getStatus().getRotation()));
                    }
                    else if (e.isStanceChange() && e.getNumber() != 340) {
                        if (getStanceForAction(e) != this.currentStance) {
                            defender.setAction(new Action(defender, -1L, opponent.getWurmId(), e.getNumber(), defender.getPosX(), defender.getPosY(), defender.getPositionZ() + defender.getAltOffZ(), defender.getStatus().getRotation()));
                        }
                    }
                    else if (defender.mayRaiseFightLevel() && e.getNumber() == 340) {
                        defender.setAction(new Action(defender, -1L, opponent.getWurmId(), e.getNumber(), defender.getPosX(), defender.getPosY(), defender.getPositionZ() + defender.getAltOffZ(), defender.getStatus().getRotation()));
                    }
                    else {
                        defender.setAction(new Action(defender, -1L, opponent.getWurmId(), e.getNumber(), defender.getPosX(), defender.getPosY(), defender.getPositionZ() + defender.getAltOffZ(), defender.getStatus().getRotation()));
                    }
                }
                else {
                    CombatHandler.logger.log(Level.INFO, defender.getName() + " too far away for stance " + e.getActionString() + " attacking " + opponent.getName() + " with range " + Creature.rangeTo(defender, opponent));
                }
            }
            catch (Exception fe) {
                CombatHandler.logger.log(Level.WARNING, defender.getName() + " failed:" + fe.getMessage(), fe);
            }
        }
    }
    
    public static final byte[] getOptions(final List<ActionEntry> list, final byte currentStance) {
        if (list == null || list.isEmpty()) {
            return CombatHandler.NO_COMBAT_OPTIONS;
        }
        final byte[] toReturn = new byte[31];
        final ListIterator<ActionEntry> it = list.listIterator();
        while (it.hasNext()) {
            final ActionEntry act = it.next();
            final int x = act.getNumber() - 287;
            if (act.isDefend()) {
                toReturn[x] = 50;
            }
            else {
                if (x < 0 || x > 30 || getStanceForAction(Actions.actionEntrys[act.getNumber()]) == currentStance) {
                    continue;
                }
                toReturn[x] = Byte.parseByte(act.getActionString().substring(0, act.getActionString().indexOf("%")));
            }
        }
        return toReturn;
    }
    
    public void addParryModifier(final DoubleValueModifier modifier) {
        if (this.parryModifiers == null) {
            this.parryModifiers = new HashSet<DoubleValueModifier>();
        }
        this.parryModifiers.add(modifier);
    }
    
    public void removeParryModifier(final DoubleValueModifier modifier) {
        if (this.parryModifiers != null) {
            this.parryModifiers.remove(modifier);
        }
    }
    
    private double getParryMod() {
        if (this.parryModifiers == null) {
            return 1.0;
        }
        double doubleModifier = 1.0;
        for (final DoubleValueModifier lDoubleValueModifier : this.parryModifiers) {
            doubleModifier += lDoubleValueModifier.getModifier();
        }
        return doubleModifier;
    }
    
    public void addDodgeModifier(final DoubleValueModifier modifier) {
        if (this.dodgeModifiers == null) {
            this.dodgeModifiers = new HashSet<DoubleValueModifier>();
        }
        this.dodgeModifiers.add(modifier);
    }
    
    public void removeDodgeModifier(final DoubleValueModifier modifier) {
        if (this.dodgeModifiers != null) {
            this.dodgeModifiers.remove(modifier);
        }
    }
    
    private double getDodgeMod() {
        float diff = this.creature.getTemplate().getWeight() / this.creature.getWeight();
        if (this.creature.isPlayer()) {
            diff = this.creature.getTemplate().getWeight() / (this.creature.getWeight() + this.creature.getBody().getBodyItem().getFullWeight() + this.creature.getInventory().getFullWeight());
        }
        diff = 0.8f + diff * 0.2f;
        if (this.dodgeModifiers == null) {
            return 1.0f * diff;
        }
        double doubleModifier = 1.0;
        for (final DoubleValueModifier lDoubleValueModifier : this.dodgeModifiers) {
            doubleModifier += lDoubleValueModifier.getModifier();
        }
        return doubleModifier * diff;
    }
    
    void setFightingStyle(final byte style) {
        if (style == 2) {
            this.currentStrength = 0;
        }
        else if (style == 1) {
            this.currentStrength = 3;
        }
        else {
            this.currentStrength = 1;
        }
    }
    
    public List<ActionEntry> getMoveStack() {
        return this.moveStack;
    }
    
    void clearMoveStack() {
        if (this.moveStack != null) {
            this.moveStack.clear();
            this.moveStack = null;
        }
    }
    
    byte getOpportunityAttacks() {
        return this.opportunityAttacks;
    }
    
    public boolean isSentAttacks() {
        return this.sentAttacks;
    }
    
    public void setSentAttacks(final boolean aSentAttacks) {
        this.sentAttacks = aSentAttacks;
    }
    
    public void setRodEffect(final boolean effect) {
        this.hasRodEffect = effect;
        this.sendRodEffect();
    }
    
    public void sendRodEffect() {
        if (this.hasRodEffect) {
            this.creature.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.ROD_BEGUILING_EFFECT, 100000, 100.0f);
        }
        else {
            this.creature.getCommunicator().sendRemoveSpellEffect(SpellEffectsEnum.ROD_BEGUILING_EFFECT);
        }
    }
    
    public static String getOthersString() {
        return CombatHandler.othersString;
    }
    
    public static void setOthersString(final String othersString) {
        CombatHandler.othersString = othersString;
    }
    
    static {
        logger = Logger.getLogger(CombatHandler.class.getName());
        NO_COMBAT_OPTIONS = new byte[0];
        CombatHandler.specialmoves = null;
        standardDefences = new LinkedList<ActionEntry>();
        CombatHandler.hit = false;
        CombatHandler.miss = true;
        CombatHandler.crit = false;
        CombatHandler.dead = false;
        CombatHandler.aiming = false;
        CombatHandler.chanceToHit = 0.0f;
        CombatHandler.attCheck = 0.0;
        CombatHandler.attBonus = 0.0;
        CombatHandler.defCheck = 0.0;
        CombatHandler.defBonus = 0.0;
        CombatHandler.damage = 0.0;
        CombatHandler.pos = 0;
        CombatHandler.type = 0;
        CombatHandler.defShield = null;
        CombatHandler.defParryWeapon = null;
        CombatHandler.defLeftWeapon = null;
        CombatHandler.defPrimWeaponSkill = null;
        CombatHandler.defenderSkills = null;
        CombatHandler.attString = "";
        CombatHandler.othersString = "";
        selectStanceList = new LinkedList<ActionEntry>();
        CombatHandler.justOpen = false;
        CombatHandler.manouvreMod = 0.0;
        CombatHandler.parryBonus = 1.0f;
        CombatHandler.standardDefences.add(Actions.actionEntrys[314]);
        CombatHandler.standardDefences.add(Actions.actionEntrys[315]);
        CombatHandler.standardDefences.add(Actions.actionEntrys[316]);
        CombatHandler.standardDefences.add(Actions.actionEntrys[317]);
    }
}
