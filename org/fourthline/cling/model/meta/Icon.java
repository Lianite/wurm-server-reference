// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.meta;

import java.net.MalformedURLException;
import java.util.ArrayList;
import org.fourthline.cling.model.ValidationError;
import java.util.List;
import org.fourthline.cling.model.types.BinHexDatatype;
import java.io.InputStream;
import org.seamless.util.io.IO;
import java.io.IOException;
import java.io.File;
import org.seamless.util.URIUtil;
import java.net.URL;
import java.net.URI;
import org.seamless.util.MimeType;
import java.util.logging.Logger;
import org.fourthline.cling.model.Validatable;

public class Icon implements Validatable
{
    private static final Logger log;
    private final MimeType mimeType;
    private final int width;
    private final int height;
    private final int depth;
    private final URI uri;
    private final byte[] data;
    private Device device;
    
    public Icon(final String mimeType, final int width, final int height, final int depth, final URI uri) {
        this((mimeType != null && mimeType.length() > 0) ? MimeType.valueOf(mimeType) : null, width, height, depth, uri, null);
    }
    
    public Icon(final String mimeType, final int width, final int height, final int depth, final URL url) throws IOException {
        this(mimeType, width, height, depth, new File(URIUtil.toURI(url)));
    }
    
    public Icon(final String mimeType, final int width, final int height, final int depth, final File file) throws IOException {
        this(mimeType, width, height, depth, file.getName(), IO.readBytes(file));
    }
    
    public Icon(final String mimeType, final int width, final int height, final int depth, final String uniqueName, final InputStream is) throws IOException {
        this(mimeType, width, height, depth, uniqueName, IO.readBytes(is));
    }
    
    public Icon(final String mimeType, final int width, final int height, final int depth, final String uniqueName, final byte[] data) {
        this((mimeType != null && mimeType.length() > 0) ? MimeType.valueOf(mimeType) : null, width, height, depth, URI.create(uniqueName), data);
    }
    
    public Icon(final String mimeType, final int width, final int height, final int depth, final String uniqueName, final String binHexEncoded) {
        this(mimeType, width, height, depth, uniqueName, (byte[])((binHexEncoded != null && !binHexEncoded.equals("")) ? new BinHexDatatype().valueOf(binHexEncoded) : null));
    }
    
    protected Icon(final MimeType mimeType, final int width, final int height, final int depth, final URI uri, final byte[] data) {
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.uri = uri;
        this.data = data;
    }
    
    public MimeType getMimeType() {
        return this.mimeType;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getDepth() {
        return this.depth;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public Device getDevice() {
        return this.device;
    }
    
    void setDevice(final Device device) {
        if (this.device != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.device = device;
    }
    
    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<ValidationError>();
        if (this.getMimeType() == null) {
            Icon.log.warning("UPnP specification violation of: " + this.getDevice());
            Icon.log.warning("Invalid icon, missing mime type: " + this);
        }
        if (this.getWidth() == 0) {
            Icon.log.warning("UPnP specification violation of: " + this.getDevice());
            Icon.log.warning("Invalid icon, missing width: " + this);
        }
        if (this.getHeight() == 0) {
            Icon.log.warning("UPnP specification violation of: " + this.getDevice());
            Icon.log.warning("Invalid icon, missing height: " + this);
        }
        if (this.getDepth() == 0) {
            Icon.log.warning("UPnP specification violation of: " + this.getDevice());
            Icon.log.warning("Invalid icon, missing bitmap depth: " + this);
        }
        if (this.getUri() == null) {
            errors.add(new ValidationError(this.getClass(), "uri", "URL is required"));
        }
        else {
            try {
                final URL testURI = this.getUri().toURL();
                if (testURI == null) {
                    throw new MalformedURLException();
                }
            }
            catch (MalformedURLException ex) {
                errors.add(new ValidationError(this.getClass(), "uri", "URL must be valid: " + ex.getMessage()));
            }
            catch (IllegalArgumentException ex2) {}
        }
        return errors;
    }
    
    public Icon deepCopy() {
        return new Icon(this.getMimeType(), this.getWidth(), this.getHeight(), this.getDepth(), this.getUri(), this.getData());
    }
    
    @Override
    public String toString() {
        return "Icon(" + this.getWidth() + "x" + this.getHeight() + ", MIME: " + this.getMimeType() + ") " + this.getUri();
    }
    
    static {
        log = Logger.getLogger(StateVariable.class.getName());
    }
}
