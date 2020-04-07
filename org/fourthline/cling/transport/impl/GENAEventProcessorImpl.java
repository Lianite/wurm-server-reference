// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.fourthline.cling.model.meta.StateVariable;
import org.w3c.dom.NodeList;
import java.util.Iterator;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.model.state.StateVariableValue;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.fourthline.cling.model.UnsupportedDataException;
import java.util.logging.Level;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.fourthline.cling.transport.spi.GENAEventProcessor;

public class GENAEventProcessorImpl implements GENAEventProcessor, ErrorHandler
{
    private static Logger log;
    
    protected DocumentBuilderFactory createDocumentBuilderFactory() throws FactoryConfigurationError {
        return DocumentBuilderFactory.newInstance();
    }
    
    @Override
    public void writeBody(final OutgoingEventRequestMessage requestMessage) throws UnsupportedDataException {
        GENAEventProcessorImpl.log.fine("Writing body of: " + requestMessage);
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final Document d = factory.newDocumentBuilder().newDocument();
            final Element propertysetElement = this.writePropertysetElement(d);
            this.writeProperties(d, propertysetElement, requestMessage);
            requestMessage.setBody(UpnpMessage.BodyType.STRING, this.toString(d));
            if (GENAEventProcessorImpl.log.isLoggable(Level.FINER)) {
                GENAEventProcessorImpl.log.finer("===================================== GENA BODY BEGIN ============================================");
                GENAEventProcessorImpl.log.finer(requestMessage.getBody().toString());
                GENAEventProcessorImpl.log.finer("====================================== GENA BODY END =============================================");
            }
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public void readBody(final IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {
        GENAEventProcessorImpl.log.fine("Reading body of: " + requestMessage);
        if (GENAEventProcessorImpl.log.isLoggable(Level.FINER)) {
            GENAEventProcessorImpl.log.finer("===================================== GENA BODY BEGIN ============================================");
            GENAEventProcessorImpl.log.finer((requestMessage.getBody() != null) ? requestMessage.getBody().toString() : "null");
            GENAEventProcessorImpl.log.finer("-===================================== GENA BODY END ============================================");
        }
        final String body = this.getMessageBody(requestMessage);
        try {
            final DocumentBuilderFactory factory = this.createDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);
            final Document d = documentBuilder.parse(new InputSource(new StringReader(body)));
            final Element propertysetElement = this.readPropertysetElement(d);
            this.readProperties(propertysetElement, requestMessage);
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex.getMessage(), ex, body);
        }
    }
    
    protected Element writePropertysetElement(final Document d) {
        final Element propertysetElement = d.createElementNS("urn:schemas-upnp-org:event-1-0", "e:propertyset");
        d.appendChild(propertysetElement);
        return propertysetElement;
    }
    
    protected Element readPropertysetElement(final Document d) {
        final Element propertysetElement = d.getDocumentElement();
        if (propertysetElement == null || !this.getUnprefixedNodeName(propertysetElement).equals("propertyset")) {
            throw new RuntimeException("Root element was not 'propertyset'");
        }
        return propertysetElement;
    }
    
    protected void writeProperties(final Document d, final Element propertysetElement, final OutgoingEventRequestMessage message) {
        for (final StateVariableValue stateVariableValue : message.getStateVariableValues()) {
            final Element propertyElement = d.createElementNS("urn:schemas-upnp-org:event-1-0", "e:property");
            propertysetElement.appendChild(propertyElement);
            XMLUtil.appendNewElement(d, propertyElement, stateVariableValue.getStateVariable().getName(), stateVariableValue.toString());
        }
    }
    
    protected void readProperties(final Element propertysetElement, final IncomingEventRequestMessage message) {
        final NodeList propertysetElementChildren = propertysetElement.getChildNodes();
        final StateVariable[] stateVariables = message.getService().getStateVariables();
        for (int i = 0; i < propertysetElementChildren.getLength(); ++i) {
            final Node propertysetChild = propertysetElementChildren.item(i);
            if (propertysetChild.getNodeType() == 1) {
                if (this.getUnprefixedNodeName(propertysetChild).equals("property")) {
                    final NodeList propertyChildren = propertysetChild.getChildNodes();
                    for (int j = 0; j < propertyChildren.getLength(); ++j) {
                        final Node propertyChild = propertyChildren.item(j);
                        if (propertyChild.getNodeType() == 1) {
                            final String stateVariableName = this.getUnprefixedNodeName(propertyChild);
                            for (final StateVariable stateVariable : stateVariables) {
                                if (stateVariable.getName().equals(stateVariableName)) {
                                    GENAEventProcessorImpl.log.fine("Reading state variable value: " + stateVariableName);
                                    final String value = XMLUtil.getTextContent(propertyChild);
                                    message.getStateVariableValues().add(new StateVariableValue(stateVariable, value));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected String getMessageBody(final UpnpMessage message) throws UnsupportedDataException {
        if (!message.isBodyNonEmptyString()) {
            throw new UnsupportedDataException("Can't transform null or non-string/zero-length body of: " + message);
        }
        return message.getBodyString().trim();
    }
    
    protected String toString(final Document d) throws Exception {
        String output;
        for (output = XMLUtil.documentToString(d); output.endsWith("\n") || output.endsWith("\r"); output = output.substring(0, output.length() - 1)) {}
        return output;
    }
    
    protected String getUnprefixedNodeName(final Node node) {
        return (node.getPrefix() != null) ? node.getNodeName().substring(node.getPrefix().length() + 1) : node.getNodeName();
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        GENAEventProcessorImpl.log.warning(e.toString());
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    static {
        GENAEventProcessorImpl.log = Logger.getLogger(GENAEventProcessor.class.getName());
    }
}
