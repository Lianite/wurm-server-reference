// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class PositionInfo
{
    private UnsignedIntegerFourBytes track;
    private String trackDuration;
    private String trackMetaData;
    private String trackURI;
    private String relTime;
    private String absTime;
    private int relCount;
    private int absCount;
    
    public PositionInfo() {
        this.track = new UnsignedIntegerFourBytes(0L);
        this.trackDuration = "00:00:00";
        this.trackMetaData = "NOT_IMPLEMENTED";
        this.trackURI = "";
        this.relTime = "00:00:00";
        this.absTime = "00:00:00";
        this.relCount = Integer.MAX_VALUE;
        this.absCount = Integer.MAX_VALUE;
    }
    
    public PositionInfo(final Map<String, ActionArgumentValue> args) {
        this(((UnsignedIntegerFourBytes)args.get("Track").getValue()).getValue(), (String)args.get("TrackDuration").getValue(), (String)args.get("TrackMetaData").getValue(), (String)args.get("TrackURI").getValue(), (String)args.get("RelTime").getValue(), (String)args.get("AbsTime").getValue(), (int)args.get("RelCount").getValue(), (int)args.get("AbsCount").getValue());
    }
    
    public PositionInfo(final PositionInfo copy, final String relTime, final String absTime) {
        this.track = new UnsignedIntegerFourBytes(0L);
        this.trackDuration = "00:00:00";
        this.trackMetaData = "NOT_IMPLEMENTED";
        this.trackURI = "";
        this.relTime = "00:00:00";
        this.absTime = "00:00:00";
        this.relCount = Integer.MAX_VALUE;
        this.absCount = Integer.MAX_VALUE;
        this.track = copy.track;
        this.trackDuration = copy.trackDuration;
        this.trackMetaData = copy.trackMetaData;
        this.trackURI = copy.trackURI;
        this.relTime = relTime;
        this.absTime = absTime;
        this.relCount = copy.relCount;
        this.absCount = copy.absCount;
    }
    
    public PositionInfo(final PositionInfo copy, final long relTimeSeconds, final long absTimeSeconds) {
        this.track = new UnsignedIntegerFourBytes(0L);
        this.trackDuration = "00:00:00";
        this.trackMetaData = "NOT_IMPLEMENTED";
        this.trackURI = "";
        this.relTime = "00:00:00";
        this.absTime = "00:00:00";
        this.relCount = Integer.MAX_VALUE;
        this.absCount = Integer.MAX_VALUE;
        this.track = copy.track;
        this.trackDuration = copy.trackDuration;
        this.trackMetaData = copy.trackMetaData;
        this.trackURI = copy.trackURI;
        this.relTime = ModelUtil.toTimeString(relTimeSeconds);
        this.absTime = ModelUtil.toTimeString(absTimeSeconds);
        this.relCount = copy.relCount;
        this.absCount = copy.absCount;
    }
    
    public PositionInfo(final long track, final String trackDuration, final String trackURI, final String relTime, final String absTime) {
        this.track = new UnsignedIntegerFourBytes(0L);
        this.trackDuration = "00:00:00";
        this.trackMetaData = "NOT_IMPLEMENTED";
        this.trackURI = "";
        this.relTime = "00:00:00";
        this.absTime = "00:00:00";
        this.relCount = Integer.MAX_VALUE;
        this.absCount = Integer.MAX_VALUE;
        this.track = new UnsignedIntegerFourBytes(track);
        this.trackDuration = trackDuration;
        this.trackURI = trackURI;
        this.relTime = relTime;
        this.absTime = absTime;
    }
    
    public PositionInfo(final long track, final String trackDuration, final String trackMetaData, final String trackURI, final String relTime, final String absTime, final int relCount, final int absCount) {
        this.track = new UnsignedIntegerFourBytes(0L);
        this.trackDuration = "00:00:00";
        this.trackMetaData = "NOT_IMPLEMENTED";
        this.trackURI = "";
        this.relTime = "00:00:00";
        this.absTime = "00:00:00";
        this.relCount = Integer.MAX_VALUE;
        this.absCount = Integer.MAX_VALUE;
        this.track = new UnsignedIntegerFourBytes(track);
        this.trackDuration = trackDuration;
        this.trackMetaData = trackMetaData;
        this.trackURI = trackURI;
        this.relTime = relTime;
        this.absTime = absTime;
        this.relCount = relCount;
        this.absCount = absCount;
    }
    
    public PositionInfo(final long track, final String trackMetaData, final String trackURI) {
        this.track = new UnsignedIntegerFourBytes(0L);
        this.trackDuration = "00:00:00";
        this.trackMetaData = "NOT_IMPLEMENTED";
        this.trackURI = "";
        this.relTime = "00:00:00";
        this.absTime = "00:00:00";
        this.relCount = Integer.MAX_VALUE;
        this.absCount = Integer.MAX_VALUE;
        this.track = new UnsignedIntegerFourBytes(track);
        this.trackMetaData = trackMetaData;
        this.trackURI = trackURI;
    }
    
    public UnsignedIntegerFourBytes getTrack() {
        return this.track;
    }
    
    public String getTrackDuration() {
        return this.trackDuration;
    }
    
    public String getTrackMetaData() {
        return this.trackMetaData;
    }
    
    public String getTrackURI() {
        return this.trackURI;
    }
    
    public String getRelTime() {
        return this.relTime;
    }
    
    public String getAbsTime() {
        return this.absTime;
    }
    
    public int getRelCount() {
        return this.relCount;
    }
    
    public int getAbsCount() {
        return this.absCount;
    }
    
    public void setTrackDuration(final String trackDuration) {
        this.trackDuration = trackDuration;
    }
    
    public void setRelTime(final String relTime) {
        this.relTime = relTime;
    }
    
    public long getTrackDurationSeconds() {
        return (this.getTrackDuration() == null) ? 0L : ModelUtil.fromTimeString(this.getTrackDuration());
    }
    
    public long getTrackElapsedSeconds() {
        return (this.getRelTime() == null || this.getRelTime().equals("NOT_IMPLEMENTED")) ? 0L : ModelUtil.fromTimeString(this.getRelTime());
    }
    
    public long getTrackRemainingSeconds() {
        return this.getTrackDurationSeconds() - this.getTrackElapsedSeconds();
    }
    
    public int getElapsedPercent() {
        final long elapsed = this.getTrackElapsedSeconds();
        final long total = this.getTrackDurationSeconds();
        if (elapsed == 0L || total == 0L) {
            return 0;
        }
        return (int)(Object)new Double(elapsed / (total / 100.0));
    }
    
    @Override
    public String toString() {
        return "(PositionInfo) Track: " + this.getTrack() + " RelTime: " + this.getRelTime() + " Duration: " + this.getTrackDuration() + " Percent: " + this.getElapsedPercent();
    }
}
