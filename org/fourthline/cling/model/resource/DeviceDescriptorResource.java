// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.resource;

import java.net.URI;
import org.fourthline.cling.model.meta.LocalDevice;

public class DeviceDescriptorResource extends Resource<LocalDevice>
{
    public DeviceDescriptorResource(final URI localURI, final LocalDevice model) {
        super(localURI, model);
    }
}
