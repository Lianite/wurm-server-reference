// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.msv.grammar.Expression;
import java.util.Hashtable;
import com.sun.tools.xjc.grammar.BGMWalker;

public final class FieldItemCollector extends BGMWalker
{
    private final Hashtable m;
    
    private FieldItemCollector() {
        this.m = new Hashtable();
    }
    
    public static FieldItem[] collect(final Expression exp) {
        final FieldItemCollector fim = new FieldItemCollector();
        exp.visit((ExpressionVisitorVoid)fim);
        return (FieldItem[])fim.m.values().toArray(new FieldItem[fim.m.values().size()]);
    }
    
    public Object onSuper(final SuperClassItem item) {
        return null;
    }
    
    public Object onField(final FieldItem item) {
        this.m.put(item.name, item);
        return null;
    }
    
    public Object onIgnore(final IgnoreItem item) {
        return null;
    }
    
    public Object onClass(final ClassItem item) {
        throw new JAXBAssertionError();
    }
    
    public Object onInterface(final InterfaceItem item) {
        throw new JAXBAssertionError();
    }
    
    public Object onPrimitive(final PrimitiveItem item) {
        throw new JAXBAssertionError();
    }
    
    public Object onExternal(final ExternalItem item) {
        throw new JAXBAssertionError();
    }
}
