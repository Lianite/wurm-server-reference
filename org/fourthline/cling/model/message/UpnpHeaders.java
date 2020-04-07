// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.LinkedHashMap;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.fourthline.cling.model.message.header.UpnpHeader;
import java.util.Map;
import java.util.logging.Logger;
import org.seamless.http.Headers;

public class UpnpHeaders extends Headers
{
    private static final Logger log;
    protected Map<UpnpHeader.Type, List<UpnpHeader>> parsedHeaders;
    
    public UpnpHeaders() {
    }
    
    public UpnpHeaders(final Map<String, List<String>> headers) {
        super(headers);
    }
    
    public UpnpHeaders(final ByteArrayInputStream inputStream) {
        super(inputStream);
    }
    
    public UpnpHeaders(final boolean normalizeHeaders) {
        super(normalizeHeaders);
    }
    
    protected void parseHeaders() {
        this.parsedHeaders = new LinkedHashMap<UpnpHeader.Type, List<UpnpHeader>>();
        if (UpnpHeaders.log.isLoggable(Level.FINE)) {
            UpnpHeaders.log.fine("Parsing all HTTP headers for known UPnP headers: " + this.size());
        }
        for (final Map.Entry<String, List<String>> entry : this.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            final UpnpHeader.Type type = UpnpHeader.Type.getByHttpName(entry.getKey());
            if (type == null) {
                if (!UpnpHeaders.log.isLoggable(Level.FINE)) {
                    continue;
                }
                UpnpHeaders.log.fine("Ignoring non-UPNP HTTP header: " + entry.getKey());
            }
            else {
                for (final String value : entry.getValue()) {
                    final UpnpHeader upnpHeader = UpnpHeader.newInstance(type, value);
                    if (upnpHeader == null || upnpHeader.getValue() == null) {
                        if (!UpnpHeaders.log.isLoggable(Level.FINE)) {
                            continue;
                        }
                        UpnpHeaders.log.fine("Ignoring known but irrelevant header (value violates the UDA specification?) '" + type.getHttpName() + "': " + value);
                    }
                    else {
                        this.addParsedValue(type, upnpHeader);
                    }
                }
            }
        }
    }
    
    protected void addParsedValue(final UpnpHeader.Type type, final UpnpHeader value) {
        if (UpnpHeaders.log.isLoggable(Level.FINE)) {
            UpnpHeaders.log.fine("Adding parsed header: " + value);
        }
        List<UpnpHeader> list = this.parsedHeaders.get(type);
        if (list == null) {
            list = new LinkedList<UpnpHeader>();
            this.parsedHeaders.put(type, list);
        }
        list.add(value);
    }
    
    @Override
    public List<String> put(final String key, final List<String> values) {
        this.parsedHeaders = null;
        return super.put(key, values);
    }
    
    @Override
    public void add(final String key, final String value) {
        this.parsedHeaders = null;
        super.add(key, value);
    }
    
    @Override
    public List<String> remove(final Object key) {
        this.parsedHeaders = null;
        return super.remove(key);
    }
    
    @Override
    public void clear() {
        this.parsedHeaders = null;
        super.clear();
    }
    
    public boolean containsKey(final UpnpHeader.Type type) {
        if (this.parsedHeaders == null) {
            this.parseHeaders();
        }
        return this.parsedHeaders.containsKey(type);
    }
    
    public List<UpnpHeader> get(final UpnpHeader.Type type) {
        if (this.parsedHeaders == null) {
            this.parseHeaders();
        }
        return this.parsedHeaders.get(type);
    }
    
    public void add(final UpnpHeader.Type type, final UpnpHeader value) {
        super.add(type.getHttpName(), value.getString());
        if (this.parsedHeaders != null) {
            this.addParsedValue(type, value);
        }
    }
    
    public void remove(final UpnpHeader.Type type) {
        super.remove((Object)type.getHttpName());
        if (this.parsedHeaders != null) {
            this.parsedHeaders.remove(type);
        }
    }
    
    public UpnpHeader[] getAsArray(final UpnpHeader.Type type) {
        if (this.parsedHeaders == null) {
            this.parseHeaders();
        }
        return (this.parsedHeaders.get(type) != null) ? this.parsedHeaders.get(type).toArray(new UpnpHeader[this.parsedHeaders.get(type).size()]) : new UpnpHeader[0];
    }
    
    public UpnpHeader getFirstHeader(final UpnpHeader.Type type) {
        return (this.getAsArray(type).length > 0) ? this.getAsArray(type)[0] : null;
    }
    
    public <H extends UpnpHeader> H getFirstHeader(final UpnpHeader.Type type, final Class<H> subtype) {
        final UpnpHeader[] headers = this.getAsArray(type);
        if (headers.length == 0) {
            return null;
        }
        for (final UpnpHeader header : headers) {
            if (subtype.isAssignableFrom(header.getClass())) {
                return (H)header;
            }
        }
        return null;
    }
    
    public String getFirstHeaderString(final UpnpHeader.Type type) {
        final UpnpHeader header = this.getFirstHeader(type);
        return (header != null) ? header.getString() : null;
    }
    
    public void log() {
        if (UpnpHeaders.log.isLoggable(Level.FINE)) {
            UpnpHeaders.log.fine("############################ RAW HEADERS ###########################");
            for (final Map.Entry<String, List<String>> entry : this.entrySet()) {
                UpnpHeaders.log.fine("=== NAME : " + entry.getKey());
                for (final String v : entry.getValue()) {
                    UpnpHeaders.log.fine("VALUE: " + v);
                }
            }
            if (this.parsedHeaders != null && this.parsedHeaders.size() > 0) {
                UpnpHeaders.log.fine("########################## PARSED HEADERS ##########################");
                for (final Map.Entry<UpnpHeader.Type, List<UpnpHeader>> entry2 : this.parsedHeaders.entrySet()) {
                    UpnpHeaders.log.fine("=== TYPE: " + entry2.getKey());
                    for (final UpnpHeader upnpHeader : entry2.getValue()) {
                        UpnpHeaders.log.fine("HEADER: " + upnpHeader);
                    }
                }
            }
            UpnpHeaders.log.fine("####################################################################");
        }
    }
    
    static {
        log = Logger.getLogger(UpnpHeaders.class.getName());
    }
}
