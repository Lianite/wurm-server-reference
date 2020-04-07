// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.xml.bind.JAXBAssertionError;
import com.sun.codemodel.JForLoop;
import com.sun.msv.grammar.NameClass;
import com.sun.codemodel.JAssignmentTarget;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.codemodel.JClass;
import java.text.MessageFormat;
import com.sun.codemodel.JInvocation;
import java.util.Iterator;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JWhileLoop;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JExpr;
import com.sun.tools.xjc.generator.unmarshaller.automaton.State;
import com.sun.codemodel.JLabel;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JCodeModel;

abstract class HandlerMethodGenerator
{
    protected final PerClassGenerator parent;
    protected final JCodeModel codeModel;
    protected final boolean trace;
    protected final JVar $tracer;
    protected final String methodName;
    private final Class alphabetType;
    protected final TransitionTable table;
    private JBlock $case;
    private JSwitch $switch;
    private JVar $attIdx;
    private JLabel outerLabel;
    
    protected HandlerMethodGenerator(final PerClassGenerator _parent, final String _mname, final Class _alphabetType) {
        this.parent = _parent;
        this.methodName = _mname;
        this.alphabetType = _alphabetType;
        this.codeModel = this.parent.parent.codeModel;
        this.trace = this.parent.parent.trace;
        this.$tracer = this.parent.$tracer;
        this.table = this.parent.transitionTable;
    }
    
    protected JBlock getCase(final State source) {
        if (this.$case != null) {
            return this.$case;
        }
        return this.$case = this.getSwitch()._case(JExpr.lit(this.parent.automaton.getStateNumber(source))).body();
    }
    
    protected boolean hasCase(final State source) {
        return this.$case != null;
    }
    
    protected String getNameOfMethodDecl() {
        return this.methodName;
    }
    
    protected final JSwitch getSwitch() {
        if (this.$switch != null) {
            return this.$switch;
        }
        final JMethod method = this.parent.unmarshaller.method(1, this.codeModel.VOID, this.getNameOfMethodDecl());
        method._throws((HandlerMethodGenerator.class$org$xml$sax$SAXException == null) ? (HandlerMethodGenerator.class$org$xml$sax$SAXException = class$("org.xml.sax.SAXException")) : HandlerMethodGenerator.class$org$xml$sax$SAXException);
        this.$attIdx = method.body().decl(this.codeModel.INT, "attIdx");
        this.outerLabel = method.body().label("outer");
        final JWhileLoop w = method.body()._while(JExpr.TRUE);
        this.$switch = this.makeSwitch(method, w.body());
        w.body()._break();
        return this.$switch;
    }
    
    protected JSwitch makeSwitch(final JMethod method, final JBlock parentBody) {
        return parentBody._switch(this.parent.$state);
    }
    
    private void onState(final State state, final TransitionTable table) {
        final TransitionTable.Entry[] row = table.list(state);
        boolean canFallThrough = true;
        TransitionTable.Entry catchAll = null;
        for (int i = 0; i < row.length && canFallThrough; ++i) {
            final Alphabet a = row[i].alphabet;
            if (this.alphabetType.isInstance(a)) {
                canFallThrough = this.performTransition(state, a, row[i].transition);
            }
            else if (a.isEnterAttribute()) {
                this.buildAttributeCheckClause(this.getCase(state), state, (Alphabet.EnterAttribute)a, row[i]);
            }
            else if (a.isDispatch() && this.alphabetType != ((HandlerMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute == null) ? (HandlerMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$EnterAttribute")) : HandlerMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute) && this.alphabetType != ((HandlerMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveAttribute == null) ? (HandlerMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveAttribute = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$LeaveAttribute")) : HandlerMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveAttribute)) {
                this.generateDispatch(this.getCase(state), a.asDispatch(), row[i]);
            }
            else if (a == Alphabet.EverythingElse.theInstance) {
                catchAll = row[i];
            }
        }
        if (canFallThrough && catchAll != null) {
            canFallThrough = this.performTransition(state, catchAll.alphabet, catchAll.transition);
        }
        if (canFallThrough) {
            if (state.getDelegatedState() != null) {
                this.generateGoto(this.getCase(state), state.getDelegatedState());
                this.getCase(state)._continue(this.outerLabel);
            }
            else if (this.hasCase(state)) {
                this.getCase(state)._break();
            }
        }
    }
    
    protected abstract boolean performTransition(final State p0, final Alphabet p1, final Transition p2);
    
    protected final void generate() {
        final Iterator itr = this.parent.automaton.states();
        while (itr.hasNext()) {
            this.$case = null;
            this.onState(itr.next(), this.table);
        }
    }
    
    protected abstract void addParametersToContextSwitch(final JInvocation p0);
    
    protected final String capitalize() {
        return Character.toUpperCase(this.methodName.charAt(0)) + this.methodName.substring(1);
    }
    
    protected final void generateRevertToParent(final JBlock $body) {
        if (this.trace) {
            $body.invoke(this.$tracer, "onRevertToParent");
        }
        final JInvocation inv = $body.invoke("revertToParentFrom" + this.capitalize());
        this.addParametersToContextSwitch(inv);
        $body._return();
    }
    
    protected void generateSpawnChildFromExternal(final JBlock $body, final Transition tr, final JExpression memento) {
        _assert(false);
    }
    
    protected final void generateSpawnChild(final JBlock $body, final Transition tr) {
        _assert(tr.alphabet instanceof Alphabet.Reference);
        final JExpression memento = JExpr.lit(this.parent.automaton.getStateNumber(tr.to));
        if (tr.alphabet instanceof Alphabet.External) {
            this.generateSpawnChildFromExternal($body, tr, memento);
        }
        else if (tr.alphabet instanceof Alphabet.Interleave) {
            final Alphabet.Interleave ia = (Alphabet.Interleave)tr.alphabet;
            final JInvocation $inv = $body.invoke("spawnHandlerFrom" + this.capitalize()).arg(JExpr._new(this.parent.getInterleaveDispatcher(ia))).arg(memento);
            this.addParametersToContextSwitch($inv);
            $body._return();
        }
        else if (!tr.alphabet.isDispatch()) {
            final Alphabet.StaticReference sr = (Alphabet.StaticReference)tr.alphabet;
            final JClass childType = sr.target.getOwner().implRef;
            if (tr.alphabet instanceof Alphabet.SuperClass) {
                if (this.trace) {
                    $body.invoke(this.$tracer, "onSpawnSuper").arg(JExpr.lit(childType.name()));
                    $body.invoke(this.$tracer, "suspend");
                }
                final JInvocation $inv2 = $body.invoke("spawnHandlerFrom" + this.capitalize()).arg(JExpr.direct(MessageFormat.format("(({0}){1}.this).new Unmarshaller(context)", childType.fullName(), this.parent.context.implClass.fullName()))).arg(memento);
                this.addParametersToContextSwitch($inv2);
                $body._return();
            }
            else {
                final Alphabet.Child c = (Alphabet.Child)tr.alphabet;
                if (this.trace) {
                    $body.invoke(this.$tracer, "onSpawnChild").arg(JExpr.lit(childType.name())).arg(JExpr.lit(c.field.getFieldUse().name));
                    $body.invoke(this.$tracer, "suspend");
                }
                final JInvocation $childObj = JExpr.invoke("spawnChildFrom" + this.capitalize()).arg(JExpr.dotclass(childType)).arg(memento);
                this.addParametersToContextSwitch($childObj);
                c.field.setter($body, JExpr.cast(childType, $childObj));
                $body._return();
            }
        }
    }
    
    protected final void generateGoto(final JBlock $body, final State target) {
        this.parent.generateGoto($body, target);
    }
    
    private void buildAttributeCheckClause(final JBlock body, final State current, final Alphabet.EnterAttribute alphabet, final TransitionTable.Entry tte) {
        final NameClass nc = alphabet.name;
        final JExpression $context = JExpr.ref("context");
        if (nc instanceof SimpleNameClass) {
            final SimpleNameClass snc = (SimpleNameClass)nc;
            body.assign(this.$attIdx, $context.invoke("getAttribute").arg(JExpr.lit(snc.namespaceURI)).arg(JExpr.lit(snc.localName)));
        }
        else {
            final JBlock b = body.block();
            final JVar $a = b.decl(this.codeModel.ref((HandlerMethodGenerator.class$org$xml$sax$Attributes == null) ? (HandlerMethodGenerator.class$org$xml$sax$Attributes = class$("org.xml.sax.Attributes")) : HandlerMethodGenerator.class$org$xml$sax$Attributes), "a", $context.invoke("getUnconsumedAttributes"));
            final JForLoop loop = b._for();
            loop.init(this.$attIdx, JExpr.invoke($a, "getLength").minus(JExpr.lit(1)));
            loop.test(this.$attIdx.gte(JExpr.lit(0)));
            loop.update(this.$attIdx.decr());
            final JType str = this.codeModel.ref((HandlerMethodGenerator.class$java$lang$String == null) ? (HandlerMethodGenerator.class$java$lang$String = class$("java.lang.String")) : HandlerMethodGenerator.class$java$lang$String);
            final JVar $uri = loop.body().decl(str, "uri", $a.invoke("getURI").arg(this.$attIdx));
            final JVar $local = loop.body().decl(str, "local", $a.invoke("getLocalName").arg(this.$attIdx));
            loop.body()._if(this.parent.parent.generateNameClassTest(nc, $uri, $local))._then()._break();
        }
        final JBlock _then = body._if(this.$attIdx.gte(JExpr.lit(0)))._then();
        final AttOptimizeInfo aoi = this.calcOptimizableAttribute(tte);
        if (aoi == null) {
            _then.invoke($context, "consumeAttribute").arg(this.$attIdx);
            this.addParametersToContextSwitch(_then.invoke($context.invoke("getCurrentHandler"), this.methodName));
            _then._return();
        }
        else {
            final JVar $v = _then.decl(8, this.codeModel.ref((HandlerMethodGenerator.class$java$lang$String == null) ? (HandlerMethodGenerator.class$java$lang$String = class$("java.lang.String")) : HandlerMethodGenerator.class$java$lang$String), "v", $context.invoke("eatAttribute").arg(this.$attIdx));
            this.parent.eatText(_then, aoi.valueHandler, (JExpression)$v);
            this.generateGoto(_then, aoi.nextState);
            if (aoi.nextState.isListState ^ current.isListState) {
                this.addParametersToContextSwitch(_then.invoke($context.invoke("getCurrentHandler"), this.methodName));
                _then._return();
            }
            else {
                _then._continue(this.outerLabel);
            }
        }
    }
    
    private void generateDispatch(final JBlock $body, final Alphabet.Dispatch da, final TransitionTable.Entry tte) {
        JBlock block = $body.block();
        final JVar $childType = block.decl(this.codeModel.ref((HandlerMethodGenerator.class$java$lang$Class == null) ? (HandlerMethodGenerator.class$java$lang$Class = class$("java.lang.Class")) : HandlerMethodGenerator.class$java$lang$Class), "child", this.parent.invokeLookup(da, tte));
        block = block._if($childType.ne(JExpr._null()))._then();
        if (this.trace) {
            block.invoke(this.$tracer, "onSpawnChild").arg(JExpr.lit('{' + da.attName.namespaceURI + '}' + da.attName.localName)).arg(JExpr.lit(da.field.getFieldUse().name));
            block.invoke(this.$tracer, "suspend");
        }
        final JInvocation $childObj = JExpr.invoke("spawnChildFrom" + this.capitalize()).arg($childType).arg(JExpr.lit(this.parent.automaton.getStateNumber(tte.transition.to)));
        this.addParametersToContextSwitch($childObj);
        da.field.setter(block, JExpr.cast(da.field.getFieldUse().type, $childObj));
        block._return();
    }
    
    private AttOptimizeInfo calcOptimizableAttribute(final TransitionTable.Entry tte) {
        if (!tte.transition.alphabet.isEnterAttribute()) {
            return null;
        }
        final Transition[] hop1 = tte.transition.to.listTransitions();
        if (hop1.length != 1) {
            return null;
        }
        final Transition t1 = hop1[0];
        if (!t1.alphabet.isBoundText()) {
            return null;
        }
        final Transition[] hop2 = t1.to.listTransitions();
        if (hop2.length != 1) {
            return null;
        }
        final Transition t2 = hop2[0];
        if (!t2.alphabet.isLeaveAttribute()) {
            return null;
        }
        return new AttOptimizeInfo(t1.alphabet.asBoundText(), t2.to);
    }
    
    protected static final void _assert(final boolean b) {
        if (!b) {
            throw new JAXBAssertionError();
        }
    }
    
    private static class AttOptimizeInfo
    {
        public final Alphabet.BoundText valueHandler;
        public final State nextState;
        
        AttOptimizeInfo(final Alphabet.BoundText _valueHandler, final State _nextState) {
            this.valueHandler = _valueHandler;
            this.nextState = _nextState;
        }
    }
}
