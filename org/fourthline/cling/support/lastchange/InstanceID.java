// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import java.util.ArrayList;
import java.util.List;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class InstanceID
{
    protected UnsignedIntegerFourBytes id;
    protected List<EventedValue> values;
    
    public InstanceID(final UnsignedIntegerFourBytes id) {
        this(id, new ArrayList<EventedValue>());
    }
    
    public InstanceID(final UnsignedIntegerFourBytes id, final List<EventedValue> values) {
        this.values = new ArrayList<EventedValue>();
        this.id = id;
        this.values = values;
    }
    
    public UnsignedIntegerFourBytes getId() {
        return this.id;
    }
    
    public List<EventedValue> getValues() {
        return this.values;
    }
}
