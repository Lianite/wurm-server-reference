// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.msv.grammar.NameClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.generator.unmarshaller.automaton.State;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import com.sun.codemodel.JVar;

class EnterLeaveMethodGenerator extends HandlerMethodGenerator
{
    protected JVar $uri;
    protected JVar $local;
    protected JVar $qname;
    
    EnterLeaveMethodGenerator(final PerClassGenerator parent, final String _methodName, final Class _alphabetType) {
        super(parent, _methodName, _alphabetType);
    }
    
    protected void generateAction(final Alphabet alpha, final Transition tr, final JBlock $body) {
    }
    
    protected boolean performTransition(final State state, final Alphabet alphabet, final Transition action) {
        JBlock $body = this.getCase(state);
        if (alphabet.isNamed()) {
            $body = $body._if(this.generateNameClassTest(alphabet.asNamed().name))._then();
        }
        this.generateAction(alphabet, action, $body);
        if (action == Transition.REVERT_TO_PARENT) {
            this.generateRevertToParent($body);
        }
        else if (action.alphabet instanceof Alphabet.Reference) {
            this.generateSpawnChild($body, action);
        }
        else {
            this.generateGoto($body, action.to);
            $body._return();
        }
        return alphabet.isNamed();
    }
    
    protected JSwitch makeSwitch(final JMethod method, final JBlock body) {
        this.declareParameters(method);
        if (this.trace) {
            body.invoke(this.$tracer, "on" + this.capitalize()).arg(this.$uri).arg(this.$local);
        }
        final JSwitch s = super.makeSwitch(method, body);
        this.addParametersToContextSwitch(body.invoke(JExpr.ref("super"), method));
        return s;
    }
    
    protected void declareParameters(final JMethod method) {
        this.$uri = method.param((EnterLeaveMethodGenerator.class$java$lang$String == null) ? (EnterLeaveMethodGenerator.class$java$lang$String = class$("java.lang.String")) : EnterLeaveMethodGenerator.class$java$lang$String, "___uri");
        this.$local = method.param((EnterLeaveMethodGenerator.class$java$lang$String == null) ? (EnterLeaveMethodGenerator.class$java$lang$String = class$("java.lang.String")) : EnterLeaveMethodGenerator.class$java$lang$String, "___local");
        this.$qname = method.param((EnterLeaveMethodGenerator.class$java$lang$String == null) ? (EnterLeaveMethodGenerator.class$java$lang$String = class$("java.lang.String")) : EnterLeaveMethodGenerator.class$java$lang$String, "___qname");
    }
    
    protected void addParametersToContextSwitch(final JInvocation inv) {
        inv.arg(this.$uri).arg(this.$local).arg(this.$qname);
    }
    
    private JExpression generateNameClassTest(final NameClass nc) {
        this.getSwitch();
        return this.parent.parent.generateNameClassTest(nc, this.$uri, this.$local);
    }
}
