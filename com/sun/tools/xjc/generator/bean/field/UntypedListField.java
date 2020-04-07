// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JAssignmentTarget;
import java.util.ArrayList;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JClass;

public class UntypedListField extends AbstractListField
{
    private final JClass coreList;
    private JMethod $get;
    
    protected UntypedListField(final ClassOutlineImpl context, final CPropertyInfo prop, final JClass coreList) {
        super(context, prop, !coreList.fullName().equals("java.util.ArrayList"));
        this.coreList = coreList.narrow(this.exposedType.boxify());
        this.generate();
    }
    
    protected final JClass getCoreListType() {
        return this.coreList;
    }
    
    public void generateAccessors() {
        final MethodWriter writer = this.outline.createMethodWriter();
        final Accessor acc = this.create(JExpr._this());
        this.$get = writer.declareMethod(this.listT, "get" + this.prop.getName(true));
        writer.javadoc().append(this.prop.javadoc);
        final JBlock block = this.$get.body();
        this.fixNullRef(block);
        block._return(acc.ref(true));
        final String pname = NameConverter.standard.toVariableName(this.prop.getName(true));
        writer.javadoc().append("Gets the value of the " + pname + " property.\n\n" + "<p>\n" + "This accessor method returns a reference to the live list,\n" + "not a snapshot. Therefore any modification you make to the\n" + "returned list will be present inside the JAXB object.\n" + "This is why there is not a <CODE>set</CODE> method for the " + pname + " property.\n" + "\n" + "<p>\n" + "For example, to add a new item, do as follows:\n" + "<pre>\n" + "   get" + this.prop.getName(true) + "().add(newItem);\n" + "</pre>\n" + "\n\n");
        writer.javadoc().append("<p>\nObjects of the following type(s) are allowed in the list\n").append(this.listPossibleTypes(this.prop));
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
            block.assign($var, JExpr._new(UntypedListField.this.codeModel.ref(ArrayList.class).narrow(UntypedListField.this.exposedType.boxify())).arg(this.$target.invoke(UntypedListField.this.$get)));
        }
        
        public void fromRawValue(final JBlock block, final String uniqueName, final JExpression $var) {
            final JVar $list = block.decl(UntypedListField.this.listT, uniqueName + 'l', this.$target.invoke(UntypedListField.this.$get));
            block.invoke($list, "addAll").arg($var);
        }
    }
}
