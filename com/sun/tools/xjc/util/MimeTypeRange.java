// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.util;

import java.util.Collection;
import java.util.Iterator;
import javax.activation.MimeTypeParseException;
import javax.activation.MimeType;
import java.util.HashMap;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MimeTypeRange
{
    public final String majorType;
    public final String subType;
    public final Map<String, String> parameters;
    public final float q;
    public static final MimeTypeRange ALL;
    
    public static List<MimeTypeRange> parseRanges(final String s) throws ParseException {
        final StringCutter cutter = new StringCutter(s, true);
        final List<MimeTypeRange> r = new ArrayList<MimeTypeRange>();
        while (cutter.length() > 0) {
            r.add(new MimeTypeRange(cutter));
        }
        return r;
    }
    
    public MimeTypeRange(final String s) throws ParseException {
        this(new StringCutter(s, true));
    }
    
    private static MimeTypeRange create(final String s) {
        try {
            return new MimeTypeRange(s);
        }
        catch (ParseException e) {
            throw new Error(e);
        }
    }
    
    private MimeTypeRange(final StringCutter cutter) throws ParseException {
        this.parameters = new HashMap<String, String>();
        this.majorType = cutter.until("/");
        cutter.next("/");
        this.subType = cutter.until("[;,]");
        float q = 1.0f;
        while (cutter.length() > 0) {
            final String sep = cutter.next("[;,]");
            if (sep.equals(",")) {
                break;
            }
            final String key = cutter.until("=");
            cutter.next("=");
            final char ch = cutter.peek();
            String value;
            if (ch == '\"') {
                cutter.next("\"");
                value = cutter.until("\"");
                cutter.next("\"");
            }
            else {
                value = cutter.until("[;,]");
            }
            if (key.equals("q")) {
                q = Float.parseFloat(value);
            }
            else {
                this.parameters.put(key, value);
            }
        }
        this.q = q;
    }
    
    public MimeType toMimeType() throws MimeTypeParseException {
        return new MimeType(this.toString());
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.majorType + '/' + this.subType);
        if (this.q != 1.0f) {
            sb.append("; q=").append(this.q);
        }
        for (final Map.Entry<String, String> p : this.parameters.entrySet()) {
            sb.append("; ").append(p.getKey()).append('=').append(p.getValue());
        }
        return sb.toString();
    }
    
    public static MimeTypeRange merge(final Collection<MimeTypeRange> types) {
        if (types.size() == 0) {
            throw new IllegalArgumentException();
        }
        if (types.size() == 1) {
            return types.iterator().next();
        }
        String majorType = null;
        for (final MimeTypeRange mt : types) {
            if (majorType == null) {
                majorType = mt.majorType;
            }
            if (!majorType.equals(mt.majorType)) {
                return MimeTypeRange.ALL;
            }
        }
        return create(majorType + "/*");
    }
    
    public static void main(final String[] args) throws ParseException {
        for (final MimeTypeRange m : parseRanges(args[0])) {
            System.out.println(m.toString());
        }
    }
    
    static {
        ALL = create("*/*");
    }
}
