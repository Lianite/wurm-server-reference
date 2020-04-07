// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding;

import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.meta.LocalService;

public interface LocalServiceBinder
{
    LocalService read(final Class<?> p0) throws LocalServiceBindingException;
    
    LocalService read(final Class<?> p0, final ServiceId p1, final ServiceType p2, final boolean p3, final Class[] p4) throws LocalServiceBindingException;
}
