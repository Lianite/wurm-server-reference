// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.seamless.util.MimeType;
import org.fourthline.cling.model.types.InvalidValueException;

public class ProtocolInfo
{
    public static final String WILDCARD = "*";
    protected Protocol protocol;
    protected String network;
    protected String contentFormat;
    protected String additionalInfo;
    
    public ProtocolInfo(String s) throws InvalidValueException {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        if (s == null) {
            throw new NullPointerException();
        }
        s = s.trim();
        final String[] split = s.split(":");
        if (split.length != 4) {
            throw new InvalidValueException("Can't parse ProtocolInfo string: " + s);
        }
        this.protocol = Protocol.value(split[0]);
        this.network = split[1];
        this.contentFormat = split[2];
        this.additionalInfo = split[3];
    }
    
    public ProtocolInfo(final MimeType contentFormatMimeType) {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        this.protocol = Protocol.HTTP_GET;
        this.contentFormat = contentFormatMimeType.toString();
    }
    
    public ProtocolInfo(final Protocol protocol, final String network, final String contentFormat, final String additionalInfo) {
        this.protocol = Protocol.ALL;
        this.network = "*";
        this.contentFormat = "*";
        this.additionalInfo = "*";
        this.protocol = protocol;
        this.network = network;
        this.contentFormat = contentFormat;
        this.additionalInfo = additionalInfo;
    }
    
    public Protocol getProtocol() {
        return this.protocol;
    }
    
    public String getNetwork() {
        return this.network;
    }
    
    public String getContentFormat() {
        return this.contentFormat;
    }
    
    public MimeType getContentFormatMimeType() throws IllegalArgumentException {
        return MimeType.valueOf(this.contentFormat);
    }
    
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ProtocolInfo that = (ProtocolInfo)o;
        return this.additionalInfo.equals(that.additionalInfo) && this.contentFormat.equals(that.contentFormat) && this.network.equals(that.network) && this.protocol == that.protocol;
    }
    
    @Override
    public int hashCode() {
        int result = this.protocol.hashCode();
        result = 31 * result + this.network.hashCode();
        result = 31 * result + this.contentFormat.hashCode();
        result = 31 * result + this.additionalInfo.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return this.protocol.toString() + ":" + this.network + ":" + this.contentFormat + ":" + this.additionalInfo;
    }
}
