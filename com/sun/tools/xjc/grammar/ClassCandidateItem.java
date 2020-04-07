// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.grammar;

import com.sun.codemodel.JClassContainer;
import com.sun.msv.grammar.Expression;
import org.xml.sax.Locator;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.util.CodeModelClassFactory;
import com.sun.msv.grammar.OtherExp;

public class ClassCandidateItem extends OtherExp
{
    public final String name;
    private final CodeModelClassFactory classFactory;
    private final AnnotatedGrammar grammar;
    public final JPackage targetPackage;
    public final Locator locator;
    private ClassItem ci;
    
    public ClassCandidateItem(final CodeModelClassFactory _classFactory, final AnnotatedGrammar _grammar, final JPackage _targetPackage, final String _name, final Locator _loc, final Expression body) {
        super(body);
        this.ci = null;
        this.grammar = _grammar;
        this.classFactory = _classFactory;
        this.targetPackage = _targetPackage;
        this.name = _name;
        this.locator = _loc;
    }
    
    public ClassItem toClassItem() {
        if (this.ci == null) {
            this.ci = this.grammar.createClassItem(this.classFactory.createInterface(this.targetPackage, this.name, this.locator), this.exp, this.locator);
        }
        return this.ci;
    }
    
    public String printName() {
        return super.printName() + "#" + this.name;
    }
}
