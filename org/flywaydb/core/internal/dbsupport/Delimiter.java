// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

public class Delimiter
{
    private final String delimiter;
    private final boolean aloneOnLine;
    
    public Delimiter(final String delimiter, final boolean aloneOnLine) {
        this.delimiter = delimiter;
        this.aloneOnLine = aloneOnLine;
    }
    
    public String getDelimiter() {
        return this.delimiter;
    }
    
    public boolean isAloneOnLine() {
        return this.aloneOnLine;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Delimiter delimiter1 = (Delimiter)o;
        return this.aloneOnLine == delimiter1.aloneOnLine && this.delimiter.equals(delimiter1.delimiter);
    }
    
    @Override
    public int hashCode() {
        int result = this.delimiter.hashCode();
        result = 31 * result + (this.aloneOnLine ? 1 : 0);
        return result;
    }
}
