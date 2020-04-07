// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property.editor;

import javafx.application.Platform;
import java.lang.reflect.Constructor;
import java.util.Optional;
import org.controlsfx.dialog.FontSelectorDialog;
import javafx.scene.text.Font;
import java.time.LocalDate;
import javafx.scene.control.DatePicker;
import javafx.scene.paint.Color;
import javafx.scene.control.ColorPicker;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import java.util.Collection;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.CheckBox;
import java.lang.reflect.InvocationTargetException;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputControl;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet;

public class Editors
{
    public static final PropertyEditor<?> createTextEditor(final PropertySheet.Item property) {
        return new AbstractPropertyEditor<String, TextField>(property, new TextField()) {
            {
                enableAutoSelectAll(((AbstractPropertyEditor<T, TextInputControl>)this).getEditor());
            }
            
            protected StringProperty getObservableValue() {
                return ((AbstractPropertyEditor<T, TextField>)this).getEditor().textProperty();
            }
            
            @Override
            public void setValue(final String value) {
                ((AbstractPropertyEditor<T, TextField>)this).getEditor().setText(value);
            }
        };
    }
    
    public static final PropertyEditor<?> createNumericEditor(final PropertySheet.Item property) {
        return new AbstractPropertyEditor<Number, NumericField>(property, new NumericField((Class<? extends Number>)property.getType())) {
            private Class<? extends Number> sourceClass = property.getType();
            
            {
                enableAutoSelectAll(((AbstractPropertyEditor<T, TextInputControl>)this).getEditor());
            }
            
            @Override
            protected ObservableValue<Number> getObservableValue() {
                return ((AbstractPropertyEditor<T, NumericField>)this).getEditor().valueProperty();
            }
            
            @Override
            public Number getValue() {
                try {
                    return (Number)this.sourceClass.getConstructor(String.class).newInstance(((AbstractPropertyEditor<T, NumericField>)this).getEditor().getText());
                }
                catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex2) {
                    final Exception ex;
                    final Exception e = ex;
                    e.printStackTrace();
                    return null;
                }
            }
            
            @Override
            public void setValue(final Number value) {
                this.sourceClass = value.getClass();
                ((AbstractPropertyEditor<T, NumericField>)this).getEditor().setText(value.toString());
            }
        };
    }
    
    public static final PropertyEditor<?> createCheckEditor(final PropertySheet.Item property) {
        return new AbstractPropertyEditor<Boolean, CheckBox>(property, new CheckBox()) {
            protected BooleanProperty getObservableValue() {
                return ((AbstractPropertyEditor<T, CheckBox>)this).getEditor().selectedProperty();
            }
            
            @Override
            public void setValue(final Boolean value) {
                ((AbstractPropertyEditor<T, CheckBox>)this).getEditor().setSelected((boolean)value);
            }
        };
    }
    
    public static final <T> PropertyEditor<?> createChoiceEditor(final PropertySheet.Item property, final Collection<T> choices) {
        return new AbstractPropertyEditor<T, ComboBox<T>>(property, new ComboBox()) {
            {
                ((AbstractPropertyEditor<T, ComboBox>)this).getEditor().setItems(FXCollections.observableArrayList(choices));
            }
            
            @Override
            protected ObservableValue<T> getObservableValue() {
                return (ObservableValue<T>)((AbstractPropertyEditor<T, ComboBox>)this).getEditor().getSelectionModel().selectedItemProperty();
            }
            
            @Override
            public void setValue(final T value) {
                ((AbstractPropertyEditor<T, ComboBox>)this).getEditor().getSelectionModel().select((Object)value);
            }
        };
    }
    
    public static final PropertyEditor<?> createColorEditor(final PropertySheet.Item property) {
        return new AbstractPropertyEditor<Color, ColorPicker>(property, new ColorPicker()) {
            @Override
            protected ObservableValue<Color> getObservableValue() {
                return (ObservableValue<Color>)((AbstractPropertyEditor<T, ColorPicker>)this).getEditor().valueProperty();
            }
            
            @Override
            public void setValue(final Color value) {
                ((AbstractPropertyEditor<T, ColorPicker>)this).getEditor().setValue((Object)value);
            }
        };
    }
    
    public static final PropertyEditor<?> createDateEditor(final PropertySheet.Item property) {
        return new AbstractPropertyEditor<LocalDate, DatePicker>(property, new DatePicker()) {
            @Override
            protected ObservableValue<LocalDate> getObservableValue() {
                return (ObservableValue<LocalDate>)((AbstractPropertyEditor<T, DatePicker>)this).getEditor().valueProperty();
            }
            
            @Override
            public void setValue(final LocalDate value) {
                ((AbstractPropertyEditor<T, DatePicker>)this).getEditor().setValue((Object)value);
            }
        };
    }
    
    public static final PropertyEditor<?> createFontEditor(final PropertySheet.Item property) {
        return new AbstractPropertyEditor<Font, AbstractObjectField<Font>>(property, new AbstractObjectField<Font>() {
            @Override
            protected Class<Font> getType() {
                return Font.class;
            }
            
            @Override
            protected String objectToString(final Font font) {
                return (font == null) ? "" : String.format("%s, %.1f", font.getName(), font.getSize());
            }
            
            @Override
            protected Font edit(final Font font) {
                final FontSelectorDialog dlg = new FontSelectorDialog(font);
                final Optional<Font> optionalFont = (Optional<Font>)dlg.showAndWait();
                return optionalFont.orElse(null);
            }
        }) {
            @Override
            protected ObservableValue<Font> getObservableValue() {
                return (ObservableValue<Font>)((AbstractPropertyEditor<T, AbstractObjectField>)this).getEditor().getObjectProperty();
            }
            
            @Override
            public void setValue(final Font value) {
                ((AbstractPropertyEditor<T, AbstractObjectField>)this).getEditor().getObjectProperty().set((Object)value);
            }
        };
    }
    
    public static final Optional<PropertyEditor<?>> createCustomEditor(final PropertySheet.Item property) {
        Constructor<?> cn;
        final Exception ex2;
        Exception ex;
        return property.getPropertyEditorClass().map(cls -> {
            try {
                cn = cls.getConstructor(PropertySheet.Item.class);
                if (cn != null) {
                    return (PropertyEditor)cn.newInstance(property);
                }
            }
            catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex3) {
                ex = ex2;
                ex.printStackTrace();
            }
            return null;
        });
    }
    
    private static void enableAutoSelectAll(final TextInputControl control) {
        control.focusedProperty().addListener((o, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> control.selectAll());
            }
        });
    }
}
