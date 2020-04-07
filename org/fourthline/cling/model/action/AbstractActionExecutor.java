// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.types.ErrorCode;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.Command;
import java.util.HashMap;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ActionArgument;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractActionExecutor implements ActionExecutor
{
    private static Logger log;
    protected Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors;
    
    protected AbstractActionExecutor() {
        this.outputArgumentAccessors = new HashMap<ActionArgument<LocalService>, StateVariableAccessor>();
    }
    
    protected AbstractActionExecutor(final Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors) {
        this.outputArgumentAccessors = new HashMap<ActionArgument<LocalService>, StateVariableAccessor>();
        this.outputArgumentAccessors = outputArgumentAccessors;
    }
    
    public Map<ActionArgument<LocalService>, StateVariableAccessor> getOutputArgumentAccessors() {
        return this.outputArgumentAccessors;
    }
    
    @Override
    public void execute(final ActionInvocation<LocalService> actionInvocation) {
        AbstractActionExecutor.log.fine("Invoking on local service: " + actionInvocation);
        final LocalService service = actionInvocation.getAction().getService();
        try {
            if (service.getManager() == null) {
                throw new IllegalStateException("Service has no implementation factory, can't get service instance");
            }
            service.getManager().execute(new Command() {
                @Override
                public void execute(final ServiceManager serviceManager) throws Exception {
                    AbstractActionExecutor.this.execute(actionInvocation, serviceManager.getImplementation());
                }
                
                @Override
                public String toString() {
                    return "Action invocation: " + actionInvocation.getAction();
                }
            });
        }
        catch (ActionException ex) {
            if (AbstractActionExecutor.log.isLoggable(Level.FINE)) {
                AbstractActionExecutor.log.fine("ActionException thrown by service, wrapping in invocation and returning: " + ex);
                AbstractActionExecutor.log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            }
            actionInvocation.setFailure(ex);
        }
        catch (InterruptedException ex2) {
            if (AbstractActionExecutor.log.isLoggable(Level.FINE)) {
                AbstractActionExecutor.log.fine("InterruptedException thrown by service, wrapping in invocation and returning: " + ex2);
                AbstractActionExecutor.log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex2));
            }
            actionInvocation.setFailure(new ActionCancelledException(ex2));
        }
        catch (Throwable t) {
            final Throwable rootCause = Exceptions.unwrap(t);
            if (AbstractActionExecutor.log.isLoggable(Level.FINE)) {
                AbstractActionExecutor.log.fine("Execution has thrown, wrapping root cause in ActionException and returning: " + t);
                AbstractActionExecutor.log.log(Level.FINE, "Exception root cause: ", rootCause);
            }
            actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, (rootCause.getMessage() != null) ? rootCause.getMessage() : rootCause.toString(), rootCause));
        }
    }
    
    protected abstract void execute(final ActionInvocation<LocalService> p0, final Object p1) throws Exception;
    
    protected Object readOutputArgumentValues(final Action<LocalService> action, final Object instance) throws Exception {
        final Object[] results = new Object[action.getOutputArguments().length];
        AbstractActionExecutor.log.fine("Attempting to retrieve output argument values using accessor: " + results.length);
        int i = 0;
        for (final ActionArgument outputArgument : action.getOutputArguments()) {
            AbstractActionExecutor.log.finer("Calling acccessor method for: " + outputArgument);
            final StateVariableAccessor accessor = this.getOutputArgumentAccessors().get(outputArgument);
            if (accessor == null) {
                throw new IllegalStateException("No accessor bound for: " + outputArgument);
            }
            AbstractActionExecutor.log.fine("Calling accessor to read output argument value: " + accessor);
            results[i++] = accessor.read(instance);
        }
        if (results.length == 1) {
            return results[0];
        }
        return (results.length > 0) ? results : null;
    }
    
    protected void setOutputArgumentValue(final ActionInvocation<LocalService> actionInvocation, final ActionArgument<LocalService> argument, final Object result) throws ActionException {
        final LocalService service = actionInvocation.getAction().getService();
        if (result != null) {
            try {
                if (service.isStringConvertibleType(result)) {
                    AbstractActionExecutor.log.fine("Result of invocation matches convertible type, setting toString() single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<LocalService>(argument, result.toString()));
                }
                else {
                    AbstractActionExecutor.log.fine("Result of invocation is Object, setting single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<LocalService>(argument, result));
                }
                return;
            }
            catch (InvalidValueException ex) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Wrong type or invalid value for '" + argument.getName() + "': " + ex.getMessage(), ex);
            }
        }
        AbstractActionExecutor.log.fine("Result of invocation is null, not setting any output argument value(s)");
    }
    
    static {
        AbstractActionExecutor.log = Logger.getLogger(AbstractActionExecutor.class.getName());
    }
}
