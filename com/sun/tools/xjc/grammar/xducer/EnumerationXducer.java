// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JMethod;
import java.util.Set;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JJavaName;
import com.sun.msv.datatype.SerializationContext;
import java.util.HashSet;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JExpr;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.Expression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JFieldVar;
import com.sun.msv.grammar.ValueExp;
import org.xml.sax.Locator;
import java.util.Map;
import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.codemodel.JDefinedClass;

public class EnumerationXducer extends TransducerImpl
{
    private final JDefinedClass type;
    private final NameConverter nameConverter;
    private final JCodeModel codeModel;
    private final Map members;
    private Locator sourceLocator;
    private boolean populated;
    private ValueExp[] values;
    private JFieldVar[] items;
    private JType valueType;
    private static final String ERR_CONTEXT_DEPENDENT_TYPE = "EnumerationXducer.ContextDependentType";
    private static final String ERR_UNSUPPORTED_TYPE_FOR_ENUM = "EnumerationXducer.UnsupportedTypeForEnum";
    private static final String ERR_UNUSABLE_NAME = "EnumerationXducer.UnusableName";
    private static final String ERR_MULTIPLE_TYPES_IN_ENUM = "EnumerationXducer.MultipleTypesInEnum";
    private static final String ERR_NAME_COLLISION = "EnumerationXducer.NameCollision";
    
    public JType getReturnType() {
        return this.type;
    }
    
    public EnumerationXducer(final NameConverter _nc, final JDefinedClass clz, final Expression enumExp, final Map _members, final Locator _sourceLocator) {
        this.populated = false;
        this.type = clz;
        this.codeModel = clz.owner();
        this.nameConverter = _nc;
        this.members = _members;
        this.sourceLocator = _sourceLocator;
        this.values = this.getValues(enumExp);
    }
    
    public void populate(final AnnotatedGrammar grammar, final GeneratorContext context) {
        if (this.populated) {
            return;
        }
        this.populated = true;
        final JClass stringRef = this.codeModel.ref((EnumerationXducer.class$java$lang$String == null) ? (EnumerationXducer.class$java$lang$String = class$("java.lang.String")) : EnumerationXducer.class$java$lang$String);
        final JClass objectRef = this.codeModel.ref((EnumerationXducer.class$java$lang$Object == null) ? (EnumerationXducer.class$java$lang$Object = class$("java.lang.Object")) : EnumerationXducer.class$java$lang$Object);
        if (!this.sanityCheck(context)) {
            return;
        }
        final Transducer xducer = BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)this.values[0].dt);
        this.valueType = xducer.getReturnType();
        final JVar $valueMap = this.type.field(28, (EnumerationXducer.class$java$util$Map == null) ? (EnumerationXducer.class$java$util$Map = class$("java.util.Map")) : EnumerationXducer.class$java$util$Map, "valueMap", JExpr._new(this.codeModel.ref((EnumerationXducer.class$java$util$HashMap == null) ? (EnumerationXducer.class$java$util$HashMap = class$("java.util.HashMap")) : EnumerationXducer.class$java$util$HashMap)));
        this.items = new JFieldVar[this.values.length];
        final JVar[] valueObjs = new JVar[this.values.length];
        final Set enumFieldNames = new HashSet();
        for (int i = 0; i < this.values.length; ++i) {
            String lexical;
            if (this.values[i].dt instanceof XSDatatype) {
                lexical = ((XSDatatype)this.values[i].dt).convertToLexicalValue(this.values[i].value, (SerializationContext)null);
            }
            else {
                lexical = this.values[i].value.toString();
            }
            final MemberInfo mem = this.members.get(this.values[i]);
            String constName = null;
            if (mem != null) {
                constName = mem.name;
            }
            if (constName == null) {
                constName = this.nameConverter.toConstantName(this.fixUnsafeCharacters(lexical));
            }
            if (!JJavaName.isJavaIdentifier(constName)) {
                this.reportError(context, Messages.format("EnumerationXducer.UnusableName", (Object)lexical, (Object)constName));
            }
            if (!enumFieldNames.add(constName)) {
                this.reportError(context, Messages.format("EnumerationXducer.NameCollision", (Object)constName));
            }
            else if (!enumFieldNames.add('_' + constName)) {
                this.reportError(context, Messages.format("EnumerationXducer.NameCollision", (Object)('_' + constName)));
            }
            valueObjs[i] = this.type.field(25, this.valueType, '_' + constName);
            (this.items[i] = this.type.field(25, this.type, constName)).init(JExpr._new(this.type).arg(valueObjs[i]));
            if (mem != null && mem.javadoc != null) {
                this.items[i].javadoc().appendComment(mem.javadoc);
            }
            valueObjs[i].init(xducer.generateDeserializer((JExpression)this.codeModel.ref((EnumerationXducer.class$com$sun$xml$bind$DatatypeConverterImpl == null) ? (EnumerationXducer.class$com$sun$xml$bind$DatatypeConverterImpl = class$("com.sun.xml.bind.DatatypeConverterImpl")) : EnumerationXducer.class$com$sun$xml$bind$DatatypeConverterImpl).staticInvoke("installHook").arg(JExpr.lit(lexical)), (DeserializerContext)null));
        }
        final JVar $lexical = this.type.field(12, stringRef, "lexicalValue");
        final JVar $value = this.type.field(12, this.valueType, "value");
        JMethod m = this.type.constructor(2);
        JVar $v = m.param(this.valueType, "v");
        m.body().assign($value, $v);
        m.body().assign($lexical, xducer.generateSerializer((JExpression)$v, (SerializerContext)null));
        m.body().invoke($valueMap, "put").arg(this.wrapToObject($v)).arg(JExpr._this());
        this.type.method(1, stringRef, "toString").body()._return($lexical);
        this.type.method(1, this.valueType, "getValue").body()._return($value);
        this.type.method(9, this.codeModel.INT, "hashCode").body()._return(JExpr._super().invoke("hashCode"));
        m = this.type.method(9, this.codeModel.BOOLEAN, "equals");
        final JVar o = m.param((EnumerationXducer.class$java$lang$Object == null) ? (EnumerationXducer.class$java$lang$Object = class$("java.lang.Object")) : EnumerationXducer.class$java$lang$Object, "o");
        m.body()._return(JExpr._super().invoke("equals").arg(o));
        final JMethod fromValue = this.type.method(17, this.type, "fromValue");
        $v = fromValue.param(this.valueType, "value");
        final JVar $t = fromValue.body().decl(this.type, "t", JExpr.cast(this.type, $valueMap.invoke("get").arg(this.wrapToObject($v))));
        final JConditional cond = fromValue.body()._if($t.eq(JExpr._null()));
        cond._then()._throw(JExpr._new(this.codeModel.ref((EnumerationXducer.class$java$lang$IllegalArgumentException == null) ? (EnumerationXducer.class$java$lang$IllegalArgumentException = class$("java.lang.IllegalArgumentException")) : EnumerationXducer.class$java$lang$IllegalArgumentException)));
        cond._else()._return($t);
        final JMethod fromString = this.type.method(17, this.type, "fromString");
        final JVar $str = fromString.param(stringRef, "str");
        final JExpression rhs = xducer.generateDeserializer((JExpression)$str, (DeserializerContext)null);
        fromString.body()._return(JExpr.invoke("fromValue").arg(rhs));
        if (grammar.serialVersionUID != null) {
            this.type._implements((EnumerationXducer.class$java$io$Serializable == null) ? (EnumerationXducer.class$java$io$Serializable = class$("java.io.Serializable")) : EnumerationXducer.class$java$io$Serializable);
            this.type.method(4, objectRef, "readResolve").body()._return(JExpr.invoke("fromValue").arg(JExpr.invoke("getValue")));
        }
    }
    
    private JExpression wrapToObject(final JExpression var) {
        if (this.valueType.isPrimitive()) {
            return ((JPrimitiveType)this.valueType).wrap(var);
        }
        return var;
    }
    
    private String fixUnsafeCharacters(final String lexical) {
        final StringBuffer buf = new StringBuffer();
        for (int len = lexical.length(), i = 0; i < len; ++i) {
            final char ch = lexical.charAt(i);
            if (!Character.isJavaIdentifierPart(ch)) {
                buf.append('-');
            }
            else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }
    
    private boolean sanityCheck(final GeneratorContext context) {
        for (int i = 0; i < this.values.length; ++i) {
            if (this.values[i].dt.isContextDependent()) {
                this.reportError(context, Messages.format("EnumerationXducer.ContextDependentType"));
                return false;
            }
            if (!(this.values[i].dt instanceof XSDatatype)) {
                this.reportError(context, Messages.format("EnumerationXducer.UnsupportedTypeForEnum", (Object)this.values[i].getName()));
            }
            if (!this.values[0].dt.equals(this.values[i].dt)) {
                this.reportError(context, Messages.format("EnumerationXducer.MultipleTypesInEnum", (Object)this.values[0].name, (Object)this.values[i].name));
                return false;
            }
        }
        return true;
    }
    
    public JExpression generateSerializer(final JExpression value, final SerializerContext context) {
        return value.invoke("toString");
    }
    
    public JExpression generateDeserializer(final JExpression value, final DeserializerContext context) {
        return this.type.staticInvoke("fromString").arg(value);
    }
    
    public JExpression generateConstant(final ValueExp exp) {
        for (int i = 0; i < this.values.length; ++i) {
            if (exp.dt.sameValue(this.values[i].value, exp.value)) {
                return this.type.staticRef(this.items[i].name());
            }
        }
        throw new JAXBAssertionError();
    }
    
    private ValueExp[] getValues(final Expression exp) {
        if (exp instanceof ChoiceExp) {
            final Expression[] children = ((ChoiceExp)exp).getChildren();
            final ValueExp[] values = new ValueExp[children.length];
            System.arraycopy(children, 0, values, 0, children.length);
            return values;
        }
        if (!(exp instanceof ValueExp)) {
            System.out.println(ExpressionPrinter.printContentModel(exp));
            throw new InternalError("assertion failed");
        }
        return new ValueExp[] { (ValueExp)exp };
    }
    
    private void reportError(final GeneratorContext context, final String msg) {
        context.getErrorReceiver().error(this.sourceLocator, msg);
    }
    
    public static class MemberInfo
    {
        public final String name;
        public final String javadoc;
        
        public MemberInfo(final String _name, final String _javadoc) {
            this.name = _name;
            this.javadoc = _javadoc;
        }
    }
}
