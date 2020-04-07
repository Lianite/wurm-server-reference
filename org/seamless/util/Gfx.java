// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util;

import java.awt.Image;
import java.io.IOException;
import java.awt.Graphics2D;
import java.io.OutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Gfx
{
    public static byte[] resizeProportionally(final ImageIcon icon, final String contentType, int newWidth, int newHeight) throws IOException {
        final double widthRatio = (newWidth != icon.getIconWidth()) ? (newWidth / icon.getIconWidth()) : 1.0;
        final double heightRatio = (newHeight != icon.getIconHeight()) ? (newHeight / icon.getIconHeight()) : 1.0;
        if (widthRatio < heightRatio) {
            newHeight = (int)(icon.getIconHeight() * widthRatio);
        }
        else {
            newWidth = (int)(icon.getIconWidth() * heightRatio);
        }
        final int imageType = "image/png".equals(contentType) ? 2 : 1;
        final BufferedImage bImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), imageType);
        final Graphics2D g2d = bImg.createGraphics();
        g2d.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
        g2d.dispose();
        final BufferedImage scaledImg = getScaledInstance(bImg, newWidth, newHeight, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR, false);
        String formatName = "";
        if ("image/png".equals(contentType)) {
            formatName = "png";
        }
        else if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) {
            formatName = "jpeg";
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        ImageIO.write(scaledImg, formatName, baos);
        return baos.toByteArray();
    }
    
    public static BufferedImage getScaledInstance(final BufferedImage img, final int targetWidth, final int targetHeight, final Object hint, final boolean higherQuality) {
        final int type = (img.getTransparency() == 1) ? 1 : 2;
        BufferedImage ret = img;
        int w;
        int h;
        if (higherQuality) {
            w = img.getWidth();
            h = img.getHeight();
        }
        else {
            w = targetWidth;
            h = targetHeight;
        }
        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            final BufferedImage tmp = new BufferedImage(w, h, type);
            final Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();
            ret = tmp;
        } while (w != targetWidth || h != targetHeight);
        return ret;
    }
}
