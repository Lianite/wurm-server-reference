// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.message.header.SoapActionHeader;
import org.fourthline.cling.model.types.SoapActionType;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import java.util.List;
import java.util.Map;
import org.fourthline.cling.model.message.header.UserAgentHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.action.RemoteActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.message.UpnpRequest;
import java.net.URL;
import org.fourthline.cling.model.action.ActionInvocation;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class OutgoingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage
{
    private static Logger log;
    private final String actionNamespace;
    
    public OutgoingActionRequestMessage(final ActionInvocation actionInvocation, final URL controlURL) {
        this(actionInvocation.getAction(), new UpnpRequest(UpnpRequest.Method.POST, controlURL));
        if (actionInvocation instanceof RemoteActionInvocation) {
            final RemoteActionInvocation remoteActionInvocation = (RemoteActionInvocation)actionInvocation;
            if (remoteActionInvocation.getRemoteClientInfo() != null && remoteActionInvocation.getRemoteClientInfo().getRequestUserAgent() != null) {
                this.getHeaders().add(UpnpHeader.Type.USER_AGENT, new UserAgentHeader(remoteActionInvocation.getRemoteClientInfo().getRequestUserAgent()));
            }
        }
        else if (actionInvocation.getClientInfo() != null) {
            this.getHeaders().putAll(actionInvocation.getClientInfo().getRequestHeaders());
        }
    }
    
    public OutgoingActionRequestMessage(final Action action, final UpnpRequest operation) {
        super(operation);
        this.getHeaders().add(UpnpHeader.Type.CONTENT_TYPE, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8));
        SoapActionHeader soapActionHeader;
        if (action instanceof QueryStateVariableAction) {
            OutgoingActionRequestMessage.log.fine("Adding magic control SOAP action header for state variable query action");
            soapActionHeader = new SoapActionHeader(new SoapActionType("schemas-upnp-org", "control-1-0", null, action.getName()));
        }
        else {
            soapActionHeader = new SoapActionHeader(new SoapActionType(action.getService().getServiceType(), action.getName()));
        }
        this.actionNamespace = soapActionHeader.getValue().getTypeString();
        if (this.getOperation().getMethod().equals(UpnpRequest.Method.POST)) {
            this.getHeaders().add(UpnpHeader.Type.SOAPACTION, soapActionHeader);
            OutgoingActionRequestMessage.log.fine("Added SOAP action header: " + soapActionHeader);
            return;
        }
        throw new IllegalArgumentException("Can't send action with request method: " + this.getOperation().getMethod());
    }
    
    @Override
    public String getActionNamespace() {
        return this.actionNamespace;
    }
    
    static {
        OutgoingActionRequestMessage.log = Logger.getLogger(OutgoingActionRequestMessage.class.getName());
    }
}
