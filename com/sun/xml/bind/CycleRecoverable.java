// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind;

import javax.xml.bind.Marshaller;

public interface CycleRecoverable
{
    Object onCycleDetected(final Context p0);
    
    public interface Context
    {
        Marshaller getMarshaller();
    }
}
