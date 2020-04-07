// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.meta.StateVariable;
import org.xmlpull.v1.XmlPullParser;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.xml.XmlPullParserUtils;
import org.fourthline.cling.model.message.UpnpMessage;
import java.util.logging.Level;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;

@Alternative
public class PullGENAEventProcessorImpl extends GENAEventProcessorImpl
{
    private static Logger log;
    
    @Override
    public void readBody(final IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {
        PullGENAEventProcessorImpl.log.fine("Reading body of: " + requestMessage);
        if (PullGENAEventProcessorImpl.log.isLoggable(Level.FINER)) {
            PullGENAEventProcessorImpl.log.finer("===================================== GENA BODY BEGIN ============================================");
            PullGENAEventProcessorImpl.log.finer((requestMessage.getBody() != null) ? requestMessage.getBody().toString() : null);
            PullGENAEventProcessorImpl.log.finer("-===================================== GENA BODY END ============================================");
        }
        final String body = this.getMessageBody(requestMessage);
        try {
            final XmlPullParser xpp = XmlPullParserUtils.createParser(body);
            this.readProperties(xpp, requestMessage);
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex, body);
        }
    }
    
    protected void readProperties(final XmlPullParser xpp, final IncomingEventRequestMessage message) throws Exception {
        final StateVariable[] stateVariables = message.getService().getStateVariables();
        int event;
        while ((event = xpp.next()) != 1) {
            if (event != 2) {
                continue;
            }
            if (!xpp.getName().equals("property")) {
                continue;
            }
            this.readProperty(xpp, message, stateVariables);
        }
    }
    
    protected void readProperty(final XmlPullParser xpp, final IncomingEventRequestMessage message, final StateVariable[] stateVariables) throws Exception {
        int event;
        do {
            event = xpp.next();
            if (event == 2) {
                final String stateVariableName = xpp.getName();
                for (final StateVariable stateVariable : stateVariables) {
                    if (stateVariable.getName().equals(stateVariableName)) {
                        PullGENAEventProcessorImpl.log.fine("Reading state variable value: " + stateVariableName);
                        final String value = xpp.nextText();
                        message.getStateVariableValues().add(new StateVariableValue(stateVariable, value));
                        break;
                    }
                }
            }
        } while (event != 1 && (event != 3 || !xpp.getName().equals("property")));
    }
    
    static {
        PullGENAEventProcessorImpl.log = Logger.getLogger(GENAEventProcessor.class.getName());
    }
}
