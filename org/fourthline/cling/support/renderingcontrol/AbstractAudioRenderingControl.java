// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.VolumeDBRange;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolumeDB;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelLoudness;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.lastchange.LastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import java.beans.PropertyChangeSupport;
import org.fourthline.cling.support.model.PresetName;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.binding.annotations.UpnpStateVariable;
import org.fourthline.cling.binding.annotations.UpnpStateVariables;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.binding.annotations.UpnpServiceType;
import org.fourthline.cling.binding.annotations.UpnpServiceId;
import org.fourthline.cling.binding.annotations.UpnpService;
import org.fourthline.cling.support.lastchange.LastChangeDelegator;

@UpnpService(serviceId = @UpnpServiceId("RenderingControl"), serviceType = @UpnpServiceType(value = "RenderingControl", version = 1), stringConvertibleTypes = { LastChange.class })
@UpnpStateVariables({ @UpnpStateVariable(name = "PresetNameList", sendEvents = false, datatype = "string"), @UpnpStateVariable(name = "Mute", sendEvents = false, datatype = "boolean"), @UpnpStateVariable(name = "Volume", sendEvents = false, datatype = "ui2", allowedValueMinimum = 0L, allowedValueMaximum = 100L), @UpnpStateVariable(name = "VolumeDB", sendEvents = false, datatype = "i2", allowedValueMinimum = -36864L, allowedValueMaximum = 32767L), @UpnpStateVariable(name = "Loudness", sendEvents = false, datatype = "boolean"), @UpnpStateVariable(name = "A_ARG_TYPE_Channel", sendEvents = false, allowedValuesEnum = Channel.class), @UpnpStateVariable(name = "A_ARG_TYPE_PresetName", sendEvents = false, allowedValuesEnum = PresetName.class), @UpnpStateVariable(name = "A_ARG_TYPE_InstanceID", sendEvents = false, datatype = "ui4") })
public abstract class AbstractAudioRenderingControl implements LastChangeDelegator
{
    @UpnpStateVariable(eventMaximumRateMilliseconds = 200)
    private final LastChange lastChange;
    protected final PropertyChangeSupport propertyChangeSupport;
    
    protected AbstractAudioRenderingControl() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = new LastChange(new RenderingControlLastChangeParser());
    }
    
    protected AbstractAudioRenderingControl(final LastChange lastChange) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = lastChange;
    }
    
    protected AbstractAudioRenderingControl(final PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = new LastChange(new RenderingControlLastChangeParser());
    }
    
    protected AbstractAudioRenderingControl(final PropertyChangeSupport propertyChangeSupport, final LastChange lastChange) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = lastChange;
    }
    
    @Override
    public LastChange getLastChange() {
        return this.lastChange;
    }
    
    @Override
    public void appendCurrentState(final LastChange lc, final UnsignedIntegerFourBytes instanceId) throws Exception {
        for (final Channel channel : this.getCurrentChannels()) {
            final String channelString = channel.name();
            lc.setEventedValue(instanceId, new RenderingControlVariable.Mute(new ChannelMute(channel, this.getMute(instanceId, channelString))), new RenderingControlVariable.Loudness(new ChannelLoudness(channel, this.getLoudness(instanceId, channelString))), new RenderingControlVariable.Volume(new ChannelVolume(channel, (int)(Object)this.getVolume(instanceId, channelString).getValue())), new RenderingControlVariable.VolumeDB(new ChannelVolumeDB(channel, this.getVolumeDB(instanceId, channelString))), new RenderingControlVariable.PresetNameList(PresetName.FactoryDefaults.name()));
        }
    }
    
    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }
    
    public static UnsignedIntegerFourBytes getDefaultInstanceID() {
        return new UnsignedIntegerFourBytes(0L);
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "CurrentPresetNameList", stateVariable = "PresetNameList") })
    public String listPresets(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId) throws RenderingControlException {
        return PresetName.FactoryDefaults.toString();
    }
    
    @UpnpAction
    public void selectPreset(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "PresetName") final String presetName) throws RenderingControlException {
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "CurrentMute", stateVariable = "Mute") })
    public abstract boolean getMute(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "Channel") final String p1) throws RenderingControlException;
    
    @UpnpAction
    public abstract void setMute(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "Channel") final String p1, @UpnpInputArgument(name = "DesiredMute", stateVariable = "Mute") final boolean p2) throws RenderingControlException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "CurrentVolume", stateVariable = "Volume") })
    public abstract UnsignedIntegerTwoBytes getVolume(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "Channel") final String p1) throws RenderingControlException;
    
    @UpnpAction
    public abstract void setVolume(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes p0, @UpnpInputArgument(name = "Channel") final String p1, @UpnpInputArgument(name = "DesiredVolume", stateVariable = "Volume") final UnsignedIntegerTwoBytes p2) throws RenderingControlException;
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "CurrentVolume", stateVariable = "VolumeDB") })
    public Integer getVolumeDB(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") final String channelName) throws RenderingControlException {
        return 0;
    }
    
    @UpnpAction
    public void setVolumeDB(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") final String channelName, @UpnpInputArgument(name = "DesiredVolume", stateVariable = "VolumeDB") final Integer desiredVolumeDB) throws RenderingControlException {
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "MinValue", stateVariable = "VolumeDB", getterName = "getMinValue"), @UpnpOutputArgument(name = "MaxValue", stateVariable = "VolumeDB", getterName = "getMaxValue") })
    public VolumeDBRange getVolumeDBRange(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") final String channelName) throws RenderingControlException {
        return new VolumeDBRange(0, 0);
    }
    
    @UpnpAction(out = { @UpnpOutputArgument(name = "CurrentLoudness", stateVariable = "Loudness") })
    public boolean getLoudness(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") final String channelName) throws RenderingControlException {
        return false;
    }
    
    @UpnpAction
    public void setLoudness(@UpnpInputArgument(name = "InstanceID") final UnsignedIntegerFourBytes instanceId, @UpnpInputArgument(name = "Channel") final String channelName, @UpnpInputArgument(name = "DesiredLoudness", stateVariable = "Loudness") final boolean desiredLoudness) throws RenderingControlException {
    }
    
    protected abstract Channel[] getCurrentChannels();
    
    protected Channel getChannel(final String channelName) throws RenderingControlException {
        try {
            return Channel.valueOf(channelName);
        }
        catch (IllegalArgumentException ex) {
            throw new RenderingControlException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported audio channel: " + channelName);
        }
    }
}
