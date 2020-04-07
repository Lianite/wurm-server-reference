// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.resource;

import java.net.URI;
import org.fourthline.cling.model.meta.LocalService;

public class ServiceControlResource extends Resource<LocalService>
{
    public ServiceControlResource(final URI localURI, final LocalService model) {
        super(localURI, model);
    }
}
