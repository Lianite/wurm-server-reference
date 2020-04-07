// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.Action;
import java.util.List;
import org.fourthline.cling.binding.staging.MutableAllowedValueRange;
import java.util.ArrayList;
import org.fourthline.cling.model.types.CustomDatatype;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.meta.StateVariableEventDetails;
import org.fourthline.cling.binding.staging.MutableStateVariable;
import org.fourthline.cling.model.meta.ActionArgument;
import java.util.Locale;
import org.fourthline.cling.binding.staging.MutableActionArgument;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.binding.staging.MutableAction;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Device;
import org.w3c.dom.Element;
import org.fourthline.cling.binding.staging.MutableService;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.fourthline.cling.model.ValidationException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;

public class UDA10ServiceDescriptorBinderImpl implements ServiceDescriptorBinder, ErrorHandler
{
    private static Logger log;
    
    @Override
    public <S extends Service> S describe(final S undescribedService, final String descriptorXml) throws DescriptorBindingException, ValidationException {
        if (descriptorXml == null || descriptorXml.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            UDA10ServiceDescriptorBinderImpl.log.fine("Populating service from XML descriptor: " + undescribedService);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);
            final Document d = documentBuilder.parse(new InputSource(new StringReader(descriptorXml.trim())));
            return this.describe(undescribedService, d);
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new DescriptorBindingException("Could not parse service descriptor: " + ex2.toString(), ex2);
        }
    }
    
    @Override
    public <S extends Service> S describe(final S undescribedService, final Document dom) throws DescriptorBindingException, ValidationException {
        try {
            UDA10ServiceDescriptorBinderImpl.log.fine("Populating service from DOM: " + undescribedService);
            final MutableService descriptor = new MutableService();
            this.hydrateBasic(descriptor, undescribedService);
            final Element rootElement = dom.getDocumentElement();
            this.hydrateRoot(descriptor, rootElement);
            return this.buildInstance(undescribedService, descriptor);
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new DescriptorBindingException("Could not parse service DOM: " + ex2.toString(), ex2);
        }
    }
    
    protected <S extends Service> S buildInstance(final S undescribedService, final MutableService descriptor) throws ValidationException {
        return (S)descriptor.build(undescribedService.getDevice());
    }
    
    protected void hydrateBasic(final MutableService descriptor, final Service undescribedService) {
        descriptor.serviceId = undescribedService.getServiceId();
        descriptor.serviceType = undescribedService.getServiceType();
        if (undescribedService instanceof RemoteService) {
            final RemoteService rs = (RemoteService)undescribedService;
            descriptor.controlURI = rs.getControlURI();
            descriptor.eventSubscriptionURI = rs.getEventSubscriptionURI();
            descriptor.descriptorURI = rs.getDescriptorURI();
        }
    }
    
    protected void hydrateRoot(final MutableService descriptor, final Element rootElement) throws DescriptorBindingException {
        if (!Descriptor.Service.ELEMENT.scpd.equals(rootElement)) {
            throw new DescriptorBindingException("Root element name is not <scpd>: " + rootElement.getNodeName());
        }
        final NodeList rootChildren = rootElement.getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); ++i) {
            final Node rootChild = rootChildren.item(i);
            if (rootChild.getNodeType() == 1) {
                if (!Descriptor.Service.ELEMENT.specVersion.equals(rootChild)) {
                    if (Descriptor.Service.ELEMENT.actionList.equals(rootChild)) {
                        this.hydrateActionList(descriptor, rootChild);
                    }
                    else if (Descriptor.Service.ELEMENT.serviceStateTable.equals(rootChild)) {
                        this.hydrateServiceStateTableList(descriptor, rootChild);
                    }
                    else {
                        UDA10ServiceDescriptorBinderImpl.log.finer("Ignoring unknown element: " + rootChild.getNodeName());
                    }
                }
            }
        }
    }
    
    public void hydrateActionList(final MutableService descriptor, final Node actionListNode) throws DescriptorBindingException {
        final NodeList actionListChildren = actionListNode.getChildNodes();
        for (int i = 0; i < actionListChildren.getLength(); ++i) {
            final Node actionListChild = actionListChildren.item(i);
            if (actionListChild.getNodeType() == 1) {
                if (Descriptor.Service.ELEMENT.action.equals(actionListChild)) {
                    final MutableAction action = new MutableAction();
                    this.hydrateAction(action, actionListChild);
                    descriptor.actions.add(action);
                }
            }
        }
    }
    
    public void hydrateAction(final MutableAction action, final Node actionNode) {
        final NodeList actionNodeChildren = actionNode.getChildNodes();
        for (int i = 0; i < actionNodeChildren.getLength(); ++i) {
            final Node actionNodeChild = actionNodeChildren.item(i);
            if (actionNodeChild.getNodeType() == 1) {
                if (Descriptor.Service.ELEMENT.name.equals(actionNodeChild)) {
                    action.name = XMLUtil.getTextContent(actionNodeChild);
                }
                else if (Descriptor.Service.ELEMENT.argumentList.equals(actionNodeChild)) {
                    final NodeList argumentChildren = actionNodeChild.getChildNodes();
                    for (int j = 0; j < argumentChildren.getLength(); ++j) {
                        final Node argumentChild = argumentChildren.item(j);
                        if (argumentChild.getNodeType() == 1) {
                            final MutableActionArgument actionArgument = new MutableActionArgument();
                            this.hydrateActionArgument(actionArgument, argumentChild);
                            action.arguments.add(actionArgument);
                        }
                    }
                }
            }
        }
    }
    
    public void hydrateActionArgument(final MutableActionArgument actionArgument, final Node actionArgumentNode) {
        final NodeList argumentNodeChildren = actionArgumentNode.getChildNodes();
        for (int i = 0; i < argumentNodeChildren.getLength(); ++i) {
            final Node argumentNodeChild = argumentNodeChildren.item(i);
            if (argumentNodeChild.getNodeType() == 1) {
                if (Descriptor.Service.ELEMENT.name.equals(argumentNodeChild)) {
                    actionArgument.name = XMLUtil.getTextContent(argumentNodeChild);
                }
                else if (Descriptor.Service.ELEMENT.direction.equals(argumentNodeChild)) {
                    final String directionString = XMLUtil.getTextContent(argumentNodeChild);
                    try {
                        actionArgument.direction = ActionArgument.Direction.valueOf(directionString.toUpperCase(Locale.ROOT));
                    }
                    catch (IllegalArgumentException ex) {
                        UDA10ServiceDescriptorBinderImpl.log.warning("UPnP specification violation: Invalid action argument direction, assuming 'IN': " + directionString);
                        actionArgument.direction = ActionArgument.Direction.IN;
                    }
                }
                else if (Descriptor.Service.ELEMENT.relatedStateVariable.equals(argumentNodeChild)) {
                    actionArgument.relatedStateVariable = XMLUtil.getTextContent(argumentNodeChild);
                }
                else if (Descriptor.Service.ELEMENT.retval.equals(argumentNodeChild)) {
                    actionArgument.retval = true;
                }
            }
        }
    }
    
    public void hydrateServiceStateTableList(final MutableService descriptor, final Node serviceStateTableNode) {
        final NodeList serviceStateTableChildren = serviceStateTableNode.getChildNodes();
        for (int i = 0; i < serviceStateTableChildren.getLength(); ++i) {
            final Node serviceStateTableChild = serviceStateTableChildren.item(i);
            if (serviceStateTableChild.getNodeType() == 1) {
                if (Descriptor.Service.ELEMENT.stateVariable.equals(serviceStateTableChild)) {
                    final MutableStateVariable stateVariable = new MutableStateVariable();
                    this.hydrateStateVariable(stateVariable, (Element)serviceStateTableChild);
                    descriptor.stateVariables.add(stateVariable);
                }
            }
        }
    }
    
    public void hydrateStateVariable(final MutableStateVariable stateVariable, final Element stateVariableElement) {
        stateVariable.eventDetails = new StateVariableEventDetails(stateVariableElement.getAttribute("sendEvents") != null && stateVariableElement.getAttribute(Descriptor.Service.ATTRIBUTE.sendEvents.toString()).toUpperCase(Locale.ROOT).equals("YES"));
        final NodeList stateVariableChildren = stateVariableElement.getChildNodes();
        for (int i = 0; i < stateVariableChildren.getLength(); ++i) {
            final Node stateVariableChild = stateVariableChildren.item(i);
            if (stateVariableChild.getNodeType() == 1) {
                if (Descriptor.Service.ELEMENT.name.equals(stateVariableChild)) {
                    stateVariable.name = XMLUtil.getTextContent(stateVariableChild);
                }
                else if (Descriptor.Service.ELEMENT.dataType.equals(stateVariableChild)) {
                    final String dtName = XMLUtil.getTextContent(stateVariableChild);
                    final Datatype.Builtin builtin = Datatype.Builtin.getByDescriptorName(dtName);
                    stateVariable.dataType = ((builtin != null) ? builtin.getDatatype() : new CustomDatatype(dtName));
                }
                else if (Descriptor.Service.ELEMENT.defaultValue.equals(stateVariableChild)) {
                    stateVariable.defaultValue = XMLUtil.getTextContent(stateVariableChild);
                }
                else if (Descriptor.Service.ELEMENT.allowedValueList.equals(stateVariableChild)) {
                    final List<String> allowedValues = new ArrayList<String>();
                    final NodeList allowedValueListChildren = stateVariableChild.getChildNodes();
                    for (int j = 0; j < allowedValueListChildren.getLength(); ++j) {
                        final Node allowedValueListChild = allowedValueListChildren.item(j);
                        if (allowedValueListChild.getNodeType() == 1) {
                            if (Descriptor.Service.ELEMENT.allowedValue.equals(allowedValueListChild)) {
                                allowedValues.add(XMLUtil.getTextContent(allowedValueListChild));
                            }
                        }
                    }
                    stateVariable.allowedValues = allowedValues;
                }
                else if (Descriptor.Service.ELEMENT.allowedValueRange.equals(stateVariableChild)) {
                    final MutableAllowedValueRange range = new MutableAllowedValueRange();
                    final NodeList allowedValueRangeChildren = stateVariableChild.getChildNodes();
                    for (int j = 0; j < allowedValueRangeChildren.getLength(); ++j) {
                        final Node allowedValueRangeChild = allowedValueRangeChildren.item(j);
                        if (allowedValueRangeChild.getNodeType() == 1) {
                            if (Descriptor.Service.ELEMENT.minimum.equals(allowedValueRangeChild)) {
                                try {
                                    range.minimum = Long.valueOf(XMLUtil.getTextContent(allowedValueRangeChild));
                                }
                                catch (Exception ex) {}
                            }
                            else if (Descriptor.Service.ELEMENT.maximum.equals(allowedValueRangeChild)) {
                                try {
                                    range.maximum = Long.valueOf(XMLUtil.getTextContent(allowedValueRangeChild));
                                }
                                catch (Exception ex2) {}
                            }
                            else if (Descriptor.Service.ELEMENT.step.equals(allowedValueRangeChild)) {
                                try {
                                    range.step = Long.valueOf(XMLUtil.getTextContent(allowedValueRangeChild));
                                }
                                catch (Exception ex3) {}
                            }
                        }
                    }
                    stateVariable.allowedValueRange = range;
                }
            }
        }
    }
    
    @Override
    public String generate(final Service service) throws DescriptorBindingException {
        try {
            UDA10ServiceDescriptorBinderImpl.log.fine("Generating XML descriptor from service model: " + service);
            return XMLUtil.documentToString(this.buildDOM(service));
        }
        catch (Exception ex) {
            throw new DescriptorBindingException("Could not build DOM: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public Document buildDOM(final Service service) throws DescriptorBindingException {
        try {
            UDA10ServiceDescriptorBinderImpl.log.fine("Generating XML descriptor from service model: " + service);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final Document d = factory.newDocumentBuilder().newDocument();
            this.generateScpd(service, d);
            return d;
        }
        catch (Exception ex) {
            throw new DescriptorBindingException("Could not generate service descriptor: " + ex.getMessage(), ex);
        }
    }
    
    private void generateScpd(final Service serviceModel, final Document descriptor) {
        final Element scpdElement = descriptor.createElementNS("urn:schemas-upnp-org:service-1-0", Descriptor.Service.ELEMENT.scpd.toString());
        descriptor.appendChild(scpdElement);
        this.generateSpecVersion(serviceModel, descriptor, scpdElement);
        if (serviceModel.hasActions()) {
            this.generateActionList(serviceModel, descriptor, scpdElement);
        }
        this.generateServiceStateTable(serviceModel, descriptor, scpdElement);
    }
    
    private void generateSpecVersion(final Service serviceModel, final Document descriptor, final Element rootElement) {
        final Element specVersionElement = XMLUtil.appendNewElement(descriptor, rootElement, Descriptor.Service.ELEMENT.specVersion);
        XMLUtil.appendNewElementIfNotNull(descriptor, specVersionElement, Descriptor.Service.ELEMENT.major, serviceModel.getDevice().getVersion().getMajor());
        XMLUtil.appendNewElementIfNotNull(descriptor, specVersionElement, Descriptor.Service.ELEMENT.minor, serviceModel.getDevice().getVersion().getMinor());
    }
    
    private void generateActionList(final Service serviceModel, final Document descriptor, final Element scpdElement) {
        final Element actionListElement = XMLUtil.appendNewElement(descriptor, scpdElement, Descriptor.Service.ELEMENT.actionList);
        for (final Action action : serviceModel.getActions()) {
            if (!action.getName().equals("QueryStateVariable")) {
                this.generateAction(action, descriptor, actionListElement);
            }
        }
    }
    
    private void generateAction(final Action action, final Document descriptor, final Element actionListElement) {
        final Element actionElement = XMLUtil.appendNewElement(descriptor, actionListElement, Descriptor.Service.ELEMENT.action);
        XMLUtil.appendNewElementIfNotNull(descriptor, actionElement, Descriptor.Service.ELEMENT.name, action.getName());
        if (action.hasArguments()) {
            final Element argumentListElement = XMLUtil.appendNewElement(descriptor, actionElement, Descriptor.Service.ELEMENT.argumentList);
            for (final ActionArgument actionArgument : action.getArguments()) {
                this.generateActionArgument(actionArgument, descriptor, argumentListElement);
            }
        }
    }
    
    private void generateActionArgument(final ActionArgument actionArgument, final Document descriptor, final Element actionElement) {
        final Element actionArgumentElement = XMLUtil.appendNewElement(descriptor, actionElement, Descriptor.Service.ELEMENT.argument);
        XMLUtil.appendNewElementIfNotNull(descriptor, actionArgumentElement, Descriptor.Service.ELEMENT.name, actionArgument.getName());
        XMLUtil.appendNewElementIfNotNull(descriptor, actionArgumentElement, Descriptor.Service.ELEMENT.direction, actionArgument.getDirection().toString().toLowerCase(Locale.ROOT));
        if (actionArgument.isReturnValue()) {
            UDA10ServiceDescriptorBinderImpl.log.warning("UPnP specification violation: Not producing <retval> element to be compatible with WMP12: " + actionArgument);
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, actionArgumentElement, Descriptor.Service.ELEMENT.relatedStateVariable, actionArgument.getRelatedStateVariableName());
    }
    
    private void generateServiceStateTable(final Service serviceModel, final Document descriptor, final Element scpdElement) {
        final Element serviceStateTableElement = XMLUtil.appendNewElement(descriptor, scpdElement, Descriptor.Service.ELEMENT.serviceStateTable);
        for (final StateVariable stateVariable : serviceModel.getStateVariables()) {
            this.generateStateVariable(stateVariable, descriptor, serviceStateTableElement);
        }
    }
    
    private void generateStateVariable(final StateVariable stateVariable, final Document descriptor, final Element serviveStateTableElement) {
        final Element stateVariableElement = XMLUtil.appendNewElement(descriptor, serviveStateTableElement, Descriptor.Service.ELEMENT.stateVariable);
        XMLUtil.appendNewElementIfNotNull(descriptor, stateVariableElement, Descriptor.Service.ELEMENT.name, stateVariable.getName());
        if (stateVariable.getTypeDetails().getDatatype() instanceof CustomDatatype) {
            XMLUtil.appendNewElementIfNotNull(descriptor, stateVariableElement, Descriptor.Service.ELEMENT.dataType, ((CustomDatatype)stateVariable.getTypeDetails().getDatatype()).getName());
        }
        else {
            XMLUtil.appendNewElementIfNotNull(descriptor, stateVariableElement, Descriptor.Service.ELEMENT.dataType, stateVariable.getTypeDetails().getDatatype().getBuiltin().getDescriptorName());
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, stateVariableElement, Descriptor.Service.ELEMENT.defaultValue, stateVariable.getTypeDetails().getDefaultValue());
        if (stateVariable.getEventDetails().isSendEvents()) {
            stateVariableElement.setAttribute(Descriptor.Service.ATTRIBUTE.sendEvents.toString(), "yes");
        }
        else {
            stateVariableElement.setAttribute(Descriptor.Service.ATTRIBUTE.sendEvents.toString(), "no");
        }
        if (stateVariable.getTypeDetails().getAllowedValues() != null) {
            final Element allowedValueListElement = XMLUtil.appendNewElement(descriptor, stateVariableElement, Descriptor.Service.ELEMENT.allowedValueList);
            for (final String allowedValue : stateVariable.getTypeDetails().getAllowedValues()) {
                XMLUtil.appendNewElementIfNotNull(descriptor, allowedValueListElement, Descriptor.Service.ELEMENT.allowedValue, allowedValue);
            }
        }
        if (stateVariable.getTypeDetails().getAllowedValueRange() != null) {
            final Element allowedValueRangeElement = XMLUtil.appendNewElement(descriptor, stateVariableElement, Descriptor.Service.ELEMENT.allowedValueRange);
            XMLUtil.appendNewElementIfNotNull(descriptor, allowedValueRangeElement, Descriptor.Service.ELEMENT.minimum, stateVariable.getTypeDetails().getAllowedValueRange().getMinimum());
            XMLUtil.appendNewElementIfNotNull(descriptor, allowedValueRangeElement, Descriptor.Service.ELEMENT.maximum, stateVariable.getTypeDetails().getAllowedValueRange().getMaximum());
            if (stateVariable.getTypeDetails().getAllowedValueRange().getStep() >= 1L) {
                XMLUtil.appendNewElementIfNotNull(descriptor, allowedValueRangeElement, Descriptor.Service.ELEMENT.step, stateVariable.getTypeDetails().getAllowedValueRange().getStep());
            }
        }
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        UDA10ServiceDescriptorBinderImpl.log.warning(e.toString());
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
        UDA10ServiceDescriptorBinderImpl.log = Logger.getLogger(ServiceDescriptorBinder.class.getName());
    }
}
