// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.client.entity;

import java.util.zip.InflaterInputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;

public class DeflateDecompressingEntity extends DecompressingEntity
{
    public DeflateDecompressingEntity(final HttpEntity entity) {
        super(entity);
    }
    
    InputStream decorate(final InputStream wrapped) throws IOException {
        final byte[] peeked = new byte[6];
        final PushbackInputStream pushback = new PushbackInputStream(wrapped, peeked.length);
        final int headerLength = pushback.read(peeked);
        if (headerLength == -1) {
            throw new IOException("Unable to read the response");
        }
        final byte[] dummy = { 0 };
        final Inflater inf = new Inflater();
        try {
            int n;
            while ((n = inf.inflate(dummy)) == 0) {
                if (inf.finished()) {
                    throw new IOException("Unable to read the response");
                }
                if (inf.needsDictionary()) {
                    break;
                }
                if (!inf.needsInput()) {
                    continue;
                }
                inf.setInput(peeked);
            }
            if (n == -1) {
                throw new IOException("Unable to read the response");
            }
            pushback.unread(peeked, 0, headerLength);
            return new DeflateStream(pushback, new Inflater());
        }
        catch (DataFormatException e) {
            pushback.unread(peeked, 0, headerLength);
            return new DeflateStream(pushback, new Inflater(true));
        }
        finally {
            inf.end();
        }
    }
    
    public Header getContentEncoding() {
        return null;
    }
    
    public long getContentLength() {
        return -1L;
    }
    
    static class DeflateStream extends InflaterInputStream
    {
        private boolean closed;
        
        public DeflateStream(final InputStream in, final Inflater inflater) {
            super(in, inflater);
            this.closed = false;
        }
        
        public void close() throws IOException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.inf.end();
            super.close();
        }
    }
}
