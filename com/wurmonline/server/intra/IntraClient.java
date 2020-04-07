// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import com.wurmonline.server.WurmCalendar;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Logger;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.communication.SimpleConnectionListener;

public class IntraClient implements SimpleConnectionListener, MiscConstants
{
    private SocketConnection connection;
    private IntraServerConnectionListener serverConnectionListener;
    private boolean disconnected;
    public boolean loggedIn;
    private String disconnectReason;
    int retryInSeconds;
    long timeDifference;
    private static final int DATABUFSIZE = 16384;
    private static final int TRANSFERSIZE = 16366;
    private static Logger logger;
    public boolean isConnecting;
    public boolean hasFailedConnection;
    protected static final String CHARSET_ENCODING_FOR_COMMS = "UTF-8";
    
    public IntraClient(final String serverIp, final int serverPort, final IntraServerConnectionListener aServerConnectionListener) throws IOException {
        this.disconnected = true;
        this.loggedIn = false;
        this.disconnectReason = "Lost link.";
        this.retryInSeconds = 0;
        this.timeDifference = 0L;
        this.isConnecting = false;
        this.hasFailedConnection = false;
        this.reconnect(serverIp, serverPort, aServerConnectionListener);
    }
    
    public IntraClient() {
        this.disconnected = true;
        this.loggedIn = false;
        this.disconnectReason = "Lost link.";
        this.retryInSeconds = 0;
        this.timeDifference = 0L;
        this.isConnecting = false;
        this.hasFailedConnection = false;
    }
    
    private void reconnect(final String serverIp, final int serverPort, final IntraServerConnectionListener aServerConnectionListener) throws IOException {
        this.serverConnectionListener = aServerConnectionListener;
        (this.connection = new SocketConnection(serverIp, serverPort, 20000)).setMaxBlocksPerIteration(1000000);
        this.connection.setConnectionListener(this);
        this.disconnected = false;
    }
    
    public void reconnectAsynch(final String serverIp, final int serverPort, final IntraServerConnectionListener aServerConnectionListener) {
        this.isConnecting = true;
        this.serverConnectionListener = aServerConnectionListener;
        new Thread() {
            @Override
            public void run() {
                try {
                    IntraClient.this.connection = new SocketConnection(serverIp, serverPort, 20000);
                    IntraClient.this.connection.setMaxBlocksPerIteration(1000000);
                    IntraClient.this.connection.setConnectionListener(IntraClient.this);
                    IntraClient.this.disconnected = false;
                    IntraClient.this.isConnecting = false;
                }
                catch (IOException iox) {
                    IntraClient.this.hasFailedConnection = true;
                }
            }
        }.start();
    }
    
    public void login(final String password, final boolean dev) {
        if (this.retryInSeconds <= 0) {
            try {
                final byte[] passwordBytes = password.getBytes("UTF-8");
                final ByteBuffer buf = this.connection.getBuffer();
                buf.put((byte)1);
                buf.putInt(1);
                buf.put((byte)passwordBytes.length);
                buf.put(passwordBytes);
                buf.put((byte)(dev ? 1 : 0));
                this.connection.flush();
                this.retryInSeconds = 0;
                if (IntraClient.logger.isLoggable(Level.FINE)) {
                    IntraClient.logger.fine("Client sent login");
                }
            }
            catch (IOException e) {
                IntraClient.logger.log(Level.WARNING, "Failed to login", e);
                this.serverConnectionListener.commandFailed(this);
            }
        }
        else {
            --this.retryInSeconds;
        }
    }
    
    public synchronized void update() throws IOException {
        if (this.disconnected) {
            throw new IOException(this.disconnectReason);
        }
        try {
            this.connection.tick();
        }
        catch (Exception e) {
            if (IntraClient.logger.isLoggable(Level.FINE)) {
                IntraClient.logger.log(Level.FINE, "Failed to update on connection: " + this.connection, e);
            }
            this.serverConnectionListener.commandFailed(this);
        }
    }
    
    public synchronized void disconnect(final String reason) {
        if (IntraClient.logger.isLoggable(Level.FINE) && reason != null && reason.equals("Done")) {
            IntraClient.logger.log(Level.FINE, "Disconnecting connection: " + this.connection + ", reason: " + reason);
        }
        if (this.connection != null && this.connection.isConnected()) {
            try {
                this.sendDisconnect();
            }
            catch (Exception ex) {}
            try {
                this.connection.sendShutdown();
                this.connection.disconnect();
                this.connection.closeChannel();
            }
            catch (Exception ex2) {}
        }
        this.disconnectReason = reason;
        this.disconnected = true;
        this.loggedIn = false;
        if (this.serverConnectionListener != null) {
            this.serverConnectionListener.remove(this);
        }
    }
    
    @Override
    public void reallyHandle(final int num, final ByteBuffer bb) {
        try {
            final byte cmd = bb.get();
            if (IntraClient.logger.isLoggable(Level.FINER)) {
                IntraClient.logger.finer("Received cmd " + cmd);
            }
            if (cmd == 2) {
                this.loggedIn = (bb.get() != 0);
                if (IntraClient.logger.isLoggable(Level.FINEST)) {
                    IntraClient.logger.finest("This client is loggedin=" + this.loggedIn);
                }
                final byte[] bytes = new byte[bb.getShort() & 0xFFFF];
                bb.get(bytes);
                final String message = new String(bytes, "UTF-8");
                this.retryInSeconds = (bb.getShort() & 0xFFFF);
                final long targetNow = bb.getLong();
                this.timeDifference = targetNow - System.currentTimeMillis();
                if (!this.loggedIn) {
                    IntraClient.logger.log(Level.WARNING, "Login Failed: " + message);
                    this.serverConnectionListener.commandFailed(this);
                }
                else if (IntraClient.logger.isLoggable(Level.FINER)) {
                    IntraClient.logger.finer("Client logged in - message: " + message + ", " + this);
                }
            }
            else if (cmd == 6) {
                final boolean ok = bb.get() != 0;
                final byte[] bytes2 = new byte[bb.getShort() & 0xFFFF];
                bb.get(bytes2);
                final String sessionKey = new String(bytes2, "UTF-8");
                this.retryInSeconds = (bb.getShort() & 0xFFFF);
                final long targetNow2 = bb.getLong();
                this.timeDifference = targetNow2 - System.currentTimeMillis();
                if (this.retryInSeconds > 0) {
                    this.serverConnectionListener.commandFailed(this);
                }
                else if (!ok) {
                    this.serverConnectionListener.commandFailed(this);
                }
                else if (IntraClient.logger.isLoggable(Level.FINE)) {
                    IntraClient.logger.fine("Client received transferrequest ok - " + this);
                }
            }
            else if (cmd == 10) {
                final long oldTime = WurmCalendar.currentTime;
                final long wurmTime = WurmCalendar.currentTime = bb.getLong();
                IntraClient.logger.log(Level.INFO, "The server just synched wurm clock. New wurm time=" + wurmTime + ". Difference was " + (oldTime - wurmTime) + " wurm seconds.");
                this.serverConnectionListener.commandExecuted(this);
            }
            else if (cmd == 4) {
                this.serverConnectionListener.commandExecuted(this);
            }
            else if (cmd == 5) {
                this.serverConnectionListener.commandFailed(this);
            }
            else if (cmd == 8) {
                if (IntraClient.logger.isLoggable(Level.FINEST)) {
                    IntraClient.logger.finest("Client received data received.");
                }
                this.serverConnectionListener.dataReceived(this);
            }
            else if (cmd == 9) {
                this.serverConnectionListener.receivingData(bb);
            }
            else if (cmd == 11) {
                this.serverConnectionListener.receivingData(bb);
            }
            else if (cmd == 14) {
                this.serverConnectionListener.reschedule(this);
            }
            else if (cmd == 13) {
                if (IntraClient.logger.isLoggable(Level.FINEST)) {
                    IntraClient.logger.finest("IntraClient received PONG - " + this);
                }
                this.serverConnectionListener.receivingData(bb);
            }
            else {
                IntraClient.logger.warning("Ignoring unknown cmd " + cmd);
                System.out.println("Ignoring unknown cmd " + cmd);
            }
        }
        catch (Exception e) {
            IntraClient.logger.log(Level.WARNING, "Problem handling Block: " + bb, e);
            e.printStackTrace();
        }
    }
    
    int sendNextDataPart(final byte[] data, final int index) throws IOException {
        final ByteBuffer buf = this.connection.getBuffer();
        final int length = Math.min(data.length - index, 16366);
        final int nextindex = index + length;
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Sending " + length + " out of " + data.length + " up to " + nextindex + " max size is " + 16384);
        }
        buf.put((byte)7);
        buf.putInt(length);
        buf.put(data, index, length);
        buf.put((byte)((nextindex == data.length) ? 1 : 0));
        this.connection.flush();
        return nextindex;
    }
    
    void executePlayerTransferRequest(final int posx, final int posy, final boolean surfaced) throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Requesting player transfer for coordinates: " + posx + ", " + posy + ", surfaced: " + surfaced + " - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)3);
        buf.putInt(posx);
        buf.putInt(posy);
        buf.put((byte)(surfaced ? 1 : 0));
        this.connection.flush();
    }
    
    void executeSyncCommand() throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Synchronising the time - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)10);
        this.connection.flush();
    }
    
    void executeRequestPlayerVersion(final long playerId) throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Requesting player version for player id: " + playerId + " - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)9);
        buf.putLong(playerId);
        this.connection.flush();
    }
    
    void executeRequestPlayerPaymentExpire(final long playerId) throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Requesting player payment expire for player id: " + playerId + " - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)11);
        buf.putLong(playerId);
        this.connection.flush();
    }
    
    void executeMoneyUpdate(final long playerId, final long currentMoney, final long moneyAdded, final String detail) throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Updating money update for player id: " + playerId + " - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)16);
        buf.putLong(playerId);
        buf.putLong(currentMoney);
        buf.putLong(moneyAdded);
        final byte[] det = detail.getBytes("UTF-8");
        buf.putInt(det.length);
        buf.put(det);
        this.connection.flush();
    }
    
    void executeExpireUpdate(final long playerId, final long currentExpire, final String detail, final int days, final int months, final boolean dealItems) throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Updating expire update for player id: " + playerId + " - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)17);
        buf.putLong(playerId);
        buf.putLong(currentExpire);
        buf.putInt(days);
        buf.putInt(months);
        buf.put((byte)(dealItems ? 1 : 0));
        final byte[] det = detail.getBytes("UTF-8");
        buf.putInt(det.length);
        buf.put(det);
        this.connection.flush();
    }
    
    void executePasswordUpdate(final long playerId, final String currentHashedPassword, final long timestamp) throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Updating password for player id: " + playerId + " at timestamp " + timestamp + " - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)18);
        buf.putLong(playerId);
        final byte[] pw = currentHashedPassword.getBytes("UTF-8");
        buf.putInt(pw.length);
        buf.put(pw);
        buf.putLong(timestamp);
        this.connection.flush();
    }
    
    public void executePingCommand() throws IOException {
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)13);
        this.connection.flush();
    }
    
    static final void sendShortStringLength(final String toSend, final ByteBuffer bb) throws Exception {
        final byte[] toSendStringArr = toSend.getBytes("UTF-8");
        bb.putShort((short)toSendStringArr.length);
        bb.put(toSendStringArr);
    }
    
    private void sendDisconnect() throws IOException {
        if (IntraClient.logger.isLoggable(Level.FINEST)) {
            IntraClient.logger.finest("Client sending disconnect - " + this);
        }
        final ByteBuffer buf = this.connection.getBuffer();
        buf.put((byte)15);
        this.connection.flush();
    }
    
    @Override
    public String toString() {
        return "IntraClient [SocketConnection: " + this.connection + ", disconnected: " + this.disconnected + ", loggedIn: " + this.loggedIn + ", disconnectReason: " + this.disconnectReason + ']';
    }
    
    static {
        IntraClient.logger = Logger.getLogger(IntraClient.class.getName());
    }
}
