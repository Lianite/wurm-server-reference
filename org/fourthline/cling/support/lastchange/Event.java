// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import java.util.Iterator;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import java.util.ArrayList;
import java.util.List;

public class Event
{
    protected List<InstanceID> instanceIDs;
    
    public Event() {
        this.instanceIDs = new ArrayList<InstanceID>();
    }
    
    public Event(final List<InstanceID> instanceIDs) {
        this.instanceIDs = new ArrayList<InstanceID>();
        this.instanceIDs = instanceIDs;
    }
    
    public List<InstanceID> getInstanceIDs() {
        return this.instanceIDs;
    }
    
    public InstanceID getInstanceID(final UnsignedIntegerFourBytes id) {
        for (final InstanceID instanceID : this.instanceIDs) {
            if (instanceID.getId().equals(id)) {
                return instanceID;
            }
        }
        return null;
    }
    
    public void clear() {
        this.instanceIDs = new ArrayList<InstanceID>();
    }
    
    public void setEventedValue(final UnsignedIntegerFourBytes id, final EventedValue ev) {
        InstanceID instanceID = null;
        for (final InstanceID i : this.getInstanceIDs()) {
            if (i.getId().equals(id)) {
                instanceID = i;
            }
        }
        if (instanceID == null) {
            instanceID = new InstanceID(id);
            this.getInstanceIDs().add(instanceID);
        }
        final Iterator<EventedValue> it = (Iterator<EventedValue>)instanceID.getValues().iterator();
        while (it.hasNext()) {
            final EventedValue existingEv = it.next();
            if (existingEv.getClass().equals(ev.getClass())) {
                it.remove();
            }
        }
        instanceID.getValues().add(ev);
    }
    
    public <EV extends EventedValue> EV getEventedValue(final UnsignedIntegerFourBytes id, final Class<EV> type) {
        for (final InstanceID instanceID : this.getInstanceIDs()) {
            if (instanceID.getId().equals(id)) {
                for (final EventedValue eventedValue : instanceID.getValues()) {
                    if (eventedValue.getClass().equals(type)) {
                        return (EV)eventedValue;
                    }
                }
            }
        }
        return null;
    }
    
    public boolean hasChanges() {
        for (final InstanceID instanceID : this.instanceIDs) {
            if (instanceID.getValues().size() > 0) {
                return true;
            }
        }
        return false;
    }
}
