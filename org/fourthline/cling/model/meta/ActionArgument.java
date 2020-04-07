// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ModelUtil;
import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.types.Datatype;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class ActionArgument<S extends Service> implements Validatable
{
    private static final Logger log;
    private final String name;
    private final String[] aliases;
    private final String relatedStateVariableName;
    private final Direction direction;
    private final boolean returnValue;
    private Action<S> action;
    
    public ActionArgument(final String name, final String relatedStateVariableName, final Direction direction) {
        this(name, new String[0], relatedStateVariableName, direction, false);
    }
    
    public ActionArgument(final String name, final String[] aliases, final String relatedStateVariableName, final Direction direction) {
        this(name, aliases, relatedStateVariableName, direction, false);
    }
    
    public ActionArgument(final String name, final String relatedStateVariableName, final Direction direction, final boolean returnValue) {
        this(name, new String[0], relatedStateVariableName, direction, returnValue);
    }
    
    public ActionArgument(final String name, final String[] aliases, final String relatedStateVariableName, final Direction direction, final boolean returnValue) {
        this.name = name;
        this.aliases = aliases;
        this.relatedStateVariableName = relatedStateVariableName;
        this.direction = direction;
        this.returnValue = returnValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String[] getAliases() {
        return this.aliases;
    }
    
    public boolean isNameOrAlias(final String name) {
        if (this.getName().equalsIgnoreCase(name)) {
            return true;
        }
        for (final String alias : this.aliases) {
            if (alias.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public String getRelatedStateVariableName() {
        return this.relatedStateVariableName;
    }
    
    public Direction getDirection() {
        return this.direction;
    }
    
    public boolean isReturnValue() {
        return this.returnValue;
    }
    
    public Action<S> getAction() {
        return this.action;
    }
    
    void setAction(final Action<S> action) {
        if (this.action != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.action = action;
    }
    
    public Datatype getDatatype() {
        return this.getAction().getService().getDatatype(this);
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getName() == null || this.getName().length() == 0) {
            errors.add(new ValidationError(this.getClass(), "name", "Argument without name of: " + this.getAction()));
        }
        else if (!ModelUtil.isValidUDAName(this.getName())) {
            ActionArgument.log.warning("UPnP specification violation of: " + this.getAction().getService().getDevice());
            ActionArgument.log.warning("Invalid argument name: " + this);
        }
        else if (this.getName().length() > 32) {
            ActionArgument.log.warning("UPnP specification violation of: " + this.getAction().getService().getDevice());
            ActionArgument.log.warning("Argument name should be less than 32 characters: " + this);
        }
        if (this.getDirection() == null) {
            errors.add(new ValidationError(this.getClass(), "direction", "Argument '" + this.getName() + "' requires a direction, either IN or OUT"));
        }
        if (this.isReturnValue() && this.getDirection() != Direction.OUT) {
            errors.add(new ValidationError(this.getClass(), "direction", "Return value argument '" + this.getName() + "' must be direction OUT"));
        }
        return errors;
    }
    
    public ActionArgument<S> deepCopy() {
        return new ActionArgument<S>(this.getName(), this.getAliases(), this.getRelatedStateVariableName(), this.getDirection(), this.isReturnValue());
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ", " + this.getDirection() + ") " + this.getName();
    }
    
    static {
        log = Logger.getLogger(ActionArgument.class.getName());
    }
    
    public enum Direction
    {
        IN, 
        OUT;
    }
}
