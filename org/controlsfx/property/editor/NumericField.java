// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property.editor;

import javafx.beans.binding.NumberExpression;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.IndexRange;
import javafx.beans.value.ObservableValue;
import java.math.BigInteger;
import javafx.scene.control.TextField;

class NumericField extends TextField
{
    private final NumericValidator<? extends Number> value;
    
    public NumericField(final Class<? extends Number> cls) {
        if (cls == Byte.TYPE || cls == Byte.class || cls == Short.TYPE || cls == Short.class || cls == Integer.TYPE || cls == Integer.class || cls == Long.TYPE || cls == Long.class || cls == BigInteger.class) {
            this.value = new LongValidator(this);
        }
        else {
            this.value = new DoubleValidator(this);
        }
        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                this.value.setValue((Number)this.value.toNumber(this.getText()));
            }
        });
    }
    
    public final ObservableValue<Number> valueProperty() {
        return (ObservableValue<Number>)this.value;
    }
    
    public void replaceText(final int start, final int end, final String text) {
        if (this.replaceValid(start, end, text)) {
            super.replaceText(start, end, text);
        }
    }
    
    public void replaceSelection(final String text) {
        final IndexRange range = this.getSelection();
        if (this.replaceValid(range.getStart(), range.getEnd(), text)) {
            super.replaceSelection(text);
        }
    }
    
    private Boolean replaceValid(final int start, final int end, final String fragment) {
        try {
            final String newText = this.getText().substring(0, start) + fragment + this.getText().substring(end);
            if (newText.isEmpty()) {
                return true;
            }
            this.value.toNumber(newText);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }
    
    static class DoubleValidator extends SimpleDoubleProperty implements NumericValidator<Double>
    {
        private NumericField field;
        
        public DoubleValidator(final NumericField field) {
            super((Object)field, "value", 0.0);
            this.field = field;
        }
        
        protected void invalidated() {
            this.field.setText(Double.toString(this.get()));
        }
        
        public Double toNumber(final String s) {
            if (s == null || s.trim().isEmpty()) {
                return 0.0;
            }
            final String d = s.trim();
            if (d.endsWith("f") || d.endsWith("d") || d.endsWith("F") || d.endsWith("D")) {
                throw new NumberFormatException("There should be no alpha symbols");
            }
            return new Double(d);
        }
    }
    
    static class LongValidator extends SimpleLongProperty implements NumericValidator<Long>
    {
        private NumericField field;
        
        public LongValidator(final NumericField field) {
            super((Object)field, "value", 0L);
            this.field = field;
        }
        
        protected void invalidated() {
            this.field.setText(Long.toString(this.get()));
        }
        
        public Long toNumber(final String s) {
            if (s == null || s.trim().isEmpty()) {
                return 0L;
            }
            final String d = s.trim();
            return new Long(d);
        }
    }
    
    private interface NumericValidator<T extends Number> extends NumberExpression
    {
        void setValue(final Number p0);
        
        T toNumber(final String p0);
    }
}
