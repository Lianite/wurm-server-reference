// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.security.PrivilegedActionException;
import java.awt.Frame;
import com.sun.javaws.LaunchErrorDialog;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Date;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javaws.Main;
import com.sun.deploy.config.Config;
import java.io.File;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.config.JREInfo;
import com.sun.javaws.LaunchSelection;
import java.net.URL;
import com.sun.javaws.ui.DownloadWindow;
import com.sun.javaws.LocalApplicationProperties;
import javax.jnlp.ExtensionInstallerService;

public final class ExtensionInstallerServiceImpl implements ExtensionInstallerService
{
    private LocalApplicationProperties _lap;
    private DownloadWindow _window;
    private String _target;
    private String _installPath;
    private boolean _failedJREInstall;
    static ExtensionInstallerServiceImpl _sharedInstance;
    
    private ExtensionInstallerServiceImpl(final String installPath, final LocalApplicationProperties lap, final DownloadWindow window) {
        this._failedJREInstall = false;
        this._lap = lap;
        this._window = window;
        this._installPath = installPath;
    }
    
    public static synchronized ExtensionInstallerServiceImpl getInstance() {
        return ExtensionInstallerServiceImpl._sharedInstance;
    }
    
    public static synchronized void initialize(final String s, final LocalApplicationProperties localApplicationProperties, final DownloadWindow downloadWindow) {
        if (ExtensionInstallerServiceImpl._sharedInstance == null) {
            ExtensionInstallerServiceImpl._sharedInstance = new ExtensionInstallerServiceImpl(s, localApplicationProperties, downloadWindow);
        }
    }
    
    public String getInstallPath() {
        return this._installPath;
    }
    
    public String getExtensionVersion() {
        return this._lap.getVersionId();
    }
    
    public URL getExtensionLocation() {
        return this._lap.getLocation();
    }
    
    public String getInstalledJRE(final URL url, final String s) {
        final JREInfo selectJRE = LaunchSelection.selectJRE(url, s);
        return (selectJRE != null) ? selectJRE.getPath() : null;
    }
    
    public void setHeading(final String status) {
        this._window.setStatus(status);
    }
    
    public void setStatus(final String progressText) {
        this._window.setProgressText(progressText);
    }
    
    public void updateProgress(final int progressBarValue) {
        this._window.setProgressBarValue(progressBarValue);
    }
    
    public void hideProgressBar() {
        this._window.setProgressBarVisible(false);
    }
    
    public void hideStatusWindow() {
        this._window.getFrame().setVisible(false);
    }
    
    public void setJREInfo(final String s, final String s2) {
        final int defaultSecurityModel = JNLPClassLoader.getInstance().getDefaultSecurityModel();
        if (defaultSecurityModel != 1 && defaultSecurityModel != 2) {
            throw new SecurityException("Unsigned extension installer attempting to call setJREInfo.");
        }
        Trace.println("setJREInfo: " + s2, TraceLevel.EXTENSIONS);
        if (s2 != null && new File(s2).exists()) {
            JREInfo.addJRE(new JREInfo(s, this.getExtensionVersion(), this.getExtensionLocation().toString(), s2, Config.getOSName(), Config.getOSArch(), true, false));
        }
        else {
            Trace.println("jre install failed: jrePath invalid", TraceLevel.EXTENSIONS);
            this._failedJREInstall = true;
        }
    }
    
    public void setNativeLibraryInfo(final String nativeLibDirectory) {
        Trace.println("setNativeLibInfo: " + nativeLibDirectory, TraceLevel.EXTENSIONS);
        this._lap.setNativeLibDirectory(nativeLibDirectory);
    }
    
    public void installFailed() {
        Trace.println("installFailed", TraceLevel.EXTENSIONS);
        Main.systemExit(1);
    }
    
    public void installSucceeded(final boolean b) {
        if (this._failedJREInstall) {
            return;
        }
        Trace.println("installSucceded", TraceLevel.EXTENSIONS);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                Config.store();
                return null;
            }
        });
        this._lap.setInstallDirectory(this._installPath);
        this._lap.setLastAccessed(new Date());
        if (b) {
            this._lap.setRebootNeeded(true);
        }
        else {
            this._lap.setLocallyInstalled(true);
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    ExtensionInstallerServiceImpl.this._lap.store();
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            if (ex.getException() instanceof IOException) {
                LaunchErrorDialog.show(this._window.getFrame(), ex.getException(), false);
            }
            else {
                Trace.ignoredException(ex.getException());
            }
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                Main.systemExit(0);
                return null;
            }
        });
    }
    
    static {
        ExtensionInstallerServiceImpl._sharedInstance = null;
    }
}
