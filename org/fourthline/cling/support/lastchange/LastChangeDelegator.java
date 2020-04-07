// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public interface LastChangeDelegator
{
    LastChange getLastChange();
    
    void appendCurrentState(final LastChange p0, final UnsignedIntegerFourBytes p1) throws Exception;
    
    UnsignedIntegerFourBytes[] getCurrentInstanceIds();
}
