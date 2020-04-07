// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.loot;

import com.wurmonline.server.creatures.Creature;

public class DefaultActiveFunc implements ActiveFunc
{
    @Override
    public boolean active(final Creature victim, final Creature receiver) {
        return true;
    }
}
