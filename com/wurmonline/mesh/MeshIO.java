// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

import com.wurmonline.math.TilePos;
import java.nio.MappedByteBuffer;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.logging.Level;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

public final class MeshIO
{
    private static final Logger logger;
    private static final long MAGIC_NUMBER = 5136955264682433437L;
    private static final int ROW_COUNT = 128;
    private int size_level;
    private int size;
    private short maxHeight;
    private short[] maxHeightCoord;
    public final int[] data;
    private FileChannel fileChannel;
    private ByteBuffer tmpBuf;
    private final IntBuffer tmpBufInt;
    private boolean[] rowDirty;
    private int rowId;
    private final int linesPerRow;
    private byte[] distantTerrainTypes;
    private static boolean allocateDirectBuffers;
    
    private MeshIO(final int size_level, final int[] data) {
        this.maxHeight = 0;
        this.maxHeightCoord = new short[] { -1, -1 };
        this.rowDirty = new boolean[128];
        this.rowId = 0;
        if (size_level > 32) {
            throw new IllegalArgumentException("I'm fairly sure you didn't mean to REALLY create a map 2^" + size_level + " units wide.");
        }
        if (size_level < 4) {
            throw new IllegalArgumentException("Maps can't be smaller than 2^4.");
        }
        this.size_level = size_level;
        this.size = 1 << size_level;
        this.data = data;
        int holes = 0;
        for (int x = 0; x < data.length; ++x) {
            if (Tiles.decodeType(data[x]) == Tiles.Tile.TILE_HOLE.getId()) {
                ++holes;
            }
        }
        MeshIO.logger.info("Holes=" + holes);
        if (MeshIO.allocateDirectBuffers) {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a direct byte buffer for writing the Mesh.");
            }
            this.tmpBuf = ByteBuffer.allocateDirect(this.size * 4);
        }
        else {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a heap byte buffer for writing the Mesh.");
            }
            this.tmpBuf = ByteBuffer.allocate(this.size * 4);
        }
        this.tmpBufInt = this.tmpBuf.asIntBuffer();
        this.linesPerRow = this.size / 128;
        MeshIO.logger.info("size_level: " + size_level);
        MeshIO.logger.info("size: " + this.size);
        MeshIO.logger.info("data length: " + data.length);
        MeshIO.logger.info("linesPerRow: " + this.linesPerRow);
    }
    
    private MeshIO(final ByteBuffer header) throws IOException {
        this.maxHeight = 0;
        this.maxHeightCoord = new short[] { -1, -1 };
        this.rowDirty = new boolean[128];
        this.rowId = 0;
        this.readHeader(header);
        this.data = new int[this.size * this.size + 1];
        if (MeshIO.allocateDirectBuffers) {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a direct byte buffer for writing the Mesh.");
            }
            this.tmpBuf = ByteBuffer.allocateDirect(this.size * 4);
        }
        else {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a heap byte buffer for writing the Mesh.");
            }
            this.tmpBuf = ByteBuffer.allocate(this.size * 4);
        }
        this.tmpBufInt = this.tmpBuf.asIntBuffer();
        this.linesPerRow = this.size / 128;
    }
    
    private void readHeader(final ByteBuffer header) throws IOException {
        final long magicNumber = header.getLong();
        if (MeshIO.logger.isLoggable(Level.FINE)) {
            MeshIO.logger.fine("Magic Number: " + magicNumber);
        }
        if (magicNumber != 5136955264682433437L) {
            throw new IOException("Bad magic number! This is not a valid map file.");
        }
        final byte headerVersionNumber = header.get();
        if (MeshIO.logger.isLoggable(Level.FINE)) {
            MeshIO.logger.fine("Version Number: " + headerVersionNumber);
        }
        if (headerVersionNumber == 0) {
            this.size_level = header.get();
            this.size = 1 << this.size_level;
        }
        if (MeshIO.logger.isLoggable(Level.FINE)) {
            MeshIO.logger.fine("size level: " + this.size_level);
            MeshIO.logger.fine("size: " + this.size);
        }
    }
    
    private void writeHeader(final ByteBuffer header) throws IOException {
        header.putLong(5136955264682433437L);
        header.put((byte)0);
        header.put((byte)this.size_level);
    }
    
    public static MeshIO createMap(final String filename, final int level, final int[] data) throws IOException {
        final MeshIO meshIO = new MeshIO(level, data);
        final FileChannel channel = new RandomAccessFile(filename, "rw").getChannel();
        MeshIO.logger.info(filename + " size is " + channel.size());
        MeshIO.logger.info("Data array length is " + data.length);
        final MappedByteBuffer header = channel.map(FileChannel.MapMode.READ_WRITE, 0L, 1024L);
        if (MeshIO.logger.isLoggable(Level.FINER)) {
            MeshIO.logger.finer("Header capacity: " + header.capacity() + ", header.limit: " + header.limit() + ", header.position: " + header.position());
        }
        meshIO.writeHeader(header);
        MeshIO.logger.info("meshIO size is " + meshIO.size);
        ByteBuffer stripBuffer;
        if (MeshIO.allocateDirectBuffers) {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a direct byte buffer for creating the map: " + filename);
            }
            stripBuffer = ByteBuffer.allocateDirect(meshIO.size * 4);
        }
        else {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a heap byte buffer for creating the map: " + filename);
            }
            stripBuffer = ByteBuffer.allocate(meshIO.size * 4);
        }
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
        final long lStart = System.nanoTime();
        final FileChannel channel = new RandomAccessFile(filename, "rw").getChannel();
        MeshIO.logger.info(filename + " size is " + channel.size());
        final MappedByteBuffer header = channel.map(FileChannel.MapMode.READ_ONLY, 0L, 1024L);
        if (MeshIO.logger.isLoggable(Level.FINE)) {
            MeshIO.logger.fine("Header capacity: " + header.capacity() + ", header.limit: " + header.limit() + ", header.position: " + header.position());
        }
        final MeshIO meshIO = new MeshIO(header);
        ByteBuffer stripBuffer;
        if (MeshIO.allocateDirectBuffers) {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a direct byte buffer for reading the mesh: " + filename);
            }
            stripBuffer = ByteBuffer.allocateDirect(meshIO.size * 4);
        }
        else {
            if (MeshIO.logger.isLoggable(Level.FINE)) {
                MeshIO.logger.fine("Will allocate a heap byte buffer for reading the mesh: " + filename);
            }
            stripBuffer = ByteBuffer.allocate(meshIO.size * 4);
        }
        for (int i = 0; i < meshIO.size; ++i) {
            stripBuffer.clear();
            stripBuffer.limit(meshIO.size * 4);
            stripBuffer.position(0);
            channel.read(stripBuffer, 1024 + meshIO.size * 4 * i);
            stripBuffer.flip();
            stripBuffer.asIntBuffer().get(meshIO.data, meshIO.size * i, meshIO.size);
            stripBuffer.rewind();
            if (filename.contains("top_layer")) {
                final IntBuffer tmp = stripBuffer.asIntBuffer();
                for (int x = 0; x < meshIO.size; ++x) {
                    final int tile = tmp.get(x);
                    final short height = Tiles.decodeHeight(tile);
                    if (height > meshIO.getMaxHeight()) {
                        meshIO.setMaxHeight(height);
                        meshIO.setMaxHeightCoord((short)x, (short)i);
                    }
                }
            }
        }
        meshIO.fileChannel = channel;
        if (MeshIO.logger.isLoggable(Level.FINE)) {
            final long lElapsedTime = System.nanoTime() - lStart;
            MeshIO.logger.fine("Loaded Mesh '" + filename + "', that took " + lElapsedTime / 1000000.0f + ", millis.");
        }
        return meshIO;
    }
    
    public void close() throws IOException {
        this.saveAllDirtyRows();
        this.fileChannel.close();
    }
    
    public int getSize() {
        return this.size;
    }
    
    public int getSizeLevel() {
        return this.size_level;
    }
    
    public final int getTile(final TilePos tilePos) {
        return this.getTile(tilePos.x, tilePos.y);
    }
    
    public final int getTile(final int x, final int y) {
        int tile = 0;
        try {
            tile = this.data[x | y << this.size_level];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            int xx = x;
            int yy = y;
            if (xx < 0) {
                xx = 0;
            }
            if (yy < 0) {
                yy = 0;
            }
            int idx = xx | yy << this.size_level;
            if (idx < 0 || idx > this.data.length) {
                idx = this.data.length - 1;
            }
            MeshIO.logger.log(Level.WARNING, "data: " + this.data.length + ", x: " + x + ", y: " + y + ", size_level: " + this.size_level + ", x | (y << size_level): " + (x | y << this.size_level), e);
            MeshIO.logger.log(Level.WARNING, "Attempting to find closest tile using: x: " + xx + ", y: " + yy + " for an index of: " + idx);
            return this.data[idx];
        }
        return tile;
    }
    
    public final void setTile(final int x, final int y, final int value) {
        this.data[x | y << this.size_level] = value;
        this.rowDirty[y / this.linesPerRow] = true;
    }
    
    @Deprecated
    public final void saveTile(final int x, final int y) throws IOException {
        this.tmpBuf.clear();
        this.tmpBuf.putInt(this.data[x | y << this.size_level]);
        this.tmpBuf.flip();
        this.fileChannel.write(this.tmpBuf, 1024 + ((x | y << this.size_level) << 2));
    }
    
    public final void saveFullRows(final int y, final int rows) throws IOException {
        this.fileChannel.position(1024 + (y << this.size_level << 2));
        for (int yy = 0; yy < rows; ++yy) {
            this.tmpBuf.clear();
            this.tmpBufInt.clear();
            this.tmpBufInt.put(this.data, y + yy << this.size_level, this.size);
            this.tmpBufInt.flip();
            this.tmpBuf.limit(this.size << 2);
            this.tmpBuf.position(0);
            this.fileChannel.write(this.tmpBuf);
        }
    }
    
    public final void saveAll() throws IOException {
        final long lStart = System.nanoTime();
        this.saveFullRows(0, this.size);
        if (MeshIO.logger.isLoggable(Level.FINE)) {
            final long lElapsedTime = System.nanoTime() - lStart;
            MeshIO.logger.fine("Saved all " + this.size + " rows that took " + lElapsedTime / 1000000.0f + ", millis.");
        }
    }
    
    public final int saveAllDirtyRows() throws IOException {
        final long lStart = System.nanoTime();
        int savedRowCount = 0;
        for (int i = 0; i < 128; ++i) {
            if (this.saveNextDirtyRow()) {
                ++savedRowCount;
            }
        }
        if (MeshIO.logger.isLoggable(Level.FINER)) {
            final long lElapsedTime = System.nanoTime() - lStart;
            if (savedRowCount > 0) {
                MeshIO.logger.finer("Saved all " + savedRowCount + " dirty rows that took " + lElapsedTime / 1000000.0f + ", millis.");
            }
        }
        return savedRowCount;
    }
    
    public final boolean saveNextDirtyRow() throws IOException {
        boolean lRowWasDirty = false;
        if (this.rowDirty[this.rowId]) {
            lRowWasDirty = true;
            final long lStart = System.nanoTime();
            this.saveFullRows(this.rowId * this.linesPerRow, this.linesPerRow);
            this.rowDirty[this.rowId] = false;
            if (MeshIO.logger.isLoggable(Level.FINEST)) {
                final long lElapsedTime = System.nanoTime() - lStart;
                MeshIO.logger.finest("Saved dirty row " + this.rowId + ", that took " + lElapsedTime / 1000000.0f + ", millis.");
            }
        }
        ++this.rowId;
        if (this.rowId >= 128) {
            this.rowId = 0;
            if (MeshIO.logger.isLoggable(Level.FINEST)) {
                MeshIO.logger.finest("Resetting dirty row id as it has reached 128");
            }
        }
        return lRowWasDirty;
    }
    
    int[] cloneData() {
        final int[] data2 = new int[this.data.length];
        System.arraycopy(this.data, 0, data2, 0, this.data.length);
        return data2;
    }
    
    public void calcDistantTerrain() {
        this.distantTerrainTypes = new byte[this.size * this.size / 256];
        for (int xT = 0; xT < this.size / 16; ++xT) {
            for (int yT = 0; yT < this.size / 16; ++yT) {
                final int[] counts = new int[256];
                for (int x = xT * 16; x < xT * 16 + 16; ++x) {
                    for (int y = yT * 16; y < yT * 16 + 16; ++y) {
                        final int type = Tiles.decodeType(this.getTile(x, y)) & 0xFF;
                        final int[] array = counts;
                        final int n = type;
                        ++array[n];
                    }
                }
                int mostCommon = 0;
                for (int i = 0; i < 256; ++i) {
                    if (counts[i] > counts[mostCommon]) {
                        mostCommon = i;
                    }
                }
                this.distantTerrainTypes[xT + yT * (this.size / 16)] = (byte)mostCommon;
            }
        }
    }
    
    public byte[] getDistantTerrainTypes() {
        return this.distantTerrainTypes;
    }
    
    public int[] getData() {
        return this.data;
    }
    
    public void setAllRowsDirty() {
        for (int yy = 0; yy < 128; ++yy) {
            this.rowDirty[yy] = true;
        }
    }
    
    public static boolean isAllocateDirectBuffers() {
        return MeshIO.allocateDirectBuffers;
    }
    
    public static void setAllocateDirectBuffers(final boolean newAllocateDirectBuffers) {
        MeshIO.allocateDirectBuffers = newAllocateDirectBuffers;
    }
    
    @Override
    public String toString() {
        return "MeshIO [Size: " + this.size + ", linesPerRow: " + this.linesPerRow + ", rowId: " + this.rowId + ", size_level: " + this.size_level + "]@" + this.hashCode();
    }
    
    public short getMaxHeight() {
        return this.maxHeight;
    }
    
    public void setMaxHeight(final short maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    public short[] getMaxHeightCoord() {
        return this.maxHeightCoord;
    }
    
    public void setMaxHeightCoord(final short x, final short y) {
        this.maxHeightCoord[0] = x;
        this.maxHeightCoord[1] = y;
    }
    
    static {
        logger = Logger.getLogger(MeshIO.class.getName());
    }
}
