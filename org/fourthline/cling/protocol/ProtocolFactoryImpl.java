// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.protocol.sync.ReceivingEvent;
import org.fourthline.cling.protocol.sync.ReceivingUnsubscribe;
import org.fourthline.cling.protocol.sync.ReceivingSubscribe;
import org.fourthline.cling.protocol.sync.ReceivingAction;
import org.fourthline.cling.protocol.sync.ReceivingRetrieval;
import org.fourthline.cling.protocol.sync.SendingEvent;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.protocol.sync.SendingUnsubscribe;
import org.fourthline.cling.protocol.sync.SendingRenewal;
import org.fourthline.cling.model.NetworkAddress;
import java.util.List;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.protocol.sync.SendingSubscribe;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.protocol.sync.SendingAction;
import java.net.URL;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.protocol.async.SendingNotificationByebye;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.model.meta.LocalDevice;
import java.net.URI;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.NamedServiceType;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.protocol.async.ReceivingSearchResponse;
import org.fourthline.cling.protocol.async.ReceivingSearch;
import org.fourthline.cling.protocol.async.ReceivingNotification;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.UpnpRequest;
import java.util.logging.Level;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.UpnpService;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProtocolFactoryImpl implements ProtocolFactory
{
    private static final Logger log;
    protected final UpnpService upnpService;
    
    protected ProtocolFactoryImpl() {
        this.upnpService = null;
    }
    
    public ProtocolFactoryImpl(final UpnpService upnpService) {
        ProtocolFactoryImpl.log.fine("Creating ProtocolFactory: " + this.getClass().getName());
        this.upnpService = upnpService;
    }
    
    @Override
    public UpnpService getUpnpService() {
        return this.upnpService;
    }
    
    @Override
    public ReceivingAsync createReceivingAsync(final IncomingDatagramMessage message) throws ProtocolCreationException {
        if (ProtocolFactoryImpl.log.isLoggable(Level.FINE)) {
            ProtocolFactoryImpl.log.fine("Creating protocol for incoming asynchronous: " + message);
        }
        if (message.getOperation() instanceof UpnpRequest) {
            switch (((UpnpRequest)message.getOperation()).getMethod()) {
                case NOTIFY: {
                    return (this.isByeBye(message) || this.isSupportedServiceAdvertisement(message)) ? this.createReceivingNotification(message) : null;
                }
                case MSEARCH: {
                    return this.createReceivingSearch(message);
                }
            }
        }
        else if (message.getOperation() instanceof UpnpResponse) {
            return this.isSupportedServiceAdvertisement(message) ? this.createReceivingSearchResponse(message) : null;
        }
        throw new ProtocolCreationException("Protocol for incoming datagram message not found: " + message);
    }
    
    protected ReceivingAsync createReceivingNotification(final IncomingDatagramMessage<UpnpRequest> incomingRequest) {
        return new ReceivingNotification(this.getUpnpService(), incomingRequest);
    }
    
    protected ReceivingAsync createReceivingSearch(final IncomingDatagramMessage<UpnpRequest> incomingRequest) {
        return new ReceivingSearch(this.getUpnpService(), incomingRequest);
    }
    
    protected ReceivingAsync createReceivingSearchResponse(final IncomingDatagramMessage<UpnpResponse> incomingResponse) {
        return new ReceivingSearchResponse(this.getUpnpService(), incomingResponse);
    }
    
    protected boolean isByeBye(final IncomingDatagramMessage message) {
        final String ntsHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.NTS.getHttpName());
        return ntsHeader != null && ntsHeader.equals(NotificationSubtype.BYEBYE.getHeaderString());
    }
    
    protected boolean isSupportedServiceAdvertisement(final IncomingDatagramMessage message) {
        final ServiceType[] exclusiveServiceTypes = this.getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveServiceTypes == null) {
            return false;
        }
        if (exclusiveServiceTypes.length == 0) {
            return true;
        }
        final String usnHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.USN.getHttpName());
        if (usnHeader == null) {
            return false;
        }
        try {
            final NamedServiceType nst = NamedServiceType.valueOf(usnHeader);
            for (final ServiceType exclusiveServiceType : exclusiveServiceTypes) {
                if (nst.getServiceType().implementsVersion(exclusiveServiceType)) {
                    return true;
                }
            }
        }
        catch (InvalidValueException ex) {
            ProtocolFactoryImpl.log.finest("Not a named service type header value: " + usnHeader);
        }
        ProtocolFactoryImpl.log.fine("Service advertisement not supported, dropping it: " + usnHeader);
        return false;
    }
    
    @Override
    public ReceivingSync createReceivingSync(final StreamRequestMessage message) throws ProtocolCreationException {
        ProtocolFactoryImpl.log.fine("Creating protocol for incoming synchronous: " + message);
        if (message.getOperation().getMethod().equals(UpnpRequest.Method.GET)) {
            return this.createReceivingRetrieval(message);
        }
        if (this.getUpnpService().getConfiguration().getNamespace().isControlPath(message.getUri())) {
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.POST)) {
                return this.createReceivingAction(message);
            }
        }
        else if (this.getUpnpService().getConfiguration().getNamespace().isEventSubscriptionPath(message.getUri())) {
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.SUBSCRIBE)) {
                return this.createReceivingSubscribe(message);
            }
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.UNSUBSCRIBE)) {
                return this.createReceivingUnsubscribe(message);
            }
        }
        else if (this.getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(message.getUri())) {
            if (message.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY)) {
                return this.createReceivingEvent(message);
            }
        }
        else if (message.getUri().getPath().contains("/event/cb")) {
            ProtocolFactoryImpl.log.warning("Fixing trailing garbage in event message path: " + message.getUri().getPath());
            final String invalid = message.getUri().toString();
            message.setUri(URI.create(invalid.substring(0, invalid.indexOf("/cb") + "/cb".length())));
            if (this.getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(message.getUri()) && message.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY)) {
                return this.createReceivingEvent(message);
            }
        }
        throw new ProtocolCreationException("Protocol for message type not found: " + message);
    }
    
    @Override
    public SendingNotificationAlive createSendingNotificationAlive(final LocalDevice localDevice) {
        return new SendingNotificationAlive(this.getUpnpService(), localDevice);
    }
    
    @Override
    public SendingNotificationByebye createSendingNotificationByebye(final LocalDevice localDevice) {
        return new SendingNotificationByebye(this.getUpnpService(), localDevice);
    }
    
    @Override
    public SendingSearch createSendingSearch(final UpnpHeader searchTarget, final int mxSeconds) {
        return new SendingSearch(this.getUpnpService(), searchTarget, mxSeconds);
    }
    
    @Override
    public SendingAction createSendingAction(final ActionInvocation actionInvocation, final URL controlURL) {
        return new SendingAction(this.getUpnpService(), actionInvocation, controlURL);
    }
    
    @Override
    public SendingSubscribe createSendingSubscribe(final RemoteGENASubscription subscription) throws ProtocolCreationException {
        try {
            final List<NetworkAddress> activeStreamServers = this.getUpnpService().getRouter().getActiveStreamServers(((Device<RemoteDeviceIdentity, D, S>)((Service<RemoteDevice, S>)subscription.getService()).getDevice()).getIdentity().getDiscoveredOnLocalAddress());
            return new SendingSubscribe(this.getUpnpService(), subscription, activeStreamServers);
        }
        catch (RouterException ex) {
            throw new ProtocolCreationException("Failed to obtain local stream servers (for event callback URL creation) from router", ex);
        }
    }
    
    @Override
    public SendingRenewal createSendingRenewal(final RemoteGENASubscription subscription) {
        return new SendingRenewal(this.getUpnpService(), subscription);
    }
    
    @Override
    public SendingUnsubscribe createSendingUnsubscribe(final RemoteGENASubscription subscription) {
        return new SendingUnsubscribe(this.getUpnpService(), subscription);
    }
    
    @Override
    public SendingEvent createSendingEvent(final LocalGENASubscription subscription) {
        return new SendingEvent(this.getUpnpService(), subscription);
    }
    
    protected ReceivingRetrieval createReceivingRetrieval(final StreamRequestMessage message) {
        return new ReceivingRetrieval(this.getUpnpService(), message);
    }
    
    protected ReceivingAction createReceivingAction(final StreamRequestMessage message) {
        return new ReceivingAction(this.getUpnpService(), message);
    }
    
    protected ReceivingSubscribe createReceivingSubscribe(final StreamRequestMessage message) {
        return new ReceivingSubscribe(this.getUpnpService(), message);
    }
    
    protected ReceivingUnsubscribe createReceivingUnsubscribe(final StreamRequestMessage message) {
        return new ReceivingUnsubscribe(this.getUpnpService(), message);
    }
    
    protected ReceivingEvent createReceivingEvent(final StreamRequestMessage message) {
        return new ReceivingEvent(this.getUpnpService(), message);
    }
    
    static {
        log = Logger.getLogger(ProtocolFactory.class.getName());
    }
}
