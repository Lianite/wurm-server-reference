// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import org.seamless.util.URIUtil;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import org.fourthline.cling.model.resource.Resource;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.Device;
import java.net.URI;
import java.util.logging.Logger;

public class Namespace
{
    private static final Logger log;
    public static final String DEVICE = "/dev";
    public static final String SERVICE = "/svc";
    public static final String CONTROL = "/action";
    public static final String EVENTS = "/event";
    public static final String DESCRIPTOR_FILE = "/desc";
    public static final String CALLBACK_FILE = "/cb";
    protected final URI basePath;
    protected final String decodedPath;
    
    public Namespace() {
        this("");
    }
    
    public Namespace(final String basePath) {
        this(URI.create(basePath));
    }
    
    public Namespace(final URI basePath) {
        this.basePath = basePath;
        this.decodedPath = basePath.getPath();
    }
    
    public URI getBasePath() {
        return this.basePath;
    }
    
    public URI getPath(final Device device) {
        return this.appendPathToBaseURI(this.getDevicePath(device));
    }
    
    public URI getPath(final Service service) {
        return this.appendPathToBaseURI(this.getServicePath(service));
    }
    
    public URI getDescriptorPath(final Device device) {
        return this.appendPathToBaseURI(this.getDevicePath(device.getRoot()) + "/desc");
    }
    
    public String getDescriptorPathString(final Device device) {
        return this.decodedPath + this.getDevicePath(device.getRoot()) + "/desc";
    }
    
    public URI getDescriptorPath(final Service service) {
        return this.appendPathToBaseURI(this.getServicePath(service) + "/desc");
    }
    
    public URI getControlPath(final Service service) {
        return this.appendPathToBaseURI(this.getServicePath(service) + "/action");
    }
    
    public URI getIconPath(final Icon icon) {
        return this.appendPathToBaseURI(this.getDevicePath(icon.getDevice()) + "/" + icon.getUri().toString());
    }
    
    public URI getEventSubscriptionPath(final Service service) {
        return this.appendPathToBaseURI(this.getServicePath(service) + "/event");
    }
    
    public URI getEventCallbackPath(final Service service) {
        return this.appendPathToBaseURI(this.getServicePath(service) + "/event" + "/cb");
    }
    
    public String getEventCallbackPathString(final Service service) {
        return this.decodedPath + this.getServicePath(service) + "/event" + "/cb";
    }
    
    public URI prefixIfRelative(final Device device, final URI uri) {
        if (!uri.isAbsolute() && !uri.getPath().startsWith("/")) {
            return this.appendPathToBaseURI(this.getDevicePath(device) + "/" + uri);
        }
        return uri;
    }
    
    public boolean isControlPath(final URI uri) {
        return uri.toString().endsWith("/action");
    }
    
    public boolean isEventSubscriptionPath(final URI uri) {
        return uri.toString().endsWith("/event");
    }
    
    public boolean isEventCallbackPath(final URI uri) {
        return uri.toString().endsWith("/cb");
    }
    
    public Resource[] getResources(final Device device) throws ValidationException {
        if (!device.isRoot()) {
            return null;
        }
        final Set<Resource> resources = new HashSet<Resource>();
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        Namespace.log.fine("Discovering local resources of device graph");
        final Resource[] discoverResources;
        final Resource[] discoveredResources = discoverResources = device.discoverResources(this);
        for (final Resource resource : discoverResources) {
            Namespace.log.finer("Discovered: " + resource);
            if (!resources.add(resource)) {
                Namespace.log.finer("Local resource already exists, queueing validation error");
                errors.add(new ValidationError(this.getClass(), "resources", "Local URI namespace conflict between resources of device: " + resource));
            }
        }
        if (errors.size() > 0) {
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", errors);
        }
        return resources.toArray(new Resource[resources.size()]);
    }
    
    protected URI appendPathToBaseURI(final String path) {
        try {
            return new URI(this.basePath.getScheme(), null, this.basePath.getHost(), this.basePath.getPort(), this.decodedPath + path, null, null);
        }
        catch (URISyntaxException e) {
            return URI.create(this.basePath + path);
        }
    }
    
    protected String getDevicePath(final Device device) {
        if (device.getIdentity().getUdn() == null) {
            throw new IllegalStateException("Can't generate local URI prefix without UDN");
        }
        final StringBuilder s = new StringBuilder();
        s.append("/dev").append("/");
        s.append(URIUtil.encodePathSegment(device.getIdentity().getUdn().getIdentifierString()));
        return s.toString();
    }
    
    protected String getServicePath(final Service service) {
        if (service.getServiceId() == null) {
            throw new IllegalStateException("Can't generate local URI prefix without service ID");
        }
        final StringBuilder s = new StringBuilder();
        s.append("/svc");
        s.append("/");
        s.append(service.getServiceId().getNamespace());
        s.append("/");
        s.append(service.getServiceId().getId());
        return this.getDevicePath(service.getDevice()) + s.toString();
    }
    
    static {
        log = Logger.getLogger(Namespace.class.getName());
    }
}
