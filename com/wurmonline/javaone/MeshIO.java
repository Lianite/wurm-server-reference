// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.javaone;

import java.nio.MappedByteBuffer;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public final class MeshIO
{
    public static final long MAGIC_NUMBER = 5136955264682433437L;
    private static final int ROW_COUNT = 512;
    private int sizeLevel;
    private int size;
    private final int[] data;
    private FileChannel fileChannel;
    private ByteBuffer tmpBuf;
    private IntBuffer tmpBufInt;
    private boolean[] rowDirty;
    private int rowId;
    private int linesPerRow;
    
    private MeshIO(final int aSizeLevel, final int[] aData) {
        this.rowDirty = new boolean[512];
        this.rowId = 0;
        if (aSizeLevel > 32) {
            throw new IllegalArgumentException("I'm fairly sure you didn't mean to REALLY create a map 2^" + aSizeLevel + " units wide.");
        }
        if (aSizeLevel < 4) {
            throw new IllegalArgumentException("Maps can't be smaller than 2^4.");
        }
        this.sizeLevel = aSizeLevel;
        this.size = 1 << aSizeLevel;
        this.data = aData;
        this.tmpBuf = ByteBuffer.allocate(this.size * 4);
        this.tmpBufInt = this.tmpBuf.asIntBuffer();
        this.linesPerRow = this.size / 512;
    }
    
    private MeshIO(final ByteBuffer header) throws IOException {
        this.rowDirty = new boolean[512];
        this.rowId = 0;
        this.readHeader(header);
        this.data = new int[this.size * this.size];
        this.tmpBuf = ByteBuffer.allocate(this.size * 4);
        this.tmpBufInt = this.tmpBuf.asIntBuffer();
        this.linesPerRow = this.size / 512;
    }
    
    private void readHeader(final ByteBuffer header) throws IOException {
        final long magicNumber = header.getLong();
        if (magicNumber != 5136955264682433437L) {
            throw new IOException("Bad magic number! This is not a valid map file.");
        }
        final byte headerVersionNumber = header.get();
        if (headerVersionNumber == 0) {
            this.sizeLevel = header.get();
            this.size = 1 << this.sizeLevel;
        }
    }
    
    private void writeHeader(final ByteBuffer header) throws IOException {
        header.putLong(5136955264682433437L);
        header.put((byte)0);
        header.put((byte)this.sizeLevel);
    }
    
    public static MeshIO createMap(final String filename, final int level, final int[] data) throws IOException {
        final MeshIO meshIO = new MeshIO(level, data);
        final FileChannel channel = new RandomAccessFile(filename, "rw").getChannel();
        final MappedByteBuffer header = channel.map(FileChannel.MapMode.READ_WRITE, 0L, 1024L);
        meshIO.writeHeader(header);
        final ByteBuffer stripBuffer = ByteBuffer.allocate(meshIO.size * 4);
        for (int i = 0; i < meshIO.size; ++i) {
            stripBuffer.clear();
            stripBuffer.asIntBuffer().put(meshIO.data, meshIO.size * i, meshIO.size);
            stripBuffer.flip();
            stripBuffer.limit(meshIO.size * 4);
            stripBuffer.position(0);
            channel.write(stripBuffer, 1024 + meshIO.size * 4 * i);
        }
        meshIO.fileChannel = channel;
        return meshIO;
    }
    
    public static MeshIO open(final String filename) throws IOException {
        final FileChannel channel = new RandomAccessFile(filename, "rw").getChannel();
        final MappedByteBuffer header = channel.map(FileChannel.MapMode.READ_ONLY, 0L, 1024L);
        final MeshIO meshIO = new MeshIO(header);
        final ByteBuffer stripBuffer = ByteBuffer.allocate(meshIO.size * 4);
        for (int i = 0; i < meshIO.size; ++i) {
            stripBuffer.clear();
            stripBuffer.limit(meshIO.size * 4);
            stripBuffer.position(0);
            channel.read(stripBuffer, 1024 + meshIO.size * 4 * i);
            stripBuffer.flip();
            stripBuffer.asIntBuffer().get(meshIO.data, meshIO.size * i, meshIO.size);
        }
        meshIO.fileChannel = channel;
        return meshIO;
    }
    
    public void close() throws IOException {
        this.saveAllDirtyRows();
        this.fileChannel.close();
    }
    
    public int getSize() {
        return this.size;
    }
    
    public final int getTile(final int x, final int y) {
        return this.data[x | y << this.sizeLevel];
    }
    
    public final void setTile(final int x, final int y, final int value) {
        this.data[x | y << this.sizeLevel] = value;
        this.rowDirty[y / this.linesPerRow] = true;
    }
    
    @Deprecated
    public final void saveTile(final int x, final int y) throws IOException {
        this.tmpBuf.clear();
        this.tmpBuf.putInt(this.data[x | y << this.sizeLevel]);
        this.tmpBuf.flip();
        this.fileChannel.write(this.tmpBuf, 1024 + ((x | y << this.sizeLevel) << 2));
    }
    
    public final void saveFullRows(final int y, final int rows) throws IOException {
        this.fileChannel.position(1024 + (y << this.sizeLevel << 2));
        for (int yy = 0; yy < rows; ++yy) {
            this.tmpBuf.clear();
            this.tmpBufInt.clear();
            this.tmpBufInt.put(this.data, y + yy << this.sizeLevel, this.size);
            this.tmpBufInt.flip();
            this.tmpBuf.limit(this.size << 2);
            this.tmpBuf.position(0);
            this.fileChannel.write(this.tmpBuf);
        }
    }
    
    public final void saveAll() throws IOException {
        this.saveFullRows(0, this.size);
    }
    
    public final void saveAllDirtyRows() throws IOException {
        for (int i = 0; i < 512; ++i) {
            this.saveNextDirtyRow();
        }
    }
    
    public final void saveNextDirtyRow() throws IOException {
        if (this.rowDirty[this.rowId]) {
            this.saveFullRows(this.rowId * this.linesPerRow, this.linesPerRow);
            this.rowDirty[this.rowId] = false;
        }
        ++this.rowId;
        if (this.rowId >= 512) {
            this.rowId = 0;
        }
    }
    
    public int[] cloneData() {
        final int[] data2 = new int[this.data.length];
        System.arraycopy(this.data, 0, data2, 0, this.data.length);
        return data2;
    }
}
