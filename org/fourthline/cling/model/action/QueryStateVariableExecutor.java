// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.meta.LocalService;

public class QueryStateVariableExecutor extends AbstractActionExecutor
{
    @Override
    protected void execute(final ActionInvocation<LocalService> actionInvocation, final Object serviceImpl) throws Exception {
        if (actionInvocation.getAction() instanceof QueryStateVariableAction) {
            if (!actionInvocation.getAction().getService().isSupportsQueryStateVariables()) {
                actionInvocation.setFailure(new ActionException(ErrorCode.INVALID_ACTION, "This service does not support querying state variables"));
            }
            else {
                this.executeQueryStateVariable(actionInvocation, serviceImpl);
            }
            return;
        }
        throw new IllegalStateException("This class can only execute QueryStateVariableAction's, not: " + actionInvocation.getAction());
    }
    
    protected void executeQueryStateVariable(final ActionInvocation<LocalService> actionInvocation, final Object serviceImpl) throws Exception {
        final LocalService service = actionInvocation.getAction().getService();
        final String stateVariableName = actionInvocation.getInput("varName").toString();
        final StateVariable stateVariable = service.getStateVariable(stateVariableName);
        if (stateVariable == null) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "No state variable found: " + stateVariableName);
        }
        final StateVariableAccessor accessor;
        if ((accessor = service.getAccessor(stateVariable.getName())) == null) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "No accessor for state variable, can't read state: " + stateVariableName);
        }
        try {
            this.setOutputArgumentValue(actionInvocation, (ActionArgument<LocalService>)actionInvocation.getAction().getOutputArgument("return"), accessor.read(stateVariable, serviceImpl).toString());
        }
        catch (Exception ex) {
            throw new ActionException(ErrorCode.ACTION_FAILED, ex.getMessage());
        }
    }
}
