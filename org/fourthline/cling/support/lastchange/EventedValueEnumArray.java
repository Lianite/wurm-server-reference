// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.ModelUtil;
import java.util.Map;

public abstract class EventedValueEnumArray<E extends Enum> extends EventedValue<E[]>
{
    public EventedValueEnumArray(final E[] e) {
        super(e);
    }
    
    public EventedValueEnumArray(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected E[] valueOf(final String s) throws InvalidValueException {
        return this.enumValueOf(ModelUtil.fromCommaSeparatedList(s));
    }
    
    protected abstract E[] enumValueOf(final String[] p0);
    
    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(this.getValue());
    }
    
    @Override
    protected Datatype getDatatype() {
        return null;
    }
}
