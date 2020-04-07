// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype;

public class DatatypeException extends Exception
{
    private final int index;
    public static final int UNKNOWN = -1;
    
    public DatatypeException(final int index, final String s) {
        super(s);
        this.index = index;
    }
    
    public DatatypeException(final String s) {
        this(-1, s);
    }
    
    public DatatypeException() {
        this(-1, null);
    }
    
    public int getIndex() {
        return this.index;
    }
}
