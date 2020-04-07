// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JType;
import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.Transducer;

public class PrimitiveItem extends TypeItem
{
    public final Transducer xducer;
    public final DatabindableDatatype guard;
    
    protected PrimitiveItem(final Transducer _xducer, final DatabindableDatatype _guard, final Expression _exp, final Locator loc) {
        super(_xducer.toString(), loc);
        this.xducer = _xducer;
        this.exp = _exp;
        this.guard = _guard;
    }
    
    public JType getType() {
        return this.xducer.getReturnType();
    }
    
    public Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onPrimitive(this);
    }
}
