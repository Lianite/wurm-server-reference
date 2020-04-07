// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.gena;

import org.fourthline.cling.model.meta.StateVariable;
import java.util.HashSet;
import java.util.Set;
import java.beans.PropertyChangeEvent;
import org.seamless.util.Exceptions;
import java.util.Iterator;
import java.util.Collection;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import java.util.UUID;
import java.util.logging.Level;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import java.beans.PropertyChangeListener;
import org.fourthline.cling.model.meta.LocalService;

public abstract class LocalGENASubscription extends GENASubscription<LocalService> implements PropertyChangeListener
{
    private static Logger log;
    final List<URL> callbackURLs;
    final Map<String, Long> lastSentTimestamp;
    final Map<String, Long> lastSentNumericValue;
    
    protected LocalGENASubscription(final LocalService service, final List<URL> callbackURLs) throws Exception {
        super(service);
        this.lastSentTimestamp = new HashMap<String, Long>();
        this.lastSentNumericValue = new HashMap<String, Long>();
        this.callbackURLs = callbackURLs;
    }
    
    public LocalGENASubscription(final LocalService service, final Integer requestedDurationSeconds, final List<URL> callbackURLs) throws Exception {
        super(service);
        this.lastSentTimestamp = new HashMap<String, Long>();
        this.lastSentNumericValue = new HashMap<String, Long>();
        this.setSubscriptionDuration(requestedDurationSeconds);
        LocalGENASubscription.log.fine("Reading initial state of local service at subscription time");
        final long currentTime = new Date().getTime();
        this.currentValues.clear();
        final Collection<StateVariableValue> values = (Collection<StateVariableValue>)this.getService().getManager().getCurrentState();
        LocalGENASubscription.log.finer("Got evented state variable values: " + values.size());
        for (final StateVariableValue value : values) {
            this.currentValues.put(value.getStateVariable().getName(), value);
            if (LocalGENASubscription.log.isLoggable(Level.FINEST)) {
                LocalGENASubscription.log.finer("Read state variable value '" + value.getStateVariable().getName() + "': " + value.toString());
            }
            this.lastSentTimestamp.put(value.getStateVariable().getName(), currentTime);
            if (value.getStateVariable().isModeratedNumericType()) {
                this.lastSentNumericValue.put(value.getStateVariable().getName(), Long.valueOf(value.toString()));
            }
        }
        this.subscriptionId = "uuid:" + UUID.randomUUID();
        this.currentSequence = new UnsignedIntegerFourBytes(0L);
        this.callbackURLs = callbackURLs;
    }
    
    public synchronized List<URL> getCallbackURLs() {
        return this.callbackURLs;
    }
    
    public synchronized void registerOnService() {
        this.getService().getManager().getPropertyChangeSupport().addPropertyChangeListener(this);
    }
    
    public synchronized void establish() {
        this.established();
    }
    
    public synchronized void end(final CancelReason reason) {
        try {
            this.getService().getManager().getPropertyChangeSupport().removePropertyChangeListener(this);
        }
        catch (Exception ex) {
            LocalGENASubscription.log.warning("Removal of local service property change listener failed: " + Exceptions.unwrap(ex));
        }
        this.ended(reason);
    }
    
    @Override
    public synchronized void propertyChange(final PropertyChangeEvent e) {
        if (!e.getPropertyName().equals("_EventedStateVariables")) {
            return;
        }
        LocalGENASubscription.log.fine("Eventing triggered, getting state for subscription: " + this.getSubscriptionId());
        final long currentTime = new Date().getTime();
        final Collection<StateVariableValue> newValues = (Collection<StateVariableValue>)e.getNewValue();
        final Set<String> excludedVariables = this.moderateStateVariables(currentTime, newValues);
        this.currentValues.clear();
        for (final StateVariableValue newValue : newValues) {
            final String name = newValue.getStateVariable().getName();
            if (!excludedVariables.contains(name)) {
                LocalGENASubscription.log.fine("Adding state variable value to current values of event: " + newValue.getStateVariable() + " = " + newValue);
                this.currentValues.put(newValue.getStateVariable().getName(), newValue);
                this.lastSentTimestamp.put(name, currentTime);
                if (!newValue.getStateVariable().isModeratedNumericType()) {
                    continue;
                }
                this.lastSentNumericValue.put(name, Long.valueOf(newValue.toString()));
            }
        }
        if (this.currentValues.size() > 0) {
            LocalGENASubscription.log.fine("Propagating new state variable values to subscription: " + this);
            this.eventReceived();
        }
        else {
            LocalGENASubscription.log.fine("No state variable values for event (all moderated out?), not triggering event");
        }
    }
    
    protected synchronized Set<String> moderateStateVariables(final long currentTime, final Collection<StateVariableValue> values) {
        final Set<String> excludedVariables = new HashSet<String>();
        for (final StateVariableValue stateVariableValue : values) {
            final StateVariable stateVariable = stateVariableValue.getStateVariable();
            final String stateVariableName = stateVariableValue.getStateVariable().getName();
            if (stateVariable.getEventDetails().getEventMaximumRateMilliseconds() == 0 && stateVariable.getEventDetails().getEventMinimumDelta() == 0) {
                LocalGENASubscription.log.finer("Variable is not moderated: " + stateVariable);
            }
            else if (!this.lastSentTimestamp.containsKey(stateVariableName)) {
                LocalGENASubscription.log.finer("Variable is moderated but was never sent before: " + stateVariable);
            }
            else {
                if (stateVariable.getEventDetails().getEventMaximumRateMilliseconds() > 0) {
                    final long timestampLastSent = this.lastSentTimestamp.get(stateVariableName);
                    final long timestampNextSend = timestampLastSent + stateVariable.getEventDetails().getEventMaximumRateMilliseconds();
                    if (currentTime <= timestampNextSend) {
                        LocalGENASubscription.log.finer("Excluding state variable with maximum rate: " + stateVariable);
                        excludedVariables.add(stateVariableName);
                        continue;
                    }
                }
                if (!stateVariable.isModeratedNumericType() || this.lastSentNumericValue.get(stateVariableName) == null) {
                    continue;
                }
                final long oldValue = Long.valueOf(this.lastSentNumericValue.get(stateVariableName));
                final long newValue = Long.valueOf(stateVariableValue.toString());
                final long minDelta = stateVariable.getEventDetails().getEventMinimumDelta();
                if (newValue > oldValue && newValue - oldValue < minDelta) {
                    LocalGENASubscription.log.finer("Excluding state variable with minimum delta: " + stateVariable);
                    excludedVariables.add(stateVariableName);
                }
                else {
                    if (newValue >= oldValue || oldValue - newValue >= minDelta) {
                        continue;
                    }
                    LocalGENASubscription.log.finer("Excluding state variable with minimum delta: " + stateVariable);
                    excludedVariables.add(stateVariableName);
                }
            }
        }
        return excludedVariables;
    }
    
    public synchronized void incrementSequence() {
        this.currentSequence.increment(true);
    }
    
    public synchronized void setSubscriptionDuration(final Integer requestedDurationSeconds) {
        this.setActualSubscriptionDurationSeconds(this.requestedDurationSeconds = ((requestedDurationSeconds == null) ? 1800 : requestedDurationSeconds));
    }
    
    public abstract void ended(final CancelReason p0);
    
    static {
        LocalGENASubscription.log = Logger.getLogger(LocalGENASubscription.class.getName());
    }
}
