// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.Features;
import javax.annotation.Nullable;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class ActionEntry implements ActionTypes, MiscConstants, Comparable<ActionEntry>
{
    private static final Logger logger;
    private String actionString;
    private final String verbString;
    private final short number;
    private boolean quickSkillLess;
    private final int maxRange;
    private final int prio;
    private String animationString;
    private boolean needsFood;
    private boolean isSpell;
    private boolean isOffensive;
    private boolean isFatigue;
    private boolean isPoliced;
    private boolean isNoMove;
    private boolean isNonLibila;
    private boolean isNonWhiteReligion;
    private boolean isNonReligion;
    private boolean isAttackHigh;
    private boolean isAttackLow;
    private boolean isAttackLeft;
    private boolean isAttackRight;
    private boolean isDefend;
    private boolean isStanceChange;
    private boolean isAllowFo;
    private boolean isAllowFoOnSurface;
    private boolean isAllowMagranon;
    private boolean isAllowMagranonInCave;
    private boolean isAllowVynora;
    private boolean isAllowVynoraOnSurface;
    private boolean isAllowLibila;
    private boolean isAllowLibilaInCave;
    private boolean isOpportunity;
    private boolean ignoresRange;
    private boolean vulnerable;
    private boolean isMission;
    private boolean notVulnerable;
    private boolean isBlockedByUseOnGroundOnly;
    private boolean usesNewSkillSystem;
    private boolean isVerifiedNewSystem;
    private boolean showOnSelectBar;
    private int blockType;
    private boolean sameBridgeOnly;
    private boolean isPerimeterAction;
    private boolean isCornerAction;
    private boolean isEnemyNever;
    private boolean isEnemyAlways;
    private boolean isEnemyNoGuards;
    private boolean useItemOnGround;
    private boolean stackable;
    private boolean stackableFight;
    private byte requiresActiveItem;
    
    ActionEntry(final short aNumber, final int aPriority, final String aActionString, final String aVerbString, final String aAnimationString, @Nullable final int[] aTypes, final int aRange, final boolean blockedByUseOnGroundOnly) {
        this.quickSkillLess = false;
        this.needsFood = false;
        this.isSpell = false;
        this.isOffensive = false;
        this.isFatigue = false;
        this.isPoliced = false;
        this.isNoMove = false;
        this.isNonLibila = false;
        this.isNonWhiteReligion = false;
        this.isNonReligion = false;
        this.isAttackHigh = false;
        this.isAttackLow = false;
        this.isAttackLeft = false;
        this.isAttackRight = false;
        this.isDefend = false;
        this.isStanceChange = false;
        this.isAllowFo = false;
        this.isAllowFoOnSurface = false;
        this.isAllowMagranon = false;
        this.isAllowMagranonInCave = false;
        this.isAllowVynora = false;
        this.isAllowVynoraOnSurface = false;
        this.isAllowLibila = false;
        this.isAllowLibilaInCave = false;
        this.isOpportunity = true;
        this.ignoresRange = false;
        this.vulnerable = false;
        this.isMission = false;
        this.notVulnerable = false;
        this.isBlockedByUseOnGroundOnly = true;
        this.usesNewSkillSystem = false;
        this.isVerifiedNewSystem = false;
        this.showOnSelectBar = false;
        this.blockType = 4;
        this.sameBridgeOnly = false;
        this.isPerimeterAction = false;
        this.isCornerAction = false;
        this.isEnemyNever = false;
        this.isEnemyAlways = false;
        this.isEnemyNoGuards = false;
        this.useItemOnGround = false;
        this.stackable = true;
        this.stackableFight = true;
        this.requiresActiveItem = 0;
        this.prio = aPriority;
        this.number = aNumber;
        this.actionString = aActionString;
        this.verbString = aVerbString;
        this.animationString = aAnimationString.toLowerCase().replace(" ", "");
        this.maxRange = aRange;
        this.isBlockedByUseOnGroundOnly = blockedByUseOnGroundOnly;
        this.assignTypes(aTypes);
    }
    
    ActionEntry(final short aNumber, final int aPriority, final String aActionString, final String aVerbString, @Nullable final int[] aTypes, final int aRange, final boolean blockedByUseOnGroundOnly) {
        this(aNumber, aPriority, aActionString, aVerbString, aActionString, aTypes, aRange, blockedByUseOnGroundOnly);
    }
    
    ActionEntry(final short aNumber, final String aActionString, final String aVerbString, final int[] aTypes, final int aRange) {
        this(aNumber, aActionString, aVerbString, aActionString, aTypes, aRange);
    }
    
    ActionEntry(final short aNumber, final String aActionString, final String aVerbString, final String aAnimationString, final int[] aTypes, final int aRange) {
        this(aNumber, 5, aActionString, aVerbString, aAnimationString, aTypes, aRange, true);
    }
    
    ActionEntry(final short aNumber, final String aActionString, final String aVerbString, final int[] aTypes, final int aRange, final boolean blockedByUseOnGround) {
        this(aNumber, 5, aActionString, aVerbString, aActionString, aTypes, aRange, blockedByUseOnGround);
    }
    
    ActionEntry(final short aNumber, final int aPriority, final String aActionString, final String aVerbString, final int[] aTypes) {
        this(aNumber, aPriority, aActionString, aVerbString, aActionString, aTypes, 4, true);
    }
    
    ActionEntry(final short aNumber, final int aPriority, final String aActionString, final String aVerbString, final int[] aTypes, final boolean blockedByUseOnGround) {
        this(aNumber, aPriority, aActionString, aVerbString, aActionString, aTypes, 4, blockedByUseOnGround);
    }
    
    ActionEntry(final short aNumber, final String aActionString, final String aVerbString, @Nullable final int[] aTypes) {
        this(aNumber, aActionString, aVerbString, aActionString, aTypes);
    }
    
    ActionEntry(final short aNumber, final String aActionString, final String aVerbString, final String aAnimationString, @Nullable final int[] aTypes) {
        this(aNumber, 5, aActionString, aVerbString, aAnimationString, aTypes, 4, true);
    }
    
    ActionEntry(final short aNumber, final String aActionString, final String aVerbString, @Nullable final int[] aTypes, final boolean blockedByUseOnGround) {
        this(aNumber, 5, aActionString, aVerbString, aActionString, aTypes, 4, blockedByUseOnGround);
    }
    
    public ActionEntry(final short aNumber, final String aActionString, final String aVerbString) {
        this(aNumber, aActionString, aVerbString, null);
    }
    
    public static ActionEntry createEntry(final short number, final String actionString, final String verb, final int[] types) {
        return new ActionEntry(number, actionString, verb, types, 2);
    }
    
    private final void assignTypes(@Nullable final int[] types) {
        if (types == null) {
            return;
        }
        for (int x = 0; x < types.length; ++x) {
            switch (types[x]) {
                case 0: {
                    this.quickSkillLess = true;
                    this.isOpportunity = false;
                    break;
                }
                case 1: {
                    this.needsFood = true;
                    break;
                }
                case 2: {
                    this.isSpell = true;
                    break;
                }
                case 3: {
                    this.isOffensive = true;
                    break;
                }
                case 4: {
                    this.isFatigue = true;
                    break;
                }
                case 5: {
                    this.isPoliced = true;
                    break;
                }
                case 6: {
                    this.isNoMove = true;
                    break;
                }
                case 7: {
                    this.setIsNonLibila(true);
                    break;
                }
                case 8: {
                    this.setIsNonWhiteReligion(true);
                    break;
                }
                case 9: {
                    this.setIsNonReligion(true);
                    break;
                }
                case 12: {
                    this.isAttackHigh = true;
                    break;
                }
                case 13: {
                    this.isAttackLow = true;
                    break;
                }
                case 14: {
                    this.isAttackLeft = true;
                    break;
                }
                case 15: {
                    this.isAttackRight = true;
                    break;
                }
                case 16: {
                    this.isOpportunity = false;
                    this.isDefend = true;
                    this.ignoresRange = true;
                    break;
                }
                case 17: {
                    this.isOpportunity = false;
                    this.isStanceChange = true;
                    this.ignoresRange = true;
                    break;
                }
                case 18: {
                    this.setAllowMagranon(true);
                    break;
                }
                case 38: {
                    this.setAllowMagranonInCave(true);
                    break;
                }
                case 19: {
                    this.setAllowFo(true);
                    break;
                }
                case 39: {
                    this.setAllowFoOnSurface(true);
                    break;
                }
                case 20: {
                    this.setAllowVynora(true);
                    break;
                }
                case 52: {
                    this.setAllowVynoraOnSurface(true);
                    break;
                }
                case 21: {
                    this.setAllowLibila(true);
                    break;
                }
                case 40: {
                    this.setAllowLibilaInCave(true);
                    break;
                }
                case 22: {
                    this.isOpportunity = false;
                    break;
                }
                case 23: {
                    this.blockType = 0;
                    this.ignoresRange = true;
                    break;
                }
                case 43: {
                    this.showOnSelectBar = true;
                    break;
                }
                case 24: {
                    this.vulnerable = true;
                    break;
                }
                case 25: {
                    this.isMission = true;
                    break;
                }
                case 26: {
                    this.notVulnerable = true;
                    break;
                }
                case 27: {
                    this.stackable = false;
                    break;
                }
                case 28: {
                    this.stackableFight = false;
                    break;
                }
                case 29: {
                    this.blockType = 0;
                    break;
                }
                case 30: {
                    this.blockType = 1;
                    break;
                }
                case 31: {
                    this.blockType = 2;
                    break;
                }
                case 32: {
                    this.blockType = 3;
                    break;
                }
                case 33: {
                    this.blockType = 5;
                    break;
                }
                case 34: {
                    this.blockType = 7;
                    break;
                }
                case 35: {
                    this.requiresActiveItem = 0;
                    break;
                }
                case 36: {
                    this.requiresActiveItem = 1;
                    break;
                }
                case 37: {
                    this.requiresActiveItem = 2;
                    break;
                }
                case 50: {
                    this.blockType = 8;
                    break;
                }
                case 44: {
                    this.setSameBridgeOnly(true);
                    break;
                }
                case 45: {
                    this.isPerimeterAction = true;
                    break;
                }
                case 46: {
                    this.isCornerAction = true;
                    break;
                }
                case 47: {
                    this.isEnemyNever = true;
                    break;
                }
                case 48: {
                    this.isEnemyAlways = true;
                    break;
                }
                case 49: {
                    this.isEnemyNoGuards = true;
                    break;
                }
                case 51: {
                    this.useItemOnGround = true;
                    break;
                }
                case 41: {
                    this.usesNewSkillSystem = true;
                    break;
                }
                case 42: {
                    this.isVerifiedNewSystem = true;
                    break;
                }
                default: {
                    ActionEntry.logger.warning("Unexepected ActionType: " + types[x] + " in " + this);
                    break;
                }
            }
        }
    }
    
    public final String getActionString() {
        return this.actionString;
    }
    
    public final String getAnimationString() {
        return this.animationString;
    }
    
    public final int getRange() {
        return this.maxRange;
    }
    
    public final short getNumber() {
        return this.number;
    }
    
    public final String getVerbString() {
        return this.verbString;
    }
    
    public final String getVerbStartString() {
        if (this.verbString.toLowerCase().startsWith("stop ") || this.verbString.toLowerCase().startsWith("start ")) {
            return this.verbString;
        }
        return "start " + this.verbString;
    }
    
    public final String getVerbFinishString() {
        if (this.verbString.toLowerCase().startsWith("stop ") || this.verbString.toLowerCase().startsWith("start ")) {
            return this.verbString;
        }
        return "finish " + this.verbString;
    }
    
    public final boolean isQuickSkillLess() {
        return this.quickSkillLess;
    }
    
    public final boolean isStackable() {
        return this.stackable;
    }
    
    public final boolean isStackableFight() {
        return this.stackableFight;
    }
    
    final boolean isSpell() {
        return this.isSpell;
    }
    
    final boolean needsFood() {
        return this.needsFood;
    }
    
    final boolean isFatigue() {
        return this.isFatigue;
    }
    
    final boolean isBlockedByUseOnGroundOnly() {
        return this.isBlockedByUseOnGroundOnly;
    }
    
    final boolean isPoliced() {
        return this.isPoliced;
    }
    
    final boolean isNoMove() {
        return this.isNoMove;
    }
    
    public final boolean isNonLibila() {
        return this.isNonLibila;
    }
    
    public final boolean isNonWhiteReligion() {
        return this.isNonWhiteReligion;
    }
    
    public final boolean isNonReligion() {
        return this.isNonReligion;
    }
    
    public final boolean isDefend() {
        return this.isDefend;
    }
    
    public final boolean isStanceChange() {
        return this.isStanceChange;
    }
    
    final boolean isOpportunity() {
        return this.isOpportunity;
    }
    
    public final boolean isAttackHigh() {
        return this.isAttackHigh;
    }
    
    public final boolean isAttackLow() {
        return this.isAttackLow;
    }
    
    public final boolean isAttackLeft() {
        return this.isAttackLeft;
    }
    
    public final boolean isAttackRight() {
        return this.isAttackRight;
    }
    
    final boolean isIgnoresRange() {
        return this.ignoresRange;
    }
    
    public final boolean isOffensive() {
        return this.isOffensive;
    }
    
    public final boolean isMission() {
        return this.isMission;
    }
    
    public final boolean isShowOnSelectBar() {
        return this.showOnSelectBar;
    }
    
    public final boolean isVulnerable() {
        return !this.notVulnerable && (this.isSpell || !this.isOffensive || this.vulnerable);
    }
    
    public final boolean canUseNewSkillSystem() {
        return this.usesNewSkillSystem && (this.isVerifiedNewSystem || Features.Feature.NEW_SKILL_SYSTEM.isEnabled());
    }
    
    public final int getPriority() {
        return this.prio;
    }
    
    public final int getBlockType() {
        return this.blockType;
    }
    
    public final byte getUseActiveItem() {
        return this.requiresActiveItem;
    }
    
    public boolean isAllowed(final Creature creature) {
        if (creature.getDeity() != null) {
            if (creature.getDeity().isWaterGod()) {
                if (creature.isOnSurface() && this.isAllowVynoraOnSurface()) {
                    return true;
                }
                if (this.isAllowVynora()) {
                    return true;
                }
            }
            if (creature.getDeity().isMountainGod()) {
                if (!creature.isOnSurface() && this.isAllowMagranonInCave()) {
                    return true;
                }
                if (this.isAllowMagranon()) {
                    return true;
                }
            }
            if (creature.getDeity().isForestGod()) {
                if (creature.isOnSurface() && this.isAllowFoOnSurface()) {
                    return true;
                }
                if (this.isAllowFo()) {
                    return true;
                }
            }
            if (creature.getDeity().isHateGod()) {
                if (!creature.isOnSurface() && this.isAllowLibilaInCave()) {
                    return true;
                }
                if (this.isAllowLibila()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public int compareTo(final ActionEntry aActionEntry) {
        return this.getActionString().compareTo(aActionEntry.getActionString());
    }
    
    @Override
    public String toString() {
        final StringBuilder lBuilder = new StringBuilder();
        lBuilder.append("ActionEntry [");
        lBuilder.append("Number: ").append(this.number);
        lBuilder.append(", Action String: ").append(this.actionString);
        lBuilder.append(", Verb String: ").append(this.verbString);
        lBuilder.append(", Range: ").append(this.maxRange);
        lBuilder.append(", Priority: ").append(this.prio);
        lBuilder.append(", UseActiveItem: ").append(this.requiresActiveItem);
        lBuilder.append(']');
        return lBuilder.toString();
    }
    
    public boolean isSameBridgeOnly() {
        return this.sameBridgeOnly;
    }
    
    public void setSameBridgeOnly(final boolean aSameBridgeOnly) {
        this.sameBridgeOnly = aSameBridgeOnly;
    }
    
    public boolean isPerimeterAction() {
        return this.isPerimeterAction;
    }
    
    public boolean isUseItemOnGroundAction() {
        return this.useItemOnGround;
    }
    
    public boolean isCornerAction() {
        return this.isCornerAction;
    }
    
    public boolean isEnemyNeverAllowed() {
        return this.isEnemyNever;
    }
    
    public boolean isEnemyAlwaysAllowed() {
        return this.isEnemyAlways;
    }
    
    public boolean isEnemyAllowedWhenNoGuards() {
        return this.isEnemyNoGuards;
    }
    
    public void setIsNonReligion(final boolean nonReligion) {
        this.isNonReligion = nonReligion;
    }
    
    public void setIsNonWhiteReligion(final boolean nonWhiteReligion) {
        this.isNonWhiteReligion = nonWhiteReligion;
    }
    
    public void setIsNonLibila(final boolean nonLibila) {
        this.isNonLibila = nonLibila;
    }
    
    public boolean isAllowFo() {
        return this.isAllowFo;
    }
    
    public void setAllowFo(final boolean allowFo) {
        this.isAllowFo = allowFo;
    }
    
    public boolean isAllowFoOnSurface() {
        return this.isAllowFoOnSurface;
    }
    
    public void setAllowFoOnSurface(final boolean allowFoOnSurface) {
        this.isAllowFoOnSurface = allowFoOnSurface;
    }
    
    public boolean isAllowMagranon() {
        return this.isAllowMagranon;
    }
    
    public void setAllowMagranon(final boolean allowMagranon) {
        this.isAllowMagranon = allowMagranon;
    }
    
    public boolean isAllowMagranonInCave() {
        return this.isAllowMagranonInCave;
    }
    
    public void setAllowMagranonInCave(final boolean allowMagranonInCave) {
        this.isAllowMagranonInCave = allowMagranonInCave;
    }
    
    public boolean isAllowVynora() {
        return this.isAllowVynora;
    }
    
    public void setAllowVynora(final boolean allowVynora) {
        this.isAllowVynora = allowVynora;
    }
    
    public boolean isAllowVynoraOnSurface() {
        return this.isAllowVynoraOnSurface;
    }
    
    public void setAllowVynoraOnSurface(final boolean allowVynoraOnSurface) {
        this.isAllowVynoraOnSurface = allowVynoraOnSurface;
    }
    
    public boolean isAllowLibila() {
        return this.isAllowLibila;
    }
    
    public void setAllowLibila(final boolean allowLibila) {
        this.isAllowLibila = allowLibila;
    }
    
    public boolean isAllowLibilaInCave() {
        return this.isAllowLibilaInCave;
    }
    
    public void setAllowLibilaInCave(final boolean allowLibilaInCave) {
        this.isAllowLibilaInCave = allowLibilaInCave;
    }
    
    static {
        logger = Logger.getLogger(ActionEntry.class.getName());
    }
}
