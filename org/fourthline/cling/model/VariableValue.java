// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.Datatype;
import java.util.logging.Logger;

public class VariableValue
{
    private static final Logger log;
    private final Datatype datatype;
    private final Object value;
    
    public VariableValue(final Datatype datatype, final Object value) throws InvalidValueException {
        this.datatype = datatype;
        this.value = ((value instanceof String) ? datatype.valueOf((String)value) : value);
        if (ModelUtil.ANDROID_RUNTIME) {
            return;
        }
        if (!this.getDatatype().isValid(this.getValue())) {
            throw new InvalidValueException("Invalid value for " + this.getDatatype() + ": " + this.getValue());
        }
        this.logInvalidXML(this.toString());
    }
    
    public Datatype getDatatype() {
        return this.datatype;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    protected void logInvalidXML(final String s) {
        int cp;
        for (int i = 0; i < s.length(); i += Character.charCount(cp)) {
            cp = s.codePointAt(i);
            if (cp != 9 && cp != 10 && cp != 13 && (cp < 32 || cp > 55295) && (cp < 57344 || cp > 65533) && (cp < 65536 || cp > 1114111)) {
                VariableValue.log.warning("Found invalid XML char code: " + cp);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.getDatatype().getString(this.getValue());
    }
    
    static {
        log = Logger.getLogger(VariableValue.class.getName());
    }
}
