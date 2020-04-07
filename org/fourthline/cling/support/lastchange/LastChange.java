// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class LastChange
{
    private final Event event;
    private final LastChangeParser parser;
    private String previousValue;
    
    public LastChange(final String s) {
        throw new UnsupportedOperationException("This constructor is only for service binding detection");
    }
    
    public LastChange(final LastChangeParser parser, final Event event) {
        this.parser = parser;
        this.event = event;
    }
    
    public LastChange(final LastChangeParser parser) {
        this(parser, new Event());
    }
    
    public LastChange(final LastChangeParser parser, final String xml) throws Exception {
        if (xml != null && xml.length() > 0) {
            this.event = parser.parse(xml);
        }
        else {
            this.event = new Event();
        }
        this.parser = parser;
    }
    
    public synchronized void reset() {
        this.previousValue = this.toString();
        this.event.clear();
    }
    
    public synchronized void setEventedValue(final int instanceID, final EventedValue... ev) {
        this.setEventedValue(new UnsignedIntegerFourBytes(instanceID), ev);
    }
    
    public synchronized void setEventedValue(final UnsignedIntegerFourBytes instanceID, final EventedValue... ev) {
        for (final EventedValue eventedValue : ev) {
            if (eventedValue != null) {
                this.event.setEventedValue(instanceID, eventedValue);
            }
        }
    }
    
    public synchronized UnsignedIntegerFourBytes[] getInstanceIDs() {
        final List<UnsignedIntegerFourBytes> list = new ArrayList<UnsignedIntegerFourBytes>();
        for (final InstanceID instanceID : this.event.getInstanceIDs()) {
            list.add(instanceID.getId());
        }
        return list.toArray(new UnsignedIntegerFourBytes[list.size()]);
    }
    
    synchronized EventedValue[] getEventedValues(final UnsignedIntegerFourBytes instanceID) {
        final InstanceID inst = this.event.getInstanceID(instanceID);
        return (EventedValue[])((inst != null) ? ((EventedValue[])inst.getValues().toArray(new EventedValue[inst.getValues().size()])) : null);
    }
    
    public synchronized <EV extends EventedValue> EV getEventedValue(final int instanceID, final Class<EV> type) {
        return this.getEventedValue(new UnsignedIntegerFourBytes(instanceID), type);
    }
    
    public synchronized <EV extends EventedValue> EV getEventedValue(final UnsignedIntegerFourBytes id, final Class<EV> type) {
        return this.event.getEventedValue(id, type);
    }
    
    public synchronized void fire(final PropertyChangeSupport propertyChangeSupport) {
        final String lastChanges = this.toString();
        if (lastChanges != null && lastChanges.length() > 0) {
            propertyChangeSupport.firePropertyChange("LastChange", this.previousValue, lastChanges);
            this.reset();
        }
    }
    
    @Override
    public synchronized String toString() {
        if (!this.event.hasChanges()) {
            return "";
        }
        try {
            return this.parser.generate(this.event);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
