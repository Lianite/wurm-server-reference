// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.communication;

import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.logging.Level;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Random;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class SocketConnection
{
    private static final Logger logger;
    private static final String CLASS_NAME;
    public static final int BUFFER_SIZE = 262136;
    private final ByteBuffer writeBufferTmp;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer_w;
    private ByteBuffer writeBuffer_r;
    public static final long timeOutTime = 300000L;
    public static final long disconTime = 5000L;
    private boolean connected;
    private boolean playerServerConnection;
    private SocketChannel socketChannel;
    private long lastRead;
    private SimpleConnectionListener connectionListener;
    private int toRead;
    private volatile boolean writing;
    private int bytesRead;
    private int totalBytesWritten;
    private int maxBlocksPerIteration;
    private boolean isLoggedIn;
    public int ticksToDisconnect;
    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    public Random encryptRandom;
    private int remainingEncryptBytes;
    private int encryptByte;
    private int encryptAddByte;
    public Random decryptRandom;
    private int remainingDencryptBytes;
    private int dencryptByte;
    private int decryptAddByte;
    private static final ReentrantReadWriteLock RW_LOCK;
    private boolean callTickWritingFromTick;
    static long maxRead;
    static int maxTotalRead;
    static int maxTotalReadAllowed;
    static int maxReadAllowed;
    
    SocketConnection(final SocketChannel socketChannel, final boolean enableNagles, final boolean intraServer) throws IOException {
        this.writeBufferTmp = ByteBuffer.allocate(65534);
        this.readBuffer = ByteBuffer.allocate(262136);
        this.writeBuffer_w = null;
        this.writeBuffer_r = null;
        this.playerServerConnection = false;
        this.lastRead = System.currentTimeMillis();
        this.toRead = -1;
        this.maxBlocksPerIteration = 3;
        this.isLoggedIn = true;
        this.ticksToDisconnect = -1;
        this.encryptRandom = new Random(105773331L);
        this.remainingEncryptBytes = 0;
        this.encryptByte = 0;
        this.encryptAddByte = 0;
        this.decryptRandom = new Random(105773331L);
        this.remainingDencryptBytes = 0;
        this.dencryptByte = 0;
        this.decryptAddByte = 0;
        this.callTickWritingFromTick = true;
        (this.socketChannel = socketChannel).configureBlocking(false);
        this.socket = socketChannel.socket();
        this.playerServerConnection = !intraServer;
        if (this.playerServerConnection) {
            this.readBuffer = ByteBuffer.allocate(262136);
            this.writeBuffer_w = ByteBuffer.allocate(32767);
            this.writeBuffer_r = ByteBuffer.allocate(32767);
        }
        else {
            this.readBuffer = ByteBuffer.allocate(262136);
            this.writeBuffer_w = ByteBuffer.allocate(262136);
            this.writeBuffer_r = ByteBuffer.allocate(262136);
        }
        if (!enableNagles) {
            System.out.println("Disabling Nagles");
            socketChannel.socket().setTcpNoDelay(true);
        }
        if (SocketConnection.logger.isLoggable(Level.FINE)) {
            SocketConnection.logger.fine("SocketChannel validOps: " + socketChannel.validOps() + ", isConnected: " + socketChannel.isConnected() + ", isOpen: " + socketChannel.isOpen() + ", isRegistered: " + socketChannel.isRegistered() + ", socket: " + socketChannel.socket());
        }
        this.connected = true;
        this.readBuffer.clear();
        this.readBuffer.limit(2);
        this.writing = false;
        this.writeBuffer_w.clear();
        this.writeBuffer_r.flip();
        this.isLoggedIn = false;
    }
    
    protected SocketConnection(final String ip, final int port, final boolean enableNagles) throws UnknownHostException, IOException {
        this.writeBufferTmp = ByteBuffer.allocate(65534);
        this.readBuffer = ByteBuffer.allocate(262136);
        this.writeBuffer_w = null;
        this.writeBuffer_r = null;
        this.playerServerConnection = false;
        this.lastRead = System.currentTimeMillis();
        this.toRead = -1;
        this.maxBlocksPerIteration = 3;
        this.isLoggedIn = true;
        this.ticksToDisconnect = -1;
        this.encryptRandom = new Random(105773331L);
        this.remainingEncryptBytes = 0;
        this.encryptByte = 0;
        this.encryptAddByte = 0;
        this.decryptRandom = new Random(105773331L);
        this.remainingDencryptBytes = 0;
        this.dencryptByte = 0;
        this.decryptAddByte = 0;
        this.callTickWritingFromTick = true;
        this.readBuffer = ByteBuffer.allocate(262136);
        this.writeBuffer_w = ByteBuffer.allocate(262136);
        this.writeBuffer_r = ByteBuffer.allocate(262136);
        if (SocketConnection.logger.isLoggable(Level.FINER)) {
            SocketConnection.logger.entering(SocketConnection.CLASS_NAME, "SocketConnection", new Object[] { ip, port });
        }
        (this.socketChannel = SocketChannel.open()).connect(new InetSocketAddress(ip, port));
        if (!enableNagles) {
            System.out.println("Disabling Nagles");
            this.socketChannel.socket().setTcpNoDelay(true);
        }
        if (SocketConnection.logger.isLoggable(Level.FINE)) {
            SocketConnection.logger.fine("SocketChannel validOps: " + this.socketChannel.validOps() + ", isConnected: " + this.socketChannel.isConnected() + ", isOpen: " + this.socketChannel.isOpen() + ", isRegistered: " + this.socketChannel.isRegistered() + ", socket: " + this.socketChannel.socket());
        }
        this.socketChannel.configureBlocking(false);
        this.connected = true;
        this.readBuffer.clear();
        this.readBuffer.limit(2);
        this.writing = false;
        this.writeBuffer_w.clear();
        this.writeBuffer_r.flip();
    }
    
    public SocketConnection(final String ip, final int port, final int timeout) throws UnknownHostException, IOException {
        this(ip, port, timeout, true);
    }
    
    SocketConnection(final String ip, final int port, final int timeout, final boolean enableNagles) throws UnknownHostException, IOException {
        this.writeBufferTmp = ByteBuffer.allocate(65534);
        this.readBuffer = ByteBuffer.allocate(262136);
        this.writeBuffer_w = null;
        this.writeBuffer_r = null;
        this.playerServerConnection = false;
        this.lastRead = System.currentTimeMillis();
        this.toRead = -1;
        this.maxBlocksPerIteration = 3;
        this.isLoggedIn = true;
        this.ticksToDisconnect = -1;
        this.encryptRandom = new Random(105773331L);
        this.remainingEncryptBytes = 0;
        this.encryptByte = 0;
        this.encryptAddByte = 0;
        this.decryptRandom = new Random(105773331L);
        this.remainingDencryptBytes = 0;
        this.dencryptByte = 0;
        this.decryptAddByte = 0;
        this.callTickWritingFromTick = true;
        this.readBuffer = ByteBuffer.allocate(262136);
        this.writeBuffer_w = ByteBuffer.allocate(262136);
        this.writeBuffer_r = ByteBuffer.allocate(262136);
        this.socketChannel = SocketChannel.open();
        this.socketChannel.socket().setSoTimeout(timeout);
        this.socketChannel.connect(new InetSocketAddress(ip, port));
        if (!enableNagles) {
            System.out.println("Disabling Nagles");
            this.socketChannel.socket().setTcpNoDelay(true);
        }
        if (SocketConnection.logger.isLoggable(Level.FINE)) {
            SocketConnection.logger.fine("SocketChannel validOps: " + this.socketChannel.validOps() + ", isConnected: " + this.socketChannel.isConnected() + ", isOpen: " + this.socketChannel.isOpen() + ", isRegistered: " + this.socketChannel.isRegistered() + ", socket: " + this.socketChannel.socket());
        }
        this.socketChannel.configureBlocking(false);
        this.connected = true;
        this.readBuffer.clear();
        this.readBuffer.limit(2);
        this.writing = false;
        this.writeBuffer_w.clear();
        this.writeBuffer_r.flip();
    }
    
    public void setMaxBlocksPerIteration(final int aMaxBlocksPerIteration) {
        this.maxBlocksPerIteration = aMaxBlocksPerIteration;
    }
    
    public String getIp() {
        return this.socket.getInetAddress().toString();
    }
    
    public ByteBuffer getBuffer() {
        if (this.writing) {
            throw new IllegalStateException("getBuffer() called twice in a row. You probably forgot to flush()");
        }
        this.writing = true;
        this.writeBufferTmp.clear();
        return this.writeBufferTmp;
    }
    
    public void clearBuffer() {
        if (this.writing) {
            this.writing = false;
            this.writeBufferTmp.clear();
        }
    }
    
    public int getUnflushed() {
        return this.writeBuffer_w.position() + this.writeBuffer_r.remaining();
    }
    
    public void flush() throws IOException {
        if (!this.writing) {
            throw new IllegalStateException("flush() called twice in a row.");
        }
        this.writing = false;
        this.writeBufferTmp.flip();
        final int bytesWritten = this.writeBufferTmp.limit();
        this.totalBytesWritten += bytesWritten;
        if (bytesWritten > 65524) {
            SocketConnection.logger.log(Level.WARNING, "WARNING Written " + bytesWritten, new Exception());
        }
        if (this.writeBuffer_w.remaining() < bytesWritten + 2) {
            if (!this.tickWriting(0L)) {
                throw new IOException("BufferOverflow: Tried to write " + (bytesWritten + 2) + " bytes, but only " + this.writeBuffer_w.remaining() + " bytes remained. Written=" + this.totalBytesWritten + ", BufferTmp: " + this.writeBufferTmp + ", Buffer_w: " + this.writeBuffer_w + ", Buffer_r: " + this.writeBuffer_r);
            }
            SocketConnection.logger.log(Level.INFO, "Possibly saved client crash by forcing a write of the writeBuffer_w.");
        }
        if (SocketConnection.logger.isLoggable(Level.FINEST) && (bytesWritten > 1 || SocketConnection.logger.isLoggable(Level.FINEST))) {
            SocketConnection.logger.finer("Number of bytes in the write buffer: " + bytesWritten);
        }
        final int startPos = this.writeBuffer_w.position();
        this.writeBuffer_w.putShort((short)bytesWritten);
        this.writeBuffer_w.put(this.writeBufferTmp);
        final int endPos = this.writeBuffer_w.position();
        this.encrypt(this.writeBuffer_w, startPos, endPos);
    }
    
    public void setConnectionListener(final SimpleConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
    
    public boolean isConnected() {
        if (this.playerServerConnection) {
            if (this.isLoggedIn) {
                if (this.lastRead < System.currentTimeMillis() - 300000L) {
                    return false;
                }
            }
            else if (this.lastRead < System.currentTimeMillis() - 5000L) {
                return false;
            }
        }
        return this.connected;
    }
    
    public void setLogin(final boolean li) {
        if (!this.isLoggedIn && li && this.playerServerConnection) {
            this.writeBuffer_w = ByteBuffer.allocate(786408);
            this.writeBuffer_r = ByteBuffer.allocate(786408);
            this.writeBuffer_w.clear();
            this.writeBuffer_r.flip();
        }
        this.isLoggedIn = li;
    }
    
    public void disconnect() {
        if (SocketConnection.logger.isLoggable(Level.FINER)) {
            SocketConnection.logger.entering(SocketConnection.CLASS_NAME, "disconnect");
        }
        this.connected = false;
        try {
            if (this.in != null) {
                this.in.close();
            }
            this.in = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.out != null) {
                this.out.close();
            }
            this.out = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            this.socket = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.readBuffer.clear();
        this.writeBuffer_w.clear();
        this.writeBuffer_r.clear();
        this.isLoggedIn = false;
    }
    
    public void sendShutdown() {
        if (SocketConnection.logger.isLoggable(Level.FINER)) {
            SocketConnection.logger.entering(SocketConnection.CLASS_NAME, "sendShutdown");
        }
        if (this.socketChannel != null) {
            try {
                this.socketChannel.socket().shutdownOutput();
            }
            catch (Exception ex) {}
        }
        if (this.socketChannel != null) {
            try {
                this.socketChannel.socket().shutdownInput();
            }
            catch (Exception ex2) {}
        }
    }
    
    public void closeChannel() {
        if (SocketConnection.logger.isLoggable(Level.FINER)) {
            SocketConnection.logger.entering(SocketConnection.CLASS_NAME, "closeChannel");
        }
        if (this.socketChannel != null && this.socketChannel.socket() != null) {
            try {
                this.socketChannel.socket().close();
            }
            catch (IOException iox) {
                iox.printStackTrace();
            }
        }
        if (this.socketChannel != null) {
            try {
                this.socketChannel.close();
            }
            catch (IOException iox) {
                iox.printStackTrace();
            }
        }
    }
    
    public void tick() throws IOException {
        if (this.callTickWritingFromTick) {
            this.tickWriting(0L);
        }
        if (this.ticksToDisconnect >= 0 && --this.ticksToDisconnect <= 0) {
            throw new IOException("Disconnecting by timeout.");
        }
        final int preRead = this.bytesRead;
        int totalRead = 0;
        final long maxNanosPerIteration = 3000000000L;
        final long startTime = System.nanoTime();
        int readBlocks = 0;
        while (readBlocks < this.maxBlocksPerIteration && System.nanoTime() - startTime < 3000000000L && (totalRead = this.socketChannel.read(this.readBuffer)) > 0) {
            if (this.playerServerConnection) {
                if (totalRead > SocketConnection.maxTotalRead) {
                    SocketConnection.maxTotalRead = totalRead;
                }
                if (totalRead > SocketConnection.maxTotalReadAllowed) {
                    throw new IOException(this.getIp() + " disconnected in SocketConnection. Maxtotalread not allowed: " + totalRead);
                }
            }
            this.lastRead = System.currentTimeMillis();
            if (this.toRead < 0) {
                if (this.readBuffer.position() != 2) {
                    continue;
                }
                this.bytesRead += this.readBuffer.position();
                this.readBuffer.flip();
                this.decrypt(this.readBuffer);
                this.toRead = (this.readBuffer.getShort() & 0xFFFF);
                this.readBuffer.clear();
                this.readBuffer.limit(this.toRead);
                if (!this.playerServerConnection) {
                    continue;
                }
                if (this.toRead > SocketConnection.maxRead) {
                    SocketConnection.maxRead = (this.toRead & 0xFFFF);
                }
                if (this.toRead > SocketConnection.maxReadAllowed) {
                    throw new IOException(this.getIp() + " disconnected in SocketConnection. Maxread not allowed: " + this.toRead);
                }
                continue;
            }
            else {
                if (this.readBuffer.position() != this.toRead) {
                    continue;
                }
                this.bytesRead += this.readBuffer.position();
                ++readBlocks;
                this.readBuffer.flip();
                this.decrypt(this.readBuffer);
                this.connectionListener.reallyHandle(0, this.readBuffer);
                this.readBuffer.clear();
                this.readBuffer.limit(2);
                if (this.playerServerConnection && this.toRead > SocketConnection.maxReadAllowed) {
                    throw new IOException(this.getIp() + " disconnected in SocketConnection. Maxread not allowed: " + this.toRead);
                }
                this.toRead = -1;
            }
        }
    }
    
    public boolean tickWriting(final long aNanosToWaitForLock) throws IOException {
        try {
            if ((aNanosToWaitForLock <= 0L && SocketConnection.RW_LOCK.writeLock().tryLock()) || (aNanosToWaitForLock > 0L && SocketConnection.RW_LOCK.writeLock().tryLock(aNanosToWaitForLock, TimeUnit.NANOSECONDS))) {
                if (this.socketChannel == null || !this.socketChannel.isConnected()) {
                    if (SocketConnection.logger.isLoggable(Level.FINE)) {
                        SocketConnection.logger.fine("Cannot write message to socketChannel: " + this.socketChannel);
                    }
                    return false;
                }
                try {
                    if (this.writing) {
                        throw new IllegalStateException("update called between a getBuffer() and a flush(). Don't do that.");
                    }
                    if (this.getUnflushed() > 1048576) {
                        throw new IOException("Buffer overflow (1 mb unsent)");
                    }
                    final int preWrite = this.writeBuffer_r.remaining();
                    this.socketChannel.write(this.writeBuffer_r);
                    if (this.writeBuffer_r.remaining() == 0) {
                        final ByteBuffer tmp = this.writeBuffer_w;
                        this.writeBuffer_w = this.writeBuffer_r;
                        this.writeBuffer_r = tmp;
                        this.writeBuffer_w.clear();
                        this.writeBuffer_r.flip();
                    }
                    if (SocketConnection.logger.isLoggable(Level.FINER)) {
                        final int lBytesWritten = preWrite - this.writeBuffer_r.remaining();
                        if (lBytesWritten > 0) {
                            SocketConnection.logger.finer("Number of bytes wriiten to the socketChannel: " + lBytesWritten + ", channel: " + this.socketChannel);
                        }
                    }
                    return true;
                }
                catch (IOException e) {
                    if (SocketConnection.logger.isLoggable(Level.FINE)) {
                        SocketConnection.logger.log(Level.FINE, "IOException while writing to channel: " + this.socketChannel + ", only " + this.writeBuffer_w.remaining() + " bytes remained. Written=" + this.totalBytesWritten + ", BufferTmp: " + this.writeBufferTmp + ", Buffer_w: " + this.writeBuffer_w + ", Buffer_r: " + this.writeBuffer_r, e);
                    }
                    throw e;
                }
                finally {
                    SocketConnection.RW_LOCK.writeLock().unlock();
                }
            }
            if (SocketConnection.logger.isLoggable(Level.FINEST)) {}
            return false;
        }
        catch (InterruptedException e2) {
            SocketConnection.logger.log(Level.WARNING, "Lock was interrupted", e2);
            return false;
        }
    }
    
    public void changeProtocol(final long newSeed) {
    }
    
    private void encrypt(final ByteBuffer bb, final int start, final int end) {
        final byte[] bytes = bb.array();
        for (int i = start; i < end; ++i) {
            if (--this.remainingEncryptBytes < 0) {
                this.remainingEncryptBytes = this.encryptRandom.nextInt(100) + 1;
                this.encryptByte = (byte)this.encryptRandom.nextInt(254);
                this.encryptAddByte = (byte)this.encryptRandom.nextInt(254);
            }
            bytes[i] -= (byte)this.encryptAddByte;
            final byte[] array = bytes;
            final int n = i;
            array[n] ^= (byte)this.encryptByte;
        }
    }
    
    private void decrypt(final ByteBuffer bb) {
        final byte[] bytes = bb.array();
        final int start = bb.position();
        for (int end = bb.limit(), i = start; i < end; ++i) {
            if (--this.remainingDencryptBytes < 0) {
                this.remainingDencryptBytes = this.decryptRandom.nextInt(100) + 1;
                this.dencryptByte = (byte)this.decryptRandom.nextInt(254);
                this.decryptAddByte = (byte)this.decryptRandom.nextInt(254);
            }
            final byte[] array = bytes;
            final int n = i;
            array[n] ^= (byte)this.dencryptByte;
            bytes[i] += (byte)this.decryptAddByte;
        }
    }
    
    public void setEncryptSeed(final long seed) {
        this.encryptRandom.setSeed(seed);
        this.remainingEncryptBytes = 0;
    }
    
    public void setDecryptSeed(final long seed) {
        this.decryptRandom.setSeed(seed);
        this.remainingDencryptBytes = 0;
    }
    
    public int getSentBytes() {
        return this.totalBytesWritten;
    }
    
    public int getReadBytes() {
        return this.bytesRead;
    }
    
    public void clearSentBytes() {
        this.totalBytesWritten = 0;
    }
    
    public void clearReadBytes() {
        this.bytesRead = 0;
    }
    
    public boolean isCallTickWritingFromTick() {
        return this.callTickWritingFromTick;
    }
    
    public void setCallTickWritingFromTick(final boolean newCallTickWritingFromTick) {
        this.callTickWritingFromTick = newCallTickWritingFromTick;
    }
    
    public boolean isWriting() {
        return this.writing;
    }
    
    @Override
    public String toString() {
        return "SocketConnection [IrcChannel: " + this.socketChannel + ']';
    }
    
    static {
        logger = Logger.getLogger(SocketConnection.class.getName());
        CLASS_NAME = SocketConnection.class.getName();
        RW_LOCK = new ReentrantReadWriteLock();
        SocketConnection.maxRead = 0L;
        SocketConnection.maxTotalRead = 0;
        SocketConnection.maxTotalReadAllowed = 20000;
        SocketConnection.maxReadAllowed = 20000;
    }
}
