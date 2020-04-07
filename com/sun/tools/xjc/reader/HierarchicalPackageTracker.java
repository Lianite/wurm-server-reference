// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import java.util.HashMap;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.codemodel.JPackage;
import java.util.Map;

public final class HierarchicalPackageTracker implements PackageTracker
{
    private final Map dic;
    private JPackage pkg;
    private final ExpressionWalker visitor;
    
    public HierarchicalPackageTracker() {
        this.dic = new HashMap();
        this.visitor = (ExpressionWalker)new HierarchicalPackageTracker$1(this);
    }
    
    public final JPackage get(final ReferenceExp exp) {
        return this.dic.get(exp);
    }
    
    public final void associate(final Expression exp, final JPackage _pkg) {
        this.pkg = _pkg;
        exp.visit((ExpressionVisitorVoid)this.visitor);
    }
}
