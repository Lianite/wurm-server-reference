// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.logging.Level;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;

public final class PlayerMove
{
    private static final Logger logger;
    public static final int NOHEIGHTCHANGE = -10000;
    static final float NOSPEEDCHANGE = -100.0f;
    static final byte NOWINDCHANGE = -100;
    static final short NOMOVECHANGE = -100;
    static final byte NOBRIDGECHANGE = 0;
    private float newPosX;
    private float newPosY;
    private float newPosZ;
    private float newRot;
    private byte bm;
    private byte layer;
    private PlayerMove next;
    private int sameMoves;
    private boolean handled;
    private boolean toggleClimb;
    private boolean climbing;
    private boolean weatherChange;
    private float newSpeedMod;
    private byte newWindMod;
    private short newMountSpeed;
    private long newBridgeId;
    private int newHeightOffset;
    private boolean changeHeightImmediately;
    private boolean isOnFloor;
    private boolean isFalling;
    int number;
    boolean cleared;
    
    public PlayerMove() {
        this.next = null;
        this.sameMoves = 0;
        this.handled = false;
        this.toggleClimb = false;
        this.climbing = false;
        this.weatherChange = false;
        this.newSpeedMod = -100.0f;
        this.newWindMod = -100;
        this.newMountSpeed = -100;
        this.newBridgeId = 0L;
        this.newHeightOffset = -10000;
        this.changeHeightImmediately = false;
        this.isOnFloor = false;
        this.isFalling = false;
        this.number = -1;
        this.cleared = false;
    }
    
    public void clear(final boolean clearMoveChanges, final MovementScheme ticker, final Player player, final Logger cheatlogger) {
        PlayerMove nnext;
        for (PlayerMove cnext = this.next; cnext != null; cnext = nnext) {
            if (cnext.cleared) {
                PlayerMove.logger.log(Level.INFO, "This (" + cnext + ") was already cleared. Returning. Next=" + this.next, new Exception());
                return;
            }
            if (clearMoveChanges) {
                CommuincatorMoveChangeChecker.checkMoveChanges(cnext, ticker, player, cheatlogger);
            }
            cnext.cleared = true;
            if (cnext.next == cnext) {
                PlayerMove.logger.log(Level.INFO, "This (" + cnext + ") was same as this. Returning. Next=" + cnext.next, new Exception());
                this.next = null;
                return;
            }
            nnext = cnext.next;
            cnext.next = null;
        }
    }
    
    float getNewPosX() {
        return this.newPosX;
    }
    
    void setNewPosX(final float aNewPosX) {
        this.newPosX = aNewPosX;
    }
    
    float getNewPosY() {
        return this.newPosY;
    }
    
    void setNewPosY(final float aNewPosY) {
        this.newPosY = aNewPosY;
    }
    
    float getNewPosZ() {
        return this.newPosZ;
    }
    
    void setNewPosZ(final float aNewPosZ) {
        this.newPosZ = aNewPosZ;
    }
    
    float getNewRot() {
        return this.newRot;
    }
    
    void setNewRot(final float aNewRot) {
        this.newRot = aNewRot;
    }
    
    byte getBm() {
        return this.bm;
    }
    
    void setBm(final byte aBm) {
        this.bm = aBm;
    }
    
    byte getLayer() {
        return this.layer;
    }
    
    void setLayer(final byte aLayer) {
        this.layer = aLayer;
    }
    
    public PlayerMove getNext() {
        return this.next;
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public void setNext(final PlayerMove aNext) {
        this.next = aNext;
        if (this.next != null) {
            this.next.number = this.number + 1;
        }
    }
    
    int getSameMoves() {
        return this.sameMoves;
    }
    
    void incrementSameMoves() {
        ++this.sameMoves;
    }
    
    void sameNoMoves() {
        this.sameMoves = 1;
    }
    
    void resetSameMoves() {
        this.sameMoves = 0;
    }
    
    boolean isHandled() {
        return this.handled;
    }
    
    void setHandled(final boolean aHandled) {
        this.handled = aHandled;
        if (this.handled) {
            this.number = 0;
        }
    }
    
    boolean isToggleClimb() {
        return this.toggleClimb;
    }
    
    void setToggleClimb(final boolean aToggleClimb) {
        this.toggleClimb = aToggleClimb;
    }
    
    boolean isClimbing() {
        return this.climbing;
    }
    
    boolean isFalling() {
        return this.isFalling;
    }
    
    public void setIsFalling(final boolean falling) {
        this.isFalling = falling;
    }
    
    void setClimbing(final boolean aClimbing) {
        this.climbing = aClimbing;
    }
    
    boolean isWeatherChange() {
        return this.weatherChange;
    }
    
    void setWeatherChange(final boolean aWeatherChange) {
        this.weatherChange = aWeatherChange;
    }
    
    float getNewSpeedMod() {
        return this.newSpeedMod;
    }
    
    void setNewSpeedMod(final float aNewSpeedMod) {
        this.newSpeedMod = aNewSpeedMod;
    }
    
    byte getNewWindMod() {
        return this.newWindMod;
    }
    
    void setNewWindMod(final byte aNewWindMod) {
        this.newWindMod = aNewWindMod;
    }
    
    short getNewMountSpeed() {
        return this.newMountSpeed;
    }
    
    void setNewMountSpeed(final short aNewMountSpeed) {
        this.newMountSpeed = aNewMountSpeed;
    }
    
    public PlayerMove getLast() {
        if (this.next != null) {
            return this.next.getLast();
        }
        return this;
    }
    
    public int getNewHeightOffset() {
        return this.newHeightOffset;
    }
    
    public void setNewHeightOffset(final int aNewHeightOffset) {
        this.newHeightOffset = aNewHeightOffset;
    }
    
    public boolean isChangeHeightImmediately() {
        return this.changeHeightImmediately;
    }
    
    public void setChangeHeightImmediately(final boolean aChangeHeightImmediately) {
        this.changeHeightImmediately = aChangeHeightImmediately;
    }
    
    public long getNewBridgeId() {
        return this.newBridgeId;
    }
    
    public void setNewBridgeId(final long newBridgeId) {
        this.newBridgeId = newBridgeId;
    }
    
    public boolean isOnFloor() {
        return this.isOnFloor;
    }
    
    public void setOnFloor(final boolean aIsOnFloor) {
        this.isOnFloor = aIsOnFloor;
    }
    
    static {
        logger = Logger.getLogger(PlayerMove.class.getName());
    }
}
