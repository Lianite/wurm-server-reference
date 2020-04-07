// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.marshaller;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JExpression;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.Expression;
import com.sun.tools.xjc.grammar.FieldItem;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.generator.util.BlockReference;
import com.sun.tools.xjc.generator.util.ExistingBlockReference;
import com.sun.codemodel.JBlock;
import java.util.Stack;
import java.util.Map;
import com.sun.tools.xjc.grammar.FieldUse;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.xducer.SerializerContext;

final class Context implements SerializerContext
{
    Side currentSide;
    Pass currentPass;
    public final GeneratorContext genContext;
    public final JVar $serializer;
    public final JCodeModel codeModel;
    protected final ClassItem classItem;
    public final ExpressionPool pool;
    boolean inOneOrMore;
    private final Builder builder;
    private FieldUse fu;
    private int iota;
    private final Map fieldMarshallers;
    private final Stack overridedFMGs;
    private final Stack blocks;
    private final Inside inside;
    private final Outside outside;
    final Pass bodyPass;
    final Pass attPass;
    final Pass uriPass;
    final Pass skipPass;
    
    public Context(final GeneratorContext _genContext, final ExpressionPool _pool, final ClassItem _class, final JBlock codeBlock, final JVar _$serializer, final Map _fieldMarshallers) {
        this.inOneOrMore = false;
        this.builder = new Builder(this);
        this.fu = null;
        this.iota = 0;
        this.overridedFMGs = new Stack();
        this.blocks = new Stack();
        this.inside = new Inside(this);
        this.outside = new Outside(this);
        this.bodyPass = (Pass)new BodyPass(this, "Body");
        this.attPass = (Pass)new AttributePass(this);
        this.uriPass = (Pass)new URIPass(this);
        this.skipPass = (Pass)new SkipPass(this);
        this.genContext = _genContext;
        this.pool = _pool;
        this.classItem = _class;
        this.$serializer = _$serializer;
        this.fieldMarshallers = _fieldMarshallers;
        this.codeModel = this.classItem.owner.codeModel;
        this.currentSide = (Side)this.outside;
        this.pushNewBlock(new ExistingBlockReference(codeBlock));
    }
    
    protected final JClass getRuntime(final Class clazz) {
        return this.genContext.getRuntime(clazz);
    }
    
    protected final boolean isInside() {
        return this.currentSide == this.inside;
    }
    
    public FieldMarshallerGenerator getMarshaller(final FieldItem fi) {
        return this.fieldMarshallers.get(this.classItem.getDeclaredField(fi.name));
    }
    
    public void pushNewFieldMarshallerMapping(final FieldMarshallerGenerator original, final FieldMarshallerGenerator _new) {
        final Object old = this.fieldMarshallers.put(original.owner().getFieldUse(), _new);
        _assert(old == original);
        this.overridedFMGs.push(original);
    }
    
    public void popFieldMarshallerMapping() {
        final FieldMarshallerGenerator fmg = this.overridedFMGs.pop();
        this.fieldMarshallers.put(fmg.owner().getFieldUse(), fmg);
    }
    
    public void pushFieldItem(final FieldItem item) {
        _assert(this.fu == null);
        this.fu = this.classItem.getDeclaredField(item.name);
        this.currentSide = (Side)this.inside;
        _assert(this.fu != null);
    }
    
    public void popFieldItem(final FieldItem item) {
        _assert(this.fu != null && this.fu.name.equals(item.name));
        this.fu = null;
        this.currentSide = (Side)this.outside;
    }
    
    public FieldMarshallerGenerator getCurrentFieldMarshaller() {
        return this.fieldMarshallers.get(this.fu);
    }
    
    public void pushNewBlock(final BlockReference newBlock) {
        this.blocks.push(newBlock);
    }
    
    public void pushNewBlock(final JBlock block) {
        this.pushNewBlock(new ExistingBlockReference(block));
    }
    
    public void popBlock() {
        this.blocks.pop();
    }
    
    public BlockReference getCurrentBlock() {
        return this.blocks.peek();
    }
    
    public String createIdentifier() {
        return '_' + Integer.toString(this.iota++);
    }
    
    public void build(final Expression exp) {
        exp.visit((ExpressionVisitorVoid)this.builder);
    }
    
    public JExpression getNamespaceContext() {
        return this.$serializer.invoke("getNamespaceContext");
    }
    
    public JExpression onID(final JExpression object, final JExpression value) {
        return this.$serializer.invoke("onID").arg(object).arg(value);
    }
    
    public JExpression onIDREF(final JExpression target) {
        return this.$serializer.invoke("onIDREF").arg(target);
    }
    
    public void declareNamespace(final JBlock block, final JExpression uri, final JExpression prefix, final JExpression requirePrefix) {
        block.invoke(this.getNamespaceContext(), "declareNamespace").arg(uri).arg(prefix).arg(requirePrefix);
    }
    
    private static void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
