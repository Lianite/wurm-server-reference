// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.util.Enumeration;
import java.io.DataInputStream;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.security.CodeSource;
import java.security.cert.Certificate;
import com.sun.javaws.security.AppPolicy;
import com.sun.javaws.exceptions.UnsignedAccessViolationException;
import com.sun.javaws.security.SigningInfo;
import java.util.jar.Manifest;
import com.sun.javaws.jnl.AppletDesc;
import com.sun.javaws.jnl.InstallerDesc;
import com.sun.javaws.jnl.ApplicationDesc;
import com.sun.javaws.exceptions.LaunchDescException;
import com.sun.deploy.resources.ResourceManager;
import java.util.jar.JarFile;
import com.sun.javaws.exceptions.NativeLibViolationException;
import com.sun.javaws.exceptions.MultipleHostsException;
import com.sun.javaws.exceptions.FailedDownloadingResourceException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.sun.javaws.exceptions.ErrorCodeResponseException;
import java.net.MalformedURLException;
import com.sun.deploy.config.Config;
import com.sun.javaws.exceptions.MissingFieldException;
import com.sun.javaws.jnl.JREDesc;
import com.sun.javaws.jnl.PackageDesc;
import com.sun.javaws.jnl.PropertyDesc;
import com.sun.javaws.jnl.ResourceVisitor;
import com.sun.deploy.config.JREInfo;
import java.util.ArrayList;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.ExtensionDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.ResourcesDesc;
import com.sun.deploy.util.TraceLevel;
import java.io.File;
import com.sun.javaws.cache.DiskCacheEntry;
import java.net.URL;
import java.io.IOException;
import com.sun.javaws.exceptions.JNLPException;
import com.sun.deploy.util.Trace;
import com.sun.javaws.cache.Cache;
import java.io.InputStream;
import com.sun.javaws.jnl.LaunchDescFactory;
import java.io.FileInputStream;
import com.sun.javaws.cache.DownloadProtocol;
import com.sun.javaws.jnl.LaunchDesc;

public class LaunchDownload
{
    private static boolean updateAvailable;
    private static int numThread;
    private static Object syncObj;
    private static final String SIGNED_JNLP_ENTRY = "JNLP-INF/APPLICATION.JNLP";
    
    private static boolean compareByteArray(final byte[] array, final byte[] array2) {
        if (array.length == array2.length) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != array2[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    static boolean updateLaunchDescInCache(final LaunchDesc launchDesc) {
        URL url = launchDesc.getLocation();
        if (url == null) {
            url = launchDesc.getCanonicalHome();
        }
        if (url != null) {
            try {
                final DiskCacheEntry cachedLaunchedFile = DownloadProtocol.getCachedLaunchedFile(url);
                if (cachedLaunchedFile != null) {
                    final File file = cachedLaunchedFile.getFile();
                    final byte[] bytes = LaunchDescFactory.readBytes(new FileInputStream(file), file.length());
                    final byte[] bytes2 = launchDesc.getSource().getBytes();
                    if (bytes != null && bytes2 != null && compareByteArray(bytes, bytes2)) {
                        return false;
                    }
                }
                Cache.putCanonicalLaunchDesc(url, launchDesc);
                return true;
            }
            catch (JNLPException ex) {
                Trace.ignoredException((Exception)ex);
            }
            catch (IOException ex2) {
                Trace.ignoredException((Exception)ex2);
            }
        }
        return false;
    }
    
    static LaunchDesc getUpdatedLaunchDesc(final LaunchDesc launchDesc) throws JNLPException, IOException {
        if (launchDesc.getLocation() == null) {
            return null;
        }
        if (!DownloadProtocol.isLaunchFileUpdateAvailable(launchDesc.getLocation())) {
            return null;
        }
        Trace.println("Downloading updated JNLP descriptor from: " + launchDesc.getLocation(), TraceLevel.BASIC);
        final DiskCacheEntry launchFile = DownloadProtocol.getLaunchFile(launchDesc.getLocation(), false);
        try {
            return LaunchDescFactory.buildDescriptor(launchFile.getFile());
        }
        catch (JNLPException ex) {
            Cache.removeEntry(launchFile);
            throw ex;
        }
    }
    
    public static boolean isInCache(final LaunchDesc launchDesc) {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return true;
        }
        try {
            if (launchDesc.getLocation() != null && DownloadProtocol.getCachedLaunchedFile(launchDesc.getLocation()) == null) {
                return false;
            }
            if (!getCachedExtensions(launchDesc)) {
                return false;
            }
            final JARDesc[] eagerOrAllJarDescs = resources.getEagerOrAllJarDescs(false);
            for (int i = 0; i < eagerOrAllJarDescs.length; ++i) {
                if (!DownloadProtocol.isInCache(eagerOrAllJarDescs[i].getLocation(), eagerOrAllJarDescs[i].getVersion(), eagerOrAllJarDescs[i].isJavaFile() ? 0 : 1)) {
                    return false;
                }
            }
        }
        catch (JNLPException ex) {
            Trace.ignoredException((Exception)ex);
            return false;
        }
        catch (IOException ex2) {
            Trace.ignoredException((Exception)ex2);
            return false;
        }
        return true;
    }
    
    private static void updateCheck(final URL url, final String s, final int n, final boolean b) {
        synchronized (LaunchDownload.syncObj) {
            ++LaunchDownload.numThread;
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    final boolean updateAvailable = DownloadProtocol.isUpdateAvailable(url, s, n);
                    if (updateAvailable && b) {
                        final File file = DownloadProtocol.getCachedVersion(url, s, n).getFile();
                        if (file != null) {
                            file.delete();
                        }
                    }
                    synchronized (LaunchDownload.syncObj) {
                        if (updateAvailable && !LaunchDownload.updateAvailable) {
                            LaunchDownload.updateAvailable = true;
                        }
                    }
                }
                catch (JNLPException ex) {
                    Trace.ignoredException((Exception)ex);
                    synchronized (LaunchDownload.syncObj) {
                        LaunchDownload.numThread--;
                    }
                }
                finally {
                    synchronized (LaunchDownload.syncObj) {
                        LaunchDownload.numThread--;
                    }
                }
            }
        }).start();
    }
    
    public static boolean isUpdateAvailable(final LaunchDesc launchDesc) throws JNLPException {
        final URL location = launchDesc.getLocation();
        if (location != null && DownloadProtocol.isLaunchFileUpdateAvailable(location)) {
            return true;
        }
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return false;
        }
        final ExtensionDesc[] extensionDescs = resources.getExtensionDescs();
        for (int i = 0; i < extensionDescs.length; ++i) {
            final URL location2 = extensionDescs[i].getLocation();
            if (location2 != null) {
                updateCheck(location2, extensionDescs[i].getVersion(), 4, false);
            }
        }
        final JARDesc[] eagerOrAllJarDescs = resources.getEagerOrAllJarDescs(true);
        for (int j = 0; j < eagerOrAllJarDescs.length; ++j) {
            final URL location3 = eagerOrAllJarDescs[j].getLocation();
            final String version = eagerOrAllJarDescs[j].getVersion();
            final int n = eagerOrAllJarDescs[j].isJavaFile() ? 0 : 1;
            if (DownloadProtocol.isInCache(location3, version, n)) {
                updateCheck(location3, version, n, eagerOrAllJarDescs[j].isLazyDownload());
            }
        }
        final IconDesc[] icons = launchDesc.getInformation().getIcons();
        if (icons != null) {
            for (int k = 0; k < icons.length; ++k) {
                final URL location4 = icons[k].getLocation();
                final String version2 = icons[k].getVersion();
                final int n2 = 2;
                if (DownloadProtocol.isInCache(location4, version2, n2)) {
                    updateCheck(location4, version2, n2, false);
                }
            }
        }
        while (LaunchDownload.numThread > 0) {
            synchronized (LaunchDownload.syncObj) {
                if (LaunchDownload.updateAvailable) {
                    break;
                }
                continue;
            }
        }
        return LaunchDownload.updateAvailable;
    }
    
    public static File[] getNativeDirectories(final LaunchDesc launchDesc) {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return new File[0];
        }
        final JARDesc[] eagerOrAllJarDescs = resources.getEagerOrAllJarDescs(true);
        final ArrayList list = new ArrayList<File>();
        for (int i = 0; i < eagerOrAllJarDescs.length; ++i) {
            if (eagerOrAllJarDescs[i].isNativeLib()) {
                final DiskCacheEntry cachedVersion = DownloadProtocol.getCachedVersion(eagerOrAllJarDescs[i].getLocation(), eagerOrAllJarDescs[i].getVersion(), 1);
                if (cachedVersion != null) {
                    list.add(cachedVersion.getDirectory());
                }
            }
        }
        return list.toArray(new File[list.size()]);
    }
    
    static void downloadExtensions(final LaunchDesc launchDesc, final DownloadProgress downloadProgress, final int n, final ArrayList list) throws IOException, JNLPException {
        downloadExtensionsHelper(launchDesc, downloadProgress, n, false, list);
    }
    
    private static boolean getCachedExtensions(final LaunchDesc launchDesc) throws IOException, JNLPException {
        return downloadExtensionsHelper(launchDesc, null, 0, true, null);
    }
    
    private static boolean downloadExtensionsHelper(final LaunchDesc launchDesc, final DownloadProgress downloadProgress, int n, final boolean b, final ArrayList list) throws IOException, JNLPException {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return true;
        }
        final String knownPlatforms = JREInfo.getKnownPlatforms();
        final ArrayList list2 = new ArrayList<ExtensionDesc>();
        resources.visit(new ResourceVisitor() {
            private final /* synthetic */ ArrayList val$list = list2;
            
            public void visitJARDesc(final JARDesc jarDesc) {
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                this.val$list.add(extensionDesc);
            }
        });
        n += list2.size();
        for (int i = 0; i < list2.size(); ++i) {
            final ExtensionDesc extensionDesc = list2.get(i);
            String s = extensionDesc.getName();
            if (s == null) {
                s = extensionDesc.getLocation().toString();
                final int lastIndex = s.lastIndexOf(47);
                if (lastIndex > 0) {
                    s = s.substring(lastIndex + 1, s.length());
                }
            }
            --n;
            if (downloadProgress != null) {
                downloadProgress.extensionDownload(s, n);
            }
            DiskCacheEntry diskCacheEntry;
            if (!b) {
                diskCacheEntry = DownloadProtocol.getExtension(extensionDesc.getLocation(), extensionDesc.getVersion(), knownPlatforms, false);
            }
            else {
                diskCacheEntry = DownloadProtocol.getCachedExtension(extensionDesc.getLocation(), extensionDesc.getVersion(), knownPlatforms);
                if (diskCacheEntry == null) {
                    return false;
                }
            }
            Trace.println("Downloaded extension: " + extensionDesc.getLocation() + ": " + diskCacheEntry.getFile(), TraceLevel.NETWORK);
            final LaunchDesc buildDescriptor = LaunchDescFactory.buildDescriptor(diskCacheEntry.getFile());
            boolean b2;
            if (buildDescriptor.getLaunchType() == 3) {
                b2 = true;
            }
            else {
                if (buildDescriptor.getLaunchType() != 4) {
                    throw new MissingFieldException(buildDescriptor.getSource(), "<component-desc>|<installer-desc>");
                }
                extensionDesc.setInstaller(true);
                b2 = !Cache.getLocalApplicationProperties(diskCacheEntry.getLocation(), diskCacheEntry.getVersionId(), launchDesc, false).isLocallyInstalled();
                if (list != null && (isUpdateAvailable(buildDescriptor) || b2)) {
                    list.add(diskCacheEntry.getFile());
                }
                if (b && b2) {
                    return false;
                }
            }
            if (b2) {
                extensionDesc.setExtensionDesc(buildDescriptor);
                if (!downloadExtensionsHelper(buildDescriptor, downloadProgress, n, b, list)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static void downloadJRE(final LaunchDesc launchDesc, final DownloadProgress downloadProgress, final ArrayList list) throws JNLPException, IOException {
        final JREDesc selectedJRE = launchDesc.getResources().getSelectedJRE();
        final String version = selectedJRE.getVersion();
        URL href = selectedJRE.getHref();
        final boolean b = href == null;
        if (href == null) {
            final String property = Config.getProperty("deployment.javaws.installURL");
            if (property != null) {
                try {
                    href = new URL(property);
                }
                catch (MalformedURLException ex2) {}
            }
        }
        if (downloadProgress != null) {
            downloadProgress.jreDownload(version, href);
        }
        final String knownPlatforms = JREInfo.getKnownPlatforms();
        DiskCacheEntry jre;
        try {
            jre = DownloadProtocol.getJRE(href, version, b, knownPlatforms);
        }
        catch (ErrorCodeResponseException ex) {
            ex.setJreDownload(true);
            throw ex;
        }
        final LaunchDesc buildDescriptor = LaunchDescFactory.buildDescriptor(jre.getFile());
        if (buildDescriptor.getLaunchType() != 4) {
            throw new MissingFieldException(buildDescriptor.getSource(), "<installer-desc>");
        }
        if (list != null) {
            list.add(jre.getFile());
        }
        selectedJRE.setExtensionDesc(buildDescriptor);
        downloadExtensionsHelper(buildDescriptor, downloadProgress, 0, false, list);
    }
    
    public static void downloadResource(final LaunchDesc launchDesc, final URL url, final String s, final DownloadProgress downloadProgress, final boolean b) throws IOException, JNLPException {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return;
        }
        downloadJarFiles(resources.getResource(url, s), downloadProgress, b);
    }
    
    public static void downloadParts(final LaunchDesc launchDesc, final String[] array, final DownloadProgress downloadProgress, final boolean b) throws IOException, JNLPException {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return;
        }
        downloadJarFiles(resources.getPartJars(array), downloadProgress, b);
    }
    
    public static void downloadExtensionPart(final LaunchDesc launchDesc, final URL url, final String s, final String[] array, final DownloadProgress downloadProgress, final boolean b) throws IOException, JNLPException {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return;
        }
        downloadJarFiles(resources.getExtensionPart(url, s, array), downloadProgress, b);
    }
    
    public static void downloadEagerorAll(final LaunchDesc launchDesc, final boolean b, final DownloadProgress downloadProgress, final boolean b2) throws IOException, JNLPException {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return;
        }
        JARDesc[] eagerOrAllJarDescs = resources.getEagerOrAllJarDescs(b);
        if (!b) {
            final JARDesc[] eagerOrAllJarDescs2 = resources.getEagerOrAllJarDescs(true);
            if (eagerOrAllJarDescs2.length != eagerOrAllJarDescs.length) {
                final HashSet set = new HashSet(Arrays.asList(eagerOrAllJarDescs));
                int n = 0;
                for (int i = 0; i < eagerOrAllJarDescs2.length; ++i) {
                    final URL location = eagerOrAllJarDescs2[i].getLocation();
                    final String version = eagerOrAllJarDescs2[i].getVersion();
                    final int n2 = eagerOrAllJarDescs2[i].isJavaFile() ? 0 : 1;
                    if (!set.contains(eagerOrAllJarDescs2[i]) && DownloadProtocol.isInCache(location, version, n2)) {
                        ++n;
                    }
                    else {
                        eagerOrAllJarDescs2[i] = null;
                    }
                }
                if (n > 0) {
                    final JARDesc[] array = new JARDesc[eagerOrAllJarDescs.length + n];
                    System.arraycopy(eagerOrAllJarDescs, 0, array, 0, eagerOrAllJarDescs.length);
                    int length = eagerOrAllJarDescs.length;
                    for (int j = 0; j < eagerOrAllJarDescs2.length; ++j) {
                        if (eagerOrAllJarDescs2[j] != null) {
                            array[length++] = eagerOrAllJarDescs2[j];
                        }
                    }
                    eagerOrAllJarDescs = array;
                }
            }
        }
        downloadJarFiles(eagerOrAllJarDescs, downloadProgress, b2);
    }
    
    private static void downloadJarFiles(final JARDesc[] array, final DownloadProgress downloadProgress, final boolean b) throws JNLPException, IOException {
        if (array == null) {
            return;
        }
        Trace.println("Contacting server for JAR file sizes", TraceLevel.NETWORK);
        long n = 0L;
        for (int n2 = 0; n2 < array.length && n != -1L; ++n2) {
            try {
                final JARDesc jarDesc = array[n2];
                final int nativeLib = jarDesc.isNativeLib() ? 1 : 0;
                long downloadSize = jarDesc.getSize();
                if (downloadSize == 0L) {
                    downloadSize = DownloadProtocol.getDownloadSize(array[n2].getLocation(), array[n2].getVersion(), nativeLib);
                }
                Trace.println("Size of " + array[n2].getLocation() + ": " + downloadSize, TraceLevel.NETWORK);
                if (downloadSize == -1L) {
                    n = -1L;
                }
                else {
                    n += downloadSize;
                }
            }
            catch (JNLPException ex) {
                if (downloadProgress != null) {
                    downloadProgress.downloadFailed(array[n2].getLocation(), array[n2].getVersion());
                }
                throw ex;
            }
        }
        Trace.println("Total size to download: " + n, TraceLevel.NETWORK);
        if (n == 0L) {
            return;
        }
        final DownloadCallbackHelper downloadCallbackHelper = new DownloadCallbackHelper(downloadProgress, n);
        for (int i = 0; i < array.length; ++i) {
            final JARDesc jarDesc2 = array[i];
            try {
                final DiskCacheEntry resource = DownloadProtocol.getResource(array[i].getLocation(), array[i].getVersion(), jarDesc2.isNativeLib() ? 1 : 0, b, downloadCallbackHelper);
                Trace.println("Downloaded " + array[i].getLocation() + ": " + resource, TraceLevel.NETWORK);
                if (resource == null) {
                    throw new FailedDownloadingResourceException(null, array[i].getLocation(), array[i].getVersion(), null);
                }
            }
            catch (JNLPException ex2) {
                if (downloadProgress != null) {
                    downloadProgress.downloadFailed(jarDesc2.getLocation(), jarDesc2.getVersion());
                }
                throw ex2;
            }
        }
    }
    
    static void checkJNLPSecurity(final LaunchDesc launchDesc) throws MultipleHostsException, NativeLibViolationException {
        final boolean[] array = { false };
        final boolean[] array2 = { false };
        if (launchDesc.getResources() == null) {
            return;
        }
        final JARDesc mainJar = launchDesc.getResources().getMainJar(true);
        if (mainJar == null) {
            return;
        }
        checkJNLPSecurityHelper(launchDesc, mainJar.getLocation().getHost(), array2, array);
        if (array2[0]) {
            throw new MultipleHostsException();
        }
        if (array[0]) {
            throw new NativeLibViolationException();
        }
    }
    
    private static void checkJNLPSecurityHelper(final LaunchDesc launchDesc, final String s, final boolean[] array, final boolean[] array2) {
        if (launchDesc.getSecurityModel() != 0) {
            return;
        }
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return;
        }
        resources.visit(new ResourceVisitor() {
            public void visitJARDesc(final JARDesc jarDesc) {
                final String host = jarDesc.getLocation().getHost();
                array[0] = (array[0] || !s.equals(host));
                array2[0] = (array2[0] || jarDesc.isNativeLib());
            }
            
            public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                if (!array[0] && !array2[0]) {
                    final LaunchDesc extensionDesc2 = extensionDesc.getExtensionDesc();
                    if (extensionDesc2 != null && extensionDesc2.getSecurityModel() == 0) {
                        final String host = extensionDesc.getLocation().getHost();
                        if (!(array[0] = (array[0] || !s.equals(host)))) {
                            checkJNLPSecurityHelper(extensionDesc2, s, array, array2);
                        }
                    }
                }
            }
            
            public void visitPropertyDesc(final PropertyDesc propertyDesc) {
            }
            
            public void visitPackageDesc(final PackageDesc packageDesc) {
            }
            
            public void visitJREDesc(final JREDesc jreDesc) {
            }
        });
    }
    
    public static long getCachedSize(final LaunchDesc launchDesc) {
        long n = 0L;
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return n;
        }
        final JARDesc[] eagerOrAllJarDescs = resources.getEagerOrAllJarDescs(true);
        for (int i = 0; i < eagerOrAllJarDescs.length; ++i) {
            eagerOrAllJarDescs[i].isNativeLib();
            n += DownloadProtocol.getCachedSize(eagerOrAllJarDescs[i].getLocation(), eagerOrAllJarDescs[i].getVersion(), 0);
        }
        return n;
    }
    
    static String getMainClassName(final LaunchDesc launchDesc, final boolean b) throws IOException, JNLPException, LaunchDescException {
        String s = null;
        final ApplicationDesc applicationDescriptor = launchDesc.getApplicationDescriptor();
        if (applicationDescriptor != null) {
            s = applicationDescriptor.getMainClass();
        }
        final InstallerDesc installerDescriptor = launchDesc.getInstallerDescriptor();
        if (installerDescriptor != null) {
            s = installerDescriptor.getMainClass();
        }
        final AppletDesc appletDescriptor = launchDesc.getAppletDescriptor();
        if (appletDescriptor != null) {
            s = appletDescriptor.getAppletClass();
        }
        if (s != null && s.length() == 0) {
            s = null;
        }
        if (launchDesc.getResources() == null) {
            return null;
        }
        final JARDesc mainJar = launchDesc.getResources().getMainJar(b);
        if (mainJar == null) {
            return null;
        }
        final JarFile jarFile = new JarFile(DownloadProtocol.getResource(mainJar.getLocation(), mainJar.getVersion(), 0, true, null).getFile());
        if (s == null && launchDesc.getLaunchType() != 2) {
            final Manifest manifest = jarFile.getManifest();
            s = ((manifest != null) ? manifest.getMainAttributes().getValue("Main-Class") : null);
        }
        if (s == null) {
            throw new LaunchDescException(launchDesc, ResourceManager.getString("launch.error.nomainclassspec"), null);
        }
        if (jarFile.getEntry(s.replace('.', '/') + ".class") == null) {
            throw new LaunchDescException(launchDesc, ResourceManager.getString("launch.error.nomainclass", s, mainJar.getLocation().toString()), null);
        }
        return s;
    }
    
    static void checkSignedLaunchDesc(final LaunchDesc launchDesc) throws IOException, JNLPException {
        final ArrayList<LaunchDesc> list = new ArrayList<LaunchDesc>();
        addExtensions(list, launchDesc);
        for (int i = 0; i < list.size(); ++i) {
            checkSignedLaunchDescHelper(list.get(i));
        }
    }
    
    static void checkSignedResources(final LaunchDesc launchDesc) throws IOException, JNLPException {
        final ArrayList<LaunchDesc> list = new ArrayList<LaunchDesc>();
        addExtensions(list, launchDesc);
        for (int i = 0; i < list.size(); ++i) {
            checkSignedResourcesHelper(list.get(i));
        }
    }
    
    private static void addExtensions(final ArrayList list, final LaunchDesc launchDesc) {
        list.add(launchDesc);
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources != null) {
            resources.visit(new ResourceVisitor() {
                public void visitJARDesc(final JARDesc jarDesc) {
                }
                
                public void visitPropertyDesc(final PropertyDesc propertyDesc) {
                }
                
                public void visitPackageDesc(final PackageDesc packageDesc) {
                }
                
                public void visitJREDesc(final JREDesc jreDesc) {
                }
                
                public void visitExtensionDesc(final ExtensionDesc extensionDesc) {
                    if (!extensionDesc.isInstaller()) {
                        addExtensions(list, extensionDesc.getExtensionDesc());
                    }
                }
            });
        }
    }
    
    private static void checkSignedLaunchDescHelper(final LaunchDesc launchDesc) throws IOException, JNLPException {
        final boolean applicationDescriptor = launchDesc.isApplicationDescriptor();
        try {
            final byte[] signedJNLPFile = getSignedJNLPFile(launchDesc, applicationDescriptor);
            if (signedJNLPFile != null) {
                final LaunchDesc buildDescriptor = LaunchDescFactory.buildDescriptor(signedJNLPFile);
                Trace.println("Signed JNLP file: ", TraceLevel.BASIC);
                Trace.println(buildDescriptor.toString(), TraceLevel.BASIC);
                launchDesc.checkSigning(buildDescriptor);
            }
        }
        catch (LaunchDescException ex) {
            ex.setIsSignedLaunchDesc();
            throw ex;
        }
        catch (IOException ex2) {
            throw ex2;
        }
        catch (JNLPException ex3) {
            throw ex3;
        }
    }
    
    private static void checkSignedResourcesHelper(final LaunchDesc launchDesc) throws IOException, JNLPException {
        final ResourcesDesc resources = launchDesc.getResources();
        if (resources == null) {
            return;
        }
        final JARDesc[] localJarDescs = resources.getLocalJarDescs();
        boolean b = true;
        Certificate[] array = null;
        CodeSource codeSource = null;
        final URL canonicalHome = launchDesc.getCanonicalHome();
        int n = 0;
        URL location = null;
        for (int i = 0; i < localJarDescs.length; ++i) {
            final JARDesc jarDesc = localJarDescs[i];
            final DiskCacheEntry cachedVersion = DownloadProtocol.getCachedVersion(jarDesc.getLocation(), jarDesc.getVersion(), jarDesc.isJavaFile() ? 0 : 1);
            if (cachedVersion != null) {
                ++n;
                final CodeSource codeSource2 = SigningInfo.getCodeSource(canonicalHome, new JarFile(cachedVersion.getFile()));
                if (codeSource2 != null) {
                    final Certificate[] certificates = codeSource2.getCertificates();
                    if (certificates == null) {
                        Trace.println("getCertChain returned null for: " + cachedVersion.getFile(), TraceLevel.BASIC);
                        b = false;
                        location = jarDesc.getLocation();
                    }
                    if (array == null) {
                        array = certificates;
                        codeSource = codeSource2;
                    }
                    else if (certificates != null && !SigningInfo.equalChains(array, certificates)) {
                        throw new LaunchDescException(launchDesc, ResourceManager.getString("launch.error.singlecertviolation"), null);
                    }
                }
            }
        }
        if (launchDesc.getSecurityModel() != 0) {
            if (!b) {
                throw new UnsignedAccessViolationException(launchDesc, location, true);
            }
            if (n > 0) {
                AppPolicy.getInstance().grantUnrestrictedAccess(launchDesc, codeSource);
            }
        }
    }
    
    private static byte[] getSignedJNLPFile(final LaunchDesc launchDesc, final boolean b) throws IOException, JNLPException {
        if (launchDesc.getResources() == null) {
            return null;
        }
        final JARDesc mainJar = launchDesc.getResources().getMainJar(b);
        if (mainJar == null) {
            return null;
        }
        final JarFile jarFile = new JarFile(DownloadProtocol.getResource(mainJar.getLocation(), mainJar.getVersion(), 0, true, null).getFile());
        JarEntry jarEntry = jarFile.getJarEntry("JNLP-INF/APPLICATION.JNLP");
        if (jarEntry == null) {
            JarEntry jarEntry2;
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements() && jarEntry == null; jarEntry = jarEntry2) {
                jarEntry2 = entries.nextElement();
                if (jarEntry2.getName().equalsIgnoreCase("JNLP-INF/APPLICATION.JNLP")) {}
            }
        }
        if (jarEntry == null) {
            if (jarFile != null) {
                jarFile.close();
            }
            return null;
        }
        final byte[] array = new byte[(int)jarEntry.getSize()];
        final DataInputStream dataInputStream = new DataInputStream(jarFile.getInputStream(jarEntry));
        dataInputStream.readFully(array, 0, (int)jarEntry.getSize());
        dataInputStream.close();
        jarFile.close();
        return array;
    }
    
    static {
        LaunchDownload.updateAvailable = false;
        LaunchDownload.numThread = 0;
        LaunchDownload.syncObj = new Object();
    }
    
    private static class DownloadCallbackHelper implements DownloadProtocol.DownloadDelegate
    {
        DownloadProgress _downloadProgress;
        long _totalSize;
        long _downloadedSoFar;
        long _currentTotal;
        
        public DownloadCallbackHelper(final DownloadProgress downloadProgress, final long totalSize) {
            this._downloadProgress = downloadProgress;
            this._totalSize = totalSize;
            this._downloadedSoFar = 0L;
        }
        
        public void downloading(final URL url, final String s, final int n, final int n2, final boolean b) {
            int percent = -1;
            if (this._totalSize != -1L) {
                percent = this.getPercent(this._downloadedSoFar + (b ? 0.8 : 0.9) * n);
                this._currentTotal = n2;
            }
            if (this._downloadProgress != null) {
                this._downloadProgress.progress(url, s, this._downloadedSoFar + n, this._totalSize, percent);
            }
        }
        
        public void patching(final URL url, final String s, final int n) {
            int percent = -1;
            if (this._totalSize != -1L) {
                percent = this.getPercent(this._downloadedSoFar + this._currentTotal * (0.8 + n / 1000.0));
            }
            if (this._downloadProgress != null) {
                this._downloadProgress.patching(url, s, n, percent);
            }
        }
        
        public void validating(final URL url, final int n, final int n2) {
            int percent = -1;
            if (this._totalSize != -1L && n2 != 0) {
                percent = this.getPercent(this._downloadedSoFar + 0.9 * this._currentTotal + 0.1 * this._currentTotal * (n / n2));
            }
            if (this._downloadProgress != null) {
                this._downloadProgress.validating(url, null, n, n2, percent);
            }
            if (n == n2) {
                this._downloadedSoFar += this._currentTotal;
            }
        }
        
        public void downloadFailed(final URL url, final String s) {
            if (this._downloadProgress != null) {
                this._downloadProgress.downloadFailed(url, s);
            }
        }
        
        private int getPercent(final double n) {
            if (n > this._totalSize) {
                this._totalSize = -1L;
                return -1;
            }
            return (int)(n * 100.0 / this._totalSize + 0.5);
        }
    }
    
    public interface DownloadProgress
    {
        void jreDownload(final String p0, final URL p1);
        
        void extensionDownload(final String p0, final int p1);
        
        void progress(final URL p0, final String p1, final long p2, final long p3, final int p4);
        
        void validating(final URL p0, final String p1, final long p2, final long p3, final int p4);
        
        void patching(final URL p0, final String p1, final int p2, final int p3);
        
        void downloadFailed(final URL p0, final String p1);
    }
}
