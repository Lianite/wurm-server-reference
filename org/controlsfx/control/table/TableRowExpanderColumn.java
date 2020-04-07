// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import impl.org.controlsfx.skin.ExpandableTableRowSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;
import javafx.beans.value.ObservableValue;
import java.util.HashMap;
import javafx.scene.control.TableRow;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Callback;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import java.util.Map;
import javafx.scene.control.TableColumn;

public final class TableRowExpanderColumn<S> extends TableColumn<S, Boolean>
{
    private static final String STYLE_CLASS = "expander-column";
    private static final String EXPANDER_BUTTON_STYLE_CLASS = "expander-button";
    private final Map<S, Node> expandedNodeCache;
    private final Map<S, BooleanProperty> expansionState;
    private Callback<TableRowDataFeatures<S>, Node> expandedNodeCallback;
    
    public BooleanProperty getExpandedProperty(final S item) {
        BooleanProperty value = this.expansionState.get(item);
        if (value == null) {
            value = (BooleanProperty)new SimpleBooleanProperty(item, "expanded", false) {
                protected void invalidated() {
                    TableRowExpanderColumn.this.getTableView().refresh();
                    if (!this.getValue()) {
                        TableRowExpanderColumn.this.expandedNodeCache.remove(this.getBean());
                    }
                }
            };
            this.expansionState.put(item, value);
        }
        return value;
    }
    
    public Node getOrCreateExpandedNode(final TableRow<S> tableRow) {
        final int index = tableRow.getIndex();
        if (index > -1 && index < this.getTableView().getItems().size()) {
            final S item = (S)this.getTableView().getItems().get(index);
            Node node = this.expandedNodeCache.get(item);
            if (node == null) {
                node = (Node)this.expandedNodeCallback.call((Object)new TableRowDataFeatures((javafx.scene.control.TableRow<Object>)tableRow, (TableRowExpanderColumn<Object>)this, item));
                this.expandedNodeCache.put(item, node);
            }
            return node;
        }
        return null;
    }
    
    public Node getExpandedNode(final S item) {
        return this.expandedNodeCache.get(item);
    }
    
    public TableRowExpanderColumn(final Callback<TableRowDataFeatures<S>, Node> expandedNodeCallback) {
        this.expandedNodeCache = new HashMap<S, Node>();
        this.expansionState = new HashMap<S, BooleanProperty>();
        this.expandedNodeCallback = expandedNodeCallback;
        this.getStyleClass().add((Object)"expander-column");
        this.setCellValueFactory(param -> this.getExpandedProperty(param.getValue()));
        this.setCellFactory(param -> new ToggleCell());
        this.installRowFactoryOnTableViewAssignment();
    }
    
    private void installRowFactoryOnTableViewAssignment() {
        this.tableViewProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.getTableView().setRowFactory(param -> new TableRow<S>() {
                    protected Skin<?> createDefaultSkin() {
                        return (Skin<?>)new ExpandableTableRowSkin((javafx.scene.control.TableRow<Object>)this, TableRowExpanderColumn.this);
                    }
                });
            }
        });
    }
    
    public void toggleExpanded(final int index) {
        final BooleanProperty expanded = (BooleanProperty)this.getCellObservableValue(index);
        expanded.setValue(!expanded.getValue());
    }
    
    private final class ToggleCell extends TableCell<S, Boolean>
    {
        private Button button;
        
        public ToggleCell() {
            (this.button = new Button()).setFocusTraversable(false);
            this.button.getStyleClass().add((Object)"expander-button");
            this.button.setPrefSize(16.0, 16.0);
            this.button.setPadding(new Insets(0.0));
            this.button.setOnAction(event -> TableRowExpanderColumn.this.toggleExpanded(this.getIndex()));
        }
        
        protected void updateItem(final Boolean expanded, final boolean empty) {
            super.updateItem((Object)expanded, empty);
            if (expanded == null || empty) {
                this.setGraphic((Node)null);
            }
            else {
                this.button.setText(((boolean)expanded) ? "-" : "+");
                this.setGraphic((Node)this.button);
            }
        }
    }
    
    public static final class TableRowDataFeatures<S>
    {
        private TableRow<S> tableRow;
        private TableRowExpanderColumn<S> tableColumn;
        private BooleanProperty expandedProperty;
        private S value;
        
        public TableRowDataFeatures(final TableRow<S> tableRow, final TableRowExpanderColumn<S> tableColumn, final S value) {
            this.tableRow = tableRow;
            this.tableColumn = tableColumn;
            this.expandedProperty = (BooleanProperty)tableColumn.getCellObservableValue(tableRow.getIndex());
            this.value = value;
        }
        
        public TableRow<S> getTableRow() {
            return this.tableRow;
        }
        
        public TableRowExpanderColumn<S> getTableColumn() {
            return this.tableColumn;
        }
        
        public BooleanProperty expandedProperty() {
            return this.expandedProperty;
        }
        
        public void toggleExpanded() {
            final BooleanProperty expanded = this.expandedProperty();
            expanded.setValue(!expanded.getValue());
        }
        
        public Boolean isExpanded() {
            return this.expandedProperty().getValue();
        }
        
        public void setExpanded(final Boolean expanded) {
            this.expandedProperty().setValue(expanded);
        }
        
        public S getValue() {
            return this.value;
        }
    }
}
