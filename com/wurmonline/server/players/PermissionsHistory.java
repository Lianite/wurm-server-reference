// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.players;

import java.util.LinkedList;

public class PermissionsHistory
{
    private LinkedList<PermissionsHistoryEntry> historyEntries;
    
    public PermissionsHistory() {
        this.historyEntries = new LinkedList<PermissionsHistoryEntry>();
    }
    
    void add(final long eventTime, final long playerId, final String playerName, final String event) {
        this.historyEntries.addFirst(new PermissionsHistoryEntry(eventTime, playerId, playerName, event.replace("\"", "'")));
    }
    
    public PermissionsHistoryEntry[] getHistoryEvents() {
        return this.historyEntries.toArray(new PermissionsHistoryEntry[this.historyEntries.size()]);
    }
    
    public String[] getHistory(final int numevents) {
        String[] hist = new String[0];
        final int lHistorySize = this.historyEntries.size();
        if (lHistorySize > 0) {
            final int numbersToFetch = Math.min(numevents, lHistorySize);
            hist = new String[numbersToFetch];
            final PermissionsHistoryEntry[] events = this.getHistoryEvents();
            for (int x = 0; x < numbersToFetch; ++x) {
                hist[x] = events[x].getLongDesc();
            }
        }
        return hist;
    }
}
