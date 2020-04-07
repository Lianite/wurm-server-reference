// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.types.SoapActionType;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.SoapActionHeader;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.message.StreamRequestMessage;

public class IncomingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage
{
    private final Action action;
    private final String actionNamespace;
    
    public IncomingActionRequestMessage(final StreamRequestMessage source, final LocalService service) throws ActionException {
        super(source);
        final SoapActionHeader soapActionHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION, SoapActionHeader.class);
        if (soapActionHeader == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Missing SOAP action header");
        }
        final SoapActionType actionType = soapActionHeader.getValue();
        this.action = service.getAction(actionType.getActionName());
        if (this.action == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't implement action: " + actionType.getActionName());
        }
        if (!"QueryStateVariable".equals(actionType.getActionName()) && !service.getServiceType().implementsVersion(actionType.getServiceType())) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't support the requested service version");
        }
        this.actionNamespace = actionType.getTypeString();
    }
    
    public Action getAction() {
        return this.action;
    }
    
    @Override
    public String getActionNamespace() {
        return this.actionNamespace;
    }
}
