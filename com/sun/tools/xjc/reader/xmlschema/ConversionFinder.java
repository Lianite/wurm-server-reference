// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import java.util.Collections;
import java.util.HashSet;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnum;
import com.sun.codemodel.JJavaName;
import java.util.Iterator;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIEnumMember;
import com.sun.tools.xjc.grammar.xducer.EnumerationXducer;
import com.sun.xml.xsom.XSFacet;
import com.sun.msv.grammar.Expression;
import com.sun.xml.xsom.visitor.XSVisitor;
import com.sun.xml.xsom.impl.util.SchemaWriter;
import java.io.Writer;
import com.sun.codemodel.util.JavadocEscapeWriter;
import java.io.StringWriter;
import com.sun.codemodel.JClassContainer;
import com.sun.tools.xjc.grammar.xducer.IdentityTransducer;
import org.xml.sax.Locator;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.xml.xsom.XSSimpleType;
import org.relaxng.datatype.DatatypeException;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.tools.xjc.grammar.xducer.BuiltinDatatypeTransducerFactory;
import com.sun.msv.datatype.xsd.DatatypeFactory;
import java.util.Hashtable;
import java.util.Set;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import java.util.Map;
import java.util.HashMap;

final class ConversionFinder
{
    private static final HashMap emptyHashMap;
    private final BGMBuilder builder;
    private final Map builtinConversions;
    private final XSSimpleTypeFunction functor;
    private static final Set builtinTypeSafeEnumCapableTypes;
    
    ConversionFinder(final BGMBuilder _builder) {
        this.builtinConversions = new Hashtable();
        this.functor = (XSSimpleTypeFunction)new ConversionFinder$1(this);
        this.builder = _builder;
        final String[] names = { "anySimpleType", "ID", "IDREF", "boolean", "base64Binary", "hexBinary", "float", "decimal", "integer", "long", "unsignedInt", "int", "unsignedShort", "short", "unsignedByte", "byte", "double", "QName", "token", "normalizedString", "date", "dateTime", "time" };
        try {
            for (int i = 0; i < names.length; ++i) {
                this.builtinConversions.put(names[i], BuiltinDatatypeTransducerFactory.getWithoutWhitespaceNormalization(this.builder.grammar, DatatypeFactory.getTypeByName(names[i])));
            }
        }
        catch (DatatypeException e) {
            e.printStackTrace();
            throw new JAXBAssertionError();
        }
    }
    
    public Transducer find(final XSSimpleType type) {
        return type.apply((XSSimpleTypeFunction<Transducer>)this.functor);
    }
    
    private boolean shouldBeMappedToTypeSafeEnumByDefault(final XSRestrictionSimpleType type) {
        if (type.isLocal()) {
            return false;
        }
        if (!this.canBeMappedToTypeSafeEnum(type)) {
            return false;
        }
        if (type.getDeclaredFacet("enumeration") == null) {
            return false;
        }
        XSSimpleType t = type;
        while (!t.isGlobal() || !this.builder.getGlobalBinding().canBeMappedToTypeSafeEnum(t)) {
            t = t.getSimpleBaseType();
            if (t == null) {
                return false;
            }
        }
        return true;
    }
    
    private boolean canBeMappedToTypeSafeEnum(XSSimpleType type) {
        do {
            if ("http://www.w3.org/2001/XMLSchema".equals(type.getTargetNamespace())) {
                final String localName = type.getName();
                if (localName != null) {
                    if (localName.equals("anySimpleType")) {
                        return false;
                    }
                    if (localName.equals("ID") || localName.equals("IDREF")) {
                        return false;
                    }
                    if (ConversionFinder.builtinTypeSafeEnumCapableTypes.contains(localName)) {
                        return true;
                    }
                }
            }
            type = type.getSimpleBaseType();
        } while (type != null);
        return false;
    }
    
    private Transducer bindToTypeSafeEnum(final XSRestrictionSimpleType type, String className, final String javadoc, final HashMap members, Locator loc) {
        if (loc == null) {
            loc = type.getLocator();
        }
        if (className == null) {
            if (!type.isGlobal()) {
                this.builder.errorReporter.error(loc, "ConversionFinder.NoEnumNameAvailable");
                return new IdentityTransducer(this.builder.grammar.codeModel);
            }
            className = type.getName();
        }
        className = this.builder.getNameConverter().toClassName(className);
        final JDefinedClass clazz = this.builder.selector.codeModelClassFactory.createClass(this.builder.selector.getPackage(type.getTargetNamespace()), className, type.getLocator());
        final StringWriter out = new StringWriter();
        final SchemaWriter sw = new SchemaWriter(new JavadocEscapeWriter(out));
        type.visit(sw);
        final JDocComment jdoc = clazz.javadoc();
        jdoc.appendComment((javadoc != null) ? (javadoc + "\n\n") : "");
        jdoc.appendComment(Messages.format("ClassSelector.JavadocHeading", (Object)type.getName()));
        jdoc.appendComment("\n<p>\n<pre>\n");
        jdoc.appendComment(out.getBuffer().toString());
        jdoc.appendComment("</pre>");
        final boolean needsToGenerateMemberName = this.checkIfMemberNamesNeedToBeGenerated(type, members);
        HashMap memberMap = new HashMap();
        int idx = 1;
        Expression exp = Expression.nullSet;
        final XSDatatype baseDt = this.builder.simpleTypeBuilder.datatypeBuilder.build(type.getSimpleBaseType());
        final Iterator itr = type.iterateDeclaredFacets();
        while (itr.hasNext()) {
            final XSFacet facet = itr.next();
            if (!facet.getName().equals("enumeration")) {
                continue;
            }
            final Expression vexp = this.builder.pool.createValue(baseDt, baseDt.createValue(facet.getValue(), facet.getContext()));
            if (needsToGenerateMemberName) {
                memberMap.put(vexp, new EnumerationXducer.MemberInfo("value" + idx++, null));
            }
            else {
                BIEnumMember mem = members.get(facet.getValue());
                if (mem == null) {
                    mem = (BIEnumMember)this.builder.getBindInfo(facet).get(BIEnumMember.NAME);
                }
                if (mem != null) {
                    memberMap.put(vexp, mem.createMemberInfo());
                }
            }
            exp = this.builder.pool.createChoice(exp, vexp);
        }
        if (memberMap.isEmpty()) {
            memberMap = ConversionFinder.emptyHashMap;
        }
        final BIConversion conv = new BIConversion(type.getLocator(), (Transducer)new EnumerationXducer(NameConverter.standard, clazz, exp, memberMap, loc));
        conv.markAsAcknowledged();
        this.builder.getOrCreateBindInfo(type).addDecl(conv);
        return conv.getTransducer();
    }
    
    private boolean checkIfMemberNamesNeedToBeGenerated(final XSRestrictionSimpleType type, final HashMap members) {
        final Iterator itr = type.iterateDeclaredFacets();
        while (itr.hasNext()) {
            final XSFacet facet = itr.next();
            if (!facet.getName().equals("enumeration")) {
                continue;
            }
            final String value = facet.getValue();
            if (members.containsKey(value)) {
                continue;
            }
            if (!JJavaName.isJavaIdentifier(this.builder.getNameConverter().toConstantName(facet.getValue()))) {
                return this.builder.getGlobalBinding().needsToGenerateEnumMemberName();
            }
        }
        return false;
    }
    
    private Transducer lookup(final XSSimpleType type) {
        final BindInfo info = this.builder.getBindInfo(type);
        final BIConversion conv = (BIConversion)info.get(BIConversion.NAME);
        if (conv != null) {
            conv.markAsAcknowledged();
            return conv.getTransducer();
        }
        final BIEnum en = (BIEnum)info.get(BIEnum.NAME);
        if (en == null) {
            if (type.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
                final String name = type.getName();
                if (name != null) {
                    return this.lookupBuiltin(name);
                }
            }
            return null;
        }
        en.markAsAcknowledged();
        if (!this.canBeMappedToTypeSafeEnum(type)) {
            this.builder.errorReporter.error(en.getLocation(), "ConversionFinder.CannotBeTypeSafeEnum");
            this.builder.errorReporter.error(type.getLocator(), "ConversionFinder.CannotBeTypeSafeEnum.Location");
            return null;
        }
        return this.bindToTypeSafeEnum((XSRestrictionSimpleType)type, en.getClassName(), en.getJavadoc(), en.getMembers(), en.getLocation());
    }
    
    private Transducer lookupBuiltin(final String typeName) {
        return this.builtinConversions.get(typeName);
    }
    
    static {
        ConversionFinder.emptyHashMap = new HashMap();
        final Set s = new HashSet();
        final String[] typeNames = { "string", "boolean", "float", "decimal", "double", "anyURI" };
        for (int i = 0; i < typeNames.length; ++i) {
            s.add(typeNames[i]);
        }
        ConversionFinder.builtinTypeSafeEnumCapableTypes = Collections.unmodifiableSet((Set<?>)s);
    }
}
