// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import java.util.HashSet;
import com.sun.codemodel.JType;
import java.lang.reflect.Modifier;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import java.lang.reflect.Type;
import java.util.Set;

public class EagerNClass extends EagerNType implements NClass
{
    final Class c;
    private static final Set<Class> boxedTypes;
    
    public EagerNClass(final Class type) {
        super(type);
        this.c = type;
    }
    
    public boolean isBoxedType() {
        return EagerNClass.boxedTypes.contains(this.c);
    }
    
    public JClass toType(final Outline o, final Aspect aspect) {
        return o.getCodeModel().ref(this.c);
    }
    
    public boolean isAbstract() {
        return Modifier.isAbstract(this.c.getModifiers());
    }
    
    static {
        (boxedTypes = new HashSet<Class>()).add(Boolean.class);
        EagerNClass.boxedTypes.add(Character.class);
        EagerNClass.boxedTypes.add(Byte.class);
        EagerNClass.boxedTypes.add(Short.class);
        EagerNClass.boxedTypes.add(Integer.class);
        EagerNClass.boxedTypes.add(Long.class);
        EagerNClass.boxedTypes.add(Float.class);
        EagerNClass.boxedTypes.add(Double.class);
    }
}
