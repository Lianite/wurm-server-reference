// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.unmarshaller;

import com.sun.tools.xjc.generator.XmlNameStoreAlgorithm;
import com.sun.codemodel.JBlock;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Transition;
import com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet;

class EnterAttributeMethodGenerator extends EnterLeaveMethodGenerator
{
    static /* synthetic */ Class class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute;
    
    public EnterAttributeMethodGenerator(final PerClassGenerator parent) {
        super(parent, "enterAttribute", (EnterAttributeMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute == null) ? (EnterAttributeMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute = class$("com.sun.tools.xjc.generator.unmarshaller.automaton.Alphabet$EnterAttribute")) : EnterAttributeMethodGenerator.class$com$sun$tools$xjc$generator$unmarshaller$automaton$Alphabet$EnterAttribute);
    }
    
    protected void generateAction(final Alphabet alpha, final Transition tr, final JBlock $body) {
        if (tr.alphabet == alpha) {
            final Alphabet.EnterAttribute ea = (Alphabet.EnterAttribute)alpha;
            XmlNameStoreAlgorithm.get(ea.name).onNameUnmarshalled(this.codeModel, $body, this.$uri, this.$local);
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
