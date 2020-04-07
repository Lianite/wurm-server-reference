// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.jnlp;

import java.util.HashMap;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import java.io.IOException;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.util.Map;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.applet.AudioClip;

final class AppletAudioClip implements AudioClip
{
    private static Constructor acConstructor;
    private URL url;
    private AudioClip audioClip;
    private static Map audioClips;
    
    public AppletAudioClip() {
        this.url = null;
        this.audioClip = null;
    }
    
    public AppletAudioClip(final URL url) {
        this.url = null;
        this.audioClip = null;
        this.url = url;
        try {
            this.createAppletAudioClip(url.openStream());
        }
        catch (IOException ex) {
            Trace.println("IOException creating AppletAudioClip" + ex, TraceLevel.BASIC);
        }
    }
    
    public static synchronized AudioClip get(final URL url) {
        checkConnect(url);
        AudioClip audioClip = AppletAudioClip.audioClips.get(url);
        if (audioClip == null) {
            audioClip = new AppletAudioClip(url);
            AppletAudioClip.audioClips.put(url, audioClip);
        }
        return audioClip;
    }
    
    void createAppletAudioClip(final InputStream inputStream) throws IOException {
        if (AppletAudioClip.acConstructor == null) {
            Trace.println("Initializing AudioClip constructor.", TraceLevel.BASIC);
            try {
                AppletAudioClip.acConstructor = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor>)new PrivilegedExceptionAction() {
                    public Object run() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
                        Class<?> clazz;
                        try {
                            clazz = Class.forName("com.sun.media.sound.JavaSoundAudioClip", true, ClassLoader.getSystemClassLoader());
                            Trace.println("Loaded JavaSoundAudioClip", TraceLevel.BASIC);
                        }
                        catch (ClassNotFoundException ex) {
                            clazz = Class.forName("sun.audio.SunAudioClip", true, null);
                            Trace.println("Loaded SunAudioClip", TraceLevel.BASIC);
                        }
                        return clazz.getConstructor(Class.forName("java.io.InputStream"));
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                Trace.println("Got a PrivilegedActionException: " + ex.getException(), TraceLevel.BASIC);
                throw new IOException("Failed to get AudioClip constructor: " + ex.getException());
            }
        }
        try {
            this.audioClip = AppletAudioClip.acConstructor.newInstance(inputStream);
        }
        catch (Exception ex2) {
            throw new IOException("Failed to construct the AudioClip: " + ex2);
        }
    }
    
    private static void checkConnect(final URL url) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                final Permission permission = url.openConnection().getPermission();
                if (permission != null) {
                    securityManager.checkPermission(permission);
                }
                else {
                    securityManager.checkConnect(url.getHost(), url.getPort());
                }
            }
            catch (IOException ex) {
                securityManager.checkConnect(url.getHost(), url.getPort());
            }
        }
    }
    
    public synchronized void play() {
        if (this.audioClip != null) {
            this.audioClip.play();
        }
    }
    
    public synchronized void loop() {
        if (this.audioClip != null) {
            this.audioClip.loop();
        }
    }
    
    public synchronized void stop() {
        if (this.audioClip != null) {
            this.audioClip.stop();
        }
    }
    
    static {
        AppletAudioClip.acConstructor = null;
        AppletAudioClip.audioClips = new HashMap();
    }
}
