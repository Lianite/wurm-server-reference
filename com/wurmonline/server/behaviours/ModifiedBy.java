// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.mesh.Tiles;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;

public enum ModifiedBy
{
    NOTHING(0), 
    NO_TREES(1), 
    NEAR_TREE(2), 
    NEAR_BUSH(3), 
    NEAR_OAK(4), 
    EASTER(5), 
    HUNGER(6), 
    WOUNDED(7), 
    NEAR_WATER(8);
    
    private final int code;
    
    private ModifiedBy(final int aCode) {
        this.code = aCode;
    }
    
    public float chanceModifier(final Creature performer, final int modifier, final int tilex, final int tiley) {
        if (this == ModifiedBy.NOTHING) {
            return 0.0f;
        }
        if (this == ModifiedBy.EASTER) {
            if (!performer.isPlayer() || (((Player)performer).isReallyPaying() && WurmCalendar.isEaster() && !((Player)performer).isReimbursed())) {
                return modifier;
            }
            return 0.0f;
        }
        else if (this == ModifiedBy.HUNGER) {
            if (performer.getStatus().getHunger() < 20) {
                return modifier;
            }
            return 0.0f;
        }
        else if (this == ModifiedBy.WOUNDED) {
            if (performer.getStatus().damage > 15) {
                return modifier;
            }
            return 0.0f;
        }
        else {
            final MeshIO mesh = Server.surfaceMesh;
            if (this.isAModifier(mesh.getTile(tilex, tiley))) {
                if (this == ModifiedBy.NO_TREES) {
                    return 0.0f;
                }
                return modifier;
            }
            else {
                for (int x = -1; x <= 1; ++x) {
                    int y = -1;
                    while (y <= 1) {
                        if ((x == -1 || x == 1 || y == -1 || y == 1) && this.isAModifier(mesh.getTile(tilex + x, tiley + y))) {
                            if (this == ModifiedBy.NO_TREES) {
                                return 0.0f;
                            }
                            return modifier / 2;
                        }
                        else {
                            ++y;
                        }
                    }
                }
                for (int x = -2; x <= 2; ++x) {
                    int y = -2;
                    while (y <= 2) {
                        if ((x == -2 || x == 2 || y == -2 || y == 2) && this.isAModifier(mesh.getTile(tilex + x, tiley + y))) {
                            if (this == ModifiedBy.NO_TREES) {
                                return 0.0f;
                            }
                            return modifier / 3;
                        }
                        else {
                            ++y;
                        }
                    }
                }
                for (int x = -5; x <= 5; ++x) {
                    int y = -5;
                    while (y <= 5) {
                        if ((x <= -3 || x >= 3 || y <= -3 || y >= 3) && this.isAModifier(mesh.getTile(tilex + x, tiley + y))) {
                            if (this == ModifiedBy.NO_TREES) {
                                return 0.0f;
                            }
                            return modifier / 4;
                        }
                        else {
                            ++y;
                        }
                    }
                }
                if (this == ModifiedBy.NO_TREES) {
                    return modifier;
                }
                return 0.0f;
            }
        }
    }
    
    private boolean isAModifier(final int tile) {
        if (this == ModifiedBy.NEAR_WATER) {
            return Tiles.decodeHeight(tile) < 5;
        }
        final byte decodedType = Tiles.decodeType(tile);
        final byte decodedData = Tiles.decodeData(tile);
        final Tiles.Tile theTile = Tiles.getTile(decodedType);
        if (this == ModifiedBy.NEAR_OAK) {
            if (theTile.isNormalTree()) {
                return theTile.isOak(decodedData);
            }
        }
        else {
            if (this == ModifiedBy.NEAR_TREE || this == ModifiedBy.NO_TREES) {
                return theTile.isNormalTree();
            }
            if (this == ModifiedBy.NEAR_BUSH) {
                return theTile.isNormalBush();
            }
        }
        return false;
    }
}
