// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.net.URL;

public abstract class BrowserSupport
{
    private static BrowserSupport _browserSupportImplementation;
    
    public static synchronized BrowserSupport getInstance() {
        if (BrowserSupport._browserSupportImplementation == null) {
            BrowserSupport._browserSupportImplementation = BrowserSupportFactory.newInstance();
        }
        return BrowserSupport._browserSupportImplementation;
    }
    
    public static boolean isWebBrowserSupported() {
        return getInstance().isWebBrowserSupportedImpl();
    }
    
    public static boolean showDocument(final URL url) {
        return getInstance().showDocumentImpl(url);
    }
    
    public abstract boolean isWebBrowserSupportedImpl();
    
    public abstract boolean showDocumentImpl(final URL p0);
    
    public abstract String getNS6MailCapInfo();
    
    public abstract OperaSupport getOperaSupport();
    
    static {
        BrowserSupport._browserSupportImplementation = null;
    }
}
