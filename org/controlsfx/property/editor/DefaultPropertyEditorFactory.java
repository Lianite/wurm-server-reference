// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.property.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import javafx.scene.text.Font;
import java.util.Collection;
import java.util.Arrays;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import org.controlsfx.control.PropertySheet;
import javafx.util.Callback;

public class DefaultPropertyEditorFactory implements Callback<PropertySheet.Item, PropertyEditor<?>>
{
    private static Class<?>[] numericTypes;
    
    public PropertyEditor<?> call(final PropertySheet.Item item) {
        final Class<?> type = item.getType();
        if (item.getPropertyEditorClass().isPresent()) {
            final Optional<PropertyEditor<?>> ed = Editors.createCustomEditor(item);
            if (ed.isPresent()) {
                return ed.get();
            }
        }
        if (type == String.class) {
            return Editors.createTextEditor(item);
        }
        if (isNumber(type)) {
            return Editors.createNumericEditor(item);
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            return Editors.createCheckEditor(item);
        }
        if (type == LocalDate.class) {
            return Editors.createDateEditor(item);
        }
        if (type == Color.class || type == Paint.class) {
            return Editors.createColorEditor(item);
        }
        if (type != null && type.isEnum()) {
            return Editors.createChoiceEditor(item, Arrays.asList(type.getEnumConstants()));
        }
        if (type == Font.class) {
            return Editors.createFontEditor(item);
        }
        return null;
    }
    
    private static boolean isNumber(final Class<?> type) {
        if (type == null) {
            return false;
        }
        for (final Class<?> cls : DefaultPropertyEditorFactory.numericTypes) {
            if (type == cls) {
                return true;
            }
        }
        return false;
    }
    
    static {
        DefaultPropertyEditorFactory.numericTypes = (Class<?>[])new Class[] { Byte.TYPE, Byte.class, Short.TYPE, Short.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Float.TYPE, Float.class, Double.TYPE, Double.class, BigInteger.class, BigDecimal.class };
    }
}
