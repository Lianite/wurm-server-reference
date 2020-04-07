// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.mesh;

import java.nio.IntBuffer;

public class Chunk
{
    private static final int CHUNK_WIDTH = 64;
    private static final int BYTES_PER_TILE = 4;
    static final int BYTES_PER_CHUNK = 16384;
    private final int[][] tiles;
    private boolean dirty;
    
    Chunk() {
        this.tiles = new int[64][64];
        this.dirty = false;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void makeDirty() {
        this.dirty = true;
    }
    
    public static Chunk decode(final IntBuffer bb) {
        final Chunk chunk = new Chunk();
        for (int i = 0; i < 64; ++i) {
            bb.get(chunk.tiles[i]);
        }
        return chunk;
    }
    
    public void encode(final IntBuffer bb) {
        for (int i = 0; i < 64; ++i) {
            bb.put(this.tiles[i]);
        }
    }
}
