// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.bodys;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NoSpaceException;
import java.util.logging.Level;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.logging.Logger;

public final class TempWound extends Wound
{
    private static final Logger logger;
    private static final long serialVersionUID = -7813873321822326094L;
    
    public TempWound(final byte aType, final byte aLocation, final float aSeverity, final long aOwner, final float aPoisonSeverity, final float aInfectionSeverity, final boolean spell) {
        super(aType, aLocation, aSeverity, aOwner, aPoisonSeverity, aInfectionSeverity, true, false, spell);
    }
    
    @Override
    final void create() {
    }
    
    @Override
    final void setSeverity(final float sev) {
        this.severity = sev;
    }
    
    @Override
    public final void setPoisonSeverity(final float sev) {
        if (this.poisonSeverity != sev) {
            this.poisonSeverity = Math.max(0.0f, sev);
            this.poisonSeverity = Math.min(100.0f, this.poisonSeverity);
        }
    }
    
    @Override
    public final void setInfectionSeverity(final float sev) {
        if (this.infectionSeverity != sev) {
            this.infectionSeverity = Math.max(0.0f, sev);
            this.infectionSeverity = Math.min(100.0f, this.infectionSeverity);
        }
    }
    
    @Override
    public final void setBandaged(final boolean aBandaged) {
        if (this.isBandaged != aBandaged) {
            this.isBandaged = aBandaged;
        }
    }
    
    @Override
    final void setLastPolled(final long lp) {
        if (this.lastPolled != lp) {
            this.lastPolled = lp;
        }
    }
    
    @Override
    public final void setHealeff(final byte healeff) {
        if (this.healEff < healeff) {
            this.healEff = healeff;
            try {
                if (this.getCreature().getBody() != null) {
                    final Item bodypart = this.getCreature().getBody().getBodyPartForWound(this);
                    try {
                        final Creature[] watchers = bodypart.getWatchers();
                        for (int x = 0; x < watchers.length; ++x) {
                            watchers[x].getCommunicator().sendUpdateWound(this, bodypart);
                        }
                    }
                    catch (NoSuchCreatureException ex) {}
                }
                else if (this.getCreature() != null) {
                    TempWound.logger.log(Level.WARNING, this.getCreature().getName() + " body is null.", new Exception());
                }
                else {
                    TempWound.logger.log(Level.WARNING, "Wound: creature==null", new Exception());
                }
            }
            catch (NoSpaceException nsp) {
                TempWound.logger.log(Level.INFO, nsp.getMessage(), nsp);
            }
        }
    }
    
    @Override
    final void delete() {
    }
    
    static {
        logger = Logger.getLogger(TempWound.class.getName());
    }
}
