// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.annotator;

import com.sun.tools.xjc.util.Util;
import com.sun.tools.xjc.grammar.ClassItem;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.tools.xjc.grammar.util.NotAllowedRemover;
import com.sun.msv.grammar.Grammar;
import com.sun.tools.xjc.writer.Writer;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.PrintStream;

public class Annotator
{
    private static PrintStream debug;
    static /* synthetic */ Class class$com$sun$tools$xjc$reader$annotator$Annotator;
    
    public static void annotate(final AnnotatedGrammar grammar, final AnnotatorController controller) {
        if (Annotator.debug != null) {
            Annotator.debug.println("---------------------------------------------");
            Annotator.debug.println("initial grammar");
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("removing notAllowed");
        }
        final NotAllowedRemover visitor = new NotAllowedRemover(grammar.getPool());
        grammar.visit((ExpressionVisitorExpression)visitor);
        if (grammar.exp == Expression.nullSet) {
            return;
        }
        ClassItem[] classes = grammar.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            classes[i].exp = classes[i].exp.visit((ExpressionVisitorExpression)visitor);
        }
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("removing empty JavaItems");
        }
        final EmptyJavaItemRemover visitor2 = new EmptyJavaItemRemover(grammar.getPool());
        grammar.visit((ExpressionVisitorExpression)visitor2);
        if (grammar.exp == Expression.nullSet) {
            return;
        }
        classes = grammar.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            classes[i].exp = classes[i].exp.visit((ExpressionVisitorExpression)visitor2);
        }
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("removing mixed");
        }
        final MixedRemover visitor3 = new MixedRemover(grammar);
        grammar.visit((ExpressionVisitorExpression)visitor3);
        if (grammar.exp == Expression.nullSet) {
            return;
        }
        classes = grammar.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            classes[i].exp = classes[i].exp.visit((ExpressionVisitorExpression)visitor3);
        }
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("simplifying datatypes");
        }
        grammar.visit((ExpressionVisitorExpression)new DatatypeSimplifier(grammar.getPool()));
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("examining primitive types");
        }
        final PrimitiveTypeAnnotator visitor4 = new PrimitiveTypeAnnotator(grammar, controller);
        grammar.visit((ExpressionVisitorExpression)visitor4);
        if (grammar.exp == Expression.nullSet) {
            return;
        }
        classes = grammar.getClasses();
        for (int i = 0; i < classes.length; ++i) {
            classes[i].exp = classes[i].exp.visit((ExpressionVisitorExpression)visitor4);
        }
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("annotating complex choices");
        }
        ChoiceAnnotator.annotate(grammar, controller);
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("removing temporary class items");
        }
        TemporaryClassItemRemover.remove(grammar);
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("adding field items");
        }
        FieldItemAnnotation.annotate(grammar, controller);
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("computing type hierarchy");
        }
        HierarchyAnnotator.annotate(grammar, controller);
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("determining types for symbol spaces");
        }
        SymbolSpaceTypeAssigner.assign(grammar, controller);
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
        }
        if (Annotator.debug != null) {
            Annotator.debug.println("normalizing relations");
        }
        RelationNormalizer.normalize(grammar, controller);
        if (Annotator.debug != null) {
            Writer.writeToConsole(true, (Grammar)grammar);
            Annotator.debug.println("---------------------------------------------");
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
    
    static {
        Annotator.debug = ((Util.getSystemProperty((Annotator.class$com$sun$tools$xjc$reader$annotator$Annotator == null) ? (Annotator.class$com$sun$tools$xjc$reader$annotator$Annotator = class$("com.sun.tools.xjc.reader.annotator.Annotator")) : Annotator.class$com$sun$tools$xjc$reader$annotator$Annotator, "debug") != null) ? System.out : null);
    }
}
