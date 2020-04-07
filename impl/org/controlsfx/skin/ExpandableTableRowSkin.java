// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import org.controlsfx.control.table.TableRowExpanderColumn;
import javafx.scene.control.TableRow;
import com.sun.javafx.scene.control.skin.TableRowSkin;

public class ExpandableTableRowSkin<S> extends TableRowSkin<S>
{
    private final TableRow<S> tableRow;
    private TableRowExpanderColumn<S> expander;
    private Double tableRowPrefHeight;
    
    public ExpandableTableRowSkin(final TableRow<S> tableRow, final TableRowExpanderColumn<S> expander) {
        super((TableRow)tableRow);
        this.tableRowPrefHeight = -1.0;
        this.tableRow = tableRow;
        this.expander = expander;
        tableRow.itemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                final Node expandedNode = this.expander.getExpandedNode((S)oldValue);
                if (expandedNode != null) {
                    this.getChildren().remove((Object)expandedNode);
                }
            }
        });
    }
    
    private Node getContent() {
        final Node node = this.expander.getOrCreateExpandedNode(this.tableRow);
        if (!this.getChildren().contains((Object)node)) {
            this.getChildren().add((Object)node);
        }
        return node;
    }
    
    private Boolean isExpanded() {
        return ((TableRow)this.getSkinnable()).getItem() != null && (boolean)this.expander.getCellData(((TableRow)this.getSkinnable()).getIndex());
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        this.tableRowPrefHeight = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        return this.isExpanded() ? (this.tableRowPrefHeight + this.getContent().prefHeight(width)) : this.tableRowPrefHeight;
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        if (this.isExpanded()) {
            this.getContent().resizeRelocate(0.0, (double)this.tableRowPrefHeight, w, h - this.tableRowPrefHeight);
        }
    }
}
