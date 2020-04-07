// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.control.IndexedCell;
import javafx.collections.ObservableList;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.util.Callback;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.GridView;
import com.sun.javafx.scene.control.skin.VirtualContainerBase;

public class GridViewSkin<T> extends VirtualContainerBase<GridView<T>, BehaviorBase<GridView<T>>, GridRow<T>>
{
    private final ListChangeListener<T> gridViewItemsListener;
    private final WeakListChangeListener<T> weakGridViewItemsListener;
    
    public GridViewSkin(final GridView<T> control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.gridViewItemsListener = (ListChangeListener<T>)new ListChangeListener<T>() {
            public void onChanged(final ListChangeListener.Change<? extends T> change) {
                GridViewSkin.this.updateRowCount();
                ((GridView)GridViewSkin.this.getSkinnable()).requestLayout();
            }
        };
        this.weakGridViewItemsListener = (WeakListChangeListener<T>)new WeakListChangeListener((ListChangeListener)this.gridViewItemsListener);
        this.updateGridViewItems();
        this.flow.setId("virtual-flow");
        this.flow.setPannable(false);
        this.flow.setVertical(true);
        this.flow.setFocusTraversable(((GridView)this.getSkinnable()).isFocusTraversable());
        this.flow.setCreateCell((Callback)new Callback<VirtualFlow, GridRow<T>>() {
            public GridRow<T> call(final VirtualFlow flow) {
                return GridViewSkin.this.createCell();
            }
        });
        this.getChildren().add((Object)this.flow);
        this.updateRowCount();
        this.registerChangeListener((ObservableValue)control.itemsProperty(), "ITEMS");
        this.registerChangeListener((ObservableValue)control.cellFactoryProperty(), "CELL_FACTORY");
        this.registerChangeListener((ObservableValue)control.parentProperty(), "PARENT");
        this.registerChangeListener((ObservableValue)control.cellHeightProperty(), "CELL_HEIGHT");
        this.registerChangeListener((ObservableValue)control.cellWidthProperty(), "CELL_WIDTH");
        this.registerChangeListener((ObservableValue)control.horizontalCellSpacingProperty(), "HORIZONZAL_CELL_SPACING");
        this.registerChangeListener((ObservableValue)control.verticalCellSpacingProperty(), "VERTICAL_CELL_SPACING");
        this.registerChangeListener((ObservableValue)control.widthProperty(), "WIDTH_PROPERTY");
        this.registerChangeListener((ObservableValue)control.heightProperty(), "HEIGHT_PROPERTY");
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if (p == "ITEMS") {
            this.updateGridViewItems();
        }
        else if (p == "CELL_FACTORY") {
            this.flow.recreateCells();
        }
        else if (p == "CELL_HEIGHT") {
            this.flow.recreateCells();
        }
        else if (p == "CELL_WIDTH") {
            this.updateRowCount();
            this.flow.recreateCells();
        }
        else if (p == "HORIZONZAL_CELL_SPACING") {
            this.updateRowCount();
            this.flow.recreateCells();
        }
        else if (p == "VERTICAL_CELL_SPACING") {
            this.flow.recreateCells();
        }
        else if (p == "PARENT") {
            if (((GridView)this.getSkinnable()).getParent() != null && ((GridView)this.getSkinnable()).isVisible()) {
                ((GridView)this.getSkinnable()).requestLayout();
            }
        }
        else if (p == "WIDTH_PROPERTY" || p == "HEIGHT_PROPERTY") {
            this.updateRowCount();
        }
    }
    
    public void updateGridViewItems() {
        if (((GridView)this.getSkinnable()).getItems() != null) {
            ((GridView)this.getSkinnable()).getItems().removeListener((ListChangeListener)this.weakGridViewItemsListener);
        }
        if (((GridView)this.getSkinnable()).getItems() != null) {
            ((GridView)this.getSkinnable()).getItems().addListener((ListChangeListener)this.weakGridViewItemsListener);
        }
        this.updateRowCount();
        this.flow.recreateCells();
        ((GridView)this.getSkinnable()).requestLayout();
    }
    
    protected void updateRowCount() {
        if (this.flow == null) {
            return;
        }
        final int oldCount = this.flow.getCellCount();
        final int newCount = this.getItemCount();
        if (newCount != oldCount) {
            this.flow.setCellCount(newCount);
            this.flow.rebuildCells();
        }
        else {
            this.flow.reconfigureCells();
        }
        this.updateRows(newCount);
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double x2 = ((GridView)this.getSkinnable()).getInsets().getLeft();
        final double y2 = ((GridView)this.getSkinnable()).getInsets().getTop();
        final double w2 = ((GridView)this.getSkinnable()).getWidth() - (((GridView)this.getSkinnable()).getInsets().getLeft() + ((GridView)this.getSkinnable()).getInsets().getRight());
        final double h2 = ((GridView)this.getSkinnable()).getHeight() - (((GridView)this.getSkinnable()).getInsets().getTop() + ((GridView)this.getSkinnable()).getInsets().getBottom());
        this.flow.resizeRelocate(x2, y2, w2, h2);
    }
    
    public GridRow<T> createCell() {
        final GridRow<T> row = new GridRow<T>();
        row.updateGridView((GridView<T>)this.getSkinnable());
        return row;
    }
    
    public int getItemCount() {
        final ObservableList<?> items = ((GridView)this.getSkinnable()).getItems();
        return (items == null) ? 0 : ((int)Math.ceil(items.size() / this.computeMaxCellsInRow()));
    }
    
    public int computeMaxCellsInRow() {
        return Math.max((int)Math.floor(this.computeRowWidth() / this.computeCellWidth()), 1);
    }
    
    protected double computeRowWidth() {
        return ((GridView)this.getSkinnable()).getWidth() - 18.0;
    }
    
    protected double computeCellWidth() {
        return ((GridView)this.getSkinnable()).cellWidthProperty().doubleValue() + ((GridView)this.getSkinnable()).horizontalCellSpacingProperty().doubleValue() * 2.0;
    }
    
    protected void updateRows(final int rowCount) {
        for (int i = 0; i < rowCount; ++i) {
            final GridRow<T> row = (GridRow<T>)this.flow.getVisibleCell(i);
            if (row != null) {
                row.updateIndex(-1);
                row.updateIndex(i);
            }
        }
    }
    
    protected boolean areRowsVisible() {
        return this.flow != null && this.flow.getFirstVisibleCell() != null && this.flow.getLastVisibleCell() != null;
    }
    
    protected double computeMinHeight(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return 0.0;
    }
}
