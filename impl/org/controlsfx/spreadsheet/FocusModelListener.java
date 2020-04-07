// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.control.TableColumn;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.scene.control.TableView;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePosition;
import javafx.beans.value.ChangeListener;

public class FocusModelListener implements ChangeListener<TablePosition<ObservableList<SpreadsheetCell>, ?>>
{
    private final TableView.TableViewFocusModel<ObservableList<SpreadsheetCell>> tfm;
    private final SpreadsheetGridView cellsView;
    private final SpreadsheetView spreadsheetView;
    
    public FocusModelListener(final SpreadsheetView spreadsheetView, final SpreadsheetGridView cellsView) {
        this.tfm = (TableView.TableViewFocusModel<ObservableList<SpreadsheetCell>>)cellsView.getFocusModel();
        this.spreadsheetView = spreadsheetView;
        this.cellsView = cellsView;
    }
    
    public void changed(final ObservableValue<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>> ov, final TablePosition<ObservableList<SpreadsheetCell>, ?> oldPosition, final TablePosition<ObservableList<SpreadsheetCell>, ?> newPosition) {
        int columnIndex = -1;
        if (newPosition != null && newPosition.getTableColumn() != null) {
            columnIndex = this.cellsView.getColumns().indexOf((Object)newPosition.getTableColumn());
        }
        final SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(newPosition.getRow(), columnIndex);
        switch (spanType) {
            case ROW_SPAN_INVISIBLE: {
                if (!this.spreadsheetView.isPressed() && oldPosition.getColumn() == newPosition.getColumn() && oldPosition.getRow() == newPosition.getRow() - 1) {
                    Platform.runLater(() -> this.tfm.focus(getNextRowNumber(oldPosition, this.cellsView, this.spreadsheetView), oldPosition.getTableColumn()));
                    break;
                }
                Platform.runLater(() -> this.tfm.focus(newPosition.getRow() - 1, newPosition.getTableColumn()));
                break;
            }
            case BOTH_INVISIBLE: {
                Platform.runLater(() -> this.tfm.focus(newPosition.getRow() - 1, (TableColumn)this.cellsView.getColumns().get(newPosition.getColumn() - 1)));
                break;
            }
            case COLUMN_SPAN_INVISIBLE: {
                if (!this.spreadsheetView.isPressed() && oldPosition.getColumn() == newPosition.getColumn() - 1 && oldPosition.getRow() == newPosition.getRow()) {
                    Platform.runLater(() -> this.tfm.focus(oldPosition.getRow(), (TableColumn)getTableColumnSpan(oldPosition, this.cellsView, this.spreadsheetView)));
                    break;
                }
                Platform.runLater(() -> this.tfm.focus(newPosition.getRow(), this.cellsView.getVisibleLeafColumn(newPosition.getColumn() - 1)));
                break;
            }
        }
    }
    
    static TableColumn<ObservableList<SpreadsheetCell>, ?> getTableColumnSpan(final TablePosition<?, ?> pos, final SpreadsheetGridView cellsView, final SpreadsheetView spv) {
        return (TableColumn<ObservableList<SpreadsheetCell>, ?>)cellsView.getVisibleLeafColumn(pos.getColumn() + spv.getColumnSpan((SpreadsheetCell)((ObservableList)cellsView.getItems().get(pos.getRow())).get(cellsView.getColumns().indexOf((Object)pos.getTableColumn()))));
    }
    
    public static int getNextRowNumber(final TablePosition<?, ?> pos, final TableView<ObservableList<SpreadsheetCell>> cellsView, final SpreadsheetView spv) {
        return spv.getRowSpan((SpreadsheetCell)((ObservableList)cellsView.getItems().get(pos.getRow())).get(cellsView.getColumns().indexOf((Object)pos.getTableColumn())), pos.getRow()) + pos.getRow();
    }
}
