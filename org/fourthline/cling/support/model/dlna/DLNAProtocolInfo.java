// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

import org.fourthline.cling.support.model.Protocol;
import org.seamless.util.MimeType;
import org.fourthline.cling.model.types.InvalidValueException;
import java.util.EnumMap;
import java.util.Map;
import org.fourthline.cling.support.model.ProtocolInfo;

public class DLNAProtocolInfo extends ProtocolInfo
{
    protected final Map<DLNAAttribute.Type, DLNAAttribute> attributes;
    
    public DLNAProtocolInfo(final String s) throws InvalidValueException {
        super(s);
        this.attributes = new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class);
        this.parseAdditionalInfo();
    }
    
    public DLNAProtocolInfo(final MimeType contentFormatMimeType) {
        super(contentFormatMimeType);
        this.attributes = new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class);
    }
    
    public DLNAProtocolInfo(final DLNAProfiles profile) {
        super(MimeType.valueOf(profile.getContentFormat()));
        (this.attributes = new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class)).put(DLNAAttribute.Type.DLNA_ORG_PN, new DLNAProfileAttribute(profile));
        this.additionalInfo = this.getAttributesString();
    }
    
    public DLNAProtocolInfo(final DLNAProfiles profile, final EnumMap<DLNAAttribute.Type, DLNAAttribute> attributes) {
        super(MimeType.valueOf(profile.getContentFormat()));
        (this.attributes = new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class)).putAll(attributes);
        this.attributes.put(DLNAAttribute.Type.DLNA_ORG_PN, new DLNAProfileAttribute(profile));
        this.additionalInfo = this.getAttributesString();
    }
    
    public DLNAProtocolInfo(final Protocol protocol, final String network, final String contentFormat, final String additionalInfo) {
        super(protocol, network, contentFormat, additionalInfo);
        this.attributes = new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class);
        this.parseAdditionalInfo();
    }
    
    public DLNAProtocolInfo(final Protocol protocol, final String network, final String contentFormat, final EnumMap<DLNAAttribute.Type, DLNAAttribute> attributes) {
        super(protocol, network, contentFormat, "");
        (this.attributes = new EnumMap<DLNAAttribute.Type, DLNAAttribute>(DLNAAttribute.Type.class)).putAll(attributes);
        this.additionalInfo = this.getAttributesString();
    }
    
    public DLNAProtocolInfo(final ProtocolInfo template) {
        this(template.getProtocol(), template.getNetwork(), template.getContentFormat(), template.getAdditionalInfo());
    }
    
    public boolean contains(final DLNAAttribute.Type type) {
        return this.attributes.containsKey(type);
    }
    
    public DLNAAttribute getAttribute(final DLNAAttribute.Type type) {
        return this.attributes.get(type);
    }
    
    public Map<DLNAAttribute.Type, DLNAAttribute> getAttributes() {
        return this.attributes;
    }
    
    protected String getAttributesString() {
        String s = "";
        for (final DLNAAttribute.Type type : DLNAAttribute.Type.values()) {
            final String value = this.attributes.containsKey(type) ? this.attributes.get(type).getString() : null;
            if (value != null && value.length() != 0) {
                s = s + ((s.length() == 0) ? "" : ";") + type.getAttributeName() + "=" + value;
            }
        }
        return s;
    }
    
    protected void parseAdditionalInfo() {
        if (this.additionalInfo != null) {
            final String[] split;
            final String[] atts = split = this.additionalInfo.split(";");
            for (final String att : split) {
                final String[] attNameValue = att.split("=");
                if (attNameValue.length == 2) {
                    final DLNAAttribute.Type type = DLNAAttribute.Type.valueOfAttributeName(attNameValue[0]);
                    if (type != null) {
                        final DLNAAttribute dlnaAttrinute = DLNAAttribute.newInstance(type, attNameValue[1], this.getContentFormat());
                        this.attributes.put(type, dlnaAttrinute);
                    }
                }
            }
        }
    }
}
