// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.collections.ListChangeListener;
import javafx.beans.Observable;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import javafx.scene.control.ListCell;
import java.util.HashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.BooleanProperty;
import java.util.Map;
import javafx.scene.control.ListView;

public class CheckListView<T> extends ListView<T>
{
    private final Map<T, BooleanProperty> itemBooleanMap;
    private ObjectProperty<IndexedCheckModel<T>> checkModel;
    
    public CheckListView() {
        this(FXCollections.observableArrayList());
    }
    
    public CheckListView(final ObservableList<T> items) {
        super((ObservableList)items);
        this.checkModel = (ObjectProperty<IndexedCheckModel<T>>)new SimpleObjectProperty((Object)this, "checkModel");
        this.itemBooleanMap = new HashMap<T, BooleanProperty>();
        this.setCheckModel(new CheckListViewBitSetCheckModel<T>((javafx.collections.ObservableList<T>)this.getItems(), this.itemBooleanMap));
        this.itemsProperty().addListener(ov -> this.setCheckModel(new CheckListViewBitSetCheckModel<T>((javafx.collections.ObservableList<T>)this.getItems(), this.itemBooleanMap)));
        this.setCellFactory(listView -> new CheckBoxListCell((Callback)new Callback<T, ObservableValue<Boolean>>() {
            public ObservableValue<Boolean> call(final T item) {
                return (ObservableValue<Boolean>)CheckListView.this.getItemBooleanProperty(item);
            }
        }));
    }
    
    public BooleanProperty getItemBooleanProperty(final int index) {
        if (index < 0 || index >= this.getItems().size()) {
            return null;
        }
        return this.getItemBooleanProperty(this.getItems().get(index));
    }
    
    public BooleanProperty getItemBooleanProperty(final T item) {
        return this.itemBooleanMap.get(item);
    }
    
    public final void setCheckModel(final IndexedCheckModel<T> value) {
        this.checkModelProperty().set((Object)value);
    }
    
    public final IndexedCheckModel<T> getCheckModel() {
        return (IndexedCheckModel<T>)((this.checkModel == null) ? null : ((IndexedCheckModel)this.checkModel.get()));
    }
    
    public final ObjectProperty<IndexedCheckModel<T>> checkModelProperty() {
        return this.checkModel;
    }
    
    private static class CheckListViewBitSetCheckModel<T> extends CheckBitSetModelBase<T>
    {
        private final ObservableList<T> items;
        
        CheckListViewBitSetCheckModel(final ObservableList<T> items, final Map<T, BooleanProperty> itemBooleanMap) {
            super(itemBooleanMap);
            (this.items = items).addListener((ListChangeListener)new ListChangeListener<T>() {
                public void onChanged(final ListChangeListener.Change<? extends T> c) {
                    CheckListViewBitSetCheckModel.this.updateMap();
                }
            });
            this.updateMap();
        }
        
        @Override
        public T getItem(final int index) {
            return (T)this.items.get(index);
        }
        
        @Override
        public int getItemCount() {
            return this.items.size();
        }
        
        @Override
        public int getItemIndex(final T item) {
            return this.items.indexOf((Object)item);
        }
    }
}
