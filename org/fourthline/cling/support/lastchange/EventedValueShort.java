// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import java.util.Map;

public class EventedValueShort extends EventedValue<Short>
{
    public EventedValueShort(final Short value) {
        super(value);
    }
    
    public EventedValueShort(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.I2_SHORT.getDatatype();
    }
}
