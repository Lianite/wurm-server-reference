// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.javaws.net.BasicDownloadLayer;
import com.sun.javaws.net.BasicNetworkLayer;
import com.sun.javaws.net.HttpDownload;
import com.sun.javaws.net.HttpRequest;

public class JavawsFactory
{
    private static HttpRequest _httpRequestImpl;
    private static HttpDownload _httpDownloadImpl;
    
    public static HttpRequest getHttpRequestImpl() {
        return JavawsFactory._httpRequestImpl;
    }
    
    public static HttpDownload getHttpDownloadImpl() {
        return JavawsFactory._httpDownloadImpl;
    }
    
    static {
        JavawsFactory._httpRequestImpl = new BasicNetworkLayer();
        JavawsFactory._httpDownloadImpl = new BasicDownloadLayer(JavawsFactory._httpRequestImpl);
    }
}
