// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.util;

import com.sun.deploy.config.Config;
import javax.swing.SwingUtilities;
import com.sun.deploy.resources.ResourceManager;
import com.sun.deploy.net.proxy.DynamicProxyManager;
import java.util.logging.Logger;
import com.sun.javaws.Globals;
import com.sun.deploy.util.ConsoleWindow;
import com.sun.deploy.util.ConsoleController;

public class JavawsConsoleController implements ConsoleController
{
    private static ConsoleWindow console;
    private static JavawsConsoleController jcc;
    
    public static JavawsConsoleController getInstance() {
        if (JavawsConsoleController.jcc == null) {
            if (Globals.isJavaVersionAtLeast14()) {
                JavawsConsoleController.jcc = new JavawsConsoleController14();
            }
            else {
                JavawsConsoleController.jcc = new JavawsConsoleController();
            }
        }
        return JavawsConsoleController.jcc;
    }
    
    public void setLogger(final Logger logger) {
    }
    
    public void setConsole(final ConsoleWindow console) {
        if (JavawsConsoleController.console == null) {
            JavawsConsoleController.console = console;
        }
    }
    
    public ConsoleWindow getConsole() {
        return JavawsConsoleController.console;
    }
    
    public boolean isIconifiedOnClose() {
        return false;
    }
    
    public boolean isDoubleBuffered() {
        return true;
    }
    
    public boolean isDumpStackSupported() {
        return false;
    }
    
    public String dumpAllStacks() {
        return null;
    }
    
    public ThreadGroup getMainThreadGroup() {
        return Thread.currentThread().getThreadGroup();
    }
    
    public boolean isSecurityPolicyReloadSupported() {
        return false;
    }
    
    public void reloadSecurityPolicy() {
    }
    
    public boolean isProxyConfigReloadSupported() {
        return true;
    }
    
    public void reloadProxyConfig() {
        DynamicProxyManager.reset();
    }
    
    public boolean isDumpClassLoaderSupported() {
        return false;
    }
    
    public String dumpClassLoaders() {
        return null;
    }
    
    public boolean isClearClassLoaderSupported() {
        return false;
    }
    
    public void clearClassLoaders() {
    }
    
    public boolean isLoggingSupported() {
        return false;
    }
    
    public boolean toggleLogging() {
        return false;
    }
    
    public boolean isJCovSupported() {
        return false;
    }
    
    public boolean dumpJCovData() {
        return false;
    }
    
    public String getProductName() {
        return ResourceManager.getString("product.javaws.name", "1.5.0_04");
    }
    
    public void invokeLater(final Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
    
    public static void showConsoleIfEnable() {
        if (Config.getProperty("deployment.console.startup.mode").equals("SHOW")) {
            JavawsConsoleController.console.showConsole(true);
        }
    }
    
    static {
        JavawsConsoleController.console = null;
        JavawsConsoleController.jcc = null;
    }
}
