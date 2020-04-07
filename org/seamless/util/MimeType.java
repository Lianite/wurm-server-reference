// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Collections;
import java.util.Map;

public class MimeType
{
    public static final String WILDCARD = "*";
    private String type;
    private String subtype;
    private Map<String, String> parameters;
    
    public MimeType() {
        this("*", "*");
    }
    
    public MimeType(final String type, final String subtype, final Map<String, String> parameters) {
        this.type = ((type == null) ? "*" : type);
        this.subtype = ((subtype == null) ? "*" : subtype);
        if (parameters == null) {
            this.parameters = (Map<String, String>)Collections.EMPTY_MAP;
        }
        else {
            final Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
                public int compare(final String o1, final String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            for (final Map.Entry<String, String> e : parameters.entrySet()) {
                map.put(e.getKey(), e.getValue());
            }
            this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
        }
    }
    
    public MimeType(final String type, final String subtype) {
        this(type, subtype, Collections.EMPTY_MAP);
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean isWildcardType() {
        return this.getType().equals("*");
    }
    
    public String getSubtype() {
        return this.subtype;
    }
    
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals("*");
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public boolean isCompatible(final MimeType other) {
        return other != null && (this.type.equals("*") || other.type.equals("*") || (this.type.equalsIgnoreCase(other.type) && (this.subtype.equals("*") || other.subtype.equals("*"))) || (this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype)));
    }
    
    public static MimeType valueOf(String stringValue) throws IllegalArgumentException {
        if (stringValue == null) {
            throw new IllegalArgumentException("String value is null");
        }
        String params = null;
        final int semicolonIndex = stringValue.indexOf(";");
        if (semicolonIndex > -1) {
            params = stringValue.substring(semicolonIndex + 1).trim();
            stringValue = stringValue.substring(0, semicolonIndex);
        }
        String major = null;
        String subtype = null;
        final String[] paths = stringValue.split("/");
        if (paths.length < 2 && stringValue.equals("*")) {
            major = "*";
            subtype = "*";
        }
        else if (paths.length == 2) {
            major = paths[0].trim();
            subtype = paths[1].trim();
        }
        else if (paths.length != 2) {
            throw new IllegalArgumentException("Error parsing string: " + stringValue);
        }
        if (params != null && params.length() > 0) {
            final HashMap<String, String> map = new HashMap<String, String>();
            for (int start = 0; start < params.length(); start = readParamsIntoMap(map, params, start)) {}
            return new MimeType(major, subtype, map);
        }
        return new MimeType(major, subtype);
    }
    
    public static int readParamsIntoMap(final Map<String, String> map, final String params, final int start) {
        boolean quote = false;
        boolean backslash = false;
        int end = getEnd(params, start);
        final String name = params.substring(start, end).trim();
        if (end < params.length() && params.charAt(end) == '=') {
            ++end;
        }
        final StringBuilder buffer = new StringBuilder(params.length() - end);
        int i;
        for (i = end; i < params.length(); ++i) {
            final char c = params.charAt(i);
            switch (c) {
                case '\"': {
                    if (backslash) {
                        backslash = false;
                        buffer.append(c);
                        break;
                    }
                    quote = !quote;
                    break;
                }
                case '\\': {
                    if (backslash) {
                        backslash = false;
                        buffer.append(c);
                        break;
                    }
                    backslash = true;
                    break;
                }
                case ';': {
                    if (!quote) {
                        final String value = buffer.toString().trim();
                        map.put(name, value);
                        return i + 1;
                    }
                    buffer.append(c);
                    break;
                }
                default: {
                    buffer.append(c);
                    break;
                }
            }
        }
        final String value2 = buffer.toString().trim();
        map.put(name, value2);
        return i;
    }
    
    protected static int getEnd(final String params, final int start) {
        final int equals = params.indexOf(61, start);
        final int semicolon = params.indexOf(59, start);
        if (equals == -1 && semicolon == -1) {
            return params.length();
        }
        if (equals == -1) {
            return semicolon;
        }
        if (semicolon == -1) {
            return equals;
        }
        return (equals < semicolon) ? equals : semicolon;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final MimeType mimeType = (MimeType)o;
        if (this.parameters != null) {
            if (this.parameters.equals(mimeType.parameters)) {
                return this.subtype.equalsIgnoreCase(mimeType.subtype) && this.type.equalsIgnoreCase(mimeType.type);
            }
        }
        else if (mimeType.parameters == null) {
            return this.subtype.equalsIgnoreCase(mimeType.subtype) && this.type.equalsIgnoreCase(mimeType.type);
        }
        return false;
    }
    
    public int hashCode() {
        int result = this.type.toLowerCase().hashCode();
        result = 31 * result + this.subtype.toLowerCase().hashCode();
        result = 31 * result + ((this.parameters != null) ? this.parameters.hashCode() : 0);
        return result;
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.toStringNoParameters());
        if (this.getParameters() != null || this.getParameters().size() > 0) {
            for (final String name : this.getParameters().keySet()) {
                sb.append(";").append(name).append("=\"").append(this.getParameters().get(name)).append("\"");
            }
        }
        return sb.toString();
    }
    
    public String toStringNoParameters() {
        return this.getType() + "/" + this.getSubtype();
    }
}
