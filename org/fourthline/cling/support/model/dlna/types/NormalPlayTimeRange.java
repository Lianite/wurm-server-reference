// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.types;

import org.fourthline.cling.model.types.InvalidValueException;

public class NormalPlayTimeRange
{
    public static final String PREFIX = "npt=";
    private NormalPlayTime timeStart;
    private NormalPlayTime timeEnd;
    private NormalPlayTime timeDuration;
    
    public NormalPlayTimeRange(final long timeStart, final long timeEnd) {
        this.timeStart = new NormalPlayTime(timeStart);
        this.timeEnd = new NormalPlayTime(timeEnd);
    }
    
    public NormalPlayTimeRange(final NormalPlayTime timeStart, final NormalPlayTime timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
    
    public NormalPlayTimeRange(final NormalPlayTime timeStart, final NormalPlayTime timeEnd, final NormalPlayTime timeDuration) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.timeDuration = timeDuration;
    }
    
    public NormalPlayTime getTimeStart() {
        return this.timeStart;
    }
    
    public NormalPlayTime getTimeEnd() {
        return this.timeEnd;
    }
    
    public NormalPlayTime getTimeDuration() {
        return this.timeDuration;
    }
    
    public String getString() {
        return this.getString(true);
    }
    
    public String getString(final boolean includeDuration) {
        String s = "npt=";
        s = s + this.timeStart.getString() + "-";
        if (this.timeEnd != null) {
            s += this.timeEnd.getString();
        }
        if (includeDuration) {
            s = s + "/" + ((this.timeDuration != null) ? this.timeDuration.getString() : "*");
        }
        return s;
    }
    
    public static NormalPlayTimeRange valueOf(final String s) throws InvalidValueException {
        return valueOf(s, false);
    }
    
    public static NormalPlayTimeRange valueOf(final String s, final boolean mandatoryTimeEnd) throws InvalidValueException {
        if (s.startsWith("npt=")) {
            NormalPlayTime timeEnd = null;
            NormalPlayTime timeDuration = null;
            final String[] params = s.substring("npt=".length()).split("[-/]");
            switch (params.length) {
                case 3: {
                    if (params[2].length() != 0 && !params[2].equals("*")) {
                        timeDuration = NormalPlayTime.valueOf(params[2]);
                    }
                }
                case 2: {
                    if (params[1].length() != 0) {
                        timeEnd = NormalPlayTime.valueOf(params[1]);
                    }
                }
                case 1: {
                    if (params[0].length() != 0 && (!mandatoryTimeEnd || (mandatoryTimeEnd && params.length > 1))) {
                        final NormalPlayTime timeStart = NormalPlayTime.valueOf(params[0]);
                        return new NormalPlayTimeRange(timeStart, timeEnd, timeDuration);
                    }
                    break;
                }
            }
        }
        throw new InvalidValueException("Can't parse NormalPlayTimeRange: " + s);
    }
}
