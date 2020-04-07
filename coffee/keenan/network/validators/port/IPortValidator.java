// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.port;

import coffee.keenan.network.config.IConfiguration;
import java.net.InetAddress;

public interface IPortValidator
{
    boolean validate(final InetAddress p0, final IConfiguration p1, final int p2);
    
    Exception getException();
}
