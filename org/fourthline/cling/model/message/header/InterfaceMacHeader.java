// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.seamless.util.io.HexBin;

public class InterfaceMacHeader extends UpnpHeader<byte[]>
{
    public InterfaceMacHeader() {
    }
    
    public InterfaceMacHeader(final byte[] value) {
        this.setValue(value);
    }
    
    public InterfaceMacHeader(final String s) {
        this.setString(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        final byte[] bytes = HexBin.stringToBytes(s, ":");
        this.setValue(bytes);
        if (bytes.length != 6) {
            throw new InvalidHeaderException("Invalid MAC address: " + s);
        }
    }
    
    @Override
    public String getString() {
        return HexBin.bytesToString(this.getValue(), ":");
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") '" + this.getString() + "'";
    }
}
