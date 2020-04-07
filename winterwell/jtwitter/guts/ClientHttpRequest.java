// 
// Decompiled by Procyon v0.5.30
// 

package winterwell.jtwitter.guts;

import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.net.URL;
import java.util.HashMap;
import java.io.IOException;
import java.util.Random;
import java.util.Map;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Observable;

public class ClientHttpRequest extends Observable
{
    URLConnection connection;
    OutputStream os;
    Map<String, String> cookies;
    String rawCookies;
    private static Random random;
    String boundary;
    private boolean isCanceled;
    private int bytesSent;
    
    static {
        ClientHttpRequest.random = new Random();
    }
    
    protected void connect() throws IOException {
        if (this.os == null) {
            this.os = this.connection.getOutputStream();
        }
    }
    
    protected void write(final char c) throws IOException {
        this.connect();
        this.os.write(c);
    }
    
    protected void write(final String s) throws IOException {
        this.connect();
        this.os.write(s.getBytes());
    }
    
    protected long newlineNumBytes() {
        return 2L;
    }
    
    protected void newline() throws IOException {
        this.connect();
        this.write("\r\n");
    }
    
    protected void writeln(final String s) throws IOException {
        this.connect();
        this.write(s);
        this.newline();
    }
    
    protected static String randomString() {
        return Long.toString(ClientHttpRequest.random.nextLong(), 36);
    }
    
    private long boundaryNumBytes() {
        return this.boundary.length() + 2;
    }
    
    private void boundary() throws IOException {
        this.write("--");
        this.write(this.boundary);
    }
    
    public ClientHttpRequest(final URLConnection connection) throws IOException {
        this.os = null;
        this.cookies = new HashMap<String, String>();
        this.rawCookies = "";
        this.boundary = "---------------------------" + randomString() + randomString() + randomString();
        this.isCanceled = false;
        this.bytesSent = 0;
        (this.connection = connection).setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
    }
    
    public ClientHttpRequest(final URL url) throws IOException {
        this(url.openConnection());
    }
    
    public ClientHttpRequest(final String urlString) throws IOException {
        this(new URL(urlString));
    }
    
    private void postCookies() {
        final StringBuffer cookieList = new StringBuffer(this.rawCookies);
        for (final Map.Entry<String, String> cookie : this.cookies.entrySet()) {
            if (cookieList.length() > 0) {
                cookieList.append("; ");
            }
            cookieList.append(String.valueOf(cookie.getKey()) + "=" + cookie.getValue());
        }
        if (cookieList.length() > 0) {
            this.connection.setRequestProperty("Cookie", cookieList.toString());
        }
    }
    
    public void setCookies(final String rawCookies) throws IOException {
        this.rawCookies = ((rawCookies == null) ? "" : rawCookies);
        this.cookies.clear();
    }
    
    public void setCookie(final String name, final String value) throws IOException {
        this.cookies.put(name, value);
    }
    
    public void setCookies(final Map cookies) throws IOException {
        if (cookies != null) {
            this.cookies.putAll(cookies);
        }
    }
    
    public void setCookies(final String[] cookies) throws IOException {
        if (cookies != null) {
            for (int i = 0; i < cookies.length - 1; i += 2) {
                this.setCookie(cookies[i], cookies[i + 1]);
            }
        }
    }
    
    private long writeNameNumBytes(final String name) {
        return this.newlineNumBytes() + "Content-Disposition: form-data; name=\"".length() + name.getBytes().length + 1L;
    }
    
    private void writeName(final String name) throws IOException {
        this.newline();
        this.write("Content-Disposition: form-data; name=\"");
        this.write(name);
        this.write('\"');
    }
    
    public int getBytesSent() {
        return this.bytesSent;
    }
    
    public void cancel() {
        this.isCanceled = true;
    }
    
    private synchronized void pipe(final InputStream in, final OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        this.bytesSent = 0;
        this.isCanceled = false;
        synchronized (in) {
            int nread;
            while ((nread = in.read(buf, 0, buf.length)) >= 0) {
                out.write(buf, 0, nread);
                this.bytesSent += nread;
                if (this.isCanceled) {
                    throw new IOException("Canceled");
                }
                out.flush();
                this.setChanged();
                this.notifyObservers(this.bytesSent);
                this.clearChanged();
            }
        }
        out.flush();
        buf = null;
    }
    
    public void setParameter(final String name, final String value) throws IOException {
        this.boundary();
        this.writeName(name);
        this.newline();
        this.newline();
        this.writeln(value);
    }
    
    public void setParameter(final String name, final String filename, final InputStream is) throws IOException {
        this.boundary();
        this.writeName(name);
        this.write("; filename=\"");
        this.write(filename);
        this.write('\"');
        this.newline();
        this.write("Content-Type: ");
        String type = URLConnection.guessContentTypeFromName(filename);
        if (type == null) {
            type = "application/octet-stream";
        }
        this.writeln(type);
        this.newline();
        this.pipe(is, this.os);
        this.newline();
    }
    
    public long getFilePostSize(final String name, final File file) {
        final String filename = file.getPath();
        String type = URLConnection.guessContentTypeFromName(filename);
        if (type == null) {
            type = "application/octet-stream";
        }
        return this.boundaryNumBytes() + this.writeNameNumBytes(name) + "; filename=\"".length() + filename.getBytes().length + 1L + this.newlineNumBytes() + "Content-Type: ".length() + type.length() + this.newlineNumBytes() + this.newlineNumBytes() + file.length() + this.newlineNumBytes();
    }
    
    public void setParameter(final String name, final File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            this.setParameter(name, file.getPath(), fis);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        if (fis != null) {
            fis.close();
        }
    }
    
    public void setParameter(final Object name, final Object object) throws IOException {
        if (object instanceof File) {
            this.setParameter(name.toString(), (File)object);
        }
        else {
            this.setParameter(name.toString(), object.toString());
        }
    }
    
    public void setParameters(final Map parameters) throws IOException {
        if (parameters != null) {
            for (final Map.Entry entry : parameters.entrySet()) {
                this.setParameter(entry.getKey().toString(), entry.getValue());
            }
        }
    }
    
    public void setParameters(final Object... parameters) throws IOException {
        for (int i = 0; i < parameters.length - 1; i += 2) {
            this.setParameter(parameters[i].toString(), parameters[i + 1]);
        }
    }
    
    public long getPostFooterSize() {
        return this.boundaryNumBytes() + 2L + this.newlineNumBytes() + this.newlineNumBytes();
    }
    
    private InputStream doPost() throws IOException {
        this.boundary();
        this.writeln("--");
        this.os.close();
        return this.connection.getInputStream();
    }
    
    public InputStream post() throws IOException {
        this.postCookies();
        return this.doPost();
    }
    
    public InputStream post(final Map parameters) throws IOException {
        this.postCookies();
        this.setParameters(parameters);
        return this.doPost();
    }
    
    public InputStream post(final Object... parameters) throws IOException {
        this.postCookies();
        this.setParameters(parameters);
        return this.doPost();
    }
    
    public InputStream post(final Map cookies, final Map parameters) throws IOException {
        this.setCookies(cookies);
        this.postCookies();
        this.setParameters(parameters);
        return this.doPost();
    }
    
    public InputStream post(final String raw_cookies, final Map parameters) throws IOException {
        this.setCookies(raw_cookies);
        this.postCookies();
        this.setParameters(parameters);
        return this.doPost();
    }
    
    public InputStream post(final String[] cookies, final Object[] parameters) throws IOException {
        this.setCookies(cookies);
        this.postCookies();
        this.setParameters(parameters);
        return this.doPost();
    }
    
    public static InputStream post(final URL url, final Map parameters) throws IOException {
        return new ClientHttpRequest(url).post(parameters);
    }
    
    public static InputStream post(final URL url, final Object[] parameters) throws IOException {
        return new ClientHttpRequest(url).post(parameters);
    }
    
    public static InputStream post(final URL url, final Map cookies, final Map parameters) throws IOException {
        return new ClientHttpRequest(url).post(cookies, parameters);
    }
    
    public static InputStream post(final URL url, final String[] cookies, final Object[] parameters) throws IOException {
        return new ClientHttpRequest(url).post(cookies, parameters);
    }
}
