// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.xml.bind.JAXBAssertionError;
import java.util.TreeSet;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.grammar.xducer.DatabindableXducer;
import org.xml.sax.Locator;
import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import java.util.Iterator;
import com.sun.codemodel.JType;
import java.util.HashSet;
import java.util.HashMap;
import com.sun.msv.grammar.Expression;
import java.util.Comparator;
import com.sun.codemodel.JClass;
import java.util.Set;
import java.util.Map;
import com.sun.tools.xjc.grammar.id.SymbolSpace;
import com.sun.codemodel.JCodeModel;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;

public final class AnnotatedGrammar extends ReferenceExp implements Grammar
{
    private final ExpressionPool pool;
    public final JCodeModel codeModel;
    public final SymbolSpace defaultSymbolSpace;
    private final Map symbolSpaces;
    private final Map classes;
    private final Map interfaces;
    private final Set primitives;
    public JClass rootClass;
    public Long serialVersionUID;
    private static final Comparator packageComparator;
    
    public AnnotatedGrammar(final ExpressionPool pool) {
        this(null, pool, new JCodeModel());
    }
    
    public AnnotatedGrammar(final Grammar source, final JCodeModel _codeModel) {
        this(source.getTopLevel(), source.getPool(), _codeModel);
    }
    
    public AnnotatedGrammar(final Expression topLevel, final ExpressionPool pool, final JCodeModel _codeModel) {
        super("");
        this.symbolSpaces = new HashMap();
        this.classes = new HashMap();
        this.interfaces = new HashMap();
        this.primitives = new HashSet();
        this.serialVersionUID = null;
        this.exp = topLevel;
        this.pool = pool;
        this.codeModel = _codeModel;
        (this.defaultSymbolSpace = new SymbolSpace(this.codeModel)).setType((JType)this.codeModel.ref((AnnotatedGrammar.class$java$lang$Object == null) ? (AnnotatedGrammar.class$java$lang$Object = class$("java.lang.Object")) : AnnotatedGrammar.class$java$lang$Object));
    }
    
    public Expression getTopLevel() {
        return this.exp;
    }
    
    public ExpressionPool getPool() {
        return this.pool;
    }
    
    public PrimitiveItem[] getPrimitives() {
        return this.primitives.toArray(new PrimitiveItem[this.primitives.size()]);
    }
    
    public ClassItem[] getClasses() {
        return (ClassItem[])this.classes.values().toArray(new ClassItem[this.classes.size()]);
    }
    
    public Iterator iterateClasses() {
        return this.classes.values().iterator();
    }
    
    public InterfaceItem[] getInterfaces() {
        return (InterfaceItem[])this.interfaces.values().toArray(new InterfaceItem[this.interfaces.size()]);
    }
    
    public Iterator iterateInterfaces() {
        return this.interfaces.values().iterator();
    }
    
    public SymbolSpace getSymbolSpace(final String name) {
        SymbolSpace ss = this.symbolSpaces.get(name);
        if (ss == null) {
            this.symbolSpaces.put(name, ss = new SymbolSpace(this.codeModel));
        }
        return ss;
    }
    
    public PrimitiveItem createPrimitiveItem(final Transducer _xducer, final DatabindableDatatype _guard, final Expression _exp, final Locator loc) {
        final PrimitiveItem pi = new PrimitiveItem(_xducer, _guard, _exp, loc);
        this.primitives.add(pi);
        return pi;
    }
    
    public PrimitiveItem createPrimitiveItem(final JCodeModel writer, final DatabindableDatatype dt, final Expression exp, final Locator loc) {
        return new PrimitiveItem((Transducer)new DatabindableXducer(writer, dt), dt, exp, loc);
    }
    
    public ClassItem getClassItem(final JDefinedClass type) {
        return this.classes.get(type);
    }
    
    public ClassItem createClassItem(final JDefinedClass type, final Expression body, final Locator loc) {
        if (this.classes.containsKey(type)) {
            System.err.println("class name " + type.fullName() + " is already defined");
            for (final JDefinedClass cls : this.classes.keySet()) {
                System.err.println(cls.fullName());
            }
            _assert(false);
        }
        final ClassItem o = new ClassItem(this, type, body, loc);
        this.classes.put(type, o);
        return o;
    }
    
    public InterfaceItem getInterfaceItem(final JDefinedClass type) {
        return this.interfaces.get(type);
    }
    
    public InterfaceItem createInterfaceItem(final JClass type, final Expression body, final Locator loc) {
        _assert(!this.interfaces.containsKey(type));
        final InterfaceItem o = new InterfaceItem(type, body, loc);
        this.interfaces.put(type, o);
        return o;
    }
    
    public JPackage[] getUsedPackages() {
        final Set s = new TreeSet(AnnotatedGrammar.packageComparator);
        Iterator itr = this.iterateClasses();
        while (itr.hasNext()) {
            s.add(itr.next().getTypeAsDefined()._package());
        }
        itr = this.iterateInterfaces();
        while (itr.hasNext()) {
            s.add(itr.next().getTypeAsClass()._package());
        }
        return s.toArray(new JPackage[s.size()]);
    }
    
    private static final void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
    
    static {
        AnnotatedGrammar.packageComparator = (Comparator)new AnnotatedGrammar$1();
    }
}
