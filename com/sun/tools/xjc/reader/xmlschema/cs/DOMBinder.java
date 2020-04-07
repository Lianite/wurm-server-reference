// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.cs;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.tools.xjc.reader.xmlschema.WildcardNameClassBuilder;
import com.sun.tools.xjc.grammar.ext.DOMItemFactory;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSTermFunction;
import com.sun.xml.xsom.XSTerm;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIXDom;
import com.sun.xml.xsom.XSParticle;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.XSComponent;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.AbstractXSFunctionImpl;

class DOMBinder extends AbstractXSFunctionImpl
{
    private final BGMBuilder builder;
    private final ClassSelector selector;
    private final ExpressionPool pool;
    
    DOMBinder(final ClassSelector _selector) {
        this.selector = _selector;
        this.builder = this.selector.builder;
        this.pool = this.builder.pool;
    }
    
    public Expression bind(final XSComponent sc) {
        return sc.apply((XSFunction<Expression>)this);
    }
    
    public TypeItem bind(final XSElementDecl sc) {
        return sc.apply((XSFunction<TypeItem>)this);
    }
    
    public Object particle(final XSParticle p) {
        final BIXDom c = (BIXDom)this.builder.getBindInfo(p).get(BIXDom.NAME);
        if (c == null) {
            return null;
        }
        return new Builder(this, c).particle(p);
    }
    
    private Expression bindTerm(final XSTerm t) {
        final BIXDom c = (BIXDom)this.builder.getBindInfo(t).get(BIXDom.NAME);
        if (c == null) {
            return null;
        }
        return t.apply((XSTermFunction<Expression>)new Builder(this, c));
    }
    
    public Object wildcard(final XSWildcard wc) {
        final Expression exp = this.bindTerm(wc);
        if (exp != null) {
            return exp;
        }
        if ((wc.getMode() == 3 || wc.getMode() == 1) && this.builder.getGlobalBinding().smartWildcardDefaultBinding) {
            try {
                return DOMItemFactory.getInstance("W3C").create(WildcardNameClassBuilder.build(wc), this.builder.grammar, wc.getLocator());
            }
            catch (DOMItemFactory.UndefinedNameException e) {
                e.printStackTrace();
                throw new JAXBAssertionError();
            }
        }
        return null;
    }
    
    public Object modelGroupDecl(final XSModelGroupDecl decl) {
        return this.bindTerm(decl);
    }
    
    public Object modelGroup(final XSModelGroup group) {
        return this.bindTerm(group);
    }
    
    public Object elementDecl(final XSElementDecl decl) {
        return this.bindTerm(decl);
    }
    
    public Object attGroupDecl(final XSAttGroupDecl decl) {
        return null;
    }
    
    public Object attributeDecl(final XSAttributeDecl decl) {
        return null;
    }
    
    public Object attributeUse(final XSAttributeUse use) {
        return null;
    }
    
    public Object complexType(final XSComplexType type) {
        return null;
    }
    
    public Object simpleType(final XSSimpleType simpleType) {
        return null;
    }
    
    public Object empty(final XSContentType empty) {
        return null;
    }
    
    private class Builder implements XSTermFunction
    {
        private final BIXDom custom;
        
        Builder(final DOMBinder this$0, final BIXDom c) {
            this.this$0 = this$0;
            this.custom = c;
        }
        
        public Object wildcard(final XSWildcard wc) {
            return this.custom.create(WildcardNameClassBuilder.build(wc), this.this$0.builder.grammar, wc.getLocator());
        }
        
        public Object modelGroupDecl(final XSModelGroupDecl decl) {
            return this.modelGroup(decl.getModelGroup());
        }
        
        public Object modelGroup(final XSModelGroup group) {
            final XSModelGroup.Compositor comp = group.getCompositor();
            Expression exp;
            if (comp == XSModelGroup.CHOICE) {
                exp = Expression.nullSet;
            }
            else {
                exp = Expression.epsilon;
            }
            for (int i = 0; i < group.getSize(); ++i) {
                final Expression item = this.particle(group.getChild(i));
                if (comp == XSModelGroup.CHOICE) {
                    exp = this.this$0.pool.createChoice(exp, item);
                }
                if (comp == XSModelGroup.SEQUENCE) {
                    exp = this.this$0.pool.createSequence(exp, item);
                }
                if (comp == XSModelGroup.ALL) {
                    exp = this.this$0.pool.createInterleave(exp, item);
                }
            }
            return exp;
        }
        
        public Object elementDecl(final XSElementDecl decl) {
            return this.custom.create((NameClass)new SimpleNameClass(decl.getTargetNamespace(), decl.getName()), this.this$0.builder.grammar, decl.getLocator());
        }
        
        public Expression particle(final XSParticle particle) {
            final XSTerm t = particle.getTerm();
            final Expression exp = t.apply((XSTermFunction<Expression>)this);
            return this.this$0.builder.processMinMax(exp, particle);
        }
    }
}
