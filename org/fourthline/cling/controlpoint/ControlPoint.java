// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.controlpoint;

import java.util.concurrent.Future;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.UpnpServiceConfiguration;

public interface ControlPoint
{
    UpnpServiceConfiguration getConfiguration();
    
    ProtocolFactory getProtocolFactory();
    
    Registry getRegistry();
    
    void search();
    
    void search(final UpnpHeader p0);
    
    void search(final int p0);
    
    void search(final UpnpHeader p0, final int p1);
    
    Future execute(final ActionCallback p0);
    
    void execute(final SubscriptionCallback p0);
}
