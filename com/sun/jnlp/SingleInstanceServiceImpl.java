// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.net.URL;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.util.Trace;
import com.sun.javaws.jnl.XMLFormat;
import com.sun.deploy.si.DeploySIListener;
import java.security.AccessController;
import com.sun.javaws.Main;
import com.sun.javaws.Globals;
import com.sun.deploy.si.SingleInstanceManager;
import com.sun.javaws.jnl.LaunchDesc;
import java.security.PrivilegedAction;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import com.sun.deploy.si.SingleInstanceImpl;

public final class SingleInstanceServiceImpl extends SingleInstanceImpl implements SingleInstanceService
{
    private static SingleInstanceServiceImpl _sharedInstance;
    
    public static synchronized SingleInstanceServiceImpl getInstance() {
        if (SingleInstanceServiceImpl._sharedInstance == null) {
            SingleInstanceServiceImpl._sharedInstance = new SingleInstanceServiceImpl();
        }
        return SingleInstanceServiceImpl._sharedInstance;
    }
    
    public void addSingleInstanceListener(final SingleInstanceListener singleInstanceListener) {
        if (singleInstanceListener == null) {
            return;
        }
        final LaunchDesc launchDesc = JNLPClassLoader.getInstance().getLaunchDesc();
        final String string = launchDesc.getCanonicalHome().toString();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                if (SingleInstanceManager.isServerRunning(string)) {
                    final String[] applicationArgs = Globals.getApplicationArgs();
                    if (applicationArgs != null) {
                        launchDesc.getApplicationDescriptor().setArguments(applicationArgs);
                    }
                    if (SingleInstanceManager.connectToServer(launchDesc.toString())) {
                        Main.systemExit(0);
                    }
                }
                return null;
            }
        });
        super.addSingleInstanceListener((DeploySIListener)new TransferListener(singleInstanceListener), string);
    }
    
    public void removeSingleInstanceListener(final SingleInstanceListener singleInstanceListener) {
        super.removeSingleInstanceListener((DeploySIListener)new TransferListener(singleInstanceListener));
    }
    
    public boolean isSame(final String s, final String s2) {
        LaunchDesc parse = null;
        try {
            parse = XMLFormat.parse(s.getBytes());
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
        }
        if (parse != null) {
            final URL canonicalHome = parse.getCanonicalHome();
            Trace.println("GOT: " + canonicalHome.toString(), TraceLevel.BASIC);
            if (s2.equals(canonicalHome.toString())) {
                return true;
            }
        }
        return false;
    }
    
    public String[] getArguments(final String s, final String s2) {
        LaunchDesc parse = null;
        try {
            parse = XMLFormat.parse(s.getBytes());
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
        }
        if (parse != null) {
            return parse.getApplicationDescriptor().getArguments();
        }
        return new String[0];
    }
    
    static {
        SingleInstanceServiceImpl._sharedInstance = null;
    }
    
    private class TransferListener implements DeploySIListener
    {
        SingleInstanceListener _sil;
        
        public TransferListener(final SingleInstanceListener sil) {
            this._sil = sil;
        }
        
        public void newActivation(final String[] array) {
            this._sil.newActivation(array);
        }
        
        public Object getSingleInstanceListener() {
            return this._sil;
        }
    }
}
