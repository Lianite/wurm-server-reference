// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.address;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import coffee.keenan.network.config.IConfiguration;
import java.net.InetAddress;

public class InternetValidator implements IAddressValidator
{
    private Exception exception;
    
    @Override
    public boolean validate(final InetAddress address, final IConfiguration configuration) {
        try (final SocketChannel socket = SocketChannel.open()) {
            socket.socket().setSoTimeout(configuration.getTimeout());
            socket.bind(new InetSocketAddress(address, 0));
            socket.connect(new InetSocketAddress(configuration.getTestUrl(), configuration.getTestPort()));
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
