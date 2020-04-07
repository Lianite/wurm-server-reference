// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.datatype.xsd.QnameValueType;
import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.util.WhitespaceNormalizer;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.codemodel.JCodeModel;

public class QNameTransducer extends TransducerImpl
{
    private final JCodeModel codeModel;
    
    public QNameTransducer(final JCodeModel cm) {
        this.codeModel = cm;
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext context) {
        context.declareNamespace(body.get(true), (JExpression)value.invoke("getNamespaceURI"), (JExpression)value.invoke("getPrefix"), JExpr.FALSE);
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return this.codeModel.ref((QNameTransducer.class$javax$xml$bind$DatatypeConverter == null) ? (QNameTransducer.class$javax$xml$bind$DatatypeConverter = class$("javax.xml.bind.DatatypeConverter")) : QNameTransducer.class$javax$xml$bind$DatatypeConverter).staticInvoke("printQName").arg(value).arg(context.getNamespaceContext());
    }
    
    public JExpression generateDeserializer(final JExpression lexical, final DeserializerContext context) {
        return this.codeModel.ref((QNameTransducer.class$javax$xml$bind$DatatypeConverter == null) ? (QNameTransducer.class$javax$xml$bind$DatatypeConverter = class$("javax.xml.bind.DatatypeConverter")) : QNameTransducer.class$javax$xml$bind$DatatypeConverter).staticInvoke("parseQName").arg(WhitespaceNormalizer.COLLAPSE.generate(this.codeModel, lexical)).arg(context.getNamespaceContext());
    }
    
    public JType getReturnType() {
        return this.codeModel.ref((QNameTransducer.class$javax$xml$namespace$QName == null) ? (QNameTransducer.class$javax$xml$namespace$QName = class$("javax.xml.namespace.QName")) : QNameTransducer.class$javax$xml$namespace$QName);
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        final QnameValueType data = (QnameValueType)exp.value;
        return JExpr._new(this.codeModel.ref((QNameTransducer.class$javax$xml$namespace$QName == null) ? (QNameTransducer.class$javax$xml$namespace$QName = class$("javax.xml.namespace.QName")) : QNameTransducer.class$javax$xml$namespace$QName)).arg(JExpr.lit(data.namespaceURI)).arg(JExpr.lit(data.localPart));
    }
}
