// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.android;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;

public interface AndroidUpnpService
{
    UpnpService get();
    
    UpnpServiceConfiguration getConfiguration();
    
    Registry getRegistry();
    
    ControlPoint getControlPoint();
}
