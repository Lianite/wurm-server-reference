// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import sun.awt.AppContext;
import java.awt.AWTPermission;
import java.net.SocketPermission;
import java.security.Permission;
import java.io.FilePermission;
import com.sun.javaws.cache.Cache;
import java.security.PermissionCollection;
import com.sun.javaws.Main;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.security.PrivilegedAction;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.security.CodeSource;
import com.sun.javaws.Globals;
import java.security.PrivilegedActionException;
import com.sun.javaws.security.Resource;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import com.sun.deploy.config.Config;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.javaws.jnl.JARDesc;
import java.io.IOException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.LaunchDownload;
import java.net.URL;
import java.security.AccessController;
import java.security.AccessControlContext;
import com.sun.javaws.security.AppPolicy;
import com.sun.javaws.security.JNLPClassPath;
import com.sun.javaws.jnl.LaunchDesc;
import java.security.SecureClassLoader;

public final class JNLPClassLoader extends SecureClassLoader
{
    private static JNLPClassLoader _instance;
    private LaunchDesc _launchDesc;
    private JNLPClassPath _jcp;
    private AppPolicy _appPolicy;
    private AccessControlContext _acc;
    private boolean _initialized;
    
    public JNLPClassLoader(final ClassLoader classLoader) {
        super(classLoader);
        this._launchDesc = null;
        this._jcp = null;
        this._acc = null;
        this._initialized = false;
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkCreateClassLoader();
        }
    }
    
    private void initialize(final LaunchDesc launchDesc, final boolean b, final AppPolicy appPolicy) {
        this._launchDesc = launchDesc;
        this._jcp = new JNLPClassPath(launchDesc, b);
        this._acc = AccessController.getContext();
        this._appPolicy = appPolicy;
        this._initialized = true;
    }
    
    public static synchronized JNLPClassLoader createClassLoader() {
        if (JNLPClassLoader._instance == null) {
            final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            if (systemClassLoader instanceof JNLPClassLoader) {
                JNLPClassLoader._instance = (JNLPClassLoader)systemClassLoader;
            }
            else {
                JNLPClassLoader._instance = new JNLPClassLoader(systemClassLoader);
            }
        }
        return JNLPClassLoader._instance;
    }
    
    public static synchronized JNLPClassLoader createClassLoader(final LaunchDesc launchDesc, final AppPolicy appPolicy) {
        final JNLPClassLoader classLoader = createClassLoader();
        if (!classLoader._initialized) {
            classLoader.initialize(launchDesc, launchDesc.isApplet(), appPolicy);
        }
        return classLoader;
    }
    
    public static synchronized JNLPClassLoader getInstance() {
        return JNLPClassLoader._instance;
    }
    
    public LaunchDesc getLaunchDesc() {
        return this._launchDesc;
    }
    
    public void downloadResource(final URL url, final String s, final LaunchDownload.DownloadProgress downloadProgress, final boolean b) throws JNLPException, IOException {
        LaunchDownload.downloadResource(this._launchDesc, url, s, downloadProgress, b);
    }
    
    public void downloadParts(final String[] array, final LaunchDownload.DownloadProgress downloadProgress, final boolean b) throws JNLPException, IOException {
        LaunchDownload.downloadParts(this._launchDesc, array, downloadProgress, b);
    }
    
    public void downloadExtensionParts(final URL url, final String s, final String[] array, final LaunchDownload.DownloadProgress downloadProgress, final boolean b) throws JNLPException, IOException {
        LaunchDownload.downloadExtensionPart(this._launchDesc, url, s, array, downloadProgress, b);
    }
    
    public void downloadEager(final LaunchDownload.DownloadProgress downloadProgress, final boolean b) throws JNLPException, IOException {
        LaunchDownload.downloadEagerorAll(this._launchDesc, false, downloadProgress, b);
    }
    
    public JARDesc getJarDescFromFileURL(final URL url) {
        return this._jcp.getJarDescFromFileURL(url);
    }
    
    public int getDefaultSecurityModel() {
        return this._launchDesc.getSecurityModel();
    }
    
    public URL getResource(final String s) {
        URL resource = null;
        for (int n = 0; resource == null && n < 3; resource = super.getResource(s), ++n) {
            Trace.println("Looking up resource: " + s + " (attempt: " + n + ")", TraceLevel.BASIC);
        }
        return resource;
    }
    
    public String findLibrary(String string) {
        if (!this._initialized) {
            return super.findLibrary(string);
        }
        string = Config.getInstance().getLibraryPrefix() + string + Config.getInstance().getLibrarySufix();
        Trace.println("Looking up native library: " + string, TraceLevel.BASIC);
        final File[] nativeDirectories = LaunchDownload.getNativeDirectories(this._launchDesc);
        for (int i = 0; i < nativeDirectories.length; ++i) {
            final File file = new File(nativeDirectories[i], string);
            if (file.exists()) {
                Trace.println("Native library found: " + file.getAbsolutePath(), TraceLevel.BASIC);
                return file.getAbsolutePath();
            }
        }
        Trace.println("Native library not found", TraceLevel.BASIC);
        return super.findLibrary(string);
    }
    
    protected Class findClass(final String s) throws ClassNotFoundException {
        if (!this._initialized) {
            return super.findClass(s);
        }
        Trace.println("Loading class " + s, TraceLevel.BASIC);
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Class>)new PrivilegedExceptionAction() {
                public Object run() throws ClassNotFoundException {
                    final Resource resource = JNLPClassLoader.this._jcp.getResource(s.replace('.', '/').concat(".class"), false);
                    if (resource != null) {
                        try {
                            return JNLPClassLoader.this.defineClass(s, resource);
                        }
                        catch (IOException ex) {
                            throw new ClassNotFoundException(s, ex);
                        }
                    }
                    throw new ClassNotFoundException(s);
                }
            }, this._acc);
        }
        catch (PrivilegedActionException ex) {
            throw (ClassNotFoundException)ex.getException();
        }
    }
    
    private Class defineClass(final String s, final Resource resource) throws IOException {
        final int lastIndex = s.lastIndexOf(46);
        final URL codeSourceURL = resource.getCodeSourceURL();
        if (lastIndex != -1) {
            final String substring = s.substring(0, lastIndex);
            final Package package1 = this.getPackage(substring);
            final Manifest manifest = resource.getManifest();
            if (package1 != null) {
                boolean sealed;
                if (package1.isSealed()) {
                    sealed = package1.isSealed(codeSourceURL);
                }
                else {
                    sealed = (manifest == null || !this.isSealed(substring, manifest));
                }
                if (!sealed) {
                    throw new SecurityException("sealing violation");
                }
            }
            else if (manifest != null) {
                this.definePackage(substring, manifest, codeSourceURL);
            }
            else {
                this.definePackage(substring, null, null, null, null, null, null, null);
            }
        }
        final byte[] bytes = resource.getBytes();
        CodeSource codeSource;
        if (Globals.isJavaVersionAtLeast15()) {
            codeSource = new CodeSource(codeSourceURL, resource.getCodeSigners());
        }
        else {
            codeSource = new CodeSource(codeSourceURL, resource.getCertificates());
        }
        return this.defineClass(s, bytes, 0, bytes.length, codeSource);
    }
    
    protected Package definePackage(final String s, final Manifest manifest, final URL url) throws IllegalArgumentException {
        final String concat = s.replace('.', '/').concat("/");
        String s2 = null;
        String s3 = null;
        String s4 = null;
        String s5 = null;
        String s6 = null;
        String s7 = null;
        String s8 = null;
        URL url2 = null;
        final Attributes attributes = manifest.getAttributes(concat);
        if (attributes != null) {
            s2 = attributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            s3 = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            s4 = attributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            s5 = attributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            s6 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            s7 = attributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            s8 = attributes.getValue(Attributes.Name.SEALED);
        }
        final Attributes mainAttributes = manifest.getMainAttributes();
        if (mainAttributes != null) {
            if (s2 == null) {
                s2 = mainAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            }
            if (s3 == null) {
                s3 = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
            if (s4 == null) {
                s4 = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            }
            if (s5 == null) {
                s5 = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            }
            if (s6 == null) {
                s6 = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }
            if (s7 == null) {
                s7 = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            }
            if (s8 == null) {
                s8 = mainAttributes.getValue(Attributes.Name.SEALED);
            }
        }
        if ("true".equalsIgnoreCase(s8)) {
            url2 = url;
        }
        return this.definePackage(s, s2, s3, s4, s5, s6, s7, url2);
    }
    
    private boolean isSealed(final String s, final Manifest manifest) {
        final Attributes attributes = manifest.getAttributes(s.replace('.', '/').concat("/"));
        String s2 = null;
        if (attributes != null) {
            s2 = attributes.getValue(Attributes.Name.SEALED);
        }
        final Attributes mainAttributes;
        if (s2 == null && (mainAttributes = manifest.getMainAttributes()) != null) {
            s2 = mainAttributes.getValue(Attributes.Name.SEALED);
        }
        return "true".equalsIgnoreCase(s2);
    }
    
    public URL findResource(final String s) {
        if (!this._initialized) {
            return super.findResource(s);
        }
        final Resource resource = AccessController.doPrivileged((PrivilegedAction<Resource>)new PrivilegedAction() {
            public Object run() {
                return JNLPClassLoader.this._jcp.getResource(s, true);
            }
        }, this._acc);
        return (resource != null) ? this._jcp.checkURL(resource.getURL()) : null;
    }
    
    public Enumeration findResources(final String s) throws IOException {
        if (!this._initialized) {
            return super.findResources(s);
        }
        return new Enumeration() {
            private URL res;
            private final /* synthetic */ Enumeration val$e = AccessController.doPrivileged((PrivilegedAction<Enumeration>)new PrivilegedAction(s) {
                private final /* synthetic */ String val$name = val$name;
                
                public Object run() {
                    return JNLPClassLoader.this._jcp.getResources(this.val$name, true);
                }
            }, JNLPClassLoader.this._acc);
            
            public Object nextElement() {
                if (this.res == null) {
                    throw new NoSuchElementException();
                }
                final URL res = this.res;
                this.res = null;
                return res;
            }
            
            public boolean hasMoreElements() {
                if (Thread.currentThread().getThreadGroup() == Main.getSecurityThreadGroup()) {
                    return false;
                }
                if (this.res != null) {
                    return true;
                }
                do {
                    final Resource resource = AccessController.doPrivileged((PrivilegedAction<Resource>)new PrivilegedAction() {
                        public Object run() {
                            if (!Enumeration.this.val$e.hasMoreElements()) {
                                return null;
                            }
                            return Enumeration.this.val$e.nextElement();
                        }
                    }, JNLPClassLoader.this._acc);
                    if (resource == null) {
                        break;
                    }
                    this.res = JNLPClassLoader.this._jcp.checkURL(resource.getURL());
                } while (this.res == null);
                return this.res != null;
            }
        };
    }
    
    protected PermissionCollection getPermissions(final CodeSource codeSource) {
        final PermissionCollection permissions = super.getPermissions(codeSource);
        this._appPolicy.addPermissions(permissions, codeSource);
        final URL location = codeSource.getLocation();
        Permission permission;
        try {
            permission = location.openConnection().getPermission();
        }
        catch (IOException ex) {
            permission = null;
        }
        final JARDesc jarDescFromFileURL = this._jcp.getJarDescFromFileURL(location);
        if (jarDescFromFileURL != null) {
            final String[] baseDirsForHost = Cache.getBaseDirsForHost(jarDescFromFileURL.getLocation());
            for (int i = 0; i < baseDirsForHost.length; ++i) {
                final String s = baseDirsForHost[i];
                if (s != null) {
                    String s2;
                    if (s.endsWith(File.separator)) {
                        s2 = s + '-';
                    }
                    else {
                        s2 = s + File.separator + '-';
                    }
                    permissions.add(new FilePermission(s2, "read"));
                }
            }
        }
        if (permission instanceof FilePermission) {
            final String name = permission.getName();
            if (name.endsWith(File.separator)) {
                permission = new FilePermission(name + "-", "read");
            }
        }
        else if (permission == null && location.getProtocol().equals("file")) {
            String s3 = location.getFile().replace('/', File.separatorChar);
            if (s3.endsWith(File.separator)) {
                s3 += "-";
            }
            permission = new FilePermission(s3, "read");
        }
        else {
            String host = location.getHost();
            if (host == null) {
                host = "localhost";
            }
            permission = new SocketPermission(host, "connect, accept");
        }
        if (permission != null) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    public Object run() throws SecurityException {
                        securityManager.checkPermission(permission);
                        return null;
                    }
                }, this._acc);
            }
            permissions.add(permission);
        }
        if (!permissions.implies(new AWTPermission("accessClipboard"))) {
            AppContext.getAppContext().put("UNTRUSTED_CLIPBOARD_ACCESS_KEY", Boolean.TRUE);
        }
        return permissions;
    }
    
    public final synchronized Class loadClass(final String s, final boolean b) throws ClassNotFoundException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final int lastIndex = s.lastIndexOf(46);
            if (lastIndex != -1) {
                securityManager.checkPackageAccess(s.substring(0, lastIndex));
            }
        }
        return super.loadClass(s, b);
    }
    
    static {
        JNLPClassLoader._instance = null;
    }
}
