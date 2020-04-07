// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionCancelledException;
import java.util.logging.Level;
import org.seamless.util.Exceptions;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.transport.RouterException;
import java.net.URL;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;
import org.fourthline.cling.model.message.control.OutgoingActionRequestMessage;
import org.fourthline.cling.protocol.SendingSync;

public class SendingAction extends SendingSync<OutgoingActionRequestMessage, IncomingActionResponseMessage>
{
    private static final Logger log;
    protected final ActionInvocation actionInvocation;
    
    public SendingAction(final UpnpService upnpService, final ActionInvocation actionInvocation, final URL controlURL) {
        super(upnpService, new OutgoingActionRequestMessage(actionInvocation, controlURL));
        this.actionInvocation = actionInvocation;
    }
    
    @Override
    protected IncomingActionResponseMessage executeSync() throws RouterException {
        return this.invokeRemote(((SendingSync<OutgoingActionRequestMessage, OUT>)this).getInputMessage());
    }
    
    protected IncomingActionResponseMessage invokeRemote(final OutgoingActionRequestMessage requestMessage) throws RouterException {
        final Device device = this.actionInvocation.getAction().getService().getDevice();
        SendingAction.log.fine("Sending outgoing action call '" + this.actionInvocation.getAction().getName() + "' to remote service of: " + device);
        IncomingActionResponseMessage responseMessage = null;
        try {
            final StreamResponseMessage streamResponse = this.sendRemoteRequest(requestMessage);
            if (streamResponse == null) {
                SendingAction.log.fine("No connection or no no response received, returning null");
                this.actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Connection error or no response received"));
                return null;
            }
            responseMessage = new IncomingActionResponseMessage(streamResponse);
            if (responseMessage.isFailedNonRecoverable()) {
                SendingAction.log.fine("Response was a non-recoverable failure: " + responseMessage);
                throw new ActionException(ErrorCode.ACTION_FAILED, "Non-recoverable remote execution failure: " + responseMessage.getOperation().getResponseDetails());
            }
            if (responseMessage.isFailedRecoverable()) {
                this.handleResponseFailure(responseMessage);
            }
            else {
                this.handleResponse(responseMessage);
            }
            return responseMessage;
        }
        catch (ActionException ex) {
            SendingAction.log.fine("Remote action invocation failed, returning Internal Server Error message: " + ex.getMessage());
            this.actionInvocation.setFailure(ex);
            if (responseMessage == null || !responseMessage.getOperation().isFailed()) {
                return new IncomingActionResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
            }
            return responseMessage;
        }
    }
    
    protected StreamResponseMessage sendRemoteRequest(final OutgoingActionRequestMessage requestMessage) throws ActionException, RouterException {
        try {
            SendingAction.log.fine("Writing SOAP request body of: " + requestMessage);
            this.getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(requestMessage, this.actionInvocation);
            SendingAction.log.fine("Sending SOAP body of message as stream to remote device");
            return this.getUpnpService().getRouter().send(requestMessage);
        }
        catch (RouterException ex) {
            final Throwable cause = Exceptions.unwrap(ex);
            if (cause instanceof InterruptedException) {
                if (SendingAction.log.isLoggable(Level.FINE)) {
                    SendingAction.log.fine("Sending action request message was interrupted: " + cause);
                }
                throw new ActionCancelledException((InterruptedException)cause);
            }
            throw ex;
        }
        catch (UnsupportedDataException ex2) {
            if (SendingAction.log.isLoggable(Level.FINE)) {
                SendingAction.log.fine("Error writing SOAP body: " + ex2);
                SendingAction.log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex2));
            }
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error writing request message. " + ex2.getMessage());
        }
    }
    
    protected void handleResponse(final IncomingActionResponseMessage responseMsg) throws ActionException {
        try {
            SendingAction.log.fine("Received response for outgoing call, reading SOAP response body: " + responseMsg);
            this.getUpnpService().getConfiguration().getSoapActionProcessor().readBody(responseMsg, this.actionInvocation);
        }
        catch (UnsupportedDataException ex) {
            SendingAction.log.fine("Error reading SOAP body: " + ex);
            SendingAction.log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error reading SOAP response message. " + ex.getMessage(), false);
        }
    }
    
    protected void handleResponseFailure(final IncomingActionResponseMessage responseMsg) throws ActionException {
        try {
            SendingAction.log.fine("Received response with Internal Server Error, reading SOAP failure message");
            this.getUpnpService().getConfiguration().getSoapActionProcessor().readBody(responseMsg, this.actionInvocation);
        }
        catch (UnsupportedDataException ex) {
            SendingAction.log.fine("Error reading SOAP body: " + ex);
            SendingAction.log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            throw new ActionException(ErrorCode.ACTION_FAILED, "Error reading SOAP response failure message. " + ex.getMessage(), false);
        }
    }
    
    static {
        log = Logger.getLogger(SendingAction.class.getName());
    }
}
