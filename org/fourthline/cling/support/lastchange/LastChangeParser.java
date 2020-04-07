// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.lastchange;

import org.seamless.util.Exceptions;
import org.fourthline.cling.support.shared.AbstractMap;
import org.xml.sax.SAXException;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.xml.sax.Attributes;
import org.seamless.xml.DOMParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.fourthline.cling.model.XMLUtil;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.logging.Level;
import java.io.InputStream;
import org.seamless.util.io.IO;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;
import org.seamless.xml.SAXParser;

public abstract class LastChangeParser extends SAXParser
{
    private static final Logger log;
    
    protected abstract String getNamespace();
    
    protected Set<Class<? extends EventedValue>> getEventedVariables() {
        return (Set<Class<? extends EventedValue>>)Collections.EMPTY_SET;
    }
    
    protected EventedValue createValue(final String name, final Map.Entry<String, String>[] attributes) throws Exception {
        for (final Class<? extends EventedValue> evType : this.getEventedVariables()) {
            if (evType.getSimpleName().equals(name)) {
                final Constructor<? extends EventedValue> ctor = evType.getConstructor(Map.Entry[].class);
                return (EventedValue)ctor.newInstance(attributes);
            }
        }
        return null;
    }
    
    public Event parseResource(final String resource) throws Exception {
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
            return this.parse(IO.readLines(is));
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public Event parse(final String xml) throws Exception {
        if (xml == null || xml.length() == 0) {
            throw new RuntimeException("Null or empty XML");
        }
        final Event event = new Event();
        new RootHandler(event, this);
        if (LastChangeParser.log.isLoggable(Level.FINE)) {
            LastChangeParser.log.fine("Parsing 'LastChange' event XML content");
            LastChangeParser.log.fine("===================================== 'LastChange' BEGIN ============================================");
            LastChangeParser.log.fine(xml);
            LastChangeParser.log.fine("====================================== 'LastChange' END  ============================================");
        }
        this.parse(new InputSource(new StringReader(xml)));
        LastChangeParser.log.fine("Parsed event with instances IDs: " + event.getInstanceIDs().size());
        if (LastChangeParser.log.isLoggable(Level.FINEST)) {
            for (final InstanceID instanceID : event.getInstanceIDs()) {
                LastChangeParser.log.finest("InstanceID '" + instanceID.getId() + "' has values: " + instanceID.getValues().size());
                for (final EventedValue eventedValue : instanceID.getValues()) {
                    LastChangeParser.log.finest(eventedValue.getName() + " => " + eventedValue.getValue());
                }
            }
        }
        return event;
    }
    
    public String generate(final Event event) throws Exception {
        return XMLUtil.documentToFragmentString(this.buildDOM(event));
    }
    
    protected Document buildDOM(final Event event) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final Document d = factory.newDocumentBuilder().newDocument();
        this.generateRoot(event, d);
        return d;
    }
    
    protected void generateRoot(final Event event, final Document descriptor) {
        final Element eventElement = descriptor.createElementNS(this.getNamespace(), CONSTANTS.Event.name());
        descriptor.appendChild(eventElement);
        this.generateInstanceIDs(event, descriptor, eventElement);
    }
    
    protected void generateInstanceIDs(final Event event, final Document descriptor, final Element rootElement) {
        for (final InstanceID instanceID : event.getInstanceIDs()) {
            if (instanceID.getId() == null) {
                continue;
            }
            final Element instanceIDElement = XMLUtil.appendNewElement(descriptor, rootElement, CONSTANTS.InstanceID.name());
            instanceIDElement.setAttribute(CONSTANTS.val.name(), instanceID.getId().toString());
            for (final EventedValue eventedValue : instanceID.getValues()) {
                this.generateEventedValue(eventedValue, descriptor, instanceIDElement);
            }
        }
    }
    
    protected void generateEventedValue(final EventedValue eventedValue, final Document descriptor, final Element parentElement) {
        final String name = eventedValue.getName();
        final Map.Entry<String, String>[] attributes = (Map.Entry<String, String>[])eventedValue.getAttributes();
        if (attributes != null && attributes.length > 0) {
            final Element evElement = XMLUtil.appendNewElement(descriptor, parentElement, name);
            for (final Map.Entry<String, String> attr : attributes) {
                evElement.setAttribute(attr.getKey(), DOMParser.escape(attr.getValue()));
            }
        }
    }
    
    static {
        log = Logger.getLogger(LastChangeParser.class.getName());
    }
    
    public enum CONSTANTS
    {
        Event, 
        InstanceID, 
        val;
        
        public boolean equals(final String s) {
            return this.name().equals(s);
        }
    }
    
    class RootHandler extends Handler<Event>
    {
        RootHandler(final Event instance, final SAXParser parser) {
            super(instance, parser);
        }
        
        RootHandler(final Event instance) {
            super(instance);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (CONSTANTS.InstanceID.equals(localName)) {
                final String valAttr = attributes.getValue(CONSTANTS.val.name());
                if (valAttr != null) {
                    final InstanceID instanceID = new InstanceID(new UnsignedIntegerFourBytes(valAttr));
                    this.getInstance().getInstanceIDs().add(instanceID);
                    new InstanceIDHandler(instanceID, this);
                }
            }
        }
    }
    
    class InstanceIDHandler extends Handler<InstanceID>
    {
        InstanceIDHandler(final InstanceID instance, final Handler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            final Map.Entry[] attributeMap = new Map.Entry[attributes.getLength()];
            for (int i = 0; i < attributeMap.length; ++i) {
                attributeMap[i] = new AbstractMap.SimpleEntry(attributes.getLocalName(i), attributes.getValue(i));
            }
            try {
                final EventedValue esv = LastChangeParser.this.createValue(localName, attributeMap);
                if (esv != null) {
                    this.getInstance().getValues().add(esv);
                }
            }
            catch (Exception ex) {
                LastChangeParser.log.warning("Error reading event XML, ignoring value: " + Exceptions.unwrap(ex));
            }
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            return CONSTANTS.InstanceID.equals(localName);
        }
    }
}
