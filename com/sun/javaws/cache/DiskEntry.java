// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.cache;

import java.io.File;
import java.net.URL;

public class DiskEntry
{
    private URL _url;
    private long _timestamp;
    private File _file;
    
    public DiskEntry(final URL url, final File file, final long timestamp) {
        this._url = url;
        this._timestamp = timestamp;
        this._file = file;
    }
    
    public URL getURL() {
        return this._url;
    }
    
    public long getTimeStamp() {
        return this._timestamp;
    }
    
    public File getFile() {
        return this._file;
    }
}
