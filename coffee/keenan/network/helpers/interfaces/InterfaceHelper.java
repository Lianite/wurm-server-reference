// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.helpers.interfaces;

import java.net.NetworkInterface;
import org.jetbrains.annotations.NotNull;
import coffee.keenan.network.config.DefaultConfiguration;
import coffee.keenan.network.validators.interfaces.UpValidator;
import coffee.keenan.network.validators.interfaces.NotLoopbackValidator;
import coffee.keenan.network.validators.interfaces.IInterfaceValidator;
import coffee.keenan.network.config.IConfiguration;
import coffee.keenan.network.helpers.ErrorTracking;

public class InterfaceHelper extends ErrorTracking
{
    private final IConfiguration configuration;
    private IInterfaceValidator[] interfaceValidators;
    
    public InterfaceHelper() {
        this.interfaceValidators = new IInterfaceValidator[] { new NotLoopbackValidator(), new UpValidator() };
        this.configuration = new DefaultConfiguration();
    }
    
    public InterfaceHelper(final IConfiguration configuration) {
        this.interfaceValidators = new IInterfaceValidator[] { new NotLoopbackValidator(), new UpValidator() };
        this.configuration = configuration;
    }
    
    public IInterfaceValidator[] getInterfaceValidators() {
        return this.interfaceValidators;
    }
    
    public void setInterfaceValidators(@NotNull final IInterfaceValidator... validators) {
        this.interfaceValidators = validators;
    }
    
    public boolean validateInterface(final NetworkInterface networkInterface) {
        if (networkInterface == null) {
            this.addException("null interface", new Exception("given interface was null"));
            return false;
        }
        for (final IInterfaceValidator validator : this.getInterfaceValidators()) {
            if (!validator.validate(networkInterface, this.configuration)) {
                this.addException("interface: " + networkInterface.getDisplayName() + "(" + networkInterface.getName() + ")", validator.getException());
                return false;
            }
        }
        return true;
    }
}
