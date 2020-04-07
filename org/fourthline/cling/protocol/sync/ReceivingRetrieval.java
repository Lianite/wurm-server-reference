// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.binding.xml.ServiceDescriptorBinder;
import org.fourthline.cling.binding.xml.DeviceDescriptorBinder;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.binding.xml.DescriptorBindingException;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.resource.IconResource;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.resource.ServiceDescriptorResource;
import org.fourthline.cling.model.message.header.ContentTypeHeader;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.resource.DeviceDescriptorResource;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.resource.Resource;
import java.net.URI;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ReceivingSync;

public class ReceivingRetrieval extends ReceivingSync<StreamRequestMessage, StreamResponseMessage>
{
    private static final Logger log;
    
    public ReceivingRetrieval(final UpnpService upnpService, final StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }
    
    @Override
    protected StreamResponseMessage executeSync() throws RouterException {
        if (!((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().hasHostHeader()) {
            ReceivingRetrieval.log.fine("Ignoring message, missing HOST header: " + ((ReceivingAsync<Object>)this).getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        final URI requestedURI = ((ReceivingAsync<StreamRequestMessage>)this).getInputMessage().getOperation().getURI();
        Resource foundResource = this.getUpnpService().getRegistry().getResource(requestedURI);
        if (foundResource == null) {
            foundResource = this.onResourceNotFound(requestedURI);
            if (foundResource == null) {
                ReceivingRetrieval.log.fine("No local resource found: " + ((ReceivingAsync<Object>)this).getInputMessage());
                return null;
            }
        }
        return this.createResponse(requestedURI, foundResource);
    }
    
    protected StreamResponseMessage createResponse(final URI requestedURI, final Resource resource) {
        StreamResponseMessage response;
        try {
            if (DeviceDescriptorResource.class.isAssignableFrom(resource.getClass())) {
                ReceivingRetrieval.log.fine("Found local device matching relative request URI: " + requestedURI);
                final LocalDevice device = resource.getModel();
                final DeviceDescriptorBinder deviceDescriptorBinder = this.getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();
                final String deviceDescriptor = deviceDescriptorBinder.generate(device, this.getRemoteClientInfo(), this.getUpnpService().getConfiguration().getNamespace());
                response = new StreamResponseMessage(deviceDescriptor, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE));
            }
            else if (ServiceDescriptorResource.class.isAssignableFrom(resource.getClass())) {
                ReceivingRetrieval.log.fine("Found local service matching relative request URI: " + requestedURI);
                final LocalService service = (LocalService)resource.getModel();
                final ServiceDescriptorBinder serviceDescriptorBinder = this.getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();
                final String serviceDescriptor = serviceDescriptorBinder.generate(service);
                response = new StreamResponseMessage(serviceDescriptor, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE));
            }
            else {
                if (!IconResource.class.isAssignableFrom(resource.getClass())) {
                    ReceivingRetrieval.log.fine("Ignoring GET for found local resource: " + resource);
                    return null;
                }
                ReceivingRetrieval.log.fine("Found local icon matching relative request URI: " + requestedURI);
                final Icon icon = (Icon)resource.getModel();
                response = new StreamResponseMessage(icon.getData(), icon.getMimeType());
            }
        }
        catch (DescriptorBindingException ex) {
            ReceivingRetrieval.log.warning("Error generating requested device/service descriptor: " + ex.toString());
            ReceivingRetrieval.log.log(Level.WARNING, "Exception root cause: ", Exceptions.unwrap(ex));
            response = new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        response.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        return response;
    }
    
    protected Resource onResourceNotFound(final URI requestedURIPath) {
        return null;
    }
    
    static {
        log = Logger.getLogger(ReceivingRetrieval.class.getName());
    }
}
