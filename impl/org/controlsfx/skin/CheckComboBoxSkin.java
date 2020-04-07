// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.collections.ListChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.Skin;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ComboBox;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.CheckComboBox;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class CheckComboBoxSkin<T> extends BehaviorSkinBase<CheckComboBox<T>, BehaviorBase<CheckComboBox<T>>>
{
    private final ComboBox<T> comboBox;
    private final ListCell<T> buttonCell;
    private final CheckComboBox<T> control;
    private final ObservableList<T> items;
    private final ReadOnlyUnbackedObservableList<Integer> selectedIndices;
    private final ReadOnlyUnbackedObservableList<T> selectedItems;
    
    public CheckComboBoxSkin(final CheckComboBox<T> control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.control = control;
        this.items = control.getItems();
        this.selectedIndices = (ReadOnlyUnbackedObservableList<Integer>)control.getCheckModel().getCheckedIndices();
        this.selectedItems = (ReadOnlyUnbackedObservableList<T>)control.getCheckModel().getCheckedItems();
        (this.comboBox = new ComboBox<T>(this.items) {
            protected Skin<?> createDefaultSkin() {
                return (Skin<?>)new ComboBoxListViewSkin<T>(this) {
                    protected boolean isHideOnClickEnabled() {
                        return false;
                    }
                };
            }
        }).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.comboBox.setCellFactory((Callback)new Callback<ListView<T>, ListCell<T>>() {
            public ListCell<T> call(final ListView<T> listView) {
                final CheckBoxListCell<T> result = (CheckBoxListCell<T>)new CheckBoxListCell(item -> control.getItemBooleanProperty(item));
                result.converterProperty().bind((ObservableValue)control.converterProperty());
                return (ListCell<T>)result;
            }
        });
        this.buttonCell = new ListCell<T>() {
            protected void updateItem(final T item, final boolean empty) {
                this.setText(CheckComboBoxSkin.this.buildString());
            }
        };
        this.comboBox.setButtonCell((ListCell)this.buttonCell);
        this.comboBox.setValue((Object)this.buildString());
        this.selectedIndices.addListener(c -> this.buttonCell.updateIndex(0));
        this.getChildren().add((Object)this.comboBox);
    }
    
    protected double computeMinWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return this.comboBox.minWidth(height);
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return this.comboBox.minHeight(width);
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return this.comboBox.prefWidth(height);
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return this.comboBox.prefHeight(width);
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((CheckComboBox)this.getSkinnable()).prefWidth(height);
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((CheckComboBox)this.getSkinnable()).prefHeight(width);
    }
    
    protected String buildString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0, max = this.selectedItems.size(); i < max; ++i) {
            final T item = (T)this.selectedItems.get(i);
            if (this.control.getConverter() == null) {
                sb.append(item);
            }
            else {
                sb.append(this.control.getConverter().toString((Object)item));
            }
            if (i < max - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
