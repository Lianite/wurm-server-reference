// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.event.EventHandler;
import javafx.event.Event;
import java.util.Optional;
import javafx.scene.Node;
import javafx.collections.ObservableSet;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventType;

public interface SpreadsheetCell
{
    public static final EventType EDITABLE_EVENT_TYPE = new EventType("EditableEventType");
    public static final EventType WRAP_EVENT_TYPE = new EventType("WrapTextEventType");
    public static final EventType CORNER_EVENT_TYPE = new EventType("CornerEventType");
    
    boolean match(final SpreadsheetCell p0);
    
    void setItem(final Object p0);
    
    Object getItem();
    
    ObjectProperty<Object> itemProperty();
    
    boolean isEditable();
    
    void setEditable(final boolean p0);
    
    boolean isWrapText();
    
    void setWrapText(final boolean p0);
    
    boolean hasPopup();
    
    void setHasPopup(final boolean p0);
    
    List<MenuItem> getPopupItems();
    
    void setStyle(final String p0);
    
    String getStyle();
    
    StringProperty styleProperty();
    
    void activateCorner(final CornerPosition p0);
    
    void deactivateCorner(final CornerPosition p0);
    
    boolean isCornerActivated(final CornerPosition p0);
    
    StringProperty formatProperty();
    
    String getFormat();
    
    void setFormat(final String p0);
    
    ReadOnlyStringProperty textProperty();
    
    String getText();
    
    SpreadsheetCellType getCellType();
    
    int getRow();
    
    int getColumn();
    
    int getRowSpan();
    
    void setRowSpan(final int p0);
    
    int getColumnSpan();
    
    void setColumnSpan(final int p0);
    
    ObservableSet<String> getStyleClass();
    
    ObjectProperty<Node> graphicProperty();
    
    void setGraphic(final Node p0);
    
    Node getGraphic();
    
    Optional<String> getTooltip();
    
    void addEventHandler(final EventType<Event> p0, final EventHandler<Event> p1);
    
    void removeEventHandler(final EventType<Event> p0, final EventHandler<Event> p1);
    
    public enum CornerPosition
    {
        TOP_LEFT, 
        TOP_RIGHT, 
        BOTTOM_RIGHT, 
        BOTTOM_LEFT;
    }
}
