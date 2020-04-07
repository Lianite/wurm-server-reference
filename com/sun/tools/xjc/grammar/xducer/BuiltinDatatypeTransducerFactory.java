// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.xducer;

import com.sun.msv.datatype.xsd.DateTimeType;
import com.sun.msv.datatype.xsd.TimeType;
import com.sun.msv.datatype.xsd.DateType;
import com.sun.msv.datatype.xsd.QnameType;
import com.sun.msv.datatype.xsd.ByteType;
import com.sun.msv.datatype.xsd.UnsignedByteType;
import com.sun.msv.datatype.xsd.ShortType;
import com.sun.msv.datatype.xsd.UnsignedShortType;
import com.sun.msv.datatype.xsd.IntType;
import com.sun.msv.datatype.xsd.UnsignedIntType;
import com.sun.msv.datatype.xsd.LongType;
import com.sun.msv.datatype.xsd.IntegerType;
import com.sun.msv.datatype.xsd.NumberType;
import com.sun.msv.datatype.xsd.DoubleType;
import com.sun.msv.datatype.xsd.FloatType;
import com.sun.msv.datatype.xsd.HexBinaryType;
import com.sun.msv.datatype.xsd.Base64BinaryType;
import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.tools.xjc.grammar.id.IDREFTransducer;
import com.sun.msv.datatype.xsd.IDREFType;
import com.sun.tools.xjc.grammar.id.IDTransducer;
import com.sun.msv.datatype.xsd.IDType;
import com.sun.msv.datatype.xsd.TokenType;
import com.sun.msv.datatype.xsd.NormalizedStringType;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.SimpleURType;
import com.sun.tools.xjc.generator.util.WhitespaceNormalizer;
import com.sun.msv.datatype.xsd.WhiteSpaceFacet;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.lang.reflect.Method;
import com.sun.codemodel.JType;
import com.sun.xml.bind.JAXBAssertionError;
import java.lang.reflect.Modifier;
import com.sun.codemodel.JCodeModel;

public class BuiltinDatatypeTransducerFactory
{
    private static Transducer create(final JCodeModel model, final Class type) {
        try {
            final Method m = type.getMethod("load", (BuiltinDatatypeTransducerFactory.class$java$lang$String == null) ? (BuiltinDatatypeTransducerFactory.class$java$lang$String = class$("java.lang.String")) : BuiltinDatatypeTransducerFactory.class$java$lang$String);
            final String className = type.getName();
            if (!Modifier.isStatic(m.getModifiers())) {
                throw new JAXBAssertionError();
            }
            return (Transducer)new UserTransducer((JType)model.ref(m.getReturnType()), className + ".load", className + ".save");
        }
        catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("cannot find the load method for " + type.getName());
        }
    }
    
    private static Transducer create(final JType returnType, final String stem) {
        return (Transducer)new UserTransducer(returnType, "javax.xml.bind.DatatypeConverter.parse" + stem, "javax.xml.bind.DatatypeConverter.print" + stem);
    }
    
    public static Transducer get(final AnnotatedGrammar grammar, final XSDatatype dt) {
        final Transducer base = getWithoutWhitespaceNormalization(grammar, dt);
        if (dt instanceof XSDatatypeImpl) {
            return WhitespaceTransducer.create(base, grammar.codeModel, ((XSDatatypeImpl)dt).whiteSpace);
        }
        final WhiteSpaceFacet wsf = (WhiteSpaceFacet)dt.getFacetObject("whiteSpace");
        if (wsf != null) {
            return WhitespaceTransducer.create(base, grammar.codeModel, wsf.whiteSpace);
        }
        return WhitespaceTransducer.create(base, grammar.codeModel, WhitespaceNormalizer.COLLAPSE);
    }
    
    public static Transducer getWithoutWhitespaceNormalization(final AnnotatedGrammar grammar, final XSDatatype dt) {
        return (Transducer)new BuiltinDatatypeTransducerFactory$1(_getWithoutWhitespaceNormalization(grammar, dt));
    }
    
    private static Transducer _getWithoutWhitespaceNormalization(final AnnotatedGrammar grammar, final XSDatatype dt) {
        final JCodeModel codeModel = grammar.codeModel;
        if (dt.getVariety() != 1) {
            throw new JAXBAssertionError();
        }
        if (dt == SimpleURType.theInstance) {
            return (Transducer)new IdentityTransducer(codeModel);
        }
        if (dt == StringType.theInstance || dt == NormalizedStringType.theInstance || dt == TokenType.theInstance) {
            return (Transducer)new IdentityTransducer(codeModel);
        }
        if (dt == IDType.theInstance) {
            return (Transducer)new IDTransducer(codeModel, grammar.defaultSymbolSpace);
        }
        if (dt == IDREFType.theInstance) {
            return (Transducer)new IDREFTransducer(codeModel, grammar.defaultSymbolSpace, false);
        }
        if (dt == BooleanType.theInstance) {
            return create((JType)codeModel.BOOLEAN, "Boolean");
        }
        if (dt == Base64BinaryType.theInstance) {
            return create(codeModel, (BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$Base64BinaryType == null) ? (BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$Base64BinaryType = class$("com.sun.msv.datatype.xsd.Base64BinaryType")) : BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$Base64BinaryType);
        }
        if (dt == HexBinaryType.theInstance) {
            return create(codeModel, (BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$HexBinaryType == null) ? (BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$HexBinaryType = class$("com.sun.msv.datatype.xsd.HexBinaryType")) : BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$HexBinaryType);
        }
        if (dt == FloatType.theInstance) {
            return create((JType)codeModel.FLOAT, "Float");
        }
        if (dt == DoubleType.theInstance) {
            return create((JType)codeModel.DOUBLE, "Double");
        }
        if (dt == NumberType.theInstance) {
            return create((JType)codeModel.ref((BuiltinDatatypeTransducerFactory.class$java$math$BigDecimal == null) ? (BuiltinDatatypeTransducerFactory.class$java$math$BigDecimal = class$("java.math.BigDecimal")) : BuiltinDatatypeTransducerFactory.class$java$math$BigDecimal), "Decimal");
        }
        if (dt == IntegerType.theInstance) {
            return create((JType)codeModel.ref((BuiltinDatatypeTransducerFactory.class$java$math$BigInteger == null) ? (BuiltinDatatypeTransducerFactory.class$java$math$BigInteger = class$("java.math.BigInteger")) : BuiltinDatatypeTransducerFactory.class$java$math$BigInteger), "Integer");
        }
        if (dt == LongType.theInstance || dt == UnsignedIntType.theInstance) {
            return create((JType)codeModel.LONG, "Long");
        }
        if (dt == IntType.theInstance || dt == UnsignedShortType.theInstance) {
            return create((JType)codeModel.INT, "Int");
        }
        if (dt == ShortType.theInstance || dt == UnsignedByteType.theInstance) {
            return create((JType)codeModel.SHORT, "Short");
        }
        if (dt == ByteType.theInstance) {
            return create((JType)codeModel.BYTE, "Byte");
        }
        if (dt == QnameType.theInstance) {
            return (Transducer)new QNameTransducer(codeModel);
        }
        if (dt == DateType.theInstance) {
            return create((JType)codeModel.ref((BuiltinDatatypeTransducerFactory.class$java$util$Calendar == null) ? (BuiltinDatatypeTransducerFactory.class$java$util$Calendar = class$("java.util.Calendar")) : BuiltinDatatypeTransducerFactory.class$java$util$Calendar), "Date");
        }
        if (dt == TimeType.theInstance) {
            return (Transducer)new DateTransducer(codeModel, codeModel.ref((BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$TimeType == null) ? (BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$TimeType = class$("com.sun.msv.datatype.xsd.TimeType")) : BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$TimeType));
        }
        if (dt == DateTimeType.theInstance) {
            return (Transducer)new DateTransducer(codeModel, codeModel.ref((BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$DateTimeType == null) ? (BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$DateTimeType = class$("com.sun.msv.datatype.xsd.DateTimeType")) : BuiltinDatatypeTransducerFactory.class$com$sun$msv$datatype$xsd$DateTimeType));
        }
        return _getWithoutWhitespaceNormalization(grammar, dt.getBaseType());
    }
}
