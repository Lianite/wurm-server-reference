// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.visitor.XSFunction;

public abstract class AbstractXSFunctionImpl implements XSFunction
{
    public Object annotation(final XSAnnotation ann) {
        _assert(false);
        return null;
    }
    
    public Object schema(final XSSchema schema) {
        _assert(false);
        return null;
    }
    
    public Object facet(final XSFacet facet) {
        _assert(false);
        return null;
    }
    
    public Object notation(final XSNotation not) {
        _assert(false);
        return null;
    }
    
    protected static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
