// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.types;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.net.URI;
import java.util.Calendar;

public interface Datatype<V>
{
    boolean isHandlingJavaType(final Class p0);
    
    Builtin getBuiltin();
    
    boolean isValid(final V p0);
    
    String getString(final V p0) throws InvalidValueException;
    
    V valueOf(final String p0) throws InvalidValueException;
    
    String getDisplayString();
    
    public enum Default
    {
        BOOLEAN((Class)Boolean.class, Builtin.BOOLEAN), 
        BOOLEAN_PRIMITIVE((Class)Boolean.TYPE, Builtin.BOOLEAN), 
        SHORT((Class)Short.class, Builtin.I2_SHORT), 
        SHORT_PRIMITIVE((Class)Short.TYPE, Builtin.I2_SHORT), 
        INTEGER((Class)Integer.class, Builtin.I4), 
        INTEGER_PRIMITIVE((Class)Integer.TYPE, Builtin.I4), 
        UNSIGNED_INTEGER_ONE_BYTE((Class)UnsignedIntegerOneByte.class, Builtin.UI1), 
        UNSIGNED_INTEGER_TWO_BYTES((Class)UnsignedIntegerTwoBytes.class, Builtin.UI2), 
        UNSIGNED_INTEGER_FOUR_BYTES((Class)UnsignedIntegerFourBytes.class, Builtin.UI4), 
        FLOAT((Class)Float.class, Builtin.R4), 
        FLOAT_PRIMITIVE((Class)Float.TYPE, Builtin.R4), 
        DOUBLE((Class)Double.class, Builtin.FLOAT), 
        DOUBLE_PRIMTIIVE((Class)Double.TYPE, Builtin.FLOAT), 
        CHAR((Class)Character.class, Builtin.CHAR), 
        CHAR_PRIMITIVE((Class)Character.TYPE, Builtin.CHAR), 
        STRING((Class)String.class, Builtin.STRING), 
        CALENDAR((Class)Calendar.class, Builtin.DATETIME), 
        BYTES((Class)byte[].class, Builtin.BIN_BASE64), 
        URI((Class)URI.class, Builtin.URI);
        
        private Class javaType;
        private Builtin builtinType;
        
        private Default(final Class javaType, final Builtin builtinType) {
            this.javaType = javaType;
            this.builtinType = builtinType;
        }
        
        public Class getJavaType() {
            return this.javaType;
        }
        
        public Builtin getBuiltinType() {
            return this.builtinType;
        }
        
        public static Default getByJavaType(final Class javaType) {
            for (final Default d : values()) {
                if (d.getJavaType().equals(javaType)) {
                    return d;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return this.getJavaType() + " => " + this.getBuiltinType();
        }
    }
    
    public enum Builtin
    {
        UI1("ui1", (AbstractDatatype<VT>)new UnsignedIntegerOneByteDatatype()), 
        UI2("ui2", (AbstractDatatype<VT>)new UnsignedIntegerTwoBytesDatatype()), 
        UI4("ui4", (AbstractDatatype<VT>)new UnsignedIntegerFourBytesDatatype()), 
        I1("i1", (AbstractDatatype<VT>)new IntegerDatatype(1)), 
        I2("i2", (AbstractDatatype<VT>)new IntegerDatatype(2)), 
        I2_SHORT("i2", (AbstractDatatype<VT>)new ShortDatatype()), 
        I4("i4", (AbstractDatatype<VT>)new IntegerDatatype(4)), 
        INT("int", (AbstractDatatype<VT>)new IntegerDatatype(4)), 
        R4("r4", (AbstractDatatype<VT>)new FloatDatatype()), 
        R8("r8", (AbstractDatatype<VT>)new DoubleDatatype()), 
        NUMBER("number", (AbstractDatatype<VT>)new DoubleDatatype()), 
        FIXED144("fixed.14.4", (AbstractDatatype<VT>)new DoubleDatatype()), 
        FLOAT("float", (AbstractDatatype<VT>)new DoubleDatatype()), 
        CHAR("char", (AbstractDatatype<VT>)new CharacterDatatype()), 
        STRING("string", (AbstractDatatype<VT>)new StringDatatype()), 
        DATE("date", (AbstractDatatype<VT>)new DateTimeDatatype(new String[] { "yyyy-MM-dd" }, "yyyy-MM-dd")), 
        DATETIME("dateTime", (AbstractDatatype<VT>)new DateTimeDatatype(new String[] { "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss" }, "yyyy-MM-dd'T'HH:mm:ss")), 
        DATETIME_TZ("dateTime.tz", (AbstractDatatype<VT>)new DateTimeDatatype(new String[] { "yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ" }, "yyyy-MM-dd'T'HH:mm:ssZ")), 
        TIME("time", (AbstractDatatype<VT>)new DateTimeDatatype(new String[] { "HH:mm:ss" }, "HH:mm:ss")), 
        TIME_TZ("time.tz", (AbstractDatatype<VT>)new DateTimeDatatype(new String[] { "HH:mm:ssZ", "HH:mm:ss" }, "HH:mm:ssZ")), 
        BOOLEAN("boolean", (AbstractDatatype<VT>)new BooleanDatatype()), 
        BIN_BASE64("bin.base64", (AbstractDatatype<VT>)new Base64Datatype()), 
        BIN_HEX("bin.hex", (AbstractDatatype<VT>)new BinHexDatatype()), 
        URI("uri", (AbstractDatatype<VT>)new URIDatatype()), 
        UUID("uuid", (AbstractDatatype<VT>)new StringDatatype());
        
        private static Map<String, Builtin> byName;
        private String descriptorName;
        private Datatype datatype;
        
        private Builtin(final String descriptorName, final AbstractDatatype<VT> datatype) {
            datatype.setBuiltin(this);
            this.descriptorName = descriptorName;
            this.datatype = datatype;
        }
        
        public String getDescriptorName() {
            return this.descriptorName;
        }
        
        public Datatype getDatatype() {
            return this.datatype;
        }
        
        public static Builtin getByDescriptorName(final String descriptorName) {
            if (descriptorName == null) {
                return null;
            }
            return Builtin.byName.get(descriptorName.toLowerCase(Locale.ROOT));
        }
        
        public static boolean isNumeric(final Builtin builtin) {
            return builtin != null && (builtin.equals(Builtin.UI1) || builtin.equals(Builtin.UI2) || builtin.equals(Builtin.UI4) || builtin.equals(Builtin.I1) || builtin.equals(Builtin.I2) || builtin.equals(Builtin.I4) || builtin.equals(Builtin.INT));
        }
        
        static {
            Builtin.byName = new HashMap<String, Builtin>() {
                {
                    for (final Builtin b : Builtin.values()) {
                        if (!this.containsKey(b.getDescriptorName().toLowerCase(Locale.ROOT))) {
                            this.put(b.getDescriptorName().toLowerCase(Locale.ROOT), b);
                        }
                    }
                }
            };
        }
    }
}
