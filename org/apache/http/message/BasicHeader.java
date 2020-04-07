// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.message;

import org.apache.http.ParseException;
import org.apache.http.HeaderElement;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.annotation.Immutable;
import java.io.Serializable;
import org.apache.http.Header;

@Immutable
public class BasicHeader implements Header, Cloneable, Serializable
{
    private static final long serialVersionUID = -5427236326487562174L;
    private final String name;
    private final String value;
    
    public BasicHeader(final String name, final String value) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public String toString() {
        return BasicLineFormatter.DEFAULT.formatHeader(null, this).toString();
    }
    
    public HeaderElement[] getElements() throws ParseException {
        if (this.value != null) {
            return BasicHeaderValueParser.parseElements(this.value, null);
        }
        return new HeaderElement[0];
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
