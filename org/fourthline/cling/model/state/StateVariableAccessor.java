// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.state;

import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.Command;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.StateVariable;

public abstract class StateVariableAccessor
{
    public StateVariableValue read(final StateVariable<LocalService> stateVariable, final Object serviceImpl) throws Exception {
        class AccessCommand implements Command
        {
            Object result;
            
            @Override
            public void execute(final ServiceManager serviceManager) throws Exception {
                this.result = StateVariableAccessor.this.read(serviceImpl);
                if (stateVariable.getService().isStringConvertibleType(this.result)) {
                    this.result = this.result.toString();
                }
            }
        }
        final AccessCommand cmd = new AccessCommand();
        stateVariable.getService().getManager().execute(cmd);
        return new StateVariableValue((StateVariable<S>)stateVariable, cmd.result);
    }
    
    public abstract Class<?> getReturnType();
    
    public abstract Object read(final Object p0) throws Exception;
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ")";
    }
}
