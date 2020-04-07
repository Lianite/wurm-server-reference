// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import java.util.Iterator;
import com.sun.codemodel.JType;
import java.util.HashMap;
import org.xml.sax.Locator;
import com.sun.msv.grammar.Expression;
import java.util.Vector;
import com.sun.msv.grammar.ReferenceExp;
import java.util.Map;
import com.sun.codemodel.JDefinedClass;

public final class ClassItem extends TypeItem
{
    private final JDefinedClass type;
    private String userSpecifiedImplClass;
    public final AnnotatedGrammar owner;
    private final Map fields;
    public final ReferenceExp agm;
    private final Vector constructors;
    public boolean hasGetContentMethod;
    public SuperClassItem superClass;
    
    protected ClassItem(final AnnotatedGrammar _owner, final JDefinedClass _type, final Expression exp, final Locator loc) {
        super(_type.name(), loc);
        this.fields = new HashMap();
        this.agm = new ReferenceExp((String)null);
        this.constructors = new Vector();
        this.hasGetContentMethod = false;
        this.type = _type;
        this.owner = _owner;
        this.exp = exp;
    }
    
    public JType getType() {
        return this.type;
    }
    
    public JDefinedClass getTypeAsDefined() {
        return this.type;
    }
    
    public final FieldUse getDeclaredField(final String name) {
        return this.fields.get(name);
    }
    
    public final FieldUse getField(final String name) {
        final FieldUse fu = this.getDeclaredField(name);
        if (fu != null) {
            return fu;
        }
        if (this.superClass != null) {
            return this.getSuperClass().getField(name);
        }
        return null;
    }
    
    public final FieldUse[] getDeclaredFieldUses() {
        return (FieldUse[])this.fields.values().toArray(new FieldUse[this.fields.size()]);
    }
    
    public FieldUse getOrCreateFieldUse(final String name) {
        FieldUse r = this.fields.get(name);
        if (r == null) {
            this.fields.put(name, r = new FieldUse(name, this));
        }
        return r;
    }
    
    public void removeDuplicateFieldUses() {
        final ClassItem superClass = this.getSuperClass();
        if (this.superClass == null) {
            return;
        }
        final FieldUse[] fu = this.getDeclaredFieldUses();
        for (int i = 0; i < fu.length; ++i) {
            if (superClass.getField(fu[i].name) != null) {
                this.fields.remove(fu[i].name);
            }
        }
    }
    
    public void addConstructor(final String[] fieldNames) {
        this.constructors.add(new Constructor(fieldNames));
    }
    
    public Iterator iterateConstructors() {
        return this.constructors.iterator();
    }
    
    public ClassItem getSuperClass() {
        if (this.superClass == null) {
            return null;
        }
        return this.superClass.definition;
    }
    
    public Object visitJI(final JavaItemVisitor visitor) {
        return visitor.onClass(this);
    }
    
    protected boolean calcEpsilonReducibility() {
        return false;
    }
    
    public String getUserSpecifiedImplClass() {
        return this.userSpecifiedImplClass;
    }
    
    public void setUserSpecifiedImplClass(final String userSpecifiedImplClass) {
        this.userSpecifiedImplClass = userSpecifiedImplClass;
    }
}
