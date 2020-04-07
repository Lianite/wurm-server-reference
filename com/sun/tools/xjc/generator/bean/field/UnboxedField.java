// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JPrimitiveType;

public class UnboxedField extends AbstractFieldWithVar
{
    private final JPrimitiveType ptype;
    
    protected UnboxedField(final ClassOutlineImpl outline, final CPropertyInfo prop) {
        super(outline, prop);
        assert this.implType == this.exposedType;
        this.ptype = (JPrimitiveType)this.implType;
        assert this.ptype != null;
        this.createField();
        final MethodWriter writer = outline.createMethodWriter();
        final NameConverter nc = outline.parent().getModel().getNameConverter();
        final JMethod $get = writer.declareMethod(this.ptype, this.getGetterMethod());
        String javadoc = prop.javadoc;
        if (javadoc.length() == 0) {
            javadoc = Messages.DEFAULT_GETTER_JAVADOC.format(nc.toVariableName(prop.getName(true)));
        }
        writer.javadoc().append(javadoc);
        $get.body()._return(this.ref());
        final JMethod $set = writer.declareMethod(this.codeModel.VOID, "set" + prop.getName(true));
        final JVar $value = writer.addParameter(this.ptype, "value");
        final JBlock body = $set.body();
        body.assign(JExpr._this().ref(this.ref()), $value);
        writer.javadoc().append(Messages.DEFAULT_SETTER_JAVADOC.format(nc.toVariableName(prop.getName(true))));
    }
    
    protected JType getType(final Aspect aspect) {
        return super.getType(aspect).boxify().getPrimitiveType();
    }
    
    protected JType getFieldType() {
        return this.ptype;
    }
    
    public FieldAccessor create(final JExpression targetObject) {
        return new Accessor(targetObject) {
            public void unsetValues(final JBlock body) {
            }
            
            public JExpression hasSetValue() {
                return JExpr.TRUE;
            }
        };
    }
}
