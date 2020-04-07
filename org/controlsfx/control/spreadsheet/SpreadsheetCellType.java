// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import javafx.util.converter.IntegerStringConverter;
import java.text.DecimalFormat;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.DefaultStringConverter;
import java.util.List;
import javafx.util.StringConverter;

public abstract class SpreadsheetCellType<T>
{
    protected StringConverter<T> converter;
    public static final SpreadsheetCellType<Object> OBJECT;
    public static final StringType STRING;
    public static final DoubleType DOUBLE;
    public static final IntegerType INTEGER;
    public static final DateType DATE;
    
    public SpreadsheetCellType() {
    }
    
    public SpreadsheetCellType(final StringConverter<T> converter) {
        this.converter = converter;
    }
    
    public abstract SpreadsheetCellEditor createEditor(final SpreadsheetView p0);
    
    public String toString(final T object, final String format) {
        return this.toString(object);
    }
    
    public abstract String toString(final T p0);
    
    public abstract boolean match(final Object p0);
    
    public boolean isError(final Object value) {
        return false;
    }
    
    public boolean acceptDrop() {
        return false;
    }
    
    public abstract T convertValue(final Object p0);
    
    public static final ListType LIST(final List<String> items) {
        return new ListType(items);
    }
    
    static {
        OBJECT = new ObjectType();
        STRING = new StringType();
        DOUBLE = new DoubleType();
        INTEGER = new IntegerType();
        DATE = new DateType();
    }
    
    public static class ObjectType extends SpreadsheetCellType<Object>
    {
        public ObjectType() {
            this(new StringConverterWithFormat<Object>() {
                public Object fromString(final String arg0) {
                    return arg0;
                }
                
                public String toString(final Object arg0) {
                    return (arg0 == null) ? "" : arg0.toString();
                }
            });
        }
        
        public ObjectType(final StringConverterWithFormat<Object> converter) {
            super(converter);
        }
        
        @Override
        public String toString() {
            return "object";
        }
        
        @Override
        public boolean match(final Object value) {
            return true;
        }
        
        public SpreadsheetCell createCell(final int row, final int column, final int rowSpan, final int columnSpan, final Object value) {
            final SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
            cell.setItem(value);
            return cell;
        }
        
        @Override
        public SpreadsheetCellEditor createEditor(final SpreadsheetView view) {
            return new SpreadsheetCellEditor.ObjectEditor(view);
        }
        
        @Override
        public Object convertValue(final Object value) {
            return value;
        }
        
        @Override
        public String toString(final Object item) {
            return this.converter.toString(item);
        }
    }
    
    public static class StringType extends SpreadsheetCellType<String>
    {
        public StringType() {
            this((StringConverter<String>)new DefaultStringConverter());
        }
        
        public StringType(final StringConverter<String> converter) {
            super(converter);
        }
        
        @Override
        public String toString() {
            return "string";
        }
        
        @Override
        public boolean match(final Object value) {
            return true;
        }
        
        public SpreadsheetCell createCell(final int row, final int column, final int rowSpan, final int columnSpan, final String value) {
            final SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
            cell.setItem(value);
            return cell;
        }
        
        @Override
        public SpreadsheetCellEditor createEditor(final SpreadsheetView view) {
            return new SpreadsheetCellEditor.StringEditor(view);
        }
        
        @Override
        public String convertValue(final Object value) {
            final String convertedValue = (String)this.converter.fromString((value == null) ? null : value.toString());
            if (convertedValue == null || convertedValue.equals("")) {
                return null;
            }
            return convertedValue;
        }
        
        @Override
        public String toString(final String item) {
            return this.converter.toString((Object)item);
        }
    }
    
    public static class DoubleType extends SpreadsheetCellType<Double>
    {
        public DoubleType() {
            this(new StringConverterWithFormat<Double>(new DoubleStringConverter()) {
                public String toString(final Double item) {
                    return this.toStringFormat(item, "");
                }
                
                public Double fromString(final String str) {
                    if (str == null || str.isEmpty() || "NaN".equals(str)) {
                        return Double.NaN;
                    }
                    return (Double)this.myConverter.fromString(str);
                }
                
                @Override
                public String toStringFormat(final Double item, final String format) {
                    try {
                        if (item == null || Double.isNaN(item)) {
                            return "";
                        }
                        return new DecimalFormat(format).format(item);
                    }
                    catch (Exception ex) {
                        return this.myConverter.toString((Object)item);
                    }
                }
            });
        }
        
        public DoubleType(final StringConverter<Double> converter) {
            super(converter);
        }
        
        @Override
        public String toString() {
            return "double";
        }
        
        public SpreadsheetCell createCell(final int row, final int column, final int rowSpan, final int columnSpan, final Double value) {
            final SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
            cell.setItem(value);
            return cell;
        }
        
        @Override
        public SpreadsheetCellEditor createEditor(final SpreadsheetView view) {
            return new SpreadsheetCellEditor.DoubleEditor(view);
        }
        
        @Override
        public boolean match(final Object value) {
            if (value instanceof Double) {
                return true;
            }
            try {
                this.converter.fromString((value == null) ? null : value.toString());
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public Double convertValue(final Object value) {
            if (value instanceof Double) {
                return (Double)value;
            }
            try {
                return (Double)this.converter.fromString((value == null) ? null : value.toString());
            }
            catch (Exception e) {
                return null;
            }
        }
        
        @Override
        public String toString(final Double item) {
            return this.converter.toString((Object)item);
        }
        
        @Override
        public String toString(final Double item, final String format) {
            return ((StringConverterWithFormat)this.converter).toStringFormat(item, format);
        }
    }
    
    public static class IntegerType extends SpreadsheetCellType<Integer>
    {
        public IntegerType() {
            this(new IntegerStringConverter() {
                public String toString(final Integer item) {
                    if (item == null || Double.isNaN(item)) {
                        return "";
                    }
                    return super.toString(item);
                }
                
                public Integer fromString(final String str) {
                    if (str == null || str.isEmpty() || "NaN".equals(str)) {
                        return null;
                    }
                    try {
                        final Double temp = Double.parseDouble(str);
                        return (int)(Object)temp;
                    }
                    catch (Exception e) {
                        return super.fromString(str);
                    }
                }
            });
        }
        
        public IntegerType(final IntegerStringConverter converter) {
            super((javafx.util.StringConverter<Object>)converter);
        }
        
        @Override
        public String toString() {
            return "Integer";
        }
        
        public SpreadsheetCell createCell(final int row, final int column, final int rowSpan, final int columnSpan, final Integer value) {
            final SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
            cell.setItem(value);
            return cell;
        }
        
        @Override
        public SpreadsheetCellEditor createEditor(final SpreadsheetView view) {
            return new SpreadsheetCellEditor.IntegerEditor(view);
        }
        
        @Override
        public boolean match(final Object value) {
            if (value instanceof Integer) {
                return true;
            }
            try {
                this.converter.fromString((value == null) ? null : value.toString());
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public Integer convertValue(final Object value) {
            if (value instanceof Integer) {
                return (Integer)value;
            }
            try {
                return (Integer)this.converter.fromString((value == null) ? null : value.toString());
            }
            catch (Exception e) {
                return null;
            }
        }
        
        @Override
        public String toString(final Integer item) {
            return this.converter.toString((Object)item);
        }
    }
    
    public static class ListType extends SpreadsheetCellType<String>
    {
        protected final List<String> items;
        
        public ListType(final List<String> items) {
            super((javafx.util.StringConverter<Object>)new DefaultStringConverter() {
                public String fromString(final String str) {
                    if (str != null && items.contains(str)) {
                        return str;
                    }
                    return null;
                }
            });
            this.items = items;
        }
        
        @Override
        public String toString() {
            return "list";
        }
        
        public SpreadsheetCell createCell(final int row, final int column, final int rowSpan, final int columnSpan, final String value) {
            final SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
            if (this.items != null && this.items.size() > 0) {
                if (value != null && this.items.contains(value)) {
                    cell.setItem(value);
                }
                else {
                    cell.setItem(this.items.get(0));
                }
            }
            return cell;
        }
        
        @Override
        public SpreadsheetCellEditor createEditor(final SpreadsheetView view) {
            return new SpreadsheetCellEditor.ListEditor<Object>(view, this.items);
        }
        
        @Override
        public boolean match(final Object value) {
            return (value instanceof String && this.items.contains(value.toString())) || this.items.contains((value == null) ? null : value.toString());
        }
        
        @Override
        public String convertValue(final Object value) {
            return (String)this.converter.fromString((value == null) ? null : value.toString());
        }
        
        @Override
        public String toString(final String item) {
            return this.converter.toString((Object)item);
        }
    }
    
    public static class DateType extends SpreadsheetCellType<LocalDate>
    {
        public DateType() {
            this(new StringConverterWithFormat<LocalDate>() {
                public String toString(final LocalDate item) {
                    return this.toStringFormat(item, "");
                }
                
                public LocalDate fromString(final String str) {
                    try {
                        return LocalDate.parse(str);
                    }
                    catch (Exception e) {
                        return null;
                    }
                }
                
                @Override
                public String toStringFormat(final LocalDate item, final String format) {
                    if ("".equals(format) && item != null) {
                        return item.toString();
                    }
                    if (item != null) {
                        return item.format(DateTimeFormatter.ofPattern(format));
                    }
                    return "";
                }
            });
        }
        
        public DateType(final StringConverter<LocalDate> converter) {
            super(converter);
        }
        
        @Override
        public String toString() {
            return "date";
        }
        
        public SpreadsheetCell createCell(final int row, final int column, final int rowSpan, final int columnSpan, final LocalDate value) {
            final SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
            cell.setItem(value);
            return cell;
        }
        
        @Override
        public SpreadsheetCellEditor createEditor(final SpreadsheetView view) {
            return new SpreadsheetCellEditor.DateEditor(view, (StringConverter<LocalDate>)this.converter);
        }
        
        @Override
        public boolean match(final Object value) {
            if (value instanceof LocalDate) {
                return true;
            }
            try {
                final LocalDate temp = (LocalDate)this.converter.fromString((value == null) ? null : value.toString());
                return temp != null;
            }
            catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public LocalDate convertValue(final Object value) {
            if (value instanceof LocalDate) {
                return (LocalDate)value;
            }
            try {
                return (LocalDate)this.converter.fromString((value == null) ? null : value.toString());
            }
            catch (Exception e) {
                return null;
            }
        }
        
        @Override
        public String toString(final LocalDate item) {
            return this.converter.toString((Object)item);
        }
        
        @Override
        public String toString(final LocalDate item, final String format) {
            return ((StringConverterWithFormat)this.converter).toStringFormat(item, format);
        }
    }
}
