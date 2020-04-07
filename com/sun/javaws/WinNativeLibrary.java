// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.io.File;
import com.sun.deploy.config.Config;

public class WinNativeLibrary extends NativeLibrary
{
    private static boolean isLoaded;
    
    public synchronized void load() {
        if (!WinNativeLibrary.isLoaded) {
            System.load(Config.getJavaHome() + File.separator + "bin" + File.separator + "deploy.dll");
            WinNativeLibrary.isLoaded = true;
        }
    }
    
    static {
        WinNativeLibrary.isLoaded = false;
    }
}
