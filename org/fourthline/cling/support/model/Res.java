// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.seamless.util.MimeType;
import java.net.URI;

public class Res
{
    protected URI importUri;
    protected ProtocolInfo protocolInfo;
    protected Long size;
    protected String duration;
    protected Long bitrate;
    protected Long sampleFrequency;
    protected Long bitsPerSample;
    protected Long nrAudioChannels;
    protected Long colorDepth;
    protected String protection;
    protected String resolution;
    protected String value;
    
    public Res() {
    }
    
    public Res(final String httpGetMimeType, final Long size, final String duration, final Long bitrate, final String value) {
        this(new ProtocolInfo(Protocol.HTTP_GET, "*", httpGetMimeType, "*"), size, duration, bitrate, value);
    }
    
    public Res(final MimeType httpGetMimeType, final Long size, final String duration, final Long bitrate, final String value) {
        this(new ProtocolInfo(httpGetMimeType), size, duration, bitrate, value);
    }
    
    public Res(final MimeType httpGetMimeType, final Long size, final String value) {
        this(new ProtocolInfo(httpGetMimeType), size, value);
    }
    
    public Res(final ProtocolInfo protocolInfo, final Long size, final String value) {
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.value = value;
    }
    
    public Res(final ProtocolInfo protocolInfo, final Long size, final String duration, final Long bitrate, final String value) {
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.duration = duration;
        this.bitrate = bitrate;
        this.value = value;
    }
    
    public Res(final URI importUri, final ProtocolInfo protocolInfo, final Long size, final String duration, final Long bitrate, final Long sampleFrequency, final Long bitsPerSample, final Long nrAudioChannels, final Long colorDepth, final String protection, final String resolution, final String value) {
        this.importUri = importUri;
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.duration = duration;
        this.bitrate = bitrate;
        this.sampleFrequency = sampleFrequency;
        this.bitsPerSample = bitsPerSample;
        this.nrAudioChannels = nrAudioChannels;
        this.colorDepth = colorDepth;
        this.protection = protection;
        this.resolution = resolution;
        this.value = value;
    }
    
    public URI getImportUri() {
        return this.importUri;
    }
    
    public void setImportUri(final URI importUri) {
        this.importUri = importUri;
    }
    
    public ProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }
    
    public void setProtocolInfo(final ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }
    
    public Long getSize() {
        return this.size;
    }
    
    public void setSize(final Long size) {
        this.size = size;
    }
    
    public String getDuration() {
        return this.duration;
    }
    
    public void setDuration(final String duration) {
        this.duration = duration;
    }
    
    public Long getBitrate() {
        return this.bitrate;
    }
    
    public void setBitrate(final Long bitrate) {
        this.bitrate = bitrate;
    }
    
    public Long getSampleFrequency() {
        return this.sampleFrequency;
    }
    
    public void setSampleFrequency(final Long sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }
    
    public Long getBitsPerSample() {
        return this.bitsPerSample;
    }
    
    public void setBitsPerSample(final Long bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }
    
    public Long getNrAudioChannels() {
        return this.nrAudioChannels;
    }
    
    public void setNrAudioChannels(final Long nrAudioChannels) {
        this.nrAudioChannels = nrAudioChannels;
    }
    
    public Long getColorDepth() {
        return this.colorDepth;
    }
    
    public void setColorDepth(final Long colorDepth) {
        this.colorDepth = colorDepth;
    }
    
    public String getProtection() {
        return this.protection;
    }
    
    public void setProtection(final String protection) {
        this.protection = protection;
    }
    
    public String getResolution() {
        return this.resolution;
    }
    
    public void setResolution(final String resolution) {
        this.resolution = resolution;
    }
    
    public void setResolution(final int x, final int y) {
        this.resolution = x + "x" + y;
    }
    
    public int getResolutionX() {
        return (this.getResolution() != null && this.getResolution().split("x").length == 2) ? Integer.valueOf(this.getResolution().split("x")[0]) : 0;
    }
    
    public int getResolutionY() {
        return (this.getResolution() != null && this.getResolution().split("x").length == 2) ? Integer.valueOf(this.getResolution().split("x")[1]) : 0;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
