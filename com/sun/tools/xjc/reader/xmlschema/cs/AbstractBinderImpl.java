// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.xml.xsom.XSSchema;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BISchemaBinding;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JDefinedClass;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;

abstract class AbstractBinderImpl implements ClassBinder
{
    protected final BGMBuilder builder;
    protected final ClassSelector owner;
    
    protected AbstractBinderImpl(final ClassSelector _owner) {
        this.owner = _owner;
        this.builder = this.owner.builder;
    }
    
    protected final ClassItem wrapByClassItem(final XSComponent sc, final JDefinedClass cls) {
        return this.owner.builder.grammar.createClassItem(cls, Expression.epsilon, sc.getLocator());
    }
    
    protected final String deriveName(final XSDeclaration comp) {
        return this.deriveName(comp.getName(), comp);
    }
    
    protected final String deriveName(String name, final XSComponent comp) {
        final XSSchema owner = comp.getOwnerSchema();
        if (owner != null) {
            final BISchemaBinding sb = (BISchemaBinding)this.builder.getBindInfo(owner).get(BISchemaBinding.NAME);
            if (sb != null) {
                name = sb.mangleClassName(name, comp);
            }
        }
        name = this.builder.getNameConverter().toClassName(name);
        return name;
    }
    
    protected static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
