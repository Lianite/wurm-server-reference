// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.controlpoint;

import org.fourthline.cling.protocol.SendingSync;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.protocol.sync.SendingAction;
import java.net.URL;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.action.ActionInvocation;

public abstract class ActionCallback implements Runnable
{
    protected final ActionInvocation actionInvocation;
    protected ControlPoint controlPoint;
    
    protected ActionCallback(final ActionInvocation actionInvocation, final ControlPoint controlPoint) {
        this.actionInvocation = actionInvocation;
        this.controlPoint = controlPoint;
    }
    
    protected ActionCallback(final ActionInvocation actionInvocation) {
        this.actionInvocation = actionInvocation;
    }
    
    public ActionInvocation getActionInvocation() {
        return this.actionInvocation;
    }
    
    public synchronized ControlPoint getControlPoint() {
        return this.controlPoint;
    }
    
    public synchronized ActionCallback setControlPoint(final ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
        return this;
    }
    
    @Override
    public void run() {
        final Service service = this.actionInvocation.getAction().getService();
        if (service instanceof LocalService) {
            final LocalService localService = (LocalService)service;
            localService.getExecutor(this.actionInvocation.getAction()).execute(this.actionInvocation);
            if (this.actionInvocation.getFailure() != null) {
                this.failure(this.actionInvocation, null);
            }
            else {
                this.success(this.actionInvocation);
            }
        }
        else if (service instanceof RemoteService) {
            if (this.getControlPoint() == null) {
                throw new IllegalStateException("Callback must be executed through ControlPoint");
            }
            final RemoteService remoteService = (RemoteService)service;
            URL controLURL;
            try {
                controLURL = ((Service<RemoteDevice, S>)remoteService).getDevice().normalizeURI(remoteService.getControlURI());
            }
            catch (IllegalArgumentException e) {
                this.failure(this.actionInvocation, null, "bad control URL: " + remoteService.getControlURI());
                return;
            }
            final SendingAction prot = this.getControlPoint().getProtocolFactory().createSendingAction(this.actionInvocation, controLURL);
            prot.run();
            final IncomingActionResponseMessage response = ((SendingSync<IN, IncomingActionResponseMessage>)prot).getOutputMessage();
            if (response == null) {
                this.failure(this.actionInvocation, null);
            }
            else if (response.getOperation().isFailed()) {
                this.failure(this.actionInvocation, response.getOperation());
            }
            else {
                this.success(this.actionInvocation);
            }
        }
    }
    
    protected String createDefaultFailureMessage(final ActionInvocation invocation, final UpnpResponse operation) {
        String message = "Error: ";
        final ActionException exception = invocation.getFailure();
        if (exception != null) {
            message += exception.getMessage();
        }
        if (operation != null) {
            message = message + " (HTTP response was: " + operation.getResponseDetails() + ")";
        }
        return message;
    }
    
    protected void failure(final ActionInvocation invocation, final UpnpResponse operation) {
        this.failure(invocation, operation, this.createDefaultFailureMessage(invocation, operation));
    }
    
    public abstract void success(final ActionInvocation p0);
    
    public abstract void failure(final ActionInvocation p0, final UpnpResponse p1, final String p2);
    
    @Override
    public String toString() {
        return "(ActionCallback) " + this.actionInvocation;
    }
    
    public static final class Default extends ActionCallback
    {
        public Default(final ActionInvocation actionInvocation, final ControlPoint controlPoint) {
            super(actionInvocation, controlPoint);
        }
        
        @Override
        public void success(final ActionInvocation invocation) {
        }
        
        @Override
        public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
        }
    }
}
