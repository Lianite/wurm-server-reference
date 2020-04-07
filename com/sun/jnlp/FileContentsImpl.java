// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.net.MalformedURLException;
import java.io.OutputStream;
import java.security.PrivilegedActionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import javax.jnlp.JNLPRandomAccessFile;
import java.net.URL;
import java.io.File;
import javax.jnlp.FileContents;

public final class FileContentsImpl implements FileContents
{
    private String _name;
    private File _file;
    private long _limit;
    private URL _url;
    private JNLPRandomAccessFile _raf;
    private PersistenceServiceImpl _psCallback;
    
    FileContentsImpl(final File file, final long limit) throws IOException {
        this._name = null;
        this._file = null;
        this._limit = Long.MAX_VALUE;
        this._url = null;
        this._raf = null;
        this._psCallback = null;
        this._file = file;
        this._limit = limit;
        this._name = this._file.getName();
    }
    
    FileContentsImpl(final File file, final PersistenceServiceImpl psCallback, final URL url, final long limit) {
        this._name = null;
        this._file = null;
        this._limit = Long.MAX_VALUE;
        this._url = null;
        this._raf = null;
        this._psCallback = null;
        this._file = file;
        this._url = url;
        this._psCallback = psCallback;
        this._limit = limit;
        final int lastIndex = url.getFile().lastIndexOf(47);
        this._name = ((lastIndex != -1) ? url.getFile().substring(lastIndex + 1) : url.getFile());
    }
    
    public String getName() {
        return this._name;
    }
    
    public long getLength() {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
            public Object run() {
                return new Long(FileContentsImpl.this._file.length());
            }
        });
    }
    
    public InputStream getInputStream() throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return new FileInputStream(FileContentsImpl.this._file);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw this.rethrowException(ex);
        }
    }
    
    public OutputStream getOutputStream(final boolean b) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<OutputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return new MeteredFileOutputStream(FileContentsImpl.this._file, !b, FileContentsImpl.this);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw this.rethrowException(ex);
        }
    }
    
    public boolean canRead() throws IOException {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return new Boolean(FileContentsImpl.this._file.canRead());
            }
        });
    }
    
    public boolean canWrite() throws IOException {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return new Boolean(FileContentsImpl.this._file.canWrite());
            }
        });
    }
    
    public JNLPRandomAccessFile getRandomAccessFile(final String s) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<JNLPRandomAccessFile>)new PrivilegedExceptionAction() {
                public Object run() throws MalformedURLException, IOException {
                    return new JNLPRandomAccessFileImpl(FileContentsImpl.this._file, s, FileContentsImpl.this);
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw this.rethrowException(ex);
        }
    }
    
    public long getMaxLength() throws IOException {
        return this._limit;
    }
    
    public long setMaxLength(final long limit) throws IOException {
        if (this._psCallback != null) {
            return this._limit = this._psCallback.setMaxLength(this._url, limit);
        }
        return this._limit = limit;
    }
    
    private IOException rethrowException(final PrivilegedActionException ex) throws IOException {
        final Exception exception = ex.getException();
        if (exception instanceof IOException) {
            throw (IOException)exception;
        }
        if (exception instanceof RuntimeException) {
            throw (RuntimeException)exception;
        }
        throw new IOException(exception.getMessage());
    }
}
