// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import org.fourthline.cling.model.ModelUtil;
import java.util.UUID;
import java.util.logging.Logger;

public class UDN
{
    private static final Logger log;
    public static final String PREFIX = "uuid:";
    private String identifierString;
    
    public UDN(final String identifierString) {
        this.identifierString = identifierString;
    }
    
    public UDN(final UUID uuid) {
        this.identifierString = uuid.toString();
    }
    
    public boolean isUDA11Compliant() {
        try {
            UUID.fromString(this.identifierString);
            return true;
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
    }
    
    public String getIdentifierString() {
        return this.identifierString;
    }
    
    public static UDN valueOf(final String udnString) {
        return new UDN(udnString.startsWith("uuid:") ? udnString.substring("uuid:".length()) : udnString);
    }
    
    public static UDN uniqueSystemIdentifier(final String salt) {
        final StringBuilder systemSalt = new StringBuilder();
        if (!ModelUtil.ANDROID_RUNTIME) {
            Label_0054: {
                try {
                    systemSalt.append(new String(ModelUtil.getFirstNetworkInterfaceHardwareAddress(), "UTF-8"));
                    break Label_0054;
                }
                catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException("This method does not create a unique identifier on Android, see the Javadoc and use new UDN(UUID) instead!");
                try {
                    final byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes("UTF-8"));
                    return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
                }
                catch (Exception ex2) {
                    throw new RuntimeException(ex2);
                }
            }
        }
        throw new RuntimeException("This method does not create a unique identifier on Android, see the Javadoc and use new UDN(UUID) instead!");
    }
    
    @Override
    public String toString() {
        return "uuid:" + this.getIdentifierString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof UDN)) {
            return false;
        }
        final UDN udn = (UDN)o;
        return this.identifierString.equals(udn.identifierString);
    }
    
    @Override
    public int hashCode() {
        return this.identifierString.hashCode();
    }
    
    static {
        log = Logger.getLogger(UDN.class.getName());
    }
}
