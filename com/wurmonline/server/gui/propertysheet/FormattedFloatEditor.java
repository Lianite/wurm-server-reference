// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.gui.propertysheet;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

final class FormattedFloatEditor<T> implements PropertyEditor<T>
{
    PropertySheet.Item item;
    TextField textField;
    
    FormattedFloatEditor(final PropertySheet.Item item) {
        this.item = item;
        (this.textField = new TextField()).setOnAction(ae -> {
            item.setValue(this.getValue());
            this.textField.textProperty().setValue(this.getValue().toString());
        });
        this.textField.textProperty().addListener((ChangeListener)new ChangeListener<String>() {
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                if (newValue.length() > 0 && !newValue.matches("(^\\d*\\.?\\d*[0-9]+\\d*$)|(^[0-9]+\\d*\\.\\d*$)")) {
                    FormattedFloatEditor.this.textField.textProperty().setValue(oldValue);
                }
            }
        });
        this.textField.focusedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
                if (!newValue) {
                    System.out.println("Focus lost");
                    item.setValue(FormattedFloatEditor.this.getValue());
                    FormattedFloatEditor.this.textField.textProperty().setValue(FormattedFloatEditor.this.getValue().toString());
                }
            }
        });
    }
    
    @Override
    public Node getEditor() {
        return (Node)this.textField;
    }
    
    @Override
    public T getValue() {
        return (T)this.stringToObj(this.textField.getText(), this.item.getType());
    }
    
    @Override
    public void setValue(final T t) {
        this.textField.setText(objToString(t));
    }
    
    public static String objToString(final Object value) {
        return value.toString();
    }
    
    private Object stringToObj(final String str, final Class<?> cls) {
        try {
            if (str == null) {
                return null;
            }
            final String name = cls.getName();
            Object oMin = null;
            Object oMax = null;
            if (this.item instanceof ServerPropertySheet.CustomPropertyItem) {
                oMin = ((ServerPropertySheet.CustomPropertyItem)this.item).getMinValue();
                oMax = ((ServerPropertySheet.CustomPropertyItem)this.item).getMaxValue();
            }
            if (!name.equals("float") && !cls.equals(Float.class)) {
                return null;
            }
            final Float min = (Float)oMin;
            final Float max = (Float)oMax;
            if (str.length() == 0 || (str.length() == 1 && str.contains("."))) {
                return (min != null) ? min : new Float(0.0f);
            }
            final Float val = new Float(str);
            if (min != null && val < min) {
                return min;
            }
            if (max != null && val > max) {
                return max;
            }
            return val;
        }
        catch (Throwable t) {
            return null;
        }
    }
}
