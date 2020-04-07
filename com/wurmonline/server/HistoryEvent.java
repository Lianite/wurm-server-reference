// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.Date;
import java.text.DateFormat;
import net.jcip.annotations.Immutable;

@Immutable
public final class HistoryEvent implements Comparable<HistoryEvent>
{
    private final DateFormat df;
    public final long time;
    public final String performer;
    public final String event;
    public final int identifier;
    
    public HistoryEvent(final long aTime, final String aPerformer, final String aEvent, final int aIdentifier) {
        this.df = DateFormat.getDateTimeInstance();
        this.time = aTime;
        this.performer = aPerformer;
        this.event = aEvent;
        this.identifier = aIdentifier;
    }
    
    public String getDate() {
        return this.df.format(new Date(this.time));
    }
    
    public String getLongDesc() {
        if (this.performer == null || this.performer.length() == 0) {
            return this.getDate() + "  " + this.event;
        }
        return this.getDate() + "  " + this.performer + " " + this.event;
    }
    
    @Override
    public int compareTo(final HistoryEvent he) {
        return Long.compare(this.time, he.time);
    }
    
    @Override
    public String toString() {
        return "HistoryEvent [" + this.getLongDesc() + ']';
    }
}
