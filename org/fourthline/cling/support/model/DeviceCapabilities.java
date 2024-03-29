// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.action.ActionArgumentValue;
import java.util.Map;

public class DeviceCapabilities
{
    private StorageMedium[] playMedia;
    private StorageMedium[] recMedia;
    private RecordQualityMode[] recQualityModes;
    
    public DeviceCapabilities(final Map<String, ActionArgumentValue> args) {
        this(StorageMedium.valueOfCommaSeparatedList((String)args.get("PlayMedia").getValue()), StorageMedium.valueOfCommaSeparatedList((String)args.get("RecMedia").getValue()), RecordQualityMode.valueOfCommaSeparatedList((String)args.get("RecQualityModes").getValue()));
    }
    
    public DeviceCapabilities(final StorageMedium[] playMedia) {
        this.recMedia = new StorageMedium[] { StorageMedium.NOT_IMPLEMENTED };
        this.recQualityModes = new RecordQualityMode[] { RecordQualityMode.NOT_IMPLEMENTED };
        this.playMedia = playMedia;
    }
    
    public DeviceCapabilities(final StorageMedium[] playMedia, final StorageMedium[] recMedia, final RecordQualityMode[] recQualityModes) {
        this.recMedia = new StorageMedium[] { StorageMedium.NOT_IMPLEMENTED };
        this.recQualityModes = new RecordQualityMode[] { RecordQualityMode.NOT_IMPLEMENTED };
        this.playMedia = playMedia;
        this.recMedia = recMedia;
        this.recQualityModes = recQualityModes;
    }
    
    public StorageMedium[] getPlayMedia() {
        return this.playMedia;
    }
    
    public StorageMedium[] getRecMedia() {
        return this.recMedia;
    }
    
    public RecordQualityMode[] getRecQualityModes() {
        return this.recQualityModes;
    }
    
    public String getPlayMediaString() {
        return ModelUtil.toCommaSeparatedList(this.playMedia);
    }
    
    public String getRecMediaString() {
        return ModelUtil.toCommaSeparatedList(this.recMedia);
    }
    
    public String getRecQualityModesString() {
        return ModelUtil.toCommaSeparatedList(this.recQualityModes);
    }
}
