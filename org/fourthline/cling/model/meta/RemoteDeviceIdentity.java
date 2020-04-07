// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.discovery.IncomingSearchResponse;
import org.fourthline.cling.model.message.discovery.IncomingNotificationRequest;
import org.fourthline.cling.model.types.UDN;
import java.net.InetAddress;
import java.net.URL;

public class RemoteDeviceIdentity extends DeviceIdentity
{
    private final URL descriptorURL;
    private final byte[] interfaceMacAddress;
    private final InetAddress discoveredOnLocalAddress;
    
    public RemoteDeviceIdentity(final UDN udn, final RemoteDeviceIdentity template) {
        this(udn, template.getMaxAgeSeconds(), template.getDescriptorURL(), template.getInterfaceMacAddress(), template.getDiscoveredOnLocalAddress());
    }
    
    public RemoteDeviceIdentity(final UDN udn, final Integer maxAgeSeconds, final URL descriptorURL, final byte[] interfaceMacAddress, final InetAddress discoveredOnLocalAddress) {
        super(udn, maxAgeSeconds);
        this.descriptorURL = descriptorURL;
        this.interfaceMacAddress = interfaceMacAddress;
        this.discoveredOnLocalAddress = discoveredOnLocalAddress;
    }
    
    public RemoteDeviceIdentity(final IncomingNotificationRequest notificationRequest) {
        this(notificationRequest.getUDN(), notificationRequest.getMaxAge(), notificationRequest.getLocationURL(), notificationRequest.getInterfaceMacHeader(), notificationRequest.getLocalAddress());
    }
    
    public RemoteDeviceIdentity(final IncomingSearchResponse searchResponse) {
        this(searchResponse.getRootDeviceUDN(), searchResponse.getMaxAge(), searchResponse.getLocationURL(), searchResponse.getInterfaceMacHeader(), searchResponse.getLocalAddress());
    }
    
    public URL getDescriptorURL() {
        return this.descriptorURL;
    }
    
    public byte[] getInterfaceMacAddress() {
        return this.interfaceMacAddress;
    }
    
    public InetAddress getDiscoveredOnLocalAddress() {
        return this.discoveredOnLocalAddress;
    }
    
    public byte[] getWakeOnLANBytes() {
        if (this.getInterfaceMacAddress() == null) {
            return null;
        }
        final byte[] bytes = new byte[6 + 16 * this.getInterfaceMacAddress().length];
        for (int i = 0; i < 6; ++i) {
            bytes[i] = -1;
        }
        for (int i = 6; i < bytes.length; i += this.getInterfaceMacAddress().length) {
            System.arraycopy(this.getInterfaceMacAddress(), 0, bytes, i, this.getInterfaceMacAddress().length);
        }
        return bytes;
    }
    
    @Override
    public String toString() {
        if (ModelUtil.ANDROID_RUNTIME) {
            return "(RemoteDeviceIdentity) UDN: " + this.getUdn() + ", Descriptor: " + this.getDescriptorURL();
        }
        return "(" + this.getClass().getSimpleName() + ") UDN: " + this.getUdn() + ", Descriptor: " + this.getDescriptorURL();
    }
}
