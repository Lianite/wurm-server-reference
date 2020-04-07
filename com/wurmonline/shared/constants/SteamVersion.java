// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.constants;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SteamVersion
{
    public static final String UNKNOWN_VERSION = "UNKNOWN";
    static final int MAJOR = 1;
    static final int COMPATIBILITY = 9;
    static final int CLIENT = 1;
    static final int SERVER = 6;
    private static final SteamVersion current;
    private static final String patternString = "^(?:version=)?(?<major>\\d+)\\.(?<compatibility>\\d+)\\.(?<client>\\d+)\\.(?<server>\\d+);?$";
    private static final Pattern versionPattern;
    private int major;
    private int compatibility;
    private int client;
    private int server;
    
    public static SteamVersion getCurrentVersion() {
        return SteamVersion.current;
    }
    
    public SteamVersion(final String version) {
        this.major = 0;
        this.compatibility = 0;
        this.client = 0;
        this.server = 0;
        if (version == null) {
            return;
        }
        final Matcher matcher = SteamVersion.versionPattern.matcher(version);
        if (matcher.matches()) {
            this.major = Integer.valueOf(matcher.group("major"));
            this.compatibility = Integer.valueOf(matcher.group("compatibility"));
            this.client = Integer.valueOf(matcher.group("client"));
            this.server = Integer.valueOf(matcher.group("server"));
        }
    }
    
    SteamVersion(final int major, final int compatibility, final int client, final int server) {
        this.major = 0;
        this.compatibility = 0;
        this.client = 0;
        this.server = 0;
        this.major = major;
        this.compatibility = compatibility;
        this.client = client;
        this.server = server;
    }
    
    public boolean isCompatibleWith(final SteamVersion version) {
        return this.major == version.major && this.compatibility == version.compatibility;
    }
    
    public String getTag() {
        return "version=" + this.toString() + ";";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SteamVersion version = (SteamVersion)o;
        return this.major == version.major && this.compatibility == version.compatibility && this.client == version.client && this.server == version.server;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.major, this.compatibility, this.client, this.server);
    }
    
    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d", this.major, this.compatibility, this.client, this.server);
    }
    
    public boolean isCompatibleWith(final String version) {
        return this.isCompatibleWith(new SteamVersion(version));
    }
    
    static {
        current = new SteamVersion(1, 9, 1, 6);
        versionPattern = Pattern.compile("^(?:version=)?(?<major>\\d+)\\.(?<compatibility>\\d+)\\.(?<client>\\d+)\\.(?<server>\\d+);?$");
    }
}
