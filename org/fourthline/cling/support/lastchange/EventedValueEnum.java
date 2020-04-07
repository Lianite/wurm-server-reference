// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.Map;

public abstract class EventedValueEnum<E extends Enum> extends EventedValue<E>
{
    public EventedValueEnum(final E e) {
        super(e);
    }
    
    public EventedValueEnum(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected E valueOf(final String s) throws InvalidValueException {
        return this.enumValueOf(s);
    }
    
    protected abstract E enumValueOf(final String p0);
    
    @Override
    public String toString() {
        return this.getValue().name();
    }
    
    @Override
    protected Datatype getDatatype() {
        return null;
    }
}
