// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.params.HttpParams;
import java.net.Socket;
import org.apache.http.annotation.NotThreadSafe;

@NotThreadSafe
public class SocketOutputBuffer extends AbstractSessionOutputBuffer
{
    public SocketOutputBuffer(final Socket socket, int buffersize, final HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        if (buffersize < 0) {
            buffersize = socket.getSendBufferSize();
        }
        if (buffersize < 1024) {
            buffersize = 1024;
        }
        this.init(socket.getOutputStream(), buffersize, params);
    }
}
