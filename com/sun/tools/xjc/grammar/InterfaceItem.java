// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JType;
import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;
import com.sun.codemodel.JClass;

public class InterfaceItem extends TypeItem
{
    private final JClass type;
    
    protected InterfaceItem(final JClass _type, final Expression body, final Locator loc) {
        super(_type.name(), loc);
        this.type = _type;
        this.exp = body;
    }
    
    public JType getType() {
        return this.type;
    }
    
    public JClass getTypeAsClass() {
        return this.type;
    }
    
    public ClassItem getSuperType() {
        return null;
    }
    
    public Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onInterface(this);
    }
}
