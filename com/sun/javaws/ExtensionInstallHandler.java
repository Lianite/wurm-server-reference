// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.awt.Window;

public abstract class ExtensionInstallHandler
{
    private static ExtensionInstallHandler _installHandler;
    
    public static synchronized ExtensionInstallHandler getInstance() {
        if (ExtensionInstallHandler._installHandler == null) {
            ExtensionInstallHandler._installHandler = ExtensionInstallHandlerFactory.newInstance();
        }
        return ExtensionInstallHandler._installHandler;
    }
    
    public abstract boolean doPreRebootActions(final Window p0);
    
    public abstract boolean doReboot();
}
