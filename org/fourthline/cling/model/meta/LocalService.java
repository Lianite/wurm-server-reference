// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.ValidationException;
import java.util.HashSet;
import java.util.HashMap;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.ServiceManager;
import java.util.Set;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.action.ActionExecutor;
import java.util.Map;

public class LocalService<T> extends Service<LocalDevice, LocalService>
{
    protected final Map<Action, ActionExecutor> actionExecutors;
    protected final Map<StateVariable, StateVariableAccessor> stateVariableAccessors;
    protected final Set<Class> stringConvertibleTypes;
    protected final boolean supportsQueryStateVariables;
    protected ServiceManager manager;
    
    public LocalService(final ServiceType serviceType, final ServiceId serviceId, final Action[] actions, final StateVariable[] stateVariables) throws ValidationException {
        super(serviceType, serviceId, actions, stateVariables);
        this.manager = null;
        this.actionExecutors = new HashMap<Action, ActionExecutor>();
        this.stateVariableAccessors = new HashMap<StateVariable, StateVariableAccessor>();
        this.stringConvertibleTypes = new HashSet<Class>();
        this.supportsQueryStateVariables = true;
    }
    
    public LocalService(final ServiceType serviceType, final ServiceId serviceId, final Map<Action, ActionExecutor> actionExecutors, final Map<StateVariable, StateVariableAccessor> stateVariableAccessors, final Set<Class> stringConvertibleTypes, final boolean supportsQueryStateVariables) throws ValidationException {
        super(serviceType, serviceId, actionExecutors.keySet().toArray(new Action[actionExecutors.size()]), stateVariableAccessors.keySet().toArray(new StateVariable[stateVariableAccessors.size()]));
        this.supportsQueryStateVariables = supportsQueryStateVariables;
        this.stringConvertibleTypes = stringConvertibleTypes;
        this.stateVariableAccessors = stateVariableAccessors;
        this.actionExecutors = actionExecutors;
    }
    
    public synchronized void setManager(final ServiceManager<T> manager) {
        if (this.manager != null) {
            throw new IllegalStateException("Manager is final");
        }
        this.manager = manager;
    }
    
    public synchronized ServiceManager<T> getManager() {
        if (this.manager == null) {
            throw new IllegalStateException("Unmanaged service, no implementation instance available");
        }
        return (ServiceManager<T>)this.manager;
    }
    
    public boolean isSupportsQueryStateVariables() {
        return this.supportsQueryStateVariables;
    }
    
    public Set<Class> getStringConvertibleTypes() {
        return this.stringConvertibleTypes;
    }
    
    public boolean isStringConvertibleType(final Object o) {
        return o != null && this.isStringConvertibleType(o.getClass());
    }
    
    public boolean isStringConvertibleType(final Class clazz) {
        return ModelUtil.isStringConvertibleType(this.getStringConvertibleTypes(), clazz);
    }
    
    public StateVariableAccessor getAccessor(final String stateVariableName) {
        final StateVariable sv;
        return ((sv = this.getStateVariable(stateVariableName)) != null) ? this.getAccessor(sv) : null;
    }
    
    public StateVariableAccessor getAccessor(final StateVariable stateVariable) {
        return this.stateVariableAccessors.get(stateVariable);
    }
    
    public ActionExecutor getExecutor(final String actionName) {
        final Action action;
        return ((action = this.getAction(actionName)) != null) ? this.getExecutor(action) : null;
    }
    
    public ActionExecutor getExecutor(final Action action) {
        return this.actionExecutors.get(action);
    }
    
    @Override
    public Action getQueryStateVariableAction() {
        return this.getAction("QueryStateVariable");
    }
    
    @Override
    public String toString() {
        return super.toString() + ", Manager: " + this.manager;
    }
}
