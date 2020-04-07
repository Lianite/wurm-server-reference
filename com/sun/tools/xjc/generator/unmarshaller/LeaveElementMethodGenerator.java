// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;

class LeaveElementMethodGenerator extends EnterLeaveMethodGenerator
{
    static /* synthetic */ Class class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveElement;
    
    LeaveElementMethodGenerator(final PerClassGenerator parent) {
        super(parent, "leaveElement", (LeaveElementMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveElement == null) ? (LeaveElementMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveElement = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$LeaveElement")) : LeaveElementMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$LeaveElement);
    }
    
    protected void generateAction(final Alphabet alpha, final Transition tr, final JBlock body) {
        if (tr.alphabet == alpha) {
            body.invoke(this.parent.$context, "popAttributes");
        }
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
