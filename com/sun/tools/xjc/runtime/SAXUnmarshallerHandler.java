// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.SAXException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.UnmarshallerHandler;

public interface SAXUnmarshallerHandler extends UnmarshallerHandler
{
    void handleEvent(final ValidationEvent p0, final boolean p1) throws SAXException;
}
