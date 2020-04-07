// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleBooleanProperty;
import com.sun.javafx.collections.NonIterableChange;
import java.util.Iterator;
import javafx.collections.ObservableList;
import com.sun.javafx.collections.MappingChange;
import javafx.collections.ListChangeListener;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import java.util.BitSet;
import javafx.beans.property.BooleanProperty;
import java.util.Map;

abstract class CheckBitSetModelBase<T> implements IndexedCheckModel<T>
{
    private final Map<T, BooleanProperty> itemBooleanMap;
    private final BitSet checkedIndices;
    private final ReadOnlyUnbackedObservableList<Integer> checkedIndicesList;
    private final ReadOnlyUnbackedObservableList<T> checkedItemsList;
    
    CheckBitSetModelBase(final Map<T, BooleanProperty> itemBooleanMap) {
        this.itemBooleanMap = itemBooleanMap;
        this.checkedIndices = new BitSet();
        this.checkedIndicesList = new ReadOnlyUnbackedObservableList<Integer>() {
            public Integer get(final int index) {
                if (index < 0 || index >= CheckBitSetModelBase.this.getItemCount()) {
                    return -1;
                }
                for (int pos = 0, val = CheckBitSetModelBase.this.checkedIndices.nextSetBit(0); val >= 0 || pos == index; ++pos, val = CheckBitSetModelBase.this.checkedIndices.nextSetBit(val + 1)) {
                    if (pos == index) {
                        return val;
                    }
                }
                return -1;
            }
            
            public int size() {
                return CheckBitSetModelBase.this.checkedIndices.cardinality();
            }
            
            public boolean contains(final Object o) {
                if (o instanceof Number) {
                    final Number n = (Number)o;
                    final int index = n.intValue();
                    return index >= 0 && index < CheckBitSetModelBase.this.checkedIndices.length() && CheckBitSetModelBase.this.checkedIndices.get(index);
                }
                return false;
            }
        };
        this.checkedItemsList = new ReadOnlyUnbackedObservableList<T>() {
            public T get(final int i) {
                final int pos = (int)CheckBitSetModelBase.this.checkedIndicesList.get(i);
                if (pos < 0 || pos >= CheckBitSetModelBase.this.getItemCount()) {
                    return null;
                }
                return CheckBitSetModelBase.this.getItem(pos);
            }
            
            public int size() {
                return CheckBitSetModelBase.this.checkedIndices.cardinality();
            }
        };
        final MappingChange.Map<Integer, T> map = (MappingChange.Map<Integer, T>)(f -> this.getItem(f));
        this.checkedIndicesList.addListener((ListChangeListener)new ListChangeListener<Integer>() {
            public void onChanged(final ListChangeListener.Change<? extends Integer> c) {
                boolean hasRealChangeOccurred;
                for (hasRealChangeOccurred = false; c.next() && !hasRealChangeOccurred; hasRealChangeOccurred = (c.wasAdded() || c.wasRemoved())) {}
                if (hasRealChangeOccurred) {
                    c.reset();
                    CheckBitSetModelBase.this.checkedItemsList.callObservers((ListChangeListener.Change)new MappingChange((ListChangeListener.Change)c, map, (ObservableList)CheckBitSetModelBase.this.checkedItemsList));
                }
                c.reset();
            }
        });
        this.getCheckedItems().addListener((ListChangeListener)new ListChangeListener<T>() {
            public void onChanged(final ListChangeListener.Change<? extends T> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (final T item : c.getAddedSubList()) {
                            final BooleanProperty p = CheckBitSetModelBase.this.getItemBooleanProperty(item);
                            if (p != null) {
                                p.set(true);
                            }
                        }
                    }
                    if (c.wasRemoved()) {
                        for (final T item : c.getRemoved()) {
                            final BooleanProperty p = CheckBitSetModelBase.this.getItemBooleanProperty(item);
                            if (p != null) {
                                p.set(false);
                            }
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public abstract T getItem(final int p0);
    
    @Override
    public abstract int getItemCount();
    
    @Override
    public abstract int getItemIndex(final T p0);
    
    BooleanProperty getItemBooleanProperty(final T item) {
        return this.itemBooleanMap.get(item);
    }
    
    @Override
    public ObservableList<Integer> getCheckedIndices() {
        return (ObservableList<Integer>)this.checkedIndicesList;
    }
    
    @Override
    public ObservableList<T> getCheckedItems() {
        return (ObservableList<T>)this.checkedItemsList;
    }
    
    @Override
    public void checkAll() {
        for (int i = 0; i < this.getItemCount(); ++i) {
            this.check(i);
        }
    }
    
    @Override
    public void checkIndices(final int... indices) {
        for (int i = 0; i < indices.length; ++i) {
            this.check(indices[i]);
        }
    }
    
    @Override
    public void clearCheck(final T item) {
        final int index = this.getItemIndex(item);
        this.clearCheck(index);
    }
    
    @Override
    public void clearChecks() {
        for (int index = 0; index < this.checkedIndices.length(); ++index) {
            this.clearCheck(index);
        }
    }
    
    @Override
    public void clearCheck(final int index) {
        if (index < 0 || index >= this.getItemCount()) {
            return;
        }
        this.checkedIndices.clear(index);
        final int changeIndex = this.checkedIndicesList.indexOf((Object)index);
        this.checkedIndicesList.callObservers((ListChangeListener.Change)new NonIterableChange.SimpleRemovedChange(changeIndex, changeIndex, (Object)index, (ObservableList)this.checkedIndicesList));
    }
    
    @Override
    public boolean isEmpty() {
        return this.checkedIndices.isEmpty();
    }
    
    @Override
    public boolean isChecked(final T item) {
        final int index = this.getItemIndex(item);
        return this.isChecked(index);
    }
    
    @Override
    public boolean isChecked(final int index) {
        return this.checkedIndices.get(index);
    }
    
    @Override
    public void check(final int index) {
        if (index < 0 || index >= this.getItemCount()) {
            return;
        }
        this.checkedIndices.set(index);
        final int changeIndex = this.checkedIndicesList.indexOf((Object)index);
        this.checkedIndicesList.callObservers((ListChangeListener.Change)new NonIterableChange.SimpleAddChange(changeIndex, changeIndex + 1, (ObservableList)this.checkedIndicesList));
    }
    
    @Override
    public void check(final T item) {
        final int index = this.getItemIndex(item);
        this.check(index);
    }
    
    protected void updateMap() {
        this.itemBooleanMap.clear();
        for (int i = 0; i < this.getItemCount(); ++i) {
            final int index = i;
            final T item = this.getItem(index);
            final BooleanProperty booleanProperty = (BooleanProperty)new SimpleBooleanProperty((Object)item, "selected", false);
            this.itemBooleanMap.put(item, booleanProperty);
            booleanProperty.addListener((InvalidationListener)new InvalidationListener() {
                public void invalidated(final Observable o) {
                    if (booleanProperty.get()) {
                        CheckBitSetModelBase.this.checkedIndices.set(index);
                        final int changeIndex = CheckBitSetModelBase.this.checkedIndicesList.indexOf((Object)index);
                        CheckBitSetModelBase.this.checkedIndicesList.callObservers((ListChangeListener.Change)new NonIterableChange.SimpleAddChange(changeIndex, changeIndex + 1, (ObservableList)CheckBitSetModelBase.this.checkedIndicesList));
                    }
                    else {
                        final int changeIndex = CheckBitSetModelBase.this.checkedIndicesList.indexOf((Object)index);
                        CheckBitSetModelBase.this.checkedIndices.clear(index);
                        CheckBitSetModelBase.this.checkedIndicesList.callObservers((ListChangeListener.Change)new NonIterableChange.SimpleRemovedChange(changeIndex, changeIndex, (Object)index, (ObservableList)CheckBitSetModelBase.this.checkedIndicesList));
                    }
                }
            });
        }
    }
}
