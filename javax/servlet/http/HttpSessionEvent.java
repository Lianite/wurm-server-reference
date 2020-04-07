// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.util.EventObject;

public class HttpSessionEvent extends EventObject
{
    public HttpSessionEvent(final HttpSession source) {
        super(source);
    }
    
    public HttpSession getSession() {
        return (HttpSession)super.getSource();
    }
}
