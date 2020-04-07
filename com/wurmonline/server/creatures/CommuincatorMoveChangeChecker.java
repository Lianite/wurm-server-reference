// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;

final class CommuincatorMoveChangeChecker
{
    private static final Logger logger;
    
    static boolean checkMoveChanges(final PlayerMove currentmove, final MovementScheme ticker, final Player player, final Logger cheatlogger) {
        checkIsFalling(currentmove, ticker);
        checkFloorOverride(currentmove, ticker);
        return checkBridgeChange(currentmove, player, cheatlogger) | checkClimb(currentmove, ticker) | checkWeather(currentmove, ticker) | checkWindMod(currentmove, player, cheatlogger) | checkMountSpeed(currentmove, player, cheatlogger) | checkSpeedMod(currentmove, player, cheatlogger) | checkHeightOffsetChanged(currentmove, player);
    }
    
    static void checkFloorOverride(final PlayerMove currentmove, final MovementScheme ticker) {
        if (currentmove != null) {
            ticker.setOnFloorOverride(currentmove.isOnFloor());
        }
    }
    
    static boolean checkClimb(final PlayerMove currentmove, final MovementScheme ticker) {
        if (currentmove != null && currentmove.isToggleClimb()) {
            ticker.setServerClimbing(currentmove.isClimbing());
            currentmove.setToggleClimb(false);
            return true;
        }
        return false;
    }
    
    static void checkIsFalling(final PlayerMove currentmove, final MovementScheme ticker) {
        if (currentmove != null) {
            ticker.setIsFalling(currentmove.isFalling());
        }
    }
    
    static boolean checkWeather(final PlayerMove currentmove, final MovementScheme ticker) {
        if (currentmove != null && currentmove.isWeatherChange()) {
            ticker.diffWindX = Server.getWeather().getXWind();
            ticker.diffWindY = Server.getWeather().getYWind();
            ticker.setWindRotation(Server.getWeather().getWindRotation());
            ticker.setWindStrength(Server.getWeather().getWindPower());
            currentmove.setWeatherChange(false);
            return true;
        }
        return false;
    }
    
    static boolean checkSpeedMod(final PlayerMove currentmove, final Player player, final Logger cheatlogger) {
        if (currentmove != null && currentmove.getNewSpeedMod() != -100.0f) {
            player.getMovementScheme().setSpeedModifier(currentmove.getNewSpeedMod());
            currentmove.setNewSpeedMod(-100.0f);
            return true;
        }
        return false;
    }
    
    static boolean checkBridgeChange(final PlayerMove currentmove, final Player player, final Logger cheatlogger) {
        if (currentmove != null && currentmove.getNewBridgeId() != 0L) {
            if (player.getBridgeId() > 0L && currentmove.getNewBridgeId() < 0L && currentmove.getNewHeightOffset() > 0) {
                player.getMovementScheme().setGroundOffset(currentmove.getNewHeightOffset(), currentmove.isChangeHeightImmediately());
            }
            player.setBridgeId(currentmove.getNewBridgeId(), false);
            player.getMovementScheme().setBridgeId(currentmove.getNewBridgeId());
            currentmove.setNewBridgeId(0L);
            return true;
        }
        return false;
    }
    
    static boolean checkWindMod(final PlayerMove currentmove, final Player player, final Logger cheatlogger) {
        if (currentmove != null && currentmove.getNewWindMod() != -100) {
            player.getMovementScheme().setWindMod(currentmove.getNewWindMod());
            currentmove.setNewWindMod((byte)(-100));
            return true;
        }
        return false;
    }
    
    static boolean checkMountSpeed(final PlayerMove currentmove, final Player player, final Logger cheatlogger) {
        if (currentmove != null && currentmove.getNewMountSpeed() != -100) {
            player.getMovementScheme().setMountSpeed(currentmove.getNewMountSpeed());
            currentmove.setNewMountSpeed((short)(-100));
            return true;
        }
        return false;
    }
    
    static boolean checkHeightOffsetChanged(final PlayerMove currentMove, final Player player) {
        if (currentMove != null && currentMove.getNewHeightOffset() != -10000) {
            player.getMovementScheme().setGroundOffset(currentMove.getNewHeightOffset(), currentMove.isChangeHeightImmediately());
            currentMove.setNewHeightOffset(-10000);
            return true;
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(CommuincatorMoveChangeChecker.class.getName());
    }
}
