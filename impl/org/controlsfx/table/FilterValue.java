// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.table;

import javafx.scene.paint.Color;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.beans.property.Property;
import javafx.beans.WeakInvalidationListener;
import java.util.function.Function;
import java.util.Optional;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.HBox;

final class FilterValue<T, R> extends HBox implements Comparable<FilterValue<T, R>>
{
    private final R value;
    private final BooleanProperty isSelected;
    private final BooleanProperty inScope;
    private final ColumnFilter<T, R> columnFilter;
    private final InvalidationListener scopeListener;
    
    FilterValue(final R value, final ColumnFilter<T, R> columnFilter) {
        this.isSelected = (BooleanProperty)new SimpleBooleanProperty(true);
        this.inScope = (BooleanProperty)new SimpleBooleanProperty(true);
        this.value = value;
        this.columnFilter = columnFilter;
        final CheckBox checkBox = new CheckBox();
        final Label label = new Label();
        label.setText((String)Optional.ofNullable(value).map((Function<? super R, ? extends String>)Object::toString).orElse(null));
        this.scopeListener = (v -> label.textFillProperty().set((Object)(this.getInScopeProperty().get() ? Color.BLACK : Color.LIGHTGRAY)));
        this.inScope.addListener((InvalidationListener)new WeakInvalidationListener(this.scopeListener));
        checkBox.selectedProperty().bindBidirectional((Property)this.selectedProperty());
        this.getChildren().addAll((Object[])new Node[] { checkBox, label });
    }
    
    public R getValue() {
        return this.value;
    }
    
    public BooleanProperty selectedProperty() {
        return this.isSelected;
    }
    
    public BooleanProperty getInScopeProperty() {
        return this.inScope;
    }
    
    public void refreshScope() {
        this.inScope.setValue(this.columnFilter.wasLastFiltered() || this.columnFilter.valueIsVisible(this.value));
    }
    
    public String toString() {
        return Optional.ofNullable(this.value).map((Function<? super R, ? extends String>)Object::toString).orElse("");
    }
    
    public int compareTo(final FilterValue<T, R> other) {
        if (this.value != null && other.value != null && this.value instanceof Comparable && other.value instanceof Comparable) {
            return ((Comparable)this.value).compareTo(other.value);
        }
        return Optional.ofNullable(this.value).map((Function<? super R, ? extends String>)Object::toString).orElse("").compareTo((String)Optional.ofNullable(other).map((Function<? super FilterValue<T, R>, ? extends String>)Object::toString).orElse(""));
    }
}
