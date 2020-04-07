// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Collection;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.model.meta.LocalService;

public interface ServiceManager<T>
{
    public static final String EVENTED_STATE_VARIABLES = "_EventedStateVariables";
    
    LocalService<T> getService();
    
    T getImplementation();
    
    void execute(final Command<T> p0) throws Exception;
    
    PropertyChangeSupport getPropertyChangeSupport();
    
    Collection<StateVariableValue> getCurrentState() throws Exception;
}
