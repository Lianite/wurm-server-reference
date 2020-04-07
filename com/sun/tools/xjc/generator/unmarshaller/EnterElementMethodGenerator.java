// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.XmlNameStoreAlgorithm;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;
import com.sun.codemodel.JVar;

class EnterElementMethodGenerator extends EnterLeaveMethodGenerator
{
    private JVar $atts;
    static /* synthetic */ Class class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterElement;
    static /* synthetic */ Class class$org$xml$sax$Attributes;
    
    EnterElementMethodGenerator(final PerClassGenerator parent) {
        super(parent, "enterElement", (EnterElementMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterElement == null) ? (EnterElementMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterElement = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$EnterElement")) : EnterElementMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterElement);
    }
    
    protected void generateAction(final Alphabet alpha, final Transition tr, final JBlock body) {
        if (tr.alphabet == alpha) {
            final Alphabet.EnterElement ee = (Alphabet.EnterElement)alpha;
            XmlNameStoreAlgorithm.get(ee.name).onNameUnmarshalled(this.codeModel, body, this.$uri, this.$local);
            body.invoke(this.parent.$context, "pushAttributes").arg(this.$atts).arg(ee.isDataElement ? JExpr.TRUE : JExpr.FALSE);
        }
    }
    
    protected void declareParameters(final JMethod method) {
        super.declareParameters(method);
        this.$atts = method.param((EnterElementMethodGenerator.class$org$xml$sax$Attributes == null) ? (EnterElementMethodGenerator.class$org$xml$sax$Attributes = class$("org.xml.sax.Attributes")) : EnterElementMethodGenerator.class$org$xml$sax$Attributes, "__atts");
    }
    
    protected void addParametersToContextSwitch(final JInvocation inv) {
        super.addParametersToContextSwitch(inv);
        inv.arg(this.$atts);
    }
    
    protected void generateSpawnChildFromExternal(final JBlock $body, final Transition tr, final JExpression memento) {
        if (this.trace) {
            $body.invoke(this.$tracer, "onSpawnWildcard");
            $body.invoke(this.$tracer, "suspend");
        }
        final Alphabet.External ae = (Alphabet.External)tr.alphabet;
        final JExpression co = ae.owner.generateUnmarshaller(this.parent.parent.context, (JExpression)this.parent.$context, $body, memento, this.$uri, this.$local, this.$qname, this.$atts);
        final JBlock then = $body._if(co.ne(JExpr._null()))._then();
        ae.field.setter(then, co);
        $body._return();
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
