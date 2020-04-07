// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.awt.AWTPermission;
import java.io.FilePermission;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;

public final class CheckServicePermission
{
    private static boolean checkPermission(final Permission permission) {
        try {
            AccessController.checkPermission(permission);
            return true;
        }
        catch (AccessControlException ex) {
            return false;
        }
    }
    
    static boolean hasFileAccessPermissions() {
        return checkPermission(new FilePermission("*", "read,write"));
    }
    
    static boolean hasPrintAccessPermissions() {
        return checkPermission(new RuntimePermission("queuePrintJob"));
    }
    
    static boolean hasClipboardPermissions() {
        return checkPermission(new AWTPermission("accessClipboard"));
    }
}
