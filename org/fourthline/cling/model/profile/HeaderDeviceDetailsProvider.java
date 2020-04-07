// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.profile;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.fourthline.cling.model.meta.DeviceDetails;

public class HeaderDeviceDetailsProvider implements DeviceDetailsProvider
{
    private final DeviceDetails defaultDeviceDetails;
    private final Map<Key, DeviceDetails> headerDetails;
    
    public HeaderDeviceDetailsProvider(final DeviceDetails defaultDeviceDetails) {
        this(defaultDeviceDetails, null);
    }
    
    public HeaderDeviceDetailsProvider(final DeviceDetails defaultDeviceDetails, final Map<Key, DeviceDetails> headerDetails) {
        this.defaultDeviceDetails = defaultDeviceDetails;
        this.headerDetails = ((headerDetails != null) ? headerDetails : new HashMap<Key, DeviceDetails>());
    }
    
    public DeviceDetails getDefaultDeviceDetails() {
        return this.defaultDeviceDetails;
    }
    
    public Map<Key, DeviceDetails> getHeaderDetails() {
        return this.headerDetails;
    }
    
    @Override
    public DeviceDetails provide(final RemoteClientInfo info) {
        if (info == null || info.getRequestHeaders().isEmpty()) {
            return this.getDefaultDeviceDetails();
        }
        for (final Key key : this.getHeaderDetails().keySet()) {
            final List<String> headerValues;
            if ((headerValues = info.getRequestHeaders().get((Object)key.getHeaderName())) == null) {
                continue;
            }
            for (final String headerValue : headerValues) {
                if (key.isValuePatternMatch(headerValue)) {
                    return this.getHeaderDetails().get(key);
                }
            }
        }
        return this.getDefaultDeviceDetails();
    }
    
    public static class Key
    {
        final String headerName;
        final String valuePattern;
        final Pattern pattern;
        
        public Key(final String headerName, final String valuePattern) {
            this.headerName = headerName;
            this.valuePattern = valuePattern;
            this.pattern = Pattern.compile(valuePattern, 2);
        }
        
        public String getHeaderName() {
            return this.headerName;
        }
        
        public String getValuePattern() {
            return this.valuePattern;
        }
        
        public boolean isValuePatternMatch(final String value) {
            return this.pattern.matcher(value).matches();
        }
    }
}
