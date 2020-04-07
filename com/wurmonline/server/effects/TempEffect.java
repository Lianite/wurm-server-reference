// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.effects;

import java.io.IOException;

public class TempEffect extends Effect
{
    public TempEffect(final long aOwner, final short aType, final float aPosX, final float aPosY, final float aPosZ, final boolean aSurfaced) {
        super(aOwner, aType, aPosX, aPosY, aPosZ, aSurfaced);
    }
    
    public TempEffect(final int num, final long ownerid, final short typ, final float posx, final float posy, final float posz, final long stime) {
        super(num, ownerid, typ, posx, posy, posz, stime);
    }
    
    public TempEffect(final long aOwner, final int aNumber) throws IOException {
        super(aOwner, aNumber);
    }
    
    @Override
    public void save() throws IOException {
    }
    
    @Override
    void load() throws IOException {
    }
    
    @Override
    void delete() {
    }
}
