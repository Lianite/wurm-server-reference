// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.scene.control.CustomMenuItem;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.scene.control.ListView;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Set;
import java.util.BitSet;
import javafx.scene.control.MenuButton;

public class FilterBase implements Filter
{
    private final SpreadsheetView spv;
    private final int column;
    private MenuButton menuButton;
    private BitSet hiddenRows;
    private Set<String> stringSet;
    private Set<String> copySet;
    private final Comparator ascendingComp;
    private final Comparator descendingComp;
    
    public FilterBase(final SpreadsheetView spv, final int column) {
        this.stringSet = new HashSet<String>();
        this.copySet = new HashSet<String>();
        this.ascendingComp = new Comparator<ObservableList<SpreadsheetCell>>() {
            @Override
            public int compare(final ObservableList<SpreadsheetCell> o1, final ObservableList<SpreadsheetCell> o2) {
                final SpreadsheetCell cell1 = (SpreadsheetCell)o1.get(FilterBase.this.column);
                final SpreadsheetCell cell2 = (SpreadsheetCell)o2.get(FilterBase.this.column);
                if (cell1.getRow() <= FilterBase.this.spv.getFilteredRow()) {
                    return Integer.compare(cell1.getRow(), cell2.getRow());
                }
                if (cell2.getRow() <= FilterBase.this.spv.getFilteredRow()) {
                    return Integer.compare(cell1.getRow(), cell2.getRow());
                }
                if (cell1.getCellType() == SpreadsheetCellType.INTEGER && cell2.getCellType() == SpreadsheetCellType.INTEGER) {
                    return Integer.compare((int)cell1.getItem(), (int)cell2.getItem());
                }
                if (cell1.getCellType() == SpreadsheetCellType.DOUBLE && cell2.getCellType() == SpreadsheetCellType.DOUBLE) {
                    return Double.compare((double)cell1.getItem(), (double)cell2.getItem());
                }
                return cell1.getText().compareToIgnoreCase(cell2.getText());
            }
        };
        this.descendingComp = new Comparator<ObservableList<SpreadsheetCell>>() {
            @Override
            public int compare(final ObservableList<SpreadsheetCell> o1, final ObservableList<SpreadsheetCell> o2) {
                final SpreadsheetCell cell1 = (SpreadsheetCell)o1.get(FilterBase.this.column);
                final SpreadsheetCell cell2 = (SpreadsheetCell)o2.get(FilterBase.this.column);
                if (cell1.getRow() <= FilterBase.this.spv.getFilteredRow()) {
                    return Integer.compare(cell1.getRow(), cell2.getRow());
                }
                if (cell2.getRow() <= FilterBase.this.spv.getFilteredRow()) {
                    return Integer.compare(cell1.getRow(), cell2.getRow());
                }
                if (cell1.getCellType() == SpreadsheetCellType.INTEGER && cell2.getCellType() == SpreadsheetCellType.INTEGER) {
                    return Integer.compare((int)cell2.getItem(), (int)cell1.getItem());
                }
                if (cell1.getCellType() == SpreadsheetCellType.DOUBLE && cell2.getCellType() == SpreadsheetCellType.DOUBLE) {
                    return Double.compare((double)cell2.getItem(), (double)cell1.getItem());
                }
                return cell2.getText().compareToIgnoreCase(cell1.getText());
            }
        };
        this.spv = spv;
        this.column = column;
    }
    
    @Override
    public MenuButton getMenuButton() {
        if (this.menuButton == null) {
            this.menuButton = new MenuButton();
            this.menuButton.getStyleClass().add((Object)"filter-menu-button");
            this.menuButton.showingProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
                public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
                    if (newValue) {
                        FilterBase.this.addMenuItems();
                        FilterBase.this.hiddenRows = new BitSet(FilterBase.this.spv.getHiddenRows().size());
                        FilterBase.this.hiddenRows.or(FilterBase.this.spv.getHiddenRows());
                    }
                    else {
                        for (int i = FilterBase.this.spv.getFilteredRow() + 1; i < FilterBase.this.spv.getGrid().getRowCount(); ++i) {
                            FilterBase.this.hiddenRows.set(i, !FilterBase.this.copySet.contains(((SpreadsheetCell)((ObservableList)FilterBase.this.spv.getGrid().getRows().get(i)).get(FilterBase.this.column)).getText()));
                        }
                        FilterBase.this.spv.setHiddenRows(FilterBase.this.hiddenRows);
                    }
                }
            });
        }
        return this.menuButton;
    }
    
    private void addMenuItems() {
        if (this.menuButton.getItems().isEmpty()) {
            final MenuItem sortItem = new MenuItem("Sort ascending");
            sortItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent event) {
                    if (FilterBase.this.spv.getComparator() == FilterBase.this.ascendingComp) {
                        FilterBase.this.spv.setComparator(FilterBase.this.descendingComp);
                        sortItem.setText("Remove sort");
                    }
                    else if (FilterBase.this.spv.getComparator() == FilterBase.this.descendingComp) {
                        FilterBase.this.spv.setComparator(null);
                        sortItem.setText("Sort ascending");
                    }
                    else {
                        FilterBase.this.spv.setComparator(FilterBase.this.ascendingComp);
                        sortItem.setText("Sort descending");
                    }
                }
            });
            final ListView<String> listView = (ListView<String>)new ListView();
            listView.setCellFactory((Callback)new Callback<ListView<String>, ListCell<String>>() {
                public ListCell<String> call(final ListView<String> param) {
                    return new ListCell<String>() {
                        public void updateItem(final String item, final boolean empty) {
                            super.updateItem((Object)item, empty);
                            this.setText(item);
                            if (item != null) {
                                final CheckBox checkBox = new CheckBox();
                                checkBox.setSelected(FilterBase.this.copySet.contains(item));
                                checkBox.selectedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
                                    public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
                                        if (newValue) {
                                            FilterBase.this.copySet.add(item);
                                        }
                                        else {
                                            FilterBase.this.copySet.remove(item);
                                        }
                                    }
                                });
                                this.setGraphic((Node)checkBox);
                            }
                        }
                    };
                }
            });
            for (int i = this.spv.getFilteredRow() + 1; i < this.spv.getGrid().getRowCount(); ++i) {
                this.stringSet.add(((SpreadsheetCell)((ObservableList)this.spv.getGrid().getRows().get(i)).get(this.column)).getText());
            }
            listView.setItems(FXCollections.observableArrayList((Collection)this.stringSet));
            final CustomMenuItem customMenuItem = new CustomMenuItem((Node)listView);
            customMenuItem.setHideOnClick(false);
            this.menuButton.getItems().addAll((Object[])new MenuItem[] { sortItem, customMenuItem });
        }
        this.copySet.clear();
        for (int j = this.spv.getFilteredRow() + 1; j < this.spv.getGrid().getRowCount(); ++j) {
            if (!this.spv.getHiddenRows().get(j)) {
                this.copySet.add(((SpreadsheetCell)((ObservableList)this.spv.getGrid().getRows().get(j)).get(this.column)).getText());
            }
        }
    }
}
