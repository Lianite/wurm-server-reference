// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.mock;

import org.fourthline.cling.protocol.sync.SendingEvent;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.protocol.sync.SendingUnsubscribe;
import org.fourthline.cling.protocol.sync.SendingRenewal;
import org.fourthline.cling.protocol.sync.SendingSubscribe;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.protocol.sync.SendingAction;
import java.net.URL;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.protocol.async.SendingNotificationByebye;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.protocol.ProtocolCreationException;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.UpnpService;
import javax.enterprise.inject.Alternative;
import org.fourthline.cling.protocol.ProtocolFactory;

@Alternative
public class MockProtocolFactory implements ProtocolFactory
{
    @Override
    public UpnpService getUpnpService() {
        return null;
    }
    
    @Override
    public ReceivingAsync createReceivingAsync(final IncomingDatagramMessage message) throws ProtocolCreationException {
        return null;
    }
    
    @Override
    public ReceivingSync createReceivingSync(final StreamRequestMessage requestMessage) throws ProtocolCreationException {
        return null;
    }
    
    @Override
    public SendingNotificationAlive createSendingNotificationAlive(final LocalDevice localDevice) {
        return null;
    }
    
    @Override
    public SendingNotificationByebye createSendingNotificationByebye(final LocalDevice localDevice) {
        return null;
    }
    
    @Override
    public SendingSearch createSendingSearch(final UpnpHeader searchTarget, final int mxSeconds) {
        return null;
    }
    
    @Override
    public SendingAction createSendingAction(final ActionInvocation actionInvocation, final URL controlURL) {
        return null;
    }
    
    @Override
    public SendingSubscribe createSendingSubscribe(final RemoteGENASubscription subscription) {
        return null;
    }
    
    @Override
    public SendingRenewal createSendingRenewal(final RemoteGENASubscription subscription) {
        return null;
    }
    
    @Override
    public SendingUnsubscribe createSendingUnsubscribe(final RemoteGENASubscription subscription) {
        return null;
    }
    
    @Override
    public SendingEvent createSendingEvent(final LocalGENASubscription subscription) {
        return null;
    }
}
