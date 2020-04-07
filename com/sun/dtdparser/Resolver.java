// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.dtdparser;

import java.io.FileInputStream;
import java.io.File;
import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.util.Hashtable;
import org.xml.sax.EntityResolver;

public class Resolver implements EntityResolver
{
    private boolean ignoringMIME;
    private Hashtable id2uri;
    private Hashtable id2resource;
    private Hashtable id2loader;
    private static final String[] types;
    
    public static InputSource createInputSource(String contentType, final InputStream stream, final boolean checkType, final String scheme) throws IOException {
        String charset = null;
        if (contentType != null) {
            contentType = contentType.toLowerCase();
            int index = contentType.indexOf(59);
            if (index != -1) {
                String attributes = contentType.substring(index + 1);
                contentType = contentType.substring(0, index);
                index = attributes.indexOf("charset");
                if (index != -1) {
                    attributes = attributes.substring(index + 7);
                    if ((index = attributes.indexOf(59)) != -1) {
                        attributes = attributes.substring(0, index);
                    }
                    if ((index = attributes.indexOf(61)) != -1) {
                        attributes = attributes.substring(index + 1);
                        if ((index = attributes.indexOf(40)) != -1) {
                            attributes = attributes.substring(0, index);
                        }
                        if ((index = attributes.indexOf(34)) != -1) {
                            attributes = attributes.substring(index + 1);
                            attributes = attributes.substring(0, attributes.indexOf(34));
                        }
                        charset = attributes.trim();
                    }
                }
            }
            if (checkType) {
                boolean isOK = false;
                for (int i = 0; i < Resolver.types.length; ++i) {
                    if (Resolver.types[i].equals(contentType)) {
                        isOK = true;
                        break;
                    }
                }
                if (!isOK) {
                    throw new IOException("Not XML: " + contentType);
                }
            }
            if (charset == null) {
                contentType = contentType.trim();
                if (contentType.startsWith("text/") && !"file".equalsIgnoreCase(scheme)) {
                    charset = "US-ASCII";
                }
            }
        }
        final InputSource retval = new InputSource(XmlReader.createReader(stream, charset));
        retval.setByteStream(stream);
        retval.setEncoding(charset);
        return retval;
    }
    
    public static InputSource createInputSource(final URL uri, final boolean checkType) throws IOException {
        final URLConnection conn = uri.openConnection();
        InputSource retval;
        if (checkType) {
            final String contentType = conn.getContentType();
            retval = createInputSource(contentType, conn.getInputStream(), false, uri.getProtocol());
        }
        else {
            retval = new InputSource(XmlReader.createReader(conn.getInputStream()));
        }
        retval.setSystemId(conn.getURL().toString());
        return retval;
    }
    
    public static InputSource createInputSource(final File file) throws IOException {
        final InputSource retval = new InputSource(XmlReader.createReader(new FileInputStream(file)));
        String path = file.getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/") && file.isDirectory()) {
            path += "/";
        }
        retval.setSystemId("file:" + path);
        return retval;
    }
    
    public InputSource resolveEntity(final String name, String uri) throws IOException {
        final String mappedURI = this.name2uri(name);
        final InputStream stream;
        InputSource retval;
        if (mappedURI == null && (stream = this.mapResource(name)) != null) {
            uri = "java:resource:" + this.id2resource.get(name);
            retval = new InputSource(XmlReader.createReader(stream));
        }
        else {
            if (mappedURI != null) {
                uri = mappedURI;
            }
            else if (uri == null) {
                return null;
            }
            final URL url = new URL(uri);
            final URLConnection conn = url.openConnection();
            uri = conn.getURL().toString();
            if (this.ignoringMIME) {
                retval = new InputSource(XmlReader.createReader(conn.getInputStream()));
            }
            else {
                final String contentType = conn.getContentType();
                retval = createInputSource(contentType, conn.getInputStream(), false, url.getProtocol());
            }
        }
        retval.setSystemId(uri);
        retval.setPublicId(name);
        return retval;
    }
    
    public boolean isIgnoringMIME() {
        return this.ignoringMIME;
    }
    
    public void setIgnoringMIME(final boolean value) {
        this.ignoringMIME = value;
    }
    
    private String name2uri(final String publicId) {
        if (publicId == null || this.id2uri == null) {
            return null;
        }
        return this.id2uri.get(publicId);
    }
    
    public void registerCatalogEntry(final String publicId, final String uri) {
        if (this.id2uri == null) {
            this.id2uri = new Hashtable(17);
        }
        this.id2uri.put(publicId, uri);
    }
    
    private InputStream mapResource(final String publicId) {
        if (publicId == null || this.id2resource == null) {
            return null;
        }
        final String resourceName = this.id2resource.get(publicId);
        ClassLoader loader = null;
        if (resourceName == null) {
            return null;
        }
        if (this.id2loader != null) {
            loader = this.id2loader.get(publicId);
        }
        if (loader == null) {
            return ClassLoader.getSystemResourceAsStream(resourceName);
        }
        return loader.getResourceAsStream(resourceName);
    }
    
    public void registerCatalogEntry(final String publicId, final String resourceName, final ClassLoader loader) {
        if (this.id2resource == null) {
            this.id2resource = new Hashtable(17);
        }
        this.id2resource.put(publicId, resourceName);
        if (loader != null) {
            if (this.id2loader == null) {
                this.id2loader = new Hashtable(17);
            }
            this.id2loader.put(publicId, loader);
        }
    }
    
    static {
        types = new String[] { "application/xml", "text/xml", "text/plain", "text/html", "application/x-netcdf", "content/unknown" };
    }
}
