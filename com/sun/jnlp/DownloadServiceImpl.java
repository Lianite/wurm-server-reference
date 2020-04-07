// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import com.sun.javaws.cache.DiskCacheEntry;
import com.sun.javaws.cache.Cache;
import com.sun.javaws.jnl.JARDesc;
import java.security.PrivilegedActionException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.LaunchDownload;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.javaws.cache.DownloadProtocol;
import java.net.URL;
import java.security.AccessController;
import com.sun.javaws.ui.DownloadWindow;
import java.security.PrivilegedAction;
import javax.jnlp.DownloadServiceListener;
import javax.jnlp.DownloadService;

public final class DownloadServiceImpl implements DownloadService
{
    private static DownloadServiceImpl _sharedInstance;
    private DownloadServiceListener _defaultProgress;
    
    private DownloadServiceImpl() {
        this._defaultProgress = null;
    }
    
    public static synchronized DownloadServiceImpl getInstance() {
        initialize();
        return DownloadServiceImpl._sharedInstance;
    }
    
    public static synchronized void initialize() {
        if (DownloadServiceImpl._sharedInstance == null) {
            DownloadServiceImpl._sharedInstance = new DownloadServiceImpl();
        }
    }
    
    public DownloadServiceListener getDefaultProgressWindow() {
        if (this._defaultProgress == null) {
            this._defaultProgress = AccessController.doPrivileged((PrivilegedAction<DownloadServiceListener>)new PrivilegedAction() {
                public Object run() {
                    return new DefaultProgressImpl(new DownloadWindow(JNLPClassLoader.getInstance().getLaunchDesc(), false));
                }
            });
        }
        return this._defaultProgress;
    }
    
    public boolean isResourceCached(final URL url, final String s) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                if (DownloadProtocol.isInCache(url, s, 0) || DownloadProtocol.isInCache(url, s, 1)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });
    }
    
    public boolean isPartCached(final String s) {
        return this.isPartCached(new String[] { s });
    }
    
    public boolean isPartCached(final String[] array) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                final ResourcesDesc resources = JNLPClassLoader.getInstance().getLaunchDesc().getResources();
                if (resources == null) {
                    return Boolean.FALSE;
                }
                return new Boolean(DownloadServiceImpl.this.isJARInCache(resources.getPartJars(array), true));
            }
        });
    }
    
    public boolean isExtensionPartCached(final URL url, final String s, final String s2) {
        return this.isExtensionPartCached(url, s, new String[] { s2 });
    }
    
    public boolean isExtensionPartCached(final URL url, final String s, final String[] array) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                final ResourcesDesc resources = JNLPClassLoader.getInstance().getLaunchDesc().getResources();
                if (resources == null) {
                    return Boolean.FALSE;
                }
                return new Boolean(DownloadServiceImpl.this.isJARInCache(resources.getExtensionPart(url, s, array), true));
            }
        });
    }
    
    public void loadResource(final URL url, final String s, final DownloadServiceListener downloadServiceListener) throws IOException {
        if (this.isResourceCached(url, s)) {
            return;
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    try {
                        JNLPClassLoader.getInstance().downloadResource(url, s, new ProgressHelper(downloadServiceListener), true);
                    }
                    catch (JNLPException ex) {
                        throw new IOException(ex.getMessage());
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public void loadPart(final String s, final DownloadServiceListener downloadServiceListener) throws IOException {
        this.loadPart(new String[] { s }, downloadServiceListener);
    }
    
    public void loadPart(final String[] array, final DownloadServiceListener downloadServiceListener) throws IOException {
        if (this.isPartCached(array)) {
            return;
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    try {
                        JNLPClassLoader.getInstance().downloadParts(array, new ProgressHelper(downloadServiceListener), true);
                    }
                    catch (JNLPException ex) {
                        throw new IOException(ex.getMessage());
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public void loadExtensionPart(final URL url, final String s, final String s2, final DownloadServiceListener downloadServiceListener) throws IOException {
        this.loadExtensionPart(url, s, new String[] { s2 }, downloadServiceListener);
    }
    
    public void loadExtensionPart(final URL url, final String s, final String[] array, final DownloadServiceListener downloadServiceListener) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    try {
                        JNLPClassLoader.getInstance().downloadExtensionParts(url, s, array, new ProgressHelper(downloadServiceListener), true);
                    }
                    catch (JNLPException ex) {
                        throw new IOException(ex.getMessage());
                    }
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public void removeResource(final URL url, final String s) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    final ResourcesDesc resources = JNLPClassLoader.getInstance().getLaunchDesc().getResources();
                    if (resources == null) {
                        return null;
                    }
                    DownloadServiceImpl.this.removeJARFromCache(resources.getResource(url, s));
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public void removePart(final String s) throws IOException {
        this.removePart(new String[] { s });
    }
    
    public void removePart(final String[] array) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    final ResourcesDesc resources = JNLPClassLoader.getInstance().getLaunchDesc().getResources();
                    if (resources == null) {
                        return null;
                    }
                    DownloadServiceImpl.this.removeJARFromCache(resources.getPartJars(array));
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public void removeExtensionPart(final URL url, final String s, final String s2) throws IOException {
        this.removeExtensionPart(url, s, new String[] { s2 });
    }
    
    public void removeExtensionPart(final URL url, final String s, final String[] array) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    final ResourcesDesc resources = JNLPClassLoader.getInstance().getLaunchDesc().getResources();
                    if (resources == null) {
                        return null;
                    }
                    DownloadServiceImpl.this.removeJARFromCache(resources.getExtensionPart(url, s, array));
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private void removeJARFromCache(final JARDesc[] array) throws IOException {
        if (array == null) {
            return;
        }
        if (array.length == 0) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            final int nativeLib = array[i].isNativeLib() ? 1 : 0;
            DiskCacheEntry resource;
            try {
                resource = DownloadProtocol.getResource(array[i].getLocation(), array[i].getVersion(), nativeLib, true, null);
            }
            catch (JNLPException ex) {
                throw new IOException(ex.getMessage());
            }
            if (resource != null) {
                Cache.removeEntry(resource);
            }
        }
    }
    
    private boolean isJARInCache(final JARDesc[] array, final boolean b) {
        if (array == null) {
            return false;
        }
        if (array.length == 0) {
            return false;
        }
        boolean b2 = true;
        for (int i = 0; i < array.length; ++i) {
            if (array[i].isNativeLib()) {
                if (DownloadProtocol.isInCache(array[i].getLocation(), array[i].getVersion(), 1)) {
                    if (!b) {
                        return true;
                    }
                }
                else {
                    b2 = false;
                }
            }
            else if (DownloadProtocol.isInCache(array[i].getLocation(), array[i].getVersion(), 0)) {
                if (!b) {
                    return true;
                }
            }
            else {
                b2 = false;
            }
        }
        return b2;
    }
    
    static {
        DownloadServiceImpl._sharedInstance = null;
    }
    
    private class DefaultProgressImpl implements DownloadServiceListener
    {
        private DownloadWindow _dw;
        
        DefaultProgressImpl(final DownloadWindow downloadWindow) {
            this._dw = null;
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    DefaultProgressImpl.this._dw = downloadWindow;
                    DefaultProgressImpl.this._dw.buildIntroScreen();
                    DefaultProgressImpl.this._dw.showLoadingProgressScreen();
                    return null;
                }
            });
        }
        
        public void progress(final URL url, final String s, final long n, final long n2, final int n3) {
            this.ensureVisible();
            if (n == 0L) {
                this._dw.resetDownloadTimer();
            }
            this._dw.progress(url, s, n, n2, n3);
            if (n3 >= 100) {
                this.hideFrame();
            }
            if (this._dw.isCanceled()) {
                this.hideFrame();
                throw new RuntimeException("canceled by user");
            }
        }
        
        public void validating(final URL url, final String s, final long n, final long n2, final int n3) {
            this.ensureVisible();
            this._dw.validating(url, s, n, n2, n3);
            if (n >= n2 && (n3 < 0 || n3 >= 99)) {
                this.hideFrame();
            }
        }
        
        public void upgradingArchive(final URL url, final String s, final int n, final int n2) {
            this.ensureVisible();
            this._dw.patching(url, s, n, n2);
            if (n2 >= 100) {
                this.hideFrame();
            }
        }
        
        public void downloadFailed(final URL url, final String s) {
            this.hideFrame();
        }
        
        private void ensureVisible() {
            if (!this._dw.getFrame().isVisible()) {
                this._dw.getFrame().setVisible(true);
                this._dw.getFrame().toFront();
            }
        }
        
        private synchronized void hideFrame() {
            this._dw.resetCancled();
            this._dw.getFrame().hide();
        }
    }
    
    private class ProgressHelper implements LaunchDownload.DownloadProgress
    {
        DownloadServiceListener _dsp;
        
        public ProgressHelper(final DownloadServiceListener dsp) {
            this._dsp = null;
            (this._dsp = dsp).progress(null, null, 0L, 0L, -1);
        }
        
        public void extensionDownload(final String s, final int n) {
        }
        
        public void jreDownload(final String s, final URL url) {
        }
        
        public void progress(final URL url, final String s, final long n, final long n2, final int n3) {
            if (this._dsp != null) {
                this._dsp.progress(url, s, n, n2, n3);
            }
        }
        
        public void validating(final URL url, final String s, final long n, final long n2, final int n3) {
            if (this._dsp != null) {
                this._dsp.validating(url, s, n, n2, n3);
            }
        }
        
        public void patching(final URL url, final String s, final int n, final int n2) {
            if (this._dsp != null) {
                this._dsp.upgradingArchive(url, s, n, n2);
            }
        }
        
        public void downloadFailed(final URL url, final String s) {
            if (this._dsp != null) {
                this._dsp.downloadFailed(url, s);
            }
        }
    }
}
