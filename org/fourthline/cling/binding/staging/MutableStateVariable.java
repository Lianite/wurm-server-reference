// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.binding.staging;

import org.fourthline.cling.model.meta.StateVariableTypeDetails;
import org.fourthline.cling.model.meta.StateVariableAllowedValueRange;
import org.fourthline.cling.model.meta.StateVariable;
import org.fourthline.cling.model.meta.StateVariableEventDetails;
import java.util.List;
import org.fourthline.cling.model.types.Datatype;

public class MutableStateVariable
{
    public String name;
    public Datatype dataType;
    public String defaultValue;
    public List<String> allowedValues;
    public MutableAllowedValueRange allowedValueRange;
    public StateVariableEventDetails eventDetails;
    
    public StateVariable build() {
        return new StateVariable(this.name, new StateVariableTypeDetails(this.dataType, this.defaultValue, (String[])((this.allowedValues == null || this.allowedValues.size() == 0) ? null : ((String[])this.allowedValues.toArray(new String[this.allowedValues.size()]))), (this.allowedValueRange == null) ? null : new StateVariableAllowedValueRange(this.allowedValueRange.minimum, this.allowedValueRange.maximum, this.allowedValueRange.step)), this.eventDetails);
    }
}
