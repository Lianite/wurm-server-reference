// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.caves;

final class Caves
{
    private static final float MAX_CAVE_SLOPE = 8.0f;
    
    public void digHoleAt(final int xFrom, final int yFrom, final int xTarget, final int yTarget, final int slope) {
        if (!this.isRockExposed(xTarget, yTarget)) {
            System.out.println("You can't mine an entrance here.. There's too much dirt on the tile.");
            return;
        }
        if (!this.isCaveWall(xFrom, yFrom)) {
            System.out.println("You can't mine an entrance here.. There's a tunnel in the way.");
            return;
        }
        for (int x = xTarget - 1; x <= xTarget + 1; ++x) {
            for (int y = yTarget - 1; y <= yTarget + 1; ++y) {
                if (this.isTerrainHole(x, y)) {
                    System.out.println("You can't mine an entrance here.. Too close to an existing entrance.");
                    return;
                }
            }
        }
        if (this.isMinable(xTarget, yTarget)) {
            for (int x = xFrom; x <= xFrom + 1; ++x) {
                for (int y = yFrom; y <= yFrom + 1; ++y) {}
            }
            this.mine(xFrom, yFrom, xTarget, yTarget, slope);
        }
    }
    
    public void mineAt(final int xFrom, final int yFrom, final int xTarget, final int yTarget, final int slope) {
        if (!this.isMinable(xTarget, yTarget)) {
            System.out.println("You can't mine here.. There's a tunnel in the way.");
            return;
        }
        if (!this.isCaveWall(xTarget, xTarget)) {
            this.mine(xFrom, yFrom, xTarget, yTarget, slope);
        }
    }
    
    private void mine(final int xFrom, final int yFrom, final int xTarget, final int yTarget, final int slope) {
    }
    
    private boolean isMinable(final int xTarget, final int yTarget) {
        float lowestFloor = 100000.0f;
        float highestFloor = -100000.0f;
        int tunnels = 0;
        for (int x = xTarget; x <= xTarget + 1; ++x) {
            for (int y = yTarget; y <= yTarget + 1; ++y) {
                if (this.isExitCorner(x, y)) {
                    ++tunnels;
                    final float h = this.getTerrainHeight(x, y);
                    if (h < lowestFloor) {
                        lowestFloor = h;
                    }
                    if (h > highestFloor) {
                        highestFloor = h;
                    }
                }
                else if (this.isTunnelCorner(x, y)) {
                    ++tunnels;
                    final float h = this.getCaveFloorHeight(x, y);
                    if (h < lowestFloor) {
                        lowestFloor = h;
                    }
                    if (h > highestFloor) {
                        highestFloor = h;
                    }
                }
            }
        }
        if (tunnels == 0) {
            return true;
        }
        final float diff = highestFloor - lowestFloor;
        return diff < 8.0f;
    }
    
    private boolean isTunnelCorner(final int x, final int y) {
        return this.isCaveTunnel(x, y) || this.isCaveTunnel(x - 1, y) || this.isCaveTunnel(x - 1, y - 1) || this.isCaveTunnel(x, y - 1);
    }
    
    private boolean isExitCorner(final int x, final int y) {
        return this.isCaveExit(x, y) || this.isCaveExit(x - 1, y) || this.isCaveExit(x - 1, y - 1) || this.isCaveExit(x, y - 1);
    }
    
    private float getTerrainHeight(final int x, final int y) {
        return 10.0f;
    }
    
    private float getCaveFloorHeight(final int x, final int y) {
        return 10.0f;
    }
    
    private boolean isCaveExit(final int x, final int y) {
        return true;
    }
    
    private boolean isCaveTunnel(final int x, final int y) {
        return true;
    }
    
    private boolean isCaveWall(final int x, final int y) {
        return true;
    }
    
    private boolean isTerrainHole(final int x, final int y) {
        return false;
    }
    
    private boolean isRockExposed(final int x, final int y) {
        return true;
    }
}
