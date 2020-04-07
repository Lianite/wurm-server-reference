// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class PortMapping
{
    private boolean enabled;
    private UnsignedIntegerFourBytes leaseDurationSeconds;
    private String remoteHost;
    private UnsignedIntegerTwoBytes externalPort;
    private UnsignedIntegerTwoBytes internalPort;
    private String internalClient;
    private Protocol protocol;
    private String description;
    
    public PortMapping() {
    }
    
    public PortMapping(final Map<String, ActionArgumentValue<Service>> map) {
        this((boolean)map.get("NewEnabled").getValue(), (UnsignedIntegerFourBytes)map.get("NewLeaseDuration").getValue(), (String)map.get("NewRemoteHost").getValue(), (UnsignedIntegerTwoBytes)map.get("NewExternalPort").getValue(), (UnsignedIntegerTwoBytes)map.get("NewInternalPort").getValue(), (String)map.get("NewInternalClient").getValue(), Protocol.valueOf(map.get("NewProtocol").toString()), (String)map.get("NewPortMappingDescription").getValue());
    }
    
    public PortMapping(final int port, final String internalClient, final Protocol protocol) {
        this(true, new UnsignedIntegerFourBytes(0L), null, new UnsignedIntegerTwoBytes(port), new UnsignedIntegerTwoBytes(port), internalClient, protocol, null);
    }
    
    public PortMapping(final int port, final String internalClient, final Protocol protocol, final String description) {
        this(true, new UnsignedIntegerFourBytes(0L), null, new UnsignedIntegerTwoBytes(port), new UnsignedIntegerTwoBytes(port), internalClient, protocol, description);
    }
    
    public PortMapping(final String remoteHost, final UnsignedIntegerTwoBytes externalPort, final Protocol protocol) {
        this(true, new UnsignedIntegerFourBytes(0L), remoteHost, externalPort, null, null, protocol, null);
    }
    
    public PortMapping(final boolean enabled, final UnsignedIntegerFourBytes leaseDurationSeconds, final String remoteHost, final UnsignedIntegerTwoBytes externalPort, final UnsignedIntegerTwoBytes internalPort, final String internalClient, final Protocol protocol, final String description) {
        this.enabled = enabled;
        this.leaseDurationSeconds = leaseDurationSeconds;
        this.remoteHost = remoteHost;
        this.externalPort = externalPort;
        this.internalPort = internalPort;
        this.internalClient = internalClient;
        this.protocol = protocol;
        this.description = description;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public UnsignedIntegerFourBytes getLeaseDurationSeconds() {
        return this.leaseDurationSeconds;
    }
    
    public void setLeaseDurationSeconds(final UnsignedIntegerFourBytes leaseDurationSeconds) {
        this.leaseDurationSeconds = leaseDurationSeconds;
    }
    
    public boolean hasRemoteHost() {
        return this.remoteHost != null && this.remoteHost.length() > 0;
    }
    
    public String getRemoteHost() {
        return (this.remoteHost == null) ? "-" : this.remoteHost;
    }
    
    public void setRemoteHost(final String remoteHost) {
        this.remoteHost = ((remoteHost == null || remoteHost.equals("-") || remoteHost.length() == 0) ? null : remoteHost);
    }
    
    public UnsignedIntegerTwoBytes getExternalPort() {
        return this.externalPort;
    }
    
    public void setExternalPort(final UnsignedIntegerTwoBytes externalPort) {
        this.externalPort = externalPort;
    }
    
    public UnsignedIntegerTwoBytes getInternalPort() {
        return this.internalPort;
    }
    
    public void setInternalPort(final UnsignedIntegerTwoBytes internalPort) {
        this.internalPort = internalPort;
    }
    
    public String getInternalClient() {
        return this.internalClient;
    }
    
    public void setInternalClient(final String internalClient) {
        this.internalClient = internalClient;
    }
    
    public Protocol getProtocol() {
        return this.protocol;
    }
    
    public void setProtocol(final Protocol protocol) {
        this.protocol = protocol;
    }
    
    public boolean hasDescription() {
        return this.description != null;
    }
    
    public String getDescription() {
        return (this.description == null) ? "-" : this.description;
    }
    
    public void setDescription(final String description) {
        this.description = ((description == null || description.equals("-") || description.length() == 0) ? null : description);
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") Protocol: " + this.getProtocol() + ", " + this.getExternalPort() + " => " + this.getInternalClient();
    }
    
    public enum Protocol
    {
        UDP, 
        TCP;
    }
}
