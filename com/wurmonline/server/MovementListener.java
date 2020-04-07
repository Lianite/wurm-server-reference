// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.creatures.NoSuchCreatureException;

public interface MovementListener
{
    void creatureMoved(final long p0, final float p1, final float p2, final float p3, final int p4, final int p5) throws NoSuchCreatureException, NoSuchPlayerException;
}
