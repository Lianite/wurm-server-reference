// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

public class AsyncEvent
{
    private AsyncContext context;
    private ServletRequest request;
    private ServletResponse response;
    private Throwable throwable;
    
    public AsyncEvent(final AsyncContext context) {
        this(context, null, null, null);
    }
    
    public AsyncEvent(final AsyncContext context, final ServletRequest request, final ServletResponse response) {
        this(context, request, response, null);
    }
    
    public AsyncEvent(final AsyncContext context, final Throwable throwable) {
        this(context, null, null, throwable);
    }
    
    public AsyncEvent(final AsyncContext context, final ServletRequest request, final ServletResponse response, final Throwable throwable) {
        this.context = context;
        this.request = request;
        this.response = response;
        this.throwable = throwable;
    }
    
    public AsyncContext getAsyncContext() {
        return this.context;
    }
    
    public ServletRequest getSuppliedRequest() {
        return this.request;
    }
    
    public ServletResponse getSuppliedResponse() {
        return this.response;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
}
