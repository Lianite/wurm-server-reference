// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.annotations;

import java.util.Locale;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.fourthline.cling.model.state.GetterStateVariableAccessor;
import org.fourthline.cling.model.state.FieldStateVariableAccessor;
import org.seamless.util.Reflections;
import java.util.HashMap;
import org.fourthline.cling.model.types.csv.CSV;
import java.net.URL;
import java.net.URI;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.meta.StateVariable;
import java.util.Map;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.action.QueryStateVariableExecutor;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import java.lang.annotation.Annotation;
import org.fourthline.cling.model.meta.LocalService;
import java.util.logging.Logger;
import org.fourthline.cling.binding.LocalServiceBinder;

public class AnnotationLocalServiceBinder implements LocalServiceBinder
{
    private static Logger log;
    
    @Override
    public LocalService read(final Class<?> clazz) throws LocalServiceBindingException {
        AnnotationLocalServiceBinder.log.fine("Reading and binding annotations of service implementation class: " + clazz);
        if (clazz.isAnnotationPresent(UpnpService.class)) {
            final UpnpService annotation = clazz.getAnnotation(UpnpService.class);
            final UpnpServiceId idAnnotation = annotation.serviceId();
            final UpnpServiceType typeAnnotation = annotation.serviceType();
            final ServiceId serviceId = idAnnotation.namespace().equals("upnp-org") ? new UDAServiceId(idAnnotation.value()) : new ServiceId(idAnnotation.namespace(), idAnnotation.value());
            final ServiceType serviceType = typeAnnotation.namespace().equals("schemas-upnp-org") ? new UDAServiceType(typeAnnotation.value(), typeAnnotation.version()) : new ServiceType(typeAnnotation.namespace(), typeAnnotation.value(), typeAnnotation.version());
            final boolean supportsQueryStateVariables = annotation.supportsQueryStateVariables();
            final Set<Class> stringConvertibleTypes = this.readStringConvertibleTypes(annotation.stringConvertibleTypes());
            return this.read(clazz, serviceId, serviceType, supportsQueryStateVariables, stringConvertibleTypes);
        }
        throw new LocalServiceBindingException("Given class is not an @UpnpService");
    }
    
    @Override
    public LocalService read(final Class<?> clazz, final ServiceId id, final ServiceType type, final boolean supportsQueryStateVariables, final Class[] stringConvertibleTypes) throws LocalServiceBindingException {
        return this.read(clazz, id, type, supportsQueryStateVariables, new HashSet<Class>(Arrays.asList((Class[])stringConvertibleTypes)));
    }
    
    public LocalService read(final Class<?> clazz, final ServiceId id, final ServiceType type, final boolean supportsQueryStateVariables, final Set<Class> stringConvertibleTypes) throws LocalServiceBindingException {
        final Map<StateVariable, StateVariableAccessor> stateVariables = this.readStateVariables(clazz, stringConvertibleTypes);
        final Map<Action, ActionExecutor> actions = this.readActions(clazz, stateVariables, stringConvertibleTypes);
        if (supportsQueryStateVariables) {
            actions.put(new QueryStateVariableAction(), new QueryStateVariableExecutor());
        }
        try {
            return new LocalService(type, id, actions, stateVariables, stringConvertibleTypes, supportsQueryStateVariables);
        }
        catch (ValidationException ex) {
            AnnotationLocalServiceBinder.log.severe("Could not validate device model: " + ex.toString());
            for (final ValidationError validationError : ex.getErrors()) {
                AnnotationLocalServiceBinder.log.severe(validationError.toString());
            }
            throw new LocalServiceBindingException("Validation of model failed, check the log");
        }
    }
    
    protected Set<Class> readStringConvertibleTypes(final Class[] declaredTypes) throws LocalServiceBindingException {
        for (final Class stringConvertibleType : declaredTypes) {
            if (!Modifier.isPublic(stringConvertibleType.getModifiers())) {
                throw new LocalServiceBindingException("Declared string-convertible type must be public: " + stringConvertibleType);
            }
            try {
                stringConvertibleType.getConstructor(String.class);
            }
            catch (NoSuchMethodException ex) {
                throw new LocalServiceBindingException("Declared string-convertible type needs a public single-argument String constructor: " + stringConvertibleType);
            }
        }
        final Set<Class> stringConvertibleTypes = new HashSet<Class>(Arrays.asList((Class[])declaredTypes));
        stringConvertibleTypes.add(URI.class);
        stringConvertibleTypes.add(URL.class);
        stringConvertibleTypes.add(CSV.class);
        return stringConvertibleTypes;
    }
    
    protected Map<StateVariable, StateVariableAccessor> readStateVariables(final Class<?> clazz, final Set<Class> stringConvertibleTypes) throws LocalServiceBindingException {
        final Map<StateVariable, StateVariableAccessor> map = new HashMap<StateVariable, StateVariableAccessor>();
        if (clazz.isAnnotationPresent(UpnpStateVariables.class)) {
            final UpnpStateVariables variables = clazz.getAnnotation(UpnpStateVariables.class);
            for (final UpnpStateVariable v : variables.value()) {
                if (v.name().length() == 0) {
                    throw new LocalServiceBindingException("Class-level @UpnpStateVariable name attribute value required");
                }
                final String javaPropertyName = toJavaStateVariableName(v.name());
                final Method getter = Reflections.getGetterMethod(clazz, javaPropertyName);
                final Field field = Reflections.getField(clazz, javaPropertyName);
                StateVariableAccessor accessor = null;
                if (getter != null && field != null) {
                    accessor = (variables.preferFields() ? new FieldStateVariableAccessor(field) : new GetterStateVariableAccessor(getter));
                }
                else if (field != null) {
                    accessor = new FieldStateVariableAccessor(field);
                }
                else if (getter != null) {
                    accessor = new GetterStateVariableAccessor(getter);
                }
                else {
                    AnnotationLocalServiceBinder.log.finer("No field or getter found for state variable, skipping accessor: " + v.name());
                }
                final StateVariable stateVar = new AnnotationStateVariableBinder(v, v.name(), accessor, stringConvertibleTypes).createStateVariable();
                map.put(stateVar, accessor);
            }
        }
        for (final Field field2 : Reflections.getFields(clazz, UpnpStateVariable.class)) {
            final UpnpStateVariable svAnnotation = field2.getAnnotation(UpnpStateVariable.class);
            final StateVariableAccessor accessor2 = new FieldStateVariableAccessor(field2);
            final StateVariable stateVar2 = new AnnotationStateVariableBinder(svAnnotation, (svAnnotation.name().length() == 0) ? toUpnpStateVariableName(field2.getName()) : svAnnotation.name(), accessor2, stringConvertibleTypes).createStateVariable();
            map.put(stateVar2, accessor2);
        }
        for (final Method getter2 : Reflections.getMethods(clazz, UpnpStateVariable.class)) {
            final String propertyName = Reflections.getMethodPropertyName(getter2.getName());
            if (propertyName == null) {
                throw new LocalServiceBindingException("Annotated method is not a getter method (: " + getter2);
            }
            if (getter2.getParameterTypes().length > 0) {
                throw new LocalServiceBindingException("Getter method defined as @UpnpStateVariable can not have parameters: " + getter2);
            }
            final UpnpStateVariable svAnnotation2 = getter2.getAnnotation(UpnpStateVariable.class);
            final StateVariableAccessor accessor3 = new GetterStateVariableAccessor(getter2);
            final StateVariable stateVar3 = new AnnotationStateVariableBinder(svAnnotation2, (svAnnotation2.name().length() == 0) ? toUpnpStateVariableName(propertyName) : svAnnotation2.name(), accessor3, stringConvertibleTypes).createStateVariable();
            map.put(stateVar3, accessor3);
        }
        return map;
    }
    
    protected Map<Action, ActionExecutor> readActions(final Class<?> clazz, final Map<StateVariable, StateVariableAccessor> stateVariables, final Set<Class> stringConvertibleTypes) throws LocalServiceBindingException {
        final Map<Action, ActionExecutor> map = new HashMap<Action, ActionExecutor>();
        for (final Method method : Reflections.getMethods(clazz, UpnpAction.class)) {
            final AnnotationActionBinder actionBinder = new AnnotationActionBinder(method, stateVariables, stringConvertibleTypes);
            final Action action = actionBinder.appendAction(map);
            if (this.isActionExcluded(action)) {
                map.remove(action);
            }
        }
        return map;
    }
    
    protected boolean isActionExcluded(final Action action) {
        return false;
    }
    
    static String toUpnpStateVariableName(final String javaName) {
        if (javaName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return javaName.substring(0, 1).toUpperCase(Locale.ROOT) + javaName.substring(1);
    }
    
    static String toJavaStateVariableName(final String upnpName) {
        if (upnpName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return upnpName.substring(0, 1).toLowerCase(Locale.ROOT) + upnpName.substring(1);
    }
    
    static String toUpnpActionName(final String javaName) {
        if (javaName.length() < 1) {
            throw new IllegalArgumentException("Action name must be at least 1 character long");
        }
        return javaName.substring(0, 1).toUpperCase(Locale.ROOT) + javaName.substring(1);
    }
    
    static String toJavaActionName(final String upnpName) {
        if (upnpName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return upnpName.substring(0, 1).toLowerCase(Locale.ROOT) + upnpName.substring(1);
    }
    
    static {
        AnnotationLocalServiceBinder.log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());
    }
}
