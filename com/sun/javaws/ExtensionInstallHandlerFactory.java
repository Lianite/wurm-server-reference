// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

public class ExtensionInstallHandlerFactory
{
    public static ExtensionInstallHandler newInstance() {
        return new WinExtensionInstallHandler();
    }
}
