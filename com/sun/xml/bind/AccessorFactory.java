// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind;

import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;

public interface AccessorFactory
{
    Accessor createFieldAccessor(final Class p0, final Field p1, final boolean p2) throws JAXBException;
    
    Accessor createPropertyAccessor(final Class p0, final Method p1, final Method p2) throws JAXBException;
}
