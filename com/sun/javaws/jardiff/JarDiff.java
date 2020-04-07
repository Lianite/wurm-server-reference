// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.javaws.jardiff;

import java.util.Enumeration;
import java.util.ListIterator;
import java.util.LinkedList;
import java.io.File;
import java.util.jar.JarFile;
import com.sun.javaws.cache.Patcher;
import java.util.MissingResourceException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.HashSet;
import java.util.HashMap;
import java.io.OutputStream;
import java.util.ResourceBundle;

public class JarDiff implements JarDiffConstants
{
    private static final int DEFAULT_READ_SIZE = 2048;
    private static byte[] newBytes;
    private static byte[] oldBytes;
    private static ResourceBundle _resources;
    private static boolean _debug;
    
    public static ResourceBundle getResources() {
        if (JarDiff._resources == null) {
            JarDiff._resources = ResourceBundle.getBundle("com/sun/javaws/jardiff/resources/strings");
        }
        return JarDiff._resources;
    }
    
    public static void createPatch(final String s, final String s2, final OutputStream outputStream, final boolean b) throws IOException {
        final JarFile2 jarFile2 = new JarFile2(s);
        final JarFile2 jarFile3 = new JarFile2(s2);
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        final HashSet set = new HashSet();
        final HashSet set2 = new HashSet<String>();
        final HashSet set3 = new HashSet<String>();
        final HashSet set4 = new HashSet<String>();
        final Iterator jarEntries = jarFile3.getJarEntries();
        if (jarEntries != null) {
            while (jarEntries.hasNext()) {
                final JarEntry jarEntry = jarEntries.next();
                final String name = jarEntry.getName();
                final String bestMatch = jarFile2.getBestMatch(jarFile3, jarEntry);
                if (bestMatch == null) {
                    if (JarDiff._debug) {
                        System.out.println("NEW: " + name);
                    }
                    set4.add(name);
                }
                else if (bestMatch.equals(name) && !set3.contains(bestMatch)) {
                    if (JarDiff._debug) {
                        System.out.println(name + " added to implicit set!");
                    }
                    set2.add(name);
                }
                else {
                    if (!b && (set2.contains(bestMatch) || set3.contains(bestMatch))) {
                        if (JarDiff._debug) {
                            System.out.println("NEW: " + name);
                        }
                        set4.add(name);
                    }
                    else {
                        if (JarDiff._debug) {
                            System.err.println("moved.put " + name + " " + bestMatch);
                        }
                        hashMap.put(name, bestMatch);
                        set3.add(bestMatch);
                    }
                    if (!set2.contains(bestMatch) || !b) {
                        continue;
                    }
                    if (JarDiff._debug) {
                        System.err.println("implicit.remove " + bestMatch);
                        System.err.println("moved.put " + bestMatch + " " + bestMatch);
                    }
                    set2.remove(bestMatch);
                    hashMap.put(bestMatch, bestMatch);
                    set3.add(bestMatch);
                }
            }
        }
        final ArrayList<String> list = new ArrayList<String>();
        final Iterator jarEntries2 = jarFile2.getJarEntries();
        if (jarEntries2 != null) {
            while (jarEntries2.hasNext()) {
                final String name2 = jarEntries2.next().getName();
                if (!set2.contains(name2) && !set3.contains(name2) && !set4.contains(name2)) {
                    if (JarDiff._debug) {
                        System.err.println("deleted.add " + name2);
                    }
                    list.add(name2);
                }
            }
        }
        if (JarDiff._debug) {
            final Iterator<String> iterator = hashMap.keySet().iterator();
            if (iterator != null) {
                System.out.println("MOVED MAP!!!");
                while (iterator.hasNext()) {
                    final String s3 = iterator.next();
                    System.out.println("key is " + s3 + " value is " + hashMap.get(s3));
                }
            }
            final Iterator<String> iterator2 = set2.iterator();
            if (iterator2 != null) {
                System.out.println("IMOVE MAP!!!");
                while (iterator2.hasNext()) {
                    System.out.println("key is " + iterator2.next());
                }
            }
        }
        final JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
        createIndex(jarOutputStream, list, hashMap);
        final Iterator<String> iterator3 = set4.iterator();
        if (iterator3 != null) {
            while (iterator3.hasNext()) {
                final String s4 = iterator3.next();
                if (JarDiff._debug) {
                    System.out.println("New File: " + s4);
                }
                writeEntry(jarOutputStream, jarFile3.getEntryByName(s4), jarFile3);
            }
        }
        jarOutputStream.finish();
    }
    
    private static void createIndex(final JarOutputStream jarOutputStream, final List list, final Map map) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        stringWriter.write("version 1.0");
        stringWriter.write("\r\n");
        for (int i = 0; i < list.size(); ++i) {
            final String s = list.get(i);
            stringWriter.write("remove");
            stringWriter.write(" ");
            writeEscapedString(stringWriter, s);
            stringWriter.write("\r\n");
        }
        final Iterator iterator = map.keySet().iterator();
        if (iterator != null) {
            while (iterator.hasNext()) {
                final String s2 = iterator.next();
                final String s3 = map.get(s2);
                stringWriter.write("move");
                stringWriter.write(" ");
                writeEscapedString(stringWriter, s3);
                stringWriter.write(" ");
                writeEscapedString(stringWriter, s2);
                stringWriter.write("\r\n");
            }
        }
        final JarEntry jarEntry = new JarEntry("META-INF/INDEX.JD");
        final byte[] bytes = stringWriter.toString().getBytes("UTF-8");
        stringWriter.close();
        jarOutputStream.putNextEntry(jarEntry);
        jarOutputStream.write(bytes, 0, bytes.length);
    }
    
    private static void writeEscapedString(final Writer writer, final String s) throws IOException {
        int index = 0;
        int n = 0;
        char[] charArray = null;
        while ((index = s.indexOf(32, index)) != -1) {
            if (n != index) {
                if (charArray == null) {
                    charArray = s.toCharArray();
                }
                writer.write(charArray, n, index - n);
            }
            n = index;
            ++index;
            writer.write(92);
        }
        if (n != 0) {
            writer.write(charArray, n, charArray.length - n);
        }
        else {
            writer.write(s);
        }
    }
    
    private static void writeEntry(final JarOutputStream jarOutputStream, final JarEntry jarEntry, final JarFile2 jarFile2) throws IOException {
        writeEntry(jarOutputStream, jarEntry, jarFile2.getJarFile().getInputStream(jarEntry));
    }
    
    private static void writeEntry(final JarOutputStream jarOutputStream, final JarEntry jarEntry, final InputStream inputStream) throws IOException {
        jarOutputStream.putNextEntry(jarEntry);
        for (int i = inputStream.read(JarDiff.newBytes); i != -1; i = inputStream.read(JarDiff.newBytes)) {
            jarOutputStream.write(JarDiff.newBytes, 0, i);
        }
        inputStream.close();
    }
    
    private static void showHelp() {
        System.out.println("JarDiff: [-nonminimal (for backward compatibility with 1.0.1/1.0] [-creatediff | -applydiff] [-output file] old.jar new.jar");
    }
    
    public static void main(final String[] array) throws IOException {
        boolean b = true;
        boolean b2 = true;
        String s = "out.jardiff";
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-nonminimal") || array[i].equals("-n")) {
                b2 = false;
            }
            else if (array[i].equals("-creatediff") || array[i].equals("-c")) {
                b = true;
            }
            else if (array[i].equals("-applydiff") || array[i].equals("-a")) {
                b = false;
            }
            else if (array[i].equals("-debug") || array[i].equals("-d")) {
                JarDiff._debug = true;
            }
            else if (array[i].equals("-output") || array[i].equals("-o")) {
                if (++i < array.length) {
                    s = array[i];
                }
            }
            else if (array[i].equals("-applydiff") || array[i].equals("-a")) {
                b = false;
            }
            else {
                if (i + 2 != array.length) {
                    showHelp();
                    System.exit(0);
                }
                if (b) {
                    try {
                        final FileOutputStream fileOutputStream = new FileOutputStream(s);
                        createPatch(array[i], array[i + 1], fileOutputStream, b2);
                        fileOutputStream.close();
                    }
                    catch (IOException ex) {
                        try {
                            System.out.println(getResources().getString("jardiff.error.create") + " " + ex);
                        }
                        catch (MissingResourceException ex3) {}
                    }
                }
                else {
                    try {
                        final FileOutputStream fileOutputStream2 = new FileOutputStream(s);
                        new JarDiffPatcher().applyPatch(null, array[i], array[i + 1], fileOutputStream2);
                        fileOutputStream2.close();
                    }
                    catch (IOException ex2) {
                        try {
                            System.out.println(getResources().getString("jardiff.error.apply") + " " + ex2);
                        }
                        catch (MissingResourceException ex4) {}
                    }
                }
                System.exit(0);
            }
        }
        showHelp();
    }
    
    static {
        JarDiff.newBytes = new byte[2048];
        JarDiff.oldBytes = new byte[2048];
        JarDiff._resources = null;
    }
    
    private static class JarFile2
    {
        private JarFile _jar;
        private List _entries;
        private HashMap _nameToEntryMap;
        private HashMap _crcToEntryMap;
        
        public JarFile2(final String s) throws IOException {
            this._jar = new JarFile(new File(s));
            this.index();
        }
        
        public JarFile getJarFile() {
            return this._jar;
        }
        
        public Iterator getJarEntries() {
            return this._entries.iterator();
        }
        
        public JarEntry getEntryByName(final String s) {
            return this._nameToEntryMap.get(s);
        }
        
        private static boolean differs(final InputStream inputStream, final InputStream inputStream2) throws IOException {
            int i = 0;
            int n = 0;
            while (i != -1) {
                i = inputStream2.read(JarDiff.newBytes);
                final int read = inputStream.read(JarDiff.oldBytes);
                if (i != read) {
                    if (JarDiff._debug) {
                        System.out.println("\tread sizes differ: " + i + " " + read + " total " + n);
                    }
                    return true;
                }
                if (i <= 0) {
                    continue;
                }
                while (--i >= 0) {
                    ++n;
                    if (JarDiff.newBytes[i] != JarDiff.oldBytes[i]) {
                        if (JarDiff._debug) {
                            System.out.println("\tbytes differ at " + n);
                        }
                        return true;
                    }
                }
                i = 0;
            }
            return false;
        }
        
        public String getBestMatch(final JarFile2 jarFile2, final JarEntry jarEntry) throws IOException {
            if (this.contains(jarFile2, jarEntry)) {
                return jarEntry.getName();
            }
            return this.hasSameContent(jarFile2, jarEntry);
        }
        
        public boolean contains(final JarFile2 jarFile2, final JarEntry jarEntry) throws IOException {
            final JarEntry entryByName = this.getEntryByName(jarEntry.getName());
            return entryByName != null && entryByName.getCrc() == jarEntry.getCrc() && !differs(this.getJarFile().getInputStream(entryByName), jarFile2.getJarFile().getInputStream(jarEntry));
        }
        
        public String hasSameContent(final JarFile2 jarFile2, final JarEntry jarEntry) throws IOException {
            final String s = null;
            final Long n = new Long(jarEntry.getCrc());
            if (this._crcToEntryMap.containsKey(n)) {
                final ListIterator<JarEntry> listIterator = this._crcToEntryMap.get(n).listIterator(0);
                if (listIterator != null) {
                    while (listIterator.hasNext()) {
                        final JarEntry jarEntry2 = listIterator.next();
                        if (!differs(this.getJarFile().getInputStream(jarEntry2), jarFile2.getJarFile().getInputStream(jarEntry))) {
                            return jarEntry2.getName();
                        }
                    }
                }
            }
            return s;
        }
        
        private void index() throws IOException {
            final Enumeration<JarEntry> entries = this._jar.entries();
            this._nameToEntryMap = new HashMap();
            this._crcToEntryMap = new HashMap();
            this._entries = new ArrayList();
            if (JarDiff._debug) {
                System.out.println("indexing: " + this._jar.getName());
            }
            if (entries != null) {
                while (entries.hasMoreElements()) {
                    final JarEntry jarEntry = entries.nextElement();
                    final long crc = jarEntry.getCrc();
                    final Long n = new Long(crc);
                    if (JarDiff._debug) {
                        System.out.println("\t" + jarEntry.getName() + " CRC " + crc);
                    }
                    this._nameToEntryMap.put(jarEntry.getName(), jarEntry);
                    this._entries.add(jarEntry);
                    if (this._crcToEntryMap.containsKey(n)) {
                        final LinkedList<JarEntry> list = this._crcToEntryMap.get(n);
                        list.add(jarEntry);
                        this._crcToEntryMap.put(n, list);
                    }
                    else {
                        final LinkedList<JarEntry> list2 = new LinkedList<JarEntry>();
                        list2.add(jarEntry);
                        this._crcToEntryMap.put(n, list2);
                    }
                }
            }
        }
    }
}
