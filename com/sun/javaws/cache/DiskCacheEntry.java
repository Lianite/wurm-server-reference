// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.io.File;
import java.net.URL;

public class DiskCacheEntry
{
    private char _type;
    private URL _location;
    private String _versionId;
    private long _timestamp;
    private File _file;
    private File _directory;
    private File _mappedBitmap;
    private File _muffinTag;
    
    public DiskCacheEntry() {
        this('\0', null, null, null, 0L);
    }
    
    public DiskCacheEntry(final char c, final URL url, final String s, final File file, final long n) {
        this(c, url, s, file, n, null, null, null);
    }
    
    public DiskCacheEntry(final char c, final URL url, final String s, final File file, final long n, final File file2, final File file3) {
        this(c, url, s, file, n, file2, file3, null);
    }
    
    public DiskCacheEntry(final char type, final URL location, final String versionId, final File file, final long timestamp, final File directory, final File mappedBitmap, final File muffinTag) {
        this._type = type;
        this._location = location;
        this._versionId = versionId;
        this._timestamp = timestamp;
        this._file = file;
        this._directory = directory;
        this._mappedBitmap = mappedBitmap;
        this._muffinTag = muffinTag;
    }
    
    public char getType() {
        return this._type;
    }
    
    public void setType(final char type) {
        this._type = type;
    }
    
    public URL getLocation() {
        return this._location;
    }
    
    public void setLocataion(final URL location) {
        this._location = location;
    }
    
    public long getTimeStamp() {
        return this._timestamp;
    }
    
    public void setTimeStamp(final long timestamp) {
        this._timestamp = timestamp;
    }
    
    public File getMuffinTagFile() {
        return this._muffinTag;
    }
    
    public void setMuffinTagFile(final File muffinTag) {
        this._muffinTag = muffinTag;
    }
    
    public String getVersionId() {
        return this._versionId;
    }
    
    public void setVersionId(final String versionId) {
        this._versionId = versionId;
    }
    
    public File getFile() {
        return this._file;
    }
    
    public void setFile(final File file) {
        this._file = file;
    }
    
    public File getDirectory() {
        return this._directory;
    }
    
    public void setDirectory(final File directory) {
        this._directory = directory;
    }
    
    public File getMappedBitmap() {
        return this._mappedBitmap;
    }
    
    public void setMappedBitmap(final File mappedBitmap) {
        this._mappedBitmap = mappedBitmap;
    }
    
    public long getLastAccess() {
        return (this._file == null) ? 0L : this._file.lastModified();
    }
    
    public void setLastAccess(final long lastModified) {
        if (this._file != null) {
            this._file.setLastModified(lastModified);
        }
    }
    
    public boolean isEmpty() {
        return this._location == null;
    }
    
    public long getSize() {
        if (this._directory != null && this._directory.isDirectory()) {
            long n = 0L;
            final File[] listFiles = this._directory.listFiles();
            for (int i = 0; i < listFiles.length; ++i) {
                n += listFiles[i].length();
            }
            return n;
        }
        return this._file.length();
    }
    
    public boolean newerThan(final DiskCacheEntry diskCacheEntry) {
        return diskCacheEntry == null || this.getVersionId() != null || this.getTimeStamp() > diskCacheEntry.getTimeStamp();
    }
    
    public String toString() {
        if (this.isEmpty()) {
            return "DisckCacheEntry[<empty>]";
        }
        return "DisckCacheEntry[" + this._type + ";" + this._location + ";" + this._versionId + ";" + this._timestamp + ";" + this._file + ";" + this._directory + "]";
    }
}
