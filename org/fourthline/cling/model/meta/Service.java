// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.types.Datatype;
import java.util.HashMap;
import org.fourthline.cling.model.ValidationException;
import java.util.Map;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import java.util.logging.Logger;

public abstract class Service<D extends Device, S extends Service>
{
    private static final Logger log;
    private final ServiceType serviceType;
    private final ServiceId serviceId;
    private final Map<String, Action> actions;
    private final Map<String, StateVariable> stateVariables;
    private D device;
    
    public Service(final ServiceType serviceType, final ServiceId serviceId) throws ValidationException {
        this(serviceType, serviceId, null, null);
    }
    
    public Service(final ServiceType serviceType, final ServiceId serviceId, final Action<S>[] actions, final StateVariable<S>[] stateVariables) throws ValidationException {
        this.actions = new HashMap<String, Action>();
        this.stateVariables = new HashMap<String, StateVariable>();
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        if (actions != null) {
            for (final Action action : actions) {
                this.actions.put(action.getName(), action);
                action.setService(this);
            }
        }
        if (stateVariables != null) {
            for (final StateVariable stateVariable : stateVariables) {
                this.stateVariables.put(stateVariable.getName(), stateVariable);
                stateVariable.setService(this);
            }
        }
    }
    
    public ServiceType getServiceType() {
        return this.serviceType;
    }
    
    public ServiceId getServiceId() {
        return this.serviceId;
    }
    
    public boolean hasActions() {
        return this.getActions() != null && this.getActions().length > 0;
    }
    
    public Action<S>[] getActions() {
        return (Action<S>[])((this.actions == null) ? null : ((Action<S>[])this.actions.values().toArray(new Action[this.actions.values().size()])));
    }
    
    public boolean hasStateVariables() {
        return this.getStateVariables() != null && this.getStateVariables().length > 0;
    }
    
    public StateVariable<S>[] getStateVariables() {
        return (StateVariable<S>[])((this.stateVariables == null) ? null : ((StateVariable<S>[])this.stateVariables.values().toArray(new StateVariable[this.stateVariables.values().size()])));
    }
    
    public D getDevice() {
        return this.device;
    }
    
    void setDevice(final D device) {
        if (this.device != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.device = device;
    }
    
    public Action<S> getAction(final String name) {
        return (this.actions == null) ? null : this.actions.get(name);
    }
    
    public StateVariable<S> getStateVariable(final String name) {
        if ("VirtualQueryActionInput".equals(name)) {
            return new StateVariable<S>("VirtualQueryActionInput", new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()));
        }
        if ("VirtualQueryActionOutput".equals(name)) {
            return new StateVariable<S>("VirtualQueryActionOutput", new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()));
        }
        return (this.stateVariables == null) ? null : this.stateVariables.get(name);
    }
    
    public StateVariable<S> getRelatedStateVariable(final ActionArgument argument) {
        return this.getStateVariable(argument.getRelatedStateVariableName());
    }
    
    public Datatype<S> getDatatype(final ActionArgument argument) {
        return (Datatype<S>)this.getRelatedStateVariable(argument).getTypeDetails().getDatatype();
    }
    
    public ServiceReference getReference() {
        return new ServiceReference(this.getDevice().getIdentity().getUdn(), this.getServiceId());
    }
    
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getServiceType() == null) {
            errors.add(new ValidationError(this.getClass(), "serviceType", "Service type/info is required"));
        }
        if (this.getServiceId() == null) {
            errors.add(new ValidationError(this.getClass(), "serviceId", "Service ID is required"));
        }
        if (this.hasStateVariables()) {
            for (final StateVariable stateVariable : this.getStateVariables()) {
                errors.addAll(stateVariable.validate());
            }
        }
        if (this.hasActions()) {
            for (final Action action : this.getActions()) {
                final List<ValidationError> actionErrors = (List<ValidationError>)action.validate();
                if (actionErrors.size() > 0) {
                    this.actions.remove(action.getName());
                    Service.log.warning("Discarding invalid action of service '" + this.getServiceId() + "': " + action.getName());
                    for (final ValidationError actionError : actionErrors) {
                        Service.log.warning("Invalid action '" + action.getName() + "': " + actionError);
                    }
                }
            }
        }
        return errors;
    }
    
    public abstract Action getQueryStateVariableAction();
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") ServiceId: " + this.getServiceId();
    }
    
    static {
        log = Logger.getLogger(Service.class.getName());
    }
}
