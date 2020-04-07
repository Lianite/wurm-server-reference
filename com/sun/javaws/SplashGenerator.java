// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws;

import com.sun.image.codec.jpeg.JPEGCodec;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import javax.swing.border.Border;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Dimension;
import com.sun.deploy.util.TraceLevel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Component;
import javax.swing.border.CompoundBorder;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JPanel;
import com.sun.javaws.jnl.IconDesc;
import com.sun.javaws.jnl.InformationDesc;
import com.sun.javaws.cache.CacheImageLoader;
import java.awt.Image;
import java.io.IOException;
import com.sun.deploy.util.Trace;
import java.io.InputStream;
import java.io.FileInputStream;
import com.sun.deploy.config.Config;
import java.util.Properties;
import java.awt.Frame;
import com.sun.javaws.jnl.LaunchDesc;
import java.io.File;
import com.sun.javaws.cache.CacheImageLoaderCallback;

class SplashGenerator extends Thread implements CacheImageLoaderCallback
{
    private File _index;
    private File _dir;
    private final String _key;
    private final LaunchDesc _ld;
    private final Frame _owner;
    private Properties _props;
    private boolean _useAppSplash;
    
    public SplashGenerator(final Frame owner, final LaunchDesc ld) {
        this._props = new Properties();
        this._useAppSplash = false;
        this._owner = owner;
        this._ld = ld;
        this._dir = new File(Config.getSplashDir());
        this._key = this._ld.getCanonicalHome().toString();
        this._index = new File(Config.getSplashIndex());
        Config.setSplashCache();
        Config.storeIfDirty();
        if (this._index.exists()) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(this._index);
                if (fileInputStream != null) {
                    this._props.load(fileInputStream);
                    fileInputStream.close();
                }
            }
            catch (IOException ex) {
                Trace.ignoredException((Exception)ex);
            }
        }
    }
    
    public boolean needsCustomSplash() {
        return !this._props.containsKey(this._key);
    }
    
    public void remove() {
        this.addSplashToCacheIndex(this._key, null);
    }
    
    public void run() {
        final InformationDesc information = this._ld.getInformation();
        information.getIcons();
        try {
            this._dir.mkdirs();
        }
        catch (Throwable t) {
            this.splashError(t);
        }
        try {
            this._index.createNewFile();
        }
        catch (Throwable t2) {
            this.splashError(t2);
        }
        IconDesc iconDesc = information.getIconLocation(2, 4);
        if (!(this._useAppSplash = (iconDesc != null))) {
            iconDesc = information.getIconLocation(2, 0);
        }
        if (iconDesc == null) {
            try {
                this.create(null, null);
            }
            catch (Throwable t3) {
                this.splashError(t3);
            }
        }
        else {
            CacheImageLoader.getInstance().loadImage(iconDesc, this);
        }
    }
    
    public void imageAvailable(final IconDesc iconDesc, final Image image, final File file) {
    }
    
    public void finalImageAvailable(final IconDesc iconDesc, final Image image, final File file) {
        if (!Globals.isHeadless()) {
            try {
                this.create(image, file);
            }
            catch (Throwable t) {
                this.splashError(t);
            }
        }
    }
    
    public void create(final Image image, final File file) {
        final InformationDesc information = this._ld.getInformation();
        final String title = information.getTitle();
        final String vendor = information.getVendor();
        int n = 5;
        final JPanel panel = new JPanel();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final CompoundBorder compoundBorder = new CompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createBevelBorder(0));
        final Insets borderInsets = compoundBorder.getBorderInsets(panel);
        int min = 320;
        int min2 = 64 + 2 * n + borderInsets.top + borderInsets.bottom;
        int width;
        int height;
        if (image == null) {
            height = (width = 0);
        }
        else if (this._useAppSplash) {
            height = image.getHeight(this._owner);
            width = image.getWidth(this._owner);
            if (file != null) {
                try {
                    final String canonicalPath = file.getCanonicalPath();
                    if (canonicalPath.endsWith(".jpg")) {
                        this.addSplashToCacheIndex(this._key, canonicalPath);
                        return;
                    }
                }
                catch (IOException ex2) {}
            }
            n = 0;
            min = Math.min(width, screenSize.width);
            min2 = Math.min(height, screenSize.height);
        }
        else {
            height = (width = 64);
        }
        final BufferedImage bufferedImage = new BufferedImage(min, min2, 5);
        final Graphics2D graphics = bufferedImage.createGraphics();
        Rectangle rectangle;
        if (this._useAppSplash) {
            rectangle = new Rectangle(0, 0, min, min2);
        }
        else {
            graphics.setColor(new Color(238, 238, 238));
            graphics.fillRect(0, 0, min, min2);
            graphics.setColor(Color.black);
            compoundBorder.paintBorder(panel, graphics, 0, 0, min, min2);
            final Rectangle rectangle2 = new Rectangle(borderInsets.left, borderInsets.top, min - borderInsets.left - borderInsets.right, min2 - borderInsets.top - borderInsets.bottom);
            final Border lineBorder = BorderFactory.createLineBorder(Color.black);
            final Insets borderInsets2 = lineBorder.getBorderInsets(panel);
            rectangle = new Rectangle(borderInsets.left + n, borderInsets.top + n, width, height);
            if (image != null) {
                lineBorder.paintBorder(panel, graphics, rectangle.x - borderInsets2.left, rectangle.y - borderInsets2.top, rectangle.width + borderInsets2.left + borderInsets2.right, rectangle.height + borderInsets2.top + borderInsets2.bottom);
                final Rectangle rectangle3 = rectangle2;
                rectangle3.x += width + 2 * n;
                final Rectangle rectangle4 = rectangle2;
                rectangle4.width -= width + 2 * n;
            }
            final Font font = new Font("SansSerif", 1, 20);
            final Font font2 = new Font("SansSerif", 1, 16);
            graphics.setColor(Color.black);
            graphics.setFont(font);
            final Rectangle rectangle6;
            final Rectangle rectangle5 = rectangle6 = new Rectangle(rectangle2.x, rectangle2.y + 6, rectangle2.width, rectangle2.height - 12);
            rectangle6.height /= 2;
            this.drawStringInRect(graphics, title, rectangle5, 1);
            graphics.setFont(font2);
            final Rectangle rectangle7 = rectangle5;
            rectangle7.y += rectangle5.height;
            this.drawStringInRect(graphics, vendor, rectangle5, 1);
        }
        if (image != null) {
            int n2 = 0;
            while (!graphics.drawImage(image, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this._owner)) {
                try {
                    Thread.sleep(2000L);
                }
                catch (Exception ex3) {}
                if (++n2 > 5) {
                    Trace.println("couldnt draw splash image : " + image, TraceLevel.BASIC);
                    break;
                }
            }
        }
        try {
            final File tempFile = File.createTempFile("splash", ".jpg", this._dir);
            this.writeImage(tempFile, bufferedImage);
            this.addSplashToCacheIndex(this._key, tempFile.getCanonicalPath());
        }
        catch (IOException ex) {
            this.splashError(ex);
        }
    }
    
    private void drawStringInRect(final Graphics2D graphics2D, String string, final Rectangle rectangle, final int n) {
        final FontMetrics fontMetrics = graphics2D.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(string, graphics2D);
        final int maxAscent = fontMetrics.getMaxAscent();
        final int n2 = (int)stringBounds.getWidth();
        final int n3 = (int)stringBounds.getHeight();
        int n4 = 0;
        if (n2 > rectangle.width) {
            n4 = rectangle.x;
            String s = string.substring(0, string.length() - 3);
            for (int length = s.length(); length > 3 && fontMetrics.stringWidth(s + "...") > rectangle.width; --length, s = s.substring(0, length)) {}
            string = s + "...";
        }
        else {
            switch (n) {
                default: {
                    n4 = rectangle.x;
                    break;
                }
                case 1: {
                    n4 = rectangle.x + (rectangle.width - n2) / 2;
                    break;
                }
                case 2: {
                    n4 = rectangle.x + (rectangle.width - n2 - 1);
                    break;
                }
            }
        }
        if (n4 < rectangle.x) {
            n4 = rectangle.x;
        }
        graphics2D.drawString(string, n4, rectangle.y + maxAscent + (rectangle.height - n3) / 2);
    }
    
    private void addSplashToCacheIndex(final String s, final String s2) {
        if (s2 != null) {
            this._props.setProperty(s, s2);
        }
        else if (this._props.containsKey(s)) {
            this._props.remove(s);
        }
        final File[] listFiles = this._dir.listFiles();
        if (listFiles == null) {
            return;
        }
        for (int i = 0; i < listFiles.length; ++i) {
            if (!listFiles[i].equals(this._index)) {
                try {
                    if (!this._props.containsValue(listFiles[i].getCanonicalPath())) {
                        listFiles[i].delete();
                    }
                }
                catch (IOException ex) {
                    this.splashError(ex);
                }
            }
        }
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(this._index);
            this._props.store(fileOutputStream, "");
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (IOException ex2) {
            this.splashError(ex2);
        }
    }
    
    private void writeImage(final File file, final BufferedImage bufferedImage) {
        try {
            JPEGCodec.createJPEGEncoder(new FileOutputStream(file)).encode(bufferedImage);
        }
        catch (Throwable t) {
            this.splashError(t);
        }
    }
    
    private void splashError(final Throwable t) {
        LaunchErrorDialog.show(this._owner, t, false);
        throw new Error(t.toString());
    }
}
