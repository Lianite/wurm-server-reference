// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.msv.datatype.xsd.LongType;
import com.sun.msv.datatype.xsd.IntType;
import com.sun.msv.datatype.xsd.ShortType;
import com.sun.msv.datatype.xsd.ByteType;
import com.sun.msv.datatype.xsd.DoubleType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.tools.xjc.grammar.xducer.BuiltinDatatypeTransducerFactory;
import com.sun.msv.datatype.xsd.FloatType;
import com.sun.tools.xjc.grammar.id.SymbolSpace;
import com.sun.tools.xjc.grammar.xducer.TypeAdaptedTransducer;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.grammar.xducer.CastTranducer;
import com.sun.codemodel.JPrimitiveType;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.xducer.DelayedTransducer;

public class MagicTransducer extends DelayedTransducer
{
    private final JType targetType;
    private BIConversion parent;
    protected static final String ERR_ATTRIBUTE_REQUIRED = "MagicTransducer.AttributeRequired";
    
    public MagicTransducer(final JType _targetType) {
        this.targetType = _targetType;
    }
    
    public void setParent(final BIConversion conv) {
        this.parent = conv;
    }
    
    protected Transducer create() {
        if (this.targetType.isPrimitive()) {
            return new CastTranducer((JPrimitiveType)this.targetType, this.createCore());
        }
        final JPrimitiveType unboxed = ((JClass)this.targetType).getPrimitiveType();
        if (unboxed == null) {
            return this.error();
        }
        return TypeAdaptedTransducer.adapt(new CastTranducer(unboxed, this.createCore()), this.targetType);
    }
    
    public boolean isID() {
        return false;
    }
    
    public SymbolSpace getIDSymbolSpace() {
        return null;
    }
    
    protected Transducer createCore() {
        final XSSimpleType owner = this.findOwner();
        final AnnotatedGrammar grammar = this.parent.getBuilder().grammar;
        for (XSSimpleType st = owner; st != null; st = st.getSimpleBaseType()) {
            if ("http://www.w3.org/2001/XMLSchema".equals(st.getTargetNamespace())) {
                final String name = st.getName().intern();
                if (name == "float") {
                    return BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)FloatType.theInstance);
                }
                if (name == "double") {
                    return BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)DoubleType.theInstance);
                }
                if (name == "byte") {
                    return BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)ByteType.theInstance);
                }
                if (name == "short") {
                    return BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)ShortType.theInstance);
                }
                if (name == "int") {
                    return BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)IntType.theInstance);
                }
                if (name == "long") {
                    return BuiltinDatatypeTransducerFactory.get(grammar, (XSDatatype)LongType.theInstance);
                }
            }
        }
        return this.error();
    }
    
    private XSSimpleType findOwner() {
        final XSComponent c = this.parent.getOwner();
        if (c instanceof XSSimpleType) {
            return (XSSimpleType)c;
        }
        if (c instanceof XSComplexType) {
            return ((XSComplexType)c).getContentType().asSimpleType();
        }
        if (c instanceof XSElementDecl) {
            return ((XSElementDecl)c).getType().asSimpleType();
        }
        if (c instanceof XSAttributeDecl) {
            return ((XSAttributeDecl)c).getType();
        }
        return null;
    }
    
    private Transducer error() {
        this.parent.getBuilder().errorReceiver.error(this.parent.getLocation(), Messages.format("MagicTransducer.AttributeRequired"));
        return new IdentityTransducer(this.parent.getBuilder().grammar.codeModel);
    }
}
