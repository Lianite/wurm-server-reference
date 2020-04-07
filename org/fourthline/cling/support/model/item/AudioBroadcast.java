// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.DIDLObject;

public class AudioBroadcast extends AudioItem
{
    public static final Class CLASS;
    
    public AudioBroadcast() {
        this.setClazz(AudioBroadcast.CLASS);
    }
    
    public AudioBroadcast(final Item other) {
        super(other);
    }
    
    public AudioBroadcast(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(AudioBroadcast.CLASS);
    }
    
    public String getRegion() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.REGION.class);
    }
    
    public AudioBroadcast setRegion(final String region) {
        this.replaceFirstProperty(new Property.UPNP.REGION(region));
        return this;
    }
    
    public String getRadioCallSign() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.RADIO_CALL_SIGN.class);
    }
    
    public AudioBroadcast setRadioCallSign(final String radioCallSign) {
        this.replaceFirstProperty(new Property.UPNP.RADIO_CALL_SIGN(radioCallSign));
        return this;
    }
    
    public String getRadioStationID() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.RADIO_STATION_ID.class);
    }
    
    public AudioBroadcast setRadioStationID(final String radioStationID) {
        this.replaceFirstProperty(new Property.UPNP.RADIO_STATION_ID(radioStationID));
        return this;
    }
    
    public String getRadioBand() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.RADIO_BAND.class);
    }
    
    public AudioBroadcast setRadioBand(final String radioBand) {
        this.replaceFirstProperty(new Property.UPNP.RADIO_BAND(radioBand));
        return this;
    }
    
    public Integer getChannelNr() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Integer>>)Property.UPNP.CHANNEL_NR.class);
    }
    
    public AudioBroadcast setChannelNr(final Integer channelNr) {
        this.replaceFirstProperty(new Property.UPNP.CHANNEL_NR(channelNr));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.audioItem.audioBroadcast");
    }
}
