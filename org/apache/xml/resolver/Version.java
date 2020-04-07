// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver;

public class Version
{
    public static String getVersion() {
        return getProduct() + " " + getVersionNum();
    }
    
    public static String getProduct() {
        return "XmlResolver";
    }
    
    public static String getVersionNum() {
        return "1.0";
    }
    
    public static void main(final String[] array) {
        System.out.println(getVersion());
    }
}
