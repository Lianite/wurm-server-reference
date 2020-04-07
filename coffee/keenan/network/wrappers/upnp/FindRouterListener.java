// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.wrappers.upnp;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.registry.DefaultRegistryListener;

class FindRouterListener extends DefaultRegistryListener
{
    private final ServiceType[] searchServices;
    
    FindRouterListener() {
        this.searchServices = new ServiceType[] { PortMappingListener.IP_SERVICE_TYPE, PortMappingListener.PPP_SERVICE_TYPE };
    }
    
    @Override
    public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
        super.remoteDeviceAdded(registry, device);
        if (UPNPService.getRouterDevice() != null && UPNPService.getWanService() != null) {
            return;
        }
        for (final ServiceType serviceType : this.searchServices) {
            final Service service = device.findService(serviceType);
            if (service != null) {
                UPNPService.getInstance().setRouterAndService(device, service);
                return;
            }
        }
    }
}
