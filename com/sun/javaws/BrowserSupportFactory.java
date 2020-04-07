// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

public class BrowserSupportFactory
{
    public static BrowserSupport newInstance() {
        return new WinBrowserSupport();
    }
}
