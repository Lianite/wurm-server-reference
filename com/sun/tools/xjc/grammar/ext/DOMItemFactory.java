// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.ext;

import com.sun.tools.xjc.grammar.ExternalItem;
import org.xml.sax.Locator;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import com.sun.msv.grammar.NameClass;

public abstract class DOMItemFactory
{
    public abstract ExternalItem create(final NameClass p0, final AnnotatedGrammar p1, final Locator p2);
    
    public static DOMItemFactory getInstance(String type) throws UndefinedNameException {
        type = type.toUpperCase();
        if (type.equals("W3C")) {
            return W3CDOMItem.factory;
        }
        if (type.equals("DOM4J")) {
            return Dom4jItem.factory;
        }
        throw new UndefinedNameException(type);
    }
    
    public static class UndefinedNameException extends Exception
    {
        UndefinedNameException(final String typeName) {
            super("DOM type " + typeName + " is not recognized ");
        }
    }
}
