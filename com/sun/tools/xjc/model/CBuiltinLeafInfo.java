// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.impl.LeafInfoImpl;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import com.sun.tools.xjc.runtime.ZeroOneBooleanAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.transform.Source;
import java.awt.Image;
import javax.activation.DataHandler;
import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.NamespaceContext;
import com.sun.tools.xjc.util.NamespaceContextAdapter;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.xml.xsom.XmlString;
import java.util.HashMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;
import javax.activation.MimeType;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.impl.BuiltinLeafInfoImpl;

public abstract class CBuiltinLeafInfo extends BuiltinLeafInfoImpl<NType, NClass> implements CNonElement
{
    private final ID id;
    public static final Map<NType, CBuiltinLeafInfo> LEAVES;
    public static final CBuiltinLeafInfo ANYTYPE;
    public static final CBuiltinLeafInfo STRING;
    public static final CBuiltinLeafInfo BOOLEAN;
    public static final CBuiltinLeafInfo INT;
    public static final CBuiltinLeafInfo LONG;
    public static final CBuiltinLeafInfo BYTE;
    public static final CBuiltinLeafInfo SHORT;
    public static final CBuiltinLeafInfo FLOAT;
    public static final CBuiltinLeafInfo DOUBLE;
    public static final CBuiltinLeafInfo QNAME;
    public static final CBuiltinLeafInfo CALENDAR;
    public static final CBuiltinLeafInfo DURATION;
    public static final CBuiltinLeafInfo BIG_INTEGER;
    public static final CBuiltinLeafInfo BIG_DECIMAL;
    public static final CBuiltinLeafInfo BASE64_BYTE_ARRAY;
    public static final CBuiltinLeafInfo DATA_HANDLER;
    public static final CBuiltinLeafInfo IMAGE;
    public static final CBuiltinLeafInfo XML_SOURCE;
    public static final TypeUse HEXBIN_BYTE_ARRAY;
    public static final TypeUse TOKEN;
    public static final TypeUse NORMALIZED_STRING;
    public static final TypeUse ID;
    public static final TypeUse BOOLEAN_ZERO_OR_ONE;
    public static final TypeUse IDREF;
    public static final TypeUse STRING_LIST;
    
    private CBuiltinLeafInfo(final NType typeToken, final QName typeName, final ID id) {
        super(typeToken, new QName[] { typeName });
        this.id = id;
    }
    
    public JType toType(final Outline o, final Aspect aspect) {
        return ((LeafInfoImpl<NType, ClassDeclT>)this).getType().toType(o, aspect);
    }
    
    @Deprecated
    public final boolean isCollection() {
        return false;
    }
    
    @Deprecated
    public CNonElement getInfo() {
        return this;
    }
    
    public ID idUse() {
        return this.id;
    }
    
    public MimeType getExpectedMimeType() {
        return null;
    }
    
    @Deprecated
    public final CAdapter getAdapterUse() {
        return null;
    }
    
    public Locator getLocator() {
        return Model.EMPTY_LOCATOR;
    }
    
    public final XSComponent getSchemaComponent() {
        throw new UnsupportedOperationException("TODO. If you hit this, let us know.");
    }
    
    public final TypeUse makeCollection() {
        return TypeUseFactory.makeCollection(this);
    }
    
    public final TypeUse makeAdapted(final Class<? extends XmlAdapter> adapter, final boolean copy) {
        return TypeUseFactory.adapt(this, adapter, copy);
    }
    
    public final TypeUse makeMimeTyped(final MimeType mt) {
        return TypeUseFactory.makeMimeTyped(this, mt);
    }
    
    static {
        LEAVES = new HashMap<NType, CBuiltinLeafInfo>();
        ANYTYPE = new NoConstantBuiltin(Object.class, "anyType");
        STRING = new Builtin(String.class, "string") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.lit(lexical.value);
            }
        };
        BOOLEAN = new Builtin(Boolean.class, "boolean") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.lit(DatatypeConverterImpl._parseBoolean(lexical.value));
            }
        };
        INT = new Builtin(Integer.class, "int") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.lit(DatatypeConverterImpl._parseInt(lexical.value));
            }
        };
        LONG = new Builtin(Long.class, "long") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.lit(DatatypeConverterImpl._parseLong(lexical.value));
            }
        };
        BYTE = new Builtin(Byte.class, "byte") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.cast(outline.getCodeModel().BYTE, JExpr.lit(DatatypeConverterImpl._parseByte(lexical.value)));
            }
        };
        SHORT = new Builtin(Short.class, "short") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.cast(outline.getCodeModel().SHORT, JExpr.lit(DatatypeConverterImpl._parseShort(lexical.value)));
            }
        };
        FLOAT = new Builtin(Float.class, "float") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.lit(DatatypeConverterImpl._parseFloat(lexical.value));
            }
        };
        DOUBLE = new Builtin(Double.class, "double") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr.lit(DatatypeConverterImpl._parseDouble(lexical.value));
            }
        };
        QNAME = new Builtin(QName.class, "QName") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                final QName qn = DatatypeConverterImpl._parseQName(lexical.value, new NamespaceContextAdapter(lexical));
                return JExpr._new(outline.getCodeModel().ref(QName.class)).arg(qn.getNamespaceURI()).arg(qn.getLocalPart()).arg(qn.getPrefix());
            }
        };
        CALENDAR = new NoConstantBuiltin(XMLGregorianCalendar.class, "\u0000");
        DURATION = new NoConstantBuiltin(Duration.class, "duration");
        BIG_INTEGER = new Builtin(BigInteger.class, "integer") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr._new(outline.getCodeModel().ref(BigInteger.class)).arg(lexical.value.trim());
            }
        };
        BIG_DECIMAL = new Builtin(BigDecimal.class, "decimal") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return JExpr._new(outline.getCodeModel().ref(BigDecimal.class)).arg(lexical.value.trim());
            }
        };
        BASE64_BYTE_ARRAY = new Builtin(byte[].class, "base64Binary") {
            public JExpression createConstant(final Outline outline, final XmlString lexical) {
                return outline.getCodeModel().ref(DatatypeConverter.class).staticInvoke("parseBase64Binary").arg(lexical.value);
            }
        };
        DATA_HANDLER = new NoConstantBuiltin(DataHandler.class, "base64Binary");
        IMAGE = new NoConstantBuiltin(Image.class, "base64Binary");
        XML_SOURCE = new NoConstantBuiltin(Source.class, "base64Binary");
        HEXBIN_BYTE_ARRAY = CBuiltinLeafInfo.STRING.makeAdapted(HexBinaryAdapter.class, false);
        TOKEN = CBuiltinLeafInfo.STRING.makeAdapted(CollapsedStringAdapter.class, false);
        NORMALIZED_STRING = CBuiltinLeafInfo.STRING.makeAdapted(NormalizedStringAdapter.class, false);
        ID = TypeUseFactory.makeID(CBuiltinLeafInfo.TOKEN, com.sun.xml.bind.v2.model.core.ID.ID);
        BOOLEAN_ZERO_OR_ONE = CBuiltinLeafInfo.STRING.makeAdapted(ZeroOneBooleanAdapter.class, true);
        IDREF = TypeUseFactory.makeID(CBuiltinLeafInfo.ANYTYPE, com.sun.xml.bind.v2.model.core.ID.IDREF);
        STRING_LIST = CBuiltinLeafInfo.STRING.makeCollection();
    }
    
    private abstract static class Builtin extends CBuiltinLeafInfo
    {
        protected Builtin(final Class c, final String typeName) {
            this(c, typeName, com.sun.xml.bind.v2.model.core.ID.NONE);
        }
        
        protected Builtin(final Class c, final String typeName, final ID id) {
            super(NavigatorImpl.theInstance.ref(c), new QName("http://www.w3.org/2001/XMLSchema", typeName), id, null);
            Builtin.LEAVES.put(((LeafInfoImpl<NType, ClassDeclT>)this).getType(), this);
        }
        
        public CCustomizations getCustomizations() {
            return CCustomizations.EMPTY;
        }
    }
    
    private static final class NoConstantBuiltin extends Builtin
    {
        public NoConstantBuiltin(final Class c, final String typeName) {
            super(c, typeName);
        }
        
        public JExpression createConstant(final Outline outline, final XmlString lexical) {
            return null;
        }
    }
}
