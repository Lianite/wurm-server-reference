// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.bind.helpers.ValidationEventImpl;
import com.sun.xml.bind.util.ValidationEventLocatorExImpl;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.PrintConversionEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.SAXException;
import java.util.Map;

public class RuntimeUtil
{
    public static final Map<Class, Class> boxToPrimitive;
    public static final Map<Class, Class> primitiveToBox;
    
    public static void handlePrintConversionException(final Object caller, final Exception e, final XMLSerializer serializer) throws SAXException {
        if (e instanceof SAXException) {
            throw (SAXException)e;
        }
        final ValidationEvent ve = new PrintConversionEventImpl(1, e.getMessage(), new ValidationEventLocatorImpl(caller), e);
        serializer.reportError(ve);
    }
    
    public static void handleTypeMismatchError(final XMLSerializer serializer, final Object parentObject, final String fieldName, final Object childObject) throws SAXException {
        final ValidationEvent ve = new ValidationEventImpl(1, Messages.TYPE_MISMATCH.format(getTypeName(parentObject), fieldName, getTypeName(childObject)), new ValidationEventLocatorExImpl(parentObject, fieldName));
        serializer.reportError(ve);
    }
    
    private static String getTypeName(final Object o) {
        return o.getClass().getName();
    }
    
    static {
        final Map<Class, Class> b = new HashMap<Class, Class>();
        b.put(Byte.TYPE, Byte.class);
        b.put(Short.TYPE, Short.class);
        b.put(Integer.TYPE, Integer.class);
        b.put(Long.TYPE, Long.class);
        b.put(Character.TYPE, Character.class);
        b.put(Boolean.TYPE, Boolean.class);
        b.put(Float.TYPE, Float.class);
        b.put(Double.TYPE, Double.class);
        b.put(Void.TYPE, Void.class);
        primitiveToBox = Collections.unmodifiableMap((Map<? extends Class, ? extends Class>)b);
        final Map<Class, Class> p = new HashMap<Class, Class>();
        for (final Map.Entry<Class, Class> e : b.entrySet()) {
            p.put(e.getValue(), e.getKey());
        }
        boxToPrimitive = Collections.unmodifiableMap((Map<? extends Class, ? extends Class>)p);
    }
    
    public static final class ToStringAdapter extends XmlAdapter<String, Object>
    {
        public Object unmarshal(final String s) {
            throw new UnsupportedOperationException();
        }
        
        public String marshal(final Object o) {
            if (o == null) {
                return null;
            }
            return o.toString();
        }
    }
}
