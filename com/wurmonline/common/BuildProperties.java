// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BuildProperties
{
    private final Properties properties;
    
    private BuildProperties() {
        this.properties = new Properties();
    }
    
    public static BuildProperties getPropertiesFor(final String path) throws IOException {
        final BuildProperties bp = new BuildProperties();
        try (final InputStream inputStream = BuildProperties.class.getResourceAsStream(path)) {
            bp.properties.load(inputStream);
        }
        return bp;
    }
    
    public String getGitSha1Short() {
        final String sha = this.getGitSha1();
        if (sha.length() < 7) {
            return sha;
        }
        return sha.substring(0, 7);
    }
    
    public String getGitBranch() {
        return this.properties.getProperty("git-branch");
    }
    
    public String getGitSha1() {
        return this.properties.getProperty("git-sha-1");
    }
    
    public String getVersion() {
        return this.properties.getProperty("version");
    }
    
    public String getBuildTimeString() {
        return this.properties.getProperty("build-time");
    }
}
