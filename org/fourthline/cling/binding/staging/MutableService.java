// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

import java.util.Iterator;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.Device;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.ServiceType;

public class MutableService
{
    public ServiceType serviceType;
    public ServiceId serviceId;
    public URI descriptorURI;
    public URI controlURI;
    public URI eventSubscriptionURI;
    public List<MutableAction> actions;
    public List<MutableStateVariable> stateVariables;
    
    public MutableService() {
        this.actions = new ArrayList<MutableAction>();
        this.stateVariables = new ArrayList<MutableStateVariable>();
    }
    
    public Service build(final Device prototype) throws ValidationException {
        return prototype.newInstance(this.serviceType, this.serviceId, this.descriptorURI, this.controlURI, this.eventSubscriptionURI, this.createActions(), this.createStateVariables());
    }
    
    public Action[] createActions() {
        final Action[] array = new Action[this.actions.size()];
        int i = 0;
        for (final MutableAction action : this.actions) {
            array[i++] = action.build();
        }
        return array;
    }
    
    public StateVariable[] createStateVariables() {
        final StateVariable[] array = new StateVariable[this.stateVariables.size()];
        int i = 0;
        for (final MutableStateVariable stateVariable : this.stateVariables) {
            array[i++] = stateVariable.build();
        }
        return array;
    }
}
