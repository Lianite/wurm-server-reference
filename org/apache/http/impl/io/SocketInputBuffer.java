// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import java.net.SocketTimeoutException;
import java.io.IOException;
import org.apache.http.params.HttpParams;
import java.net.Socket;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.EofSensor;

@NotThreadSafe
public class SocketInputBuffer extends AbstractSessionInputBuffer implements EofSensor
{
    private final Socket socket;
    private boolean eof;
    
    public SocketInputBuffer(final Socket socket, int buffersize, final HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        this.socket = socket;
        this.eof = false;
        if (buffersize < 0) {
            buffersize = socket.getReceiveBufferSize();
        }
        if (buffersize < 1024) {
            buffersize = 1024;
        }
        this.init(socket.getInputStream(), buffersize, params);
    }
    
    protected int fillBuffer() throws IOException {
        final int i = super.fillBuffer();
        this.eof = (i == -1);
        return i;
    }
    
    public boolean isDataAvailable(final int timeout) throws IOException {
        boolean result = this.hasBufferedData();
        if (!result) {
            final int oldtimeout = this.socket.getSoTimeout();
            try {
                this.socket.setSoTimeout(timeout);
                this.fillBuffer();
                result = this.hasBufferedData();
            }
            catch (SocketTimeoutException ex) {
                throw ex;
            }
            finally {
                this.socket.setSoTimeout(oldtimeout);
            }
        }
        return result;
    }
    
    public boolean isEof() {
        return this.eof;
    }
}
