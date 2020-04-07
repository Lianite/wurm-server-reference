// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.types.DeviceType;
import java.util.Iterator;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.UDAVersion;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import java.util.ArrayList;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.DLNADoc;
import java.util.List;
import java.net.URI;
import java.net.URL;
import org.fourthline.cling.model.types.UDN;

public class MutableDevice
{
    public UDN udn;
    public MutableUDAVersion udaVersion;
    public URL baseURL;
    public String deviceType;
    public String friendlyName;
    public String manufacturer;
    public URI manufacturerURI;
    public String modelName;
    public String modelDescription;
    public String modelNumber;
    public URI modelURI;
    public String serialNumber;
    public String upc;
    public URI presentationURI;
    public List<DLNADoc> dlnaDocs;
    public DLNACaps dlnaCaps;
    public List<MutableIcon> icons;
    public List<MutableService> services;
    public List<MutableDevice> embeddedDevices;
    public MutableDevice parentDevice;
    
    public MutableDevice() {
        this.udaVersion = new MutableUDAVersion();
        this.dlnaDocs = new ArrayList<DLNADoc>();
        this.icons = new ArrayList<MutableIcon>();
        this.services = new ArrayList<MutableService>();
        this.embeddedDevices = new ArrayList<MutableDevice>();
    }
    
    public Device build(final Device prototype) throws ValidationException {
        return this.build(prototype, this.createDeviceVersion(), this.baseURL);
    }
    
    public Device build(final Device prototype, final UDAVersion deviceVersion, final URL baseURL) throws ValidationException {
        final List<Device> embeddedDevicesList = new ArrayList<Device>();
        for (final MutableDevice embeddedDevice : this.embeddedDevices) {
            embeddedDevicesList.add(embeddedDevice.build(prototype, deviceVersion, baseURL));
        }
        return prototype.newInstance(this.udn, deviceVersion, this.createDeviceType(), this.createDeviceDetails(baseURL), this.createIcons(), this.createServices(prototype), embeddedDevicesList);
    }
    
    public UDAVersion createDeviceVersion() {
        return new UDAVersion(this.udaVersion.major, this.udaVersion.minor);
    }
    
    public DeviceType createDeviceType() {
        return DeviceType.valueOf(this.deviceType);
    }
    
    public DeviceDetails createDeviceDetails(final URL baseURL) {
        return new DeviceDetails(baseURL, this.friendlyName, new ManufacturerDetails(this.manufacturer, this.manufacturerURI), new ModelDetails(this.modelName, this.modelDescription, this.modelNumber, this.modelURI), this.serialNumber, this.upc, this.presentationURI, this.dlnaDocs.toArray(new DLNADoc[this.dlnaDocs.size()]), this.dlnaCaps);
    }
    
    public Icon[] createIcons() {
        final Icon[] iconArray = new Icon[this.icons.size()];
        int i = 0;
        for (final MutableIcon icon : this.icons) {
            iconArray[i++] = icon.build();
        }
        return iconArray;
    }
    
    public Service[] createServices(final Device prototype) throws ValidationException {
        final Service[] services = prototype.newServiceArray(this.services.size());
        int i = 0;
        for (final MutableService service : this.services) {
            services[i++] = service.build(prototype);
        }
        return services;
    }
}
