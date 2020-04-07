// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client;

import org.apache.http.protocol.HttpContext;
import java.io.IOException;

public interface HttpRequestRetryHandler
{
    boolean retryRequest(final IOException p0, final int p1, final HttpContext p2);
}
