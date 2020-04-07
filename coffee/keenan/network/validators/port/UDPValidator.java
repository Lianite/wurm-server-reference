// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.port;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import coffee.keenan.network.config.IConfiguration;
import java.net.InetAddress;

public class UDPValidator implements IPortValidator
{
    private Exception exception;
    
    @Override
    public boolean validate(final InetAddress address, final IConfiguration configuration, final int port) {
        try (final DatagramChannel datagram = DatagramChannel.open()) {
            datagram.socket().setSoTimeout(configuration.getTimeout());
            datagram.bind(new InetSocketAddress(address, port));
        }
        catch (Exception e) {
            this.exception = e;
            return false;
        }
        return true;
    }
    
    @Override
    public Exception getException() {
        return this.exception;
    }
}
