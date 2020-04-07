// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import org.xml.sax.SAXException;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.bind.marshaller.NamespaceSupport;

public class NamespaceContextImpl implements NamespaceContext2
{
    private int iota;
    private final NamespaceSupport nss;
    private boolean inCollectingMode;
    private final NamespacePrefixMapper prefixMapper;
    private final Map decls;
    private final Map reverseDecls;
    
    public NamespaceContextImpl(final NamespacePrefixMapper _prefixMapper) {
        this.iota = 1;
        this.nss = new NamespaceSupport();
        this.decls = new HashMap();
        this.reverseDecls = new HashMap();
        this.prefixMapper = _prefixMapper;
        this.nss.declarePrefix("", "");
        this.nss.declarePrefix("xmlns", "http://www.w3.org/2000/xmlns/");
    }
    
    public final NamespacePrefixMapper getNamespacePrefixMapper() {
        return this.prefixMapper;
    }
    
    public String declareNamespace(String namespaceUri, final String preferedPrefix, final boolean requirePrefix) {
        if (!this.inCollectingMode) {
            if (!requirePrefix && this.nss.getURI("").equals(namespaceUri)) {
                return "";
            }
            if (requirePrefix) {
                return this.nss.getPrefix2(namespaceUri);
            }
            return this.nss.getPrefix(namespaceUri);
        }
        else {
            if (requirePrefix && namespaceUri.length() == 0) {
                return "";
            }
            String prefix = this.reverseDecls.get(namespaceUri);
            if (prefix != null) {
                if (!requirePrefix || prefix.length() != 0) {
                    return prefix;
                }
                this.decls.remove(prefix);
                this.reverseDecls.remove(namespaceUri);
            }
            if (namespaceUri.length() == 0) {
                prefix = "";
            }
            else {
                prefix = this.nss.getPrefix(namespaceUri);
                if (prefix == null) {
                    prefix = this.reverseDecls.get(namespaceUri);
                }
                if (prefix == null) {
                    if (this.prefixMapper != null) {
                        prefix = this.prefixMapper.getPreferredPrefix(namespaceUri, preferedPrefix, requirePrefix);
                    }
                    else {
                        prefix = preferedPrefix;
                    }
                    if (prefix == null) {
                        prefix = "ns" + this.iota++;
                    }
                }
            }
            if (requirePrefix && prefix.length() == 0) {
                prefix = "ns" + this.iota++;
            }
            while (true) {
                final String existingUri = this.decls.get(prefix);
                if (existingUri == null) {
                    break;
                }
                if (existingUri.length() != 0) {
                    this.decls.put(prefix, namespaceUri);
                    this.reverseDecls.put(namespaceUri, prefix);
                    namespaceUri = existingUri;
                }
                prefix = "ns" + this.iota++;
            }
            this.decls.put(prefix, namespaceUri);
            this.reverseDecls.put(namespaceUri, prefix);
            return prefix;
        }
    }
    
    public String getPrefix(final String namespaceUri) {
        return this.declareNamespace(namespaceUri, null, false);
    }
    
    public String getNamespaceURI(final String prefix) {
        final String uri = this.decls.get(prefix);
        if (uri != null) {
            return uri;
        }
        return this.nss.getURI(prefix);
    }
    
    public Iterator getPrefixes(final String namespaceUri) {
        final Set s = new HashSet();
        final String prefix = this.reverseDecls.get(namespaceUri);
        if (prefix != null) {
            s.add(prefix);
        }
        if (this.nss.getURI("").equals(namespaceUri)) {
            s.add("");
        }
        final Enumeration e = this.nss.getPrefixes(namespaceUri);
        while (e.hasMoreElements()) {
            s.add(e.nextElement());
        }
        return s.iterator();
    }
    
    public void startElement() {
        this.nss.pushContext();
        this.inCollectingMode = true;
    }
    
    public void endNamespaceDecls() {
        if (!this.decls.isEmpty()) {
            for (final Map.Entry e : this.decls.entrySet()) {
                final String prefix = e.getKey();
                final String uri = e.getValue();
                if (!uri.equals(this.nss.getURI(prefix))) {
                    this.nss.declarePrefix(prefix, uri);
                }
            }
            this.decls.clear();
            this.reverseDecls.clear();
        }
        this.inCollectingMode = false;
    }
    
    public void endElement() {
        this.nss.popContext();
    }
    
    public void iterateDeclaredPrefixes(final PrefixCallback callback) throws SAXException {
        final Enumeration e = this.nss.getDeclaredPrefixes();
        while (e.hasMoreElements()) {
            final String p = e.nextElement();
            final String uri = this.nss.getURI(p);
            callback.onPrefixMapping(p, uri);
        }
    }
}
