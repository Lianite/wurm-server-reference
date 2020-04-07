// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.annotations;

import java.util.Iterator;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.state.GetterStateVariableAccessor;
import org.seamless.util.Reflections;
import java.util.LinkedHashMap;
import java.lang.annotation.Annotation;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import java.util.ArrayList;
import org.fourthline.cling.model.action.MethodActionExecutor;
import org.fourthline.cling.binding.LocalServiceBindingException;
import java.util.List;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ActionArgument;
import java.util.Collection;
import org.fourthline.cling.model.action.ActionExecutor;
import org.fourthline.cling.model.meta.Action;
import java.util.Set;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.meta.StateVariable;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class AnnotationActionBinder
{
    private static Logger log;
    protected UpnpAction annotation;
    protected Method method;
    protected Map<StateVariable, StateVariableAccessor> stateVariables;
    protected Set<Class> stringConvertibleTypes;
    
    public AnnotationActionBinder(final Method method, final Map<StateVariable, StateVariableAccessor> stateVariables, final Set<Class> stringConvertibleTypes) {
        this.annotation = method.getAnnotation(UpnpAction.class);
        this.stateVariables = stateVariables;
        this.method = method;
        this.stringConvertibleTypes = stringConvertibleTypes;
    }
    
    public UpnpAction getAnnotation() {
        return this.annotation;
    }
    
    public Map<StateVariable, StateVariableAccessor> getStateVariables() {
        return this.stateVariables;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    public Set<Class> getStringConvertibleTypes() {
        return this.stringConvertibleTypes;
    }
    
    public Action appendAction(final Map<Action, ActionExecutor> actions) throws LocalServiceBindingException {
        String name;
        if (this.getAnnotation().name().length() != 0) {
            name = this.getAnnotation().name();
        }
        else {
            name = AnnotationLocalServiceBinder.toUpnpActionName(this.getMethod().getName());
        }
        AnnotationActionBinder.log.fine("Creating action and executor: " + name);
        final List<ActionArgument> inputArguments = this.createInputArguments();
        final Map<ActionArgument<LocalService>, StateVariableAccessor> outputArguments = this.createOutputArguments();
        inputArguments.addAll(outputArguments.keySet());
        final ActionArgument<LocalService>[] actionArguments = inputArguments.toArray(new ActionArgument[inputArguments.size()]);
        final Action action = new Action(name, actionArguments);
        final ActionExecutor executor = this.createExecutor(outputArguments);
        actions.put(action, executor);
        return action;
    }
    
    protected ActionExecutor createExecutor(final Map<ActionArgument<LocalService>, StateVariableAccessor> outputArguments) {
        return new MethodActionExecutor(outputArguments, this.getMethod());
    }
    
    protected List<ActionArgument> createInputArguments() throws LocalServiceBindingException {
        final List<ActionArgument> list = new ArrayList<ActionArgument>();
        int annotatedParams = 0;
        final Annotation[][] params = this.getMethod().getParameterAnnotations();
        for (int i = 0; i < params.length; ++i) {
            final Annotation[] array;
            final Annotation[] param = array = params[i];
            for (final Annotation paramAnnotation : array) {
                if (paramAnnotation instanceof UpnpInputArgument) {
                    final UpnpInputArgument inputArgumentAnnotation = (UpnpInputArgument)paramAnnotation;
                    ++annotatedParams;
                    final String argumentName = inputArgumentAnnotation.name();
                    final StateVariable stateVariable = this.findRelatedStateVariable(inputArgumentAnnotation.stateVariable(), argumentName, this.getMethod().getName());
                    if (stateVariable == null) {
                        throw new LocalServiceBindingException("Could not detected related state variable of argument: " + argumentName);
                    }
                    this.validateType(stateVariable, this.getMethod().getParameterTypes()[i]);
                    final ActionArgument inputArgument = new ActionArgument(argumentName, inputArgumentAnnotation.aliases(), stateVariable.getName(), ActionArgument.Direction.IN);
                    list.add(inputArgument);
                }
            }
        }
        if (annotatedParams < this.getMethod().getParameterTypes().length && !RemoteClientInfo.class.isAssignableFrom(this.method.getParameterTypes()[this.method.getParameterTypes().length - 1])) {
            throw new LocalServiceBindingException("Method has parameters that are not input arguments: " + this.getMethod().getName());
        }
        return list;
    }
    
    protected Map<ActionArgument<LocalService>, StateVariableAccessor> createOutputArguments() throws LocalServiceBindingException {
        final Map<ActionArgument<LocalService>, StateVariableAccessor> map = new LinkedHashMap<ActionArgument<LocalService>, StateVariableAccessor>();
        final UpnpAction actionAnnotation = this.getMethod().getAnnotation(UpnpAction.class);
        if (actionAnnotation.out().length == 0) {
            return map;
        }
        final boolean hasMultipleOutputArguments = actionAnnotation.out().length > 1;
        for (final UpnpOutputArgument outputArgumentAnnotation : actionAnnotation.out()) {
            final String argumentName = outputArgumentAnnotation.name();
            StateVariable stateVariable = this.findRelatedStateVariable(outputArgumentAnnotation.stateVariable(), argumentName, this.getMethod().getName());
            if (stateVariable == null && outputArgumentAnnotation.getterName().length() > 0) {
                stateVariable = this.findRelatedStateVariable(null, null, outputArgumentAnnotation.getterName());
            }
            if (stateVariable == null) {
                throw new LocalServiceBindingException("Related state variable not found for output argument: " + argumentName);
            }
            final StateVariableAccessor accessor = this.findOutputArgumentAccessor(stateVariable, outputArgumentAnnotation.getterName(), hasMultipleOutputArguments);
            AnnotationActionBinder.log.finer("Found related state variable for output argument '" + argumentName + "': " + stateVariable);
            final ActionArgument outputArgument = new ActionArgument(argumentName, stateVariable.getName(), ActionArgument.Direction.OUT, !hasMultipleOutputArguments);
            map.put(outputArgument, accessor);
        }
        return map;
    }
    
    protected StateVariableAccessor findOutputArgumentAccessor(final StateVariable stateVariable, final String getterName, final boolean multipleArguments) throws LocalServiceBindingException {
        final boolean isVoid = this.getMethod().getReturnType().equals(Void.TYPE);
        if (isVoid) {
            if (getterName == null || getterName.length() <= 0) {
                AnnotationActionBinder.log.finer("Action method is void, trying to find existing accessor of related: " + stateVariable);
                return this.getStateVariables().get(stateVariable);
            }
            AnnotationActionBinder.log.finer("Action method is void, will use getter method named: " + getterName);
            final Method getter = Reflections.getMethod(this.getMethod().getDeclaringClass(), getterName);
            if (getter == null) {
                throw new LocalServiceBindingException("Declared getter method '" + getterName + "' not found on: " + this.getMethod().getDeclaringClass());
            }
            this.validateType(stateVariable, getter.getReturnType());
            return new GetterStateVariableAccessor(getter);
        }
        else {
            if (getterName == null || getterName.length() <= 0) {
                if (!multipleArguments) {
                    AnnotationActionBinder.log.finer("Action method is not void, will use the returned instance: " + this.getMethod().getReturnType());
                    this.validateType(stateVariable, this.getMethod().getReturnType());
                }
                return null;
            }
            AnnotationActionBinder.log.finer("Action method is not void, will use getter method on returned instance: " + getterName);
            final Method getter = Reflections.getMethod(this.getMethod().getReturnType(), getterName);
            if (getter == null) {
                throw new LocalServiceBindingException("Declared getter method '" + getterName + "' not found on return type: " + this.getMethod().getReturnType());
            }
            this.validateType(stateVariable, getter.getReturnType());
            return new GetterStateVariableAccessor(getter);
        }
    }
    
    protected StateVariable findRelatedStateVariable(final String declaredName, final String argumentName, final String methodName) throws LocalServiceBindingException {
        StateVariable relatedStateVariable = null;
        if (declaredName != null && declaredName.length() > 0) {
            relatedStateVariable = this.getStateVariable(declaredName);
        }
        if (relatedStateVariable == null && argumentName != null && argumentName.length() > 0) {
            final String actualName = AnnotationLocalServiceBinder.toUpnpStateVariableName(argumentName);
            AnnotationActionBinder.log.finer("Finding related state variable with argument name (converted to UPnP name): " + actualName);
            relatedStateVariable = this.getStateVariable(argumentName);
        }
        if (relatedStateVariable == null && argumentName != null && argumentName.length() > 0) {
            String actualName = AnnotationLocalServiceBinder.toUpnpStateVariableName(argumentName);
            actualName = "A_ARG_TYPE_" + actualName;
            AnnotationActionBinder.log.finer("Finding related state variable with prefixed argument name (converted to UPnP name): " + actualName);
            relatedStateVariable = this.getStateVariable(actualName);
        }
        if (relatedStateVariable == null && methodName != null && methodName.length() > 0) {
            final String methodPropertyName = Reflections.getMethodPropertyName(methodName);
            if (methodPropertyName != null) {
                AnnotationActionBinder.log.finer("Finding related state variable with method property name: " + methodPropertyName);
                relatedStateVariable = this.getStateVariable(AnnotationLocalServiceBinder.toUpnpStateVariableName(methodPropertyName));
            }
        }
        return relatedStateVariable;
    }
    
    protected void validateType(final StateVariable stateVariable, final Class type) throws LocalServiceBindingException {
        final Datatype.Default expectedDefaultMapping = ModelUtil.isStringConvertibleType(this.getStringConvertibleTypes(), type) ? Datatype.Default.STRING : Datatype.Default.getByJavaType(type);
        AnnotationActionBinder.log.finer("Expecting '" + stateVariable + "' to match default mapping: " + expectedDefaultMapping);
        if (expectedDefaultMapping != null && !stateVariable.getTypeDetails().getDatatype().isHandlingJavaType(expectedDefaultMapping.getJavaType())) {
            throw new LocalServiceBindingException("State variable '" + stateVariable + "' datatype can't handle action " + "argument's Java type (change one): " + expectedDefaultMapping.getJavaType());
        }
        if (expectedDefaultMapping == null && stateVariable.getTypeDetails().getDatatype().getBuiltin() != null) {
            throw new LocalServiceBindingException("State variable '" + stateVariable + "' should be custom datatype " + "(action argument type is unknown Java type): " + type.getSimpleName());
        }
        AnnotationActionBinder.log.finer("State variable matches required argument datatype (or can't be validated because it is custom)");
    }
    
    protected StateVariable getStateVariable(final String name) {
        for (final StateVariable stateVariable : this.getStateVariables().keySet()) {
            if (stateVariable.getName().equals(name)) {
                return stateVariable;
            }
        }
        return null;
    }
    
    static {
        AnnotationActionBinder.log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());
    }
}
