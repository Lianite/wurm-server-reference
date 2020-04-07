// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import com.wurmonline.mesh.Tiles;
import java.util.logging.Logger;

public abstract strictfp class MovementChecker
{
    protected static final Logger logger;
    int blocks;
    public static final float DEGS_TO_RADS = 0.017453292f;
    public static final int BIT_FORWARD = 1;
    public static final int BIT_BACK = 2;
    public static final int BIT_LEFT = 4;
    public static final int BIT_RIGHT = 8;
    private static final float WALK_SPEED = 0.08f;
    public static final float FLOATING_HEIGHT = -1.45f;
    public boolean serverWestAvailable;
    public boolean serverNorthAvailable;
    public boolean serverEastAvailable;
    public boolean serverSouthAvailable;
    private static final float CLIMB_SPEED_MODIFIER = 0.25f;
    private float speedMod;
    private boolean climbing;
    private long bridgeId;
    private int bridgeCounter;
    private float x;
    private float y;
    private float z;
    private float xRot;
    public float xOld;
    public float yOld;
    private float zOld;
    private float xa;
    private float ya;
    private float za;
    private float groundOffset;
    private int targetGroundOffset;
    private byte bitmask;
    public boolean onGround;
    public boolean inWater;
    private int layer;
    private boolean abort;
    public boolean ignoreErrors;
    public boolean started;
    public float diffWindX;
    public float diffWindY;
    private float windRotation;
    private float windStrength;
    private float windImpact;
    private float mountSpeed;
    public boolean commandingBoat;
    private float vehicleRotation;
    private float currx;
    private float curry;
    private boolean first;
    public static final float MINHEIGHTC = -3000.0f;
    public static final float MAXHEIGHTC = 3000.0f;
    private static final float fallMod = 0.04f;
    private static final float deltaH = 1.0f;
    private static final float moveMod = 0.4f;
    private boolean movingVehicle;
    private float leftTurnMod;
    private float rightTurnMod;
    public float offZ;
    private boolean flying;
    protected boolean wasOnStair;
    private int counter;
    private boolean acceptedError;
    private boolean isFalling;
    private boolean onFloorOverridden;
    float mhdlog;
    
    public MovementChecker() {
        this.blocks = 0;
        this.serverWestAvailable = true;
        this.serverNorthAvailable = true;
        this.serverEastAvailable = true;
        this.serverSouthAvailable = true;
        this.speedMod = 1.0f;
        this.climbing = false;
        this.bridgeId = -10L;
        this.bridgeCounter = 0;
        this.onGround = false;
        this.inWater = false;
        this.layer = 0;
        this.abort = false;
        this.ignoreErrors = false;
        this.started = false;
        this.diffWindX = 0.0f;
        this.diffWindY = 0.0f;
        this.windRotation = 0.0f;
        this.windStrength = 0.0f;
        this.windImpact = 0.0f;
        this.mountSpeed = 0.1f;
        this.commandingBoat = false;
        this.vehicleRotation = 0.0f;
        this.first = true;
        this.movingVehicle = false;
        this.leftTurnMod = 1.0f;
        this.rightTurnMod = 1.0f;
        this.offZ = 0.0f;
        this.flying = false;
        this.wasOnStair = false;
        this.counter = 0;
        this.acceptedError = false;
        this.isFalling = false;
        this.onFloorOverridden = false;
        this.mhdlog = 0.0f;
    }
    
    protected strictfp boolean isPressed(final int key) {
        return (this.bitmask & key) != 0x0;
    }
    
    public final strictfp void setFlying(final boolean fly) {
        this.flying = fly;
    }
    
    public final strictfp void setIsFalling(final boolean falling) {
        this.isFalling = falling;
    }
    
    public strictfp boolean isFalling() {
        return this.isFalling;
    }
    
    public final strictfp boolean isFlying() {
        return this.flying;
    }
    
    public strictfp boolean isAborted() {
        return this.abort;
    }
    
    protected strictfp void setAbort(final boolean abort) {
        this.abort = abort;
    }
    
    public strictfp boolean isKeyPressed() {
        return this.isPressed(1) || this.isPressed(2) || this.isPressed(4) || this.isPressed(8);
    }
    
    public strictfp void resetBm() {
        this.bitmask = 0;
    }
    
    public final strictfp void movestep(final float maxHeightDiff, final float xTarget, final float zTarget, final float yTarget, final float maxDepth, final float maxHeight, final float rotation, final byte _bitmask, final int estimatedLayer) {
        this.abort = false;
        this.currx = xTarget;
        this.curry = yTarget;
        this.movingVehicle = false;
        this.mhdlog = maxHeightDiff;
        if (estimatedLayer != this.layer) {
            this.handleWrongLayer(estimatedLayer, this.layer);
        }
        final boolean isCommanding = this.isCommanding(maxDepth, maxHeight);
        if (isCommanding) {
            this.maybePrintDebugInfo(1);
        }
        if (xTarget != this.x || yTarget != this.y) {
            final float expxDist = this.x - this.xOld;
            final float expyDist = this.y - this.yOld;
            final float expectedDistance = (float)StrictMath.sqrt(expxDist * expxDist + expyDist * expyDist);
            final float realxDist = xTarget - this.xOld;
            final float realyDist = yTarget - this.yOld;
            final float realDistance = (float)StrictMath.sqrt(realxDist * realxDist + realyDist * realyDist);
            if (this.bridgeCounter <= 0 && !this.movedOnStair()) {
                if (!this.isFalling && realDistance > expectedDistance) {
                    if (this.acceptedError) {
                        this.handleMoveTooFar(realDistance, expectedDistance);
                    }
                    else {
                        this.acceptedError = true;
                    }
                }
                else if (this.acceptedError) {
                    this.handleMoveTooShort(realDistance, expectedDistance);
                }
                else {
                    this.acceptedError = true;
                }
            }
        }
        else if (zTarget != this.z && Math.abs(zTarget - this.z) > 0.25f) {
            if (!this.isFalling && this.bridgeCounter <= 0 && this.bridgeId == -10L && !this.movedOnStair()) {
                if (this.acceptedError) {
                    this.handleZError(zTarget, this.z);
                }
                else {
                    this.acceptedError = true;
                }
            }
        }
        else {
            this.acceptedError = false;
        }
        this.bridgeCounter = Math.max(0, this.bridgeCounter - 1);
        if (this.abort) {
            return;
        }
        final int currentTileX = (int)(xTarget / 4.0f);
        final int currentTileY = (int)(yTarget / 4.0f);
        this.x = xTarget;
        this.y = yTarget;
        this.z = zTarget;
        this.layer = estimatedLayer;
        this.xRot = rotation;
        this.bitmask = _bitmask;
        float speedModifier = this.speedMod;
        float heightTarget = this.getHeight(this.x, this.y, -3000.0f);
        this.inWater = (this.z + this.za <= -1.0f);
        if (isCommanding) {
            this.inWater = false;
        }
        int dirs = 0;
        float xPosMod = 0.0f;
        float yPosMod = 0.0f;
        if (!this.onGround && !this.inWater && !this.commandingBoat && !this.isOnFloor()) {
            speedModifier *= 0.1f;
        }
        speedModifier *= 1.5f;
        if (this.climbing) {
            speedModifier *= 0.25f;
        }
        if (this.isPressed(1)) {
            ++dirs;
            if (isCommanding) {
                if (speedModifier > 0.0f) {
                    xPosMod += (float)StrictMath.sin(this.vehicleRotation * 0.017453292f) * this.mountSpeed;
                    if (!this.serverWestAvailable && xPosMod < 0.0f) {
                        xPosMod = 0.0f;
                    }
                    else if (!this.serverEastAvailable && xPosMod > 0.0f) {
                        xPosMod = 0.0f;
                    }
                    yPosMod -= (float)StrictMath.cos(this.vehicleRotation * 0.017453292f) * this.mountSpeed;
                    if (!this.serverNorthAvailable && yPosMod < 0.0f) {
                        yPosMod = 0.0f;
                    }
                    else if (!this.serverSouthAvailable && yPosMod > 0.0f) {
                        yPosMod = 0.0f;
                    }
                    this.movingVehicle = true;
                }
            }
            else {
                xPosMod += (float)StrictMath.sin(rotation * 0.017453292f) * 0.08f * speedModifier;
                yPosMod -= (float)StrictMath.cos(rotation * 0.017453292f) * 0.08f * speedModifier;
            }
        }
        if (this.isPressed(2)) {
            ++dirs;
            if (isCommanding) {
                if (speedModifier > 0.0f) {
                    xPosMod -= (float)StrictMath.sin(this.vehicleRotation * 0.017453292f) * this.mountSpeed * 0.3f;
                    if (!this.serverWestAvailable && xPosMod < 0.0f) {
                        xPosMod = 0.0f;
                    }
                    else if (!this.serverEastAvailable && xPosMod > 0.0f) {
                        xPosMod = 0.0f;
                    }
                    yPosMod += (float)StrictMath.cos(this.vehicleRotation * 0.017453292f) * this.mountSpeed * 0.3f;
                    if (!this.serverNorthAvailable && yPosMod < 0.0f) {
                        yPosMod = 0.0f;
                    }
                    else if (!this.serverSouthAvailable && yPosMod > 0.0f) {
                        yPosMod = 0.0f;
                    }
                    this.movingVehicle = true;
                }
            }
            else {
                xPosMod -= (float)StrictMath.sin(rotation * 0.017453292f) * 0.08f * speedModifier;
                yPosMod += (float)StrictMath.cos(rotation * 0.017453292f) * 0.08f * speedModifier;
            }
        }
        if (this.isPressed(4)) {
            ++dirs;
            if (isCommanding) {
                if (!this.commandingBoat || this.windImpact != 0.0f) {
                    if (!this.commandingBoat) {
                        if (this.movingVehicle) {
                            ++this.leftTurnMod;
                            int mod = 3;
                            if (this.leftTurnMod > 20.0f) {
                                mod = 2;
                            }
                            if (this.leftTurnMod > 40.0f) {
                                mod = 1;
                            }
                            this.vehicleRotation = normalizeAngle(this.vehicleRotation - mod);
                        }
                        else if (speedModifier > 0.0f) {
                            ++this.leftTurnMod;
                            int mod = 3;
                            if (this.leftTurnMod > 20.0f) {
                                mod = 2;
                            }
                            if (this.leftTurnMod > 40.0f) {
                                mod = 1;
                            }
                            this.vehicleRotation = normalizeAngle(this.vehicleRotation - mod);
                            xPosMod += (float)StrictMath.sin(this.vehicleRotation * 0.017453292f) * this.mountSpeed * 0.3f;
                            if (!this.serverWestAvailable && xPosMod < 0.0f) {
                                xPosMod = 0.0f;
                            }
                            else if (!this.serverEastAvailable && xPosMod > 0.0f) {
                                xPosMod = 0.0f;
                            }
                            yPosMod -= (float)StrictMath.cos(this.vehicleRotation * 0.017453292f) * this.mountSpeed * 0.3f;
                            if (!this.serverNorthAvailable && yPosMod < 0.0f) {
                                yPosMod = 0.0f;
                            }
                            else if (!this.serverSouthAvailable && yPosMod > 0.0f) {
                                yPosMod = 0.0f;
                            }
                            this.movingVehicle = true;
                        }
                    }
                    else {
                        this.vehicleRotation = normalizeAngle(this.vehicleRotation - 1.0f);
                    }
                }
            }
            else {
                xPosMod -= (float)StrictMath.cos(rotation * 0.017453292f) * 0.08f * speedModifier;
                yPosMod -= (float)StrictMath.sin(rotation * 0.017453292f) * 0.08f * speedModifier;
            }
        }
        else {
            this.leftTurnMod = 0.0f;
        }
        if (this.isPressed(8)) {
            ++dirs;
            if (isCommanding) {
                if (!this.commandingBoat || this.windImpact != 0.0f) {
                    if (!this.commandingBoat) {
                        if (this.movingVehicle) {
                            ++this.rightTurnMod;
                            int mod = 3;
                            if (this.rightTurnMod > 20.0f) {
                                mod = 2;
                            }
                            if (this.rightTurnMod > 40.0f) {
                                mod = 1;
                            }
                            this.vehicleRotation = normalizeAngle(this.vehicleRotation + mod);
                        }
                        else if (speedModifier > 0.0f) {
                            ++this.rightTurnMod;
                            int mod = 3;
                            if (this.rightTurnMod > 20.0f) {
                                mod = 2;
                            }
                            if (this.rightTurnMod > 40.0f) {
                                mod = 1;
                            }
                            this.vehicleRotation = normalizeAngle(this.vehicleRotation + mod);
                            xPosMod += (float)StrictMath.sin(this.vehicleRotation * 0.017453292f) * this.mountSpeed * 0.3f;
                            if (!this.serverWestAvailable && xPosMod < 0.0f) {
                                xPosMod = 0.0f;
                            }
                            else if (!this.serverEastAvailable && xPosMod > 0.0f) {
                                xPosMod = 0.0f;
                            }
                            yPosMod -= (float)StrictMath.cos(this.vehicleRotation * 0.017453292f) * this.mountSpeed * 0.3f;
                            if (!this.serverNorthAvailable && yPosMod < 0.0f) {
                                yPosMod = 0.0f;
                            }
                            else if (!this.serverSouthAvailable && yPosMod > 0.0f) {
                                yPosMod = 0.0f;
                            }
                            this.movingVehicle = true;
                        }
                    }
                    else {
                        this.vehicleRotation = normalizeAngle(this.vehicleRotation + 1.0f);
                    }
                }
            }
            else {
                xPosMod += (float)StrictMath.cos(rotation * 0.017453292f) * 0.08f * speedModifier;
                yPosMod += (float)StrictMath.sin(rotation * 0.017453292f) * 0.08f * speedModifier;
            }
        }
        else {
            this.rightTurnMod = 0.0f;
        }
        if (dirs > 0) {
            this.xa += (float)(xPosMod / StrictMath.sqrt(dirs));
            this.ya += (float)(yPosMod / StrictMath.sqrt(dirs));
        }
        if (this.windImpact != 0.0f && speedModifier > 0.0f) {
            final float strength = getWindPower(this.windRotation - 180.0f, this.vehicleRotation);
            float driftx = this.diffWindX * this.windImpact * 0.05f;
            float drifty = this.diffWindY * this.windImpact * 0.05f;
            if (!this.serverWestAvailable && driftx < 0.0f) {
                driftx = 0.0f;
            }
            if (!this.serverEastAvailable && driftx > 0.0f) {
                driftx = 0.0f;
            }
            if (!this.serverSouthAvailable && drifty > 0.0f) {
                drifty = 0.0f;
            }
            if (!this.serverNorthAvailable && drifty < 0.0f) {
                drifty = 0.0f;
            }
            this.xa += driftx;
            this.ya += drifty;
            float windx = (float)StrictMath.sin(this.vehicleRotation * 0.017453292f) * Math.abs(this.windStrength) * this.windImpact * strength;
            float windy = (float)StrictMath.cos(this.vehicleRotation * 0.017453292f) * Math.abs(this.windStrength) * this.windImpact * strength;
            if (!this.serverWestAvailable && windx < 0.0f) {
                windx = 0.0f;
            }
            if (!this.serverEastAvailable && windx > 0.0f) {
                windx = 0.0f;
            }
            if (!this.serverSouthAvailable && windy > 0.0f) {
                windy = 0.0f;
            }
            if (!this.serverNorthAvailable && windy < 0.0f) {
                windy = 0.0f;
            }
            this.xa += windx;
            this.ya -= windy;
        }
        final float waterHeight = -1.45f;
        if (this.commandingBoat) {
            final float dHeight = this.getHeight(this.x + this.xa, this.y + this.ya, this.getHeight(this.x, this.y, -3000.0f));
            if (dHeight < maxDepth || dHeight > maxHeight) {
                this.xa = 0.0f;
                this.ya = 0.0f;
            }
            if (this.layer == 0 && this.getTextureForTile(currentTileX, currentTileY, this.layer, this.bridgeId) == Tiles.Tile.TILE_HOLE.id) {
                this.setLayer(this.layer = -1);
            }
        }
        else if (heightTarget < waterHeight) {
            heightTarget = waterHeight;
            this.xa *= 0.6f;
            this.ya *= 0.6f;
            final float dHeight = this.getHeight(this.x + this.xa, this.y + this.ya, this.getHeight(this.x, this.y, -3000.0f));
            if (dHeight < maxDepth || dHeight > maxHeight) {
                this.xa = 0.0f;
                this.ya = 0.0f;
            }
            if (this.onGround && this.layer == 0 && this.getTextureForTile(currentTileX, currentTileY, this.layer, this.bridgeId) == Tiles.Tile.TILE_HOLE.id) {
                this.setLayer(this.layer = -1);
            }
        }
        else if (this.onGround) {
            if (this.layer == 0 && this.getTextureForTile(currentTileX, currentTileY, this.layer, this.bridgeId) == Tiles.Tile.TILE_HOLE.id) {
                this.setLayer(this.layer = -1);
            }
            final float tileSpeedMod = this.isOnFloor() ? 1.0f : this.getSpeedForTile(currentTileX, currentTileY, this.layer);
            this.xa *= tileSpeedMod;
            this.ya *= tileSpeedMod;
            final float dHeight2 = this.getHeight(this.x + this.xa, this.y + this.ya, this.getHeight(this.x, this.y, -3000.0f));
            if (dHeight2 < maxDepth || dHeight2 > maxHeight) {
                this.xa = 0.0f;
                this.ya = 0.0f;
            }
            else {
                final float hDiff = this.getHeight(this.x + this.xa, this.y + this.ya, heightTarget) - heightTarget;
                if (hDiff > 0.0f) {
                    final float dist = (float)StrictMath.sqrt(this.xa * this.xa + this.ya * this.ya);
                    this.xa /= hDiff * hDiff / dist * 10.0f + 1.0f;
                    this.ya /= hDiff * hDiff / dist * 10.0f + 1.0f;
                }
                final int ntx = (int)StrictMath.floor((this.x + this.xa) / 4.0f);
                final int nty = (int)StrictMath.floor((this.y + this.ya) / 4.0f);
                if (currentTileX != ntx || currentTileY != nty) {
                    final byte text = this.getTextureForTile(ntx, nty, this.layer, this.bridgeId);
                    if (!Tiles.isSolidCave(text) && text != Tiles.Tile.TILE_HOLE.id && this.getTileSteepness(ntx, nty, this.layer) > maxHeightDiff * 100.0f && (this.getHeightOfBridge(ntx, nty, this.layer) <= -1000.0f || (this.bridgeId <= 0L && hDiff > 0.0f))) {
                        this.xa = 0.0f;
                        this.ya = 0.0f;
                    }
                }
                if (this.started && !this.climbing && !isCommanding) {
                    final float suggestedHeight = this.getHeight(this.x, this.y, -3000.0f);
                    float xSlip = (this.getHeight(this.x - 0.25f, this.y, suggestedHeight) - this.getHeight(this.x + 0.25f, this.y, suggestedHeight)) / 0.5f;
                    float ySlip = (this.getHeight(this.x, this.y - 0.25f, suggestedHeight) - this.getHeight(this.x, this.y + 0.25f, suggestedHeight)) / 0.5f;
                    final float slipTreshold = 0.6f;
                    final float slipDampen = 0.3f;
                    if (xSlip > 0.6f) {
                        xSlip -= 0.3f;
                    }
                    else if (xSlip < -0.6f) {
                        xSlip += 0.3f;
                    }
                    else {
                        xSlip = 0.0f;
                    }
                    if (ySlip > 0.6f) {
                        ySlip -= 0.3f;
                    }
                    else if (ySlip < -0.6f) {
                        ySlip += 0.3f;
                    }
                    else {
                        ySlip = 0.0f;
                    }
                    if (xSlip != 0.0f || ySlip != 0.0f) {
                        float slipDist = xSlip * xSlip + ySlip * ySlip;
                        float dist2 = slipDist * 0.25f;
                        if (dist2 > 0.2f) {
                            dist2 = 0.2f;
                        }
                        slipDist = (float)Math.sqrt(slipDist);
                        xSlip = xSlip * dist2 / slipDist;
                        ySlip = ySlip * dist2 / slipDist;
                        this.xa += xSlip;
                        this.ya += ySlip;
                    }
                }
            }
        }
        else if (this.layer == 0 && this.getTextureForTile(currentTileX, currentTileY, this.layer, this.bridgeId) == Tiles.Tile.TILE_HOLE.id) {
            this.setLayer(this.layer = -1);
        }
        else {
            if (this.isOnFloor() && this.bridgeId <= 0L) {
                final float tileSpeedMod = 1.0f;
                this.xa *= 1.0f;
                this.ya *= 1.0f;
            }
            else if (this.bridgeId > 0L) {
                final float tileSpeedMod = this.getSpeedForTile(currentTileX, currentTileY, this.layer);
                this.xa *= tileSpeedMod;
                this.ya *= tileSpeedMod;
            }
            boolean onBridge = false;
            if (isCommanding && !this.commandingBoat && this.bridgeId == -10L) {
                final float hDiff2 = this.getHeight(this.x + this.xa, this.y + this.ya, heightTarget) - heightTarget;
                if (hDiff2 > 0.0f) {
                    final float dist3 = (float)StrictMath.sqrt(this.xa * this.xa + this.ya * this.ya);
                    this.xa /= hDiff2 * hDiff2 / dist3 * 20.0f + 1.0f;
                    this.ya /= hDiff2 * hDiff2 / dist3 * 20.0f + 1.0f;
                }
                final int ntx2 = (int)StrictMath.floor((this.x + this.xa) / 4.0f);
                final int nty2 = (int)StrictMath.floor((this.y + this.ya) / 4.0f);
                if (currentTileX != ntx2 || currentTileY != nty2) {
                    final byte text2 = this.getTextureForTile(ntx2, nty2, this.layer, this.bridgeId);
                    if (!Tiles.isSolidCave(text2) && text2 != Tiles.Tile.TILE_HOLE.id && this.getTileSteepness(ntx2, nty2, this.layer) > maxHeightDiff * 100.0f) {
                        if (this.getHeightOfBridge(ntx2, nty2, this.layer) <= -1000.0f || (this.bridgeId <= 0L && hDiff2 > 0.0f)) {
                            this.xa = 0.0f;
                            this.ya = 0.0f;
                        }
                        else {
                            onBridge = true;
                        }
                    }
                }
            }
            else if (this.bridgeId != -10L) {
                final float hDiff2 = this.getHeight(this.x + this.xa, this.y + this.ya, heightTarget) - heightTarget;
                if (hDiff2 > 0.0f && hDiff2 < 1.0f) {
                    final float dist3 = (float)StrictMath.sqrt(this.xa * this.xa + this.ya * this.ya);
                    this.xa /= hDiff2 * hDiff2 / dist3 * 10.0f + 1.0f;
                    this.ya /= hDiff2 * hDiff2 / dist3 * 10.0f + 1.0f;
                }
                this.maybePrintDebugInfo(75);
            }
            final float dHeight2 = this.getHeight(this.x + this.xa, this.y + this.ya, this.getHeight(this.x, this.y, -3000.0f));
            if (!onBridge && (dHeight2 < maxDepth || dHeight2 > maxHeight)) {
                this.xa = 0.0f;
                this.ya = 0.0f;
            }
        }
        if (Math.abs(this.getTargetGroundOffset() - this.getGroundOffset()) > 3.0f) {
            this.xa = 0.0f;
            this.ya = 0.0f;
        }
        float dist4 = this.xa * this.xa + this.ya * this.ya;
        final float maxSpeed = 0.65000004f;
        if (dist4 > 0.42250004f) {
            dist4 = (float)Math.sqrt(dist4);
            this.xa = this.xa / dist4 * 0.65000004f;
            this.ya = this.ya / dist4 * 0.65000004f;
            this.za = this.za / dist4 * 0.65000004f;
        }
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        final int nextTileX = (int)((this.x + this.xa) / 4.0f);
        final int nextTileY = (int)((this.y + this.ya) / 4.0f);
        if (this.layer == -1 && Tiles.isSolidCave(this.getTextureForTile(currentTileX, currentTileY, this.layer, this.bridgeId))) {
            this.handlePlayerInRock();
        }
        else if (this.layer == -1 && this.getTextureForTile(currentTileX, currentTileY, this.layer, this.bridgeId) == Tiles.Tile.TILE_CAVE_EXIT.id) {
            if (Tiles.isSolidCave(this.getTextureForTile(nextTileX, nextTileY, this.layer, this.bridgeId))) {
                this.layer = 0;
                final float dHeight3 = this.getHeight(this.x + this.xa, this.y + this.ya, this.getHeight(this.x, this.y, -3000.0f));
                if (dHeight3 > maxHeight) {
                    this.layer = -1;
                    this.xa = 0.0f;
                    this.ya = 0.0f;
                }
                else {
                    this.setLayer(this.layer);
                }
            }
            else {
                final int diffx = nextTileX - currentTileX;
                final int diffy = nextTileY - currentTileY;
                if (diffx != 0 && diffy != 0) {
                    if (diffx < 0 && diffy < 0) {
                        final byte text3 = this.getTextureForTile(currentTileX - 1, currentTileY, -1, this.bridgeId);
                        final byte text4 = this.getTextureForTile(currentTileX, currentTileY - 1, -1, this.bridgeId);
                        if (Tiles.isSolidCave(text3) && Tiles.isSolidCave(text4)) {
                            this.xa = 0.0f;
                            this.ya = 0.0f;
                        }
                    }
                    if (diffx > 0 && diffy < 0) {
                        final byte text3 = this.getTextureForTile(currentTileX + 1, currentTileY, -1, this.bridgeId);
                        final byte text4 = this.getTextureForTile(currentTileX, currentTileY - 1, -1, this.bridgeId);
                        if (Tiles.isSolidCave(text3) && Tiles.isSolidCave(text4)) {
                            this.xa = 0.0f;
                            this.ya = 0.0f;
                        }
                    }
                    if (diffx > 0 && diffy > 0) {
                        final byte text3 = this.getTextureForTile(currentTileX + 1, currentTileY, -1, this.bridgeId);
                        final byte text4 = this.getTextureForTile(currentTileX, currentTileY + 1, -1, this.bridgeId);
                        if (Tiles.isSolidCave(text3) && Tiles.isSolidCave(text4)) {
                            this.xa = 0.0f;
                            this.ya = 0.0f;
                        }
                    }
                    if (diffx < 0 && diffy > 0) {
                        final byte text3 = this.getTextureForTile(currentTileX - 1, currentTileY, -1, this.bridgeId);
                        final byte text4 = this.getTextureForTile(currentTileX, currentTileY + 1, -1, this.bridgeId);
                        if (Tiles.isSolidCave(text3) && Tiles.isSolidCave(text4)) {
                            this.xa = 0.0f;
                            this.ya = 0.0f;
                        }
                    }
                }
            }
        }
        this.x += this.xa;
        this.y += this.ya;
        this.z += this.za;
        this.updateGroundOffset();
        final float nextHeightTarget = Math.max(this.getHeight(this.x, this.y, -3000.0f), waterHeight);
        this.onGround = (this.z <= nextHeightTarget && !this.isOnFloor());
        if ((isCommanding || this.offZ != 0.0f) && (!this.isOnFloor() || !this.commandingBoat)) {
            this.onGround = false;
            this.inWater = false;
            if (!this.commandingBoat) {
                if (this.isOnFloor() && this.z - nextHeightTarget > 2.9 + this.groundOffset / 10.0f) {
                    this.za = 0.0f;
                }
                else {
                    this.z = nextHeightTarget + (this.isOnFloor() ? 0.25f : 0.0f);
                    this.za = 0.0f;
                }
            }
        }
        else if (this.onGround) {
            boolean landed = false;
            if (this.za < -0.25 && !this.inWater && this.bridgeId <= 0L) {
                this.hitGround(-this.za);
                landed = true;
            }
            if (landed && nextHeightTarget > heightTarget) {
                final float dzPlayer = this.z - this.zOld;
                final float dzTerrain = nextHeightTarget - heightTarget;
                final float intersection = (this.zOld - heightTarget) / (dzTerrain - dzPlayer);
                this.xa = 0.0f;
                this.ya = 0.0f;
                this.za = 0.0f;
                this.x = this.xOld + intersection * (this.x - this.xOld);
                this.y = this.yOld + intersection * (this.y - this.yOld);
                this.z = this.zOld + intersection * dzPlayer;
            }
            else {
                this.z = nextHeightTarget;
                this.za = 0.0f;
            }
        }
        else if (this.isOnFloor()) {
            if (this.bridgeId <= 0L || this.z < nextHeightTarget) {
                if (this.isAdjustingGroundOffset() || (this.xa == 0.0f && this.ya == 0.0f)) {
                    this.z = nextHeightTarget;
                }
                else {
                    this.z = this.zOld;
                }
            }
            if (this.za < -0.25 && !this.inWater && this.isOnFloor() && !isCommanding) {
                this.hitGround(-this.za);
            }
            this.za = 0.0f;
        }
        if (this.onGround || this.inWater || isCommanding || this.isOnFloor()) {
            this.xa *= this.getMoveMod();
            this.ya *= this.getMoveMod();
        }
        if ((isCommanding || this.offZ != 0.0f || this.flying) && !this.isFalling()) {
            this.za = 0.0f;
        }
        else if (this.started) {
            this.za -= this.getFallMod();
        }
        if (this.wasOnStair) {
            this.wasOnStair = false;
        }
        if (isCommanding) {
            this.maybePrintDebugInfo(100);
        }
    }
    
    protected strictfp float getWaterLevel(final float x, final float y) {
        return 0.0f;
    }
    
    private final strictfp void maybePrintDebugInfo(final int step) {
        this.maybePrintDebugInfo(step, 0.0f, 0.0f, 0.0f);
    }
    
    private final strictfp void maybePrintDebugInfo(final int step, final float val1, final float val2, final float val3) {
    }
    
    public strictfp void setOnFloorOverride(final boolean onFloor) {
        if (onFloor != this.onFloorOverridden) {
            this.counter = 0;
        }
        if (onFloor) {
            this.onFloorOverridden = true;
        }
        else {
            this.onFloorOverridden = false;
        }
    }
    
    public strictfp boolean getOnFloorOverride() {
        return this.onFloorOverridden;
    }
    
    public strictfp boolean isOnFloor() {
        return (this.getGroundOffset() > 0.0f && !this.isAdjustingGroundOffset()) || this.bridgeId > 0L || this.onFloorOverridden;
    }
    
    public final strictfp float getFallMod() {
        return 0.04f;
    }
    
    public final strictfp float getMoveMod() {
        return 0.4f;
    }
    
    public strictfp boolean movedOnStair() {
        return this.wasOnStair;
    }
    
    private final strictfp boolean isCommanding(final float maxDepth, final float maxHeight) {
        return maxDepth > -2500.0f || maxHeight < 2500.0f;
    }
    
    protected abstract void hitGround(final float p0);
    
    public abstract float getTileSteepness(final int p0, final int p1, final int p2);
    
    private strictfp float getHeight(final float xp, final float yp, final float suggestedHeight) {
        final int xx = (int)StrictMath.floor(xp / 4.0f);
        final int yy = (int)StrictMath.floor(yp / 4.0f);
        if (this.layer == 0 && this.getTextureForTile(xx, yy, this.layer, this.bridgeId) == Tiles.Tile.TILE_HOLE.id) {
            return this.getHeight(xp, yp, suggestedHeight, -1);
        }
        if (this.layer == -1 && Tiles.isSolidCave(this.getTextureForTile(xx, yy, this.layer, this.bridgeId))) {
            return suggestedHeight;
        }
        return this.getHeight(xp, yp, suggestedHeight, this.layer);
    }
    
    private final strictfp float getHeight(final float xp, final float yp, final float suggestedHeight, final int layer) {
        final int xx = (int)StrictMath.floor(xp / 4.0f);
        final int yy = (int)StrictMath.floor(yp / 4.0f);
        final float xa = xp / 4.0f - xx;
        final float ya = yp / 4.0f - yy;
        if (layer == -1 && suggestedHeight > -2999.0f) {
            final byte id = this.getTextureForTile(xx, yy, layer, this.bridgeId);
            if (id == Tiles.Tile.TILE_CAVE_WALL.id || id == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                return suggestedHeight;
            }
        }
        final float[] hts = this.getNodeHeights(xx, yy, layer, this.bridgeId);
        final float height = hts[0] * (1.0f - xa) * (1.0f - ya) + hts[1] * xa * (1.0f - ya) + hts[2] * (1.0f - xa) * ya + hts[3] * xa * ya;
        return height + this.getCurrentGroundOffset() / 10.0f;
    }
    
    public final strictfp void setSpeedModifier(final float speedModifier) {
        this.speedMod = speedModifier;
    }
    
    public final strictfp void setPosition(final float x, final float y, final float z, final float xRot, final int _layer) {
        this.abort = true;
        this.onGround = false;
        this.inWater = false;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.layer = _layer;
        this.xa = 0.0f;
        this.ya = 0.0f;
        this.za = 0.0f;
        if (this.layer != _layer) {
            this.setLayer(_layer);
        }
    }
    
    public final strictfp void changeLayer(final int _layer) {
        this.layer = _layer;
    }
    
    private strictfp float getSpeedForTile(final int xTile, final int yTile, final int layer) {
        try {
            return Tiles.getTile(this.getTextureForTile(xTile, yTile, layer, this.bridgeId)).getSpeed();
        }
        catch (NullPointerException e) {
            System.out.println("Can't get speed for tile " + xTile + ", " + yTile + ", layer " + layer + ", since it's of id " + this.getTextureForTile(xTile, yTile, layer, this.bridgeId));
            return 0.1f;
        }
    }
    
    public static final strictfp byte buildBitmap(final boolean f, final boolean b, final boolean l, final boolean r) {
        byte result = 0;
        if (f) {
            result |= 0x1;
        }
        if (b) {
            result |= 0x2;
        }
        if (l) {
            result |= 0x4;
        }
        if (r) {
            result |= 0x8;
        }
        return result;
    }
    
    public final strictfp float getX() {
        return this.x;
    }
    
    public final strictfp float getY() {
        return this.y;
    }
    
    public final strictfp float getZ() {
        return this.z;
    }
    
    public final strictfp float getRot() {
        return this.xRot;
    }
    
    protected strictfp boolean isServerWestAvailable() {
        return this.serverWestAvailable;
    }
    
    protected strictfp void setServerWestAvailable(final boolean serverWestAvailable) {
        this.serverWestAvailable = serverWestAvailable;
    }
    
    protected strictfp boolean isServerNorthAvailable() {
        return this.serverNorthAvailable;
    }
    
    protected strictfp void setServerNorthAvailable(final boolean serverNorthAvailable) {
        this.serverNorthAvailable = serverNorthAvailable;
    }
    
    protected strictfp boolean isServerEastAvailable() {
        return this.serverEastAvailable;
    }
    
    protected strictfp void setServerEastAvailable(final boolean serverEastAvailable) {
        this.serverEastAvailable = serverEastAvailable;
    }
    
    protected strictfp boolean isServerSouthAvailable() {
        return this.serverSouthAvailable;
    }
    
    protected strictfp void setServerSouthAvailable(final boolean serverSouthAvailable) {
        this.serverSouthAvailable = serverSouthAvailable;
    }
    
    protected strictfp float getXa() {
        return this.xa;
    }
    
    protected strictfp void setXa(final float xa) {
        this.xa = xa;
    }
    
    protected strictfp float getYa() {
        return this.ya;
    }
    
    protected strictfp void setYa(final float ya) {
        this.ya = ya;
    }
    
    protected strictfp float getZa() {
        return this.za;
    }
    
    protected strictfp void setZa(final float za) {
        this.za = za;
    }
    
    protected strictfp boolean isOnGround() {
        return this.onGround;
    }
    
    protected strictfp void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }
    
    public strictfp boolean isInWater() {
        return this.inWater;
    }
    
    protected strictfp void setInWater(final boolean inWater) {
        this.inWater = inWater;
    }
    
    protected strictfp boolean isIgnoreErrors() {
        return this.ignoreErrors;
    }
    
    protected strictfp void setIgnoreErrors(final boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }
    
    protected strictfp boolean isStarted() {
        return this.started;
    }
    
    protected strictfp void setStarted(final boolean started) {
        this.started = started;
    }
    
    protected strictfp float getDiffWindX() {
        return this.diffWindX;
    }
    
    protected strictfp void setDiffWindX(final float diffWindX) {
        this.diffWindX = diffWindX;
    }
    
    protected strictfp float getDiffWindY() {
        return this.diffWindY;
    }
    
    protected strictfp void setDiffWindY(final float diffWindY) {
        this.diffWindY = diffWindY;
    }
    
    protected strictfp boolean isCommandingBoat() {
        return this.commandingBoat;
    }
    
    protected strictfp void setCommandingBoat(final boolean commandingBoat) {
        this.commandingBoat = commandingBoat;
    }
    
    protected strictfp float getCurrx() {
        return this.currx;
    }
    
    protected strictfp float getCurry() {
        return this.curry;
    }
    
    protected strictfp boolean isFirst() {
        return this.first;
    }
    
    protected strictfp void setFirst(final boolean first) {
        this.first = first;
    }
    
    protected strictfp boolean isMovingVehicle() {
        return this.movingVehicle;
    }
    
    protected strictfp float getOffZ() {
        return this.offZ;
    }
    
    protected strictfp void setOffZ(final float offZ) {
        this.offZ = offZ;
    }
    
    protected strictfp boolean isClimbing() {
        return this.climbing;
    }
    
    protected strictfp void setX(final float x) {
        this.x = x;
    }
    
    protected strictfp void setY(final float y) {
        this.y = y;
    }
    
    protected strictfp void setZ(final float z) {
        this.z = z;
    }
    
    public abstract float getHeightOfBridge(final int p0, final int p1, final int p2);
    
    protected abstract byte getTextureForTile(final int p0, final int p1, final int p2, final long p3);
    
    protected abstract float getCeilingForNode(final int p0, final int p1);
    
    protected abstract float getHeightForNode(final int p0, final int p1, final int p2);
    
    protected abstract float[] getNodeHeights(final int p0, final int p1, final int p2, final long p3);
    
    protected abstract boolean handleWrongLayer(final int p0, final int p1);
    
    protected abstract boolean handleMoveTooFar(final float p0, final float p1);
    
    protected abstract boolean handleMoveTooShort(final float p0, final float p1);
    
    protected abstract boolean handleZError(final float p0, final float p1);
    
    protected abstract void handlePlayerInRock();
    
    protected strictfp void setLayer(final int layer) {
    }
    
    public final strictfp void fly(final float xTarget, final float yTarget, final float zTarget, final float xRot, final float yRot, final byte bitmask, final int layerTarget) {
        this.x = xTarget;
        this.y = yTarget;
        this.z = zTarget;
        this.layer = layerTarget;
        this.onGround = false;
        this.xRot = xRot;
        this.bitmask = bitmask;
        final float speedModifier = 1.0f;
        int dirs = 0;
        float xPosMod = 0.0f;
        float yPosMod = 0.0f;
        float zPosMod = 0.0f;
        if (this.isPressed(1)) {
            ++dirs;
            xPosMod += (float)StrictMath.sin(xRot * 0.017453292f) * 0.08f * 1.0f * (float)StrictMath.cos(yRot * 0.017453292f);
            yPosMod -= (float)StrictMath.cos(xRot * 0.017453292f) * 0.08f * 1.0f * (float)StrictMath.cos(yRot * 0.017453292f);
            zPosMod -= (float)StrictMath.sin(yRot * 0.017453292f) * 0.08f * 1.0f;
        }
        if (this.isPressed(2)) {
            ++dirs;
            xPosMod -= (float)StrictMath.sin(xRot * 0.017453292f) * 0.08f * 1.0f * (float)StrictMath.cos(yRot * 0.017453292f);
            yPosMod += (float)StrictMath.cos(xRot * 0.017453292f) * 0.08f * 1.0f * (float)StrictMath.cos(yRot * 0.017453292f);
            zPosMod += (float)StrictMath.sin(yRot * 0.017453292f) * 0.08f * 1.0f;
        }
        if (this.isPressed(4)) {
            ++dirs;
            xPosMod -= (float)StrictMath.cos(xRot * 0.017453292f) * 0.08f * 1.0f;
            yPosMod -= (float)StrictMath.sin(xRot * 0.017453292f) * 0.08f * 1.0f;
        }
        if (this.isPressed(8)) {
            ++dirs;
            xPosMod += (float)StrictMath.cos(xRot * 0.017453292f) * 0.08f * 1.0f;
            yPosMod += (float)StrictMath.sin(xRot * 0.017453292f) * 0.08f * 1.0f;
        }
        if (dirs > 0) {
            this.xa += (float)(xPosMod / StrictMath.sqrt(dirs));
            this.ya += (float)(yPosMod / StrictMath.sqrt(dirs));
            this.za += (float)(zPosMod / StrictMath.sqrt(dirs));
        }
        float height = this.getHeight(this.x, this.y, -3000.0f);
        if (height < -1.45) {
            height = -1.45f;
        }
        float dist = this.xa * this.xa + this.ya * this.ya;
        final float maxSpeed = 0.65000004f;
        if (dist > 0.42250004f) {
            dist = (float)Math.sqrt(dist);
            this.xa = this.xa / dist * 0.65000004f;
            this.ya = this.ya / dist * 0.65000004f;
            this.za = this.za / dist * 0.65000004f;
        }
        this.xOld = this.x;
        this.yOld = this.y;
        this.zOld = this.z;
        final int xx = (int)(this.x / 4.0f);
        final int yy = (int)(this.y / 4.0f);
        this.x += this.xa;
        this.y += this.ya;
        this.z += this.za;
        final int newxx = (int)(this.x / 4.0f);
        final int newyy = (int)(this.y / 4.0f);
        if (this.layer == -1 && this.getTextureForTile(xx, yy, this.layer, this.bridgeId) != Tiles.Tile.TILE_CAVE.id && !Tiles.isReinforcedFloor(this.getTextureForTile(xx, yy, this.layer, this.bridgeId)) && this.getTextureForTile(xx, yy, this.layer, this.bridgeId) != Tiles.Tile.TILE_CAVE_EXIT.id) {
            this.handlePlayerInRock();
        }
        else if (this.layer == -1 && this.getTextureForTile(xx, yy, this.layer, this.bridgeId) == Tiles.Tile.TILE_CAVE_EXIT.id) {
            if (this.getTextureForTile(newxx, newyy, this.layer, this.bridgeId) == Tiles.Tile.TILE_CAVE_WALL.id || this.getTextureForTile(newxx, newyy, this.layer, this.bridgeId) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                this.setLayer(this.layer = 0);
            }
            else {
                if (newyy == yy) {
                    final int xa = (newxx >= xx) ? 1 : 0;
                    if (this.getCeilingForNode(xx + xa, yy) < 0.5f && this.getCeilingForNode(xx + xa, yy + 1) < 0.5f) {
                        this.setLayer(this.layer = 0);
                    }
                }
                if (newxx == xx) {
                    final int ya = (newyy >= yy) ? 1 : 0;
                    if (this.getCeilingForNode(xx, yy + ya) < 0.5f && this.getCeilingForNode(xx + 1, yy + ya) < 0.5f) {
                        this.setLayer(this.layer = 0);
                    }
                }
            }
        }
        if (this.z < height) {
            this.z = height;
            this.za = 0.0f;
        }
        this.xa *= 0.9f;
        this.ya *= 0.9f;
        this.za *= 0.9f;
    }
    
    public final strictfp void setClimbing(final boolean climbing) {
        this.climbing = climbing;
    }
    
    public final strictfp int getLayer() {
        return this.layer;
    }
    
    public strictfp void setMountSpeed(final float newMountSpeed) {
        this.mountSpeed = newMountSpeed;
    }
    
    public strictfp float getWindImpact() {
        return this.windImpact;
    }
    
    public strictfp void setWindImpact(final float wrot) {
        this.windImpact = wrot;
    }
    
    public static final strictfp float normalizeAngle(float angle) {
        angle -= (int)(angle / 360.0f) * 360;
        if (angle < 0.0f) {
            angle += 360.0f;
        }
        return angle;
    }
    
    public strictfp void reset() {
        this.setMountSpeed(0.0f);
        this.setWindImpact(0.0f);
        this.setWindRotation(0.0f);
        this.setWindStrength(0.0f);
        this.diffWindX = 0.0f;
        this.diffWindY = 0.0f;
    }
    
    public static final strictfp float getWindPower(final float aWindRotation, final float aVehicleRotation) {
        final float lWindRotation = normalizeAngle(aWindRotation);
        float lVehicleRotation;
        if (lWindRotation > aVehicleRotation) {
            lVehicleRotation = normalizeAngle(lWindRotation - aVehicleRotation);
        }
        else {
            lVehicleRotation = normalizeAngle(aVehicleRotation - lWindRotation);
        }
        if (lVehicleRotation > 150.0f && lVehicleRotation < 210.0f) {
            return 0.0f;
        }
        if (lVehicleRotation > 120.0f && lVehicleRotation < 240.0f) {
            return 0.5f;
        }
        if (lVehicleRotation > 90.0f && lVehicleRotation < 270.0f) {
            return 0.65f;
        }
        if (lVehicleRotation > 60.0f && lVehicleRotation < 300.0f) {
            return 0.8f;
        }
        if (lVehicleRotation > 30.0f && lVehicleRotation < 330.0f) {
            return 1.0f;
        }
        return 0.9f;
    }
    
    public final strictfp float getSpeedMod() {
        return this.speedMod;
    }
    
    public final strictfp float getMountSpeed() {
        return this.mountSpeed;
    }
    
    protected strictfp float getXold() {
        return this.xOld;
    }
    
    protected strictfp float getYold() {
        return this.yOld;
    }
    
    protected strictfp float getZold() {
        return this.zOld;
    }
    
    protected strictfp void setLog(final boolean log) {
    }
    
    public strictfp byte getBitMask() {
        return this.bitmask;
    }
    
    public strictfp float getVehicleRotation() {
        return this.vehicleRotation;
    }
    
    public strictfp void setVehicleRotation(final float rotation) {
        this.vehicleRotation = rotation;
    }
    
    public strictfp float getWindStrength() {
        return this.windStrength;
    }
    
    public strictfp void setWindStrength(final float wstr) {
        this.windStrength = wstr;
    }
    
    public strictfp float getWindRotation() {
        return this.windRotation;
    }
    
    public strictfp void setWindRotation(final float wrot) {
        this.windRotation = wrot;
    }
    
    public strictfp void setGroundOffset(final int newOffset, final boolean immediately) {
        this.setTargetGroundOffset(Math.min(this.getMaxTargetGroundOffset(newOffset), newOffset));
        if (immediately) {
            this.setGroundOffset(this.getTargetGroundOffset());
        }
    }
    
    public strictfp int getMaxTargetGroundOffset(final int suggestedOffset) {
        return suggestedOffset;
    }
    
    public final strictfp float getTargetGroundOffset() {
        return this.targetGroundOffset;
    }
    
    public final strictfp void setTargetGroundOffset(final int newOffset) {
        this.targetGroundOffset = newOffset;
    }
    
    public final strictfp float getGroundOffset() {
        return this.groundOffset;
    }
    
    public final strictfp void setGroundOffset(final float newOffset) {
        this.groundOffset = newOffset;
    }
    
    private final strictfp float getCurrentGroundOffset() {
        return this.getGroundOffset();
    }
    
    private final strictfp void updateGroundOffset() {
        if (this.getTargetGroundOffset() > this.getGroundOffset() + 1.0f) {
            this.setGroundOffset(this.getGroundOffset() + 1.0f);
        }
        else if (this.getTargetGroundOffset() < this.getGroundOffset() - 1.0f) {
            this.setGroundOffset(this.getGroundOffset() - 1.0f);
        }
        else {
            this.setGroundOffset(this.getTargetGroundOffset());
        }
    }
    
    public final strictfp boolean isAdjustingGroundOffset() {
        return this.getGroundOffset() != this.getTargetGroundOffset();
    }
    
    public strictfp String getInfo() {
        return "commanding boat: " + this.commandingBoat + "in water=" + this.inWater + " onground=" + this.onGround + " speedmod=" + this.speedMod + ",mountspeed=" + this.mountSpeed + " vehic rot " + this.vehicleRotation + " windrot=" + this.windRotation + " wind str=" + this.windStrength + " windImpact=" + this.windImpact;
    }
    
    public strictfp long getBridgeId() {
        return this.bridgeId;
    }
    
    public strictfp void setBridgeCounter(final int nums) {
        this.bridgeCounter = nums;
    }
    
    public strictfp void setBridgeId(final long bridgeId) {
        this.bridgeId = bridgeId;
        this.bridgeCounter = 10;
    }
    
    static {
        logger = Logger.getLogger(MovementChecker.class.getName());
    }
}
