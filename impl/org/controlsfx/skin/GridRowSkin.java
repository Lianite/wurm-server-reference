// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import java.util.Iterator;
import javafx.scene.Node;
import org.controlsfx.control.GridView;
import org.controlsfx.control.GridCell;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Cell;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.CellSkinBase;

public class GridRowSkin<T> extends CellSkinBase<GridRow<T>, BehaviorBase<GridRow<T>>>
{
    public GridRowSkin(final GridRow<T> control) {
        super((Cell)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.getChildren().clear();
        this.updateCells();
        this.registerChangeListener((ObservableValue)((GridRow)this.getSkinnable()).indexProperty(), "INDEX");
        this.registerChangeListener((ObservableValue)((GridRow)this.getSkinnable()).widthProperty(), "WIDTH");
        this.registerChangeListener((ObservableValue)((GridRow)this.getSkinnable()).heightProperty(), "HEIGHT");
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if ("INDEX".equals(p)) {
            this.updateCells();
        }
        else if ("WIDTH".equals(p)) {
            this.updateCells();
        }
        else if ("HEIGHT".equals(p)) {
            this.updateCells();
        }
    }
    
    public GridCell<T> getCellAtIndex(final int index) {
        if (index < this.getChildren().size()) {
            return (GridCell<T>)this.getChildren().get(index);
        }
        return null;
    }
    
    public void updateCells() {
        final int rowIndex = ((GridRow)this.getSkinnable()).getIndex();
        if (rowIndex >= 0) {
            final GridView<T> gridView = ((GridRow)this.getSkinnable()).getGridView();
            final int maxCellsInRow = ((GridViewSkin)gridView.getSkin()).computeMaxCellsInRow();
            final int totalCellsInGrid = gridView.getItems().size();
            final int startCellIndex = rowIndex * maxCellsInRow;
            final int endCellIndex = startCellIndex + maxCellsInRow - 1;
            int cacheIndex = 0;
            for (int cellIndex = startCellIndex; cellIndex <= endCellIndex && cellIndex < totalCellsInGrid; ++cellIndex, ++cacheIndex) {
                GridCell<T> cell = this.getCellAtIndex(cacheIndex);
                if (cell == null) {
                    cell = this.createCell();
                    this.getChildren().add((Object)cell);
                }
                cell.updateIndex(-1);
                cell.updateIndex(cellIndex);
            }
            this.getChildren().remove(cacheIndex, this.getChildren().size());
        }
    }
    
    private GridCell<T> createCell() {
        final GridView<T> gridView = (GridView<T>)((GridRow)this.getSkinnable()).gridViewProperty().get();
        GridCell<T> cell;
        if (gridView.getCellFactory() != null) {
            cell = (GridCell<T>)gridView.getCellFactory().call((Object)gridView);
        }
        else {
            cell = this.createDefaultCellImpl();
        }
        cell.updateGridView(gridView);
        return cell;
    }
    
    private GridCell<T> createDefaultCellImpl() {
        return new GridCell<T>() {
            protected void updateItem(final T item, final boolean empty) {
                super.updateItem((Object)item, empty);
                if (empty) {
                    this.setText("");
                }
                else {
                    this.setText(item.toString());
                }
            }
        };
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return Double.MAX_VALUE;
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        final GridView<T> gv = (GridView<T>)((GridRow)this.getSkinnable()).gridViewProperty().get();
        return gv.getCellHeight() + gv.getVerticalCellSpacing() * 2.0;
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double cellWidth = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getCellWidth();
        final double cellHeight = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getCellHeight();
        final double horizontalCellSpacing = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getHorizontalCellSpacing();
        final double verticalCellSpacing = ((GridView)((GridRow)this.getSkinnable()).gridViewProperty().get()).getVerticalCellSpacing();
        double xPos = 0.0;
        final double yPos = 0.0;
        for (final Node child : this.getChildren()) {
            child.relocate(xPos + horizontalCellSpacing, yPos + verticalCellSpacing);
            child.resize(cellWidth, cellHeight);
            xPos = xPos + horizontalCellSpacing + cellWidth + horizontalCellSpacing;
        }
    }
}
