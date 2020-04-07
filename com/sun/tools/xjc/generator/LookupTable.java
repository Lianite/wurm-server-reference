// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.msv.grammar.ValueExp;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.xducer.DeserializerContext;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.xducer.BuiltinDatatypeTransducerFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;
import com.sun.codemodel.JExpression;
import java.util.Iterator;
import java.util.HashSet;
import com.sun.tools.xjc.grammar.xducer.Transducer;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import java.util.Set;

public class LookupTable
{
    private final Set entries;
    private final int id;
    private final LookupTableFactory owner;
    private JMethod $lookup;
    private JMethod $reverseLookup;
    private JMethod $add;
    private JFieldVar $map;
    private JFieldVar $rmap;
    private Transducer xducer;
    private GeneratorContext genContext;
    
    LookupTable(final LookupTableFactory _owner, final int _id) {
        this.entries = new HashSet();
        this.owner = _owner;
        this.id = _id;
    }
    
    public boolean isConsistentWith(final LookupTable rhs) {
        for (final Entry a : this.entries) {
            if (!rhs.isConsistentWith(a)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isConsistentWith(final Entry e) {
        for (final Entry a : this.entries) {
            if (!a.isConsistentWith(e)) {
                return false;
            }
        }
        return true;
    }
    
    public void add(final Entry e) {
        this.entries.add(e);
        if (this.$lookup != null) {
            this.generateEntry(e);
        }
    }
    
    public void absorb(final LookupTable rhs) {
        for (final Entry e : rhs.entries) {
            this.add(e);
        }
    }
    
    public JExpression lookup(final GeneratorContext context, final JExpression literal, final JExpression unmContext) {
        if (this.$lookup == null) {
            this.generateCode(context);
        }
        return this.owner.getTableClass().staticInvoke(this.$lookup).arg(literal).arg(unmContext);
    }
    
    public JExpression reverseLookup(final JExpression obj, final SerializerContext serializer) {
        return this.xducer.generateSerializer((JExpression)this.owner.getTableClass().staticInvoke(this.$reverseLookup).arg(obj), serializer);
    }
    
    public void declareNamespace(final BlockReference body, final JExpression value, final SerializerContext serializer) {
        this.xducer.declareNamespace(body, (JExpression)this.owner.getTableClass().staticInvoke(this.$reverseLookup).arg(value), serializer);
    }
    
    private void generateCode(final GeneratorContext context) {
        this.genContext = context;
        final JDefinedClass table = this.owner.getTableClass();
        final JCodeModel codeModel = table.owner();
        this.$map = table.field(28, (LookupTable.class$java$util$Map == null) ? (LookupTable.class$java$util$Map = class$("java.util.Map")) : LookupTable.class$java$util$Map, "table" + this.id, JExpr._new(codeModel.ref((LookupTable.class$java$util$HashMap == null) ? (LookupTable.class$java$util$HashMap = class$("java.util.HashMap")) : LookupTable.class$java$util$HashMap)));
        this.$rmap = table.field(28, (LookupTable.class$java$util$Map == null) ? (LookupTable.class$java$util$Map = class$("java.util.Map")) : LookupTable.class$java$util$Map, "rtable" + this.id, JExpr._new(codeModel.ref((LookupTable.class$java$util$HashMap == null) ? (LookupTable.class$java$util$HashMap = class$("java.util.HashMap")) : LookupTable.class$java$util$HashMap)));
        final Entry[] e = this.entries.toArray(new Entry[this.entries.size()]);
        this.xducer = BuiltinDatatypeTransducerFactory.get(context.getGrammar(), (XSDatatype)e[0].valueExp.dt);
        for (int i = 0; i < e.length; ++i) {
            this.generateEntry(e[i]);
        }
        this.$lookup = table.method(25, (LookupTable.class$java$lang$Class == null) ? (LookupTable.class$java$lang$Class = class$("java.lang.Class")) : LookupTable.class$java$lang$Class, "lookup" + this.id);
        final JVar $literal = this.$lookup.param((LookupTable.class$java$lang$String == null) ? (LookupTable.class$java$lang$String = class$("java.lang.String")) : LookupTable.class$java$lang$String, "literal");
        final DeserializerContext dc = (DeserializerContext)new XMLDeserializerContextImpl((JExpression)this.$lookup.param(context.getRuntime((LookupTable.class$com$sun$tools$xjc$runtime$UnmarshallingContext == null) ? (LookupTable.class$com$sun$tools$xjc$runtime$UnmarshallingContext = class$("com.sun.tools.xjc.runtime.UnmarshallingContext")) : LookupTable.class$com$sun$tools$xjc$runtime$UnmarshallingContext), "context"));
        this.$lookup.body()._return(JExpr.cast(codeModel.ref((LookupTable.class$java$lang$Class == null) ? (LookupTable.class$java$lang$Class = class$("java.lang.Class")) : LookupTable.class$java$lang$Class), this.$map.invoke("get").arg(this.xducer.generateDeserializer((JExpression)$literal, dc))));
        this.$reverseLookup = table.method(25, this.xducer.getReturnType(), "reverseLookup" + this.id);
        final JVar $o = this.$reverseLookup.param((LookupTable.class$java$lang$Object == null) ? (LookupTable.class$java$lang$Object = class$("java.lang.Object")) : LookupTable.class$java$lang$Object, "o");
        this.$reverseLookup.body()._return(JExpr.cast(this.xducer.getReturnType(), this.$rmap.invoke("get").arg(codeModel.ref((LookupTable.class$com$sun$xml$bind$ProxyGroup == null) ? (LookupTable.class$com$sun$xml$bind$ProxyGroup = class$("com.sun.xml.bind.ProxyGroup")) : LookupTable.class$com$sun$xml$bind$ProxyGroup).staticInvoke("blindWrap").arg($o).arg(context.getRuntime((LookupTable.class$com$sun$tools$xjc$runtime$ValidatableObject == null) ? (LookupTable.class$com$sun$tools$xjc$runtime$ValidatableObject = class$("com.sun.tools.xjc.runtime.ValidatableObject")) : LookupTable.class$com$sun$tools$xjc$runtime$ValidatableObject).dotclass()).arg(JExpr._null()).invoke("getClass"))));
        this.$add = table.method(20, codeModel.VOID, "add" + this.id);
        final JVar $key = this.$add.param((LookupTable.class$java$lang$Object == null) ? (LookupTable.class$java$lang$Object = class$("java.lang.Object")) : LookupTable.class$java$lang$Object, "key");
        final JVar $value = this.$add.param((LookupTable.class$java$lang$Object == null) ? (LookupTable.class$java$lang$Object = class$("java.lang.Object")) : LookupTable.class$java$lang$Object, "value");
        this.$add.body().invoke(this.$map, "put").arg($key).arg($value);
        this.$add.body().invoke(this.$rmap, "put").arg($value).arg($key);
    }
    
    private void generateEntry(final Entry e) {
        this.owner.getTableClass().init().invoke("add" + this.id).arg(this.xducer.generateConstant(e.valueExp)).arg(this.genContext.getClassContext(e.target).implRef.dotclass());
    }
    
    static class Entry
    {
        private final ClassItem target;
        private final ValueExp valueExp;
        
        Entry(final ClassItem _target, final ValueExp _value) {
            this.target = _target;
            this.valueExp = _value;
        }
        
        public int hashCode() {
            return this.target.hashCode() ^ this.valueExp.name.hashCode() ^ this.valueExp.dt.valueHashCode(this.valueExp.value);
        }
        
        public boolean equals(final Object o) {
            final Entry rhs = (Entry)o;
            return this.target == rhs.target && this.valueExp.name.equals((Object)rhs.valueExp.name) && this.valueExp.dt.sameValue(this.valueExp.value, rhs.valueExp.value);
        }
        
        public boolean isConsistentWith(final Entry rhs) {
            return this.equals(rhs) || (this.target != rhs.target && this.valueExp.name.equals((Object)rhs.valueExp.name) && !this.valueExp.dt.sameValue(this.valueExp.value, rhs.valueExp.value));
        }
    }
}
