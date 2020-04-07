// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.helpers.port;

import java.util.Arrays;
import org.fourthline.cling.support.model.PortMapping;
import java.util.Collection;
import java.util.Collections;
import coffee.keenan.network.validators.port.UDPValidator;
import coffee.keenan.network.validators.port.TCPValidator;
import java.util.HashSet;
import java.util.ArrayList;
import coffee.keenan.network.validators.port.IPortValidator;
import java.util.Set;
import java.net.InetAddress;
import java.util.List;
import coffee.keenan.network.helpers.ErrorTracking;

public class Port extends ErrorTracking
{
    private final List<Integer> ports;
    private Integer favortedPort;
    private final Protocol protocols;
    private final InetAddress address;
    private final Set<IPortValidator> validators;
    private String description;
    private boolean toMap;
    private int assignedPort;
    private boolean isMapped;
    
    public Port(final InetAddress address, final Protocol protocol) {
        this.ports = new ArrayList<Integer>();
        this.validators = new HashSet<IPortValidator>();
        this.description = "";
        this.address = address;
        this.protocols = protocol;
        switch (this.getProtocol()) {
            case TCP: {
                this.validators.add(new TCPValidator());
                break;
            }
            case UDP: {
                this.validators.add(new UDPValidator());
                break;
            }
            case Both: {
                this.validators.add(new TCPValidator());
                this.validators.add(new UDPValidator());
                break;
            }
        }
    }
    
    public Port setFavoredPort(final int port) {
        this.favortedPort = port;
        return this;
    }
    
    public Integer getFavoredPort() {
        return this.favortedPort;
    }
    
    public Port addPort(final int port) {
        this.ports.add(port);
        return this;
    }
    
    public Port addPortRange(final int start, final int end) {
        for (int i = start; i <= end; ++i) {
            this.ports.add(i);
        }
        return this;
    }
    
    public Port addPorts(final Integer... ports) {
        Collections.addAll(this.ports, ports);
        return this;
    }
    
    public Port addPorts(final List<Integer> ports) {
        this.ports.addAll(ports);
        return this;
    }
    
    public Port toMap() {
        return this.toMap(true);
    }
    
    public Port toMap(final boolean toMap) {
        this.toMap = toMap;
        return this;
    }
    
    public List<Integer> getPorts() {
        return this.ports;
    }
    
    public Protocol getProtocol() {
        return this.protocols;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public Port setDescription(final String description) {
        this.description = description;
        return this;
    }
    
    public boolean isToMap() {
        return this.toMap;
    }
    
    public boolean isMapped() {
        return this.isMapped;
    }
    
    public Port setMapped(final boolean value) {
        this.isMapped = value;
        return this;
    }
    
    public int getAssignedPort() {
        return this.assignedPort;
    }
    
    public Port setAssignedPort(final int assignedPort) {
        this.assignedPort = assignedPort;
        return this;
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    public PortMapping[] getMappings() {
        if (!this.isToMap()) {
            return new PortMapping[0];
        }
        final List<PortMapping> mappings = new ArrayList<PortMapping>();
        switch (this.getProtocol()) {
            case TCP: {
                mappings.add(new PortMapping(this.getAssignedPort(), this.address.getHostAddress(), PortMapping.Protocol.TCP, this.getDescription()));
                break;
            }
            case UDP: {
                mappings.add(new PortMapping(this.getAssignedPort(), this.address.getHostAddress(), PortMapping.Protocol.UDP, this.getDescription()));
                break;
            }
            case Both: {
                mappings.add(new PortMapping(this.getAssignedPort(), this.address.getHostAddress(), PortMapping.Protocol.UDP, this.getDescription()));
                mappings.add(new PortMapping(this.getAssignedPort(), this.address.getHostAddress(), PortMapping.Protocol.TCP, this.getDescription()));
                break;
            }
        }
        return mappings.toArray(new PortMapping[0]);
    }
    
    public Collection<IPortValidator> getValidators() {
        return this.validators;
    }
    
    public Port addValidators(final IPortValidator... validators) {
        this.validators.addAll(Arrays.asList(validators));
        return this;
    }
    
    @Override
    public String toString() {
        return this.getDescription() + " (" + this.getAddress().getHostAddress() + ":" + this.getAssignedPort() + ")";
    }
}
