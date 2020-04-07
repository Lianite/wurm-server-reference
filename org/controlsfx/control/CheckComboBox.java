// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.collections.ListChangeListener;
import impl.org.controlsfx.skin.CheckComboBoxSkin;
import javafx.scene.control.Skin;
import javafx.collections.FXCollections;
import java.util.HashMap;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.BooleanProperty;
import java.util.Map;
import javafx.collections.ObservableList;

public class CheckComboBox<T> extends ControlsFXControl
{
    private final ObservableList<T> items;
    private final Map<T, BooleanProperty> itemBooleanMap;
    private ObjectProperty<IndexedCheckModel<T>> checkModel;
    private ObjectProperty<StringConverter<T>> converter;
    
    public CheckComboBox() {
        this(null);
    }
    
    public CheckComboBox(final ObservableList<T> items) {
        this.checkModel = (ObjectProperty<IndexedCheckModel<T>>)new SimpleObjectProperty((Object)this, "checkModel");
        this.converter = (ObjectProperty<StringConverter<T>>)new SimpleObjectProperty((Object)this, "converter");
        final int initialSize = (items == null) ? 32 : items.size();
        this.itemBooleanMap = new HashMap<T, BooleanProperty>(initialSize);
        this.items = (ObservableList<T>)((items == null) ? FXCollections.observableArrayList() : items);
        this.setCheckModel(new CheckComboBoxBitSetCheckModel<T>(this.items, this.itemBooleanMap));
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new CheckComboBoxSkin((CheckComboBox<Object>)this);
    }
    
    public ObservableList<T> getItems() {
        return this.items;
    }
    
    public BooleanProperty getItemBooleanProperty(final int index) {
        if (index < 0 || index >= this.items.size()) {
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
    
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return this.converter;
    }
    
    public final void setConverter(final StringConverter<T> value) {
        this.converterProperty().set((Object)value);
    }
    
    public final StringConverter<T> getConverter() {
        return (StringConverter<T>)this.converterProperty().get();
    }
    
    private static class CheckComboBoxBitSetCheckModel<T> extends CheckBitSetModelBase<T>
    {
        private final ObservableList<T> items;
        
        CheckComboBoxBitSetCheckModel(final ObservableList<T> items, final Map<T, BooleanProperty> itemBooleanMap) {
            super(itemBooleanMap);
            (this.items = items).addListener((ListChangeListener)new ListChangeListener<T>() {
                public void onChanged(final ListChangeListener.Change<? extends T> c) {
                    CheckComboBoxBitSetCheckModel.this.updateMap();
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
