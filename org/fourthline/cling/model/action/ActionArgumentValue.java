// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.VariableValue;
import org.fourthline.cling.model.meta.Service;

public class ActionArgumentValue<S extends Service> extends VariableValue
{
    private final ActionArgument<S> argument;
    
    public ActionArgumentValue(final ActionArgument<S> argument, final Object value) throws InvalidValueException {
        super(argument.getDatatype(), (value != null && value.getClass().isEnum()) ? value.toString() : value);
        this.argument = argument;
    }
    
    public ActionArgument<S> getArgument() {
        return this.argument;
    }
}
