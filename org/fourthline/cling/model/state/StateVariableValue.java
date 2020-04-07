// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.state;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.VariableValue;
import org.fourthline.cling.model.meta.Service;

public class StateVariableValue<S extends Service> extends VariableValue
{
    private StateVariable<S> stateVariable;
    
    public StateVariableValue(final StateVariable<S> stateVariable, final Object value) throws InvalidValueException {
        super(stateVariable.getTypeDetails().getDatatype(), value);
        this.stateVariable = stateVariable;
    }
    
    public StateVariable<S> getStateVariable() {
        return this.stateVariable;
    }
}
