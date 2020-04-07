// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.tools;

import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.scene.control.TableView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.Node;
import java.util.Iterator;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import javafx.scene.control.Control;
import java.util.function.Predicate;
import java.util.List;

public class ValueExtractor
{
    private static List<ObservableValueExtractor> extractors;
    private static final List<NodeValueExtractor> valueExtractors;
    
    public static void addObservableValueExtractor(final Predicate<Control> test, final Callback<Control, ObservableValue<?>> extract) {
        ValueExtractor.extractors.add(new ObservableValueExtractor(test, extract));
    }
    
    public static final Optional<Callback<Control, ObservableValue<?>>> getObservableValueExtractor(final Control c) {
        for (final ObservableValueExtractor e : ValueExtractor.extractors) {
            if (e.applicability.test(c)) {
                return Optional.of(e.extraction);
            }
        }
        return Optional.empty();
    }
    
    public static void addValueExtractor(final Predicate<Node> test, final Callback<Node, Object> extractor) {
        ValueExtractor.valueExtractors.add(new NodeValueExtractor(test, extractor));
    }
    
    public static Object getValue(final Node n) {
        for (final NodeValueExtractor nve : ValueExtractor.valueExtractors) {
            if (nve.applicability.test(n)) {
                return nve.extraction.call((Object)n);
            }
        }
        return null;
    }
    
    static {
        ValueExtractor.extractors = (List<ObservableValueExtractor>)FXCollections.observableArrayList();
        addObservableValueExtractor(c -> c instanceof TextInputControl, (Callback<Control, ObservableValue<?>>)(c -> ((TextInputControl)c).textProperty()));
        addObservableValueExtractor(c -> c instanceof ComboBox, (Callback<Control, ObservableValue<?>>)(c -> ((ComboBox)c).valueProperty()));
        addObservableValueExtractor(c -> c instanceof ChoiceBox, (Callback<Control, ObservableValue<?>>)(c -> ((ChoiceBox)c).valueProperty()));
        addObservableValueExtractor(c -> c instanceof CheckBox, (Callback<Control, ObservableValue<?>>)(c -> ((CheckBox)c).selectedProperty()));
        addObservableValueExtractor(c -> c instanceof Slider, (Callback<Control, ObservableValue<?>>)(c -> ((Slider)c).valueProperty()));
        addObservableValueExtractor(c -> c instanceof ColorPicker, (Callback<Control, ObservableValue<?>>)(c -> ((ColorPicker)c).valueProperty()));
        addObservableValueExtractor(c -> c instanceof DatePicker, (Callback<Control, ObservableValue<?>>)(c -> ((DatePicker)c).valueProperty()));
        addObservableValueExtractor(c -> c instanceof ListView, (Callback<Control, ObservableValue<?>>)(c -> ((ListView)c).itemsProperty()));
        addObservableValueExtractor(c -> c instanceof TableView, (Callback<Control, ObservableValue<?>>)(c -> ((TableView)c).itemsProperty()));
        valueExtractors = (List)FXCollections.observableArrayList();
        addValueExtractor(n -> n instanceof CheckBox, (Callback<Node, Object>)(cb -> ((CheckBox)cb).isSelected()));
        addValueExtractor(n -> n instanceof ChoiceBox, (Callback<Node, Object>)(cb -> ((ChoiceBox)cb).getValue()));
        addValueExtractor(n -> n instanceof ComboBox, (Callback<Node, Object>)(cb -> ((ComboBox)cb).getValue()));
        addValueExtractor(n -> n instanceof DatePicker, (Callback<Node, Object>)(dp -> ((DatePicker)dp).getValue()));
        addValueExtractor(n -> n instanceof RadioButton, (Callback<Node, Object>)(rb -> ((RadioButton)rb).isSelected()));
        addValueExtractor(n -> n instanceof Slider, (Callback<Node, Object>)(sl -> ((Slider)sl).getValue()));
        addValueExtractor(n -> n instanceof TextInputControl, (Callback<Node, Object>)(ta -> ((TextInputControl)ta).getText()));
        addValueExtractor(n -> n instanceof ListView, (Callback<Node, Object>)(lv -> {
            final MultipleSelectionModel<?> sm = (MultipleSelectionModel<?>)((ListView)lv).getSelectionModel();
            return (sm.getSelectionMode() == SelectionMode.MULTIPLE) ? sm.getSelectedItems() : sm.getSelectedItem();
        }));
        addValueExtractor(n -> n instanceof TreeView, (Callback<Node, Object>)(tv -> {
            final MultipleSelectionModel<?> sm = (MultipleSelectionModel<?>)((TreeView)tv).getSelectionModel();
            return (sm.getSelectionMode() == SelectionMode.MULTIPLE) ? sm.getSelectedItems() : sm.getSelectedItem();
        }));
        addValueExtractor(n -> n instanceof TableView, (Callback<Node, Object>)(tv -> {
            final MultipleSelectionModel<?> sm = (MultipleSelectionModel<?>)((TableView)tv).getSelectionModel();
            return (sm.getSelectionMode() == SelectionMode.MULTIPLE) ? sm.getSelectedItems() : sm.getSelectedItem();
        }));
        addValueExtractor(n -> n instanceof TreeTableView, (Callback<Node, Object>)(tv -> {
            final MultipleSelectionModel<?> sm = (MultipleSelectionModel<?>)((TreeTableView)tv).getSelectionModel();
            return (sm.getSelectionMode() == SelectionMode.MULTIPLE) ? sm.getSelectedItems() : sm.getSelectedItem();
        }));
    }
    
    private static class ObservableValueExtractor
    {
        public final Predicate<Control> applicability;
        public final Callback<Control, ObservableValue<?>> extraction;
        
        public ObservableValueExtractor(final Predicate<Control> applicability, final Callback<Control, ObservableValue<?>> extraction) {
            this.applicability = Objects.requireNonNull(applicability);
            this.extraction = Objects.requireNonNull(extraction);
        }
    }
    
    private static class NodeValueExtractor
    {
        public final Predicate<Node> applicability;
        public final Callback<Node, Object> extraction;
        
        public NodeValueExtractor(final Predicate<Node> applicability, final Callback<Node, Object> extraction) {
            this.applicability = Objects.requireNonNull(applicability);
            this.extraction = Objects.requireNonNull(extraction);
        }
    }
}
