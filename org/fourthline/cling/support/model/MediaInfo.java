// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class MediaInfo
{
    private String currentURI;
    private String currentURIMetaData;
    private String nextURI;
    private String nextURIMetaData;
    private UnsignedIntegerFourBytes numberOfTracks;
    private String mediaDuration;
    private StorageMedium playMedium;
    private StorageMedium recordMedium;
    private RecordMediumWriteStatus writeStatus;
    
    public MediaInfo() {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
    }
    
    public MediaInfo(final Map<String, ActionArgumentValue> args) {
        this((String)args.get("CurrentURI").getValue(), (String)args.get("CurrentURIMetaData").getValue(), (String)args.get("NextURI").getValue(), (String)args.get("NextURIMetaData").getValue(), (UnsignedIntegerFourBytes)args.get("NrTracks").getValue(), (String)args.get("MediaDuration").getValue(), StorageMedium.valueOrVendorSpecificOf((String)args.get("PlayMedium").getValue()), StorageMedium.valueOrVendorSpecificOf((String)args.get("RecordMedium").getValue()), RecordMediumWriteStatus.valueOrUnknownOf((String)args.get("WriteStatus").getValue()));
    }
    
    public MediaInfo(final String currentURI, final String currentURIMetaData) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
    }
    
    public MediaInfo(final String currentURI, final String currentURIMetaData, final UnsignedIntegerFourBytes numberOfTracks, final String mediaDuration, final StorageMedium playMedium) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
    }
    
    public MediaInfo(final String currentURI, final String currentURIMetaData, final UnsignedIntegerFourBytes numberOfTracks, final String mediaDuration, final StorageMedium playMedium, final StorageMedium recordMedium, final RecordMediumWriteStatus writeStatus) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
        this.recordMedium = recordMedium;
        this.writeStatus = writeStatus;
    }
    
    public MediaInfo(final String currentURI, final String currentURIMetaData, final String nextURI, final String nextURIMetaData, final UnsignedIntegerFourBytes numberOfTracks, final String mediaDuration, final StorageMedium playMedium) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.nextURI = nextURI;
        this.nextURIMetaData = nextURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
    }
    
    public MediaInfo(final String currentURI, final String currentURIMetaData, final String nextURI, final String nextURIMetaData, final UnsignedIntegerFourBytes numberOfTracks, final String mediaDuration, final StorageMedium playMedium, final StorageMedium recordMedium, final RecordMediumWriteStatus writeStatus) {
        this.currentURI = "";
        this.currentURIMetaData = "";
        this.nextURI = "NOT_IMPLEMENTED";
        this.nextURIMetaData = "NOT_IMPLEMENTED";
        this.numberOfTracks = new UnsignedIntegerFourBytes(0L);
        this.mediaDuration = "00:00:00";
        this.playMedium = StorageMedium.NONE;
        this.recordMedium = StorageMedium.NOT_IMPLEMENTED;
        this.writeStatus = RecordMediumWriteStatus.NOT_IMPLEMENTED;
        this.currentURI = currentURI;
        this.currentURIMetaData = currentURIMetaData;
        this.nextURI = nextURI;
        this.nextURIMetaData = nextURIMetaData;
        this.numberOfTracks = numberOfTracks;
        this.mediaDuration = mediaDuration;
        this.playMedium = playMedium;
        this.recordMedium = recordMedium;
        this.writeStatus = writeStatus;
    }
    
    public String getCurrentURI() {
        return this.currentURI;
    }
    
    public String getCurrentURIMetaData() {
        return this.currentURIMetaData;
    }
    
    public String getNextURI() {
        return this.nextURI;
    }
    
    public String getNextURIMetaData() {
        return this.nextURIMetaData;
    }
    
    public UnsignedIntegerFourBytes getNumberOfTracks() {
        return this.numberOfTracks;
    }
    
    public String getMediaDuration() {
        return this.mediaDuration;
    }
    
    public StorageMedium getPlayMedium() {
        return this.playMedium;
    }
    
    public StorageMedium getRecordMedium() {
        return this.recordMedium;
    }
    
    public RecordMediumWriteStatus getWriteStatus() {
        return this.writeStatus;
    }
}
