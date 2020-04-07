// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import java.util.Iterator;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.tools.xjc.reader.TypeUtil;
import java.util.Set;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.grammar.id.SymbolSpace;
import java.util.Map;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import java.util.HashMap;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;

public class SymbolSpaceTypeAssigner
{
    public static void assign(final AnnotatedGrammar grammar, final AnnotatorController controller) {
        final Map applicableTypes = new HashMap();
        final ClassItem[] classes = grammar.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            final ClassItem ci = classes[i];
            ci.exp.visit((ExpressionVisitorVoid)new SymbolSpaceTypeAssigner$1(applicableTypes, ci));
        }
        for (final Map.Entry e : applicableTypes.entrySet()) {
            e.getKey().setType(TypeUtil.getCommonBaseType(grammar.codeModel, (JType[])e.getValue().toArray(new JType[0])));
        }
    }
}
