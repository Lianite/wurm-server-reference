// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind.annotation.adapters;

import javax.xml.bind.DatatypeConverter;

public final class HexBinaryAdapter extends XmlAdapter<String, byte[]>
{
    public byte[] unmarshal(final String s) {
        if (s == null) {
            return null;
        }
        return DatatypeConverter.parseHexBinary(s);
    }
    
    public String marshal(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return DatatypeConverter.printHexBinary(bytes);
    }
}
