// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.stream;

public class FactoryConfigurationError extends Error
{
    Exception nested;
    
    public FactoryConfigurationError() {
    }
    
    public FactoryConfigurationError(final Exception e) {
        this.nested = e;
    }
    
    public FactoryConfigurationError(final Exception e, final String msg) {
        super(msg);
        this.nested = e;
    }
    
    public FactoryConfigurationError(final String msg, final Exception e) {
        super(msg);
        this.nested = e;
    }
    
    public FactoryConfigurationError(final String msg) {
        super(msg);
    }
    
    public Exception getException() {
        return this.nested;
    }
    
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        }
        if (this.nested != null) {
            msg = this.nested.getMessage();
            if (msg == null) {
                msg = this.nested.getClass().toString();
            }
        }
        return msg;
    }
}
