// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util;

import org.flywaydb.core.api.FlywayException;

public final class Location implements Comparable<Location>
{
    private static final String CLASSPATH_PREFIX = "classpath:";
    public static final String FILESYSTEM_PREFIX = "filesystem:";
    private String prefix;
    private String path;
    
    public Location(final String descriptor) {
        final String normalizedDescriptor = descriptor.trim().replace("\\", "/");
        if (normalizedDescriptor.contains(":")) {
            this.prefix = normalizedDescriptor.substring(0, normalizedDescriptor.indexOf(":") + 1);
            this.path = normalizedDescriptor.substring(normalizedDescriptor.indexOf(":") + 1);
        }
        else {
            this.prefix = "classpath:";
            this.path = normalizedDescriptor;
        }
        if (this.isClassPath()) {
            this.path = this.path.replace(".", "/");
            if (this.path.startsWith("/")) {
                this.path = this.path.substring(1);
            }
        }
        else if (!this.isFileSystem()) {
            throw new FlywayException("Unknown prefix for location (should be either filesystem: or classpath:): " + normalizedDescriptor);
        }
        if (this.path.endsWith("/")) {
            this.path = this.path.substring(0, this.path.length() - 1);
        }
    }
    
    public boolean isClassPath() {
        return "classpath:".equals(this.prefix);
    }
    
    public boolean isFileSystem() {
        return "filesystem:".equals(this.prefix);
    }
    
    public boolean isParentOf(final Location other) {
        return (other.getDescriptor() + "/").startsWith(this.getDescriptor() + "/");
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public String getDescriptor() {
        return this.prefix + this.path;
    }
    
    @Override
    public int compareTo(final Location o) {
        return this.getDescriptor().compareTo(o.getDescriptor());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Location location = (Location)o;
        return this.getDescriptor().equals(location.getDescriptor());
    }
    
    @Override
    public int hashCode() {
        return this.getDescriptor().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getDescriptor();
    }
}
