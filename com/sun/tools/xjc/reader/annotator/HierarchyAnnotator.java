// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.tools.xjc.util.Util;
import com.sun.tools.xjc.grammar.InterfaceItem;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.grammar.BGMWalker;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.PrintStream;

public class HierarchyAnnotator
{
    private static PrintStream debug;
    
    public static void annotate(final AnnotatedGrammar grammar, final AnnotatorController controller) {
        final BGMWalker annotator = (BGMWalker)new HierarchyAnnotator$1(controller);
        final ClassItem[] cs = grammar.getClasses();
        for (int i = 0; i < cs.length; ++i) {
            cs[i].visit((ExpressionVisitorVoid)annotator);
        }
        final InterfaceItem[] is = grammar.getInterfaces();
        for (int j = 0; j < is.length; ++j) {
            is[j].visit((ExpressionVisitorVoid)annotator);
        }
    }
    
    static {
        HierarchyAnnotator.debug = ((Util.getSystemProperty((HierarchyAnnotator.class$com$sun$tools$xjc$reader$annotator$HierarchyAnnotator == null) ? (HierarchyAnnotator.class$com$sun$tools$xjc$reader$annotator$HierarchyAnnotator = class$("com.sun.tools.xjc.reader.annotator.HierarchyAnnotator")) : HierarchyAnnotator.class$com$sun$tools$xjc$reader$annotator$HierarchyAnnotator, "debug") != null) ? System.out : null);
    }
}
