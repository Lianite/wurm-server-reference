// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import java.awt.image.PixelGrabber;
import java.io.FileOutputStream;
import com.sun.deploy.config.Config;
import com.sun.javaws.cache.DiskCacheEntry;
import java.io.File;
import com.sun.javaws.exceptions.JNLPException;
import java.io.IOException;
import com.sun.javaws.cache.Cache;
import com.sun.javaws.cache.CacheUtilities;
import com.sun.javaws.cache.DownloadProtocol;
import com.sun.deploy.util.Trace;
import com.sun.deploy.util.TraceLevel;
import java.net.URL;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.LaunchDesc;
import java.awt.Image;
import java.io.OutputStream;

public class IcoEncoder
{
    private static final int IMAGE_TYPE = 1;
    private static final int IMAGE_KIND = 0;
    private OutputStream outputS;
    private static byte ICON_SIZE;
    private static byte NUM_COLORS;
    private static int BYTE_SIZE;
    private Image awtImage;
    
    private IcoEncoder(final OutputStream outputS, final Image awtImage) {
        this.outputS = outputS;
        this.awtImage = awtImage;
    }
    
    public static String getIconPath(final LaunchDesc launchDesc) {
        final IconDesc iconLocation = launchDesc.getInformation().getIconLocation(1, 0);
        if (iconLocation != null) {
            return getIconPath(iconLocation.getLocation(), iconLocation.getVersion());
        }
        return null;
    }
    
    public static String getIconPath(final URL url, final String s) {
        Trace.println("Getting icon path", TraceLevel.BASIC);
        File saveICOfile = null;
        try {
            final DiskCacheEntry resource = DownloadProtocol.getResource(url, s, 2, true, null);
            File file = resource.getMappedBitmap();
            if (file == null || !file.exists()) {
                file = null;
                saveICOfile = saveICOfile(CacheUtilities.getSharedInstance().loadImage(resource.getFile().getPath()));
                Trace.println("updating ICO: " + saveICOfile, TraceLevel.BASIC);
                if (saveICOfile != null) {
                    file = Cache.putMappedImage(url, s, saveICOfile);
                    saveICOfile = null;
                }
            }
            if (file != null) {
                return file.getPath();
            }
        }
        catch (IOException ex) {
            Trace.println("exception creating BMP: " + ex, TraceLevel.BASIC);
        }
        catch (JNLPException ex2) {
            Trace.println("exception creating BMP: " + ex2, TraceLevel.BASIC);
        }
        if (saveICOfile != null) {
            saveICOfile.delete();
        }
        return null;
    }
    
    private static File saveICOfile(final Image image) {
        OutputStream outputStream = null;
        File tempFile = null;
        final File file = new File(Config.getJavawsCacheDir());
        try {
            tempFile = File.createTempFile("javaws", ".ico", file);
            outputStream = new FileOutputStream(tempFile);
            new IcoEncoder(outputStream, image).encode();
            ((FileOutputStream)outputStream).close();
            return tempFile;
        }
        catch (Throwable t) {
            if (outputStream != null) {
                try {
                    ((FileOutputStream)outputStream).close();
                }
                catch (IOException ex) {}
            }
            if (tempFile != null) {
                tempFile.delete();
            }
            return null;
        }
    }
    
    private void createBitmap() throws IOException {
        final int n = 32;
        final int n2 = 32;
        int n3 = 0;
        final int[] array = new int[n * n2];
        final byte[] array2 = new byte[n * n2 * 3];
        final byte[] array3 = new byte[n * n2 * 3];
        final byte[] array4 = new byte[n * 4];
        final byte[] array5 = new byte[n * 4];
        final Image awtImage = this.awtImage;
        final int n4 = 32;
        final int n5 = 32;
        final Image awtImage2 = this.awtImage;
        final PixelGrabber pixelGrabber = new PixelGrabber(awtImage.getScaledInstance(n4, n5, 1), 0, 0, n, n2, array, 0, n);
        try {
            if (pixelGrabber.grabPixels()) {
                Trace.println("pixels grabbed successfully", TraceLevel.BASIC);
            }
            else {
                Trace.println("cannot grab pixels!", TraceLevel.BASIC);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        byte b = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        for (int i = 0; i < n * n2; ++i) {
            final int n9 = array[i] >> 24 & 0xFF;
            final int n10 = array[i] >> 16 & 0xFF;
            final int n11 = array[i] >> 8 & 0xFF;
            final int n12 = array[i] & 0xFF;
            if (n9 != 0) {
                Trace.print(" 1", TraceLevel.BASIC);
            }
            else {
                Trace.print(" " + n9, TraceLevel.BASIC);
            }
            if (++n8 == 32) {
                Trace.println(" ", TraceLevel.BASIC);
                n8 = 0;
            }
            if (n9 == 0) {
                b |= (byte)(128 >> n7);
            }
            if (++n7 == 8) {
                array4[n6++] = b;
                b = 0;
                n7 = 0;
            }
            array2[n3++] = (byte)n12;
            array2[n3++] = (byte)n11;
            array2[n3++] = (byte)n10;
        }
        int n13 = 0;
        Trace.println("andPxiels bitmap", TraceLevel.BASIC);
        for (int j = 0; j < 128; ++j) {
            for (int k = 0; k < 8; k = (byte)(k + 1)) {
                if ((array4[j] & 128 >> k) != 0x0) {
                    Trace.print(" 1", TraceLevel.BASIC);
                }
                else {
                    Trace.print(" 0", TraceLevel.BASIC);
                }
            }
            if (++n13 == 4) {
                Trace.println(" ", TraceLevel.BASIC);
                n13 = 0;
            }
        }
        for (int l = 0; l < n2; ++l) {
            final int n14 = l * n * 3;
            final int n15 = (n2 - l - 1) * n * 3;
            for (int n16 = 0; n16 < n * 3; ++n16) {
                array3[n14 + n16] = array2[n15 + n16];
            }
            final int n17 = l * (n / 8);
            final int n18 = (n2 - l - 1) * (n / 8);
            for (int n19 = 0; n19 < n / 8; ++n19) {
                array5[n17 + n19] = array4[n18 + n19];
            }
        }
        this.outputS.write(array3);
        this.outputS.write(array5);
    }
    
    public void encode() {
        this.writeIcoHeader();
        this.writeIconDirEntry();
        try {
            this.writeInfoHeader(40, 24);
            this.createBitmap();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void writeInfoHeader(final int n, final int n2) throws IOException {
        this.writeDWord(n);
        this.writeDWord(32);
        this.writeDWord(64);
        this.writeWord(1);
        this.writeWord(n2);
        this.writeDWord(0);
        this.writeDWord(0);
        this.writeDWord(0);
        this.writeDWord(0);
        this.writeDWord(0);
        this.writeDWord(0);
    }
    
    private void writeIconDirEntry() {
        try {
            final byte icon_SIZE = IcoEncoder.ICON_SIZE;
            this.outputS.write(icon_SIZE);
            this.outputS.write(icon_SIZE);
            this.outputS.write(IcoEncoder.NUM_COLORS);
            this.outputS.write(0);
            this.writeWord(1);
            this.writeWord(24);
            this.writeDWord(3240);
            this.writeDWord(22);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void writeIcoHeader() {
        try {
            this.writeWord(0);
            final int n = 1;
            this.writeWord(n);
            this.writeWord(n);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void writeWord(final int n) throws IOException {
        this.outputS.write(n & 0xFF);
        this.outputS.write((n & 0xFF00) >> 8);
    }
    
    public void writeDWord(final int n) throws IOException {
        this.outputS.write(n & 0xFF);
        this.outputS.write((n & 0xFF00) >> 8);
        this.outputS.write((n & 0xFF0000) >> 16);
        this.outputS.write((n & 0xFF000000) >> 24);
    }
    
    static {
        IcoEncoder.ICON_SIZE = 32;
        IcoEncoder.NUM_COLORS = 0;
        IcoEncoder.BYTE_SIZE = 8;
    }
}
