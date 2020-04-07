// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javafx.scene.control.TableColumn;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.List;
import java.util.function.BiPredicate;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.collections.FXCollections;
import impl.org.controlsfx.table.ColumnFilter;
import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public final class TableFilter<T>
{
    private final TableView<T> tableView;
    private final ObservableList<T> backingList;
    private final FilteredList<T> filteredList;
    private final ObservableList<ColumnFilter<T, ?>> columnFilters;
    
    public TableFilter(final TableView<T> tableView) {
        this(tableView, false);
    }
    
    private TableFilter(final TableView<T> tableView, final boolean isLazy) {
        this.columnFilters = (ObservableList<ColumnFilter<T, ?>>)FXCollections.observableArrayList();
        this.tableView = tableView;
        this.backingList = (ObservableList<T>)tableView.getItems();
        this.filteredList = (FilteredList<T>)new FilteredList((ObservableList)new SortedList((ObservableList)this.backingList));
        final SortedList<T> sortedControlList = (SortedList<T>)new SortedList((ObservableList)this.filteredList);
        this.filteredList.setPredicate(v -> true);
        sortedControlList.comparatorProperty().bind((ObservableValue)tableView.comparatorProperty());
        tableView.setItems((ObservableList)sortedControlList);
        this.applyForAllColumns(isLazy);
        tableView.getStylesheets().add((Object)"/impl/org/controlsfx/table/tablefilter.css");
        if (!isLazy) {
            this.columnFilters.forEach(ColumnFilter::initialize);
        }
    }
    
    public void setSearchStrategy(final BiPredicate<String, String> searchStrategy) {
        this.columnFilters.forEach(cf -> cf.setSearchStrategy(searchStrategy));
    }
    
    public ObservableList<T> getBackingList() {
        return this.backingList;
    }
    
    public FilteredList<T> getFilteredList() {
        return this.filteredList;
    }
    
    private void applyForAllColumns(final boolean isLazy) {
        this.columnFilters.setAll((Collection)this.tableView.getColumns().stream().flatMap(this::extractNestedColumns).map(c -> new ColumnFilter((TableFilter<Object>)this, (javafx.scene.control.TableColumn<Object, Object>)c)).collect((Collector<? super Object, ?, List<Object>>)Collectors.toList()));
    }
    
    private <S> Stream<TableColumn<T, ?>> extractNestedColumns(final TableColumn<T, S> tableColumn) {
        if (tableColumn.getColumns().size() == 0) {
            return (Stream<TableColumn<T, ?>>)Stream.of(tableColumn);
        }
        return tableColumn.getColumns().stream().flatMap(this::extractNestedColumns);
    }
    
    public void selectValue(final TableColumn<?, ?> column, final Object value) {
        this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(c -> c.selectValue(value));
    }
    
    public void unselectValue(final TableColumn<?, ?> column, final Object value) {
        this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(c -> c.unselectValue(value));
    }
    
    public void selectAllValues(final TableColumn<?, ?> column) {
        this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(ColumnFilter::selectAllValues);
    }
    
    public void unSelectAllValues(final TableColumn<?, ?> column) {
        this.columnFilters.stream().filter(c -> c.getTableColumn() == column).forEach(ColumnFilter::unSelectAllValues);
    }
    
    public void executeFilter() {
        if (this.columnFilters.stream().filter(ColumnFilter::isFiltered).findAny().isPresent()) {
            this.filteredList.setPredicate(item -> !this.columnFilters.stream().filter(cf -> !cf.evaluate(item)).findAny().isPresent());
        }
        else {
            this.resetFilter();
        }
    }
    
    public void resetFilter() {
        this.filteredList.setPredicate(item -> true);
    }
    
    public TableView<T> getTableView() {
        return this.tableView;
    }
    
    public ObservableList<ColumnFilter<T, ?>> getColumnFilters() {
        return this.columnFilters;
    }
    
    public Optional<ColumnFilter<T, ?>> getColumnFilter(final TableColumn<T, ?> tableColumn) {
        return this.columnFilters.stream().filter(f -> f.getTableColumn().equals(tableColumn)).findAny();
    }
    
    public boolean isDirty() {
        return this.columnFilters.stream().filter(ColumnFilter::isFiltered).findAny().isPresent();
    }
    
    public static <T> Builder<T> forTableView(final TableView<T> tableView) {
        return new Builder<T>((TableView)tableView);
    }
    
    public static final class Builder<T>
    {
        private final TableView<T> tableView;
        private volatile boolean lazyInd;
        
        private Builder(final TableView<T> tableView) {
            this.lazyInd = false;
            this.tableView = tableView;
        }
        
        public Builder<T> lazy(final boolean isLazy) {
            this.lazyInd = isLazy;
            return this;
        }
        
        public TableFilter<T> apply() {
            return new TableFilter<T>(this.tableView, this.lazyInd, null);
        }
    }
}
