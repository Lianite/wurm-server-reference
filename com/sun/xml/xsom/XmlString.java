// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom;

import org.relaxng.datatype.ValidationContext;

public final class XmlString
{
    public final String value;
    public final ValidationContext context;
    private static final ValidationContext NULL_CONTEXT;
    
    public XmlString(final String value, final ValidationContext context) {
        this.value = value;
        this.context = context;
        if (context == null) {
            throw new IllegalArgumentException();
        }
    }
    
    public XmlString(final String value) {
        this(value, XmlString.NULL_CONTEXT);
    }
    
    public final String resolvePrefix(final String prefix) {
        return this.context.resolveNamespacePrefix(prefix);
    }
    
    public String toString() {
        return this.value;
    }
    
    static {
        NULL_CONTEXT = new ValidationContext() {
            public String resolveNamespacePrefix(final String s) {
                if (s.length() == 0) {
                    return "";
                }
                if (s.equals("xml")) {
                    return "http://www.w3.org/XML/1998/namespace";
                }
                return null;
            }
            
            public String getBaseUri() {
                return null;
            }
            
            public boolean isUnparsedEntity(final String s) {
                return false;
            }
            
            public boolean isNotation(final String s) {
                return false;
            }
        };
    }
}
