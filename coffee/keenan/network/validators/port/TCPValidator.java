// 
// Decompiled by Procyon v0.5.30
// 

package coffee.keenan.network.validators.port;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import coffee.keenan.network.config.IConfiguration;
import java.net.InetAddress;

public class TCPValidator implements IPortValidator
{
    private Exception exception;
    
    @Override
    public boolean validate(final InetAddress address, final IConfiguration configuration, final int port) {
        try (final SocketChannel socket = SocketChannel.open()) {
            socket.socket().setSoTimeout(configuration.getTimeout());
            socket.bind(new InetSocketAddress(address, port));
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
