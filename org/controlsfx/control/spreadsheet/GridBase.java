// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.beans.Observable;
import javafx.event.EventDispatcher;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventType;
import impl.org.controlsfx.spreadsheet.RectangleSelection;
import java.util.Collection;
import javafx.event.Event;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.TreeSet;
import java.util.BitSet;
import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.property.BooleanProperty;
import javafx.util.Callback;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;

public class GridBase implements Grid, EventTarget
{
    private ObservableList<ObservableList<SpreadsheetCell>> rows;
    private int rowCount;
    private int columnCount;
    private Callback<Integer, Double> rowHeightFactory;
    private final BooleanProperty locked;
    private final EventHandlerManager eventHandlerManager;
    private final ObservableList<String> rowsHeader;
    private final ObservableList<String> columnsHeader;
    private BitSet resizableRow;
    private final TreeSet<Long> displaySelectionCells;
    private final TreeSet<Long> noDisplaySelectionCells;
    private final BooleanProperty displaySelection;
    
    public GridBase(final int rowCount, final int columnCount) {
        this.eventHandlerManager = new EventHandlerManager((Object)this);
        this.displaySelectionCells = new TreeSet<Long>();
        this.noDisplaySelectionCells = new TreeSet<Long>();
        this.displaySelection = (BooleanProperty)new SimpleBooleanProperty(true);
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.rowsHeader = (ObservableList<String>)FXCollections.observableArrayList();
        this.columnsHeader = (ObservableList<String>)FXCollections.observableArrayList();
        this.locked = (BooleanProperty)new SimpleBooleanProperty(false);
        this.rowHeightFactory = (Callback<Integer, Double>)new MapBasedRowHeightFactory(new HashMap<Integer, Double>());
        (this.rows = (ObservableList<ObservableList<SpreadsheetCell>>)FXCollections.observableArrayList()).addListener(observable -> this.setRowCount(this.rows.size()));
        (this.resizableRow = new BitSet(rowCount)).set(0, rowCount, true);
    }
    
    @Override
    public ObservableList<ObservableList<SpreadsheetCell>> getRows() {
        return this.rows;
    }
    
    @Override
    public void setCellValue(final int modelRow, final int column, final Object value) {
        if (modelRow < this.getRowCount() && column < this.columnCount && !this.isLocked()) {
            final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getRows().get(modelRow)).get(column);
            final Object previousItem = cell.getItem();
            final Object convertedValue = cell.getCellType().convertValue(value);
            cell.setItem(convertedValue);
            if (!Objects.equals(previousItem, cell.getItem())) {
                final GridChange cellChange = new GridChange(cell.getRow(), cell.getColumn(), previousItem, convertedValue);
                Event.fireEvent((EventTarget)this, (Event)cellChange);
            }
        }
    }
    
    @Override
    public int getRowCount() {
        return this.rowCount;
    }
    
    @Override
    public int getColumnCount() {
        return this.columnCount;
    }
    
    @Override
    public double getRowHeight(final int row) {
        return (double)this.rowHeightFactory.call((Object)row);
    }
    
    public void setRowHeightCallback(final Callback<Integer, Double> rowHeight) {
        this.rowHeightFactory = rowHeight;
    }
    
    @Override
    public ObservableList<String> getRowHeaders() {
        return this.rowsHeader;
    }
    
    @Override
    public ObservableList<String> getColumnHeaders() {
        return this.columnsHeader;
    }
    
    public BooleanProperty lockedProperty() {
        return this.locked;
    }
    
    public boolean isLocked() {
        return this.locked.get();
    }
    
    public void setLocked(final Boolean lock) {
        this.locked.setValue(lock);
    }
    
    @Override
    public void spanRow(final int count, final int rowIndex, final int colIndex) {
        if (count <= 0 || count > this.getRowCount() || rowIndex >= this.getRowCount() || colIndex >= this.columnCount) {
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.rows.get(rowIndex)).get(colIndex);
        final int colSpan = cell.getColumnSpan();
        cell.setRowSpan(count);
        for (int row = rowIndex; row < rowIndex + count && row < this.getRowCount(); ++row) {
            for (int col = colIndex; col < colIndex + colSpan && col < this.columnCount; ++col) {
                if (row != rowIndex || col != colIndex) {
                    ((ObservableList)this.rows.get(row)).set(col, (Object)cell);
                }
            }
        }
    }
    
    @Override
    public void spanColumn(final int count, final int rowIndex, final int colIndex) {
        if (count <= 0 || count > this.columnCount || rowIndex >= this.getRowCount() || colIndex >= this.columnCount) {
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.rows.get(rowIndex)).get(colIndex);
        final int rowSpan = cell.getRowSpan();
        cell.setColumnSpan(count);
        for (int row = rowIndex; row < rowIndex + rowSpan && row < this.getRowCount(); ++row) {
            for (int col = colIndex; col < colIndex + count && col < this.columnCount; ++col) {
                if (row != rowIndex || col != colIndex) {
                    ((ObservableList)this.rows.get(row)).set(col, (Object)cell);
                }
            }
        }
    }
    
    @Override
    public void setRows(final Collection<ObservableList<SpreadsheetCell>> rows) {
        this.rows.clear();
        this.rows.addAll((Collection)rows);
        this.setRowCount(rows.size());
        this.setColumnCount((this.rowCount == 0) ? 0 : ((ObservableList)this.rows.get(0)).size());
    }
    
    public void setResizableRows(final BitSet resizableRow) {
        this.resizableRow = resizableRow;
    }
    
    @Override
    public boolean isRowResizable(final int row) {
        return this.resizableRow.get(row);
    }
    
    @Override
    public boolean isDisplaySelection() {
        return this.displaySelection.get();
    }
    
    @Override
    public void setDisplaySelection(final boolean value) {
        this.displaySelection.setValue(value);
    }
    
    @Override
    public BooleanProperty displaySelectionProperty() {
        return this.displaySelection;
    }
    
    @Override
    public void setCellDisplaySelection(final int row, final int column, final boolean displaySelection) {
        final Long key = RectangleSelection.SelectionRange.key(row, column);
        if (displaySelection) {
            this.displaySelectionCells.add(key);
            this.noDisplaySelectionCells.remove(key);
        }
        else {
            this.displaySelectionCells.remove(key);
            this.noDisplaySelectionCells.add(key);
        }
    }
    
    @Override
    public boolean isCellDisplaySelection(final int row, final int column) {
        final Long key = RectangleSelection.SelectionRange.key(row, column);
        return this.displaySelectionCells.contains(key) || (!this.noDisplaySelectionCells.contains(key) && this.isDisplaySelection());
    }
    
    @Override
    public <E extends GridChange> void addEventHandler(final EventType<E> eventType, final EventHandler<E> eventHandler) {
        this.eventHandlerManager.addEventHandler((EventType)eventType, (EventHandler)eventHandler);
    }
    
    @Override
    public <E extends GridChange> void removeEventHandler(final EventType<E> eventType, final EventHandler<E> eventHandler) {
        this.eventHandlerManager.removeEventHandler((EventType)eventType, (EventHandler)eventHandler);
    }
    
    public EventDispatchChain buildEventDispatchChain(final EventDispatchChain tail) {
        return tail.append((EventDispatcher)this.eventHandlerManager);
    }
    
    private void setRowCount(final int rowCount) {
        this.rowCount = rowCount;
    }
    
    private void setColumnCount(final int columnCount) {
        this.columnCount = columnCount;
    }
    
    public static class MapBasedRowHeightFactory implements Callback<Integer, Double>
    {
        private final Map<Integer, Double> rowHeightMap;
        
        public MapBasedRowHeightFactory(final Map<Integer, Double> rowHeightMap) {
            this.rowHeightMap = rowHeightMap;
        }
        
        public Double call(final Integer index) {
            final Double value = this.rowHeightMap.get(index);
            return (value == null) ? -1.0 : value;
        }
    }
}
