// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class XMLStreamException2 extends XMLStreamException
{
    public XMLStreamException2(final String msg) {
        super(msg);
    }
    
    public XMLStreamException2(final Throwable th) {
        super(th);
    }
    
    public XMLStreamException2(final String msg, final Throwable th) {
        super(msg, th);
    }
    
    public XMLStreamException2(final String msg, final Location location) {
        super(msg, location);
    }
    
    public XMLStreamException2(final String msg, final Location location, final Throwable th) {
        super(msg, location, th);
    }
    
    public Throwable getCause() {
        return this.getNestedException();
    }
}
