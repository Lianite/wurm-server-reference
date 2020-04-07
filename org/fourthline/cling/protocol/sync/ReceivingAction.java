// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.types.ErrorCode;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionCancelledException;
import org.fourthline.cling.model.message.control.OutgoingActionResponseMessage;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import org.fourthline.cling.model.action.RemoteActionInvocation;
import org.fourthline.cling.model.message.control.IncomingActionRequestMessage;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.resource.ServiceControlResource;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ReceivingSync;

public class ReceivingAction extends ReceivingSync<StreamRequestMessage, StreamResponseMessage>
{
    private static final Logger log;
    
    public ReceivingAction(final UpnpService upnpService, final StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }
    
    @Override
    protected StreamResponseMessage executeSync() throws RouterException {
        final ContentTypeHeader contentTypeHeader = ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);
        if (contentTypeHeader != null && !contentTypeHeader.isUDACompliantXML()) {
            ReceivingAction.log.warning("Received invalid Content-Type '" + contentTypeHeader + "': " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE));
        }
        if (contentTypeHeader == null) {
            ReceivingAction.log.warning("Received without Content-Type: " + ((ReceivingAsync<Object>)this).getInputMessage());
        }
        final ServiceControlResource resource = this.getUpnpService().getRegistry().getResource(ServiceControlResource.class, ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        if (resource == null) {
            ReceivingAction.log.fine("No local resource found: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return null;
        }
        ReceivingAction.log.fine("Found local action resource matching relative request URI: " + ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getUri());
        OutgoingActionResponseMessage responseMessage = null;
        RemoteActionInvocation invocation;
        try {
            final IncomingActionRequestMessage requestMessage = new IncomingActionRequestMessage(((ReceivingAsync<StreamRequestMessage>)this).getInputMessage(), resource.getModel());
            ReceivingAction.log.finer("Created incoming action request message: " + requestMessage);
            invocation = new RemoteActionInvocation(requestMessage.getAction(), this.getRemoteClientInfo());
            ReceivingAction.log.fine("Reading body of request message");
            this.getUpnpService().getConfiguration().getSoapActionProcessor().readBody(requestMessage, invocation);
            ReceivingAction.log.fine("Executing on local service: " + invocation);
            resource.getModel().getExecutor(invocation.getAction()).execute(invocation);
            if (invocation.getFailure() == null) {
                responseMessage = new OutgoingActionResponseMessage(invocation.getAction());
            }
            else {
                if (invocation.getFailure() instanceof ActionCancelledException) {
                    ReceivingAction.log.fine("Action execution was cancelled, returning 404 to client");
                    return null;
                }
                responseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR, invocation.getAction());
            }
        }
        catch (ActionException ex) {
            ReceivingAction.log.finer("Error executing local action: " + ex);
            invocation = new RemoteActionInvocation(ex, this.getRemoteClientInfo());
            responseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        catch (UnsupportedDataException ex2) {
            ReceivingAction.log.log(Level.WARNING, "Error reading action request XML body: " + ex2.toString(), Exceptions.unwrap(ex2));
            invocation = new RemoteActionInvocation((Exceptions.unwrap(ex2) instanceof ActionException) ? ((ActionException)Exceptions.unwrap(ex2)) : new ActionException(ErrorCode.ACTION_FAILED, ex2.getMessage()), this.getRemoteClientInfo());
            responseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        try {
            ReceivingAction.log.fine("Writing body of response message");
            this.getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(responseMessage, invocation);
            ReceivingAction.log.fine("Returning finished response message: " + responseMessage);
            return responseMessage;
        }
        catch (UnsupportedDataException ex2) {
            ReceivingAction.log.warning("Failure writing body of response message, sending '500 Internal Server Error' without body");
            ReceivingAction.log.log(Level.WARNING, "Exception root cause: ", Exceptions.unwrap(ex2));
            return new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    static {
        log = Logger.getLogger(ReceivingAction.class.getName());
    }
}
