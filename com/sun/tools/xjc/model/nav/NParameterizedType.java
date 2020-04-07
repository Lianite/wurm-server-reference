// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model.nav;

import com.sun.codemodel.JType;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

final class NParameterizedType implements NClass
{
    final NClass rawType;
    final NType[] args;
    
    NParameterizedType(final NClass rawType, final NType[] args) {
        this.rawType = rawType;
        this.args = args;
        assert args.length > 0;
    }
    
    public JClass toType(final Outline o, final Aspect aspect) {
        JClass r = this.rawType.toType(o, aspect);
        for (final NType arg : this.args) {
            r = r.narrow(arg.toType(o, aspect).boxify());
        }
        return r;
    }
    
    public boolean isAbstract() {
        return this.rawType.isAbstract();
    }
    
    public boolean isBoxedType() {
        return false;
    }
    
    public String fullName() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.rawType.fullName());
        buf.append('<');
        for (int i = 0; i < this.args.length; ++i) {
            if (i != 0) {
                buf.append(',');
            }
            buf.append(this.args[i].fullName());
        }
        buf.append('>');
        return buf.toString();
    }
}
