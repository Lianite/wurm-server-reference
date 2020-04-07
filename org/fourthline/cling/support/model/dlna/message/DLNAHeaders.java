// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.LinkedHashMap;
import java.io.ByteArrayInputStream;
import org.fourthline.cling.model.message.header.UpnpHeader;
import java.util.List;
import org.fourthline.cling.support.model.dlna.message.header.DLNAHeader;
import java.util.Map;
import java.util.logging.Logger;
import org.fourthline.cling.model.message.UpnpHeaders;

public class DLNAHeaders extends UpnpHeaders
{
    private static final Logger log;
    protected Map<DLNAHeader.Type, List<UpnpHeader>> parsedDLNAHeaders;
    
    public DLNAHeaders() {
    }
    
    public DLNAHeaders(final Map<String, List<String>> headers) {
        super(headers);
    }
    
    public DLNAHeaders(final ByteArrayInputStream inputStream) {
        super(inputStream);
    }
    
    @Override
    protected void parseHeaders() {
        if (this.parsedHeaders == null) {
            super.parseHeaders();
        }
        this.parsedDLNAHeaders = new LinkedHashMap<DLNAHeader.Type, List<UpnpHeader>>();
        DLNAHeaders.log.log(Level.FINE, "Parsing all HTTP headers for known UPnP headers: {0}", this.size());
        for (final Map.Entry<String, List<String>> entry : this.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            final DLNAHeader.Type type = DLNAHeader.Type.getByHttpName(entry.getKey());
            if (type == null) {
                DLNAHeaders.log.log(Level.FINE, "Ignoring non-UPNP HTTP header: {0}", entry.getKey());
            }
            else {
                for (final String value : entry.getValue()) {
                    final UpnpHeader upnpHeader = DLNAHeader.newInstance(type, value);
                    if (upnpHeader == null || upnpHeader.getValue() == null) {
                        DLNAHeaders.log.log(Level.FINE, "Ignoring known but non-parsable header (value violates the UDA specification?) '{0}': {1}", new Object[] { type.getHttpName(), value });
                    }
                    else {
                        this.addParsedValue(type, upnpHeader);
                    }
                }
            }
        }
    }
    
    protected void addParsedValue(final DLNAHeader.Type type, final UpnpHeader value) {
        DLNAHeaders.log.log(Level.FINE, "Adding parsed header: {0}", value);
        List<UpnpHeader> list = this.parsedDLNAHeaders.get(type);
        if (list == null) {
            list = new LinkedList<UpnpHeader>();
            this.parsedDLNAHeaders.put(type, list);
        }
        list.add(value);
    }
    
    @Override
    public List<String> put(final String key, final List<String> values) {
        this.parsedDLNAHeaders = null;
        return super.put(key, values);
    }
    
    @Override
    public void add(final String key, final String value) {
        this.parsedDLNAHeaders = null;
        super.add(key, value);
    }
    
    @Override
    public List<String> remove(final Object key) {
        this.parsedDLNAHeaders = null;
        return super.remove(key);
    }
    
    @Override
    public void clear() {
        this.parsedDLNAHeaders = null;
        super.clear();
    }
    
    public boolean containsKey(final DLNAHeader.Type type) {
        if (this.parsedDLNAHeaders == null) {
            this.parseHeaders();
        }
        return this.parsedDLNAHeaders.containsKey(type);
    }
    
    public List<UpnpHeader> get(final DLNAHeader.Type type) {
        if (this.parsedDLNAHeaders == null) {
            this.parseHeaders();
        }
        return this.parsedDLNAHeaders.get(type);
    }
    
    public void add(final DLNAHeader.Type type, final UpnpHeader value) {
        super.add(type.getHttpName(), value.getString());
        if (this.parsedDLNAHeaders != null) {
            this.addParsedValue(type, value);
        }
    }
    
    public void remove(final DLNAHeader.Type type) {
        super.remove(type.getHttpName());
        if (this.parsedDLNAHeaders != null) {
            this.parsedDLNAHeaders.remove(type);
        }
    }
    
    public UpnpHeader[] getAsArray(final DLNAHeader.Type type) {
        if (this.parsedDLNAHeaders == null) {
            this.parseHeaders();
        }
        return (this.parsedDLNAHeaders.get(type) != null) ? this.parsedDLNAHeaders.get(type).toArray(new UpnpHeader[this.parsedDLNAHeaders.get(type).size()]) : new UpnpHeader[0];
    }
    
    public UpnpHeader getFirstHeader(final DLNAHeader.Type type) {
        return (this.getAsArray(type).length > 0) ? this.getAsArray(type)[0] : null;
    }
    
    public <H extends UpnpHeader> H getFirstHeader(final DLNAHeader.Type type, final Class<H> subtype) {
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
    
    @Override
    public void log() {
        if (DLNAHeaders.log.isLoggable(Level.FINE)) {
            super.log();
            if (this.parsedDLNAHeaders != null && this.parsedDLNAHeaders.size() > 0) {
                DLNAHeaders.log.fine("########################## PARSED DLNA HEADERS ##########################");
                for (final Map.Entry<DLNAHeader.Type, List<UpnpHeader>> entry : this.parsedDLNAHeaders.entrySet()) {
                    DLNAHeaders.log.log(Level.FINE, "=== TYPE: {0}", entry.getKey());
                    for (final UpnpHeader upnpHeader : entry.getValue()) {
                        DLNAHeaders.log.log(Level.FINE, "HEADER: {0}", upnpHeader);
                    }
                }
            }
            DLNAHeaders.log.fine("####################################################################");
        }
    }
    
    static {
        log = Logger.getLogger(DLNAHeaders.class.getName());
    }
}
