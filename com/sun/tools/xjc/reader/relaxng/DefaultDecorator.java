// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import com.sun.msv.util.StartTagInfo;
import com.sun.codemodel.JClassContainer;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.AttributeExp;
import com.sun.tools.xjc.grammar.ClassCandidateItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.msv.reader.GrammarReader;
import com.sun.tools.xjc.reader.NameConverter;
import com.sun.tools.xjc.reader.decorator.DecoratorImpl;

class DefaultDecorator extends DecoratorImpl
{
    DefaultDecorator(final TRELAXNGReader reader, final NameConverter nc) {
        super((GrammarReader)reader, reader.annGrammar, nc);
    }
    
    private CodeModelClassFactory getClassFactory() {
        return ((TRELAXNGReader)this.reader).classFactory;
    }
    
    public Expression decorate(final State state, final Expression exp) {
        final StartTagInfo tag = state.getStartTag();
        final TRELAXNGReader reader = (TRELAXNGReader)this.reader;
        if (tag.localName.equals("define") && (exp == Expression.nullSet || exp == Expression.epsilon) && state.getStartTag().containsAttribute("combine")) {
            return exp;
        }
        if ((tag.localName.equals("element") || tag.localName.equals("define")) && !(exp instanceof ClassItem) && !(exp instanceof ClassCandidateItem)) {
            final String baseName = this.decideName(state, exp, "class", "", state.getLocation());
            return (Expression)new ClassCandidateItem(this.getClassFactory(), this.grammar, reader.packageManager.getCurrentPackage(), baseName, state.getLocation(), exp);
        }
        if (exp instanceof AttributeExp && !(((AttributeExp)exp).nameClass instanceof SimpleNameClass)) {
            return (Expression)this.grammar.createClassItem(this.getClassFactory().createInterface(reader.packageManager.getCurrentPackage(), this.decideName(state, exp, "class", "Attr", state.getLocation()), state.getLocation()), exp, state.getLocation());
        }
        return exp;
    }
}
