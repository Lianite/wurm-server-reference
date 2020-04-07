// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.flywaydb.core.internal.util.FileCopyUtils;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.net.URLDecoder;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.scanner.Resource;

public class ClassPathResource implements Comparable<ClassPathResource>, Resource
{
    private String location;
    private ClassLoader classLoader;
    
    public ClassPathResource(final String location, final ClassLoader classLoader) {
        this.location = location;
        this.classLoader = classLoader;
    }
    
    @Override
    public String getLocation() {
        return this.location;
    }
    
    @Override
    public String getLocationOnDisk() {
        final URL url = this.getUrl();
        if (url == null) {
            throw new FlywayException("Unable to location resource on disk: " + this.location);
        }
        try {
            return new File(URLDecoder.decode(url.getPath(), "UTF-8")).getAbsolutePath();
        }
        catch (UnsupportedEncodingException e) {
            throw new FlywayException("Unknown encoding: UTF-8", e);
        }
    }
    
    private URL getUrl() {
        return this.classLoader.getResource(this.location);
    }
    
    @Override
    public String loadAsString(final String encoding) {
        try {
            final InputStream inputStream = this.classLoader.getResourceAsStream(this.location);
            if (inputStream == null) {
                throw new FlywayException("Unable to obtain inputstream for resource: " + this.location);
            }
            final Reader reader = new InputStreamReader(inputStream, Charset.forName(encoding));
            return FileCopyUtils.copyToString(reader);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load resource: " + this.location + " (encoding: " + encoding + ")", e);
        }
    }
    
    @Override
    public byte[] loadAsBytes() {
        try {
            final InputStream inputStream = this.classLoader.getResourceAsStream(this.location);
            if (inputStream == null) {
                throw new FlywayException("Unable to obtain inputstream for resource: " + this.location);
            }
            return FileCopyUtils.copyToByteArray(inputStream);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load resource: " + this.location, e);
        }
    }
    
    @Override
    public String getFilename() {
        return this.location.substring(this.location.lastIndexOf("/") + 1);
    }
    
    public boolean exists() {
        return this.getUrl() != null;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ClassPathResource that = (ClassPathResource)o;
        return this.location.equals(that.location);
    }
    
    @Override
    public int hashCode() {
        return this.location.hashCode();
    }
    
    @Override
    public int compareTo(final ClassPathResource o) {
        return this.location.compareTo(o.location);
    }
}
