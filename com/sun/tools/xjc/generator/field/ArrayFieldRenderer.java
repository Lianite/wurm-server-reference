// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.field;

import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JOp;
import com.sun.tools.xjc.generator.JavadocBuilder;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JType;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.tools.xjc.generator.ClassContext;

public class ArrayFieldRenderer extends AbstractListFieldRenderer
{
    public static final FieldRendererFactory theFactory;
    
    protected ArrayFieldRenderer(final ClassContext cc, final FieldUse fu, final JClass coreList) {
        super(cc, fu, coreList);
    }
    
    public void generateAccessors() {
        final JType arrayType = this.fu.type.array();
        final JType exposedType = this.fu.type;
        final JType internalType = (this.primitiveType != null) ? this.primitiveType.getWrapperClass() : this.fu.type;
        JMethod $get = this.writer.declareMethod((JType)exposedType.array(), "get" + this.fu.name);
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        JBlock body = $get.body();
        if (this.$defValues != null) {
            final JBlock then = body._if(this.hasSetValue().not())._then();
            final JVar $r = then.decl(exposedType.array(), "r", JExpr.newArray(exposedType, this.$defValues.ref("length")));
            then.staticInvoke(this.codeModel.ref((ArrayFieldRenderer.class$java$lang$System == null) ? (ArrayFieldRenderer.class$java$lang$System = class$("java.lang.System")) : ArrayFieldRenderer.class$java$lang$System), "arraycopy").arg(this.$defValues).arg(JExpr.lit(0)).arg($r).arg(JExpr.lit(0)).arg(this.$defValues.ref("length"));
            then._return($r);
        }
        else {
            body._if(this.ref(true).eq(JExpr._null()))._then()._return(JExpr.newArray(exposedType, 0));
        }
        if (this.primitiveType == null) {
            body._return(JExpr.cast(arrayType, this.ref(true).invoke("toArray").arg(JExpr.newArray(this.fu.type, this.ref(true).invoke("size")))));
        }
        else {
            final JVar $r2 = body.decl(exposedType.array(), "r", JExpr.newArray(exposedType, this.ref(true).invoke("size")));
            final JForLoop loop = body._for();
            final JVar $i = loop.init(this.codeModel.INT, "__i", JExpr.lit(0));
            loop.test($i.lt($r2.ref("length")));
            loop.update($i.incr());
            loop.body().assign($r2.component($i), this.primitiveType.unwrap(JExpr.cast(internalType, this.ref(true).invoke("get").arg($i))));
            body._return($r2);
        }
        this.writer.javadoc().addReturn("array of\n" + JavadocBuilder.listPossibleTypes(this.fu));
        $get = this.writer.declareMethod(exposedType, "get" + this.fu.name);
        JVar $idx = this.writer.addParameter((JType)this.codeModel.INT, "idx");
        if (this.$defValues != null) {
            final JBlock then = $get.body()._if(this.hasSetValue().not())._then();
            then._return(this.$defValues.component($idx));
        }
        else {
            $get.body()._if(this.ref(true).eq(JExpr._null()))._then()._throw(JExpr._new(this.codeModel.ref((ArrayFieldRenderer.class$java$lang$IndexOutOfBoundsException == null) ? (ArrayFieldRenderer.class$java$lang$IndexOutOfBoundsException = class$("java.lang.IndexOutOfBoundsException")) : ArrayFieldRenderer.class$java$lang$IndexOutOfBoundsException)));
        }
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        $get.body()._return(this.unbox((JExpression)JExpr.cast(internalType, this.ref(true).invoke("get").arg($idx))));
        this.writer.javadoc().addReturn("one of\n" + JavadocBuilder.listPossibleTypes(this.fu));
        final JMethod $getLength = this.writer.declareMethod((JType)this.codeModel.INT, "get" + this.fu.name + "Length");
        if (this.$defValues != null) {
            $getLength.body()._if(this.hasSetValue().not())._then()._return(this.$defValues.ref("length"));
        }
        else {
            $getLength.body()._if(this.ref(true).eq(JExpr._null()))._then()._return(JExpr.lit(0));
        }
        $getLength.body()._return(this.ref(true).invoke("size"));
        JMethod $set = this.writer.declareMethod((JType)this.codeModel.VOID, "set" + this.fu.name);
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        JVar $value = this.writer.addParameter((JType)exposedType.array(), "values");
        $set.body().invoke(this.ref(false), "clear");
        final JVar $len = $set.body().decl(this.codeModel.INT, "len", $value.ref("length"));
        final JForLoop _for = $set.body()._for();
        final JVar $i2 = _for.init(this.codeModel.INT, "i", JExpr.lit(0));
        _for.test(JOp.lt($i2, $len));
        _for.update($i2.incr());
        _for.body().invoke(this.ref(true), "add").arg(this.box((JExpression)$value.component($i2)));
        this.writer.javadoc().addParam($value, "allowed objects are\n" + JavadocBuilder.listPossibleTypes(this.fu));
        $set = this.writer.declareMethod(exposedType, "set" + this.fu.name);
        $idx = this.writer.addParameter((JType)this.codeModel.INT, "idx");
        $value = this.writer.addParameter(exposedType, "value");
        this.writer.javadoc().appendComment(this.fu.getJavadoc());
        body = $set.body();
        body._return(this.unbox((JExpression)JExpr.cast(internalType, this.ref(true).invoke("set").arg($idx).arg(this.box((JExpression)$value)))));
        this.writer.javadoc().addParam($value, "allowed object is\n" + JavadocBuilder.listPossibleTypes(this.fu));
    }
    
    static {
        ArrayFieldRenderer.theFactory = (FieldRendererFactory)new ArrayFieldRenderer$1();
    }
}
