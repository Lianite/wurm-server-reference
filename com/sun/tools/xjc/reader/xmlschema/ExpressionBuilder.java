// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.gbind.OneOrMore;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.tools.xjc.reader.gbind.Element;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.tools.xjc.reader.gbind.Sequence;
import com.sun.tools.xjc.reader.gbind.Choice;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSWildcard;
import java.util.HashMap;
import com.sun.xml.xsom.XSParticle;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.tools.xjc.reader.gbind.Expression;
import com.sun.xml.xsom.visitor.XSTermFunction;

public final class ExpressionBuilder implements XSTermFunction<Expression>
{
    private GWildcardElement wildcard;
    private final Map<QName, GElementImpl> decls;
    private XSParticle current;
    
    public static Expression createTree(final XSParticle p) {
        return new ExpressionBuilder().particle(p);
    }
    
    private ExpressionBuilder() {
        this.wildcard = null;
        this.decls = new HashMap<QName, GElementImpl>();
    }
    
    public Expression wildcard(final XSWildcard wc) {
        if (this.wildcard == null) {
            this.wildcard = new GWildcardElement();
        }
        this.wildcard.merge(wc);
        this.wildcard.particles.add(this.current);
        return this.wildcard;
    }
    
    public Expression modelGroupDecl(final XSModelGroupDecl decl) {
        return this.modelGroup(decl.getModelGroup());
    }
    
    public Expression modelGroup(final XSModelGroup group) {
        final XSModelGroup.Compositor comp = group.getCompositor();
        if (comp == XSModelGroup.CHOICE) {
            Expression e = Expression.EPSILON;
            for (final XSParticle p : group.getChildren()) {
                if (e == null) {
                    e = this.particle(p);
                }
                else {
                    e = new Choice(e, this.particle(p));
                }
            }
            return e;
        }
        Expression e = Expression.EPSILON;
        for (final XSParticle p : group.getChildren()) {
            if (e == null) {
                e = this.particle(p);
            }
            else {
                e = new Sequence(e, this.particle(p));
            }
        }
        return e;
    }
    
    public Element elementDecl(final XSElementDecl decl) {
        final QName n = BGMBuilder.getName(decl);
        GElementImpl e = this.decls.get(n);
        if (e == null) {
            this.decls.put(n, e = new GElementImpl(n, decl));
        }
        e.particles.add(this.current);
        assert this.current.getTerm() == decl;
        return e;
    }
    
    public Expression particle(final XSParticle p) {
        this.current = p;
        Expression e = p.getTerm().apply((XSTermFunction<Expression>)this);
        if (p.isRepeated()) {
            e = new OneOrMore(e);
        }
        if (p.getMinOccurs() == 0) {
            e = new Choice(e, Expression.EPSILON);
        }
        return e;
    }
}
