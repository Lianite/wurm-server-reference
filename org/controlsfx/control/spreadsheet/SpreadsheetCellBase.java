// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.event.EventHandler;
import javafx.event.EventType;
import java.util.Objects;
import javafx.event.EventDispatcher;
import javafx.event.EventDispatchChain;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringProperty;
import java.util.ArrayList;
import javafx.event.Event;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MenuItem;
import java.util.List;
import javafx.collections.ObservableSet;
import com.sun.javafx.event.EventHandlerManager;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventTarget;

public class SpreadsheetCellBase implements SpreadsheetCell, EventTarget
{
    private static final int EDITABLE_BIT_POSITION = 4;
    private static final int WRAP_BIT_POSITION = 5;
    private static final int POPUP_BIT_POSITION = 6;
    private final SpreadsheetCellType type;
    private final int row;
    private final int column;
    private int rowSpan;
    private int columnSpan;
    private final StringProperty format;
    private final StringProperty text;
    private final StringProperty styleProperty;
    private final ObjectProperty<Node> graphic;
    private String tooltip;
    private int propertyContainer;
    private final EventHandlerManager eventHandlerManager;
    private ObservableSet<String> styleClass;
    private List<MenuItem> actionsList;
    private final ObjectProperty<Object> item;
    
    public SpreadsheetCellBase(final int row, final int column, final int rowSpan, final int columnSpan) {
        this(row, column, rowSpan, columnSpan, SpreadsheetCellType.OBJECT);
    }
    
    public SpreadsheetCellBase(final int row, final int column, final int rowSpan, final int columnSpan, final SpreadsheetCellType<?> type) {
        this.propertyContainer = 0;
        this.eventHandlerManager = new EventHandlerManager((Object)this);
        this.item = (ObjectProperty<Object>)new SimpleObjectProperty<Object>((Object)this, "item") {
            protected void invalidated() {
                SpreadsheetCellBase.this.updateText();
            }
        };
        this.row = row;
        this.column = column;
        this.rowSpan = rowSpan;
        this.columnSpan = columnSpan;
        this.type = type;
        this.text = (StringProperty)new SimpleStringProperty("");
        this.format = (StringProperty)new SimpleStringProperty("");
        this.graphic = (ObjectProperty<Node>)new SimpleObjectProperty();
        this.format.addListener((ChangeListener)new ChangeListener<String>() {
            public void changed(final ObservableValue<? extends String> arg0, final String arg1, final String arg2) {
                SpreadsheetCellBase.this.updateText();
            }
        });
        this.setEditable(true);
        this.getStyleClass().add((Object)"spreadsheet-cell");
        this.styleProperty = (StringProperty)new SimpleStringProperty();
    }
    
    @Override
    public boolean match(final SpreadsheetCell cell) {
        return this.type.match(cell);
    }
    
    @Override
    public final void setItem(final Object value) {
        if (this.isEditable()) {
            this.item.set(value);
        }
    }
    
    @Override
    public final Object getItem() {
        return this.item.get();
    }
    
    @Override
    public final ObjectProperty<Object> itemProperty() {
        return this.item;
    }
    
    @Override
    public final boolean isEditable() {
        return this.isSet(4);
    }
    
    @Override
    public final void setEditable(final boolean editable) {
        if (this.setMask(editable, 4)) {
            Event.fireEvent((EventTarget)this, new Event(SpreadsheetCellBase.EDITABLE_EVENT_TYPE));
        }
    }
    
    @Override
    public boolean isWrapText() {
        return this.isSet(5);
    }
    
    @Override
    public void setWrapText(final boolean wrapText) {
        if (this.setMask(wrapText, 5)) {
            Event.fireEvent((EventTarget)this, new Event(SpreadsheetCellBase.WRAP_EVENT_TYPE));
        }
    }
    
    @Override
    public boolean hasPopup() {
        return this.isSet(6);
    }
    
    @Override
    public void setHasPopup(final boolean value) {
        this.setMask(value, 6);
        Event.fireEvent((EventTarget)this, new Event(SpreadsheetCellBase.CORNER_EVENT_TYPE));
    }
    
    @Override
    public List<MenuItem> getPopupItems() {
        if (this.actionsList == null) {
            this.actionsList = new ArrayList<MenuItem>();
        }
        return this.actionsList;
    }
    
    @Override
    public final StringProperty formatProperty() {
        return this.format;
    }
    
    @Override
    public final String getFormat() {
        return (String)this.format.get();
    }
    
    @Override
    public final void setFormat(final String format) {
        this.formatProperty().set((Object)format);
        this.updateText();
    }
    
    @Override
    public final ReadOnlyStringProperty textProperty() {
        return (ReadOnlyStringProperty)this.text;
    }
    
    @Override
    public final String getText() {
        return (String)this.text.get();
    }
    
    @Override
    public final SpreadsheetCellType getCellType() {
        return this.type;
    }
    
    @Override
    public final int getRow() {
        return this.row;
    }
    
    @Override
    public final int getColumn() {
        return this.column;
    }
    
    @Override
    public final int getRowSpan() {
        return this.rowSpan;
    }
    
    @Override
    public final void setRowSpan(final int rowSpan) {
        this.rowSpan = rowSpan;
    }
    
    @Override
    public final int getColumnSpan() {
        return this.columnSpan;
    }
    
    @Override
    public final void setColumnSpan(final int columnSpan) {
        this.columnSpan = columnSpan;
    }
    
    @Override
    public final ObservableSet<String> getStyleClass() {
        if (this.styleClass == null) {
            this.styleClass = (ObservableSet<String>)FXCollections.observableSet((Object[])new String[0]);
        }
        return this.styleClass;
    }
    
    @Override
    public void setStyle(final String style) {
        this.styleProperty.set((Object)style);
    }
    
    @Override
    public String getStyle() {
        return (String)this.styleProperty.get();
    }
    
    @Override
    public StringProperty styleProperty() {
        return this.styleProperty;
    }
    
    @Override
    public ObjectProperty<Node> graphicProperty() {
        return this.graphic;
    }
    
    @Override
    public void setGraphic(final Node graphic) {
        this.graphic.set((Object)graphic);
    }
    
    @Override
    public Node getGraphic() {
        return (Node)this.graphic.get();
    }
    
    @Override
    public Optional<String> getTooltip() {
        return Optional.ofNullable(this.tooltip);
    }
    
    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }
    
    @Override
    public void activateCorner(final CornerPosition position) {
        if (this.setMask(true, this.getCornerBitNumber(position))) {
            Event.fireEvent((EventTarget)this, new Event(SpreadsheetCellBase.CORNER_EVENT_TYPE));
        }
    }
    
    @Override
    public void deactivateCorner(final CornerPosition position) {
        if (this.setMask(false, this.getCornerBitNumber(position))) {
            Event.fireEvent((EventTarget)this, new Event(SpreadsheetCellBase.CORNER_EVENT_TYPE));
        }
    }
    
    @Override
    public boolean isCornerActivated(final CornerPosition position) {
        return this.isSet(this.getCornerBitNumber(position));
    }
    
    public EventDispatchChain buildEventDispatchChain(final EventDispatchChain tail) {
        return tail.append((EventDispatcher)this.eventHandlerManager);
    }
    
    @Override
    public String toString() {
        return "cell[" + this.row + "][" + this.column + "]" + this.rowSpan + "-" + this.columnSpan;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SpreadsheetCell)) {
            return false;
        }
        final SpreadsheetCell otherCell = (SpreadsheetCell)obj;
        return otherCell.getRow() == this.row && otherCell.getColumn() == this.column && Objects.equals(otherCell.getText(), this.getText()) && this.rowSpan == otherCell.getRowSpan() && this.columnSpan == otherCell.getColumnSpan() && Objects.equals(this.getStyleClass(), otherCell.getStyleClass());
    }
    
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.column;
        result = 31 * result + this.row;
        result = 31 * result + this.rowSpan;
        result = 31 * result + this.columnSpan;
        result = 31 * result + Objects.hashCode(this.getText());
        result = 31 * result + Objects.hashCode(this.getStyleClass());
        return result;
    }
    
    @Override
    public void addEventHandler(final EventType<Event> eventType, final EventHandler<Event> eventHandler) {
        this.eventHandlerManager.addEventHandler((EventType)eventType, (EventHandler)eventHandler);
    }
    
    @Override
    public void removeEventHandler(final EventType<Event> eventType, final EventHandler<Event> eventHandler) {
        this.eventHandlerManager.removeEventHandler((EventType)eventType, (EventHandler)eventHandler);
    }
    
    private void updateText() {
        if (this.getItem() == null) {
            this.text.setValue("");
        }
        else if (!"".equals(this.getFormat())) {
            this.text.setValue(this.type.toString(this.getItem(), this.getFormat()));
        }
        else {
            this.text.setValue(this.type.toString(this.getItem()));
        }
    }
    
    private int getCornerBitNumber(final CornerPosition position) {
        switch (position) {
            case TOP_LEFT: {
                return 0;
            }
            case TOP_RIGHT: {
                return 1;
            }
            case BOTTOM_RIGHT: {
                return 2;
            }
            default: {
                return 3;
            }
        }
    }
    
    private boolean setMask(final boolean flag, final int position) {
        final int oldCorner = this.propertyContainer;
        if (flag) {
            this.propertyContainer |= 1 << position;
        }
        else {
            this.propertyContainer &= ~(1 << position);
        }
        return this.propertyContainer != oldCorner;
    }
    
    private boolean isSet(final int position) {
        return (this.propertyContainer & 1 << position) != 0x0;
    }
}
