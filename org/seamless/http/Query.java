// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.http;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Collections;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URL;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Query
{
    protected final Map<String, List<String>> parameters;
    
    public static Query newInstance(final Map<String, List<String>> parameters) {
        final Query query = new Query();
        query.parameters.putAll(parameters);
        return query;
    }
    
    public Query() {
        this.parameters = new LinkedHashMap<String, List<String>>();
    }
    
    public Query(final Map<String, String[]> parameters) {
        this.parameters = new LinkedHashMap<String, List<String>>();
        for (final Map.Entry<String, String[]> entry : parameters.entrySet()) {
            final List<String> list = Arrays.asList((entry.getValue() != null) ? entry.getValue() : new String[0]);
            this.parameters.put(entry.getKey(), list);
        }
    }
    
    public Query(final URL url) {
        this(url.getQuery());
    }
    
    public Query(final String qs) {
        this.parameters = new LinkedHashMap<String, List<String>>();
        if (qs == null) {
            return;
        }
        final String[] arr$;
        final String[] pairs = arr$ = qs.split("&");
        for (final String pair : arr$) {
            final int pos = pair.indexOf(61);
            String name;
            String value;
            if (pos == -1) {
                name = pair;
                value = null;
            }
            else {
                try {
                    name = URLDecoder.decode(pair.substring(0, pos), "UTF-8");
                    value = URLDecoder.decode(pair.substring(pos + 1, pair.length()), "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException("Query string is not UTF-8");
                }
            }
            List<String> list = this.parameters.get(name);
            if (list == null) {
                list = new ArrayList<String>();
                this.parameters.put(name, list);
            }
            list.add(value);
        }
    }
    
    public String get(final String name) {
        final List<String> values = this.parameters.get(name);
        if (values == null) {
            return "";
        }
        if (values.size() == 0) {
            return "";
        }
        return values.get(0);
    }
    
    public String[] getValues(final String name) {
        final List<String> values = this.parameters.get(name);
        if (values == null) {
            return null;
        }
        return values.toArray(new String[values.size()]);
    }
    
    public List<String> getValuesAsList(final String name) {
        return this.parameters.containsKey(name) ? Collections.unmodifiableList((List<? extends String>)this.parameters.get(name)) : null;
    }
    
    public Enumeration<String> getNames() {
        return Collections.enumeration(this.parameters.keySet());
    }
    
    public Map<String, String[]> getMap() {
        final Map<String, String[]> map = new TreeMap<String, String[]>();
        for (final Map.Entry<String, List<String>> entry : this.parameters.entrySet()) {
            final List<String> list = entry.getValue();
            String[] values;
            if (list == null) {
                values = null;
            }
            else {
                values = list.toArray(new String[list.size()]);
            }
            map.put(entry.getKey(), values);
        }
        return map;
    }
    
    public Map<String, List<String>> getMapWithLists() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends List<String>>)this.parameters);
    }
    
    public boolean isEmpty() {
        return this.parameters.size() == 0;
    }
    
    public Query cloneAndAdd(final String name, final String... values) {
        final Map<String, List<String>> params = new HashMap<String, List<String>>(this.getMapWithLists());
        List<String> existingValues = params.get(name);
        if (existingValues == null) {
            existingValues = new ArrayList<String>();
            params.put(name, existingValues);
        }
        existingValues.addAll(Arrays.asList(values));
        return newInstance(params);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, List<String>> entry : this.parameters.entrySet()) {
            for (final String v : entry.getValue()) {
                if (v != null) {
                    if (v.length() == 0) {
                        continue;
                    }
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(v);
                }
            }
        }
        return sb.toString();
    }
}
