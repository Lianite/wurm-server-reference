// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.URI;
import java.util.BitSet;

public class URIUtil
{
    public static final BitSet ALLOWED;
    public static final BitSet PATH_SEGMENT;
    public static final BitSet PATH_PARAM_NAME;
    public static final BitSet PATH_PARAM_VALUE;
    public static final BitSet QUERY;
    public static final BitSet FRAGMENT;
    
    public static URI createAbsoluteURI(final URI base, final String uri) throws IllegalArgumentException {
        return createAbsoluteURI(base, URI.create(uri));
    }
    
    public static URI createAbsoluteURI(URI base, final URI relativeOrNot) throws IllegalArgumentException {
        if (base == null && !relativeOrNot.isAbsolute()) {
            throw new IllegalArgumentException("Base URI is null and given URI is not absolute");
        }
        if (base == null && relativeOrNot.isAbsolute()) {
            return relativeOrNot;
        }
        assert base != null;
        if (base.getPath().length() == 0) {
            try {
                base = new URI(base.getScheme(), base.getAuthority(), "/", base.getQuery(), base.getFragment());
            }
            catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        return base.resolve(relativeOrNot);
    }
    
    public static URL createAbsoluteURL(final URL base, final String uri) throws IllegalArgumentException {
        return createAbsoluteURL(base, URI.create(uri));
    }
    
    public static URL createAbsoluteURL(final URL base, final URI relativeOrNot) throws IllegalArgumentException {
        if (base == null && !relativeOrNot.isAbsolute()) {
            throw new IllegalArgumentException("Base URL is null and given URI is not absolute");
        }
        if (base == null && relativeOrNot.isAbsolute()) {
            try {
                return relativeOrNot.toURL();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Base URL was null and given URI can't be converted to URL");
            }
        }
        try {
            assert base != null;
            final URI baseURI = base.toURI();
            final URI absoluteURI = createAbsoluteURI(baseURI, relativeOrNot);
            return absoluteURI.toURL();
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Base URL is not an URI, or can't create absolute URI (null?), or absolute URI can not be converted to URL", ex);
        }
    }
    
    public static URL createAbsoluteURL(final URI base, final URI relativeOrNot) throws IllegalArgumentException {
        try {
            return createAbsoluteURI(base, relativeOrNot).toURL();
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Absolute URI can not be converted to URL", ex);
        }
    }
    
    public static URL createAbsoluteURL(final InetAddress address, final int localStreamPort, final URI relativeOrNot) throws IllegalArgumentException {
        try {
            if (address instanceof Inet6Address) {
                return createAbsoluteURL(new URL("http://[" + address.getHostAddress() + "]:" + localStreamPort), relativeOrNot);
            }
            if (address instanceof Inet4Address) {
                return createAbsoluteURL(new URL("http://" + address.getHostAddress() + ":" + localStreamPort), relativeOrNot);
            }
            throw new IllegalArgumentException("InetAddress is neither IPv4 nor IPv6: " + address);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Address, port, and URI can not be converted to URL", ex);
        }
    }
    
    public static URI createRelativePathURI(final URI uri) {
        assertRelativeURI("Given", uri);
        final URI normalizedURI = uri.normalize();
        String uriString;
        int idx;
        for (uriString = normalizedURI.toString(); (idx = uriString.indexOf("../")) != -1; uriString = uriString.substring(0, idx) + uriString.substring(idx + 3)) {}
        while (uriString.startsWith("/")) {
            uriString = uriString.substring(1);
        }
        return URI.create(uriString);
    }
    
    public static URI createRelativeURI(final URI base, final URI full) {
        return base.relativize(full);
    }
    
    public static URI createRelativeURI(final URL base, final URL full) throws IllegalArgumentException {
        try {
            return createRelativeURI(base.toURI(), full.toURI());
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Can't convert base or full URL to URI", ex);
        }
    }
    
    public static URI createRelativeURI(final URI base, final URL full) throws IllegalArgumentException {
        try {
            return createRelativeURI(base, full.toURI());
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Can't convert full URL to URI", ex);
        }
    }
    
    public static URI createRelativeURI(final URL base, final URI full) throws IllegalArgumentException {
        try {
            return createRelativeURI(base.toURI(), full);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Can't convert base URL to URI", ex);
        }
    }
    
    public static boolean isAbsoluteURI(final String s) {
        final URI uri = URI.create(s);
        return uri.isAbsolute();
    }
    
    public static void assertRelativeURI(final String what, final URI uri) {
        if (uri.isAbsolute()) {
            throw new IllegalArgumentException(what + " URI must be relative, without scheme and authority");
        }
    }
    
    public static URL toURL(final URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return uri.toURL();
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static URI toURI(final URL url) {
        if (url == null) {
            return null;
        }
        try {
            return url.toURI();
        }
        catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String percentEncode(final String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String percentDecode(final String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String encodePathSegment(final String pathSegment) {
        return encode(URIUtil.PATH_SEGMENT, pathSegment, "UTF-8");
    }
    
    public static String encodePathParamName(final String pathParamName) {
        return encode(URIUtil.PATH_PARAM_NAME, pathParamName, "UTF-8");
    }
    
    public static String encodePathParamValue(final String pathParamValue) {
        return encode(URIUtil.PATH_PARAM_VALUE, pathParamValue, "UTF-8");
    }
    
    public static String encodeQueryNameOrValue(final String queryNameOrValue) {
        return encode(URIUtil.QUERY, queryNameOrValue, "UTF-8");
    }
    
    public static String encodeFragment(final String fragment) {
        return encode(URIUtil.FRAGMENT, fragment, "UTF-8");
    }
    
    public static String encode(final BitSet allowedCharacters, final String s, final String charset) {
        if (s == null) {
            return null;
        }
        final StringBuilder encoded = new StringBuilder(s.length() * 3);
        final char[] characters = s.toCharArray();
        try {
            for (final char c : characters) {
                if (allowedCharacters.get(c)) {
                    encoded.append(c);
                }
                else {
                    final byte[] arr$2;
                    final byte[] bytes = arr$2 = String.valueOf(c).getBytes(charset);
                    for (final byte b : arr$2) {
                        encoded.append(String.format("%%%1$02X", b & 0xFF));
                    }
                }
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return encoded.toString();
    }
    
    static {
        ALLOWED = new BitSet() {
            {
                for (int i = 97; i <= 122; ++i) {
                    this.set(i);
                }
                for (int i = 65; i <= 90; ++i) {
                    this.set(i);
                }
                for (int i = 48; i <= 57; ++i) {
                    this.set(i);
                }
                this.set(33);
                this.set(36);
                this.set(38);
                this.set(39);
                this.set(40);
                this.set(41);
                this.set(42);
                this.set(43);
                this.set(44);
                this.set(59);
                this.set(61);
                this.set(45);
                this.set(46);
                this.set(95);
                this.set(126);
                this.set(58);
                this.set(64);
            }
        };
        PATH_SEGMENT = new BitSet() {
            {
                this.or(URIUtil.ALLOWED);
                this.clear(59);
            }
        };
        PATH_PARAM_NAME = new BitSet() {
            {
                this.or(URIUtil.ALLOWED);
                this.clear(59);
                this.clear(61);
            }
        };
        PATH_PARAM_VALUE = new BitSet() {
            {
                this.or(URIUtil.ALLOWED);
                this.clear(59);
            }
        };
        QUERY = new BitSet() {
            {
                this.or(URIUtil.ALLOWED);
                this.set(47);
                this.set(63);
                this.clear(61);
                this.clear(38);
                this.clear(43);
            }
        };
        FRAGMENT = new BitSet() {
            {
                this.or(URIUtil.ALLOWED);
                this.set(47);
                this.set(63);
            }
        };
    }
}
