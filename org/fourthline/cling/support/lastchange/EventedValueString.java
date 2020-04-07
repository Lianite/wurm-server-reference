// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import java.util.Map;

public class EventedValueString extends EventedValue<String>
{
    public EventedValueString(final String value) {
        super(value);
    }
    
    public EventedValueString(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.STRING.getDatatype();
    }
}
