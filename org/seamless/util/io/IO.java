// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.io;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.CharArrayWriter;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.util.List;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

public class IO
{
    public static final String LINE_SEPARATOR;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    public static String makeRelativePath(final String path, final String base) {
        String p = "";
        if (path != null && path.length() > 0) {
            if (path.startsWith("/")) {
                if (path.startsWith(base)) {
                    p = path.substring(base.length());
                }
                else {
                    p = base + path;
                }
            }
            else {
                p = (path.endsWith("/") ? path.substring(0, path.length() - 1) : path);
            }
            if (p.startsWith("/")) {
                p = p.substring(1);
            }
        }
        return p;
    }
    
    public static void recursiveRename(final File dir, final String from, final String to) {
        final File[] arr$;
        final File[] subfiles = arr$ = dir.listFiles();
        for (final File file : arr$) {
            if (file.isDirectory()) {
                recursiveRename(file, from, to);
                file.renameTo(new File(dir, file.getName().replace(from, to)));
            }
            else {
                file.renameTo(new File(dir, file.getName().replace(from, to)));
            }
        }
    }
    
    public static void findFiles(final File file, final FileFinder finder) {
        finder.found(file);
        final File[] children = file.listFiles();
        if (children != null) {
            for (final File child : children) {
                findFiles(child, finder);
            }
        }
    }
    
    public static boolean deleteFile(final File path) {
        if (path.exists()) {
            final File[] files = path.listFiles();
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteFile(file);
                    }
                    else {
                        file.delete();
                    }
                }
            }
        }
        return path.delete();
    }
    
    public static void copyFile(final File sourceFile, final File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0L, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    
    public static byte[] readBytes(final InputStream is) throws IOException {
        return toByteArray(is);
    }
    
    public static byte[] readBytes(final File file) throws IOException {
        final InputStream is = new FileInputStream(file);
        try {
            return readBytes(is);
        }
        finally {
            is.close();
        }
    }
    
    public static void writeBytes(final OutputStream outputStream, final byte[] data) throws IOException {
        write(data, outputStream);
    }
    
    public static void writeBytes(final File file, final byte[] data) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + file);
        }
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + file);
        }
        final OutputStream os = new FileOutputStream(file);
        try {
            writeBytes(os, data);
            os.flush();
        }
        finally {
            os.close();
        }
    }
    
    public static void writeUTF8(final OutputStream outputStream, final String data) throws IOException {
        write(data, outputStream, "UTF-8");
    }
    
    public static void writeUTF8(final File file, final String contents) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist: " + file);
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + file);
        }
        if (!file.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + file);
        }
        final OutputStream os = new FileOutputStream(file);
        try {
            writeUTF8(os, contents);
            os.flush();
        }
        finally {
            os.close();
        }
    }
    
    public static String readLines(final InputStream is) throws IOException {
        if (is == null) {
            throw new IllegalArgumentException("Inputstream was null");
        }
        final BufferedReader inputReader = new BufferedReader(new InputStreamReader(is));
        final StringBuilder input = new StringBuilder();
        String inputLine;
        while ((inputLine = inputReader.readLine()) != null) {
            input.append(inputLine).append(System.getProperty("line.separator"));
        }
        return (input.length() > 0) ? input.toString() : "";
    }
    
    public static String readLines(final File file) throws IOException {
        final InputStream is = new FileInputStream(file);
        try {
            return readLines(is);
        }
        finally {
            is.close();
        }
    }
    
    public static String[] readLines(final File file, final boolean trimLines) throws IOException {
        return readLines(file, trimLines, null);
    }
    
    public static String[] readLines(final File file, final boolean trimLines, final Character commentChar) throws IOException {
        return readLines(file, trimLines, commentChar, false);
    }
    
    public static String[] readLines(final File file, final boolean trimLines, final Character commentChar, final boolean skipEmptyLines) throws IOException {
        final List<String> contents = new ArrayList<String>();
        final BufferedReader input = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (commentChar != null && line.matches("^\\s*" + commentChar + ".*")) {
                    continue;
                }
                final String l = trimLines ? line.trim() : line;
                if (skipEmptyLines && l.length() == 0) {
                    continue;
                }
                contents.add(l);
            }
        }
        finally {
            input.close();
        }
        return contents.toArray(new String[contents.size()]);
    }
    
    public static void closeQuietly(final Reader input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException ex) {}
    }
    
    public static void closeQuietly(final Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException ex) {}
    }
    
    public static void closeQuietly(final InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        }
        catch (IOException ex) {}
    }
    
    public static void closeQuietly(final OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        }
        catch (IOException ex) {}
    }
    
    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }
    
    public static byte[] toByteArray(final Reader input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }
    
    public static byte[] toByteArray(final Reader input, final String encoding) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }
    
    public static byte[] toByteArray(final String input) throws IOException {
        return input.getBytes();
    }
    
    public static char[] toCharArray(final InputStream is) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        copy(is, output);
        return output.toCharArray();
    }
    
    public static char[] toCharArray(final InputStream is, final String encoding) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }
    
    public static char[] toCharArray(final Reader input) throws IOException {
        final CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }
    
    public static String toString(final InputStream input) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }
    
    public static String toString(final InputStream input, final String encoding) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }
    
    public static String toString(final Reader input) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }
    
    public static String toString(final byte[] input) throws IOException {
        return new String(input);
    }
    
    public static String toString(final byte[] input, final String encoding) throws IOException {
        if (encoding == null) {
            return new String(input);
        }
        return new String(input, encoding);
    }
    
    public static InputStream toInputStream(final String input) {
        final byte[] bytes = input.getBytes();
        return new ByteArrayInputStream(bytes);
    }
    
    public static InputStream toInputStream(final String input, final String encoding) throws IOException {
        final byte[] bytes = (encoding != null) ? input.getBytes(encoding) : input.getBytes();
        return new ByteArrayInputStream(bytes);
    }
    
    public static void write(final byte[] data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
    
    public static void write(final byte[] data, final Writer output) throws IOException {
        if (data != null) {
            output.write(new String(data));
        }
    }
    
    public static void write(final byte[] data, final Writer output, final String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            }
            else {
                output.write(new String(data, encoding));
            }
        }
    }
    
    public static void write(final char[] data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
    
    public static void write(final char[] data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes());
        }
    }
    
    public static void write(final char[] data, final OutputStream output, final String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            }
            else {
                output.write(new String(data).getBytes(encoding));
            }
        }
    }
    
    public static void write(final String data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }
    
    public static void write(final String data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(data.getBytes());
        }
    }
    
    public static void write(final String data, final OutputStream output, final String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            }
            else {
                output.write(data.getBytes(encoding));
            }
        }
    }
    
    public static void write(final StringBuffer data, final Writer output) throws IOException {
        if (data != null) {
            output.write(data.toString());
        }
    }
    
    public static void write(final StringBuffer data, final OutputStream output) throws IOException {
        if (data != null) {
            output.write(data.toString().getBytes());
        }
    }
    
    public static void write(final StringBuffer data, final OutputStream output, final String encoding) throws IOException {
        if (data != null) {
            if (encoding == null) {
                write(data, output);
            }
            else {
                output.write(data.toString().getBytes(encoding));
            }
        }
    }
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int)count;
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static void copy(final InputStream input, final Writer output) throws IOException {
        final InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }
    
    public static void copy(final InputStream input, final Writer output, final String encoding) throws IOException {
        if (encoding == null) {
            copy(input, output);
        }
        else {
            final InputStreamReader in = new InputStreamReader(input, encoding);
            copy(in, output);
        }
    }
    
    public static int copy(final Reader input, final Writer output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > 2147483647L) {
            return -1;
        }
        return (int)count;
    }
    
    public static long copyLarge(final Reader input, final Writer output) throws IOException {
        final char[] buffer = new char[4096];
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
    
    public static void copy(final Reader input, final OutputStream output) throws IOException {
        final OutputStreamWriter out = new OutputStreamWriter(output);
        copy(input, out);
        out.flush();
    }
    
    public static void copy(final Reader input, final OutputStream output, final String encoding) throws IOException {
        if (encoding == null) {
            copy(input, output);
        }
        else {
            final OutputStreamWriter out = new OutputStreamWriter(output, encoding);
            copy(input, out);
            out.flush();
        }
    }
    
    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = input2.read();
        return ch2 == -1;
    }
    
    public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
        if (!(input1 instanceof BufferedReader)) {
            input1 = new BufferedReader(input1);
        }
        if (!(input2 instanceof BufferedReader)) {
            input2 = new BufferedReader(input2);
        }
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = input2.read();
        return ch2 == -1;
    }
    
    static {
        final StringWriter buf = new StringWriter(4);
        final PrintWriter out = new PrintWriter(buf);
        out.println();
        LINE_SEPARATOR = buf.toString();
    }
    
    public interface FileFinder
    {
        void found(final File p0);
    }
}
