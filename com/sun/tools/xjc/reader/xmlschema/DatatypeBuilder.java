// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;
import java.util.Iterator;
import com.sun.msv.datatype.xsd.ErrorType;
import com.sun.xml.xsom.XSFacet;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.xml.xsom.XSSimpleType;
import org.relaxng.datatype.DatatypeException;
import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.tools.xjc.reader.Const;
import java.util.HashMap;
import com.sun.xml.xsom.XSSchemaSet;
import java.util.Map;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;

public class DatatypeBuilder implements XSSimpleTypeFunction
{
    private final BGMBuilder builder;
    private final Map cache;
    
    DatatypeBuilder(final BGMBuilder builder, final XSSchemaSet schemas) {
        this.cache = new HashMap();
        this.builder = builder;
        try {
            for (int i = 0; i < Const.builtinTypeNames.length; ++i) {
                final XSSimpleType type = schemas.getSimpleType("http://www.w3.org/2001/XMLSchema", Const.builtinTypeNames[i]);
                _assert(type != null);
                this.cache.put(type, DatatypeFactory.getTypeByName(Const.builtinTypeNames[i]));
            }
        }
        catch (DatatypeException e) {
            e.printStackTrace();
            _assert(false);
        }
    }
    
    public XSDatatype build(final XSSimpleType type) {
        return type.apply((XSSimpleTypeFunction<XSDatatype>)this);
    }
    
    public Object restrictionSimpleType(final XSRestrictionSimpleType type) {
        XSDatatype dt = this.cache.get(type);
        if (dt != null) {
            return dt;
        }
        try {
            final TypeIncubator ti = new TypeIncubator(this.build(type.getSimpleBaseType()));
            final Iterator itr = type.iterateDeclaredFacets();
            while (itr.hasNext()) {
                final XSFacet facet = itr.next();
                ti.addFacet(facet.getName(), facet.getValue(), facet.isFixed(), facet.getContext());
            }
            dt = (XSDatatype)ti.derive(type.getTargetNamespace(), type.getName());
            this.cache.put(type, dt);
            return dt;
        }
        catch (DatatypeException e) {
            this.builder.errorReporter.error(type.getLocator(), "DatatypeBuilder.DatatypeError", (Object)e.getMessage());
            return ErrorType.theInstance;
        }
    }
    
    public Object listSimpleType(final XSListSimpleType type) {
        XSDatatype dt = this.cache.get(type);
        if (dt != null) {
            return dt;
        }
        try {
            dt = DatatypeFactory.deriveByList(type.getTargetNamespace(), type.getName(), this.build(type.getItemType()));
            this.cache.put(type, dt);
            return dt;
        }
        catch (DatatypeException e) {
            this.builder.errorReporter.error(type.getLocator(), "DatatypeBuilder.DatatypeError", (Object)e.getMessage());
            return ErrorType.theInstance;
        }
    }
    
    public Object unionSimpleType(final XSUnionSimpleType type) {
        XSDatatype dt = this.cache.get(type);
        if (dt != null) {
            return dt;
        }
        try {
            final XSDatatype[] members = new XSDatatype[type.getMemberSize()];
            for (int i = 0; i < members.length; ++i) {
                members[i] = this.build(type.getMember(i));
            }
            dt = DatatypeFactory.deriveByUnion(type.getTargetNamespace(), type.getName(), members);
            this.cache.put(type, dt);
            return dt;
        }
        catch (DatatypeException e) {
            this.builder.errorReporter.error(type.getLocator(), "DatatypeBuilder.DatatypeError", (Object)e.getMessage());
            return ErrorType.theInstance;
        }
    }
    
    private static final void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
