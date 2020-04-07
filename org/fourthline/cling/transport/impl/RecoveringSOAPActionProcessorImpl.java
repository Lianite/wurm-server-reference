// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.xml.XmlPullParserUtils;
import org.fourthline.cling.model.message.control.ActionMessage;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;

@Alternative
public class RecoveringSOAPActionProcessorImpl extends PullSOAPActionProcessorImpl
{
    private static Logger log;
    
    @Override
    public void readBody(final ActionRequestMessage requestMessage, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        try {
            super.readBody(requestMessage, actionInvocation);
        }
        catch (UnsupportedDataException ex) {
            if (!requestMessage.isBodyNonEmptyString()) {
                throw ex;
            }
            RecoveringSOAPActionProcessorImpl.log.warning("Trying to recover from invalid SOAP XML request: " + ex);
            final String body = this.getMessageBody(requestMessage);
            final String fixedBody = XmlPullParserUtils.fixXMLEntities(body);
            try {
                requestMessage.setBody(fixedBody);
                super.readBody(requestMessage, actionInvocation);
            }
            catch (UnsupportedDataException ex2) {
                this.handleInvalidMessage(actionInvocation, ex, ex2);
            }
        }
    }
    
    @Override
    public void readBody(final ActionResponseMessage responseMsg, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        try {
            super.readBody(responseMsg, actionInvocation);
        }
        catch (UnsupportedDataException ex) {
            if (!responseMsg.isBodyNonEmptyString()) {
                throw ex;
            }
            RecoveringSOAPActionProcessorImpl.log.warning("Trying to recover from invalid SOAP XML response: " + ex);
            final String body = this.getMessageBody(responseMsg);
            String fixedBody = XmlPullParserUtils.fixXMLEntities(body);
            if (fixedBody.endsWith("</s:Envelop")) {
                fixedBody += "e>";
            }
            try {
                responseMsg.setBody(fixedBody);
                super.readBody(responseMsg, actionInvocation);
            }
            catch (UnsupportedDataException ex2) {
                this.handleInvalidMessage(actionInvocation, ex, ex2);
            }
        }
    }
    
    protected void handleInvalidMessage(final ActionInvocation actionInvocation, final UnsupportedDataException originalException, final UnsupportedDataException recoveringException) throws UnsupportedDataException {
        throw originalException;
    }
    
    static {
        RecoveringSOAPActionProcessorImpl.log = Logger.getLogger(SOAPActionProcessor.class.getName());
    }
}
