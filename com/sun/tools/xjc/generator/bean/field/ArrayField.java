// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import java.util.Arrays;
import com.sun.tools.xjc.outline.FieldAccessor;
import java.util.ArrayList;
import com.sun.codemodel.JClass;
import java.util.List;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JMethod;

final class ArrayField extends AbstractListField
{
    private JMethod $setAll;
    private JMethod $getAll;
    
    ArrayField(final ClassOutlineImpl context, final CPropertyInfo prop) {
        super(context, prop, false);
        this.generate();
    }
    
    public void generateAccessors() {
        final MethodWriter writer = this.outline.createMethodWriter();
        final Accessor acc = this.create(JExpr._this());
        final JType arrayType = this.exposedType.array();
        this.$getAll = writer.declareMethod(this.exposedType.array(), "get" + this.prop.getName(true));
        writer.javadoc().append(this.prop.javadoc);
        JBlock body = this.$getAll.body();
        body._if(acc.ref(true).eq(JExpr._null()))._then()._return(JExpr.newArray(this.exposedType, 0));
        if (this.primitiveType == null) {
            body._return(JExpr.cast(arrayType, acc.ref(true).invoke("toArray").arg(JExpr.newArray(this.implType, acc.ref(true).invoke("size")))));
        }
        else {
            final JVar $r = body.decl(this.exposedType.array(), "r", JExpr.newArray(this.exposedType, acc.ref(true).invoke("size")));
            final JForLoop loop = body._for();
            final JVar $i = loop.init(this.codeModel.INT, "__i", JExpr.lit(0));
            loop.test($i.lt($r.ref("length")));
            loop.update($i.incr());
            loop.body().assign($r.component($i), this.primitiveType.unwrap(acc.ref(true).invoke("get").arg($i)));
            body._return($r);
        }
        final List<Object> returnTypes = this.listPossibleTypes(this.prop);
        writer.javadoc().addReturn().append("array of\n").append(returnTypes);
        final JMethod $get = writer.declareMethod(this.exposedType, "get" + this.prop.getName(true));
        JVar $idx = writer.addParameter(this.codeModel.INT, "idx");
        $get.body()._if(acc.ref(true).eq(JExpr._null()))._then()._throw(JExpr._new(this.codeModel.ref(IndexOutOfBoundsException.class)));
        writer.javadoc().append(this.prop.javadoc);
        $get.body()._return(acc.unbox(acc.ref(true).invoke("get").arg($idx)));
        writer.javadoc().addReturn().append("one of\n").append(returnTypes);
        final JMethod $getLength = writer.declareMethod(this.codeModel.INT, "get" + this.prop.getName(true) + "Length");
        $getLength.body()._if(acc.ref(true).eq(JExpr._null()))._then()._return(JExpr.lit(0));
        $getLength.body()._return(acc.ref(true).invoke("size"));
        this.$setAll = writer.declareMethod(this.codeModel.VOID, "set" + this.prop.getName(true));
        writer.javadoc().append(this.prop.javadoc);
        JVar $value = writer.addParameter(this.exposedType.array(), "values");
        this.$setAll.body().invoke(acc.ref(false), "clear");
        final JVar $len = this.$setAll.body().decl(this.codeModel.INT, "len", $value.ref("length"));
        final JForLoop _for = this.$setAll.body()._for();
        final JVar $i2 = _for.init(this.codeModel.INT, "i", JExpr.lit(0));
        _for.test(JOp.lt($i2, $len));
        _for.update($i2.incr());
        _for.body().invoke(acc.ref(true), "add").arg(this.castToImplType(acc.box($value.component($i2))));
        writer.javadoc().addParam($value).append("allowed objects are\n").append(returnTypes);
        final JMethod $set = writer.declareMethod(this.exposedType, "set" + this.prop.getName(true));
        $idx = writer.addParameter(this.codeModel.INT, "idx");
        $value = writer.addParameter(this.exposedType, "value");
        writer.javadoc().append(this.prop.javadoc);
        body = $set.body();
        body._return(acc.unbox(acc.ref(true).invoke("set").arg($idx).arg(this.castToImplType(acc.box($value)))));
        writer.javadoc().addParam($value).append("allowed object is\n").append(returnTypes);
    }
    
    protected JClass getCoreListType() {
        return this.codeModel.ref(ArrayList.class).narrow(this.exposedType.boxify());
    }
    
    public Accessor create(final JExpression targetObject) {
        return new Accessor(targetObject);
    }
    
    class Accessor extends AbstractListField.Accessor
    {
        protected Accessor(final JExpression $target) {
            super($target);
        }
        
        public void toRawValue(final JBlock block, final JVar $var) {
            block.assign($var, ArrayField.this.codeModel.ref(Arrays.class).staticInvoke("asList").arg(this.$target.invoke(ArrayField.this.$getAll)));
        }
        
        public void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            block.invoke(this.$target, ArrayField.this.$setAll).arg($var.invoke("toArray").arg(JExpr.newArray(ArrayField.this.exposedType, $var.invoke("size"))));
        }
    }
}
