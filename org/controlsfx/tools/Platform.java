// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.tools;

import java.security.AccessController;
import java.security.PrivilegedAction;

public enum Platform
{
    WINDOWS("windows"), 
    OSX("mac"), 
    UNIX("unix"), 
    UNKNOWN("");
    
    private static Platform current;
    private String platformId;
    
    private Platform(final String platformId) {
        this.platformId = platformId;
    }
    
    public String getPlatformId() {
        return this.platformId;
    }
    
    public static Platform getCurrent() {
        return Platform.current;
    }
    
    private static Platform getCurrentPlatform() {
        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return Platform.WINDOWS;
        }
        if (osName.startsWith("Mac")) {
            return Platform.OSX;
        }
        if (osName.startsWith("SunOS")) {
            return Platform.UNIX;
        }
        if (osName.startsWith("Linux")) {
            final String javafxPlatform = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("javafx.platform");
                }
            });
            if (!"android".equals(javafxPlatform) && !"Dalvik".equals(System.getProperty("java.vm.name"))) {
                return Platform.UNIX;
            }
        }
        return Platform.UNKNOWN;
    }
    
    static {
        Platform.current = getCurrentPlatform();
    }
}
