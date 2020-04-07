// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.json;

public class JSONException extends RuntimeException
{
    private Throwable cause;
    
    public JSONException(final String message) {
        super(message);
    }
    
    public JSONException(final Throwable t) {
        super(t.getMessage());
        this.cause = t;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
