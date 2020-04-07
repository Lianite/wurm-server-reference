// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;
import java.net.URI;

public class RemoteService extends Service<RemoteDevice, RemoteService>
{
    private final URI descriptorURI;
    private final URI controlURI;
    private final URI eventSubscriptionURI;
    
    public RemoteService(final ServiceType serviceType, final ServiceId serviceId, final URI descriptorURI, final URI controlURI, final URI eventSubscriptionURI) throws ValidationException {
        this(serviceType, serviceId, descriptorURI, controlURI, eventSubscriptionURI, null, null);
    }
    
    public RemoteService(final ServiceType serviceType, final ServiceId serviceId, final URI descriptorURI, final URI controlURI, final URI eventSubscriptionURI, final Action<RemoteService>[] actions, final StateVariable<RemoteService>[] stateVariables) throws ValidationException {
        super(serviceType, serviceId, actions, stateVariables);
        this.descriptorURI = descriptorURI;
        this.controlURI = controlURI;
        this.eventSubscriptionURI = eventSubscriptionURI;
        final List<ValidationError> errors = this.validateThis();
        if (errors.size() > 0) {
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", errors);
        }
    }
    
    @Override
    public Action getQueryStateVariableAction() {
        return new QueryStateVariableAction(this);
    }
    
    public URI getDescriptorURI() {
        return this.descriptorURI;
    }
    
    public URI getControlURI() {
        return this.controlURI;
    }
    
    public URI getEventSubscriptionURI() {
        return this.eventSubscriptionURI;
    }
    
    public List<ValidationError> validateThis() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getDescriptorURI() == null) {
            errors.add(new ValidationError(this.getClass(), "descriptorURI", "Descriptor location (SCPDURL) is required"));
        }
        if (this.getControlURI() == null) {
            errors.add(new ValidationError(this.getClass(), "controlURI", "Control URL is required"));
        }
        if (this.getEventSubscriptionURI() == null) {
            errors.add(new ValidationError(this.getClass(), "eventSubscriptionURI", "Event subscription URL is required"));
        }
        return errors;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") Descriptor: " + this.getDescriptorURI();
    }
}
