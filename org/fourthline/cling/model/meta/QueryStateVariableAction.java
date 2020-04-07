// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.Collections;
import org.fourthline.cling.model.ValidationError;
import java.util.List;

public class QueryStateVariableAction<S extends Service> extends Action<S>
{
    public static final String INPUT_ARG_VAR_NAME = "varName";
    public static final String OUTPUT_ARG_RETURN = "return";
    public static final String ACTION_NAME = "QueryStateVariable";
    public static final String VIRTUAL_STATEVARIABLE_INPUT = "VirtualQueryActionInput";
    public static final String VIRTUAL_STATEVARIABLE_OUTPUT = "VirtualQueryActionOutput";
    
    public QueryStateVariableAction() {
        this(null);
    }
    
    public QueryStateVariableAction(final S service) {
        super("QueryStateVariable", new ActionArgument[] { new ActionArgument("varName", "VirtualQueryActionInput", ActionArgument.Direction.IN), new ActionArgument("return", "VirtualQueryActionOutput", ActionArgument.Direction.OUT) });
        this.setService(service);
    }
    
    @Override
    public String getName() {
        return "QueryStateVariable";
    }
    
    @Override
    public List<ValidationError> validate() {
        return (List<ValidationError>)Collections.EMPTY_LIST;
    }
}
