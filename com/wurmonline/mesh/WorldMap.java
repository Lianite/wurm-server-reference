// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public final class WorldMap
{
    private static final byte[] HEADER_PREFIX;
    private static final byte FILE_FORMAT_VERSION = 0;
    private static final int HEADER_SIZE = 12;
    private final int width;
    private Chunk[][] chunks;
    private final FileChannel channel;
    private final ByteBuffer byteBuffer;
    
    private WorldMap(final FileChannel channel, final int width, final int height) {
        this.byteBuffer = ByteBuffer.allocate(16384);
        this.width = width;
        this.chunks = new Chunk[width][height];
        this.channel = channel;
    }
    
    protected void freeChunk(final int x, final int y) throws IOException {
        if (this.chunks[x][y] != null) {
            if (this.chunks[x][y].isDirty()) {
                final long pos = (x + y * this.width) * 16384L + 12L;
                this.channel.position(pos);
                this.byteBuffer.clear();
                final IntBuffer ib = this.byteBuffer.asIntBuffer();
                this.chunks[x][y].encode(ib);
                this.byteBuffer.position(ib.position() * 4);
                this.byteBuffer.flip();
                this.channel.write(this.byteBuffer);
            }
            this.chunks[x][y] = null;
        }
    }
    
    protected void loadChunk(final int x, final int y) throws IOException {
        if (this.chunks[x][y] == null) {
            final long pos = (x + y * this.width) * 16384L + 12L;
            this.channel.position(pos);
            this.byteBuffer.clear();
            this.channel.read(this.byteBuffer);
            this.byteBuffer.flip();
            final IntBuffer ib = this.byteBuffer.asIntBuffer();
            this.chunks[x][y] = Chunk.decode(ib);
            this.byteBuffer.position(ib.position() * 4);
        }
    }
    
    public static WorldMap getWorldMap(final File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("Failed to locate mapfile");
        }
        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        final FileChannel channel = randomAccessFile.getChannel();
        if (file.length() < 12L) {
            throw new IOException("Map file too small to even contain a header.");
        }
        channel.position(0L);
        final ByteBuffer header = ByteBuffer.allocate(12);
        channel.read(header);
        header.flip();
        final byte[] prefix = new byte[7];
        header.get(prefix);
        final byte version = header.get();
        final ShortBuffer headerShort = header.asShortBuffer();
        final short width = headerShort.get();
        final short height = headerShort.get();
        if (!Arrays.equals(prefix, WorldMap.HEADER_PREFIX)) {
            throw new IOException("Bad map file header: " + new String(prefix) + ".");
        }
        if (version != 0) {
            throw new IOException("Bad map file format version number.");
        }
        if (file.length() != width * height * 16384 + 12) {
            throw new IOException("Found the map file, but it was the wrong size. (found " + file.length() + ", expected " + (width * height * 16384 + 12) + ")");
        }
        return new WorldMap(channel, width, height);
    }
    
    static {
        HEADER_PREFIX = "WURMMAP".getBytes();
    }
}
