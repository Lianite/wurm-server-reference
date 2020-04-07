// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.igd;

import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import java.util.Iterator;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import java.util.ArrayList;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.Registry;
import java.util.HashMap;
import java.util.List;
import org.fourthline.cling.model.meta.Service;
import java.util.Map;
import org.fourthline.cling.support.model.PortMapping;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.DeviceType;
import java.util.logging.Logger;
import org.fourthline.cling.registry.DefaultRegistryListener;

public class PortMappingListener extends DefaultRegistryListener
{
    private static final Logger log;
    public static final DeviceType IGD_DEVICE_TYPE;
    public static final DeviceType CONNECTION_DEVICE_TYPE;
    public static final ServiceType IP_SERVICE_TYPE;
    public static final ServiceType PPP_SERVICE_TYPE;
    protected PortMapping[] portMappings;
    protected Map<Service, List<PortMapping>> activePortMappings;
    
    public PortMappingListener(final PortMapping portMapping) {
        this(new PortMapping[] { portMapping });
    }
    
    public PortMappingListener(final PortMapping[] portMappings) {
        this.activePortMappings = new HashMap<Service, List<PortMapping>>();
        this.portMappings = portMappings;
    }
    
    @Override
    public synchronized void deviceAdded(final Registry registry, final Device device) {
        final Service connectionService;
        if ((connectionService = this.discoverConnectionService(device)) == null) {
            return;
        }
        PortMappingListener.log.fine("Activating port mappings on: " + connectionService);
        final List<PortMapping> activeForService = new ArrayList<PortMapping>();
        for (final PortMapping pm : this.portMappings) {
            new PortMappingAdd(connectionService, registry.getUpnpService().getControlPoint(), pm) {
                @Override
                public void success(final ActionInvocation invocation) {
                    PortMappingListener.log.fine("Port mapping added: " + pm);
                    activeForService.add(pm);
                }
                
                @Override
                public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                    PortMappingListener.this.handleFailureMessage("Failed to add port mapping: " + pm);
                    PortMappingListener.this.handleFailureMessage("Reason: " + defaultMsg);
                }
            }.run();
        }
        this.activePortMappings.put(connectionService, activeForService);
    }
    
    @Override
    public synchronized void deviceRemoved(final Registry registry, final Device device) {
        for (final Service service : device.findServices()) {
            final Iterator<Map.Entry<Service, List<PortMapping>>> it = this.activePortMappings.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<Service, List<PortMapping>> activeEntry = it.next();
                if (!activeEntry.getKey().equals(service)) {
                    continue;
                }
                if (activeEntry.getValue().size() > 0) {
                    this.handleFailureMessage("Device disappeared, couldn't delete port mappings: " + activeEntry.getValue().size());
                }
                it.remove();
            }
        }
    }
    
    @Override
    public synchronized void beforeShutdown(final Registry registry) {
        for (final Map.Entry<Service, List<PortMapping>> activeEntry : this.activePortMappings.entrySet()) {
            final Iterator<PortMapping> it = activeEntry.getValue().iterator();
            while (it.hasNext()) {
                final PortMapping pm = it.next();
                PortMappingListener.log.fine("Trying to delete port mapping on IGD: " + pm);
                new PortMappingDelete((Service)activeEntry.getKey(), registry.getUpnpService().getControlPoint(), pm) {
                    @Override
                    public void success(final ActionInvocation invocation) {
                        PortMappingListener.log.fine("Port mapping deleted: " + pm);
                        it.remove();
                    }
                    
                    @Override
                    public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                        PortMappingListener.this.handleFailureMessage("Failed to delete port mapping: " + pm);
                        PortMappingListener.this.handleFailureMessage("Reason: " + defaultMsg);
                    }
                }.run();
            }
        }
    }
    
    protected Service discoverConnectionService(final Device device) {
        if (!device.getType().equals(PortMappingListener.IGD_DEVICE_TYPE)) {
            return null;
        }
        final Device[] connectionDevices = device.findDevices(PortMappingListener.CONNECTION_DEVICE_TYPE);
        if (connectionDevices.length == 0) {
            PortMappingListener.log.fine("IGD doesn't support '" + PortMappingListener.CONNECTION_DEVICE_TYPE + "': " + device);
            return null;
        }
        final Device connectionDevice = connectionDevices[0];
        PortMappingListener.log.fine("Using first discovered WAN connection device: " + connectionDevice);
        final Service ipConnectionService = connectionDevice.findService(PortMappingListener.IP_SERVICE_TYPE);
        final Service pppConnectionService = connectionDevice.findService(PortMappingListener.PPP_SERVICE_TYPE);
        if (ipConnectionService == null && pppConnectionService == null) {
            PortMappingListener.log.fine("IGD doesn't support IP or PPP WAN connection service: " + device);
        }
        return (ipConnectionService != null) ? ipConnectionService : pppConnectionService;
    }
    
    protected void handleFailureMessage(final String s) {
        PortMappingListener.log.warning(s);
    }
    
    static {
        log = Logger.getLogger(PortMappingListener.class.getName());
        IGD_DEVICE_TYPE = new UDADeviceType("InternetGatewayDevice", 1);
        CONNECTION_DEVICE_TYPE = new UDADeviceType("WANConnectionDevice", 1);
        IP_SERVICE_TYPE = new UDAServiceType("WANIPConnection", 1);
        PPP_SERVICE_TYPE = new UDAServiceType("WANPPPConnection", 1);
    }
}
