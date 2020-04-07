// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

public class ServletContextAttributeEvent extends ServletContextEvent
{
    private String name;
    private Object value;
    
    public ServletContextAttributeEvent(final ServletContext source, final String name, final Object value) {
        super(source);
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
}
