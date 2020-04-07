// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import java.util.Map;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

public class EventedValueUnsignedIntegerTwoBytes extends EventedValue<UnsignedIntegerTwoBytes>
{
    public EventedValueUnsignedIntegerTwoBytes(final UnsignedIntegerTwoBytes value) {
        super(value);
    }
    
    public EventedValueUnsignedIntegerTwoBytes(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI2.getDatatype();
    }
}
