// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JTryBlock;
import com.sun.tools.xjc.grammar.xducer.TypeAdaptedTransducer;
import java.util.Iterator;
import com.sun.tools.xjc.generator.unmarshaller.automaton.State;
import com.sun.codemodel.JBlock;
import com.sun.msv.grammar.NameClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.XMLDeserializerContextImpl;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.xml.sax.Locator;
import com.sun.codemodel.JClassContainer;
import java.util.HashMap;
import java.util.Map;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.grammar.xducer.DeserializerContext;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Automaton;
import com.sun.tools.xjc.generator.ClassContext;
import com.sun.codemodel.JCodeModel;

class PerClassGenerator
{
    final UnmarshallerGenerator parent;
    private final JCodeModel codeModel;
    final ClassContext context;
    final Automaton automaton;
    final JDefinedClass unmarshaller;
    final JFieldRef $state;
    final JFieldRef $context;
    private final DeserializerContext dc;
    final TransitionTable transitionTable;
    JVar $tracer;
    private int idGen;
    private final Map interleaveDispatcherImpls;
    private final Map eatTextFunctions;
    private final Map dispatchLookupFunctions;
    
    public int createId() {
        return ++this.idGen;
    }
    
    PerClassGenerator(final UnmarshallerGenerator _parent, final Automaton a) {
        this.idGen = 0;
        this.interleaveDispatcherImpls = new HashMap();
        this.eatTextFunctions = new HashMap();
        this.dispatchLookupFunctions = new HashMap();
        this.parent = _parent;
        this.codeModel = this.parent.codeModel;
        this.context = a.getOwner();
        this.automaton = a;
        final JDefinedClass impl = this.context.implClass;
        impl._implements(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallableObject == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallableObject = class$("com.sun.tools.xjc.runtime.UnmarshallableObject")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallableObject));
        (this.unmarshaller = this.parent.context.getClassFactory().createClass(impl, "Unmarshaller", null))._extends(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$AbstractUnmarshallingEventHandlerImpl == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$AbstractUnmarshallingEventHandlerImpl = class$("com.sun.tools.xjc.runtime.AbstractUnmarshallingEventHandlerImpl")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$AbstractUnmarshallingEventHandlerImpl));
        JMethod method = this.unmarshaller.method(1, (PerClassGenerator.class$java$lang$Object == null) ? (PerClassGenerator.class$java$lang$Object = class$("java.lang.Object")) : PerClassGenerator.class$java$lang$Object, "owner");
        method.body()._return(impl.staticRef("this"));
        this.$state = JExpr.ref("state");
        this.$context = JExpr.ref("context");
        this.dc = (DeserializerContext)new XMLDeserializerContextImpl(this.$context);
        method = impl.method(1, this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.UnmarshallingEventHandler")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler), "createUnmarshaller");
        final JVar $context = method.param(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext = class$("com.sun.tools.xjc.runtime.UnmarshallingContext")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext), "context");
        method.body()._return(JExpr._new(this.unmarshaller).arg($context));
        this.transitionTable = new TransitionTable(this.automaton);
    }
    
    protected void generate() {
        JMethod con = this.unmarshaller.constructor(1);
        JVar $context = con.param(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext = class$("com.sun.tools.xjc.runtime.UnmarshallingContext")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext), "context");
        con.body().invoke("super").arg($context).arg(JExpr.lit(this.generateEncodedTextType()));
        if (this.parent.trace) {
            this.$tracer = this.unmarshaller.field(4, (PerClassGenerator.class$com$sun$xml$bind$unmarshaller$Tracer == null) ? (PerClassGenerator.class$com$sun$xml$bind$unmarshaller$Tracer = class$("com.sun.xml.bind.unmarshaller.Tracer")) : PerClassGenerator.class$com$sun$xml$bind$unmarshaller$Tracer, "tracer");
            con.body().assign(this.$tracer, $context.invoke("getTracer"));
        }
        con = this.unmarshaller.constructor(2);
        $context = con.param(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext = class$("com.sun.tools.xjc.runtime.UnmarshallingContext")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingContext), "context");
        final JVar $init = con.param(this.codeModel.INT, "startState");
        con.body().invoke("this").arg($context);
        con.body().assign(this.$state, $init);
        new EnterElementMethodGenerator(this).generate();
        new LeaveElementMethodGenerator(this).generate();
        new EnterAttributeMethodGenerator(this).generate();
        new EnterLeaveMethodGenerator(this, "leaveAttribute", (PerClassGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveAttribute == null) ? (PerClassGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveAttribute = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$LeaveAttribute")) : PerClassGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveAttribute).generate();
        new TextMethodGenerator(this).generate();
        this.generateLeaveChild();
    }
    
    private JClass getRuntime(final Class clazz) {
        return this.parent.context.getRuntime(clazz);
    }
    
    public JClass getInterleaveDispatcher(final Alphabet.Interleave a) {
        final JClass cls = this.interleaveDispatcherImpls.get(a);
        if (cls != null) {
            return cls;
        }
        JDefinedClass impl = null;
        try {
            impl = this.unmarshaller._class(4, "Interleave" + this.createId());
        }
        catch (JClassAlreadyExistsException e) {
            e.printStackTrace();
            _assert(false);
        }
        impl._extends(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$InterleaveDispatcher == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$InterleaveDispatcher = class$("com.sun.tools.xjc.runtime.InterleaveDispatcher")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$InterleaveDispatcher));
        final JMethod cstr = impl.constructor(4);
        final JInvocation arrayInit = JExpr._new(this.getRuntime((PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler == null) ? (PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler = class$("com.sun.tools.xjc.runtime.UnmarshallingEventHandler")) : PerClassGenerator.class$com$sun$tools$xjc$runtime$UnmarshallingEventHandler).array());
        for (int i = 0; i < a.branches.length; ++i) {
            arrayInit.arg(JExpr._new(this.unmarshaller).arg(JExpr._super().ref("sites").component(JExpr.lit(i))).arg(this.getStateNumber(a.branches[i].initialState)));
        }
        cstr.body().invoke("super").arg(this.$context).arg(JExpr.lit(a.branches.length));
        cstr.body().invoke("init").arg(arrayInit);
        this.generateGetBranchForXXX(impl, a, "Element", 0);
        this.generateGetBranchForXXX(impl, a, "Attribute", 1);
        final JMethod m = impl.method(2, this.codeModel.INT, "getBranchForText");
        m.body()._return(JExpr.lit(a.getTextBranchIndex()));
        this.interleaveDispatcherImpls.put(a, impl);
        return impl;
    }
    
    private void generateGetBranchForXXX(final JDefinedClass clazz, final Alphabet.Interleave a, final String methodSuffix, final int nameIdx) {
        final JMethod method = clazz.method(2, this.codeModel.INT, "getBranchFor" + methodSuffix);
        final JVar $uri = method.param(this.codeModel.ref((PerClassGenerator.class$java$lang$String == null) ? (PerClassGenerator.class$java$lang$String = class$("java.lang.String")) : PerClassGenerator.class$java$lang$String), "uri");
        final JVar $local = method.param(this.codeModel.ref((PerClassGenerator.class$java$lang$String == null) ? (PerClassGenerator.class$java$lang$String = class$("java.lang.String")) : PerClassGenerator.class$java$lang$String), "local");
        for (int i = 0; i < a.branches.length; ++i) {
            final Alphabet.Interleave.Branch br = a.branches[i];
            final NameClass nc = br.getName(nameIdx);
            if (!nc.isNull()) {
                method.body()._if(this.parent.generateNameClassTest(nc, $uri, $local))._then()._return(JExpr.lit(i));
            }
        }
        method.body()._return(JExpr.lit(-1));
    }
    
    protected final void generateGoto(final JBlock $body, final State target) {
        this.generateGoto($body, this.getStateNumber(target));
    }
    
    private JExpression getStateNumber(final State state) {
        return JExpr.lit(this.automaton.getStateNumber(state));
    }
    
    private void generateGoto(final JBlock $body, final JExpression nextState) {
        if (this.parent.trace) {
            $body.invoke(this.$tracer, "nextState").arg(nextState);
        }
        $body.assign(this.$state, nextState);
    }
    
    protected void generateLeaveChild() {
        if (!this.parent.trace) {
            return;
        }
        final JMethod method = this.unmarshaller.method(1, this.codeModel.VOID, "leaveChild");
        method._throws((PerClassGenerator.class$org$xml$sax$SAXException == null) ? (PerClassGenerator.class$org$xml$sax$SAXException = class$("org.xml.sax.SAXException")) : PerClassGenerator.class$org$xml$sax$SAXException);
        final JVar $nextState = method.param(this.codeModel.INT, "nextState");
        method.body().invoke(this.$tracer, "nextState").arg($nextState);
        method.body().invoke(JExpr._super(), "leaveChild").arg($nextState);
    }
    
    private String generateEncodedTextType() {
        final StringBuffer buf = new StringBuffer(this.automaton.getStateSize());
        for (int i = this.automaton.getStateSize() - 1; i >= 0; --i) {
            buf.append('-');
        }
        final Iterator itr = this.automaton.states();
        while (itr.hasNext()) {
            final State s = itr.next();
            buf.setCharAt(this.automaton.getStateNumber(s), s.isListState ? 'L' : '-');
        }
        return buf.toString();
    }
    
    protected final void eatText(final JBlock block, final Alphabet.BoundText ta, final JExpression $attValue) {
        JMethod method = this.eatTextFunctions.get(ta);
        if (method == null) {
            method = this.generateEatTextFunction(ta);
            this.eatTextFunctions.put(ta, method);
        }
        block.invoke(method).arg($attValue);
    }
    
    private JMethod generateEatTextFunction(final Alphabet.BoundText ta) {
        final JMethod method = this.unmarshaller.method(4, this.codeModel.VOID, "eatText" + this.createId());
        method._throws((PerClassGenerator.class$org$xml$sax$SAXException == null) ? (PerClassGenerator.class$org$xml$sax$SAXException = class$("org.xml.sax.SAXException")) : PerClassGenerator.class$org$xml$sax$SAXException);
        final JVar $value = method.param(8, (PerClassGenerator.class$java$lang$String == null) ? (PerClassGenerator.class$java$lang$String = class$("java.lang.String")) : PerClassGenerator.class$java$lang$String, "value");
        final JTryBlock $try = method.body()._try();
        final JCatchBlock $catch = $try._catch(this.codeModel.ref((PerClassGenerator.class$java$lang$Exception == null) ? (PerClassGenerator.class$java$lang$Exception = class$("java.lang.Exception")) : PerClassGenerator.class$java$lang$Exception));
        $catch.body().invoke("handleParseConversionException").arg($catch.param("e"));
        if (this.parent.trace) {
            $try.body().invoke(this.$tracer, "onConvertValue").arg($value).arg(JExpr.lit(ta.field.getFieldUse().name));
        }
        if (!ta.item.xducer.needsDelayedDeserialization()) {
            ta.field.setter($try.body(), TypeAdaptedTransducer.adapt(ta.item.xducer, ta.field).generateDeserializer((JExpression)$value, this.dc));
        }
        else {
            final JDefinedClass patcher = this.codeModel.newAnonymousClass(this.codeModel.ref((PerClassGenerator.class$java$lang$Runnable == null) ? (PerClassGenerator.class$java$lang$Runnable = class$("java.lang.Runnable")) : PerClassGenerator.class$java$lang$Runnable));
            final JMethod run = patcher.method(1, this.codeModel.VOID, "run");
            ta.field.setter(run.body(), ta.item.xducer.generateDeserializer((JExpression)$value, this.dc));
            $try.body().invoke(this.$context, "addPatcher").arg(JExpr._new(patcher));
        }
        return method;
    }
    
    protected final JExpression invokeLookup(final Alphabet.Dispatch da, final TransitionTable.Entry tte) {
        JMethod lookup = this.dispatchLookupFunctions.get(da);
        if (lookup == null) {
            this.dispatchLookupFunctions.put(da, lookup = this.generateDispatchFunction(da, tte));
        }
        return JExpr.invoke(lookup);
    }
    
    protected final JMethod generateDispatchFunction(final Alphabet.Dispatch da, final TransitionTable.Entry tte) {
        final JMethod lookup = this.unmarshaller.method(4, (PerClassGenerator.class$java$lang$Class == null) ? (PerClassGenerator.class$java$lang$Class = class$("java.lang.Class")) : PerClassGenerator.class$java$lang$Class, "lookup" + this.createId());
        lookup._throws((PerClassGenerator.class$org$xml$sax$SAXException == null) ? (PerClassGenerator.class$org$xml$sax$SAXException = class$("org.xml.sax.SAXException")) : PerClassGenerator.class$org$xml$sax$SAXException);
        final JBlock body = lookup.body();
        final JExpression $context = JExpr.ref("context");
        final JVar $idx = body.decl(this.codeModel.INT, "idx", $context.invoke("getAttribute").arg(JExpr.lit(da.attName.namespaceURI)).arg(JExpr.lit(da.attName.localName)));
        final JConditional cond = body._if($idx.gte(JExpr.lit(0)));
        cond._then()._return(da.table.lookup(this.parent.context, $context.invoke("eatAttribute").arg($idx), $context));
        cond._else()._return(JExpr._null());
        return lookup;
    }
    
    private static final void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
}
