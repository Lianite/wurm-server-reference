// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.filesystem;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.FileCopyUtils;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import org.flywaydb.core.internal.util.StringUtils;
import java.io.File;
import org.flywaydb.core.internal.util.scanner.Resource;

public class FileSystemResource implements Resource, Comparable<FileSystemResource>
{
    private File location;
    
    public FileSystemResource(final String location) {
        this.location = new File(location);
    }
    
    @Override
    public String getLocation() {
        return StringUtils.replaceAll(this.location.getPath(), "\\", "/");
    }
    
    @Override
    public String getLocationOnDisk() {
        return this.location.getAbsolutePath();
    }
    
    @Override
    public String loadAsString(final String encoding) {
        try {
            final InputStream inputStream = new FileInputStream(this.location);
            final Reader reader = new InputStreamReader(inputStream, Charset.forName(encoding));
            return FileCopyUtils.copyToString(reader);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load filesystem resource: " + this.location.getPath() + " (encoding: " + encoding + ")", e);
        }
    }
    
    @Override
    public byte[] loadAsBytes() {
        try {
            final InputStream inputStream = new FileInputStream(this.location);
            return FileCopyUtils.copyToByteArray(inputStream);
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load filesystem resource: " + this.location.getPath(), e);
        }
    }
    
    @Override
    public String getFilename() {
        return this.location.getName();
    }
    
    @Override
    public int compareTo(final FileSystemResource o) {
        return this.location.compareTo(o.location);
    }
}
