// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.util;

import java.util.logging.Level;
import java.security.Policy;
import java.util.logging.Logger;

public class JavawsConsoleController14 extends JavawsConsoleController
{
    private static Logger logger;
    
    public void setLogger(final Logger logger) {
        if (JavawsConsoleController14.logger == null) {
            JavawsConsoleController14.logger = logger;
        }
    }
    
    public Logger getLogger() {
        return JavawsConsoleController14.logger;
    }
    
    public boolean isSecurityPolicyReloadSupported() {
        return true;
    }
    
    public void reloadSecurityPolicy() {
        Policy.getPolicy().refresh();
    }
    
    public boolean isLoggingSupported() {
        return true;
    }
    
    public boolean toggleLogging() {
        if (JavawsConsoleController14.logger != null) {
            Level level;
            if (JavawsConsoleController14.logger.getLevel() == Level.OFF) {
                level = Level.ALL;
            }
            else {
                level = Level.OFF;
            }
            JavawsConsoleController14.logger.setLevel(level);
            return level == Level.ALL;
        }
        return false;
    }
    
    static {
        JavawsConsoleController14.logger = null;
    }
}
