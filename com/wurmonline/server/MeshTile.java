// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.MeshIO;

public class MeshTile implements MiscConstants
{
    final MeshIO mesh;
    final int tilex;
    final int tiley;
    final int currentTile;
    final short currentHeight;
    
    public MeshTile(final MeshIO mesh, final int tilex, final int tiley) {
        this.mesh = mesh;
        this.tilex = Zones.safeTileX(tilex);
        this.tiley = Zones.safeTileY(tiley);
        this.currentTile = mesh.getTile(this.tilex, this.tiley);
        this.currentHeight = Tiles.decodeHeight(this.currentTile);
    }
    
    public int getEncodedTile() {
        return this.currentTile;
    }
    
    public byte getTileType() {
        return Tiles.decodeType(this.currentTile);
    }
    
    public short getTileHeight() {
        return this.currentHeight;
    }
    
    public float getHeightAsFloat() {
        return this.currentHeight / 10.0f;
    }
    
    public byte getTileData() {
        return Tiles.decodeData(this.currentTile);
    }
    
    public boolean isHole() {
        return this.getTileType() == Tiles.Tile.TILE_HOLE.id;
    }
    
    public int getLowerLip() {
        if (this.getNorthSlope() == 0 && this.getWestSlope() > 0 && this.getEastSlope() > 0) {
            return 0;
        }
        if (this.getSouthSlope() == 0 && this.getWestSlope() < 0 && this.getEastSlope() < 0) {
            return 4;
        }
        if (this.getWestSlope() == 0 && this.getNorthSlope() > 0 && this.getSouthSlope() > 0) {
            return 6;
        }
        if (this.getEastSlope() == 0 && this.getNorthSlope() < 0 && this.getSouthSlope() < 0) {
            return 2;
        }
        return -1;
    }
    
    public short getWestSlope() {
        final int southTile = this.mesh.getTile(this.tilex, this.tiley + 1);
        final short heightSW = Tiles.decodeHeight(southTile);
        return (short)(heightSW - this.currentHeight);
    }
    
    public short getNorthSlope() {
        final int eastTile = this.mesh.getTile(this.tilex + 1, this.tiley);
        final short heightNE = Tiles.decodeHeight(eastTile);
        return (short)(heightNE - this.currentHeight);
    }
    
    public short getEastSlope() {
        final int eastTile = this.mesh.getTile(this.tilex + 1, this.tiley);
        final short heightNE = Tiles.decodeHeight(eastTile);
        final int southEastTile = this.mesh.getTile(this.tilex + 1, this.tiley + 1);
        final short heightSE = Tiles.decodeHeight(southEastTile);
        return (short)(heightSE - heightNE);
    }
    
    public short getSouthSlope() {
        final int southEastTile = this.mesh.getTile(this.tilex + 1, this.tiley + 1);
        final short heightSE = Tiles.decodeHeight(southEastTile);
        final int southTile = this.mesh.getTile(this.tilex, this.tiley + 1);
        final short heightSW = Tiles.decodeHeight(southTile);
        return (short)(heightSE - heightSW);
    }
    
    public boolean checkSlopes(final int maxStraight, final int maxDiagonal) {
        if (Math.abs(this.getNorthSlope()) > maxStraight || Math.abs(this.getSouthSlope()) > maxStraight || Math.abs(this.getEastSlope()) > maxStraight || Math.abs(this.getWestSlope()) > maxStraight) {
            return true;
        }
        final int southEastTile = this.mesh.getTile(this.tilex + 1, this.tiley + 1);
        final short heightSE = Tiles.decodeHeight(southEastTile);
        if (Math.abs(this.currentHeight - heightSE) > maxDiagonal) {
            return true;
        }
        final int southTile = this.mesh.getTile(this.tilex, this.tiley + 1);
        final short heightS = Tiles.decodeHeight(southTile);
        final int eastTile = this.mesh.getTile(this.tilex + 1, this.tiley);
        final short heightE = Tiles.decodeHeight(eastTile);
        return Math.abs(heightE - heightS) > maxDiagonal;
    }
    
    public boolean isFlat() {
        return this.getNorthSlope() == 0 && this.getSouthSlope() == 0 && this.getEastSlope() == 0;
    }
    
    public MeshTile getNorthMeshTile() {
        return new MeshTile(this.mesh, this.tilex, this.tiley - 1);
    }
    
    public MeshTile getNorthWestMeshTile() {
        return new MeshTile(this.mesh, this.tilex - 1, this.tiley - 1);
    }
    
    public MeshTile getWestMeshTile() {
        return new MeshTile(this.mesh, this.tilex - 1, this.tiley);
    }
    
    public MeshTile getSouthWestMeshTile() {
        return new MeshTile(this.mesh, this.tilex - 1, this.tiley + 1);
    }
    
    public MeshTile getSouthMeshTile() {
        return new MeshTile(this.mesh, this.tilex, this.tiley + 1);
    }
    
    public MeshTile getSouthEastMeshTile() {
        return new MeshTile(this.mesh, this.tilex + 1, this.tiley + 1);
    }
    
    public MeshTile getEastMeshTile() {
        return new MeshTile(this.mesh, this.tilex + 1, this.tiley);
    }
    
    public MeshTile getNorthEastMeshTile() {
        return new MeshTile(this.mesh, this.tilex + 1, this.tiley + 1);
    }
    
    public boolean isUnder(final int height) {
        return this.currentHeight <= height || this.getSouthMeshTile().getTileHeight() <= height || this.getEastMeshTile().getTileHeight() <= height || this.getSouthEastMeshTile().getTileHeight() <= height;
    }
}
