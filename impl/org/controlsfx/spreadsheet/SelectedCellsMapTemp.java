// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import java.util.Iterator;
import java.util.Collection;
import java.util.TreeMap;
import javafx.collections.transformation.SortedList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import java.util.BitSet;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TablePositionBase;

public class SelectedCellsMapTemp<T extends TablePositionBase>
{
    private final ObservableList<T> selectedCells;
    private final ObservableList<T> sortedSelectedCells;
    private final Map<Integer, BitSet> selectedCellBitSetMap;
    
    public SelectedCellsMapTemp(final ListChangeListener<T> listener) {
        this.selectedCells = (ObservableList<T>)FXCollections.observableArrayList();
        final int result;
        (this.sortedSelectedCells = (ObservableList<T>)new SortedList((ObservableList)this.selectedCells, (o1, o2) -> {
            result = o1.getRow() - o2.getRow();
            return (result == 0) ? (o1.getColumn() - o2.getColumn()) : result;
        })).addListener((ListChangeListener)listener);
        this.selectedCellBitSetMap = new TreeMap<Integer, BitSet>((o1, o2) -> o1.compareTo(o2));
    }
    
    public int size() {
        return this.selectedCells.size();
    }
    
    public T get(final int i) {
        if (i < 0) {
            return null;
        }
        return (T)this.sortedSelectedCells.get(i);
    }
    
    public void add(final T tp) {
        final int row = tp.getRow();
        final int columnIndex = tp.getColumn();
        BitSet bitset;
        if (!this.selectedCellBitSetMap.containsKey(row)) {
            bitset = new BitSet();
            this.selectedCellBitSetMap.put(row, bitset);
        }
        else {
            bitset = this.selectedCellBitSetMap.get(row);
        }
        if (columnIndex >= 0) {
            final boolean isAlreadySet = bitset.get(columnIndex);
            bitset.set(columnIndex);
            if (!isAlreadySet) {
                this.selectedCells.add((Object)tp);
            }
        }
        else if (!this.selectedCells.contains((Object)tp)) {
            this.selectedCells.add((Object)tp);
        }
    }
    
    public void addAll(final Collection<T> cells) {
        for (final T tp : cells) {
            final int row = tp.getRow();
            final int columnIndex = tp.getColumn();
            BitSet bitset;
            if (!this.selectedCellBitSetMap.containsKey(row)) {
                bitset = new BitSet();
                this.selectedCellBitSetMap.put(row, bitset);
            }
            else {
                bitset = this.selectedCellBitSetMap.get(row);
            }
            if (columnIndex < 0) {
                continue;
            }
            bitset.set(columnIndex);
        }
        this.selectedCells.addAll((Collection)cells);
    }
    
    public void setAll(final Collection<T> cells) {
        this.selectedCellBitSetMap.clear();
        for (final T tp : cells) {
            final int row = tp.getRow();
            final int columnIndex = tp.getColumn();
            BitSet bitset;
            if (!this.selectedCellBitSetMap.containsKey(row)) {
                bitset = new BitSet();
                this.selectedCellBitSetMap.put(row, bitset);
            }
            else {
                bitset = this.selectedCellBitSetMap.get(row);
            }
            if (columnIndex < 0) {
                continue;
            }
            bitset.set(columnIndex);
        }
        this.selectedCells.setAll((Collection)cells);
    }
    
    public void remove(final T tp) {
        final int row = tp.getRow();
        final int columnIndex = tp.getColumn();
        if (this.selectedCellBitSetMap.containsKey(row)) {
            final BitSet bitset = this.selectedCellBitSetMap.get(row);
            if (columnIndex >= 0) {
                bitset.clear(columnIndex);
            }
            if (bitset.isEmpty()) {
                this.selectedCellBitSetMap.remove(row);
            }
        }
        this.selectedCells.remove((Object)tp);
    }
    
    public void clear() {
        this.selectedCellBitSetMap.clear();
        this.selectedCells.clear();
    }
    
    public boolean isSelected(final int row, final int columnIndex) {
        if (columnIndex < 0) {
            return this.selectedCellBitSetMap.containsKey(row);
        }
        return this.selectedCellBitSetMap.containsKey(row) && this.selectedCellBitSetMap.get(row).get(columnIndex);
    }
    
    public int indexOf(final T tp) {
        return this.sortedSelectedCells.indexOf((Object)tp);
    }
    
    public boolean isEmpty() {
        return this.selectedCells.isEmpty();
    }
    
    public ObservableList<T> getSelectedCells() {
        return this.selectedCells;
    }
}
