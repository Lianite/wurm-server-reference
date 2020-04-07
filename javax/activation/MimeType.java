// 
// Decompiled by Procyon v0.5.30
// 

package javax.activation;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Locale;
import java.io.Externalizable;

public class MimeType implements Externalizable
{
    private String primaryType;
    private String subType;
    private MimeTypeParameterList parameters;
    private static final String TSPECIALS = "()<>@,;:/[]?=\\\"";
    
    public MimeType() {
        this.primaryType = "application";
        this.subType = "*";
        this.parameters = new MimeTypeParameterList();
    }
    
    public MimeType(final String rawdata) throws MimeTypeParseException {
        this.parse(rawdata);
    }
    
    public MimeType(final String primary, final String sub) throws MimeTypeParseException {
        if (!this.isValidToken(primary)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        this.primaryType = primary.toLowerCase(Locale.ENGLISH);
        if (this.isValidToken(sub)) {
            this.subType = sub.toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList();
            return;
        }
        throw new MimeTypeParseException("Sub type is invalid.");
    }
    
    private void parse(final String rawdata) throws MimeTypeParseException {
        final int slashIndex = rawdata.indexOf(47);
        final int semIndex = rawdata.indexOf(59);
        if (slashIndex < 0 && semIndex < 0) {
            throw new MimeTypeParseException("Unable to find a sub type.");
        }
        if (slashIndex < 0 && semIndex >= 0) {
            throw new MimeTypeParseException("Unable to find a sub type.");
        }
        if (slashIndex >= 0 && semIndex < 0) {
            this.primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase(Locale.ENGLISH);
            this.subType = rawdata.substring(slashIndex + 1).trim().toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList();
        }
        else {
            if (slashIndex >= semIndex) {
                throw new MimeTypeParseException("Unable to find a sub type.");
            }
            this.primaryType = rawdata.substring(0, slashIndex).trim().toLowerCase(Locale.ENGLISH);
            this.subType = rawdata.substring(slashIndex + 1, semIndex).trim().toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList(rawdata.substring(semIndex));
        }
        if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
    }
    
    public String getPrimaryType() {
        return this.primaryType;
    }
    
    public void setPrimaryType(final String primary) throws MimeTypeParseException {
        if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
        }
        this.primaryType = primary.toLowerCase(Locale.ENGLISH);
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    public void setSubType(final String sub) throws MimeTypeParseException {
        if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
        }
        this.subType = sub.toLowerCase(Locale.ENGLISH);
    }
    
    public MimeTypeParameterList getParameters() {
        return this.parameters;
    }
    
    public String getParameter(final String name) {
        return this.parameters.get(name);
    }
    
    public void setParameter(final String name, final String value) {
        this.parameters.set(name, value);
    }
    
    public void removeParameter(final String name) {
        this.parameters.remove(name);
    }
    
    public String toString() {
        return this.getBaseType() + this.parameters.toString();
    }
    
    public String getBaseType() {
        return this.primaryType + "/" + this.subType;
    }
    
    public boolean match(final MimeType type) {
        return this.primaryType.equals(type.getPrimaryType()) && (this.subType.equals("*") || type.getSubType().equals("*") || this.subType.equals(type.getSubType()));
    }
    
    public boolean match(final String rawdata) throws MimeTypeParseException {
        return this.match(new MimeType(rawdata));
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeUTF(this.toString());
        out.flush();
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        try {
            this.parse(in.readUTF());
        }
        catch (MimeTypeParseException e) {
            throw new IOException(e.toString());
        }
    }
    
    private static boolean isTokenChar(final char c) {
        return c > ' ' && c < '\u007f' && "()<>@,;:/[]?=\\\"".indexOf(c) < 0;
    }
    
    private boolean isValidToken(final String s) {
        final int len = s.length();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                final char c = s.charAt(i);
                if (!isTokenChar(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
