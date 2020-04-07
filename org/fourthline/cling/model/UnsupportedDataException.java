// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

public class UnsupportedDataException extends RuntimeException
{
    private static final long serialVersionUID = 661795454401413339L;
    protected Object data;
    
    public UnsupportedDataException(final String s) {
        super(s);
    }
    
    public UnsupportedDataException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
    
    public UnsupportedDataException(final String s, final Throwable throwable, final Object data) {
        super(s, throwable);
        this.data = data;
    }
    
    public Object getData() {
        return this.data;
    }
}
