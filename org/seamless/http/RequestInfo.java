// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.http;

import javax.servlet.http.Cookie;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class RequestInfo
{
    private static final Logger log;
    
    public static void reportRequest(final StringBuilder builder, final HttpServletRequest req) {
        builder.append("Request: ");
        builder.append(req.getMethod());
        builder.append(' ');
        builder.append(req.getRequestURL());
        final String queryString = req.getQueryString();
        if (queryString != null) {
            builder.append('?');
            builder.append(queryString);
        }
        builder.append(" - ");
        final String sessionId = req.getRequestedSessionId();
        if (sessionId != null) {
            builder.append("\nSession ID: ");
        }
        if (sessionId == null) {
            builder.append("No Session");
        }
        else if (req.isRequestedSessionIdValid()) {
            builder.append(sessionId);
            builder.append(" (from ");
            if (req.isRequestedSessionIdFromCookie()) {
                builder.append("cookie)\n");
            }
            else if (req.isRequestedSessionIdFromURL()) {
                builder.append("url)\n");
            }
            else {
                builder.append("unknown)\n");
            }
        }
        else {
            builder.append("Invalid Session ID\n");
        }
    }
    
    public static void reportParameters(final StringBuilder builder, final HttpServletRequest req) {
        final Enumeration names = req.getParameterNames();
        if (names == null) {
            return;
        }
        if (names.hasMoreElements()) {
            builder.append("Parameters:\n");
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                final String[] values = req.getParameterValues(name);
                if (values != null) {
                    for (final String value : values) {
                        builder.append("    ").append(name).append(" = ").append(value).append('\n');
                    }
                }
            }
        }
    }
    
    public static void reportHeaders(final StringBuilder builder, final HttpServletRequest req) {
        final Enumeration names = req.getHeaderNames();
        if (names == null) {
            return;
        }
        if (names.hasMoreElements()) {
            builder.append("Headers:\n");
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                final String value = req.getHeader(name);
                builder.append("    ").append(name).append(": ").append(value).append('\n');
            }
        }
    }
    
    public static void reportCookies(final StringBuilder builder, final HttpServletRequest req) {
        final Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return;
        }
        final int l = cookies.length;
        if (l > 0) {
            builder.append("Cookies:\n");
            for (final Cookie cookie : cookies) {
                builder.append("    ").append(cookie.getName()).append(" = ").append(cookie.getValue()).append('\n');
            }
        }
    }
    
    public static void reportClient(final StringBuilder builder, final HttpServletRequest req) {
        builder.append("Remote Address: ").append(req.getRemoteAddr()).append("\n");
        if (!req.getRemoteAddr().equals(req.getRemoteHost())) {
            builder.append("Remote Host: ").append(req.getRemoteHost()).append("\n");
        }
        builder.append("Remote Port: ").append(req.getRemotePort()).append("\n");
        if (req.getRemoteUser() != null) {
            builder.append("Remote User: ").append(req.getRemoteUser()).append("\n");
        }
    }
    
    public static boolean isPS3Request(final String userAgent, final String avClientInfo) {
        return (userAgent != null && userAgent.contains("PLAYSTATION 3")) || (avClientInfo != null && avClientInfo.contains("PLAYSTATION 3"));
    }
    
    public static boolean isAndroidBubbleUPnPRequest(final String userAgent) {
        return userAgent != null && userAgent.contains("BubbleUPnP");
    }
    
    public static boolean isPS3Request(final HttpServletRequest request) {
        return isPS3Request(request.getHeader("User-Agent"), request.getHeader("X-AV-Client-Info"));
    }
    
    public static boolean isJRiverRequest(final HttpServletRequest request) {
        return isJRiverRequest(request.getHeader("User-Agent"));
    }
    
    public static boolean isJRiverRequest(final String userAgent) {
        return userAgent != null && (userAgent.contains("J-River") || userAgent.contains("J. River"));
    }
    
    public static boolean isWMPRequest(final String userAgent) {
        return userAgent != null && userAgent.contains("Windows-Media-Player") && !isJRiverRequest(userAgent);
    }
    
    public static boolean isXbox360Request(final HttpServletRequest request) {
        return isXbox360Request(request.getHeader("User-Agent"), request.getHeader("Server"));
    }
    
    public static boolean isXbox360Request(final String userAgent, final String server) {
        return (userAgent != null && (userAgent.contains("Xbox") || userAgent.contains("Xenon"))) || (server != null && server.contains("Xbox"));
    }
    
    public static boolean isXbox360AlbumArtRequest(final HttpServletRequest request) {
        return "true".equals(request.getParameter("albumArt")) && isXbox360Request(request);
    }
    
    public static void dumpRequestHeaders(final long timestamp, final HttpServletRequest request) {
        dumpRequestHeaders(timestamp, "REQUEST HEADERS", request);
    }
    
    public static void dumpRequestString(final long timestamp, final HttpServletRequest request) {
        RequestInfo.log.info(getRequestInfoString(timestamp, request));
    }
    
    public static void dumpRequestHeaders(final long timestamp, final String text, final HttpServletRequest request) {
        RequestInfo.log.info(text);
        dumpRequestString(timestamp, request);
        final Enumeration headers = request.getHeaderNames();
        if (headers != null) {
            while (headers.hasMoreElements()) {
                final String headerName = headers.nextElement();
                RequestInfo.log.info(String.format("%s: %s", headerName, request.getHeader(headerName)));
            }
        }
        RequestInfo.log.info("----------------------------------------");
    }
    
    public static String getRequestInfoString(final long timestamp, final HttpServletRequest request) {
        return String.format("%s %s %s %s %s %d", request.getMethod(), request.getRequestURI(), request.getProtocol(), request.getParameterMap(), request.getRemoteAddr(), timestamp);
    }
    
    public static String getRequestFullURL(final HttpServletRequest req) {
        final String scheme = req.getScheme();
        final String serverName = req.getServerName();
        final int serverPort = req.getServerPort();
        final String contextPath = req.getContextPath();
        final String servletPath = req.getServletPath();
        final String pathInfo = req.getPathInfo();
        final String queryString = req.getQueryString();
        final StringBuffer url = new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath).append(servletPath);
        if (pathInfo != null) {
            url.append(pathInfo);
        }
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }
    
    static {
        log = Logger.getLogger(RequestInfo.class.getName());
    }
}
