// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.controlpoint;

import org.fourthline.cling.protocol.sync.SendingSubscribe;
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import org.fourthline.cling.model.gena.CancelReason;
import java.net.URL;
import java.util.List;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import java.util.Collections;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.meta.Service;
import java.util.logging.Logger;

public abstract class SubscriptionCallback implements Runnable
{
    protected static Logger log;
    protected final Service service;
    protected final Integer requestedDurationSeconds;
    private ControlPoint controlPoint;
    private GENASubscription subscription;
    
    protected SubscriptionCallback(final Service service) {
        this.service = service;
        this.requestedDurationSeconds = 1800;
    }
    
    protected SubscriptionCallback(final Service service, final int requestedDurationSeconds) {
        this.service = service;
        this.requestedDurationSeconds = requestedDurationSeconds;
    }
    
    public Service getService() {
        return this.service;
    }
    
    public synchronized ControlPoint getControlPoint() {
        return this.controlPoint;
    }
    
    public synchronized void setControlPoint(final ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }
    
    public synchronized GENASubscription getSubscription() {
        return this.subscription;
    }
    
    public synchronized void setSubscription(final GENASubscription subscription) {
        this.subscription = subscription;
    }
    
    @Override
    public synchronized void run() {
        if (this.getControlPoint() == null) {
            throw new IllegalStateException("Callback must be executed through ControlPoint");
        }
        if (this.getService() instanceof LocalService) {
            this.establishLocalSubscription((LocalService)this.service);
        }
        else if (this.getService() instanceof RemoteService) {
            this.establishRemoteSubscription((RemoteService)this.service);
        }
    }
    
    private void establishLocalSubscription(final LocalService service) {
        if (this.getControlPoint().getRegistry().getLocalDevice(service.getDevice().getIdentity().getUdn(), false) == null) {
            SubscriptionCallback.log.fine("Local device service is currently not registered, failing subscription immediately");
            this.failed(null, null, new IllegalStateException("Local device is not registered"));
            return;
        }
        LocalGENASubscription localSubscription = null;
        try {
            localSubscription = new LocalGENASubscription(service, Integer.MAX_VALUE, Collections.EMPTY_LIST) {
                public void failed(final Exception ex) {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.this.setSubscription(null);
                        SubscriptionCallback.this.failed(null, null, ex);
                    }
                }
                
                @Override
                public void established() {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.this.setSubscription(this);
                        SubscriptionCallback.this.established(this);
                    }
                }
                
                @Override
                public void ended(final CancelReason reason) {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.this.setSubscription(null);
                        SubscriptionCallback.this.ended(this, reason, null);
                    }
                }
                
                @Override
                public void eventReceived() {
                    synchronized (SubscriptionCallback.this) {
                        SubscriptionCallback.log.fine("Local service state updated, notifying callback, sequence is: " + this.getCurrentSequence());
                        SubscriptionCallback.this.eventReceived(this);
                        this.incrementSequence();
                    }
                }
            };
            SubscriptionCallback.log.fine("Local device service is currently registered, also registering subscription");
            this.getControlPoint().getRegistry().addLocalSubscription(localSubscription);
            SubscriptionCallback.log.fine("Notifying subscription callback of local subscription availablity");
            localSubscription.establish();
            SubscriptionCallback.log.fine("Simulating first initial event for local subscription callback, sequence: " + localSubscription.getCurrentSequence());
            this.eventReceived(localSubscription);
            localSubscription.incrementSequence();
            SubscriptionCallback.log.fine("Starting to monitor state changes of local service");
            localSubscription.registerOnService();
        }
        catch (Exception ex) {
            SubscriptionCallback.log.fine("Local callback creation failed: " + ex.toString());
            SubscriptionCallback.log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            if (localSubscription != null) {
                this.getControlPoint().getRegistry().removeLocalSubscription(localSubscription);
            }
            this.failed(localSubscription, null, ex);
        }
    }
    
    private void establishRemoteSubscription(final RemoteService service) {
        final RemoteGENASubscription remoteSubscription = new RemoteGENASubscription(service, (int)this.requestedDurationSeconds) {
            @Override
            public void failed(final UpnpResponse responseStatus) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.setSubscription(null);
                    SubscriptionCallback.this.failed(this, responseStatus, null);
                }
            }
            
            @Override
            public void established() {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.setSubscription(this);
                    SubscriptionCallback.this.established(this);
                }
            }
            
            @Override
            public void ended(final CancelReason reason, final UpnpResponse responseStatus) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.setSubscription(null);
                    SubscriptionCallback.this.ended(this, reason, responseStatus);
                }
            }
            
            @Override
            public void eventReceived() {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.eventReceived(this);
                }
            }
            
            @Override
            public void eventsMissed(final int numberOfMissedEvents) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.eventsMissed(this, numberOfMissedEvents);
                }
            }
            
            @Override
            public void invalidMessage(final UnsupportedDataException ex) {
                synchronized (SubscriptionCallback.this) {
                    SubscriptionCallback.this.invalidMessage(this, ex);
                }
            }
        };
        SendingSubscribe protocol;
        try {
            protocol = this.getControlPoint().getProtocolFactory().createSendingSubscribe(remoteSubscription);
        }
        catch (ProtocolCreationException ex) {
            this.failed(this.subscription, null, ex);
            return;
        }
        protocol.run();
    }
    
    public synchronized void end() {
        if (this.subscription == null) {
            return;
        }
        if (this.subscription instanceof LocalGENASubscription) {
            this.endLocalSubscription((LocalGENASubscription)this.subscription);
        }
        else if (this.subscription instanceof RemoteGENASubscription) {
            this.endRemoteSubscription((RemoteGENASubscription)this.subscription);
        }
    }
    
    private void endLocalSubscription(final LocalGENASubscription subscription) {
        SubscriptionCallback.log.fine("Removing local subscription and ending it in callback: " + subscription);
        this.getControlPoint().getRegistry().removeLocalSubscription(subscription);
        subscription.end(null);
    }
    
    private void endRemoteSubscription(final RemoteGENASubscription subscription) {
        SubscriptionCallback.log.fine("Ending remote subscription: " + subscription);
        this.getControlPoint().getConfiguration().getSyncProtocolExecutorService().execute(this.getControlPoint().getProtocolFactory().createSendingUnsubscribe(subscription));
    }
    
    protected void failed(final GENASubscription subscription, final UpnpResponse responseStatus, final Exception exception) {
        this.failed(subscription, responseStatus, exception, createDefaultFailureMessage(responseStatus, exception));
    }
    
    protected abstract void failed(final GENASubscription p0, final UpnpResponse p1, final Exception p2, final String p3);
    
    protected abstract void established(final GENASubscription p0);
    
    protected abstract void ended(final GENASubscription p0, final CancelReason p1, final UpnpResponse p2);
    
    protected abstract void eventReceived(final GENASubscription p0);
    
    protected abstract void eventsMissed(final GENASubscription p0, final int p1);
    
    public static String createDefaultFailureMessage(final UpnpResponse responseStatus, final Exception exception) {
        String message = "Subscription failed: ";
        if (responseStatus != null) {
            message = message + " HTTP response was: " + responseStatus.getResponseDetails();
        }
        else if (exception != null) {
            message = message + " Exception occured: " + exception;
        }
        else {
            message += " No response received.";
        }
        return message;
    }
    
    protected void invalidMessage(final RemoteGENASubscription remoteGENASubscription, final UnsupportedDataException ex) {
        SubscriptionCallback.log.info("Invalid event message received, causing: " + ex);
        if (SubscriptionCallback.log.isLoggable(Level.FINE)) {
            SubscriptionCallback.log.fine("------------------------------------------------------------------------------");
            SubscriptionCallback.log.fine((ex.getData() != null) ? ex.getData().toString() : "null");
            SubscriptionCallback.log.fine("------------------------------------------------------------------------------");
        }
    }
    
    @Override
    public String toString() {
        return "(SubscriptionCallback) " + this.getService();
    }
    
    static {
        SubscriptionCallback.log = Logger.getLogger(SubscriptionCallback.class.getName());
    }
}
