// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;

public class text_html extends text_plain
{
    private static ActivationDataFlavor myDF;
    static /* synthetic */ Class class$java$lang$String;
    
    protected ActivationDataFlavor getDF() {
        return text_html.myDF;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        text_html.myDF = new ActivationDataFlavor((text_html.class$java$lang$String == null) ? (text_html.class$java$lang$String = class$("java.lang.String")) : text_html.class$java$lang$String, "text/html", "HTML String");
    }
}
