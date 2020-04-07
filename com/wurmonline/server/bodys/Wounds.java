// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSpaceException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Wounds implements MiscConstants
{
    private static final Logger logger;
    private Map<Long, Wound> wounds;
    private static final Map<Long, Wound> allWounds;
    public static final Wound[] emptyWounds;
    
    public Wound[] getWounds() {
        Wound[] toReturn = null;
        if (this.wounds == null || this.wounds.size() == 0) {
            toReturn = Wounds.emptyWounds;
        }
        else {
            toReturn = this.wounds.values().toArray(new Wound[this.wounds.size()]);
        }
        return toReturn;
    }
    
    boolean hasWounds() {
        return this.wounds != null && !this.wounds.isEmpty();
    }
    
    void addWound(final Wound wound) {
        if (this.wounds == null) {
            this.wounds = new HashMap<Long, Wound>();
        }
        this.wounds.put(new Long(wound.getWurmId()), wound);
        Wounds.allWounds.put(new Long(wound.getWurmId()), wound);
        if (wound.getCreature() != null) {
            try {
                final Item bodypart = wound.getCreature().getBody().getBodyPartForWound(wound);
                try {
                    final Creature[] watchers = bodypart.getWatchers();
                    for (int x = 0; x < watchers.length; ++x) {
                        watchers[x].getCommunicator().sendAddWound(wound, bodypart);
                    }
                }
                catch (NoSuchCreatureException ex) {}
            }
            catch (NoSpaceException nsp) {
                Wounds.logger.log(Level.INFO, nsp.getMessage(), nsp);
            }
        }
    }
    
    public Wound getWound(final long id) {
        Wound toReturn = null;
        if (this.wounds != null) {
            toReturn = this.wounds.get(new Long(id));
        }
        return toReturn;
    }
    
    public Wound getWoundAtLocation(final byte location) {
        if (this.wounds != null) {
            final Wound[] w = this.getWounds();
            for (int x = 0; x < w.length; ++x) {
                if (w[x].getLocation() == location) {
                    return w[x];
                }
            }
        }
        return null;
    }
    
    public Wound getWoundTypeAtLocation(final byte location, final byte type) {
        if (this.wounds != null) {
            final Wound[] w = this.getWounds();
            for (int x = 0; x < w.length; ++x) {
                if (w[x].getLocation() == location && w[x].getType() == type) {
                    return w[x];
                }
            }
        }
        return null;
    }
    
    public static Wound getAnyWound(final long id) {
        return Wounds.allWounds.get(new Long(id));
    }
    
    void remove(final Wound wound) {
        if (this.wounds != null) {
            wound.removeAllModifiers();
            this.wounds.remove(new Long(wound.getWurmId()));
            Wounds.allWounds.remove(new Long(wound.getWurmId()));
            wound.delete();
            if (this.wounds.size() == 0) {
                this.wounds = null;
            }
            if (wound.getCreature() != null) {
                try {
                    final Item bodypart = wound.getCreature().getBody().getBodyPartForWound(wound);
                    final Creature[] watchers = bodypart.getWatchers();
                    for (int x = 0; x < watchers.length; ++x) {
                        watchers[x].getCommunicator().sendRemoveWound(wound);
                    }
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSpaceException nsp) {
                    Wounds.logger.log(Level.INFO, nsp.getMessage(), nsp);
                }
                wound.removeCreature();
            }
        }
    }
    
    void poll(final Creature holder) {
        if (this.wounds != null) {
            final boolean woundPrevention = holder != null && holder.hasFingerOfFoBonus();
            final Wound[] w = this.getWounds();
            for (int x = 0; x < w.length; ++x) {
                try {
                    w[x].poll(woundPrevention);
                }
                catch (Exception ex) {
                    Wounds.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }
    
    static final int getModifiedSkill(final int woundPos) {
        return getModifiedSkill(woundPos, (byte)0);
    }
    
    static final int getModifiedSkill(final int woundPos, final byte woundType) {
        if (woundPos == 1) {
            if (woundType == 9) {
                return 10067;
            }
            return 100;
        }
        else {
            if (woundPos == 21) {
                return 10073;
            }
            if (woundPos == 13 || woundPos == 14) {
                return 10056;
            }
            if (woundPos == 9 || woundPos == 10) {
                return 1030;
            }
            if (woundPos == 3) {
                return 1002;
            }
            if (woundPos == 22 || woundPos == 24) {
                return 102;
            }
            if (woundPos == 25) {
                return 104;
            }
            if (woundPos == 29) {
                return 101;
            }
            if (woundPos == 33 || woundPos == 17) {
                return 10067;
            }
            return -1;
        }
    }
    
    static {
        logger = Logger.getLogger(Wounds.class.getName());
        allWounds = new HashMap<Long, Wound>();
        emptyWounds = new Wound[0];
    }
}
