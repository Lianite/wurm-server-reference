// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.Date;
import java.text.DateFormat;

public class PermissionsHistoryEntry implements Comparable<PermissionsHistoryEntry>
{
    private static final DateFormat df;
    private final long eventDate;
    private final long playerId;
    public final String performer;
    public final String event;
    
    public PermissionsHistoryEntry(final long eventDate, final long playerId, final String performer, final String event) {
        this.eventDate = eventDate;
        this.playerId = playerId;
        this.performer = performer;
        this.event = event;
    }
    
    long getEventDate() {
        return this.eventDate;
    }
    
    String getPlayerName() {
        return this.performer;
    }
    
    String getEvent() {
        return this.event;
    }
    
    public String getDate() {
        return PermissionsHistoryEntry.df.format(new Date(this.eventDate));
    }
    
    public long getPlayerId() {
        return this.playerId;
    }
    
    public String getLongDesc() {
        if (this.performer == null || this.performer.length() == 0) {
            return this.getDate() + "  " + this.event;
        }
        return this.getDate() + "  " + this.performer + " " + this.event;
    }
    
    @Override
    public int compareTo(final PermissionsHistoryEntry he) {
        return Long.compare(this.eventDate, he.eventDate);
    }
    
    @Override
    public String toString() {
        return "PermissionsHistoryEntry [" + this.getLongDesc() + ']';
    }
    
    static {
        df = DateFormat.getDateTimeInstance();
    }
}
