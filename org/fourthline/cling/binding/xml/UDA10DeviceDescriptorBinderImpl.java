// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.seamless.util.Exceptions;
import java.net.URI;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.binding.staging.MutableService;
import org.seamless.util.MimeType;
import org.fourthline.cling.binding.staging.MutableIcon;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.UDN;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.net.URL;
import org.fourthline.cling.model.XMLUtil;
import org.w3c.dom.Element;
import org.fourthline.cling.binding.staging.MutableDevice;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.fourthline.cling.model.ValidationException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.fourthline.cling.model.meta.Device;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;

public class UDA10DeviceDescriptorBinderImpl implements DeviceDescriptorBinder, ErrorHandler
{
    private static Logger log;
    
    @Override
    public <D extends Device> D describe(final D undescribedDevice, final String descriptorXml) throws DescriptorBindingException, ValidationException {
        if (descriptorXml == null || descriptorXml.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            UDA10DeviceDescriptorBinderImpl.log.fine("Populating device from XML descriptor: " + undescribedDevice);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);
            final Document d = documentBuilder.parse(new InputSource(new StringReader(descriptorXml.trim())));
            return this.describe(undescribedDevice, d);
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new DescriptorBindingException("Could not parse device descriptor: " + ex2.toString(), ex2);
        }
    }
    
    @Override
    public <D extends Device> D describe(final D undescribedDevice, final Document dom) throws DescriptorBindingException, ValidationException {
        try {
            UDA10DeviceDescriptorBinderImpl.log.fine("Populating device from DOM: " + undescribedDevice);
            final MutableDevice descriptor = new MutableDevice();
            final Element rootElement = dom.getDocumentElement();
            this.hydrateRoot(descriptor, rootElement);
            return this.buildInstance(undescribedDevice, descriptor);
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new DescriptorBindingException("Could not parse device DOM: " + ex2.toString(), ex2);
        }
    }
    
    public <D extends Device> D buildInstance(final D undescribedDevice, final MutableDevice descriptor) throws ValidationException {
        return (D)descriptor.build(undescribedDevice);
    }
    
    protected void hydrateRoot(final MutableDevice descriptor, final Element rootElement) throws DescriptorBindingException {
        if (rootElement.getNamespaceURI() == null || !rootElement.getNamespaceURI().equals("urn:schemas-upnp-org:device-1-0")) {
            UDA10DeviceDescriptorBinderImpl.log.warning("Wrong XML namespace declared on root element: " + rootElement.getNamespaceURI());
        }
        if (!rootElement.getNodeName().equals(Descriptor.Device.ELEMENT.root.name())) {
            throw new DescriptorBindingException("Root element name is not <root>: " + rootElement.getNodeName());
        }
        final NodeList rootChildren = rootElement.getChildNodes();
        Node deviceNode = null;
        for (int i = 0; i < rootChildren.getLength(); ++i) {
            final Node rootChild = rootChildren.item(i);
            if (rootChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.specVersion.equals(rootChild)) {
                    this.hydrateSpecVersion(descriptor, rootChild);
                }
                else {
                    if (Descriptor.Device.ELEMENT.URLBase.equals(rootChild)) {
                        try {
                            final String urlString = XMLUtil.getTextContent(rootChild);
                            if (urlString != null && urlString.length() > 0) {
                                descriptor.baseURL = new URL(urlString);
                            }
                            continue;
                        }
                        catch (Exception ex) {
                            throw new DescriptorBindingException("Invalid URLBase: " + ex.getMessage());
                        }
                    }
                    if (Descriptor.Device.ELEMENT.device.equals(rootChild)) {
                        if (deviceNode != null) {
                            throw new DescriptorBindingException("Found multiple <device> elements in <root>");
                        }
                        deviceNode = rootChild;
                    }
                    else {
                        UDA10DeviceDescriptorBinderImpl.log.finer("Ignoring unknown element: " + rootChild.getNodeName());
                    }
                }
            }
        }
        if (deviceNode == null) {
            throw new DescriptorBindingException("No <device> element in <root>");
        }
        this.hydrateDevice(descriptor, deviceNode);
    }
    
    public void hydrateSpecVersion(final MutableDevice descriptor, final Node specVersionNode) throws DescriptorBindingException {
        final NodeList specVersionChildren = specVersionNode.getChildNodes();
        for (int i = 0; i < specVersionChildren.getLength(); ++i) {
            final Node specVersionChild = specVersionChildren.item(i);
            if (specVersionChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.major.equals(specVersionChild)) {
                    String version = XMLUtil.getTextContent(specVersionChild).trim();
                    if (!version.equals("1")) {
                        UDA10DeviceDescriptorBinderImpl.log.warning("Unsupported UDA major version, ignoring: " + version);
                        version = "1";
                    }
                    descriptor.udaVersion.major = Integer.valueOf(version);
                }
                else if (Descriptor.Device.ELEMENT.minor.equals(specVersionChild)) {
                    String version = XMLUtil.getTextContent(specVersionChild).trim();
                    if (!version.equals("0")) {
                        UDA10DeviceDescriptorBinderImpl.log.warning("Unsupported UDA minor version, ignoring: " + version);
                        version = "0";
                    }
                    descriptor.udaVersion.minor = Integer.valueOf(version);
                }
            }
        }
    }
    
    public void hydrateDevice(final MutableDevice descriptor, final Node deviceNode) throws DescriptorBindingException {
        final NodeList deviceNodeChildren = deviceNode.getChildNodes();
        for (int i = 0; i < deviceNodeChildren.getLength(); ++i) {
            final Node deviceNodeChild = deviceNodeChildren.item(i);
            if (deviceNodeChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.deviceType.equals(deviceNodeChild)) {
                    descriptor.deviceType = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.friendlyName.equals(deviceNodeChild)) {
                    descriptor.friendlyName = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.manufacturer.equals(deviceNodeChild)) {
                    descriptor.manufacturer = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.manufacturerURL.equals(deviceNodeChild)) {
                    descriptor.manufacturerURI = parseURI(XMLUtil.getTextContent(deviceNodeChild));
                }
                else if (Descriptor.Device.ELEMENT.modelDescription.equals(deviceNodeChild)) {
                    descriptor.modelDescription = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.modelName.equals(deviceNodeChild)) {
                    descriptor.modelName = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.modelNumber.equals(deviceNodeChild)) {
                    descriptor.modelNumber = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.modelURL.equals(deviceNodeChild)) {
                    descriptor.modelURI = parseURI(XMLUtil.getTextContent(deviceNodeChild));
                }
                else if (Descriptor.Device.ELEMENT.presentationURL.equals(deviceNodeChild)) {
                    descriptor.presentationURI = parseURI(XMLUtil.getTextContent(deviceNodeChild));
                }
                else if (Descriptor.Device.ELEMENT.UPC.equals(deviceNodeChild)) {
                    descriptor.upc = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.serialNumber.equals(deviceNodeChild)) {
                    descriptor.serialNumber = XMLUtil.getTextContent(deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.UDN.equals(deviceNodeChild)) {
                    descriptor.udn = UDN.valueOf(XMLUtil.getTextContent(deviceNodeChild));
                }
                else if (Descriptor.Device.ELEMENT.iconList.equals(deviceNodeChild)) {
                    this.hydrateIconList(descriptor, deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.serviceList.equals(deviceNodeChild)) {
                    this.hydrateServiceList(descriptor, deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.deviceList.equals(deviceNodeChild)) {
                    this.hydrateDeviceList(descriptor, deviceNodeChild);
                }
                else if (Descriptor.Device.ELEMENT.X_DLNADOC.equals(deviceNodeChild) && "dlna".equals(deviceNodeChild.getPrefix())) {
                    final String txt = XMLUtil.getTextContent(deviceNodeChild);
                    try {
                        descriptor.dlnaDocs.add(DLNADoc.valueOf(txt));
                    }
                    catch (InvalidValueException ex) {
                        UDA10DeviceDescriptorBinderImpl.log.info("Invalid X_DLNADOC value, ignoring value: " + txt);
                    }
                }
                else if (Descriptor.Device.ELEMENT.X_DLNACAP.equals(deviceNodeChild) && "dlna".equals(deviceNodeChild.getPrefix())) {
                    descriptor.dlnaCaps = DLNACaps.valueOf(XMLUtil.getTextContent(deviceNodeChild));
                }
            }
        }
    }
    
    public void hydrateIconList(final MutableDevice descriptor, final Node iconListNode) throws DescriptorBindingException {
        final NodeList iconListNodeChildren = iconListNode.getChildNodes();
        for (int i = 0; i < iconListNodeChildren.getLength(); ++i) {
            final Node iconListNodeChild = iconListNodeChildren.item(i);
            if (iconListNodeChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.icon.equals(iconListNodeChild)) {
                    final MutableIcon icon = new MutableIcon();
                    final NodeList iconChildren = iconListNodeChild.getChildNodes();
                    for (int x = 0; x < iconChildren.getLength(); ++x) {
                        final Node iconChild = iconChildren.item(x);
                        if (iconChild.getNodeType() == 1) {
                            if (Descriptor.Device.ELEMENT.width.equals(iconChild)) {
                                icon.width = Integer.valueOf(XMLUtil.getTextContent(iconChild));
                            }
                            else if (Descriptor.Device.ELEMENT.height.equals(iconChild)) {
                                icon.height = Integer.valueOf(XMLUtil.getTextContent(iconChild));
                            }
                            else if (Descriptor.Device.ELEMENT.depth.equals(iconChild)) {
                                final String depth = XMLUtil.getTextContent(iconChild);
                                try {
                                    icon.depth = Integer.valueOf(depth);
                                }
                                catch (NumberFormatException ex) {
                                    UDA10DeviceDescriptorBinderImpl.log.warning("Invalid icon depth '" + depth + "', using 16 as default: " + ex);
                                    icon.depth = 16;
                                }
                            }
                            else if (Descriptor.Device.ELEMENT.url.equals(iconChild)) {
                                icon.uri = parseURI(XMLUtil.getTextContent(iconChild));
                            }
                            else if (Descriptor.Device.ELEMENT.mimetype.equals(iconChild)) {
                                try {
                                    MimeType.valueOf(icon.mimeType = XMLUtil.getTextContent(iconChild));
                                }
                                catch (IllegalArgumentException ex2) {
                                    UDA10DeviceDescriptorBinderImpl.log.warning("Ignoring invalid icon mime type: " + icon.mimeType);
                                    icon.mimeType = "";
                                }
                            }
                        }
                    }
                    descriptor.icons.add(icon);
                }
            }
        }
    }
    
    public void hydrateServiceList(final MutableDevice descriptor, final Node serviceListNode) throws DescriptorBindingException {
        final NodeList serviceListNodeChildren = serviceListNode.getChildNodes();
        for (int i = 0; i < serviceListNodeChildren.getLength(); ++i) {
            final Node serviceListNodeChild = serviceListNodeChildren.item(i);
            if (serviceListNodeChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.service.equals(serviceListNodeChild)) {
                    final NodeList serviceChildren = serviceListNodeChild.getChildNodes();
                    try {
                        final MutableService service = new MutableService();
                        for (int x = 0; x < serviceChildren.getLength(); ++x) {
                            final Node serviceChild = serviceChildren.item(x);
                            if (serviceChild.getNodeType() == 1) {
                                if (Descriptor.Device.ELEMENT.serviceType.equals(serviceChild)) {
                                    service.serviceType = ServiceType.valueOf(XMLUtil.getTextContent(serviceChild));
                                }
                                else if (Descriptor.Device.ELEMENT.serviceId.equals(serviceChild)) {
                                    service.serviceId = ServiceId.valueOf(XMLUtil.getTextContent(serviceChild));
                                }
                                else if (Descriptor.Device.ELEMENT.SCPDURL.equals(serviceChild)) {
                                    service.descriptorURI = parseURI(XMLUtil.getTextContent(serviceChild));
                                }
                                else if (Descriptor.Device.ELEMENT.controlURL.equals(serviceChild)) {
                                    service.controlURI = parseURI(XMLUtil.getTextContent(serviceChild));
                                }
                                else if (Descriptor.Device.ELEMENT.eventSubURL.equals(serviceChild)) {
                                    service.eventSubscriptionURI = parseURI(XMLUtil.getTextContent(serviceChild));
                                }
                            }
                        }
                        descriptor.services.add(service);
                    }
                    catch (InvalidValueException ex) {
                        UDA10DeviceDescriptorBinderImpl.log.warning("UPnP specification violation, skipping invalid service declaration. " + ex.getMessage());
                    }
                }
            }
        }
    }
    
    public void hydrateDeviceList(final MutableDevice descriptor, final Node deviceListNode) throws DescriptorBindingException {
        final NodeList deviceListNodeChildren = deviceListNode.getChildNodes();
        for (int i = 0; i < deviceListNodeChildren.getLength(); ++i) {
            final Node deviceListNodeChild = deviceListNodeChildren.item(i);
            if (deviceListNodeChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.device.equals(deviceListNodeChild)) {
                    final MutableDevice embeddedDevice = new MutableDevice();
                    embeddedDevice.parentDevice = descriptor;
                    descriptor.embeddedDevices.add(embeddedDevice);
                    this.hydrateDevice(embeddedDevice, deviceListNodeChild);
                }
            }
        }
    }
    
    @Override
    public String generate(final Device deviceModel, final RemoteClientInfo info, final Namespace namespace) throws DescriptorBindingException {
        try {
            UDA10DeviceDescriptorBinderImpl.log.fine("Generating XML descriptor from device model: " + deviceModel);
            return XMLUtil.documentToString(this.buildDOM(deviceModel, info, namespace));
        }
        catch (Exception ex) {
            throw new DescriptorBindingException("Could not build DOM: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public Document buildDOM(final Device deviceModel, final RemoteClientInfo info, final Namespace namespace) throws DescriptorBindingException {
        try {
            UDA10DeviceDescriptorBinderImpl.log.fine("Generating DOM from device model: " + deviceModel);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final Document d = factory.newDocumentBuilder().newDocument();
            this.generateRoot(namespace, deviceModel, d, info);
            return d;
        }
        catch (Exception ex) {
            throw new DescriptorBindingException("Could not generate device descriptor: " + ex.getMessage(), ex);
        }
    }
    
    protected void generateRoot(final Namespace namespace, final Device deviceModel, final Document descriptor, final RemoteClientInfo info) {
        final Element rootElement = descriptor.createElementNS("urn:schemas-upnp-org:device-1-0", Descriptor.Device.ELEMENT.root.toString());
        descriptor.appendChild(rootElement);
        this.generateSpecVersion(namespace, deviceModel, descriptor, rootElement);
        this.generateDevice(namespace, deviceModel, descriptor, rootElement, info);
    }
    
    protected void generateSpecVersion(final Namespace namespace, final Device deviceModel, final Document descriptor, final Element rootElement) {
        final Element specVersionElement = XMLUtil.appendNewElement(descriptor, rootElement, Descriptor.Device.ELEMENT.specVersion);
        XMLUtil.appendNewElementIfNotNull(descriptor, specVersionElement, Descriptor.Device.ELEMENT.major, deviceModel.getVersion().getMajor());
        XMLUtil.appendNewElementIfNotNull(descriptor, specVersionElement, Descriptor.Device.ELEMENT.minor, deviceModel.getVersion().getMinor());
    }
    
    protected void generateDevice(final Namespace namespace, final Device deviceModel, final Document descriptor, final Element rootElement, final RemoteClientInfo info) {
        final Element deviceElement = XMLUtil.appendNewElement(descriptor, rootElement, Descriptor.Device.ELEMENT.device);
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.deviceType, deviceModel.getType());
        final DeviceDetails deviceModelDetails = deviceModel.getDetails(info);
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.friendlyName, deviceModelDetails.getFriendlyName());
        if (deviceModelDetails.getManufacturerDetails() != null) {
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.manufacturer, deviceModelDetails.getManufacturerDetails().getManufacturer());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.manufacturerURL, deviceModelDetails.getManufacturerDetails().getManufacturerURI());
        }
        if (deviceModelDetails.getModelDetails() != null) {
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelDescription, deviceModelDetails.getModelDetails().getModelDescription());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelName, deviceModelDetails.getModelDetails().getModelName());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelNumber, deviceModelDetails.getModelDetails().getModelNumber());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelURL, deviceModelDetails.getModelDetails().getModelURI());
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.serialNumber, deviceModelDetails.getSerialNumber());
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.UDN, deviceModel.getIdentity().getUdn());
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.presentationURL, deviceModelDetails.getPresentationURI());
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.UPC, deviceModelDetails.getUpc());
        if (deviceModelDetails.getDlnaDocs() != null) {
            for (final DLNADoc dlnaDoc : deviceModelDetails.getDlnaDocs()) {
                XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, "dlna:" + Descriptor.Device.ELEMENT.X_DLNADOC, dlnaDoc, "urn:schemas-dlna-org:device-1-0");
            }
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, "dlna:" + Descriptor.Device.ELEMENT.X_DLNACAP, deviceModelDetails.getDlnaCaps(), "urn:schemas-dlna-org:device-1-0");
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, "sec:" + Descriptor.Device.ELEMENT.ProductCap, deviceModelDetails.getSecProductCaps(), "http://www.sec.co.kr/dlna");
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, "sec:" + Descriptor.Device.ELEMENT.X_ProductCap, deviceModelDetails.getSecProductCaps(), "http://www.sec.co.kr/dlna");
        this.generateIconList(namespace, deviceModel, descriptor, deviceElement);
        this.generateServiceList(namespace, deviceModel, descriptor, deviceElement);
        this.generateDeviceList(namespace, deviceModel, descriptor, deviceElement, info);
    }
    
    protected void generateIconList(final Namespace namespace, final Device deviceModel, final Document descriptor, final Element deviceElement) {
        if (!deviceModel.hasIcons()) {
            return;
        }
        final Element iconListElement = XMLUtil.appendNewElement(descriptor, deviceElement, Descriptor.Device.ELEMENT.iconList);
        for (final Icon icon : deviceModel.getIcons()) {
            final Element iconElement = XMLUtil.appendNewElement(descriptor, iconListElement, Descriptor.Device.ELEMENT.icon);
            XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.mimetype, icon.getMimeType());
            XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.width, icon.getWidth());
            XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.height, icon.getHeight());
            XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.depth, icon.getDepth());
            if (deviceModel instanceof RemoteDevice) {
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.url, icon.getUri());
            }
            else if (deviceModel instanceof LocalDevice) {
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.url, namespace.getIconPath(icon));
            }
        }
    }
    
    protected void generateServiceList(final Namespace namespace, final Device deviceModel, final Document descriptor, final Element deviceElement) {
        if (!deviceModel.hasServices()) {
            return;
        }
        final Element serviceListElement = XMLUtil.appendNewElement(descriptor, deviceElement, Descriptor.Device.ELEMENT.serviceList);
        for (final Service service : deviceModel.getServices()) {
            final Element serviceElement = XMLUtil.appendNewElement(descriptor, serviceListElement, Descriptor.Device.ELEMENT.service);
            XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.serviceType, service.getServiceType());
            XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.serviceId, service.getServiceId());
            if (service instanceof RemoteService) {
                final RemoteService rs = (RemoteService)service;
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.SCPDURL, rs.getDescriptorURI());
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.controlURL, rs.getControlURI());
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.eventSubURL, rs.getEventSubscriptionURI());
            }
            else if (service instanceof LocalService) {
                final LocalService ls = (LocalService)service;
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.SCPDURL, namespace.getDescriptorPath(ls));
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.controlURL, namespace.getControlPath(ls));
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.eventSubURL, namespace.getEventSubscriptionPath(ls));
            }
        }
    }
    
    protected void generateDeviceList(final Namespace namespace, final Device deviceModel, final Document descriptor, final Element deviceElement, final RemoteClientInfo info) {
        if (!deviceModel.hasEmbeddedDevices()) {
            return;
        }
        final Element deviceListElement = XMLUtil.appendNewElement(descriptor, deviceElement, Descriptor.Device.ELEMENT.deviceList);
        for (final Device device : deviceModel.getEmbeddedDevices()) {
            this.generateDevice(namespace, device, descriptor, deviceListElement, info);
        }
    }
    
    @Override
    public void warning(final SAXParseException e) throws SAXException {
        UDA10DeviceDescriptorBinderImpl.log.warning(e.toString());
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    @Override
    public void fatalError(final SAXParseException e) throws SAXException {
        throw e;
    }
    
    protected static URI parseURI(String uri) {
        if (uri.startsWith("www.")) {
            uri = "http://" + uri;
        }
        if (uri.contains(" ")) {
            uri = uri.replaceAll(" ", "%20");
        }
        try {
            return URI.create(uri);
        }
        catch (Throwable ex) {
            UDA10DeviceDescriptorBinderImpl.log.fine("Illegal URI, trying with ./ prefix: " + Exceptions.unwrap(ex));
            try {
                return URI.create("./" + uri);
            }
            catch (IllegalArgumentException ex2) {
                UDA10DeviceDescriptorBinderImpl.log.warning("Illegal URI '" + uri + "', ignoring value: " + Exceptions.unwrap(ex2));
                return null;
            }
        }
    }
    
    static {
        UDA10DeviceDescriptorBinderImpl.log = Logger.getLogger(DeviceDescriptorBinder.class.getName());
    }
}
