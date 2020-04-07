// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.action;

import javafx.event.ActionEvent;
import java.util.Objects;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class AnnotatedAction extends Action
{
    private final Method method;
    private final WeakReference<Object> target;
    
    public AnnotatedAction(final String text, final Method method, final Object target) {
        super(text);
        Objects.requireNonNull(method);
        Objects.requireNonNull(target);
        this.setEventHandler(this::handleAction);
        (this.method = method).setAccessible(true);
        this.target = new WeakReference<Object>(target);
    }
    
    public Object getTarget() {
        return this.target.get();
    }
    
    protected void handleAction(final ActionEvent ae) {
        try {
            final Object actionTarget = this.getTarget();
            if (actionTarget == null) {
                throw new IllegalStateException("Action target object is no longer reachable");
            }
            final int paramCount = this.method.getParameterCount();
            if (paramCount == 0) {
                this.method.invoke(actionTarget, new Object[0]);
            }
            else if (paramCount == 1) {
                this.method.invoke(actionTarget, ae);
            }
            else if (paramCount == 2) {
                this.method.invoke(actionTarget, ae, this);
            }
        }
        catch (Throwable e) {
            this.handleActionException(ae, e);
        }
    }
    
    protected void handleActionException(final ActionEvent ae, final Throwable ex) {
        ex.printStackTrace();
    }
    
    @Override
    public String toString() {
        return this.getText();
    }
}
