// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

public class XMLStreamException extends Exception
{
    protected Throwable nested;
    protected Location location;
    
    public XMLStreamException() {
    }
    
    public XMLStreamException(final String msg) {
        super(msg);
    }
    
    public XMLStreamException(final Throwable th) {
        this.nested = th;
    }
    
    public XMLStreamException(final String msg, final Throwable th) {
        super(msg);
        this.nested = th;
    }
    
    public XMLStreamException(final String msg, final Location location, final Throwable th) {
        super("ParseError at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]\n" + "Message: " + msg);
        this.nested = th;
        this.location = location;
    }
    
    public XMLStreamException(final String msg, final Location location) {
        super("ParseError at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]\n" + "Message: " + msg);
        this.location = location;
    }
    
    public Throwable getNestedException() {
        return this.nested;
    }
    
    public Location getLocation() {
        return this.location;
    }
}
