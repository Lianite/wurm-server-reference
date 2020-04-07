// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

public class BytesRange
{
    public static final String PREFIX = "bytes=";
    private Long firstByte;
    private Long lastByte;
    private Long byteLength;
    
    public BytesRange(final Long firstByte, final Long lastByte) {
        this.firstByte = firstByte;
        this.lastByte = lastByte;
        this.byteLength = null;
    }
    
    public BytesRange(final Long firstByte, final Long lastByte, final Long byteLength) {
        this.firstByte = firstByte;
        this.lastByte = lastByte;
        this.byteLength = byteLength;
    }
    
    public Long getFirstByte() {
        return this.firstByte;
    }
    
    public Long getLastByte() {
        return this.lastByte;
    }
    
    public Long getByteLength() {
        return this.byteLength;
    }
    
    public String getString() {
        return this.getString(false, null);
    }
    
    public String getString(final boolean includeDuration) {
        return this.getString(includeDuration, null);
    }
    
    public String getString(final boolean includeDuration, final String rangePrefix) {
        String s = (rangePrefix != null) ? rangePrefix : "bytes=";
        if (this.firstByte != null) {
            s += this.firstByte.toString();
        }
        s += "-";
        if (this.lastByte != null) {
            s += this.lastByte.toString();
        }
        if (includeDuration) {
            s = s + "/" + ((this.byteLength != null) ? this.byteLength.toString() : "*");
        }
        return s;
    }
    
    public static BytesRange valueOf(final String s) throws InvalidValueException {
        return valueOf(s, null);
    }
    
    public static BytesRange valueOf(final String s, final String rangePrefix) throws InvalidValueException {
        if (s.startsWith((rangePrefix != null) ? rangePrefix : "bytes=")) {
            Long firstByte = null;
            Long lastByte = null;
            Long byteLength = null;
            final String[] params = s.substring(((rangePrefix != null) ? rangePrefix : "bytes=").length()).split("[-/]");
            switch (params.length) {
                case 3: {
                    if (params[2].length() != 0 && !params[2].equals("*")) {
                        byteLength = Long.parseLong(params[2]);
                    }
                }
                case 2: {
                    if (params[1].length() != 0) {
                        lastByte = Long.parseLong(params[1]);
                    }
                }
                case 1: {
                    if (params[0].length() != 0) {
                        firstByte = Long.parseLong(params[0]);
                    }
                    if (firstByte != null || lastByte != null) {
                        return new BytesRange(firstByte, lastByte, byteLength);
                    }
                    break;
                }
            }
        }
        throw new InvalidValueException("Can't parse Bytes Range: " + s);
    }
}
