// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.fourthline.cling.transport.spi.GENAEventProcessor;
import java.util.regex.Matcher;
import org.fourthline.cling.model.XMLUtil;
import java.util.regex.Pattern;
import org.fourthline.cling.model.UnsupportedDataException;
import org.seamless.xml.XmlPullParserUtils;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import java.util.logging.Logger;
import javax.enterprise.inject.Alternative;

@Alternative
public class RecoveringGENAEventProcessorImpl extends PullGENAEventProcessorImpl
{
    private static Logger log;
    
    @Override
    public void readBody(final IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {
        try {
            super.readBody(requestMessage);
        }
        catch (UnsupportedDataException ex) {
            if (!requestMessage.isBodyNonEmptyString()) {
                throw ex;
            }
            RecoveringGENAEventProcessorImpl.log.warning("Trying to recover from invalid GENA XML event: " + ex);
            requestMessage.getStateVariableValues().clear();
            final String body = this.getMessageBody(requestMessage);
            final String fixedBody = this.fixXMLEncodedLastChange(XmlPullParserUtils.fixXMLEntities(body));
            try {
                requestMessage.setBody(fixedBody);
                super.readBody(requestMessage);
            }
            catch (UnsupportedDataException ex2) {
                if (requestMessage.getStateVariableValues().isEmpty()) {
                    throw ex;
                }
                RecoveringGENAEventProcessorImpl.log.warning("Partial read of GENA event properties (probably due to truncated XML)");
            }
        }
    }
    
    protected String fixXMLEncodedLastChange(final String xml) {
        final Pattern pattern = Pattern.compile("<LastChange>(.*)</LastChange>", 32);
        final Matcher matcher = pattern.matcher(xml);
        if (!matcher.find() || matcher.groupCount() != 1) {
            return xml;
        }
        String lastChange = matcher.group(1);
        if (XmlPullParserUtils.isNullOrEmpty(lastChange)) {
            return xml;
        }
        String fixedLastChange;
        lastChange = (fixedLastChange = lastChange.trim());
        if (lastChange.charAt(0) == '<') {
            fixedLastChange = XMLUtil.encodeText(fixedLastChange);
        }
        if (fixedLastChange.equals(lastChange)) {
            return xml;
        }
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?><e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\"><e:property><LastChange>" + fixedLastChange + "</LastChange>" + "</e:property>" + "</e:propertyset>";
    }
    
    static {
        RecoveringGENAEventProcessorImpl.log = Logger.getLogger(GENAEventProcessor.class.getName());
    }
}
