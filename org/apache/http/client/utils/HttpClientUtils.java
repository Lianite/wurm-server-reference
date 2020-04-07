// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpEntity;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;

public class HttpClientUtils
{
    public static void closeQuietly(final HttpResponse response) {
        if (response != null) {
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    EntityUtils.consume(entity);
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static void closeQuietly(final HttpClient httpClient) {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
