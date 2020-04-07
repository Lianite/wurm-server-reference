// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.w3c.dom.Document;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;

public interface DeviceDescriptorBinder
{
     <T extends Device> T describe(final T p0, final String p1) throws DescriptorBindingException, ValidationException;
    
     <T extends Device> T describe(final T p0, final Document p1) throws DescriptorBindingException, ValidationException;
    
    String generate(final Device p0, final RemoteClientInfo p1, final Namespace p2) throws DescriptorBindingException;
    
    Document buildDOM(final Device p0, final RemoteClientInfo p1, final Namespace p2) throws DescriptorBindingException;
}
