// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.io.IOException;
import com.sun.deploy.resources.ResourceManager;
import java.io.File;
import javax.jnlp.FileContents;
import java.io.RandomAccessFile;
import javax.jnlp.JNLPRandomAccessFile;

public final class JNLPRandomAccessFileImpl implements JNLPRandomAccessFile
{
    private RandomAccessFile _raf;
    private FileContents _contents;
    private long _length;
    private String _message;
    
    JNLPRandomAccessFileImpl(final File file, final String s, final FileContents contents) throws IOException {
        this._raf = null;
        this._contents = null;
        this._length = 0L;
        this._message = null;
        this._raf = new RandomAccessFile(file, s);
        this._length = this._raf.length();
        this._contents = contents;
        if (this._contents == null) {
            throw new IllegalArgumentException("FileContents can not be null");
        }
        if (this._message == null) {
            this._message = ResourceManager.getString("APIImpl.persistence.filesizemessage");
        }
    }
    
    public void close() throws IOException {
        this._raf.close();
    }
    
    public long length() throws IOException {
        return this._raf.length();
    }
    
    public long getFilePointer() throws IOException {
        return this._raf.getFilePointer();
    }
    
    public int read() throws IOException {
        return this._raf.read();
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this._raf.read(array, n, n2);
    }
    
    public int read(final byte[] array) throws IOException {
        return this._raf.read(array);
    }
    
    public void readFully(final byte[] array) throws IOException {
        this._raf.readFully(array);
    }
    
    public void readFully(final byte[] array, final int n, final int n2) throws IOException {
        this._raf.readFully(array, n, n2);
    }
    
    public int skipBytes(final int n) throws IOException {
        return this._raf.skipBytes(n);
    }
    
    public boolean readBoolean() throws IOException {
        return this._raf.readBoolean();
    }
    
    public byte readByte() throws IOException {
        return this._raf.readByte();
    }
    
    public int readUnsignedByte() throws IOException {
        return this._raf.readUnsignedByte();
    }
    
    public short readShort() throws IOException {
        return this._raf.readShort();
    }
    
    public int readUnsignedShort() throws IOException {
        return this._raf.readUnsignedShort();
    }
    
    public char readChar() throws IOException {
        return this._raf.readChar();
    }
    
    public int readInt() throws IOException {
        return this._raf.readInt();
    }
    
    public long readLong() throws IOException {
        return this._raf.readLong();
    }
    
    public float readFloat() throws IOException {
        return this._raf.readFloat();
    }
    
    public double readDouble() throws IOException {
        return this._raf.readDouble();
    }
    
    public String readLine() throws IOException {
        return this._raf.readLine();
    }
    
    public String readUTF() throws IOException {
        return this._raf.readUTF();
    }
    
    public void seek(final long n) throws IOException {
        this._raf.seek(n);
    }
    
    public void setLength(final long length) throws IOException {
        if (length > this._contents.getMaxLength()) {
            throw new IOException(this._message);
        }
        this._raf.setLength(length);
    }
    
    public void write(final int n) throws IOException {
        this.checkWrite(1);
        this._raf.write(n);
    }
    
    public void write(final byte[] array) throws IOException {
        if (array != null) {
            this.checkWrite(array.length);
        }
        this._raf.write(array);
    }
    
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.checkWrite(n2);
        this._raf.write(array, n, n2);
    }
    
    public void writeBoolean(final boolean b) throws IOException {
        this.checkWrite(1);
        this._raf.writeBoolean(b);
    }
    
    public void writeByte(final int n) throws IOException {
        this.checkWrite(1);
        this._raf.writeByte(n);
    }
    
    public void writeShort(final int n) throws IOException {
        this.checkWrite(2);
        this._raf.writeShort(n);
    }
    
    public void writeChar(final int n) throws IOException {
        this.checkWrite(2);
        this._raf.writeChar(n);
    }
    
    public void writeInt(final int n) throws IOException {
        this.checkWrite(4);
        this._raf.writeInt(n);
    }
    
    public void writeLong(final long n) throws IOException {
        this.checkWrite(8);
        this._raf.writeLong(n);
    }
    
    public void writeFloat(final float n) throws IOException {
        this.checkWrite(4);
        this._raf.writeFloat(n);
    }
    
    public void writeDouble(final double n) throws IOException {
        this.checkWrite(8);
        this._raf.writeDouble(n);
    }
    
    public void writeBytes(final String s) throws IOException {
        if (s != null) {
            this.checkWrite(s.length());
        }
        this._raf.writeBytes(s);
    }
    
    public void writeChars(final String s) throws IOException {
        if (s != null) {
            this.checkWrite(s.length() * 2);
        }
        this._raf.writeChars(s);
    }
    
    public void writeUTF(final String s) throws IOException {
        if (s != null) {
            this.checkWrite(this.getUTFLen(s));
        }
        this._raf.writeUTF(s);
    }
    
    private int getUTFLen(final String s) {
        final int length = s.length();
        final char[] array = new char[length];
        s.getChars(0, length, array, 0);
        int n = 2;
        for (final char c : array) {
            if (c >= '\u0001' && c <= '\u007f') {
                ++n;
            }
            else if (c > '\u07ff') {
                n += 3;
            }
            else {
                n += 2;
            }
        }
        return n;
    }
    
    private void checkWrite(final int n) throws IOException {
        if (this._raf.getFilePointer() + n > this._contents.getMaxLength()) {
            throw new IOException(this._message);
        }
    }
}
