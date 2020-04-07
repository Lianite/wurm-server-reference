// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import java.util.Date;

public class ExpirationDetails
{
    public static final int UNLIMITED_AGE = 0;
    private int maxAgeSeconds;
    private long lastRefreshTimestampSeconds;
    private static String simpleName;
    
    public ExpirationDetails() {
        this.maxAgeSeconds = 0;
        this.lastRefreshTimestampSeconds = this.getCurrentTimestampSeconds();
    }
    
    public ExpirationDetails(final int maxAgeSeconds) {
        this.maxAgeSeconds = 0;
        this.lastRefreshTimestampSeconds = this.getCurrentTimestampSeconds();
        this.maxAgeSeconds = maxAgeSeconds;
    }
    
    public int getMaxAgeSeconds() {
        return this.maxAgeSeconds;
    }
    
    public long getLastRefreshTimestampSeconds() {
        return this.lastRefreshTimestampSeconds;
    }
    
    public void setLastRefreshTimestampSeconds(final long lastRefreshTimestampSeconds) {
        this.lastRefreshTimestampSeconds = lastRefreshTimestampSeconds;
    }
    
    public void stampLastRefresh() {
        this.setLastRefreshTimestampSeconds(this.getCurrentTimestampSeconds());
    }
    
    public boolean hasExpired() {
        return this.hasExpired(false);
    }
    
    public boolean hasExpired(final boolean halfTime) {
        return this.maxAgeSeconds != 0 && this.lastRefreshTimestampSeconds + this.maxAgeSeconds / (halfTime ? 2 : 1) < this.getCurrentTimestampSeconds();
    }
    
    public long getSecondsUntilExpiration() {
        return (this.maxAgeSeconds == 0) ? 2147483647L : (this.lastRefreshTimestampSeconds + this.maxAgeSeconds - this.getCurrentTimestampSeconds());
    }
    
    protected long getCurrentTimestampSeconds() {
        return new Date().getTime() / 1000L;
    }
    
    @Override
    public String toString() {
        return "(" + ExpirationDetails.simpleName + ")" + " MAX AGE: " + this.maxAgeSeconds;
    }
    
    static {
        ExpirationDetails.simpleName = ExpirationDetails.class.getSimpleName();
    }
}
