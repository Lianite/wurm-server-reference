// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import winterwell.json.JSONException;
import winterwell.json.JSONObject;
import java.util.Calendar;
import java.lang.reflect.Field;
import java.util.GregorianCalendar;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import com.winterwell.jgeoplanet.IPlace;
import com.winterwell.jgeoplanet.MFloat;
import java.util.List;
import java.text.SimpleDateFormat;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Comparator;
import java.text.DateFormat;
import java.util.regex.Pattern;

public class InternalUtils
{
    public static final Pattern TAG_REGEX;
    static final DateFormat df;
    static final DateFormat dfMarko;
    public static final Pattern latLongLocn;
    static final Comparator<Status> NEWEST_FIRST;
    public static final Pattern REGEX_JUST_DIGITS;
    static final Pattern URL_REGEX;
    static ConcurrentHashMap<String, Long> usage;
    private static final Charset UTF_8;
    public static final Pattern pComment;
    public static final Pattern pScriptOrStyle;
    public static final Pattern pDocType;
    
    static {
        TAG_REGEX = Pattern.compile("<!?/?[\\[\\-a-zA-Z][^>]*>", 32);
        df = new SimpleDateFormat("yyyy-MM-dd");
        dfMarko = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        latLongLocn = Pattern.compile("(\\S+:)?\\s*(-?[\\d\\.]+)\\s*,\\s*(-?[\\d\\.]+)");
        NEWEST_FIRST = new Comparator<Status>() {
            @Override
            public int compare(final Status o1, final Status o2) {
                return -o1.id.compareTo(o2.id);
            }
        };
        REGEX_JUST_DIGITS = Pattern.compile("\\d+");
        URL_REGEX = Pattern.compile("[hf]tt?ps?://[a-zA-Z0-9_%\\-\\.,\\?&\\/=\\+'~#!\\*:]+[a-zA-Z0-9_%\\-&\\/=\\+]");
        UTF_8 = Charset.forName("UTF-8");
        pComment = Pattern.compile("<!-*.*?-+>", 32);
        pScriptOrStyle = Pattern.compile("<(script|style)[^<>]*>.+?</(script|style)>", 34);
        pDocType = Pattern.compile("<!DOCTYPE.*?>", 34);
    }
    
    public static <P extends IPlace> P prefer(List<P> places, final String prefType, final MFloat confidence, final float baseConfidence) {
        assert places.size() != 0;
        assert baseConfidence >= 0.0f && baseConfidence <= 1.0f;
        final List cities = new ArrayList();
        for (final IPlace place : places) {
            if (prefType.equals(place.getType())) {
                cities.add(place);
            }
        }
        if (cities.size() != 0 && cities.size() != places.size()) {
            if (confidence != null) {
                final float conf = 0.95f * baseConfidence / cities.size();
                confidence.value = conf;
            }
            places = (List<P>)cities;
        }
        else if (confidence != null) {
            confidence.set(baseConfidence / places.size());
        }
        return places.get(0);
    }
    
    public static String stripUrls(final String text) {
        return Regex.VALID_URL.matcher(text).replaceAll("");
    }
    
    public static Map asMap(final Object... keyValuePairs) {
        assert keyValuePairs.length % 2 == 0;
        final Map m = new HashMap(keyValuePairs.length / 2);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            final Object v = keyValuePairs[i + 1];
            if (v != null) {
                m.put(keyValuePairs[i], v);
            }
        }
        return m;
    }
    
    public static void close(final OutputStream output) {
        if (output == null) {
            return;
        }
        try {
            output.flush();
        }
        catch (Exception ex) {}
        finally {
            try {
                output.close();
            }
            catch (IOException ex2) {}
        }
        try {
            output.close();
        }
        catch (IOException ex3) {}
    }
    
    public static void close(final InputStream input) {
        if (input == null) {
            return;
        }
        try {
            input.close();
        }
        catch (IOException ex) {}
    }
    
    static void count(String url) {
        if (InternalUtils.usage == null) {
            return;
        }
        int i = url.indexOf("?");
        if (i != -1) {
            url = url.substring(0, i);
        }
        i = url.indexOf("/1/");
        if (i != -1) {
            url = url.substring(i + 3);
        }
        url = url.replaceAll("\\d+", "");
        for (int j = 0; j < 100; ++j) {
            final Long v = InternalUtils.usage.get(url);
            boolean done;
            if (v == null) {
                final Long old = InternalUtils.usage.putIfAbsent(url, 1L);
                done = (old == null);
            }
            else {
                final long nv = v + 1L;
                done = InternalUtils.usage.replace(url, v, nv);
            }
            if (done) {
                break;
            }
        }
    }
    
    static String encode(final Object x) {
        String encd;
        try {
            encd = URLEncoder.encode(String.valueOf(x), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            encd = URLEncoder.encode(String.valueOf(x));
        }
        encd = encd.replace("*", "%2A");
        return encd.replace("+", "%20");
    }
    
    public static ConcurrentHashMap<String, Long> getAPIUsageStats() {
        return InternalUtils.usage;
    }
    
    public static Date getDate(final int year, final String month, final int day) {
        try {
            final Field field = GregorianCalendar.class.getField(month.toUpperCase());
            final int m = field.getInt(null);
            final Calendar date = new GregorianCalendar(year, m, day);
            return date.getTime();
        }
        catch (Exception x) {
            throw new IllegalArgumentException(x.getMessage());
        }
    }
    
    static Boolean getOptBoolean(final JSONObject obj, final String key) throws JSONException {
        final Object o = obj.opt(key);
        if (o == null || o.equals(JSONObject.NULL)) {
            return null;
        }
        if (o instanceof Boolean) {
            return (Boolean)o;
        }
        if (o instanceof String) {
            final String os = (String)o;
            if (os.equalsIgnoreCase("true")) {
                return true;
            }
            if (os.equalsIgnoreCase("false")) {
                return false;
            }
        }
        if (o instanceof Integer) {
            final int oi = (int)o;
            if (oi == 1) {
                return true;
            }
            if (oi == 0 || oi == -1) {
                return false;
            }
        }
        System.err.println("JSON parse fail: " + o + " (" + key + ") is not boolean");
        return null;
    }
    
    static String join(final List screenNamesOrIds, final int first, final int last) {
        final StringBuilder names = new StringBuilder();
        for (int si = first, n = Math.min(last, screenNamesOrIds.size()); si < n; ++si) {
            names.append(screenNamesOrIds.get(si));
            names.append(",");
        }
        if (names.length() != 0) {
            names.delete(names.length() - 1, names.length());
        }
        return names.toString();
    }
    
    public static String join(final String[] screenNames) {
        final StringBuilder names = new StringBuilder();
        for (int si = 0, n = screenNames.length; si < n; ++si) {
            names.append(screenNames[si]);
            names.append(",");
        }
        if (names.length() != 0) {
            names.delete(names.length() - 1, names.length());
        }
        return names.toString();
    }
    
    protected static String jsonGet(final String key, final JSONObject jsonObj) {
        assert key != null : jsonObj;
        assert jsonObj != null;
        final Object val = jsonObj.opt(key);
        if (val == null) {
            return null;
        }
        if (JSONObject.NULL.equals(val)) {
            return null;
        }
        final String s = val.toString();
        return s;
    }
    
    static Date parseDate(final String c) {
        if (InternalUtils.REGEX_JUST_DIGITS.matcher(c).matches()) {
            return new Date(Long.valueOf(c));
        }
        try {
            final Date _createdAt = new Date(c);
            return _createdAt;
        }
        catch (Exception e2) {
            try {
                final Date _createdAt2 = InternalUtils.dfMarko.parse(c);
                return _createdAt2;
            }
            catch (ParseException e1) {
                throw new TwitterException.Parsing(c, e1);
            }
        }
    }
    
    public static void setTrackAPIUsage(final boolean on) {
        if (!on) {
            InternalUtils.usage = null;
            return;
        }
        if (InternalUtils.usage != null) {
            return;
        }
        InternalUtils.usage = new ConcurrentHashMap<String, Long>();
    }
    
    protected static String read(final InputStream inputStream) {
        try {
            Reader reader = new InputStreamReader(inputStream, InternalUtils.UTF_8);
            reader = new BufferedReader(reader);
            final StringBuilder output = new StringBuilder();
            while (true) {
                final int c = reader.read();
                if (c == -1) {
                    break;
                }
                output.append((char)c);
            }
            return output.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            close(inputStream);
        }
    }
    
    static String unencode(String text) {
        if (text == null) {
            return null;
        }
        text = text.replace("&quot;", "\"");
        text = text.replace("&apos;", "'");
        text = text.replace("&nbsp;", " ");
        text = text.replace("&amp;", "&");
        text = text.replace("&gt;", ">");
        text = text.replace("&lt;", "<");
        if (text.indexOf(0) != -1) {
            text = text.replace('\0', ' ').trim();
        }
        return text;
    }
    
    static URI URI(final String uri) {
        try {
            return new URI(uri);
        }
        catch (URISyntaxException e) {
            return null;
        }
    }
    
    static User user(final String json) {
        try {
            final JSONObject obj = new JSONObject(json);
            final User u = new User(obj, null);
            return u;
        }
        catch (JSONException e) {
            throw new TwitterException(e);
        }
    }
    
    public static String stripTags(String xml) {
        if (xml == null) {
            return null;
        }
        if (xml.indexOf(60) == -1) {
            return xml;
        }
        final Matcher m4 = InternalUtils.pScriptOrStyle.matcher(xml);
        xml = m4.replaceAll("");
        final Matcher m5 = InternalUtils.pComment.matcher(xml);
        final String txt = m5.replaceAll("");
        final Matcher i = InternalUtils.TAG_REGEX.matcher(txt);
        final String txt2 = i.replaceAll("");
        final Matcher m6 = InternalUtils.pDocType.matcher(txt2);
        final String txt3 = m6.replaceAll("");
        return txt3;
    }
    
    public static void sleep(final long msecs) {
        try {
            Thread.sleep(msecs);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    static boolean authoriseIn11(final Twitter jtwit) {
        return jtwit.getHttpClient().canAuthenticate() || jtwit.TWITTER_URL.endsWith("1.1");
    }
    
    public static BigInteger getMinId(final BigInteger maxId, final List<? extends Twitter.ITweet> stati) {
        BigInteger min = maxId;
        for (final Twitter.ITweet s : stati) {
            if (min == null || min.compareTo(s.getId()) > 0) {
                min = s.getId();
            }
        }
        if (min != null) {
            min = min.subtract(BigInteger.ONE);
        }
        return min;
    }
}
