// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.address;

import coffee.keenan.network.config.IConfiguration;
import java.net.InetAddress;

public interface IAddressValidator
{
    boolean validate(final InetAddress p0, final IConfiguration p1);
    
    Exception getException();
}
