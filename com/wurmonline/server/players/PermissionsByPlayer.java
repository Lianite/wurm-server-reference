// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.io.IOException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class PermissionsByPlayer implements MiscConstants, Comparable<PermissionsByPlayer>
{
    private static Logger logger;
    private long id;
    private Permissions permissions;
    
    PermissionsByPlayer(final long aPlayerId, final int aSettings) {
        this.id = aPlayerId;
        (this.permissions = new Permissions()).setPermissionBits(aSettings);
    }
    
    public long getPlayerId() {
        return this.id;
    }
    
    Permissions getPermissions() {
        return this.permissions;
    }
    
    public boolean hasPermission(final int bit) {
        return this.permissions.hasPermission(bit);
    }
    
    public int getSettings() {
        return this.permissions.getPermissions();
    }
    
    public String getName() {
        return getPlayerOrGroupName(this.id);
    }
    
    @Override
    public int compareTo(final PermissionsByPlayer pbp) {
        return this.getName().compareTo(pbp.getName());
    }
    
    public static String getPlayerOrGroupName(final long playerOrGroupId) {
        try {
            if (playerOrGroupId == -20L) {
                return "Allies";
            }
            if (playerOrGroupId == -30L) {
                return "Citizens";
            }
            if (playerOrGroupId == -40L) {
                return "Kingdom";
            }
            if (playerOrGroupId == -50L) {
                return "Everyone";
            }
            if (playerOrGroupId == -60L) {
                return "Brand Group";
            }
            return Players.getInstance().getNameFor(playerOrGroupId);
        }
        catch (NoSuchPlayerException | IOException ex2) {
            final Exception ex;
            final Exception e = ex;
            return "Unknown";
        }
    }
    
    static {
        PermissionsByPlayer.logger = Logger.getLogger(PermissionsByPlayer.class.getName());
    }
}
