// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.structures;

import com.wurmonline.server.zones.VolaTile;
import java.util.logging.Level;
import com.wurmonline.math.Vector3f;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Server;
import javax.annotation.Nullable;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class Blocking implements MiscConstants
{
    public static final double RADS_TO_DEGS = 57.29577951308232;
    public static final float DEGS_TO_RADS = 0.017453292f;
    private static final float MAXSTEP = 3.0f;
    private static final Logger logger;
    
    public static final BlockingResult getRangedBlockerBetween(final Creature performer, final Item target) {
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), target.getPosX(), target.getPosY(), performer.getPositionZ(), target.getPosZ(), performer.isOnSurface(), target.isOnSurface(), true, 5, target.getWurmId(), performer.getBridgeId(), target.getBridgeId(), performer.followsGround() || (target.getFloorLevel() == 0 && target.getBridgeId() == -10L));
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final Item target, final int blockingType) {
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), target.getPosX(), target.getPosY(), performer.getPositionZ(), target.getPosZ(), performer.isOnSurface(), target.isOnSurface(), false, blockingType, target.getWurmId(), performer.getBridgeId(), target.getBridgeId(), performer.followsGround() || (target.getFloorLevel() == 0 && target.getBridgeId() == -10L));
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final Floor floor, final int blockingType) {
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), 2 + Tiles.decodeTileX(floor.getId()) * 4, 2 + Tiles.decodeTileY(floor.getId()) * 4, performer.getPositionZ(), floor.getMinZ() - 0.25f, performer.isOnSurface(), floor.isOnSurface(), false, performer.isOnSurface() ? blockingType : 7, floor.getId(), performer.getBridgeId(), -10L, false);
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final long target, final boolean targetSurfaced, final int blockingType, final long sourceBridgeId, final long targetBridgeId) {
        return getBlockerBetween(performer, target, targetSurfaced, blockingType, sourceBridgeId, targetBridgeId, 0);
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final long target, final boolean targetSurfaced, final int blockingType, final long sourceBridgeId, final long targetBridgeId, final int ceilingHeight) {
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), 2 + Tiles.decodeTileX(target) * 4, 2 + Tiles.decodeTileY(target) * 4, performer.getPositionZ(), Zones.getHeightForNode(Tiles.decodeTileX(target), Tiles.decodeTileY(target), targetSurfaced ? 0 : -1) + Tiles.decodeFloorLevel(target) * 3 + ceilingHeight, performer.isOnSurface(), targetSurfaced, false, (!performer.isOnSurface() && blockingType != 8) ? 7 : blockingType, target, sourceBridgeId, targetBridgeId, false);
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final Wall target, final int blockingType) {
        float tpx = target.getPositionX();
        float tpy = target.getPositionY();
        if (target.getDir() == Tiles.TileBorderDirection.DIR_HORIZ) {
            if (tpy > performer.getTileY() * 4) {
                tpy -= 4.0f;
            }
        }
        else if (tpx > performer.getTileX() * 4) {
            tpx -= 4.0f;
        }
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), tpx, tpy, performer.getPositionZ(), target.getMinZ(), performer.isOnSurface(), target.isOnSurface(), false, blockingType, target.getId(), performer.getBridgeId(), -10L, false);
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final Fence target, final int blockingType) {
        float tpx = target.getPositionX();
        float tpy = target.getPositionY();
        if (target.getDir() == Tiles.TileBorderDirection.DIR_HORIZ) {
            if (tpy > performer.getTileY() * 4) {
                tpy -= 4.0f;
            }
        }
        else if (tpx > performer.getTileX() * 4) {
            tpx -= 4.0f;
        }
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), tpx, tpy, performer.getPositionZ(), target.getMinZ(), performer.isOnSurface(), target.isOnSurface(), false, blockingType, target.getId(), performer.getBridgeId(), -10L, false);
    }
    
    public static final BlockingResult getRangedBlockerBetween(final Creature performer, final Creature target) {
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), target.getPosX(), target.getPosY(), performer.getPositionZ() + performer.getHalfHeightDecimeters() / 10.0f, target.getPositionZ() + target.getHalfHeightDecimeters() / 10.0f, performer.isOnSurface(), target.isOnSurface(), true, 4, target.getWurmId(), performer.getBridgeId(), target.getBridgeId(), false);
    }
    
    public static final BlockingResult getBlockerBetween(final Creature performer, final Creature target, final int blockingType) {
        return getBlockerBetween(performer, performer.getPosX(), performer.getPosY(), target.getPosX(), target.getPosY(), performer.getPositionZ(), target.getPositionZ(), performer.isOnSurface(), target.isOnSurface(), false, blockingType, target.getWurmId(), performer.getBridgeId(), target.getBridgeId(), performer.followsGround() || target.followsGround());
    }
    
    public static final BlockingResult getBlockerBetween(@Nullable final Creature creature, final float startx, final float starty, final float endx, final float endy, final float startZ, final float endZ, final boolean surfaced, final boolean targetSurfaced, final boolean rangedAttack, final int typeChecked, final long target, final long sourceBridgeId, final long targetBridgeId, final boolean followGround) {
        return getBlockerBetween(creature, startx, starty, endx, endy, startZ, endZ, surfaced, targetSurfaced, rangedAttack, typeChecked, true, target, sourceBridgeId, targetBridgeId, followGround);
    }
    
    public static final boolean isSameFloorLevel(final float startZ, final float endZ) {
        return Math.abs(startZ - endZ) < 3.0f;
    }
    
    public static final BlockingResult getBlockerBetween(@Nullable final Creature creature, final float startx, final float starty, final float endx, final float endy, final float startZ, final float endZ, final boolean surfaced, final boolean targetSurfaced, final boolean rangedAttack, final int typeChecked, final boolean test, final long target, final long sourceBridgeId, final long targetBridgeId, final boolean followGround) {
        final int starttilex = Zones.safeTileX((int)startx >> 2);
        final int starttiley = Zones.safeTileY((int)starty >> 2);
        final int endtilex = Zones.safeTileX((int)endx >> 2);
        final int endtiley = Zones.safeTileY((int)endy >> 2);
        int max = rangedAttack ? 100 : 50;
        if (starttilex == endtilex && starttiley == endtiley && isSameFloorLevel(startZ, endZ)) {
            return null;
        }
        if (typeChecked == 0) {
            return null;
        }
        if (!rangedAttack && creature != null) {
            if (!creature.isPlayer()) {
                max = creature.getMaxHuntDistance() + 5;
            }
            final Creature targetCret = creature.getTarget();
            if (targetCret != null && targetCret.getWurmId() == target) {
                creature.sendToLoggers("Now checking " + starttilex + "," + starttiley + " to " + endtilex + "," + endtiley + " startZ=" + startZ + " endZ=" + endZ + " surf=" + surfaced + "," + targetSurfaced + " follow ground=" + followGround);
            }
        }
        int nextTileX = starttilex;
        int nextTileY = starttiley;
        int lastTileX = starttilex;
        int lastTileY = starttiley;
        boolean isTransition = false;
        if (creature != null && !creature.isOnSurface()) {
            isTransition = false;
            if (Tiles.decodeType(Server.caveMesh.getTile(starttilex, starttiley)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                isTransition = true;
            }
            if (creature.isPlayer() && typeChecked != 6) {
                final Vector3f actualStart = ((Player)creature).getActualPosVehicle();
                final int actualStartX = Zones.safeTileX((int)actualStart.x >> 2);
                final int actualStartY = Zones.safeTileX((int)actualStart.y >> 2);
                final int tile = Server.caveMesh.getTile(actualStartX, actualStartY);
                if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                    final BlockingResult toReturn = new BlockingResult();
                    final PathTile blocker = new PathTile(actualStartX, actualStartY, tile, false, -1);
                    toReturn.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                    return toReturn;
                }
            }
        }
        final Vector3f startPos = new Vector3f(startx, starty, startZ + 0.5f);
        final Vector3f endPos = new Vector3f(endx, endy, endZ + 0.5f);
        final Vector3f lastPos = new Vector3f(startPos);
        final Vector3f nextPos = new Vector3f(startPos);
        final Vector3f dir = new Vector3f(endPos.subtract(startPos)).normalize();
        BlockingResult result = null;
        boolean found = false;
        int debugChecks = 0;
        while (!found) {
            final Vector3f remain = endPos.subtract(lastPos);
            if (remain.length() < 3.0f) {
                if (debugChecks++ > 60) {
                    found = true;
                }
                else if (remain.length() == 0.0f) {
                    found = true;
                }
                nextPos.addLocal(remain);
            }
            else {
                nextPos.addLocal(dir.mult(3.0f));
                if (debugChecks++ > 60) {
                    if (creature != null) {
                        Blocking.logger.log(Level.INFO, creature.getName() + " checking " + 3.0f + " meters failed. Checks=" + debugChecks);
                    }
                    found = true;
                }
            }
            lastTileY = nextTileY;
            lastTileX = nextTileX;
            nextTileX = (int)nextPos.x >> 2;
            nextTileY = (int)nextPos.y >> 2;
            final int diffX = nextTileX - lastTileX;
            final int diffY = nextTileY - lastTileY;
            if (diffX == 0 && diffY == 0) {
                nextPos.z = endPos.z;
                lastPos.z = startPos.z;
            }
            if (diffX != 0 || diffY != 0 || !isSameFloorLevel(lastPos.z, nextPos.z)) {
                if (!surfaced && (!isTransition || !targetSurfaced) && typeChecked != 1 && typeChecked != 2 && typeChecked != 3) {
                    final int t = Server.caveMesh.getTile(endtilex, endtiley);
                    if (Tiles.isSolidCave(Tiles.decodeType(t)) && typeChecked == 6) {
                        result = new BlockingResult();
                        final PathTile blocker2 = new PathTile(endtilex, endtiley, t, false, -1);
                        result.addBlocker(blocker2, blocker2.getCenterPoint(), 100.0f);
                        return result;
                    }
                    result = isDiagonalRockBetween(creature, lastTileX, lastTileY, nextTileX, nextTileY);
                    if (result == null) {
                        result = isStraightRockBetween(creature, lastTileX, lastTileY, nextTileX, nextTileY);
                    }
                    if (result != null) {
                        if ((typeChecked != 7 && typeChecked != 8) || result.getFirstBlocker().getTileX() != endtilex || result.getFirstBlocker().getTileY() != endtiley) {
                            return result;
                        }
                        result = null;
                    }
                }
                VolaTile checkedTile = null;
                if (diffX >= 0 && diffY >= 0) {
                    for (int x = Math.min(0, diffX); x <= diffX; ++x) {
                        for (int y = Math.min(0, diffY); y <= diffY; ++y) {
                            checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, surfaced);
                            if (checkedTile != null) {
                                result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                            }
                            if (surfaced != targetSurfaced) {
                                checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, targetSurfaced);
                                if (checkedTile != null) {
                                    result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                                }
                            }
                        }
                    }
                }
                if (diffX < 0 && diffY >= 0) {
                    for (int x = 0; x >= diffX; --x) {
                        for (int y = Math.min(0, diffY); y <= diffY; ++y) {
                            checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, surfaced);
                            if (checkedTile != null) {
                                result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                            }
                            if (surfaced != targetSurfaced) {
                                checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, targetSurfaced);
                                if (checkedTile != null) {
                                    result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                                }
                            }
                        }
                    }
                }
                if (diffX >= 0 && diffY < 0) {
                    for (int x = Math.min(0, diffX); x <= diffX; ++x) {
                        for (int y = 0; y >= diffY; --y) {
                            checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, surfaced);
                            if (checkedTile != null) {
                                result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                            }
                            if (surfaced != targetSurfaced) {
                                checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, targetSurfaced);
                                if (checkedTile != null) {
                                    result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                                }
                            }
                        }
                    }
                }
                if (diffX < 0 && diffY < 0) {
                    for (int x = 0; x >= diffX; --x) {
                        for (int y = 0; y >= diffY; --y) {
                            checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, surfaced);
                            if (checkedTile != null) {
                                result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                            }
                            if (surfaced != targetSurfaced) {
                                checkedTile = Zones.getTileOrNull(lastTileX + x, lastTileY + y, targetSurfaced);
                                if (checkedTile != null) {
                                    result = returnIterativeCheck(checkedTile, result, creature, dir, lastPos, nextPos, rangedAttack, starttilex, nextTileX, starttiley, nextTileY, typeChecked, target, sourceBridgeId, targetBridgeId, followGround);
                                }
                            }
                        }
                    }
                }
            }
            lastPos.set(nextPos);
            if (found) {
                return result;
            }
            if (Math.abs(nextTileX - starttilex) > max || Math.abs(nextTileY - starttiley) > max) {
                return result;
            }
        }
        return result;
    }
    
    private static final BlockingResult returnIterativeCheck(final VolaTile checkedTile, BlockingResult result, final Creature creature, final Vector3f dir, final Vector3f lastPos, final Vector3f nextPos, final boolean rangedAttack, final int startTileX, final int nextTileX, final int startTileY, final int nextTileY, final int typeChecked, final long targetId, final long sourceBridgeId, final long targetBridgeId, final boolean followGround) {
        Blocker[] blockers = null;
        Vector3f toCheck = lastPos.clone();
        if (typeChecked == 4 || typeChecked == 2 || typeChecked == 5 || typeChecked == 6 || typeChecked == 7) {
            blockers = checkedTile.getWalls();
            result = checkForResult(creature, result, blockers, dir, toCheck, nextPos, rangedAttack, startTileX, nextTileX, startTileY, nextTileY, typeChecked, targetId, sourceBridgeId, targetBridgeId, followGround);
            if (result != null && result.getTotalCover() >= 100.0f) {
                return result;
            }
        }
        if (typeChecked == 4 || typeChecked == 1 || typeChecked == 5 || typeChecked == 6 || typeChecked == 7) {
            blockers = checkedTile.getFences();
            toCheck = lastPos.clone();
            result = checkForResult(creature, result, blockers, dir, toCheck, nextPos, rangedAttack, startTileX, nextTileX, startTileY, nextTileY, typeChecked, targetId, sourceBridgeId, targetBridgeId, followGround);
            if (result != null && result.getTotalCover() >= 100.0f) {
                return result;
            }
        }
        if (typeChecked == 4 || typeChecked == 3 || typeChecked == 5 || typeChecked == 6 || typeChecked == 7) {
            blockers = checkedTile.getFloors();
            toCheck = lastPos.clone();
            result = checkForResult(creature, result, blockers, dir, toCheck, nextPos, rangedAttack, startTileX, nextTileX, startTileY, nextTileY, typeChecked, targetId, sourceBridgeId, targetBridgeId, followGround);
            blockers = checkedTile.getBridgeParts();
            toCheck = lastPos.clone();
            result = checkForResult(creature, result, blockers, dir, toCheck, nextPos, rangedAttack, startTileX, nextTileX, startTileY, nextTileY, typeChecked, targetId, sourceBridgeId, targetBridgeId, followGround);
            if (result != null && result.getTotalCover() >= 100.0f) {
                return result;
            }
        }
        return result;
    }
    
    private static final BlockingResult checkForResult(final Creature creature, BlockingResult result, final Blocker[] blockers, final Vector3f dir, final Vector3f startPos, final Vector3f endPos, final boolean rangedAttack, final int starttilex, final int currTileX, final int starttiley, final int currTileY, final int blockType, final long target, final long sourceBridgeId, final long targetBridgeId, final boolean followGround) {
        for (int w = 0; w < blockers.length; ++w) {
            if (blockers[w].isWithinZ(Math.max(startPos.z, endPos.z), Math.min(startPos.z, endPos.z), followGround)) {
                boolean skip = false;
                if (blockers[w] instanceof BridgePart) {
                    final BridgePart bp = (BridgePart)blockers[w];
                    if (bp.getStructureId() == sourceBridgeId && (sourceBridgeId == targetBridgeId || blockType == 6)) {
                        skip = true;
                    }
                }
                if (!skip) {
                    final Vector3f intersection = blockers[w].isBlocking(creature, startPos, endPos, dir, blockType, target, followGround);
                    if (intersection != null) {
                        if (result == null) {
                            result = new BlockingResult();
                        }
                        if (!rangedAttack && blockType != 5) {
                            result.addBlocker(blockers[w], intersection, 100.0f);
                            return result;
                        }
                        final float addedCover = blockers[w].getBlockPercent(creature);
                        if (Math.abs(starttilex - currTileX) > 1 || Math.abs(starttiley - currTileY) > 1 || addedCover >= 100.0f) {
                            if (addedCover >= 100.0f) {
                                result.addBlocker(blockers[w], intersection, 100.0f);
                                return result;
                            }
                            if (result.addBlocker(blockers[w], intersection, addedCover) >= 100.0f) {
                                return result;
                            }
                        }
                        else if (result.addBlocker(blockers[w], intersection, addedCover) >= 100.0f) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static final BlockingResult isDiagonalRockBetween(final Creature creature, final int startx, final int starty, final int endx, final int endy) {
        if (startx != endx && endy != starty) {
            final int northTile = Server.caveMesh.getTile(Zones.safeTileX(startx), Zones.safeTileY(starty - 1));
            final int southTile = Server.caveMesh.getTile(Zones.safeTileX(startx), Zones.safeTileY(starty + 1));
            final int westTile = Server.caveMesh.getTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty));
            final int eastTile = Server.caveMesh.getTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty));
            if (endx < startx) {
                if (endy < starty) {
                    if (Tiles.isSolidCave(Tiles.decodeType(westTile)) && Tiles.isSolidCave(Tiles.decodeType(northTile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty), westTile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        final PathTile blocker2 = new PathTile(Zones.safeTileX(startx), Zones.safeTileY(starty - 1), northTile, false, -1);
                        result.addBlocker(blocker2, blocker2.getCenterPoint(), 100.0f);
                        return result;
                    }
                    final int nw = Server.caveMesh.getTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty - 1));
                    if (Tiles.isSolidCave(Tiles.decodeType(nw))) {
                        final BlockingResult result2 = new BlockingResult();
                        final PathTile blocker3 = new PathTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty - 1), nw, false, -1);
                        result2.addBlocker(blocker3, blocker3.getCenterPoint(), 100.0f);
                        return result2;
                    }
                }
                if (endy > starty) {
                    if (Tiles.isSolidCave(Tiles.decodeType(westTile)) && Tiles.isSolidCave(Tiles.decodeType(southTile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty), westTile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        final PathTile blocker2 = new PathTile(Zones.safeTileX(startx), Zones.safeTileY(starty + 1), southTile, false, -1);
                        result.addBlocker(blocker2, blocker2.getCenterPoint(), 100.0f);
                        return result;
                    }
                    final int sw = Server.caveMesh.getTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty + 1));
                    if (Tiles.isSolidCave(Tiles.decodeType(sw))) {
                        final BlockingResult result2 = new BlockingResult();
                        final PathTile blocker3 = new PathTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty + 1), sw, false, -1);
                        result2.addBlocker(blocker3, blocker3.getCenterPoint(), 100.0f);
                        return result2;
                    }
                }
            }
            else {
                if (endy < starty) {
                    if (Tiles.isSolidCave(Tiles.decodeType(eastTile)) && Tiles.isSolidCave(Tiles.decodeType(northTile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty), eastTile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        final PathTile blocker2 = new PathTile(Zones.safeTileX(startx), Zones.safeTileY(starty - 1), northTile, false, -1);
                        result.addBlocker(blocker2, blocker2.getCenterPoint(), 100.0f);
                        return result;
                    }
                    final int ne = Server.caveMesh.getTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty - 1));
                    if (Tiles.isSolidCave(Tiles.decodeType(ne))) {
                        final BlockingResult result2 = new BlockingResult();
                        final PathTile blocker3 = new PathTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty - 1), ne, false, -1);
                        result2.addBlocker(blocker3, blocker3.getCenterPoint(), 100.0f);
                        return result2;
                    }
                }
                if (endy > starty) {
                    if (Tiles.isSolidCave(Tiles.decodeType(eastTile)) && Tiles.isSolidCave(Tiles.decodeType(southTile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty), eastTile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        final PathTile blocker2 = new PathTile(Zones.safeTileX(startx), Zones.safeTileY(starty + 1), southTile, false, -1);
                        result.addBlocker(blocker2, blocker2.getCenterPoint(), 100.0f);
                        return result;
                    }
                    final int se = Server.caveMesh.getTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty + 1));
                    if (Tiles.isSolidCave(Tiles.decodeType(se))) {
                        final BlockingResult result2 = new BlockingResult();
                        final PathTile blocker3 = new PathTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty + 1), se, false, -1);
                        result2.addBlocker(blocker3, blocker3.getCenterPoint(), 100.0f);
                        return result2;
                    }
                }
            }
        }
        return null;
    }
    
    public static final BlockingResult isStraightRockBetween(final Creature creature, final int startx, final int starty, final int endx, final int endy) {
        if (startx == endx || endy == starty) {
            if (endx < startx) {
                if (endy == starty) {
                    final int tile = Server.caveMesh.getTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty));
                    if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx - 1), Zones.safeTileY(starty), tile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        return result;
                    }
                }
            }
            else if (endx > startx) {
                if (endy == starty) {
                    final int tile = Server.caveMesh.getTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty));
                    if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx + 1), Zones.safeTileY(starty), tile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        return result;
                    }
                }
            }
            else if (endy > starty) {
                if (endx == startx) {
                    final int tile = Server.caveMesh.getTile(Zones.safeTileX(startx), Zones.safeTileY(starty + 1));
                    if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                        final BlockingResult result = new BlockingResult();
                        final PathTile blocker = new PathTile(Zones.safeTileX(startx), Zones.safeTileY(starty + 1), tile, false, -1);
                        result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                        return result;
                    }
                }
            }
            else if (endy < starty && endx == startx) {
                final int tile = Server.caveMesh.getTile(Zones.safeTileX(startx), Zones.safeTileY(starty - 1));
                if (Tiles.isSolidCave(Tiles.decodeType(tile))) {
                    final BlockingResult result = new BlockingResult();
                    final PathTile blocker = new PathTile(Zones.safeTileX(startx), Zones.safeTileY(starty - 1), tile, false, -1);
                    result.addBlocker(blocker, blocker.getCenterPoint(), 100.0f);
                    return result;
                }
            }
        }
        return null;
    }
    
    static {
        logger = Logger.getLogger(Blocking.class.getName());
    }
}
