// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import java.text.ParseException;
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.xml.xsom.XSModelGroup;

public final class NameGenerator
{
    public static String getName(final BGMBuilder builder, final XSModelGroup mg) throws ParseException {
        final StringBuffer name = new StringBuffer();
        mg.visit((XSTermVisitor)new NameGenerator$1(name, builder));
        if (name.length() == 0) {
            throw new ParseException("no element", -1);
        }
        return name.toString();
    }
}
