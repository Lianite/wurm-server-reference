// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;

public abstract class WatchServiceConfiguration
{
    static final WatchServiceConfiguration DEFAULT;
    
    public static WatchServiceConfiguration polling(final long interval, final TimeUnit timeUnit) {
        return new PollingConfig(interval, timeUnit);
    }
    
    abstract AbstractWatchService newWatchService(final FileSystemView p0, final PathService p1);
    
    static {
        DEFAULT = polling(5L, TimeUnit.SECONDS);
    }
    
    private static final class PollingConfig extends WatchServiceConfiguration
    {
        private final long interval;
        private final TimeUnit timeUnit;
        
        private PollingConfig(final long interval, final TimeUnit timeUnit) {
            Preconditions.checkArgument(interval > 0L, "interval (%s) must be positive", interval);
            this.interval = interval;
            this.timeUnit = Preconditions.checkNotNull(timeUnit);
        }
        
        @Override
        AbstractWatchService newWatchService(final FileSystemView view, final PathService pathService) {
            return new PollingWatchService(view, pathService, view.state(), this.interval, this.timeUnit);
        }
        
        @Override
        public String toString() {
            return "WatchServiceConfiguration.polling(" + this.interval + ", " + this.timeUnit + ")";
        }
    }
}
