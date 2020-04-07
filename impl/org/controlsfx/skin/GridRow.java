// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.control.Skin;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import org.controlsfx.control.GridView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.IndexedCell;

class GridRow<T> extends IndexedCell<T>
{
    private final SimpleObjectProperty<GridView<T>> gridView;
    
    public GridRow() {
        this.gridView = (SimpleObjectProperty<GridView<T>>)new SimpleObjectProperty((Object)this, "gridView");
        this.getStyleClass().add((Object)"grid-row");
        this.indexProperty().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                GridRow.access$000(GridRow.this, null, GridRow.this.getIndex() == -1);
            }
        });
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new GridRowSkin((GridRow<Object>)this);
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
    
    static /* synthetic */ void access$000(final GridRow x0, final Object x1, final boolean x2) {
        x0.updateItem(x1, x2);
    }
}
