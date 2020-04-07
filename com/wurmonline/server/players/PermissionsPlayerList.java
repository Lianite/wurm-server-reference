// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.io.IOException;
import com.wurmonline.server.creatures.Creature;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.wurmonline.server.MiscConstants;

public class PermissionsPlayerList implements MiscConstants
{
    private Map<Long, PermissionsByPlayer> playerPermissions;
    
    public PermissionsPlayerList() {
        this.playerPermissions = new ConcurrentHashMap<Long, PermissionsByPlayer>();
    }
    
    public void remove(final long aPlayerId) {
        this.playerPermissions.remove(aPlayerId);
    }
    
    public boolean isEmpty() {
        return this.playerPermissions.isEmpty();
    }
    
    public int size() {
        return this.playerPermissions.size();
    }
    
    public PermissionsByPlayer[] getPermissionsByPlayer() {
        return this.playerPermissions.values().toArray(new PermissionsByPlayer[this.playerPermissions.size()]);
    }
    
    public PermissionsByPlayer add(final long aPlayerId, final int aPermissions) {
        return this.playerPermissions.put(aPlayerId, new PermissionsByPlayer(aPlayerId, aPermissions));
    }
    
    public boolean hasPermission(final long playerId, final int bit) {
        final Long id = playerId;
        final PermissionsByPlayer playerPerm = this.playerPermissions.get(id);
        return playerPerm != null && playerPerm.hasPermission(bit);
    }
    
    public PermissionsByPlayer getPermissionsByPlayer(final long playerId) {
        final Long id = playerId;
        return this.playerPermissions.get(id);
    }
    
    public Permissions getPermissionsFor(final long playerId) {
        final Long id = playerId;
        final PermissionsByPlayer playerPerm = this.playerPermissions.get(id);
        if (playerPerm != null) {
            return playerPerm.getPermissions();
        }
        final PermissionsByPlayer everyone = this.playerPermissions.get(-10L);
        if (everyone == null) {
            return new Permissions();
        }
        return everyone.getPermissions();
    }
    
    public boolean exists(final long playerId) {
        return this.playerPermissions.containsKey(playerId);
    }
    
    public interface ISettings
    {
        void addDefaultCitizenPermissions();
        
        void addGuest(final long p0, final int p1);
        
        boolean canAllowEveryone();
        
        boolean canChangeOwner(final Creature p0);
        
        boolean canChangeName(final Creature p0);
        
        boolean canHavePermissions();
        
        String getAllianceName();
        
        String getKingdomName();
        
        int getMaxAllowed();
        
        String getObjectName();
        
        String getOwnerName();
        
        PermissionsPlayerList getPermissionsPlayerList();
        
        String getRolePermissionName();
        
        String getSettlementName();
        
        String getTypeName();
        
        String getWarning();
        
        long getWurmId();
        
        int getTemplateId();
        
        boolean isActualOwner(final long p0);
        
        boolean isAllied(final Creature p0);
        
        boolean isCitizen(final Creature p0);
        
        boolean isGuest(final Creature p0);
        
        boolean isGuest(final long p0);
        
        boolean isManaged();
        
        boolean isManageEnabled(final Player p0);
        
        boolean isOwner(final Creature p0);
        
        boolean isOwner(final long p0);
        
        boolean isSameKingdom(final Creature p0);
        
        String mayManageHover(final Player p0);
        
        String mayManageText(final Player p0);
        
        boolean mayShowPermissions(final Creature p0);
        
        String messageOnTick();
        
        String messageUnTick();
        
        String questionOnTick();
        
        String questionUnTick();
        
        void removeGuest(final long p0);
        
        void save() throws IOException;
        
        void setIsManaged(final boolean p0, final Player p1);
        
        boolean setNewOwner(final long p0);
        
        boolean setObjectName(final String p0, final Creature p1);
        
        boolean isItem();
    }
}
