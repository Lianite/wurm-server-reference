// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import java.lang.reflect.Type;

class EagerNType implements NType
{
    final Type t;
    
    public EagerNType(final Type type) {
        this.t = type;
        assert this.t != null;
    }
    
    public JType toType(final Outline o, final Aspect aspect) {
        try {
            return o.getCodeModel().parseType(this.t.toString());
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EagerNType)) {
            return false;
        }
        final EagerNType eagerNType = (EagerNType)o;
        return this.t.equals(eagerNType.t);
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public int hashCode() {
        return this.t.hashCode();
    }
    
    public String fullName() {
        return Navigator.REFLECTION.getTypeName(this.t);
    }
}
