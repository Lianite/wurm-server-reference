// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

public final class IPBan implements Ban
{
    private final String identifier;
    private String reason;
    private long expiry;
    private static final String ADD_BANNED_IP = "insert into BANNEDIPS (IPADDRESS,BANREASON,BANEXPIRY) values(?,?,?)";
    private static final String UPDATE_BANNED_IP = "UPDATE BANNEDIPS SET BANREASON=?,BANEXPIRY=? WHERE IPADDRESS=?";
    private static final String GET_BANNED_IPS = "select * from BANNEDIPS";
    private static final String REMOVE_BANNED_IP = "delete from BANNEDIPS where IPADDRESS=?";
    
    public IPBan(final String _identifier, final String _reason, final long _expiry) {
        this.identifier = _identifier;
        this.setReason(_reason);
        this.setExpiry(_expiry);
    }
    
    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpiry();
    }
    
    @Override
    public String getIdentifier() {
        return this.identifier;
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
        return "UPDATE BANNEDIPS SET BANREASON=?,BANEXPIRY=? WHERE IPADDRESS=?";
    }
    
    @Override
    public String getInsertSql() {
        return "insert into BANNEDIPS (IPADDRESS,BANREASON,BANEXPIRY) values(?,?,?)";
    }
    
    @Override
    public String getDeleteSql() {
        return "delete from BANNEDIPS where IPADDRESS=?";
    }
    
    public static String getSelectSql() {
        return "select * from BANNEDIPS";
    }
}
