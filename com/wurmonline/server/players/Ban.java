// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.steam.SteamId;

public interface Ban
{
    boolean isExpired();
    
    String getIdentifier();
    
    String getReason();
    
    void setReason(final String p0);
    
    long getExpiry();
    
    void setExpiry(final long p0);
    
    default String getUpdateSql() {
        return "";
    }
    
    default String getInsertSql() {
        return "";
    }
    
    default String getDeleteSql() {
        return "";
    }
    
    default Ban fromString(final String identifier) {
        return fromString(identifier, "", 0L);
    }
    
    default Ban fromString(final String identifier, final String reason, final long expiry) {
        final SteamId id = SteamId.fromAnyString(identifier);
        if (id != null) {
            return new SteamIdBan(id, reason, expiry);
        }
        return new IPBan(identifier, reason, expiry);
    }
}
