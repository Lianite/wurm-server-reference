// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import org.xml.sax.SAXException;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.model.Model;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

public abstract class Plugin
{
    public abstract String getOptionName();
    
    public abstract String getUsage();
    
    public int parseArgument(final Options opt, final String[] args, final int i) throws BadCommandLineException, IOException {
        return 0;
    }
    
    public List<String> getCustomizationURIs() {
        return Collections.emptyList();
    }
    
    public boolean isCustomizationTagName(final String nsUri, final String localName) {
        return false;
    }
    
    public void onActivated(final Options opts) throws BadCommandLineException {
    }
    
    public void postProcessModel(final Model model, final ErrorHandler errorHandler) {
    }
    
    public abstract boolean run(final Outline p0, final Options p1, final ErrorHandler p2) throws SAXException;
}
