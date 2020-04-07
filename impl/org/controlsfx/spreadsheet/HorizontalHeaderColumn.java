// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.beans.Observable;
import javafx.scene.control.TableColumn;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import javafx.scene.Node;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.TableColumnBase;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;

public class HorizontalHeaderColumn extends NestedTableColumnHeader
{
    int lastColumnResized;
    
    public HorizontalHeaderColumn(final TableViewSkinBase<?, ?, ?, ?, ?, ?> skin, final TableColumnBase<?, ?> tc) {
        super((TableViewSkinBase)skin, (TableColumnBase)tc);
        this.lastColumnResized = -1;
        this.widthProperty().addListener(observable -> {
            ((GridViewSkin)skin).hBarValue.clear();
            ((GridViewSkin)skin).rectangleSelection.updateRectangle();
        });
        this.columnReorderLine.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            final HorizontalHeader headerRow = (HorizontalHeader)((GridViewSkin)skin).getTableHeaderRow();
            final GridViewSkin mySkin = (GridViewSkin)skin;
            if (newValue.intValue() == 0 && this.lastColumnResized >= 0 && headerRow.selectedColumns.get(this.lastColumnResized)) {
                final double width1 = ((TableColumn)mySkin.getColumns().get(this.lastColumnResized)).getWidth();
                for (int i = headerRow.selectedColumns.nextSetBit(0); i >= 0; i = headerRow.selectedColumns.nextSetBit(i + 1)) {
                    ((TableColumn)mySkin.getColumns().get(i)).setPrefWidth(width1);
                }
            }
        });
    }
    
    protected TableColumnHeader createTableColumnHeader(final TableColumnBase col) {
        final TableViewSkinBase<?, ?, ?, ?, ?, TableColumnBase<?, ?>> tableViewSkin = (TableViewSkinBase<?, ?, ?, ?, ?, TableColumnBase<?, ?>>)this.getTableViewSkin();
        if (col.getColumns().isEmpty()) {
            final TableColumnHeader columnHeader = new TableColumnHeader((TableViewSkinBase)tableViewSkin, col);
            columnHeader.setOnMousePressed((EventHandler)new EventHandler<MouseEvent>() {
                public void handle(final MouseEvent mouseEvent) {
                    if (mouseEvent.getClickCount() == 2 && mouseEvent.isPrimaryButtonDown()) {
                        ((GridViewSkin)tableViewSkin).resize((TableColumnBase<?, ?>)col, -1);
                    }
                }
            });
            return columnHeader;
        }
        return (TableColumnHeader)new HorizontalHeaderColumn((TableViewSkinBase<?, ?, ?, ?, ?, ?>)this.getTableViewSkin(), (TableColumnBase<?, ?>)col);
    }
    
    protected void layoutChildren() {
        super.layoutChildren();
        this.layoutFixedColumns();
    }
    
    public void layoutFixedColumns() {
        final SpreadsheetHandle handle = ((GridViewSkin)this.getTableViewSkin()).handle;
        final SpreadsheetView spreadsheetView = handle.getView();
        if (handle.getCellsViewSkin() == null || this.getChildren().isEmpty()) {
            return;
        }
        final double hbarValue = handle.getCellsViewSkin().getHBar().getValue();
        final int labelHeight = (int)((Node)this.getChildren().get(0)).prefHeight(-1.0);
        double fixedColumnWidth = 0.0;
        double x = this.snappedLeftInset();
        int max = this.getColumnHeaders().size();
        max = ((max > handle.getGridView().getVisibleLeafColumns().size()) ? handle.getGridView().getVisibleLeafColumns().size() : max);
        max = ((max > spreadsheetView.getColumns().size()) ? spreadsheetView.getColumns().size() : max);
        for (int j = 0; j < max; ++j) {
            final TableColumnHeader n = (TableColumnHeader)this.getColumnHeaders().get(j);
            final double prefWidth = this.snapSize(n.prefWidth(-1.0));
            n.setPrefHeight(24.0);
            if (((SpreadsheetColumn)spreadsheetView.getColumns().get(spreadsheetView.getModelColumn(j))).isFixed()) {
                double tableCellX = 0.0;
                if (hbarValue + fixedColumnWidth > x) {
                    tableCellX = Math.abs(hbarValue - x + fixedColumnWidth);
                    n.toFront();
                    fixedColumnWidth += prefWidth;
                }
                n.relocate(x + tableCellX, labelHeight + this.snappedTopInset());
            }
            x += prefWidth;
        }
    }
}
