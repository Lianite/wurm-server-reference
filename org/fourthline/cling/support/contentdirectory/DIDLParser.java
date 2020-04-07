// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory;

import org.xml.sax.SAXException;
import org.fourthline.cling.support.model.DIDLAttribute;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.Person;
import java.util.logging.Level;
import org.w3c.dom.NodeList;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.model.XMLUtil;
import java.util.Iterator;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Document;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.Exceptions;
import org.fourthline.cling.support.model.ProtocolInfo;
import java.net.URI;
import org.fourthline.cling.model.types.Datatype;
import org.xml.sax.Attributes;
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.container.Container;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.InputStream;
import org.seamless.util.io.IO;
import org.fourthline.cling.support.model.DIDLContent;
import java.util.logging.Logger;
import org.seamless.xml.SAXParser;

public class DIDLParser extends SAXParser
{
    private static final Logger log;
    public static final String UNKNOWN_TITLE = "Unknown Title";
    
    public DIDLContent parseResource(final String resource) throws Exception {
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
    
    public DIDLContent parse(final String xml) throws Exception {
        if (xml == null || xml.length() == 0) {
            throw new RuntimeException("Null or empty XML");
        }
        final DIDLContent content = new DIDLContent();
        this.createRootHandler(content, this);
        DIDLParser.log.fine("Parsing DIDL XML content");
        this.parse(new InputSource(new StringReader(xml)));
        return content;
    }
    
    protected RootHandler createRootHandler(final DIDLContent instance, final SAXParser parser) {
        return new RootHandler(instance, parser);
    }
    
    protected ContainerHandler createContainerHandler(final Container instance, final Handler parent) {
        return new ContainerHandler(instance, parent);
    }
    
    protected ItemHandler createItemHandler(final Item instance, final Handler parent) {
        return new ItemHandler(instance, parent);
    }
    
    protected ResHandler createResHandler(final Res instance, final Handler parent) {
        return new ResHandler(instance, parent);
    }
    
    protected DescMetaHandler createDescMetaHandler(final DescMeta instance, final Handler parent) {
        return new DescMetaHandler(instance, parent);
    }
    
    protected Container createContainer(final Attributes attributes) {
        final Container container = new Container();
        container.setId(attributes.getValue("id"));
        container.setParentID(attributes.getValue("parentID"));
        if (attributes.getValue("childCount") != null) {
            container.setChildCount(Integer.valueOf(attributes.getValue("childCount")));
        }
        try {
            Boolean value = Datatype.Builtin.BOOLEAN.getDatatype().valueOf(attributes.getValue("restricted"));
            if (value != null) {
                container.setRestricted(value);
            }
            value = Datatype.Builtin.BOOLEAN.getDatatype().valueOf(attributes.getValue("searchable"));
            if (value != null) {
                container.setSearchable(value);
            }
        }
        catch (Exception ex) {}
        return container;
    }
    
    protected Item createItem(final Attributes attributes) {
        final Item item = new Item();
        item.setId(attributes.getValue("id"));
        item.setParentID(attributes.getValue("parentID"));
        try {
            final Boolean value = Datatype.Builtin.BOOLEAN.getDatatype().valueOf(attributes.getValue("restricted"));
            if (value != null) {
                item.setRestricted(value);
            }
        }
        catch (Exception ex) {}
        if (attributes.getValue("refID") != null) {
            item.setRefID(attributes.getValue("refID"));
        }
        return item;
    }
    
    protected Res createResource(final Attributes attributes) {
        final Res res = new Res();
        if (attributes.getValue("importUri") != null) {
            res.setImportUri(URI.create(attributes.getValue("importUri")));
        }
        try {
            res.setProtocolInfo(new ProtocolInfo(attributes.getValue("protocolInfo")));
        }
        catch (InvalidValueException ex) {
            DIDLParser.log.warning("In DIDL content, invalid resource protocol info: " + Exceptions.unwrap(ex));
            return null;
        }
        if (attributes.getValue("size") != null) {
            res.setSize(this.toLongOrNull(attributes.getValue("size")));
        }
        if (attributes.getValue("duration") != null) {
            res.setDuration(attributes.getValue("duration"));
        }
        if (attributes.getValue("bitrate") != null) {
            res.setBitrate(this.toLongOrNull(attributes.getValue("bitrate")));
        }
        if (attributes.getValue("sampleFrequency") != null) {
            res.setSampleFrequency(this.toLongOrNull(attributes.getValue("sampleFrequency")));
        }
        if (attributes.getValue("bitsPerSample") != null) {
            res.setBitsPerSample(this.toLongOrNull(attributes.getValue("bitsPerSample")));
        }
        if (attributes.getValue("nrAudioChannels") != null) {
            res.setNrAudioChannels(this.toLongOrNull(attributes.getValue("nrAudioChannels")));
        }
        if (attributes.getValue("colorDepth") != null) {
            res.setColorDepth(this.toLongOrNull(attributes.getValue("colorDepth")));
        }
        if (attributes.getValue("protection") != null) {
            res.setProtection(attributes.getValue("protection"));
        }
        if (attributes.getValue("resolution") != null) {
            res.setResolution(attributes.getValue("resolution"));
        }
        return res;
    }
    
    private Long toLongOrNull(final String value) {
        try {
            return Long.valueOf(value);
        }
        catch (NumberFormatException x) {
            return null;
        }
    }
    
    protected DescMeta createDescMeta(final Attributes attributes) {
        final DescMeta desc = new DescMeta();
        desc.setId(attributes.getValue("id"));
        if (attributes.getValue("type") != null) {
            desc.setType(attributes.getValue("type"));
        }
        if (attributes.getValue("nameSpace") != null) {
            desc.setNameSpace(URI.create(attributes.getValue("nameSpace")));
        }
        return desc;
    }
    
    public String generate(final DIDLContent content) throws Exception {
        return this.generate(content, false);
    }
    
    public String generate(final DIDLContent content, final boolean nestedItems) throws Exception {
        return this.documentToString(this.buildDOM(content, nestedItems), true);
    }
    
    protected String documentToString(final Document document, final boolean omitProlog) throws Exception {
        final TransformerFactory transFactory = TransformerFactory.newInstance();
        final Transformer transformer = transFactory.newTransformer();
        if (omitProlog) {
            transformer.setOutputProperty("omit-xml-declaration", "yes");
        }
        final StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        return out.toString();
    }
    
    protected Document buildDOM(final DIDLContent content, final boolean nestedItems) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final Document d = factory.newDocumentBuilder().newDocument();
        this.generateRoot(content, d, nestedItems);
        return d;
    }
    
    protected void generateRoot(final DIDLContent content, final Document descriptor, final boolean nestedItems) {
        final Element rootElement = descriptor.createElementNS("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/", "DIDL-Lite");
        descriptor.appendChild(rootElement);
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:upnp", "urn:schemas-upnp-org:metadata-1-0/upnp/");
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", "http://purl.org/dc/elements/1.1/");
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:sec", "http://www.sec.co.kr/");
        for (final Container container : content.getContainers()) {
            if (container == null) {
                continue;
            }
            this.generateContainer(container, descriptor, rootElement, nestedItems);
        }
        for (final Item item : content.getItems()) {
            if (item == null) {
                continue;
            }
            this.generateItem(item, descriptor, rootElement);
        }
        for (final DescMeta descMeta : content.getDescMetadata()) {
            if (descMeta == null) {
                continue;
            }
            this.generateDescMetadata(descMeta, descriptor, rootElement);
        }
    }
    
    protected void generateContainer(final Container container, final Document descriptor, final Element parent, final boolean nestedItems) {
        if (container.getClazz() == null) {
            throw new RuntimeException("Missing 'upnp:class' element for container: " + container.getId());
        }
        final Element containerElement = XMLUtil.appendNewElement(descriptor, parent, "container");
        if (container.getId() == null) {
            throw new NullPointerException("Missing id on container: " + container);
        }
        containerElement.setAttribute("id", container.getId());
        if (container.getParentID() == null) {
            throw new NullPointerException("Missing parent id on container: " + container);
        }
        containerElement.setAttribute("parentID", container.getParentID());
        if (container.getChildCount() != null) {
            containerElement.setAttribute("childCount", Integer.toString(container.getChildCount()));
        }
        containerElement.setAttribute("restricted", this.booleanToInt(container.isRestricted()));
        containerElement.setAttribute("searchable", this.booleanToInt(container.isSearchable()));
        String title = container.getTitle();
        if (title == null) {
            DIDLParser.log.warning("Missing 'dc:title' element for container: " + container.getId());
            title = "Unknown Title";
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, containerElement, "dc:title", title, "http://purl.org/dc/elements/1.1/");
        XMLUtil.appendNewElementIfNotNull(descriptor, containerElement, "dc:creator", container.getCreator(), "http://purl.org/dc/elements/1.1/");
        XMLUtil.appendNewElementIfNotNull(descriptor, containerElement, "upnp:writeStatus", container.getWriteStatus(), "urn:schemas-upnp-org:metadata-1-0/upnp/");
        this.appendClass(descriptor, containerElement, container.getClazz(), "upnp:class", false);
        for (final DIDLObject.Class searchClass : container.getSearchClasses()) {
            this.appendClass(descriptor, containerElement, searchClass, "upnp:searchClass", true);
        }
        for (final DIDLObject.Class createClass : container.getCreateClasses()) {
            this.appendClass(descriptor, containerElement, createClass, "upnp:createClass", true);
        }
        this.appendProperties(descriptor, containerElement, container, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, "urn:schemas-upnp-org:metadata-1-0/upnp/");
        this.appendProperties(descriptor, containerElement, container, "dc", DIDLObject.Property.DC.NAMESPACE.class, "http://purl.org/dc/elements/1.1/");
        if (nestedItems) {
            for (final Item item : container.getItems()) {
                if (item == null) {
                    continue;
                }
                this.generateItem(item, descriptor, containerElement);
            }
        }
        for (final Res resource : container.getResources()) {
            if (resource == null) {
                continue;
            }
            this.generateResource(resource, descriptor, containerElement);
        }
        for (final DescMeta descMeta : container.getDescMetadata()) {
            if (descMeta == null) {
                continue;
            }
            this.generateDescMetadata(descMeta, descriptor, containerElement);
        }
    }
    
    protected void generateItem(final Item item, final Document descriptor, final Element parent) {
        if (item.getClazz() == null) {
            throw new RuntimeException("Missing 'upnp:class' element for item: " + item.getId());
        }
        final Element itemElement = XMLUtil.appendNewElement(descriptor, parent, "item");
        if (item.getId() == null) {
            throw new NullPointerException("Missing id on item: " + item);
        }
        itemElement.setAttribute("id", item.getId());
        if (item.getParentID() == null) {
            throw new NullPointerException("Missing parent id on item: " + item);
        }
        itemElement.setAttribute("parentID", item.getParentID());
        if (item.getRefID() != null) {
            itemElement.setAttribute("refID", item.getRefID());
        }
        itemElement.setAttribute("restricted", this.booleanToInt(item.isRestricted()));
        String title = item.getTitle();
        if (title == null) {
            DIDLParser.log.warning("Missing 'dc:title' element for item: " + item.getId());
            title = "Unknown Title";
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, itemElement, "dc:title", title, "http://purl.org/dc/elements/1.1/");
        XMLUtil.appendNewElementIfNotNull(descriptor, itemElement, "dc:creator", item.getCreator(), "http://purl.org/dc/elements/1.1/");
        XMLUtil.appendNewElementIfNotNull(descriptor, itemElement, "upnp:writeStatus", item.getWriteStatus(), "urn:schemas-upnp-org:metadata-1-0/upnp/");
        this.appendClass(descriptor, itemElement, item.getClazz(), "upnp:class", false);
        this.appendProperties(descriptor, itemElement, item, "upnp", DIDLObject.Property.UPNP.NAMESPACE.class, "urn:schemas-upnp-org:metadata-1-0/upnp/");
        this.appendProperties(descriptor, itemElement, item, "dc", DIDLObject.Property.DC.NAMESPACE.class, "http://purl.org/dc/elements/1.1/");
        this.appendProperties(descriptor, itemElement, item, "sec", DIDLObject.Property.SEC.NAMESPACE.class, "http://www.sec.co.kr/");
        for (final Res resource : item.getResources()) {
            if (resource == null) {
                continue;
            }
            this.generateResource(resource, descriptor, itemElement);
        }
        for (final DescMeta descMeta : item.getDescMetadata()) {
            if (descMeta == null) {
                continue;
            }
            this.generateDescMetadata(descMeta, descriptor, itemElement);
        }
    }
    
    protected void generateResource(final Res resource, final Document descriptor, final Element parent) {
        if (resource.getValue() == null) {
            throw new RuntimeException("Missing resource URI value" + resource);
        }
        if (resource.getProtocolInfo() == null) {
            throw new RuntimeException("Missing resource protocol info: " + resource);
        }
        final Element resourceElement = XMLUtil.appendNewElement(descriptor, parent, "res", resource.getValue());
        resourceElement.setAttribute("protocolInfo", resource.getProtocolInfo().toString());
        if (resource.getImportUri() != null) {
            resourceElement.setAttribute("importUri", resource.getImportUri().toString());
        }
        if (resource.getSize() != null) {
            resourceElement.setAttribute("size", resource.getSize().toString());
        }
        if (resource.getDuration() != null) {
            resourceElement.setAttribute("duration", resource.getDuration());
        }
        if (resource.getBitrate() != null) {
            resourceElement.setAttribute("bitrate", resource.getBitrate().toString());
        }
        if (resource.getSampleFrequency() != null) {
            resourceElement.setAttribute("sampleFrequency", resource.getSampleFrequency().toString());
        }
        if (resource.getBitsPerSample() != null) {
            resourceElement.setAttribute("bitsPerSample", resource.getBitsPerSample().toString());
        }
        if (resource.getNrAudioChannels() != null) {
            resourceElement.setAttribute("nrAudioChannels", resource.getNrAudioChannels().toString());
        }
        if (resource.getColorDepth() != null) {
            resourceElement.setAttribute("colorDepth", resource.getColorDepth().toString());
        }
        if (resource.getProtection() != null) {
            resourceElement.setAttribute("protection", resource.getProtection());
        }
        if (resource.getResolution() != null) {
            resourceElement.setAttribute("resolution", resource.getResolution());
        }
    }
    
    protected void generateDescMetadata(final DescMeta descMeta, final Document descriptor, final Element parent) {
        if (descMeta.getId() == null) {
            throw new RuntimeException("Missing id of description metadata: " + descMeta);
        }
        if (descMeta.getNameSpace() == null) {
            throw new RuntimeException("Missing namespace of description metadata: " + descMeta);
        }
        final Element descElement = XMLUtil.appendNewElement(descriptor, parent, "desc");
        descElement.setAttribute("id", descMeta.getId());
        descElement.setAttribute("nameSpace", descMeta.getNameSpace().toString());
        if (descMeta.getType() != null) {
            descElement.setAttribute("type", descMeta.getType());
        }
        this.populateDescMetadata(descElement, descMeta);
    }
    
    protected void populateDescMetadata(final Element descElement, final DescMeta descMeta) {
        if (descMeta.getMetadata() instanceof Document) {
            final Document doc = descMeta.getMetadata();
            final NodeList nl = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i) {
                final Node n = nl.item(i);
                if (n.getNodeType() == 1) {
                    final Node clone = descElement.getOwnerDocument().importNode(n, true);
                    descElement.appendChild(clone);
                }
            }
        }
        else {
            DIDLParser.log.warning("Unknown desc metadata content, please override populateDescMetadata(): " + descMeta.getMetadata());
        }
    }
    
    protected void appendProperties(final Document descriptor, final Element parent, final DIDLObject object, final String prefix, final Class<? extends DIDLObject.Property.NAMESPACE> namespace, final String namespaceURI) {
        for (final DIDLObject.Property<Object> property : object.getPropertiesByNamespace(namespace)) {
            final Element el = descriptor.createElementNS(namespaceURI, prefix + ":" + property.getDescriptorName());
            parent.appendChild(el);
            property.setOnElement(el);
        }
    }
    
    protected void appendClass(final Document descriptor, final Element parent, final DIDLObject.Class clazz, final String element, final boolean appendDerivation) {
        final Element classElement = XMLUtil.appendNewElementIfNotNull(descriptor, parent, element, clazz.getValue(), "urn:schemas-upnp-org:metadata-1-0/upnp/");
        if (clazz.getFriendlyName() != null && clazz.getFriendlyName().length() > 0) {
            classElement.setAttribute("name", clazz.getFriendlyName());
        }
        if (appendDerivation) {
            classElement.setAttribute("includeDerived", Boolean.toString(clazz.isIncludeDerived()));
        }
    }
    
    protected String booleanToInt(final boolean b) {
        return b ? "1" : "0";
    }
    
    public void debugXML(final String s) {
        if (DIDLParser.log.isLoggable(Level.FINE)) {
            DIDLParser.log.fine("-------------------------------------------------------------------------------------");
            DIDLParser.log.fine("\n" + s);
            DIDLParser.log.fine("-------------------------------------------------------------------------------------");
        }
    }
    
    static {
        log = Logger.getLogger(DIDLParser.class.getName());
    }
    
    public abstract class DIDLObjectHandler<I extends DIDLObject> extends Handler<I>
    {
        protected DIDLObjectHandler(final I instance, final Handler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("http://purl.org/dc/elements/1.1/".equals(uri)) {
                if ("title".equals(localName)) {
                    this.getInstance().setTitle(this.getCharacters());
                }
                else if ("creator".equals(localName)) {
                    this.getInstance().setCreator(this.getCharacters());
                }
                else if ("description".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.DESCRIPTION(this.getCharacters()));
                }
                else if ("publisher".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.PUBLISHER(new Person(this.getCharacters())));
                }
                else if ("contributor".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.CONTRIBUTOR(new Person(this.getCharacters())));
                }
                else if ("date".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.DATE(this.getCharacters()));
                }
                else if ("language".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.LANGUAGE(this.getCharacters()));
                }
                else if ("rights".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.RIGHTS(this.getCharacters()));
                }
                else if ("relation".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.DC.RELATION(URI.create(this.getCharacters())));
                }
            }
            else if ("urn:schemas-upnp-org:metadata-1-0/upnp/".equals(uri)) {
                if ("writeStatus".equals(localName)) {
                    try {
                        this.getInstance().setWriteStatus(WriteStatus.valueOf(this.getCharacters()));
                    }
                    catch (Exception ex) {
                        DIDLParser.log.info("Ignoring invalid writeStatus value: " + this.getCharacters());
                    }
                }
                else if ("class".equals(localName)) {
                    this.getInstance().setClazz(new DIDLObject.Class(this.getCharacters(), this.getAttributes().getValue("name")));
                }
                else if ("artist".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.ARTIST(new PersonWithRole(this.getCharacters(), this.getAttributes().getValue("role"))));
                }
                else if ("actor".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.ACTOR(new PersonWithRole(this.getCharacters(), this.getAttributes().getValue("role"))));
                }
                else if ("author".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.AUTHOR(new PersonWithRole(this.getCharacters(), this.getAttributes().getValue("role"))));
                }
                else if ("producer".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.PRODUCER(new Person(this.getCharacters())));
                }
                else if ("director".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.DIRECTOR(new Person(this.getCharacters())));
                }
                else if ("longDescription".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.LONG_DESCRIPTION(this.getCharacters()));
                }
                else if ("storageUsed".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_USED(Long.valueOf(this.getCharacters())));
                }
                else if ("storageTotal".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_TOTAL(Long.valueOf(this.getCharacters())));
                }
                else if ("storageFree".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_FREE(Long.valueOf(this.getCharacters())));
                }
                else if ("storageMaxPartition".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_MAX_PARTITION(Long.valueOf(this.getCharacters())));
                }
                else if ("storageMedium".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.STORAGE_MEDIUM(StorageMedium.valueOrVendorSpecificOf(this.getCharacters())));
                }
                else if ("genre".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.GENRE(this.getCharacters()));
                }
                else if ("album".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.ALBUM(this.getCharacters()));
                }
                else if ("playlist".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.PLAYLIST(this.getCharacters()));
                }
                else if ("region".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.REGION(this.getCharacters()));
                }
                else if ("rating".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.RATING(this.getCharacters()));
                }
                else if ("toc".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.TOC(this.getCharacters()));
                }
                else if ("albumArtURI".equals(localName)) {
                    final DIDLObject.Property albumArtURI = new DIDLObject.Property.UPNP.ALBUM_ART_URI(URI.create(this.getCharacters()));
                    final Attributes albumArtURIAttributes = this.getAttributes();
                    for (int i = 0; i < albumArtURIAttributes.getLength(); ++i) {
                        if ("profileID".equals(albumArtURIAttributes.getLocalName(i))) {
                            albumArtURI.addAttribute(new DIDLObject.Property.DLNA.PROFILE_ID(new DIDLAttribute("urn:schemas-dlna-org:metadata-1-0/", "dlna", albumArtURIAttributes.getValue(i))));
                        }
                    }
                    this.getInstance().addProperty(albumArtURI);
                }
                else if ("artistDiscographyURI".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.ARTIST_DISCO_URI(URI.create(this.getCharacters())));
                }
                else if ("lyricsURI".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.LYRICS_URI(URI.create(this.getCharacters())));
                }
                else if ("icon".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.ICON(URI.create(this.getCharacters())));
                }
                else if ("radioCallSign".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.RADIO_CALL_SIGN(this.getCharacters()));
                }
                else if ("radioStationID".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.RADIO_STATION_ID(this.getCharacters()));
                }
                else if ("radioBand".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.RADIO_BAND(this.getCharacters()));
                }
                else if ("channelNr".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.CHANNEL_NR(Integer.valueOf(this.getCharacters())));
                }
                else if ("channelName".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.CHANNEL_NAME(this.getCharacters()));
                }
                else if ("scheduledStartTime".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.SCHEDULED_START_TIME(this.getCharacters()));
                }
                else if ("scheduledEndTime".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.SCHEDULED_END_TIME(this.getCharacters()));
                }
                else if ("DVDRegionCode".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.DVD_REGION_CODE(Integer.valueOf(this.getCharacters())));
                }
                else if ("originalTrackNumber".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.ORIGINAL_TRACK_NUMBER(Integer.valueOf(this.getCharacters())));
                }
                else if ("userAnnotation".equals(localName)) {
                    this.getInstance().addProperty(new DIDLObject.Property.UPNP.USER_ANNOTATION(this.getCharacters()));
                }
            }
        }
    }
    
    public class RootHandler extends Handler<DIDLContent>
    {
        RootHandler(final DIDLContent instance, final SAXParser parser) {
            super(instance, parser);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (!"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri)) {
                return;
            }
            if (localName.equals("container")) {
                final Container container = DIDLParser.this.createContainer(attributes);
                this.getInstance().addContainer(container);
                DIDLParser.this.createContainerHandler(container, this);
            }
            else if (localName.equals("item")) {
                final Item item = DIDLParser.this.createItem(attributes);
                this.getInstance().addItem(item);
                DIDLParser.this.createItemHandler(item, this);
            }
            else if (localName.equals("desc")) {
                final DescMeta desc = DIDLParser.this.createDescMeta(attributes);
                this.getInstance().addDescMetadata(desc);
                DIDLParser.this.createDescMetaHandler(desc, this);
            }
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            if ("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri) && "DIDL-Lite".equals(localName)) {
                this.getInstance().replaceGenericContainerAndItems();
                return true;
            }
            return false;
        }
    }
    
    public class ContainerHandler extends DIDLObjectHandler<Container>
    {
        public ContainerHandler(final Container instance, final Handler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (!"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri)) {
                return;
            }
            if (localName.equals("item")) {
                final Item item = DIDLParser.this.createItem(attributes);
                this.getInstance().addItem(item);
                DIDLParser.this.createItemHandler(item, this);
            }
            else if (localName.equals("desc")) {
                final DescMeta desc = DIDLParser.this.createDescMeta(attributes);
                this.getInstance().addDescMetadata(desc);
                DIDLParser.this.createDescMetaHandler(desc, this);
            }
            else if (localName.equals("res")) {
                final Res res = DIDLParser.this.createResource(attributes);
                if (res != null) {
                    this.getInstance().addResource(res);
                    DIDLParser.this.createResHandler(res, this);
                }
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("urn:schemas-upnp-org:metadata-1-0/upnp/".equals(uri)) {
                if ("searchClass".equals(localName)) {
                    this.getInstance().getSearchClasses().add(new DIDLObject.Class(this.getCharacters(), this.getAttributes().getValue("name"), "true".equals(this.getAttributes().getValue("includeDerived"))));
                }
                else if ("createClass".equals(localName)) {
                    this.getInstance().getCreateClasses().add(new DIDLObject.Class(this.getCharacters(), this.getAttributes().getValue("name"), "true".equals(this.getAttributes().getValue("includeDerived"))));
                }
            }
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            if ("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri) && "container".equals(localName)) {
                if (this.getInstance().getTitle() == null) {
                    DIDLParser.log.warning("In DIDL content, missing 'dc:title' element for container: " + this.getInstance().getId());
                }
                if (this.getInstance().getClazz() == null) {
                    DIDLParser.log.warning("In DIDL content, missing 'upnp:class' element for container: " + this.getInstance().getId());
                }
                return true;
            }
            return false;
        }
    }
    
    public class ItemHandler extends DIDLObjectHandler<Item>
    {
        public ItemHandler(final Item instance, final Handler parent) {
            super(instance, parent);
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (!"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri)) {
                return;
            }
            if (localName.equals("res")) {
                final Res res = DIDLParser.this.createResource(attributes);
                if (res != null) {
                    this.getInstance().addResource(res);
                    DIDLParser.this.createResHandler(res, this);
                }
            }
            else if (localName.equals("desc")) {
                final DescMeta desc = DIDLParser.this.createDescMeta(attributes);
                this.getInstance().addDescMetadata(desc);
                DIDLParser.this.createDescMetaHandler(desc, this);
            }
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            if ("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri) && "item".equals(localName)) {
                if (this.getInstance().getTitle() == null) {
                    DIDLParser.log.warning("In DIDL content, missing 'dc:title' element for item: " + this.getInstance().getId());
                }
                if (this.getInstance().getClazz() == null) {
                    DIDLParser.log.warning("In DIDL content, missing 'upnp:class' element for item: " + this.getInstance().getId());
                }
                return true;
            }
            return false;
        }
    }
    
    protected class ResHandler extends Handler<Res>
    {
        public ResHandler(final Res instance, final Handler parent) {
            super(instance, parent);
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            this.getInstance().setValue(this.getCharacters());
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            return "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri) && "res".equals(localName);
        }
    }
    
    public class DescMetaHandler extends Handler<DescMeta>
    {
        protected Element current;
        
        public DescMetaHandler(final DescMeta instance, final Handler parent) {
            super(instance, parent);
            instance.setMetadata(instance.createMetadataDocument());
            this.current = this.getInstance().getMetadata().getDocumentElement();
        }
        
        @Override
        public DescMeta<Document> getInstance() {
            return super.getInstance();
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            final Element newEl = this.getInstance().getMetadata().createElementNS(uri, qName);
            for (int i = 0; i < attributes.getLength(); ++i) {
                newEl.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
            }
            this.current.appendChild(newEl);
            this.current = newEl;
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (this.isLastElement(uri, localName, qName)) {
                return;
            }
            if (this.getCharacters().length() > 0 && !this.getCharacters().matches("[\\t\\n\\x0B\\f\\r\\s]+")) {
                this.current.appendChild(this.getInstance().getMetadata().createTextNode(this.getCharacters()));
            }
            this.current = (Element)this.current.getParentNode();
            this.characters = new StringBuilder();
            this.attributes = null;
        }
        
        @Override
        protected boolean isLastElement(final String uri, final String localName, final String qName) {
            return "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/".equals(uri) && "desc".equals(localName);
        }
    }
}
