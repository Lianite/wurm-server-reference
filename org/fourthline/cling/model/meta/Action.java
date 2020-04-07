// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.Collection;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class Action<S extends Service> implements Validatable
{
    private static final Logger log;
    private final String name;
    private final ActionArgument[] arguments;
    private final ActionArgument[] inputArguments;
    private final ActionArgument[] outputArguments;
    private S service;
    
    public Action(final String name, final ActionArgument[] arguments) {
        this.name = name;
        if (arguments != null) {
            final List<ActionArgument> inputList = new ArrayList<ActionArgument>();
            final List<ActionArgument> outputList = new ArrayList<ActionArgument>();
            for (final ActionArgument argument : arguments) {
                argument.setAction(this);
                if (argument.getDirection().equals(ActionArgument.Direction.IN)) {
                    inputList.add(argument);
                }
                if (argument.getDirection().equals(ActionArgument.Direction.OUT)) {
                    outputList.add(argument);
                }
            }
            this.arguments = arguments;
            this.inputArguments = inputList.toArray(new ActionArgument[inputList.size()]);
            this.outputArguments = outputList.toArray(new ActionArgument[outputList.size()]);
        }
        else {
            this.arguments = new ActionArgument[0];
            this.inputArguments = new ActionArgument[0];
            this.outputArguments = new ActionArgument[0];
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean hasArguments() {
        return this.getArguments() != null && this.getArguments().length > 0;
    }
    
    public ActionArgument[] getArguments() {
        return this.arguments;
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
    
    public ActionArgument<S> getFirstInputArgument() {
        if (!this.hasInputArguments()) {
            throw new IllegalStateException("No input arguments: " + this);
        }
        return this.getInputArguments()[0];
    }
    
    public ActionArgument<S> getFirstOutputArgument() {
        if (!this.hasOutputArguments()) {
            throw new IllegalStateException("No output arguments: " + this);
        }
        return this.getOutputArguments()[0];
    }
    
    public ActionArgument<S>[] getInputArguments() {
        return (ActionArgument<S>[])this.inputArguments;
    }
    
    public ActionArgument<S> getInputArgument(final String name) {
        for (final ActionArgument<S> arg : this.getInputArguments()) {
            if (arg.isNameOrAlias(name)) {
                return arg;
            }
        }
        return null;
    }
    
    public ActionArgument<S>[] getOutputArguments() {
        return (ActionArgument<S>[])this.outputArguments;
    }
    
    public ActionArgument<S> getOutputArgument(final String name) {
        for (final ActionArgument<S> arg : this.getOutputArguments()) {
            if (arg.getName().equals(name)) {
                return arg;
            }
        }
        return null;
    }
    
    public boolean hasInputArguments() {
        return this.getInputArguments() != null && this.getInputArguments().length > 0;
    }
    
    public boolean hasOutputArguments() {
        return this.getOutputArguments() != null && this.getOutputArguments().length > 0;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ", Arguments: " + ((this.getArguments() != null) ? this.getArguments().length : "NO ARGS") + ") " + this.getName();
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getName() == null || this.getName().length() == 0) {
            errors.add(new ValidationError(this.getClass(), "name", "Action without name of: " + this.getService()));
        }
        else if (!ModelUtil.isValidUDAName(this.getName())) {
            Action.log.warning("UPnP specification violation of: " + this.getService().getDevice());
            Action.log.warning("Invalid action name: " + this);
        }
        for (final ActionArgument actionArgument : this.getArguments()) {
            if (this.getService().getStateVariable(actionArgument.getRelatedStateVariableName()) == null) {
                errors.add(new ValidationError(this.getClass(), "arguments", "Action argument references an unknown state variable: " + actionArgument.getRelatedStateVariableName()));
            }
        }
        ActionArgument retValueArgument = null;
        int retValueArgumentIndex = 0;
        int i = 0;
        for (final ActionArgument actionArgument2 : this.getArguments()) {
            if (actionArgument2.isReturnValue()) {
                if (actionArgument2.getDirection() == ActionArgument.Direction.IN) {
                    Action.log.warning("UPnP specification violation of :" + this.getService().getDevice());
                    Action.log.warning("Input argument can not have <retval/>");
                }
                else {
                    if (retValueArgument != null) {
                        Action.log.warning("UPnP specification violation of: " + this.getService().getDevice());
                        Action.log.warning("Only one argument of action '" + this.getName() + "' can be <retval/>");
                    }
                    retValueArgument = actionArgument2;
                    retValueArgumentIndex = i;
                }
            }
            ++i;
        }
        if (retValueArgument != null) {
            for (int j = 0; j < retValueArgumentIndex; ++j) {
                final ActionArgument a = this.getArguments()[j];
                if (a.getDirection() == ActionArgument.Direction.OUT) {
                    Action.log.warning("UPnP specification violation of: " + this.getService().getDevice());
                    Action.log.warning("Argument '" + retValueArgument.getName() + "' of action '" + this.getName() + "' is <retval/> but not the first OUT argument");
                }
            }
        }
        for (final ActionArgument argument : this.arguments) {
            errors.addAll(argument.validate());
        }
        return errors;
    }
    
    public Action<S> deepCopy() {
        final ActionArgument<S>[] actionArgumentsDupe = (ActionArgument<S>[])new ActionArgument[this.getArguments().length];
        for (int i = 0; i < this.getArguments().length; ++i) {
            final ActionArgument arg = this.getArguments()[i];
            actionArgumentsDupe[i] = arg.deepCopy();
        }
        return new Action<S>(this.getName(), actionArgumentsDupe);
    }
    
    static {
        log = Logger.getLogger(Action.class.getName());
    }
}
