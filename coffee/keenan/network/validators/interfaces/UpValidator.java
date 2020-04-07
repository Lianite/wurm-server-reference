// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.interfaces;

import coffee.keenan.network.config.IConfiguration;
import java.net.NetworkInterface;

public class UpValidator implements IInterfaceValidator
{
    private Exception exception;
    
    @Override
    public boolean validate(final NetworkInterface networkInterface, final IConfiguration configuration) {
        try {
            return networkInterface.isUp();
        }
        catch (Exception e) {
            this.exception = e;
            return false;
        }
    }
    
    @Override
    public Exception getException() {
        return (this.exception == null) ? new Exception("interface is not up") : this.exception;
    }
}
