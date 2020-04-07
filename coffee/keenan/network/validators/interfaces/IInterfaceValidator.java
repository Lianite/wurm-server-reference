// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.interfaces;

import coffee.keenan.network.config.IConfiguration;
import java.net.NetworkInterface;

public interface IInterfaceValidator
{
    boolean validate(final NetworkInterface p0, final IConfiguration p1);
    
    Exception getException();
}
