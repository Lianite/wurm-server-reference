// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.deploy.util.WinRegistry;
import java.awt.Component;
import com.sun.deploy.util.DialogFactory;
import com.sun.deploy.resources.ResourceManager;
import java.awt.Window;

public class WinExtensionInstallHandler extends ExtensionInstallHandler
{
    private static final String KEY_RUNONCE = "Software\\Microsoft\\Windows\\CurrentVersion\\RunOnce";
    
    public boolean doPreRebootActions(final Window window) {
        final int[] array = { 1 };
        window.setVisible(true);
        window.requestFocus();
        array[0] = DialogFactory.showConfirmDialog((Component)window, (Object)ResourceManager.getString("extensionInstall.rebootMessage"), ResourceManager.getString("extensionInstall.rebootTitle"));
        window.setVisible(false);
        return array[0] == 0;
    }
    
    public boolean doReboot() {
        return WinRegistry.doReboot();
    }
    
    static {
        NativeLibrary.getInstance().load();
    }
}
