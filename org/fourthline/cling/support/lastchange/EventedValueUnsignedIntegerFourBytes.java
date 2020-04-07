// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import java.util.Map;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class EventedValueUnsignedIntegerFourBytes extends EventedValue<UnsignedIntegerFourBytes>
{
    public EventedValueUnsignedIntegerFourBytes(final UnsignedIntegerFourBytes value) {
        super(value);
    }
    
    public EventedValueUnsignedIntegerFourBytes(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI4.getDatatype();
    }
}
