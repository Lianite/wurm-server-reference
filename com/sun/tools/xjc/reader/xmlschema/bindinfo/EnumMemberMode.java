// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum EnumMemberMode
{
    SKIP, 
    ERROR, 
    GENERATE;
    
    public EnumMemberMode getModeWithEnum() {
        if (this == EnumMemberMode.SKIP) {
            return EnumMemberMode.ERROR;
        }
        return this;
    }
}
