// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.impl;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.util.Iterator;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.XMLUtil;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.fourthline.cling.model.action.ActionException;
import javax.xml.parsers.DocumentBuilder;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.fourthline.cling.model.message.control.ActionMessage;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.fourthline.cling.model.UnsupportedDataException;
import java.util.logging.Level;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;

public class SOAPActionProcessorImpl implements SOAPActionProcessor, ErrorHandler
{
    private static Logger log;
    
    protected DocumentBuilderFactory createDocumentBuilderFactory() throws FactoryConfigurationError {
        return DocumentBuilderFactory.newInstance();
    }
    
    @Override
    public void writeBody(final ActionRequestMessage requestMessage, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        SOAPActionProcessorImpl.log.fine("Writing body of " + requestMessage + " for: " + actionInvocation);
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final Document d = factory.newDocumentBuilder().newDocument();
            final Element body = this.writeBodyElement(d);
            this.writeBodyRequest(d, body, requestMessage, actionInvocation);
            if (SOAPActionProcessorImpl.log.isLoggable(Level.FINER)) {
                SOAPActionProcessorImpl.log.finer("===================================== SOAP BODY BEGIN ============================================");
                SOAPActionProcessorImpl.log.finer(requestMessage.getBodyString());
                SOAPActionProcessorImpl.log.finer("-===================================== SOAP BODY END ============================================");
            }
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex);
        }
    }
    
    @Override
    public void writeBody(final ActionResponseMessage responseMessage, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        SOAPActionProcessorImpl.log.fine("Writing body of " + responseMessage + " for: " + actionInvocation);
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final Document d = factory.newDocumentBuilder().newDocument();
            final Element body = this.writeBodyElement(d);
            if (actionInvocation.getFailure() != null) {
                this.writeBodyFailure(d, body, responseMessage, actionInvocation);
            }
            else {
                this.writeBodyResponse(d, body, responseMessage, actionInvocation);
            }
            if (SOAPActionProcessorImpl.log.isLoggable(Level.FINER)) {
                SOAPActionProcessorImpl.log.finer("===================================== SOAP BODY BEGIN ============================================");
                SOAPActionProcessorImpl.log.finer(responseMessage.getBodyString());
                SOAPActionProcessorImpl.log.finer("-===================================== SOAP BODY END ============================================");
            }
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex);
        }
    }
    
    @Override
    public void readBody(final ActionRequestMessage requestMessage, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        SOAPActionProcessorImpl.log.fine("Reading body of " + requestMessage + " for: " + actionInvocation);
        if (SOAPActionProcessorImpl.log.isLoggable(Level.FINER)) {
            SOAPActionProcessorImpl.log.finer("===================================== SOAP BODY BEGIN ============================================");
            SOAPActionProcessorImpl.log.finer(requestMessage.getBodyString());
            SOAPActionProcessorImpl.log.finer("-===================================== SOAP BODY END ============================================");
        }
        final String body = this.getMessageBody(requestMessage);
        try {
            final DocumentBuilderFactory factory = this.createDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);
            final Document d = documentBuilder.parse(new InputSource(new StringReader(body)));
            final Element bodyElement = this.readBodyElement(d);
            this.readBodyRequest(d, bodyElement, requestMessage, actionInvocation);
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex, body);
        }
    }
    
    @Override
    public void readBody(final ActionResponseMessage responseMsg, final ActionInvocation actionInvocation) throws UnsupportedDataException {
        SOAPActionProcessorImpl.log.fine("Reading body of " + responseMsg + " for: " + actionInvocation);
        if (SOAPActionProcessorImpl.log.isLoggable(Level.FINER)) {
            SOAPActionProcessorImpl.log.finer("===================================== SOAP BODY BEGIN ============================================");
            SOAPActionProcessorImpl.log.finer(responseMsg.getBodyString());
            SOAPActionProcessorImpl.log.finer("-===================================== SOAP BODY END ============================================");
        }
        final String body = this.getMessageBody(responseMsg);
        try {
            final DocumentBuilderFactory factory = this.createDocumentBuilderFactory();
            factory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);
            final Document d = documentBuilder.parse(new InputSource(new StringReader(body)));
            final Element bodyElement = this.readBodyElement(d);
            final ActionException failure = this.readBodyFailure(d, bodyElement);
            if (failure == null) {
                this.readBodyResponse(d, bodyElement, responseMsg, actionInvocation);
            }
            else {
                actionInvocation.setFailure(failure);
            }
        }
        catch (Exception ex) {
            throw new UnsupportedDataException("Can't transform message payload: " + ex, ex, body);
        }
    }
    
    protected void writeBodyFailure(final Document d, final Element bodyElement, final ActionResponseMessage message, final ActionInvocation actionInvocation) throws Exception {
        this.writeFaultElement(d, bodyElement, actionInvocation);
        message.setBody(this.toString(d));
    }
    
    protected void writeBodyRequest(final Document d, final Element bodyElement, final ActionRequestMessage message, final ActionInvocation actionInvocation) throws Exception {
        final Element actionRequestElement = this.writeActionRequestElement(d, bodyElement, message, actionInvocation);
        this.writeActionInputArguments(d, actionRequestElement, actionInvocation);
        message.setBody(this.toString(d));
    }
    
    protected void writeBodyResponse(final Document d, final Element bodyElement, final ActionResponseMessage message, final ActionInvocation actionInvocation) throws Exception {
        final Element actionResponseElement = this.writeActionResponseElement(d, bodyElement, message, actionInvocation);
        this.writeActionOutputArguments(d, actionResponseElement, actionInvocation);
        message.setBody(this.toString(d));
    }
    
    protected ActionException readBodyFailure(final Document d, final Element bodyElement) throws Exception {
        return this.readFaultElement(bodyElement);
    }
    
    protected void readBodyRequest(final Document d, final Element bodyElement, final ActionRequestMessage message, final ActionInvocation actionInvocation) throws Exception {
        final Element actionRequestElement = this.readActionRequestElement(bodyElement, message, actionInvocation);
        this.readActionInputArguments(actionRequestElement, actionInvocation);
    }
    
    protected void readBodyResponse(final Document d, final Element bodyElement, final ActionResponseMessage message, final ActionInvocation actionInvocation) throws Exception {
        final Element actionResponse = this.readActionResponseElement(bodyElement, actionInvocation);
        this.readActionOutputArguments(actionResponse, actionInvocation);
    }
    
    protected Element writeBodyElement(final Document d) {
        final Element envelopeElement = d.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "s:Envelope");
        final Attr encodingStyleAttr = d.createAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "s:encodingStyle");
        encodingStyleAttr.setValue("http://schemas.xmlsoap.org/soap/encoding/");
        envelopeElement.setAttributeNode(encodingStyleAttr);
        d.appendChild(envelopeElement);
        final Element bodyElement = d.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "s:Body");
        envelopeElement.appendChild(bodyElement);
        return bodyElement;
    }
    
    protected Element readBodyElement(final Document d) {
        final Element envelopeElement = d.getDocumentElement();
        if (envelopeElement == null || !this.getUnprefixedNodeName(envelopeElement).equals("Envelope")) {
            throw new RuntimeException("Response root element was not 'Envelope'");
        }
        final NodeList envelopeElementChildren = envelopeElement.getChildNodes();
        for (int i = 0; i < envelopeElementChildren.getLength(); ++i) {
            final Node envelopeChild = envelopeElementChildren.item(i);
            if (envelopeChild.getNodeType() == 1) {
                if (this.getUnprefixedNodeName(envelopeChild).equals("Body")) {
                    return (Element)envelopeChild;
                }
            }
        }
        throw new RuntimeException("Response envelope did not contain 'Body' child element");
    }
    
    protected Element writeActionRequestElement(final Document d, final Element bodyElement, final ActionRequestMessage message, final ActionInvocation actionInvocation) {
        SOAPActionProcessorImpl.log.fine("Writing action request element: " + actionInvocation.getAction().getName());
        final Element actionRequestElement = d.createElementNS(message.getActionNamespace(), "u:" + actionInvocation.getAction().getName());
        bodyElement.appendChild(actionRequestElement);
        return actionRequestElement;
    }
    
    protected Element readActionRequestElement(final Element bodyElement, final ActionRequestMessage message, final ActionInvocation actionInvocation) {
        final NodeList bodyChildren = bodyElement.getChildNodes();
        SOAPActionProcessorImpl.log.fine("Looking for action request element matching namespace:" + message.getActionNamespace());
        for (int i = 0; i < bodyChildren.getLength(); ++i) {
            final Node bodyChild = bodyChildren.item(i);
            if (bodyChild.getNodeType() == 1) {
                final String unprefixedName = this.getUnprefixedNodeName(bodyChild);
                if (unprefixedName.equals(actionInvocation.getAction().getName())) {
                    if (bodyChild.getNamespaceURI() == null || !bodyChild.getNamespaceURI().equals(message.getActionNamespace())) {
                        throw new UnsupportedDataException("Illegal or missing namespace on action request element: " + bodyChild);
                    }
                    SOAPActionProcessorImpl.log.fine("Reading action request element: " + unprefixedName);
                    return (Element)bodyChild;
                }
            }
        }
        throw new UnsupportedDataException("Could not read action request element matching namespace: " + message.getActionNamespace());
    }
    
    protected Element writeActionResponseElement(final Document d, final Element bodyElement, final ActionResponseMessage message, final ActionInvocation actionInvocation) {
        SOAPActionProcessorImpl.log.fine("Writing action response element: " + actionInvocation.getAction().getName());
        final Element actionResponseElement = d.createElementNS(message.getActionNamespace(), "u:" + actionInvocation.getAction().getName() + "Response");
        bodyElement.appendChild(actionResponseElement);
        return actionResponseElement;
    }
    
    protected Element readActionResponseElement(final Element bodyElement, final ActionInvocation actionInvocation) {
        final NodeList bodyChildren = bodyElement.getChildNodes();
        for (int i = 0; i < bodyChildren.getLength(); ++i) {
            final Node bodyChild = bodyChildren.item(i);
            if (bodyChild.getNodeType() == 1) {
                if (this.getUnprefixedNodeName(bodyChild).equals(actionInvocation.getAction().getName() + "Response")) {
                    SOAPActionProcessorImpl.log.fine("Reading action response element: " + this.getUnprefixedNodeName(bodyChild));
                    return (Element)bodyChild;
                }
            }
        }
        SOAPActionProcessorImpl.log.fine("Could not read action response element");
        return null;
    }
    
    protected void writeActionInputArguments(final Document d, final Element actionRequestElement, final ActionInvocation actionInvocation) {
        for (final ActionArgument argument : actionInvocation.getAction().getInputArguments()) {
            SOAPActionProcessorImpl.log.fine("Writing action input argument: " + argument.getName());
            final String value = (actionInvocation.getInput(argument) != null) ? actionInvocation.getInput(argument).toString() : "";
            XMLUtil.appendNewElement(d, actionRequestElement, argument.getName(), value);
        }
    }
    
    public void readActionInputArguments(final Element actionRequestElement, final ActionInvocation actionInvocation) throws ActionException {
        actionInvocation.setInput(this.readArgumentValues(actionRequestElement.getChildNodes(), actionInvocation.getAction().getInputArguments()));
    }
    
    protected void writeActionOutputArguments(final Document d, final Element actionResponseElement, final ActionInvocation actionInvocation) {
        for (final ActionArgument argument : actionInvocation.getAction().getOutputArguments()) {
            SOAPActionProcessorImpl.log.fine("Writing action output argument: " + argument.getName());
            final String value = (actionInvocation.getOutput(argument) != null) ? actionInvocation.getOutput(argument).toString() : "";
            XMLUtil.appendNewElement(d, actionResponseElement, argument.getName(), value);
        }
    }
    
    protected void readActionOutputArguments(final Element actionResponseElement, final ActionInvocation actionInvocation) throws ActionException {
        actionInvocation.setOutput(this.readArgumentValues(actionResponseElement.getChildNodes(), actionInvocation.getAction().getOutputArguments()));
    }
    
    protected void writeFaultElement(final Document d, final Element bodyElement, final ActionInvocation actionInvocation) {
        final Element faultElement = d.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "s:Fault");
        bodyElement.appendChild(faultElement);
        XMLUtil.appendNewElement(d, faultElement, "faultcode", "s:Client");
        XMLUtil.appendNewElement(d, faultElement, "faultstring", "UPnPError");
        final Element detailElement = d.createElement("detail");
        faultElement.appendChild(detailElement);
        final Element upnpErrorElement = d.createElementNS("urn:schemas-upnp-org:control-1-0", "UPnPError");
        detailElement.appendChild(upnpErrorElement);
        final int errorCode = actionInvocation.getFailure().getErrorCode();
        final String errorDescription = actionInvocation.getFailure().getMessage();
        SOAPActionProcessorImpl.log.fine("Writing fault element: " + errorCode + " - " + errorDescription);
        XMLUtil.appendNewElement(d, upnpErrorElement, "errorCode", Integer.toString(errorCode));
        XMLUtil.appendNewElement(d, upnpErrorElement, "errorDescription", errorDescription);
    }
    
    protected ActionException readFaultElement(final Element bodyElement) {
        boolean receivedFaultElement = false;
        String errorCode = null;
        String errorDescription = null;
        final NodeList bodyChildren = bodyElement.getChildNodes();
        for (int i = 0; i < bodyChildren.getLength(); ++i) {
            final Node bodyChild = bodyChildren.item(i);
            if (bodyChild.getNodeType() == 1) {
                if (this.getUnprefixedNodeName(bodyChild).equals("Fault")) {
                    receivedFaultElement = true;
                    final NodeList faultChildren = bodyChild.getChildNodes();
                    for (int j = 0; j < faultChildren.getLength(); ++j) {
                        final Node faultChild = faultChildren.item(j);
                        if (faultChild.getNodeType() == 1) {
                            if (this.getUnprefixedNodeName(faultChild).equals("detail")) {
                                final NodeList detailChildren = faultChild.getChildNodes();
                                for (int x = 0; x < detailChildren.getLength(); ++x) {
                                    final Node detailChild = detailChildren.item(x);
                                    if (detailChild.getNodeType() == 1) {
                                        if (this.getUnprefixedNodeName(detailChild).equals("UPnPError")) {
                                            final NodeList errorChildren = detailChild.getChildNodes();
                                            for (int y = 0; y < errorChildren.getLength(); ++y) {
                                                final Node errorChild = errorChildren.item(y);
                                                if (errorChild.getNodeType() == 1) {
                                                    if (this.getUnprefixedNodeName(errorChild).equals("errorCode")) {
                                                        errorCode = XMLUtil.getTextContent(errorChild);
                                                    }
                                                    if (this.getUnprefixedNodeName(errorChild).equals("errorDescription")) {
                                                        errorDescription = XMLUtil.getTextContent(errorChild);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (errorCode != null) {
            try {
                final int numericCode = Integer.valueOf(errorCode);
                final ErrorCode standardErrorCode = ErrorCode.getByCode(numericCode);
                if (standardErrorCode != null) {
                    SOAPActionProcessorImpl.log.fine("Reading fault element: " + standardErrorCode.getCode() + " - " + errorDescription);
                    return new ActionException(standardErrorCode, errorDescription, false);
                }
                SOAPActionProcessorImpl.log.fine("Reading fault element: " + numericCode + " - " + errorDescription);
                return new ActionException(numericCode, errorDescription);
            }
            catch (NumberFormatException ex) {
                throw new RuntimeException("Error code was not a number");
            }
        }
        if (receivedFaultElement) {
            throw new RuntimeException("Received fault element but no error code");
        }
        return null;
    }
    
    protected String getMessageBody(final ActionMessage message) throws UnsupportedDataException {
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
    
    protected ActionArgumentValue[] readArgumentValues(final NodeList nodeList, final ActionArgument[] args) throws ActionException {
        final List<Node> nodes = this.getMatchingNodes(nodeList, args);
        final ActionArgumentValue[] values = new ActionArgumentValue[args.length];
        for (int i = 0; i < args.length; ++i) {
            final ActionArgument arg = args[i];
            final Node node = this.findActionArgumentNode(nodes, arg);
            if (node == null) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Could not find argument '" + arg.getName() + "' node");
            }
            SOAPActionProcessorImpl.log.fine("Reading action argument: " + arg.getName());
            final String value = XMLUtil.getTextContent(node);
            values[i] = this.createValue(arg, value);
        }
        return values;
    }
    
    protected List<Node> getMatchingNodes(final NodeList nodeList, final ActionArgument[] args) throws ActionException {
        final List<String> names = new ArrayList<String>();
        for (final ActionArgument argument : args) {
            names.add(argument.getName());
            names.addAll(Arrays.asList(argument.getAliases()));
        }
        final List<Node> matches = new ArrayList<Node>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node child = nodeList.item(i);
            if (child.getNodeType() == 1) {
                if (names.contains(this.getUnprefixedNodeName(child))) {
                    matches.add(child);
                }
            }
        }
        if (matches.size() < args.length) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Invalid number of input or output arguments in XML message, expected " + args.length + " but found " + matches.size());
        }
        return matches;
    }
    
    protected ActionArgumentValue createValue(final ActionArgument arg, final String value) throws ActionException {
        try {
            return new ActionArgumentValue(arg, value);
        }
        catch (InvalidValueException ex) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Wrong type or invalid value for '" + arg.getName() + "': " + ex.getMessage(), ex);
        }
    }
    
    protected Node findActionArgumentNode(final List<Node> nodes, final ActionArgument arg) {
        for (final Node node : nodes) {
            if (arg.isNameOrAlias(this.getUnprefixedNodeName(node))) {
                return node;
            }
        }
        return null;
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        SOAPActionProcessorImpl.log.warning(e.toString());
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
        SOAPActionProcessorImpl.log = Logger.getLogger(SOAPActionProcessor.class.getName());
    }
}
