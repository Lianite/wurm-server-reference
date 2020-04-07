// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.addon.code_injector;

import com.sun.tools.xjc.model.CPluginCustomization;
import java.util.Iterator;
import com.sun.tools.xjc.util.DOMUtils;
import com.sun.tools.xjc.outline.ClassOutline;
import org.xml.sax.ErrorHandler;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.Outline;
import java.util.Collections;
import java.util.List;
import com.sun.tools.xjc.Plugin;

public class PluginImpl extends Plugin
{
    public String getOptionName() {
        return "Xinject-code";
    }
    
    public List<String> getCustomizationURIs() {
        return Collections.singletonList("http://jaxb.dev.java.net/plugin/code-injector");
    }
    
    public boolean isCustomizationTagName(final String nsUri, final String localName) {
        return nsUri.equals("http://jaxb.dev.java.net/plugin/code-injector") && localName.equals("code");
    }
    
    public String getUsage() {
        return "  -Xinject-code      :  inject specified Java code fragments into the generated code";
    }
    
    public boolean run(final Outline model, final Options opt, final ErrorHandler errorHandler) {
        for (final ClassOutline co : model.getClasses()) {
            final CPluginCustomization c = co.target.getCustomizations().find("http://jaxb.dev.java.net/plugin/code-injector", "code");
            if (c == null) {
                continue;
            }
            c.markAsAcknowledged();
            final String codeFragment = DOMUtils.getElementText(c.element);
            co.implClass.direct(codeFragment);
        }
        return true;
    }
}
