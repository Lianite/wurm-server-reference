// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.security;

import sun.awt.SunToolkit;
import sun.awt.AppContext;

public class AppContextUtil
{
    private static AppContext _mainAppContext;
    private static AppContext _securityAppContext;
    
    public static void createSecurityAppContext() {
        if (AppContextUtil._mainAppContext == null) {
            AppContextUtil._mainAppContext = AppContext.getAppContext();
        }
        if (AppContextUtil._securityAppContext == null) {
            SunToolkit.createNewAppContext();
            AppContextUtil._securityAppContext = AppContext.getAppContext();
        }
    }
    
    public static boolean isSecurityAppContext() {
        return AppContext.getAppContext() == AppContextUtil._securityAppContext;
    }
    
    public static boolean isApplicationAppContext() {
        return AppContext.getAppContext() == AppContextUtil._mainAppContext;
    }
    
    static {
        AppContextUtil._mainAppContext = null;
        AppContextUtil._securityAppContext = null;
    }
}
