// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

public class Main
{
    public static void main(final String[] args) throws Exception {
        final RegistryListener listener = new RegistryListener() {
            @Override
            public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
                System.out.println("Discovery started: " + device.getDisplayString());
            }
            
            @Override
            public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
                System.out.println("Discovery failed: " + device.getDisplayString() + " => " + ex);
            }
            
            @Override
            public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
                System.out.println("Remote device available: " + device.getDisplayString());
            }
            
            @Override
            public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
                System.out.println("Remote device updated: " + device.getDisplayString());
            }
            
            @Override
            public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
                System.out.println("Remote device removed: " + device.getDisplayString());
            }
            
            @Override
            public void localDeviceAdded(final Registry registry, final LocalDevice device) {
                System.out.println("Local device added: " + device.getDisplayString());
            }
            
            @Override
            public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
                System.out.println("Local device removed: " + device.getDisplayString());
            }
            
            @Override
            public void beforeShutdown(final Registry registry) {
                System.out.println("Before shutdown, the registry has devices: " + registry.getDevices().size());
            }
            
            @Override
            public void afterShutdown() {
                System.out.println("Shutdown of registry complete!");
            }
        };
        System.out.println("Starting Cling...");
        final UpnpService upnpService = new UpnpServiceImpl(new RegistryListener[] { listener });
        System.out.println("Sending SEARCH message to all devices...");
        upnpService.getControlPoint().search(new STAllHeader());
        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(10000L);
        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }
}
