// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.collect.Iterables;
import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.IOException;
import java.text.DateFormat;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.google.common.base.MoreObjects;
import java.nio.file.OpenOption;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.google.common.base.Preconditions;
import java.net.URL;
import com.google.common.collect.ImmutableListMultimap;
import java.io.InputStream;
import java.net.URLConnection;

final class PathURLConnection extends URLConnection
{
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private InputStream stream;
    private ImmutableListMultimap<String, String> headers;
    
    PathURLConnection(final URL url) {
        super(Preconditions.checkNotNull(url));
        this.headers = ImmutableListMultimap.of();
    }
    
    @Override
    public void connect() throws IOException {
        if (this.stream != null) {
            return;
        }
        final Path path = Paths.get(toUri(this.url));
        long length;
        if (Files.isDirectory(path, new LinkOption[0])) {
            final StringBuilder builder = new StringBuilder();
            try (final DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
                for (final Path file : files) {
                    builder.append(file.getFileName()).append('\n');
                }
            }
            final byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            this.stream = new ByteArrayInputStream(bytes);
            length = bytes.length;
        }
        else {
            this.stream = Files.newInputStream(path, new OpenOption[0]);
            length = Files.size(path);
        }
        final FileTime lastModified = Files.getLastModifiedTime(path, new LinkOption[0]);
        final String contentType = MoreObjects.firstNonNull(Files.probeContentType(path), "application/octet-stream");
        final ImmutableListMultimap.Builder<String, String> builder2 = ImmutableListMultimap.builder();
        builder2.put("content-length", "" + length);
        builder2.put("content-type", contentType);
        if (lastModified != null) {
            final DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            builder2.put("last-modified", format.format(new Date(lastModified.toMillis())));
        }
        this.headers = builder2.build();
    }
    
    private static URI toUri(final URL url) throws IOException {
        try {
            return url.toURI();
        }
        catch (URISyntaxException e) {
            throw new IOException("URL " + url + " cannot be converted to a URI", e);
        }
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        this.connect();
        return this.stream;
    }
    
    @Override
    public Map<String, List<String>> getHeaderFields() {
        try {
            this.connect();
        }
        catch (IOException e) {
            return (Map<String, List<String>>)ImmutableMap.of();
        }
        return (Map<String, List<String>>)this.headers.asMap();
    }
    
    @Override
    public String getHeaderField(final String name) {
        try {
            this.connect();
        }
        catch (IOException e) {
            return null;
        }
        return Iterables.getFirst(this.headers.get(Ascii.toLowerCase(name)), (String)null);
    }
}
