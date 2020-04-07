// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.state;

import org.seamless.util.Reflections;
import java.lang.reflect.Field;

public class FieldStateVariableAccessor extends StateVariableAccessor
{
    protected Field field;
    
    public FieldStateVariableAccessor(final Field field) {
        this.field = field;
    }
    
    public Field getField() {
        return this.field;
    }
    
    @Override
    public Class<?> getReturnType() {
        return this.getField().getType();
    }
    
    @Override
    public Object read(final Object serviceImpl) throws Exception {
        return Reflections.get(this.field, serviceImpl);
    }
    
    @Override
    public String toString() {
        return super.toString() + " Field: " + this.getField();
    }
}
