// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import java.util.function.Consumer;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TreeView;

public class CheckTreeView<T> extends TreeView<T>
{
    private ObjectProperty<CheckModel<TreeItem<T>>> checkModel;
    
    public CheckTreeView() {
        this(null);
    }
    
    public CheckTreeView(final CheckBoxTreeItem<T> root) {
        super((TreeItem)root);
        this.checkModel = (ObjectProperty<CheckModel<TreeItem<T>>>)new SimpleObjectProperty((Object)this, "checkModel");
        this.rootProperty().addListener(o -> this.updateCheckModel());
        this.updateCheckModel();
        this.setCellFactory(CheckBoxTreeCell.forTreeView());
    }
    
    protected void updateCheckModel() {
        if (this.getRoot() != null) {
            this.setCheckModel(new CheckTreeViewCheckModel<T>(this));
        }
    }
    
    public BooleanProperty getItemBooleanProperty(final int index) {
        final CheckBoxTreeItem<T> treeItem = (CheckBoxTreeItem<T>)this.getTreeItem(index);
        return treeItem.selectedProperty();
    }
    
    public final void setCheckModel(final CheckModel<TreeItem<T>> value) {
        this.checkModelProperty().set((Object)value);
    }
    
    public final CheckModel<TreeItem<T>> getCheckModel() {
        return (CheckModel<TreeItem<T>>)((this.checkModel == null) ? null : ((CheckModel)this.checkModel.get()));
    }
    
    public final ObjectProperty<CheckModel<TreeItem<T>>> checkModelProperty() {
        return this.checkModel;
    }
    
    private static class CheckTreeViewCheckModel<T> implements CheckModel<TreeItem<T>>
    {
        private final CheckTreeView<T> treeView;
        private final TreeItem<T> root;
        private ObservableList<TreeItem<T>> checkedItems;
        
        CheckTreeViewCheckModel(final CheckTreeView<T> treeView) {
            this.checkedItems = (ObservableList<TreeItem<T>>)FXCollections.observableArrayList();
            this.treeView = treeView;
            (this.root = (TreeItem<T>)treeView.getRoot()).addEventHandler(CheckBoxTreeItem.checkBoxSelectionChangedEvent(), e -> {
                final CheckBoxTreeItem<T> treeItem = (CheckBoxTreeItem<T>)e.getTreeItem();
                if (treeItem.isSelected()) {
                    this.check((TreeItem<T>)treeItem);
                }
                else {
                    this.clearCheck((TreeItem<T>)treeItem);
                }
            });
            this.clearChecks();
            for (int i = 0; i < treeView.getExpandedItemCount(); ++i) {
                final CheckBoxTreeItem<T> treeItem = (CheckBoxTreeItem<T>)treeView.getTreeItem(i);
                if (treeItem.isSelected() && !treeItem.isIndeterminate()) {
                    this.check((TreeItem<T>)treeItem);
                }
            }
        }
        
        @Override
        public int getItemCount() {
            return this.treeView.getExpandedItemCount();
        }
        
        @Override
        public ObservableList<TreeItem<T>> getCheckedItems() {
            return this.checkedItems;
        }
        
        @Override
        public void checkAll() {
            this.iterateOverTree(this::check);
        }
        
        @Override
        public void clearCheck(final TreeItem<T> item) {
            if (item instanceof CheckBoxTreeItem) {
                ((CheckBoxTreeItem)item).setSelected(false);
            }
            this.checkedItems.remove((Object)item);
        }
        
        @Override
        public void clearChecks() {
            final List<TreeItem<T>> items = new ArrayList<TreeItem<T>>((Collection<? extends TreeItem<T>>)this.checkedItems);
            for (final TreeItem<T> item : items) {
                this.clearCheck(item);
            }
        }
        
        @Override
        public boolean isEmpty() {
            return this.checkedItems.isEmpty();
        }
        
        @Override
        public boolean isChecked(final TreeItem<T> item) {
            return this.checkedItems.contains((Object)item);
        }
        
        @Override
        public void check(final TreeItem<T> item) {
            if (item instanceof CheckBoxTreeItem) {
                ((CheckBoxTreeItem)item).setSelected(true);
            }
            if (!this.checkedItems.contains((Object)item)) {
                this.checkedItems.add((Object)item);
            }
        }
        
        private void iterateOverTree(final Consumer<TreeItem<T>> consumer) {
            this.processNode(consumer, this.root);
        }
        
        private void processNode(final Consumer<TreeItem<T>> consumer, final TreeItem<T> node) {
            if (node == null) {
                return;
            }
            consumer.accept(node);
            this.processChildren(consumer, (List<TreeItem<T>>)node.getChildren());
        }
        
        private void processChildren(final Consumer<TreeItem<T>> consumer, final List<TreeItem<T>> children) {
            if (children == null) {
                return;
            }
            for (final TreeItem<T> child : children) {
                this.processNode(consumer, child);
            }
        }
    }
}
