// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import com.wurmonline.server.steam.SteamId;

public class SteamIdBan implements Ban
{
    private SteamId identifier;
    private String reason;
    private long expiry;
    private static final String ADD_BANNED_STEAMID = "insert into BANNED_STEAM_IDS (STEAM_ID,BANREASON,BANEXPIRY) values(?,?,?)";
    private static final String UPDATE_BANNED_STEAMID = "UPDATE BANNED_STEAM_IDS SET BANREASON=?,BANEXPIRY=? WHERE STEAM_ID=?";
    private static final String GET_BANNED_STEAMIDS = "select * from BANNED_STEAM_IDS";
    private static final String REMOVE_BANNED_STEAMID = "delete from BANNED_STEAM_IDS where STEAM_ID=?";
    
    public SteamIdBan(final SteamId identifier, final String reason, final long expiry) {
        this.identifier = identifier;
        this.reason = reason;
        this.expiry = expiry;
    }
    
    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpiry();
    }
    
    @Override
    public String getIdentifier() {
        return this.identifier.toString();
    }
    
    @Override
    public String getReason() {
        return this.reason;
    }
    
    @Override
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    @Override
    public long getExpiry() {
        return this.expiry;
    }
    
    @Override
    public void setExpiry(final long expiry) {
        this.expiry = expiry;
    }
    
    @Override
    public String getUpdateSql() {
        return "UPDATE BANNED_STEAM_IDS SET BANREASON=?,BANEXPIRY=? WHERE STEAM_ID=?";
    }
    
    @Override
    public String getInsertSql() {
        return "insert into BANNED_STEAM_IDS (STEAM_ID,BANREASON,BANEXPIRY) values(?,?,?)";
    }
    
    @Override
    public String getDeleteSql() {
        return "delete from BANNED_STEAM_IDS where STEAM_ID=?";
    }
    
    public static String getSelectSql() {
        return "select * from BANNED_STEAM_IDS";
    }
}
