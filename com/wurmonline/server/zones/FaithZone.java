// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.zones;

import com.wurmonline.server.Players;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.Server;
import com.wurmonline.server.Features;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.deities.Deity;

public final class FaithZone
{
    private final Deity[] activeGods;
    private final byte[] newFaiths;
    private short startX;
    private short startY;
    private short endX;
    private short endY;
    public Deity currentRuler;
    private int strength;
    private static int nums;
    private int num;
    private static final Logger logger;
    private Item altar;
    private int areaQL;
    
    public FaithZone(final Item altar) {
        this.currentRuler = null;
        this.strength = 0;
        this.num = 0;
        this.altar = altar;
        this.updateArea();
        this.activeGods = null;
        this.newFaiths = null;
        this.currentRuler = altar.getBless();
    }
    
    public FaithZone(final short startx, final short starty, final short endx, final short endy) {
        this.currentRuler = null;
        this.strength = 0;
        this.num = 0;
        this.num = FaithZone.nums++;
        this.startX = startx;
        this.startY = starty;
        this.endX = endx;
        this.endY = endy;
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            this.activeGods = null;
            this.newFaiths = null;
        }
        else {
            this.activeGods = new Deity[4];
            this.newFaiths = new byte[4];
            for (int f = 0; f < 4; ++f) {
                this.activeGods[f] = null;
                this.newFaiths[f] = 0;
            }
        }
    }
    
    private void updateArea() {
        this.areaQL = (int)this.altar.getCurrentQualityLevel();
        this.startX = (short)(this.altar.getTileX() - this.areaQL);
        this.startY = (short)(this.altar.getTileY() - this.areaQL);
        this.endX = (short)(this.altar.getTileX() + this.areaQL);
        this.endY = (short)(this.altar.getTileY() + this.areaQL);
    }
    
    public int getStrength() {
        return this.strength;
    }
    
    public int getStrengthForTile(final int tileX, final int tileY, final boolean surfaced) {
        return (int)(this.altar.getCurrentQualityLevel() / ((surfaced == this.altar.isOnSurface()) ? 1 : 2) - Math.max(Math.abs(this.altar.getTileX() - tileX), Math.abs(this.altar.getTileY() - tileY)));
    }
    
    public boolean containsTile(final int tileX, final int tileY) {
        if (this.areaQL != (int)this.altar.getCurrentQualityLevel()) {
            this.updateArea();
        }
        return tileX > this.startX && tileX < this.endX && tileY > this.startY && tileY < this.endY;
    }
    
    public int getStartX() {
        return this.startX;
    }
    
    public int getStartY() {
        return this.startY;
    }
    
    public int getEndX() {
        return this.endX;
    }
    
    public int getEndY() {
        return this.endY;
    }
    
    public int getCenterX() {
        return this.startX + (this.endX - this.startX) / 2;
    }
    
    public int getCenterY() {
        return this.startY + (this.endY - this.startY) / 2;
    }
    
    public Deity getCurrentRuler() {
        if (Features.Feature.NEWDOMAINS.isEnabled()) {
            return this.altar.getBless();
        }
        return this.currentRuler;
    }
    
    boolean poll() {
        if (this.num == 1) {
            Zones.checkAltars();
        }
        int max = 0;
        Deity newRuler = null;
        for (int f = 0; f < 4; ++f) {
            if (this.activeGods[f] != null) {
                if (this.newFaiths[f] > 0 && this.newFaiths[f] >= max) {
                    max = this.newFaiths[f];
                    newRuler = this.activeGods[f];
                }
                else if (this.activeGods[f] == this.getCurrentRuler()) {
                    this.currentRuler = null;
                }
            }
            this.newFaiths[f] = 0;
            this.activeGods[f] = null;
        }
        boolean toReturn = false;
        if (this.getCurrentRuler() != newRuler) {
            this.currentRuler = newRuler;
            toReturn = true;
        }
        if (this.getCurrentRuler() == null) {
            this.strength = 0;
        }
        else {
            this.strength = max;
            this.strength = Math.max(-100, this.strength);
            this.strength = Math.min(100, this.strength);
            this.pollMycelium();
        }
        return toReturn;
    }
    
    public void pollMycelium() {
        if (this.getCurrentRuler() == null) {
            return;
        }
        if (this.getCurrentRuler().number == 4 && Server.rand.nextInt(50) == 0) {
            final int x = this.startX + Server.rand.nextInt(this.endX - this.startX);
            final int y = this.startY + Server.rand.nextInt(this.endY - this.startY);
            if (Kingdoms.getKingdomTemplateFor(Zones.getKingdom(x, y)) == 3) {
                spawnMycelium(x, y);
            }
        }
    }
    
    private static void spawnMycelium(final int tilex, final int tiley) {
        final int tile = Server.surfaceMesh.getTile(tilex, tiley);
        if (Tiles.decodeHeight(tile) > 0) {
            final byte type = Tiles.decodeType(tile);
            final byte data = Tiles.decodeData(tile);
            final Tiles.Tile theTile = Tiles.getTile(type);
            if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_KELP.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_DIRT_PACKED.id) {
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_MYCELIUM.id, (byte)0);
                Players.getInstance().sendChangedTile(tilex, tiley, true, false);
            }
            else if (theTile.isNormalTree()) {
                final byte newType = theTile.getTreeType(data).asMyceliumTree();
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), newType, data);
                Players.getInstance().sendChangedTile(tilex, tiley, true, false);
            }
            else if (theTile.isNormalBush()) {
                final byte newType = theTile.getBushType(data).asMyceliumBush();
                Server.setSurfaceTile(tilex, tiley, Tiles.decodeHeight(tile), newType, data);
                Players.getInstance().sendChangedTile(tilex, tiley, true, false);
            }
        }
    }
    
    void addToFaith(final Deity aDeity, final int aStrength) {
        int f = 0;
        while (f < 4) {
            if (this.activeGods[f] == null || this.activeGods[f] == aDeity) {
                this.activeGods[f] = aDeity;
                if (this.newFaiths[f] < aStrength) {
                    this.newFaiths[f] = (byte)Math.min(100, aStrength);
                    break;
                }
                break;
            }
            else {
                ++f;
            }
        }
    }
    
    static {
        logger = Logger.getLogger(FaithZone.class.getName());
    }
}
