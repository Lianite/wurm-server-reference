// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MANHeader extends UpnpHeader<String>
{
    public static final Pattern PATTERN;
    public static final Pattern NAMESPACE_PATTERN;
    public String namespace;
    
    public MANHeader() {
    }
    
    public MANHeader(final String value) {
        this.setValue(value);
    }
    
    public MANHeader(final String value, final String namespace) {
        this(value);
        this.namespace = namespace;
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        final Matcher matcher = MANHeader.PATTERN.matcher(s);
        if (matcher.matches()) {
            this.setValue(matcher.group(1));
            if (matcher.group(2) != null) {
                final Matcher nsMatcher = MANHeader.NAMESPACE_PATTERN.matcher(matcher.group(2));
                if (!nsMatcher.matches()) {
                    throw new InvalidHeaderException("Invalid namespace in MAN header value: " + s);
                }
                this.setNamespace(nsMatcher.group(1));
            }
            return;
        }
        throw new InvalidHeaderException("Invalid MAN header value: " + s);
    }
    
    @Override
    public String getString() {
        if (this.getValue() == null) {
            return null;
        }
        final StringBuilder s = new StringBuilder();
        s.append("\"").append(this.getValue()).append("\"");
        if (this.getNamespace() != null) {
            s.append("; ns=").append(this.getNamespace());
        }
        return s.toString();
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }
    
    static {
        PATTERN = Pattern.compile("\"(.+?)\"(;.+?)??");
        NAMESPACE_PATTERN = Pattern.compile(";\\s?ns\\s?=\\s?([0-9]{2})");
    }
}
