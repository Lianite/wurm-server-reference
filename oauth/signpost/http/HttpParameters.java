// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost.http;

import java.util.Set;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import oauth.signpost.OAuth;
import java.util.Iterator;
import java.util.TreeMap;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.Map;

public class HttpParameters implements Map<String, SortedSet<String>>, Serializable
{
    private TreeMap<String, SortedSet<String>> wrappedMap;
    
    public HttpParameters() {
        this.wrappedMap = new TreeMap<String, SortedSet<String>>();
    }
    
    public SortedSet<String> put(final String key, final SortedSet<String> value) {
        return this.wrappedMap.put(key, value);
    }
    
    public SortedSet<String> put(final String key, final SortedSet<String> values, final boolean percentEncode) {
        if (percentEncode) {
            this.remove((Object)key);
            for (final String v : values) {
                this.put(key, v, true);
            }
            return this.get((Object)key);
        }
        return this.wrappedMap.put(key, values);
    }
    
    public String put(final String key, final String value) {
        return this.put(key, value, false);
    }
    
    public String put(String key, String value, final boolean percentEncode) {
        key = (percentEncode ? OAuth.percentEncode(key) : key);
        SortedSet<String> values = this.wrappedMap.get(key);
        if (values == null) {
            values = new TreeSet<String>();
            this.wrappedMap.put(key, values);
        }
        if (value != null) {
            value = (percentEncode ? OAuth.percentEncode(value) : value);
            values.add(value);
        }
        return value;
    }
    
    public String putNull(final String key, final String nullString) {
        return this.put(key, nullString);
    }
    
    public void putAll(final Map<? extends String, ? extends SortedSet<String>> m) {
        this.wrappedMap.putAll(m);
    }
    
    public void putAll(final Map<? extends String, ? extends SortedSet<String>> m, final boolean percentEncode) {
        if (percentEncode) {
            for (final String key : m.keySet()) {
                this.put(key, (SortedSet<String>)m.get(key), true);
            }
        }
        else {
            this.wrappedMap.putAll(m);
        }
    }
    
    public void putAll(final String[] keyValuePairs, final boolean percentEncode) {
        for (int i = 0; i < keyValuePairs.length - 1; i += 2) {
            this.put(keyValuePairs[i], keyValuePairs[i + 1], percentEncode);
        }
    }
    
    public void putMap(final Map<String, List<String>> m) {
        for (final String key : m.keySet()) {
            SortedSet<String> vals = this.get((Object)key);
            if (vals == null) {
                vals = new TreeSet<String>();
                this.put(key, vals);
            }
            vals.addAll((Collection<?>)m.get(key));
        }
    }
    
    public SortedSet<String> get(final Object key) {
        return this.wrappedMap.get(key);
    }
    
    public String getFirst(final Object key) {
        return this.getFirst(key, false);
    }
    
    public String getFirst(final Object key, final boolean percentDecode) {
        final SortedSet<String> values = this.wrappedMap.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        final String value = values.first();
        return percentDecode ? OAuth.percentDecode(value) : value;
    }
    
    public String getAsQueryString(final Object key) {
        return this.getAsQueryString(key, true);
    }
    
    public String getAsQueryString(Object key, final boolean percentEncode) {
        final StringBuilder sb = new StringBuilder();
        if (percentEncode) {
            key = OAuth.percentEncode((String)key);
        }
        final Set<String> values = this.wrappedMap.get(key);
        if (values == null) {
            return key + "=";
        }
        final Iterator<String> iter = values.iterator();
        while (iter.hasNext()) {
            sb.append(key + "=" + iter.next());
            if (iter.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }
    
    public String getAsHeaderElement(final String key) {
        final String value = this.getFirst(key);
        if (value == null) {
            return null;
        }
        return key + "=\"" + value + "\"";
    }
    
    public boolean containsKey(final Object key) {
        return this.wrappedMap.containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        for (final Set<String> values : this.wrappedMap.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }
    
    public int size() {
        int count = 0;
        for (final String key : this.wrappedMap.keySet()) {
            count += this.wrappedMap.get(key).size();
        }
        return count;
    }
    
    public boolean isEmpty() {
        return this.wrappedMap.isEmpty();
    }
    
    public void clear() {
        this.wrappedMap.clear();
    }
    
    public SortedSet<String> remove(final Object key) {
        return this.wrappedMap.remove(key);
    }
    
    public Set<String> keySet() {
        return this.wrappedMap.keySet();
    }
    
    public Collection<SortedSet<String>> values() {
        return this.wrappedMap.values();
    }
    
    public Set<Entry<String, SortedSet<String>>> entrySet() {
        return this.wrappedMap.entrySet();
    }
    
    public HttpParameters getOAuthParameters() {
        final HttpParameters oauthParams = new HttpParameters();
        for (final Entry<String, SortedSet<String>> param : this.entrySet()) {
            final String key = param.getKey();
            if (key.startsWith("oauth_") || key.startsWith("x_oauth_")) {
                oauthParams.put(key, (SortedSet<String>)param.getValue());
            }
        }
        return oauthParams;
    }
}
