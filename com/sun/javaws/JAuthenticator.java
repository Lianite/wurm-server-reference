// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.deploy.config.Config;
import java.net.PasswordAuthentication;
import java.awt.Frame;
import com.sun.deploy.security.DeployAuthenticator;

public class JAuthenticator extends DeployAuthenticator
{
    private static JAuthenticator _instance;
    private boolean _challanging;
    private boolean _cancel;
    
    private JAuthenticator() {
        this._challanging = false;
        this._cancel = false;
    }
    
    public static synchronized JAuthenticator getInstance(final Frame parentFrame) {
        if (JAuthenticator._instance == null) {
            JAuthenticator._instance = new JAuthenticator();
        }
        JAuthenticator._instance.setParentFrame(parentFrame);
        return JAuthenticator._instance;
    }
    
    protected synchronized PasswordAuthentication getPasswordAuthentication() {
        PasswordAuthentication passwordAuthentication = null;
        if (Config.getBooleanProperty("deployment.security.authenticator")) {
            this._challanging = true;
            passwordAuthentication = super.getPasswordAuthentication();
            this._challanging = false;
        }
        return passwordAuthentication;
    }
    
    boolean isChallanging() {
        return this._challanging;
    }
}
