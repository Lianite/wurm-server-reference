// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.HashMap;

public class JSONObject
{
    private final HashMap myHashMap;
    public static final Object NULL;
    
    static {
        NULL = new Null(null);
    }
    
    public JSONObject() {
        this.myHashMap = new HashMap();
    }
    
    public JSONObject(final JSONObject jo, final String[] sa) throws JSONException {
        this();
        for (int i = 0; i < sa.length; ++i) {
            this.putOpt(sa[i], jo.opt(sa[i]));
        }
    }
    
    public JSONObject(final JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        while (true) {
            char c = x.nextClean();
            switch (c) {
                case '\0': {
                    throw x.syntaxError("A JSONObject text must end with '}'");
                }
                case '}': {}
                default: {
                    x.back();
                    final String key = x.nextValue().toString();
                    c = x.nextClean();
                    if (c == '=') {
                        if (x.next() != '>') {
                            x.back();
                        }
                    }
                    else if (c != ':') {
                        throw x.syntaxError("Expected a ':' after a key");
                    }
                    this.put(key, x.nextValue());
                    switch (x.nextClean()) {
                        case ',':
                        case ';': {
                            if (x.nextClean() == '}') {
                                return;
                            }
                            x.back();
                            continue;
                        }
                        case '}': {
                            return;
                        }
                        default: {
                            throw x.syntaxError("Expected a ',' or '}'");
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public JSONObject(final Map map) {
        this.myHashMap = ((map == null) ? new HashMap() : new HashMap(map));
    }
    
    public JSONObject(final Object object, final String[] names) {
        this();
        final Class c = object.getClass();
        for (int i = 0; i < names.length; ++i) {
            try {
                final String name = names[i];
                final Field field = c.getField(name);
                final Object value = field.get(object);
                this.put(name, value);
            }
            catch (Exception ex) {}
        }
    }
    
    public JSONObject(final String string) throws JSONException {
        this(new JSONTokener(string));
    }
    
    public JSONObject accumulate(final String key, final Object value) throws JSONException {
        testValidity(value);
        final Object o = this.opt(key);
        if (o == null) {
            this.put(key, (value instanceof JSONArray) ? new JSONArray().put(value) : value);
        }
        else if (o instanceof JSONArray) {
            ((JSONArray)o).put(value);
        }
        else {
            this.put(key, new JSONArray().put(o).put(value));
        }
        return this;
    }
    
    public JSONObject append(final String key, final Object value) throws JSONException {
        testValidity(value);
        final Object o = this.opt(key);
        if (o == null) {
            this.put(key, new JSONArray().put(value));
        }
        else {
            if (!(o instanceof JSONArray)) {
                throw new JSONException("JSONObject[" + key + "] is not a JSONArray. " + o.getClass());
            }
            this.put(key, ((JSONArray)o).put(value));
        }
        return this;
    }
    
    public static String doubleToString(final double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }
        String s = Double.toString(d);
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    public Object get(final String key) throws JSONException {
        final Object o = this.opt(key);
        if (o == null) {
            throw new JSONException("JSONObject[" + quote(key) + "] not found.");
        }
        return o;
    }
    
    public boolean getBoolean(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o.equals(Boolean.FALSE) || (o instanceof String && ((String)o).equalsIgnoreCase("false"))) {
            return false;
        }
        if (o.equals(Boolean.TRUE) || (o instanceof String && ((String)o).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a Boolean. " + o.getClass());
    }
    
    public double getDouble(final String key) throws JSONException {
        final Object o = this.get(key);
        try {
            return (o instanceof Number) ? ((Number)o).doubleValue() : Double.valueOf((String)o);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a number. " + o.getClass());
        }
    }
    
    public int getInt(final String key) throws JSONException {
        final Object o = this.get(key);
        return (o instanceof Number) ? ((Number)o).intValue() : ((int)this.getDouble(key));
    }
    
    public JSONArray getJSONArray(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o instanceof JSONArray) {
            return (JSONArray)o;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONArray. " + o.getClass());
    }
    
    public JSONObject getJSONObject(final String key) throws JSONException {
        final Object o = this.get(key);
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] is not a JSONObject. " + o.getClass());
    }
    
    public long getLong(final String key) throws JSONException {
        final Object o = this.get(key);
        return (o instanceof Number) ? ((Number)o).longValue() : ((long)this.getDouble(key));
    }
    
    public String getString(final String key) throws JSONException {
        return this.get(key).toString();
    }
    
    public boolean has(final String key) {
        return this.myHashMap.containsKey(key);
    }
    
    public boolean isNull(final String key) {
        return JSONObject.NULL.equals(this.opt(key));
    }
    
    public Iterator keys() {
        return this.myHashMap.keySet().iterator();
    }
    
    public int length() {
        return this.myHashMap.size();
    }
    
    public JSONArray names() {
        final JSONArray ja = new JSONArray();
        final Iterator keys = this.keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return (ja.length() == 0) ? null : ja;
    }
    
    public static String numberToString(final Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(n);
        String s = n.toString();
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    public Object opt(final String key) {
        return (key == null) ? null : this.myHashMap.get(key);
    }
    
    public boolean optBoolean(final String key) {
        return this.optBoolean(key, false);
    }
    
    public boolean optBoolean(final String key, final boolean defaultValue) {
        try {
            return this.getBoolean(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public JSONObject put(final String key, final Collection value) throws JSONException {
        this.put(key, new JSONArray(value));
        return this;
    }
    
    public double optDouble(final String key) {
        return this.optDouble(key, Double.NaN);
    }
    
    public double optDouble(final String key, final double defaultValue) {
        try {
            final Object o = this.opt(key);
            return (o instanceof Number) ? ((Number)o).doubleValue() : new Double((String)o);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public int optInt(final String key) {
        return this.optInt(key, 0);
    }
    
    public int optInt(final String key, final int defaultValue) {
        try {
            return this.getInt(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public JSONArray optJSONArray(final String key) {
        final Object o = this.opt(key);
        return (o instanceof JSONArray) ? ((JSONArray)o) : null;
    }
    
    public JSONObject optJSONObject(final String key) {
        final Object o = this.opt(key);
        return (o instanceof JSONObject) ? ((JSONObject)o) : null;
    }
    
    public long optLong(final String key) {
        return this.optLong(key, 0L);
    }
    
    public long optLong(final String key, final long defaultValue) {
        try {
            return this.getLong(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public String optString(final String key) {
        return this.optString(key, "");
    }
    
    public Map<String, ?> getMap() {
        return (Map<String, ?>)this.myHashMap;
    }
    
    public String optString(final String key, final String defaultValue) {
        final Object o = this.opt(key);
        return (o != null) ? o.toString() : defaultValue;
    }
    
    public JSONObject put(final String key, final boolean value) throws JSONException {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONObject put(final String key, final double value) throws JSONException {
        this.put(key, new Double(value));
        return this;
    }
    
    public JSONObject put(final String key, final int value) throws JSONException {
        this.put(key, new Integer(value));
        return this;
    }
    
    public JSONObject put(final String key, final long value) throws JSONException {
        this.put(key, new Long(value));
        return this;
    }
    
    public JSONObject put(final String key, final Map value) throws JSONException {
        this.put(key, new JSONObject(value));
        return this;
    }
    
    public JSONObject put(final String key, final Object value) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.myHashMap.put(key, value);
        }
        else {
            this.remove(key);
        }
        return this;
    }
    
    public JSONObject putOpt(final String key, final Object value) throws JSONException {
        if (key != null && value != null) {
            this.put(key, value);
        }
        return this;
    }
    
    public static String quote(final String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        char c = '\0';
        final int len = string.length();
        final StringBuffer sb = new StringBuffer(len + 4);
        sb.append('\"');
        for (int i = 0; i < len; ++i) {
            final char b = c;
            c = string.charAt(i);
            switch (c) {
                case '\"':
                case '\\': {
                    sb.append('\\');
                    sb.append(c);
                    break;
                }
                case '/': {
                    if (b == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                default: {
                    if (c < ' ' || (c >= '\u0080' && c < ' ') || (c >= '\u2000' && c < '\u2100')) {
                        final String t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                        break;
                    }
                    sb.append(c);
                    break;
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
    
    public Object remove(final String key) {
        return this.myHashMap.remove(key);
    }
    
    static void testValidity(final Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            }
            else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
        }
    }
    
    public JSONArray toJSONArray(final JSONArray names) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        final JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); ++i) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }
    
    @Override
    public String toString() {
        try {
            final Iterator keys = this.keys();
            final StringBuffer sb = new StringBuffer("{");
            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                final Object o = keys.next();
                sb.append(quote(o.toString()));
                sb.append(':');
                sb.append(valueToString(this.myHashMap.get(o)));
            }
            sb.append('}');
            return sb.toString();
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public String toString(final int indentFactor) throws JSONException {
        return this.toString(indentFactor, 0);
    }
    
    String toString(final int indentFactor, final int indent) throws JSONException {
        final int n = this.length();
        if (n == 0) {
            return "{}";
        }
        final Iterator keys = this.keys();
        final StringBuffer sb = new StringBuffer("{");
        final int newindent = indent + indentFactor;
        if (n == 1) {
            final Object o = keys.next();
            sb.append(quote(o.toString()));
            sb.append(": ");
            sb.append(valueToString(this.myHashMap.get(o), indentFactor, indent));
        }
        else {
            while (keys.hasNext()) {
                final Object o = keys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                }
                else {
                    sb.append('\n');
                }
                for (int i = 0; i < newindent; ++i) {
                    sb.append(' ');
                }
                sb.append(quote(o.toString()));
                sb.append(": ");
                sb.append(valueToString(this.myHashMap.get(o), indentFactor, newindent));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (int i = 0; i < indent; ++i) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }
    
    static String valueToString(final Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object o;
            try {
                o = ((JSONString)value).toJSONString();
            }
            catch (Exception e) {
                throw new JSONException(e);
            }
            if (o instanceof String) {
                return (String)o;
            }
            throw new JSONException("Bad value from toJSONString: " + o);
        }
        else {
            if (value instanceof Number) {
                return numberToString((Number)value);
            }
            if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
                return value.toString();
            }
            return quote(value.toString());
        }
    }
    
    static String valueToString(final Object value, final int indentFactor, final int indent) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        try {
            if (value instanceof JSONString) {
                final Object o = ((JSONString)value).toJSONString();
                if (o instanceof String) {
                    return (String)o;
                }
            }
        }
        catch (Exception ex) {}
        if (value instanceof Number) {
            return numberToString((Number)value);
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof JSONObject) {
            return ((JSONObject)value).toString(indentFactor, indent);
        }
        if (value instanceof JSONArray) {
            return ((JSONArray)value).toString(indentFactor, indent);
        }
        return quote(value.toString());
    }
    
    public Writer write(final Writer writer) throws JSONException {
        try {
            boolean b = false;
            final Iterator keys = this.keys();
            writer.write(123);
            while (keys.hasNext()) {
                if (b) {
                    writer.write(44);
                }
                final Object k = keys.next();
                writer.write(quote(k.toString()));
                writer.write(58);
                final Object v = this.myHashMap.get(k);
                if (v instanceof JSONObject) {
                    ((JSONObject)v).write(writer);
                }
                else if (v instanceof JSONArray) {
                    ((JSONArray)v).write(writer);
                }
                else {
                    writer.write(valueToString(v));
                }
                b = true;
            }
            writer.write(125);
            return writer;
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
    }
    
    private static final class Null
    {
        @Override
        protected final Object clone() {
            return this;
        }
        
        @Override
        public boolean equals(final Object object) {
            return object == null || object == this;
        }
        
        @Override
        public String toString() {
            return "null";
        }
    }
}
