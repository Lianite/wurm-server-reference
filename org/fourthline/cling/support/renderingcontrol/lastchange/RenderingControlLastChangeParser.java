// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.support.lastchange.EventedValue;
import java.util.Set;
import javax.xml.transform.stream.StreamSource;
import org.fourthline.cling.model.ModelUtil;
import javax.xml.transform.Source;
import org.fourthline.cling.support.lastchange.LastChangeParser;

public class RenderingControlLastChangeParser extends LastChangeParser
{
    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/RCS/";
    public static final String SCHEMA_RESOURCE = "org/fourthline/cling/support/renderingcontrol/metadata-1.0-rcs.xsd";
    
    @Override
    protected String getNamespace() {
        return "urn:schemas-upnp-org:metadata-1-0/RCS/";
    }
    
    @Override
    protected Source[] getSchemaSources() {
        if (!ModelUtil.ANDROID_RUNTIME) {
            return new Source[] { new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/fourthline/cling/support/renderingcontrol/metadata-1.0-rcs.xsd")) };
        }
        return null;
    }
    
    @Override
    protected Set<Class<? extends EventedValue>> getEventedVariables() {
        return RenderingControlVariable.ALL;
    }
}
