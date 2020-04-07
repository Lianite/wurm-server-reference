// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.util;

import java.security.AccessController;
import com.sun.javaws.SplashScreen;
import java.security.PrivilegedAction;
import com.sun.deploy.util.DialogListener;

public final class JavawsDialogListener implements DialogListener
{
    public void beforeShow() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                SplashScreen.hide();
                return null;
            }
        });
    }
}
