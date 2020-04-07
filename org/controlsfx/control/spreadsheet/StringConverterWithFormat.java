// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.util.StringConverter;

public abstract class StringConverterWithFormat<T> extends StringConverter<T>
{
    protected StringConverter<T> myConverter;
    
    public StringConverterWithFormat() {
    }
    
    public StringConverterWithFormat(final StringConverter<T> specificStringConverter) {
        this.myConverter = specificStringConverter;
    }
    
    public String toStringFormat(final T value, final String format) {
        return this.toString((Object)value);
    }
}
