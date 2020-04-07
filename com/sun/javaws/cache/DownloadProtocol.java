// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import com.sun.javaws.security.SigningInfo;
import java.util.jar.JarFile;
import com.sun.javaws.net.CanceledDownloadException;
import com.sun.javaws.net.HttpDownloadListener;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import com.sun.deploy.config.Config;
import java.io.OutputStream;
import com.sun.javaws.exceptions.InvalidJarDiffException;
import java.io.FileOutputStream;
import com.sun.javaws.jardiff.JarDiffPatcher;
import java.io.File;
import com.sun.javaws.net.HttpResponse;
import com.sun.javaws.net.HttpRequest;
import java.net.URL;
import com.sun.javaws.exceptions.JNLPException;
import java.util.zip.ZipException;
import com.sun.javaws.exceptions.BadJARFileException;
import com.sun.javaws.exceptions.BadVersionResponseException;
import com.sun.javaws.util.VersionString;
import com.sun.javaws.exceptions.MissingVersionResponseException;
import com.sun.javaws.exceptions.BadMimeTypeResponseException;
import com.sun.javaws.exceptions.ErrorCodeResponseException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import com.sun.javaws.util.VersionID;
import com.sun.javaws.exceptions.FailedDownloadingResourceException;
import java.io.IOException;
import com.sun.javaws.JavawsFactory;
import com.sun.javaws.Globals;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;

public class DownloadProtocol
{
    public static final int JAR_DOWNLOAD = 0;
    public static final int NATIVE_DOWNLOAD = 1;
    public static final int IMAGE_DOWNLOAD = 2;
    public static final int APPLICATION_JNLP_DOWNLOAD = 3;
    public static final int EXTENSION_JNLP_DOWNLOAD = 4;
    private static final String JNLP_MIME_TYPE = "application/x-java-jnlp-file";
    private static final String ERROR_MIME_TYPE = "application/x-java-jnlp-error";
    private static final String JAR_MIME_TYPE = "application/x-java-archive";
    private static final String JARDIFF_MIME_TYPE = "application/x-java-archive-diff";
    private static final String GIF_MIME_TYPE = "image/gif";
    private static final String JPEG_MIME_TYPE = "image/jpeg";
    private static final String ARG_ARCH = "arch";
    private static final String ARG_OS = "os";
    private static final String ARG_LOCALE = "locale";
    private static final String ARG_VERSION_ID = "version-id";
    private static final String ARG_CURRENT_VERSION_ID = "current-version-id";
    private static final String ARG_PLATFORM_VERSION_ID = "platform-version-id";
    private static final String ARG_KNOWN_PLATFORMS = "known-platforms";
    private static final String REPLY_JNLP_VERSION = "x-java-jnlp-version-id";
    
    private static void doDownload(final DownloadInfo downloadInfo, final DownloadAction downloadAction) throws JNLPException {
        try {
            final boolean[] array = { false };
            final DiskCacheEntry bestDiskCacheEntry = findBestDiskCacheEntry(downloadInfo.getEntryType(), downloadInfo.getLocation(), downloadInfo.getVersion(), array);
            final boolean b = array[0];
            if (bestDiskCacheEntry != null && downloadInfo.isCacheOk(bestDiskCacheEntry, b)) {
                Trace.println("Found in cache: " + bestDiskCacheEntry, TraceLevel.NETWORK);
                downloadAction.actionInCache(bestDiskCacheEntry);
                return;
            }
            if (Globals.isOffline()) {
                Trace.println("Offline mode. No Web check. Cache lookup: " + bestDiskCacheEntry, TraceLevel.NETWORK);
                downloadAction.actionOffline(bestDiskCacheEntry, b);
                return;
            }
            if (downloadAction.skipDownloadStep()) {
                Trace.println("Skipping download step", TraceLevel.NETWORK);
                return;
            }
            final URL requestURL = downloadInfo.getRequestURL(bestDiskCacheEntry);
            Trace.println("Connection to: " + requestURL, TraceLevel.NETWORK);
            final HttpRequest httpRequestImpl = JavawsFactory.getHttpRequestImpl();
            HttpResponse httpResponse;
            try {
                httpResponse = (downloadAction.useHeadRequest() ? httpRequestImpl.doHeadRequest(requestURL) : httpRequestImpl.doGetRequest(requestURL));
            }
            catch (IOException ex4) {
                httpResponse = (downloadAction.useHeadRequest() ? httpRequestImpl.doHeadRequest(requestURL, false) : httpRequestImpl.doGetRequest(requestURL, false));
            }
            if (httpResponse.getStatusCode() == 404) {
                throw new FailedDownloadingResourceException(downloadInfo.getLocation(), downloadInfo.getVersion(), new IOException("HTTP response 404"));
            }
            final int contentLength = httpResponse.getContentLength();
            final long lastModified = httpResponse.getLastModified();
            String responseHeader = httpResponse.getResponseHeader("x-java-jnlp-version-id");
            final String version = downloadInfo.getVersion();
            if (version != null && responseHeader == null && Globals.getCodebaseOverride() != null && new VersionID(version).isSimpleVersion()) {
                responseHeader = version;
            }
            final String contentType = httpResponse.getContentType();
            Trace.println("Sever response: (length: " + contentLength + ", lastModified: " + new Date(lastModified) + ", downloadVersion " + responseHeader + ", mimeType: " + contentType + ")", TraceLevel.NETWORK);
            if (contentType != null && contentType.equalsIgnoreCase("application/x-java-jnlp-error")) {
                throw new ErrorCodeResponseException(downloadInfo.getLocation(), downloadInfo.getVersion(), new BufferedReader(new InputStreamReader(httpResponse.getInputStream())).readLine());
            }
            if (!downloadInfo.isValidMimeType(contentType, bestDiskCacheEntry)) {
                throw new BadMimeTypeResponseException(downloadInfo.getLocation(), downloadInfo.getVersion(), contentType);
            }
            if (downloadInfo.needsReplyVersion(bestDiskCacheEntry)) {
                if (responseHeader == null) {
                    throw new MissingVersionResponseException(downloadInfo.getLocation(), downloadInfo.getVersion());
                }
                if (!downloadInfo.isPlatformRequest()) {
                    if (!new VersionString(downloadInfo.getVersion()).contains(responseHeader)) {
                        throw new BadVersionResponseException(downloadInfo.getLocation(), downloadInfo.getVersion(), responseHeader);
                    }
                    if (!new VersionID(responseHeader).isSimpleVersion()) {
                        throw new BadVersionResponseException(downloadInfo.getLocation(), downloadInfo.getVersion(), responseHeader);
                    }
                }
            }
            if (bestDiskCacheEntry != null && !downloadInfo.isWebNewer(bestDiskCacheEntry, contentLength, lastModified, responseHeader)) {
                downloadAction.actionInCache(bestDiskCacheEntry);
                httpResponse.disconnect();
                return;
            }
            downloadAction.actionDownload(bestDiskCacheEntry, downloadInfo, lastModified, contentLength, responseHeader, contentType, httpResponse);
            httpResponse.disconnect();
        }
        catch (ZipException ex) {
            throw new BadJARFileException(downloadInfo.getLocation(), downloadInfo.getVersion(), ex);
        }
        catch (JNLPException ex2) {
            throw ex2;
        }
        catch (Exception ex3) {
            throw new FailedDownloadingResourceException(downloadInfo.getLocation(), downloadInfo.getVersion(), ex3);
        }
    }
    
    private static File applyPatch(final File file, final File file2, final URL url, final String s, final DownloadDelegate downloadDelegate) throws JNLPException {
        final JarDiffPatcher jarDiffPatcher = new JarDiffPatcher();
        File tempCacheFile = null;
        OutputStream outputStream = null;
        boolean b = false;
        try {
            tempCacheFile = Cache.getTempCacheFile(url, s);
            outputStream = new FileOutputStream(tempCacheFile);
            Patcher.PatchDelegate patchDelegate = null;
            if (downloadDelegate != null) {
                downloadDelegate.patching(url, s, 0);
                patchDelegate = new Patcher.PatchDelegate() {
                    public void patching(final int n) {
                        downloadDelegate.patching(url, s, n);
                    }
                };
            }
            try {
                jarDiffPatcher.applyPatch(patchDelegate, file.getPath(), file2.getPath(), outputStream);
            }
            catch (IOException ex) {
                throw new InvalidJarDiffException(url, s, ex);
            }
            b = true;
        }
        catch (IOException ex2) {
            Trace.println("Got exception while patching: " + ex2, TraceLevel.NETWORK);
            throw new FailedDownloadingResourceException(url, s, ex2);
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            catch (IOException ex3) {
                Trace.ignoredException((Exception)ex3);
            }
            if (!b) {
                tempCacheFile.delete();
            }
            file2.delete();
            if (downloadDelegate != null && !b) {
                downloadDelegate.downloadFailed(url, s);
            }
        }
        return tempCacheFile;
    }
    
    public static DiskCacheEntry getJRE(final URL url, final String s, final boolean b, final String s2) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, false, s2, b);
        final RetrieveAction retrieveAction = new RetrieveAction(null);
        doDownload(downloadInfo, retrieveAction);
        final DiskCacheEntry result = retrieveAction.getResult();
        if (result == null) {
            throw new FailedDownloadingResourceException(url, s, null);
        }
        return result;
    }
    
    public static DiskCacheEntry getLaunchFile(final URL url, final boolean b) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, null, 3, false);
        final RetrieveAction retrieveAction = new RetrieveAction(null);
        doDownload(downloadInfo, retrieveAction);
        final DiskCacheEntry result = retrieveAction.getResult();
        if (result == null) {
            throw new FailedDownloadingResourceException(url, null, null);
        }
        return result;
    }
    
    public static DiskCacheEntry getCachedLaunchedFile(final URL url) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, null, 3, true);
        final IsInCacheAction isInCacheAction = new IsInCacheAction();
        doDownload(downloadInfo, isInCacheAction);
        return isInCacheAction.getResult();
    }
    
    public static boolean isLaunchFileUpdateAvailable(final URL url) throws JNLPException {
        if (Globals.isOffline()) {
            return false;
        }
        final DownloadInfo downloadInfo = new DownloadInfo(url, null, 3, false);
        final UpdateAvailableAction updateAvailableAction = new UpdateAvailableAction();
        doDownload(downloadInfo, updateAvailableAction);
        return updateAvailableAction.getResult();
    }
    
    public static DiskCacheEntry getExtension(final URL url, final String s, final String s2, final boolean b) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, b, s2, false);
        final RetrieveAction retrieveAction = new RetrieveAction(null);
        doDownload(downloadInfo, retrieveAction);
        final DiskCacheEntry result = retrieveAction.getResult();
        if (result == null) {
            throw new FailedDownloadingResourceException(url, s, null);
        }
        return result;
    }
    
    public static DiskCacheEntry getCachedExtension(final URL url, final String s, final String s2) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, true, s2, false);
        final IsInCacheAction isInCacheAction = new IsInCacheAction();
        doDownload(downloadInfo, isInCacheAction);
        return isInCacheAction.getResult();
    }
    
    public static boolean isExtensionUpdateAvailable(final URL url, final String s, final String s2) throws JNLPException {
        if (Globals.isOffline()) {
            return false;
        }
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, false, s2, false);
        final UpdateAvailableAction updateAvailableAction = new UpdateAvailableAction();
        doDownload(downloadInfo, updateAvailableAction);
        return updateAvailableAction.getResult();
    }
    
    public static DiskCacheEntry getResource(final URL url, final String s, final int n, final boolean b, final DownloadDelegate downloadDelegate) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, n, b);
        final RetrieveAction retrieveAction = new RetrieveAction(downloadDelegate);
        doDownload(downloadInfo, retrieveAction);
        final DiskCacheEntry result = retrieveAction.getResult();
        if (result == null) {
            throw new FailedDownloadingResourceException(url, s, null);
        }
        return result;
    }
    
    public static boolean isInCache(final URL url, final String s, final int n) {
        return getCachedVersion(url, s, n) != null;
    }
    
    public static long getCachedSize(final URL url, final String s, final int n) {
        final DiskCacheEntry cachedVersion = getCachedVersion(url, s, n);
        return (cachedVersion != null) ? cachedVersion.getSize() : 0L;
    }
    
    public static DiskCacheEntry getCachedVersion(final URL url, final String s, final int n) {
        try {
            final DownloadInfo downloadInfo = new DownloadInfo(url, s, n, true);
            final IsInCacheAction isInCacheAction = new IsInCacheAction();
            doDownload(downloadInfo, isInCacheAction);
            return isInCacheAction.getResult();
        }
        catch (JNLPException ex) {
            Trace.ignoredException((Exception)ex);
            return null;
        }
    }
    
    public static boolean isUpdateAvailable(final URL url, final String s, final int n) throws JNLPException {
        if (Globals.isOffline()) {
            return false;
        }
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, n, false);
        final UpdateAvailableAction updateAvailableAction = new UpdateAvailableAction();
        doDownload(downloadInfo, updateAvailableAction);
        return updateAvailableAction.getResult();
    }
    
    public static long getDownloadSize(final URL url, final String s, final int n) throws JNLPException {
        final DownloadInfo downloadInfo = new DownloadInfo(url, s, n, false);
        final DownloadSizeAction downloadSizeAction = new DownloadSizeAction();
        doDownload(downloadInfo, downloadSizeAction);
        return downloadSizeAction.getResult();
    }
    
    private static DiskCacheEntry findBestDiskCacheEntry(final char c, final URL url, final String s, final boolean[] array) throws IOException {
        if (s == null) {
            array[0] = true;
            return Cache.getCacheEntry(c, url, null);
        }
        final VersionString versionString = new VersionString(s);
        if (versionString.isSimpleVersion()) {
            final DiskCacheEntry cacheEntry = Cache.getCacheEntry(c, url, s);
            if (cacheEntry != null) {
                array[0] = true;
                return cacheEntry;
            }
        }
        String s2 = null;
        String s3 = null;
        final String[] cacheVersions = Cache.getCacheVersions(c, url);
        for (int i = 0; i < cacheVersions.length; ++i) {
            if (versionString.contains(cacheVersions[i])) {
                s3 = cacheVersions[i];
                break;
            }
            if (versionString.containsGreaterThan(cacheVersions[i]) && s2 == null) {
                s2 = cacheVersions[i];
            }
        }
        if (s3 == null) {
            array[0] = false;
            if (s2 == null) {
                return null;
            }
            s3 = s2;
        }
        else {
            array[0] = true;
        }
        return Cache.getCacheEntry(c, url, s3);
    }
    
    static class DownloadInfo
    {
        private URL _location;
        private String _version;
        private int _kind;
        private boolean _isCacheOk;
        private String _knownPlatforms;
        private boolean _isPlatformVersion;
        
        public DownloadInfo(final URL location, final String version, final int kind, final boolean isCacheOk) {
            this._knownPlatforms = null;
            this._isPlatformVersion = false;
            this._location = location;
            this._version = version;
            this._kind = kind;
            this._isCacheOk = isCacheOk;
        }
        
        public DownloadInfo(final URL location, final String version, final boolean isCacheOk, final String knownPlatforms, final boolean isPlatformVersion) {
            this._knownPlatforms = null;
            this._isPlatformVersion = false;
            this._location = location;
            this._version = version;
            this._kind = 4;
            this._isCacheOk = isCacheOk;
            this._knownPlatforms = knownPlatforms;
            this._isPlatformVersion = isPlatformVersion;
        }
        
        URL getLocation() {
            return this._location;
        }
        
        String getVersion() {
            return this._version;
        }
        
        int getKind() {
            return this._kind;
        }
        
        char getEntryType() {
            switch (this._kind) {
                case 0: {
                    return 'R';
                }
                case 2: {
                    return 'R';
                }
                case 1: {
                    return 'R';
                }
                case 3: {
                    return 'A';
                }
                case 4: {
                    return 'E';
                }
                default: {
                    return 'a';
                }
            }
        }
        
        boolean isCacheOk(final DiskCacheEntry diskCacheEntry, final boolean b) {
            return b && (this._version != null || this._isCacheOk) && diskCacheEntry.getTimeStamp() != 0L;
        }
        
        URL getRequestURL(final DiskCacheEntry diskCacheEntry) {
            final StringBuffer sb = new StringBuffer();
            if (this._version != null && this._kind != 4) {
                this.addURLArgument(sb, "version-id", this._version);
                if ((this._kind == 0 || this._kind == 1) && diskCacheEntry != null && diskCacheEntry.getVersionId() != null) {
                    this.addURLArgument(sb, "current-version-id", diskCacheEntry.getVersionId());
                }
            }
            if (this._kind == 4 && this._version != null) {
                if (this._isPlatformVersion) {
                    this.addURLArgument(sb, "platform-version-id", this._version);
                }
                else {
                    this.addURLArgument(sb, "version-id", this._version);
                }
                this.addURLArgument(sb, "arch", Config.getOSArch());
                this.addURLArgument(sb, "os", Config.getOSName());
                this.addURLArgument(sb, "locale", Globals.getDefaultLocaleString());
                if (this._knownPlatforms != null) {
                    this.addURLArgument(sb, "known-platforms", this._knownPlatforms);
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            if (sb.length() > 0) {
                sb.insert(0, '?');
            }
            try {
                if (Globals.getCodebaseOverride() != null && Globals.getCodebase() != null) {
                    return new URL(Globals.getCodebaseOverride() + this._location.getFile().substring(Globals.getCodebase().getFile().length()) + (Object)sb);
                }
                return new URL(this._location.getProtocol(), this._location.getHost(), this._location.getPort(), this._location.getFile() + (Object)sb);
            }
            catch (MalformedURLException ex) {
                Trace.ignoredException((Exception)ex);
                return null;
            }
        }
        
        private void addURLArgument(final StringBuffer sb, final String s, final String s2) {
            sb.append(URLEncoder.encode(s));
            sb.append('=');
            sb.append(URLEncoder.encode(s2));
            sb.append('&');
        }
        
        boolean needsReplyVersion(final DiskCacheEntry diskCacheEntry) {
            return this._version != null;
        }
        
        boolean isPlatformRequest() {
            return this._isPlatformVersion;
        }
        
        boolean isValidMimeType(final String s, final DiskCacheEntry diskCacheEntry) {
            if (s == null) {
                return false;
            }
            if (this._kind == 0 || this._kind == 1) {
                return !s.equalsIgnoreCase("application/x-java-archive-diff") || (diskCacheEntry != null && diskCacheEntry.getVersionId() != null);
            }
            return this._kind != 2 || s.equalsIgnoreCase("image/jpeg") || s.equalsIgnoreCase("image/gif");
        }
        
        boolean isWebNewer(final DiskCacheEntry diskCacheEntry, final long n, final long n2, final String s) {
            return this._version != null || (n2 == 0L && n > 0L) || n2 > diskCacheEntry.getTimeStamp();
        }
    }
    
    private static class UpdateAvailableAction implements DownloadAction
    {
        private boolean _result;
        
        private UpdateAvailableAction() {
            this._result = false;
        }
        
        public boolean getResult() {
            return this._result;
        }
        
        public void actionInCache(final DiskCacheEntry diskCacheEntry) throws IOException, JNLPException {
            this._result = false;
        }
        
        public void actionOffline(final DiskCacheEntry diskCacheEntry, final boolean b) throws IOException, JNLPException {
            this._result = false;
        }
        
        public boolean skipDownloadStep() {
            return false;
        }
        
        public void actionDownload(final DiskCacheEntry diskCacheEntry, final DownloadInfo downloadInfo, final long n, final int n2, final String s, final String s2, final HttpResponse httpResponse) throws IOException, JNLPException {
            this._result = true;
        }
        
        public boolean useHeadRequest() {
            return true;
        }
    }
    
    private static class IsInCacheAction implements DownloadAction
    {
        private DiskCacheEntry _dce;
        
        private IsInCacheAction() {
            this._dce = null;
        }
        
        public DiskCacheEntry getResult() {
            return this._dce;
        }
        
        public void actionInCache(final DiskCacheEntry dce) throws IOException, JNLPException {
            this._dce = dce;
        }
        
        public void actionOffline(final DiskCacheEntry diskCacheEntry, final boolean b) throws IOException, JNLPException {
            this._dce = (b ? diskCacheEntry : null);
        }
        
        public boolean skipDownloadStep() {
            return true;
        }
        
        public void actionDownload(final DiskCacheEntry diskCacheEntry, final DownloadInfo downloadInfo, final long n, final int n2, final String s, final String s2, final HttpResponse httpResponse) throws IOException, JNLPException {
        }
        
        public boolean useHeadRequest() {
            return false;
        }
    }
    
    private static class DownloadSizeAction implements DownloadAction
    {
        private long _result;
        
        private DownloadSizeAction() {
            this._result = -1L;
        }
        
        public long getResult() {
            return this._result;
        }
        
        public void actionInCache(final DiskCacheEntry diskCacheEntry) throws IOException, JNLPException {
            this._result = 0L;
        }
        
        public void actionOffline(final DiskCacheEntry diskCacheEntry, final boolean b) throws IOException, JNLPException {
            this._result = (b ? 0L : -1L);
        }
        
        public boolean skipDownloadStep() {
            return false;
        }
        
        public void actionDownload(final DiskCacheEntry diskCacheEntry, final DownloadInfo downloadInfo, final long n, final int n2, final String s, final String s2, final HttpResponse httpResponse) throws IOException, JNLPException {
            this._result = n2;
        }
        
        public boolean useHeadRequest() {
            return true;
        }
    }
    
    private static class RetrieveAction implements DownloadAction
    {
        private DiskCacheEntry _result;
        private DownloadDelegate _delegate;
        
        public DiskCacheEntry getResult() {
            return this._result;
        }
        
        public RetrieveAction(final DownloadDelegate delegate) {
            this._result = null;
            this._delegate = null;
            this._delegate = delegate;
        }
        
        public void actionInCache(final DiskCacheEntry result) throws IOException, JNLPException {
            this._result = result;
        }
        
        public void actionOffline(final DiskCacheEntry diskCacheEntry, final boolean b) throws IOException, JNLPException {
            this._result = (b ? diskCacheEntry : null);
        }
        
        public boolean skipDownloadStep() {
            return false;
        }
        
        public void actionDownload(final DiskCacheEntry diskCacheEntry, final DownloadInfo downloadInfo, final long n, final int n2, final String s, final String s2, final HttpResponse httpResponse) throws IOException, JNLPException {
            final URL location = downloadInfo.getLocation();
            final boolean equalsIgnoreCase = s2.equalsIgnoreCase("application/x-java-archive-diff");
            final String s3 = (downloadInfo.getVersion() != null) ? s : null;
            Trace.println("Doing download", TraceLevel.NETWORK);
            final HttpDownloadListener httpDownloadListener = (this._delegate == null) ? null : new HttpDownloadListener() {
                public boolean downloadProgress(final int n, final int n2) {
                    RetrieveAction.this._delegate.downloading(location, s, n, n2, equalsIgnoreCase);
                    return true;
                }
            };
            File file = null;
            try {
                file = Cache.getTempCacheFile(location, s3);
                JavawsFactory.getHttpDownloadImpl().download(httpResponse, file, httpDownloadListener);
            }
            catch (IOException ex) {
                Trace.println("Got exception while downloading resource: " + ex, TraceLevel.NETWORK);
                if (this._delegate != null) {
                    this._delegate.downloadFailed(location, s);
                }
                throw new FailedDownloadingResourceException(location, s, ex);
            }
            catch (CanceledDownloadException ex2) {
                Trace.ignoredException((Exception)ex2);
            }
            if (equalsIgnoreCase) {
                file = applyPatch(diskCacheEntry.getFile(), file, location, s, this._delegate);
            }
            if (downloadInfo.getKind() == 3 || downloadInfo.getKind() == 4 || downloadInfo.getKind() == 2) {
                Cache.insertEntry(downloadInfo.getEntryType(), location, s3, file, n);
            }
            else if (downloadInfo.getKind() == 0 || downloadInfo.getKind() == 1) {
                final File file2 = (downloadInfo.getKind() == 1) ? Cache.createNativeLibDir(location, s3) : null;
                JarFile jarFile = new JarFile(file);
                try {
                    SigningInfo.checkSigning(location, s3, jarFile, this._delegate, file2);
                    jarFile.close();
                    jarFile = null;
                    Cache.insertEntry(downloadInfo.getEntryType(), location, s3, file, n);
                    file = null;
                }
                finally {
                    if (jarFile != null) {
                        jarFile.close();
                    }
                    if (file != null) {
                        file.delete();
                    }
                }
            }
            this._result = Cache.getCacheEntry(downloadInfo.getEntryType(), location, s3);
        }
        
        public boolean useHeadRequest() {
            return false;
        }
    }
    
    private interface DownloadAction
    {
        void actionInCache(final DiskCacheEntry p0) throws IOException, JNLPException;
        
        void actionOffline(final DiskCacheEntry p0, final boolean p1) throws IOException, JNLPException;
        
        boolean skipDownloadStep();
        
        void actionDownload(final DiskCacheEntry p0, final DownloadInfo p1, final long p2, final int p3, final String p4, final String p5, final HttpResponse p6) throws IOException, JNLPException;
        
        boolean useHeadRequest();
    }
    
    public interface DownloadDelegate
    {
        void downloading(final URL p0, final String p1, final int p2, final int p3, final boolean p4);
        
        void validating(final URL p0, final int p1, final int p2);
        
        void patching(final URL p0, final String p1, final int p2);
        
        void downloadFailed(final URL p0, final String p1);
    }
}
