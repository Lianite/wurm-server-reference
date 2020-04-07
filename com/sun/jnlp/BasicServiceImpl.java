// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.security.AccessController;
import com.sun.javaws.BrowserSupport;
import java.net.MalformedURLException;
import java.security.PrivilegedAction;
import java.net.URL;
import javax.jnlp.BasicService;

public final class BasicServiceImpl implements BasicService
{
    private URL _codebase;
    private boolean _isWebBrowserSupported;
    private boolean _isOffline;
    private static BasicServiceImpl _sharedInstance;
    
    private BasicServiceImpl(final URL codebase, final boolean isOffline, final boolean isWebBrowserSupported) {
        this._codebase = null;
        this._codebase = codebase;
        this._isWebBrowserSupported = isWebBrowserSupported;
        this._isOffline = isOffline;
    }
    
    public static BasicServiceImpl getInstance() {
        return BasicServiceImpl._sharedInstance;
    }
    
    public static void initialize(final URL url, final boolean b, final boolean b2) {
        if (BasicServiceImpl._sharedInstance == null) {
            BasicServiceImpl._sharedInstance = new BasicServiceImpl(url, b, b2);
        }
    }
    
    public URL getCodeBase() {
        return this._codebase;
    }
    
    public boolean isOffline() {
        return this._isOffline;
    }
    
    public boolean showDocument(final URL url) {
        if (!this.isWebBrowserSupported()) {
            return false;
        }
        final Boolean b = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                URL val$url = url;
                try {
                    val$url = new URL(BasicServiceImpl.this._codebase, url.toString());
                }
                catch (MalformedURLException ex) {}
                return new Boolean(BrowserSupport.showDocument(val$url));
            }
        });
        return b != null && b;
    }
    
    public boolean isWebBrowserSupported() {
        return this._isWebBrowserSupported;
    }
    
    static {
        BasicServiceImpl._sharedInstance = null;
    }
}
