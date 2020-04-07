// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.xhtml;

import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Option
{
    private String key;
    private String[] values;
    
    public Option(final String key, final String[] values) {
        this.key = key;
        this.values = values;
    }
    
    public static Option[] fromString(final String string) {
        if (string == null || string.length() == 0) {
            return new Option[0];
        }
        final List<Option> options = new ArrayList<Option>();
        try {
            final String[] arr$;
            final String[] fields = arr$ = string.split(";");
            for (String field : arr$) {
                field = field.trim();
                if (field.contains(":")) {
                    final String[] keyValues = field.split(":");
                    if (keyValues.length == 2) {
                        final String key = keyValues[0].trim();
                        final String[] values = keyValues[1].split(",");
                        final List<String> cleanValues = new ArrayList<String>();
                        for (final String s : values) {
                            final String value = s.trim();
                            if (value.length() > 0) {
                                cleanValues.add(value);
                            }
                        }
                        options.add(new Option(key, cleanValues.toArray(new String[cleanValues.size()])));
                    }
                }
            }
            return options.toArray(new Option[options.size()]);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Can't parse options string: " + string, ex);
        }
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String[] getValues() {
        return this.values;
    }
    
    public boolean isTrue() {
        return this.getValues().length == 1 && this.getValues()[0].toLowerCase().equals("true");
    }
    
    public boolean isFalse() {
        return this.getValues().length == 1 && this.getValues()[0].toLowerCase().equals("false");
    }
    
    public String getFirstValue() {
        return this.getValues()[0];
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getKey()).append(": ");
        final Iterator<String> it = Arrays.asList(this.getValues()).iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Option that = (Option)o;
        return this.key.equals(that.key) && Arrays.equals(this.values, that.values);
    }
    
    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + Arrays.hashCode(this.values);
        return result;
    }
}
