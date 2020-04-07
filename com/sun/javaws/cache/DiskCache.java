// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.jnl.JARDesc;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.javaws.jnl.LaunchDescFactory;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.util.Arrays;
import com.sun.javaws.util.VersionID;
import java.util.Comparator;
import java.util.Iterator;
import com.sun.javaws.util.VersionString;
import java.util.Date;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;

public class DiskCache
{
    private static final int BUF_SIZE = 32768;
    static final char DIRECTORY_TYPE = 'D';
    static final char TEMP_TYPE = 'X';
    static final char VERSION_TYPE = 'V';
    static final char INDIRECT_TYPE = 'I';
    static final char RESOURCE_TYPE = 'R';
    static final char APPLICATION_TYPE = 'A';
    static final char EXTENSION_TYPE = 'E';
    static final char MUFFIN_TYPE = 'P';
    private File _baseDir;
    static final char MAIN_FILE_TAG = 'M';
    static final char NATIVELIB_FILE_TAG = 'N';
    static final char TIMESTAMP_FILE_TAG = 'T';
    static final char CERTIFICATE_FILE_TAG = 'C';
    static final char LAP_FILE_TAG = 'L';
    static final char MAPPED_IMAGE_FILE_TAG = 'B';
    static final char MUFFIN_ATTR_FILE_TAG = 'U';
    static final int MUFFIN_TAG_INDEX = 0;
    static final int MUFFIN_MAXSIZE_INDEX = 1;
    private static final String LAST_ACCESS_FILE = "lastAccessed";
    private static final String ORPHAN_LIST_FILE = "orphans";
    private static final String BEGIN_CERT_MARK = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT_MARK = "-----END CERTIFICATE-----";
    
    public DiskCache(final File baseDir) {
        this._baseDir = baseDir;
    }
    
    long getLastUpdate() {
        return new File(this._baseDir, "lastAccessed").lastModified();
    }
    
    void recordLastUpdate() {
        final File file = new File(this._baseDir, "lastAccessed");
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(46);
            fileOutputStream.close();
        }
        catch (IOException ex) {}
    }
    
    boolean canWrite() {
        final boolean canWrite = this._baseDir.canWrite();
        if (!canWrite) {
            Trace.println("Cannot write to cache: " + this._baseDir.getAbsolutePath(), TraceLevel.BASIC);
        }
        return canWrite;
    }
    
    String getBaseDirForHost(final URL url) {
        try {
            final String keyToFileLocation = this.keyToFileLocation('R', 'M', new URL(url.getProtocol(), url.getHost(), url.getPort(), ""), null);
            return keyToFileLocation.substring(0, keyToFileLocation.lastIndexOf(File.separator));
        }
        catch (MalformedURLException ex) {
            Trace.ignoredException((Exception)ex);
            return null;
        }
    }
    
    private void removeEmptyDirs(final URL url) {
        final String baseDirForHost = this.getBaseDirForHost(url);
        if (baseDirForHost != null) {
            this.removeEmptyDirs(new File(baseDirForHost));
        }
    }
    
    private void removeEmptyDirs(final File file) {
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            boolean b = false;
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; ++i) {
                    this.removeEmptyDirs(listFiles[i]);
                    if (listFiles[i].exists()) {
                        b = true;
                    }
                }
            }
            if (!b) {
                try {
                    file.delete();
                }
                catch (Exception ex) {
                    Trace.ignoredException(ex);
                }
            }
        }
    }
    
    private File getOrphanFileForHost(final URL url) {
        try {
            return new File(this.getBaseDirForHost(url), "orphans");
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
            return null;
        }
    }
    
    private void removeOrphans(final URL url) {
        final File orphanFileForHost = this.getOrphanFileForHost(url);
        if (orphanFileForHost != null && orphanFileForHost.exists()) {
            BufferedReader bufferedReader = null;
            PrintStream printStream = null;
            boolean b = false;
            final ArrayList list = new ArrayList<String>();
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(orphanFileForHost)));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    list.add(line);
                }
                for (int i = list.size() - 1; i >= 0; --i) {
                    final File file = new File(list.get(i));
                    file.delete();
                    if (!file.exists()) {
                        b = true;
                        list.remove(i);
                    }
                }
            }
            catch (IOException ex) {
                Trace.ignoredException((Exception)ex);
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    }
                    catch (IOException ex2) {
                        Trace.ignoredException((Exception)ex2);
                    }
                }
            }
            finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    }
                    catch (IOException ex3) {
                        Trace.ignoredException((Exception)ex3);
                    }
                }
            }
            if (b) {
                try {
                    if (list.isEmpty()) {
                        Trace.println("emptying orphans file", TraceLevel.CACHE);
                        orphanFileForHost.delete();
                    }
                    else {
                        printStream = new PrintStream(new FileOutputStream(orphanFileForHost));
                        for (int j = 0; j < list.size(); ++j) {
                            Trace.println("Remaining orphan: " + (Object)list.get(j), TraceLevel.CACHE);
                            printStream.println(list.get(j));
                        }
                    }
                }
                catch (Exception ex4) {
                    Trace.ignoredException(ex4);
                }
                finally {
                    if (printStream != null) {
                        printStream.close();
                    }
                }
            }
        }
    }
    
    private void addOrphan(final URL url, final File file) {
        Trace.println("addOrphan: " + file, TraceLevel.CACHE);
        final File orphanFileForHost = this.getOrphanFileForHost(url);
        PrintStream printStream = null;
        if (orphanFileForHost != null) {
            try {
                printStream = new PrintStream(new FileOutputStream(orphanFileForHost.getPath(), true));
                printStream.println(file.getCanonicalPath());
            }
            catch (Exception ex) {
                Trace.ignoredException(ex);
            }
            finally {
                if (printStream != null) {
                    printStream.close();
                }
            }
        }
    }
    
    File getTempCacheFile(final URL url, final String s) throws IOException {
        final File file = new File(this.keyToFileLocation('X', 'M', url, s));
        final File parentFile = file.getParentFile();
        parentFile.mkdirs();
        return File.createTempFile("java-" + file.getName(), "tmp", parentFile);
    }
    
    File createNativeLibDir(final URL url, final String s) throws IOException {
        final File fileFromCache = this.getFileFromCache('R', 'N', url, s, false);
        fileFromCache.mkdirs();
        return fileFromCache;
    }
    
    File getNativeLibDir(final URL url, final String s) throws IOException {
        return this.getFileFromCache('R', 'N', url, s, false);
    }
    
    void insertMuffinEntry(final URL url, final File file, final int n, final long n2) throws IOException {
        final File fileFromCache = this.getFileFromCache('P', 'M', url, null, false);
        if (fileFromCache.exists()) {
            file.delete();
            throw new IOException("insert failed in cache: target already exixts");
        }
        final File parentFile = fileFromCache.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        if (!file.renameTo(fileFromCache)) {
            throw new IOException("rename failed in cache");
        }
        this.putMuffinAttributes(url, n, n2);
    }
    
    long getMuffinSize(final URL url) throws IOException {
        long n = 0L;
        final File fileFromCache = this.getFileFromCache('P', 'M', url, null, true);
        if (fileFromCache != null && fileFromCache.exists()) {
            n += fileFromCache.length();
        }
        return n;
    }
    
    void insertEntry(final char c, final URL url, final String s, final File file, final long n) throws IOException {
        this.putTimeStamp(c, url, s, n);
        this.putFileInCache(c, 'M', url, s, file);
        this.recordLastUpdate();
    }
    
    File putMappedImage(URL url, final String s, final File file) throws IOException {
        if (file.getPath().endsWith(".ico")) {
            final String file2 = url.getFile();
            if (!file2.endsWith(".ico")) {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), file2 + ".ico");
            }
        }
        final File putFileInCache = this.putFileInCache('R', 'B', url, s, file);
        this.recordLastUpdate();
        return putFileInCache;
    }
    
    File getMappedImage(final char c, final char c2, URL url, final String s, final boolean b) throws IOException {
        File file = this.getFileFromCache(c, c2, url, s, b);
        if (file == null || !file.exists()) {
            final String file2 = url.getFile();
            if (!file2.endsWith(".ico")) {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), file2 + ".ico");
                file = this.getFileFromCache(c, c2, url, s, b);
            }
        }
        return file;
    }
    
    void putLaunchFile(final char c, final URL url, final String s, final String s2) throws IOException {
        this.storeAtomic(c, 'M', url, s, s2.getBytes("UTF8"));
        this.putTimeStamp(c, url, s, new Date().getTime());
    }
    
    String getLaunchFile(final char c, final URL url, final String s, final String s2) throws IOException {
        final byte[] entryContent = this.getEntryContent(c, 'M', url, s);
        if (entryContent == null) {
            return null;
        }
        return new String(entryContent, "UTF8");
    }
    
    void putMuffinAttributes(final URL url, final int n, final long n2) throws IOException {
        final PrintStream printStream = new PrintStream(this.getOutputStream('P', 'U', url, null));
        try {
            printStream.println(n);
            printStream.println(n2);
        }
        finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }
    
    void putTimeStamp(final char c, final URL url, final String s, long time) throws IOException {
        if (time == 0L) {
            time = new Date().getTime();
        }
        final PrintStream printStream = new PrintStream(this.getOutputStream(c, 'T', url, s));
        try {
            printStream.println(time);
            printStream.println("# " + new Date(time));
        }
        finally {
            printStream.close();
        }
    }
    
    long[] getMuffinAttributes(final URL url) throws IOException {
        BufferedReader bufferedReader = null;
        long n = -1L;
        long long1 = -1L;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(this.getInputStream('P', 'U', url, null)));
            final String line = bufferedReader.readLine();
            try {
                n = Integer.parseInt(line);
            }
            catch (NumberFormatException ex) {
                throw new IOException(ex.getMessage());
            }
            final String line2 = bufferedReader.readLine();
            try {
                long1 = Long.parseLong(line2);
            }
            catch (NumberFormatException ex2) {
                throw new IOException(ex2.getMessage());
            }
        }
        finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return new long[] { n, long1 };
    }
    
    long getTimeStamp(final char c, final URL url, final String s) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(this.getInputStream(c, 'T', url, s)));
            final String line = bufferedReader.readLine();
            try {
                return Long.parseLong(line);
            }
            catch (NumberFormatException ex2) {
                return 0L;
            }
        }
        catch (IOException ex3) {
            return 0L;
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (IOException ex) {
                Trace.ignoredException((Exception)ex);
            }
        }
    }
    
    DiskCacheEntry getMuffinEntry(final char c, final URL url) throws IOException {
        final File fileFromCache = this.getFileFromCache(c, 'M', url, null, true);
        if (fileFromCache == null) {
            return null;
        }
        return new DiskCacheEntry(c, url, null, fileFromCache, -1L, null, null, this.getFileFromCache(c, 'U', url, null, true));
    }
    
    DiskCacheEntry getCacheEntry(final char c, final URL url, final String s) throws IOException {
        final File fileFromCache = this.getFileFromCache(c, 'M', url, s, true);
        if (fileFromCache == null) {
            return null;
        }
        return new DiskCacheEntry(c, url, s, fileFromCache, this.getTimeStamp(c, url, s), this.getFileFromCache(c, 'N', url, s, true), this.getMappedImage(c, 'B', url, s, true), null);
    }
    
    DiskCacheEntry[] getCacheEntries(final char c, final URL url, final String s, final boolean b) throws IOException {
        if (s != null) {
            final ArrayList cacheEntries = this.getCacheEntries(c, url);
            final VersionString versionString = new VersionString(s);
            DiskCacheEntry diskCacheEntry = null;
            final Iterator<DiskCacheEntry> iterator = cacheEntries.iterator();
            while (iterator.hasNext()) {
                final DiskCacheEntry diskCacheEntry2 = iterator.next();
                final String versionId = diskCacheEntry2.getVersionId();
                if (versionId == null) {
                    iterator.remove();
                }
                else {
                    if (versionString.contains(versionId)) {
                        continue;
                    }
                    if (diskCacheEntry == null && versionString.containsGreaterThan(versionId)) {
                        diskCacheEntry = diskCacheEntry2;
                    }
                    iterator.remove();
                }
            }
            if (!b && cacheEntries.size() == 0 && diskCacheEntry != null) {
                cacheEntries.add(diskCacheEntry);
            }
            return cacheEntries.toArray(new DiskCacheEntry[cacheEntries.size()]);
        }
        final DiskCacheEntry cacheEntry = this.getCacheEntry(c, url, null);
        if (cacheEntry == null) {
            return new DiskCacheEntry[0];
        }
        return new DiskCacheEntry[] { cacheEntry };
    }
    
    void removeMuffinEntry(final DiskCacheEntry diskCacheEntry) {
        final char type = diskCacheEntry.getType();
        final URL location = diskCacheEntry.getLocation();
        final String versionId = diskCacheEntry.getVersionId();
        this.deleteEntry(type, 'M', location, versionId);
        this.deleteEntry(type, 'U', location, versionId);
    }
    
    void removeEntry(final DiskCacheEntry diskCacheEntry) {
        final char type = diskCacheEntry.getType();
        final URL location = diskCacheEntry.getLocation();
        this.removeOrphans(location);
        final String versionId = diskCacheEntry.getVersionId();
        this.deleteEntry(type, 'M', location, versionId);
        this.deleteEntry(type, 'T', location, versionId);
        this.deleteEntry(type, 'C', location, versionId);
        this.deleteEntry(type, 'N', location, versionId);
        this.deleteEntry(type, 'B', location, versionId);
        this.deleteEntry(type, 'L', location, versionId);
        if (type == 'R') {
            this.deleteEntry('I', 'M', location, versionId);
        }
        this.removeEmptyDirs(location);
        this.recordLastUpdate();
    }
    
    private void deleteEntry(final char c, final char c2, final URL url, final String s) {
        File file = null;
        try {
            if (c2 == 'B') {
                file = this.getMappedImage(c, c2, url, s, false);
            }
            else {
                file = this.getFileFromCache(c, c2, url, s, false);
            }
            this.deleteFile(file);
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        if (file != null && file.exists() && c == 'R' && c2 == 'M') {
            this.addOrphan(url, file);
        }
    }
    
    DiskCacheEntry getCacheEntryFromFile(final File file) {
        final DiskCacheEntry fileToEntry = this.fileToEntry(file);
        if (fileToEntry != null) {
            try {
                if (fileToEntry.getType() == 'P') {
                    return this.getMuffinEntry(fileToEntry.getType(), fileToEntry.getLocation());
                }
                return this.getCacheEntry(fileToEntry.getType(), fileToEntry.getLocation(), fileToEntry.getVersionId());
            }
            catch (IOException ex) {
                Trace.ignoredException((Exception)ex);
            }
        }
        return fileToEntry;
    }
    
    boolean isMainMuffinFile(final File file) throws IOException {
        return file.equals(this.getFileFromCache('P', 'M', this.fileToEntry(file).getLocation(), null, false));
    }
    
    private ArrayList getCacheEntries(final char c, final URL url) throws IOException {
        final ArrayList list = new ArrayList<DiskCacheEntry>();
        final String keyToFileLocation = this.keyToFileLocation(c, 'M', url, "MATCH");
        final int index = keyToFileLocation.indexOf(File.separator + 'V' + "MATCH" + File.separator);
        if (index == -1) {
            throw new IllegalStateException("the javaws cache is corrupted");
        }
        final File[] listFiles = new File(keyToFileLocation.substring(0, index)).listFiles();
        if (listFiles == null) {
            return list;
        }
        for (int i = 0; i < listFiles.length; ++i) {
            final String name = listFiles[i].getName();
            if (listFiles[i].isDirectory() && name.length() > 1 && name.charAt(0) == 'V') {
                final String substring = name.substring(1);
                if (this.getFileFromCache(c, 'M', url, substring, true) != null) {
                    list.add(this.getCacheEntry(c, url, substring));
                }
            }
        }
        final int size = list.size();
        if (size > 1) {
            final DiskCacheEntry[] array = list.toArray(new DiskCacheEntry[size]);
            Arrays.sort(array, new Comparator() {
                public int compare(final Object o, final Object o2) {
                    return new VersionID(((DiskCacheEntry)o).getVersionId()).isGreaterThan(new VersionID(((DiskCacheEntry)o2).getVersionId())) ? -1 : 1;
                }
            });
            for (int j = 0; j < size; ++j) {
                list.set(j, array[j]);
            }
        }
        if (this.getFileFromCache(c, 'M', url, null, true) != null) {
            list.add(this.getCacheEntry(c, url, null));
        }
        return list;
    }
    
    public String[] getCacheVersions(final char c, final URL url) throws IOException {
        final ArrayList<String> list = new ArrayList<String>();
        final String keyToFileLocation = this.keyToFileLocation(c, 'M', url, "MATCH");
        final int index = keyToFileLocation.indexOf(File.separator + 'V' + "MATCH" + File.separator);
        if (index == -1) {
            throw new IllegalStateException("the javaws cache is corrupted");
        }
        final File[] listFiles = new File(keyToFileLocation.substring(0, index)).listFiles();
        if (listFiles != null) {
            for (int i = 0; i < listFiles.length; ++i) {
                final String name = listFiles[i].getName();
                if (listFiles[i].isDirectory() && name.length() > 1 && name.charAt(0) == 'V') {
                    final String substring = name.substring(1);
                    if (this.getFileFromCache(c, 'M', url, substring, true) != null) {
                        list.add(substring);
                    }
                }
            }
        }
        return list.toArray(new String[0]);
    }
    
    void visitDiskCache(final char c, final DiskCacheVisitor diskCacheVisitor) throws IOException {
        this.visitDiskCacheHelper(this._baseDir, 0, c, diskCacheVisitor);
    }
    
    private void visitDiskCacheHelper(final File file, final int n, final char c, final DiskCacheVisitor diskCacheVisitor) throws IOException {
        final String name = file.getName();
        if (file.isDirectory() && (name.length() <= 2 || file.getName().charAt(1) != 'N')) {
            final File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; ++i) {
                this.visitDiskCacheHelper(listFiles[i], n + 1, c, diskCacheVisitor);
            }
        }
        else if (name.length() > 2 && n > 3) {
            final char char1 = name.charAt(0);
            final char char2 = name.charAt(1);
            if (char1 == c && char2 == 'M') {
                final DiskCacheEntry cacheEntryFromFile = this.getCacheEntryFromFile(file);
                if (cacheEntryFromFile == null) {
                    throw new IllegalStateException("the javaws cache is corrupted");
                }
                diskCacheVisitor.visitEntry(cacheEntryFromFile);
            }
        }
    }
    
    File getMuffinFileForURL(final URL url) {
        try {
            return this.getFileFromCache('P', 'M', url, null, false);
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    URL[] getAccessibleMuffins(final URL url) throws IOException {
        final MuffinAccessVisitor muffinAccessVisitor = new MuffinAccessVisitor(this, url);
        this.visitDiskCache('P', muffinAccessVisitor);
        return muffinAccessVisitor.getAccessibleMuffins();
    }
    
    long getCacheSize() throws IOException {
        Trace.println("Computing diskcache size: " + this._baseDir.getAbsoluteFile(), TraceLevel.CACHE);
        final SizeVisitor sizeVisitor = new SizeVisitor(this);
        this.visitDiskCache('R', sizeVisitor);
        return sizeVisitor.getSize();
    }
    
    void uninstallCache() {
        this.deleteFile(this._baseDir);
        if (this._baseDir.exists()) {
            this.recordLastUpdate();
        }
    }
    
    private void deleteFile(final File file) {
        if (file.isDirectory()) {
            final File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; ++i) {
                    this.deleteFile(listFiles[i]);
                }
            }
        }
        file.delete();
    }
    
    private OutputStream getOutputStream(final char c, final char c2, final URL url, final String s) throws IOException {
        final File fileFromCache = this.getFileFromCache(c, c2, url, s, false);
        fileFromCache.getParentFile().mkdirs();
        fileFromCache.createNewFile();
        this.recordLastUpdate();
        return new FileOutputStream(fileFromCache);
    }
    
    private InputStream getInputStream(final char c, final char c2, final URL url, final String s) throws IOException {
        return new FileInputStream(this.getFileFromCache(c, c2, url, s, false));
    }
    
    byte[] getLapData(final char c, final URL url, final String s) throws IOException {
        return this.getEntryContent(c, 'L', url, s);
    }
    
    void putLapData(final char c, final URL url, final String s, final byte[] array) throws IOException {
        this.storeAtomic(c, 'L', url, s, array);
    }
    
    private byte[] getEntryContent(final char c, final char c2, final URL url, final String s) throws IOException {
        final File fileFromCache = this.getFileFromCache(c, c2, url, s, true);
        if (fileFromCache == null) {
            return null;
        }
        final long length = fileFromCache.length();
        if (length > 1073741824L) {
            return null;
        }
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileFromCache));
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int)length);
        final byte[] array = new byte[32768];
        try {
            for (int i = bufferedInputStream.read(array); i >= 0; i = bufferedInputStream.read(array)) {
                byteArrayOutputStream.write(array, 0, i);
            }
        }
        finally {
            byteArrayOutputStream.close();
            bufferedInputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private void storeAtomic(final char c, final char c2, final URL url, final String s, final byte[] array) throws IOException {
        final File tempCacheFile = this.getTempCacheFile(url, s);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(tempCacheFile));
        final byte[] array2 = new byte[32768];
        try {
            for (int i = byteArrayInputStream.read(array2); i >= 0; i = byteArrayInputStream.read(array2)) {
                bufferedOutputStream.write(array2, 0, i);
            }
        }
        finally {
            bufferedOutputStream.close();
            byteArrayInputStream.close();
        }
        this.putFileInCache(c, c2, url, s, tempCacheFile);
    }
    
    private File putFileInCache(final char c, final char c2, final URL url, final String s, final File file) throws IOException {
        final File file2 = new File(this.keyToFileLocation(c, c2, url, s));
        this.removeOrphans(url);
        file2.delete();
        if (file.renameTo(file2)) {
            if (c == 'R' && c2 == 'M' && this.getFileFromCache('I', c2, url, s, false).exists()) {
                this.deleteEntry(c, c2, url, s);
                this.deleteEntry('I', c2, url, s);
            }
            return file2;
        }
        this.deleteEntry(c, c2, url, s);
        if (c == 'R' && c2 == 'M') {
            final PrintStream printStream = new PrintStream(this.getOutputStream('I', 'M', url, s));
            try {
                printStream.println(file.getCanonicalPath());
            }
            finally {
                printStream.close();
            }
            return file;
        }
        throw new IOException("rename failed in cache to: " + file2);
    }
    
    File getFileFromCache(final char c, final char c2, final URL url, final String s, final boolean b) throws IOException {
        BufferedReader bufferedReader = null;
        if (c == 'R' && c2 == 'M' && this.getFileFromCache('I', c2, url, s, false).exists()) {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(this.getInputStream('I', 'M', url, s)));
                return new File(bufferedReader.readLine());
            }
            catch (IOException ex) {
                if (b) {
                    return null;
                }
            }
            finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
        final File file = new File(this.keyToFileLocation(c, c2, url, s));
        if (b && !file.exists()) {
            return null;
        }
        return file;
    }
    
    private DiskCacheEntry fileToEntry(final File file) {
        char char1 = '\0';
        String substring = null;
        final String absolutePath = file.getAbsolutePath();
        final String absolutePath2 = this._baseDir.getAbsolutePath();
        if (!absolutePath.startsWith(absolutePath2)) {
            return null;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(absolutePath.substring(absolutePath2.length()), File.separator, false);
        URL url;
        try {
            final String nextToken = stringTokenizer.nextToken();
            final String nextToken2 = stringTokenizer.nextToken();
            if (nextToken2.length() < 1) {
                return null;
            }
            final String substring2 = nextToken2.substring(1);
            final String nextToken3 = stringTokenizer.nextToken();
            if (nextToken3.length() < 1) {
                return null;
            }
            int int1;
            try {
                int1 = Integer.parseInt(nextToken3.substring(1));
                if (int1 == 80) {
                    int1 = -1;
                }
            }
            catch (NumberFormatException ex) {
                return null;
            }
            final StringBuffer sb = new StringBuffer();
            while (stringTokenizer.hasMoreElements()) {
                final String removeEscapes = removeEscapes(stringTokenizer.nextToken());
                if (removeEscapes.length() < 1) {
                    return null;
                }
                char1 = removeEscapes.charAt(0);
                if (char1 == 'V') {
                    substring = removeEscapes.substring(1);
                }
                else {
                    sb.append('/');
                    sb.append(removeEscapes.substring(2));
                }
            }
            url = new URL(nextToken, substring2, int1, sb.toString());
        }
        catch (MalformedURLException ex2) {
            return null;
        }
        catch (NoSuchElementException ex3) {
            return null;
        }
        return new DiskCacheEntry(char1, url, substring, file, 0L);
    }
    
    private static String removeEscapes(final String s) {
        if (s == null || s.indexOf(38) == -1) {
            return s;
        }
        final StringBuffer sb = new StringBuffer(s.length());
        int i;
        for (i = 0; i < s.length() - 1; ++i) {
            final char char1 = s.charAt(i);
            final char char2 = s.charAt(i + 1);
            if (char1 == '&' && char2 == 'p') {
                ++i;
                sb.append('%');
            }
            else if (char1 == '&' && char2 == 'c') {
                ++i;
                sb.append(':');
            }
            else if (char1 != '&' || char2 != '&') {
                sb.append(char1);
            }
        }
        if (i < s.length()) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }
    
    private String keyToFileLocation(final char c, final char c2, final URL url, final String s) {
        final StringBuffer sb = new StringBuffer(url.toString().length() + ((s == null) ? 0 : s.length()) * 2);
        sb.append(url.getProtocol());
        sb.append(File.separatorChar);
        sb.append('D');
        sb.append(url.getHost());
        sb.append(File.separatorChar);
        String string;
        if (url.getPort() == -1 && url.getProtocol().equals("http")) {
            string = "P80";
        }
        else {
            string = "P" + new Integer(url.getPort()).toString();
        }
        sb.append(string);
        sb.append(File.separatorChar);
        if (s != null) {
            sb.append('V');
            sb.append(s);
            sb.append(File.separatorChar);
        }
        sb.append(this.convertURLfile(c, c2, url.getFile()));
        return this._baseDir.getAbsolutePath() + File.separator + sb.toString();
    }
    
    private String convertURLfile(final char c, final char c2, String s) {
        String s2 = null;
        final int index;
        if ((index = s.indexOf(";")) != -1) {
            s2 = s.substring(index);
            s = s.substring(0, index);
        }
        final int index2;
        if ((index2 = s.indexOf("?")) != -1) {
            s2 = s.substring(index2) + s2;
            s = s.substring(0, index2);
        }
        if (s2 != null) {
            Trace.println("     URL: " + s + "\n  PARAMS: " + s2, TraceLevel.CACHE);
        }
        final StringBuffer sb = new StringBuffer(s.length() * 2);
        int length = -1;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '/') {
                sb.append(File.separatorChar);
                sb.append('D');
                sb.append('M');
                length = sb.length();
            }
            else if (s.charAt(i) == ':') {
                sb.append("&c");
            }
            else if (s.charAt(i) == '&') {
                sb.append("&&");
            }
            else if (s.charAt(i) == '%') {
                sb.append("&p");
            }
            else {
                sb.append(s.charAt(i));
            }
        }
        if (length == -1) {
            sb.insert(0, c);
            sb.insert(1, c2);
        }
        else {
            sb.setCharAt(length - 2, c);
            sb.setCharAt(length - 1, c2);
        }
        return sb.toString();
    }
    
    long getOrphanSize() {
        long n = 0L;
        try {
            final Iterator orphans = this.getOrphans();
            while (orphans.hasNext()) {
                n += orphans.next().getSize();
            }
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
        }
        return n;
    }
    
    void cleanResources() {
        try {
            final Iterator orphans = this.getOrphans();
            while (orphans.hasNext()) {
                this.removeEntry(orphans.next());
            }
        }
        catch (Exception ex) {
            Trace.ignoredException(ex);
        }
    }
    
    Iterator getOrphans() {
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        final DiskCacheVisitor diskCacheVisitor = new DiskCacheVisitor() {
            public void visitEntry(final DiskCacheEntry diskCacheEntry) {
                LaunchDesc buildDescriptor = null;
                try {
                    buildDescriptor = LaunchDescFactory.buildDescriptor(diskCacheEntry.getFile());
                }
                catch (Exception ex) {
                    Trace.ignoredException(ex);
                }
                if (buildDescriptor != null) {
                    final JARDesc[] eagerOrAllJarDescs = buildDescriptor.getResources().getEagerOrAllJarDescs(true);
                    if (eagerOrAllJarDescs != null) {
                        for (int i = 0; i < eagerOrAllJarDescs.length; ++i) {
                            try {
                                final File fileFromCache = DiskCache.this.getFileFromCache('R', 'M', eagerOrAllJarDescs[i].getLocation(), eagerOrAllJarDescs[i].getVersion(), false);
                                if (fileFromCache != null) {
                                    list.add(fileFromCache);
                                }
                            }
                            catch (IOException ex2) {}
                        }
                    }
                    final InformationDesc information = buildDescriptor.getInformation();
                    if (information != null) {
                        final IconDesc[] icons = information.getIcons();
                        if (icons != null) {
                            for (int j = 0; j < icons.length; ++j) {
                                try {
                                    final File fileFromCache2 = DiskCache.this.getFileFromCache('R', 'M', icons[j].getLocation(), icons[j].getVersion(), false);
                                    if (fileFromCache2 != null) {
                                        list.add(fileFromCache2);
                                    }
                                }
                                catch (IOException ex3) {}
                            }
                        }
                    }
                }
            }
        };
        try {
            this.visitDiskCache('A', diskCacheVisitor);
            this.visitDiskCache('E', diskCacheVisitor);
            final DiskCacheVisitor diskCacheVisitor2 = new DiskCacheVisitor() {
                public void visitEntry(final DiskCacheEntry diskCacheEntry) {
                    if (!list.contains(diskCacheEntry.getFile())) {
                        list2.add(diskCacheEntry);
                    }
                }
            };
            this.visitDiskCache('R', diskCacheVisitor2);
            this.visitDiskCache('I', diskCacheVisitor2);
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        return list2.iterator();
    }
    
    Iterator getJnlpCacheEntries() {
        final ArrayList list = new ArrayList();
        try {
            final DiskCacheVisitor diskCacheVisitor = new DiskCacheVisitor() {
                public void visitEntry(final DiskCacheEntry diskCacheEntry) {
                    list.add(diskCacheEntry);
                }
            };
            this.visitDiskCache('A', diskCacheVisitor);
            this.visitDiskCache('E', diskCacheVisitor);
        }
        catch (IOException ex) {
            Trace.ignoredException((Exception)ex);
        }
        return list.iterator();
    }
    
    private static class MuffinAccessVisitor implements DiskCacheVisitor
    {
        private DiskCache _diskCache;
        private URL _theURL;
        private URL[] _urls;
        private int _counter;
        
        MuffinAccessVisitor(final DiskCache diskCache, final URL theURL) {
            this._urls = new URL[255];
            this._counter = 0;
            this._diskCache = diskCache;
            this._theURL = theURL;
        }
        
        public void visitEntry(final DiskCacheEntry diskCacheEntry) {
            final URL location = diskCacheEntry.getLocation();
            if (location == null) {
                return;
            }
            if (location.getHost().equals(this._theURL.getHost())) {
                this._urls[this._counter++] = location;
            }
        }
        
        public URL[] getAccessibleMuffins() {
            return this._urls;
        }
    }
    
    private static class DeleteVisitor implements DiskCacheVisitor
    {
        private DiskCache _diskCache;
        
        DeleteVisitor(final DiskCache diskCache) {
            this._diskCache = diskCache;
        }
        
        public void visitEntry(final DiskCacheEntry diskCacheEntry) {
            this._diskCache.removeEntry(diskCacheEntry);
        }
    }
    
    private static class SizeVisitor implements DiskCacheVisitor
    {
        private DiskCache _diskCache;
        long _size;
        
        SizeVisitor(final DiskCache diskCache) {
            this._size = 0L;
            this._diskCache = diskCache;
            this._size = 0L;
        }
        
        public void visitEntry(final DiskCacheEntry diskCacheEntry) {
            if (diskCacheEntry.getDirectory() != null && diskCacheEntry.getDirectory().exists()) {
                final File[] listFiles = diskCacheEntry.getDirectory().listFiles();
                for (int i = 0; i < listFiles.length; ++i) {
                    this._size += listFiles[i].length();
                }
            }
            else {
                this._size += diskCacheEntry.getFile().length();
            }
        }
        
        public long getSize() {
            return this._size;
        }
    }
    
    public interface DiskCacheVisitor
    {
        void visitEntry(final DiskCacheEntry p0);
    }
}
