// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import com.sun.xml.xsom.util.DeferedCollection;
import java.util.Iterator;
import com.sun.xml.xsom.impl.scd.Iterators;
import java.util.Collection;
import java.util.List;
import com.sun.xml.xsom.impl.scd.TokenMgrError;
import java.text.ParseException;
import com.sun.xml.xsom.impl.scd.SCDImpl;
import com.sun.xml.xsom.impl.scd.Step;
import com.sun.xml.xsom.impl.scd.SCDParser;
import javax.xml.namespace.NamespaceContext;

public abstract class SCD
{
    public static SCD create(final String path, final NamespaceContext nsContext) throws ParseException {
        try {
            final SCDParser p = new SCDParser(path, nsContext);
            final List<?> list = (List<?>)p.RelativeSchemaComponentPath();
            return new SCDImpl(path, list.toArray(new Step[list.size()]));
        }
        catch (TokenMgrError e) {
            throw setCause(new ParseException(e.getMessage(), -1), e);
        }
        catch (com.sun.xml.xsom.impl.scd.ParseException e2) {
            throw setCause(new ParseException(e2.getMessage(), e2.currentToken.beginColumn), e2);
        }
    }
    
    private static ParseException setCause(final ParseException e, final Throwable x) {
        e.initCause(x);
        return e;
    }
    
    public final Collection<XSComponent> select(final XSComponent contextNode) {
        return new DeferedCollection<XSComponent>(this.select(Iterators.singleton(contextNode)));
    }
    
    public final Collection<XSComponent> select(final XSSchemaSet contextNode) {
        return this.select(contextNode.getSchemas());
    }
    
    public final XSComponent selectSingle(final XSComponent contextNode) {
        final Iterator<XSComponent> r = this.select(Iterators.singleton(contextNode));
        if (r.hasNext()) {
            return r.next();
        }
        return null;
    }
    
    public final XSComponent selectSingle(final XSSchemaSet contextNode) {
        final Iterator<XSComponent> r = this.select(contextNode.iterateSchema());
        if (r.hasNext()) {
            return r.next();
        }
        return null;
    }
    
    public abstract Iterator<XSComponent> select(final Iterator<? extends XSComponent> p0);
    
    public final Collection<XSComponent> select(final Collection<? extends XSComponent> contextNodes) {
        return new DeferedCollection<XSComponent>(this.select(contextNodes.iterator()));
    }
    
    public abstract String toString();
}
