// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing.logging;

import java.util.Iterator;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class LogTableModel extends AbstractTableModel
{
    protected int maxAgeSeconds;
    protected boolean paused;
    protected List<LogMessage> messages;
    
    public LogTableModel(final int maxAgeSeconds) {
        this.paused = false;
        this.messages = new ArrayList<LogMessage>();
        this.maxAgeSeconds = maxAgeSeconds;
    }
    
    public int getMaxAgeSeconds() {
        return this.maxAgeSeconds;
    }
    
    public void setMaxAgeSeconds(final int maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
    
    public synchronized void pushMessage(final LogMessage message) {
        if (this.paused) {
            return;
        }
        if (this.maxAgeSeconds != Integer.MAX_VALUE) {
            final Iterator<LogMessage> it = this.messages.iterator();
            final long currentTime = new Date().getTime();
            while (it.hasNext()) {
                final LogMessage logMessage = it.next();
                final long delta = this.maxAgeSeconds * 1000;
                if (logMessage.getCreatedOn() + delta < currentTime) {
                    it.remove();
                }
            }
        }
        this.messages.add(message);
        this.fireTableDataChanged();
    }
    
    public Object getValueAt(final int row, final int column) {
        return this.messages.get(row);
    }
    
    public void clearMessages() {
        this.messages.clear();
        this.fireTableDataChanged();
    }
    
    public int getRowCount() {
        return this.messages.size();
    }
    
    public int getColumnCount() {
        return 5;
    }
    
    public Class<?> getColumnClass(final int i) {
        return LogMessage.class;
    }
    
    public String getColumnName(final int column) {
        switch (column) {
            case 0: {
                return "";
            }
            case 1: {
                return "Time";
            }
            case 2: {
                return "Thread";
            }
            case 3: {
                return "Source";
            }
            default: {
                return "Message";
            }
        }
    }
}
