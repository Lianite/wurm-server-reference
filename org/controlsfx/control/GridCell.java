// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.GridCellSkin;
import javafx.scene.control.Skin;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexedCell;

public class GridCell<T> extends IndexedCell<T>
{
    private final SimpleObjectProperty<GridView<T>> gridView;
    
    public GridCell() {
        this.gridView = (SimpleObjectProperty<GridView<T>>)new SimpleObjectProperty((Object)this, "gridView");
        this.getStyleClass().add((Object)"grid-cell");
        this.indexProperty().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                final GridView<T> gridView = GridCell.this.getGridView();
                if (gridView == null) {
                    return;
                }
                if (GridCell.this.getIndex() < 0) {
                    GridCell.access$000(GridCell.this, null, true);
                    return;
                }
                final T item = (T)gridView.getItems().get(GridCell.this.getIndex());
                GridCell.access$100(GridCell.this, item, item == null);
            }
        });
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new GridCellSkin((GridCell<Object>)this);
    }
    
    public SimpleObjectProperty<GridView<T>> gridViewProperty() {
        return this.gridView;
    }
    
    public final void updateGridView(final GridView<T> gridView) {
        this.gridView.set((Object)gridView);
    }
    
    public GridView<T> getGridView() {
        return (GridView<T>)this.gridView.get();
    }
    
    static /* synthetic */ void access$000(final GridCell x0, final Object x1, final boolean x2) {
        x0.updateItem(x1, x2);
    }
    
    static /* synthetic */ void access$100(final GridCell x0, final Object x1, final boolean x2) {
        x0.updateItem(x1, x2);
    }
}
