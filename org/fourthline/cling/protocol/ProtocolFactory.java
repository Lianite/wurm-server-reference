// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.protocol;

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
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.UpnpService;

public interface ProtocolFactory
{
    UpnpService getUpnpService();
    
    ReceivingAsync createReceivingAsync(final IncomingDatagramMessage p0) throws ProtocolCreationException;
    
    ReceivingSync createReceivingSync(final StreamRequestMessage p0) throws ProtocolCreationException;
    
    SendingNotificationAlive createSendingNotificationAlive(final LocalDevice p0);
    
    SendingNotificationByebye createSendingNotificationByebye(final LocalDevice p0);
    
    SendingSearch createSendingSearch(final UpnpHeader p0, final int p1);
    
    SendingAction createSendingAction(final ActionInvocation p0, final URL p1);
    
    SendingSubscribe createSendingSubscribe(final RemoteGENASubscription p0) throws ProtocolCreationException;
    
    SendingRenewal createSendingRenewal(final RemoteGENASubscription p0);
    
    SendingUnsubscribe createSendingUnsubscribe(final RemoteGENASubscription p0);
    
    SendingEvent createSendingEvent(final LocalGENASubscription p0);
}
