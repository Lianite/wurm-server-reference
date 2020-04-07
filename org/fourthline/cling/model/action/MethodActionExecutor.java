// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import java.lang.reflect.Constructor;
import java.util.List;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import java.util.ArrayList;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.meta.Action;
import org.seamless.util.Reflections;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ActionArgument;
import java.util.Map;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class MethodActionExecutor extends AbstractActionExecutor
{
    private static Logger log;
    protected Method method;
    
    public MethodActionExecutor(final Method method) {
        this.method = method;
    }
    
    public MethodActionExecutor(final Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors, final Method method) {
        super(outputArgumentAccessors);
        this.method = method;
    }
    
    public Method getMethod() {
        return this.method;
    }
    
    @Override
    protected void execute(final ActionInvocation<LocalService> actionInvocation, final Object serviceImpl) throws Exception {
        final Object[] inputArgumentValues = this.createInputArgumentValues(actionInvocation, this.method);
        if (!actionInvocation.getAction().hasOutputArguments()) {
            MethodActionExecutor.log.fine("Calling local service method with no output arguments: " + this.method);
            Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            return;
        }
        final boolean isVoid = this.method.getReturnType().equals(Void.TYPE);
        MethodActionExecutor.log.fine("Calling local service method with output arguments: " + this.method);
        boolean isArrayResultProcessed = true;
        Object result;
        if (isVoid) {
            MethodActionExecutor.log.fine("Action method is void, calling declared accessors(s) on service instance to retrieve ouput argument(s)");
            Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            result = this.readOutputArgumentValues((Action<LocalService>)actionInvocation.getAction(), serviceImpl);
        }
        else if (this.isUseOutputArgumentAccessors(actionInvocation)) {
            MethodActionExecutor.log.fine("Action method is not void, calling declared accessor(s) on returned instance to retrieve ouput argument(s)");
            final Object returnedInstance = Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            result = this.readOutputArgumentValues((Action<LocalService>)actionInvocation.getAction(), returnedInstance);
        }
        else {
            MethodActionExecutor.log.fine("Action method is not void, using returned value as (single) output argument");
            result = Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            isArrayResultProcessed = false;
        }
        final ActionArgument<LocalService>[] outputArgs = (ActionArgument<LocalService>[])actionInvocation.getAction().getOutputArguments();
        if (isArrayResultProcessed && result instanceof Object[]) {
            final Object[] results = (Object[])result;
            MethodActionExecutor.log.fine("Accessors returned Object[], setting output argument values: " + results.length);
            for (int i = 0; i < outputArgs.length; ++i) {
                this.setOutputArgumentValue(actionInvocation, outputArgs[i], results[i]);
            }
        }
        else {
            if (outputArgs.length != 1) {
                throw new ActionException(ErrorCode.ACTION_FAILED, "Method return does not match required number of output arguments: " + outputArgs.length);
            }
            this.setOutputArgumentValue(actionInvocation, outputArgs[0], result);
        }
    }
    
    protected boolean isUseOutputArgumentAccessors(final ActionInvocation<LocalService> actionInvocation) {
        for (final ActionArgument argument : actionInvocation.getAction().getOutputArguments()) {
            if (this.getOutputArgumentAccessors().get(argument) != null) {
                return true;
            }
        }
        return false;
    }
    
    protected Object[] createInputArgumentValues(final ActionInvocation<LocalService> actionInvocation, final Method method) throws ActionException {
        final LocalService service = actionInvocation.getAction().getService();
        final List values = new ArrayList();
        int i = 0;
        for (final ActionArgument<LocalService> argument : actionInvocation.getAction().getInputArguments()) {
            final Class methodParameterType = method.getParameterTypes()[i];
            final ActionArgumentValue<LocalService> inputValue = (ActionArgumentValue<LocalService>)actionInvocation.getInput(argument);
            if (methodParameterType.isPrimitive() && (inputValue == null || inputValue.toString().length() == 0)) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Primitive action method argument '" + argument.getName() + "' requires input value, can't be null or empty string");
            }
            Label_0389: {
                if (inputValue == null) {
                    values.add(i++, null);
                }
                else {
                    final String inputCallValueString = inputValue.toString();
                    if (inputCallValueString.length() > 0 && service.isStringConvertibleType(methodParameterType) && !methodParameterType.isEnum()) {
                        try {
                            final Constructor<String> ctor = methodParameterType.getConstructor(String.class);
                            MethodActionExecutor.log.finer("Creating new input argument value instance with String.class constructor of type: " + methodParameterType);
                            final Object o = ctor.newInstance(inputCallValueString);
                            values.add(i++, o);
                            break Label_0389;
                        }
                        catch (Exception ex) {
                            MethodActionExecutor.log.warning("Error preparing action method call: " + method);
                            MethodActionExecutor.log.warning("Can't convert input argument string to desired type of '" + argument.getName() + "': " + ex);
                            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Can't convert input argument string to desired type of '" + argument.getName() + "': " + ex);
                        }
                    }
                    values.add(i++, inputValue.getValue());
                }
            }
        }
        if (method.getParameterTypes().length > 0 && RemoteClientInfo.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1])) {
            if (actionInvocation instanceof RemoteActionInvocation && ((RemoteActionInvocation)actionInvocation).getRemoteClientInfo() != null) {
                MethodActionExecutor.log.finer("Providing remote client info as last action method input argument: " + method);
                values.add(i, ((RemoteActionInvocation)actionInvocation).getRemoteClientInfo());
            }
            else {
                values.add(i, null);
            }
        }
        return values.toArray(new Object[values.size()]);
    }
    
    static {
        MethodActionExecutor.log = Logger.getLogger(MethodActionExecutor.class.getName());
    }
}
