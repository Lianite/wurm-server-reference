// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.statemachine;

public interface StateMachine<S>
{
    public static final String METHOD_CURRENT_STATE = "getCurrentState";
    public static final String METHOD_FORCE_STATE = "forceState";
    
    S getCurrentState();
    
    void forceState(final Class<? extends S> p0);
}
