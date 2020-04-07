// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.addon.elementwrapper;

import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.generator.GeneratorContext;
import com.sun.tools.xjc.grammar.AnnotatedGrammar;
import java.io.IOException;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.CodeAugmenter;

public class ElementWrapperAddOn implements CodeAugmenter
{
    static /* synthetic */ Class class$com$sun$tools$xjc$runtime$ElementWrapper;
    
    public String getOptionName() {
        return "Xelement-wrapper";
    }
    
    public String getUsage() {
        return "  -Xelement-wrapper  :  generates the general purpose element wrapper into impl.runtime";
    }
    
    public int parseArgument(final Options opt, final String[] args, final int i) throws BadCommandLineException, IOException {
        return 0;
    }
    
    public boolean run(final AnnotatedGrammar grammar, final GeneratorContext context, final Options opt, final ErrorHandler errorHandler) {
        context.getRuntime((ElementWrapperAddOn.class$com$sun$tools$xjc$runtime$ElementWrapper == null) ? (ElementWrapperAddOn.class$com$sun$tools$xjc$runtime$ElementWrapper = class$("com.sun.tools.xjc.runtime.ElementWrapper")) : ElementWrapperAddOn.class$com$sun$tools$xjc$runtime$ElementWrapper);
        return true;
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
