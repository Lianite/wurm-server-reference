// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.state;

import org.seamless.util.Reflections;
import java.lang.reflect.Method;

public class GetterStateVariableAccessor extends StateVariableAccessor
{
    private Method getter;
    
    public GetterStateVariableAccessor(final Method getter) {
        this.getter = getter;
    }
    
    public Method getGetter() {
        return this.getter;
    }
    
    @Override
    public Class<?> getReturnType() {
        return this.getGetter().getReturnType();
    }
    
    @Override
    public Object read(final Object serviceImpl) throws Exception {
        return Reflections.invoke(this.getGetter(), serviceImpl, new Object[0]);
    }
    
    @Override
    public String toString() {
        return super.toString() + " Method: " + this.getGetter();
    }
}
