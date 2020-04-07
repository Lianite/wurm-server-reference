// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.msv.datatype.xsd.ListType;
import com.sun.msv.datatype.xsd.UnionType;
import java.util.Iterator;
import com.sun.msv.util.StringPair;
import org.relaxng.datatype.Datatype;
import com.sun.msv.datatype.xsd.EnumerationFacet;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.Expression;
import java.util.HashMap;
import com.sun.msv.grammar.ExpressionPool;
import java.util.Map;
import com.sun.tools.xjc.grammar.util.BreadthFirstExpressionCloner;

public class DatatypeSimplifier extends BreadthFirstExpressionCloner
{
    private final Map dataExps;
    
    public DatatypeSimplifier(final ExpressionPool pool) {
        super(pool);
        this.dataExps = new HashMap();
    }
    
    public Expression onAnyString() {
        return this.pool.createData((XSDatatype)StringType.theInstance);
    }
    
    public Expression onData(final DataExp exp) {
        if (!(exp.dt instanceof XSDatatype)) {
            return (Expression)exp;
        }
        Expression r = this.dataExps.get(exp);
        if (r == null) {
            r = this.processDatatype((XSDatatype)exp.dt, false);
            this.dataExps.put(exp, r);
        }
        return r;
    }
    
    private Expression processDatatype(final XSDatatype dt, final boolean inList) {
        final EnumerationFacet ef = (EnumerationFacet)dt.getFacetObject("enumeration");
        if (ef != null) {
            return this.processEnumeration(dt, ef);
        }
        switch (dt.getVariety()) {
            case 1: {
                return this.pool.createData(dt);
            }
            case 3: {
                return this.processUnion(dt, inList);
            }
            case 2: {
                return this.processList(dt, inList);
            }
            default: {
                throw new Error();
            }
        }
    }
    
    private Expression processEnumeration(final XSDatatype type, final EnumerationFacet enums) {
        Expression exp = Expression.nullSet;
        for (final Object v : enums.values) {
            exp = this.pool.createChoice(exp, this.pool.createValue((Datatype)type, (StringPair)null, v));
        }
        return exp;
    }
    
    private Expression processUnion(XSDatatype dt, final boolean inList) {
        if (dt.getFacetObject("enumeration") != null) {
            throw new Error(Messages.format("DatatypeSimplifier.EnumFacetUnsupported"));
        }
        if (dt.getFacetObject("pattern") != null) {
            throw new Error(Messages.format("DatatypeSimplifier.PatternFacetUnsupported"));
        }
        while (!(dt instanceof UnionType)) {
            dt = dt.getBaseType();
            if (dt == null) {
                throw new Error();
            }
        }
        final UnionType ut = (UnionType)dt;
        Expression exp = Expression.nullSet;
        for (int i = 0; i < ut.memberTypes.length; ++i) {
            exp = this.pool.createChoice(exp, this.processDatatype((XSDatatype)ut.memberTypes[i], inList));
        }
        return exp;
    }
    
    private Expression processList(final XSDatatype dt, final boolean inList) {
        if (dt.getFacetObject("enumeration") != null) {
            throw new Error(Messages.format("DatatypeSimplifier.EnumFacetUnsupported"));
        }
        if (dt.getFacetObject("pattern") != null) {
            throw new Error(Messages.format("DatatypeSimplifier.PatternFacetUnsupported"));
        }
        XSDatatype d = dt;
        while (!(d instanceof ListType)) {
            d = d.getBaseType();
            if (d == null) {
                throw new Error();
            }
        }
        final ListType lt = (ListType)d;
        final Expression item = this.processDatatype((XSDatatype)lt.itemType, true);
        final Expression exp = this.pool.createZeroOrMore(item);
        if (inList) {
            return exp;
        }
        return this.pool.createList(exp);
    }
}
