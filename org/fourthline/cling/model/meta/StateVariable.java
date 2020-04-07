// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.types.Datatype;
import java.util.Collection;
import org.fourthline.cling.model.ModelUtil;
import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class StateVariable<S extends Service> implements Validatable
{
    private static final Logger log;
    private final String name;
    private final StateVariableTypeDetails type;
    private final StateVariableEventDetails eventDetails;
    private S service;
    
    public StateVariable(final String name, final StateVariableTypeDetails type) {
        this(name, type, new StateVariableEventDetails());
    }
    
    public StateVariable(final String name, final StateVariableTypeDetails type, final StateVariableEventDetails eventDetails) {
        this.name = name;
        this.type = type;
        this.eventDetails = eventDetails;
    }
    
    public String getName() {
        return this.name;
    }
    
    public StateVariableTypeDetails getTypeDetails() {
        return this.type;
    }
    
    public StateVariableEventDetails getEventDetails() {
        return this.eventDetails;
    }
    
    public S getService() {
        return this.service;
    }
    
    void setService(final S service) {
        if (this.service != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.service = service;
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getName() == null || this.getName().length() == 0) {
            errors.add(new ValidationError(this.getClass(), "name", "StateVariable without name of: " + this.getService()));
        }
        else if (!ModelUtil.isValidUDAName(this.getName())) {
            StateVariable.log.warning("UPnP specification violation of: " + this.getService().getDevice());
            StateVariable.log.warning("Invalid state variable name: " + this);
        }
        errors.addAll(this.getTypeDetails().validate());
        return errors;
    }
    
    public boolean isModeratedNumericType() {
        return Datatype.Builtin.isNumeric(this.getTypeDetails().getDatatype().getBuiltin()) && this.getEventDetails().getEventMinimumDelta() > 0;
    }
    
    public StateVariable<S> deepCopy() {
        return new StateVariable<S>(this.getName(), this.getTypeDetails(), this.getEventDetails());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(").append(this.getClass().getSimpleName());
        sb.append(", Name: ").append(this.getName());
        sb.append(", Type: ").append(this.getTypeDetails().getDatatype().getDisplayString()).append(")");
        if (!this.getEventDetails().isSendEvents()) {
            sb.append(" (No Events)");
        }
        if (this.getTypeDetails().getDefaultValue() != null) {
            sb.append(" Default Value: ").append("'").append(this.getTypeDetails().getDefaultValue()).append("'");
        }
        if (this.getTypeDetails().getAllowedValues() != null) {
            sb.append(" Allowed Values: ");
            for (final String s : this.getTypeDetails().getAllowedValues()) {
                sb.append(s).append("|");
            }
        }
        return sb.toString();
    }
    
    static {
        log = Logger.getLogger(StateVariable.class.getName());
    }
}
