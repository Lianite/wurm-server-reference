// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.util.Arrays;
import com.sun.javaws.util.VersionID;
import java.util.Comparator;
import java.util.Date;
import java.net.MalformedURLException;
import com.sun.javaws.jnl.RContentDesc;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.SplashScreen;
import com.sun.javaws.exceptions.ExitException;
import com.sun.deploy.config.JREInfo;
import com.sun.javaws.Launcher;
import com.sun.deploy.util.TraceLevel;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.net.URL;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import com.sun.javaws.LocalApplicationProperties;
import com.sun.javaws.jnl.LaunchDesc;
import java.util.Iterator;
import java.io.FilenameFilter;
import com.sun.javaws.jnl.LaunchDescFactory;
import com.sun.javaws.LocalInstallHandler;
import java.util.ArrayList;
import com.sun.deploy.config.Config;
import java.io.File;
import com.sun.deploy.util.Trace;
import com.sun.javaws.Globals;
import java.util.HashMap;

public class Cache
{
    public static final char RESOURCE_TYPE = 'R';
    public static final char APPLICATION_TYPE = 'A';
    public static final char EXTENSION_TYPE = 'E';
    public static final char MUFFIN_TYPE = 'P';
    public static final char MUFFIN_TAG_INDEX = '\0';
    public static final char MUFFIN_MAXSIZE_INDEX = '\u0001';
    private static DiskCache _activeCache;
    private static DiskCache _readOnlyCache;
    private static DiskCache _muffincache;
    private static final String LAST_ACCESSED_FILE_NAME = "lastAccessed";
    private static final String INDIRECT_EXTENSION = ".ind";
    private static HashMap _loadedProperties;
    private static final char[] cacheTypes;
    static /* synthetic */ Class class$com$sun$javaws$cache$Cache;
    
    private static void initialize() {
        final DiskCache activeCache = new DiskCache(getUserBaseDir());
        DiskCache diskCache = null;
        final File sysBaseDir = getSysBaseDir();
        if (sysBaseDir != null) {
            diskCache = new DiskCache(sysBaseDir);
        }
        Cache._muffincache = new DiskCache(getMuffinCacheBaseDir());
        Cache._loadedProperties = new HashMap();
        if (diskCache != null && Globals.isSystemCache()) {
            Cache._readOnlyCache = null;
            Cache._activeCache = diskCache;
        }
        else {
            Cache._readOnlyCache = diskCache;
            Cache._activeCache = activeCache;
            if (Globals.isSystemCache()) {
                Globals.setSystemCache(false);
                Trace.println("There is no system cache configured, \"-system\" option ignored");
            }
        }
    }
    
    public static boolean canWrite() {
        return Cache._activeCache.canWrite();
    }
    
    public static void updateCache() {
        final String property = Config.getProperty("deployment.javaws.cachedir");
        final String string = Config.getProperty("deployment.user.cachedir") + File.separator + "javaws";
        final File file = new File(property);
        final File file2 = new File(string);
        final Iterator orphans = Cache._activeCache.getOrphans();
        while (orphans.hasNext()) {
            final DiskCacheEntry diskCacheEntry = orphans.next();
        }
        final Iterator jnlpCacheEntries = Cache._activeCache.getJnlpCacheEntries();
        final ArrayList<DiskCacheEntry> list = new ArrayList<DiskCacheEntry>();
        final LocalInstallHandler instance = LocalInstallHandler.getInstance();
        while (jnlpCacheEntries.hasNext()) {
            final DiskCacheEntry diskCacheEntry2 = jnlpCacheEntries.next();
            try {
                final LaunchDesc buildDescriptor = LaunchDescFactory.buildDescriptor(diskCacheEntry2.getFile());
                final LocalApplicationProperties localApplicationProperties = getLocalApplicationProperties(diskCacheEntry2, buildDescriptor);
                if (localApplicationProperties == null || !localApplicationProperties.isLocallyInstalled()) {
                    continue;
                }
                instance.uninstall(buildDescriptor, localApplicationProperties, true);
                list.add(diskCacheEntry2);
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
        }
        if (!property.startsWith(string) && !string.startsWith(property) && file.exists() && file.isDirectory()) {
            copy(file, file2, new FilenameFilter() {
                public boolean accept(final File file, final String s) {
                    if (file.equals(file) || file.getParentFile().equals(file)) {
                        return !s.equals("splashes");
                    }
                    if (s.length() == 0) {
                        return false;
                    }
                    final char char1 = s.charAt(0);
                    for (int i = 0; i < Cache.cacheTypes.length; ++i) {
                        if (char1 == Cache.cacheTypes[i]) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        Config.setProperty("deployment.javaws.cachedir", (String)null);
        Config.storeIfDirty();
        Class class$;
        Class class$com$sun$javaws$cache$Cache;
        if (Cache.class$com$sun$javaws$cache$Cache == null) {
            class$com$sun$javaws$cache$Cache = (Cache.class$com$sun$javaws$cache$Cache = (class$ = class$("com.sun.javaws.cache.Cache")));
        }
        else {
            class$ = (class$com$sun$javaws$cache$Cache = Cache.class$com$sun$javaws$cache$Cache);
        }
        final Class clazz = class$com$sun$javaws$cache$Cache;
        synchronized (class$) {
            initialize();
        }
        final Iterator<DiskCacheEntry> iterator = list.iterator();
        while (iterator.hasNext()) {
            try {
                final DiskCacheEntry diskCacheEntry3 = iterator.next();
                final LaunchDesc buildDescriptor2 = LaunchDescFactory.buildDescriptor(diskCacheEntry3.getFile());
                instance.doInstall(buildDescriptor2, getLocalApplicationProperties(diskCacheEntry3, buildDescriptor2));
            }
            catch (Exception ex2) {
                Trace.ignoredException(ex2);
            }
        }
    }
    
    private static void copy(final File file, final File file2, final FilenameFilter filenameFilter) {
        if (file.isDirectory()) {
            file2.mkdirs();
            final File[] listFiles = file.listFiles(filenameFilter);
            for (int i = 0; i < listFiles.length; ++i) {
                copy(listFiles[i], new File(file2.getPath() + File.separator + listFiles[i].getName()), filenameFilter);
            }
        }
        else {
            final byte[] array = new byte[1024];
            FileOutputStream fileOutputStream = null;
            FileInputStream fileInputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file2);
                fileInputStream = new FileInputStream(file);
                while (true) {
                    final int read = fileInputStream.read(array);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(array, 0, read);
                }
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
            finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
                catch (Exception ex2) {}
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                }
                catch (Exception ex3) {}
            }
        }
    }
    
    public static void saveRemovedApp(final URL url, final String s) {
        final Properties removedApps = getRemovedApps();
        removedApps.setProperty(url.toString(), s);
        setRemovedApps(removedApps);
    }
    
    public static void setRemovedApps(final Properties properties) {
        try {
            properties.store(new FileOutputStream(getRemovePath()), "Removed JNLP Applications");
        }
        catch (IOException ex) {}
    }
    
    public static Properties getRemovedApps() {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(getRemovePath()));
        }
        catch (IOException ex) {}
        return properties;
    }
    
    public static String getRemovePath() {
        return Config.getJavawsCacheDir() + File.separator + "removed.apps";
    }
    
    private static File getMuffinCacheBaseDir() {
        final File file = new File(Config.getJavawsCacheDir() + File.separator + "muffins");
        if (!file.exists()) {
            file.mkdirs();
        }
        Trace.println("Muffin Cache = " + file, TraceLevel.CACHE);
        return file;
    }
    
    private static File getUserBaseDir() {
        final File file = new File(Config.getJavawsCacheDir());
        if (!file.exists()) {
            file.mkdirs();
        }
        Trace.println("User cache dir = " + file, TraceLevel.CACHE);
        return file;
    }
    
    private static File getSysBaseDir() {
        final String systemCacheDirectory = Config.getSystemCacheDirectory();
        if (systemCacheDirectory == null || systemCacheDirectory.length() == 0) {
            return null;
        }
        final File file = new File(systemCacheDirectory + File.separator + "javaws");
        if (!file.exists()) {
            file.mkdirs();
        }
        Trace.println("System cache dir = " + file, TraceLevel.CACHE);
        return file;
    }
    
    public static void remove() {
        final Iterator jnlpCacheEntries = Cache._activeCache.getJnlpCacheEntries();
        while (jnlpCacheEntries.hasNext()) {
            final DiskCacheEntry diskCacheEntry = jnlpCacheEntries.next();
            LaunchDesc buildDescriptor = null;
            try {
                buildDescriptor = LaunchDescFactory.buildDescriptor(diskCacheEntry.getFile());
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
            if (buildDescriptor != null) {
                remove(diskCacheEntry, getLocalApplicationProperties(diskCacheEntry, buildDescriptor), buildDescriptor);
            }
        }
        uninstallActiveCache();
        uninstallMuffinCache();
    }
    
    public static void remove(final String s, final LocalApplicationProperties localApplicationProperties, final LaunchDesc launchDesc) {
        try {
            remove(getCacheEntryFromFile(new File(s)), localApplicationProperties, launchDesc);
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
        }
    }
    
    public static void remove(final DiskCacheEntry diskCacheEntry, final LocalApplicationProperties localApplicationProperties, final LaunchDesc launchDesc) {
        final InformationDesc information = launchDesc.getInformation();
        final LocalInstallHandler instance = LocalInstallHandler.getInstance();
        if (launchDesc.isApplicationDescriptor() && launchDesc.getLocation() != null) {
            saveRemovedApp(launchDesc.getLocation(), information.getTitle());
        }
        localApplicationProperties.refresh();
        if (localApplicationProperties.isLocallyInstalled()) {
            if (launchDesc.isApplicationDescriptor()) {
                if (instance != null) {
                    instance.uninstall(launchDesc, localApplicationProperties, true);
                }
            }
            else if (launchDesc.isInstaller()) {
                final ArrayList<File> list = new ArrayList<File>();
                list.add(diskCacheEntry.getFile());
                try {
                    final String installDirectory = localApplicationProperties.getInstallDirectory();
                    Launcher.executeUninstallers(list);
                    JREInfo.removeJREsIn(installDirectory);
                    deleteFile(new File(installDirectory));
                }
                catch (ExitException ex) {
                    Trace.ignoredException((Exception)ex);
                }
            }
        }
        Config.getInstance().addRemoveProgramsRemove(localApplicationProperties.getRegisteredTitle(), Globals.isSystemCache());
        instance.removeAssociations(launchDesc, localApplicationProperties);
        SplashScreen.removeCustomSplash(launchDesc);
        if (information != null) {
            final IconDesc[] icons = information.getIcons();
            if (icons != null) {
                for (int i = 0; i < icons.length; ++i) {
                    removeEntries('R', icons[i].getLocation(), icons[i].getVersion());
                }
            }
            final RContentDesc[] relatedContent = information.getRelatedContent();
            if (relatedContent != null) {
                for (int j = 0; j < relatedContent.length; ++j) {
                    final URL icon = relatedContent[j].getIcon();
                    if (icon != null) {
                        removeEntries('R', icon, null);
                    }
                }
            }
        }
        final URL canonicalHome = launchDesc.getCanonicalHome();
        if (canonicalHome != null) {
            removeEntries('A', canonicalHome, null);
            removeEntries('E', canonicalHome, null);
        }
        if (diskCacheEntry != null) {
            removeEntry(diskCacheEntry);
        }
    }
    
    private static void deleteFile(final File file) {
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; ++i) {
                    deleteFile(listFiles[i]);
                }
            }
        }
        file.delete();
    }
    
    private static void removeEntries(final char c, final URL url, final String s) {
        if (url == null) {
            return;
        }
        try {
            final DiskCacheEntry[] cacheEntries = getCacheEntries(c, url, s, true);
            for (int i = 0; i < cacheEntries.length; ++i) {
                removeEntry(cacheEntries[i]);
            }
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
    }
    
    public static File getCachedLaunchedFile(final URL url) throws IOException {
        final DiskCacheEntry cacheEntry = getCacheEntry('A', url, null);
        return (cacheEntry == null) ? null : cacheEntry.getFile();
    }
    
    public static File getCachedFile(final URL url) {
        File cachedLaunchedFile = null;
        if (url.getProtocol().equals("jar")) {
            final String path = url.getPath();
            final int index = path.indexOf("!/");
            if (index > 0) {
                try {
                    return new File(createNativeLibDir(new URL(path.substring(0, index)), null), path.substring(index + 2));
                }
                catch (MalformedURLException ex) {
                    Trace.ignoredException((Exception)ex);
                }
                catch (IOException ex2) {
                    Trace.ignoredException((Exception)ex2);
                }
            }
            return null;
        }
        if (url.toString().endsWith(".jnlp")) {
            try {
                cachedLaunchedFile = getCachedLaunchedFile(url);
            }
            catch (IOException ex3) {
                Trace.ignoredException((Exception)ex3);
            }
        }
        return cachedLaunchedFile;
    }
    
    public static LocalApplicationProperties getLocalApplicationProperties(final DiskCacheEntry diskCacheEntry, final LaunchDesc launchDesc) {
        return getLocalApplicationProperties(diskCacheEntry.getLocation(), diskCacheEntry.getVersionId(), launchDesc, diskCacheEntry.getType() == 'A');
    }
    
    public static LocalApplicationProperties getLocalApplicationProperties(final String s, final LaunchDesc launchDesc) {
        final DiskCacheEntry cacheEntryFromFile = getCacheEntryFromFile(new File(s));
        if (cacheEntryFromFile == null) {
            return null;
        }
        return getLocalApplicationProperties(cacheEntryFromFile.getLocation(), cacheEntryFromFile.getVersionId(), launchDesc, cacheEntryFromFile.getType() == 'A');
    }
    
    public static LocalApplicationProperties getLocalApplicationProperties(final URL url, final LaunchDesc launchDesc) {
        return getLocalApplicationProperties(url, null, launchDesc, true);
    }
    
    public static LocalApplicationProperties getLocalApplicationProperties(final URL url, final String s, final LaunchDesc launchDesc, final boolean b) {
        if (url == null) {
            return null;
        }
        final String string = url.toString().intern() + "?" + s;
        LocalApplicationProperties localApplicationProperties;
        synchronized (Cache._loadedProperties) {
            localApplicationProperties = Cache._loadedProperties.get(string);
            if (localApplicationProperties == null) {
                localApplicationProperties = new DefaultLocalApplicationProperties(url, s, launchDesc, b);
                Cache._loadedProperties.put(string, localApplicationProperties);
            }
            else {
                localApplicationProperties.refreshIfNecessary();
            }
        }
        return localApplicationProperties;
    }
    
    public static LaunchDesc getLaunchDesc(final URL url, final String s) {
        try {
            final DiskCacheEntry cacheEntry = getCacheEntry('A', url, s);
            if (cacheEntry != null) {
                try {
                    return LaunchDescFactory.buildDescriptor(cacheEntry.getFile());
                }
                catch (Exception ex2) {
                    return null;
                }
            }
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        return null;
    }
    
    public static String getNewExtensionInstallDirectory() throws IOException {
        final String string = getUserBaseDir().getAbsolutePath() + File.separator + "ext";
        int n = 0;
        String string2;
        do {
            string2 = string + File.separator + "E" + new Date().getTime() + File.separator;
            if (!new File(string2).mkdirs()) {
                string2 = null;
            }
            Thread.yield();
        } while (string2 == null && ++n < 50);
        if (string2 == null) {
            throw new IOException("Unable to create temp. dir for extension");
        }
        return string2;
    }
    
    private static String createUniqueIndirectFile() throws IOException {
        final File file = new File(getUserBaseDir().getAbsolutePath() + File.separator + "indirect");
        file.mkdirs();
        return File.createTempFile("indirect", ".ind", file).getAbsolutePath();
    }
    
    public static void removeEntry(final DiskCacheEntry diskCacheEntry) {
        Cache._activeCache.removeEntry(diskCacheEntry);
    }
    
    public static long getLastAccessed(final boolean b) {
        if (!b) {
            return Cache._activeCache.getLastUpdate();
        }
        if (Cache._readOnlyCache == null) {
            return 0L;
        }
        return Cache._readOnlyCache.getLastUpdate();
    }
    
    public static long getLastAccessed() {
        return Cache._activeCache.getLastUpdate();
    }
    
    public static void setLastAccessed() {
        Cache._activeCache.recordLastUpdate();
    }
    
    public static String[] getBaseDirsForHost(final URL url) {
        String[] array;
        if (Cache._readOnlyCache == null) {
            array = new String[] { Cache._activeCache.getBaseDirForHost(url) };
        }
        else {
            array = new String[] { Cache._readOnlyCache.getBaseDirForHost(url), null };
            array[0] = Cache._activeCache.getBaseDirForHost(url);
        }
        return array;
    }
    
    public static long getCacheSize() throws IOException {
        return Cache._activeCache.getCacheSize();
    }
    
    public static void clean() {
        Cache._activeCache.cleanResources();
    }
    
    public static long getOrphanSize(final boolean b) {
        if (!b) {
            return Cache._activeCache.getOrphanSize();
        }
        if (Cache._readOnlyCache == null) {
            return 0L;
        }
        return Cache._readOnlyCache.getOrphanSize();
    }
    
    public static void cleanResources() {
        Cache._activeCache.cleanResources();
    }
    
    public static long getCacheSize(final boolean b) {
        try {
            if (!b) {
                return Cache._activeCache.getCacheSize();
            }
            if (Cache._readOnlyCache == null) {
                return -1L;
            }
            return Cache._readOnlyCache.getCacheSize();
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
            return 0L;
        }
    }
    
    public static String[] getCacheVersions(final char c, final URL url) throws IOException {
        final String[] cacheVersions = Cache._activeCache.getCacheVersions(c, url);
        final String[] array = new String[0];
        String[] array2 = cacheVersions;
        if (Cache._readOnlyCache != null) {
            final String[] cacheVersions2 = Cache._readOnlyCache.getCacheVersions(c, url);
            if (cacheVersions2.length > 0) {
                array2 = new String[cacheVersions.length + cacheVersions2.length];
                System.arraycopy(cacheVersions, 0, array2, 0, cacheVersions.length);
                System.arraycopy(cacheVersions2, 0, array2, cacheVersions.length, cacheVersions2.length);
            }
        }
        if (array2.length > 1) {
            Arrays.sort(array2, new Comparator() {
                public int compare(final Object o, final Object o2) {
                    return new VersionID((String)o).isGreaterThan(new VersionID((String)o2)) ? -1 : 1;
                }
            });
        }
        return array2;
    }
    
    public static DiskCacheEntry[] getCacheEntries(final char c, final URL url, final String s, final boolean b) throws IOException {
        final DiskCacheEntry[] cacheEntries = Cache._activeCache.getCacheEntries(c, url, s, b);
        DiskCacheEntry[] cacheEntries2 = new DiskCacheEntry[0];
        if (Cache._readOnlyCache != null) {
            cacheEntries2 = Cache._readOnlyCache.getCacheEntries(c, url, s, b);
        }
        if (cacheEntries2.length == 0) {
            return cacheEntries;
        }
        final DiskCacheEntry[] array = new DiskCacheEntry[cacheEntries2.length + cacheEntries.length];
        int i;
        for (i = 0; i < cacheEntries2.length; ++i) {
            array[i] = cacheEntries2[i];
        }
        for (int j = 0; j < cacheEntries.length; ++j) {
            array[i++] = cacheEntries[j];
        }
        return array;
    }
    
    public static DiskCacheEntry getMuffinCacheEntryFromFile(final File file) {
        return Cache._muffincache.getCacheEntryFromFile(file);
    }
    
    public static DiskCacheEntry getCacheEntryFromFile(final File file) {
        final DiskCacheEntry cacheEntryFromFile = Cache._activeCache.getCacheEntryFromFile(file);
        if (Cache._readOnlyCache != null) {
            final DiskCacheEntry cacheEntryFromFile2 = Cache._readOnlyCache.getCacheEntryFromFile(file);
            if (cacheEntryFromFile2 != null && cacheEntryFromFile2.newerThan(cacheEntryFromFile)) {
                return cacheEntryFromFile2;
            }
        }
        return cacheEntryFromFile;
    }
    
    public static File getTempCacheFile(final URL url, final String s) throws IOException {
        return Cache._activeCache.getTempCacheFile(url, s);
    }
    
    public static DiskCacheEntry getCacheEntry(final char c, final URL url, final String s) throws IOException {
        final DiskCacheEntry cacheEntry = Cache._activeCache.getCacheEntry(c, url, s);
        if (Cache._readOnlyCache != null) {
            final DiskCacheEntry cacheEntry2 = Cache._readOnlyCache.getCacheEntry(c, url, s);
            if (cacheEntry2 != null && cacheEntry2.newerThan(cacheEntry)) {
                return cacheEntry2;
            }
        }
        return cacheEntry;
    }
    
    public static File createNativeLibDir(final URL url, final String s) throws IOException {
        return Cache._activeCache.createNativeLibDir(url, s);
    }
    
    public static Iterator getJnlpCacheEntries(final boolean b) {
        if (!b) {
            return Cache._activeCache.getJnlpCacheEntries();
        }
        if (Cache._readOnlyCache == null) {
            return new ArrayList().iterator();
        }
        return Cache._readOnlyCache.getJnlpCacheEntries();
    }
    
    public static File putMappedImage(final URL url, final String s, final File file) throws IOException {
        return Cache._activeCache.putMappedImage(url, s, file);
    }
    
    public static byte[] getLapData(final char c, final URL url, final String s, final boolean b) throws IOException {
        if (b) {
            return (byte[])((Cache._readOnlyCache == null) ? null : Cache._readOnlyCache.getLapData(c, url, s));
        }
        return Cache._activeCache.getLapData(c, url, s);
    }
    
    public static void putLapData(final char c, final URL url, final String s, final byte[] array) throws IOException {
        Cache._activeCache.putLapData(c, url, s, array);
    }
    
    public static void insertEntry(final char c, final URL url, final String s, final File file, final long n) throws IOException {
        Cache._activeCache.insertEntry(c, url, s, file, n);
    }
    
    public static void putCanonicalLaunchDesc(final URL url, final LaunchDesc launchDesc) throws IOException {
        if (launchDesc.isApplicationDescriptor()) {
            final File tempCacheFile = getTempCacheFile(url, null);
            final FileOutputStream fileOutputStream = new FileOutputStream(tempCacheFile);
            try {
                fileOutputStream.write(launchDesc.getSource().getBytes());
            }
            finally {
                fileOutputStream.close();
            }
            insertEntry('A', url, null, tempCacheFile, new Date().getTime());
        }
    }
    
    public static void uninstallActiveCache() {
        Cache._activeCache.uninstallCache();
    }
    
    public static long getMuffinSize(final URL url) throws IOException {
        return Cache._muffincache.getMuffinSize(url);
    }
    
    public static long[] getMuffinAttributes(final URL url) throws IOException {
        return Cache._muffincache.getMuffinAttributes(url);
    }
    
    public static void putMuffinAttributes(final URL url, final int n, final long n2) throws IOException {
        Cache._muffincache.putMuffinAttributes(url, n, n2);
    }
    
    public static URL[] getAccessibleMuffins(final URL url) throws IOException {
        return Cache._muffincache.getAccessibleMuffins(url);
    }
    
    public static void insertMuffinEntry(final URL url, final File file, final int n, final long n2) throws IOException {
        Cache._muffincache.insertMuffinEntry(url, file, n, n2);
    }
    
    public static File getMuffinFileForURL(final URL url) {
        return Cache._muffincache.getMuffinFileForURL(url);
    }
    
    public static DiskCacheEntry getMuffinEntry(final char c, final URL url) throws IOException {
        return Cache._muffincache.getMuffinEntry(c, url);
    }
    
    public static boolean isMainMuffinFile(final File file) throws IOException {
        return Cache._muffincache.isMainMuffinFile(file);
    }
    
    public static void removeMuffinEntry(final DiskCacheEntry diskCacheEntry) {
        Cache._muffincache.removeMuffinEntry(diskCacheEntry);
    }
    
    public static void uninstallMuffinCache() {
        Cache._muffincache.uninstallCache();
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    static {
        Cache._activeCache = null;
        Cache._readOnlyCache = null;
        Cache._muffincache = null;
        initialize();
        cacheTypes = new char[] { 'D', 'X', 'V', 'I', 'R', 'A', 'E', 'P' };
    }
}
