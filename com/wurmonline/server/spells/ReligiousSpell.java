// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

abstract class ReligiousSpell extends Spell
{
    ReligiousSpell(final String aName, final int aNum, final int aCastingTime, final int aCost, final int aDifficulty, final int aLevel, final long cooldown) {
        super(aName, aNum, aCastingTime, aCost, aDifficulty, aLevel, cooldown, true);
    }
}
