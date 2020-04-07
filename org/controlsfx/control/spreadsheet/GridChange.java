// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.event.EventType;
import java.io.Serializable;
import javafx.event.Event;

public class GridChange extends Event implements Serializable
{
    public static final EventType<GridChange> GRID_CHANGE_EVENT;
    private static final long serialVersionUID = 210644901287223524L;
    private final int modelRow;
    private final int column;
    private final Object oldValue;
    private final Object newValue;
    
    public GridChange(final int modelRow, final int column, final Object oldValue, final Object newValue) {
        super((EventType)GridChange.GRID_CHANGE_EVENT);
        this.modelRow = modelRow;
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public int getRow() {
        return this.modelRow;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public Object getOldValue() {
        return this.oldValue;
    }
    
    public Object getNewValue() {
        return this.newValue;
    }
    
    static {
        GRID_CHANGE_EVENT = new EventType(Event.ANY, "GridChange");
    }
}
