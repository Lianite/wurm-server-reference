// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JPrimitiveType;
import com.sun.tools.xjc.reader.TypeUtil;
import com.sun.codemodel.JCodeModel;
import java.util.HashSet;
import com.sun.msv.grammar.Expression;
import org.xml.sax.Locator;
import com.sun.codemodel.JType;
import java.util.Set;
import com.sun.tools.xjc.grammar.util.Multiplicity;
import com.sun.tools.xjc.generator.field.FieldRendererFactory;

public final class FieldItem extends JavaItem
{
    public FieldRendererFactory realization;
    public DefaultValue[] defaultValues;
    public Multiplicity multiplicity;
    public boolean collisionExpected;
    public String javadoc;
    private boolean delegation;
    private final Set types;
    public final JType userSpecifiedType;
    
    public FieldItem(final String name, final Locator loc) {
        this(name, null, loc);
    }
    
    public FieldItem(final String name, final Expression exp, final Locator loc) {
        this(name, exp, null, loc);
    }
    
    public FieldItem(final String name, final Expression _exp, final JType _userDefinedType, final Locator loc) {
        super(name, loc);
        this.defaultValues = null;
        this.collisionExpected = false;
        this.javadoc = null;
        this.delegation = false;
        this.types = new HashSet();
        this.exp = _exp;
        this.userSpecifiedType = _userDefinedType;
    }
    
    public void setDelegation(final boolean f) {
        this.delegation = f;
    }
    
    protected boolean isDelegated() {
        return this.delegation;
    }
    
    public final void addType(final TypeItem ti) throws BadTypeException {
        if (this.userSpecifiedType != null) {
            throw new BadTypeException(this.userSpecifiedType, (FieldItem$1)null);
        }
        this.types.add(ti);
    }
    
    public final TypeItem[] listTypes() {
        return this.types.toArray(new TypeItem[this.types.size()]);
    }
    
    public final boolean hasTypes() {
        return !this.types.isEmpty();
    }
    
    public JType getType(final JCodeModel codeModel) {
        if (this.userSpecifiedType != null) {
            return this.userSpecifiedType;
        }
        final JType[] classes = new JType[this.types.size()];
        final TypeItem[] types = this.listTypes();
        for (int i = 0; i < types.length; ++i) {
            classes[i] = types[i].getType();
        }
        return TypeUtil.getCommonBaseType(codeModel, classes);
    }
    
    public boolean isUnboxable(final JCodeModel codeModel) {
        final TypeItem[] types = this.listTypes();
        if (!this.getType(codeModel).isPrimitive()) {
            return false;
        }
        for (int i = 0; i < types.length; ++i) {
            final JType t = types[i].getType();
            if (!(t instanceof JPrimitiveType)) {
                if (((JClass)t).getPrimitiveType() == null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onField(this);
    }
    
    public String toString() {
        return super.toString() + '[' + this.name + ']';
    }
    
    public static class BadTypeException extends Exception
    {
        private final JType type;
        
        private BadTypeException(final JType _type) {
            this.type = _type;
        }
        
        public JType getUserSpecifiedType() {
            return this.type;
        }
    }
}
