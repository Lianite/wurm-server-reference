// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath.android;

import java.io.IOException;
import org.flywaydb.core.api.FlywayException;
import java.io.Reader;
import org.flywaydb.core.internal.util.FileCopyUtils;
import java.io.InputStreamReader;
import android.content.res.AssetManager;
import org.flywaydb.core.internal.util.scanner.Resource;

public class AndroidResource implements Resource
{
    private final AssetManager assetManager;
    private final String path;
    private final String name;
    
    public AndroidResource(final AssetManager assetManager, final String path, final String name) {
        this.assetManager = assetManager;
        this.path = path;
        this.name = name;
    }
    
    @Override
    public String getLocation() {
        return this.path + "/" + this.name;
    }
    
    @Override
    public String getLocationOnDisk() {
        return null;
    }
    
    @Override
    public String loadAsString(final String encoding) {
        try {
            return FileCopyUtils.copyToString(new InputStreamReader(this.assetManager.open(this.getLocation()), encoding));
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load asset: " + this.getLocation(), e);
        }
    }
    
    @Override
    public byte[] loadAsBytes() {
        try {
            return FileCopyUtils.copyToByteArray(this.assetManager.open(this.getLocation()));
        }
        catch (IOException e) {
            throw new FlywayException("Unable to load asset: " + this.getLocation(), e);
        }
    }
    
    @Override
    public String getFilename() {
        return this.name;
    }
}
