// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import java.util.Iterator;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumnBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.EventTarget;
import javafx.event.Event;
import impl.org.controlsfx.spreadsheet.CellViewSkin;
import java.util.List;
import org.controlsfx.tools.Utils;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import impl.org.controlsfx.spreadsheet.CellView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.MenuItem;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public final class SpreadsheetColumn
{
    private final SpreadsheetView spreadsheetView;
    final TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> column;
    private final boolean canFix;
    private final Integer indexColumn;
    private MenuItem fixItem;
    private final ObjectProperty<Filter> filterProperty;
    
    SpreadsheetColumn(final TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> column, final SpreadsheetView spreadsheetView, final Integer indexColumn, final Grid grid) {
        this.filterProperty = (ObjectProperty<Filter>)new SimpleObjectProperty();
        this.spreadsheetView = spreadsheetView;
        (this.column = column).setMinWidth(0.0);
        this.indexColumn = indexColumn;
        this.canFix = this.initCanFix(grid);
        CellView.getValue(() -> column.setContextMenu(this.getColumnContextMenu()));
        spreadsheetView.fixingColumnsAllowedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
                CellView.getValue(() -> column.setContextMenu(SpreadsheetColumn.this.getColumnContextMenu()));
            }
        });
        grid.getColumnHeaders().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable arg0) {
                final List<String> columnsHeader = (List<String>)spreadsheetView.getGrid().getColumnHeaders();
                if (columnsHeader.size() <= indexColumn) {
                    SpreadsheetColumn.this.setText(Utils.getExcelLetterFromNumber(indexColumn));
                }
                else if (!columnsHeader.get(indexColumn).equals(SpreadsheetColumn.this.getText())) {
                    SpreadsheetColumn.this.setText(columnsHeader.get(indexColumn));
                }
            }
        });
        grid.getRows().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable arg0) {
                SpreadsheetColumn.this.initCanFix(grid);
            }
        });
        this.filterProperty.addListener((ChangeListener)new ChangeListener<Filter>() {
            public void changed(final ObservableValue<? extends Filter> observable, final Filter oldFilter, final Filter newFilter) {
                if (newFilter != null) {
                    if (spreadsheetView.getFilteredRow() == -1) {
                        SpreadsheetColumn.this.setFilter(null);
                        return;
                    }
                    final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)spreadsheetView.getGrid().getRows().get(spreadsheetView.getFilteredRow())).get((int)indexColumn);
                    if (cell.getColumnSpan() > 1) {
                        SpreadsheetColumn.this.setFilter(null);
                        return;
                    }
                }
                Event.fireEvent((EventTarget)column, new Event(CellViewSkin.FILTER_EVENT_TYPE));
            }
        });
    }
    
    public boolean isFixed() {
        return this.spreadsheetView.getFixedColumns().contains((Object)this);
    }
    
    public void setFixed(final boolean fixed) {
        if (fixed) {
            this.spreadsheetView.getFixedColumns().add((Object)this);
        }
        else {
            this.spreadsheetView.getFixedColumns().removeAll((Object[])new SpreadsheetColumn[] { this });
        }
    }
    
    public void setPrefWidth(double width) {
        width = Math.ceil(width);
        if (this.column.getPrefWidth() == width && this.column.getWidth() != width) {
            this.column.impl_setWidth(width);
        }
        else {
            this.column.setPrefWidth(width);
        }
        this.spreadsheetView.columnWidthSet(this.indexColumn);
    }
    
    public double getWidth() {
        return this.column.getWidth();
    }
    
    public final ReadOnlyDoubleProperty widthProperty() {
        return this.column.widthProperty();
    }
    
    public final void setMinWidth(final double value) {
        this.column.setMinWidth(value);
    }
    
    public final double getMinWidth() {
        return this.column.getMinWidth();
    }
    
    public final DoubleProperty minWidthProperty() {
        return this.column.minWidthProperty();
    }
    
    public final DoubleProperty maxWidthProperty() {
        return this.column.maxWidthProperty();
    }
    
    public final void setMaxWidth(final double value) {
        this.column.setMaxWidth(value);
    }
    
    public final double getMaxWidth() {
        return this.column.getMaxWidth();
    }
    
    public void setResizable(final boolean b) {
        this.column.setResizable(b);
    }
    
    public void fitColumn() {
        if (this.column.isResizable() && this.spreadsheetView.getCellsViewSkin() != null) {
            this.spreadsheetView.getCellsViewSkin().resize((TableColumnBase<?, ?>)this.column, 100);
        }
    }
    
    public boolean isColumnFixable() {
        return this.canFix && this.spreadsheetView.isFixingColumnsAllowed();
    }
    
    public void setFilter(final Filter filter) {
        this.filterProperty.setValue((Object)filter);
    }
    
    public Filter getFilter() {
        return (Filter)this.filterProperty.get();
    }
    
    public ObjectProperty filterProperty() {
        return this.filterProperty;
    }
    
    private void setText(final String text) {
        this.column.setText(text);
    }
    
    private String getText() {
        return this.column.getText();
    }
    
    private ContextMenu getColumnContextMenu() {
        if (this.isColumnFixable()) {
            final ContextMenu contextMenu = new ContextMenu();
            this.fixItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.column.menu.fix")));
            contextMenu.setOnShowing((EventHandler)new EventHandler<WindowEvent>() {
                public void handle(final WindowEvent event) {
                    if (!SpreadsheetColumn.this.isFixed()) {
                        SpreadsheetColumn.this.fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.column.menu.fix")));
                    }
                    else {
                        SpreadsheetColumn.this.fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.column.menu.unfix")));
                    }
                }
            });
            this.fixItem.setGraphic((Node)new ImageView(new Image(this.getClass().getResourceAsStream("pinSpreadsheetView.png"))));
            this.fixItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent arg0) {
                    if (!SpreadsheetColumn.this.isFixed()) {
                        SpreadsheetColumn.this.setFixed(true);
                    }
                    else {
                        SpreadsheetColumn.this.setFixed(false);
                    }
                }
            });
            contextMenu.getItems().addAll((Object[])new MenuItem[] { this.fixItem });
            return contextMenu;
        }
        return new ContextMenu();
    }
    
    private boolean initCanFix(final Grid grid) {
        for (final ObservableList<SpreadsheetCell> row : grid.getRows()) {
            final int columnSpan = ((SpreadsheetCell)row.get((int)this.indexColumn)).getColumnSpan();
            if (columnSpan > 1) {
                return false;
            }
        }
        return true;
    }
}
