// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.avtransport;

import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import java.net.URI;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.RecordMediumWriteStatus;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportStatus;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.support.lastchange.LastChangeDelegator;

@UpnpService(serviceId = @UpnpServiceId("AVTransport"), serviceType = @UpnpServiceType(value = "AVTransport", version = 1), stringConvertibleTypes = { LastChange.class })
@UpnpStateVariables({ @UpnpStateVariable(name = "TransportState", sendEvents = false, allowedValuesEnum = TransportState.class), @UpnpStateVariable(name = "TransportStatus", sendEvents = false, allowedValuesEnum = TransportStatus.class), @UpnpStateVariable(name = "PlaybackStorageMedium", sendEvents = false, defaultValue = "NONE", allowedValuesEnum = StorageMedium.class), @UpnpStateVariable(name = "RecordStorageMedium", sendEvents = false, defaultValue = "NOT_IMPLEMENTED", allowedValuesEnum = StorageMedium.class), @UpnpStateVariable(name = "PossiblePlaybackStorageMedia", sendEvents = false, datatype = "string", defaultValue = "NETWORK"), @UpnpStateVariable(name = "PossibleRecordStorageMedia", sendEvents = false, datatype = "string", defaultValue = "NOT_IMPLEMENTED"), @UpnpStateVariable(name = "CurrentPlayMode", sendEvents = false, defaultValue = "NORMAL", allowedValuesEnum = PlayMode.class), @UpnpStateVariable(name = "TransportPlaySpeed", sendEvents = false, datatype = "string", defaultValue = "1"), @UpnpStateVariable(name = "RecordMediumWriteStatus", sendEvents = false, defaultValue = "NOT_IMPLEMENTED", allowedValuesEnum = RecordMediumWriteStatus.class), @UpnpStateVariable(name = "CurrentRecordQualityMode", sendEvents = false, defaultValue = "NOT_IMPLEMENTED", allowedValuesEnum = RecordQualityMode.class), @UpnpStateVariable(name = "PossibleRecordQualityModes", sendEvents = false, datatype = "string", defaultValue = "NOT_IMPLEMENTED"), @UpnpStateVariable(name = "NumberOfTracks", sendEvents = false, datatype = "ui4", defaultValue = "0"), @UpnpStateVariable(name = "CurrentTrack", sendEvents = false, datatype = "ui4", defaultValue = "0"), @UpnpStateVariable(name = "CurrentTrackDuration", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "CurrentMediaDuration", sendEvents = false, datatype = "string", defaultValue = "00:00:00"), @UpnpStateVariable(name = "CurrentTrackMetaData", sendEvents = false, datatype = "string", defaultValue = "NOT_IMPLEMENTED"), @UpnpStateVariable(name = "CurrentTrackURI", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "AVTransportURI", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "AVTransportURIMetaData", sendEvents = false, datatype = "string", defaultValue = "NOT_IMPLEMENTED"), @UpnpStateVariable(name = "NextAVTransportURI", sendEvents = false, datatype = "string", defaultValue = "NOT_IMPLEMENTED"), @UpnpStateVariable(name = "NextAVTransportURIMetaData", sendEvents = false, datatype = "string", defaultValue = "NOT_IMPLEMENTED"), @UpnpStateVariable(name = "RelativeTimePosition", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "AbsoluteTimePosition", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "RelativeCounterPosition", sendEvents = false, datatype = "i4", defaultValue = "2147483647"), @UpnpStateVariable(name = "AbsoluteCounterPosition", sendEvents = false, datatype = "i4", defaultValue = "2147483647"), @UpnpStateVariable(name = "CurrentTransportActions", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_SeekMode", sendEvents = false, allowedValuesEnum = SeekMode.class), @UpnpStateVariable(name = "A_ARG_TYPE_SeekTarget", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "A_ARG_TYPE_InstanceID", sendEvents = false, datatype = "ui4") })
public abstract class AbstractAVTransportService implements LastChangeDelegator
{
    @UpnpStateVariable(eventMaximumRateMilliseconds = 200)
    private final LastChange lastChange;
    protected final PropertyChangeSupport propertyChangeSupport;
    
    protected AbstractAVTransportService() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }
    
    protected AbstractAVTransportService(final LastChange lastChange) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = lastChange;
    }
    
    protected AbstractAVTransportService(final PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }
    
    protected AbstractAVTransportService(final PropertyChangeSupport propertyChangeSupport, final LastChange lastChange) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = lastChange;
    }
    
    @Override
    public LastChange getLastChange() {
        return this.lastChange;
    }
    
    @Override
    public void appendCurrentState(final LastChange lc, final UnsignedIntegerFourBytes instanceId) throws Exception {
        final MediaInfo mediaInfo = this.getMediaInfo(instanceId);
        final TransportInfo transportInfo = this.getTransportInfo(instanceId);
        final TransportSettings transportSettings = this.getTransportSettings(instanceId);
        final PositionInfo positionInfo = this.getPositionInfo(instanceId);
        final DeviceCapabilities deviceCaps = this.getDeviceCapabilities(instanceId);
        lc.setEventedValue(instanceId, new AVTransportVariable.AVTransportURI(URI.create(mediaInfo.getCurrentURI())), new AVTransportVariable.AVTransportURIMetaData(mediaInfo.getCurrentURIMetaData()), new AVTransportVariable.CurrentMediaDuration(mediaInfo.getMediaDuration()), new AVTransportVariable.CurrentPlayMode(transportSettings.getPlayMode()), new AVTransportVariable.CurrentRecordQualityMode(transportSettings.getRecQualityMode()), new AVTransportVariable.CurrentTrack(positionInfo.getTrack()), new AVTransportVariable.CurrentTrackDuration(positionInfo.getTrackDuration()), new AVTransportVariable.CurrentTrackMetaData(positionInfo.getTrackMetaData()), new AVTransportVariable.CurrentTrackURI(URI.create(positionInfo.getTrackURI())), new AVTransportVariable.CurrentTransportActions(this.getCurrentTransportActions(instanceId)), new AVTransportVariable.NextAVTransportURI(URI.create(mediaInfo.getNextURI())), new AVTransportVariable.NextAVTransportURIMetaData(mediaInfo.getNextURIMetaData()), new AVTransportVariable.NumberOfTracks(mediaInfo.getNumberOfTracks()), new AVTransportVariable.PossiblePlaybackStorageMedia(deviceCaps.getPlayMedia()), new AVTransportVariable.PossibleRecordQualityModes(deviceCaps.getRecQualityModes()), new AVTransportVariable.PossibleRecordStorageMedia(deviceCaps.getRecMedia()), new AVTransportVariable.RecordMediumWriteStatus(mediaInfo.getWriteStatus()), new AVTransportVariable.RecordStorageMedium(mediaInfo.getRecordMedium()), new AVTransportVariable.TransportPlaySpeed(transportInfo.getCurrentSpeed()), new AVTransportVariable.TransportState(transportInfo.getCurrentTransportState()), new AVTransportVariable.TransportStatus(transportInfo.getCurrentTransportStatus()));
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }
    
    public static UnsignedIntegerFourBytes getDefaultInstanceID() {
        return new UnsignedIntegerFourBytes(0L);
    }
    
    @UpnpAction
    public abstract void setAVTransportURI(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "CurrentURI", stateVariable = "AVTransportURI") final String p1, @UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData") final String p2) throws AVTransportException;
    
    @UpnpAction
    public abstract void setNextAVTransportURI(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "NextURI", stateVariable = "AVTransportURI") final String p1, @UpnpInputArgument(name = "NextURIMetaData", stateVariable = "AVTransportURIMetaData") final String p2) throws AVTransportException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "NrTracks", stateVariable = "NumberOfTracks", getterName = "getNumberOfTracks"), @UpnpOutputArgument(name = "MediaDuration", stateVariable = "CurrentMediaDuration", getterName = "getMediaDuration"), @UpnpOutputArgument(name = "CurrentURI", stateVariable = "AVTransportURI", getterName = "getCurrentURI"), @UpnpOutputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData", getterName = "getCurrentURIMetaData"), @UpnpOutputArgument(name = "NextURI", stateVariable = "NextAVTransportURI", getterName = "getNextURI"), @UpnpOutputArgument(name = "NextURIMetaData", stateVariable = "NextAVTransportURIMetaData", getterName = "getNextURIMetaData"), @UpnpOutputArgument(name = "PlayMedium", stateVariable = "PlaybackStorageMedium", getterName = "getPlayMedium"), @UpnpOutputArgument(name = "RecordMedium", stateVariable = "RecordStorageMedium", getterName = "getRecordMedium"), @UpnpOutputArgument(name = "WriteStatus", stateVariable = "RecordMediumWriteStatus", getterName = "getWriteStatus") })
    public abstract MediaInfo getMediaInfo(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "CurrentTransportState", stateVariable = "TransportState", getterName = "getCurrentTransportState"), @UpnpOutputArgument(name = "CurrentTransportStatus", stateVariable = "TransportStatus", getterName = "getCurrentTransportStatus"), @UpnpOutputArgument(name = "CurrentSpeed", stateVariable = "TransportPlaySpeed", getterName = "getCurrentSpeed") })
    public abstract TransportInfo getTransportInfo(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "Track", stateVariable = "CurrentTrack", getterName = "getTrack"), @UpnpOutputArgument(name = "TrackDuration", stateVariable = "CurrentTrackDuration", getterName = "getTrackDuration"), @UpnpOutputArgument(name = "TrackMetaData", stateVariable = "CurrentTrackMetaData", getterName = "getTrackMetaData"), @UpnpOutputArgument(name = "TrackURI", stateVariable = "CurrentTrackURI", getterName = "getTrackURI"), @UpnpOutputArgument(name = "RelTime", stateVariable = "RelativeTimePosition", getterName = "getRelTime"), @UpnpOutputArgument(name = "AbsTime", stateVariable = "AbsoluteTimePosition", getterName = "getAbsTime"), @UpnpOutputArgument(name = "RelCount", stateVariable = "RelativeCounterPosition", getterName = "getRelCount"), @UpnpOutputArgument(name = "AbsCount", stateVariable = "AbsoluteCounterPosition", getterName = "getAbsCount") })
    public abstract PositionInfo getPositionInfo(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "PlayMedia", stateVariable = "PossiblePlaybackStorageMedia", getterName = "getPlayMediaString"), @UpnpOutputArgument(name = "RecMedia", stateVariable = "PossibleRecordStorageMedia", getterName = "getRecMediaString"), @UpnpOutputArgument(name = "RecQualityModes", stateVariable = "PossibleRecordQualityModes", getterName = "getRecQualityModesString") })
    public abstract DeviceCapabilities getDeviceCapabilities(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "PlayMode", stateVariable = "CurrentPlayMode", getterName = "getPlayMode"), @UpnpOutputArgument(name = "RecQualityMode", stateVariable = "CurrentRecordQualityMode", getterName = "getRecQualityMode") })
    public abstract TransportSettings getTransportSettings(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction
    public abstract void stop(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction
    public abstract void play(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed") final String p1) throws AVTransportException;
    
    @UpnpAction
    public abstract void pause(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction
    public abstract void record(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction
    public abstract void seek(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode") final String p1, @UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget") final String p2) throws AVTransportException;
    
    @UpnpAction
    public abstract void next(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction
    public abstract void previous(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0) throws AVTransportException;
    
    @UpnpAction
    public abstract void setPlayMode(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode") final String p1) throws AVTransportException;
    
    @UpnpAction
    public abstract void setRecordQualityMode(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode") final String p1) throws AVTransportException;
    
    @UpnpAction(name = "GetCurrentTransportActions", out = { @UpnpOutputArgument(name = "Actions", stateVariable = "CurrentTransportActions") })
    public String getCurrentTransportActionsString(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            return ModelUtil.toCommaSeparatedList(this.getCurrentTransportActions(instanceId));
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    protected abstract TransportAction[] getCurrentTransportActions(final UnsignedIntegerFourBytes p0) throws Exception;
}
