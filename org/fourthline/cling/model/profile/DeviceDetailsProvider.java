// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.profile;

import org.fourthline.cling.model.meta.DeviceDetails;

public interface DeviceDetailsProvider
{
    DeviceDetails provide(final RemoteClientInfo p0);
}
