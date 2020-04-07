// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.modifiers;

import java.util.HashSet;
import java.util.Set;

public abstract class ValueModifier implements ModifierTypes
{
    private final int type;
    private Set<ValueModifiedListener> listeners;
    
    ValueModifier() {
        this.type = 0;
    }
    
    ValueModifier(final int typ) {
        this.type = typ;
    }
    
    public final int getType() {
        return this.type;
    }
    
    public final void addListener(final ValueModifiedListener list) {
        if (this.listeners == null) {
            this.listeners = new HashSet<ValueModifiedListener>();
        }
        this.listeners.add(list);
    }
    
    public final void removeListener(final ValueModifiedListener list) {
        if (this.listeners == null) {
            this.listeners = new HashSet<ValueModifiedListener>();
        }
        else {
            this.listeners.remove(list);
        }
    }
    
    Set<ValueModifiedListener> getListeners() {
        return this.listeners;
    }
}
