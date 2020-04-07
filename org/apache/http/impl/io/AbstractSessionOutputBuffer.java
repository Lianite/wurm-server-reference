// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.impl.io;

import org.apache.http.io.HttpTransportMetrics;
import java.nio.charset.CoderResult;
import org.apache.http.util.CharArrayBuffer;
import java.nio.CharBuffer;
import java.io.IOException;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.HttpParams;
import java.nio.charset.CodingErrorAction;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import org.apache.http.util.ByteArrayBuffer;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionOutputBuffer;

@NotThreadSafe
public abstract class AbstractSessionOutputBuffer implements SessionOutputBuffer, BufferInfo
{
    private static final Charset ASCII;
    private static final byte[] CRLF;
    private OutputStream outstream;
    private ByteArrayBuffer buffer;
    private Charset charset;
    private CharsetEncoder encoder;
    private ByteBuffer bbuf;
    private boolean ascii;
    private int minChunkLimit;
    private HttpTransportMetricsImpl metrics;
    private CodingErrorAction onMalformedInputAction;
    private CodingErrorAction onUnMappableInputAction;
    
    public AbstractSessionOutputBuffer() {
        this.ascii = true;
        this.minChunkLimit = 512;
    }
    
    protected void init(final OutputStream outstream, final int buffersize, final HttpParams params) {
        if (outstream == null) {
            throw new IllegalArgumentException("Input stream may not be null");
        }
        if (buffersize <= 0) {
            throw new IllegalArgumentException("Buffer size may not be negative or zero");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.outstream = outstream;
        this.buffer = new ByteArrayBuffer(buffersize);
        this.charset = Charset.forName(HttpProtocolParams.getHttpElementCharset(params));
        this.ascii = this.charset.equals(AbstractSessionOutputBuffer.ASCII);
        this.encoder = null;
        this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
        this.metrics = this.createTransportMetrics();
        this.onMalformedInputAction = HttpProtocolParams.getMalformedInputAction(params);
        this.onUnMappableInputAction = HttpProtocolParams.getUnmappableInputAction(params);
    }
    
    protected HttpTransportMetricsImpl createTransportMetrics() {
        return new HttpTransportMetricsImpl();
    }
    
    public int capacity() {
        return this.buffer.capacity();
    }
    
    public int length() {
        return this.buffer.length();
    }
    
    public int available() {
        return this.capacity() - this.length();
    }
    
    protected void flushBuffer() throws IOException {
        final int len = this.buffer.length();
        if (len > 0) {
            this.outstream.write(this.buffer.buffer(), 0, len);
            this.buffer.clear();
            this.metrics.incrementBytesTransferred(len);
        }
    }
    
    public void flush() throws IOException {
        this.flushBuffer();
        this.outstream.flush();
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            return;
        }
        if (len > this.minChunkLimit || len > this.buffer.capacity()) {
            this.flushBuffer();
            this.outstream.write(b, off, len);
            this.metrics.incrementBytesTransferred(len);
        }
        else {
            final int freecapacity = this.buffer.capacity() - this.buffer.length();
            if (len > freecapacity) {
                this.flushBuffer();
            }
            this.buffer.append(b, off, len);
        }
    }
    
    public void write(final byte[] b) throws IOException {
        if (b == null) {
            return;
        }
        this.write(b, 0, b.length);
    }
    
    public void write(final int b) throws IOException {
        if (this.buffer.isFull()) {
            this.flushBuffer();
        }
        this.buffer.append(b);
    }
    
    public void writeLine(final String s) throws IOException {
        if (s == null) {
            return;
        }
        if (s.length() > 0) {
            if (this.ascii) {
                for (int i = 0; i < s.length(); ++i) {
                    this.write(s.charAt(i));
                }
            }
            else {
                final CharBuffer cbuf = CharBuffer.wrap(s);
                this.writeEncoded(cbuf);
            }
        }
        this.write(AbstractSessionOutputBuffer.CRLF);
    }
    
    public void writeLine(final CharArrayBuffer charbuffer) throws IOException {
        if (charbuffer == null) {
            return;
        }
        if (this.ascii) {
            int off = 0;
            int chunk;
            for (int remaining = charbuffer.length(); remaining > 0; remaining -= chunk) {
                chunk = this.buffer.capacity() - this.buffer.length();
                chunk = Math.min(chunk, remaining);
                if (chunk > 0) {
                    this.buffer.append(charbuffer, off, chunk);
                }
                if (this.buffer.isFull()) {
                    this.flushBuffer();
                }
                off += chunk;
            }
        }
        else {
            final CharBuffer cbuf = CharBuffer.wrap(charbuffer.buffer(), 0, charbuffer.length());
            this.writeEncoded(cbuf);
        }
        this.write(AbstractSessionOutputBuffer.CRLF);
    }
    
    private void writeEncoded(final CharBuffer cbuf) throws IOException {
        if (!cbuf.hasRemaining()) {
            return;
        }
        if (this.encoder == null) {
            (this.encoder = this.charset.newEncoder()).onMalformedInput(this.onMalformedInputAction);
            this.encoder.onUnmappableCharacter(this.onUnMappableInputAction);
        }
        if (this.bbuf == null) {
            this.bbuf = ByteBuffer.allocate(1024);
        }
        this.encoder.reset();
        while (cbuf.hasRemaining()) {
            final CoderResult result = this.encoder.encode(cbuf, this.bbuf, true);
            this.handleEncodingResult(result);
        }
        final CoderResult result = this.encoder.flush(this.bbuf);
        this.handleEncodingResult(result);
        this.bbuf.clear();
    }
    
    private void handleEncodingResult(final CoderResult result) throws IOException {
        if (result.isError()) {
            result.throwException();
        }
        this.bbuf.flip();
        while (this.bbuf.hasRemaining()) {
            this.write(this.bbuf.get());
        }
        this.bbuf.compact();
    }
    
    public HttpTransportMetrics getMetrics() {
        return this.metrics;
    }
    
    static {
        ASCII = Charset.forName("US-ASCII");
        CRLF = new byte[] { 13, 10 };
    }
}
