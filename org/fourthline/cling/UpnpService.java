// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling;

import org.fourthline.cling.transport.Router;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.controlpoint.ControlPoint;

public interface UpnpService
{
    UpnpServiceConfiguration getConfiguration();
    
    ControlPoint getControlPoint();
    
    ProtocolFactory getProtocolFactory();
    
    Registry getRegistry();
    
    Router getRouter();
    
    void shutdown();
    
    public static class Start
    {
    }
    
    public static class Shutdown
    {
    }
}
