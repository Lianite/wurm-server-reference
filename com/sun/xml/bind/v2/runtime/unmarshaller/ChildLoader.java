// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

public final class ChildLoader
{
    public final Loader loader;
    public final Receiver receiver;
    
    public ChildLoader(final Loader loader, final Receiver receiver) {
        assert loader != null;
        this.loader = loader;
        this.receiver = receiver;
    }
}
