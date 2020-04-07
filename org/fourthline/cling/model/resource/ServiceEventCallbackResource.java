// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.resource;

import java.net.URI;
import org.fourthline.cling.model.meta.RemoteService;

public class ServiceEventCallbackResource extends Resource<RemoteService>
{
    public ServiceEventCallbackResource(final URI localURI, final RemoteService model) {
        super(localURI, model);
    }
}
