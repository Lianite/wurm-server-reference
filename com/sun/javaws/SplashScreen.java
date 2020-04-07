// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.javaws.jnl.LaunchDesc;
import java.awt.Frame;
import java.io.OutputStream;
import com.sun.deploy.util.TraceLevel;
import com.sun.deploy.util.Trace;
import java.io.IOException;
import java.net.Socket;

public class SplashScreen
{
    private static boolean _alreadyHidden;
    private static final int HIDE_SPASH_SCREEN_TOKEN = 90;
    
    public static void hide() {
        hide(JnlpxArgs.getSplashPort());
    }
    
    private static void hide(final int n) {
        if (n <= 0 || SplashScreen._alreadyHidden) {
            return;
        }
        SplashScreen._alreadyHidden = true;
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", n);
            if (socket != null) {
                final OutputStream outputStream = socket.getOutputStream();
                try {
                    outputStream.write(90);
                    outputStream.flush();
                }
                catch (IOException ex3) {}
                outputStream.close();
            }
        }
        catch (IOException ex4) {}
        catch (Exception ex) {
            Trace.ignoredException(ex);
        }
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException ex2) {
                Trace.println("exception closing socket: " + ex2, TraceLevel.BASIC);
            }
        }
    }
    
    public static void generateCustomSplash(final Frame frame, final LaunchDesc launchDesc, final boolean b) {
        final SplashGenerator splashGenerator = new SplashGenerator(frame, launchDesc);
        if (b || splashGenerator.needsCustomSplash()) {
            splashGenerator.start();
        }
    }
    
    public static void removeCustomSplash(final LaunchDesc launchDesc) {
        new SplashGenerator(null, launchDesc).remove();
    }
    
    static {
        SplashScreen._alreadyHidden = false;
    }
}
