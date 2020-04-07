// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.annotations;

import org.fourthline.cling.binding.AllowedValueRangeProvider;
import org.fourthline.cling.binding.AllowedValueProvider;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;
import org.fourthline.cling.model.meta.StateVariableEventDetails;
import org.fourthline.cling.model.meta.StateVariableTypeDetails;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.meta.StateVariable;
import java.util.Set;
import org.fourthline.cling.model.state.StateVariableAccessor;
import java.util.logging.Logger;

public class AnnotationStateVariableBinder
{
    private static Logger log;
    protected UpnpStateVariable annotation;
    protected String name;
    protected StateVariableAccessor accessor;
    protected Set<Class> stringConvertibleTypes;
    
    public AnnotationStateVariableBinder(final UpnpStateVariable annotation, final String name, final StateVariableAccessor accessor, final Set<Class> stringConvertibleTypes) {
        this.annotation = annotation;
        this.name = name;
        this.accessor = accessor;
        this.stringConvertibleTypes = stringConvertibleTypes;
    }
    
    public UpnpStateVariable getAnnotation() {
        return this.annotation;
    }
    
    public String getName() {
        return this.name;
    }
    
    public StateVariableAccessor getAccessor() {
        return this.accessor;
    }
    
    public Set<Class> getStringConvertibleTypes() {
        return this.stringConvertibleTypes;
    }
    
    protected StateVariable createStateVariable() throws LocalServiceBindingException {
        AnnotationStateVariableBinder.log.fine("Creating state variable '" + this.getName() + "' with accessor: " + this.getAccessor());
        final Datatype datatype = this.createDatatype();
        final String defaultValue = this.createDefaultValue(datatype);
        String[] allowedValues = null;
        if (Datatype.Builtin.STRING.equals(datatype.getBuiltin())) {
            if (this.getAnnotation().allowedValueProvider() != Void.TYPE) {
                allowedValues = this.getAllowedValuesFromProvider();
            }
            else if (this.getAnnotation().allowedValues().length > 0) {
                allowedValues = this.getAnnotation().allowedValues();
            }
            else if (this.getAnnotation().allowedValuesEnum() != Void.TYPE) {
                allowedValues = this.getAllowedValues(this.getAnnotation().allowedValuesEnum());
            }
            else if (this.getAccessor() != null && this.getAccessor().getReturnType().isEnum()) {
                allowedValues = this.getAllowedValues(this.getAccessor().getReturnType());
            }
            else {
                AnnotationStateVariableBinder.log.finer("Not restricting allowed values (of string typed state var): " + this.getName());
            }
            if (allowedValues != null && defaultValue != null) {
                boolean foundValue = false;
                for (final String s : allowedValues) {
                    if (s.equals(defaultValue)) {
                        foundValue = true;
                        break;
                    }
                }
                if (!foundValue) {
                    throw new LocalServiceBindingException("Default value '" + defaultValue + "' is not in allowed values of: " + this.getName());
                }
            }
        }
        StateVariableAllowedValueRange allowedValueRange = null;
        if (Datatype.Builtin.isNumeric(datatype.getBuiltin())) {
            if (this.getAnnotation().allowedValueRangeProvider() != Void.TYPE) {
                allowedValueRange = this.getAllowedRangeFromProvider();
            }
            else if (this.getAnnotation().allowedValueMinimum() > 0L || this.getAnnotation().allowedValueMaximum() > 0L) {
                allowedValueRange = this.getAllowedValueRange(this.getAnnotation().allowedValueMinimum(), this.getAnnotation().allowedValueMaximum(), this.getAnnotation().allowedValueStep());
            }
            else {
                AnnotationStateVariableBinder.log.finer("Not restricting allowed value range (of numeric typed state var): " + this.getName());
            }
            if (defaultValue != null && allowedValueRange != null) {
                long v;
                try {
                    v = Long.valueOf(defaultValue);
                }
                catch (Exception ex) {
                    throw new LocalServiceBindingException("Default value '" + defaultValue + "' is not numeric (for range checking) of: " + this.getName());
                }
                if (!allowedValueRange.isInRange(v)) {
                    throw new LocalServiceBindingException("Default value '" + defaultValue + "' is not in allowed range of: " + this.getName());
                }
            }
        }
        final boolean sendEvents = this.getAnnotation().sendEvents();
        if (sendEvents && this.getAccessor() == null) {
            throw new LocalServiceBindingException("State variable sends events but has no accessor for field or getter: " + this.getName());
        }
        int eventMaximumRateMillis = 0;
        int eventMinimumDelta = 0;
        if (sendEvents) {
            if (this.getAnnotation().eventMaximumRateMilliseconds() > 0) {
                AnnotationStateVariableBinder.log.finer("Moderating state variable events using maximum rate (milliseconds): " + this.getAnnotation().eventMaximumRateMilliseconds());
                eventMaximumRateMillis = this.getAnnotation().eventMaximumRateMilliseconds();
            }
            if (this.getAnnotation().eventMinimumDelta() > 0 && Datatype.Builtin.isNumeric(datatype.getBuiltin())) {
                AnnotationStateVariableBinder.log.finer("Moderating state variable events using minimum delta: " + this.getAnnotation().eventMinimumDelta());
                eventMinimumDelta = this.getAnnotation().eventMinimumDelta();
            }
        }
        final StateVariableTypeDetails typeDetails = new StateVariableTypeDetails(datatype, defaultValue, allowedValues, allowedValueRange);
        final StateVariableEventDetails eventDetails = new StateVariableEventDetails(sendEvents, eventMaximumRateMillis, eventMinimumDelta);
        return new StateVariable(this.getName(), typeDetails, eventDetails);
    }
    
    protected Datatype createDatatype() throws LocalServiceBindingException {
        String declaredDatatype = this.getAnnotation().datatype();
        if (declaredDatatype.length() == 0 && this.getAccessor() != null) {
            final Class returnType = this.getAccessor().getReturnType();
            AnnotationStateVariableBinder.log.finer("Using accessor return type as state variable type: " + returnType);
            if (ModelUtil.isStringConvertibleType(this.getStringConvertibleTypes(), returnType)) {
                AnnotationStateVariableBinder.log.finer("Return type is string-convertible, using string datatype");
                return Datatype.Default.STRING.getBuiltinType().getDatatype();
            }
            final Datatype.Default defaultDatatype = Datatype.Default.getByJavaType(returnType);
            if (defaultDatatype != null) {
                AnnotationStateVariableBinder.log.finer("Return type has default UPnP datatype: " + defaultDatatype);
                return defaultDatatype.getBuiltinType().getDatatype();
            }
        }
        if ((declaredDatatype == null || declaredDatatype.length() == 0) && (this.getAnnotation().allowedValues().length > 0 || this.getAnnotation().allowedValuesEnum() != Void.TYPE)) {
            AnnotationStateVariableBinder.log.finer("State variable has restricted allowed values, hence using 'string' datatype");
            declaredDatatype = "string";
        }
        if (declaredDatatype == null || declaredDatatype.length() == 0) {
            throw new LocalServiceBindingException("Could not detect datatype of state variable: " + this.getName());
        }
        AnnotationStateVariableBinder.log.finer("Trying to find built-in UPnP datatype for detected name: " + declaredDatatype);
        final Datatype.Builtin builtin = Datatype.Builtin.getByDescriptorName(declaredDatatype);
        if (builtin != null) {
            AnnotationStateVariableBinder.log.finer("Found built-in UPnP datatype: " + builtin);
            return builtin.getDatatype();
        }
        throw new LocalServiceBindingException("No built-in UPnP datatype found, using CustomDataType (TODO: NOT IMPLEMENTED)");
    }
    
    protected String createDefaultValue(final Datatype datatype) throws LocalServiceBindingException {
        if (this.getAnnotation().defaultValue().length() != 0) {
            try {
                datatype.valueOf(this.getAnnotation().defaultValue());
                AnnotationStateVariableBinder.log.finer("Found state variable default value: " + this.getAnnotation().defaultValue());
                return this.getAnnotation().defaultValue();
            }
            catch (Exception ex) {
                throw new LocalServiceBindingException("Default value doesn't match datatype of state variable '" + this.getName() + "': " + ex.getMessage());
            }
        }
        return null;
    }
    
    protected String[] getAllowedValues(final Class enumType) throws LocalServiceBindingException {
        if (!enumType.isEnum()) {
            throw new LocalServiceBindingException("Allowed values type is not an Enum: " + enumType);
        }
        AnnotationStateVariableBinder.log.finer("Restricting allowed values of state variable to Enum: " + this.getName());
        final String[] allowedValueStrings = new String[enumType.getEnumConstants().length];
        for (int i = 0; i < enumType.getEnumConstants().length; ++i) {
            final Object o = enumType.getEnumConstants()[i];
            if (o.toString().length() > 32) {
                throw new LocalServiceBindingException("Allowed value string (that is, Enum constant name) is longer than 32 characters: " + o.toString());
            }
            AnnotationStateVariableBinder.log.finer("Adding allowed value (converted to string): " + o.toString());
            allowedValueStrings[i] = o.toString();
        }
        return allowedValueStrings;
    }
    
    protected StateVariableAllowedValueRange getAllowedValueRange(final long min, final long max, final long step) throws LocalServiceBindingException {
        if (max < min) {
            throw new LocalServiceBindingException("Allowed value range maximum is smaller than minimum: " + this.getName());
        }
        return new StateVariableAllowedValueRange(min, max, step);
    }
    
    protected String[] getAllowedValuesFromProvider() throws LocalServiceBindingException {
        final Class provider = this.getAnnotation().allowedValueProvider();
        if (!AllowedValueProvider.class.isAssignableFrom(provider)) {
            throw new LocalServiceBindingException("Allowed value provider is not of type " + AllowedValueProvider.class + ": " + this.getName());
        }
        try {
            return provider.newInstance().getValues();
        }
        catch (Exception ex) {
            throw new LocalServiceBindingException("Allowed value provider can't be instantiated: " + this.getName(), ex);
        }
    }
    
    protected StateVariableAllowedValueRange getAllowedRangeFromProvider() throws LocalServiceBindingException {
        final Class provider = this.getAnnotation().allowedValueRangeProvider();
        if (!AllowedValueRangeProvider.class.isAssignableFrom(provider)) {
            throw new LocalServiceBindingException("Allowed value range provider is not of type " + AllowedValueRangeProvider.class + ": " + this.getName());
        }
        try {
            final AllowedValueRangeProvider providerInstance = provider.newInstance();
            return this.getAllowedValueRange(providerInstance.getMinimum(), providerInstance.getMaximum(), providerInstance.getStep());
        }
        catch (Exception ex) {
            throw new LocalServiceBindingException("Allowed value range provider can't be instantiated: " + this.getName(), ex);
        }
    }
    
    static {
        AnnotationStateVariableBinder.log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());
    }
}
