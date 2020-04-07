// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DLNADoc
{
    public static final Pattern PATTERN;
    private final String devClass;
    private final String version;
    
    public DLNADoc(final String devClass, final String version) {
        this.devClass = devClass;
        this.version = version;
    }
    
    public DLNADoc(final String devClass, final Version version) {
        this.devClass = devClass;
        this.version = version.s;
    }
    
    public String getDevClass() {
        return this.devClass;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public static DLNADoc valueOf(final String s) throws InvalidValueException {
        final Matcher matcher = DLNADoc.PATTERN.matcher(s);
        if (matcher.matches()) {
            return new DLNADoc(matcher.group(1), matcher.group(2));
        }
        throw new InvalidValueException("Can't parse DLNADoc: " + s);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DLNADoc dlnaDoc = (DLNADoc)o;
        return this.devClass.equals(dlnaDoc.devClass) && this.version.equals(dlnaDoc.version);
    }
    
    @Override
    public int hashCode() {
        int result = this.devClass.hashCode();
        result = 31 * result + this.version.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return this.getDevClass() + "-" + this.getVersion();
    }
    
    static {
        PATTERN = Pattern.compile("(.+?)[ -]([0-9].[0-9]{2})");
    }
    
    public enum Version
    {
        V1_0("1.00"), 
        V1_5("1.50");
        
        String s;
        
        private Version(final String s) {
            this.s = s;
        }
        
        @Override
        public String toString() {
            return this.s;
        }
    }
}
