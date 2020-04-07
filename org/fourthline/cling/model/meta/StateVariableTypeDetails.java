// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ValidationError;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.fourthline.cling.model.types.Datatype;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class StateVariableTypeDetails implements Validatable
{
    private static final Logger log;
    private final Datatype datatype;
    private final String defaultValue;
    private final String[] allowedValues;
    private final StateVariableAllowedValueRange allowedValueRange;
    
    public StateVariableTypeDetails(final Datatype datatype) {
        this(datatype, null, null, null);
    }
    
    public StateVariableTypeDetails(final Datatype datatype, final String defaultValue) {
        this(datatype, defaultValue, null, null);
    }
    
    public StateVariableTypeDetails(final Datatype datatype, final String defaultValue, final String[] allowedValues, final StateVariableAllowedValueRange allowedValueRange) {
        this.datatype = datatype;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
        this.allowedValueRange = allowedValueRange;
    }
    
    public Datatype getDatatype() {
        return this.datatype;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public String[] getAllowedValues() {
        if (!this.foundDefaultInAllowedValues(this.defaultValue, this.allowedValues)) {
            final List<String> list = new ArrayList<String>(Arrays.asList(this.allowedValues));
            list.add(this.getDefaultValue());
            return list.toArray(new String[list.size()]);
        }
        return this.allowedValues;
    }
    
    public StateVariableAllowedValueRange getAllowedValueRange() {
        return this.allowedValueRange;
    }
    
    protected boolean foundDefaultInAllowedValues(final String defaultValue, final String[] allowedValues) {
        if (defaultValue == null || allowedValues == null) {
            return true;
        }
        for (final String s : allowedValues) {
            if (s.equals(defaultValue)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getDatatype() == null) {
            errors.add(new ValidationError(this.getClass(), "datatype", "Service state variable has no datatype"));
        }
        if (this.getAllowedValues() != null) {
            if (this.getAllowedValueRange() != null) {
                errors.add(new ValidationError(this.getClass(), "allowedValues", "Allowed value list of state variable can not also be restricted with allowed value range"));
            }
            if (!Datatype.Builtin.STRING.equals(this.getDatatype().getBuiltin())) {
                errors.add(new ValidationError(this.getClass(), "allowedValues", "Allowed value list of state variable only available for string datatype, not: " + this.getDatatype()));
            }
            for (final String s : this.getAllowedValues()) {
                if (s.length() > 31) {
                    StateVariableTypeDetails.log.warning("UPnP specification violation, allowed value string must be less than 32 chars: " + s);
                }
            }
            if (!this.foundDefaultInAllowedValues(this.defaultValue, this.allowedValues)) {
                StateVariableTypeDetails.log.warning("UPnP specification violation, allowed string values don't contain default value: " + this.defaultValue);
            }
        }
        if (this.getAllowedValueRange() != null) {
            errors.addAll(this.getAllowedValueRange().validate());
        }
        return errors;
    }
    
    static {
        log = Logger.getLogger(StateVariableTypeDetails.class.getName());
    }
}
