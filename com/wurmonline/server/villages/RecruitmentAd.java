// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Date;

public final class RecruitmentAd implements Comparable<RecruitmentAd>
{
    private int villageId;
    private long contactId;
    private String description;
    private Date created;
    private int kingdom;
    
    public RecruitmentAd(final int _villageId, final long _contactId, final String _description, final Date _created, final int _kingdom) {
        this.villageId = _villageId;
        this.contactId = _contactId;
        this.description = _description;
        this.created = _created;
        this.kingdom = _kingdom;
    }
    
    @Override
    public int compareTo(final RecruitmentAd ad1) {
        if (ad1.getVillageId() == this.getVillageId()) {
            return 1;
        }
        return 0;
    }
    
    public final long getContactId() {
        return this.contactId;
    }
    
    public final String getContactName() {
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(this.contactId);
        if (info == null) {
            return "";
        }
        return info.getName();
    }
    
    public final Date getCreated() {
        return this.created;
    }
    
    public final String getDescription() {
        return this.description;
    }
    
    public final int getKingdom() {
        return this.kingdom;
    }
    
    public final int getVillageId() {
        return this.villageId;
    }
    
    public final String getVillageName() {
        try {
            final Village village = Villages.getVillage(this.villageId);
            return village.getName();
        }
        catch (NoSuchVillageException nsv) {
            return "";
        }
    }
    
    public void setContactId(final long _contactId) {
        this.contactId = _contactId;
    }
    
    public void setCreated(final Date date) {
        this.created = date;
    }
    
    public void setDescription(final String text) {
        this.description = text;
    }
}
