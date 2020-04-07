// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.control.Skin;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.collections.MapChangeListener;
import javafx.event.EventTarget;
import javafx.event.Event;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.WeakEventHandler;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;

public class GridRow extends TableRow<ObservableList<SpreadsheetCell>>
{
    private final SpreadsheetHandle handle;
    DoubleProperty verticalShift;
    private final InvalidationListener setPrefHeightListener;
    private final WeakInvalidationListener weakPrefHeightListener;
    private final EventHandler<MouseEvent> dragDetectedEventHandler;
    private final WeakEventHandler<MouseEvent> weakDragHandler;
    private final InvalidationListener gridListener;
    private final WeakInvalidationListener weakGridListener;
    private final InvalidationListener comparatorListener;
    private final WeakInvalidationListener weakComparatorListener;
    
    public GridRow(final SpreadsheetHandle handle) {
        this.verticalShift = (DoubleProperty)new SimpleDoubleProperty();
        this.setPrefHeightListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable o) {
                GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
            }
        };
        this.weakPrefHeightListener = new WeakInvalidationListener(this.setPrefHeightListener);
        this.dragDetectedEventHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent event) {
                if (event.getTarget().getClass().equals(GridRow.class) && event.getPickResult().getIntersectedNode() != null && event.getPickResult().getIntersectedNode().getClass().equals(CellView.class)) {
                    Event.fireEvent((EventTarget)event.getPickResult().getIntersectedNode(), (Event)event);
                }
            }
        };
        this.weakDragHandler = (WeakEventHandler<MouseEvent>)new WeakEventHandler((EventHandler)this.dragDetectedEventHandler);
        this.gridListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable o) {
                GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
                GridRow.this.handle.getView().comparatorProperty().addListener((InvalidationListener)GridRow.this.weakComparatorListener);
            }
        };
        this.weakGridListener = new WeakInvalidationListener(this.gridListener);
        this.comparatorListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable o) {
                GridRow.this.updateIndex(GridRow.this.getIndex());
                GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
            }
        };
        this.weakComparatorListener = new WeakInvalidationListener(this.comparatorListener);
        this.handle = handle;
        this.indexProperty().addListener((InvalidationListener)this.weakPrefHeightListener);
        this.visibleProperty().addListener((InvalidationListener)this.weakPrefHeightListener);
        handle.getView().gridProperty().addListener((InvalidationListener)this.weakGridListener);
        handle.getView().hiddenRowsProperty().addListener((InvalidationListener)this.weakPrefHeightListener);
        handle.getView().hiddenColumnsProperty().addListener((InvalidationListener)this.weakPrefHeightListener);
        handle.getView().comparatorProperty().addListener((InvalidationListener)this.weakComparatorListener);
        handle.getCellsViewSkin().rowHeightMap.addListener((MapChangeListener)new MapChangeListener<Integer, Double>() {
            public void onChanged(final MapChangeListener.Change<? extends Integer, ? extends Double> change) {
                if (change.wasAdded() && (int)change.getKey() == handle.getView().getModelRow(GridRow.this.getIndex())) {
                    GridRow.this.setRowHeight((double)change.getValueAdded());
                }
                else if (change.wasRemoved() && (int)change.getKey() == handle.getView().getModelRow(GridRow.this.getIndex())) {
                    GridRow.this.setRowHeight(GridRow.this.computePrefHeight(-1.0));
                }
            }
        });
        this.addEventHandler(MouseEvent.DRAG_DETECTED, (EventHandler)this.weakDragHandler);
    }
    
    void addCell(final CellView cell) {
        this.getChildren().add((Object)cell);
    }
    
    void removeCell(final CellView gc) {
        this.getChildren().remove((Object)gc);
    }
    
    SpreadsheetView getSpreadsheetView() {
        return this.handle.getView();
    }
    
    protected double computePrefHeight(final double width) {
        return this.handle.getCellsViewSkin().getRowHeight(this.getIndex());
    }
    
    protected double computeMinHeight(final double width) {
        return this.handle.getCellsViewSkin().getRowHeight(this.getIndex());
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new GridRowSkin(this.handle, this);
    }
    
    public void setRowHeight(final double height) {
        CellView.getValue(() -> this.setHeight(height));
        this.setPrefHeight(height);
        this.handle.getCellsViewSkin().rectangleSelection.updateRectangle();
    }
}
