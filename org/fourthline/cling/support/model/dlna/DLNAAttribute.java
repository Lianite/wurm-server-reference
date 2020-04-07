// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.seamless.util.Exceptions;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DLNAAttribute<T>
{
    private static final Logger log;
    private T value;
    
    public void setValue(final T value) {
        this.value = value;
    }
    
    public T getValue() {
        return this.value;
    }
    
    public abstract void setString(final String p0, final String p1) throws InvalidDLNAProtocolAttributeException;
    
    public abstract String getString();
    
    public static DLNAAttribute newInstance(final Type type, final String attributeValue, final String contentFormat) {
        DLNAAttribute attr = null;
        for (int i = 0; i < type.getAttributeTypes().length && attr == null; ++i) {
            final Class<? extends DLNAAttribute> attributeClass = type.getAttributeTypes()[i];
            try {
                DLNAAttribute.log.finest("Trying to parse DLNA '" + type + "' with class: " + attributeClass.getSimpleName());
                attr = (DLNAAttribute)attributeClass.newInstance();
                if (attributeValue != null) {
                    attr.setString(attributeValue, contentFormat);
                }
            }
            catch (InvalidDLNAProtocolAttributeException ex) {
                DLNAAttribute.log.finest("Invalid DLNA attribute value for tested type: " + attributeClass.getSimpleName() + " - " + ex.getMessage());
                attr = null;
            }
            catch (Exception ex2) {
                DLNAAttribute.log.severe("Error instantiating DLNA attribute of type '" + type + "' with value: " + attributeValue);
                DLNAAttribute.log.log(Level.SEVERE, "Exception root cause: ", Exceptions.unwrap(ex2));
            }
        }
        return attr;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") '" + this.getValue() + "'";
    }
    
    static {
        log = Logger.getLogger(DLNAAttribute.class.getName());
    }
    
    public enum Type
    {
        DLNA_ORG_PN("DLNA.ORG_PN", (Class<? extends DLNAAttribute>[])new Class[] { DLNAProfileAttribute.class }), 
        DLNA_ORG_OP("DLNA.ORG_OP", (Class<? extends DLNAAttribute>[])new Class[] { DLNAOperationsAttribute.class }), 
        DLNA_ORG_PS("DLNA.ORG_PS", (Class<? extends DLNAAttribute>[])new Class[] { DLNAPlaySpeedAttribute.class }), 
        DLNA_ORG_CI("DLNA.ORG_CI", (Class<? extends DLNAAttribute>[])new Class[] { DLNAConversionIndicatorAttribute.class }), 
        DLNA_ORG_FLAGS("DLNA.ORG_FLAGS", (Class<? extends DLNAAttribute>[])new Class[] { DLNAFlagsAttribute.class });
        
        private static Map<String, Type> byName;
        private String attributeName;
        private Class<? extends DLNAAttribute>[] attributeTypes;
        
        private Type(final String attributeName, final Class<? extends DLNAAttribute>[] attributeClass) {
            this.attributeName = attributeName;
            this.attributeTypes = attributeClass;
        }
        
        public String getAttributeName() {
            return this.attributeName;
        }
        
        public Class<? extends DLNAAttribute>[] getAttributeTypes() {
            return this.attributeTypes;
        }
        
        public static Type valueOfAttributeName(final String attributeName) {
            if (attributeName == null) {
                return null;
            }
            return Type.byName.get(attributeName.toUpperCase(Locale.ROOT));
        }
        
        static {
            Type.byName = new HashMap<String, Type>() {
                {
                    for (final Type t : Type.values()) {
                        this.put(t.getAttributeName().toUpperCase(Locale.ROOT), t);
                    }
                }
            };
        }
    }
}
