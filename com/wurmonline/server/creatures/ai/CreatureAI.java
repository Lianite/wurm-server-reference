// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai;

import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.zones.VirtualZone;
import javax.annotation.Nullable;
import java.io.IOException;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.MineDoorPermission;
import java.util.logging.Level;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public abstract class CreatureAI
{
    private static final Logger logger;
    private static final boolean DETAILED_TIME_LOG = false;
    
    protected void simpleMovementTick(final Creature c) {
        final long start = System.nanoTime();
        try {
            int tileX = -1;
            int tileY = -1;
            if (Server.rand.nextInt(100) < 5) {
                tileX = c.getTileX() + (Server.rand.nextInt(4) - 2);
                tileY = c.getTileY() + (Server.rand.nextInt(4) - 2);
            }
            this.moveTowardsTile(c, tileX, tileY, true);
        }
        finally {}
    }
    
    protected void pathedMovementTick(final Creature c) {
        final long start = System.nanoTime();
        try {
            final Path p = c.getStatus().getPath();
            if (p != null && !p.isEmpty()) {
                PathTile nextTile = p.getFirst();
                if (nextTile.hasSpecificPos()) {
                    c.turnTowardsPoint(nextTile.getPosX(), nextTile.getPosY());
                    this.creatureMovementTick(c, true);
                    final float diffX = c.getStatus().getPositionX() - nextTile.getPosX();
                    final float diffY = c.getStatus().getPositionY() - nextTile.getPosY();
                    final double totalDist = Math.sqrt(diffX * diffX + diffY * diffY);
                    final float lMod = c.getMoveModifier((c.isOnSurface() ? Server.surfaceMesh : Server.caveMesh).getTile((int)c.getStatus().getPositionX() >> 2, (int)c.getStatus().getPositionY() >> 2));
                    final float aiDataMoveModifier = (c.getCreatureAIData() != null) ? c.getCreatureAIData().getMovementSpeedModifier() : 1.0f;
                    if (totalDist <= c.getSpeed() * lMod * aiDataMoveModifier) {
                        p.removeFirst();
                    }
                }
                else {
                    if (nextTile.getTileX() == c.getTileX() && nextTile.getTileY() == c.getTileY()) {
                        p.removeFirst();
                        if (p.isEmpty()) {
                            return;
                        }
                        nextTile = p.getFirst();
                    }
                    this.moveTowardsTile(c, nextTile.getTileX(), nextTile.getTileY(), true);
                    if ((nextTile.getTileX() == c.getTileX() && nextTile.getTileY() == c.getTileY()) || (c.getTarget() != null && c.getPos2f().distance(c.getTarget().getPos2f()) < 4.0f)) {
                        p.removeFirst();
                    }
                }
            }
        }
        finally {}
    }
    
    protected void moveTowardsTile(final Creature c, final int tileX, final int tileY, final boolean moveToTarget) {
        if (c.getTarget() != null && moveToTarget) {
            c.turnTowardsCreature(c.getTarget());
        }
        else if (tileX > 0 && tileY > 0) {
            c.turnTowardsTile((short)tileX, (short)tileY);
        }
        this.creatureMovementTick(c, true);
    }
    
    protected void creatureMovementTick(final Creature c, final boolean rotateFromBlocker) {
        float lPosX = c.getStatus().getPositionX();
        float lPosY = c.getStatus().getPositionY();
        float lPosZ = c.getStatus().getPositionZ();
        final float lRotation = c.getStatus().getRotation();
        final float oldPosX = lPosX;
        final float oldPosY = lPosY;
        final float oldPosZ = lPosZ;
        final int lOldTileX = (int)lPosX >> 2;
        final int lOldTileY = (int)lPosY >> 2;
        float lMoveModifier;
        if (!c.isOnSurface()) {
            lMoveModifier = c.getMoveModifier(Server.caveMesh.getTile(lOldTileX, lOldTileY));
        }
        else {
            lMoveModifier = c.getMoveModifier(Server.surfaceMesh.getTile(lOldTileX, lOldTileY));
        }
        float aiDataMoveModifier;
        if (c.getCreatureAIData() != null) {
            aiDataMoveModifier = c.getCreatureAIData().getMovementSpeedModifier();
        }
        else {
            aiDataMoveModifier = 1.0f;
        }
        final float lXPosMod = (float)Math.sin(lRotation * 0.017453292f) * c.getSpeed() * lMoveModifier * aiDataMoveModifier;
        final float lYPosMod = -(float)Math.cos(lRotation * 0.017453292f) * c.getSpeed() * lMoveModifier * aiDataMoveModifier;
        final int lNewTileX = (int)(lPosX + lXPosMod) >> 2;
        final int lNewTileY = (int)(lPosY + lYPosMod) >> 2;
        final int lDiffTileX = lNewTileX - lOldTileX;
        final int lDiffTileY = lNewTileY - lOldTileY;
        long newBridgeId = c.getBridgeId();
        if (lDiffTileX != 0 || lDiffTileY != 0) {
            if (!c.isOnSurface()) {
                if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(lOldTileX, lOldTileY)))) {
                    CreatureAI.logger.log(Level.INFO, "Destroying creature " + c.getName() + " due to being inside rock.");
                    c.die(false, "Suffocating in rock (3)");
                    return;
                }
                if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(lNewTileX, lNewTileY)))) {
                    if (c.getCurrentTile().isTransition()) {
                        if (!Tiles.isMineDoor(Tiles.decodeType(Server.caveMesh.getTile(c.getTileX(), c.getTileY()))) || MineDoorPermission.getPermission(c.getTileX(), c.getTileY()).mayPass(c)) {
                            c.setLayer(0, true);
                        }
                        else if (rotateFromBlocker) {
                            c.rotateRandom(lRotation, 45);
                        }
                        return;
                    }
                    if (rotateFromBlocker) {
                        c.rotateRandom(lRotation, 45);
                    }
                    return;
                }
            }
            else if (Tiles.Tile.TILE_LAVA.id == Tiles.decodeType(Server.surfaceMesh.getTile(lNewTileX, lNewTileY))) {
                if (rotateFromBlocker) {
                    c.rotateRandom(lRotation, 45);
                }
                return;
            }
            final VolaTile t = Zones.getOrCreateTile(lNewTileX, lNewTileY, c.isOnSurface());
            if ((!c.isHuman() && t.isGuarded() && !c.getCurrentTile().isGuarded()) || (c.isAnimal() && t.hasFire())) {
                if (rotateFromBlocker) {
                    c.rotateRandom(lRotation, 100);
                }
                return;
            }
            newBridgeId = this.getNewBridgeId(lOldTileX, lOldTileY, c.isOnSurface(), c.getBridgeId(), c.getFloorLevel(), lNewTileX, lNewTileY);
            final BlockingResult result = Blocking.getBlockerBetween(c, lPosX, lPosY, lPosX + lXPosMod, lPosY + lYPosMod, c.getPositionZ(), c.getPositionZ(), c.isOnSurface(), c.isOnSurface(), false, 6, -1L, c.getBridgeId(), newBridgeId, c.followsGround());
            if (result != null && !c.isWagoner() && ((!c.isKingdomGuard() && !c.isSpiritGuard()) || !result.getFirstBlocker().isDoor())) {
                if (rotateFromBlocker) {
                    c.rotateRandom(lRotation, 100);
                }
                return;
            }
        }
        lPosX += lXPosMod;
        lPosY += lYPosMod;
        if (lPosX >= Zones.worldTileSizeX - 1 << 2 || lPosX < 0.0f || lPosY < 0.0f || lPosY >= Zones.worldTileSizeY - 1 << 2) {
            c.destroy();
            return;
        }
        final VolaTile vt = Zones.getOrCreateTile(lNewTileX, lNewTileY, c.isOnSurface());
        lPosZ = Zones.calculatePosZ(lPosX, lPosY, vt, c.isOnSurface(), c.isFloating(), c.getPositionZ(), c, newBridgeId);
        if (c.isFloating()) {
            lPosZ = Math.max(c.getTemplate().offZ, lPosZ);
        }
        if (lPosZ < 0.5) {
            if ((lPosZ > -2.0f || oldPosZ <= -2.0f) && (oldPosZ < 0.0f || c.getTarget() != null) && c.isSwimming()) {
                lPosZ = Math.max(-1.25f, lPosZ);
                if (c.isFloating()) {
                    lPosZ = Math.max(c.getTemplate().offZ, lPosZ);
                }
            }
            else if (lPosZ < -0.7 && !c.isSubmerged()) {
                if (rotateFromBlocker) {
                    c.rotateRandom(lRotation, 100);
                }
                if (c.getTarget() != null) {
                    c.setTarget(-10L, true);
                }
                return;
            }
        }
        c.getStatus().setPositionX(lPosX);
        c.getStatus().setPositionY(lPosY);
        c.getStatus().setRotation(lRotation);
        if (Structure.isGroundFloorAtPosition(lPosX, lPosY, c.isOnSurface())) {
            c.getStatus().setPositionZ(lPosZ + 0.25f);
        }
        else {
            c.getStatus().setPositionZ(lPosZ);
        }
        c.moved(lPosX - oldPosX, lPosY - oldPosY, lPosZ - oldPosZ, lDiffTileX, lDiffTileY);
        if (c.getTarget() != null && c.getTarget().getCurrentTile() == c.getCurrentTile() && c.getTarget().getFloorLevel() != c.getFloorLevel()) {
            if (c.isSpiritGuard()) {
                c.pushToFloorLevel(c.getTarget().getFloorLevel());
            }
            else {
                final Floor[] floors;
                final Floor[] currentTileFloors = floors = c.getCurrentTile().getFloors();
                for (final Floor f : floors) {
                    if (c.getTarget().getFloorLevel() > c.getFloorLevel()) {
                        if (f.getFloorLevel() == c.getFloorLevel() + 1 && (f.isOpening() || f.isStair())) {
                            c.pushToFloorLevel(f.getFloorLevel());
                        }
                    }
                    else if (f.getFloorLevel() == c.getFloorLevel() - 1 && (f.isOpening() || f.isStair())) {
                        c.pushToFloorLevel(f.getFloorLevel());
                    }
                }
            }
        }
    }
    
    private long getNewBridgeId(final int oldTileX, final int oldTileY, final boolean onSurface, final long oldBridgeId, final int floorLevel, final int newTileX, final int newTileY) {
        if (oldBridgeId == -10L) {
            final BridgePart bp = Zones.getBridgePartFor(newTileX, newTileY, onSurface);
            if (bp != null && bp.isFinished() && bp.hasAnExit()) {
                if (newTileY < oldTileY && bp.getSouthExitFloorLevel() == floorLevel) {
                    return bp.getStructureId();
                }
                if (newTileX > oldTileX && bp.getWestExitFloorLevel() == floorLevel) {
                    return bp.getStructureId();
                }
                if (newTileY > oldTileY && bp.getNorthExitFloorLevel() == floorLevel) {
                    return bp.getStructureId();
                }
                if (newTileX < oldTileX && bp.getEastExitFloorLevel() == floorLevel) {
                    return bp.getStructureId();
                }
            }
        }
        else {
            final BridgePart bp = Zones.getBridgePartFor(newTileX, newTileY, onSurface);
            if (bp == null) {
                return -10L;
            }
        }
        return oldBridgeId;
    }
    
    protected PathTile getMovementTarget(final Creature c, int tilePosX, int tilePosY) {
        tilePosX = Zones.safeTileX(tilePosX);
        tilePosY = Zones.safeTileY(tilePosY);
        if (!c.isOnSurface()) {
            final int tile = Server.caveMesh.getTile(tilePosX, tilePosY);
            if (!Tiles.isSolidCave(Tiles.decodeType(tile)) && (Tiles.decodeHeight(tile) > -c.getHalfHeightDecimeters() || c.isSwimming() || c.isSubmerged())) {
                return new PathTile(tilePosX, tilePosY, tile, c.isOnSurface(), -1);
            }
        }
        else {
            final int tile = Server.surfaceMesh.getTile(tilePosX, tilePosY);
            if (Tiles.decodeHeight(tile) > -c.getHalfHeightDecimeters() || c.isSwimming() || c.isSubmerged()) {
                return new PathTile(tilePosX, tilePosY, tile, c.isOnSurface(), c.getFloorLevel());
            }
        }
        return null;
    }
    
    public boolean pollCreature(final Creature c, final long delta) {
        final long start = System.nanoTime();
        boolean isDead = false;
        if (c.getSpellEffects() != null) {
            c.getSpellEffects().poll();
        }
        if (this.pollSpecialPreAttack(c, delta)) {
            return true;
        }
        isDead = this.pollAttack(c, delta);
        if (c.getActions().poll(c)) {
            if (c.isFighting()) {
                c.setFighting();
            }
            else {
                if (isDead) {
                    return true;
                }
                if (this.pollSpecialPreBreeding(c, delta)) {
                    return true;
                }
                isDead = this.pollBreeding(c, delta);
                if (isDead) {
                    return true;
                }
                if (this.pollSpecialPreMovement(c, delta)) {
                    return true;
                }
                isDead = this.pollMovement(c, delta);
                if (isDead) {
                    return true;
                }
                if (System.currentTimeMillis() - c.lastSavedPos > 3600000L) {
                    c.lastSavedPos = System.currentTimeMillis() + Server.rand.nextInt(3600) * 1000;
                    try {
                        c.savePosition(c.getStatus().getZoneId());
                        c.getStatus().save();
                    }
                    catch (IOException e) {
                        CreatureAI.logger.warning("Unable to save creature position, creature id: " + c.getWurmId() + " reason: " + e.getMessage());
                    }
                }
            }
        }
        if (c.getDamageCounter() > 0) {
            c.setDamageCounter((short)(c.getDamageCounter() - 1));
            isDead = this.pollDamageCounter(c, delta, c.getDamageCounter());
        }
        if (isDead) {
            return true;
        }
        if (this.pollSpecialPreItems(c, delta)) {
            return true;
        }
        this.pollItems(c, delta);
        if (this.pollSpecialPreMisc(c, delta)) {
            return true;
        }
        isDead = this.pollMisc(c, delta);
        if (isDead) {
            return true;
        }
        isDead = this.pollSpecialFinal(c, delta);
        return isDead;
    }
    
    public int woundDamageChanged(final Creature c, final int damageToAdd) {
        return damageToAdd;
    }
    
    public double causedWound(final Creature c, @Nullable final Creature target, final byte dmgType, final int dmgPosition, final float armourMod, final double damage) {
        return damage;
    }
    
    public double receivedWound(final Creature c, @Nullable final Creature performer, final byte dmgType, final int dmgPosition, final float armourMod, final double damage) {
        return damage;
    }
    
    public boolean creatureDied(final Creature creature) {
        return false;
    }
    
    public boolean maybeAttackCreature(final Creature c, final VirtualZone vz, final Creature mover) {
        return false;
    }
    
    protected abstract boolean pollMovement(final Creature p0, final long p1);
    
    protected abstract boolean pollAttack(final Creature p0, final long p1);
    
    protected abstract boolean pollBreeding(final Creature p0, final long p1);
    
    protected boolean pollSpecialPreBreeding(final Creature c, final long delta) {
        return false;
    }
    
    protected boolean pollSpecialPreMovement(final Creature c, final long delta) {
        return false;
    }
    
    protected boolean pollSpecialPreAttack(final Creature c, final long delta) {
        return false;
    }
    
    protected boolean pollSpecialPreItems(final Creature c, final long delta) {
        return false;
    }
    
    protected boolean pollSpecialPreMisc(final Creature c, final long delta) {
        return false;
    }
    
    protected boolean pollSpecialFinal(final Creature c, final long delta) {
        return false;
    }
    
    protected boolean pollDamageCounter(final Creature c, final long delta, final short damageCounter) {
        if (damageCounter == 0) {
            c.removeWoundMod();
        }
        return false;
    }
    
    protected void pollItems(final Creature c, final long delta) {
        c.pollItems();
        if (c.getBody() != null) {
            c.getBody().poll();
        }
    }
    
    protected boolean pollMisc(final Creature c, final long delta) {
        c.pollStamina();
        c.pollFavor();
        c.pollLoyalty();
        c.trimAttackers(false);
        c.numattackers = 0;
        c.hasAddedToAttack = false;
        if (!c.isUnique() && !c.isFighting() && c.isDominated() && c.goOffline) {
            CreatureAI.logger.log(Level.INFO, c.getName() + " going offline.");
            Creatures.getInstance().setCreatureOffline(c);
            c.goOffline = false;
            return true;
        }
        return false;
    }
    
    protected boolean isTimerReady(final Creature c, final int timerId, final long minTime) {
        return c.getCreatureAIData().getTimer(timerId) >= minTime;
    }
    
    protected void increaseTimer(final Creature c, final long delta, final int... timerIds) {
        for (final int id : timerIds) {
            c.getCreatureAIData().setTimer(id, c.getCreatureAIData().getTimer(id) + delta);
        }
    }
    
    protected void resetTimer(final Creature c, final int... timerIds) {
        for (final int timerId : timerIds) {
            c.getCreatureAIData().setTimer(timerId, 0L);
        }
    }
    
    public abstract CreatureAIData createCreatureAIData();
    
    public abstract void creatureCreated(final Creature p0);
    
    static {
        logger = Logger.getLogger(CreatureAI.class.getName());
    }
}
