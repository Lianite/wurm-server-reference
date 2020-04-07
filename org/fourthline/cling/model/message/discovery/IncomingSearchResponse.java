// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.message.header.InterfaceMacHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.LocationHeader;
import java.net.URL;
import org.fourthline.cling.model.types.NamedServiceType;
import org.fourthline.cling.model.message.header.ServiceUSNHeader;
import org.fourthline.cling.model.types.NamedDeviceType;
import org.fourthline.cling.model.message.header.DeviceUSNHeader;
import org.fourthline.cling.model.message.header.UDNHeader;
import org.fourthline.cling.model.message.header.USNRootDeviceHeader;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.IncomingDatagramMessage;

public class IncomingSearchResponse extends IncomingDatagramMessage<UpnpResponse>
{
    public IncomingSearchResponse(final IncomingDatagramMessage<UpnpResponse> source) {
        super(source);
    }
    
    public boolean isSearchResponseMessage() {
        final UpnpHeader st = this.getHeaders().getFirstHeader(UpnpHeader.Type.ST);
        final UpnpHeader usn = this.getHeaders().getFirstHeader(UpnpHeader.Type.USN);
        final UpnpHeader ext = this.getHeaders().getFirstHeader(UpnpHeader.Type.EXT);
        return st != null && st.getValue() != null && usn != null && usn.getValue() != null && ext != null;
    }
    
    public UDN getRootDeviceUDN() {
        UpnpHeader<UDN> udnHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.USN, USNRootDeviceHeader.class);
        if (udnHeader != null) {
            return udnHeader.getValue();
        }
        udnHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.USN, UDNHeader.class);
        if (udnHeader != null) {
            return udnHeader.getValue();
        }
        final UpnpHeader<NamedDeviceType> deviceTypeHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.USN, DeviceUSNHeader.class);
        if (deviceTypeHeader != null) {
            return deviceTypeHeader.getValue().getUdn();
        }
        final UpnpHeader<NamedServiceType> serviceTypeHeader = this.getHeaders().getFirstHeader(UpnpHeader.Type.USN, ServiceUSNHeader.class);
        if (serviceTypeHeader != null) {
            return serviceTypeHeader.getValue().getUdn();
        }
        return null;
    }
    
    public URL getLocationURL() {
        final LocationHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION, LocationHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }
    
    public Integer getMaxAge() {
        final MaxAgeHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE, MaxAgeHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }
    
    public byte[] getInterfaceMacHeader() {
        final InterfaceMacHeader header = this.getHeaders().getFirstHeader(UpnpHeader.Type.EXT_IFACE_MAC, InterfaceMacHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }
}
