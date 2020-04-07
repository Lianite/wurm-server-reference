// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import java.util.Iterator;
import org.seamless.util.MimeType;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.UDN;
import java.util.List;
import org.fourthline.cling.binding.staging.MutableService;
import org.fourthline.cling.binding.staging.MutableIcon;
import java.util.ArrayList;
import java.net.URL;
import org.xml.sax.SAXException;
import org.fourthline.cling.binding.staging.MutableUDAVersion;
import org.xml.sax.Attributes;
import org.fourthline.cling.model.ValidationException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.fourthline.cling.binding.staging.MutableDevice;
import org.seamless.xml.SAXParser;
import org.fourthline.cling.model.meta.Device;
import java.util.logging.Logger;

public class UDA10DeviceDescriptorBinderSAXImpl extends UDA10DeviceDescriptorBinderImpl
{
    private static Logger log;
    
    @Override
    public <D extends Device> D describe(final D undescribedDevice, final String descriptorXml) throws DescriptorBindingException, ValidationException {
        if (descriptorXml == null || descriptorXml.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            UDA10DeviceDescriptorBinderSAXImpl.log.fine("Populating device from XML descriptor: " + undescribedDevice);
            final SAXParser parser = new SAXParser();
            final MutableDevice descriptor = new MutableDevice();
            new RootHandler(descriptor, parser);
            parser.parse(new InputSource(new StringReader(descriptorXml.trim())));
            return (D)descriptor.build(undescribedDevice);
        }
        catch (ValidationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new DescriptorBindingException("Could not parse device descriptor: " + ex2.toString(), ex2);
        }
    }
    
    static {
        UDA10DeviceDescriptorBinderSAXImpl.log = Logger.getLogger(DeviceDescriptorBinder.class.getName());
    }
    
    protected static class RootHandler extends DeviceDescriptorHandler<MutableDevice>
    {
        public RootHandler(final MutableDevice instance, final SAXParser parser) {
            super(instance, parser);
        }
        
        @Override
        public void startElement(final Descriptor.Device.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(SpecVersionHandler.EL)) {
                final MutableUDAVersion udaVersion = new MutableUDAVersion();
                this.getInstance().udaVersion = udaVersion;
                new SpecVersionHandler(udaVersion, this);
            }
            if (element.equals(DeviceHandler.EL)) {
                new DeviceHandler(this.getInstance(), (DeviceDescriptorHandler)this);
            }
        }
        
        @Override
        public void endElement(final Descriptor.Device.ELEMENT element) throws SAXException {
            switch (element) {
                case URLBase: {
                    try {
                        final String urlString = this.getCharacters();
                        if (urlString != null && urlString.length() > 0) {
                            this.getInstance().baseURL = new URL(urlString);
                        }
                    }
                    catch (Exception ex) {
                        throw new SAXException("Invalid URLBase: " + ex.toString());
                    }
                    break;
                }
            }
        }
    }
    
    protected static class SpecVersionHandler extends DeviceDescriptorHandler<MutableUDAVersion>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public SpecVersionHandler(final MutableUDAVersion instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final Descriptor.Device.ELEMENT element) throws SAXException {
            switch (element) {
                case major: {
                    String majorVersion = this.getCharacters().trim();
                    if (!majorVersion.equals("1")) {
                        UDA10DeviceDescriptorBinderSAXImpl.log.warning("Unsupported UDA major version, ignoring: " + majorVersion);
                        majorVersion = "1";
                    }
                    this.getInstance().major = Integer.valueOf(majorVersion);
                    break;
                }
                case minor: {
                    String minorVersion = this.getCharacters().trim();
                    if (!minorVersion.equals("0")) {
                        UDA10DeviceDescriptorBinderSAXImpl.log.warning("Unsupported UDA minor version, ignoring: " + minorVersion);
                        minorVersion = "0";
                    }
                    this.getInstance().minor = Integer.valueOf(minorVersion);
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return element.equals(SpecVersionHandler.EL);
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.specVersion;
        }
    }
    
    protected static class DeviceHandler extends DeviceDescriptorHandler<MutableDevice>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public DeviceHandler(final MutableDevice instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Device.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(IconListHandler.EL)) {
                final List<MutableIcon> icons = new ArrayList<MutableIcon>();
                this.getInstance().icons = icons;
                new IconListHandler(icons, this);
            }
            if (element.equals(ServiceListHandler.EL)) {
                final List<MutableService> services = new ArrayList<MutableService>();
                this.getInstance().services = services;
                new ServiceListHandler(services, this);
            }
            if (element.equals(DeviceListHandler.EL)) {
                final List<MutableDevice> devices = new ArrayList<MutableDevice>();
                this.getInstance().embeddedDevices = devices;
                new DeviceListHandler(devices, this);
            }
        }
        
        @Override
        public void endElement(final Descriptor.Device.ELEMENT element) throws SAXException {
            switch (element) {
                case deviceType: {
                    this.getInstance().deviceType = this.getCharacters();
                    break;
                }
                case friendlyName: {
                    this.getInstance().friendlyName = this.getCharacters();
                    break;
                }
                case manufacturer: {
                    this.getInstance().manufacturer = this.getCharacters();
                    break;
                }
                case manufacturerURL: {
                    this.getInstance().manufacturerURI = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                    break;
                }
                case modelDescription: {
                    this.getInstance().modelDescription = this.getCharacters();
                    break;
                }
                case modelName: {
                    this.getInstance().modelName = this.getCharacters();
                    break;
                }
                case modelNumber: {
                    this.getInstance().modelNumber = this.getCharacters();
                    break;
                }
                case modelURL: {
                    this.getInstance().modelURI = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                    break;
                }
                case presentationURL: {
                    this.getInstance().presentationURI = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                    break;
                }
                case UPC: {
                    this.getInstance().upc = this.getCharacters();
                    break;
                }
                case serialNumber: {
                    this.getInstance().serialNumber = this.getCharacters();
                    break;
                }
                case UDN: {
                    this.getInstance().udn = UDN.valueOf(this.getCharacters());
                    break;
                }
                case X_DLNADOC: {
                    final String txt = this.getCharacters();
                    try {
                        this.getInstance().dlnaDocs.add(DLNADoc.valueOf(txt));
                    }
                    catch (InvalidValueException ex) {
                        UDA10DeviceDescriptorBinderSAXImpl.log.info("Invalid X_DLNADOC value, ignoring value: " + txt);
                    }
                    break;
                }
                case X_DLNACAP: {
                    this.getInstance().dlnaCaps = DLNACaps.valueOf(this.getCharacters());
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return element.equals(DeviceHandler.EL);
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.device;
        }
    }
    
    protected static class IconListHandler extends DeviceDescriptorHandler<List<MutableIcon>>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public IconListHandler(final List<MutableIcon> instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Device.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(IconHandler.EL)) {
                final MutableIcon icon = new MutableIcon();
                this.getInstance().add(icon);
                new IconHandler(icon, this);
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return element.equals(IconListHandler.EL);
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.iconList;
        }
    }
    
    protected static class IconHandler extends DeviceDescriptorHandler<MutableIcon>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public IconHandler(final MutableIcon instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final Descriptor.Device.ELEMENT element) throws SAXException {
            switch (element) {
                case width: {
                    this.getInstance().width = Integer.valueOf(this.getCharacters());
                    break;
                }
                case height: {
                    this.getInstance().height = Integer.valueOf(this.getCharacters());
                    break;
                }
                case depth: {
                    try {
                        this.getInstance().depth = Integer.valueOf(this.getCharacters());
                    }
                    catch (NumberFormatException ex) {
                        UDA10DeviceDescriptorBinderSAXImpl.log.warning("Invalid icon depth '" + this.getCharacters() + "', using 16 as default: " + ex);
                        this.getInstance().depth = 16;
                    }
                    break;
                }
                case url: {
                    this.getInstance().uri = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                    break;
                }
                case mimetype: {
                    try {
                        MimeType.valueOf(this.getInstance().mimeType = this.getCharacters());
                    }
                    catch (IllegalArgumentException ex2) {
                        UDA10DeviceDescriptorBinderSAXImpl.log.warning("Ignoring invalid icon mime type: " + this.getInstance().mimeType);
                        this.getInstance().mimeType = "";
                    }
                    break;
                }
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return element.equals(IconHandler.EL);
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.icon;
        }
    }
    
    protected static class ServiceListHandler extends DeviceDescriptorHandler<List<MutableService>>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public ServiceListHandler(final List<MutableService> instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Device.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(ServiceHandler.EL)) {
                final MutableService service = new MutableService();
                this.getInstance().add(service);
                new ServiceHandler(service, this);
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            final boolean last = element.equals(ServiceListHandler.EL);
            if (last) {
                final Iterator<MutableService> it = this.getInstance().iterator();
                while (it.hasNext()) {
                    final MutableService service = it.next();
                    if (service.serviceType == null || service.serviceId == null) {
                        it.remove();
                    }
                }
            }
            return last;
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.serviceList;
        }
    }
    
    protected static class ServiceHandler extends DeviceDescriptorHandler<MutableService>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public ServiceHandler(final MutableService instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final Descriptor.Device.ELEMENT element) throws SAXException {
            try {
                switch (element) {
                    case serviceType: {
                        this.getInstance().serviceType = ServiceType.valueOf(this.getCharacters());
                        break;
                    }
                    case serviceId: {
                        this.getInstance().serviceId = ServiceId.valueOf(this.getCharacters());
                        break;
                    }
                    case SCPDURL: {
                        this.getInstance().descriptorURI = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                        break;
                    }
                    case controlURL: {
                        this.getInstance().controlURI = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                        break;
                    }
                    case eventSubURL: {
                        this.getInstance().eventSubscriptionURI = UDA10DeviceDescriptorBinderImpl.parseURI(this.getCharacters());
                        break;
                    }
                }
            }
            catch (InvalidValueException ex) {
                UDA10DeviceDescriptorBinderSAXImpl.log.warning("UPnP specification violation, skipping invalid service declaration. " + ex.getMessage());
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return element.equals(ServiceHandler.EL);
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.service;
        }
    }
    
    protected static class DeviceListHandler extends DeviceDescriptorHandler<List<MutableDevice>>
    {
        public static final Descriptor.Device.ELEMENT EL;
        
        public DeviceListHandler(final List<MutableDevice> instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final Descriptor.Device.ELEMENT element, final Attributes attributes) throws SAXException {
            if (element.equals(DeviceHandler.EL)) {
                final MutableDevice device = new MutableDevice();
                this.getInstance().add(device);
                new DeviceHandler(device, this);
            }
        }
        
        @Override
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return element.equals(DeviceListHandler.EL);
        }
        
        static {
            EL = Descriptor.Device.ELEMENT.deviceList;
        }
    }
    
    protected static class DeviceDescriptorHandler<I> extends SAXParser.Handler<I>
    {
        public DeviceDescriptorHandler(final I instance) {
            super(instance);
        }
        
        public DeviceDescriptorHandler(final I instance, final SAXParser parser) {
            super(instance, parser);
        }
        
        public DeviceDescriptorHandler(final I instance, final DeviceDescriptorHandler parent) {
            super(instance, parent);
        }
        
        public DeviceDescriptorHandler(final I instance, final SAXParser parser, final DeviceDescriptorHandler parent) {
            super(instance, parser, parent);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            final Descriptor.Device.ELEMENT el = Descriptor.Device.ELEMENT.valueOrNullOf(localName);
            if (el == null) {
                return;
            }
            this.startElement(el, attributes);
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            final Descriptor.Device.ELEMENT el = Descriptor.Device.ELEMENT.valueOrNullOf(localName);
            if (el == null) {
                return;
            }
            this.endElement(el);
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            final Descriptor.Device.ELEMENT el = Descriptor.Device.ELEMENT.valueOrNullOf(localName);
            return el != null && this.isLastElement(el);
        }
        
        public void startElement(final Descriptor.Device.ELEMENT element, final Attributes attributes) throws SAXException {
        }
        
        public void endElement(final Descriptor.Device.ELEMENT element) throws SAXException {
        }
        
        public boolean isLastElement(final Descriptor.Device.ELEMENT element) {
            return false;
        }
    }
}
