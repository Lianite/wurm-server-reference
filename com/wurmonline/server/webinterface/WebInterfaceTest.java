// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.creatures.Creature;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import com.wurmonline.shared.exceptions.WurmServerException;
import java.security.MessageDigest;
import com.wurmonline.server.epic.Valrei;
import com.wurmonline.server.epic.HexMap;
import java.util.Iterator;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import com.wurmonline.server.creatures.CreatureMove;
import java.util.LinkedList;
import com.wurmonline.server.zones.FaithZone;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WebInterfaceTest implements MiscConstants
{
    private WebInterface wurm;
    private static Logger logger;
    private static FaithZone[][] surfaceDomains;
    private final String intraServerPassword = "";
    private static final LinkedList<CreatureMove> list;
    private static final LinkedList<CreatureMove> list2;
    
    public WebInterfaceTest() {
        this.wurm = null;
    }
    
    private void connect(final String ip) throws MalformedURLException, RemoteException, NotBoundException {
        this.connect(ip, "7220");
    }
    
    private void connect(final String ip, final String port) throws MalformedURLException, RemoteException, NotBoundException {
        if (this.wurm == null) {
            final String name = "//" + ip + ":" + port + "/WebInterface";
            this.wurm = (WebInterface)Naming.lookup(name);
        }
    }
    
    public void shutDown(final String host, final String port, final String user, final String pass) {
        this.wurm = null;
        try {
            this.connect(host, port);
            if (this.wurm != null) {
                this.wurm.shutDown("", user, pass, "Console initiated shutdown.", 30);
                System.out.println("Two. Host " + host + " shutting down.");
            }
        }
        catch (Exception ex) {
            WebInterfaceTest.logger.log(Level.INFO, "failed to shut down localhost");
        }
    }
    
    public void globalShutdown(final String reason, final int time, final String user, final String pass) {
        if (Servers.localServer != Servers.loginServer) {
            System.out.println("You must initiate a global shutdown from " + Servers.loginServer.getName() + ".");
            return;
        }
        for (final ServerEntry server : Servers.getAllServers()) {
            this.wurm = null;
            System.out.println("Sending shutdown command to " + server.getName() + " @ " + server.INTRASERVERADDRESS);
            try {
                this.connect(server.INTRASERVERADDRESS);
                if (this.wurm != null) {
                    this.wurm.shutDown(server.INTRASERVERPASSWORD, user, pass, reason, time);
                }
                else {
                    System.out.println("Failed to shutdown " + server.getName());
                }
            }
            catch (Exception e) {
                System.out.println("Failed to shutdown " + server.getName());
                e.printStackTrace();
            }
        }
    }
    
    public void shutdownAll(final String reason, final int time) throws MalformedURLException, RemoteException, NotBoundException {
    }
    
    private CreatureMove getMove(final int ts) {
        for (final CreatureMove c : WebInterfaceTest.list) {
            if (c.timestamp == ts) {
                return c;
            }
        }
        for (final CreatureMove c : WebInterfaceTest.list2) {
            if (c.timestamp == ts) {
                return c;
            }
        }
        return null;
    }
    
    public static final double getModifiedEffect(final double eff) {
        return (10000.0 - (100.0 - eff) * (100.0 - eff)) / 100.0;
    }
    
    public static void runEpic() {
        new Thread() {
            private final HexMap val = new Valrei();
            private boolean loaded = false;
            
            @Override
            public void run() {
            Label_0019:
                while (true) {
                    if (this.loaded) {
                        break Label_0019;
                    }
                    this.val.loadAllEntities();
                    this.loaded = true;
                    while (true) {
                        try {
                            while (true) {
                                Thread.sleep(20L);
                                this.val.pollAllEntities(true);
                            }
                        }
                        catch (Exception ex) {
                            WebInterfaceTest.logger.log(Level.INFO, ex.getMessage(), ex);
                            continue;
                        }
                        continue Label_0019;
                    }
                    break;
                }
            }
        }.start();
    }
    
    public static String encryptMD5(final String plaintext) throws Exception {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new WurmServerException("No such algorithm 'MD5'", e);
        }
        try {
            md.update(plaintext.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e2) {
            throw new WurmServerException("No such encoding: UTF-8", e2);
        }
        final byte[] raw = md.digest();
        final BigInteger bi = new BigInteger(1, raw);
        final String hash = bi.toString(16);
        return hash;
    }
    
    public static void main(final String[] args) {
    }
    
    public static String bytesToStringUTFCustom(final byte[] bytes) {
        final char[] buffer = new char[bytes.length >> 1];
        for (int i = 0; i < buffer.length; ++i) {
            final int bpos = i << 1;
            final char c = (char)(((bytes[bpos] & 0xFF) << 8) + (bytes[bpos + 1] & 0xFF));
            buffer[i] = c;
        }
        return new String(buffer);
    }
    
    public static String bytesToStringUTFNIO(final byte[] bytes) {
        final CharBuffer cBuffer = ByteBuffer.wrap(bytes).asCharBuffer();
        return cBuffer.toString();
    }
    
    public static final void printZone(final int tilex, final int tiley) {
        System.out.println(WebInterfaceTest.surfaceDomains[tilex >> 3][tiley >> 3].getStartX() + ", " + WebInterfaceTest.surfaceDomains[tilex >> 3][tiley >> 3].getStartY() + ":" + WebInterfaceTest.surfaceDomains[tilex >> 3][tiley >> 3].getCenterX() + ", " + WebInterfaceTest.surfaceDomains[tilex >> 3][tiley >> 3].getCenterY());
    }
    
    public static int getDir(final int ctx, final int cty, final int targetX, final int targetY) {
        final double newrot = Math.atan2((targetY << 2) + 2 - ((cty << 2) + 2), (targetX << 2) + 2 - ((ctx << 2) + 2));
        float attAngle = (float)(newrot * 57.29577951308232) + 90.0f;
        attAngle = Creature.normalizeAngle(attAngle);
        final float degree = 22.5f;
        if (attAngle >= 337.5 || attAngle < 22.5f) {
            return 0;
        }
        for (int x = 0; x < 8; ++x) {
            if (attAngle < 22.5f + 45 * x) {
                return x;
            }
        }
        return 0;
    }
    
    static {
        WebInterfaceTest.logger = Logger.getLogger(WebInterfaceTest.class.getName());
        WebInterfaceTest.surfaceDomains = new FaithZone[40][40];
        list = new LinkedList<CreatureMove>();
        list2 = new LinkedList<CreatureMove>();
    }
}
