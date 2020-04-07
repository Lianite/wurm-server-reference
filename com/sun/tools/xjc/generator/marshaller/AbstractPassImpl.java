// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.codemodel.JBlock;
import com.sun.msv.datatype.xsd.QnameValueType;
import com.sun.msv.datatype.xsd.QnameType;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.Expression;

abstract class AbstractPassImpl implements Pass
{
    private final String name;
    protected final Context context;
    static /* synthetic */ Class class$javax$xml$namespace$QName;
    static /* synthetic */ Class class$javax$xml$bind$DatatypeConverter;
    
    AbstractPassImpl(final Context _context, final String _name) {
        this.context = _context;
        this.name = _name;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final void build(final Expression exp) {
        final Pass old = this.context.currentPass;
        this.context.currentPass = (Pass)this;
        this.context.build(exp);
        this.context.currentPass = old;
    }
    
    protected final void marshalValue(final ValueExp exp) {
        if (!exp.dt.isContextDependent()) {
            String literal = null;
            if (exp.dt instanceof XSDatatype) {
                literal = ((XSDatatype)exp.dt).convertToLexicalValue(exp.value, (SerializationContext)null);
            }
            if (literal == null) {
                throw new JAXBAssertionError();
            }
            this.getBlock(true).invoke(this.context.$serializer, "text").arg(JExpr.lit(literal)).arg(JExpr._null());
        }
        else {
            if (!(exp.dt instanceof QnameType)) {
                throw new JAXBAssertionError("unsupported datatype " + exp.name);
            }
            final QnameValueType qn = (QnameValueType)exp.value;
            this.getBlock(true).invoke(this.context.$serializer, "text").arg(this.context.codeModel.ref((AbstractPassImpl.class$javax$xml$bind$DatatypeConverter == null) ? (AbstractPassImpl.class$javax$xml$bind$DatatypeConverter = class$("javax.xml.bind.DatatypeConverter")) : AbstractPassImpl.class$javax$xml$bind$DatatypeConverter).staticInvoke("printQName").arg(JExpr._new(this.context.codeModel.ref((AbstractPassImpl.class$javax$xml$namespace$QName == null) ? (AbstractPassImpl.class$javax$xml$namespace$QName = class$("javax.xml.namespace.QName")) : AbstractPassImpl.class$javax$xml$namespace$QName)).arg(JExpr.lit(qn.namespaceURI)).arg(JExpr.lit(qn.localPart))).arg(this.context.$serializer.invoke("getNamespaceContext"))).arg(JExpr._null());
        }
    }
    
    protected final JBlock getBlock(final boolean create) {
        return this.context.getCurrentBlock().get(create);
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
