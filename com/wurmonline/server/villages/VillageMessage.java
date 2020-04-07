// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.villages;

import java.util.Date;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.text.DateFormat;

public final class VillageMessage implements Comparable<VillageMessage>
{
    private static final DateFormat df;
    private int villageId;
    private long posterId;
    private long toId;
    private String message;
    private int penColour;
    private long posted;
    private boolean everyone;
    
    public VillageMessage(final int aVillageId, final long aPosterId, final long aToId, final String aMessage, final int thePenColour, final long aPosted, final boolean everyone) {
        this.villageId = aVillageId;
        this.posterId = aPosterId;
        this.toId = aToId;
        this.message = aMessage;
        this.penColour = thePenColour;
        this.posted = aPosted;
        this.everyone = everyone;
    }
    
    @Override
    public int compareTo(final VillageMessage villageMsg) {
        if (this.getVillageId() == villageMsg.getVillageId()) {
            if (this.getToId() < villageMsg.getToId()) {
                return -1;
            }
            if (this.getToId() > villageMsg.getToId()) {
                return 1;
            }
            if (this.getPostedTime() < villageMsg.getPostedTime()) {
                return 1;
            }
            if (this.getPostedTime() > villageMsg.getPostedTime()) {
                return -1;
            }
            return 0;
        }
        else {
            if (this.getVillageId() < villageMsg.getVillageId()) {
                return -1;
            }
            return 1;
        }
    }
    
    public int getPenColour() {
        return this.penColour;
    }
    
    public final long getPosterId() {
        return this.posterId;
    }
    
    public final String getPosterName() {
        return this.getPlayerName(this.posterId);
    }
    
    public final long getToId() {
        return this.toId;
    }
    
    public final String getToNmae() {
        return this.getPlayerName(this.toId);
    }
    
    private final String getPlayerName(final long id) {
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(id);
        if (info == null) {
            return "";
        }
        return info.getName();
    }
    
    public final long getPostedTime() {
        return this.posted;
    }
    
    public String getDate() {
        return VillageMessage.df.format(new Date(this.posted));
    }
    
    public final String getMessage() {
        return this.message;
    }
    
    public final int getVillageId() {
        return this.villageId;
    }
    
    public final boolean isForEveryone() {
        return this.everyone;
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
    
    static {
        df = DateFormat.getDateTimeInstance();
    }
}
