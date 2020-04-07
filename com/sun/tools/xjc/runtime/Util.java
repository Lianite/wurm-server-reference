// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import com.sun.xml.bind.serializer.AbortSerializationException;
import javax.xml.bind.helpers.ValidationEventImpl;
import com.sun.xml.bind.util.ValidationEventLocatorExImpl;
import com.sun.xml.bind.Messages;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.PrintConversionEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.SAXException;

public class Util
{
    public static void handlePrintConversionException(final Object caller, final Exception e, final XMLSerializer serializer) throws SAXException {
        if (e instanceof SAXException) {
            throw (SAXException)e;
        }
        String message = e.getMessage();
        if (message == null) {
            message = e.toString();
        }
        final ValidationEvent ve = new PrintConversionEventImpl(1, message, new ValidationEventLocatorImpl(caller), e);
        serializer.reportError(ve);
    }
    
    public static void handleTypeMismatchError(final XMLSerializer serializer, final Object parentObject, final String fieldName, final Object childObject) throws AbortSerializationException {
        final ValidationEvent ve = new ValidationEventImpl(1, Messages.format("Util.TypeMismatch", (Object)getUserFriendlyTypeName(parentObject), (Object)fieldName, (Object)getUserFriendlyTypeName(childObject)), new ValidationEventLocatorExImpl(parentObject, fieldName));
        serializer.reportError(ve);
    }
    
    private static String getUserFriendlyTypeName(final Object o) {
        if (o instanceof ValidatableObject) {
            return ((ValidatableObject)o).getPrimaryInterface().getName();
        }
        return o.getClass().getName();
    }
}
