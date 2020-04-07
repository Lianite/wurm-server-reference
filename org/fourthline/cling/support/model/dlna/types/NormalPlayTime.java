// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

import java.util.regex.Matcher;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.regex.Pattern;

public class NormalPlayTime
{
    static final Pattern pattern;
    private long milliseconds;
    
    public NormalPlayTime(final long milliseconds) {
        if (milliseconds < 0L) {
            throw new InvalidValueException("Invalid parameter milliseconds: " + milliseconds);
        }
        this.milliseconds = milliseconds;
    }
    
    public NormalPlayTime(final long hours, final long minutes, final long seconds, final long milliseconds) throws InvalidValueException {
        if (hours < 0L) {
            throw new InvalidValueException("Invalid parameter hours: " + hours);
        }
        if (minutes < 0L || minutes > 59L) {
            throw new InvalidValueException("Invalid parameter minutes: " + hours);
        }
        if (seconds < 0L || seconds > 59L) {
            throw new InvalidValueException("Invalid parameter seconds: " + hours);
        }
        if (milliseconds < 0L || milliseconds > 999L) {
            throw new InvalidValueException("Invalid parameter milliseconds: " + milliseconds);
        }
        this.milliseconds = (hours * 60L * 60L + minutes * 60L + seconds) * 1000L + milliseconds;
    }
    
    public long getMilliseconds() {
        return this.milliseconds;
    }
    
    public void setMilliseconds(final long milliseconds) {
        if (milliseconds < 0L) {
            throw new InvalidValueException("Invalid parameter milliseconds: " + milliseconds);
        }
        this.milliseconds = milliseconds;
    }
    
    public String getString() {
        return this.getString(Format.SECONDS);
    }
    
    public String getString(final Format format) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(this.milliseconds);
        final long ms = this.milliseconds % 1000L;
        switch (format) {
            case TIME: {
                seconds = TimeUnit.MILLISECONDS.toSeconds(this.milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this.milliseconds));
                final long hours = TimeUnit.MILLISECONDS.toHours(this.milliseconds);
                final long minutes = TimeUnit.MILLISECONDS.toMinutes(this.milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this.milliseconds));
                return String.format(Locale.ROOT, "%d:%02d:%02d.%03d", hours, minutes, seconds, ms);
            }
            default: {
                return String.format(Locale.ROOT, "%d.%03d", seconds, ms);
            }
        }
    }
    
    public static NormalPlayTime valueOf(final String s) throws InvalidValueException {
        final Matcher matcher = NormalPlayTime.pattern.matcher(s);
        if (matcher.matches()) {
            int msMultiplier = 0;
            try {
                if (matcher.group(1) != null) {
                    msMultiplier = (int)Math.pow(10.0, 3 - matcher.group(5).length());
                    return new NormalPlayTime(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)), Long.parseLong(matcher.group(5)) * msMultiplier);
                }
                msMultiplier = (int)Math.pow(10.0, 3 - matcher.group(8).length());
                return new NormalPlayTime(Long.parseLong(matcher.group(6)) * 1000L + Long.parseLong(matcher.group(8)) * msMultiplier);
            }
            catch (NumberFormatException ex) {}
        }
        throw new InvalidValueException("Can't parse NormalPlayTime: " + s);
    }
    
    static {
        pattern = Pattern.compile("^(\\d+):(\\d{1,2}):(\\d{1,2})(\\.(\\d{1,3}))?|(\\d+)(\\.(\\d{1,3}))?$", 2);
    }
    
    public enum Format
    {
        SECONDS, 
        TIME;
    }
}
