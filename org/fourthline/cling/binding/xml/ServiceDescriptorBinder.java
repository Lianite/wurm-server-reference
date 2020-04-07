// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.xml;

import org.w3c.dom.Document;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Service;

public interface ServiceDescriptorBinder
{
     <T extends Service> T describe(final T p0, final String p1) throws DescriptorBindingException, ValidationException;
    
     <T extends Service> T describe(final T p0, final Document p1) throws DescriptorBindingException, ValidationException;
    
    String generate(final Service p0) throws DescriptorBindingException;
    
    Document buildDOM(final Service p0) throws DescriptorBindingException;
}
