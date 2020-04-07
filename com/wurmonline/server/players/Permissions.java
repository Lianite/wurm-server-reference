// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.BitSet;

public class Permissions
{
    private int permissions;
    protected BitSet permissionBits;
    
    public Permissions() {
        this.permissions = 0;
        this.permissionBits = new BitSet(32);
    }
    
    public void setPermissionBits(final int newPermissions) {
        this.permissions = newPermissions;
        this.permissionBits.clear();
        for (int x = 0; x < 32; ++x) {
            if ((newPermissions >>> x & 0x1) == 0x1) {
                this.permissionBits.set(x);
            }
        }
    }
    
    public final boolean hasPermission(final int permissionBit) {
        return this.permissions != 0 && this.permissionBits.get(permissionBit);
    }
    
    private final int getPermissionsInt() {
        int ret = 0;
        for (int x = 0; x < 32; ++x) {
            if (this.permissionBits.get(x)) {
                ret += (int)(1L << x);
            }
        }
        return ret;
    }
    
    public final void setPermissionBit(final int bit, final boolean value) {
        this.permissionBits.set(bit, value);
        this.permissions = this.getPermissionsInt();
    }
    
    public int getPermissions() {
        return this.permissions;
    }
    
    public enum Allow implements IPermission
    {
        SETTLEMENT_MAY_MANAGE(0, "Allow Settlememnt to Manage", "Allow", "Manage", ""), 
        NOT_RUNEABLE(7, "Item Attributes", "Cannot be", "Runed", ""), 
        SEALED_BY_PLAYER(8, "Item Attributes", "Cannot", "Take / Put / Eat or Drink", ""), 
        NO_EAT_OR_DRINK(9, "Item Attributes", "Cannot", "Eat or Drink", ""), 
        OWNER_TURNABLE(10, "Item Attributes", "Turnable", "by Owner", ""), 
        OWNER_MOVEABLE(11, "Item Attributes", "Moveable", "by Owner", ""), 
        NO_DRAG(12, "Item Attributes", "Cannot be", "Dragged", ""), 
        NO_IMPROVE(13, "Item Attributes", "Cannot be", "Improved", ""), 
        NO_DROP(14, "Item Attributes", "Cannot be", "Dropped", ""), 
        NO_REPAIR(15, "Item Attributes", "Cannot be", "Repaired", ""), 
        PLANTED(16, "Item Attributes", "Is", "Planted", ""), 
        AUTO_FILL(17, "Item Attributes", "Auto", "Fills", ""), 
        AUTO_LIGHT(18, "Item Attributes", "Auto", "Lights", ""), 
        ALWAYS_LIT(19, "Item Attributes", "Always", "Lit", ""), 
        HAS_COURIER(20, "Item Attributes", "Has", "Courier", ""), 
        HAS_DARK_MESSENGER(21, "Item Attributes", "Has", "Dark Messanger", ""), 
        DECAY_DISABLED(22, "Item Attributes", "Decay", "Disabled", ""), 
        NO_TAKE(23, "Item Attributes", "Cannot be", "Taken", ""), 
        NO_SPELLS(24, "Item Restrictions", "Cannot be", "Cast Upon", ""), 
        NO_BASH(25, "Item Restrictions", "Cannot be", "Bashed / Destroyed", ""), 
        NOT_LOCKABLE(26, "Item Restrictions", "Cannot be", "Locked", ""), 
        NOT_LOCKPICKABLE(27, "Item Restrictions", "Cannot be", "Lockpicked", ""), 
        NOT_MOVEABLE(28, "Item Restrictions", "Cannot be", "Moved", ""), 
        NOT_TURNABLE(29, "Item Restrictions", "Cannot be", "Turned", ""), 
        NOT_PAINTABLE(30, "Item Restrictions", "Cannot be", "Painted", ""), 
        NO_PUT(31, "Item Attributes", "Cannot", "Put items inside", "");
        
        final byte bit;
        final String description;
        final String header1;
        final String header2;
        final String hover;
        private static final Allow[] types;
        
        private Allow(final int aBit, final String aDescription, final String aHeader1, final String aHeader2, final String aHover) {
            this.bit = (byte)aBit;
            this.description = aDescription;
            this.header1 = aHeader1;
            this.header2 = aHeader2;
            this.hover = aHover;
        }
        
        @Override
        public byte getBit() {
            return this.bit;
        }
        
        @Override
        public int getValue() {
            return 1 << this.bit;
        }
        
        @Override
        public String getDescription() {
            return this.description;
        }
        
        @Override
        public String getHeader1() {
            return this.header1;
        }
        
        @Override
        public String getHeader2() {
            return this.header2;
        }
        
        @Override
        public String getHover() {
            return this.hover;
        }
        
        public static IPermission[] getPermissions() {
            return Allow.types;
        }
        
        static {
            types = values();
        }
    }
    
    public interface IPermission
    {
        byte getBit();
        
        int getValue();
        
        String getDescription();
        
        String getHeader1();
        
        String getHeader2();
        
        String getHover();
    }
    
    public interface IAllow
    {
        boolean canBeAlwaysLit();
        
        boolean canBeAutoFilled();
        
        boolean canBeAutoLit();
        
        boolean canBePeggedByPlayer();
        
        boolean canBePlanted();
        
        boolean canBeSealedByPlayer();
        
        boolean canChangeCreator();
        
        boolean canDisableDecay();
        
        boolean canDisableDestroy();
        
        boolean canDisableDrag();
        
        boolean canDisableDrop();
        
        boolean canDisableEatAndDrink();
        
        boolean canDisableImprove();
        
        boolean canDisableLocking();
        
        boolean canDisableLockpicking();
        
        boolean canDisableMoveable();
        
        boolean canDisableOwnerMoveing();
        
        boolean canDisableOwnerTurning();
        
        boolean canDisablePainting();
        
        boolean canDisablePut();
        
        boolean canDisableRepair();
        
        boolean canDisableRuneing();
        
        boolean canDisableSpellTarget();
        
        boolean canDisableTake();
        
        boolean canDisableTurning();
        
        boolean canHaveCourier();
        
        boolean canHaveDakrMessenger();
        
        String getCreatorName();
        
        float getDamage();
        
        String getName();
        
        float getQualityLevel();
        
        boolean hasCourier();
        
        boolean hasDarkMessenger();
        
        boolean hasNoDecay();
        
        boolean isAlwaysLit();
        
        boolean isAutoFilled();
        
        boolean isAutoLit();
        
        boolean isIndestructible();
        
        boolean isNoDrag();
        
        boolean isNoDrop();
        
        boolean isNoEatOrDrink();
        
        boolean isNoImprove();
        
        boolean isNoMove();
        
        boolean isNoPut();
        
        boolean isNoRepair();
        
        boolean isNoTake();
        
        boolean isNotLockable();
        
        boolean isNotLockpickable();
        
        boolean isNotPaintable();
        
        boolean isNotRuneable();
        
        boolean isNotSpellTarget();
        
        boolean isNotTurnable();
        
        boolean isOwnerMoveable();
        
        boolean isOwnerTurnable();
        
        boolean isPlanted();
        
        boolean isSealedByPlayer();
        
        void setCreator(final String p0);
        
        boolean setDamage(final float p0);
        
        void setHasCourier(final boolean p0);
        
        void setHasDarkMessenger(final boolean p0);
        
        void setHasNoDecay(final boolean p0);
        
        void setIsAlwaysLit(final boolean p0);
        
        void setIsAutoFilled(final boolean p0);
        
        void setIsAutoLit(final boolean p0);
        
        void setIsIndestructible(final boolean p0);
        
        void setIsNoDrag(final boolean p0);
        
        void setIsNoDrop(final boolean p0);
        
        void setIsNoEatOrDrink(final boolean p0);
        
        void setIsNoImprove(final boolean p0);
        
        void setIsNoMove(final boolean p0);
        
        void setIsNoPut(final boolean p0);
        
        void setIsNoRepair(final boolean p0);
        
        void setIsNoTake(final boolean p0);
        
        void setIsNotLockable(final boolean p0);
        
        void setIsNotLockpickable(final boolean p0);
        
        void setIsNotPaintable(final boolean p0);
        
        void setIsNotRuneable(final boolean p0);
        
        void setIsNotSpellTarget(final boolean p0);
        
        void setIsNotTurnable(final boolean p0);
        
        void setIsOwnerMoveable(final boolean p0);
        
        void setIsOwnerTurnable(final boolean p0);
        
        void setIsPlanted(final boolean p0);
        
        void setIsSealedByPlayer(final boolean p0);
        
        boolean setQualityLevel(final float p0);
        
        void setOriginalQualityLevel(final float p0);
        
        void savePermissions();
    }
}
