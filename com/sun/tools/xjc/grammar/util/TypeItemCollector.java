// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar.util;

import com.sun.tools.xjc.grammar.IgnoreItem;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.xml.bind.JAXBAssertionError;
import com.sun.tools.xjc.grammar.SuperClassItem;
import com.sun.tools.xjc.grammar.ExternalItem;
import com.sun.tools.xjc.grammar.PrimitiveItem;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.TypeItem;
import com.sun.msv.grammar.Expression;
import java.util.Vector;
import com.sun.tools.xjc.grammar.BGMWalker;

public final class TypeItemCollector extends BGMWalker
{
    private final Vector vec;
    
    private TypeItemCollector() {
        this.vec = new Vector();
    }
    
    public static TypeItem[] collect(final Expression e) {
        final TypeItemCollector tic = new TypeItemCollector();
        e.visit((ExpressionVisitorVoid)tic);
        return tic.vec.toArray(new TypeItem[tic.vec.size()]);
    }
    
    public Object onClass(final ClassItem item) {
        this.vec.add(item);
        return null;
    }
    
    public Object onInterface(final InterfaceItem item) {
        this.vec.add(item);
        return null;
    }
    
    public Object onPrimitive(final PrimitiveItem item) {
        this.vec.add(item);
        return null;
    }
    
    public Object onExternal(final ExternalItem item) {
        this.vec.add(item);
        return null;
    }
    
    public Object onSuper(final SuperClassItem item) {
        throw new JAXBAssertionError();
    }
    
    public Object onField(final FieldItem item) {
        throw new JAXBAssertionError();
    }
    
    public Object onIgnore(final IgnoreItem item) {
        return null;
    }
}
