// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.shared.constants.AttitudeConstants;

public abstract class KarmaSpell extends Spell implements AttitudeConstants
{
    public KarmaSpell(final String aName, final int aNum, final int aCastingTime, final int aCost, final int aDifficulty, final int aLevel, final long cooldown) {
        super(aName, aNum, aCastingTime, aCost, aDifficulty, aLevel, cooldown, false);
        this.karmaSpell = true;
    }
}
