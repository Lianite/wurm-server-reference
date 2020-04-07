// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.codemodel.JType;
import java.util.Calendar;
import com.sun.codemodel.JExpr;
import com.sun.xml.bind.util.CalendarConv;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.codemodel.JExpression;
import com.sun.msv.grammar.ValueExp;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;

public class DateTransducer extends TransducerImpl
{
    private final JCodeModel codeModel;
    private final JClass datatypeImpl;
    
    public DateTransducer(final JCodeModel cm, final JClass datatypeImpl) {
        this.codeModel = cm;
        this.datatypeImpl = datatypeImpl;
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        final Calendar data = ((IDateTimeValueType)exp.value).toCalendar();
        final String str = CalendarConv.formatter.format(data.getTime());
        return this.codeModel.ref((DateTransducer.class$com$sun$xml$bind$util$CalendarConv == null) ? (DateTransducer.class$com$sun$xml$bind$util$CalendarConv = class$("com.sun.xml.bind.util.CalendarConv")) : DateTransducer.class$com$sun$xml$bind$util$CalendarConv).staticInvoke("createCalendar").arg(JExpr.lit(str));
    }
    
    public JExpression generateDeserializer(final JExpression literal, final DeserializerContext context) {
        return JExpr.cast(this.getReturnType(), this.datatypeImpl.staticRef("theInstance").invoke("createJavaObject").arg(literal).arg(JExpr._null()));
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return this.datatypeImpl.staticRef("theInstance").invoke("serializeJavaObject").arg(value).arg(JExpr._null());
    }
    
    public JType getReturnType() {
        return this.codeModel.ref((DateTransducer.class$java$util$Calendar == null) ? (DateTransducer.class$java$util$Calendar = class$("java.util.Calendar")) : DateTransducer.class$java$util$Calendar);
    }
}
