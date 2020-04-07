// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import com.wurmonline.server.Server;
import com.wurmonline.server.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;

final class StaticPathMesh
{
    private static final Logger logger;
    private final PathTile start;
    private final PathTile finish;
    private static PathTile[][] pathables;
    private static final int WORLD_SIZE;
    private final int sizex;
    private final int sizey;
    private final int borderStartX;
    private final int borderEndX;
    private final int borderStartY;
    private final int borderEndY;
    private final boolean surfaced;
    
    StaticPathMesh(final int startx, final int starty, final int endx, final int endy, final boolean surf, final int borderSize, final int[] aMesh) {
        this.surfaced = surf;
        this.borderStartX = Math.max(0, Math.min(startx, endx) - borderSize);
        this.borderEndX = Math.min(StaticPathMesh.WORLD_SIZE - 1, Math.max(startx, endx) + borderSize);
        this.borderStartY = Math.max(0, Math.min(starty, endy) - borderSize);
        this.borderEndY = Math.min(StaticPathMesh.WORLD_SIZE - 1, Math.max(starty, endy) + borderSize);
        this.sizex = this.borderEndX - this.borderStartX + 1;
        this.sizey = this.borderEndY - this.borderStartY + 1;
        if (StaticPathMesh.logger.isLoggable(Level.FINEST)) {
            StaticPathMesh.logger.finest("PathMesh start-end: " + startx + "," + starty + "-" + endx + "," + endy);
            StaticPathMesh.logger.finest("PathMesh border start-end: " + this.borderStartX + ", " + this.borderStartY + " - " + this.borderEndX + ", " + this.borderEndY + ", sz=" + this.sizex + "," + this.sizey);
        }
        StaticPathMesh.pathables = new PathTile[this.sizex][this.sizey];
        PathTile lStart = null;
        PathTile lFinish = null;
        for (int y = 0; y < this.sizey; ++y) {
            for (int x = 0; x < this.sizex; ++x) {
                StaticPathMesh.pathables[x][y] = new PathTile(this.borderStartX + x, this.borderStartY + y, aMesh[this.borderStartX + x | this.borderStartY + y << Constants.meshSize], this.surfaced, (byte)(this.surfaced ? 0 : -1));
                if (StaticPathMesh.pathables[x][y].getTileX() == startx && StaticPathMesh.pathables[x][y].getTileY() == starty) {
                    lStart = StaticPathMesh.pathables[x][y];
                }
                if (StaticPathMesh.pathables[x][y].getTileX() == endx && StaticPathMesh.pathables[x][y].getTileY() == endy) {
                    lFinish = StaticPathMesh.pathables[x][y];
                }
            }
        }
        this.start = lStart;
        this.finish = lFinish;
        if (this.start == this.finish) {
            StaticPathMesh.logger.log(Level.INFO, "START=FINISH!");
        }
    }
    
    StaticPathMesh(final int startx, final int starty, final int endx, final int endy, final boolean surf, final int borderSize) {
        this(startx, starty, endx, endy, surf, borderSize, surf ? Server.surfaceMesh.data : Server.caveMesh.data);
    }
    
    PathTile getPathTile(final int realx, final int realy) {
        final int diffX = realx - this.borderStartX;
        final int diffY = realy - this.borderStartY;
        return StaticPathMesh.pathables[diffX][diffY];
    }
    
    boolean contains(final int realx, final int realy) {
        return realx >= this.borderStartX && realx <= this.borderEndX && realy >= this.borderStartY && realy <= this.borderEndY;
    }
    
    PathTile[] getAdjacent(final PathTile p) {
        final PathTile[] surrPathables = new PathTile[4];
        final int x = p.getTileX();
        final int y = p.getTileY();
        final int minOneX = x - 1;
        final int plusOneX = x + 1;
        final int minOneY = y - 1;
        final int plusOneY = y + 1;
        if (minOneX >= this.borderStartX) {
            surrPathables[3] = this.getPathTile(minOneX, y);
        }
        if (plusOneX < this.borderEndX) {
            surrPathables[1] = this.getPathTile(plusOneX, y);
        }
        if (minOneY >= this.borderStartY) {
            surrPathables[0] = this.getPathTile(x, minOneY);
        }
        if (plusOneY < this.borderEndY) {
            surrPathables[2] = this.getPathTile(x, plusOneY);
        }
        return surrPathables;
    }
    
    PathTile getStart() {
        return this.start;
    }
    
    PathTile getFinish() {
        return this.finish;
    }
    
    static void clearPathables() {
        StaticPathMesh.pathables = null;
    }
    
    int getSizex() {
        return this.sizex;
    }
    
    int getSizey() {
        return this.sizey;
    }
    
    int getBorderStartX() {
        return this.borderStartX;
    }
    
    int getBorderEndX() {
        return this.borderEndX;
    }
    
    int getBorderStartY() {
        return this.borderStartY;
    }
    
    int getBorderEndY() {
        return this.borderEndY;
    }
    
    boolean isSurfaced() {
        return this.surfaced;
    }
    
    static {
        logger = Logger.getLogger(PathMesh.class.getName());
        WORLD_SIZE = 1 << Constants.meshSize;
    }
}
