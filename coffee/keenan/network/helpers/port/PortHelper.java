// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.helpers.port;

import java.util.Collection;
import java.util.Arrays;
import coffee.keenan.network.validators.port.IPortValidator;
import java.net.InetAddress;
import java.util.Iterator;
import coffee.keenan.network.wrappers.upnp.UPNPService;
import org.jetbrains.annotations.NotNull;
import coffee.keenan.network.config.DefaultConfiguration;
import coffee.keenan.network.config.IConfiguration;
import coffee.keenan.network.helpers.ErrorTracking;

public class PortHelper extends ErrorTracking
{
    private final IConfiguration configuration;
    
    public PortHelper() {
        this.configuration = new DefaultConfiguration();
    }
    
    public PortHelper(final IConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public static Port assignPort(@NotNull final Port port) {
        return assignPort(port, new DefaultConfiguration());
    }
    
    public Port assignFavoredPort(@NotNull final Port port) {
        final Integer p = port.getFavoredPort();
        if (p != null && this.validatePort(port.getAddress(), p, port.getValidators())) {
            port.setAssignedPort(p);
        }
        return port;
    }
    
    public static Port assignPort(@NotNull final Port port, @NotNull final IConfiguration configuration) {
        final PortHelper portHelper = new PortHelper(configuration);
        portHelper.assignFavoredPort(port);
        if (port.getAssignedPort() != 0) {
            return port;
        }
        for (final int p : port.getPorts()) {
            if (portHelper.validatePort(port.getAddress(), p, port.getValidators())) {
                port.setAssignedPort(p);
                break;
            }
        }
        if (port.isToMap() && port.getAssignedPort() != 0) {
            UPNPService.getInstance().openPort(port);
        }
        return port;
    }
    
    public boolean validatePort(final InetAddress address, final int port, final IPortValidator... validators) {
        return this.validatePort(address, port, Arrays.asList(validators));
    }
    
    public boolean validatePort(final InetAddress address, final int port, final Collection<IPortValidator> validators) {
        for (final IPortValidator validator : validators) {
            if (!validator.validate(address, this.configuration, port)) {
                this.addException("address: " + address.toString() + ", port: " + String.valueOf(port), validator.getException());
                return false;
            }
        }
        return true;
    }
}
