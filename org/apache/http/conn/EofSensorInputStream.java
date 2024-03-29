// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.conn;

import java.io.IOException;
import org.apache.http.annotation.NotThreadSafe;
import java.io.InputStream;

@NotThreadSafe
public class EofSensorInputStream extends InputStream implements ConnectionReleaseTrigger
{
    protected InputStream wrappedStream;
    private boolean selfClosed;
    private final EofSensorWatcher eofWatcher;
    
    public EofSensorInputStream(final InputStream in, final EofSensorWatcher watcher) {
        if (in == null) {
            throw new IllegalArgumentException("Wrapped stream may not be null.");
        }
        this.wrappedStream = in;
        this.selfClosed = false;
        this.eofWatcher = watcher;
    }
    
    protected boolean isReadAllowed() throws IOException {
        if (this.selfClosed) {
            throw new IOException("Attempted read on closed stream.");
        }
        return this.wrappedStream != null;
    }
    
    public int read() throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            try {
                l = this.wrappedStream.read();
                this.checkEOF(l);
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return l;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            try {
                l = this.wrappedStream.read(b, off, len);
                this.checkEOF(l);
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return l;
    }
    
    public int read(final byte[] b) throws IOException {
        int l = -1;
        if (this.isReadAllowed()) {
            try {
                l = this.wrappedStream.read(b);
                this.checkEOF(l);
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return l;
    }
    
    public int available() throws IOException {
        int a = 0;
        if (this.isReadAllowed()) {
            try {
                a = this.wrappedStream.available();
            }
            catch (IOException ex) {
                this.checkAbort();
                throw ex;
            }
        }
        return a;
    }
    
    public void close() throws IOException {
        this.selfClosed = true;
        this.checkClose();
    }
    
    protected void checkEOF(final int eof) throws IOException {
        if (this.wrappedStream != null && eof < 0) {
            try {
                boolean scws = true;
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.eofDetected(this.wrappedStream);
                }
                if (scws) {
                    this.wrappedStream.close();
                }
            }
            finally {
                this.wrappedStream = null;
            }
        }
    }
    
    protected void checkClose() throws IOException {
        if (this.wrappedStream != null) {
            try {
                boolean scws = true;
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.streamClosed(this.wrappedStream);
                }
                if (scws) {
                    this.wrappedStream.close();
                }
            }
            finally {
                this.wrappedStream = null;
            }
        }
    }
    
    protected void checkAbort() throws IOException {
        if (this.wrappedStream != null) {
            try {
                boolean scws = true;
                if (this.eofWatcher != null) {
                    scws = this.eofWatcher.streamAbort(this.wrappedStream);
                }
                if (scws) {
                    this.wrappedStream.close();
                }
            }
            finally {
                this.wrappedStream = null;
            }
        }
    }
    
    public void releaseConnection() throws IOException {
        this.close();
    }
    
    public void abortConnection() throws IOException {
        this.selfClosed = true;
        this.checkAbort();
    }
}
