// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.InvalidValueException;
import java.util.Collections;
import org.fourthline.cling.model.meta.ActionArgument;
import java.util.LinkedHashMap;
import java.util.Map;
import org.fourthline.cling.model.profile.ClientInfo;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;

public class ActionInvocation<S extends Service>
{
    protected final Action<S> action;
    protected final ClientInfo clientInfo;
    protected Map<String, ActionArgumentValue<S>> input;
    protected Map<String, ActionArgumentValue<S>> output;
    protected ActionException failure;
    
    public ActionInvocation(final Action<S> action) {
        this(action, null, null, null);
    }
    
    public ActionInvocation(final Action<S> action, final ClientInfo clientInfo) {
        this(action, null, null, clientInfo);
    }
    
    public ActionInvocation(final Action<S> action, final ActionArgumentValue<S>[] input) {
        this(action, input, null, null);
    }
    
    public ActionInvocation(final Action<S> action, final ActionArgumentValue<S>[] input, final ClientInfo clientInfo) {
        this(action, input, null, clientInfo);
    }
    
    public ActionInvocation(final Action<S> action, final ActionArgumentValue<S>[] input, final ActionArgumentValue<S>[] output) {
        this(action, input, output, null);
    }
    
    public ActionInvocation(final Action<S> action, final ActionArgumentValue<S>[] input, final ActionArgumentValue<S>[] output, final ClientInfo clientInfo) {
        this.input = new LinkedHashMap<String, ActionArgumentValue<S>>();
        this.output = new LinkedHashMap<String, ActionArgumentValue<S>>();
        this.failure = null;
        if (action == null) {
            throw new IllegalArgumentException("Action can not be null");
        }
        this.action = action;
        this.setInput(input);
        this.setOutput(output);
        this.clientInfo = clientInfo;
    }
    
    public ActionInvocation(final ActionException failure) {
        this.input = new LinkedHashMap<String, ActionArgumentValue<S>>();
        this.output = new LinkedHashMap<String, ActionArgumentValue<S>>();
        this.failure = null;
        this.action = null;
        this.input = null;
        this.output = null;
        this.failure = failure;
        this.clientInfo = null;
    }
    
    public Action<S> getAction() {
        return this.action;
    }
    
    public ActionArgumentValue<S>[] getInput() {
        return this.input.values().toArray(new ActionArgumentValue[this.input.size()]);
    }
    
    public ActionArgumentValue<S> getInput(final String argumentName) {
        return this.getInput(this.getInputArgument(argumentName));
    }
    
    public ActionArgumentValue<S> getInput(final ActionArgument<S> argument) {
        return this.input.get(argument.getName());
    }
    
    public Map<String, ActionArgumentValue<S>> getInputMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ActionArgumentValue<S>>)this.input);
    }
    
    public ActionArgumentValue<S>[] getOutput() {
        return this.output.values().toArray(new ActionArgumentValue[this.output.size()]);
    }
    
    public ActionArgumentValue<S> getOutput(final String argumentName) {
        return this.getOutput(this.getOutputArgument(argumentName));
    }
    
    public Map<String, ActionArgumentValue<S>> getOutputMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ActionArgumentValue<S>>)this.output);
    }
    
    public ActionArgumentValue<S> getOutput(final ActionArgument<S> argument) {
        return this.output.get(argument.getName());
    }
    
    public void setInput(final String argumentName, final Object value) throws InvalidValueException {
        this.setInput(new ActionArgumentValue<S>(this.getInputArgument(argumentName), value));
    }
    
    public void setInput(final ActionArgumentValue<S> value) {
        this.input.put(value.getArgument().getName(), value);
    }
    
    public void setInput(final ActionArgumentValue<S>[] input) {
        if (input == null) {
            return;
        }
        for (final ActionArgumentValue<S> argumentValue : input) {
            this.input.put(argumentValue.getArgument().getName(), argumentValue);
        }
    }
    
    public void setOutput(final String argumentName, final Object value) throws InvalidValueException {
        this.setOutput(new ActionArgumentValue<S>(this.getOutputArgument(argumentName), value));
    }
    
    public void setOutput(final ActionArgumentValue<S> value) {
        this.output.put(value.getArgument().getName(), value);
    }
    
    public void setOutput(final ActionArgumentValue<S>[] output) {
        if (output == null) {
            return;
        }
        for (final ActionArgumentValue<S> argumentValue : output) {
            this.output.put(argumentValue.getArgument().getName(), argumentValue);
        }
    }
    
    protected ActionArgument<S> getInputArgument(final String name) {
        final ActionArgument<S> argument = this.getAction().getInputArgument(name);
        if (argument == null) {
            throw new IllegalArgumentException("Argument not found: " + name);
        }
        return argument;
    }
    
    protected ActionArgument<S> getOutputArgument(final String name) {
        final ActionArgument<S> argument = this.getAction().getOutputArgument(name);
        if (argument == null) {
            throw new IllegalArgumentException("Argument not found: " + name);
        }
        return argument;
    }
    
    public ActionException getFailure() {
        return this.failure;
    }
    
    public void setFailure(final ActionException failure) {
        this.failure = failure;
    }
    
    public ClientInfo getClientInfo() {
        return this.clientInfo;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") " + this.getAction();
    }
}
