// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

public final class Discarder extends Loader
{
    public static final Loader INSTANCE;
    
    private Discarder() {
        super(false);
    }
    
    public void childElement(final UnmarshallingContext.State state, final TagName ea) {
        state.target = null;
        state.loader = this;
    }
    
    static {
        INSTANCE = new Discarder();
    }
}
