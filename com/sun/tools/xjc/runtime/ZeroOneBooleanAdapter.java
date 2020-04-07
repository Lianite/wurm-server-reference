// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ZeroOneBooleanAdapter extends XmlAdapter<String, Boolean>
{
    public Boolean unmarshal(final String v) {
        if (v == null) {
            return null;
        }
        return DatatypeConverter.parseBoolean(v);
    }
    
    public String marshal(final Boolean v) {
        if (v == null) {
            return null;
        }
        if (v) {
            return "1";
        }
        return "0";
    }
}
