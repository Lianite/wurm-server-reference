// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.IOException;

public interface CodeAugmenter
{
    String getOptionName();
    
    String getUsage();
    
    int parseArgument(final Options p0, final String[] p1, final int p2) throws BadCommandLineException, IOException;
    
    boolean run(final AnnotatedGrammar p0, final GeneratorContext p1, final Options p2, final ErrorHandler p3);
}
