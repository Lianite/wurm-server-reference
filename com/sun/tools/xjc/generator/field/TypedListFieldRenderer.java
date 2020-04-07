// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.generator.JavadocBuilder;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;

public class TypedListFieldRenderer extends AbstractListFieldRenderer
{
    public static final FieldRendererFactory theFactory;
    
    protected TypedListFieldRenderer(final ClassContext context, final FieldUse fu, final JClass coreList) {
        super(context, fu, coreList);
    }
    
    public void generateAccessors() {
        final JMethod $add = this.writer.declareMethod((JType)this.codeModel.VOID, "add" + this.fu.name);
        JVar $idx = this.writer.addParameter((JType)this.codeModel.INT, "idx");
        JVar $value = this.writer.addParameter(this.fu.type, "value");
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        JBlock body = $add.body();
        body.invoke(this.ref(false), "add").arg($idx).arg($value);
        this.writer.javadoc().addParam($value, "allowed object is\n" + JavadocBuilder.listPossibleTypes(this.fu));
        final JMethod $get = this.writer.declareMethod(this.fu.type, "get" + this.fu.name);
        $idx = this.writer.addParameter((JType)this.codeModel.INT, "idx");
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        $get.body()._return(JExpr.cast(this.fu.type, this.ref(true).invoke("get").arg($idx)));
        this.writer.javadoc().addReturn(JavadocBuilder.listPossibleTypes(this.fu));
        final JMethod $iterate = this.writer.declareMethod((JType)this.codeModel.ref((TypedListFieldRenderer.class$java$util$Iterator == null) ? (TypedListFieldRenderer.class$java$util$Iterator = class$("java.util.Iterator")) : TypedListFieldRenderer.class$java$util$Iterator), "iterate" + this.fu.name);
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        $iterate.body()._return(JOp.cond(this.ref(true).eq(JExpr._null()), this.codeModel.ref((TypedListFieldRenderer.class$com$sun$xml$bind$util$EmptyIterator == null) ? (TypedListFieldRenderer.class$com$sun$xml$bind$util$EmptyIterator = class$("com.sun.xml.bind.util.EmptyIterator")) : TypedListFieldRenderer.class$com$sun$xml$bind$util$EmptyIterator).staticRef("theInstance"), this.ref(true).invoke("iterator")));
        final JMethod $size = this.writer.declareMethod((JType)this.codeModel.INT, "sizeOf" + this.fu.name);
        $size.body()._return(this.count());
        final JMethod $set = this.writer.declareMethod(this.fu.type, "set" + this.fu.name);
        $idx = this.writer.addParameter((JType)this.codeModel.INT, "idx");
        $value = this.writer.addParameter(this.fu.type, "value");
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        body = $set.body();
        body._return(JExpr.cast(this.fu.type, this.ref(false).invoke("set").arg($idx).arg($value)));
        this.writer.javadoc().addParam($value, "allowed object is\n" + JavadocBuilder.listPossibleTypes(this.fu));
    }
    
    static {
        TypedListFieldRenderer.theFactory = (FieldRendererFactory)new TypedListFieldRenderer$1();
    }
}
