// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jardiff;

import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.List;
import java.io.InputStream;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ResourceBundle;
import com.sun.javaws.cache.Patcher;

public class JarDiffPatcher implements JarDiffConstants, Patcher
{
    private static final int DEFAULT_READ_SIZE = 2048;
    private static byte[] newBytes;
    private static byte[] oldBytes;
    private static ResourceBundle _resources;
    
    public static ResourceBundle getResources() {
        return JarDiff.getResources();
    }
    
    public void applyPatch(final PatchDelegate patchDelegate, final String s, final String s2, final OutputStream outputStream) throws IOException {
        final File file = new File(s);
        final File file2 = new File(s2);
        final JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
        final JarFile jarFile = new JarFile(file);
        final JarFile jarFile2 = new JarFile(file2);
        final HashSet set = new HashSet<Object>();
        final HashMap<Object, String> hashMap = new HashMap<Object, String>();
        this.determineNameMapping(jarFile2, set, hashMap);
        final Object[] array = hashMap.keySet().toArray();
        final HashSet<Object> set2 = new HashSet<Object>();
        final Enumeration<JarEntry> entries = jarFile.entries();
        if (entries != null) {
            while (entries.hasMoreElements()) {
                set2.add(entries.nextElement().getName());
            }
        }
        final double n = set2.size() + array.length + jarFile2.size();
        double n2 = 0.0;
        set2.removeAll(set);
        double n3 = n - set.size();
        final Enumeration<JarEntry> entries2 = jarFile2.entries();
        if (entries2 != null) {
            while (entries2.hasMoreElements()) {
                final JarEntry jarEntry = entries2.nextElement();
                if (!"META-INF/INDEX.JD".equals(jarEntry.getName())) {
                    this.updateDelegate(patchDelegate, n2, n3);
                    ++n2;
                    this.writeEntry(jarOutputStream, jarEntry, jarFile2);
                    if (!set2.remove(jarEntry.getName())) {
                        continue;
                    }
                    --n3;
                }
                else {
                    --n3;
                }
            }
        }
        for (int i = 0; i < array.length; ++i) {
            final String s3 = (String)array[i];
            final String s4 = hashMap.get(s3);
            final JarEntry jarEntry2 = jarFile.getJarEntry(s4);
            if (jarEntry2 == null) {
                this.handleException("jardiff.error.badmove", "move" + s4 + " " + s3);
            }
            final JarEntry jarEntry3 = new JarEntry(s3);
            jarEntry3.setTime(jarEntry2.getTime());
            jarEntry3.setSize(jarEntry2.getSize());
            jarEntry3.setCompressedSize(jarEntry2.getCompressedSize());
            jarEntry3.setCrc(jarEntry2.getCrc());
            jarEntry3.setMethod(jarEntry2.getMethod());
            jarEntry3.setExtra(jarEntry2.getExtra());
            jarEntry3.setComment(jarEntry2.getComment());
            this.updateDelegate(patchDelegate, n2, n3);
            ++n2;
            this.writeEntry(jarOutputStream, jarEntry3, jarFile.getInputStream(jarEntry2));
            if (set2.remove(s4)) {
                --n3;
            }
        }
        final Iterator<String> iterator = set2.iterator();
        if (iterator != null) {
            while (iterator.hasNext()) {
                final JarEntry jarEntry4 = jarFile.getJarEntry(iterator.next());
                this.updateDelegate(patchDelegate, n2, n3);
                ++n2;
                this.writeEntry(jarOutputStream, jarEntry4, jarFile);
            }
        }
        this.updateDelegate(patchDelegate, n2, n3);
        jarOutputStream.finish();
    }
    
    private void updateDelegate(final PatchDelegate patchDelegate, final double n, final double n2) {
        if (patchDelegate != null) {
            patchDelegate.patching((int)(n / n2));
        }
    }
    
    private void determineNameMapping(final JarFile jarFile, final Set set, final Map map) throws IOException {
        final InputStream inputStream = jarFile.getInputStream(jarFile.getEntry("META-INF/INDEX.JD"));
        if (inputStream == null) {
            this.handleException("jardiff.error.noindex", null);
        }
        final LineNumberReader lineNumberReader = new LineNumberReader(new InputStreamReader(inputStream, "UTF-8"));
        final String line = lineNumberReader.readLine();
        if (line == null || !line.equals("version 1.0")) {
            this.handleException("jardiff.error.badheader", line);
        }
        String line2;
        while ((line2 = lineNumberReader.readLine()) != null) {
            if (line2.startsWith("remove")) {
                final List subpaths = this.getSubpaths(line2.substring("remove".length()));
                if (subpaths.size() != 1) {
                    this.handleException("jardiff.error.badremove", line2);
                }
                set.add(subpaths.get(0));
            }
            else if (line2.startsWith("move")) {
                final List subpaths2 = this.getSubpaths(line2.substring("move".length()));
                if (subpaths2.size() != 2) {
                    this.handleException("jardiff.error.badmove", line2);
                }
                if (map.put(subpaths2.get(1), subpaths2.get(0)) == null) {
                    continue;
                }
                this.handleException("jardiff.error.badmove", line2);
            }
            else {
                if (line2.length() <= 0) {
                    continue;
                }
                this.handleException("jardiff.error.badcommand", line2);
            }
        }
    }
    
    private void handleException(final String s, final String s2) throws IOException {
        try {
            throw new IOException(getResources().getString(s) + " " + s2);
        }
        catch (MissingResourceException ex) {
            System.err.println("Fatal error: " + s);
            new Throwable().printStackTrace(System.err);
            System.exit(-1);
        }
    }
    
    private List getSubpaths(final String s) {
        int i = 0;
        final int length = s.length();
        final ArrayList<String> list = new ArrayList<String>();
        while (i < length) {
            while (i < length && Character.isWhitespace(s.charAt(i))) {
                ++i;
            }
            if (i < length) {
                int n = i;
                String s2 = null;
                while (i < length) {
                    final char char1 = s.charAt(i);
                    if (char1 == '\\' && i + 1 < length && s.charAt(i + 1) == ' ') {
                        if (s2 == null) {
                            s2 = s.substring(n, i);
                        }
                        else {
                            s2 += s.substring(n, i);
                        }
                        n = ++i;
                    }
                    else if (Character.isWhitespace(char1)) {
                        break;
                    }
                    ++i;
                }
                if (n != i) {
                    if (s2 == null) {
                        s2 = s.substring(n, i);
                    }
                    else {
                        s2 += s.substring(n, i);
                    }
                }
                list.add(s2);
            }
        }
        return list;
    }
    
    private void writeEntry(final JarOutputStream jarOutputStream, final JarEntry jarEntry, final JarFile jarFile) throws IOException {
        this.writeEntry(jarOutputStream, jarEntry, jarFile.getInputStream(jarEntry));
    }
    
    private void writeEntry(final JarOutputStream jarOutputStream, final JarEntry jarEntry, final InputStream inputStream) throws IOException {
        jarOutputStream.putNextEntry(jarEntry);
        for (int i = inputStream.read(JarDiffPatcher.newBytes); i != -1; i = inputStream.read(JarDiffPatcher.newBytes)) {
            jarOutputStream.write(JarDiffPatcher.newBytes, 0, i);
        }
        inputStream.close();
    }
    
    static {
        JarDiffPatcher.newBytes = new byte[2048];
        JarDiffPatcher.oldBytes = new byte[2048];
        JarDiffPatcher._resources = JarDiff.getResources();
    }
}
