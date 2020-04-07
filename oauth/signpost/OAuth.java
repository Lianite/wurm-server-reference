// 
// Decompiled by Procyon v0.5.30
// 

package oauth.signpost;

import java.util.HashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import oauth.signpost.http.HttpParameters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.io.OutputStream;
import java.util.Collection;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.google.gdata.util.common.base.PercentEscaper;

public class OAuth
{
    public static final String VERSION_1_0 = "1.0";
    public static final String ENCODING = "UTF-8";
    public static final String FORM_ENCODED = "application/x-www-form-urlencoded";
    public static final String HTTP_AUTHORIZATION_HEADER = "Authorization";
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    public static final String OAUTH_SIGNATURE = "oauth_signature";
    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    public static final String OAUTH_NONCE = "oauth_nonce";
    public static final String OAUTH_VERSION = "oauth_version";
    public static final String OAUTH_CALLBACK = "oauth_callback";
    public static final String OAUTH_CALLBACK_CONFIRMED = "oauth_callback_confirmed";
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String OUT_OF_BAND = "oob";
    private static final PercentEscaper percentEncoder;
    
    public static String percentEncode(final String s) {
        if (s == null) {
            return "";
        }
        return OAuth.percentEncoder.escape(s);
    }
    
    public static String percentDecode(final String s) {
        try {
            if (s == null) {
                return "";
            }
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }
    
    public static <T extends Map.Entry<String, String>> void formEncode(final Collection<T> parameters, final OutputStream into) throws IOException {
        if (parameters != null) {
            boolean first = true;
            for (final Map.Entry<String, String> entry : parameters) {
                if (first) {
                    first = false;
                }
                else {
                    into.write(38);
                }
                into.write(percentEncode(safeToString(entry.getKey())).getBytes());
                into.write(61);
                into.write(percentEncode(safeToString(entry.getValue())).getBytes());
            }
        }
    }
    
    public static <T extends Map.Entry<String, String>> String formEncode(final Collection<T> parameters) throws IOException {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        formEncode(parameters, b);
        return new String(b.toByteArray());
    }
    
    public static HttpParameters decodeForm(final String form) {
        final HttpParameters params = new HttpParameters();
        if (isEmpty(form)) {
            return params;
        }
        for (final String nvp : form.split("\\&")) {
            final int equals = nvp.indexOf(61);
            String name;
            String value;
            if (equals < 0) {
                name = percentDecode(nvp);
                value = null;
            }
            else {
                name = percentDecode(nvp.substring(0, equals));
                value = percentDecode(nvp.substring(equals + 1));
            }
            params.put(name, value);
        }
        return params;
    }
    
    public static HttpParameters decodeForm(final InputStream content) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        final StringBuilder sb = new StringBuilder();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            sb.append(line);
        }
        return decodeForm(sb.toString());
    }
    
    public static <T extends Map.Entry<String, String>> Map<String, String> toMap(final Collection<T> from) {
        final HashMap<String, String> map = new HashMap<String, String>();
        if (from != null) {
            for (final Map.Entry<String, String> entry : from) {
                final String key = entry.getKey();
                if (!map.containsKey(key)) {
                    map.put(key, entry.getValue());
                }
            }
        }
        return map;
    }
    
    public static final String safeToString(final Object from) {
        return (from == null) ? null : from.toString();
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }
    
    public static String addQueryParameters(final String url, final String... kvPairs) {
        final String queryDelim = url.contains("?") ? "&" : "?";
        final StringBuilder sb = new StringBuilder(url + queryDelim);
        for (int i = 0; i < kvPairs.length; i += 2) {
            if (i > 0) {
                sb.append("&");
            }
            sb.append(percentEncode(kvPairs[i]) + "=" + percentEncode(kvPairs[i + 1]));
        }
        return sb.toString();
    }
    
    public static String addQueryParameters(final String url, final Map<String, String> params) {
        final String[] kvPairs = new String[params.size() * 2];
        int idx = 0;
        for (final String key : params.keySet()) {
            kvPairs[idx] = key;
            kvPairs[idx + 1] = params.get(key);
            idx += 2;
        }
        return addQueryParameters(url, kvPairs);
    }
    
    public static String addQueryString(final String url, final String queryString) {
        final String queryDelim = url.contains("?") ? "&" : "?";
        final StringBuilder sb = new StringBuilder(url + queryDelim);
        sb.append(queryString);
        return sb.toString();
    }
    
    public static String prepareOAuthHeader(final String... kvPairs) {
        final StringBuilder sb = new StringBuilder("OAuth ");
        for (int i = 0; i < kvPairs.length; i += 2) {
            if (i > 0) {
                sb.append(", ");
            }
            final boolean isOAuthElem = kvPairs[i].startsWith("oauth_") || kvPairs[i].startsWith("x_oauth_");
            final String value = isOAuthElem ? percentEncode(kvPairs[i + 1]) : kvPairs[i + 1];
            sb.append(percentEncode(kvPairs[i]) + "=\"" + value + "\"");
        }
        return sb.toString();
    }
    
    public static HttpParameters oauthHeaderToParamsMap(String oauthHeader) {
        final HttpParameters params = new HttpParameters();
        if (oauthHeader == null || !oauthHeader.startsWith("OAuth ")) {
            return params;
        }
        oauthHeader = oauthHeader.substring("OAuth ".length());
        final String[] arr$;
        final String[] elements = arr$ = oauthHeader.split(",");
        for (final String keyValuePair : arr$) {
            final String[] keyValue = keyValuePair.split("=");
            params.put(keyValue[0].trim(), keyValue[1].replace("\"", "").trim());
        }
        return params;
    }
    
    public static String toHeaderElement(final String name, final String value) {
        return percentEncode(name) + "=\"" + percentEncode(value) + "\"";
    }
    
    public static void debugOut(final String key, final String value) {
        if (System.getProperty("debug") != null) {
            System.out.println("[SIGNPOST] " + key + ": " + value);
        }
    }
    
    static {
        percentEncoder = new PercentEscaper("-._~", false);
    }
}
