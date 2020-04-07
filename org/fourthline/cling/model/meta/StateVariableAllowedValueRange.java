// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class StateVariableAllowedValueRange implements Validatable
{
    private static final Logger log;
    private final long minimum;
    private final long maximum;
    private final long step;
    
    public StateVariableAllowedValueRange(final long minimum, final long maximum) {
        this(minimum, maximum, 1L);
    }
    
    public StateVariableAllowedValueRange(final long minimum, final long maximum, final long step) {
        if (minimum > maximum) {
            StateVariableAllowedValueRange.log.warning("UPnP specification violation, allowed value range minimum '" + minimum + "' is greater than maximum '" + maximum + "', switching values.");
            this.minimum = maximum;
            this.maximum = minimum;
        }
        else {
            this.minimum = minimum;
            this.maximum = maximum;
        }
        this.step = step;
    }
    
    public long getMinimum() {
        return this.minimum;
    }
    
    public long getMaximum() {
        return this.maximum;
    }
    
    public long getStep() {
        return this.step;
    }
    
    public boolean isInRange(final long value) {
        return value >= this.getMinimum() && value <= this.getMaximum() && value % this.step == 0L;
    }
    
    @Override
    public List<ValidationError> validate() {
        return new ArrayList<ValidationError>();
    }
    
    @Override
    public String toString() {
        return "Range Min: " + this.getMinimum() + " Max: " + this.getMaximum() + " Step: " + this.getStep();
    }
    
    static {
        log = Logger.getLogger(StateVariableAllowedValueRange.class.getName());
    }
}
