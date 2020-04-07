// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.net;

import java.net.URL;
import java.io.IOException;
import java.io.File;

public interface HttpDownload
{
    void download(final HttpResponse p0, final File p1, final HttpDownloadListener p2) throws IOException, CanceledDownloadException;
    
    void download(final URL p0, final File p1, final HttpDownloadListener p2) throws IOException, CanceledDownloadException;
}
