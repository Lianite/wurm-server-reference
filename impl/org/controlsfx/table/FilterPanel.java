// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.table;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import java.util.function.Function;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.geometry.Side;
import javafx.scene.input.ContextMenuEvent;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.control.TableColumn;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.CustomMenuItem;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import impl.org.controlsfx.i18n.Localization;
import javafx.geometry.Insets;
import java.util.ArrayList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Skin;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.ImageView;
import java.util.function.Supplier;
import javafx.scene.image.Image;
import javafx.beans.InvalidationListener;
import java.util.Collection;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;
import javafx.scene.layout.VBox;

public final class FilterPanel<T, R> extends VBox
{
    private final ColumnFilter<T, R> columnFilter;
    private final FilteredList<FilterValue> filterList;
    private final TextField searchBox;
    private boolean searchMode;
    private boolean bumpedWidth;
    private final ListView<FilterValue> checkListView;
    private final Collection<InvalidationListener> columnHeadersChangeListeners;
    private static final Image filterIcon;
    private static final Supplier<ImageView> filterImageView;
    private final ChangeListener<Skin<?>> skinListener;
    
    void selectAllValues() {
        this.checkListView.getItems().stream().forEach(item -> item.selectedProperty().set(true));
    }
    
    void unSelectAllValues() {
        this.checkListView.getItems().stream().forEach(item -> item.selectedProperty().set(false));
    }
    
    void selectValue(final Object value) {
        this.checkListView.getItems().stream().filter(item -> item.getValue().equals(value)).forEach(item -> item.selectedProperty().set(true));
    }
    
    void unSelectValue(final Object value) {
        this.checkListView.getItems().stream().filter(item -> item.getValue() == value).forEach(item -> item.selectedProperty().set(false));
    }
    
    FilterPanel(final ColumnFilter<T, R> columnFilter, final ContextMenu contextMenu) {
        this.searchBox = new TextField();
        this.searchMode = false;
        this.bumpedWidth = false;
        this.columnHeadersChangeListeners = new ArrayList<InvalidationListener>();
        this.skinListener = (ChangeListener<Skin<?>>)((w, o, n) -> {
            this.columnHeadersChangeListeners.clear();
            if (n instanceof TableViewSkin) {
                final TableViewSkin<?> skin = (TableViewSkin<?>)n;
                checkChangeContextMenu(skin, this.getColumnFilter().getTableColumn(), this);
            }
        });
        columnFilter.setFilterPanel(this);
        this.columnFilter = columnFilter;
        this.getStyleClass().add((Object)"filter-panel");
        this.setPadding(new Insets(3.0));
        this.searchBox.setPromptText(Localization.getString("filterpanel.search.field"));
        this.getChildren().add((Object)this.searchBox);
        this.filterList = (FilteredList<FilterValue>)new FilteredList((ObservableList)new SortedList((ObservableList)columnFilter.getFilterValues()), t -> true);
        (this.checkListView = (ListView<FilterValue>)new ListView()).setItems((ObservableList)new SortedList((ObservableList)this.filterList, FilterValue::compareTo));
        this.getChildren().add((Object)this.checkListView);
        final HBox buttonBox = new HBox();
        final Button applyBttn = new Button(Localization.getString("filterpanel.apply.button"));
        HBox.setHgrow((Node)applyBttn, Priority.ALWAYS);
        applyBttn.setOnAction(e -> {
            if (this.searchMode) {
                this.filterList.forEach(v -> v.selectedProperty().setValue(true));
                columnFilter.getFilterValues().stream().filter(v -> !this.filterList.stream().filter(fl -> fl.equals((Object)v)).findAny().isPresent()).forEach(v -> v.selectedProperty().setValue(false));
                this.resetSearchFilter();
            }
            if (columnFilter.getTableFilter().isDirty()) {
                columnFilter.applyFilter();
                columnFilter.getTableFilter().getColumnFilters().stream().map(ColumnFilter::getFilterPanel).forEach(fp -> {
                    if (!fp.columnFilter.hasUnselections()) {
                        fp.columnFilter.getTableColumn().setGraphic((Node)null);
                    }
                    else {
                        fp.columnFilter.getTableColumn().setGraphic((Node)FilterPanel.filterImageView.get());
                        if (!this.bumpedWidth) {
                            fp.columnFilter.getTableColumn().setPrefWidth(columnFilter.getTableColumn().getWidth() + 20.0);
                            this.bumpedWidth = true;
                        }
                    }
                    return;
                });
            }
            contextMenu.hide();
        });
        buttonBox.getChildren().add((Object)applyBttn);
        final Button unselectAllButton = new Button(Localization.getString("filterpanel.none.button"));
        HBox.setHgrow((Node)unselectAllButton, Priority.ALWAYS);
        unselectAllButton.setOnAction(e -> columnFilter.getFilterValues().forEach(v -> v.selectedProperty().set(false)));
        buttonBox.getChildren().add((Object)unselectAllButton);
        final Button selectAllButton = new Button(Localization.getString("filterpanel.all.button"));
        HBox.setHgrow((Node)selectAllButton, Priority.ALWAYS);
        selectAllButton.setOnAction(e -> columnFilter.getFilterValues().forEach(v -> v.selectedProperty().set(true)));
        buttonBox.getChildren().add((Object)selectAllButton);
        final Button clearAllButton = new Button(Localization.getString("filterpanel.resetall.button"));
        HBox.setHgrow((Node)clearAllButton, Priority.ALWAYS);
        clearAllButton.setOnAction(e -> {
            columnFilter.resetAllFilters();
            columnFilter.getTableFilter().getColumnFilters().stream().forEach(cf -> cf.getTableColumn().setGraphic((Node)null));
            contextMenu.hide();
        });
        buttonBox.getChildren().add((Object)clearAllButton);
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        this.getChildren().add((Object)buttonBox);
    }
    
    public void resetSearchFilter() {
        this.filterList.setPredicate(t -> true);
        this.searchBox.clear();
    }
    
    public static <T, R> CustomMenuItem getInMenuItem(final ColumnFilter<T, R> columnFilter, final ContextMenu contextMenu) {
        final FilterPanel<T, R> filterPanel = new FilterPanel<T, R>(columnFilter, contextMenu);
        final CustomMenuItem menuItem = new CustomMenuItem();
        filterPanel.initializeListeners();
        menuItem.contentProperty().set((Object)filterPanel);
        columnFilter.getTableFilter().getTableView().skinProperty().addListener((ChangeListener)new WeakChangeListener((ChangeListener)filterPanel.skinListener));
        menuItem.setHideOnClick(false);
        return menuItem;
    }
    
    private void initializeListeners() {
        this.searchBox.textProperty().addListener(l -> {
            this.searchMode = !this.searchBox.getText().isEmpty();
            this.filterList.setPredicate(val -> this.searchBox.getText().isEmpty() || this.columnFilter.getSearchStrategy().test(this.searchBox.getText(), Optional.ofNullable((Object)val.getValue()).map((Function<? super Object, ?>)Object::toString).orElse("")));
        });
    }
    
    private static void checkChangeContextMenu(final TableViewSkin<?> skin, final TableColumn<?, ?> column, final FilterPanel filterPanel) {
        final NestedTableColumnHeader header = skin.getTableHeaderRow().getRootHeader();
        final InvalidationListener listener = filterPanel.getOrCreateChangeListener(header, column);
        header.getColumnHeaders().addListener((InvalidationListener)new WeakInvalidationListener(listener));
        changeContextMenu(header, column);
    }
    
    private InvalidationListener getOrCreateChangeListener(final NestedTableColumnHeader header, final TableColumn<?, ?> column) {
        final InvalidationListener listener = obs -> changeContextMenu(header, column);
        this.columnHeadersChangeListeners.add(listener);
        return listener;
    }
    
    private static void changeContextMenu(final NestedTableColumnHeader header, final TableColumn<?, ?> column) {
        final TableColumnHeader headerSkin = scan(column, (TableColumnHeader)header);
        if (headerSkin != null) {
            headerSkin.setOnContextMenuRequested(ev -> {
                final ContextMenu cMenu = column.getContextMenu();
                if (cMenu != null) {
                    cMenu.show((Node)headerSkin, Side.BOTTOM, 5.0, 5.0);
                }
                ev.consume();
            });
        }
    }
    
    private static TableColumnHeader scan(final TableColumn<?, ?> search, final TableColumnHeader header) {
        if (search.equals(header.getTableColumn())) {
            return header;
        }
        if (header instanceof NestedTableColumnHeader) {
            final NestedTableColumnHeader parent = (NestedTableColumnHeader)header;
            for (int i = 0; i < parent.getColumnHeaders().size(); ++i) {
                final TableColumnHeader result = scan(search, (TableColumnHeader)parent.getColumnHeaders().get(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    public ColumnFilter<T, R> getColumnFilter() {
        return this.columnFilter;
    }
    
    static {
        filterIcon = new Image("/impl/org/controlsfx/table/filter.png");
        final ImageView imageView;
        filterImageView = (() -> {
            imageView = new ImageView(FilterPanel.filterIcon);
            imageView.setFitHeight(15.0);
            imageView.setPreserveRatio(true);
            return imageView;
        });
    }
}
