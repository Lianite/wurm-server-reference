// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.table;

import javafx.beans.Observable;
import java.util.stream.Stream;
import javafx.stage.WindowEvent;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.collections.WeakListChangeListener;
import java.util.Optional;
import java.util.Objects;
import javafx.beans.value.WeakChangeListener;
import java.util.function.Function;
import javafx.beans.value.ObservableValue;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import java.util.function.BiPredicate;
import javafx.beans.value.ChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import org.controlsfx.control.table.TableFilter;

public final class ColumnFilter<T, R>
{
    private final TableFilter<T> tableFilter;
    private final TableColumn<T, R> tableColumn;
    private final ObservableList<FilterValue<T, R>> filterValues;
    private final DupeCounter<R> filterValuesDupeCounter;
    private final DupeCounter<R> visibleValuesDupeCounter;
    private final HashSet<R> unselectedValues;
    private final HashMap<CellIdentity<T>, ChangeListener<R>> trackedCells;
    private boolean lastFilter;
    private boolean isDirty;
    private BiPredicate<String, String> searchStrategy;
    private volatile FilterPanel filterPanel;
    private boolean initialized;
    private final ListChangeListener<T> backingListListener;
    private final ListChangeListener<T> itemsListener;
    private final ChangeListener<R> changeListener;
    private final ListChangeListener<FilterValue<T, R>> filterValueListChangeListener;
    
    public ColumnFilter(final TableFilter<T> tableFilter, final TableColumn<T, R> tableColumn) {
        this.filterValuesDupeCounter = new DupeCounter<R>(false);
        this.visibleValuesDupeCounter = new DupeCounter<R>(false);
        this.unselectedValues = new HashSet<R>();
        this.trackedCells = new HashMap<CellIdentity<T>, ChangeListener<R>>();
        this.lastFilter = false;
        this.isDirty = false;
        this.searchStrategy = ((inputString, subjectString) -> subjectString.contains(inputString));
        this.initialized = false;
        this.backingListListener = (ListChangeListener<T>)(lc -> {
            while (lc.next()) {
                if (lc.wasAdded()) {
                    lc.getAddedSubList().stream().forEach(t -> this.addBackingItem(t, (ObservableValue<R>)this.getTableColumn().getCellObservableValue((Object)t)));
                }
                if (lc.wasRemoved()) {
                    lc.getRemoved().stream().forEach(t -> this.removeBackingItem(t, (ObservableValue<R>)this.getTableColumn().getCellObservableValue((Object)t)));
                }
            }
        });
        this.itemsListener = (ListChangeListener<T>)(lc -> {
            while (lc.next()) {
                if (lc.wasAdded()) {
                    lc.getAddedSubList().stream().map(this.getTableColumn()::getCellObservableValue).forEach(this::addVisibleItem);
                }
                if (lc.wasRemoved()) {
                    lc.getRemoved().stream().map(this.getTableColumn()::getCellObservableValue).forEach(this::removeVisibleItem);
                }
            }
        });
        this.changeListener = (ChangeListener<R>)((observable, oldValue, newValue) -> {
            if (this.filterValuesDupeCounter.add((R)newValue) == 1) {
                this.getFilterValues().add((Object)new FilterValue(newValue, (ColumnFilter<Object, Object>)this));
            }
            this.removeValue(oldValue);
        });
        this.filterValueListChangeListener = (ListChangeListener<FilterValue<T, R>>)(lc -> {
            while (lc.next()) {
                if (lc.wasRemoved()) {
                    lc.getRemoved().stream().filter(v -> !v.selectedProperty().get()).forEach(this.unselectedValues::remove);
                }
                if (lc.wasUpdated()) {
                    final int from = lc.getFrom();
                    final int to = lc.getTo();
                    final boolean value;
                    lc.getList().subList(from, to).forEach(v -> {
                        this.isDirty = true;
                        value = v.selectedProperty().getValue();
                        if (!value) {
                            this.unselectedValues.add(v.getValue());
                        }
                        else {
                            this.unselectedValues.remove(v.getValue());
                        }
                    });
                }
            }
        });
        this.tableFilter = tableFilter;
        this.tableColumn = tableColumn;
        this.filterValues = (ObservableList<FilterValue<T, R>>)FXCollections.observableArrayList(cb -> new Observable[] { cb.selectedProperty() });
        this.attachContextMenu();
    }
    
    void setFilterPanel(final FilterPanel filterPanel) {
        this.filterPanel = filterPanel;
    }
    
    FilterPanel getFilterPanel() {
        return this.filterPanel;
    }
    
    public void initialize() {
        if (!this.initialized) {
            this.initializeListeners();
            this.initializeValues();
            this.initialized = true;
        }
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public void selectValue(final Object value) {
        this.filterPanel.selectValue(value);
    }
    
    public void unselectValue(final Object value) {
        this.filterPanel.unSelectValue(value);
    }
    
    public void selectAllValues() {
        this.filterPanel.selectAllValues();
    }
    
    public void unSelectAllValues() {
        this.filterPanel.unSelectAllValues();
    }
    
    public boolean wasLastFiltered() {
        return this.lastFilter;
    }
    
    public boolean hasUnselections() {
        return this.unselectedValues.size() != 0;
    }
    
    public void setSearchStrategy(final BiPredicate<String, String> searchStrategy) {
        this.searchStrategy = searchStrategy;
    }
    
    public BiPredicate<String, String> getSearchStrategy() {
        return this.searchStrategy;
    }
    
    public boolean isFiltered() {
        return this.isDirty || this.unselectedValues.size() > 0;
    }
    
    public boolean valueIsVisible(final R value) {
        return this.visibleValuesDupeCounter.get(value) > 0;
    }
    
    public void applyFilter() {
        this.tableFilter.executeFilter();
        this.lastFilter = true;
        this.tableFilter.getColumnFilters().stream().filter(c -> !c.equals(this)).forEach(c -> c.lastFilter = false);
        this.tableFilter.getColumnFilters().stream().flatMap(c -> c.filterValues.stream()).forEach(FilterValue::refreshScope);
        this.isDirty = false;
    }
    
    public void resetAllFilters() {
        this.tableFilter.getColumnFilters().stream().flatMap(c -> c.filterValues.stream()).forEach(fv -> fv.selectedProperty().set(true));
        this.tableFilter.resetFilter();
        this.tableFilter.getColumnFilters().stream().forEach(c -> c.lastFilter = false);
        this.tableFilter.getColumnFilters().stream().flatMap(c -> c.filterValues.stream()).forEach(FilterValue::refreshScope);
        this.isDirty = false;
    }
    
    public ObservableList<FilterValue<T, R>> getFilterValues() {
        return this.filterValues;
    }
    
    public TableColumn<T, R> getTableColumn() {
        return this.tableColumn;
    }
    
    public TableFilter<T> getTableFilter() {
        return this.tableFilter;
    }
    
    public boolean evaluate(final T item) {
        final ObservableValue<R> value = (ObservableValue<R>)this.tableColumn.getCellObservableValue((Object)item);
        return this.unselectedValues.size() == 0 || !this.unselectedValues.contains(value.getValue());
    }
    
    private void initializeValues() {
        this.tableFilter.getBackingList().stream().forEach(t -> this.addBackingItem(t, (ObservableValue<R>)this.tableColumn.getCellObservableValue((Object)t)));
        this.tableFilter.getTableView().getItems().stream().map(this.tableColumn::getCellObservableValue).forEach(this::addVisibleItem);
    }
    
    private void addBackingItem(final T item, final ObservableValue<R> cellValue) {
        if (cellValue == null) {
            return;
        }
        if (this.filterValuesDupeCounter.add((R)cellValue.getValue()) == 1) {
            this.filterValues.add((Object)new FilterValue(cellValue.getValue(), (ColumnFilter<Object, Object>)this));
        }
        final CellIdentity<T> trackedCellValue = new CellIdentity<T>(item);
        final ChangeListener<R> cellListener = (ChangeListener<R>)new WeakChangeListener((ChangeListener)this.changeListener);
        cellValue.addListener((ChangeListener)cellListener);
        this.trackedCells.put(trackedCellValue, cellListener);
    }
    
    private void removeBackingItem(final T item, final ObservableValue<R> cellValue) {
        if (cellValue == null) {
            return;
        }
        this.removeValue(cellValue.getValue());
        final ChangeListener<R> listener = this.trackedCells.get(new CellIdentity(item));
        cellValue.removeListener((ChangeListener)listener);
        this.trackedCells.remove(new CellIdentity(item));
    }
    
    private void removeValue(final R value) {
        final boolean removedLastDuplicate = this.filterValuesDupeCounter.remove(value) == 0;
        if (removedLastDuplicate) {
            final Optional<FilterValue<T, R>> existingFilterValue = this.getFilterValues().stream().filter(fv -> Objects.equals(fv.getValue(), value)).findAny();
            if (existingFilterValue.isPresent()) {
                this.getFilterValues().remove((Object)existingFilterValue.get());
            }
        }
    }
    
    private void addVisibleItem(final ObservableValue<R> cellValue) {
        if (cellValue != null) {
            this.visibleValuesDupeCounter.add((R)cellValue.getValue());
        }
    }
    
    private void removeVisibleItem(final ObservableValue<R> cellValue) {
        if (cellValue != null) {
            this.visibleValuesDupeCounter.remove((R)cellValue.getValue());
        }
    }
    
    private void initializeListeners() {
        this.tableFilter.getBackingList().addListener((ListChangeListener)new WeakListChangeListener((ListChangeListener)this.backingListListener));
        this.tableFilter.getTableView().getItems().addListener((ListChangeListener)new WeakListChangeListener((ListChangeListener)this.itemsListener));
        this.filterValues.addListener((ListChangeListener)new WeakListChangeListener((ListChangeListener)this.filterValueListChangeListener));
    }
    
    private void attachContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final CustomMenuItem item = FilterPanel.getInMenuItem((ColumnFilter<Object, Object>)this, contextMenu);
        contextMenu.getStyleClass().add((Object)"column-filter");
        contextMenu.getItems().add((Object)item);
        this.tableColumn.setContextMenu(contextMenu);
        contextMenu.setOnShowing(ae -> this.initialize());
    }
    
    private static final class CellIdentity<T>
    {
        private final T item;
        
        CellIdentity(final T item) {
            this.item = item;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this.item == ((CellIdentity)other).item;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.item);
        }
    }
}
