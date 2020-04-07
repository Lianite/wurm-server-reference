// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

public class HttpSessionBindingEvent extends HttpSessionEvent
{
    private String name;
    private Object value;
    
    public HttpSessionBindingEvent(final HttpSession session, final String name) {
        super(session);
        this.name = name;
    }
    
    public HttpSessionBindingEvent(final HttpSession session, final String name, final Object value) {
        super(session);
        this.name = name;
        this.value = value;
    }
    
    public HttpSession getSession() {
        return super.getSession();
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
}
