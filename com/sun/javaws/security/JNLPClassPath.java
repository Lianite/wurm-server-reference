// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.security;

import java.io.FileInputStream;
import com.sun.javaws.Globals;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.security.AccessControlException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.jar.JarFile;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.Permission;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.ListIterator;
import com.sun.javaws.cache.DiskCacheEntry;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.javaws.cache.DownloadProtocol;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.IOException;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import java.net.URL;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.util.URLUtil;
import java.util.HashMap;
import com.sun.javaws.jnl.LaunchDesc;
import java.util.ArrayList;
import java.util.Stack;

public class JNLPClassPath
{
    private Stack _pendingJarDescs;
    private ArrayList _loaders;
    private Loader _appletLoader;
    private LaunchDesc _launchDesc;
    private HashMap _fileToUrls;
    
    public JNLPClassPath(final LaunchDesc launchDesc, final boolean b) {
        this._pendingJarDescs = new Stack();
        this._loaders = new ArrayList();
        this._appletLoader = null;
        this._launchDesc = null;
        this._fileToUrls = new HashMap();
        this._launchDesc = launchDesc;
        if (b) {
            final URL base = URLUtil.getBase(launchDesc.getCanonicalHome());
            Trace.println("Classpath: " + base, TraceLevel.BASIC);
            if ("file".equals(base.getProtocol())) {
                this._appletLoader = new FileDirectoryLoader(base);
            }
            else {
                this._appletLoader = new URLDirectoryLoader(base);
            }
        }
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources != null) {
            final JARDesc[] eagerOrAllJarDescs = resources.getEagerOrAllJarDescs(true);
            for (int i = eagerOrAllJarDescs.length - 1; i >= 0; --i) {
                if (eagerOrAllJarDescs[i].isJavaFile()) {
                    Trace.println("Classpath: " + eagerOrAllJarDescs[i].getLocation() + ":" + eagerOrAllJarDescs[i].getVersion(), TraceLevel.BASIC);
                    this._pendingJarDescs.add(eagerOrAllJarDescs[i]);
                }
            }
        }
    }
    
    public synchronized JARDesc getJarDescFromFileURL(final URL url) {
        return this._fileToUrls.get(url.toString());
    }
    
    private void loadAllResources() {
        try {
            for (JARDesc jarDesc = this.getNextPendingJarDesc(); jarDesc != null; jarDesc = this.getNextPendingJarDesc()) {
                this.createLoader(jarDesc);
            }
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        if (this._appletLoader != null) {
            synchronized (this._loaders) {
                this._loaders.add(this._appletLoader);
            }
        }
    }
    
    private synchronized JARDesc getNextPendingJarDesc() {
        return this._pendingJarDescs.isEmpty() ? null : this._pendingJarDescs.pop();
    }
    
    private synchronized JARDesc getIfPendingJarDesc(final JARDesc jarDesc) {
        if (this._pendingJarDescs.contains(jarDesc)) {
            this._pendingJarDescs.remove(jarDesc);
            return jarDesc;
        }
        return null;
    }
    
    private Loader createLoader(final JARDesc jarDesc) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Loader>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return JNLPClassPath.this.createLoaderHelper(jarDesc);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            Trace.println("Failed to create loader for: " + jarDesc + " (" + ex.getException() + ")", TraceLevel.BASIC);
            throw (IOException)ex.getException();
        }
    }
    
    private Loader createLoaderHelper(final JARDesc jarDesc) throws IOException {
        final URL location = jarDesc.getLocation();
        final String version = jarDesc.getVersion();
        try {
            final DiskCacheEntry resource = DownloadProtocol.getResource(location, version, 0, true, null);
            if (resource == null || !resource.getFile().exists()) {
                throw new IOException("Resource not found: " + jarDesc.getLocation() + ":" + jarDesc.getVersion());
            }
            final URL url = new URL("file", "", URLUtil.getEncodedPath(resource.getFile()));
            Trace.println("Creating loader for: " + url, TraceLevel.BASIC);
            final JarLoader jarLoader = new JarLoader(url);
            synchronized (this) {
                this._loaders.add(jarLoader);
                this._fileToUrls.put(url.toString(), jarDesc);
            }
            return jarLoader;
        }
        catch (JNLPException ex) {
            Trace.println("Failed to download: " + ex + " (" + ex + ")", TraceLevel.BASIC);
            Trace.ignoredException((Exception)ex);
            throw new IOException(ex.getMessage());
        }
    }
    
    private Resource findNamedResource(final String s, final boolean b) throws IOException {
        Resource resource = this.findNamedResourceInLoaders(s, b);
        if (resource != null) {
            return resource;
        }
        synchronized (this) {
            if (this._pendingJarDescs.isEmpty()) {
                return null;
            }
        }
        final ResourcesDesc.PackageInformation packageInformation = this._launchDesc.getResources().getPackageInformation(s);
        if (packageInformation != null) {
            final JARDesc[] part = packageInformation.getLaunchDesc().getResources().getPart(packageInformation.getPart());
            for (int i = 0; i < part.length; ++i) {
                final JARDesc ifPendingJarDesc = this.getIfPendingJarDesc(part[i]);
                if (ifPendingJarDesc != null) {
                    this.createLoader(ifPendingJarDesc);
                }
            }
            resource = this.findNamedResourceInLoaders(s, b);
            if (resource != null) {
                return resource;
            }
        }
        synchronized (this) {
            final ListIterator listIterator = this._pendingJarDescs.listIterator(this._pendingJarDescs.size());
            while (listIterator.hasPrevious()) {
                final JARDesc jarDesc = listIterator.previous();
                if (!this._launchDesc.getResources().isPackagePart(jarDesc.getPartName())) {
                    listIterator.remove();
                    resource = this.createLoader(jarDesc).getResource(s, b);
                    if (resource != null) {
                        return resource;
                    }
                    continue;
                }
            }
        }
        if (this._appletLoader != null) {
            resource = this._appletLoader.getResource(s, b);
        }
        return resource;
    }
    
    private Resource findNamedResourceInLoaders(final String s, final boolean b) throws IOException {
        int size = 0;
        synchronized (this) {
            size = this._loaders.size();
        }
        for (int i = 0; i < size; ++i) {
            Loader loader = null;
            synchronized (this) {
                loader = this._loaders.get(i);
            }
            final Resource resource = loader.getResource(s, b);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }
    
    public Resource getResource(final String s, final boolean b) {
        Trace.println("getResource: " + s + " (check: " + b + ")", TraceLevel.BASIC);
        try {
            return this.findNamedResource(s, b);
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
            return null;
        }
    }
    
    public Resource getResource(final String s) {
        return this.getResource(s, true);
    }
    
    public Enumeration getResources(final String s, final boolean b) {
        this.loadAllResources();
        final int size;
        synchronized (this) {
            size = this._loaders.size();
        }
        return new Enumeration() {
            private int index = 0;
            private Resource res = null;
            
            private boolean next() {
                if (this.res != null) {
                    return true;
                }
                while (this.index < size) {
                    this.res = JNLPClassPath.this._loaders.get(this.index++).getResource(s, b);
                    if (this.res != null) {
                        return true;
                    }
                }
                return false;
            }
            
            public boolean hasMoreElements() {
                return this.next();
            }
            
            public Object nextElement() {
                if (!this.next()) {
                    throw new NoSuchElementException();
                }
                final Resource res = this.res;
                this.res = null;
                return res;
            }
        };
    }
    
    public Enumeration getResources(final String s) {
        return this.getResources(s, true);
    }
    
    public URL checkURL(final URL url) {
        try {
            check(url);
        }
        catch (Exception ex) {
            return null;
        }
        return url;
    }
    
    private static void check(final URL url) throws IOException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final Permission permission = url.openConnection().getPermission();
            if (permission != null) {
                securityManager.checkPermission(permission);
            }
        }
    }
    
    private abstract static class Loader
    {
        private final URL base;
        
        Loader(final URL base) {
            this.base = base;
        }
        
        Resource getResource(final String s) {
            return this.getResource(s, true);
        }
        
        abstract Resource getResource(final String p0, final boolean p1);
        
        URL getBaseURL() {
            return this.base;
        }
    }
    
    private static class URLDirectoryLoader extends Loader
    {
        URLDirectoryLoader(final URL url) {
            super(url);
        }
        
        Resource getResource(final String s, final boolean b) {
            URL url;
            try {
                url = new URL(this.getBaseURL(), s);
            }
            catch (MalformedURLException ex) {
                throw new IllegalArgumentException("name");
            }
            URLConnection openConnection;
            try {
                if (b) {
                    check(url);
                }
                openConnection = url.openConnection();
                if (openConnection instanceof HttpURLConnection) {
                    final HttpURLConnection httpURLConnection = (HttpURLConnection)openConnection;
                    final int responseCode = httpURLConnection.getResponseCode();
                    httpURLConnection.disconnect();
                    if (responseCode >= 400) {
                        return null;
                    }
                }
                else {
                    url.openStream().close();
                }
            }
            catch (Exception ex2) {
                return null;
            }
            return new Resource() {
                public String getName() {
                    return s;
                }
                
                public URL getURL() {
                    return url;
                }
                
                public URL getCodeSourceURL() {
                    return URLDirectoryLoader.this.getBaseURL();
                }
                
                public InputStream getInputStream() throws IOException {
                    return openConnection.getInputStream();
                }
                
                public int getContentLength() throws IOException {
                    return openConnection.getContentLength();
                }
            };
        }
    }
    
    private static class JarLoader extends Loader
    {
        private JarFile jar;
        private URL csu;
        
        JarLoader(final URL csu) throws IOException {
            super(new URL("jar", "", -1, csu + "!/"));
            this.jar = this.getJarFile(csu);
            this.csu = csu;
        }
        
        private JarFile getJarFile(final URL url) throws IOException {
            if (!"file".equals(url.getProtocol())) {
                throw new IOException("Must be file URL");
            }
            final String pathFromURL = URLUtil.getPathFromURL(url);
            if (!new File(pathFromURL).exists()) {
                throw new FileNotFoundException(pathFromURL);
            }
            return new JarFile(pathFromURL);
        }
        
        Resource getResource(final String s, final boolean b) {
            final JarEntry jarEntry = this.jar.getJarEntry(s);
            if (jarEntry != null) {
                URL url;
                try {
                    url = new URL(this.getBaseURL(), s);
                    if (b) {
                        check(url);
                    }
                }
                catch (MalformedURLException ex) {
                    Trace.ignoredException((Exception)ex);
                    return null;
                }
                catch (IOException ex2) {
                    Trace.ignoredException((Exception)ex2);
                    return null;
                }
                catch (AccessControlException ex3) {
                    Trace.ignoredException((Exception)ex3);
                    return null;
                }
                return new Resource() {
                    public String getName() {
                        return s;
                    }
                    
                    public URL getURL() {
                        return url;
                    }
                    
                    public URL getCodeSourceURL() {
                        return JarLoader.this.csu;
                    }
                    
                    public InputStream getInputStream() throws IOException {
                        return JarLoader.this.jar.getInputStream(jarEntry);
                    }
                    
                    public int getContentLength() {
                        return (int)jarEntry.getSize();
                    }
                    
                    public Manifest getManifest() throws IOException {
                        return JarLoader.this.jar.getManifest();
                    }
                    
                    public Certificate[] getCertificates() {
                        return jarEntry.getCertificates();
                    }
                    
                    public CodeSigner[] getCodeSigners() {
                        if (Globals.isJavaVersionAtLeast15()) {
                            return jarEntry.getCodeSigners();
                        }
                        return null;
                    }
                };
            }
            return null;
        }
    }
    
    private static class FileDirectoryLoader extends Loader
    {
        private File dir;
        
        FileDirectoryLoader(final URL url) {
            super(url);
            if (!"file".equals(url.getProtocol())) {
                throw new IllegalArgumentException("must be FILE URL");
            }
            this.dir = new File(URLUtil.getPathFromURL(url));
        }
        
        Resource getResource(final String s, final boolean b) {
            try {
                final URL url = new URL(this.getBaseURL(), s);
                if (!url.getFile().startsWith(this.getBaseURL().getFile())) {
                    return null;
                }
                if (b) {
                    check(url);
                }
                final File file = new File(this.dir, s.replace('/', File.separatorChar));
                if (file.exists()) {
                    return new Resource() {
                        public String getName() {
                            return s;
                        }
                        
                        public URL getURL() {
                            return url;
                        }
                        
                        public URL getCodeSourceURL() {
                            return FileDirectoryLoader.this.getBaseURL();
                        }
                        
                        public InputStream getInputStream() throws IOException {
                            return new FileInputStream(file);
                        }
                        
                        public int getContentLength() throws IOException {
                            return (int)file.length();
                        }
                    };
                }
            }
            catch (Exception ex) {
                return null;
            }
            return null;
        }
    }
}
