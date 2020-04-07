// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import java.net.URI;
import org.w3c.dom.Element;
import java.util.Collection;
import java.util.Locale;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public abstract class DIDLObject
{
    protected String id;
    protected String parentID;
    protected String title;
    protected String creator;
    protected boolean restricted;
    protected WriteStatus writeStatus;
    protected Class clazz;
    protected List<Res> resources;
    protected List<Property> properties;
    protected List<DescMeta> descMetadata;
    
    protected DIDLObject() {
        this.restricted = true;
        this.resources = new ArrayList<Res>();
        this.properties = new ArrayList<Property>();
        this.descMetadata = new ArrayList<DescMeta>();
    }
    
    protected DIDLObject(final DIDLObject other) {
        this(other.getId(), other.getParentID(), other.getTitle(), other.getCreator(), other.isRestricted(), other.getWriteStatus(), other.getClazz(), other.getResources(), other.getProperties(), other.getDescMetadata());
    }
    
    protected DIDLObject(final String id, final String parentID, final String title, final String creator, final boolean restricted, final WriteStatus writeStatus, final Class clazz, final List<Res> resources, final List<Property> properties, final List<DescMeta> descMetadata) {
        this.restricted = true;
        this.resources = new ArrayList<Res>();
        this.properties = new ArrayList<Property>();
        this.descMetadata = new ArrayList<DescMeta>();
        this.id = id;
        this.parentID = parentID;
        this.title = title;
        this.creator = creator;
        this.restricted = restricted;
        this.writeStatus = writeStatus;
        this.clazz = clazz;
        this.resources = resources;
        this.properties = properties;
        this.descMetadata = descMetadata;
    }
    
    public String getId() {
        return this.id;
    }
    
    public DIDLObject setId(final String id) {
        this.id = id;
        return this;
    }
    
    public String getParentID() {
        return this.parentID;
    }
    
    public DIDLObject setParentID(final String parentID) {
        this.parentID = parentID;
        return this;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public DIDLObject setTitle(final String title) {
        this.title = title;
        return this;
    }
    
    public String getCreator() {
        return this.creator;
    }
    
    public DIDLObject setCreator(final String creator) {
        this.creator = creator;
        return this;
    }
    
    public boolean isRestricted() {
        return this.restricted;
    }
    
    public DIDLObject setRestricted(final boolean restricted) {
        this.restricted = restricted;
        return this;
    }
    
    public WriteStatus getWriteStatus() {
        return this.writeStatus;
    }
    
    public DIDLObject setWriteStatus(final WriteStatus writeStatus) {
        this.writeStatus = writeStatus;
        return this;
    }
    
    public Res getFirstResource() {
        return (this.getResources().size() > 0) ? this.getResources().get(0) : null;
    }
    
    public List<Res> getResources() {
        return this.resources;
    }
    
    public DIDLObject setResources(final List<Res> resources) {
        this.resources = resources;
        return this;
    }
    
    public DIDLObject addResource(final Res resource) {
        this.getResources().add(resource);
        return this;
    }
    
    public Class getClazz() {
        return this.clazz;
    }
    
    public DIDLObject setClazz(final Class clazz) {
        this.clazz = clazz;
        return this;
    }
    
    public List<Property> getProperties() {
        return this.properties;
    }
    
    public DIDLObject setProperties(final List<Property> properties) {
        this.properties = properties;
        return this;
    }
    
    public DIDLObject addProperty(final Property property) {
        if (property == null) {
            return this;
        }
        this.getProperties().add(property);
        return this;
    }
    
    public DIDLObject replaceFirstProperty(final Property property) {
        if (property == null) {
            return this;
        }
        final Iterator<Property> it = (Iterator<Property>)this.getProperties().iterator();
        while (it.hasNext()) {
            final Property p = it.next();
            if (p.getClass().isAssignableFrom(property.getClass())) {
                it.remove();
            }
        }
        this.addProperty(property);
        return this;
    }
    
    public DIDLObject replaceProperties(final java.lang.Class<? extends Property> propertyClass, final Property[] properties) {
        if (properties.length == 0) {
            return this;
        }
        this.removeProperties(propertyClass);
        return this.addProperties(properties);
    }
    
    public DIDLObject addProperties(final Property[] properties) {
        if (properties == null) {
            return this;
        }
        for (final Property property : properties) {
            this.addProperty(property);
        }
        return this;
    }
    
    public DIDLObject removeProperties(final java.lang.Class<? extends Property> propertyClass) {
        final Iterator<Property> it = (Iterator<Property>)this.getProperties().iterator();
        while (it.hasNext()) {
            final Property property = it.next();
            if (propertyClass.isInstance(property)) {
                it.remove();
            }
        }
        return this;
    }
    
    public boolean hasProperty(final java.lang.Class<? extends Property> propertyClass) {
        for (final Property property : this.getProperties()) {
            if (propertyClass.isInstance(property)) {
                return true;
            }
        }
        return false;
    }
    
    public <V> Property<V> getFirstProperty(final java.lang.Class<? extends Property<V>> propertyClass) {
        for (final Property property : this.getProperties()) {
            if (propertyClass.isInstance(property)) {
                return (Property<V>)property;
            }
        }
        return null;
    }
    
    public <V> Property<V> getLastProperty(final java.lang.Class<? extends Property<V>> propertyClass) {
        Property found = null;
        for (final Property property : this.getProperties()) {
            if (propertyClass.isInstance(property)) {
                found = property;
            }
        }
        return (Property<V>)found;
    }
    
    public <V> Property<V>[] getProperties(final java.lang.Class<? extends Property<V>> propertyClass) {
        final List<Property<V>> list = new ArrayList<Property<V>>();
        for (final Property property : this.getProperties()) {
            if (propertyClass.isInstance(property)) {
                list.add(property);
            }
        }
        return list.toArray(new Property[list.size()]);
    }
    
    public <V> Property<V>[] getPropertiesByNamespace(final java.lang.Class<? extends Property.NAMESPACE> namespace) {
        final List<Property<V>> list = new ArrayList<Property<V>>();
        for (final Property property : this.getProperties()) {
            if (namespace.isInstance(property)) {
                list.add(property);
            }
        }
        return list.toArray(new Property[list.size()]);
    }
    
    public <V> V getFirstPropertyValue(final java.lang.Class<? extends Property<V>> propertyClass) {
        final Property<V> prop = this.getFirstProperty(propertyClass);
        return (prop == null) ? null : prop.getValue();
    }
    
    public <V> List<V> getPropertyValues(final java.lang.Class<? extends Property<V>> propertyClass) {
        final List<V> list = new ArrayList<V>();
        for (final Property property : this.getProperties((java.lang.Class<? extends Property<Object>>)propertyClass)) {
            list.add(property.getValue());
        }
        return list;
    }
    
    public List<DescMeta> getDescMetadata() {
        return this.descMetadata;
    }
    
    public void setDescMetadata(final List<DescMeta> descMetadata) {
        this.descMetadata = descMetadata;
    }
    
    public DIDLObject addDescMetadata(final DescMeta descMetadata) {
        this.getDescMetadata().add(descMetadata);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DIDLObject that = (DIDLObject)o;
        return this.id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
    public abstract static class Property<V>
    {
        private V value;
        private final String descriptorName;
        private final List<Property<DIDLAttribute>> attributes;
        
        protected Property() {
            this(null, null);
        }
        
        protected Property(final String descriptorName) {
            this(null, descriptorName);
        }
        
        protected Property(final V value, final String descriptorName) {
            this.attributes = new ArrayList<Property<DIDLAttribute>>();
            this.value = value;
            this.descriptorName = ((descriptorName == null) ? this.getClass().getSimpleName().toLowerCase(Locale.ROOT).replace("didlobject$property$upnp$", "") : descriptorName);
        }
        
        protected Property(final V value, final String descriptorName, final List<Property<DIDLAttribute>> attributes) {
            this.attributes = new ArrayList<Property<DIDLAttribute>>();
            this.value = value;
            this.descriptorName = ((descriptorName == null) ? this.getClass().getSimpleName().toLowerCase(Locale.ROOT).replace("didlobject$property$upnp$", "") : descriptorName);
            this.attributes.addAll(attributes);
        }
        
        public V getValue() {
            return this.value;
        }
        
        public void setValue(final V value) {
            this.value = value;
        }
        
        public String getDescriptorName() {
            return this.descriptorName;
        }
        
        public void setOnElement(final Element element) {
            element.setTextContent(this.toString());
            for (final Property<DIDLAttribute> attr : this.attributes) {
                element.setAttributeNS(attr.getValue().getNamespaceURI(), attr.getValue().getPrefix() + ':' + attr.getDescriptorName(), attr.getValue().getValue());
            }
        }
        
        public void addAttribute(final Property<DIDLAttribute> attr) {
            this.attributes.add(attr);
        }
        
        public void removeAttribute(final Property<DIDLAttribute> attr) {
            this.attributes.remove(attr);
        }
        
        public void removeAttribute(final String descriptorName) {
            for (final Property<DIDLAttribute> attr : this.attributes) {
                if (attr.getDescriptorName().equals(descriptorName)) {
                    this.removeAttribute(attr);
                    break;
                }
            }
        }
        
        public Property<DIDLAttribute> getAttribute(final String descriptorName) {
            for (final Property<DIDLAttribute> attr : this.attributes) {
                if (attr.getDescriptorName().equals(descriptorName)) {
                    return attr;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return (this.getValue() != null) ? this.getValue().toString() : "";
        }
        
        public static class PropertyPersonWithRole extends Property<PersonWithRole>
        {
            public PropertyPersonWithRole() {
            }
            
            public PropertyPersonWithRole(final String descriptorName) {
                super(descriptorName);
            }
            
            public PropertyPersonWithRole(final PersonWithRole value, final String descriptorName) {
                super(value, descriptorName);
            }
            
            @Override
            public void setOnElement(final Element element) {
                if (this.getValue() != null) {
                    this.getValue().setOnElement(element);
                }
            }
        }
        
        public static class DC
        {
            public static class DESCRIPTION extends Property<String> implements DC.NAMESPACE
            {
                public DESCRIPTION() {
                }
                
                public DESCRIPTION(final String value) {
                    super(value, null);
                }
            }
            
            public static class PUBLISHER extends Property<Person> implements DC.NAMESPACE
            {
                public PUBLISHER() {
                }
                
                public PUBLISHER(final Person value) {
                    super(value, null);
                }
            }
            
            public static class CONTRIBUTOR extends Property<Person> implements DC.NAMESPACE
            {
                public CONTRIBUTOR() {
                }
                
                public CONTRIBUTOR(final Person value) {
                    super(value, null);
                }
            }
            
            public static class DATE extends Property<String> implements DC.NAMESPACE
            {
                public DATE() {
                }
                
                public DATE(final String value) {
                    super(value, null);
                }
            }
            
            public static class LANGUAGE extends Property<String> implements DC.NAMESPACE
            {
                public LANGUAGE() {
                }
                
                public LANGUAGE(final String value) {
                    super(value, null);
                }
            }
            
            public static class RELATION extends Property<URI> implements DC.NAMESPACE
            {
                public RELATION() {
                }
                
                public RELATION(final URI value) {
                    super(value, null);
                }
            }
            
            public static class RIGHTS extends Property<String> implements DC.NAMESPACE
            {
                public RIGHTS() {
                }
                
                public RIGHTS(final String value) {
                    super(value, null);
                }
            }
            
            public interface NAMESPACE extends Property.NAMESPACE
            {
                public static final String URI = "http://purl.org/dc/elements/1.1/";
            }
        }
        
        public abstract static class SEC
        {
            public static class CAPTIONINFOEX extends Property<URI> implements SEC.NAMESPACE
            {
                public CAPTIONINFOEX() {
                    this((URI)null);
                }
                
                public CAPTIONINFOEX(final URI value) {
                    super(value, "CaptionInfoEx");
                }
                
                public CAPTIONINFOEX(final URI value, final List<Property<DIDLAttribute>> attributes) {
                    super(value, "CaptionInfoEx", attributes);
                }
            }
            
            public static class CAPTIONINFO extends Property<URI> implements SEC.NAMESPACE
            {
                public CAPTIONINFO() {
                    this((URI)null);
                }
                
                public CAPTIONINFO(final URI value) {
                    super(value, "CaptionInfo");
                }
                
                public CAPTIONINFO(final URI value, final List<Property<DIDLAttribute>> attributes) {
                    super(value, "CaptionInfo", attributes);
                }
            }
            
            public static class TYPE extends Property<DIDLAttribute> implements SEC.NAMESPACE
            {
                public TYPE() {
                    this((DIDLAttribute)null);
                }
                
                public TYPE(final DIDLAttribute value) {
                    super(value, "type");
                }
            }
            
            public interface NAMESPACE extends Property.NAMESPACE
            {
                public static final String URI = "http://www.sec.co.kr/";
            }
        }
        
        public abstract static class UPNP
        {
            public static class ARTIST extends PropertyPersonWithRole implements UPNP.NAMESPACE
            {
                public ARTIST() {
                }
                
                public ARTIST(final PersonWithRole value) {
                    super(value, null);
                }
            }
            
            public static class ACTOR extends PropertyPersonWithRole implements UPNP.NAMESPACE
            {
                public ACTOR() {
                }
                
                public ACTOR(final PersonWithRole value) {
                    super(value, null);
                }
            }
            
            public static class AUTHOR extends PropertyPersonWithRole implements UPNP.NAMESPACE
            {
                public AUTHOR() {
                }
                
                public AUTHOR(final PersonWithRole value) {
                    super(value, null);
                }
            }
            
            public static class PRODUCER extends Property<Person> implements UPNP.NAMESPACE
            {
                public PRODUCER() {
                }
                
                public PRODUCER(final Person value) {
                    super(value, null);
                }
            }
            
            public static class DIRECTOR extends Property<Person> implements UPNP.NAMESPACE
            {
                public DIRECTOR() {
                }
                
                public DIRECTOR(final Person value) {
                    super(value, null);
                }
            }
            
            public static class GENRE extends Property<String> implements UPNP.NAMESPACE
            {
                public GENRE() {
                }
                
                public GENRE(final String value) {
                    super(value, null);
                }
            }
            
            public static class ALBUM extends Property<String> implements UPNP.NAMESPACE
            {
                public ALBUM() {
                }
                
                public ALBUM(final String value) {
                    super(value, null);
                }
            }
            
            public static class PLAYLIST extends Property<String> implements UPNP.NAMESPACE
            {
                public PLAYLIST() {
                }
                
                public PLAYLIST(final String value) {
                    super(value, null);
                }
            }
            
            public static class REGION extends Property<String> implements UPNP.NAMESPACE
            {
                public REGION() {
                }
                
                public REGION(final String value) {
                    super(value, null);
                }
            }
            
            public static class RATING extends Property<String> implements UPNP.NAMESPACE
            {
                public RATING() {
                }
                
                public RATING(final String value) {
                    super(value, null);
                }
            }
            
            public static class TOC extends Property<String> implements UPNP.NAMESPACE
            {
                public TOC() {
                }
                
                public TOC(final String value) {
                    super(value, null);
                }
            }
            
            public static class ALBUM_ART_URI extends Property<URI> implements UPNP.NAMESPACE
            {
                public ALBUM_ART_URI() {
                    this((URI)null);
                }
                
                public ALBUM_ART_URI(final URI value) {
                    super(value, "albumArtURI");
                }
                
                public ALBUM_ART_URI(final URI value, final List<Property<DIDLAttribute>> attributes) {
                    super(value, "albumArtURI", attributes);
                }
            }
            
            public static class ARTIST_DISCO_URI extends Property<URI> implements UPNP.NAMESPACE
            {
                public ARTIST_DISCO_URI() {
                    this((URI)null);
                }
                
                public ARTIST_DISCO_URI(final URI value) {
                    super(value, "artistDiscographyURI");
                }
            }
            
            public static class LYRICS_URI extends Property<URI> implements UPNP.NAMESPACE
            {
                public LYRICS_URI() {
                    this((URI)null);
                }
                
                public LYRICS_URI(final URI value) {
                    super(value, "lyricsURI");
                }
            }
            
            public static class STORAGE_TOTAL extends Property<Long> implements UPNP.NAMESPACE
            {
                public STORAGE_TOTAL() {
                    this((Long)null);
                }
                
                public STORAGE_TOTAL(final Long value) {
                    super(value, "storageTotal");
                }
            }
            
            public static class STORAGE_USED extends Property<Long> implements UPNP.NAMESPACE
            {
                public STORAGE_USED() {
                    this((Long)null);
                }
                
                public STORAGE_USED(final Long value) {
                    super(value, "storageUsed");
                }
            }
            
            public static class STORAGE_FREE extends Property<Long> implements UPNP.NAMESPACE
            {
                public STORAGE_FREE() {
                    this((Long)null);
                }
                
                public STORAGE_FREE(final Long value) {
                    super(value, "storageFree");
                }
            }
            
            public static class STORAGE_MAX_PARTITION extends Property<Long> implements UPNP.NAMESPACE
            {
                public STORAGE_MAX_PARTITION() {
                    this((Long)null);
                }
                
                public STORAGE_MAX_PARTITION(final Long value) {
                    super(value, "storageMaxPartition");
                }
            }
            
            public static class STORAGE_MEDIUM extends Property<StorageMedium> implements UPNP.NAMESPACE
            {
                public STORAGE_MEDIUM() {
                    this((StorageMedium)null);
                }
                
                public STORAGE_MEDIUM(final StorageMedium value) {
                    super(value, "storageMedium");
                }
            }
            
            public static class LONG_DESCRIPTION extends Property<String> implements UPNP.NAMESPACE
            {
                public LONG_DESCRIPTION() {
                    this(null);
                }
                
                public LONG_DESCRIPTION(final String value) {
                    super(value, "longDescription");
                }
            }
            
            public static class ICON extends Property<URI> implements UPNP.NAMESPACE
            {
                public ICON() {
                    this((URI)null);
                }
                
                public ICON(final URI value) {
                    super(value, "icon");
                }
            }
            
            public static class RADIO_CALL_SIGN extends Property<String> implements UPNP.NAMESPACE
            {
                public RADIO_CALL_SIGN() {
                    this(null);
                }
                
                public RADIO_CALL_SIGN(final String value) {
                    super(value, "radioCallSign");
                }
            }
            
            public static class RADIO_STATION_ID extends Property<String> implements UPNP.NAMESPACE
            {
                public RADIO_STATION_ID() {
                    this(null);
                }
                
                public RADIO_STATION_ID(final String value) {
                    super(value, "radioStationID");
                }
            }
            
            public static class RADIO_BAND extends Property<String> implements UPNP.NAMESPACE
            {
                public RADIO_BAND() {
                    this(null);
                }
                
                public RADIO_BAND(final String value) {
                    super(value, "radioBand");
                }
            }
            
            public static class CHANNEL_NR extends Property<Integer> implements UPNP.NAMESPACE
            {
                public CHANNEL_NR() {
                    this((Integer)null);
                }
                
                public CHANNEL_NR(final Integer value) {
                    super(value, "channelNr");
                }
            }
            
            public static class CHANNEL_NAME extends Property<String> implements UPNP.NAMESPACE
            {
                public CHANNEL_NAME() {
                    this(null);
                }
                
                public CHANNEL_NAME(final String value) {
                    super(value, "channelName");
                }
            }
            
            public static class SCHEDULED_START_TIME extends Property<String> implements UPNP.NAMESPACE
            {
                public SCHEDULED_START_TIME() {
                    this(null);
                }
                
                public SCHEDULED_START_TIME(final String value) {
                    super(value, "scheduledStartTime");
                }
            }
            
            public static class SCHEDULED_END_TIME extends Property<String> implements UPNP.NAMESPACE
            {
                public SCHEDULED_END_TIME() {
                    this(null);
                }
                
                public SCHEDULED_END_TIME(final String value) {
                    super(value, "scheduledEndTime");
                }
            }
            
            public static class DVD_REGION_CODE extends Property<Integer> implements UPNP.NAMESPACE
            {
                public DVD_REGION_CODE() {
                    this((Integer)null);
                }
                
                public DVD_REGION_CODE(final Integer value) {
                    super(value, "DVDRegionCode");
                }
            }
            
            public static class ORIGINAL_TRACK_NUMBER extends Property<Integer> implements UPNP.NAMESPACE
            {
                public ORIGINAL_TRACK_NUMBER() {
                    this((Integer)null);
                }
                
                public ORIGINAL_TRACK_NUMBER(final Integer value) {
                    super(value, "originalTrackNumber");
                }
            }
            
            public static class USER_ANNOTATION extends Property<String> implements UPNP.NAMESPACE
            {
                public USER_ANNOTATION() {
                    this(null);
                }
                
                public USER_ANNOTATION(final String value) {
                    super(value, "userAnnotation");
                }
            }
            
            public interface NAMESPACE extends Property.NAMESPACE
            {
                public static final String URI = "urn:schemas-upnp-org:metadata-1-0/upnp/";
            }
        }
        
        public abstract static class DLNA
        {
            public static class PROFILE_ID extends Property<DIDLAttribute> implements DLNA.NAMESPACE
            {
                public PROFILE_ID() {
                    this((DIDLAttribute)null);
                }
                
                public PROFILE_ID(final DIDLAttribute value) {
                    super(value, "profileID");
                }
            }
            
            public interface NAMESPACE extends Property.NAMESPACE
            {
                public static final String URI = "urn:schemas-dlna-org:metadata-1-0/";
            }
        }
        
        public interface NAMESPACE
        {
        }
    }
    
    public static class Class
    {
        protected String value;
        protected String friendlyName;
        protected boolean includeDerived;
        
        public Class() {
        }
        
        public Class(final String value) {
            this.value = value;
        }
        
        public Class(final String value, final String friendlyName) {
            this.value = value;
            this.friendlyName = friendlyName;
        }
        
        public Class(final String value, final String friendlyName, final boolean includeDerived) {
            this.value = value;
            this.friendlyName = friendlyName;
            this.includeDerived = includeDerived;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
        
        public String getFriendlyName() {
            return this.friendlyName;
        }
        
        public void setFriendlyName(final String friendlyName) {
            this.friendlyName = friendlyName;
        }
        
        public boolean isIncludeDerived() {
            return this.includeDerived;
        }
        
        public void setIncludeDerived(final boolean includeDerived) {
            this.includeDerived = includeDerived;
        }
        
        public boolean equals(final DIDLObject instance) {
            return this.getValue().equals(instance.getClazz().getValue());
        }
    }
}
