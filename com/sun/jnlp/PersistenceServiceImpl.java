// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import com.sun.javaws.jnl.LaunchDesc;
import com.sun.deploy.resources.ResourceManager;
import java.security.PrivilegedAction;
import java.util.Vector;
import com.sun.deploy.util.URLUtil;
import com.sun.javaws.cache.DiskCacheEntry;
import java.io.FileNotFoundException;
import javax.jnlp.FileContents;
import java.io.File;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.javaws.cache.Cache;
import java.net.URL;
import com.sun.deploy.config.Config;
import javax.jnlp.PersistenceService;

public final class PersistenceServiceImpl implements PersistenceService
{
    private long _globalLimit;
    private long _appLimit;
    private long _size;
    private static PersistenceServiceImpl _sharedInstance;
    private final SmartSecurityDialog _securityDialog;
    
    private PersistenceServiceImpl() {
        this._globalLimit = -1L;
        this._appLimit = -1L;
        this._size = -1L;
        this._securityDialog = new SmartSecurityDialog();
    }
    
    public static synchronized PersistenceServiceImpl getInstance() {
        initialize();
        return PersistenceServiceImpl._sharedInstance;
    }
    
    public static synchronized void initialize() {
        if (PersistenceServiceImpl._sharedInstance == null) {
            PersistenceServiceImpl._sharedInstance = new PersistenceServiceImpl();
        }
        if (PersistenceServiceImpl._sharedInstance != null) {
            PersistenceServiceImpl._sharedInstance._appLimit = Config.getIntProperty("deployment.javaws.muffin.max") * 1024L;
        }
    }
    
    long getLength(final URL url) throws MalformedURLException, IOException {
        this.checkAccess(url);
        return Cache.getMuffinSize(url);
    }
    
    long getMaxLength(final URL url) throws MalformedURLException, IOException {
        this.checkAccess(url);
        Long n;
        try {
            n = AccessController.doPrivileged((PrivilegedExceptionAction<Long>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    final long[] muffinAttributes = Cache.getMuffinAttributes(url);
                    if (muffinAttributes == null) {
                        return new Long(-1L);
                    }
                    return new Long(muffinAttributes[1]);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        return n;
    }
    
    long setMaxLength(final URL url, final long n) throws MalformedURLException, IOException {
        this.checkAccess(url);
        final long checkSetMaxSize;
        if ((checkSetMaxSize = this.checkSetMaxSize(url, n)) < 0L) {
            return -1L;
        }
        final long n2 = checkSetMaxSize;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    Cache.putMuffinAttributes(url, PersistenceServiceImpl.this.getTag(url), n2);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof MalformedURLException) {
                throw (MalformedURLException)exception;
            }
        }
        return checkSetMaxSize;
    }
    
    private long checkSetMaxSize(final URL url, final long n) throws IOException {
        URL[] array;
        try {
            array = AccessController.doPrivileged((PrivilegedExceptionAction<URL[]>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return Cache.getAccessibleMuffins(url);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        long n2 = 0L;
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != null) {
                    final URL url2 = array[i];
                    Long n3;
                    try {
                        n3 = AccessController.doPrivileged((PrivilegedExceptionAction<Long>)new PrivilegedExceptionAction() {
                            public Object run() throws IOException {
                                return new Long(Cache.getMuffinSize(url2));
                            }
                        });
                    }
                    catch (PrivilegedActionException ex2) {
                        throw (IOException)ex2.getException();
                    }
                    n2 += n3;
                }
            }
        }
        if (n + n2 > this._appLimit) {
            return this.reconcileMaxSize(n, n2, this._appLimit);
        }
        return n;
    }
    
    private long reconcileMaxSize(final long n, final long n2, final long n3) {
        final long appLimit = n + n2;
        if (CheckServicePermission.hasFileAccessPermissions() || this.askUser(appLimit, n3)) {
            this._appLimit = appLimit;
            return n;
        }
        return n3 - n2;
    }
    
    private URL[] getAccessibleMuffins(final URL url) throws IOException {
        return Cache.getAccessibleMuffins(url);
    }
    
    public long create(final URL url, final long n) throws MalformedURLException, IOException {
        this.checkAccess(url);
        Long n2 = null;
        final long checkSetMaxSize;
        if ((checkSetMaxSize = this.checkSetMaxSize(url, n)) < 0L) {
            return -1L;
        }
        final long n3 = checkSetMaxSize;
        try {
            n2 = AccessController.doPrivileged((PrivilegedExceptionAction<Long>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    final File tempCacheFile = Cache.getTempCacheFile(url, null);
                    if (tempCacheFile == null) {
                        return new Long(-1L);
                    }
                    Cache.insertMuffinEntry(url, tempCacheFile, 0, n3);
                    return new Long(n3);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof MalformedURLException) {
                throw (MalformedURLException)exception;
            }
        }
        return n2;
    }
    
    public FileContents get(final URL url) throws MalformedURLException, IOException {
        this.checkAccess(url);
        final File muffinFileForURL = Cache.getMuffinFileForURL(url);
        if (muffinFileForURL == null) {
            throw new FileNotFoundException(url.toString());
        }
        return new FileContentsImpl(muffinFileForURL, this, url, this.getMaxLength(url));
    }
    
    public void delete(final URL url) throws MalformedURLException, IOException {
        this.checkAccess(url);
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    final DiskCacheEntry muffinEntry = Cache.getMuffinEntry('P', url);
                    if (muffinEntry == null) {
                        throw new FileNotFoundException(url.toString());
                    }
                    Cache.removeMuffinEntry(muffinEntry);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof MalformedURLException) {
                throw (MalformedURLException)exception;
            }
        }
    }
    
    public String[] getNames(final URL url) throws MalformedURLException, IOException {
        String[] array = null;
        final URL pathURL = URLUtil.asPathURL(url);
        this.checkAccess(pathURL);
        try {
            array = AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    File file = Cache.getMuffinFileForURL(pathURL);
                    if (!file.isDirectory()) {
                        file = file.getParentFile();
                    }
                    final File[] listFiles = file.listFiles();
                    final Vector<String> vector = new Vector<String>();
                    for (int i = 0; i < listFiles.length; ++i) {
                        if (Cache.isMainMuffinFile(listFiles[i])) {
                            vector.addElement(new File(Cache.getMuffinCacheEntryFromFile(listFiles[i]).getLocation().getFile()).getName());
                        }
                    }
                    return vector.toArray(new String[0]);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof MalformedURLException) {
                throw (MalformedURLException)exception;
            }
        }
        return array;
    }
    
    public int getTag(final URL url) throws MalformedURLException, IOException {
        Integer n = null;
        this.checkAccess(url);
        try {
            n = AccessController.doPrivileged((PrivilegedExceptionAction<Integer>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    final long[] muffinAttributes = Cache.getMuffinAttributes(url);
                    if (muffinAttributes == null) {
                        throw new MalformedURLException();
                    }
                    return new Integer((int)muffinAttributes[0]);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof MalformedURLException) {
                throw (MalformedURLException)exception;
            }
        }
        return n;
    }
    
    public void setTag(final URL url, final int n) throws MalformedURLException, IOException {
        this.checkAccess(url);
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    Cache.putMuffinAttributes(url, n, PersistenceServiceImpl.this.getMaxLength(url));
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof MalformedURLException) {
                throw (MalformedURLException)exception;
            }
        }
    }
    
    private boolean askUser(final long n, final long n2) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                final boolean showDialog = PersistenceServiceImpl.this._securityDialog.showDialog(ResourceManager.getString("APIImpl.persistence.message", new Long(n), new Long(n2)));
                if (showDialog) {
                    Config.setIntProperty("deployment.javaws.muffin.max", (int)Math.min(2147483647L, (n + 1023L) / 1024L));
                    Config.storeIfDirty();
                }
                return new Boolean(showDialog);
            }
        });
    }
    
    private void checkAccess(final URL url) throws MalformedURLException {
        final LaunchDesc launchDesc = JNLPClassLoader.getInstance().getLaunchDesc();
        if (launchDesc != null) {
            final URL codebase = launchDesc.getCodebase();
            if (codebase != null) {
                if (url == null || !codebase.getHost().equals(url.getHost())) {
                    this.throwAccessDenied(url);
                }
                final String file = url.getFile();
                if (file == null) {
                    this.throwAccessDenied(url);
                }
                final int lastIndex = file.lastIndexOf(47);
                if (lastIndex == -1) {
                    return;
                }
                if (!codebase.getFile().startsWith(file.substring(0, lastIndex + 1))) {
                    this.throwAccessDenied(url);
                }
            }
        }
    }
    
    private void throwAccessDenied(final URL url) throws MalformedURLException {
        throw new MalformedURLException(ResourceManager.getString("APIImpl.persistence.accessdenied", url.toString()));
    }
    
    static {
        PersistenceServiceImpl._sharedInstance = null;
    }
}
